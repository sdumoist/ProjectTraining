/*
 * ApiConstant.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.client.response.constant;

/**
 * 类的用途：统一API常量<p>
 * 创建日期：2019年4月19日 <br>
 * 修改历史：<br>
 * 修改日期：2019年4月19日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public interface ApiConstant {

    /**
     * 默认为空消息
     */
    String DEFAULT_NULL_MESSAGE = "暂无承载数据";

    /**
     * 默认成功消息
     */
    String DEFAULT_SUCCESS_MESSAGE = "操作成功";

    /**
     * 默认失败消息
     */
    String DEFAULT_FAILURE_MESSAGE = "操作失败";

    /**
     * 默认未授权消息
     */
    String DEFAULT_UNAUTHORIZED_MESSAGE = "签名认证失败";
}
