package com.jxdinfo.doc.manager.handovermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOverAttachment;

import java.util.List;

public interface HandOverAttachmentService extends IService<DocHandOverAttachment> {
    List<FsFolderView> getAttachmentList(Integer beginNum, Integer limit, String name , String id);
    Integer getAttachmentCount(String name ,String id);
}
