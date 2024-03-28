package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.answer.dao.QaMessageMapper;
import com.jxdinfo.doc.answer.model.QaMessage;
import com.jxdinfo.doc.answer.service.QaMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 消息提醒service层实现类
 * @author sjw
 * @since 2021-02-23
 */
@Service
public class QaMessageServiceImpl extends ServiceImpl<QaMessageMapper,QaMessage> implements QaMessageService{

    @Autowired
    private QaMessageMapper qaMessageMapper;

    /**
     * 首页消息提醒查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getMessageList(String userId) {
        return qaMessageMapper.getMessageList(userId);
    }

    /**
     * 未读消息数查询
     * @return  查询结果
     */
    @Override
    public int getMessageListCount(String userId) {
        return qaMessageMapper.getMessageListCount(userId);
    }

    /**
     * 消息设置为已读
     * @return  更新结果
     */
    @Override
    public int setRead(String userId) {
        return qaMessageMapper.setRead(userId);
    }
}
