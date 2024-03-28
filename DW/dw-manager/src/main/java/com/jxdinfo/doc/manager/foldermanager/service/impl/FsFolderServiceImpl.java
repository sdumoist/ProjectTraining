package com.jxdinfo.doc.manager.foldermanager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * <p>
 * 目录表 服务实现类
 * </p>
 *
 * @author zf
 * @since 2018-09-06
 */
@Service
public class FsFolderServiceImpl extends ServiceImpl<FsFolderMapper, FsFolder> implements IFsFolderService {

    /**
     * 目录dao层
     */
    @Resource
    private FsFolderMapper fsFolderMapper;

    @Autowired
    private ISysUsersService sysUsersService;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService folderService;

    /**
     * 权限群组服务类
     */
    @Autowired
    private DocGroupService docGroupService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Autowired
    private ISysUsersService iSysUsersService;

    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Value("${fileAudit.using}")
    private String using;

    @Override
    public List<FsFolderView> getFilesAndFloderAll(String foldId) {
        List<FsFolderView> list = fsFolderMapper.getFilesAndFloderAll(foldId);
        return list;
    }

    /**
     * @param fsFolderParams 获取子目录信息参数
     * @return java.util.List<com.jxdinfo.doc.manager.foldermanager.model.FsFolder>
     * @Author zoufeng
     * @Description 获取子目录信息
     * @Date 19:10 2018/9/7
     **/
    public List<FsFolder> getChildren(FsFolderParams fsFolderParams) {
        List<FsFolder> list = null;

        String orderResult = fsFolderParams.getOrderResult();
        int adminFlag = fsFolderParams.getAdminFlag();
        List<String> groupList = fsFolderParams.getGroupList();
        String userId = fsFolderParams.getUserId();
        String type = fsFolderParams.getType();

        if ("title".equals(orderResult)) {
            orderResult = "folder_name";
        } else if ("doc_type".equals(orderResult)) {
            orderResult = "show_order";
        }

        fsFolderParams.setOrderResult(orderResult);

        if (adminFlag == 1) {
            list = fsFolderMapper.getChildrenBySuperAdmin(fsFolderParams);
        } else {
            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);

            fsFolderParams.setLevelCodeList(levelCodeList);

            list = fsFolderMapper.selectByLevelCodePage(fsFolderParams);
        }
        return list;
    }

    /**
     * 获取子目录数量
     *
     * @return 子目录数量
     */
    public int getNum(FsFolderParams fsFolderParams) {
        int num = 0;
        if (fsFolderParams.getAdminFlag() == 1) {
            num = fsFolderMapper.getNumBySuperAdmin(fsFolderParams.getId(), fsFolderParams.getName());

        } else {
            String levelCodeString = folderService.getLevelCodeByUser(fsFolderParams);
//            List<String> levelCodeList = folderService.getlevelCodeList(fsFolderParams.getGroupList(), fsFolderParams.getUserId(), fsFolderParams.getType());
            num = fsFolderMapper.selectByLevelCodeNum(fsFolderParams.getId(), fsFolderParams.getUserId(), levelCodeString, fsFolderParams.getName());
        }

        return num;
    }

    /**
     * 动态加载文件树
     *
     * @return 目录信息
     */
    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams) {
        List<FsFolder> list = null;
        //获取目录结构
        //超级管理员：1 文库管理员：2
        if (fsFolderParams.getAdminFlag() == 1) {
            //获取节点的信息
            list = fsFolderMapper.getTreeDataLazyBySuper(fsFolderParams.getId());
        } else {

//                List<String> levelCodeList = folderService.getlevelCodeList(fsFolderParams.getGroupList(), fsFolderParams.getUserId(), fsFolderParams.getType());
            list = fsFolderMapper.selectByLevelCode(fsFolderParams.getId(), fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString());
        }
        return list;
    }
    public List<FsFolder> getTreeDataLazyMobile(FsFolderParams fsFolderParams,List folderIds) {
        List<FsFolder> list = null;
        //获取目录结构
        //超级管理员：1 文库管理员：2
        if (fsFolderParams.getAdminFlag() == 1) {
            //获取节点的信息
            list = fsFolderMapper.getTreeDataLazyBySuper(fsFolderParams.getId());
        } else {

//                List<String> levelCodeList = folderService.getlevelCodeList(fsFolderParams.getGroupList(), fsFolderParams.getUserId(), fsFolderParams.getType());
            list = fsFolderMapper.selectByLevelCodeMobile(fsFolderParams.getId(), fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString(),folderIds);
        }
        return list;
    }

    /**
     * @return java.util.List<com.jxdinfo.doc.manager.foldermanager.model.FsFolder>
     * @Author zoufeng
     * @Description 获取根节点信息
     * @Date 11:23 2018/9/10
     * @Param []
     **/
    public List<FsFolder> getRoot() {
        List<FsFolder> list = fsFolderMapper.getRoot();
        return list;
    }

    /**
     * @return boolean 是否有子节点
     * @Author zoufeng
     * @Description 判断是否有子节点
     * @Date 11:25 2018/9/10
     * @Param [id] 目录id
     **/
    @Override
    public boolean isChildren(String id) {
        boolean result = false;
        int num = fsFolderMapper.getNumByChildFloder(id);
        if (num == 0) {
            result = true;
        }
        return result;
    }

    /**
     * @param pid  新增文件id
     * @param name 新增文件名称
     * @return java.util.List<com.jxdinfo.doc.manager.foldermanager.model.FsFolder> 是否重命名
     * @Author zoufeng
     * @Description 获取新增目录重命名检测
     * @Date 11:26 2018/9/10
     **/
    public List<FsFolder> addCheck(String pid, String name, String folderId) {
        List<FsFolder> list = fsFolderMapper.addCheck(pid, name, folderId);
        return list;
    }

    @Override
    public List<FsFolder> selectFoldersByLevelCode(String levelCode,Integer length) {
        return  fsFolderMapper.selectFoldersByLevelCode(levelCode,length);
    }

    /**
     * 获取第一级目录
     *
     * @param list      第一级目录id
     * @param groupList 群组id
     * @param userId    用户id
     * @param adminFlag 管理员级别
     * @param type      前后台标识
     * @return 第一级节点相关信息
     */
    public List<FsFolder> getChildList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type, String levelCodeString) {
        List<FsFolder> listFsFolder = new ArrayList<FsFolder>();
        if (adminFlag == 1) {
            listFsFolder = fsFolderMapper.getChildListForSuperAdmin(list);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
            listFsFolder = fsFolderMapper.selectByLevelCodeList(list, userId, levelCodeString);
        }
        return listFsFolder;
    }
    public List<FsFolder> getChildListMobile(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type,
                                             String levelCodeString,List folderIds) {
        List<FsFolder> listFsFolder = new ArrayList<FsFolder>();
        if (adminFlag == 1) {
            listFsFolder = fsFolderMapper.getChildListForSuperAdmin(list);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
            listFsFolder = fsFolderMapper.selectByLevelCodeListMobile(list, userId, levelCodeString,folderIds);
        }
        return listFsFolder;
    }


    /**
     * 获取下级信息
     *
     * @param userId    当前用户id
     * @param list      当前目录id
     * @param groupList 群组id
     * @param adminFlag 管理员级别
     * @param type      前后台
     * @return 下一级节点信息
     */
    public List<Map> getChildCountList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type, String levelCodeString) {

        List<Map> listMap = null;
        if (adminFlag == 1) {
            listMap = fsFolderMapper.getChildCountListForSuperAdmin(list);
        } else {
            listMap = fsFolderMapper.getChildCount(list, userId, levelCodeString);
        }

        return listMap;
    }
    public List<Map> getChildCountListMobile(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type,
                                       String levelCodeString,List folderIds) {

        List<Map> listMap = null;
        if (adminFlag == 1) {
            listMap = fsFolderMapper.getChildCountListForSuperAdmin(list);
        } else {
            listMap = fsFolderMapper.getChildCountMobile(list, userId, levelCodeString,folderIds);
        }

        return listMap;
    }

    /**
     * @param pid  打开文件id
     * @param list 文件名称
     * @return 是否存在重复记录
     * @Author zoufeng
     * @Description 是否存在重复记录
     * @Date 11:52 2018/9/10
     **/
    public List<FsFolder> countFolderName(@Param("pid") String pid, @Param("list") List list) {
        List<FsFolder> listFsFolder = fsFolderMapper.countFolderName(pid, list);
        return listFsFolder;
    }

    /**
     * 查询当前目录的权限
     *
     * @param folderId 目录id
     * @return 返回权限集合
     */
    public List getAuthority(@Param("folderId") String folderId) {
        List list = fsFolderMapper.getAuthority(folderId);
        return list;
    }

    /**
     * 文件统计获取文件
     *
     * @param pageNumber 页数
     * @param pageSize   每页条数
     * @return 文件信息
     */
    @Override
    public List<FsFolder> getChildrenByRoot(int pageNumber, int pageSize) {
        List<FsFolder> listFsFolder = fsFolderMapper.getChildrenByRoot(pageNumber, pageSize);
        return listFsFolder;
    }

    /**
     * 级联删除文件目录
     *
     * @param ids 选中目录id
     * @return 删除条数
     */
    public int deleteInIds(List ids) {
        int amount = fsFolderMapper.deleteInIds(ids);
        return amount;
    }

    /**
     * 获取某个节点下所有子节点ID及本身ID
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String getChildFsFolder(String rootId) {
        String ids = fsFolderMapper.getChildFsFolder(rootId);
        return ids;
    }

    /**
     * 判断目录下是否有文件
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String checkChildType(String rootId) {
        String ids = fsFolderMapper.getChildFsFolderType(rootId);
        return ids;
    }

    /**
     * 获取级别
     *
     * @return 返回级别信息
     */
    @Override
    public List searchLevel() {
        return fsFolderMapper.searchLevel();
    }

    /**
     * 获取人员信息
     *
     * @param pageNumber 页数
     * @param pageSize   每页显示多少条
     * @param name       人员姓名
     * @param deptId     组织机构id
     * @return 人员信息集合
     */
    @Override
    public List<Map> getPersonList(int pageNumber, int pageSize, String name, String deptId, List roleList) {
        List<Map> list = fsFolderMapper.getPersonList(pageNumber, pageSize, name, deptId,roleList);
        return list;
    }


    /**
     * 获取下级目录信息
     *
     * @param pageNumber  页数
     * @param pageSize    每页多少条
     * @param id          文件id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return
     */
    @Override
    public List<FsFolderView> getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                                String orderResult, List groupList, String userId, Integer adminFlag,
                                                String operateType, String levelCodeString, String levelCode, String isDesc, String orgId, List roleList) {
        List<FsFolderView> list = null;
        if (adminFlag == 1) {
            list = fsFolderMapper.getFilesAndFloderBySuperAdmin(pageNumber, pageSize, id, name, orderResult, typeArr, isDesc);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            list = fsFolderMapper.getFilesAndFloderByAdmin(levelCodeString, pageNumber, pageSize, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, isDesc, orgId,roleList);
        }
        return list;
    }
    /**
     * 手机端获取下级目录信息
     *
     * @param pageNumber  页数
     * @param pageSize    每页多少条
     * @param id          文件id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return
     */
    @Override

    public List<FsFolderView> getFilesAndFloderMobile(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                                      String orderResult, List groupList, String userId, Integer adminFlag,
                                                      String operateType, String levelCodeString, String levelCode, String isDesc,
                                                      String orgId, List roleList,List folderIds) {
        List<FsFolderView> list = null;
        if (adminFlag == 1) {
            list = fsFolderMapper.getFilesAndFloderBySuperAdmin(pageNumber, pageSize, id, name, orderResult, typeArr, isDesc);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            list = fsFolderMapper.getFilesAndFloderByAdminMobile(levelCodeString, pageNumber, pageSize, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, isDesc, orgId,roleList,folderIds);
        }
        return list;
    }


    /**
     * 获取下级文件及目录的数量
     *
     * @param id          节点id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    @Override
    public int getFilesAndFloderNum(String id, String[] typeArr, String name, String orderResult, List groupList,
                                    String userId, Integer adminFlag, String operateType, String levelCodeString,
                                    String levelCode, String orgId,List roleList) {
        int num = 0;
        if (adminFlag == 1) {
            num = fsFolderMapper.getFilesAndFloderNumBySuperAdmin(id, name, orderResult, typeArr);

        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            num = fsFolderMapper.getFilesAndFloderByAdminNum(levelCodeString, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, orgId,roleList);
        }
        return num;
    }
    /**
     * 获取下级文件及目录的数量
     *
     * @param id          节点id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    @Override
    public int getFilesAndFloderNumMobile(String id, String[] typeArr, String name, String orderResult, List groupList,
                                    String userId, Integer adminFlag, String operateType, String levelCodeString,
                                    String levelCode, String orgId,List roleList,List folderIds) {
        int num = 0;
        if (adminFlag == 1) {
            num = fsFolderMapper.getFilesAndFloderNumBySuperAdmin(id, name, orderResult, typeArr);

        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            num = fsFolderMapper.getFilesAndFloderByAdminNumMobile(levelCodeString, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, orgId,roleList,folderIds);
        }
        return num;
    }

    /**
     * 获取最大的层级码
     *
     * @param parentId 父节点id
     * @return 最大的层级码
     */
    @Override
    public String getCurrentMaxLevelCode(String parentId) {
        return fsFolderMapper.getCurrentMaxLevelCode(parentId);
    }


    /**
     * 获取子文件的数量
     *
     * @param id        节点id
     * @param typeArr   文件类型
     * @param name      文件名称
     * @param groupList 群组id
     * @param userId    用户id
     * @param adminFlag 管理员标识
     * @param type      前后台标识
     * @return 文件的数量
     */
    public int getFileNum(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag,
                          String type, String levelCodeString, String orgId, @Param("roleList") List roleList) {
        int num = 0;
        if (adminFlag == 1) {
            num = fsFolderMapper.getFileNumBySuperAdmin(id, name, typeArr);

        } else {
            if ("0".equals(type)) {
                num = fsFolderMapper.getFileNum(id, name, groupList, userId, type, typeArr, levelCodeString, orgId,roleList);
            } else {
//                List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
                num = fsFolderMapper.getFileNum(id, name, groupList, userId, type, typeArr, levelCodeString, orgId,roleList);
            }

        }
        return num;
    }

    /**
     * 修改更新文件--只修改名字和权限
     *
     * @param fsFolder 目录对象
     * @return 返回是否更新成功
     */
    public String updateFsFolder(FsFolder fsFolder) {
        if (fsFolder.getFolderName() == null || "".equals(fsFolder.getFolderName())) {
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            updateById(fsFolder);
        } else {
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            updateById(fsFolder);
        }
        return "success";
    }

    /**
     * 生产目录层级码
     *
     * @param rootId 根节点ID
     * @return void
     */
    @Override
    public void addPath(String rootId) {
        if (rootId == null) {
            rootId = "2bb61cdb2b3c11e8aacf429ff4208431";
        }
        FsFolder fsFolder = fsFolderMapper.selectById(rootId);
        String levelCode = fsFolder.getLevelCode();
        List<FsFolder> list = fsFolderMapper.getFolderByParentLevelCode(levelCode);
        for (int i = 0; i < list.size(); i++) {
            String levelCodeStr = list.get(i).getLevelCode();
            String localName = "";
            for (int j = 1; j <= levelCodeStr.length() / 4; j++) {
                String levelCodeString = levelCodeStr.substring(0, j * 4);
                String folderName = fsFolderMapper.getFolderNameByLevelCode(levelCodeString);
                localName = localName + "\\" + folderName;
            }
            list.get(i).setFolderPath(localName);
            fsFolderMapper.updateById(list.get(i));


        }
    }

    /**
     * 获得目录总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    @Override
    public Integer getChildFolderNum(String levelCode) {
        return fsFolderMapper.getChildFolderNum(levelCode);
    }

    /**
     * 获得文件总数量（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    @Override
    public Integer getChildFileNum(String levelCode) {
        return fsFolderMapper.getChildFileNum(levelCode);
    }

    /**
     * 获得文件总大小（含子集的子集）
     *
     * @param levelCode 根节点ID
     * @return Integer
     */
    @Override
    public Long getTotalFileSize(String levelCode) {
        return fsFolderMapper.getTotalFileSize(levelCode);
    }

    /**
     * 生产目录层级码
     *
     * @param rootId 根节点ID
     * @return void
     */
    @Override
    public void addLevel(String rootId) {
        List<FsFolder> listFsFolder = new ArrayList<>();
        int ss = 1;
        FsFolder root = new FsFolder();
        //如果根节点为空，则取出系统的根节点
        if (rootId == null) {
            root = fsFolderMapper.selectById("2bb61cdb2b3c11e8aacf429ff4208431");
            root.setLevelCode("0001");
        } else {
            root = fsFolderMapper.selectById(rootId);
        }
        //此ID为根目录ID
        listFsFolder.add(root);
        //循环遍历根节点
        List<FsFolder> listFsFirst = fsFolderMapper.getChildByParentId(root.getFolderId());
        if (listFsFirst != null && listFsFirst.size() > 0) {
            for (int i = 0; i < listFsFirst.size(); i++) {
                ss++;
                String levelCode = root.getLevelCode();
                int newI = i + 1;
                if (newI < 10) {
                    levelCode = levelCode + "000" + newI;
                } else if (newI >= 10 && newI < 100) {
                    levelCode = levelCode + "00" + newI;
                } else if (newI >= 100 && newI < 1000) {
                    levelCode = levelCode + "0" + newI;
                } else {
                    levelCode = levelCode + "" + newI;
                }
                listFsFirst.get(i).setShowOrder(ss);
                listFsFirst.get(i).setLevelCode(levelCode);
                listFsFolder.add(listFsFirst.get(i));
                List<FsFolder> listFsSec = fsFolderMapper.getChildByParentId(listFsFirst.get(i).getFolderId());
                if (listFsSec != null && listFsSec.size() > 0) {
                    for (int j = 0; j < listFsSec.size(); j++) {
                        ss++;
                        String levelCodeSec = levelCode;
                        int newJ = j + 1;
                        if (newJ < 10) {
                            levelCodeSec = levelCodeSec + "000" + newJ;
                        } else if (newJ >= 10 && newJ < 100) {
                            levelCodeSec = levelCodeSec + "00" + newJ;
                        } else if (newJ >= 100 && newJ < 1000) {
                            levelCodeSec = levelCodeSec + "0" + newJ;
                        } else {
                            levelCodeSec = levelCodeSec + "" + newJ;
                        }
                        listFsSec.get(j).setShowOrder(ss);
                        listFsSec.get(j).setLevelCode(levelCodeSec);
                        listFsFolder.add(listFsSec.get(j));
                        List<FsFolder> listFsTh = fsFolderMapper.getChildByParentId(listFsSec.get(j).getFolderId());
                        if (listFsTh != null && listFsTh.size() > 0) {
                            for (int k = 0; k < listFsTh.size(); k++) {
                                ss++;
                                String levelCodeTh = levelCodeSec;
                                int newK = k + 1;
                                if (newK < 10) {
                                    levelCodeTh = levelCodeTh + "000" + newK;
                                } else if (newK >= 10 && newK < 100) {
                                    levelCodeTh = levelCodeTh + "00" + newK;
                                } else if (newK >= 100 && newK < 1000) {
                                    levelCodeTh = levelCodeTh + "0" + newK;
                                } else {
                                    levelCodeTh = levelCodeTh + "" + newK;
                                }
                                listFsTh.get(k).setLevelCode(levelCodeTh);
                                listFsTh.get(k).setShowOrder(ss);
                                listFsFolder.add(listFsTh.get(k));
                                List<FsFolder> listFsForth = fsFolderMapper.getChildByParentId(listFsTh.get(k).getFolderId());
                                if (listFsForth != null && listFsForth.size() > 0) {
                                    for (int n = 0; n < listFsForth.size(); n++) {
                                        ss++;
                                        String levelCodeForth = levelCodeTh;
                                        int newN = n + 1;
                                        if (newN < 10) {
                                            levelCodeForth = levelCodeForth + "000" + newN;
                                        } else if (newN >= 10 && newN < 100) {
                                            levelCodeForth = levelCodeForth + "00" + newN;
                                        } else if (newN >= 100 && newN < 1000) {
                                            levelCodeForth = levelCodeForth + "0" + newN;
                                        } else {
                                            levelCodeForth = levelCodeForth + "" + newN;
                                        }
                                        listFsForth.get(n).setLevelCode(levelCodeForth);
                                        listFsForth.get(n).setShowOrder(ss);
                                        listFsFolder.add(listFsForth.get(n));
                                        List<FsFolder> listFsFirth = fsFolderMapper.getChildByParentId(listFsForth.get(n).getFolderId());
                                        if (listFsFirth != null && listFsFirth.size() > 0) {
                                            for (int m = 0; m < listFsFirth.size(); m++) {
                                                ss++;
                                                String levelCodeFirth = levelCodeForth;
                                                int newM = m + 1;
                                                if (newM < 10) {
                                                    levelCodeFirth = levelCodeFirth + "000" + newM;
                                                } else if (newM >= 10 && newM < 100) {
                                                    levelCodeFirth = levelCodeFirth + "00" + newM;
                                                } else if (newM >= 100 && newM < 1000) {
                                                    levelCodeFirth = levelCodeFirth + "0" + newM;
                                                } else {
                                                    levelCodeFirth = levelCodeFirth + "" + newM;
                                                }
                                                listFsFirth.get(m).setShowOrder(ss);
                                                listFsFirth.get(m).setLevelCode(levelCodeFirth);
                                                listFsFolder.add(listFsFirth.get(m));
                                                List<FsFolder> listFsSixth = fsFolderMapper.getChildByParentId(listFsFirth.get(m).getFolderId());
                                                if (listFsSixth != null && listFsSixth.size() > 0) {
                                                    for (int s = 0; s < listFsSixth.size(); s++) {
                                                        ss++;
                                                        String levelCodeSixth = levelCodeFirth;
                                                        int newS = s + 1;
                                                        if (newS < 10) {
                                                            levelCodeSixth = levelCodeSixth + "000" + newS;
                                                        } else if (newS >= 10 && newS < 100) {
                                                            levelCodeSixth = levelCodeSixth + "00" + newS;
                                                        } else if (newS >= 100 && newS < 1000) {
                                                            levelCodeSixth = levelCodeSixth + "0" + newS;
                                                        } else {
                                                            levelCodeSixth = levelCodeSixth + "" + newS;
                                                        }
                                                        listFsSixth.get(s).setShowOrder(ss);
                                                        listFsSixth.get(s).setLevelCode(levelCodeSixth);
                                                        listFsFolder.add(listFsSixth.get(s));
                                                        List<FsFolder> listFsSeventh = fsFolderMapper.getChildByParentId(listFsSixth.get(s).getFolderId());
                                                        if (listFsSeventh != null && listFsSeventh.size() > 0) {
                                                            for (int d = 0; d < listFsSeventh.size(); d++) {
                                                                ss++;
                                                                String levelCodeSeventh = levelCodeSixth;
                                                                int newD = d + 1;
                                                                if (newD < 10) {
                                                                    levelCodeSeventh = levelCodeSeventh + "000" + newD;
                                                                } else if (newD >= 10 && newD < 100) {
                                                                    levelCodeSeventh = levelCodeSeventh + "00" + newD;
                                                                } else if (newD >= 100 && newD < 1000) {
                                                                    levelCodeSeventh = levelCodeSeventh + "0" + newD;
                                                                } else {
                                                                    levelCodeSeventh = levelCodeSeventh + "" + newD;
                                                                }
                                                                listFsSeventh.get(d).setShowOrder(ss);
                                                                listFsSeventh.get(d).setLevelCode(levelCodeSeventh);
                                                                listFsFolder.add(listFsSeventh.get(d));
                                                                List<FsFolder> listFsEighth = fsFolderMapper.getChildByParentId(listFsSeventh.get(d).getFolderId());
                                                                if (listFsEighth != null && listFsEighth.size() > 0) {
                                                                    for (int f = 0; f < listFsEighth.size(); f++) {
                                                                        ss++;
                                                                        String levelCodeEighth = levelCodeSeventh;
                                                                        int newF = f + 1;
                                                                        if (newF < 10) {
                                                                            levelCodeEighth = levelCodeEighth + "000" + newF;
                                                                        } else if (newF >= 10 && newF < 100) {
                                                                            levelCodeEighth = levelCodeEighth + "00" + newF;
                                                                        } else if (newF >= 100 && newF < 1000) {
                                                                            levelCodeEighth = levelCodeEighth + "0" + newF;
                                                                        } else {
                                                                            levelCodeEighth = levelCodeEighth + "" + newF;
                                                                        }
                                                                        listFsEighth.get(f).setShowOrder(ss);
                                                                        listFsEighth.get(f).setLevelCode(levelCodeEighth);
                                                                        listFsFolder.add(listFsEighth.get(f));
                                                                        List<FsFolder> listFsNinth = fsFolderMapper.getChildByParentId(listFsEighth.get(f).getFolderId());
                                                                        if (listFsNinth != null && listFsNinth.size() > 0) {
                                                                            for (int g = 0; g < listFsNinth.size(); g++) {
                                                                                ss++;
                                                                                String levelCodeNinth = levelCodeEighth;
                                                                                int newG = g + 1;
                                                                                if (newG < 10) {
                                                                                    levelCodeNinth = levelCodeNinth + "000" + newG;
                                                                                } else if (newG >= 10 && newG < 100) {
                                                                                    levelCodeNinth = levelCodeNinth + "00" + newG;
                                                                                } else if (newG >= 100 && newG < 1000) {
                                                                                    levelCodeNinth = levelCodeNinth + "0" + newG;
                                                                                } else {
                                                                                    levelCodeNinth = levelCodeNinth + "" + newG;
                                                                                }
                                                                                listFsNinth.get(g).setShowOrder(ss);
                                                                                listFsNinth.get(g).setLevelCode(levelCodeNinth);
                                                                                listFsFolder.add(listFsNinth.get(g));
                                                                                List<FsFolder> listFsTenth = fsFolderMapper.getChildByParentId(listFsNinth.get(g).getFolderId());
                                                                                if (listFsTenth != null && listFsTenth.size() > 0) {
                                                                                    for (int x = 0; x < listFsTenth.size(); x++) {
                                                                                        ss++;
                                                                                        String levelCodeTenth = levelCodeNinth;
                                                                                        int newX = x + 1;
                                                                                        if (newX < 10) {
                                                                                            levelCodeTenth = levelCodeTenth + "000" + newX;
                                                                                        } else if (newX >= 10 && newX < 100) {
                                                                                            levelCodeTenth = levelCodeTenth + "00" + newX;
                                                                                        } else if (newX >= 100 && newX < 1000) {
                                                                                            levelCodeTenth = levelCodeTenth + "0" + newX;
                                                                                        } else {
                                                                                            levelCodeTenth = levelCodeTenth + "" + newX;
                                                                                        }
                                                                                        listFsTenth.get(x).setShowOrder(ss);
                                                                                        listFsTenth.get(x).setLevelCode(levelCodeTenth);
                                                                                        listFsFolder.add(listFsTenth.get(x));
                                                                                        List<FsFolder> listFsEleventh = fsFolderMapper.getChildByParentId(listFsTenth.get(x).getFolderId());
                                                                                        if (listFsEleventh != null && listFsEleventh.size() > 0) {
                                                                                            for (int y = 0; y < listFsEleventh.size(); y++) {
                                                                                                ss++;
                                                                                                String levelCodeEleventh = levelCodeTenth;
                                                                                                int newY = y + 1;
                                                                                                if (newY < 10) {
                                                                                                    levelCodeEleventh = levelCodeEleventh + "000" + newY;
                                                                                                } else if (newY >= 10 && newY < 100) {
                                                                                                    levelCodeEleventh = levelCodeEleventh + "00" + newY;
                                                                                                } else if (newY >= 100 && newY < 1000) {
                                                                                                    levelCodeEleventh = levelCodeEleventh + "0" + newY;
                                                                                                } else {
                                                                                                    levelCodeEleventh = levelCodeEleventh + "" + newY;
                                                                                                }
                                                                                                listFsEleventh.get(y).setShowOrder(ss);
                                                                                                listFsEleventh.get(y).setLevelCode(levelCodeEleventh);
                                                                                                listFsFolder.add(listFsEleventh.get(y));
                                                                                                List<FsFolder> listFsTwelfth = fsFolderMapper.getChildByParentId(listFsEleventh.get(y).getFolderId());
                                                                                                if (listFsTwelfth != null && listFsTwelfth.size() > 0) {
                                                                                                    for (int z = 0; z < listFsTwelfth.size(); z++) {
                                                                                                        ss++;
                                                                                                        String levelCodeTwelfth = levelCodeEleventh;
                                                                                                        int newZ = z + 1;
                                                                                                        if (newZ < 10) {
                                                                                                            levelCodeTwelfth = levelCodeTwelfth + "000" + newZ;
                                                                                                        } else if (newZ >= 10 && newZ < 100) {
                                                                                                            levelCodeTwelfth = levelCodeTwelfth + "00" + newZ;
                                                                                                        } else if (newZ >= 100 && newZ < 1000) {
                                                                                                            levelCodeTwelfth = levelCodeTwelfth + "0" + newZ;
                                                                                                        } else {
                                                                                                            levelCodeTwelfth = levelCodeTwelfth + "" + newZ;
                                                                                                        }
                                                                                                        listFsTwelfth.get(z).setShowOrder(ss);
                                                                                                        listFsTwelfth.get(z).setLevelCode(levelCodeTwelfth);
                                                                                                        listFsFolder.add(listFsTwelfth.get(z));
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //存到数组中执行更新操作
        updateBatchById(listFsFolder);
    }

    /**
     * 查询子级层级码（包括自己）
     *
     * @param rootId 根节点ID
     * @return void
     */
    @Override
    public int getChildCodeCount(String rootId) {
        String levelCode = fsFolderMapper.selectById(rootId).getLevelCode();
        return fsFolderMapper.getChildCodeCount(levelCode);
    }

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    public List getTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //人员Id
        String userId = ShiroKit.getUser().getId();
        //所属群组id
        List<String> listGroup = docGroupService.getPremission(userId);

        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);

        String levelCodeString = businessService.getLevelCodeByUser(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList = getChildList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountList(secondList, listGroup, userId, adminFlag, type, levelCodeString);
            List<Map> childResultList = checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                String createUserId = fsFolder.getCreateUserId();
                SysUsers users = sysUsersService.getById(createUserId);
                String authorName = "";
                if(users == null){
                    authorName = createUserId;
                }else{
                    authorName = users.getUserName();
                }
                parentMap.put("authorName", authorName);
                parentMap.put("createTime", fsFolder.getCreateTime());
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            //非首次访问， 获取点击节点的下级
            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            resultList = checkChildCount(list, childCountList);
        }
        return resultList;
    }

    public List getTreeDataLazyMobile(String id, String type,List folderIds) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //人员Id
        String userId = ShiroKit.getUser().getId();
        //所属群组id
        List<String> listGroup = docGroupService.getPremission(userId);

        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);

        String levelCodeString = businessService.getLevelCodeByUser(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            List<FsFolder> list = getTreeDataLazyMobile(fsFolderParams,folderIds);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList = getChildListMobile(firstList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountListMobile(secondList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            List<Map> childResultList = checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                String createUserId = fsFolder.getCreateUserId();
                SysUsers users = sysUsersService.getById(createUserId);
                String authorName = "";
                if(users == null){
                    authorName = createUserId;
                }else{
                    authorName = users.getUserName();
                }
                parentMap.put("authorName", authorName);
                parentMap.put("createTime", fsFolder.getCreateTime());
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            //非首次访问， 获取点击节点的下级
            List<FsFolder> list = getTreeDataLazyMobile(fsFolderParams,folderIds);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountListMobile(firstList, listGroup, userId, adminFlag, type, levelCodeString,folderIds);
            resultList = checkChildCount(list, childCountList);
        }
        return resultList;
    }

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    public List getTreeDataLazyClient(String id, String type, String userId, String orgId, List<String> listGroup, List<String> roleList) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        //人员Id
        //所属群组id

        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);

        String levelCodeString = businessService.getLevelCodeByUserClient(fsFolderParams, orgId);
        fsFolderParams.setLevelCodeString(levelCodeString);

        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id) || "".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList = getChildList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountList(secondList, listGroup, userId, adminFlag, type, levelCodeString);
            List<Map> childResultList = checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            //非首次访问， 获取点击节点的下级
            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountList(firstList, listGroup, userId, adminFlag, type, levelCodeString);
            resultList = checkChildCount(list, childCountList);
        }
        return resultList;
    }

    /**
     * @return java.util.List<java.util.Map>
     * @Author zoufeng
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     **/
    public List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList) {
        String userId = ShiroKit.getUser().getId();
        List<Map> resultList = new ArrayList<>();
        List<String> listGroup = docGroupService.getPremission(userId);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        //String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        for (int j = 0; j < list.size(); j++) {
            FsFolder fsFolderChild = list.get(j);
            Map childMap = new HashMap();

            childMap.put("id", fsFolderChild.getFolderId());
            childMap.put("text", fsFolderChild.getFolderName());
            childMap.put("pid", fsFolderChild.getParentFolderId());
            childMap.put("path", fsFolderChild.getFolderPath());
            childMap.put("isEdit", fsFolderChild.getIsEdit());
            String createUserId = fsFolderChild.getCreateUserId();
            SysUsers users = sysUsersService.getById(createUserId);
            String authorName = "";
            if(users == null){
                authorName = createUserId;
            }else{
                authorName = users.getUserName();
            }
            childMap.put("authorName", authorName);
            childMap.put("createTime", fsFolderChild.getCreateTime());
            for (int i = 0; i < childCountList.size(); i++) {

                Map map = childCountList.get(i);
                if (fsFolderChild.getFolderId().equals(map.get("id"))) {

                    if (Integer.valueOf(map.get("num").toString()) > 0) {
                        childMap.put("children", true);
                    } else {
                        childMap.put("children", false);

                    }

                }

            }
            if (adminFlag == 1){
                childMap.put("result", true);
            }else {
                int isEdits = docFoldAuthorityService.findEditClient(fsFolderChild.getFolderId(), listGroup, userId, deptId);
                if (isEdits != 2) {
                    childMap.put("result", false);
                } else {
                    childMap.put("result", true);
                }
            }
            resultList.add(childMap);
        }
        return resultList;
    }

    /**
     * @return java.lang.String
     * @Author zoufeng
     * @Description 获取层级码
     * @Date 10:24 2018/9/10
     * @Param [parentCode, parentId]父节点层级码，父节点id
     **/
    public synchronized String getCurrentLevelCode(String parentCode, String parentId) {
        if (parentCode != null) {
            String currentMaxCode = getCurrentMaxLevelCode(parentId);
            if (currentMaxCode != null && !"".equals(currentMaxCode)) {
                currentMaxCode = currentMaxCode.substring(currentMaxCode.length() - 4, currentMaxCode.length());
                Integer currCodeInt = Integer.parseInt(currentMaxCode) + 1;
                if (currCodeInt < 10) {
                    return parentCode + "000" + currCodeInt;
                } else if (currCodeInt >= 10 && currCodeInt < 100) {
                    return parentCode + "00" + currCodeInt;
                } else if (currCodeInt >= 100 && currCodeInt < 1000) {
                    return parentCode + "0" + currCodeInt;
                } else {
                    return parentCode + "" + currCodeInt;
                }

            } else {
                return parentCode + "" + "0001";
            }
        }
        return parentCode;
    }

    /**
     * @return 目录树信息
     * @title: 查询下级目录
     * @description: 查询下级节点（目录）
     * @date: 2018-8-15
     * @author: yjs
     */
    public Object getChildren(FsFolderParams fsFolderParams, String nameFlag) {

        boolean isChild = isChildren(fsFolderParams.getId());
        FsFolder fsFolder = getById(fsFolderParams.getId());

        if (fsFolderParams.getPageNumber() == 0) {
            fsFolderParams.setPageNumber(1);
        }
        if (fsFolderParams.getPageSize() == 0) {
            fsFolderParams.setPageSize(300);
        }
        Map<String, String> orderMap = new HashMap<>();
        //排序和查询规则
        orderMap.put("0", "create_time");
        orderMap.put("1", "folder_name");

        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        String orderResult = (String) orderMap.get(fsFolderParams.getOrder());

        Map<String, Object> result = new HashMap<>(5);
        List<FsFolder> list = new ArrayList<>();
        int num = 0;
        List<String> roleList = ShiroKit.getUser().getRolesList();

        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setPageNumber((fsFolderParams.getPageNumber() - 1) * fsFolderParams.getPageSize());
        fsFolderParams.setOrderResult(orderResult);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setAdminFlag(adminFlag);

        if (nameFlag == null || "".equals(nameFlag)) {
            fsFolderParams.setName(null);
            //获得下一级目录
            list = getChildren(fsFolderParams);
            //获得下一级目录数量
            num = getNum(fsFolderParams);
        } else {
            list = getChildren(fsFolderParams);
            num = getNum(fsFolderParams);
        }
        //判断是否有可编辑子目录权限
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditNew(fsFolderParams.getId(), listGroup, userId);
            result.put("noChildPower", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 1);
        }
        result.put("userId", ShiroKit.getUser().getName());
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        result.put("isChild", isChild);
        return result;
    }

    /**
     * @return
     * @Author zoufeng
     * @Description 判断选中目录是否有编辑权限
     * @Date 14:44 2018/10/16
     * @Param
     **/
    public boolean getIsEditClient(String chooseFolderId, String userId, String orgId, List<String> listGroup, List<String> roleList) {

        boolean res = false;
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String[] chooseFolderIds = chooseFolderId.split(",");
        for (int i = 0; i < chooseFolderIds.length; i++) {
            FsFolder fsFolder = getById(chooseFolderIds[i]);
            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEditNewClient(chooseFolderIds[i], listGroup, userId, orgId);
                if (isEdits > 0) {
                    res = true;
                } else {
                    if (userId.equals(fsFolder.getCreateUserId())) {
                        res = true;
                    } else {
                        return false;
                    }
                }
            } else {
                res = true;
            }
            if (userId.equals(fsFolder.getCreateUserId())) {
                res = true;
            }
        }
        return res;
    }

    /**
     * @return
     * @Author zoufeng
     * @Description 判断选中目录是否有编辑权限
     * @Date 14:44 2018/10/16
     * @Param
     **/
    public boolean getIsEdit(String chooseFolderId) {

        boolean res = false;
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String[] chooseFolderIds = chooseFolderId.split(",");
        for (int i = 0; i < chooseFolderIds.length; i++) {
            FsFolder fsFolder = getById(chooseFolderIds[i]);
            if (adminFlag != 1) {
                int isEdits = docFoldAuthorityService.findEditNew(chooseFolderIds[i], listGroup, userId);
                if (isEdits > 0) {
                    res = true;
                } else {
                    if (userId.equals(fsFolder.getCreateUserId())) {
                        res = true;
                    } else {
                        return false;
                    }
                }
            } else {
                res = true;
            }
            if (userId.equals(fsFolder.getCreateUserId())) {
                res = true;
            }
        }
        return res;
    }

    @Override
    public List<FsFolderView> getPersonUpload(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order) {
        List<FsFolderView> list = null;
        list = fsFolderMapper.getPersonUpload(userId, pageNumber, pageSize, name, typeArr, order, using);
//        list = changeFolderLocation(list);
        return list;
    }

    @Override
    public List<FsFolderView> getPersonUploadMobile(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order,List folderIds) {
        List<FsFolderView> list = null;
        list = fsFolderMapper.getPersonUploadMobile(userId, pageNumber, pageSize, name, typeArr, order, using,folderIds);
//        list = changeFolderLocation(list);
        return list;
    }

    @Override
    public List<FsFolderView> getPersonUploadClient(String userId, Integer pageNumber, Integer pageSize, String name, String[] typeArr, String order, String timeType) {
        List<FsFolderView> list = null;
        list = fsFolderMapper.getPersonUploadClient(userId, pageNumber, pageSize, name, typeArr, order, timeType);
//        list = changeFolderLocation(list);
        return list;
    }

    @Override
    public int getPersonUploadNum(String userId, String name) {
        int num = fsFolderMapper.getPersonUploadNum(userId, name, using);
        return num;
    }

    @Override
    public int getPersonUploadNumMobile(String userId, String name,List folderIds) {
        int num = fsFolderMapper.getPersonUploadNumMobile(userId, name, using,folderIds);
        return num;
    }

    @Override
    public int getPersonUploadNumClient(String userId, String name, String timeType) {
        int num = fsFolderMapper.getPersonUploadNumClient(userId, name, timeType);
        return num;
    }

    /**
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeFolderLocation(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFolderLocal() != null && !"".equals(fsFolderView.getFolderLocal())) {
                String localName = "";
                for (int i = 1; i <= fsFolderView.getFolderLocal().length() / 4; i++) {
                    String levelCode = fsFolderView.getFolderLocal().substring(0, i * 4);
                    String folderName = fsFolderMapper.getFolderNameByLevelCode(levelCode);
                    localName = localName + "/" + folderName;
                }
                localName = localName.substring(1, localName.length());
                fsFolderView.setFolderLocal(localName);
            }
        }
        return list;
    }

    public String getFolderNameByLevelCode(String levelCode) {
        return fsFolderMapper.getFolderNameByLevelCode(levelCode);
    }

    @Override
    public List<FsFolder> getFolderByLevelCodeStringFirst(String levelCodeString,Integer adminFlag) {
        if(adminFlag == 1){
            return fsFolderMapper.getFolderByLevelCodeStringFirstBySuper();
        }else{
        return fsFolderMapper.getFolderByLevelCodeStringFirst(levelCodeString);
        }
    }
    @Override
    public List<FsFolder> getFolderByLevelCodeStringFirstByFolderId(String levelCodeString,Integer adminFlag,String folderId) {
        if(adminFlag == 1){
            return fsFolderMapper.getFolderByLevelCodeStringFirstByFolderIdBySuper(folderId);
        }else{
            return fsFolderMapper.getFolderByLevelCodeStringFirstFolderId(levelCodeString,folderId);
        }
    }

    @Override
    public List<FsFolder> getFolderByLevelCodeStringSecond(String levelCodeString,Integer adminFlag) {
        if(adminFlag == 1){
            return fsFolderMapper.getFolderByLevelCodeStringSecondBySuper();
        }else{
            return fsFolderMapper.getFolderByLevelCodeStringSecond(levelCodeString);
        }
    }

    @Override
    public List<FsFolder> getFolderByLevelCodeStringThird(String levelCodeString,Integer adminFlag) {
        if(adminFlag == 1){
            return fsFolderMapper.getFolderByLevelCodeStringThirdBySuper();
        }else{
            return fsFolderMapper.getFolderByLevelCodeStringThird(levelCodeString);
        }
    }
    @Override
    public List<FsFolderView> getFilesAndFloderByFolderShare(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                                             String orderResult, List groupList, String userId, Integer adminFlag,
                                                             String operateType,String levelCodeString,String  levelCode,String isDesc,String orgId) {
        List<FsFolderView> list = null;
        list = fsFolderMapper.getFilesAndFloderBySuperAdminByFolderShare(pageNumber, pageSize, id, name, orderResult, typeArr,isDesc);

        return list;
    }

    /**
     * 获取下级文件及目录的数量
     *
     * @param id          节点id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    @Override
    public int getFilesAndFloderNumByFolderShare(String id, String[] typeArr, String name, String orderResult, List groupList,
                                                 String userId, Integer adminFlag, String operateType,String levelCodeString,
                                                 String  levelCode,String orgId) {
        int num = 0;
        num = fsFolderMapper.getFilesAndFloderNumBySuperAdminByFolderShare(id, name, orderResult, typeArr);


        return num;
    }

    /**
     * 获取子文件的数量
     *
     * @param id        节点id
     * @param typeArr   文件类型
     * @param name      文件名称
     * @param groupList 群组id
     * @param userId    用户id
     * @param adminFlag 管理员标识
     * @param type      前后台标识
     * @return 文件的数量
     */
    public int getFileNumByFolderShare(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag,
                                       String type,String levelCodeString,String orgId) {
        int num = 0;
        num = fsFolderMapper.getFileNumBySuperAdminByFolderShare(id, name, typeArr);


        return num;
    }

    @Override
    public String getPersonPic(String name) {
        return fsFolderMapper.getPersonPic(name);
    }
    @Override
    public String getPersonPath(String name) {
        return fsFolderMapper.getPersonPath(name);
    }
    @Override
    public List getDeptList(int pageNumber, int pageSize,String visibleRange) {
        List<Map<String,Object>> listFsFolder = fsFolderMapper.getDeptList(pageNumber, pageSize,visibleRange);
        return listFsFolder;
    }

    @Override
    public int updateDeptVisibleRange(List<String> organId,String visibleRange) {

        return fsFolderMapper.updateDeptVisibleRange(organId,visibleRange);
    }

    /**
     * 判断目录下是否存在待审核文件
     *
     * @param fsFolderId 目录ID
     * @return 是否
     */
    @Override
    public String checkAuditDoc(String fsFolderId) {
        return baseMapper.checkAuditDoc(fsFolderId);
    }

    @Override
    public boolean copyDire(String folderIds, String pId, String isCopyAuth) {
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        String[] ids = folderIds.split(",");
        for (int k = 0; k < ids.length; k++) {
            String folderId = ids[k];
            // 获取子目录
            FsFolder fo = fsFolderMapper.selectById(folderId);
            if(fo == null){
                return true;
            }
            List<String> folderList = fsFolderMapper.getFsFolderBylevelOrder(fo.getLevelCode());

            List<String> resultId = new ArrayList<>();
            List<DocFoldAuthority> dfaList = new ArrayList<>();

            if(folderList!= null ){
                for(int i=0;i<folderList.size();i++){
                    String fsId = UUID.randomUUID().toString().replaceAll("-", "");
                    resultId.add(fsId);

                    if(StringUtils.equals("$",folderList.get(i))){
                        continue;
                    }
                    FsFolder fsFolder = fsFolderMapper.selectById(folderList.get(i));
                    if(fsFolder == null){
                        continue;
                    }
                    fsFolder.setFolderId(fsId);
                    if(StringUtils.equals(folderId,folderList.get(i))){
                        fsFolder.setParentFolderId(pId);
                        fsFolder.setFolderName(newName(fsFolder));
                    }
                    String parentFolderId = fsFolder.getParentFolderId();
                    if(!StringUtils.equals(folderId,folderList.get(i)) && StringUtils.isNotEmpty(parentFolderId)){
                        int dex = getIndex(parentFolderId,folderList);
                        fsFolder.setParentFolderId(resultId.get(dex));

                    }
                    if(StringUtils.isNotEmpty(fsFolder.getParentFolderId())){
                        FsFolder parentFolder = fsFolderMapper.selectById(fsFolder.getParentFolderId());
                        String parentCode = parentFolder.getLevelCode();
                        String currentCode = getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                        fsFolder.setLevelCode(currentCode);
                        String localName = "";
                        for (int j = 1; j <= currentCode.length() / 4-1; j++) {
                            String levelCodeString = currentCode.substring(0, j * 4);
                            String folderName = getFolderNameByLevelCode(levelCodeString);
                            localName = localName + "\\" + folderName;
                        }
                        localName=localName+"\\"+fsFolder.getFolderName();
                        fsFolder.setFolderPath(localName);

                    }
                    //生成showOrder
                    String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
                    int num = Integer.parseInt(currentCode);
                    fsFolder.setShowOrder(num);
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    fsFolder.setCreateTime(ts);
                    fsFolder.setCreateUserId(ShiroKit.getUser().getId());
                    fsFolder.setUpdateUserId(null);
                    fsFolder.setUpdateTime(null);
                    //resutAddList.add(fsFolder);
                    save(fsFolder);
                    if ("true".equals(isCopyAuth)) {
                        // 权限信息
                        List<DocFoldAuthority> authoritys = docFoldAuthorityService.list(new QueryWrapper<DocFoldAuthority>().eq("FOLDER_ID", folderList.get(i)));
                        for(DocFoldAuthority item:authoritys){
                            DocFoldAuthority dfa = new DocFoldAuthority();
                            String dfaid = UUID.randomUUID().toString().replaceAll("-", "");
                            dfa.setId(dfaid);
                            dfa.setFoldId(fsId);
                            dfa.setAuthorId(item.getAuthorId());
                            dfa.setAuthorType(item.getAuthorType());
                            dfa.setIsEdit(item.getIsEdit());
                            dfa.setOrganId(item.getOrganId());
                            dfa.setOperateType(item.getOperateType());
                            dfaList.add(dfa);
                        }
                    }
                }
            }
            if(dfaList.size()>0){
                docFoldAuthorityService.saveBatch(dfaList);
            }
        }
        return true;
    }

    public int getIndex(String name,List<String> nameList){
        int index = -1;
        for(int y=0;y<nameList.size();y++){
            if(StringUtils.equals(name,nameList.get(y))){
                index = y;
            }
        }
        return  index;
    }

    public String newName(FsFolder fsFolder){
        boolean lock = true;
        String name = fsFolder.getFolderName();
        while (lock) {
            if(StringUtils.isEmpty(fsFolder.getParentFolderId()) || StringUtils.isEmpty(fsFolder.getFolderName())){
                lock = false;
                fsFolder.setFolderName(fsFolder.getFolderName()+"-副本");
                break;
            } else {
                List<FsFolder> list = fsFolderMapper.addCheck(fsFolder.getParentFolderId(), name, null);
                if(list.size() > 0 ){
                    if(StringUtils.equals(name,fsFolder.getFolderName())){
                        name = name + "2";
                    } else {
                        String last = name.replaceAll(fsFolder.getFolderName(),"");
                        if(isNumeric0(last)){
                            Integer suff = Integer.parseInt(last) + 1;
                            String suffStr = suff.toString();
                            name = fsFolder.getFolderName() + suffStr;
                        }else {
                            name = name + "2";
                        }
                    }
                } else {
                    lock = false;
                    break;
                }
            }
        }
        return name;
    }

    public static boolean isNumeric0(String str) {
        for(int i=str.length();--i>=0;) {
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
                return false;
        }
        return true;
    }

    @Override
    public List<Map> findFolderExtranetAuthTree() {
        List<Map> folds = fsFolderMapper.selectAllFold();
        if (folds != null && folds.size() > 0) {
            for (Map fold : folds) {
                if (fold.containsKey("fuFolderId")) {

                    JSONObject state = new JSONObject();
                    state.put("selected", true);
                    state.put("checked", true);
                    state.put("opened", true);
                    fold.put("state", state);
                }

            }
        }
        return folds;
    }

    @Override
    public List<Map> findFolderTree(List<String> folderIds, String folderId) {
        List<Map> folds = fsFolderMapper.selectAllFolder(folderIds, folderId);
        return folds;
    }
   /* @Override
    public List<Map> findFolderExtranetAuthTree() {
        List<Map> resultList = new ArrayList<Map>();

        // 顶级目录
        List<FsFolder> folders = fsFolderMapper.getRoot();
        FsFolder folder = folders.get(0);
        Map foldMap = new HashMap();
        foldMap.put("id", folder.getFolderId());
        foldMap.put("text", folder.getFolderName());
        foldMap.put("parent", "#");
        foldMap.put("levelCode", folder.getLevelCode());
        foldMap.put("folderPath", folder.getFolderPath());
        foldMap.put("children", true);

        resultList.add(foldMap);
        getFsFolderChildren(resultList, folder.getFolderId());
        return resultList;
    }*/

    @Override
    public List<Map> findFolderExtranetAuthTreeLazy(String id) {

        List<Map> list = new ArrayList<Map>();
        if ("#".equals(id) || "".equals(id)) {
            //首次访问
            List<FsFolder> folders = fsFolderMapper.getRoot();
            FsFolder folder = folders.get(0);
            Map foldMap = new HashMap();
            foldMap.put("id", folder.getFolderId());
            foldMap.put("text", folder.getFolderName());
            foldMap.put("parent", "#");
            foldMap.put("levelCode", folder.getLevelCode());
            foldMap.put("folderPath", folder.getFolderPath());
            foldMap.put("children", true);
            //  添加顶级目录
            list.add(foldMap);
        }else{
            list = fsFolderMapper.getFolderTreeByParentIdLazy(id);
            if(list!=null && list.size()>0){
                for(Map map : list){
                    if(map.containsKey("children")){
                        if(StringUtils.equals(String.valueOf(map.get("children")), "true")){
                            map.put("children",  true);
                        }else{
                            map.put("children",  false);
                        }
                    }
                }
            }
        }


        return list;
    }

    @Override
    public List<String> getFsFolderBylevelOrder(String id) {
        return fsFolderMapper.getFsFolderBylevelOrder(id);
    }


    public void getFsFolderChildren(List<Map> resultList, String parentId) {
        List<Map> list = fsFolderMapper.getFolderTreeByParentIdLazy(parentId);
        if (list.size() > 0) {
            for (Map folder : list) {
                resultList.add(folder);
                getFsFolderChildren(resultList, String.valueOf(folder.get("id")));
            }
        }

    }
}
