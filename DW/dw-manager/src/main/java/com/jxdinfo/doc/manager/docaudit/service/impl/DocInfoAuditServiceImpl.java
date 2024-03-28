package com.jxdinfo.doc.manager.docaudit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docaudit.dao.DocInfoAuditMapper;
import com.jxdinfo.doc.manager.docaudit.model.DocInfoAudit;
import com.jxdinfo.doc.manager.docaudit.model.FsFolderAuditor;
import com.jxdinfo.doc.manager.docaudit.service.IDocInfoAuditService;
import com.jxdinfo.doc.manager.docaudit.service.IFsFolderAuditorService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文件审核表 服务实现类
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
@Service
public class DocInfoAuditServiceImpl extends ServiceImpl<DocInfoAuditMapper, DocInfoAudit> implements IDocInfoAuditService {

    @Resource
    IFsFolderAuditorService fsFolderAuditorService;

    @Resource
    DocInfoService docInfoService;

    @Value("${fileAudit.auditRole}")
    private String auditRole;

    /**
     * 添加文档审核信息
     * @param folderId 目录主键
     * @param docId 文档主键
     * @return 是否成功
     */
    @Override
    public boolean addDocInfoAudit(String folderId, String docId) {
        boolean flag = true;
        // 查询目录审批人
        QueryWrapper<FsFolderAuditor> ew = new QueryWrapper<>();
        ew.eq("folder_id",folderId);
        List<FsFolderAuditor> folderAuditorList = fsFolderAuditorService.list(ew);
        if(ToolUtil.isNotEmpty(folderAuditorList)){
            List<DocInfoAudit> docInfoAuditList = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            for(FsFolderAuditor fsFolderAuditor:folderAuditorList){
                String auditUserName = fsFolderAuditor.getAuditUserName();
                if(sb.length() == 0){
                    sb.append(auditUserName);
                } else {
                    sb.append(",").append(auditUserName);
                }
                DocInfoAudit docInfoAudit = new DocInfoAudit();
                docInfoAudit.setDocId(docId);
                docInfoAudit.setAuditUserId(fsFolderAuditor.getAuditUserId());
                docInfoAudit.setAuditUserName(auditUserName);
                docInfoAudit.setAuditResult("1");
                docInfoAudit.setCreator(ShiroKit.getUser().getId());
                docInfoAudit.setCreateTime(new Timestamp(System.currentTimeMillis()));
                docInfoAuditList.add(docInfoAudit);
            }
            if(ToolUtil.isNotEmpty(docInfoAuditList)){
                DocInfo docInfo = new DocInfo();
                docInfo.setDocId(docId);
                docInfo.setAuditUser(sb.toString());
                docInfoService.updateById(docInfo);
                flag = this.saveBatch(docInfoAuditList);
            }
        }
        return flag;
    }

    /**
     * 查询待我审批列表数据
     *
     * @param userId 用户ID
     * @param pageNumber 页码
     * @param pageSize 条数
     * @param order 排序
     * @param name 文档名称
     * @return 人员数据
     */
    @Override
    public List<FsFolderView> getApprovalList(String userId, int pageNumber, int pageSize, String order, String name) {
        List<FsFolderView> result = new ArrayList<>();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        boolean flag = checkAdmin(roleList);
        if(flag){
            result = baseMapper.getApprovalListByAdmin(userId, pageNumber, pageSize, order, name);
        } else {
            result = baseMapper.getApprovalList(userId, pageNumber, pageSize, order, name);
        }
        return result;
    }

    /**
     * 查询待我审批列表数据条数
     *
     * @param userId 用户ID
     * @param name   文档名称
     * @return 数目
     */
    @Override
    public int getApprovalListCount(String userId, String name) {
        int result = 0;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        boolean flag = checkAdmin(roleList);
        if(flag){
            result = baseMapper.getApprovalListCountByAdmin(userId, name);
        } else {
            result = baseMapper.getApprovalListCount(userId, name);
        }
        return result;
    }

    /**
     * 判断角色是否包含管理员  或者 有审核文件角色
     *
     * @param roleList 角色列表
     * @return 是否
     */
    boolean checkAdmin(List<String> roleList) {
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag == 1) {
            return true;
        }

        boolean flag = false;
        for (int i = 0; i < roleList.size(); i++) {
            if (auditRole.equals(roleList.get(i))) {
                flag = true;
                break;
            }
        }
        return flag;
    }

}
