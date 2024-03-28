package com.jxdinfo.doc.manager.middlegroundConsulation.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * describe:
 *
 * @author lixin
 * @date 2020/01/08
 */
@TableName("middleground_consulation")
public class MiddlegroundConsulation extends Model<MiddlegroundConsulation> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("consulation_id")
    private String consulationId;

    /**
     * 标题
     */
    @TableField("consulation_title")
    private String consulationTitle;

    /**
     * 部门id
     */
    @TableField("dept_id")
    private String deptId;

    /**
     * 部门名称
     */
    @TableField("dept_name")
    private String deptName;

    /**
     * 时间
     */
    @TableField("consulation_time")
    private String consulationTime;

    /**
     * 项目Id
     */
    @TableField("project_id")
    private String projectId;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String projectName;

    @TableField("project_desc")
    private String projectDesc;
    /**
     * 参与人
     */
    @TableField("consulation_participant")
    private String participant;

    /**
     * 内容
     */
    @TableField("consulation_content")
    private String consulationContent;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 创建人名称
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 创建人id
     */
    @TableField("create_user_id")
    private String createUserId;

    /**
     * 最后修改时间
     */
    @TableField("update_time")
    private Timestamp updateTime;

    /**
     * 状态 0：驳回，1：申请，2：通过
     */
    @TableField("state")
    private String state;

    @TableField("content_text")
    private String contentText;

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    @Override
    protected Serializable pkVal() {
        return consulationId;
    }

    public String getConsulationId() {
        return consulationId;
    }

    public void setConsulationId(String consulationId) {
        this.consulationId = consulationId;
    }

    public String getConsulationTitle() {
        return consulationTitle;
    }

    public void setConsulationTitle(String consulationTitle) {
        this.consulationTitle = consulationTitle;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getConsulationTime() {
        return consulationTime;
    }

    public void setConsulationTime(String consulationTime) {
        this.consulationTime = consulationTime;
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

    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getConsulationContent() {
        return consulationContent;
    }

    public void setConsulationContent(String consulationContent) {
        this.consulationContent = consulationContent;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }
}
