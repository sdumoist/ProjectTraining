package com.jxdinfo.doc.manager.docaudit.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 目录审核人表
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
@TableName("fs_folder_auditor")
public class FsFolderAuditor extends Model<FsFolderAuditor> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("audit_id")
    private String auditId;
    /**
     * 目录主键
     */
    @TableField("folder_id")
    private String folderId;
    /**
     * 审核人主键
     */
    @TableField("audit_user_id")
    private String auditUserId;
    /**
     * 审核人姓名
     */
    @TableField("audit_user_name")
    private String auditUserName;
    /**
     * 创建人
     */
    @TableField("creator")
    private String creator;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.auditId;
    }

    @Override
    public String toString() {
        return "FsFolderAuditor{" +
        "auditId=" + auditId +
        ", folderId=" + folderId +
        ", auditUserId=" + auditUserId +
        ", auditUserName=" + auditUserName +
        ", creator=" + creator +
        ", createTime=" + createTime +
        "}";
    }
}
