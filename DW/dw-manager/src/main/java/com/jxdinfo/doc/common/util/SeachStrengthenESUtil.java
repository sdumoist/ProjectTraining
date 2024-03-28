package com.jxdinfo.doc.common.util;

import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类的用途：ElasticSearch 工具类<p>
 * 创建日期：2018年8月28日 <br>
 * 修改历史：<br>
 * 修改日期：2018年8月28日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 *
 * @author WangBinBin
 * @version 1.0
 */
@Service
public class SeachStrengthenESUtil {

    @Value("${docbase.es-base}")
    private String INDEX_OF_DOCBASE;
    @Value("${docbase.username}")
    private String userName;
    @Value("${docbase.password}")
    private String password;
    @Value("${docbase.es-address}")
    private String hostStr;
    @Value("${docbase.es-port}")
    private String esPort;
    private static ApplicationContext appCtx = SpringContextHolder.getApplicationContext();
    private Environment environment = appCtx.getBean(Environment.class);
    @Autowired
    private static RestHighLevelClient client;
    @Value("${examine.using}")
    private boolean examineUsingFlag;

    @Resource
    private DocInfoMapper docInfoMapper;

    private String TYPE_OF_DOCBASE = "doc";

    private String[] FIELDS_OF_DOC = {"title", "category", "tags", "content", "readType", "title.pinyin"};
    private String SUGGEST = "title.suggest";

    private int SIZE_OF_PAGE = 10;

    private String hosts;


    /**
     * 单例，只在项目启动过程初始化一遍TransportClient
     *
     * @Title:ESUtil
     */
    public SeachStrengthenESUtil() {
        if (client == null) {
            client = getClient();

        }
    }


    /**
     * 只调用一次，用于初始化ES索引
     *
     * @throws IOException
     */
    public void createIndex() throws IOException {
//
//        if (this.client.admin().indices().prepareExists(INDEX_OF_DOCBASE).get().isExists()) {
//            this.client.admin().indices().prepareDelete(INDEX_OF_DOCBASE).get();
//        }
//        Settings settings = Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2)
//                .build();
//
//        Map<String, Object> mappings = new HashMap<>();
//        Map<String, Object> types = new HashMap<>();
//        Map<String, Object> properties = new HashMap<>();
//        for (String field : FIELDS_OF_DOC) {
//            Map<String, Object> property = new HashMap<>();
//            property.put("type", "text");
//            property.put("analyzer", "ik_max_word");
//            property.put("search_analyzer", "ik_max_word");
//            properties.put(field, property);
//        }
//        types.put("properties", properties);
//        mappings.put(TYPE_OF_DOCBASE, types);
//
//        this.client.admin().indices().prepareCreate(INDEX_OF_DOCBASE).setSettings(settings)
//                .addMapping(TYPE_OF_DOCBASE, mappings).get();
    }

    /**
     * 为文档创建索引
     *
     * @param docId
     * @param source
     * @return 新建索引时返回201 索引存在时返回200
     */
    public int index(String docId, Map<String, Object> source) {
        //System.out.println("传输的值为：" + source);
        IndexRequest request = new IndexRequest(INDEX_OF_DOCBASE).id(docId).source(source);
        IndexResponse response = null;
        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    /*    this.client.prepareIndex(INDEX_OF_DOCBASE, TYPE_OF_DOCBASE, docId).setSource(source)
                .get();*/
        RestStatus status = response.status();
        return status.getStatus();
    }

    /**
     * 获取索引
     * *@param docId  索引ID
     *
     * @return 索引信息
     */
    public Map<String, Object> getIndex(String docId) {
        GetRequest request = new GetRequest(INDEX_OF_DOCBASE).id(docId);
        GetResponse response = null;
        try {
            response = this.client.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.isExists()) {
            return response.getSourceAsMap();
        }
        return null;
    }

    /**
     * 将一个文件索引复制到另一个文件中
     * *@param oldDocId  已经存在的docID
     *
     * @param newDocId 新上传的docID
     * @return 新建索引时返回201 索引存在时返回200
     */
    public int copyIndex(String oldDocId, String newDocId) {
        //根据已经存在的文件ID获取文件索引信息
        GetRequest request = new GetRequest(INDEX_OF_DOCBASE).id(oldDocId);
        GetResponse response = null;
        try {
            response = this.client.get(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> sourceMap = response.getSourceAsMap();
        System.out.println("****************************************************************************秒传是输出的索引：");
        sourceMap.put("recycle", "1");
        sourceMap.put("folderId", "");
        //将获取的索引信息存放到新文件中
        IndexRequest indexRequest = new IndexRequest(INDEX_OF_DOCBASE).id(newDocId).source(sourceMap);
        IndexResponse indexResponse = null;
        try {
            indexResponse = this.client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestStatus status = indexResponse.status();

        return status.getStatus();
    }

    /**
     * 根据索引数据id删除索引
     *
     * @param docId
     * @return
     * @Title: deleteIndex
     * @author: WangBinBin
     */
    public int deleteIndex(String docId) {
        DeleteRequest deleteRequest = new DeleteRequest(INDEX_OF_DOCBASE).id(docId);
        DeleteResponse response = null;
        try {
            response = this.client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RestStatus status = response.status();
        return status.getStatus();
    }

    /**
     * 根据索引数据ID更新索引
     *
     * @param docId
     * @param source
     * @return
     * @Title: updateIndex
     * @author: WangBinBin
     */
    public int updateIndex(String docId, Map<String, Object> source) {
        UpdateRequest request = new UpdateRequest(INDEX_OF_DOCBASE, docId).doc(source);
        UpdateResponse response = null;
        try {
            response = this.client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RestStatus status = response.status();
        return status.getStatus();
    }


    /*  *//**
     *  根据索引数据ID更新索引,存在则更新，不存在则新增
     * @Title: upsertIndex
     * @author: WangBinBin
     * @param docId
     * @param source
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     *//*
    public int upsertIndex(String docId, Map<String, Object> source) throws ExecutionException, InterruptedException {
        IndexRequest indexRequest = new IndexRequest(INDEX_OF_DOCBASE, TYPE_OF_DOCBASE, docId).source(source);
        UpdateRequest updateRequest = new UpdateRequest(INDEX_OF_DOCBASE, TYPE_OF_DOCBASE, docId).doc(source)
                .upsert(indexRequest);
        UpdateResponse response = this.client.update(updateRequest).get();
        return response.status().getStatus();
    }*/

    /**
     * 处理高亮显示字段
     *
     * @param response
     * @return
     * @Title: handleHits
     * @author: WangBinBin
     */
    private ESResponse<Map<String, Object>> handleHits(SearchResponse response) {

        SearchHits hits = response.getHits();
        ESResponse<Map<String, Object>> result = new ESResponse<>();
        long totalHits = hits.getTotalHits().value;
        result.setTotal(totalHits);
        int totalPage = (int) (totalHits / 10) + 1;
        result.setTotalPages(totalPage);

        List<Map<String, Object>> items = result.getItems();

        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();// 每个查询对象
            Map<String, Object> source = searchHit.getSourceAsMap();
            source.put("id", searchHit.getId());
            // 将高亮处理后的内容，替换原有内容（原有内容，可能会出现显示不全）
            Map<String, HighlightField> hightlightFields = searchHit.getHighlightFields();
            Iterator<String> hightlightKeys = hightlightFields.keySet().iterator();
            while (hightlightKeys.hasNext()) {
                String key = hightlightKeys.next();
                HighlightField field = hightlightFields.get(key);
                // 获取到原有内容中 每个高亮显示 集中位置fragment就是高亮片段
                Text[] fragments = field.fragments();
                StringBuffer sb = new StringBuffer();
                for (Text text : fragments) {
                    sb.append(text);
                }
                source.put(key, sb.toString());
            }
            items.add(source);
        }

        return result;
    }

    public List<String> getCompletionSuggest(Integer size,
                                             String prefix) {
        List<String> listStr = new ArrayList<>();

        SuggestBuilder suggest = null;
        suggest = new SuggestBuilder();
        CompletionSuggestionBuilder suggestion = SuggestBuilders.completionSuggestion("title.suggestpinyin").prefix(prefix).size(size);
        suggest.addSuggestion("my-suggest", suggestion);
        //设置去重
        suggestion.skipDuplicates(true);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.suggest(suggest);
        SearchRequest request = new SearchRequest(INDEX_OF_DOCBASE).source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Suggest suggestBean = response.getSuggest();
        List list = response.getSuggest().getSuggestion("my-suggest").getEntries().get(0).getOptions();
        for (int i = 0; i < list.size(); i++) {
            listStr.add(String.valueOf(response.getSuggest().getSuggestion("my-suggest").getEntries().get(0).getOptions().get(i).getText()));
        }
        return listStr;

    }


    private SearchResponse doQuery(QueryBuilder queryBuilder, int page, Integer size, Integer order) {
        if (size == null) {
            size = SIZE_OF_PAGE;
        }
        // 启用高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String field : FIELDS_OF_DOC) {
            HighlightBuilder.Field fld = new HighlightBuilder.Field(field);
            fld.highlighterType("unified"); // 高亮器类型 （1）Unified高亮器 （2）Plain高亮器  （3）fvh高亮器
            highlightBuilder.field(fld);
        }
        String[] source = new String[]{"title", "upDate"};
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.fetchSource(source, new String[0]);
        if (order == null || order == 1) {
            searchSourceBuilder.from((page - 1) * size).size(size).highlighter(highlightBuilder).query(queryBuilder).sort("_score", SortOrder.DESC).sort("upDate", SortOrder.DESC);
        } else if (order == 2) {
            searchSourceBuilder.from((page - 1) * size).size(size).highlighter(highlightBuilder).query(queryBuilder).sort("upDate", SortOrder.DESC)
                    .sort("_score", SortOrder.DESC);
        }else if(order == 3){
            searchSourceBuilder.from((page - 1) * size).size(size).highlighter(highlightBuilder).query(queryBuilder).sort("upDate", SortOrder.ASC).sort("_score", SortOrder.DESC);
        }
        SearchRequest request = new SearchRequest(INDEX_OF_DOCBASE).source(searchSourceBuilder);
        try {
            return client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 单字段查询
     *
     * @param field
     * @param keyword
     */
    public ESResponse<Map<String, Object>> matchQuery(String field, String keyword, int page, int size) {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery(field, keyword);
        SearchResponse response = doQuery(queryBuilder, page, size, null);
        return handleHits(response);
    }

    /**
     * 多字段查询
     *
     * @param keyword
     * @param page
     * @param adminFlag
     * @return
     * @Title: multiMatchQuery
     * @author: WangBinBin
     */
    public ESResponse<Map<String, Object>> multiMatchQuery(String keyword, int page, Boolean adminFlag, Integer size, Integer titlePower, Integer contentPower, Integer tagsPower, Integer categoryPower,String folderIds) {

        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission();
        // 关键字进行过滤
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("ik_max_word");
        if (titlePower != null) {
            queryBuilder.field("title", (float) titlePower);
        }
        if (contentPower != null) {
            queryBuilder.field("content", (float) contentPower);
        }
        if (tagsPower != null) {
            queryBuilder.field("tags", (float) tagsPower);
        }
        if (categoryPower != null) {
            queryBuilder.field("category", (float) categoryPower);
        }
        // 权限条件过滤 因为es会自动对中文进行分词，所以在此处权限查询的时候需要加.keyword。防止分词
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder);
        // 如果是文库超级管理员，不加权限限制
        if (folderIds != null) {
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            if (!adminFlag) {

                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }


    /**
     * 多字段查询
     *
     * @param keyword
     * @param page
     * @param adminFlag
     * @return
     * @Title: multiMatchQuery
     * @author: WangBinBin
     */
    public ESResponse<Map<String, Object>> multiMatchQuery(String keyword, int page, Boolean adminFlag, Integer size, String userId, String folderIds) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission(userId);
        // 关键字进行过滤
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("ik_max_word");
        queryBuilder.field("title", 5.0F);

        // 权限条件过滤 因为es会自动对中文进行分词，所以在此处权限查询的时候需要加.keyword。防止分词
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder);

        // 如果是文库超级管理员，不加权限限制
        if (folderIds != null && !folderIds.equals("") && folderIds.length() > 1) {
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            if (!adminFlag) {
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }

    /**
     * 多字段查询
     *
     * @param keyword
     * @param page
     * @param adminFlag
     * @return
     * @Title: multiMatchQuery
     * @author: WangBinBin
     */
    public ESResponse<Map<String, Object>> multiMatchQueryMobile(String keyword, int page, Boolean adminFlag, Integer size, String userId, String folderIds, List<String> folderExtranetIds) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission(userId);
        // 关键字进行过滤
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("ik_max_word");
        queryBuilder.field("title", 5.0F);

        // 权限条件过滤 因为es会自动对中文进行分词，所以在此处权限查询的时候需要加.keyword。防止分词
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder);

        // 如果是文库超级管理员，不加权限限制
        if (folderIds != null && !folderIds.equals("") && folderIds.length() > 1) {
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            if (!adminFlag) {
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }

        if(!adminFlag){
            if(folderExtranetIds!=null && folderExtranetIds.size()>0){
                TermsQueryBuilder folderExtranetQueryBuilder = QueryBuilders.termsQuery("folderId", folderExtranetIds);
                boolQueryBuilder.must(folderExtranetQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }

    public ESResponse<Map<String, Object>> onlyMatchWordQuery(String keyword, int size) {
        //对标题进行关键字匹配
        MatchPhraseQueryBuilder termQueryBuilder = QueryBuilders.matchPhraseQuery("title", keyword);
        //对标签进行关键字匹配
        MatchPhraseQueryBuilder termQueryBuilder2 = QueryBuilders.matchPhraseQuery("tags", keyword);
        //对文档内容进行关键字匹配
        MatchPhraseQueryBuilder termQueryBuilder3 = QueryBuilders.matchPhraseQuery("content", keyword);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().should(termQueryBuilder).should(termQueryBuilder2).should(termQueryBuilder3);
        SearchResponse response = doQuery(boolQueryBuilder, 1, size, null);
        return handleHits(response);
    }

    /**
     * 带文件类型过滤查询
     *
     * @param keyword
     * @param contentType
     * @param page
     * @return
     */
    public ESResponse<Map<String, Object>> boolQuery(String keyword, String contentType, int page,
                                                     Boolean adminFlag, Integer size, String tagString, Integer titlePower, Integer contentPower, Integer tagsPower, Integer categoryPower, String folderIds, Integer order) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission();
        // 关键字进行过滤  ik_max_word: 最细粒度拆分 ik_smart: 粗粒度的拆分  FIELDS_OF_DOC：搜索的字段
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("by_max_word");
        // 设置字段的权重 权重高的查询出来的排名靠前
        if (titlePower != null) {
            queryBuilder.field("title", (float) titlePower);
        } else {
            queryBuilder.field("title", (float) 5.0);
        }
        if (contentPower != null) {
            queryBuilder.field("content", (float) contentPower);
        }
        if (tagsPower != null) {
            queryBuilder.field("tags", (float) tagsPower);
        }
        if (categoryPower != null) {
            queryBuilder.field("category", (float) categoryPower);
        }
        QueryBuilder queryBase = null;

        //关键词是数字的模糊查询
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(keyword);
        if (StringUtils.isNotEmpty(keyword) && m.matches()){
        WildcardQueryBuilder wildcardQueryBuilder =QueryBuilders.wildcardQuery("title.keyword","*"+keyword+"*");
        queryBase = QueryBuilders.boolQuery().should(queryBuilder).should(wildcardQueryBuilder);
        } else {
            queryBase = QueryBuilders.boolQuery().must(queryBuilder);
        }

        // 权限条件过滤  keyword: 精确查询 不能分词    termsQuery: 类似sql  in 查询功能
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1  termQuery精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        // 文件类型过滤
        //文档  word：application/msword 、application/vnd.openxmlformats-officedocument.wordprocessingml.document  、application/rtf
        //    Excel：spreadsheetml     PPT：application/vnd.openxmlformats-officedocument.presentationml.presentation
        //      pdf:application/pdf    txt：text/plain
        //     图片：image       视频：video/mp4           音频：application/octet-stream.text/plain，audio/mpeg
        TermsQueryBuilder queryStringQueryBuilder1 = null;
        QueryBuilder queryStringQueryBuilder2 = null;
        QueryBuilder queryStringQueryBuilder3 = null;
        BoolQueryBuilder boolQueryBuilder = null;
        /**
         * contentType
         * 查询全部 notimage image
         * 查询文档 allword
         * 查询图片 image
         * 查询视频 video
         * 查询音频 audio
         */
        if ("allword".equals(contentType)) {//对文档进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/octet-stream","application/msword", "spreadsheetml",
                    "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/rtf",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("video".equals(contentType)) {  //对视频进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/mp4", "video/avi", "video/mp4",
                    "video/mpeg4", "video/mpeg4","video/mpeg", "video/x-ms-wmv", "video/x-sgi-movie","application/x-matroska","application/x-shockwave-flash");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("component".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "component");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);

        /* else if ("qa".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "qa");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);

        }*/
        }else if ("notimage".equals(contentType)) { //对非图片进行过滤
            contentType = "image";
            String psd = "contentType:*photoshop*";
            String docType = "contentType:*" + contentType + "*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }

            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).mustNot(boolQueryBuilder2);
        } else if ("image".equals(contentType)) {   //对图片进行过滤
            String docType = "contentType:*" + contentType + "*";
            String psd = "contentType:*photoshop*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).
                    must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            //对图片传输的标签进行判定
            if (tagString != null && !"".equals(tagString) && tagString.split("\\|").length != 0) {
                //将前台拼装的分类传过来
                String[] strs = tagString.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    // 记录一个分类中多个标签的情况
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = strs[i].split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);

                }

//                if(strs.length==0){
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
//                }else if(strs.length==1){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*铁路*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*电力*");
//                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().should(queryImage1).should(queryImage2);
//                    boolQueryBuilder =  QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(boolQueryBuilder2);
//                }else if(strs.length==2){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2);
//                }else if(strs.length==3){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    QueryBuilder queryImage3 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[2]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2).must(queryImage3);
//                }
            }
        }else if ("entry".equals(contentType)) {
                String docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);

                if (tagString != null && !"".equals(tagString) && tagString.split(",").length != 0) {
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = tagString.split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);
                }

        } else { //其他过滤
            String docType = "";
            String docType1 = "";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                if (contentType.equals("presentationml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "powerpoint";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else if (contentType.equals("spreadsheetml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "excel";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
                }

            } else {
                docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
            }


        }
//        String docType = "contentType:*" + contentType + "*";
//        if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
//            docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
//        }
//        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(docType);
//        QueryBuilder queryStringQueryBuilder = QueryBuilders.wildcardQuery("contentType", "*word*");

        // folderIds 有管理权限的目录id
        if (folderIds != null&&!folderIds.equals("")&&folderIds.length()>1) {
            folderIds = folderIds.substring(1);
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            // 不是超级管理员
            if (!adminFlag) {
                // 只能查询有权限的文档  termsQueryBuilder2: 有权限的目录id   termsQueryBuilder:文件权限
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                //  termsQueryBuilder:文件权限
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, order);
        return handleHits(response);
    }

    /**
     * 带文件类型过滤查询
     *
     * @param keyword
     * @param contentType
     * @param page
     * @return
     */
    public ESResponse<Map<String, Object>> boolQuery(String keyword, String contentType, int page,
                                                     Boolean adminFlag, Integer size, String tagString, String userId, String folderIds) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission(userId);
        // 关键字进行过滤  ik_max_word: 最细粒度拆分 ik_smart: 粗粒度的拆分  FIELDS_OF_DOC：搜索的字段
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("by_max_word");
        // 设置字段的权重 权重高的查询出来的排名靠前
        queryBuilder.field("title", (float) 5.0);

        // 权限条件过滤  keyword: 精确查询 不能分词    termsQuery: 类似sql  in 查询功能
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1  termQuery精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        // 文件类型过滤
        //文档  word：application/msword 、application/vnd.openxmlformats-officedocument.wordprocessingml.document  、application/rtf
        //    Excel：spreadsheetml     PPT：application/vnd.openxmlformats-officedocument.presentationml.presentation
        //      pdf:application/pdf    txt：text/plain
        //     图片：image       视频：video/mp4           音频：application/octet-stream.text/plain，audio/mpeg
        TermsQueryBuilder queryStringQueryBuilder1 = null;
        QueryBuilder queryStringQueryBuilder2 = null;
        QueryBuilder queryStringQueryBuilder3 = null;
        BoolQueryBuilder boolQueryBuilder = null;
        /**
         * contentType
         * 查询全部 notimage image
         * 查询文档 allword
         * 查询图片 image
         * 查询视频 video
         * 查询音频 audio
         */
        if ("allword".equals(contentType)) {//对文档进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/msword", "spreadsheetml",
                    "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/rtf",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("video".equals(contentType)) {  //对视频进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/mp4", "video/avi", "video/mp4",
                    "video/mpeg4", "video/mpeg4", "video/x-ms-wmv", "video/x-sgi-movie");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("component".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "component");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("notimage".equals(contentType)) { //对非图片进行过滤
            contentType = "image";
            String psd = "contentType:*photoshop*";
            String docType = "contentType:*" + contentType + "*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }

            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).mustNot(boolQueryBuilder2);
        } else if ("image".equals(contentType)) {   //对图片进行过滤
            String docType = "contentType:*" + contentType + "*";
            String psd = "contentType:*photoshop*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).
                    must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            //对图片传输的标签进行判定
            if (tagString != null && !"".equals(tagString) && tagString.split("\\|").length != 0) {
                //将前台拼装的分类传过来
                String[] strs = tagString.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    // 记录一个分类中多个标签的情况
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = strs[i].split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);

                }

//                if(strs.length==0){
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
//                }else if(strs.length==1){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*铁路*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*电力*");
//                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().should(queryImage1).should(queryImage2);
//                    boolQueryBuilder =  QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(boolQueryBuilder2);
//                }else if(strs.length==2){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2);
//                }else if(strs.length==3){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    QueryBuilder queryImage3 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[2]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2).must(queryImage3);
//                }
            }
        } else { //其他过滤
            String docType = "";
            String docType1 = "";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                if (contentType.equals("presentationml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "powerpoint";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(query);
                } else if (contentType.equals("spreadsheetml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "excel";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(query);
                } else {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
                }

            } else {
                docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
            }


        }
//        String docType = "contentType:*" + contentType + "*";
//        if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
//            docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
//        }
//        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(docType);
//        QueryBuilder queryStringQueryBuilder = QueryBuilders.wildcardQuery("contentType", "*word*");

        // folderIds 有管理权限的目录id
        if (folderIds != null && !folderIds.equals("") && folderIds.length() > 1) {
            folderIds = folderIds.substring(1);
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            // 不是超级管理员
            if (!adminFlag) {
                // 只能查询有权限的文档  termsQueryBuilder2: 有权限的目录id   termsQueryBuilder:文件权限
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                //  termsQueryBuilder:文件权限
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }


    /**
     * 手机端
     * 带文件类型过滤查询
     *
     * @param keyword
     * @param contentType
     * @param page
     * @return
     */
    public ESResponse<Map<String, Object>> boolQueryMobile(String keyword, String contentType, int page,
                                                     Boolean adminFlag, Integer size, String tagString, String userId, String folderIds, List<String> folderExtranetIds) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission(userId);
        // 关键字进行过滤  ik_max_word: 最细粒度拆分 ik_smart: 粗粒度的拆分  FIELDS_OF_DOC：搜索的字段
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("by_max_word");
        // 设置字段的权重 权重高的查询出来的排名靠前
        queryBuilder.field("title", (float) 5.0);

        // 权限条件过滤  keyword: 精确查询 不能分词    termsQuery: 类似sql  in 查询功能
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1  termQuery精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        // 文件类型过滤
        //文档  word：application/msword 、application/vnd.openxmlformats-officedocument.wordprocessingml.document  、application/rtf
        //    Excel：spreadsheetml     PPT：application/vnd.openxmlformats-officedocument.presentationml.presentation
        //      pdf:application/pdf    txt：text/plain
        //     图片：image       视频：video/mp4           音频：application/octet-stream.text/plain，audio/mpeg
        TermsQueryBuilder queryStringQueryBuilder1 = null;
        QueryBuilder queryStringQueryBuilder2 = null;
        QueryBuilder queryStringQueryBuilder3 = null;
        BoolQueryBuilder boolQueryBuilder = null;
        /**
         * contentType
         * 查询全部 notimage image
         * 查询文档 allword
         * 查询图片 image
         * 查询视频 video
         * 查询音频 audio
         */
        if ("allword".equals(contentType)) {//对文档进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/msword", "spreadsheetml",
                    "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/rtf",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("video".equals(contentType)) {  //对视频进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/mp4", "video/avi", "video/mp4",
                    "video/mpeg4", "video/mpeg4", "video/x-ms-wmv", "video/x-sgi-movie");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("component".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "component");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("notimage".equals(contentType)) { //对非图片进行过滤
            contentType = "image";
            String psd = "contentType:*photoshop*";
            String docType = "contentType:*" + contentType + "*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }

            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).mustNot(boolQueryBuilder2);
        } else if ("image".equals(contentType)) {   //对图片进行过滤
            String docType = "contentType:*" + contentType + "*";
            String psd = "contentType:*photoshop*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).
                    must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            //对图片传输的标签进行判定
            if (tagString != null && !"".equals(tagString) && tagString.split("\\|").length != 0) {
                //将前台拼装的分类传过来
                String[] strs = tagString.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    // 记录一个分类中多个标签的情况
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = strs[i].split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);

                }

//                if(strs.length==0){
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
//                }else if(strs.length==1){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*铁路*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*电力*");
//                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().should(queryImage1).should(queryImage2);
//                    boolQueryBuilder =  QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(boolQueryBuilder2);
//                }else if(strs.length==2){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2);
//                }else if(strs.length==3){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    QueryBuilder queryImage3 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[2]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2).must(queryImage3);
//                }
            }
        } else { //其他过滤
            String docType = "";
            String docType1 = "";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                if (contentType.equals("presentationml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "powerpoint";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(query);
                } else if (contentType.equals("spreadsheetml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "excel";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(query);
                } else {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
                }

            } else {
                docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
            }


        }
//        String docType = "contentType:*" + contentType + "*";
//        if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
//            docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
//        }
//        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(docType);
//        QueryBuilder queryStringQueryBuilder = QueryBuilders.wildcardQuery("contentType", "*word*");

        // folderIds 有管理权限的目录id
        if (folderIds != null && !folderIds.equals("") && folderIds.length() > 1) {
            folderIds = folderIds.substring(1);
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            // 不是超级管理员
            if (!adminFlag) {
                // 只能查询有权限的文档  termsQueryBuilder2: 有权限的目录id   termsQueryBuilder:文件权限
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                //  termsQueryBuilder:文件权限
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        // 普通用户 如果是外网访问 只能查询配置了外网访问权限的目录
        if(!adminFlag){
            if(folderExtranetIds!=null && folderExtranetIds.size()>0){
                TermsQueryBuilder folderExtranetQueryBuilder = QueryBuilders.termsQuery("folderId", folderExtranetIds);
                boolQueryBuilder.must(folderExtranetQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }

    /**
     * 关闭链接
     *
     * @Title: close
     * @author: WangBinBin
     */
    public void close() {
        if (this.client != null) {
            try {
                this.client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\123\\8.mp4");
        Encoder encoder = new Encoder();
        MultimediaInfo info = null;
        try {
            info = encoder.getInfo(file);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        VideoInfo video = info.getVideo();
        String decoder = video.getDecoder();
        System.out.println(decoder);


    }

    public RestHighLevelClient getClient() {
        hostStr = environment.getProperty("docbase.es-address");
        userName = environment.getProperty("docbase.username");
        password = environment.getProperty("docbase.password");
        esPort = environment.getProperty("docbase.es-port");
        String[] hosts = hostStr.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            httpHosts[i] = new HttpHost(hosts[i], Integer.parseInt(esPort), "http");
        }

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        RestClientBuilder builder = RestClient.builder(httpHosts);
        builder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(-1);
                requestConfigBuilder.setSocketTimeout(-1);
                requestConfigBuilder.setConnectionRequestTimeout(-1);
                return requestConfigBuilder;
            }
        });
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.disableAuthCaching();
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }


    /**
     * 多字段查询
     *
     * @param keyword
     * @param page
     * @param adminFlag
     * @return
     * @Title: multiMatchQuery
     * @author: WangBinBin
     */
    /*public ESResponse<Map<String, Object>> multiMatchQuerySeachStrengthen(String keyword, int page, Boolean adminFlag, Integer size, Integer titlePower, Integer contentPower, Integer tagsPower,
                                                                          Integer categoryPower,String folderIds, boolean analyze, String type){

        // 关键字进行过滤
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).type(type);
        if (analyze) {
            queryBuilder.analyzer("ik_smart");  // query分词
        } else {
            queryBuilder.analyzer("keyword");  // query不分词
        }
        queryBuilder.operator(Operator.OR).minimumShouldMatch("2");  // 必须匹配2个term以上

        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission();
        // 关键字进行过滤
        // MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("ik_max_word");
        if (titlePower != null) {
            queryBuilder.field("title", (float) titlePower);
            //queryBuilder.field("title", (float) 5.0);
        }
        if (contentPower != null) {
            queryBuilder.field("content", (float) contentPower);
        }
        if (tagsPower != null) {
            queryBuilder.field("tags", (float) tagsPower);
        }
        if (categoryPower != null) {
            queryBuilder.field("category", (float) categoryPower);
        }
        queryBuilder.fuzzyTranspositions(false);
        // 权限条件过滤 因为es会自动对中文进行分词，所以在此处权限查询的时候需要加.keyword。防止分词
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder);
        // 如果是文库超级管理员，不加权限限制
        if (folderIds != null) {
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            if (!adminFlag) {

                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, null);
        return handleHits(response);
    }*/

    /**
     * 带文件类型过滤查询
     *
     * @param keyword
     * @param contentType
     * @param page
     * @return
     */
    public ESResponse<Map<String, Object>> multiMatchQuerySeachStrengthen(String keyword, String contentType, int page,
                                                         Boolean adminFlag, Integer size, String tagString, Integer titlePower, Integer contentPower,
                                                                          Integer tagsPower, Integer categoryPower, String folderIds, Integer order,
                                                                          boolean analyze, String type) {
        // 关键字进行过滤
        MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).type(type);
        if (analyze) {
            queryBuilder.analyzer("ik_smart");  // query分词
        } else {
            queryBuilder.analyzer("keyword");  // query不分词
        }
        queryBuilder.operator(Operator.OR).minimumShouldMatch("2");  // 必须匹配2个term以上

        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission();
        // 关键字进行过滤  ik_max_word: 最细粒度拆分 ik_smart: 粗粒度的拆分  FIELDS_OF_DOC：搜索的字段
        // MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("by_max_word");
        // 设置字段的权重 权重高的查询出来的排名靠前
        if (titlePower != 10000) {
            queryBuilder.field("title", (float) titlePower);
        } else {
            queryBuilder.field("title", (float) 5.0);
        }
        if (contentPower != 10000) {
            queryBuilder.field("content", (float) contentPower);
        }
        if (tagsPower != 10000) {
            queryBuilder.field("tags", (float) tagsPower);
        }
        if (categoryPower != 10000) {
            queryBuilder.field("category", (float) categoryPower);
        }
        if(order == 10000){
            order = null;
        }
        queryBuilder.fuzzyTranspositions(false);
        QueryBuilder queryBase = null;

        //关键词是数字的模糊查询
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(keyword);
        if (StringUtils.isNotEmpty(keyword) && m.matches()){
            WildcardQueryBuilder wildcardQueryBuilder =QueryBuilders.wildcardQuery("title.keyword","*"+keyword+"*");
            queryBase = QueryBuilders.boolQuery().should(queryBuilder).should(wildcardQueryBuilder);
        } else {
            queryBase = QueryBuilders.boolQuery().must(queryBuilder);
        }

        // 权限条件过滤  keyword: 精确查询 不能分词    termsQuery: 类似sql  in 查询功能
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1  termQuery精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        // 文件类型过滤
        //文档  word：application/msword 、application/vnd.openxmlformats-officedocument.wordprocessingml.document  、application/rtf
        //    Excel：spreadsheetml     PPT：application/vnd.openxmlformats-officedocument.presentationml.presentation
        //      pdf:application/pdf    txt：text/plain
        //     图片：image       视频：video/mp4           音频：application/octet-stream.text/plain，audio/mpeg
        TermsQueryBuilder queryStringQueryBuilder1 = null;
        QueryBuilder queryStringQueryBuilder2 = null;
        QueryBuilder queryStringQueryBuilder3 = null;
        BoolQueryBuilder boolQueryBuilder = null;
        /**
         * contentType
         * 查询全部 notimage image
         * 查询文档 allword
         * 查询图片 image
         * 查询视频 video
         * 查询音频 audio
         */
        if ("allword".equals(contentType)) {//对文档进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/octet-stream","application/msword", "spreadsheetml",
                    "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/rtf",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("video".equals(contentType)) {  //对视频进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/mp4", "video/avi", "video/mp4",
                    "video/mpeg4", "video/mpeg4","video/mpeg", "video/x-ms-wmv", "video/x-sgi-movie","application/x-matroska","application/x-shockwave-flash");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("component".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "component");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);

        /* else if ("qa".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "qa");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);

        }*/
        }else if ("notimage".equals(contentType)) { //对非图片进行过滤
            contentType = "image";
            String psd = "contentType:*photoshop*";
            String docType = "contentType:*" + contentType + "*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }

            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).mustNot(boolQueryBuilder2);
        } else if ("image".equals(contentType)) {   //对图片进行过滤
            String docType = "contentType:*" + contentType + "*";
            String psd = "contentType:*photoshop*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).
                    must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            //对图片传输的标签进行判定
            if (tagString != null && !"".equals(tagString) && tagString.split("\\|").length != 0) {
                //将前台拼装的分类传过来
                String[] strs = tagString.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    // 记录一个分类中多个标签的情况
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = strs[i].split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);

                }

//                if(strs.length==0){
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
//                }else if(strs.length==1){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*铁路*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*电力*");
//                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().should(queryImage1).should(queryImage2);
//                    boolQueryBuilder =  QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(boolQueryBuilder2);
//                }else if(strs.length==2){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2);
//                }else if(strs.length==3){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    QueryBuilder queryImage3 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[2]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2).must(queryImage3);
//                }
            }
        }else if ("entry".equals(contentType)) {
            String docType = "contentType:*" + contentType + "*";
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);

            if (tagString != null && !"".equals(tagString) && tagString.split(",").length != 0) {
                BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                // 将标签按照逗号分隔开
                String[] strs_tags = tagString.split(",");
                // 将一个分类中的多个标签拼装，达到OR的效果
                for (int j = 0; j < strs_tags.length; j++) {
                    String strs_tag = strs_tags[j];
                    // wildcardQuery 通配符查询
                    builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                }
                //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                boolQueryBuilder = boolQueryBuilder.must(builderTag);
            }

        } else { //其他过滤
            String docType = "";
            String docType1 = "";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                if (contentType.equals("presentationml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "powerpoint";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else if (contentType.equals("spreadsheetml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "excel";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
                }

            } else {
                docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
            }


        }
//        String docType = "contentType:*" + contentType + "*";
//        if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
//            docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
//        }
//        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(docType);
//        QueryBuilder queryStringQueryBuilder = QueryBuilders.wildcardQuery("contentType", "*word*");

        // folderIds 有管理权限的目录id
        if (folderIds != null&&!folderIds.equals("")&&folderIds.length()>1) {
            folderIds = folderIds.substring(1);
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            // 不是超级管理员
            if (!adminFlag) {
                // 只能查询有权限的文档  termsQueryBuilder2: 有权限的目录id   termsQueryBuilder:文件权限
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                //  termsQueryBuilder:文件权限
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, order);
        return handleHitsSeachStrengthen(response);
    }

    /**
     * 带文件类型过滤查询
     *
     * @param keyword
     * @param contentType
     * @param page
     * @return
     */
    public ESResponse<Map<String, Object>> boolQuerySeachStrengthen(String keyword, String contentType, int page,
                                                     Boolean adminFlag, Integer size, String tagString, Integer titlePower, Integer contentPower, Integer tagsPower, Integer categoryPower, String folderIds, Integer order) {
        // 获取当前登录人所拥有的所有权限
        String[] permission = PrivilegeUtil.getPremission();
        // 关键字进行过滤  ik_max_word: 最细粒度拆分 ik_smart: 粗粒度的拆分  FIELDS_OF_DOC：搜索的字段
        // MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword, FIELDS_OF_DOC).analyzer("by_max_word");
        MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("title", keyword);
        // 设置字段的权重 权重高的查询出来的排名靠前

        /*if (titlePower != 10000) {
            queryBuilder.field("title", (float) titlePower);
        } else {
            queryBuilder.field("title", (float) 5.0);
        }
        if (contentPower != 10000) {
            queryBuilder.field("content", (float) contentPower);
        }
        if (tagsPower != 10000) {
            queryBuilder.field("tags", (float) tagsPower);
        }
        if (categoryPower != 10000) {
            queryBuilder.field("category", (float) categoryPower);
        }*/
        if(order == 10000){
            order = null;
        }

        QueryBuilder queryBase = null;

        //关键词是数字的模糊查询
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(keyword);
        if (StringUtils.isNotEmpty(keyword) && m.matches()){
            WildcardQueryBuilder wildcardQueryBuilder =QueryBuilders.wildcardQuery("title.keyword","*"+keyword+"*");
            queryBase = QueryBuilders.boolQuery().should(queryBuilder).should(wildcardQueryBuilder);
        } else {
            queryBase = QueryBuilders.boolQuery().must(queryBuilder);
        }

        // 权限条件过滤  keyword: 精确查询 不能分词    termsQuery: 类似sql  in 查询功能
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("permission.keyword", permission);
        // 回收站条件过滤 不在回收站的为0在回收站的为1  termQuery精确查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("recycle", "1");
        // 文件类型过滤
        //文档  word：application/msword 、application/vnd.openxmlformats-officedocument.wordprocessingml.document  、application/rtf
        //    Excel：spreadsheetml     PPT：application/vnd.openxmlformats-officedocument.presentationml.presentation
        //      pdf:application/pdf    txt：text/plain
        //     图片：image       视频：video/mp4           音频：application/octet-stream.text/plain，audio/mpeg
        TermsQueryBuilder queryStringQueryBuilder1 = null;
        QueryBuilder queryStringQueryBuilder2 = null;
        QueryBuilder queryStringQueryBuilder3 = null;
        BoolQueryBuilder boolQueryBuilder = null;
        /**
         * contentType
         * 查询全部 notimage image
         * 查询文档 allword
         * 查询图片 image
         * 查询视频 video
         * 查询音频 audio
         */
        if ("allword".equals(contentType)) {//对文档进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/octet-stream","application/msword", "spreadsheetml",
                    "application/pdf", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/rtf",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("video".equals(contentType)) {  //对视频进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "application/mp4", "video/avi", "video/mp4",
                    "video/mpeg4", "video/mpeg4","video/mpeg", "video/x-ms-wmv", "video/x-sgi-movie","application/x-matroska","application/x-shockwave-flash");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);
        } else if ("component".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "component");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder1);

        /* else if ("qa".equals(contentType)) {  //对组件进行过滤
            queryStringQueryBuilder1 = QueryBuilders.termsQuery("contentType.keyword", "qa");
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder1);

        }*/
        }else if ("notimage".equals(contentType)) { //对非图片进行过滤
            contentType = "image";
            String psd = "contentType:*photoshop*";
            String docType = "contentType:*" + contentType + "*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }

            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).mustNot(boolQueryBuilder2);
        } else if ("image".equals(contentType)) {   //对图片进行过滤
            String docType = "contentType:*" + contentType + "*";
            String psd = "contentType:*photoshop*";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
            }
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).
                    must(queryStringQueryBuilder2).mustNot(QueryBuilders.queryStringQuery(psd));
            //对图片传输的标签进行判定
            if (tagString != null && !"".equals(tagString) && tagString.split("\\|").length != 0) {
                //将前台拼装的分类传过来
                String[] strs = tagString.split("\\|");
                for (int i = 0; i < strs.length; i++) {
                    // 记录一个分类中多个标签的情况
                    BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                    // 将标签按照逗号分隔开
                    String[] strs_tags = strs[i].split(",");
                    // 将一个分类中的多个标签拼装，达到OR的效果
                    for (int j = 0; j < strs_tags.length; j++) {
                        String strs_tag = strs_tags[j];
                        // wildcardQuery 通配符查询
                        builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                    }
                    //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    boolQueryBuilder = boolQueryBuilder.must(builderTag);

                }

//                if(strs.length==0){
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2);
//                }else if(strs.length==1){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*铁路*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*电力*");
//                    BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery().should(queryImage1).should(queryImage2);
//                    boolQueryBuilder =  QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(boolQueryBuilder2);
//                }else if(strs.length==2){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2);
//                }else if(strs.length==3){
//                    QueryBuilder queryImage1 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[0]+"*");
//                    QueryBuilder queryImage2 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[1]+"*");
//                    QueryBuilder queryImage3 = QueryBuilders.wildcardQuery("tags.keyword", "*"+strs[2]+"*");
//                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBuilder).must(termQueryBuilder).must(queryStringQueryBuilder2).must(queryImage1).must(queryImage2).must(queryImage3);
//                }
            }
        }else if ("entry".equals(contentType)) {
            String docType = "contentType:*" + contentType + "*";
            queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
            boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);

            if (tagString != null && !"".equals(tagString) && tagString.split(",").length != 0) {
                BoolQueryBuilder builderTag = QueryBuilders.boolQuery();
                // 将标签按照逗号分隔开
                String[] strs_tags = tagString.split(",");
                // 将一个分类中的多个标签拼装，达到OR的效果
                for (int j = 0; j < strs_tags.length; j++) {
                    String strs_tag = strs_tags[j];
                    // wildcardQuery 通配符查询
                    builderTag.should(QueryBuilders.wildcardQuery("tags.keyword", "*" + strs_tag + "*"));
                }
                //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                boolQueryBuilder = boolQueryBuilder.must(builderTag);
            }

        } else { //其他过滤
            String docType = "";
            String docType1 = "";
            if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
                if (contentType.equals("presentationml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "powerpoint";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else if (contentType.equals("spreadsheetml")) {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);

                    contentType = "excel";
                    docType1 = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder3 = QueryBuilders.queryStringQuery(docType1);
                    QueryBuilder query = QueryBuilders.boolQuery().
                            should(queryStringQueryBuilder2).should(queryStringQueryBuilder3);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(query);
                } else {
                    docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
                    queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                    boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
                }

            } else {
                docType = "contentType:*" + contentType + "*";
                queryStringQueryBuilder2 = QueryBuilders.queryStringQuery(docType);
                boolQueryBuilder = QueryBuilders.boolQuery().must(queryBase).must(termQueryBuilder).must(queryStringQueryBuilder2);
            }


        }
//        String docType = "contentType:*" + contentType + "*";
//        if (!"text".equals(contentType) && !"pdf".equals(contentType) && !"msword".equals(contentType)) {
//            docType = "contentType:*" + contentType.replaceAll(",", "*  contentType:*") + "*";
//        }
//        QueryStringQueryBuilder queryStringQueryBuilder = QueryBuilders.queryStringQuery(docType);
//        QueryBuilder queryStringQueryBuilder = QueryBuilders.wildcardQuery("contentType", "*word*");

        // folderIds 有管理权限的目录id
        if (folderIds != null&&!folderIds.equals("")&&folderIds.length()>1) {
            folderIds = folderIds.substring(1);
            String[] folderStr = folderIds.split(",");
            TermsQueryBuilder termsQueryBuilder2 = QueryBuilders.termsQuery("folderId", folderStr);

            // 不是超级管理员
            if (!adminFlag) {
                // 只能查询有权限的文档  termsQueryBuilder2: 有权限的目录id   termsQueryBuilder:文件权限
                QueryBuilder query2 = QueryBuilders.boolQuery().should(termsQueryBuilder).should(termsQueryBuilder2);
                boolQueryBuilder.must(query2);
            }
        } else {
            if (!adminFlag) {
                //  termsQueryBuilder:文件权限
                boolQueryBuilder.must(termsQueryBuilder);
            }
        }
        SearchResponse response = doQuery(boolQueryBuilder, page, size, order);
        return handleHitsSeachStrengthen(response);
    }

    private ESResponse<Map<String, Object>> handleHitsSeachStrengthen(SearchResponse response) {

        SearchHits hits = response.getHits();
        ESResponse<Map<String, Object>> result = new ESResponse<>();
        long totalHits = hits.getTotalHits().value;
        result.setTotal(totalHits);
        int totalPage = (int) (totalHits / 10) + 1;
        result.setTotalPages(totalPage);

        List<Map<String, Object>> items = result.getItems();
        float maxScore = 0;
        Iterator<SearchHit> iterator = hits.iterator();
        while (iterator.hasNext()) {
            SearchHit searchHit = iterator.next();// 每个查询对象
            Map<String, Object> source = searchHit.getSourceAsMap();
            source.put("id", searchHit.getId());
            float score = searchHit.getScore();
            if (score >= 1.7014E38) {score = (float) ((float) score - 1.7014E38) / (float) 1.0E31;}
            if (score > maxScore) {maxScore = score;}
            source.put("score", score);
            // 将高亮处理后的内容，替换原有内容（原有内容，可能会出现显示不全）
            Map<String, HighlightField> hightlightFields = searchHit.getHighlightFields();
            Iterator<String> hightlightKeys = hightlightFields.keySet().iterator();
            while (hightlightKeys.hasNext()) {
                String key = hightlightKeys.next();
                HighlightField field = hightlightFields.get(key);
                // 获取到原有内容中 每个高亮显示 集中位置fragment就是高亮片段
                Text[] fragments = field.fragments();
                StringBuffer sb = new StringBuffer();
                for (Text text : fragments) {
                    sb.append(text);
                }
                source.put(key, sb.toString());
            }
            result.setMaxScore(maxScore);
            items.add(source);
        }

        return result;
    }
}
