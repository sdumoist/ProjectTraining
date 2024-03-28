package com.jxdinfo.doc.knowledge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.knowledge.model.KnowledgeBase;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 知识库表 mapper
 * @author cxk
 * @since 2021-05-12
 */
public interface KnowledgeMapper extends BaseMapper<KnowledgeBase> {

    /**
     * 知识库表数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getKnowledgeList(@Param("title") String title, @Param("label") String label,
                                               @Param("state") String state, @Param("order") String order,
                                               @Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("userId") String userId);
}
