package com.jxdinfo.doc.question.controller;

import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.question.model.QaFollowInfo;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.service.QaFollowInfoService;
import com.jxdinfo.doc.question.service.QuestionService;
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
 * 问题关注控制层
 * @author sjw
 * @since 2021-02-24
 */
@Controller
@RequestMapping("/qaFollowInfo")
public class QaFollowInfoController {

    @Autowired
    private QaFollowInfoService qaFollowInfoService;

    @Autowired
    private QuestionService questionService;

    @RequestMapping("/add")
    @ResponseBody
    public String addFollow(String queId) {

        // xss过滤
        queId = XSSUtil.xss(queId);

        // 判断问题是否删除
        QaQuestion qaQuestion = questionService.getById(queId);
        if (qaQuestion == null || "3".equals(qaQuestion.getState())) {
            return "queDel";
        }
        String followInfoId = UUID.randomUUID().toString().replaceAll("-", "");
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        QaFollowInfo qaFollowInfo = new QaFollowInfo();
        qaFollowInfo.setId(followInfoId);
        qaFollowInfo.setQueId(queId);
        qaFollowInfo.setUserId(userId);
        qaFollowInfo.setFollowTime(new Timestamp(new Date().getTime()));
        qaFollowInfo.insert();
        return "success";
    }

    @RequestMapping("/cancel")
    @ResponseBody
    public String deleteFollow(String followId) {

        // xss过滤
        followId = XSSUtil.xss(followId);

        qaFollowInfoService.removeById(followId);
        return "success";
    }

    /**
     * 取消收藏
     * queId问题ID
     * @return  结果
     */
    @RequestMapping(value = "/cancelFollow")
    @ResponseBody
    public String deleteFollowInfo(String queId) {

        // xss过滤
        queId = XSSUtil.xss(queId);

        // 判断问题是否删除
        QaQuestion qaQuestion = questionService.getById(queId);
        if (qaQuestion == null || "3".equals(qaQuestion.getState())) {
            return "queDel";
        }
        // 获取当前登陆人当前问题的收藏数据
        List<QaFollowInfo> followList = questionService.getFollowInfoByQueId(queId,ShiroKit.getUser().getId());
        for(QaFollowInfo item: followList){
            qaFollowInfoService.removeById(item.getId());
            // 添加日志
            QaLog qaLog = new QaLog();
            qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            qaLog.setType("1");
            qaLog.setOperation("6");
            qaLog.setDataId(item.getId());
            qaLog.setUserId(ShiroKit.getUser().getId());
            qaLog.setUserName(ShiroKit.getUser().getName());
            qaLog.setTime(new Timestamp(new Date().getTime()));
            qaLog.setState("0");
            qaLog.setIp(HttpKit.getIp());
            qaLog.insert();
        }
        return "success";
    }

}
