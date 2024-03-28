package com.jxdinfo.doc.unstructured.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

@TableName("platform_system_info")
public class PlatformSystemInfo extends Model<PlatformSystemInfo> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 系统id
     */
    @TableId("system_id")
    private String systemId;

    /**
     * 系统名称
     */
    @TableField("system_name")
    private String systemName;


    /**
     * 系统key密码
     */
    @TableField("system_key")
    private String systemKey;


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
     * 是否有效  1:有效 0:无效
     */
    @TableField("valid_flag")
    private String validFlag;
    
    /**
     * 当前操作者id
     */
    @TableField("create_user_id")
    private String createUserId;

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
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

    public String getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(String validFlag) {
        this.validFlag = validFlag;
    }

    public String getSystemKey() {
        return systemKey;
    }

    public void setSystemKey(String systemKey) {
        this.systemKey = systemKey;
    }

    @Override
    protected Serializable pkVal() {
        return this.systemId;
    }
}
