package com.jxdinfo.doc.manager.docmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyq
 * @since 2018-08-08
 */
@TableName("doc_resource_log")
public class DocResourceLog extends Model<DocResourceLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("ID")
    private String id;
    /**
     * 资源ID（文件ID或目录ID）
     */
    @TableField("RESOURCE_ID")
    private String resourceId;
    /**
     * 资源类型(0:FILE,1:FOLD)
     */
    @TableField("RESOURCE_TYPE")
    private Integer resourceType;
    /**
     * 操作者用户ID
     */
    @TableField("USER_ID")
    private String userId;
    /**
     * 操作类型（0：上传,1:修改,2:删除,3:预览,4:下载）
     */
    @TableField("OPERATE_TYPE")
    private Integer operateType;
    /**
     * 操作时间
     */
    @TableField("OPERATE_TIME")
    private Timestamp operateTime;

    @TableField("delete_path")
    private String deletePath;

    public String getDeletePath() {
        return deletePath;
    }

    public void setDeletePath(String deletePath) {
        this.deletePath = deletePath;
    }

    /**
     * 有效性
     */
    @TableField("VALID_FLAG")
    private String validFlag;

    /**
     * 操作时间
     */
    @TableField("origin")
    private String origin;
    /**
     * 操作时间
     */
    @TableField("before_id")
    private String beforeId;
    /**
     * 操作时间
     */
    @TableField("before_name")
    private String beforeName;
    /**
     * 操作时间
     */
    @TableField("after_name")
    private String afterName;

    /**
     * 资源类型(0:FILE,1:FOLD)
     */
    @TableField("address_ip")
    private String addressIp;

    public String getAddressIp() {
        return addressIp;
    }

    public void setAddressIp(String addressIp) {
        this.addressIp = addressIp;
    }

    public String getAfterId() {
        return afterId;
    }

    public void setAfterId(String afterId) {
        this.afterId = afterId;
    }

    public String getBeforeId() {
        return beforeId;
    }

    public void setBeforeId(String beforeId) {
        this.beforeId = beforeId;
    }

    public String getBeforeName() {
        return beforeName;
    }

    public void setBeforeName(String beforeName) {
        this.beforeName = beforeName;
    }

    public String getAfterName() {
        return afterName;
    }

    public void setAfterName(String afterName) {
        this.afterName = afterName;
    }

    /**
     * 操作时间
     */
    @TableField("after_id")
    private String afterId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Timestamp getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Timestamp operateTime) {
        this.operateTime = operateTime;
    }

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "DocResourceLog{" +
        "id=" + id +
        ", resourceId=" + resourceId +
        ", resourceType=" + resourceType +
        ", userId=" + userId +
        ", operateType=" + operateType +
        ", operateTime=" + operateTime +
        "}";
    }
}
