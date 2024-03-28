package com.jxdinfo.doc.client.foldermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.client.foldermanager.service.IClientFsFolderService;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 目录表 服务实现类
 * </p>
 *
 * @author zf
 * @since 2018-09-06
 */
@Service
public class FsClientFolderServiceImpl extends ServiceImpl<FsFolderMapper, FsFolder> implements IClientFsFolderService {

    @Resource
    private JWTUtil jwtUtil;
    @Autowired
    private ISysUsersService iSysUsersService;
    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 目录dao层
     */
    @Resource
    private FsFolderMapper fsFolderMapper;

    /**
     * 权限群组服务类
     */
    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;


    /**
     * 目录服务类
     */
    @Resource
    private BusinessService folderService;

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    @Override
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
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setType(type);

        String levelCodeString = businessService.getLevelCodeByUserClient(fsFolderParams, orgId);
        fsFolderParams.setLevelCodeString(levelCodeString);

        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id) || "".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setRoleList(roleList);
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
                parentMap.put("levelCode", fsFolder.getLevelCode());
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
            fsFolderParams.setRoleList(roleList);
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


    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams) {
        List<FsFolder> list = null;
        //获取目录结构
        //超级管理员：1 文库管理员：2
        if (fsFolderParams.getAdminFlag() == 1) {
            //获取节点的信息
            list = fsFolderMapper.getTreeDataLazyBySuper(fsFolderParams.getId());
        } else {
            list = fsFolderMapper.selectByLevelCode(fsFolderParams.getId(), fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString());
        }
        return list;
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

    /**
     * @return java.util.List<java.util.Map>
     * @Author zoufeng
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     **/
    public List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList) {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<Map> resultList = new ArrayList<>();
        List<String> listGroup = docGroupService.getPremission(userId);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        for (int j = 0; j < list.size(); j++) {
            FsFolder fsFolderChild = list.get(j);
            Map childMap = new HashMap();

            childMap.put("id", fsFolderChild.getFolderId());
            childMap.put("text", fsFolderChild.getFolderName());
            childMap.put("pid", fsFolderChild.getParentFolderId());
            childMap.put("path", fsFolderChild.getFolderPath());
            childMap.put("isEdit", fsFolderChild.getIsEdit());
            childMap.put("levelCode", fsFolderChild.getLevelCode());
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
                int isEdits = docFoldAuthorityService.findEditClient(fsFolderChild.getFolderId(), listGroup, userId, orgId);
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
}