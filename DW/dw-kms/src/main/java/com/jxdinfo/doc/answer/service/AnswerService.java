package com.jxdinfo.doc.answer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;

import java.util.List;
import java.util.Map;

/**
 * 问题回答service层
 * @author sjw
 * @since 2021-02-22
 */
public interface AnswerService extends IService<QaQuestionAnswer> {

    /**
     * 首页优秀答主查询
     * @return  查询结果
     */
    List<Map<String, Object>> getExcellentAnswers();

    /**
     * 问题详情答案查询
     * @return  查询结果
     */
    List<Map<String, Object>> getAnswerToQuestion(String userId, String queId, String onAll);

    /**
     * 点赞\取消点赞
     *
     * @param answerId   回答id
     * @param agreeState 点赞状态
     */
    void updateAgreeState(String answerId, String agreeState);
}
