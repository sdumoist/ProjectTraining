package com.jxdinfo.doc.manager.foldermanager.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
@TableName("doc_fold_authority")
public class DocFoldAuthority extends Model<DocFoldAuthority> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("FOLD_AUTORITY_ID")
    private String id;
    /**
     * 目录ID
     */
    @TableField("FOLDER_ID")
    private String foldId;
    /**
     * 操作者ID
     */
    @TableField("AUTHOR_ID")
    private String authorId;
    /**
     * 操作者类型（0：userID,1:groupID,2:roleID）
     */
    @TableField("AUTHOR_TYPE")
    private String authorType;
    /**
     * 是否可编辑
     */
    @TableField("IS_EDIT")
    private String isEdit;
    /**
     * user在组织机构中的id
     */
    @TableField("organ_id")
    private String organId;

    /**
     * user在组织机构中的id
     */
    @TableField("operate_type")
    private String operateType;

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoldId() {
        return foldId;
    }

    public void setFoldId(String foldId) {
        this.foldId = foldId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }
    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DocFoldAuthority{" +
        "id=" + id +
        ", foldId=" + foldId +
        ", authorId=" + authorId +
        ", authorType=" + authorType +
        ", organId=" + organId +
        ", isEdit=" + isEdit +
        "}";
    }
}
