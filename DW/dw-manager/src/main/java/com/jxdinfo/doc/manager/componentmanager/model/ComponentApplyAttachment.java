package com.jxdinfo.doc.manager.componentmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 附件实体类
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@TableName("component_apply_attachment")
public class ComponentApplyAttachment extends Model<ComponentApplyAttachment> {
    private static final long serialVersionUID = 1L;

    /**
     * 组件ID
     */
    @TableId("attachment_id")
    private String attachmentId;
    /**
     * 组件名称
     */
    @TableField("component_id")
    private String componentId;
    /**
     * 组件描述
     */
    @TableField("attachment_type")
    private String attachmentType;
    /**
     * 用户ID
     */
    @TableField("attachment_name")
    private String attachmentName;
    @TableField(exist = false)
    private String fileType;

    @TableField(exist = false)
    private String pdfPath;

    @TableField(exist = false)
    private double  height;

    @TableField(exist = false)
    private double width;

    @TableField(exist = false)
    private String fileSize;

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    protected Serializable pkVal() {
        return attachmentId;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}
