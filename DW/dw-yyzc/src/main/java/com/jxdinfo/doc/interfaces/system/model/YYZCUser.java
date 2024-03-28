package com.jxdinfo.doc.interfaces.system.model;

/**
 * <p>
 * 
 * </p>
 *
 * @author xuxinying
 * @since 2018-06-27
 */
public class YYZCUser {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用戶id
     */
    private String userid;

    /**
     * 组织id
     */
    private String deptid;

    /**
     * 人员编码
     */
    private String usercode;

    /**
     * 人员名称
     */
    private String username;

    /**
     * byname
     */
    private String byname;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 排序
     */
    private Integer showorder;

    /**
     * 穿件日期
     */
    private String createdate;

    /**
     * 创建人员ID
     */
    private String createuserid;

    /**
     * 状态
     */
    private Integer datastatusid;

    /**
     * 性别
     */
    private String xb;

    /**
     * 员工编号
     */
    private String jobnumber;

    /**
     * 开始时间
     */
    private String starttime;

    /**
     * 结束时间
     */
    private String endtime;

    /**
     * maxsession
     */
    private Integer maxsession;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 公司ID
     */
    private String compid;

    /**
     * passwordexpires
     */
    private String passwordexpires;

    /**
     * signimgurl
     */
    private String signimgurl;

    /**
     * 加密
     */
    private String md5;

    /**
     * 联系方式
     */
    private String mobile;
    
    /**
     * 职务名称
     */
    private String zwmc;

    private String sfjqgc;
    
    /**
     * 职务排序
     */
    private String zworder;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDeptid() {
        return deptid;
    }

    public void setDeptid(String deptid) {
        this.deptid = deptid;
    }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getShoworder() {
        return showorder;
    }

    public void setShoworder(Integer showorder) {
        this.showorder = showorder;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid;
    }

    public Integer getDatastatusid() {
        return datastatusid;
    }

    public void setDatastatusid(Integer datastatusid) {
        this.datastatusid = datastatusid;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getJobnumber() {
        return jobnumber;
    }

    public void setJobnumber(String jobnumber) {
        this.jobnumber = jobnumber;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public Integer getMaxsession() {
        return maxsession;
    }

    public void setMaxsession(Integer maxsession) {
        this.maxsession = maxsession;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCompid() {
        return compid;
    }

    public void setCompid(String compid) {
        this.compid = compid;
    }

    public String getPasswordexpires() {
        return passwordexpires;
    }

    public void setPasswordexpires(String passwordexpires) {
        this.passwordexpires = passwordexpires;
    }

    public String getSignimgurl() {
        return signimgurl;
    }

    public void setSignimgurl(String signimgurl) {
        this.signimgurl = signimgurl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    
    

    public String getZwmc() {
		return zwmc;
	}

	public void setZwmc(String zwmc) {
		this.zwmc = zwmc;
	}

	public String getZworder() {
		return zworder;
	}

	public void setZworder(String zworder) {
		this.zworder = zworder;
	}

    public String getSfjqgc() {
        return sfjqgc;
    }

    public void setSfjqgc(String sfjqgc) {
        this.sfjqgc = sfjqgc;
    }

    @Override
    public String toString() {
        return userid + deptid + usercode + username + byname + passwd + showorder + createdate + createuserid
                + datastatusid + xb + jobnumber + starttime + endtime + maxsession + status + compid + passwordexpires
                + signimgurl + mobile+zworder+zwmc;
    }
}
