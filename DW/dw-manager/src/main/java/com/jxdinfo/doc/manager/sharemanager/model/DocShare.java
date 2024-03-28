package com.jxdinfo.doc.manager.sharemanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * @ClassName: DocShare
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/10/30
 * @Version: 1.0
 */
@TableName("doc_share_resource")
public class DocShare {
    private static final long serialVersionUID = 1L;
    /**
     * 主键分享资源ID
     */
    @TableId("share_id")
    private String shareId;
    /**
     * 分享者ID
     */
    @TableField("create_user_id")
    private String createUserId;
    /**
     * 映射地址对应文档id
     */
    @TableField("doc_id")
    private String docId;
    /**
     * 资源链接地址
     */
    @TableField("true_url")
    private String trueUrl;
    /**
     * 资源链接加密后的映射值
     */
    @TableField("mapping_url")
    private String mappingUrl;
    /**
     * 访问该资源是否需要登录，默认不需要
     */
    @TableField("login_flag")
    private String loginFlag;
    /**
     * 是否需要密码
     */
    @TableField("pwd_flag")
    private String pwdFlag;
    /**
     * 访问资源密码
     */
    @TableField("password")
    private String password;
    /**
     * 资源的有效性，默认有效
     */
    @TableField("valid")
    private String valid;
    /**
     * 有效时长
     */
    @TableField("valid_time")
    private Timestamp validTime;
    /**
     * 分享时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 数据库不存在此字段
     * 有效期限
     */
    @TableField(exist = false)
    private String activeTime;
    /**
     * 数据库不存在此字段
     * 文件的大小
     */
    @TableField(exist = false)
    private Integer view;

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    /**
     * 数据库不存在此字段
     * 文件的大小
     */
    @TableField(exist = false)
    private String fileSize;
    /**
     * 数据库不存在此字段
     * 文件的大小
     */
    @TableField(exist = false)
    private String authority;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    /**
     * 数据库不存在
     * 上传人姓名
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 数据库不存在
     * 文档名称
     */
    @TableField(exist = false)
    private String fileName;

    /**
     * 数据库不存在
     * 文档类型
     */
    @TableField(exist = false)
    private String docType;

    /**
     * 数据库不存在此字段
     * 文件的大小
     */
    @TableField(exist = false)
    private String size;

    @TableField(exist = false)
    private String effectiveDays;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTrueUrl() {
        return trueUrl;
    }

    public void setTrueUrl(String trueUrl) {
        this.trueUrl = trueUrl;
    }

    public String getMappingUrl() {
        return mappingUrl;
    }

    public void setMappingUrl(String mappingUrl) {
        this.mappingUrl = mappingUrl;
    }

    public String getLoginFlag() {
        return loginFlag;
    }

    public void setLoginFlag(String loginFlag) {
        this.loginFlag = loginFlag;
    }

    public String getPwdFlag() {
        return pwdFlag;
    }

    public void setPwdFlag(String pwdFlag) {
        this.pwdFlag = pwdFlag;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public Timestamp getValidTime() {
        return validTime;
    }

    public void setValidTime(Timestamp validTime) {
        this.validTime = validTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getActiveTime() {
//        Date beginDate = new Date();
//        if (ToolUtil.isNotEmpty(validTime)) {
//            String day = String.valueOf((validTime.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000));
//            long day1 = (validTime.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
//            if (day.indexOf("-") != -1) {
//                return "0天";
//            } else if (day){
//                return day + "天";
//            }
//        } else {
//            return "0天";
//        }
        return activeTime;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getEffectiveDays() {
        return effectiveDays;
    }

    public void setEffectiveDays(String effectiveDays) {
        this.effectiveDays = effectiveDays;
    }

    @Override
    public String toString() {
        return "DocShare{" +
                "shareId='" + shareId + '\'' +
                ", createUserId='" + createUserId + '\'' +
                ", docId='" + docId + '\'' +
                ", trueUrl='" + trueUrl + '\'' +
                ", mappingUrl='" + mappingUrl + '\'' +
                ", loginFlag='" + loginFlag + '\'' +
                ", pwdFlag='" + pwdFlag + '\'' +
                ", password='" + password + '\'' +
                ", valid='" + valid + '\'' +
                ", validTime=" + validTime +
                ", createTime=" + createTime +
                ", activeTime='" + activeTime + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", authority='" + authority + '\'' +
                ", userName='" + userName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", docType='" + docType + '\'' +
                ", size='" + size + '\'' +
                ", effectiveDays='" + effectiveDays + '\'' +
                '}';
    }
}

