package com.jxdinfo.doc.front.groupmanager.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FrontDocGroupMapper {

    /**
     * 获取当前登录人所在的所有群组
     * @param userId
     * @return
     */
    List<String> getPremission(@Param("userId") String userId);
    
}