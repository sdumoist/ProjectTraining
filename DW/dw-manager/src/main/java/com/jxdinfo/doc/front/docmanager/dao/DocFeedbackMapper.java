package com.jxdinfo.doc.front.docmanager.dao;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.front.docmanager.model.DocFeedback;

/**
 * <p>
 * 文档问题反馈表 Mapper 接口
 * </p>
 *
 * @author  zhongguangrui
 * @since 2018-12-03
 */
public interface DocFeedbackMapper extends BaseMapper<DocFeedback>{
    /**
     * 根据反馈ID查询反馈记录与附件列表
     * @param feedbackId    反馈ID
     * @return  反馈对象
     */
    DocFeedback selectDetailFeedback(@Param("feedbackId") String feedbackId);

}
