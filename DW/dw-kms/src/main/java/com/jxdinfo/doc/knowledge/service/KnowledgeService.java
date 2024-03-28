package com.jxdinfo.doc.knowledge.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.knowledge.model.KnowledgeBase;

import java.util.List;
import java.util.Map;


/**
 * 知识库表 service层
 * @author cxk
 * @since 2021-05-12
 */
public interface KnowledgeService extends IService<KnowledgeBase> {

    /**
     * 知识库列表页数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getKnowledgeList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);
}
