package com.jxdinfo.doc.interfaces.system.model;

public class HeadPhoto {

    /**
     * 用户Id
     */
    private String userid;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户头像
     */
    private String picture64;

    /**
     * 创建时间
     */
    private String createdate;
    /**
     * 用户Id
     */
    private String path;
    /**
     * md5
     */
    private String md5;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture64() {
        return picture64;
    }

    public void setPicture64(String picture64) {
        this.picture64 = picture64;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return picture64;
    }

}
