package com.jxdinfo.doc.front.docmanager.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.doc.front.docmanager.dao.FrontFsFileMapper;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.foldermanager.dao.FrontFolderMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类的用途：获取首页文件目录
 * 创建日期：
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
@Service
public class FrontFsFileServiceImpl implements FrontFsFileService {

    /**  文件mappper */
    @Resource
    private FrontFsFileMapper frontFsFileMapper;
    /**  文件mappper */
    @Resource
    private FrontDocInfoMapper frontDocInfoMapper;

    /** 前台文档mapper */
    @Resource
    private FrontFolderMapper frontFolderMapper;

    /** 目录管理工具类 */
    @Resource
    private BusinessService businessService;

    @Resource
    private  DocGroupService docGroupService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 获取首页文件目录
     *
     * @param userId    用户名
     * @param groupList 群组集合
     * @param adminFlag 权限标志（1为超管和文库管理员，2为部门负责人管理员，3普通用户）
     * @return List<FsFolder> 文件夹集合
     */
    @Override
    public List<FsFolder> getFsFileList(String userId, List groupList, Integer adminFlag) {

        FsFolderParams fsFolderParams = new FsFolderParams();

        if (adminFlag == 1) {
            //查询根节点ID
            FsFolder rootFloder = frontFolderMapper.getRoot().get(0);
            //超级管理员获取目录文件
            return frontFsFileMapper.getFsFolderListBySuperAdmin(rootFloder.getFolderId());
        } else {
            //其他管理员获取目录权限
//            List<String> levelCodeList = businessService.getlevelCodeList(groupList, userId, "0");
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("front");

            //加载首页金现代层级码001
            fsFolderParams.setLevelCodeString("0001");
            String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("2");
            List list = new ArrayList<>();
            //查询根节点ID
            FsFolder rootFloder = frontFolderMapper.getRoot().get(0);
            list.add(rootFloder.getFolderId());
            //通过层级码和用户进行查询目录
            return frontFolderMapper.selectByLevelCodeList(list, userId, levelCodeString);
        }
    }
    /**
     * 获取首页文件目录
     * 手机端方法
     * @param userId    用户名
     * @param groupList 群组集合
     * @param adminFlag 权限标志（1为超管和文库管理员，2为部门负责人管理员，3普通用户）
     * @return List<FsFolder> 文件夹集合
     */
    @Override
    public List<FsFolder> getFsFileListMobile(String userId, List groupList, Integer adminFlag) {

        FsFolderParams fsFolderParams = new FsFolderParams();

        if (adminFlag == 1) {
            //查询根节点ID
            FsFolder rootFloder = frontFolderMapper.getRoot().get(0);
            //超级管理员获取目录文件
            return frontFsFileMapper.getFsFolderListBySuperAdmin(rootFloder.getFolderId());
        } else {
            //其他管理员获取目录权限
//            List<String> levelCodeList = businessService.getlevelCodeList(groupList, userId, "0");
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("0");
            //加载首页金现代层级码001
            fsFolderParams.setLevelCodeString("0001");
            String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
            List list = new ArrayList<>();
            //查询根节点ID
            FsFolder rootFloder = frontFolderMapper.getRoot().get(0);
            list.add(rootFloder.getFolderId());
            //通过层级码和用户进行查询目录
            return frontFolderMapper.selectByLevelCodeList(list, userId, levelCodeString);
        }
    }
    /**
     * 获取根节点
     */
    public List<FsFolder> getRoot() {
        List<FsFolder> list = frontFolderMapper.getRoot();
        return list;
    }

    /**
     * 获取下载次数等信息
     */
    public List<Map> getInfo(List ids,String userId ,List<String> listGroup, List roleList) {
        List<Map> list = frontFsFileMapper.getInfo(ids,userId,listGroup,roleList);
        return list;
    }

    @Override
    public boolean isChildren(String id) {
        int num = frontFsFileMapper.getNumByChildFloder(id);
        if (num == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<DocInfo> getList(Integer pageNumber, Integer pageSize) {
        return frontDocInfoMapper.getList(pageNumber,pageSize);

    }

    @Override
    public List<DocInfo> getListByAdmin(Integer pageSize) {
        return frontDocInfoMapper.getListByAdmin(pageSize);

    }

    @Override
    public List<DocInfo> getNewList(Integer pageNumber, Integer pageSize, List groupList, String userId, String orgId, String levelCodeString,List roleList) {
        return frontDocInfoMapper.getNewList(pageNumber,pageSize,groupList,userId,orgId,levelCodeString,roleList);

    }

    @Override
    public List<DocInfo> getListByFolderId(Integer pageNumber, Integer pageSize,String folderId) {
        return frontDocInfoMapper.getListByFolderId(pageNumber,pageSize,folderId);

    }

    @Override
    public List<DocInfo> getTopList(List groupList, String userId, String orgId, String levelCodeString,List roleList) {
        return frontDocInfoMapper.getTopList(groupList,userId,orgId,levelCodeString,roleList);
    }

    @Override
    public List<DocInfo> getTopListByAdmin() {
        return frontDocInfoMapper.getTopListByAdmin();
    }

    @Override
    public List<FsFolderView> getListByType(Integer pageNumber, Integer pageSize, String folderId) {
        return frontDocInfoMapper.getListByType(pageNumber,pageSize,folderId);
    }


    @Override
    public List<DocInfo> getListByPermission(Integer pageNumber, Integer pageSize) {
        FsFolderParams fsFolderParams = new FsFolderParams();
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("front");
        fsFolderParams.setLevelCodeString("0001");
        fsFolderParams.setId("2bb61cdb2b3c11e8aacf429ff4208431");
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        fsFolderParams.setRoleList(roleList);
        String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);

        //获得目录管理权限层级码

        fsFolderParams.setType("2");
//        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if(adminFlag==1){
            return frontDocInfoMapper.getListByPermissionSuper(pageNumber,pageSize);

        }else{
            String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
            return frontDocInfoMapper.getListByPermission(pageNumber,pageSize,levelCodeString,null,userId,listGroup,orgId,ShiroKit.getUser().getRolesList());
        }

    }
    @Override
    public List<Map> hotWord(Integer beginNum,Integer endNum) {
        return  frontDocInfoMapper.hotWord(beginNum,endNum);
    }
    @Override
    public List<Map> hotWordByLevelCode(Integer beginNum,Integer endNum,String levelCode) {
        return  frontDocInfoMapper.hotWordByLevelCode(beginNum,endNum,levelCode);
    }

    @Override
    public Integer hotWordCount() {
        return frontDocInfoMapper.hotWordCount();
    }

    @Override
    public int hotWordNum() {
        return frontDocInfoMapper.hotWordNum();
    }


}
