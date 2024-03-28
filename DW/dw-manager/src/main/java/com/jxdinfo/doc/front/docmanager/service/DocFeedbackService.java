package com.jxdinfo.doc.front.docmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.front.docmanager.model.DocFeedback;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 文档问题反馈表 服务层
 * </p>
 *
 * @author  zhongguangrui
 * @since 2018-12-03
 */
public interface DocFeedbackService extends IService<DocFeedback> {
    /**
     * 根据反馈ID查询反馈记录与附件列表
     * @param feedbackId    反馈ID
     * @return  反馈对象
     */
    DocFeedback selectDetailFeedback(@Param("feedbackId") String feedbackId);
}
