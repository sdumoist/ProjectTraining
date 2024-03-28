package com.jxdinfo.doc.manager.system.model;


import java.util.Date;

/**
 * 用户对象
 *
 * @Author liqui
 * @Date 2018-3-12
 * @Desc
 */

public class SysUserInfo  {
    private String id; //用户id
    private String username; //用户名
    private String password;//密码
    private String departId;//机构编号
    private String deptName;//机构名称
    private String employeeNo;//员工工号
    private Integer points;//积分
    private String certified;//认证
    private Date loginTime;//登录时间
    private char lock;//是否被锁定
    private String ip;//IP地址

    public SysUserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public SysUserInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDepartId() {
        return departId;
    }

    public void setDepartId(String departId) {
        this.departId = departId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getCertified() {
        return certified;
    }

    public void setCertified(String certified) {
        this.certified = certified;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public char getLock() {
        return lock;
    }

    public void setLock(char lock) {
        this.lock = lock;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
