/*
 * 版权所有：金现代信息产业股份有限公司 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.common.util;

import java.math.BigDecimal;

/**
 * 类的用途：Math工具类 <br>
 * 创建日期：2018-9-5 <br>
 * 修改历史：<br>
 * 修改日期：2018-9-5 <br>
 * 修改作者：wangning <br>
 * 修改内容：修改内容 <br>
 * @author wangning
 * @version 1.0
 */
public class MathUtil{
	
    /**
     * 
     * 保留小数
     * @Title getDecimal 
     * @param num 要转换的数值
     * @param decimal 要保留的位数
     * @author wangning
     * @return double
     */
    public static double getDecimal(double num, int decimal) {
    	BigDecimal bg = new BigDecimal(num);  
        return bg.setScale(decimal, BigDecimal.ROUND_HALF_UP).doubleValue(); 
    }
}
