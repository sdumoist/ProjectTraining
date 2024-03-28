package com.jxdinfo.doc.manager.docrecycle.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jxdinfo.hussar.core.util.ToolUtil;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>
 * 回收站
 * </p>
 *
 * @author
 * @since 2018-08-09
 */
@TableName("doc_recycle")
public class DocRecycle extends Model<DocRecycle> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("recycle_id")
    private String recycleId;
    /**
     * 文档ID
     */
    @TableField("doc_id")
    private String docId;
    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;
    /**
     * 目录ID
     */
    @TableField("fold_id")
    private String foldId;
    /**
     * 上传者ID
     */
    @TableField("user_id")
    private String userId;
    /**
     * 作者ID
     */
    @TableField("author_id")
    private String authorId;
    /**
     * 联系人ID
     */
    @TableField("contacts_id")
    private String contactsId;
    /**
     * 标题
     */
    @TableField("title")
    private String title;
    /**
     * 分类
     */
    @TableField("category_id")
    private String categoryId;
    /**
     * 标签
     */
    @TableField("tag")
    private String tag;
    /**
     * 文档简介
     */
    @TableField("doc_abstract")
    private String docAbstract;
    /**
     * 文档类型
     */
    @TableField("doc_type")
    private String docType;
    /**
     * 下载积分
     */
    @TableField("download_points")
    private Integer downloadPoints;
    /**
     * 下载次数
     */
    @TableField("download_num")
    private Integer downloadNum;
    /**
     * 阅读次数
     */
    @TableField("read_num")
    private Integer readNum;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Timestamp updateTime;
    /**
     * 可见范围(0:完全公开,1:部分可见)
     */
    @TableField("visible_range")
    private Integer visibleRange;
    /**
     * 操作权限（0:预览,1:下载,2:收藏,3:分享,4:打印）（暂用，后期计划去掉，多个权限时用逗号隔开。如0,1,3）
     */
    @TableField("authority")
    private String authority;
    /**
     * 是否有效（0:无效,1:有效）
     */
    @TableField("valid_flag")
    private String validFlag;
    /**
     * 删除时间
     */
    @TableField("delete_time")
    private Date deleteTime;
    /**
     * 删除者用户ID
     */
    @TableField("delete_user_id")
    private String deleteUserId;
    /**
     * 删除状态
     */
    @TableField("clear_flag")
    private String clearFlag;

    /**
     * 数据库不存在此字段
     */
    @TableField(exist = false)
    private String activeTime;

    /**
     * 数据库不存在此字段
     */
    @TableField(exist = false)
    private String fileSize;
    /**
     * 数据库不存在
     * 上传人姓名
     */
    @TableField(exist = false)
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRecycleId() {
        return recycleId;
    }

    public void setRecycleId(String recycleId) {
        this.recycleId = recycleId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFoldId() {
        return foldId;
    }

    public void setFoldId(String foldId) {
        this.foldId = foldId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDocAbstract() {
        return docAbstract;
    }

    public void setDocAbstract(String docAbstract) {
        this.docAbstract = docAbstract;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Integer getDownloadPoints() {
        return downloadPoints;
    }

    public void setDownloadPoints(Integer downloadPoints) {
        this.downloadPoints = downloadPoints;
    }

    public Integer getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(Integer downloadNum) {
        this.downloadNum = downloadNum;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVisibleRange() {
        return visibleRange;
    }

    public void setVisibleRange(Integer visibleRange) {
        this.visibleRange = visibleRange;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public String getDeleteUserId() {
        return deleteUserId;
    }

    public void setDeleteUserId(String deleteUserId) {
        this.deleteUserId = deleteUserId;
    }

    public String getClearFlag() {
        return clearFlag;
    }

    public void setClearFlag(String clearFlag) {
        this.clearFlag = clearFlag;
    }

    public String getActiveTime() {
        Date beginDate = new Date();
        if (ToolUtil.isNotEmpty(deleteTime)) {
            String day = String.valueOf(10 - (beginDate.getTime() - deleteTime.getTime()) / (24 * 60 * 60 * 1000));
            if (day.indexOf("-") != -1) {
                return "0天";
            } else {
                return day + "天";
            }
        } else {
            return "0天";
        }
    }

    public void setActiveTime(String activeTime) {
        this.activeTime = activeTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    protected Serializable pkVal() {
        return this.recycleId;
    }

    @Override
    public String toString() {
        return "DocRecycle{" +
                "recycleId=" + recycleId +
                ", docId=" + docId +
                ", fileId=" + fileId +
                ", foldId=" + foldId +
                ", userId=" + userId +
                ", authorId=" + authorId +
                ", contactsId=" + contactsId +
                ", title=" + title +
                ", categoryId=" + categoryId +
                ", tag=" + tag +
                ", docAbstract=" + docAbstract +
                ", docType=" + docType +
                ", downloadPoints=" + downloadPoints +
                ", downloadNum=" + downloadNum +
                ", readNum=" + readNum +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", visibleRange=" + visibleRange +
                ", authority=" + authority +
                ", validFlag=" + validFlag +
                ", deleteTime=" + deleteTime +
                ", deleteUserId=" + deleteUserId +
                ", clearFlag=" + clearFlag +
                "}";
    }
}
