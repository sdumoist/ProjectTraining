package com.jxdinfo.doc.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.question.model.QaContinueQa;

import java.util.List;
import java.util.Map;

/**
 * 追问追答service
 * @author sjw
 * @since 2021-02-24
 */
public interface QaContinueQaService extends IService<QaContinueQa> {

    /**
     * 问题详情查询
     * @return  查询结果
     */
    List<Map<String, Object>> getContinutQaByAnswer(String ansId);

}
