package com.jxdinfo.doc.manager.foldermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录表 服务类
 * </p>
 * @author zf
 * @since 2018-09-06
 */
public interface IFsFolderService extends IService<FsFolder> {

    /**
     * 查询目录下的子目录和文件
     *
     * @param foldId
     * @return
     */
    List<FsFolderView> getFilesAndFloderAll(String foldId);

    /**
     * @Author zoufeng
     * @Description 获取目录子集
     * @Date 10:34 2018/9/10
     * @Param [fsFolderPage]
     * @return java.util.List<com.jxdinfo.doc.manager.foldermanager.model.FsFolder>
     **/
    public List<FsFolder> getChildren(FsFolderParams fsFolderParams);

    /**
     * 获取子目录数量
     * @return 子目录数量
     */
    public int getNum(FsFolderParams fsFolderParams);

    /**
     * 获取根节点信息
     * @return 目录信息
     */
    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams);
    public List<FsFolder> getTreeDataLazyMobile(FsFolderParams fsFolderParams,List folderIds);


    /**
     * 获取根节点
     * @return 返回根节点信息
     */
    public List<FsFolder> getRoot();

    /**
     * 判断是否是子节点
     * @param id 节点id
     * @return 返回是否有子节点
     */
    public boolean isChildren(String id);

    /**
     * 级联删除文件目录
     * @param ids 选中目录id
     * @return 删除条数
     */
    public int deleteInIds(List ids);

    /**
     * 获取某个节点下所有子节点ID及本身ID
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String getChildFsFolder(String rootId);

    /**
     * 新增目录重名检测
     * @param pid 打开文件的id
     * @param name 新增的文件名
     * @return 是否重名
     */
    public List<FsFolder> addCheck(String pid, String name, String folderId);
    public List<FsFolder> selectFoldersByLevelCode(String levelCode,Integer length);

    /**
     * 获取第一级目录
     * @param pids 第一级目录id
     * @param groupList 群组id
     * @param groupList 用户id
     * @param adminFlag 管理员级别
     * @param type 前后台标识
     * @return 第一级节点相关信息
     */
    public List<FsFolder> getChildList(List pids, List groupList, String userId, Integer adminFlag, String type, String levelCodeString);
    public List<FsFolder> getChildListMobile(List pids, List groupList, String userId, Integer adminFlag, String type, String levelCodeString,List folderIds);

    /**
     * 获取下级信息
     * @param pids 当前目录id
     * @param groupList 群组id
     * @param userId 当前用户id
     * @param adminFlag 管理员级别
     * @param type 前后台
     * @return 下一级节点信息
     */
    public List<Map> getChildCountList(List pids, List groupList, String userId, Integer adminFlag, String type, String levelCodeString);
    public List<Map> getChildCountListMobile(List pids, List groupList, String userId, Integer adminFlag, String type, String levelCodeString,List folderIds);

    /**
     * 查询是否存在重名记录（粘贴）
     * @param pid 打开文件的id
     * @param list 文件名称
     * @return
     */
    public List<FsFolder> countFolderName(String pid, List list);

    /**
     * 查询当前目录的权限
     * @param folderId 目录id
     * @return 返回权限集合
     */
    public List getAuthority(String folderId);

    /**
     * 文件统计获取文件
     * @param pageNumber 页数
     * @param pageSize 每页条数
     * @return 文件信息
     */
    public List<FsFolder> getChildrenByRoot(int pageNumber, int pageSize);

    /**
     * 判断目录下是否有文件
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String checkChildType(String rootId);

    /**
     *获取级别
     * @return 返回级别信息
     */
    public List searchLevel();

    /**
     * 获取人员信息
     * @param pageNumber 页数
     * @param pageSize 每页显示多少条
     * @param name 人员姓名
     * @param deptId 组织机构id
     * @return 人员信息集合
     */
    public List<Map> getPersonList(int pageNumber, int pageSize, String name, String deptId,List roleList);

    /**
     *获取下级目录信息
     * @param pageNumber 页数
     * @param pageSize 每页多少条
     * @param id 文件id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return
     */
    public List<FsFolderView> getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                                List groupList, String userId, Integer adminFlag, String operateType,
                                                String levelCodeString, String levelCode, String isDesc, String orgId,List roleList);

    /**
     *手机端获取下级目录信息
     * @param pageNumber 页数
     * @param pageSize 每页多少条
     * @param id 文件id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return
     */


    public List<FsFolderView> getFilesAndFloderMobile(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                                      List groupList, String userId, Integer adminFlag, String operateType,
                                                      String levelCodeString, String levelCode, String isDesc, String orgId,
                                                      List roleList,List folderIds);

    /**
     * 获取下级文件及目录的数量
     * @param id 节点idgetFilesAndFloderNum
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    public int getFilesAndFloderNum(String id, String[] typeArr, String name, String orderResult,
                                    List groupList, String userId, Integer adminFlag, String operateType, String levelCodeString, String levelCode, String orgId,List roleList);

    public int getFilesAndFloderNumMobile(String id, String[] typeArr, String name, String orderResult,
                                    List groupList, String userId, Integer adminFlag, String operateType,
                                    String levelCodeString, String levelCode, String orgId,List roleList,List folderIds);

    /**
     * 获取最大的层级码
     * @param parentId 父节点id
     * @return 最大的层级码
     */
    public String getCurrentMaxLevelCode(String parentId);

    /**
     * 获取子文件的数量
     * @param id 节点id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员标识
     * @param type 前后台标识
     * @return 文件的数量
     */
    public int getFileNum(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag,
                          String type, String levelCodeList, String orgId,List roleList);

    /**
     * 修改更新文件--只修改名字和权限
     * @param fsFolder 目录对象
     * @return 返回是否成功
     */
    public String updateFsFolder(FsFolder fsFolder);

    /**
     * 生产目录层级码
     *
     * @param rootId 根节点ID
     * @return void
     */
    void addLevel(String rootId);

    /**
     * 查询当前目录后的层级码数量
     * @param rootId 根节点ID
     * @return void
     */
    int  getChildCodeCount(String rootId);

    /**
     * 动态加载目录树
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    List getTreeDataLazy(String id, String type);

    List getTreeDataLazyMobile(String id, String type,List folderIds);


    /**
     * 动态加载目录树
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    List getTreeDataLazyClient(String id, String type,String userId,String orgId,  List<String> listGroup ,  List<String> roleList);

    /**
     * @Author zoufeng
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     * @return java.util.List<java.util.Map>
     **/
    List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList);

    /**
     * @return java.lang.String
     * @Author zoufeng
     * @Description 获取层级码
     * @Date 10:24 2018/9/10
     * @Param [parentCode, parentId]父节点层级码，父节点id
     **/
      String getCurrentLevelCode(String parentCode, String parentId);

    /**
     * @return 目录树信息
     * @title: 查询下级目录
     * @description: 查询下级节点（目录）
     * @date: 2018-8-15
     * @author: yjs
     */
    Object getChildren(FsFolderParams fsFolderParams, String nameFlag);

    /**
     * @Author zoufeng
     * @Description 判断选中目录是否有编辑权限
     * @Date 14:44 2018/10/16
     * @Param
     * @return
     **/
    public boolean getIsEdit(String chooseFolderId);

    /**
     * @Author zoufeng
     * @Description 判断选中目录是否有编辑权限
     * @Date 14:44 2018/10/16
     * @Param
     * @return
     **/
    public boolean getIsEditClient(String chooseFolderId,String userId,String orgId, List<String> listGroup, List<String> roleList );

    /**
     *  获得个人上传信息
     * @author: yjs
     * @Param: fileId
     * @Param: folderId
     * @return boolean
     */
    List<FsFolderView>  getPersonUpload(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order);

    List<FsFolderView>  getPersonUploadMobile(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order,List folderIds);

    /**
     *  获得个人上传信息
     * @author: lishilin
     * @Param: fileId
     * @Param: folderId
     * @return boolean
     */
    List<FsFolderView>  getPersonUploadClient(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order,String timeType);

    /**
     *  获得个人上传信息数量
     * @author: yjs
     * @Param: fileId
     * @Param: folderId
     * @return boolean
     */
    int getPersonUploadNum(String userId, String name);

    int getPersonUploadNumMobile(String userId, String name,List folderIds);

    /**
     *  获得个人上传信息数量
     * @author: lishilin
     * @Param: fileId
     * @Param: folderId
     * @return boolean
     */
    int getPersonUploadNumClient(String userId, String name,String timeType);

    String  getFolderNameByLevelCode(String levelCode);

    /**
     * 根据levelCodeString查询一级、二级、三级目录
     * @param levelCodeString
     * @return
     */
    List<FsFolder> getFolderByLevelCodeStringFirst(String levelCodeString, Integer adminFlag);
    List<FsFolder> getFolderByLevelCodeStringSecond(String levelCodeString,Integer adminFlag);
    List<FsFolder> getFolderByLevelCodeStringThird(String levelCodeString,Integer adminFlag);
    List<FsFolder> getFolderByLevelCodeStringFirstByFolderId(String levelCodeString,Integer adminFlag,String folderId);
    /**
     * 生成目录路径
     *
     * @param rootId 根节点ID
     * @return void
     */
    void addPath(String rootId);

    /**
     * 获得目录总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Integer getChildFolderNum(String levelCode);

    /**
     * 获得文件总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Integer getChildFileNum(String levelCode);

    /**
     * 获得文件总大小（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    Long getTotalFileSize(String levelCode);

    /**
     *获取下级目录信息
     * @param pageNumber 页数
     * @param pageSize 每页多少条
     * @param id 文件id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return
     */
    public List<FsFolderView> getFilesAndFloderByFolderShare(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                                             List groupList, String userId, Integer adminFlag, String operateType,
                                                             String levelCodeString, String levelCode, String isDesc, String orgId);

    /**
     * 获取下级文件及目录的数量
     * @param id 节点id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    public int getFilesAndFloderNumByFolderShare(String id, String[] typeArr, String name, String orderResult,
                                                 List groupList, String userId, Integer adminFlag, String operateType, String levelCodeString, String levelCode, String orgId);
    /**
     * 获取子文件的数量
     * @param id 节点id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员标识
     * @param type 前后台标识
     * @return 文件的数量
     */
    public int getFileNumByFolderShare(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag,
                                       String type, String levelCodeList, String orgId);

    String  getPersonPic( String name);
    String  getPersonPath( String name);
    /**
     * 文件统计获取文件
     * @param pageNumber 页数
     * @param pageSize 每页条数
     * @return 文件信息
     */
    public List getDeptList(int pageNumber, int pageSize,String visibleRange);

    public int updateDeptVisibleRange(List<String> organId,String visibleRange);

    /**
     * 判断目录下是否存在待审核文件
     * @param fsFolderIds 目录ID
     * @return 是否
     */
    String checkAuditDoc(String fsFolderIds);

    /**
     * 复制目录
     * @param folderId 目标目录ID
     * @param pId 要移动到的目录id
     * @param isCopyAuth 是否需要复制权限
     * @return
     */
    boolean copyDire(String folderId, String pId, String isCopyAuth);

    /**
     * 查根据目录表联查出目录外网访问权限集合
     * @return
     */
    List<Map> findFolderExtranetAuthTree();

    /**
     * 查询目录树
     */

    List<Map> findFolderTree(List<String> folderIds, String folderId);

    /**
     * 查询目录树
     * id  目录id
     * @return
     */
    List<Map> findFolderExtranetAuthTreeLazy(String id);

    List<String> getFsFolderBylevelOrder(String id);
}
