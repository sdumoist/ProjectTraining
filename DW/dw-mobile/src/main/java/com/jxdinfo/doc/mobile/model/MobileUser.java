/*
 * MobileUser
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.model;

/**
 * @author huy
 * @version 1.0
 * @since 2017-05-11
 */
public class MobileUser {
    protected String _c = "com.sdjxd.hussar.core.permit72.bo.support.UserBo";
    /**
     * 用户ID
     */
    protected String uid;
    /**
     * 用户名称
     */
    protected String name;
    /**
     * 用户编码
     */
    protected String code;
    /**
     * 部门ID
     */
    protected String deptId;
    /**
     * 部门名称
     */
    protected String deptName;
    /**
     * 公司ID
     */
    protected String compId;
    /**
     * 公司名称
     */
    protected String compName;
    /**
     * 密码
     */
    protected String password;
    /**
     * 工号
     */
    protected String jobNum;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCompId() {
        return compId;
    }

    public void setCompId(String compId) {
        this.compId = compId;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }
}
