package com.jxdinfo.doc.manager.docconfigure.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * @ClassName DocConfigure
 * @Description 通用功能配置表
 * @Author zoufeng
 * @Date 2018/10/15 11:15
 * @Version 1.0
 **/
@TableName("doc_configure")
public class DocConfigure extends Model<DocConfigure> {

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /** 配置文件主键 */
    @TableId("CONFIG_ID")
    public String id;

    /** 配置文件key */
    @TableField("CONFIG_KEY")
    public String configKey;

    /** 配置文件value */
    @TableField("CONFIG_VALUE")
    public String configValue;

    /** 是否启用标识 */
    @TableField("CONFIG_VALID_FLAG")
    public String configValidFlag;



    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigValidFlag() {
        return configValidFlag;
    }

    public void setConfigValidFlag(String configValidFlag) {
        this.configValidFlag = configValidFlag;
    }
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
