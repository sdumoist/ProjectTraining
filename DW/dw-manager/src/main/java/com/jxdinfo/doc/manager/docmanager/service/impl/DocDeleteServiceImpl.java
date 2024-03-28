package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docmanager.dao.DocDeleteMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocDelete;
import com.jxdinfo.doc.manager.docmanager.service.DocDeleteService;
import org.springframework.stereotype.Service;

@Service
public class DocDeleteServiceImpl extends ServiceImpl<DocDeleteMapper, DocDelete>
        implements DocDeleteService {
}
