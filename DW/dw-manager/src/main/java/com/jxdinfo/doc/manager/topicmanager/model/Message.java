package com.jxdinfo.doc.manager.topicmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * 资讯印象
 * @author zhangzhen
 * @date 2018/4/9
 */
@TableName("doc_message")
public class Message  implements Serializable {
    /**
     * 主键
     */
    @TableId("message_id")
    private String  ARTICLEID;

    /**
     * fengmian
     */
    @TableField("message_title")
        private String TITLE;
    /**
     * 专题名称
     */
    @TableField("message_content")
    private String CONTENT;
    /**
     * 专题名称
     */
    @TableField("user_name")
    private String USERNAME;
    /**
     * 展示顺序
     */
    @TableField("create_time")
    private String  ADDTIME;

    public String getARTICLEID() {
        return ARTICLEID;
    }

    public void setARTICLEID(String ARTICLEID) {
        this.ARTICLEID = ARTICLEID;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(String CONTENT) {
        this.CONTENT = CONTENT;
    }

    public String getADDTIME() {
        return ADDTIME;
    }

    public void setADDTIME(String ADDTIME) {
        this.ADDTIME = ADDTIME;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }
}
