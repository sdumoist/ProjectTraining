package com.jxdinfo.doc.answer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.question.model.QaInviteAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface QaInviteAnswerMapper extends BaseMapper<QaInviteAnswer> {


    /**
     *  获取邀请数据
     * @param userId 登陆人ID
     * @param queId 问题ID
     * @return
     */
    QaInviteAnswer getQaInviteAnswer(@Param("userId") String userId, @Param("queId") String queId);

    /**
     *  当前登陆人是否回复了问题
     * @param userId
     * @param queId
     * @return
     */
    List<QaQuestionAnswer> getQaAnswer(@Param("userId") String userId, @Param("queId") String queId);


    /**
     * 获取 问题提出时间
     * @param queId 问题Id
     * @return
     */
    String getQuestionTime(@Param("queId") String queId);

    /**
     *  获取品论数据
     * @param ansId
     * @return
     */
    QaQuestionAnswer getQaAnswerById(@Param("ansId") String ansId);
}
