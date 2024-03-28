package com.jxdinfo.doc.manager.docmanager.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件系统-文件 Mapper 接口
 * </p>
 *
 * @author smallcat
 * @since 2018-06-30
 */
public interface FsFileMapper extends BaseMapper<FsFile> {

    // 物理删除
    int deleteScopeReally(List ids);

    // 物理删除
    int deleteDocInfoReally(List ids);

    // 物理删除
    int deleteFileUploadReally(List ids);

    /**
     * 获取子目录（不含文件）
     */
    List<FsFile> getChildrenFolder(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("typeArr") String[] typeArr, @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId);

    /**
     * 获取子目录
     */
    List<FsFile> getChildren(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("typeArr") String[] typeArr, @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId);

    /**
     * 获取超级管理员子目录(不含文件夹)
     */
    List<FsFile> getChildrenFolderBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                               @Param("id") String id, @Param("typeArr") String[] typeArr,
                                               @Param("name") String name, @Param("orderResult") String orderResult);

    /**
     * 获取子目录
     */
    List<FsFile> getChildrenTable(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("typeArr") String[] typeArr, @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId);

    /**
     * 获取超级管理员子目录
     */
    List<FsFile> getChildrenBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                         @Param("id") String id, @Param("typeArr") String[] typeArr,
                                         @Param("name") String name, @Param("orderResult") String orderResult);

    /**
     * 获取超级管理员子目录
     */
    List<FsFile> getChildrenTableBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                              @Param("id") String id, @Param("typeArr") String[] typeArr,
                                              @Param("name") String name, @Param("orderResult") String orderResult);

    /**
     * 获取子目录数量
     */
    int getNum(@Param("id") String id, @Param("typeArr") String[] typeArr, @Param("name") String name
            , @Param("groupList") List groupList, @Param("userId") String userId);

    /**
     * 获取超级管理员子目录数量
     */
    int getNumBySuperAdmin(@Param("id") String id, @Param("typeArr") String[] typeArr, @Param("name") String name
    );

    /**
     * 动态加载文件树
     */
    List<FsFolder> getTreeDataLazy(@Param("id") String id, @Param("groupList") List groupList,
                                   @Param("userId") String userId, @Param("type") String type);

    /**
     * 超级管理员动态加载文件树
     */
    List<FsFolder> getTreeDataLazyBySuper(@Param("id") String id);

    /**
     * 新增重名检测
     */
    List<FsFile> addCheck(@Param("pid") String pid, @Param("name") String name);

    /**
     * 树，二级
     */
    List<FsFolder> getChildList(@Param("fileList") List fileList, @Param("groupList") List groupList,
                                @Param("userId") String userId, @Param("type") String type);

    /**
     * 树，超级管理员二级
     */
    List<FsFolder> getChildListForSuperAdmin(@Param("fileList") List fileList);

    /**
     * 树，二级数量
     */
    List<Map> getChildCountList(@Param("list") List list, @Param("groupList") List groupList,
                                @Param("userId") String userId, @Param("type") String type);

    List<Map> getChildCountListForSuperAdmin(@Param("list") List list);

    /**
     * 新增重名检测
     */
    List<FsFile> countFileName(@Param("pid") String pid, @Param("list") List list);

    /**
     * 剪切重名检测
     */
    List<FsFile> checkName(@Param("pid") String pid, @Param("fileType") String fileType, @Param("name") String[] name);

    /**
     * 获取根节点
     */
    List<FsFolder> getRoot();

    /**
     * 删除文件（级联删除）
     */
    int deleteInIds(List ids);

    /**
     * 删除资源权限
     */
    int deleteScope(List ids);

    /**
     * 更新回收站
     *
     * @param ids
     * @return
     */
    int insertDocRecycle(@Param("list") List ids, @Param("userId") String userId);

    /**
     * 获取某个节点下所有子节点ID及本身ID
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String getChildFsFile(@Param("rootId") String rootId);

    /**
     * 查看子文件夹数量
     *
     * @param id
     * @return 节点ID字符串
     */
    int getNumByChildFloder(@Param("id") String id);

    /**
     * 获取某个节点下所有子节点type
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String getChildFsFileType(@Param("rootId") String rootId);

    /**
     * 获取下载次数等信息
     */
    List<Map> getInfo(@Param("list") List list, @Param("userId") String userId, @Param("groupList") List<String> listGroup,
                      @Param("levelCode") String levelCode, @Param("orgId") String orgId,@Param("roleList") List roleList);

    /**
     * 获取对应文件id
     */
    List<Map> getDocId(String ids);


    List searchLevel();

    List downloadAble();


    List getFsFolderDetail(@Param("fsFileId") String fsFileId);


    List getFsfileDetail(@Param("fsFileId") String fsFileId);

    void updateFileAuthor(@Param("fileId") String fileId, @Param("authorId") String authorId,
                          @Param("contactsId") String contactsId);

    /**
     * 获取权限人员
     */
    List<Map> getPersonList(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                            @Param("name") String name, @Param("deptId") String deptId);

    /**
     * 获取权限人员
     */
    int getPersonNum(@Param("name") String name, @Param("deptId") String deptId);

    /**
     * 获取权限文件列表
     */
    List getAuthority(@Param("fileId") String fileId);

  /*  *//**
     * 根据MD5值判断数据库中是否存在相同的MD5
     * @param md5 md5字符串
     * @return 节点ID字符串
     *//*
    boolean checkMd5(@Param("md5") String md5);*/

    /**
    * 根据服务器的pdf路径找到信息
    * @param pdfPath pdf路径
    * @return List<FsFile>
    */
    List<FsFile> getInfoByPdfPath(@Param("pdfPath") String pdfPath);


    /**
     * 根据服务器的路径找到信息
     * @param pdfPath pdf路径
     * @return List<FsFile>
     */
    List<FsFile> getInfoByPath(@Param("path") String pdfPath);

    /**
     * 根据MD5获取文件
     * @param md5 md5值
     * @return 节点ID字符串
     */
    List<FsFile> getInfoByMd5(@Param("md5") String md5);

    List<FsFile> getThumbByPath(@Param("path") String path);
    List<FsFile> getVideoByPath(@Param("path") String path);

    /**
     * 获取已删除的文件信息
     * @param params 查询参数
     * @return 查询结果
     */
    List<Map> getDeletedFiles(Page page, @Param("params") Map params);
    String getMd5(@Param("id") String id);
    int getFileNum(@Param("md5") String md5);
}
