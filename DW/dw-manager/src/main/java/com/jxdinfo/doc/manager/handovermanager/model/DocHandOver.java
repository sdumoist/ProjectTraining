package com.jxdinfo.doc.manager.handovermanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

@TableName("doc_hand_over")
public class DocHandOver extends Model<DocHandOver> {

    /**
     * 交接ID
     */
    @TableId("hand_over_id")
    private String handOverId;
    /**
     * 交接名称
     */

    @TableField("hand_over_date")
    private Timestamp handOverDate;
    @TableField("hand_over_type")
    private Integer handOverType;
    @TableField("accept_id")
    private String acceptId;
    @TableField("hand_over_user_id")
    private String handOverUserId;
    @TableField("hand_over_user_name")
    private String handOverUserName;
    @TableField("hand_over_dept_id")
    private String handOverDeptId;
    @TableField("hand_over_dept_name")
    private String handOverDeptName;
    @TableField("hand_over_state")
    private Integer handOverState;
    @TableField("accept_name")
    private String acceptName;
    @TableField("accept_dept_id")
    private String acceptDeptId;
    @TableField("accept_dept_name")
    private String acceptDeptName;
    @TableField(exist = false)
    private Integer num;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getHandOverId() {
        return handOverId;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getAcceptDeptId() {
        return acceptDeptId;
    }

    public void setAcceptDeptId(String acceptDeptId) {
        this.acceptDeptId = acceptDeptId;
    }

    public String getAcceptDeptName() {
        return acceptDeptName;
    }

    public void setAcceptDeptName(String acceptDeptName) {
        this.acceptDeptName = acceptDeptName;
    }

    public void setHandOverId(String handOverId) {
        this.handOverId = handOverId;
    }


    public Timestamp getHandOverDate() {
        return handOverDate;
    }

    public void setHandOverDate(Timestamp handOverDate) {
        this.handOverDate = handOverDate;
    }

    public Integer getHandOverType() {
        return handOverType;
    }

    public void setHandOverType(Integer handOverType) {
        this.handOverType = handOverType;
    }

    public String getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(String acceptId) {
        this.acceptId = acceptId;
    }

    public String getHandOverUserId() {
        return handOverUserId;
    }

    public void setHandOverUserId(String handOverUserId) {
        this.handOverUserId = handOverUserId;
    }

    public String getHandOverUserName() {
        return handOverUserName;
    }

    public void setHandOverUserName(String handOverUserName) {
        this.handOverUserName = handOverUserName;
    }

    public String getHandOverDeptId() {
        return handOverDeptId;
    }

    public void setHandOverDeptId(String handOverDeptId) {
        this.handOverDeptId = handOverDeptId;
    }

    public String getHandOverDeptName() {
        return handOverDeptName;
    }

    public void setHandOverDeptName(String handOverDeptName) {
        this.handOverDeptName = handOverDeptName;
    }

    public Integer getHandOverState() {
        return handOverState;
    }

    public void setHandOverState(Integer handOverState) {
        this.handOverState = handOverState;
    }

    @Override
    protected Serializable pkVal() {
        return handOverId;
    }
}
