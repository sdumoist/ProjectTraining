package com.jxdinfo.doc.manager.topicmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.topicmanager.model.Message;

import java.util.List;

public interface MessageService   extends IService<Message> {
     void insertMessage(List<Message> messageList);
     void deleteMessage();
     List<Message> getList(String name, String month, String year, Integer pageNumber, Integer pageSize);
     int getListCount(String name, String month, String year);
}
