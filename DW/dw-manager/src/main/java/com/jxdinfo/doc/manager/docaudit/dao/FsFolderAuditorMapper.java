package com.jxdinfo.doc.manager.docaudit.dao;

import com.jxdinfo.doc.manager.docaudit.model.FsFolderAuditor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录审核人表 Mapper 接口
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
public interface FsFolderAuditorMapper extends BaseMapper<FsFolderAuditor> {

    /**
     * 查询目录审核信息
     * @param folderId 目录ID
     * @return 目录审核信息
     */
    Map<String,Object> getFoldAuditInfo(@Param("folderId") String folderId);

    /**
     * 查询某部门下拥有审核角色的用户
     * @param deptId 部门ID
     * @param auditRole 审核角色主键
     * @return 用户
     */
    Map<String,Object> getAuditor(@Param("deptId") String deptId,@Param("auditRole") String auditRole);

    /**
     * 根据群组id查询群组下的人员
     * @param groupStrList 群组id
     * @return 群组人员数据
     */
    List<Map<String, String>> getGroupUser(List<String> groupStrList);

    /**
     * 根据部门id查询部门编码
     * @param orgId 部门id
     * @return 部门编码数据
     */
    String getOrganIds(@Param("orgId") String orgId);

    /**
     * 根据部门主键查询部门下的所有人员
     * @param organIdList 部门主键
     * @return 人员数据
     */
    List<Map<String, String>> getOrganUsers(List<String> organIdList);

    /**
     * 获取选择审批人用户数据
     * @param list 有目录管理权限的用户
     * @return 审批人用户数据
     */
    List<JSTreeModel> getUserTree(List<Map<String, String>> list);
}
