package com.jxdinfo.doc.question.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.question.dao.QaTextMapper;
import com.jxdinfo.doc.question.model.QaText;
import com.jxdinfo.doc.question.service.QaTextService;
import org.springframework.stereotype.Service;

/**
 * 问答纯文本service实现
 */
@Service
public class QaTextServiceImpl extends ServiceImpl<QaTextMapper,QaText> implements QaTextService {
}
