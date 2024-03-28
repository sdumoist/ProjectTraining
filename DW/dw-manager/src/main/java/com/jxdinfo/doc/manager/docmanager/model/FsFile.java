package com.jxdinfo.doc.manager.docmanager.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * <p>
 * 文件系统-文件
 * </p>
 *
 * @author smallcat
 * @since 2018-06-30
 */
@TableName("fs_file")
public class FsFile extends Model<FsFile> {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId("file_id")
    private String fileId;

    /**
     * 文件名
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件图标
     */
    @TableField("file_icon")
    private String fileIcon;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    /**
     * 修改时间
     */
    @TableField("file_size")
    private String fileSize;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;
    
    /**
     * PDF文件路径
     */
    @TableField("file_pdf_path")
    private String filePdfPath;

    /**
     * 文件路径
     */
    @TableField("pdf_key")
    private String pdfKey;

    /**
     * 文件路径
     */
    @TableField("md5")
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPdfKey() {
        return pdfKey;
    }

    public void setPdfKey(String pdfKey) {
        this.pdfKey = pdfKey;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    /**
     * PDF文件路径
     */
    @TableField("source_key")
    private String sourceKey;

    public String getFilePdfPath() {
		return filePdfPath;
	}

	public void setFilePdfPath(String filePdfPath) {
		this.filePdfPath = filePdfPath;
	}

	public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(String fileIcon) {
        this.fileIcon = fileIcon;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @TableField("size")
    private long size;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
	public String toString() {
		return "FsFile {fileId=" + fileId + ", fileName=" + fileName + ", fileIcon=" + fileIcon + ", fileType="
				+ fileType + ", createTime=" + createTime + ", fileSize=" + fileSize + ", filePath=" + filePath
				+ ", filePdfPath=" + filePdfPath + "}";
	}
}
