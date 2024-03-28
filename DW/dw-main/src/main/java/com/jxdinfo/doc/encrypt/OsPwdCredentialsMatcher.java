/*
 * OsPwdCredentialsMatcher.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.encrypt;

import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.util.MD5Util;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;

/**
 * 类的用途：运营支撑密码加密存储算法匹配规则<p>
 * 创建日期：2018年9月4日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月4日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
@Repository
@Conditional(OsPwdCondition.class)
public class OsPwdCredentialsMatcher extends AbstractCredentialsMatcher {

    /**
     * MD5加密逻辑 转换为大写<<<<<
     * @Title: passwordEncode
     * @param plainByte
     * @return 
     */
    @Override
    public String passwordEncode(byte[] plainByte) {
        return MD5Util.encrypt(new String(plainByte)).toUpperCase();
    }

}
