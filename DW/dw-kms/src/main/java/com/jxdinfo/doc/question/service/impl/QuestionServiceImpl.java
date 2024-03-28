package com.jxdinfo.doc.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.answer.model.QaCommentReply;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.question.dao.QuestionMapper;
import com.jxdinfo.doc.question.model.QaContinueQa;
import com.jxdinfo.doc.question.model.QaFollowInfo;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 知识问答问题service实现类
 * @author sjw
 * @since 2021-02-24
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper,QaQuestion> implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    /**
     * 问题详情查询
     * @return  查询结果
     */
    @Override
    public Map<String, Object> getQuestionDetail(String userId, String queId) {
        return questionMapper.getQuestionDetail(userId, queId);
    }

    /**
     * 删除问题、回答、追问追答、评论回复
     * @param queId 问题ID
     * @return
     */
    @Override
    public Integer delQuestion(String queId) {
//        return questionMapper.delQuestion(queId);
        QaQuestionAnswer qaQuestionAnswer = new QaQuestionAnswer();
        qaQuestionAnswer.setState("1");
        qaQuestionAnswer.update(new QueryWrapper<QaQuestionAnswer>().eq("que_id",queId));
        QaCommentReply qaCommentReply = new QaCommentReply();
        qaCommentReply.setState("1");
        qaCommentReply.update(new QueryWrapper<QaCommentReply>().eq("que_id",queId));
        QaContinueQa qaContinueQa = new QaContinueQa();
        qaContinueQa.setState("1");
        qaContinueQa.update(new QueryWrapper<QaContinueQa>().eq("que_id",queId));
        QaQuestion qaQuestion = new QaQuestion();
        qaQuestion.setState("3");
        qaQuestion.update(new QueryWrapper<QaQuestion>().eq("que_id",queId));
        return 1;
    }

    /**
     * 首页知识问答数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getQueListByFirstPage() {
        return questionMapper.getQueListByFirstPage();
    }

    /**
     * 相关问题数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getRelevantQuestion(String label, String queId) {
        return questionMapper.getRelevantQuestion(label, queId);
    }

    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getQueTableList(String title, String label, String state, String order, int startIndex, int pageSize , String userId) {
        return questionMapper.getQueTableList(title, label, state, order, startIndex, pageSize, userId);
    }

    /**
     * 我的提问数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getMyQuestionList(String title, String label, String state, String order, int startIndex, int pageSize, String userId) {
        return  questionMapper.getMyQuestionList(title, label, state, order, startIndex, pageSize, userId);
    }

    /**
     * 我的关注数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getMyFollowQuestionList(String title, String label, String state, String order, int startIndex, int pageSize, String userId) {
        return  questionMapper.getMyFollowQuestionList(title, label, state, order, startIndex, pageSize, userId);
    }

    /**
     * 我的回答数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getMyAnswerList(String title, String label, String state, String order, int startIndex, int pageSize, String userId) {
        return  questionMapper.getMyAnswerList(title, label, state, order, startIndex, pageSize, userId);
    }

    @Override
    public List<Map<String, Object>> getInviteMeAnswerList(String title, String label, String state, String order, int startIndex, int pageSize, String userId) {
        return questionMapper.getInviteMeAnswerList(title, label, state, order, startIndex, pageSize, userId);
    }

    @Override
    public List<QaFollowInfo> getFollowInfoByQueId(String queId, String userId) {
        return questionMapper.getFollowInfoByQueId(queId,userId);
    }
}
