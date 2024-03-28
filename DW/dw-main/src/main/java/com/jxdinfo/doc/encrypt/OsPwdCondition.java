/*
 * OsPwdOperation.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.encrypt;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 类的用途：运营支撑密码加密匹配规则<p>
 * 创建日期：2018年9月4日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月4日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public class OsPwdCondition implements Condition{

    /**
     * 运营支撑密码加密存储算法
     * @Title: matches
     * @param context
     * @param metadata
     * @return 
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String type = context.getEnvironment().getProperty("hussar.encryptType.db-encrypt-type");
        return "ospwd".equalsIgnoreCase(type);
    }

}
