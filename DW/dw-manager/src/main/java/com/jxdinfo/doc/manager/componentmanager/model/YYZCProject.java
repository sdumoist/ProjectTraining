package com.jxdinfo.doc.manager.componentmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 部门项目实体类
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@TableName("yyzc_project")
public class YYZCProject extends Model<YYZCProject> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId("project_id")
    private String PORJECTID;

    /**
     * 项目名称
     */
    @TableField("project_name")
    private String PROJECTNAME;

    /**
     * 项目所属部门
     */
    @TableField("project_dept")
    private String PROJECTDEPT;

    /**
     * 复用简介
     */
    @TableField("project_user")
    private String PROJECTUSER;

    /**
     * 修改时间
     */
    @TableField("create_time")
    private String  CREATEDATE;
    /**
     * 修改时间
     */
    @TableField("project_dept_id")
    private String  PROJECTDEPTID;

    public String getPROJECTDEPTID() {
        return PROJECTDEPTID;
    }

    public void setPROJECTDEPTID(String PROJECTDEPTID) {
        this.PROJECTDEPTID = PROJECTDEPTID;
    }

    public String getPORJECTID() {
        return PORJECTID;
    }

    public void setPORJECTID(String PORJECTID) {
        this.PORJECTID = PORJECTID;
    }

    public String getPROJECTNAME() {
        return PROJECTNAME;
    }

    public void setPROJECTNAME(String PROJECTNAME) {
        this.PROJECTNAME = PROJECTNAME;
    }

    public String getPROJECTDEPT() {
        return PROJECTDEPT;
    }

    public void setPROJECTDEPT(String PROJECTDEPT) {
        this.PROJECTDEPT = PROJECTDEPT;
    }

    public String getPROJECTUSER() {
        return PROJECTUSER;
    }

    public void setPROJECTUSER(String PROJECTUSER) {
        this.PROJECTUSER = PROJECTUSER;
    }

    public String getCREATEDATE() {
        return CREATEDATE;
    }

    public void setCREATEDATE(String CREATEDATE) {
        this.CREATEDATE = CREATEDATE;
    }

    @Override
    protected Serializable pkVal() {
        return PORJECTID;
    }
}
