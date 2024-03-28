package com.jxdinfo.doc.manager.docmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

@TableName("doc_delete")
public class DocDelete {
    /**
     * 文档ID
     */
    @TableId("file_id")
    private String fileId;

    /**
     * 上传者ID
     */
    @TableField("file_path")
    private String filePath;


    /**
     * 创建时间
     */
    @TableField("create_time")
    private Timestamp createTime;

    public String getFileId() {
        return fileId;
    }
    /**
     * 上传者ID
     */
    @TableField("server_address")
    private String serverAddress;



    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
}
