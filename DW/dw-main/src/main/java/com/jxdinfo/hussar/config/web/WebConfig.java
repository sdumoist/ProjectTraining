//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jxdinfo.hussar.config.web;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.BeanTypeAutoProxyCreator;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.jxdinfo.hussar.common.firewall.csrf.CsrfFilter;
import com.jxdinfo.hussar.common.firewall.xss.XssFilter;
import com.jxdinfo.hussar.config.properties.ConnectionPoolProperties;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.listener.ConfigListener;
import com.jxdinfo.hussar.isc.conf.ShiroIscConfiguration;
import com.jxdinfo.hussar.otp.credential.AbstractOTPCredentialsMatcher;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.catalina.Context;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class WebConfig {
    @Autowired
    private HussarProperties hussarProperties;
    @Autowired
    ConnectionPoolProperties connectionPoolProperties;
    @Autowired
    AbstractOTPCredentialsMatcher abstractOTPCredentialsMatcher;
    @Autowired
    ServerProperties serverProperties;
    @Autowired
    private ShiroIscConfiguration iscConf;

    public WebConfig() {
    }

    @Bean
    public ServletRegistrationBean<StatViewServlet> druidServletRegistration() {
        ServletRegistrationBean<StatViewServlet> registration = new ServletRegistrationBean(new StatViewServlet(), new String[0]);
        registration.addUrlMappings(new String[]{"/druid/*"});
        if (this.connectionPoolProperties.isNeedLogin()) {
            registration.addInitParameter("loginUsername", this.connectionPoolProperties.getLoginUsername());
            registration.addInitParameter("loginPassword", this.connectionPoolProperties.getLoginPassword());
        }

        return registration;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> druidStatFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter(), new ServletRegistrationBean[0]);
        filterRegistrationBean.addUrlPatterns(new String[]{"/*"});
        filterRegistrationBean.addInitParameter("exclusions", "/logout,/login,/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid,/druid/*");
        filterRegistrationBean.addInitParameter("principalSessionName", "username");
        return filterRegistrationBean;
    }

    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        String patterns = "com.jxdinfo.hussar.*.service.*";
        druidStatPointcut.setPatterns(new String[]{patterns});
        return druidStatPointcut;
    }

    @Bean
    public BeanTypeAutoProxyCreator beanTypeAutoProxyCreator() {
        BeanTypeAutoProxyCreator beanTypeAutoProxyCreator = new BeanTypeAutoProxyCreator();
        beanTypeAutoProxyCreator.setTargetBeanType(DruidDataSource.class);
        beanTypeAutoProxyCreator.setInterceptorNames(new String[]{"druidStatInterceptor"});
        return beanTypeAutoProxyCreator;
    }

    @Bean
    public Advisor druidStatAdvisor() {
        return new DefaultPointcutAdvisor(this.druidStatPointcut(), this.druidStatInterceptor());
    }

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        XssFilter xssFilter = new XssFilter();
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean(xssFilter, new ServletRegistrationBean[0]);
        registration.addUrlPatterns(new String[]{"/*"});
        xssFilter.setXssLevel(this.hussarProperties.getFirewallXssLevel());
        List<String> xssList = this.hussarProperties.getXssWhitelist();
        StringBuffer exclusions = new StringBuffer("/logout,/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico");
        StringBuffer pathChecks = new StringBuffer("*/sqlnet.log,*/sqlnet.trc,*/status.cgi,*.cgi,*.dll,*.sh,*.bat*,/servlet/viewsource.jsp,/cgi-bin/htgrep/*,*.asp,*.aspx,*php,*php5,*php4,*php3,*php2,*php1,*.swp");
        if (xssList != null && xssList.size() > 0) {
            Iterator var6 = xssList.iterator();

            while(var6.hasNext()) {
                String string = (String)var6.next();
                exclusions.append(",");
                exclusions.append(string);
            }
        }

        registration.addInitParameter("exclusions", exclusions.toString());
        List<String> xssBlackList = this.hussarProperties.getXssBlacklist();
        if (xssBlackList != null && xssBlackList.size() > 0) {
            Iterator var10 = xssBlackList.iterator();

            while(var10.hasNext()) {
                String string = (String)var10.next();
                pathChecks.append(",");
                pathChecks.append(string);
            }
        }

        registration.addInitParameter("pathChecks", pathChecks.toString());
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CsrfFilter> csrfFilterRegistration() {
        CsrfFilter csrfFilter = new CsrfFilter(this.abstractOTPCredentialsMatcher);
        csrfFilter.setCsrfcheck(this.hussarProperties.isCheckCsrfOpen());
        FilterRegistrationBean<CsrfFilter> registration = new FilterRegistrationBean(csrfFilter, new ServletRegistrationBean[0]);
        registration.addUrlPatterns(new String[]{"/*"});
        StringBuffer exclusions = new StringBuffer("/logout,/static/*,*.js,*.gif,*.jpg,*.png,*.css,*.ico");
        List<String> csrfList = this.hussarProperties.getCsrfWhitelist();
        if (csrfList != null && csrfList.size() > 0) {
            Iterator var5 = csrfList.iterator();

            while(var5.hasNext()) {
                String string = (String)var5.next();
                exclusions.append(",");
                exclusions.append(string);
            }
        }

        StringBuffer referExclusions = new StringBuffer();
        List<String> referList = this.hussarProperties.getReferWhitelist();
        if (this.iscConf.isActive()) {
            String iscServerUrl = this.iscConf.getServerUrlPrefix();
            referExclusions.append(",");
            referExclusions.append(iscServerUrl);
        }

        if (referList != null && referList.size() > 0) {
            Iterator var11 = referList.iterator();

            while(var11.hasNext()) {
                String string = (String)var11.next();
                referExclusions.append(",");
                referExclusions.append(string);
            }
        }

        csrfFilter.setRefererWhitelist(referExclusions.toString());
        registration.addInitParameter("exclusions", exclusions.toString());
        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<RequestContextListener> requestContextListenerRegistration() {
        return new ServletListenerRegistrationBean(new RequestContextListener());
    }

    @Bean
    public ServletListenerRegistrationBean<ConfigListener> configListenerRegistration() {
        return new ServletListenerRegistrationBean(new ConfigListener());
    }

    @Bean
    public DefaultKaptcha kaptcha() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/kaptcha.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        Config config = new Config(propertiesFactoryBean.getObject());
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @ConditionalOnClass({ServerProperties.class})
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                if (WebConfig.this.serverProperties != null) {
                    String portheader = WebConfig.this.serverProperties.getTomcat().getPortHeader();
                    String[] portheaders = portheader.split(",");
                    if (portheaders.length > 0) {
                        String[] var6 = portheaders;
                        int var7 = portheaders.length;

                        for(int var8 = 0; var8 < var7; ++var8) {
                            String string = var6[var8];
                            collection.addMethod(string.trim());
                        }
                    }
                }

                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer[]{(connector) -> {
            connector.setAllowTrace(true);
        }});
        return tomcat;
    }
}
