package com.jxdinfo.doc.manager.docintegral.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * <p>
 * 积分记录
 * </p>
 *
 * @author yjs
 * @since 2018-12-3
 */
@TableName("doc_integral_record")
public class IntegralRecord extends Model<IntegralRecord> {
    /**
     * 主键，记录id
     */
    @TableField("record_id")
    String recordId;
    /**
     * 文档id
     */
    @TableField("doc_id")
    String docId;
    /**
     * 用户id
     */
    @TableField("user_id")
    String userId;
    /**
     * 积分规则编码
     */
    @TableField("operate_rule_code")
    String operateRuleCode;
    /**
     * 操作次数
     */
    @TableField("operate_time")
    Timestamp operateTime;
    /**
     * 积分状态
     */
    @TableField("integral_state")
    String IntegralState;
    /**
     * 积分数
     */
    @TableField("integral")
    Integer   Integral;
    /**
     * 规则名
     */
    @TableField("rule_name")
    String ruleName;
    /**
     * 规则描述
     */
    @TableField("rule_des")
    String ruleDes;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDes() {
        return ruleDes;
    }

    public void setRuleDes(String ruleDes) {
        this.ruleDes = ruleDes;
    }

    /**
     * 一对一映射 积分规则
     */
    @TableField(exist = false)
    Map integralRule;

    public Map<String, Object> getIntegralRule() {
        return integralRule;
    }

    public void setIntegralRule(Map<String, Object> integralRule) {
        this.integralRule = integralRule;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOperateRuleCode() {
        return operateRuleCode;
    }

    public void setOperateRuleCode(String operateRuleCode) {
        this.operateRuleCode = operateRuleCode;
    }

    public Timestamp getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Timestamp operateTime) {
        this.operateTime = operateTime;
    }

    public String getIntegralState() {
        return IntegralState;
    }

    public void setIntegralState(String integralState) {
        IntegralState = integralState;
    }

    public Integer getIntegral() {
        return Integral;
    }

    public void setIntegral(Integer integral) {
        Integral = integral;
    }

    @Override
    protected Serializable pkVal() {
        return this.recordId;
    }
}
