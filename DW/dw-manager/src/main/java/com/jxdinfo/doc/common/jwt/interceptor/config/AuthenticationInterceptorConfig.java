package com.jxdinfo.doc.common.jwt.interceptor.config;


import com.jxdinfo.doc.common.jwt.interceptor.AuthenticationInterceptor;
import com.jxdinfo.doc.common.jwt.interceptor.MobileAuthenticationInterceptor;
import com.jxdinfo.doc.common.jwt.properties.TokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 文库客户端拦截器配置
 *
 * @author yjs
 */
@Configuration
public class AuthenticationInterceptorConfig implements WebMvcConfigurer {
    /**
     * shiroProperties
     */
    @Autowired
    private TokenProperties tokenProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //从配置文件读取defaultPath
        String defaultPath = tokenProperties.getDefaultPath();
        //从配置文件读取whitePath
        String whitePath = tokenProperties.getWhitePath();
        String[] whitePathAttr = whitePath.split(",");
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns(defaultPath);
        InterceptorRegistration interceptorRegistration2 =  registry.addInterceptor(mobileAuthenticationInterceptor())
                .addPathPatterns("/mobile/**");
        //循环排除白名单
        for (int i = 0; i < whitePathAttr.length; i++) {
            interceptorRegistration.excludePathPatterns(whitePathAttr[i]);
            interceptorRegistration2.excludePathPatterns(whitePathAttr[i]);
        }
        //给指定路径增加拦截器

    }

    @Bean
    public MobileAuthenticationInterceptor mobileAuthenticationInterceptor() {
        return new MobileAuthenticationInterceptor();
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}