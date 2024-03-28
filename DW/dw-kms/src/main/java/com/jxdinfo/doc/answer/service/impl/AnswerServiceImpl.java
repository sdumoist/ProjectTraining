package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.constant.QuestionConstant;
import com.jxdinfo.doc.answer.dao.AnswerMapper;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.AnswerService;
import com.jxdinfo.doc.question.dao.QaAgreeMapper;
import com.jxdinfo.doc.question.model.QaAgree;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import dm.jdbc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 问题回答service层实现类
 * @author sjw
 * @since 2021-02-22
 */
@Service
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper,QaQuestionAnswer> implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private QaAgreeMapper agreeMapper;

    /**
     * 首页优秀答主查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getExcellentAnswers() {
        return answerMapper.getExcellentAnswers();
    }

    /**
     * 问题详情答案查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getAnswerToQuestion(String userId, String queId, String onAll) {
        return answerMapper.getAnswerToQuestion(userId, queId, onAll);
    }

    /**
     * 点赞\取消点赞
     *
     * @param answerId   回答id
     * @param agreeState 点赞状态
     *                   agreeState=1 说明是已赞状态  这次操作要取消点赞
     *                   agreeState=0 说明是未赞状态  这次操作要点赞
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAgreeState(String answerId, String agreeState) {
        // 获取原有点赞数
        QaQuestionAnswer qaQuestionAnswer = getById(answerId);
        int agreeNum = qaQuestionAnswer.getAgreeNum();

        // 点赞数
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            agreeNum--;
        } else {
            agreeNum++;
        }
        qaQuestionAnswer.setAgreeNum(agreeNum);
        qaQuestionAnswer.updateById();

        // 点赞表数据
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            agreeMapper.delete(new QueryWrapper<QaAgree>().eq("ANS_ID", answerId).eq("USER_ID", ShiroKit.getUser().getId()));
        } else {
            QaAgree qaAgree = new QaAgree();
            qaAgree.setAgreeId(UUID.randomUUID().toString().replaceAll("-", ""));
            qaAgree.setAnsId(answerId);
            qaAgree.setUserId(ShiroKit.getUser().getId());
            qaAgree.insert();
        }

        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType(QuestionConstant.QALOG_TYPE_ANS);
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            qaLog.setOperation(QuestionConstant.QALOG_OPERATION_CANCEL_AGREE);
        } else {
            qaLog.setOperation(QuestionConstant.QALOG_OPERATION_AGREE);
        }
        qaLog.setDataId(answerId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState(QuestionConstant.VALID_FLAG_NORMAL);
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
    }
}
