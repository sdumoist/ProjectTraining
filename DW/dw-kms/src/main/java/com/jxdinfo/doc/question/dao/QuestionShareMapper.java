package com.jxdinfo.doc.question.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.question.model.QaShareInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface QuestionShareMapper  extends BaseMapper<QaShareInfo> {
    /**
     * @author yjs
     *
     * @date 2021-3-1
     * @description 新增分享问题
     * @param map 要新增的问题信息
     * @return 影响的数量
     */
    int newShareResource(Map map);

    Map getShareResource(@Param("hash") String hash);

}
