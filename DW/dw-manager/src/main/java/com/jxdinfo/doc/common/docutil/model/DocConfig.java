package com.jxdinfo.doc.common.docutil.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 文库配置表实体
 * @author wangning
 */
@TableName("doc_configure")
public class DocConfig extends Model<DocConfig> {
   
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;


	/**
     * configID
     */
    @TableId("config_id")
    private String configId;


    /**
     * 配置项key值
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置项value值
     */
    @TableField("config_value")
    private String configValue;

    public String getConfigId() {
		return configId;
	}

	public void setConfigId(String configId) {
		this.configId = configId;
	}

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

	@Override
    protected Serializable pkVal() {
        return getConfigId();
    }
}
