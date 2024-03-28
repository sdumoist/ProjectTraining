/*
 * 金现代轻骑兵V8开发平台 
 * ShiroProperties.java 
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com.jxdinfo.doc.common.jwt.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类的用途：<p>
 * 获取token配置
 * @author yjs
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = TokenProperties.PREFIX)
public class TokenProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "token";

    /**
     * 默认扫描的路径
     */
    private String defaultPath = "/client/**";

    /**
     * 排除扫描的路径
     */
    private String whitePath = "/client/login";

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String getWhitePath() {
        return whitePath;
    }

    public void setWhitePath(String whitePath) {
        this.whitePath = whitePath;
    }
}
