package com.jxdinfo.doc.question.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.question.model.QaFollowInfo;
import com.jxdinfo.doc.question.model.QaQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 问题mapper
 * @author sjw
 * @since 2021-02-24
 */
public interface QuestionMapper extends BaseMapper<QaQuestion> {

    /**
     * 问题详情查询
     * @return  查询结果
     */
    Map<String, Object> getQuestionDetail(@Param("userId") String userId, @Param("queId") String queId);

    /**
     * 删除问题、回答、追问追答、评论回复
     * @param queId 问题ID
     * @return
     */
    Integer delQuestion(@Param("queId") String queId);

    /**
     * 首页知识问答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getQueListByFirstPage();

    /**
     * 相关问题数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getRelevantQuestion(@Param("label") String label, @Param("queId") String queId);

    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getQueTableList(@Param("title") String title, @Param("label") String label,
                                              @Param("state") String state, @Param("order") String order,
                                              @Param("startIndex") int startIndex, @Param("pageSize") int pageSize
            , @Param("userId") String userId);

    /**
     * 我的提问数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyQuestionList(@Param("title") String title, @Param("label") String label,
                                                @Param("state") String state, @Param("order") String order,
                                                @Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("userId") String userId);

    /**
     * 我的关注数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyFollowQuestionList(@Param("title") String title, @Param("label") String label,
                                                      @Param("state") String state, @Param("order") String order,
                                                      @Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("userId") String userId);

    /**
     * 我的回答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMyAnswerList(@Param("title") String title, @Param("label") String label,
                                              @Param("state") String state, @Param("order") String order,
                                              @Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("userId") String userId);

    /**
     * 邀我回答数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getInviteMeAnswerList(@Param("title") String title, @Param("label") String label,
                                                    @Param("state") String state, @Param("order") String order,
                                                    @Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("userId") String userId);

    /**
     * 获取当前登陆人当前问题的收藏记录
     * @param queId 问题ID
     * @param userId 当前登陆人ID
     * @return
     */
    List<QaFollowInfo> getFollowInfoByQueId(@Param("queId") String queId, @Param("userId") String userId);
}
