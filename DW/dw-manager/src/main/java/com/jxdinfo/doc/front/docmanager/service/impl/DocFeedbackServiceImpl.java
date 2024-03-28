package com.jxdinfo.doc.front.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.docmanager.dao.DocFeedbackMapper;
import com.jxdinfo.doc.front.docmanager.model.DocFeedback;
import com.jxdinfo.doc.front.docmanager.service.DocFeedbackService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文档问题反馈表 服务层实现类
 * </p>
 *
 * @author  zhongguangrui
 * @since 2018-12-03
 */
@Service
public class DocFeedbackServiceImpl extends ServiceImpl<DocFeedbackMapper,DocFeedback> implements DocFeedbackService {
    @Autowired
    private DocFeedbackMapper docFeedbackMapper;

    /**
     * 根据反馈ID查询反馈记录与附件列表
     * @param feedbackId    反馈ID
     * @return  反馈对象
     */
    @Override
    public DocFeedback selectDetailFeedback(@Param("feedbackId") String feedbackId) {
        return docFeedbackMapper.selectDetailFeedback(feedbackId);
    }
}
