package com.jxdinfo.doc.front.topicmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.topicmanager.dao.DocUserTopicMapper;
import com.jxdinfo.doc.front.topicmanager.model.DocUserTopic;
import com.jxdinfo.doc.front.topicmanager.service.DocUserTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by ZhongGuangrui on 2019/1/22.
 * 用户定制专题 服务层
 */
@Service
public class DocUserTopicServiceImpl extends ServiceImpl<DocUserTopicMapper,DocUserTopic> implements DocUserTopicService{
    @Autowired
    private DocUserTopicMapper docUserTopicMapper;
}
