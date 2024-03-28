package com.jxdinfo.doc.timer.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.apache.poi.ss.formula.functions.T;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author yjs
 * @Description:
 * @Date: 2018/11/23 10:00
 */
public class SaveIndexCache implements BaseJob {

    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    /**
     * 文件统计接口
     */

    private HussarCacheManager hussarCacheManager = appCtx.getBean(HussarCacheManager.class);

    private FileStatisticsService fileStatisticsService = appCtx.getBean(FileStatisticsService.class);

    private FrontTopicService frontTopicService = appCtx.getBean(FrontTopicService.class);

    private BusinessService businessService = appCtx.getBean(BusinessService.class);

    private FrontDocGroupService frontDocGroupService = appCtx.getBean(FrontDocGroupService.class);

    /**
     * 执行代码
     *
     * @param context
     * @throws JobExecutionException
     * @Title: execute
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        saveIndexCache();
        System.out.println("===========定时器执行===========" + CacheConstant.DOC_TOTAL_COUNT);
    }

    public void saveIndexCache() {
        int num = fileStatisticsService.getFilesCount();
        hussarCacheManager.setObject(CacheConstant.DOC_TOTAL_COUNT, CacheConstant.PREX_DOC_TOTAL_COUNT, num);
        List<Map> uploadDataList = new ArrayList<>();
        uploadDataList = fileStatisticsService.getUploadData("user");
        hussarCacheManager.setObject(CacheConstant.UPLOAD_DATA_LIST,
                CacheConstant.PREX_UPLOAD_DATA_LIST, uploadDataList);
        List<Map> hotDocList = new ArrayList<>();
        Page<T> page = new Page<>(1, 5);
        hotDocList = fileStatisticsService.getFileListDataAllPerson(page, "3");
        hussarCacheManager.setObject(CacheConstant.HOT_DOC_LIST,
                CacheConstant.PREX_HOT_DOC_LIST, hotDocList);
//        List<SpecialTopic> topicList = new ArrayList<>();
//        topicList = frontTopicService.getTopicList(0, 5);
//        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
//        List<String> listGroup = frontDocGroupService.getPremission(userId);
//        if (topicList != null && topicList.size() > 0) {
//            for (SpecialTopic specialTopic : topicList) {
//                String topicId = specialTopic.getTopicId();
//                String topicCover = null;
//                try {
//                    topicCover = URLEncoder.encode(specialTopic.getTopicCover(), "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                specialTopic.setTopicCover(topicCover);
//                FsFolderParams fsFolderParams = new FsFolderParams();
//                fsFolderParams.setGroupList(listGroup);
//                fsFolderParams.setUserId(userId);
//                fsFolderParams.setType("2");
//                String levelCode = businessService.getLevelCodeByUser(fsFolderParams);
//                //测试不考虑专题权限,取专题下面的文档
//                List<FsFile> docList = frontTopicService.getDocByTopicId(topicId, "create_time", 0, 4, userId, listGroup, levelCode);
//                specialTopic.setDocList(docList);
//            }
//        }
//        hussarCacheManager.setObject(CacheConstant.TOPIC_DOC_LIST,
//                CacheConstant.PREX_TOPIC_DOC_LIST, topicList);
    }

}
