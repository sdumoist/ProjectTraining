package com.jxdinfo.doc.answer.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 问答日志表 实体类
 * @author sjw
 * @since 2021-02-22
 */
@TableName("qa_log")
public class QaLog extends Model<QaLog> {

    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("ID")
    private String id;

    /**
     * 类型，1问题，2回答，3评论回复，4追问，5追答, 6知识库
     */
    @TableField("TYPE")
    private String type;

    /**
     * 操作，1新增，2删除，3查看，4点赞，5关注，6取消关注，7分享，8取消分享，9补充，10修改，11结束，12设为最佳，13附件下载
     */
    @TableField("OPERATION")
    private String operation;

    /**
     * 数据ID
     */
    @TableField("DATA_ID")
    private String dataId;

    /**
     * 操作人ID
     */
    @TableField("USER_ID")
    private String userId;

    /**
     * 操作人姓名
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 数据
     */
    @TableField("TIME")
    private Timestamp time;

    /**
     * 状态，0正常，1删除
     */
    @TableField("STATE")
    private String state;

    /**
     * 操作人IP
     */
    @TableField("IP")
    private String ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
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

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
