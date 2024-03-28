package com.jxdinfo.doc.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.question.dao.QaContinueQaMapper;
import com.jxdinfo.doc.question.model.QaContinueQa;
import com.jxdinfo.doc.question.service.QaContinueQaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 追问追答service实现
 * @author sjw
 * @since 2021-02-24
 */
@Service
public class QaContinueQaServiceImpl extends ServiceImpl<QaContinueQaMapper,QaContinueQa> implements QaContinueQaService {

    @Autowired
    private QaContinueQaMapper qaContinueQaMapper;

    /**
     * 问题详情查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getContinutQaByAnswer(String ansId) {
        return qaContinueQaMapper.getContinutQaByAnswer(ansId);
    }

}
