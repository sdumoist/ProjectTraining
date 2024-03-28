package com.jxdinfo.doc.manager.foldermanager.dao;

import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
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
public interface FsFolderMapper extends BaseMapper<FsFolder> {

    List<FsFolderView> getFilesAndFloderAll(@Param("foldId") String foldId);

    // 查询目录的所有子目录
    List<String> getChildFolders(@Param("folderIds") List<String> folderIds);

    List<Map> selectAllFold();

    List<Map> selectAllFolder(@Param("folderIds") List<String> folderIds, @Param("folderId") String folderId);

    List<Map> getFolderTreeByParentIdLazy(@Param("parentId") String parentId);

    String selectIdsByLevelCode(@Param("levelCodes") List<String> levelCodes);

    List<Map<String,Object>> getDeptList(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,@Param("visibleRange") String visibleRange);

    int updateDeptVisibleRange(@Param("organId") List<String> organId,@Param("visibleRange") String visibleRange);
    /**
     * 获取子目录
     */
    List<FsFolder> getChildren(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("type") String type
            , @Param("roleList") List roleList);


    /**
     * 获取超级管理员子目录
     */
    List<FsFolder> getChildrenBySuperAdmin(@Param("fsFolderParams") FsFolderParams fsFolderParams);

    /**
     * 获取子目录数量
     */
    int getNum(@Param("id") String id, @Param("name") String name
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("type") String type  , @Param("roleList") List roleList);

    /**
     * 获取超级管理员子目录数量
     */
    int getNumBySuperAdmin(@Param("id") String id, @Param("name") String name);

    /**
     * 查询当前用户所拥有的目录
     */
    List<FsFolder> getTreeDataLazy(@Param("id") String id, @Param("groupList") List groupList,
                                   @Param("userId") String userId, @Param("type") String type  , @Param("roleList") List roleList);

    /**
     * 超级管理员动态加载文件树
     */
    List<FsFolder> getTreeDataLazyBySuper(@Param("id") String id);

    /**
     * 新增目录重名检测
     * @param pid 打开文件的id
     * @param name 新增的文件名
     * @return 是否重名
     */
    List<FsFolder> addCheck(@Param("pid") String pid, @Param("name") String name, @Param("folderId") String folderId);

    /**
     * 树，二级
     */
    List<FsFolder> getChildList(@Param("fileList") List fileList, @Param("groupList") List groupList,
                                @Param("userId") String userId, @Param("type") String type  , @Param("roleList") List roleList);

    /**
     * 树，超级管理员二级
     */
    List<FsFolder> getChildListForSuperAdmin(@Param("folderList") List folderList);

    /**
     * 树，二级数量
     */
    List<Map> getChildCountList(@Param("list") List list, @Param("groupList") List groupList,
                                @Param("userId") String userId, @Param("type") String type  , @Param("roleList") List roleList);

    List<Map> getChildCountListForSuperAdmin(@Param("list") List list);

    List<Map> getChildCount(@Param("list") List list, @Param("userId") String userId, @Param("levelCodeString") String levelCodeString);

    List<Map> getChildCountMobile(@Param("list") List list, @Param("userId") String userId, @Param("levelCodeString") String levelCodeString,@Param("folderIds") List folderIds);

    /**
     * 查询是否存在重名记录（粘贴）
     * @param pid 打开文件的id
     * @param list 文件名称集合
     * @return
     */
    List<FsFolder> countFolderName(@Param("pid") String pid, @Param("list") List list);

    /**
     * 剪切重名检测
     */
    List<FsFolder> checkName(@Param("pid") String pid, @Param("fileType") String fileType, @Param("name") String[] name);

    /**
     * 获取根节点
     */
    List<FsFolder> getRoot();

    /**
     * 级联删除文件目录
     * @param ids 选中目录id
     * @return
     */
    int deleteInIds(List ids);

    /**
     * 获取某个节点下所有子节点ID及本身ID
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String getChildFsFolder(@Param("rootId") String rootId);


    /**
     * 查看子文件夹数量
     *
     * @param id
     * @return 节点ID字符串
     */
    int getNumByChildFloder(@Param("id") String id);

    /**
     * 获取某个节点下所有子节点type
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    String getChildFsFolderType(@Param("rootId") String rootId);

    List searchLevel();

    List getFsFolderDetail(@Param("fsFolderId") String fsFolderId);

    /**
     * 查询当前目录的权限
     * @param folderId 目录id
     * @return 返回权限集合
     */
    List getAuthority(@Param("folderId") String folderId);

    /**
     * 获取权限人员
     */
    List<Map> getPersonList(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                            @Param("name") String name, @Param("deptId") String deptId  , @Param("roleList") List roleList);

    /**
     * 获取权限人员
     */
    int getPersonNum(@Param("name") String name, @Param("deptId") String deptId);

    List<String> getFsFolderBylevel(String node);

    /**
     * 取出公司文件夹的子文件夹
     */
    List<FsFolder> getChildrenByRoot(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize);

    /**
     * 获取子目录（含文件）
     */
    List<FsFolderView> getFilesAndFloderBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                     @Param("id") String id, @Param("name") String name,
                                                     @Param("orderResult") String orderResult,
                                                     @Param("typeArr") String[] typeArr, @Param("isDesc") String isDesc
    );

    /**
     * 获取子目录
     */
    List<FsFolderView> getFilesAndFloder(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                         @Param("id") String id, @Param("name") String name,
                                         @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                         @Param("operateType") String operateType  , @Param("roleList") List roleList);

    /**
     * 获取子目录（含文件）数量
     */
    int getFilesAndFloderNumBySuperAdmin(@Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult, @Param("typeArr") String[] typeArr);

    /**
     * 获取子目录数量
     */
    int getFilesAndFloderNum(@Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                             @Param("operateType") String operateType  , @Param("roleList") List roleList);

    /**
     * 子目录数量
     */
    int getFilesAndFloderByAdminNum(@Param("levelCodeString") String levelCodeString, @Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                    @Param("operateType") String operateType, @Param("levelCode") String levelCode
            , @Param("orgId") String orgId, @Param("roleList") List roleLis);

    int getFilesAndFloderByAdminNumMobile(@Param("levelCodeString") String levelCodeString, @Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                    @Param("operateType") String operateType, @Param("levelCode") String levelCode
            , @Param("orgId") String orgId, @Param("roleList") List roleLis,@Param("folderIds") List folderIds);

    List<FsFolder> selectByLevelCode(@Param("id") String id, @Param("userId") String userId,
                                     @Param("levelCodeString") String levelCodeString);

    List<FsFolder> selectByLevelCodeMobile(@Param("id") String id, @Param("userId") String userId,
                                     @Param("levelCodeString") String levelCodeString,List folderIds);

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

    List<FsFolder> selectByLevelCodeListMobile(@Param("list") List list, @Param("userId") String userId,
                                         @Param("levelCodeString") String levelCodeString,@Param("folderIds") List folderIds);

    List<FsFolder> selectByLevelCodePage(@Param("fsFolderParams") FsFolderParams fsFolderParams);


    int selectByLevelCodeNum(@Param("id") String id, @Param("userId") String userId,
                             @Param("levelCodeString") String levelCodeString, @Param("name") String name)
            ;

    /**
     * 管理员获取子目录
     */
    List<FsFolderView> getFilesAndFloderByAdmin(@Param("levelCodeString") String levelCodeString, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                @Param("id") String id, @Param("name") String name,
                                                @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                                @Param("operateType") String operateType,
                                                @Param("levelCode") String levelCode, @Param("isDesc") String isDesc, @Param("orgId") String orgId  , @Param("roleList") List roleList);


    List<FsFolderView> getFilesAndFloderByAdminMobile(@Param("levelCodeString") String levelCodeString, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                @Param("id") String id, @Param("name") String name,
                                                @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                                @Param("operateType") String operateType,
                                                @Param("levelCode") String levelCode, @Param("isDesc") String isDesc, @Param("orgId") String orgId  , @Param("roleList") List roleList,
                                                      @Param("folderExtranetIds")List folderExtranetIds);


    String getCurrentMaxLevelCode(@Param("parentId") String parentId);

    /**
     * 根据父级节点，取出子集的id集合
     *
     * @param parentId 父级节点ID
     * @return void
     */
    List<FsFolder> getChildByParentId(@Param("parentId") String parentId);


    /**
     * 获取下层层级码
     *
     * @param levelCode 层级码
     * @return List<String> 下层层级码集合
     */
    List<String> getChildLevelCode(@Param("levelCode") String levelCode);

    int getFileNumBySuperAdmin(@Param("id") String id, @Param("name") String name, @Param("typeArr") String[] typeArr);

    int getFileNum(@Param("id") String id, @Param("name") String name
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("type") String type,
                   @Param("typeArr") String[] typeArr, @Param("levelCodeString") String levelCodeString, @Param("orgId") String orgId  , @Param("roleList") List roleList);

    /**
     * 查询当前目录后的层级码数量
     * @param rootId 根节点ID
     * @return void
     */
    int  getChildCodeCount(@Param("rootId") String rootId);

    /**
     * 管理员获取子目录
     */
    List<FsFolderView> getPersonUpload(@Param("userId") String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                       @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order, @Param("auditUsing") String auditUsing);

    List<FsFolderView> getPersonUploadMobile(@Param("userId") String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                       @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order, @Param("auditUsing") String auditUsing,List folderIds);
    /**
     * 管理员获取子目录
     */
    List<FsFolderView> getPersonUploadClient(@Param("userId") String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                       @Param("name") String name, @Param("typeArr") String[] typeArr, @Param("order") String order, @Param("timeType") String timeType);

    /**
     * 管理员获取子目录
     */
   int getPersonUploadNum(@Param("userId") String userId,
                          @Param("name") String name, @Param("auditUsing") String auditUsing);

    int getPersonUploadNumMobile(@Param("userId") String userId,
                           @Param("name") String name, @Param("auditUsing") String auditUsing ,
                            @Param("folderIds") List folderIds);

    /**
     * 管理员获取子目录
     */
    int getPersonUploadNumClient(@Param("userId") String userId,
                           @Param("name") String name, @Param("timeType") String timeType);

   String  getFolderNameByLevelCode(@Param("levelCode") String levelCode);
    /**
     * 根据levelCodeString查询一级、二级、三级目录
     * @param levelCodeString
     * @return
     */
    List<FsFolder> getFolderByLevelCodeStringFirst(@Param("levelCodeString") String levelCodeString);
    List<FsFolder> getFolderByLevelCodeStringSecond(@Param("levelCodeString") String levelCodeString);
    List<FsFolder> getFolderByLevelCodeStringThird(@Param("levelCodeString") String levelCodeString);
    List<FsFolder> getFolderByLevelCodeStringFirstBySuper();
    List<FsFolder> getFolderByLevelCodeStringSecondBySuper();
    List<FsFolder> getFolderByLevelCodeStringThirdBySuper();
    List<FsFolder> getFolderByParentLevelCode(@Param("levelCodeString") String levelCodeString);
    List<FsFolder> getFolderByLevelCodeStringFirstByFolderIdBySuper(@Param("folderId") String folderId);
    List<FsFolder> getFolderByLevelCodeStringFirstFolderId(@Param("levelCodeString") String levelCodeString,@Param("folderId") String folderId);
    /**
     * 获得目录总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Integer getChildFolderNum(@Param("levelCode")String levelCode);

    /**
     * 获得文件总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Integer getChildFileNum(@Param("levelCode")String levelCode);

    List<FsFolder> getFolderByLevelCode(@Param("levelCode")String levelCode);
    /**
     * 获得文件总大小（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Long getTotalFileSize(@Param("levelCode")String levelCode);
    /**
     * 获取子目录（含文件）
     */
    List<FsFolderView> getFilesAndFloderBySuperAdminByFolderShare(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                                                  @Param("id") String id, @Param("name") String name,
                                                                  @Param("orderResult") String orderResult,
                                                                  @Param("typeArr") String[] typeArr, @Param("isDesc") String isDesc
    );

    /**
     * 获取子目录（含文件）数量
     */
    int getFilesAndFloderNumBySuperAdminByFolderShare(@Param("id") String id
            , @Param("name") String name, @Param("orderResult") String orderResult, @Param("typeArr") String[] typeArr);

    int getFileNumBySuperAdminByFolderShare(@Param("id") String id, @Param("name") String name, @Param("typeArr") String[] typeArr);
    /**
     * 管理员获取子目录
     */
    String getPersonPic(@Param("name") String name);
    String getPersonPath(@Param("name") String name);

    /**
     * 判断目录下是否存在待审核文件
     * @param fsFolderId 目录ID
     * @return 是否
     */
    String checkAuditDoc(@Param("fsFolderId") String fsFolderId);
     List<FsFolder> selectFoldersByLevelCode(@Param("levelCode")String levelCode,@Param("length")Integer length);

    List<String> getFsFolderBylevelOrder(@Param("code") String node);

    /**
     * 根据目录id查询目录 以及目录的上级目录
     *
     * @return
     */
    List<Map<String, String>> getFolderPath(@Param("foldId") String foldId);

    /**
     * 获取目录外网访问权限集合
     * @return
     */
    List<FsFolder> getFolderExtranetAuthTree();
}
