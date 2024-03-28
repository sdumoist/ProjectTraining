package com.jxdinfo.doc.knowledge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.knowledge.dao.KnowledgeMapper;
import com.jxdinfo.doc.knowledge.model.KnowledgeBase;
import com.jxdinfo.doc.knowledge.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 知识库表 service实现层
 * @author cxk
 * @since 2021-05-12
 */
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper,KnowledgeBase> implements KnowledgeService {

    @Autowired
    private KnowledgeMapper mapper;

    /**
     * 知识库列表页数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getKnowledgeList(String title, String label, String state, String order, int startIndex, int pageSize, String userId) {
        return mapper.getKnowledgeList(title, label, state, order, startIndex, pageSize, userId);
    }
}
