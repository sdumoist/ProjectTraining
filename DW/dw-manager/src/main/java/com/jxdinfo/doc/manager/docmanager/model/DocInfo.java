package com.jxdinfo.doc.manager.docmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * <p>
 * 文档信息表
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
@TableName("doc_info")
public class DocInfo {


    /**
     * 文档ID
     */
	@TableId("doc_id")
    private String docId;

    /**
     * 上传者ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 上传者姓名
     */
    @TableField(exist = false)
    private String userName;
    /**
     * 作者ID
     */
    @TableField("author_id")
    private String authorId;
    /**
     * 作者
     */
    @TableField(exist = false)
    private String authorName;

    /**
     * 联系人ID
     */
    @TableField("contacts_id")
    private String contactsId;
    /**
     * 联系人姓名
     */
    @TableField(exist = false)
    private String contactsName;
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
     * 是否可分享（0：不可  1：可分享）
     */
    @TableField("share_flag")
    private String shareFlag;

    /**
     * 流程实例id
     */
    @TableField("process_instance_id")
    private String processInstanceId;

    /**
     * 任务id
     */
    @TableField("task_id")
    private String taskId;

    public String getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(String shareFlag) {
        this.shareFlag = shareFlag;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * 标签(新)

     */
    @TableField("tags")
    private String tags;
    /**
     * 文档简介 这个后续可以删掉
     */
    @TableField(exist = false)
    private String fileAbstract;

    /**
     * 文档简介
     */
    @TableField("doc_abstract")
    private String docAbstract;

    /**
     * 文档类型  这个后续可以删掉
     */
    @TableField(exist = false)
    private String fileType;
    /**
     * 文档类型
     */
    @TableField("doc_type")
    private String docType;
    /**
     * 用户水印（0:不添加；1:添加）
     * 预览时在文档里添加当前预览用户的水印
     */
    @TableField("watermark_user")
    private String watermarkUser;
    /**
     * 公司水印（默认为空，有值就添加）
     */
    @TableField("watermark_company")
    private String watermarkCompany;
    /**
     * 下载积分
     */
    @TableField("download_points")
    private Integer downloadPoints;

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
     * 下载次数
     */
    @TableField("download_num")
    private Integer downloadNum;

    /**
     * 预览次数
     */
    @TableField("read_num")
    private Integer readNum;

    /**
     * 允许下载
     */
    @TableField(exist = false)
    private Integer allowDownload;

    /**
     * 文件id
     */
    @TableField("file_id")
    private String fileId;
    /**
     * 文件夹id
     */
    @TableField("fold_id")
    private String foldId;

    @TableField("set_authority")
    private String setAuthority;
    public String getSetAuthority() {
        return setAuthority;
    }

    public void setSetAuthority(String setAuthority) {
        this.setAuthority = setAuthority;
    }


    /**
     * 预览文件id
     */
    @TableField(exist = false)
    private String deptName;


    /**
     * 预览文件id
     */
    @TableField(exist = false)
    private String folderName;
    /**
     * 预览文件id
     */
    @TableField(exist = false)
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 预览文件id
     */
    @TableField(exist = false)
    private String size;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    /**
     * 预览文件id
     */
    @TableField(exist = false)
    private String previewFileId;
    /**
     * 下载文件id
     */
    @TableField(exist = false)
    private String downloadFileId;
    /**
     * 文件路径
     */
    @TableField(exist = false)
    private String filePath;
    /**
     * pdf文件路径
     */
    @TableField(exist = false)
    private String filePdfPath;

    /**
     * 联系人电话
     */
    @TableField(exist = false)
    private String levelCode;
    /**
     * 联系人电话
     */
    @TableField(exist = false)
    private String mobile;
    /**
     * 文件大小
     */
    @TableField(exist = false)
    private String fileSize;
    /**
     * 上传的时间点
     */
    @TableField(exist = false)
    private String showTime;
    /**
     * 收藏量
     */
    @TableField(exist = false)
    private String collectNum;
    /**
     * 用于同步
     */
    @TableField(exist = false)
    private String recordId;
    /**
     * 用于同步
     */
    @TableField(exist = false)
    private String operateType;
    /**
     * 用于同步
     */
    @TableField(exist = false)
    private String md5;
    /**
     * 收藏量
     */
    @TableField(exist = false)
    private String localPath;

    /**
     * 审核状态
     */
    @TableField("examine_state")
    private String examineState;

    public String getCollectNum() {
        return collectNum;
    }

    public void setCollectNum(String collectNum) {
        this.collectNum = collectNum;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * 文件状态
     */
    @TableField("valid_flag")
    private String validFlag;
    /**
     * 操作权限（0:预览,1:下载,2:收藏,3:分享,4:打印）（暂用，后期计划去掉，多个权限时用逗号隔开。如0,1,3）
     */
    @TableField("authority")
    private String authority;
    /**
     * 可见范围(0:完全公开,1:部分可见)
     */
    @TableField("visible_range")
    private Integer visibleRange;
    @TableField(exist = false)
    private String thumbPath;


    /**
     * 版本号
     */
    @TableField(exist = false)
    private Integer versionNumber;

    /**
     * 审核人
     */
    @TableField("audit_user")
    private String auditUser;

    /**
     * 审核时间
     */
    @TableField("audit_time")
    private Timestamp auditTime;

    /**
     * 审核意见
     */
    @TableField("audit_opinion")
    private String auditOpinion;

    /**
     * 随机数
     */
    @TableField("random_num")
    private Integer randomNum;

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDocAbstract() {
        return docAbstract;
    }

    public void setDocAbstract(String docAbstract) {
        this.docAbstract = docAbstract;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
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

    public String getFileAbstract() {
        return fileAbstract;
    }

    public void setFileAbstract(String fileAbstract) {
        this.fileAbstract = fileAbstract;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getDownloadPoints() {
        return downloadPoints;
    }

    public void setDownloadPoints(Integer downloadPoints) {
        this.downloadPoints = downloadPoints;
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

    public Integer getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(Integer allowDownload) {
        this.allowDownload = allowDownload;
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

    public String getPreviewFileId() {
        return previewFileId;
    }

    public void setPreviewFileId(String previewFileId) {
        this.previewFileId = previewFileId;
    }

    public String getDownloadFileId() {
        return downloadFileId;
    }

    public void setDownloadFileId(String downloadFileId) {
        this.downloadFileId = downloadFileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePdfPath() {
        return filePdfPath;
    }

    public void setFilePdfPath(String filePdfPath) {
        this.filePdfPath = filePdfPath;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getWatermarkUser() {
        return watermarkUser;
    }

    public void setWatermarkUser(String watermarkUser) {
        this.watermarkUser = watermarkUser;
    }

    public String getWatermarkCompany() {
        return watermarkCompany;
    }

    public void setWatermarkCompany(String watermarkCompany) {
        this.watermarkCompany = watermarkCompany;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }


    public Integer getVisibleRange() {
        return visibleRange;
    }

    public void setVisibleRange(Integer visibleRange) {
        this.visibleRange = visibleRange;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
	public String toString() {
		return "DocInfo {docId=" + docId + ", userId=" + userId + ", userName=" + userName + ", authorId=" + authorId
				+ ", authorName=" + authorName + ", contactsId=" + contactsId + ", contactsName=" + contactsName
				+ ", title=" + title + ", categoryId=" + categoryId + ", tag=" + tag + ", fileAbstract=" + fileAbstract
				+ ", docAbstract=" + docAbstract + ", fileType=" + fileType + ", docType=" + docType
				+ ", watermarkUser=" + watermarkUser + ", watermarkCompany=" + watermarkCompany + ", downloadPoints="
				+ downloadPoints + ", createTime=" + createTime + ", updateTime=" + updateTime + ", downloadNum="
				+ downloadNum + ", readNum=" + readNum + ", allowDownload=" + allowDownload + ", fileId=" + fileId
				+ ", foldId=" + foldId + ", previewFileId=" + previewFileId + ", downloadFileId=" + downloadFileId
				+ ", filePath=" + filePath + ", filePdfPath=" + filePdfPath + ", mobile=" + mobile + ", fileSize="
				+ fileSize + ", validFlag=" + validFlag + ", authority=" + authority + ", visibleRange=" + visibleRange
				+ "}";
	}

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public Timestamp getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Timestamp auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public Integer getRandomNum() {
        return randomNum;
    }

    public void setRandomNum(Integer randomNum) {
        this.randomNum = randomNum;
    }

    public String getExamineState() {
        return examineState;
    }

    public void setExamineState(String examineState) {
        this.examineState = examineState;
    }
}
