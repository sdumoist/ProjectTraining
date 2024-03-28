package com.jxdinfo.doc.manager.handovermanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

@TableName("doc_hand_over_attachment")
public class DocHandOverAttachment extends Model<DocHandOverAttachment> {
    /**
     * 交接ID
     */
    @TableField("attachment_id")
    private String attachmentId;
    @TableField("hand_over_id")
    private String handOverId;
    @TableField("resource_type")
    private Integer resourceType;
    @TableId("resource_id")
    private String resourceId;

    @TableField("resource_name")
    private String resourceName;

    public String getHandOverId() {
        return handOverId;
    }

    public void setHandOverId(String handOverId) {
        this.handOverId = handOverId;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    @Override
    protected Serializable pkVal() {
        return resourceId ;
    }
}
