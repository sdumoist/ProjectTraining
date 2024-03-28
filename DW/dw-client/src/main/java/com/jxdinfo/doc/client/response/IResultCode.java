/*
 * IResultCode.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.client.response;

import java.io.Serializable;

/**
 * 类的用途：业务代码接口<p>
 * 创建日期：2019年4月19日 <br>
 * 修改历史：<br>
 * 修改日期：2019年4月19日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public interface IResultCode extends Serializable {

    /**
     * 消息
     *
     * @return String
     */
    String getMessage();

    /**
     * 状态码
     *
     * @return int
     */
    int getCode();
}
