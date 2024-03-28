package com.jxdinfo.doc.Synchronous.model;

import java.math.BigDecimal;
import java.util.Date;

public class SynchronousOrgan {
    private static final long serialVersionUID = 1L;

    // sys_stru表字段
    private String struId;
    private String struType;
    private String organId;
    private String organAlias;
    private String parentId;
    private String principalId;
    private String corporationId;
    private BigDecimal struLevel;
    private String struPath;
    private BigDecimal struOrder;
    private BigDecimal globalOrder;
    private String isLeaf;
    private String inUse;
    private String departmentId;
    private String permissionStruId;
    private String provinceCode;
    private String creator;
    private Date createTime;
    private String lastEditor;
    private Date lastTime;
    private String isEmployee;
    private String staffPosition;

    // sys_organ表字段
    private String organCode;
    private String organName;
    private String shortName;
    private String organType;
    private String workplaceId;
    private String beginDate;
    private String endDate;
    private BigDecimal scn;
    private String parentTypeCode;
    private String organInUse;
    private String organCreator;
    private Date organCreateTime;
    private String organLastEditor;
    private Date organLastTime;

    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getStruId() {
        return struId;
    }

    public void setStruId(String struId) {
        this.struId = struId;
    }

    public String getStruType() {
        return struType;
    }

    public void setStruType(String struType) {
        this.struType = struType;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOrganAlias() {
        return organAlias;
    }

    public void setOrganAlias(String organAlias) {
        this.organAlias = organAlias;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(String corporationId) {
        this.corporationId = corporationId;
    }

    public BigDecimal getStruLevel() {
        return struLevel;
    }

    public void setStruLevel(BigDecimal struLevel) {
        this.struLevel = struLevel;
    }

    public String getStruPath() {
        return struPath;
    }

    public void setStruPath(String struPath) {
        this.struPath = struPath;
    }

    public BigDecimal getStruOrder() {
        return struOrder;
    }

    public void setStruOrder(BigDecimal struOrder) {
        this.struOrder = struOrder;
    }

    public BigDecimal getGlobalOrder() {
        return globalOrder;
    }

    public void setGlobalOrder(BigDecimal globalOrder) {
        this.globalOrder = globalOrder;
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getInUse() {
        return inUse;
    }

    public void setInUse(String inUse) {
        this.inUse = inUse;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getPermissionStruId() {
        return permissionStruId;
    }

    public void setPermissionStruId(String permissionStruId) {
        this.permissionStruId = permissionStruId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastEditor() {
        return lastEditor;
    }

    public void setLastEditor(String lastEditor) {
        this.lastEditor = lastEditor;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getIsEmployee() {
        return isEmployee;
    }

    public void setIsEmployee(String isEmployee) {
        this.isEmployee = isEmployee;
    }

    public String getStaffPosition() {
        return staffPosition;
    }

    public void setStaffPosition(String staffPosition) {
        this.staffPosition = staffPosition;
    }

    public String getOrganCode() {
        return organCode;
    }

    public void setOrganCode(String organCode) {
        this.organCode = organCode;
    }

    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getOrganType() {
        return organType;
    }

    public void setOrganType(String organType) {
        this.organType = organType;
    }

    public String getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getScn() {
        return scn;
    }

    public void setScn(BigDecimal scn) {
        this.scn = scn;
    }

    public String getParentTypeCode() {
        return parentTypeCode;
    }

    public void setParentTypeCode(String parentTypeCode) {
        this.parentTypeCode = parentTypeCode;
    }

    public String getOrganInUse() {
        return organInUse;
    }

    public void setOrganInUse(String organInUse) {
        this.organInUse = organInUse;
    }

    public String getOrganCreator() {
        return organCreator;
    }

    public void setOrganCreator(String organCreator) {
        this.organCreator = organCreator;
    }

    public Date getOrganCreateTime() {
        return organCreateTime;
    }

    public void setOrganCreateTime(Date organCreateTime) {
        this.organCreateTime = organCreateTime;
    }

    public String getOrganLastEditor() {
        return organLastEditor;
    }

    public void setOrganLastEditor(String organLastEditor) {
        this.organLastEditor = organLastEditor;
    }

    public Date getOrganLastTime() {
        return organLastTime;
    }

    public void setOrganLastTime(Date organLastTime) {
        this.organLastTime = organLastTime;
    }

    @Override
    public String toString() {
        return "Organise{" +
                "struId=" + struId +
                ", struType=" + struType +
                ", organId=" + organId +
                ", organAlias=" + organAlias +
                ", parentId=" + parentId +
                ", principalId=" + principalId +
                ", corporationId=" + corporationId +
                ", struLevel=" + struLevel +
                ", struPath=" + struPath +
                ", struOrder=" + struOrder +
                ", globalOrder=" + globalOrder +
                ", isLeaf=" + isLeaf +
                ", inUse=" + inUse +
                ", departmentId=" + departmentId +
                ", permissionStruId=" + permissionStruId +
                ", provinceCode=" + provinceCode +
                ", createTime=" + createTime +
                ", lastEditor=" + lastEditor +
                ", lastTime=" + lastTime +
                ", isEmployee=" + isEmployee +
                ", staffPosition=" + staffPosition +
                ", organCode=" + organCode +
                ", organName=" + organName +
                ", shortName=" + shortName +
                ", organType=" + organType +
                ", workplaceId=" + workplaceId +
                ", beginDate=" + beginDate +
                ", endDate=" + endDate +
                ", scn=" + scn +
                ", parentTypeCode=" + parentTypeCode +
                ", organInUse=" + organInUse +
                ", organCreator=" + organCreator +
                ", organCreateTime=" + organCreateTime +
                "}";
    }
}


