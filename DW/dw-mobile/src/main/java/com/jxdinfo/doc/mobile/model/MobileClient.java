/*
 * MobileClient
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.model;


/**
 * @author huy
 * @version 1.0
 * @since 2017-05-11
 */
public class MobileClient {
    /**
     * 硬件编码
     */
    private String imei;
    /**
     * 分辨率
     */
    private String resolution;
    /**
     * sim卡的卡号
     */
    private String sim;
    /**
     * 设备类型
     */
    private String device;
    /**
     * 设备型号
     */
    private String deviceType;
    /**
     * 操作系统版本
     */
    private String osversion;
    /**
     * 应用版本
     */
    private String appVersion;
    /**
     * 设备标记
     */
    private String deviceTag;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSim() {
        return sim;
    }

    public void setSim(String sim) {
        this.sim = sim;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceTag() {
        return deviceTag;
    }

    public void setDeviceTag(String deviceTag) {
        this.deviceTag = deviceTag;
    }
}
