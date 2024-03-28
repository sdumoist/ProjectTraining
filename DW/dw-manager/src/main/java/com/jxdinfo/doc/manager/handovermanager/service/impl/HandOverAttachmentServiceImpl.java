package com.jxdinfo.doc.manager.handovermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.dao.HandOverAttachmentMapper;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOverAttachment;
import com.jxdinfo.doc.manager.handovermanager.service.HandOverAttachmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HandOverAttachmentServiceImpl  extends ServiceImpl<HandOverAttachmentMapper,
        DocHandOverAttachment> implements HandOverAttachmentService {

    @Resource
    private HandOverAttachmentMapper handOverAttachmentMapper;
    @Override
    public List<FsFolderView> getAttachmentList(Integer beginNum, Integer limit, String name, String id) {
        return handOverAttachmentMapper.getAttachmentList(beginNum,limit,name,id);
    }

    @Override
    public Integer getAttachmentCount(String name, String id) {
        return handOverAttachmentMapper.getAttachmentCount(name,id);
    }
}
