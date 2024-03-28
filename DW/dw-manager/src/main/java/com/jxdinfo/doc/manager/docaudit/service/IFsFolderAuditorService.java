package com.jxdinfo.doc.manager.docaudit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docaudit.model.FsFolderAuditor;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录审核人表 服务类
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
public interface IFsFolderAuditorService extends IService<FsFolderAuditor> {

    /**
     * 查询目录审核信息
     * @param folderId 目录ID
     * @return 目录审核信息
     */
    Map<String,Object> getFoldAuditInfo(String folderId);

    /**
     * 查询某部门下拥有审核角色的用户
     * @param deptId 部门ID
     * @param auditRole 审核角色主键
     * @return 用户
     */
    Map<String,Object> getAuditor(String deptId,String auditRole);

    /**
     * 查询有文件夹管理权限的用户
     * @param folderId 文件夹id
     * @return 管理权限数据
     */
    List<DocFoldAuthority> queryFolderAuthority(String folderId);

    /**
     * 根据群组id查询群组下的人员
     * @param groupStrList 群组id
     * @return 群组人员数据
     */
    List<Map<String, String>> getGroupUser(List<String> groupStrList);

    /**
     * 根据部门id查询部门编码
     * @param organStrList 部门id
     * @return 部门编码数据
     */
    String getOrganIds(List<String> organStrList);

    /**
     * 根据部门主键查询部门下的所有人员
     * @param organIds 部门主键
     * @return 人员数据
     */
    List<Map<String, String>> getOrganUsers(String organIds);

    /**
     * 获取选择审批人用户数据
     * @param list 有目录管理权限的用户
     * @return 审批人用户数据
     */
    List<JSTreeModel> getUserTree(List<Map<String, String>> list);

    /**
     * 添加目录审核人
     * @param folderId 目录主键
     * @param auditorIds 审核人主键
     * @param auditorNames 审核人姓名
     * @return 是否成功
     */
    boolean addFolderAuditor(String folderId, String auditorIds, String auditorNames);
}
