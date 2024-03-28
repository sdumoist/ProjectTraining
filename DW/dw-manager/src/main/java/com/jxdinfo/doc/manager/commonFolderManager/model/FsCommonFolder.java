package com.jxdinfo.doc.manager.commonFolderManager.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 常用目录表
 */
@TableName("fs_common_folder")
public class FsCommonFolder extends Model<FsCommonFolder> {

    private static final long serialVersionUID = 1L;

    /**
     * 常用目录ID
     */
    @TableId(value = "common_folder_id",type = IdType.ASSIGN_UUID)
    private String commonFolderId;

    /**
     * 常用目录名称
     */
    @TableField("common_folder_name")
    private String commonFolderName;

    /**
     * 目录ID
     */
    @TableField("folder_id")
    private String folderId;

    /**
     * 目录名称
     */
    @TableField("folder_name")
    private String folderName;

    /**
     * 创建人ID
     */
    @TableField("create_user_id")
    private String createUserId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 显示顺序
     */
    @TableField("show_order")
    private Integer showOrder;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;


    public String getCommonFolderId() {
        return commonFolderId;
    }

    public void setCommonFolderId(String commonFolderId) {
        this.commonFolderId = commonFolderId;
    }

    public String getCommonFolderName() {
        return commonFolderName;
    }

    public void setCommonFolderName(String commonFolderName) {
        this.commonFolderName = commonFolderName;
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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    protected Serializable pkVal() {
        return this.commonFolderId;
    }

    @Override
    public String toString() {
        return "FsCommonFolder{" +
                "commonFolderId=" + commonFolderId  +
                ", commonFolderName=" + commonFolderName  +
                ", folderId=" + folderId  +
                ", folderName=" + folderName  +
                ", createUserId=" + createUserId  +
                ", createTime=" + createTime +
                ", showOrder=" + showOrder +
                ", fileType=" + fileType +
                '}';
    }
}
