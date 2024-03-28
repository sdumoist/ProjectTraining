package com.jxdinfo.doc.manager.groupmanager.model;/**
 * Created by zoufeng on 2018/9/27.
 */

import java.util.Date;

/**
 * @ClassName DocGroupSort
 * @Description 分组信息实体类
 * @Author zoufeng
 * @Date 2018/9/27 9:36
 * @Version 1.0
 **/
public class DocGroupSort {

    /** 分组id */
    private String sortId;

    /** 分组名称 */
    private String sortName;

    /** 父节点id */
    private String parentSortId;

    /** 层级码 */
    private String levelCode;

    /** 排序码 */
    private Integer showOrder;

    /** 创建人id*/
    private String createUserId;

    /** 创建时间*/
    private Date createTime;

    /** 群组标识*/
    private String groupFlag;

    public String getParentSortName() {
        return parentSortName;
    }

    public void setParentSortName(String parentSortName) {
        this.parentSortName = parentSortName;
    }

    /** 父节点名称 */
    private String parentSortName;

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getParentSortId() {
        return parentSortId;
    }

    public void setParentSortId(String parentSortId) {
        this.parentSortId = parentSortId;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(String groupFlag) {
        this.groupFlag = groupFlag;
    }
}
