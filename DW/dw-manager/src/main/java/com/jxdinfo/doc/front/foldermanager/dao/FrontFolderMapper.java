package com.jxdinfo.doc.front.foldermanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录表 Mapper 接口
 * </p>
 *
 * @author lyq
 * @since 2018-08-09
 */
public interface FrontFolderMapper extends BaseMapper<FsFolder> {

    /**
     * 查询当前用户所拥有的目录
     */
    List<FsFolder> getTreeDataLazy(@Param("id") String id, @Param("groupList") List groupList,
                                   @Param("userId") String userId, @Param("type") String type);

    /**
     * 超级管理员动态加载文件树
     */
    List<FsFolder> getTreeDataLazyBySuper(@Param("id") String id);

    List<FsFolder> selectByLevelCode(@Param("id") String id, @Param("userId") String userId,
                                     @Param("levelCodeString") String levelCodeString);

    /**
     * 树，超级管理员二级
     */
    List<FsFolder> getChildListForSuperAdmin(@Param("fileList") List fileList);

    List<Map> getChildCount(@Param("list") List list, @Param("userId") String userId, @Param("levelCodeString") String levelCodeString);

    List<Map> getChildCountListForSuperAdmin(@Param("list") List list);

    /**
     * 通过层级码查询目录集合
     *
     * @param list          夫级目录集合
     * @param userId        当前用户
     * @param levelCodeString 层级码集合
     * @return List<FsFolder> 文件夹集合
     */
    List<FsFolder> selectByLevelCodeList(@Param("list") List list, @Param("userId") String userId,
                                         @Param("levelCodeString") String levelCodeString);

    /**
     * 获取根节点
     */
    List<FsFolder> getRoot();

    /**
     * 管理员获取子目录
     */
    List<FsFolderView> getFilesAndFloderByAdmin(@Param("levelCodeString") String levelCodeString, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                @Param("id") String id, @Param("name") String name,
                                                @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                                @Param("operateType") String operateType, @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("roleList") List roleList);


    /**
     * 获取子目录（含文件）
     */
    List<FsFolderView> getFilesAndFloderBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                     @Param("id") String id, @Param("name") String name,
                                                     @Param("orderResult") String orderResult,
                                                     @Param("typeArr") String[] typeArr, @Param("userId") String userId
    );

    /**
     * 获取子目录（含文件）数量
     */
    int getFilesAndFloderNumBySuperAdmin(@Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult, @Param("typeArr") String[] typeArr);

    /**
     * 子目录数量
     */
    int getFilesAndFloderByAdminNum(@Param("levelCodeString") String levelCodeString, @Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                    @Param("operateType") String operateType,
                                    @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("roleList") List roleList);

    int getFileNumBySuperAdmin(@Param("id") String id, @Param("name") String name, @Param("typeArr") String[] typeArr);


    int getFileNum(@Param("id") String id, @Param("name") String name
            , @Param("groupList") List groupList, @Param("userId") String userId,
                   @Param("type") String type, @Param("typeArr") String[] typeArr,
                   @Param("levelCodeString") String levelCodeString, @Param("orgId") String orgId, @Param("roleList") List roleList);


    int selectByLevelCodeNum(@Param("id") String id, @Param("userId") String userId,
                             @Param("levelCodeString") String levelCodeString, @Param("name") String name)
            ;

    List<FsFolder>  getFsFolderByName(@Param("name") String name)
            ;

    List<Map> getFolderList(@Param("pFolderId") String pFolderId, @Param("startNum") int startNum, @Param("size") int size);

  List<DocInfo>  getFileByAuthorBySuper(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                        @Param("userName") String userName, @Param("typeArr") String[] typeArr, @Param("order") Integer order);

    Integer  getFileByAuthorBySuperNum(@Param("userName") String userName, @Param("typeArr") String[] typeArr);

    List<DocInfo>  getFileByAuthorByAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                          @Param("userName") String userName, @Param("groupList") List groupList,
                                          @Param("userId") String userId, @Param("levelCode") String levelCode,
                                          @Param("orgId") String orgId, @Param("typeArr") String[] typeArr, @Param("roleList") List roleList, @Param("order") Integer order);

  Integer getFileByAuthorByAdminNum(@Param("userName") String userName, @Param("groupList") List groupList,
                                    @Param("userId") String userId, @Param("levelCode") String levelCode,
                                    @Param("orgId") String orgId, @Param("typeArr") String[] typeArr, @Param("roleList") List roleList);

    List<Map> getFolderListPerson(@Param("pFolderId")String pFolderId, @Param("startNum") int startNum, @Param("size") int size, @Param("userId") String userId,@Param("levelCodeString") String levelCodeString );
}
