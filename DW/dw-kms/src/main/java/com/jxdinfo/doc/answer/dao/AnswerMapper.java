package com.jxdinfo.doc.answer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 问题回答mapper
 * @author sjw
 * @since 2021-02-22
 */
public interface AnswerMapper extends BaseMapper<QaQuestionAnswer> {

    /**
     * 首页优秀答主查询
     * @return  查询结果
     */
    List<Map<String, Object>> getExcellentAnswers();

    /**
     * 问题详情答案查询
     * @return  查询结果
     */
    List<Map<String, Object>> getAnswerToQuestion(@Param("userId") String userId, @Param("queId") String queId, @Param("onAll") String onAll);
}
