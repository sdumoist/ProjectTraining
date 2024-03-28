package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docmanager.dao.DocEsLogMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocEsLog;
import com.jxdinfo.doc.manager.docmanager.service.DocEsLogService;
import org.springframework.stereotype.Service;

@Service
public class DocEsLogServiceImpl  extends ServiceImpl<DocEsLogMapper, DocEsLog> implements DocEsLogService {
}
