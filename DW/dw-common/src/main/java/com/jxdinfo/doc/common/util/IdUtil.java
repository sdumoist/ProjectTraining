/*
 * IdUtil.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.common.util;

import java.util.List;

/**
 * 类的用途：<p>
 * 创建日期：2018年7月10日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月10日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public class IdUtil {
    private IdUtil() {
    }

    /**
     * 
     * 将id集合转换为用'',''分隔
     * @Title: idListConverts 
     * @author: XuXinYing
     * @return String
     * @modify wangning
     */
    public static String idListConverts(List<String> idlist) {
    	if (idlist == null){
    		return "";
    	}
        return String.join(",", idlist);
    }

}
