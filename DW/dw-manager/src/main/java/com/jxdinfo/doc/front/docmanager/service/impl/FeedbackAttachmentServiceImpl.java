package com.jxdinfo.doc.front.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.docmanager.dao.FeedbackAttachmentMapper;
import com.jxdinfo.doc.front.docmanager.model.FeedbackAttachment;
import com.jxdinfo.doc.front.docmanager.service.FeedbackAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 反馈附件表 服务层实现类
 * </p>
 *
 * @author  zhongguangrui
 * @since 2018-12-04
 */
@Service
public class FeedbackAttachmentServiceImpl extends ServiceImpl<FeedbackAttachmentMapper,FeedbackAttachment> implements FeedbackAttachmentService{
    @Autowired
    private FeedbackAttachmentMapper feedbackAttachmentMapper;
}
