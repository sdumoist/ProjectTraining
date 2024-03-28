package com.jxdinfo.doc.manager.componentmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 复用登记实体类
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@TableName("multiplex_project")
public class MultiplexProject extends Model<MultiplexProject> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId("project_id")
    private String projectId;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    /**
     * 项目所属部门
     */
    @TableField("project_dept")
    private String projectDept;

    /**
     * 复用简介
     */
    @TableField("multiplex_desc")
    private String multiplexDesc;

    /**
     * 项目负责人
     */
    @TableField(exist = false)
    private  String projectUser;


    /**
     * 修改时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    @TableField(exist = false)
    private String createTimeStr;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;
    /**
     * 用户ID
     */
    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String caUserName;

    public String getCaUserName() {
        return caUserName;
    }

    public void setCaUserName(String caUserName) {
        this.caUserName = caUserName;
    }

    @TableField(exist = false)
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    /**
     * ID
     */
    @TableField(exist = false)
    private String multiplexId;

    /**
     * ID
     */
    @TableField(exist = false)
    private String componentId;

    /**
     * 部门
     */
    @TableField(exist = false)
    private String organAlias;

    public String getOrganAlias() {
        return organAlias;
    }

    public void setOrganAlias(String organAlias) {
        this.organAlias = organAlias;
    }

    /**
     * 统计项目复用数量
     */
        @TableField(exist = false)
    private String projectCount;

    /**
     * 数量
     */
    @TableField(exist = false)
    private String re_num;

    public String getRe_num() {
        return re_num;
    }

    public void setRe_num(String re_num) {
        this.re_num = re_num;
    }
    /**
     * ID
     */
    @TableField(exist = false)
    private String componentName;

    public String getMultiplexId() {
        return multiplexId;
    }

    public void setMultiplexId(String multiplexId) {
        this.multiplexId = multiplexId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * ID
     */
    @TableField(exist = false)
    private String componentType;

    /**
     * ID
     */
    @TableField(exist = false)
    private String economize;


    public String getEconomize() {
        return economize;
    }

    public void setEconomize(String economize) {
        this.economize = economize;
    }

    @Override

    protected Serializable pkVal() {
        return projectId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDept() {
        return projectDept;
    }

    public void setProjectDept(String projectDept) {
        this.projectDept = projectDept;
    }

    public String getMultiplexDesc() {
        return multiplexDesc;
    }

    public void setMultiplexDesc(String multiplexDesc) {
        this.multiplexDesc = multiplexDesc;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectUser() {
        return projectUser;
    }

    public void setProjectUser(String projectUser) {
        this.projectUser = projectUser;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(String projectCount) {
        this.projectCount = projectCount;
    }

    /**
     * 统计项目复用数量
     */
    @TableField(exist = false)
    private int componentCount;

    public int getComponentCount() {
        return componentCount;
    }

    public void setComponentCount(int componentCount) {
        this.componentCount = componentCount;
    }
}
