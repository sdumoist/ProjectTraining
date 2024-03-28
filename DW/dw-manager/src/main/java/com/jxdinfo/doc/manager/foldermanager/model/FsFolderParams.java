package com.jxdinfo.doc.manager.foldermanager.model;/**
 * Created by zoufeng on 2018/9/7.
 */


import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName FsFolderEdit
 * @Description TODO 编辑及新增保存参数vo
 * @Author zoufeng
 * @Date 2018/9/7 17:05
 * @Version 1.0
 **/
public class FsFolderParams extends Model<FsFolderParams> {

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPersonOrgan() {
        return personOrgan;
    }

    public void setPersonOrgan(String personOrgan) {
        this.personOrgan = personOrgan;
    }

    public String getGroupPower() {
        return groupPower;
    }

    public void setGroupPower(String groupPower) {
        this.groupPower = groupPower;
    }

    public String getPersonPower() {
        return personPower;
    }

    public void setPersonPower(String personPower) {
        this.personPower = personPower;
    }

    public String getPersonOrganPower() {
        return personOrganPower;
    }

    public void setPersonOrganPower(String personOrganPower) {
        this.personOrganPower = personOrganPower;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    protected Serializable pkVal() {
        return this.folderId;
    }

    /** 是否公开 */
    private String visible;

    /** 目录名称 */
    private String folderName;

    /** 是否可编辑 */
    private String isEdit;

    /** 群组 */
    private String group;

    /** 人员 */
    private String person;

    /** 人员组织机构id */
    private String personOrgan;

    /** 群组集合 */
    private String groupPower;

    /** 人员集合 */
    private String personPower;

    /** 人员组织机构id集合*/
    private String personOrganPower;

    /** 目录id */
    private String folderId;

    /** 父节点*/
    private String parentFolderId;

    /** 目录页数 */
    private int pageNumber;

    /** 每页条数 */
    private int pageSize;

    /** 目录id */
    private String id;

    /** 文件类型 */
    private String[] typeArr;

    /** 文件名称 */
    private String name;

    /** 排序字段 */
    private String orderResult;

    /** 人员权限*/
    private String operateTypeStrPerson;

    /** 组织机构 全员 群组权限 */
    private String operateTypeStrGroup;
    /** 角色 */
    private String role;

    /** 角色类型 */
    private String authorTypeStrRole;

    /** 角色权限*/
    private String operateTypeStrRole;
    /** 角色id集合 */
    private List roleList;

    public List getRoleList() {
        return roleList;
    }

    public void setRoleList(List roleList) {
        this.roleList = roleList;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAuthorTypeStrRole() {
        return authorTypeStrRole;
    }

    public void setAuthorTypeStrRole(String authorTypeStrRole) {
        this.authorTypeStrRole = authorTypeStrRole;
    }

    public String getOperateTypeStrRole() {
        return operateTypeStrRole;
    }

    public void setOperateTypeStrRole(String operateTypeStrRole) {
        this.operateTypeStrRole = operateTypeStrRole;
    }

    public String getOperateTypeStrPerson() {
        return operateTypeStrPerson;
    }

    public void setOperateTypeStrPerson(String operateTypeStrPerson) {
        this.operateTypeStrPerson = operateTypeStrPerson;
    }

    public String getOperateTypeStrGroup() {
        return operateTypeStrGroup;
    }

    public void setOperateTypeStrGroup(String operateTypeStrGroup) {
        this.operateTypeStrGroup = operateTypeStrGroup;
    }

    public String getAuthorTypeStrPerson() {
        return authorTypeStrPerson;
    }

    public void setAuthorTypeStrPerson(String authorTypeStrPerson) {
        this.authorTypeStrPerson = authorTypeStrPerson;
    }

    public String getAuthorTypeStrGroup() {
        return authorTypeStrGroup;
    }

    public void setAuthorTypeStrGroup(String authorTypeStrGroup) {
        this.authorTypeStrGroup = authorTypeStrGroup;
    }

    /** 操作者人员类型 */
    private String authorTypeStrPerson;

    /** 操作者群组、组织机构、全体人员*/
    private String authorTypeStrGroup;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    private String order;

    /** 群组id */
    private List groupList;

    /** 用户id */
    private String userId;

    /** 管理员类型*/
    private Integer adminFlag;

    /** 前后台标识 */
    private String type;

    /** 层级码*/
    private List<String> levelCodeList;

    public String getLevelCodeString() {
        return levelCodeString;
    }

    public void setLevelCodeString(String levelCodeString) {
        this.levelCodeString = levelCodeString;
    }

    /** 权限目录层级码String */
    private String levelCodeString;

    public List<String> getLevelCodeList() {
        return levelCodeList;
    }

    public void setLevelCodeList(List<String> levelCodeList) {
        this.levelCodeList = levelCodeList;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getTypeArr() {
        return typeArr;
    }

    public void setTypeArr(String[] typeArr) {
        this.typeArr = typeArr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderResult() {
        return orderResult;
    }

    public void setOrderResult(String orderResult) {
        this.orderResult = orderResult;
    }

    public List getGroupList() {
        return groupList;
    }

    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAdminFlag() {
        return adminFlag;
    }

    public void setAdminFlag(Integer adminFlag) {
        this.adminFlag = adminFlag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    /** 目录是否审核 */
    private String auditFlag;

    /** 目录审核人主键 */
    private String auditorIds;

    /** 目录审核人名称 */
    private String auditorNames;

    public String getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(String auditFlag) {
        this.auditFlag = auditFlag;
    }

    public String getAuditorIds() {
        return auditorIds;
    }

    public void setAuditorIds(String auditorIds) {
        this.auditorIds = auditorIds;
    }

    public String getAuditorNames() {
        return auditorNames;
    }

    public void setAuditorNames(String auditorNames) {
        this.auditorNames = auditorNames;
    }
}
