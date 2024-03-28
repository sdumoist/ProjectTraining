package com.jxdinfo.doc.statistical.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.question.model.QaQuestion;

import java.util.List;
import java.util.Map;


/**
 * 统计service
 * @author cxk
 * @since 2021-05-14
 */
public interface StatisticalService extends IService<QaQuestion> {


    /**
     * 获取Echart 图 数据
     * @param majorIdList 专业字典ID list
     * @return
     */
    Map getEchartData(List<String> majorIdList);

    /**
     * 获取Echart 图 数据
     * @return
     */
    List<Map<String,Object>> getTableData();
}
