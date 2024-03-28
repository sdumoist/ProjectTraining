package com.jxdinfo.doc.manager.docintegral.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docintegral.model.ExemptIntegral;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExemptIntegralMapper extends BaseMapper<ExemptIntegral> {

  List<ExemptIntegral> show(@Param("title") String title, @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit);

  int showCount(@Param("title") String title);
  List<ExemptIntegral> addCheck(@Param("ids") List ids);
}
