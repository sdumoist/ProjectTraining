package com.jxdinfo.doc.manager.docintegral.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docintegral.dao.ExemptIntegralMapper;
import com.jxdinfo.doc.manager.docintegral.model.ExemptIntegral;
import com.jxdinfo.doc.manager.docintegral.service.ExemptIntegralService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ExemptIntegralServiceImpl extends ServiceImpl<ExemptIntegralMapper,ExemptIntegral> implements ExemptIntegralService {

    @Resource
    private ExemptIntegralMapper exemptIntegralMapper;

    @Override
    public List<ExemptIntegral> show(String title, Integer beginIndex, Integer limit) {
        return exemptIntegralMapper.show(title,beginIndex,limit);
    }

    @Override
    public int showCount(String title) {
        return exemptIntegralMapper.showCount(title);
    }

    @Override
    public List<ExemptIntegral> addCheck(List ids) {
        return exemptIntegralMapper.addCheck(ids);
    }
}
