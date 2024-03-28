package com.jxdinfo.doc.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取applicationContext工具类
 * @author
 * @Date 2018/3/26 0026
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringContextUtil.applicationContext == null){
            SpringContextUtil.applicationContext = applicationContext;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext(){
        return applicationContext;
    }
    //通过name获取bean
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
    //通过class获取bean
    public static <T>T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
    //通过name以及clazz返回指定的bean
    public static <T>T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name,clazz);
    }
}
