/*
 * Response
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.model;


/**
 * 类的用途：响应<p>
 * 创建日期：2017-6-12 <br>
 * 修改日期：2017-10-12 <br>
 * 修改作者：shiyongxun <br>
 * 修改内容：修改内容 <br>
 *
 * @author hy
 * @version 1.0
 */

public class Response {
    /**
     * 升级app
     */
    private MobileApp app;
    /**
     * 业务ID
     */
    private String businessID;
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 错误码
     */
    private String errcode;

    /**
     * 返回内容
     */
    private Object data;

    /**
     * 请求标记
     */
    private String tag;
    /**
     * 结果信息
     */
    private String msg;

    public String getTag() {
        return tag;
    }

    public Response() {
        super();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MobileApp getApp() {
        return app;
    }

    public void setApp(MobileApp app) {
        this.app = app;
    }

    public String getBusinessID() {
        return businessID;
    }

    public void setBusinessID(String businessID) {
        this.businessID = businessID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return businessID;
    }
}
