package com.jxdinfo.doc.manager.personextranetaccess.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;



import java.sql.Timestamp;

@TableName("sys_personnel_network_permissions")
public class SysPersonnelNetworkPermissions   {


    @TableField("department")
    private String department;

    @TableField("user_name")
    private String userName;

    @TableField("create_time")
    private Timestamp createTime;

    @TableField("create_user_id")
    private String createUserId;

    @TableField("user_id")
    private String userId;

    @TableField("create_user_name")
    private String createUserName;



    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
}
