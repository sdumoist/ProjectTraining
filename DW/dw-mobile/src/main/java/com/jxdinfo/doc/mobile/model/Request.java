/*
 * Request
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.model;


import java.util.HashMap;

/**
 * 请求
 *
 * @author hy
 * @since 20170606
 */

public class Request {
    /**
     * 用户
     */
    private MobileUser user;
    /**
     * 设备
     */
    private MobileClient client;
    /**
     * 业务ID
     */
    private String businessID;
//    /**
//     * 参数
//     */
//    private List<String> params;
    /**
     * 参数
     */
    private HashMap<String,String> params;
    /**
     * 请求标记
     */
    private String tag;

    public MobileUser getUser() {
        return user;
    }

    public void setUser(MobileUser user) {
        this.user = user;
    }

    public MobileClient getClient() {
        return client;
    }

    public void setClient(MobileClient client) {
        this.client = client;
    }

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }

    public HashMap<String,String> getParams() {
        return params;
    }

    public void setParams(HashMap<String,String> params) {
        this.params = params;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
