package com.jxdinfo.doc.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.question.dao.SupplementMapper;
import com.jxdinfo.doc.question.model.QaQuestionSupplement;
import com.jxdinfo.doc.question.service.SupplementService;
import org.springframework.stereotype.Service;

/**
 * 问题补充service实现类
 * @author sjw
 * @since 2021-02-24
 */
@Service
public class SupplementServiceImpl extends ServiceImpl<SupplementMapper,QaQuestionSupplement> implements SupplementService {

}
