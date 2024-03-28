/*
 * ApiBaseService.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.service;


import com.jxdinfo.doc.mobile.model.Response;

import java.text.ParseException;
import java.util.HashMap;

/**
 * Created by zlz on 2018/8/29.
 */
public interface ApiBaseService {

    String getBusinessID();

    Response execute(HashMap<String, String> params) throws ParseException;

}
