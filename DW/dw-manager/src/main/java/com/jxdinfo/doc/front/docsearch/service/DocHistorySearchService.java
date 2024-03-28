package com.jxdinfo.doc.front.docsearch.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhongGuangrui on 2019/1/16.
 * 搜索历史记录，业务逻辑层
 */
public interface DocHistorySearchService extends IService<DocHistorySearch> {
    /**
     * 查询热门搜索关键词（前8条）
     * @return
     * @author zgr
     */
    List<Map> selectHotKeywords();
    List<DocHistorySearch> getList(String userId, Integer pageSize, Integer pageNumber);
    Integer updateFlag(String userId,String keyWord);
    /**
     * 搜索联想去重
     * @return
     * @author sjw
     */
    List<String> distinctSuggest(List<String> suggestList);
}
