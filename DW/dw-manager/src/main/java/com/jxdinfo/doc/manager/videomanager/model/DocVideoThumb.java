package com.jxdinfo.doc.manager.videomanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * @ClassName: ResourceLog
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/11/13
 * @Version: 1.0
 */
@TableName("doc_video_thumb")
public class DocVideoThumb  extends Model<DocVideoThumb> {
    @TableId("doc_id")
    private String docId;
    @TableField("path")
    private String path;
    @TableField("path_key")
    private String pathKey;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathKey() {
        return pathKey;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }

    @Override
    protected Serializable pkVal() {
        return docId;
    }
}
