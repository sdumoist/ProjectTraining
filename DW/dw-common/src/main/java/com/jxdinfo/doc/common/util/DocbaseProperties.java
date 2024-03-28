package com.jxdinfo.doc.common.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix="docbase")
@PropertySource(value="classpath:application-produce.yml")
public class DocbaseProperties {
    private String tempdir;

    public String getTempdir() {
        return tempdir;
    }

    public void setTempdir(String tempdir) {
        this.tempdir = tempdir;
    }
}
