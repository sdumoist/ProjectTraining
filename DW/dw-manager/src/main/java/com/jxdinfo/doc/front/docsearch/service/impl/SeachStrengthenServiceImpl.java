package com.jxdinfo.doc.front.docsearch.service.impl;

import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.util.SeachStrengthenESUtil;
import com.jxdinfo.doc.front.docsearch.dao.SeachStrengthenMapper;
import com.jxdinfo.doc.front.docsearch.service.SeachStrengthenService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class SeachStrengthenServiceImpl implements SeachStrengthenService {

    @Resource
    private SeachStrengthenESUtil esUtil;

    @Resource
    private SeachStrengthenMapper mapper;

    @Override
    public ESResponse<Map<String, Object>> seachStrengthen(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString, Integer titlePower, Integer contentPower, Integer tagsPower, Integer categoryPower, String folderIds, Integer order) {

        keyword = cutPinyin(keyword);   // 处理拼音
        // 多路召回
        String[] recallNames = {"multiMatch", "multiPhraseMatch", "titlePhraseMatch"};
        float[] recallWeights = {1, 2, 3};
        int searchPages = 4;
        int oriSize = size;
        size = oriSize * searchPages;
        int oriPage = page;
        page = oriPage / searchPages + 1;
        Map<String, Object> recallResult = this.multiRecall(recallNames, recallWeights, keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower, categoryPower,folderIds, order);

        // final score计算过程
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        List<Map<String,Object>> items = (List<Map<String, Object>>) recallResult.get("items");
        long total = (long) recallResult.get("totalHits");
        // final score计算过程
        for (Map<String, Object> item: items) {
            String doc_id = item.get("id").toString();
            Map<String, String> tem = mapper.getFileInfo(doc_id);
            if (tem == null) {
                continue;
            }
            Date createTime = null;  // 创建时间
            try {
                createTime = dateTimeFormatter.parse((tem.get("uploadTime").toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 各项权重
            float fresh_weight = 0.2F;

            float recall_weight = 1.5F;
            float click_weight = 0.4F;

            if (keyword.contains("最新") || keyword.contains("最近")) {
                fresh_weight = 1.0F;
                click_weight = 0.01F;
            }
            if (order == 2) {  // 时间降序排序
                fresh_weight = 100;
            } else if (order == 3) {  // 时间升序排序
                fresh_weight = -100;
            }

            // 召回分数
            float recall_score = (float) item.get("score");
            // 时鲜分
            float fresh_score = getTimeScore(createTime);

            // 后验分，目前只是文档总体的后验数据，不是搜索的
            float download_num = Float.parseFloat(String.valueOf(tem.get("downloadNum")));
            float read_num = Float.parseFloat(String.valueOf(tem.get("readNum")));
            float click_score = getClickScore(download_num, read_num);

            // 安源项目需要判断是否是标准文件
            String title = (String) item.get("title");

            // 计算final score
            float final_score = recall_weight*recall_score + fresh_weight*fresh_score + click_weight*click_score;

            // 装载计算分数
            item.remove("score");
            item.put("score", final_score);
            item.put("recall_score", recall_score);
            item.put("fresh_score", fresh_score);
            item.put("recall_weight * recall_score", recall_weight * recall_score);
            item.put("fresh_weight * fresh_score", fresh_weight * fresh_score);
            item.put("click_score", click_score);
            item.put("click_weight * click_score", click_weight * click_score);
        }

        // 排序
        if (items.size() > 0) {
            sort(items, "score");
        }

        List<Map<String,Object>> resultItems = new ArrayList<>();

        int start_index = ((oriPage-1)*oriSize) % size;
        int page_size = Math.min(oriSize, items.size()-start_index);
        if (items != null && items.size() > 0){
            for (int i = start_index; i < start_index+page_size; i++){
                Map<String, Object> map = items.get(i);
                resultItems.add(map);
            }
        }

        ESResponse<Map<String, Object>> result = new ESResponse<>();
        result.setTotal(total);
        int totalPage = (int) (total / 10) + 1;
        result.setTotalPages(totalPage);
        result.setItems(resultItems);
        return result;
    }


    public static void sort(List<Map<String, Object>> x, String k) {
        x.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                float v1 = (float) o1.get(k);
                float v2 = (float) o2.get(k);
                return Float.compare(v2, v1);
            }
        });
    }

    public static float getTimeScore(Date createDate){
        float millis = System.currentTimeMillis() - createDate.getTime();
        float day = millis / 1000 / 3600 / 24;
        float outOfDate = 365 * 5; // 5年前的文件就不考虑时鲜了
        return day > outOfDate? 0: -day / outOfDate + 1;
    }

    public static float getClickScore(float download_num, float read_num) {
        float click_num = download_num * 2 + read_num;
        // 公式可能不太好，后续有条件可以修改
        return (float) (1 / (1 + Math.exp(-(click_num- 120) / 25)));  // 在x=200时基本收敛
    }

    //声母表
    static String[] smb = new String[]{"b", "p", "m", "f", "d", "t", "l", "n", "g", "h", "k"
            , "j", "q", "x", "z", "c", "s", "r", "y", "w", "zh", "ch", "sh"};
    //韵母表
    static String[] ymbmax = new String[]{
            "iang", "iong", "uang",
            "ang", "ong", "eng", "ing", "iao", "ian", "uai", "uan",
            "an", "ao", "ai", "ou", "en", "er", "ei",
            "ia", "iu", "ie", "in", "un", "ua", "uo", "ue", "ui",
            "a", "o", "e", "i", "u", "v"
    };
    //独立成字韵母表
    static String[] ymbmin = new String[]{
            "ang", "ong", "eng", "ai", "an", "ao", "ou", "en", "er", "o", "a", "e"
    };

    //将汉语拼音连写s分割成String数组
    public static String cutPinyin(String s) {
        List<String> list = cut(s, 0);
        if (list == null || list.isEmpty()) return s;
        int size = list.size();
        if (list.get(size - 1).length() == 0) {
            list.remove(size - 1);
        }
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<list.size();i++) {
            sb.append(list.get(i));
            if (i < size-1)
                sb.append(" ");
        }
        return sb.toString();
    }

    private static List<String> cut(String s, int index) {
        List<Integer> list = findWord(s, index);
        if (list == null || list.size() == 0) return null;
        for (int x : list) {
            if (x == 0) {
                return Collections.singletonList("");
            }
            List<String> left = cut(s, index + x);
            if (left != null && left.size() > 0) {
                List<String> ans = new ArrayList<>();
                ans.add(s.substring(index, index + x));
                ans.addAll(left);
                return ans;
            }
        }
        return null;
    }

    //找声母
    private static int findSm(String s, int index) {
        int n = s.length();
        for (String asm : smb) {
            if (s.startsWith(asm, index)) {
                int nextidx = index + asm.length();
                if (nextidx < n) {
                    String next = s.charAt(nextidx) + "";
                    boolean smAgain = false;
                    for (String asm2 : smb) {
                        if (next.equals(asm2)) {
                            smAgain = true;
                            break;
                        }
                    }
                    if (!smAgain) {
                        return asm.length();
                    }
                }
            }
        }
        return 0;
    }

    //找独立成字的韵母
    private static List<Integer> findDlym(String s, int index) {
        List<Integer> list = new ArrayList<>();
        for (String ym : ymbmin) {
            if (s.startsWith(ym, index)) {
                list.add(ym.length());
            }
        }
        return list;
    }

    //找韵母
    private static List<Integer> findYm(String s, int index) {
        List<Integer> list = new ArrayList<>();
        for (String ym : ymbmax) {
            if (s.startsWith(ym, index)) {
                list.add(ym.length());
            }
        }
        return list;
    }

    //找单字
    private static List<Integer> findWord(String s, int index) {
        if (index >= s.length()) return Collections.singletonList(0);
        int len = findSm(s, index);
        List<Integer> r = len == 0 ? findDlym(s, index) : findYm(s, index + len);
        for (int i = 0, size = r.size(); i < size; i++) {
            r.set(i, r.get(i) + len);
        }
        return r;
    }

    public Map<String, Object> multiRecall(String[] recallNames, float[] recallWeights, String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString, Integer titlePower, Integer contentPower,
                                           Integer tagsPower, Integer categoryPower, String folderIds, Integer order) {
        ShiroUser user = ShiroKit.getUser();
        List<recallThread> recallThreads = new ArrayList<>();
        for (String recallName: recallNames) {
            recallThreads.add(new recallThread(recallName, keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower,
                    categoryPower,folderIds, order, user));
        }
        for (recallThread thread: recallThreads) {
            thread.start();
        }
        for (recallThread thread : recallThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<Float> weights = new ArrayList<>();
        float weightSum = 0;
        List<ESResponse<Map<String, Object>>> recallResponses = new ArrayList<>();
        for (int i=0;i<recallThreads.size();i++) {
            recallThread thread = recallThreads.get(i);
            if (thread.response != null && thread.response.getItems().size() > 0) {
                weights.add(recallWeights[i]);
                weightSum += recallWeights[i];
                recallResponses.add(thread.response);
            }
        }
        // 防止全空
        if (recallResponses.size() < 1) {
            weights.add(1.0F);
            weightSum = 1;
            recallResponses.add(esUtil.multiMatchQuerySeachStrengthen(keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower,
                    categoryPower, folderIds, order, true, "cross_fields"));
            //recallResponses.add(esHomePageUtil.multiMatchQuery(keyword, page, size, order, startDate, endDate, contentType, uploadDeptId, uploadUserId, fileType, true, "cross_fields", user));
        }
        List<Map<String,Object>> items = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        long totalHits = 0;
        for (int i=0;i<recallResponses.size();i++) {
            ESResponse<Map<String, Object>> response = recallResponses.get(i);
            totalHits = Math.max(response.getTotal(), totalHits);
            float thisWeight = weights.get(i);
            List<Map<String,Object>> thisItems = response.getItems();
            float thisMaxScore = response.getMaxScore();
            // 各路召回分数求和
            for (Map<String, Object> thisItem: thisItems) {
                String thisId = thisItem.get("id").toString();
                float thisScore = (float)thisItem.get("score") * thisWeight / thisMaxScore;  // 归一化
                if (!ids.contains(thisId)) {
                    ids.add(thisId);
                    thisItem.remove("score");
                    thisItem.put("score", thisScore);
                    items.add(thisItem);
                } else {
                    int index = ids.indexOf(thisId);
                    Map<String,Object> item = items.get(index);
                    float score = (float)item.get("score") + thisScore;
                    item.remove("score");
                    item.put("score", score);
                }
            }
            // 归一化
            for (Map<String, Object> item: items) {
                float score = (float) item.get("score");
                score /= weightSum;
                item.remove("score");
                item.put("score", score);
            }
        }

        long finalTotalHits = totalHits;
        return new HashMap<String, Object>(){{
            put("items", items);
            put("totalHits", finalTotalHits);
        }};
    }


    class recallThread extends Thread {
        private final String name;
        private final String keyword;
        private final String contentType;
        private final int page;
        private final boolean adminFlag;
        private final int size;
        private final String tagString;
        private final int titlePower;
        private final int contentPower;
        private final int tagsPower;
        private final int categoryPower;
        private final String folderIds;
        private final int order;
        private long totalHits;
        private final ShiroUser user;

        public ESResponse<Map<String, Object>> response = null;

        public recallThread(String name, String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString,
                            Integer titlePower, Integer contentPower,
                            Integer tagsPower, Integer categoryPower, String folderIds, Integer order, ShiroUser user) {

            this.name = name;
            this.keyword = keyword;
            this.contentType = contentType;
            this.page = page;
            this.adminFlag = adminFlag;
            this.size = size;
            this.tagString = tagString;
            this.titlePower = titlePower;
            this.contentPower = contentPower;
            this.tagsPower = tagsPower;
            this.categoryPower = categoryPower;
            this.folderIds = folderIds;
            this.order = order;
            this.totalHits = 0;
            this.user = user;
        }

        public void run() {
            switch (name) {
                case "multiMatch":
                    response = esUtil.multiMatchQuerySeachStrengthen(keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower,
                            categoryPower, folderIds, order, true, "cross_fields");
                    this.totalHits = response.getTotal();
                    break;
                case "multiPhraseMatch":
                    // response = esHomePageUtil.multiMatchQuery(keyword, page, size, order, startDate, endDate, contentType, uploadDeptId, uploadUserId, fileType, false, "phrase", user);
                    response = esUtil.multiMatchQuerySeachStrengthen(keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower,
                            categoryPower, folderIds, order, false, "phrase");
                    this.totalHits = response.getTotal();
                    break;
                case "titlePhraseMatch":
                    response = esUtil.boolQuerySeachStrengthen(keyword, contentType, page, adminFlag, size, tagString, titlePower, contentPower, tagsPower,
                            categoryPower, folderIds, order);
                    // response = esHomePageUtil.MatchPhraseQuery("title", keyword, page, size, order, startDate, endDate, contentType, uploadDeptId, uploadUserId, fileType, user);
                    this.totalHits = response.getTotal();
                    break;
                default:
                    response = null;
                    break;
            }
        }
    }
}
