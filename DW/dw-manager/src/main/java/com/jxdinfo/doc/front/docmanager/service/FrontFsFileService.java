package com.jxdinfo.doc.front.docmanager.service;

import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

import java.util.List;
import java.util.Map;

/**
 * 类的用途：获取首页文件目录<p>
 * 创建日期：
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
public interface FrontFsFileService {

    /**
     * 获取首页文件目录
     * @param userId 用户名
     * @param groupList 群组集合
     * @param adminFlag 权限标志（ 1为超管和文库管理员，2为部门负责人管理员，3普通用户）
     * @return List<FsFolder> 文件夹集合
     */
    List<FsFolder> getFsFileList(String userId, List groupList, Integer adminFlag);
    /**
     * 获取首页文件目录
     * 手机端方法
     * @param userId 用户名
     * @param groupList 群组集合
     * @param adminFlag 权限标志（ 1为超管和文库管理员，2为部门负责人管理员，3普通用户）
     * @return List<FsFolder> 文件夹集合
     */
    List<FsFolder> getFsFileListMobile(String userId, List groupList, Integer adminFlag);

    /**
     * 获取根节点
     */
    List<FsFolder> getRoot();

    /**
     * 获取下载次数等信息
     */
    List<Map> getInfo(List ids, String userId, List<String> listGroup,List roleList);

    /**
     * 判断是否是子节点
     */
    public boolean isChildren(String id);

    /**
     * 获得docInfo的list（最新动态）
     */
    List<DocInfo> getList(Integer pageNumber, Integer pageSize);

    /**
     * 获得docInfo的list（最新动态-管理员）
     */
    List<DocInfo> getListByAdmin(Integer pageSize);

    /**
     * 获得docInfo的list（最新动态-有权限）
     */
    List<DocInfo> getNewList(Integer pageNumber, Integer pageSize, List groupList, String userId, String orgId, String levelCodeString,List roleList);

    /**
     * 获得docInfo的list（置顶文件-有权限）
     */
    List<DocInfo> getTopList(List groupList, String userId, String orgId, String levelCodeString,List roleList);

    /**
     * 获得docInfo的list（置顶文件-管理员）
     */
    List<DocInfo> getTopListByAdmin();

    /**
     * 获得docInfo的list（最新动态）
     */
    List<FsFolderView> getListByType(Integer pageNumber, Integer pageSize, String folderId);

    List<DocInfo> getListByFolderId(Integer pageNumber, Integer pageSize, String folderId);

    /**
     * 获得docInfo的list（最新动态）
     */
    List<DocInfo> getListByPermission(Integer pageNumber, Integer pageSize);
    List<Map> hotWord(Integer beginNum,Integer endNum);
    List<Map> hotWordByLevelCode(Integer beginNum,Integer endNum,String levelCode);
    Integer hotWordCount();
    int hotWordNum();


}
