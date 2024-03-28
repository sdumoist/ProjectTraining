package com.jxdinfo.doc.front.docsearch.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;


public interface SeachStrengthenMapper {

    Map<String, String> getFileInfo(@Param("id")  String id);
}
