package com.jxdinfo.doc.answer.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.answer.model.QaCommentReply;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.model.QaMessage;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.AnswerService;
import com.jxdinfo.doc.answer.service.CommentService;
import com.jxdinfo.doc.answer.service.QaInviteAnswerService;
import com.jxdinfo.doc.answer.service.QaMessageService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.model.QaText;
import com.jxdinfo.doc.question.service.QuestionService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 问题回答控制层
 * @author sjw
 * @since 2021-02-22
 */
@Controller
@RequestMapping("/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QaMessageService qaMessageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QaInviteAnswerService qaInviteAnswerService;

    /**
     * 日志
     */
    private static Logger LOGGER = LoggerFactory.getLogger(AnswerController.class);



    @GetMapping("/view")
    public String searchAuthor() {

            return "/doc/manager/system/synchronousData.html";
    }

    /**
     * 新增回答
     * queId问题ID
     * ansContent回答内容
     * ansContentText回答内容纯文本
     * @return  新增结果
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public String addAnswer(@RequestParam Map<String,String> paramMap) {

        String queId = paramMap.get("queId");
        String ansContent = paramMap.get("ansContent");
        String ansContentText = paramMap.get("ansContentText");

        // xss过滤
        queId = XSSUtil.xss(queId);

        QaQuestionAnswer qaQuestionAnswer = new QaQuestionAnswer();
        // 获取问题信息
        QaQuestion qaQuestion = questionService.getById(queId);
        // 判断问题是否存在或删除 问题的状态 0待回答，1已解决，2已结束，3已删除  原版是1
        if (qaQuestion == null || "3".equals(qaQuestion.getState())) {
            return "queDelete";
        }
        // 生成主键
        String ansId = UUID.randomUUID().toString().replaceAll("-", "");
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取用户名
        String userName = shiroUser.getName();
        // 获取用户ID
        String userId = shiroUser.getId();
        qaQuestionAnswer.setAnsId(ansId);
        qaQuestionAnswer.setQueId(queId);
        /*if(ansContent != null){
            // 传参数的时候 src 参数丢失，前台换成了sr1c
            ansContent = ansContent.replaceAll("sr1c" , "src");
        }*/
        qaQuestionAnswer.setAnsContent(ansContent);
        qaQuestionAnswer.setAnsTime(new Timestamp(new Date().getTime()));
        qaQuestionAnswer.setAnsUserId(userId);
        qaQuestionAnswer.setAnsUserName(userName);
        qaQuestionAnswer.setState("0");
        qaQuestionAnswer.setAgreeNum(0);
        qaQuestionAnswer.setBestAnswer("0");
        qaQuestionAnswer.setAnsContentText(ansContentText);
        // 新增回答
        qaQuestionAnswer.insert();
        // 新增 回答文本数据
        QaText qaText = new QaText();
        qaText.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaText.setType("2"); // 回答
        qaText.setQaId(ansId);
        qaText.setContent(ansContentText);
        qaText.insert();
        //修改邀我回答表
        qaInviteAnswerService.addAnswerProcessData(queId,ansId);
        // 添加问答日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("2");
        qaLog.setOperation("1");
        qaLog.setDataId(ansId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 新增消息
        QaMessage qaMessage = new QaMessage();
        qaMessage.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaMessage.setType("1");
        qaMessage.setContent(ansContentText);
        qaMessage.setDataId(ansId);
        qaMessage.setMessageTime(new Timestamp(new Date().getTime()));
        qaMessage.setState("0");
        // 获取问题的提问人
        qaMessage.setUserId(qaQuestion.getQueUserId());
        qaMessage.insert();
        // 存es
        /*
        docES.setCategory();
         */
        return "success";
    }

    /**
     * 设为最佳答案
     * ansId回答ID
     * @return  新增结果
     */
    @RequestMapping(value = "/setBestAnswer")
    @ResponseBody
    public String setBestAnswer(String ansId) {

        // xss过滤
        ansId = XSSUtil.xss(ansId);

        QaQuestionAnswer qaQuestionAnswer = answerService.getById(ansId);
        // 判断是否已有最佳答案
        List<QaQuestionAnswer> qaQuestionAnswerList = answerService.list(new QueryWrapper<QaQuestionAnswer>().eq("QUE_ID",qaQuestionAnswer.getQueId()).eq("BEST_ANSWER", "1").eq("STATE",'0'));
        if (qaQuestionAnswerList.size() > 0) {
            return "hasBast";
        }
        qaQuestionAnswer.setBestAnswer("1");
        qaQuestionAnswer.updateById();
        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("2");
        qaLog.setOperation("12");
        qaLog.setDataId(ansId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        return "success";
    }

    /**
     * 点赞回答
     * ansId回答ID
     *
     * @return 结果
     */
    @RequestMapping(value = "/agree")
    @ResponseBody
    public String agree(String ansId, String isAgree) {

        // xss过滤
        ansId = XSSUtil.xss(ansId);
        isAgree = XSSUtil.xss(isAgree);

        try {
            answerService.updateAgreeState(ansId, isAgree);
        } catch (Exception e) {
            LOGGER.error("点赞回答异常：" + ExceptionUtils.getErrorInfo(e));
            return "error";
        }
        return "success";
    }

    /**
     * 首页优秀答主查询
     * @return  查询结果
     */
    @RequestMapping(value = "/excellentAnswers")
    @ResponseBody
    public List<Map<String, Object>> excellentAnswers(String ansId) {
        List<Map<String, Object>> result = new ArrayList<>();
        result = answerService.getExcellentAnswers();
        return result;
    }

    /**
     * 删除回答
     * ansId回答ID
     * @return  删除结果
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public String delete(String ansId) {

        // xss过滤
        ansId = XSSUtil.xss(ansId);

        QaQuestionAnswer qaQuestionAnswer = answerService.getById(ansId);
        qaQuestionAnswer.setState("1");
        // 删除回答（逻辑删除）
        qaQuestionAnswer.updateById();
        // 获取相关评论回复
        List<QaCommentReply> qaCommentReplyList = commentService.list(new QueryWrapper<QaCommentReply>().eq("ANS_ID", ansId));
        // 逻辑删除评论回复
        for (QaCommentReply qaCommentReply:qaCommentReplyList) {
            qaCommentReply.setState("1");
            qaCommentReply.updateById();
        }
        // 获取回答的消息提醒
        List<QaMessage> qaMessagesList = qaMessageService.list(new QueryWrapper<QaMessage>().eq("DATA_ID", ansId));
        // 逻辑删除消息
        for (QaMessage qaMessage:qaMessagesList) {
            qaMessage.setState("1");
            qaMessage.updateById();
        }
        //修改邀我回答表
        qaInviteAnswerService.delAnswerProcessData(ansId);
        // 添加删除回答的日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("2");
        qaLog.setOperation("2");
        qaLog.setDataId(ansId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        return "success";
    }

    /**
     * 根据ID获取回答详情
     * ansId回答ID
     * @return  回答详情
     */
    @RequestMapping(value = "/getAnswerById")
    @ResponseBody
    public QaQuestionAnswer getAnswerById(String ansId) {

        // xss过滤
        ansId = XSSUtil.xss(ansId);

        QaQuestionAnswer qaQuestionAnswer = qaInviteAnswerService.getQaAnswerById(ansId);
        return qaQuestionAnswer;
    }

}
