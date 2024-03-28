package com.jxdinfo.doc.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.question.dao.QaFileMapper;
import com.jxdinfo.doc.question.model.QaFile;
import com.jxdinfo.doc.question.service.QaFileService;
import org.springframework.stereotype.Service;

/**
 * 问题附件service实现
 * @author yjs
 * @since 2021-03-1
 */
@Service
public class QaFileServiceImpl  extends ServiceImpl<QaFileMapper,QaFile> implements QaFileService {
}
