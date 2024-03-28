/*
 * MobileApp
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.model;

/**
 * @author huy
 * @version 1.0
 * @since 2017-05-11
 */
public class MobileApp {
    /**
     * 版本号
     */
    private String versionCode;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 升级说明
     */
    private String versionExplain;

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersionExplain() {
        return versionExplain;
    }

    public void setVersionExplain(String versionExplain) {
        this.versionExplain = versionExplain;
    }
}
