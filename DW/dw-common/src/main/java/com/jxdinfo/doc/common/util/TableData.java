package com.jxdinfo.doc.common.util;



import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2018/1/18 0018.
 */
public class TableData {

    private long totalCount;
    private List<?> list;

    public void setTotalCount(long totalCount){
        this.totalCount= totalCount;
    }

    public void addRows(List<?> list){
        this.list = list;
    }
    @Override
    public String toString(){
        JSONObject json = new JSONObject();
        json.put("total", totalCount);
        json.put("rows", list);
        return json.toString();
    }
}
