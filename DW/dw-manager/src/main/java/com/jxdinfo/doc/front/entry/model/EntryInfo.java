package com.jxdinfo.doc.front.entry.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.sql.Timestamp;
import java.util.List;

@TableName("entry_info")
public class EntryInfo extends Model<EntryInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("id")
    private String id;

    /**
     * 词条名
     */
    @TableField("name")
    private String name;

    /**
     * 词条概述(富文本)
     */
    @TableField("summary")
    private String summary;

    /**
     * 词条概述(纯文本)
     */
    @TableField("summary_text")
    private String summaryText;

    /**
     * 标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 状态(0：待审核，1：已审核 2：已驳回)
     */
    @TableField("state")
    private String state;

    /**
     * 状态( 0 无效 1 有效)
     */
    @TableField("valid_flag")
    private String validFlag;


    /**
     * 审核人
     */
    @TableField("audit_user_id")
    private String auditUserId;

    /**
     * 预览次数
     */
    @TableField("read_num")
    private Integer readNum;


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
     * 更新时间
     */
    @TableField("update_time")
    private Timestamp updateTime;

    /**
     * 用户头像地址
     */
    @TableField(exist = false)
    private String url;

    /**
     * 部门地址
     */
    @TableField(exist = false)
    private String deptName;

    /**
     * 创建人名称
     */
    @TableField(exist = false)
    private String createUserName;

    /**
     * 图册路径
     */
    @TableField(exist = false)
    private String imgUrl;

    /**
     * 图册标题
     */
    @TableField(exist = false)
    private String imgTitle;

    /**
     * 词条信息栏
     */
    @TableField(exist = false)
    private List<EntryInfoBar> infoBars;

    /**
     * 词条正文
     */
    @TableField(exist = false)
    private List<EntryBody> entryBodys;

    /**
     * 词条图册
     */
    @TableField(exist = false)
    private List<EntryImgs> entryImgs;


    /**
     * 词条数量
     */
    @TableField(exist = false)
    private int imgNum;

    public int getImgNum() {
        return imgNum;
    }

    public void setImgNum(int imgNum) {
        this.imgNum = imgNum;
    }

    public List<EntryBody> getEntryBodys() {
        return entryBodys;
    }

    public void setEntryBodys(List<EntryBody> entryBodys) {
        this.entryBodys = entryBodys;
    }

    public List<EntryImgs> getEntryImgs() {
        return entryImgs;
    }

    public void setEntryImgs(List<EntryImgs> entryImgs) {
        this.entryImgs = entryImgs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptname) {
        this.deptName = deptname;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public List<EntryInfoBar> getInfoBars() {
        return infoBars;
    }

    public void setInfoBars(List<EntryInfoBar> infoBars) {
        this.infoBars = infoBars;
    }
}























