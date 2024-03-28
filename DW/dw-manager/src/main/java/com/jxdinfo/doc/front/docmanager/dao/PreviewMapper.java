package com.jxdinfo.doc.front.docmanager.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 类的用途：预览页面获取预览文件的路径
 * 创建日期：
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */

public interface PreviewMapper {

    /**
     * 根据用户取文件夹信息
     *
     * @param docId
     * @return Map<String,String>
     */
    Map<String, String> getFoldInfoByDocId(@Param("docId") String docId);

    /**
     * 根据子文件夹ID获取上级文件夹信息
     *
     * @param childFoldId
     * @return Map<String,String>
     */
    Map<String, String> getFoldInfoByChildFoldId(@Param("childFoldId") String childFoldId);
}
