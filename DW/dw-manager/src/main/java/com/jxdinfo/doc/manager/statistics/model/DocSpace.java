package com.jxdinfo.doc.manager.statistics.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * 部门空间实体类
 * @author yjs
 */
@TableName("doc_space")
public class DocSpace extends Model<DocSpace> {
    /**
     * 部门id
     */
    @TableId("organ_id")
    private String organId;


    /**
     * 部门总计空间
     */
    @TableField("space_size")
    private Double  spaceSize;

    public Double getSpaceSize() {
        return spaceSize;
    }

    public void setSpaceSize(Double spaceSize) {
        this.spaceSize = spaceSize;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }


    @Override
    protected Serializable pkVal() {
        return getOrganId();
    }
}
