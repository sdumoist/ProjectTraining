package com.jxdinfo.doc.manager.foldermanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 目录表
 * </p>
 *
 * @author lyq
 * @since 2018-08-09
 */
@TableName("fs_folder")
public class FsFolder extends Model<FsFolder> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId("folder_id")
    private String folderId;
    /**
     * 目录名
     */
    @TableField("folder_name")
    private String folderName;
    /**
     * 可见范围(0:完全公开,1:部分可见)
     */
    @TableField("visible_range")
    private String visibleRange;
    /**
     * 是否可编辑（0:可编辑,1:不可编辑）（下阶段需要去除该字段，将权限设置到目录权限人员对应表中）
     */
    @TableField("is_edit")
    private String isEdit;
    /**
     * 上级目录ID
     */
    @TableField("parent_folder_id")
    private String parentFolderId;
    /**
     * 目录层级码（规则：001,001001,001002,001002001,001002002）
     */
    @TableField("level_code")
    private String levelCode;
    /**
     * 显示顺序
     */
    @TableField("show_order")
    private Integer showOrder;
    /**
     * 创建人
     */
    @TableField("create_user_id")
    private String createUserId;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;
    /**
     * 更新人
     */
    @TableField("update_user_id")
    private String updateUserId;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Timestamp updateTime;
    /**
     * 子目录
     * 不在表中
     */


    @TableField("own_id")
    private String ownId;
    @TableField("folder_path")
    private String folderPath;

    /**
     * 是否审核
     */
    @TableField("audit_flag")
    private String auditFlag;

    public String getOwnId() {
        return ownId;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setOwnId(String ownId) {
        this.ownId = ownId;
    }
    @TableField(exist = false)
    private List<FsFolder> children;

    public List<FsFolder> getChildren() {
        return children;
    }

    public void setChildren(List<FsFolder> children) {
        this.children = children;
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

    public String getVisibleRange() {
        return visibleRange;
    }

    public void setVisibleRange(String visibleRange) {
        this.visibleRange = visibleRange;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
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

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }


    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getAuditFlag() {
        return auditFlag;
    }

    public void setAuditFlag(String auditFlag) {
        this.auditFlag = auditFlag;
    }

    @Override
    protected Serializable pkVal() {
        return this.folderId;
    }

    @Override
    public String toString() {
        return "FsFolder{" +
        "folderId=" + folderId +
        ", folderName=" + folderName +
        ", visibleRange=" + visibleRange +
        ", isEdit=" + isEdit +
        ", parentFolderId=" + parentFolderId +
        ", levelCode=" + levelCode +
        ", showOrder=" + showOrder +
        ", createUserId=" + createUserId +
        ", createTime=" + createTime +
        ", updateUserId=" + updateUserId +
        ", updateTime=" + updateTime +
        "}";
    }
}
