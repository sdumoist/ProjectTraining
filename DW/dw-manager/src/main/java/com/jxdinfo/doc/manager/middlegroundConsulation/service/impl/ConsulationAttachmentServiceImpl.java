package com.jxdinfo.doc.manager.middlegroundConsulation.service.impl;

import com.jxdinfo.doc.manager.middlegroundConsulation.dao.ConsulationAttachmentMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.dao.MiddlegroundConsulationMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulationAttachment;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.ConsulationAttachmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName: ConsulationAttachmentServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/2/14
 * @Version: 1.0
 */
@Service
public class ConsulationAttachmentServiceImpl implements ConsulationAttachmentService{
    @Resource
    private ConsulationAttachmentMapper consulationAttachmentMapper;

    @Override
    public List<MiddlegroundConsulationAttachment> getAttachmentList(String consulationId) {
        return consulationAttachmentMapper.getAttachmentList(consulationId);
    }
}
