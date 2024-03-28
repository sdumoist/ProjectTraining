package com.jxdinfo.doc.knowledge.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 知识库表 实体类
 * @author cxk
 * @since 2021-05-12
 */
@TableName("qa_knowledge_base")
public class KnowledgeBase extends Model<KnowledgeBase>  {


    /**
     * 序列化
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId("KNOW_ID")
    private String knowId;

    /**
     * 问题标题
     */
    @TableField("TITLE")
    private String title;

    /**
     * 状态，0待回答，1已解决，2已删除
     */
    @TableField("STATE")
    private String state;

    /**
     * 标签
     */
    @TableField("LABEL")
    private String label;

    /**
     * 提问者ID
     */
    @TableField("INPUT_USER_ID")
    private String inputUserId;

    /**
     * 提问者姓名
     */
    @TableField("INPUT_USER_NAME")
    private String inputUserName;

    /**
     * 提问时间
     */
    @TableField("INPUT_TIME")
    private Timestamp inputTime;

    /**
     * 阅读数
     */
    @TableField("READ_NUM")
    private int readNum;

    /**
     * 录入类型（0：问题转入，1：主动录入）
     */
    @TableField("INPUT_TYPE")
    private String inputType;

    /**
     * 关联问题ID
     */
    @TableField("QUE_ID")
    private String queId;

    /**
     * 内容
     */
    @TableField("CONTENT")
    private String content;

    /**
     * 内容纯文本
     */
    @TableField("CONTENT_TEXT")
    private String contentText;

    public String getKnowId() {
        return knowId;
    }

    public void setKnowId(String knowId) {
        this.knowId = knowId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public String getInputUserName() {
        return inputUserName;
    }

    public void setInputUserName(String inputUserName) {
        this.inputUserName = inputUserName;
    }

    public Timestamp getInputTime() {
        return inputTime;
    }

    public void setInputTime(Timestamp inputTime) {
        this.inputTime = inputTime;
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getQueId() {
        return queId;
    }

    public void setQueId(String queId) {
        this.queId = queId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    @Override
    protected Serializable pkVal() {
        return this.knowId;
    }
}
