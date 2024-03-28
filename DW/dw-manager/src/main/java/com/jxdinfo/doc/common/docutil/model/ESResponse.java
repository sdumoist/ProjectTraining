package com.jxdinfo.doc.common.docutil.model;

import java.util.ArrayList;
import java.util.List;

public class ESResponse<T> {
    /**
     * 标志
     */
    private boolean success;
    /**
     * 总条数
     */
    private Long total;
    /**
     * 文档列表
     */
    private List<T> items;
    /**
     * 信息
     */
    private String msg;
    /**
     * 总页数
     */
    private int  totalPages;

    private float maxScore;
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public float getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(float maxScore) {
        this.maxScore = maxScore;
    }

    public ESResponse() {
        this.success = true;
        this.items = new ArrayList<>();
    }

}
