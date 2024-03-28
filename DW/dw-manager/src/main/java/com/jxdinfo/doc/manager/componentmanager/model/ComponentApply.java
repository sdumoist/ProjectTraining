package com.jxdinfo.doc.manager.componentmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 科研成果实体类
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@TableName("component_apply")
public class ComponentApply extends Model<ComponentApply> {

    private static final long serialVersionUID = 1L;

    /**
     * 组件ID
     */
    @TableId("component_id")
    private String componentId;
    /**
     * 组件名称
     */
    @TableField("component_name")
    private String componentName;
    /**
     * 组件描述
     */
    @TableField("component_desc")
    private String componentDesc;
    @TableField("component_desc_text")
    private String componentDescText;

    public String getComponentDescText() {
        return componentDescText;
    }

    public void setComponentDescText(String componentDescText) {
        this.componentDescText = componentDescText;
    }
    /**
     * 用户ID
     */
    @TableField("dept_id")
    private String deptId;
    /**
     * 用户名
     */
    @TableField("dept_name")
    private String deptName;

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

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 用户ID
     */
    @TableField("tags")
    private String tags;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 组件类型
     */
    @TableField("component_type")
    private Integer componentType;

    /**
     * 组件类型
     */
    @TableField("publish_time")
    private Timestamp publishTime;

    public Timestamp getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Timestamp publishTime) {
        this.publishTime = publishTime;
    }

    /**
     * 创建时间
     */
    @TableField("component_state")
    private Integer componentState;
    /**
     * 更新人
     */
    @TableField("return_reasons")
    private String returnReasons;

    /**
     * 更新人
     */
    @TableField("return_reasons_wyh")
    private String returnReasonsWyh;

    /**
     * 更新人
     */
    @TableField("return_user_id")
    private String returnUserId;

    public String getReturnUserId() {
        return returnUserId;
    }

    public void setReturnUserId(String returnUserId) {
        this.returnUserId = returnUserId;
    }


    /**
     * 更新人
     */
    @TableField("return_dept_name")
    private String returnDeptName;
    public String getReturnDeptName() {
        return returnDeptName;
    }

    public void getReturnDeptName(String returnDeptName) {
        this.returnDeptName = returnDeptName;
    }
    @TableField("why_user_id")
    private String whyUserId;



    /**
     * 更新人
     */
    @TableField("why_dept_name")
    private String wyhDeptName;

    public void setReturnDeptName(String returnDeptName) {
        this.returnDeptName = returnDeptName;
    }

    public String getWhyUserId() {
        return whyUserId;
    }

    public void setWhyUserId(String whyUserId) {
        this.whyUserId = whyUserId;
    }

    public String getWyhDeptName() {
        return wyhDeptName;
    }

    public void setWyhDeptName(String wyhDeptName) {
        this.wyhDeptName = wyhDeptName;
    }

    public String getReturnReasonsWyh() {
        return returnReasonsWyh;
    }

    public void setReturnReasonsWyh(String returnReasonsWyh) {
        this.returnReasonsWyh = returnReasonsWyh;
    }

    /**
     * 修改时间
     */
    @TableField("update_time")
    private Timestamp update_time;
    /**
     * 修改时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    @TableField(exist = false)
    private String projectName;
    @TableField(exist = false)
    private String showTime;
    @TableField(exist = false)
    private String projectDept;

    @TableField(exist = false)
    private String isProject;
    /**
     * 部门
     */
    @TableField(exist = false)
    private String economize;


    @TableField(exist = false)
    private String organAlias;


    @TableField(exist = false)
    private String organAliasBu;
    @TableField(exist = false)
    private String orgId;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * 数量
     */
    @TableField(exist = false)
    private String re_num;
    /**
     * 组件类型
     */
    @TableField("read_num")
    private Integer readNum;
    public String getRe_num() {
        return re_num;
    }

    public Integer getReadNum() {
        return readNum;
    }

    public void setReadNum(Integer readNum) {
        this.readNum = readNum;
    }

    public void setRe_num(String re_num) {
        this.re_num = re_num;
    }

    public String getOrganAlias() {
        return organAlias;
    }

    public String getOrganAliasBu() {
        return organAliasBu;
    }

    public void setOrganAliasBu(String organAliasBu) {
        this.organAliasBu = organAliasBu;
    }

    public void setOrganAlias(String organAlias) {
        this.organAlias = organAlias;
    }

    /**
     * 项目来源
     */
    @TableField("component_origin")
    private Integer componentOrigin;

    /**
     * 应用场景
     */
    @TableField("component_range")
    private String componentRange;

    /**
     * 委员会评价
     */
    @TableField("appraise")
    private String appraise;

    /**
     * 是否为发布奖(0-否,1-是)
     */
    @TableField("publish_flag")
    private String publishFlag;

    /**
     * 是否为汇总奖(0-否,1-是)
     */
    @TableField("collect_flag")
    private String collectFlag;

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise;
    }

    public String getComponentRange() {
        return componentRange;
    }

    public void setComponentRange(String componentRange) {
        this.componentRange = componentRange;
    }

    public String getIsProject() {
        return isProject;
    }

    public void setIsProject(String isProject) {
        this.isProject = isProject;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDept() {
        return projectDept;
    }

    public void setProjectDept(String projectDept) {
        this.projectDept = projectDept;
    }



    public Integer getComponentOrigin() {
        return componentOrigin;
    }

    public void setComponentOrigin(Integer componentOrigin) {
        this.componentOrigin = componentOrigin;
    }

    @Override
    protected Serializable pkVal() {
        return componentId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentDesc() {
        return componentDesc;
    }

    public void setComponentDesc(String componentDesc) {
        this.componentDesc = componentDesc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getComponentType() {
        return componentType;
    }

    public void setComponentType(Integer componentType) {
        this.componentType = componentType;
    }

    public Integer getComponentState() {
        return componentState;
    }

    public void setComponentState(Integer componentState) {
        this.componentState = componentState;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getReturnReasons() {
        return returnReasons;
    }

    public void setReturnReasons(String returnReasons) {
        this.returnReasons = returnReasons;
    }

    public Timestamp getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Timestamp update_time) {
        this.update_time = update_time;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEconomize() {
        return economize;
    }

    public void setEconomize(String economize) {
        this.economize = economize;
    }

    public String getPublishFlag() {
        return publishFlag;
    }

    public void setPublishFlag(String publishFlag) {
        this.publishFlag = publishFlag;
    }

    public String getCollectFlag() {
        return collectFlag;
    }

    public void setCollectFlag(String collectFlag) {
        this.collectFlag = collectFlag;
    }
}
