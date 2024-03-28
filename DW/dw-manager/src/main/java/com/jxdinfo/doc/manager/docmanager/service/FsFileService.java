package com.jxdinfo.doc.manager.docmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件系统-文件 服务类
 * </p>
 *
 * @author smallcat
 * @since 2018-06-30
 */
public interface FsFileService extends IService<FsFile> {

    /**
     * 获取子目录(不包含文件)
     */
    public List<FsFile> getChildrenFolder(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag);

    /**
     * 获取子目录
     */
    public List<FsFile> getChildren(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag);

    /**
     * 获取子目录
     */
    public List<FsFile> getChildrenTable(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag);

    /**
     * 获取子目录数量
     */
    public int getNum(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag);

    /**
     * 动态加载文件树
     */
    List<FsFolder> getTreeDataLazy(String id, Integer adminFlag, List groupList, String userId, String type);

    /**
     * 获取根节点
     */
    List<FsFolder> getRoot();

    /**
     * 判断是否是子节点
     */
    public boolean isChildren(String id);

    /**
     * 删除文件（级联删除）
     */
    int deleteInIds(List ids);

    /**
     * 删除权限表
     */
    int deleteScope(List ids);

    void backAuth(String fsFileIds, String chooseType);

    /**
     * 删除权限表
     */
    int deleteScopeClient(List ids, String userId);

    /**
     * 删除权限表
     */
    int deleteScopeYYZC(List ids, String userId);

    /**
     * 真删除
     */
    int deleteReally(List ids, String userId);

    /**
     * 获取某个节点下所有子节点ID及本身ID
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String getChildFsFile(String rootId);

    /**
     * 判断目录下是否有文件
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String checkChildType(String rootId);

    /**
     * 新增重名检测
     */
    List<FsFile> addCheck(String pid, String name);

    /**
     * 获得子目录文件
     */
    List<FsFolder> getChildList(List pids, List groupList, String userId, Integer adminFlag, String type);

    /**
     * 统计子目录数量
     */
    List<Map> getChildCountList(List pids, List groupList, String userId, Integer adminFlag, String type);

    List<FsFile> countFileName(String pid, List list);

    /**
     * 获取下载次数等信息
     */
    List<Map> getInfo(List ids, String userId, List<String> listGroup, String levelCode, String orgId,List roleList);

    /**
     * 获取对应文件id
     */
    List<Map> getDocId(String ids);

    List searchLevel();

    List downloadAble();

    List getFsFolderDetail(String fsFileId);

    List getFsfileDetail(String fsFileId);

    void updateFileAuthor(String fileId, String authorId, String contactsId);

    List<Map> getPersonList(int pageNumber, int pageSize, String name, String deptId);

    int getPersonNum(String name, String deptId);

    List getAuthority(String fileId);

    /**
     * 根据MD5获取文件
     *
     * @param md5 md5值
     * @return 节点ID字符串
     */
    List<FsFile> getInfoByMd5(String md5);

    /**
     * 移动文件
     *
     * @return boolean
     * @author: yjs
     * @Param: fileId
     * @Param: folderId
     */
    boolean remove(String fileId, String folderId, String userId);

    /**
     * 获取已删除的文件信息
     * @param params 查询参数
     * @return 查询结果
     */
    List<Map> getDeletedFiles(Page page, Map params);

    /**
     * 真删除文档相关数据
     * @param ids 文档id
     * 删除结果（1：成功；0：失败）
     */
    int delDoc(String[] ids);

}
