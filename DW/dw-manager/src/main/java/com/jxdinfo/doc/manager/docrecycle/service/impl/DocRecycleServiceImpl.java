package com.jxdinfo.doc.manager.docrecycle.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docrecycle.dao.DocRecycleMapper;
import com.jxdinfo.doc.manager.docrecycle.model.DocRecycle;
import com.jxdinfo.doc.manager.docrecycle.service.IDocRecycleService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 回收站 服务实现类
 * </p>
 *
 * @author 
 * @since 2018-08-09
 */
@Service
public class DocRecycleServiceImpl extends ServiceImpl<DocRecycleMapper, DocRecycle> implements IDocRecycleService {
    @Resource
    private DocRecycleMapper docRecycleMapper;
    @Resource
    private DocInfoMapper docInfoMapper;
    @Resource
    private CacheToolService cacheToolService;
    @Resource
    private ESUtil esUtil;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Resource
    private DocGroupService docGroupService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Override
    public Map<String, Object> getDocRecycleList(String pageNum, String limitNum, String fileName) {
        HashMap<String, Object> map = new HashMap<String, Object>(4);
        if(ToolUtil.isNotEmpty(pageNum) && ToolUtil.isNotEmpty(limitNum)) {
            Page pages = new Page(Integer.valueOf(pageNum).intValue(), Integer.valueOf(limitNum).intValue());
            List<String> roleList = ShiroKit.getUser().getRolesList();
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String userId=null;
            if(adminFlag!=1){
                userId= ShiroKit.getUser().getId();
            }
            List<DocRecycle> list = this.docRecycleMapper.getDocRecycleList(pages, fileName,userId);
            for (DocRecycle docRecycle : list) {
                if (docRecycle.getFileSize() != null&&!"".equals(docRecycle.getFileSize())) {
                	docRecycle.setFileSize(FileTool.longToString(docRecycle.getFileSize()));
                }
            }
            map.put("count", Integer.valueOf((int)pages.getTotal()));
            map.put("data", list);
            map.put("code", 0);
            return map;
        } else {
            map.put("data", null);
            map.put("code", "500");
            map.put("msg", "参数不符");
            map.put("count", null);
            return map;
        }
    }

    @Override
    public Map<String, Object> getDocRecycleList(String pageNum, String limitNum, String fileName, String order) {
        HashMap<String, Object> map = new HashMap<String, Object>(4);
        if(ToolUtil.isNotEmpty(pageNum) && ToolUtil.isNotEmpty(limitNum)) {
            Page pages = new Page(Integer.valueOf(pageNum).intValue(), Integer.valueOf(limitNum).intValue());
            List<String> roleList = ShiroKit.getUser().getRolesList();
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String userId=null;
            String levelCodes = null;
            if(adminFlag!=1){
                userId= ShiroKit.getUser().getId();
                String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
                FsFolderParams fsFolderParams = new FsFolderParams();
                List<String> listGroup = docGroupService.getPremission(userId);
                fsFolderParams.setGroupList(listGroup);
                fsFolderParams.setUserId(userId);
                fsFolderParams.setType("1");
                fsFolderParams.setRoleList(roleList);
                String groupIds = String.join(",", fsFolderParams.getGroupList());
                String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
                levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
                if(levelCodes == null){
                    levelCodes = "";
                }
                if (levelCodes.indexOf(",") == 0) {
                    levelCodes = levelCodes.substring(1, levelCodes.length());
                    levelCodes = "('" + levelCodes.replace(",", "','") + "')";
                }else{
                    levelCodes = "('')";
                }
            }
            List<DocRecycle> list = this.docRecycleMapper.getDocRecycleOrderedList(pages, fileName,userId,order,levelCodes);

            for (DocRecycle docRecycle : list) {
                if (docRecycle.getFileSize() != null&&!"".equals(docRecycle.getFileSize())) {
                    docRecycle.setFileSize(FileTool.longToString(docRecycle.getFileSize()));
                }
            }
            map.put("count", (int)pages.getTotal());
            map.put("data", list);
            map.put("code", 0);
            return map;
        } else {
            map.put("data", null);
            map.put("code", "500");
            map.put("msg", "参数不符");
            map.put("count", null);
            return map;
        }
    }
    @Override
    public Map<String, Object> getDocRecycleListClient(String pageNum, String limitNum, String fileName, String order,String userId,String orgId) {
        HashMap<String, Object> map = new HashMap<String, Object>(4);
        if(ToolUtil.isNotEmpty(pageNum) && ToolUtil.isNotEmpty(limitNum)) {
            Page pages = new Page(Integer.valueOf(pageNum).intValue(), Integer.valueOf(limitNum).intValue());
            List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String levelCodes = null;
            if(adminFlag!=1){
                FsFolderParams fsFolderParams = new FsFolderParams();
                List<String> listGroup = docGroupService.getPremission(userId);
                fsFolderParams.setGroupList(listGroup);
                fsFolderParams.setUserId(userId);
                fsFolderParams.setRoleList(roleList);
                fsFolderParams.setType("1");
                String groupIds = String.join(",", fsFolderParams.getGroupList());
                String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
                levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
                if(levelCodes == null){
                    levelCodes = "";
                }
                if (levelCodes.indexOf(",") == 0) {
                    levelCodes = levelCodes.substring(1, levelCodes.length());
                    levelCodes = "('" + levelCodes.replace(",", "','") + "')";
                }else{
                    levelCodes = "('')";
                }
            }else {
                userId=null;
            }

            List<DocRecycle> list = this.docRecycleMapper.getDocRecycleOrderedList(pages, fileName,userId,order,levelCodes);

            for (DocRecycle docRecycle : list) {
                if (docRecycle.getFileSize() != null&&!"".equals(docRecycle.getFileSize())) {
                    docRecycle.setFileSize(FileTool.longToString(docRecycle.getFileSize()));
                }
            }
            map.put("count", Integer.valueOf((int)pages.getTotal()));
            map.put("data", list);
            return map;
        } else {

            return map;
        }
    }

    @Override
    public Map<String, Object> getDocRecycleListMobile(String pageNum, String limitNum, String fileName, String order,String userId,String orgId,List folderIds) {
        HashMap<String, Object> map = new HashMap<String, Object>(4);
        if(ToolUtil.isNotEmpty(pageNum) && ToolUtil.isNotEmpty(limitNum)) {
            Page pages = new Page(Integer.valueOf(pageNum).intValue(), Integer.valueOf(limitNum).intValue());
            List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String levelCodes = null;
            if(adminFlag!=1){
                FsFolderParams fsFolderParams = new FsFolderParams();
                List<String> listGroup = docGroupService.getPremission(userId);
                fsFolderParams.setGroupList(listGroup);
                fsFolderParams.setUserId(userId);
                fsFolderParams.setRoleList(roleList);
                fsFolderParams.setType("1");
                String groupIds = String.join(",", fsFolderParams.getGroupList());
                String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
                levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
                if(levelCodes == null){
                    levelCodes = "";
                }
                if (levelCodes.indexOf(",") == 0) {
                    levelCodes = levelCodes.substring(1, levelCodes.length());
                    levelCodes = "('" + levelCodes.replace(",", "','") + "')";
                }else{
                    levelCodes = "('')";
                }
            }else {
                userId=null;
            }

            List<DocRecycle> list = this.docRecycleMapper.getDocRecycleOrderedListMobile(pages, fileName,userId,order,levelCodes,folderIds);

            for (DocRecycle docRecycle : list) {
                if (docRecycle.getFileSize() != null&&!"".equals(docRecycle.getFileSize())) {
                    docRecycle.setFileSize(FileTool.longToString(docRecycle.getFileSize()));
                }
            }
            map.put("count", Integer.valueOf((int)pages.getTotal()));
            map.put("data", list);
            return map;
        } else {

            return map;
        }
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public boolean restore(String fileId, String folderId) {
        DocInfo docInfo=new DocInfo();
        //更新目录
        docInfo.setFoldId(folderId);
        //更新为有效状态
        docInfo.setValidFlag("1");
        String[] fileIds=fileId.split(",");
        //删除回收站文件
        docRecycleMapper.delete(new QueryWrapper<DocRecycle>().in("file_id",fileIds));
        //更新文档信息表，更改目录
        docInfoMapper.update(docInfo,new QueryWrapper<DocInfo>().in("file_id",fileIds));
        //更新索引
        for(String id:fileIds){
            DocInfo docInfoOld = docInfoMapper.selectById(id);
            Map map=new HashMap(1);
            if (docInfoOld!=null){
                map.put("title",docInfoOld.getTitle());
            }
            map.put("recycle","1");
            esUtil.updateIndex(id,map);
        }
        return true;
    }

    @Override
    public boolean clear() {
        String deleteUserId = null;
        String levelCodes = null;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if(adminFlag!=1){
            deleteUserId = ShiroKit.getUser().getId();
            String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
            FsFolderParams fsFolderParams = new FsFolderParams();
            List<String> listGroup = docGroupService.getPremission(deleteUserId);
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(deleteUserId);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setType("1");
            String groupIds = String.join(",", fsFolderParams.getGroupList());
            String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
            levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds,deleteUserId,"2",orgId,roleIds);
            if(levelCodes == null){
                levelCodes = "";
            }
            if (levelCodes.indexOf(",") == 0) {
                levelCodes = levelCodes.substring(1, levelCodes.length());
                levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            }else{
                levelCodes = "('')";
            }
        }
        List<DocRecycle> list = this.docRecycleMapper.getDocRecycleOrderedList(new Page(1,1000), null,deleteUserId,null,levelCodes);
        this.docRecycleMapper.updateDocRecycle(deleteUserId,levelCodes);
       // docRecycleMapper.update(docRecycle,new EntityWrapper<DocRecycle>());
      //  List<DocRecycle> list=docRecycleMapper.selectList(new EntityWrapper<DocRecycle>());
        for(DocRecycle doc:list){
            esUtil.deleteIndex(doc.getRecycleId());
        }
        return true;
    }

    @Override
    public boolean checkDocExist(String folderId, String fileName) {
        fileName= "'"+fileName.replace(",","','")+ "'";
        int num=docRecycleMapper.checkDocExist(folderId,fileName);
        if(num > 0){
            return true;
        }
        return false;
    }

    /**
     * 判断目录下是否存在同名待审核文件
     *
     * @param folderId 目录ID
     * @param fileName 文件名
     * @return
     */
    @Override
    public boolean checkAuditDocExist(String folderId, String fileName) {
        fileName= "'"+fileName.replace(",","','")+ "'";
        int num=docRecycleMapper.checkAuditDocExist(folderId,fileName);
        if(num > 0){
            return true;
        }
        return false;
    }

    @Override
    public boolean clearClient(String userId,String orgId) {
        String deleteUserId = null;
        String levelCodes = null;
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if(adminFlag!=1){
            deleteUserId = userId;
            FsFolderParams fsFolderParams = new FsFolderParams();
            List<String> listGroup = docGroupService.getPremission(deleteUserId);
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(deleteUserId);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setType("1");
            String groupIds = String.join(",", fsFolderParams.getGroupList());
            String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
            levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds,deleteUserId,"2",orgId,roleIds);
            if(levelCodes == null){
                levelCodes = "";
            }
            if (levelCodes.indexOf(",") == 0) {
                levelCodes = levelCodes.substring(1, levelCodes.length());
                levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            }else{
                levelCodes = "('')";
            }
        }else {

        }
        List<DocRecycle> list = this.docRecycleMapper.getDocRecycleOrderedList(new Page(1,1000), null,deleteUserId,null,levelCodes);
        this.docRecycleMapper.updateDocRecycle(deleteUserId,levelCodes);
        // docRecycleMapper.update(docRecycle,new EntityWrapper<DocRecycle>());
        //  List<DocRecycle> list=docRecycleMapper.selectList(new EntityWrapper<DocRecycle>());
        for(DocRecycle doc:list){
            esUtil.deleteIndex(doc.getRecycleId());
        }
        return true;
    }
}
