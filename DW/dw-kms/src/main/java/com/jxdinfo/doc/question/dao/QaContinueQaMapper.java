package com.jxdinfo.doc.question.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.question.model.QaContinueQa;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 追问追答mapper
 * @author sjw
 * @since 2021-02-24
 */
public interface QaContinueQaMapper extends BaseMapper<QaContinueQa> {

    /**
     * 问题详情查询
     * @return  查询结果
     */
    List<Map<String, Object>> getContinutQaByAnswer(@Param("ansId") String ansId);

}
