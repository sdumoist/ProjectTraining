package com.jxdinfo.doc.manager.docmanager.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
@TableName("doc_file_authority")
public class DocFileAuthority extends Model<DocFileAuthority> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId("FILE_AUTHORITY_ID")
    private String fileAuthorityId;
    /**
     * 文件ID
     */
    @TableField("FILE_ID")
    private String fileId;
    /**
     * 操作者ID
     */
    @TableField("AUTHOR_ID")
    private String authorId;
    /**
     * 操作者类型（0：userID,1:groupID,2:roleID）	
     */
    @TableField("AUTHOR_TYPE")
    private Integer authorType;
    /**
     * 是否可编辑
     */
    @TableField("AUTHORITY")
    private Integer authority;

    /**
     * user在组织机构中的id
     */
    @TableField("organ_id")
    private String organId;


    @Override
    protected Serializable pkVal() {
        return null;
    }

    public String getFileAuthorityId() {
        return fileAuthorityId;
    }

    public void setFileAuthorityId(String fileAuthorityId) {
        this.fileAuthorityId = fileAuthorityId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Integer getAuthorType() {
        return authorType;
    }

    public void setAuthorType(Integer authorType) {
        this.authorType = authorType;
    }

    public Integer getAuthority() {
        return authority;
    }

    public void setAuthority(Integer authority) {
        this.authority = authority;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }
}
