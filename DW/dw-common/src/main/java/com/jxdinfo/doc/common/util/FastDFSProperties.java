package com.jxdinfo.doc.common.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix="fastdfs")
@PropertySource(value="classpath:application-produce.yml")
public class FastDFSProperties {
    private boolean using;

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }
}
