package com.jxdinfo.doc.answer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.answer.model.QaMessage;

import java.util.List;
import java.util.Map;

/**
 * 消息提醒service层
 * @author sjw
 * @since 2021-02-22
 */
public interface QaMessageService extends IService<QaMessage> {

    /**
     * 首页消息提醒查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMessageList(String userId);

    /**
     * 未读消息数查询
     * @return  查询结果
     */
    int getMessageListCount(String userId);

    /**
     * 消息设置为已读
     * @return  更新结果
     */
    int setRead(String userId);
}
