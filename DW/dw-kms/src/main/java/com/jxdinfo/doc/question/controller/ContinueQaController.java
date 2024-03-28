package com.jxdinfo.doc.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.model.QaMessage;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.AnswerService;
import com.jxdinfo.doc.answer.service.QaMessageService;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.question.model.QaContinueQa;
import com.jxdinfo.doc.question.service.QaContinueQaService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 追问追答控制层
 * @author sjw
 * @since 2021-02-26
 */
@Controller
@RequestMapping("/continueQa")
public class ContinueQaController {

    @Autowired
    private QaContinueQaService qaContinueQaService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QaMessageService qaMessageService;


    @RequestMapping("/add")
    @ResponseBody
    public String addContinueQa (String queId, String ansId, String type, String byReplyId, String content) {
        // xss过滤
        queId = XSSUtil.xss(queId);
        ansId = XSSUtil.xss(ansId);
        type = XSSUtil.xss(type);
        byReplyId = XSSUtil.xss(byReplyId);

        int showOrder = 0;
        // 被回复用户，用于消息提醒
        String byReplyUser = "";
        // 判断是否是第一个追问
        if (!"1".equals(type) || !byReplyId.equals(ansId)) {
            // 不是第一个，则在之前的showOrder基础上加1
            QaContinueQa qaContinueQa = qaContinueQaService.getById(byReplyId);
            if (qaContinueQa != null && "0".equals(qaContinueQa.getState())) {
                showOrder = qaContinueQa.getShowOrder() + 1;
                byReplyUser = qaContinueQa.getUserId();
            } else {
                return "error";
            }
        } else if ("1".equals(type) && byReplyId.equals(ansId)) {
            // 是第一个追问,被回复用户是回答用户
            QaQuestionAnswer qaQuestionAnswer = answerService.getById(ansId);
            byReplyUser = qaQuestionAnswer.getAnsUserId();
        }
        // 新增追问追答
        String continueQaId = UUID.randomUUID().toString().replaceAll("-", "");
        QaContinueQa addContinueQa = new QaContinueQa();
        addContinueQa.setId(continueQaId);
        addContinueQa.setQueId(queId);
        addContinueQa.setAnsId(ansId);
        addContinueQa.setType(type);
        addContinueQa.setByReplyId(byReplyId);
        addContinueQa.setContent(content);
        addContinueQa.setTime(new Timestamp(new Date().getTime()));
        addContinueQa.setUserId(ShiroKit.getUser().getId());
        addContinueQa.setUserName(ShiroKit.getUser().getName());
        addContinueQa.setState("0");
        addContinueQa.setShowOrder(showOrder);
        addContinueQa.insert();
        String messageType = "";
        if ("1".equals(type)) {
            messageType = "4";
        } else if ("2".equals(type)) {
            messageType = "5";
        }
        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType(messageType);
        qaLog.setOperation("1");
        qaLog.setDataId(continueQaId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 新增消息
        QaMessage qaMessage = new QaMessage();
        qaMessage.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaMessage.setType(messageType);
        qaMessage.setContent(content);
        qaMessage.setDataId(continueQaId);
        qaMessage.setMessageTime(new Timestamp(new Date().getTime()));
        qaMessage.setState("0");
        // 获取问题的提问人
        qaMessage.setUserId(byReplyUser);
        qaMessage.insert();
        return "success";
    }

    @RequestMapping("/update")
    @ResponseBody
    public String updateContinueQa(String continueId, String content) {

        // xss过滤
        continueId = XSSUtil.xss(continueId);

        // 获取修改前数据
        QaContinueQa qaContinueQa = qaContinueQaService.getById(continueId);
        String type = qaContinueQa.getType();
        qaContinueQa.setContent(content);
        qaContinueQa.updateById();
        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        String messageType = "";
        if ("1".equals(type)) {
            messageType = "4";
        } else if ("2".equals(type)) {
            messageType = "5";
        }
        qaLog.setType(messageType);
        qaLog.setOperation("10");
        qaLog.setDataId(continueId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        return "success";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delContinueQa(String continueId) {

        // xss过滤
        continueId = XSSUtil.xss(continueId);

        // 判断是否可以删除
        List<QaContinueQa> qaContinueQaList = qaContinueQaService.list(new QueryWrapper<QaContinueQa>().eq("BY_REPLY_ID", continueId).eq("STATE", "0"));
        if (qaContinueQaList.size() > 0) {
            return "hasChild";
        }
        // 获取原数据
        QaContinueQa qaContinueQa = qaContinueQaService.getById(continueId);
        String type = qaContinueQa.getType();
        // 逻辑删除追问追答
        qaContinueQa.setState("1");
        qaContinueQa.updateById();
        // 添加删除日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        String messageType = "";
        if ("1".equals(type)) {
            messageType = "4";
        } else if ("2".equals(type)) {
            messageType = "5";
        }
        qaLog.setType(messageType);
        qaLog.setOperation("2");
        qaLog.setDataId(continueId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 逻辑删除消息
        List<QaMessage> qaMessageList = qaMessageService.list(new QueryWrapper<QaMessage>().eq("DATA_ID", continueId));
        for (QaMessage qaMessage:qaMessageList) {
            qaMessage.setState("1");
            qaMessage.updateById();
        }
        return "success";
    }
}
