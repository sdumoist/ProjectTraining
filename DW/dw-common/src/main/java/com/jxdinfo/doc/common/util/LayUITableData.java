package com.jxdinfo.doc.common.util;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * @author
 * @Date 2018/3/16 0016
 */
public class LayUITableData {

    private long totalCount;//数据总数
    private String msg;//返回信息
    private int code;//编号
    private List<?> list;//列表数据

    public void setTotalCount(long totalCount){
        this.totalCount= totalCount;
    }

    public void setList(List<?> list){
        this.list = list;
    }

    public void setMsg(String msg1){ this.msg = msg1; }

    public void setCode(int code1){ this.code = code1; }

    @Override
    public String toString(){
        JSONObject json = new JSONObject();
        json.put("count", totalCount);
        json.put("data", list);
        json.put("msg",msg);
        json.put("code",code);
        return json.toString();
    }
}
