package com.jxdinfo.doc.unstructured.service;


import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Map;

public interface UnstructurePlatformApiService {

    /**
     * 获取目录信息
     * folderId 如果folderId参数不为空 则递归查询子级目录
     *          如果folderId参数为空 则全部目录信息
     * @return
     */
    List<Map> getFoldList(String folderId);

    /**
     * 注册系统
     *
     * @param systemId   系统id
     * @param systemName 系统名称
     * @param systemKey  系统key
     * @param userId  注册人
     */
    void systemRegister(String systemId, String systemName, String systemKey, String userId);

    /**
     * 新增目录
     *
     * @param folderId   目录id
     * @param folderName 目录名称
     * @param parentId   父级id
     * @param userId   用户id
     */
    void folderAdd(String folderId, String folderName, String parentId, String userId);

    /**
     * 删除目录
     *
     * @param folderIds   删除目录id
     * @param cascadeType 是否级联删除 1：是2：否
     * @param fileDelType 是否删除文件 1：是2：否
     * @param userId    用户id
     */
    void foldersDel(String folderIds, String cascadeType, String fileDelType, String userId);

    /**
     * 新增目录(集合)
     *
     * @param folders           目录集合信息
     * @param successFoldList   成功新增的目录集合
     * @param errorFoldIdList   新增失败的目录id集合
     * @param successFoldIdList 新增成功的目录id集合
     * @param errorMsg          新增失败的目录信息
     * @param userId          用户id
     */
    void foldersAdd(JSONArray folders, JSONArray successFoldList, JSONArray errorFoldIdList, JSONArray successFoldIdList, StringBuilder errorMsg, String userId);
}
