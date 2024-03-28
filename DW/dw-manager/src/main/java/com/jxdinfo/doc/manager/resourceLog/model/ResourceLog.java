package com.jxdinfo.doc.manager.resourceLog.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * @ClassName: ResourceLog
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/11/13
 * @Version: 1.0
 */
@TableName("doc_resource_log")
public class ResourceLog {
    @TableField("ID")
    private String id;
    @TableField("RESOURCE_ID")
    private String resourceId;
    @TableField("RESOURCE_TYPE")
    private int resourceType;
    @TableField("USER_ID")
    private String userId;
    @TableField("OPERATE_TYPE")
    private int operateType;
    @TableField("OPERATE_TIME")
    private Timestamp operateTime;
    @TableField("valid_flag")
    private String validFlag;
    @TableField("delete_path")
    private String deletePath;
    @TableField("address_ip")
    private String addressIp;

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }

    /**
     * 数据库不存在
     * 文档名称
     */
    @TableField(exist = false)
    private String fileName;

    public String getDeletePath() {
        return deletePath;
    }

    public void setDeletePath(String deletePath) {
        this.deletePath = deletePath;
    }

    /**
     * 数据库不存在
     * 文档名称
     */

    @TableField(exist = false)
    private String userName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public Timestamp getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Timestamp operateTime) {
        this.operateTime = operateTime;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
