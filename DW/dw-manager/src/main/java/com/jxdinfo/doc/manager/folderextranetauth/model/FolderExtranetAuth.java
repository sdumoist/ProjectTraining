package com.jxdinfo.doc.manager.folderextranetauth.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.sql.Timestamp;

@TableName("folder_extranet_auth")
public class FolderExtranetAuth extends Model<FolderExtranetAuth> {
    /**
     * 关联的目录ID
     */
    @TableField("folder_id")
    private String folderId;
    /**
     * 目录名
     */
    @TableField("folder_name")
    private String folderName;
    /**
     * 操作时间
     */
    @TableField("create_time")
    private Timestamp createTime;
    /**
     * 操作人id
     */
    @TableField("create_user_id")
    private String createUserId;

    /**
     * 操作人名称
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 目录层级码
     */
    @TableField("folder_level_code")
    private String folderLevelCode;

    /**
     * 目录的路径
     */
    @TableField(exist = false)
    private String folderPath;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
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

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }


}
