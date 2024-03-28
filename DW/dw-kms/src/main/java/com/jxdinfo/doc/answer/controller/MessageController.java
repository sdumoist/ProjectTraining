package com.jxdinfo.doc.answer.controller;

import com.jxdinfo.doc.answer.service.QaMessageService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息提醒控制层
 * @author sjw
 * @since 2021-02-23
 */
@Controller
@RequestMapping("/qaMessage")
public class MessageController {

    @Autowired
    private QaMessageService qaMessageService;

    /**
     * 首页消息提醒查询
     * @return  查询结果
     */
    @RequestMapping(value = "/messageList")
    @ResponseBody
    public Map<String, Object> getMessageList() {
        String userId = ShiroKit.getUser().getId();
        Map<String, Object> result = new HashMap<>();
        // 获取当前用户的消息
        List<Map<String, Object>> rows = qaMessageService.getMessageList(userId);
        // 获取当前用户的未读消息条数
        int count = qaMessageService.getMessageListCount(userId);
        // 消息数据
        result.put("rows", rows);
        // 消息条数
        result.put("unReadNum", count);
        return result;
    }

    /**
     * 消息设置为已读
     * @return  查询结果
     */
    @RequestMapping(value = "/setRead")
    @ResponseBody
    public String setRead() {
        String userId = ShiroKit.getUser().getId();
        qaMessageService.setRead(userId);
        return "success";
    }

}
