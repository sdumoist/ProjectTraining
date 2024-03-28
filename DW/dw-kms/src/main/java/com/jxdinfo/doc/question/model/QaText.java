package com.jxdinfo.doc.question.model;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

@TableName("qa_text")
public class QaText extends Model<QaText> {

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
     *  类型，1问题，2回答
     */
    @TableField("TYPE")
    private String type;

    /**
     *  问答id
     */
    @TableField("QA_ID")
    private String qaId;

    /**
     *  文本内容
     */
    @TableField("CONTENT")
    private String content;

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

    public String getQaId() {
        return qaId;
    }

    public void setQaId(String qaId) {
        this.qaId = qaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
