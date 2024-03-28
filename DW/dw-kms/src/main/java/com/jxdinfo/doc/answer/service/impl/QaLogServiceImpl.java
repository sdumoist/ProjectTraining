package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.answer.dao.QaLogMapper;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.service.QaLogService;
import org.springframework.stereotype.Service;

/**
 * 问答日志service层实现类
 * @author sjw
 * @since 2021-02-23
 */
@Service
public class QaLogServiceImpl extends ServiceImpl<QaLogMapper,QaLog> implements QaLogService {
}
