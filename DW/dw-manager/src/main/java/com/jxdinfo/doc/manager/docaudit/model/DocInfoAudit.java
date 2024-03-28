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
 * 文件审核表
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
@TableName("doc_info_audit")
public class DocInfoAudit extends Model<DocInfoAudit> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("audit_id")
    private String auditId;
    /**
     * 文档主键
     */
    @TableField("doc_id")
    private String docId;
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
     * 审核时间
     */
    @TableField("audit_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 审核结果，1待审批，2通过，3驳回
     */
    @TableField("audit_result")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String auditResult;
    /**
     * 创建人
     */
    @TableField("creator")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String creator;
    /**
     * 创建时间
     */
    @TableField("create_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 修改人
     */
    @TableField("last_editor")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private String lastEditor;
    /**
     * 修改时间
     */
    @TableField("last_time")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;
    /**
     * 审核意见
     */
    @TableField("audit_opinion")
    private String auditOpinion;

    public String getAuditId() {
        return auditId;
    }

    public void setAuditId(String auditId) {
        this.auditId = auditId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult;
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

    public String getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    @Override
    protected Serializable pkVal() {
        return this.auditId;
    }

    @Override
    public String toString() {
        return "DocInfoAudit{" +
        "auditId=" + auditId +
        ", docId=" + docId +
        ", auditUserId=" + auditUserId +
        ", auditUserName=" + auditUserName +
        ", auditTime=" + auditTime +
        ", auditResult=" + auditResult +
        ", creator=" + creator +
        ", createTime=" + createTime +
        ", lastEditor=" + lastEditor +
        ", lastTime=" + lastTime +
        "}";
    }
}
