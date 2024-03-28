package com.jxdinfo.doc.common.docutil.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DocES {
    /**
     * id
     */
    private String id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 标签
     */
    private String tags;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 文档类型
     */
    private String contentType;
    
    /**
     * 目录
     */
    private String readType;

    /**
     * 文档权属
     */
    private String owner;
    /**
     * 文档权属
     */
    private String folderId;
    
    /**
     * 期望积分值
     */
    private Float expectPoint;
    
    /**
     * 文档路径
     */
    private String filePath;
    
    /**
     * 文档简介
     */
    private String brief;
    
    /**
     * 是否允许预览，0不允许浏览，其它证书为允许浏览
     */
    private Integer allowPreview;
    
    /**
     * 是否允许下载，0不允许，1允许
     */
    private Integer allowDownload;
    
    /**
     * 上传时间
     */
    private Timestamp optTs;
    
    /**
     * 下载次数
     */
    private Integer downLoadNum;
    
    /**
     * 文件名称
     */
    private  String fileName;

    private  String titleSuggest;

    public String getTitleSuggest() {
        return titleSuggest;
    }

    public void setTitleSuggest(String titleSuggest) {
        this.titleSuggest = titleSuggest;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    /**
     * 文档总共的页数
     */
    private Integer pageCount;
    
    /**
     * 权限
     */
    private String[] permission;
    
    /**
     * 更新日期
     */
    private Date  upDate;
    
    /**
     * 是否在回收站 0表示不在回收站，1表示在回收站
     */
    private String  recycle;

    public Date getUpDate() {
        return upDate;
    }

    public void setUpDate(Date upDate) {
        this.upDate = upDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public int getAllowPreview() {
        return allowPreview;
    }

    public void setAllowPreview(int allowPreview) {
        this.allowPreview = allowPreview;
    }

    public int getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(int allowDownload) {
        this.allowDownload = allowDownload;
    }

    public Timestamp getOptTs() {
        return optTs;
    }

    public void setOptTs(Timestamp optTs) {
        this.optTs = optTs;
    }

    public int getDownLoadNum() {
        return downLoadNum;
    }

    public void setDownLoadNum(int downLoadNum) {
        this.downLoadNum = downLoadNum;
    }

    public float getExpectPoint() {
        return expectPoint;
    }

    public void setExpectPoint(float expectPoint) {
        this.expectPoint = expectPoint;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public String[] getPermission() {
        return permission;
    }

    public void setPermission(String[] permission) {
        this.permission = permission;
    }

    public String getRecycle() {
        return recycle;
    }

    public void setRecycle(String recycle) {
        this.recycle = recycle;
    }
    

    public String getReadType() {
        return readType;
    }

    public void setReadType(String readType) {
        this.readType = readType;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> ret = new HashMap<>();
         ret.put("upDate",this.upDate);
        ret.put("title",this.title);
        ret.put("category",this.category);
        ret.put("tags",this.tags);
        ret.put("pageCount",this.pageCount);
        ret.put("content",this.content);
        ret.put("contentType",this.contentType);
        ret.put("permission",this.permission);
        ret.put("recycle",this.recycle);
        ret.put("folderId",this.folderId);
        return ret;
    }

}
