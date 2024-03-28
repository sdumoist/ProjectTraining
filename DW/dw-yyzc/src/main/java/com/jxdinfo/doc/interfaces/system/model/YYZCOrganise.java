package com.jxdinfo.doc.interfaces.system.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("jxd7_xt_organise")
public class YYZCOrganise {
    @TableField("ORGANISEID")
    private String organiseid;
    @TableField("ORGANISENAME")
    private String organisename;
    @TableField("ORGANISEKINDID")
    private Integer organisekindid;
    @TableField("ORGANISELEVEL")
    private String organiselevel;
    @TableField("PREORGANISEID")
    private String preorganiseid;
    @TableField("ORGANISETYPE")
    private Integer organisetype;
    @TableField("CREATEUSERID")
    private String createuserid;
    @TableField("CREATEDATE")
    private String createdate;
    @TableField("DATASTATUSID")
    private Integer datastatusid;
    @TableField("SHOWORDER")
    private Integer showorder;
    @TableField("SHORTNAME")
    private String shortname;
    @TableField("STARTTIME")
    private String starttime;
    @TableField("ENDTIME")
    private String endtime;
    @TableField("MD5")
    private String md5;


    public String getOrganiseid() {
        return organiseid;
    }

    public void setOrganiseid(String organiseid) {
        this.organiseid = organiseid;
    }

    public String getOrganisename() {
        return organisename;
    }

    public void setOrganisename(String organisename) {
        this.organisename = organisename;
    }

    public Integer getOrganisekindid() {
        return organisekindid;
    }

    public void setOrganisekindid(Integer organisekindid) {
        this.organisekindid = organisekindid;
    }

    public String getOrganiselevel() {
        return organiselevel;
    }

    public void setOrganiselevel(String organiselevel) {
        this.organiselevel = organiselevel;
    }

    public String getPreorganiseid() {
        return preorganiseid;
    }

    public void setPreorganiseid(String preorganiseid) {
        this.preorganiseid = preorganiseid;
    }

    public Integer getOrganisetype() {
        return organisetype;
    }

    public void setOrganisetype(Integer organisetype) {
        this.organisetype = organisetype;
    }

    public String getCreateuserid() {
        return createuserid;
    }

    public void setCreateuserid(String createuserid) {
        this.createuserid = createuserid;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public Integer getDatastatusid() {
        return datastatusid;
    }

    public void setDatastatusid(Integer datastatusid) {
        this.datastatusid = datastatusid;
    }

    public Integer getShoworder() {
        return showorder;
    }

    public void setShoworder(Integer showorder) {
        this.showorder = showorder;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
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

    public String getMD5() {
        return md5;
    }

    public void setMD5(String md5) {
        this.md5 = md5;
    }


    @Override
    public String toString() {
        return "Jxd7XtOrganise{" +
                "organiseid=" + organiseid +
                ", organisename=" + organisename +
                ", organisekindid=" + organisekindid +
                ", organiselevel=" + organiselevel +
                ", preorganiseid=" + preorganiseid +
                ", organisetype=" + organisetype +
                ", createuserid=" + createuserid +
                ", createdate=" + createdate +
                ", datastatusid=" + datastatusid +
                ", showorder=" + showorder +
                ", shortname=" + shortname +
                ", starttime=" + starttime +
                ", endtime=" + endtime +
                "}";
    }
}

