package com.jxdinfo.doc.manager.docintegral.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docintegral.model.ExemptIntegral;

import java.util.List;

public interface ExemptIntegralService extends IService<ExemptIntegral> {

    /**
     * 查看用户所有积分
     */
    List<ExemptIntegral> show(String title, Integer beginIndex, Integer limit);

    public int showCount(String title);
    List<ExemptIntegral> addCheck(List ids);

}
