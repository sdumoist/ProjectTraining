package com.jxdinfo.doc.manager.middlegroundConsulation.service;

import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulationAttachment;

import java.util.List;

public interface ConsulationAttachmentService{
    List<MiddlegroundConsulationAttachment> getAttachmentList(String consulationId);
}
