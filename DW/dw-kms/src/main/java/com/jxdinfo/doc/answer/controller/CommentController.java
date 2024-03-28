package com.jxdinfo.doc.answer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.answer.model.QaCommentReply;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.model.QaMessage;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.AnswerService;
import com.jxdinfo.doc.answer.service.CommentService;
import com.jxdinfo.doc.answer.service.QaMessageService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 回答评论控制层
 * @author sjw
 * @since 2021-02-22
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QaMessageService qaMessageService;


    /**
     * 日志
     */
    private static Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    /**
     * 新增评论回复
     * @return  新增结果
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public String addComment(String queId, String ansId, String byReplyId, String replyContent) {

        // xss过滤
        queId = XSSUtil.xss(queId);
        ansId = XSSUtil.xss(ansId);
        byReplyId = XSSUtil.xss(byReplyId);
        replyContent = XSSUtil.xss(replyContent);

        QaCommentReply qaCommentReply = new QaCommentReply();
        // 获取回答信息
        QaQuestionAnswer qaQuestionAnswer = answerService.getById(ansId);
        // 判断回答是否删除，若删除，直接返回
        if (qaQuestionAnswer == null || "1".equals(qaQuestionAnswer.getState())) {
            // 问题不存在或已删除
            return "ansDelete";
        }
        String commentReplyId = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取被回复人ID和名字
        String byReplyUserId = null;
        String byReplyUserName = null;
        if (byReplyId.equals(ansId)) {
            // 回答ID和被回复ID相同，说明是评论
            byReplyUserId = qaQuestionAnswer.getAnsUserId();
            byReplyUserName = qaQuestionAnswer.getAnsUserName();
        } else {
            QaCommentReply byReplyComment = commentService.getById(byReplyId);
            // 判断评论回复是否删除，若删除，直接返回
            if (byReplyComment == null || "1".equals(byReplyComment.getState())) {
                // 该评论回复不存在或已删除
                return "commDelete";
            }
            byReplyUserId = byReplyComment.getReplyUserId();
            byReplyUserName = byReplyComment.getReplyUserName();
        }
        qaCommentReply.setCommentReplyId(commentReplyId);
        qaCommentReply.setQueId(queId);
        qaCommentReply.setAnsId(ansId);
        qaCommentReply.setByReplyId(byReplyId);
        qaCommentReply.setByReplyUserId(byReplyUserId);
        qaCommentReply.setByReplyUserName(byReplyUserName);
        qaCommentReply.setReplyContent(replyContent);
        qaCommentReply.setReplyTime(new Timestamp(new Date().getTime()));
        qaCommentReply.setReplyUserId(ShiroKit.getUser().getId());
        qaCommentReply.setReplyUserName(ShiroKit.getUser().getName());
        qaCommentReply.setState("0");
        qaCommentReply.setAgreeNum(0);
        qaCommentReply.insert();
        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("3");
        qaLog.setOperation("1");
        qaLog.setDataId(ansId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 新增消息
        QaMessage qaMessage = new QaMessage();
        qaMessage.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaMessage.setType("2");
        qaMessage.setContent(replyContent);
        qaMessage.setDataId(commentReplyId);
        qaMessage.setUserId(byReplyUserId);
        qaMessage.setMessageTime(new Timestamp(new Date().getTime()));
        qaMessage.setState("0");
        qaMessage.insert();
        return "success";
    }

    /**
     * 删除评论回复
     * @return  删除结果
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delComment(String commentPeplyId) {

        // xss过滤
        commentPeplyId = XSSUtil.xss(commentPeplyId);

        // 判断是否有回复
        List<QaCommentReply> qaCommentReplyList = commentService.list(new QueryWrapper<QaCommentReply>().eq("BY_REPLY_ID", commentPeplyId));
        if (qaCommentReplyList.size() > 0) {
            return "hasChild";
        }
        // 获取评论回复信息
        QaCommentReply qaCommentReply = commentService.getById(commentPeplyId);
        // 逻辑删除
        qaCommentReply.setState("1");
        qaCommentReply.updateById();
        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("3");
        qaLog.setOperation("2");
        qaLog.setDataId(commentPeplyId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 获取回答的消息提醒
        List<QaMessage> qaMessagesList = qaMessageService.list(new QueryWrapper<QaMessage>().eq("DATA_ID", commentPeplyId));
        // 逻辑删除消息
        for (QaMessage qaMessage:qaMessagesList) {
            qaMessage.setState("1");
            qaMessage.updateById();
        }
        return "success";
    }

    /**
     * 问题详情页获取评论回复
     * byReplyId被回复对象的id
     * @return  查询结果
     */
    @RequestMapping(value = "/getCommentDetail")
    @ResponseBody
    public List<Map<String, Object>> getCommentDetail(String byReplyId) {

        // xss过滤
        byReplyId = XSSUtil.xss(byReplyId);

        String userId = ShiroKit.getUser().getId();
        return  commentService.getCommentDetail(userId,byReplyId);
    }

    /**
     * 点赞评论回复
     * commentPeplyId回复ID
     * @return  结果
     */
    @RequestMapping(value = "/agree")
    @ResponseBody
    public String agree(String commentPeplyId, String isAgree) {
        // xss过滤
        commentPeplyId = XSSUtil.xss(commentPeplyId);
        isAgree = XSSUtil.xss(isAgree);

        try {
            commentService.updateAgreeState(commentPeplyId, isAgree);
        } catch (Exception e) {
            LOGGER.error("点赞回答异常：" + ExceptionUtils.getErrorInfo(e));
            return "error";
        }
        return "success";
    }
}
