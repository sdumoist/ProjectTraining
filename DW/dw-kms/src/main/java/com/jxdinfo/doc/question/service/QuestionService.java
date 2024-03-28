package com.jxdinfo.doc.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.question.model.QaFollowInfo;
import com.jxdinfo.doc.question.model.QaQuestion;

import java.util.List;
import java.util.Map;

/**
 * 知识问答问题service
 * @author sjw
 * @since 2021-02-24
 */
public interface QuestionService extends IService<QaQuestion> {

    /**
     * 问题详情查询
     * @return  查询结果
     */
    Map<String, Object> getQuestionDetail(String userId, String queId);

    /**
     * 删除问题、回答、追问追答、评论回复
     * @param queId 问题ID
     * @return
     */
    Integer delQuestion(String queId);

    /**
     * 首页知识问答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getQueListByFirstPage();

    /**
     * 相关问题数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getRelevantQuestion(String label, String queId);

    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getQueTableList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);

    /**
     * 我的提问数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyQuestionList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);

    /**
     * 我的关注数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyFollowQuestionList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);

    /**
     * 我的回答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyAnswerList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);

    /**
     * 邀我回答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getInviteMeAnswerList(String title, String label, String state, String order, int startIndex, int pageSize, String userId);

    /**
     * 获取当前登陆人当前问题的收藏记录
     * @param queId 问题ID
     * @param userId 当前登陆人ID
     * @return
     */
    List<QaFollowInfo> getFollowInfoByQueId(String queId, String userId);

}
