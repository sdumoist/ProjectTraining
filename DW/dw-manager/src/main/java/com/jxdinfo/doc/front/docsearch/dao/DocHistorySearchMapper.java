package com.jxdinfo.doc.front.docsearch.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhongGuangrui on 2019/1/16.
 * 搜索历史记录 数据访问层
 */
public interface DocHistorySearchMapper extends BaseMapper<DocHistorySearch>{
    /**
     * 查询热门搜索关键词(前8条)
     * @return
     * @author zgr
     */
    List<Map> selectHotKeywords();

    /**
     * 搜索联想去重
     * @return
     * @author sjw
     */
    List<String> distinctSuggest(@Param("suggestList") List<String> suggestList);

    List<DocHistorySearch> getList(@Param("userId") String userId,
                                   @Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize);
   Integer updateFlag(@Param("userId") String userId,@Param("keyword") String keyword);
}
