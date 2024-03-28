package com.jxdinfo.doc.question.controller;

import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.question.model.QaQuestionSupplement;
import com.jxdinfo.doc.question.service.SupplementService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * 问题补充控制层
 * @author sjw
 * @since 2021-02-24
 */
@Controller
@RequestMapping("/supplement")
public class SupplementController {

    @Autowired
    private SupplementService supplementService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;

    @RequestMapping("/add")
    @ResponseBody
    public String addSupplement(String queId, String supplement, String text) {

        // xss过滤
        queId = XSSUtil.xss(queId);
        supplement = XSSUtil.xss(supplement);

        String supplementId = UUID.randomUUID().toString().replaceAll("-", "");
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        QaQuestionSupplement qaQuestionSupplement = new QaQuestionSupplement();
        qaQuestionSupplement.setId(supplementId);
        qaQuestionSupplement.setSupplement(supplement);
        qaQuestionSupplement.setQueId(queId);
        qaQuestionSupplement.setSupTime(new Timestamp(new Date().getTime()));
        qaQuestionSupplement.setSupUserId(userId);
        qaQuestionSupplement.setSupUserName(userName);
        qaQuestionSupplement.insert();
        // 添加问答日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("1");
        qaLog.setOperation("9");
        qaLog.setDataId(supplementId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        /*// 获取原es数据
        Map<String, Object> oldEs = esService.getIndex(queId);
        String oldContent = oldEs.get("content").toString();
        String newContent = oldContent + supplement;
        Map<String, Object> newEs = new HashMap<>();
        newEs.put("content", newContent);*/
        return "success";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String deleteSupplement(String supplementId) {

        // xss过滤
        supplementId = XSSUtil.xss(supplementId);

        supplementService.removeById(supplementId);
        return "success";
    }

}
