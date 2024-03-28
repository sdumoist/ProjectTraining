package com.jxdinfo.doc.manager.middlegroundConsulation.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @ClassName: MiddlegroundConsulationAttachment
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/2/14
 * @Version: 1.0
 */
@TableName("consulation_attachment")
public class MiddlegroundConsulationAttachment {
    /**
     * 组件ID
     */
    @TableId("attachment_id")
    private String attachmentId;
    /**
     * 组件名称
     */
    @TableField("consulation_id")
    private String consulationId;
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getConsulationId() {
        return consulationId;
    }

    public void setConsulationId(String consulationId) {
        this.consulationId = consulationId;
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
}
