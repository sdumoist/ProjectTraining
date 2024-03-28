package com.jxdinfo.doc.answer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.answer.model.QaCommentReply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 评论回复mapper
 * @author sjw
 * @since 2021-02-22
 */
public interface CommentMapper extends BaseMapper<QaCommentReply> {

    /**
     * 回复评论数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getCommentDetail(@Param("userId") String userId, @Param("byReplyId") String byReplyId);
}
