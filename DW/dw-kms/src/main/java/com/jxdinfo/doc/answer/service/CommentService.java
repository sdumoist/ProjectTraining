package com.jxdinfo.doc.answer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.answer.model.QaCommentReply;

import java.util.List;
import java.util.Map;

/**
 * 评论回复service层
 * @author sjw
 * @since 2021-02-22
 */
public interface CommentService extends IService<QaCommentReply> {

    /**
     * 回复评论数据查询
     * @return  查询结果
     */
    List<Map<String, Object>> getCommentDetail(String userId, String byReplyId);


    /**
     * 点赞\取消点赞
     *
     * @param commentPeplyId   回复id
     * @param agreeState 点赞状态
     */
    void updateAgreeState(String commentPeplyId, String agreeState);

}
