package com.jxdinfo.doc.manager.professional.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 专业专职表
 * </p>
 *
 * @author cxk
 * @since 2021-05-08
 */
@TableName("qa_professional")
public class Professional extends Model<Professional> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId("ID")
    private String id;
    /**
     * 专职人员ID
     */
    @TableField("User_ID")
    private String userId;
    /**
     * 专职人员姓名
     */
    @TableField("User_Name")
    private String userName;
    /**
     * 对应专业
     */
    @TableField("major")
    private String major;
    /**
     * 对应专业ID(数据字典)
     */
    @TableField("major_ID")
    private String majorId;
    /**
     * 排序
     */
    @TableField("show_order")
    private Integer showOrder;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMajorId() {
        return majorId;
    }

    public void setMajorId(String majorId) {
        this.majorId = majorId;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Professional{" +
        "id=" + id +
        ", userId=" + userId +
        ", userName=" + userName +
        ", major=" + major +
        ", majorId=" + majorId +
        ", showOrder=" + showOrder +
        "}";
    }
}
