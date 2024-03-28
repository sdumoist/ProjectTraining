package com.jxdinfo.doc.manager.docaudit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docaudit.dao.FsFolderAuditorMapper;
import com.jxdinfo.doc.manager.docaudit.model.FsFolderAuditor;
import com.jxdinfo.doc.manager.docaudit.service.IFsFolderAuditorService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录审核人表 服务实现类
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
@Service
public class FsFolderAuditorServiceImpl extends ServiceImpl<FsFolderAuditorMapper, FsFolderAuditor> implements IFsFolderAuditorService {
    @Resource
    IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 查询目录审核信息
     * @param folderId 目录ID
     * @return 目录审核信息
     */
    @Override
    public Map<String, Object> getFoldAuditInfo(String folderId) {
        return baseMapper.getFoldAuditInfo(folderId);
    }

    /**
     * 查询某部门下拥有审核角色的用户
     * @param deptId 部门ID
     * @param auditRole 审核角色主键
     * @return 用户
     */
    @Override
    public Map<String, Object> getAuditor(String deptId,String auditRole) {
        return baseMapper.getAuditor(deptId,auditRole);
    }

    /**
     * 查询有文件夹管理权限的用户
     *
     * @param folderId 文件夹id
     * @return 管理权限数据
     */
    @Override
    public List<DocFoldAuthority> queryFolderAuthority(String folderId) {
        // 查询有管理权限的数据
        List<DocFoldAuthority> list = docFoldAuthorityService.list(new QueryWrapper<DocFoldAuthority>()
                .eq("FOLDER_ID",folderId).eq("operate_type","2").orderBy(true, false,"author_type"));
        // 返回数据
        return list;
    }

    /**
     * 根据群组id查询群组下的人员
     *
     * @param groupStrList 群组id
     * @return 群组人员数据
     */
    @Override
    public List<Map<String, String>> getGroupUser(List<String> groupStrList) {
        // 返回数据
        return baseMapper.getGroupUser(groupStrList);
    }

    /**
     * 根据部门id查询部门编码
     *
     * @param organStrList 部门id
     * @return 部门编码数据
     */
    @Override
    public String getOrganIds(List<String> organStrList) {
        StringBuilder sb = new StringBuilder();
        for(String orgId:organStrList){
            String temp = baseMapper.getOrganIds(orgId);
            if(ToolUtil.isNotEmpty(temp)){
                if(sb.length() == 0){
                    sb.append(temp);
                } else {
                    sb.append(",").append(temp);
                }
            }
        }
        // 返回数据
        return sb.toString();
    }

    /**
     * 根据部门主键查询部门下的所有人员
     *
     * @param organIds 部门主键
     * @return 人员数据
     */
    @Override
    public List<Map<String, String>> getOrganUsers(String organIds) {
        String[] organIdArr = organIds.split(",");
        List<String> list = Arrays.asList(organIdArr);
        // 返回数据
        return baseMapper.getOrganUsers(list);
    }

    /**
     * 获取选择审批人用户数据
     *
     * @param list 有目录管理权限的用户
     * @return 审批人用户数据
     */
    @Override
    public List<JSTreeModel> getUserTree(List<Map<String, String>> list) {
        // 返回数据
        return baseMapper.getUserTree(list);
    }

    /**
     * 添加目录审核人
     *
     * @param folderId     目录主键
     * @param auditorIds   审核人主键
     * @param auditorNames 审核人姓名
     * @return 是否成功
     */
    @Override
    public boolean addFolderAuditor(String folderId, String auditorIds, String auditorNames) {
        boolean result = true;
        List<FsFolderAuditor> folderAuditorList = new ArrayList<>();
        String[] auditUserIdArr = auditorIds.split(",");
        String[] auditUserNameArr = auditorNames.split(",");
        if(auditUserIdArr.length == auditUserNameArr.length){
            for(int k = 0;k < auditUserIdArr.length;k++){
                String auditUserId = auditUserIdArr[k];
                String auditUserName = auditUserNameArr[k];
                String userId = ShiroKit.getUser().getId();
                FsFolderAuditor fsFolderAuditor = new FsFolderAuditor();
                fsFolderAuditor.setFolderId(folderId);
                fsFolderAuditor.setAuditUserId(auditUserId);
                fsFolderAuditor.setAuditUserName(auditUserName);
                fsFolderAuditor.setCreator(userId);
                fsFolderAuditor.setCreateTime(new Timestamp(System.currentTimeMillis()));
                folderAuditorList.add(fsFolderAuditor);
            }
            if(ToolUtil.isNotEmpty(folderAuditorList)){
                result = this.saveBatch(folderAuditorList);
            }
        }
        return result;
    }
}
