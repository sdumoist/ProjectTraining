package com.jxdinfo.doc.statistical.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.question.model.QaQuestion;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 统计mapper
 * @author cxk
 * @since 2021-05-14
 */
public interface StatisticalMapper extends BaseMapper<QaQuestion> {


    /**
     * 按专业区分，分别有多少个
     * @return
     */
    List<Map<String,Object>> distinguishMajor();


    /**
     * 统计及时率
     * @param queIdStr 某专业下所有问题ID的字符串
     * @return
     */
    int timelyRate(@Param("queIdStr") String queIdStr);
}
