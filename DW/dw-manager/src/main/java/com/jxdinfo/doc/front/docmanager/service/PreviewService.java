package com.jxdinfo.doc.front.docmanager.service;

import java.util.List;
import java.util.Map;

/**
 * 类的用途：预览页面获取预览文件的路径
 * 创建日期：
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
public interface PreviewService {

    /**
     * 获取预览文件的路径
     * @param docId 文件ID
     * @return List<Map<String,String>> 路径文件集合
     */
    public List<Map<String,String>> getFoldPathByDocId(String docId,String showType);
    /**
     * 获取预览文件夹的路径
     *
     * @param foldInfo 文件夹ID、文件夹名
     * @return List<Map<String,String>> 路径文件集合
     */
    public List<Map<String, String>> getFoldPathByFolder(Map<String, String> foldInfo);
}
