package com.jxdinfo.doc.answer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.question.model.QaInviteAnswer;


/**
 * 邀我回答service层
 * @author cxk
 * @since 2021-05-14
 */
public interface QaInviteAnswerService extends IService<QaInviteAnswer> {


    /**
     * 评论时处理数据
     * @param queId 问题ID
     * @param ansId ansId
     */
    void addAnswerProcessData(String queId, String ansId);


    /**
     *  删除评论时处理数据
     * @param ansId ansId
     */
    void delAnswerProcessData(String ansId);

    /**
     *  获取评论数据
     * @param ansId
     * @return
     */
    QaQuestionAnswer getQaAnswerById(String ansId);
}
