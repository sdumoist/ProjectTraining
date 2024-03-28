package com.jxdinfo.doc.manager.collectionmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

/**
 * @ClassName: DocCollection
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/3/9
 * @Version: 1.0
 */
@TableName("doc_collection")
public class DocCollection {
    /**
     * 主键收藏ID
     */
    @TableId("collection_id")
    private String collectionId;
    /**
     * 收藏资源ID
     */
    @TableField("resource_id")
    private String resourceId;
    /**
     * 收藏资源类型
     */
    @TableField("resource_type")
    private String resourceType;
    /**
     * 收藏资源父级目录ID
     */
    @TableField("parent_folder_id")
    private String parentFolderId;
    /**
     * 收藏人ID
     */
    @TableField("create_user_id")
    private String createUserId;
    /**
     * 收藏时间
     */
    @TableField("create_time")
    private Timestamp createTime;
    /**
     * 修改时间
     */
    @TableField("update_time")
    private Timestamp updateTime;
    /**
     * 层级吗
     */
    @TableField("levelCode")
    private String levelCode;
    /**
     * 收藏资源Name
     */
    @TableField("resource_name")
    private String resourceName;
    /**
     * 收藏夹简介
     */
    @TableField("synopsis")
    private String synopsis;

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public void setParentFolderId(String parentFolderId) {
        this.parentFolderId = parentFolderId;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public String getLevelCode() {
        return levelCode;
    }

    public void setLevelCode(String levelCode) {
        this.levelCode = levelCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
}
