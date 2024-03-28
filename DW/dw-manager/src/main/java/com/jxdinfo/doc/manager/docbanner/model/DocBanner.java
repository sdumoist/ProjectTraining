package com.jxdinfo.doc.manager.docbanner.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

@TableName("doc_banner_file")
public class DocBanner {
    @TableField("banner_id")
    private  String bannerId;
    @TableField("doc_id")
    private  String docId;
    @TableField("banner_href")
    private  String bannerHref;
    @TableField("create_time")
    private Timestamp createTime;
    @TableField("banner_path")
    private  String bannerPath;
    @TableField("show_order")
    private  Integer showOrder;
    @TableField("banner_name")
    private  String bannerName;
    @TableField("classify")
    private String classify;

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getBannerHref() {
        return bannerHref;
    }

    public void setBannerHref(String bannerHref) {
        this.bannerHref = bannerHref;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public String getClassify() {
        return classify;
    }

    public void setClassify(String classify) {
        this.classify = classify;
    }
}
