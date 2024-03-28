package com.jxdinfo.doc.manager.topicmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.topicmanager.dao.MessageMapper;
import com.jxdinfo.doc.manager.topicmanager.model.Message;
import com.jxdinfo.doc.manager.topicmanager.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;
    @Override
    public void insertMessage(List<Message> messageList) {
        messageMapper.insertMessageList(messageList);
    }

    @Override
    public List<Message> getList(String name, String month, String year,Integer pageNum,Integer pageSize) {
     return    messageMapper.getList(name,month,year,pageNum,pageSize);
    }

    @Override
    public int getListCount(String name, String month, String year) {
        return    messageMapper.getListCount(name,month,year);
    }

    @Override
    public void  deleteMessage() {
            messageMapper.deleteMessage();
    }
}
