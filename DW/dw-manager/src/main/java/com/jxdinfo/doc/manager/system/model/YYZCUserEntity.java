package com.jxdinfo.doc.manager.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xuxinying
 * @since 2018-06-27
 */
@TableName("jxd7_xt_user")
public class YYZCUserEntity extends Model<FsFolder> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用戶id
     */
    @TableId("USERID")
    private String userid;

    /**
     * 组织id
     */
    @TableField("DEPTID")
    private String deptid;

    /**
     * 人员编码
     */
    @TableField("USERCODE")
    private String usercode;

    /**
     * 人员名称
     */
    @TableField("USERNAME")
    private String username;

    /**
     * byname
     */
    @TableField("BYNAME")
    private String byname;

    /**
     * 密码
     */
    @TableField("PASSWD")
    private String passwd;

    /**
     * 排序
     */
    @TableField("SHOWORDER")
    private Integer showorder;

    /**
     * 穿件日期
     */
    @TableField("CREATEDATE")
    private String createdate;

    /**
     * 创建人员ID
     */
    @TableField("CREATEUSERID")
    private String createuserid;

    /**
     * 状态
     */
    @TableField("DATASTATUSID")
    private Integer datastatusid;

    /**
     * 性别
     */
    @TableField("XB")
    private String xb;

    /**
     * 员工编号
     */
    @TableField("JOBNUMBER")
    private String jobnumber;

    /**
     * 开始时间
     */
    @TableField("STARTTIME")
    private String starttime;

    /**
     * 结束时间
     */
    @TableField("ENDTIME")
    private String endtime;

    /**
     * maxsession
     */
    @TableField("MAXSESSION")
    private Integer maxsession;

    /**
     * 状态
     */
    @TableField("STATUS")
    private Integer status;

    /**
     * 公司ID
     */
    @TableField("COMPID")
    private String compid;

    /**
     * passwordexpires
     */
    @TableField("PASSWORDEXPIRES")
    private String passwordexpires;

    /**
     * signimgurl
     */
    @TableField("SIGNIMGURL")
    private String signimgurl;

    /**
     * 加密
     */
    @TableField("MD5")
    private String md5;

    /**
     * 联系方式
     */
    @TableField("MOBILE")
    private String mobile;
    
    /**
     * 职务名称
     */
    @TableField("ZWMC")
    private String zwmc;
    
    /**
     * 职务排序
     */
    @TableField("ZWORDER")
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

	@Override
    public String toString() {
        return userid + deptid + usercode + username + byname + passwd + showorder + createdate + createuserid
                + datastatusid + xb + jobnumber + starttime + endtime + maxsession + status + compid + passwordexpires
                + signimgurl + mobile+zworder+zwmc;
    }

    @Override
    protected Serializable pkVal() {
        return userid;
    }
}
