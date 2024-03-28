//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jxdinfo.hussar.common.firewall.csrf;

import com.jxdinfo.hussar.core.filter.HussarPathMatcher;
import com.jxdinfo.hussar.core.filter.PatternMatcher;
import com.jxdinfo.hussar.otp.credential.AbstractOTPCredentialsMatcher;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsrfFilter implements Filter {
    private static Logger LOGGER = LoggerFactory.getLogger(CsrfFilter.class);
    private Boolean csrfcheck = true;
    private String refererWhitelist = "";
    private AbstractOTPCredentialsMatcher abstractOTPCredentialsMatcher;
    private Set<String> excludesPattern;
    protected String contextPath;
    protected PatternMatcher pathMatcher = HussarPathMatcher.getInstance();
    public static final String PARAM_NAME_EXCLUSIONS = "exclusions";

    public CsrfFilter(AbstractOTPCredentialsMatcher abstractOTPCredentialsMatcher) {
        this.abstractOTPCredentialsMatcher = abstractOTPCredentialsMatcher;
    }

    public void init(FilterConfig config) throws ServletException {
        String exclusions = config.getInitParameter("exclusions");
        if (exclusions != null && exclusions.trim().length() != 0) {
            this.excludesPattern = new HashSet(Arrays.asList(exclusions.split("\\s*,\\s*")));
        }

        this.contextPath = this.getContextPath(config.getServletContext());
    }

    private String getContextPath(ServletContext servletContext) {
        String contextPath = servletContext.getContextPath();
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = "/";
        }

        return contextPath;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        String referer = httpRequest.getHeader("Referer");
        String requestUri = httpRequest.getRequestURI();
        if (!this.csrfcheck) {
            LOGGER.error("当前CSRF过滤器已经关闭，不进行任何安全校验，当前请求URI为:" + requestUri + "\nREFERER" + referer);
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            httpResponse.addHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-XSS-Protection", "1;mode=block");
            String schme = httpRequest.getScheme();
            String serverName = httpRequest.getServerName();
            int serverPort = httpRequest.getServerPort();
            String realpath = schme + "://" + serverName + ((!"http".equals(schme) || serverPort != 80) && (!"https".equals(schme) || serverPort != 443) ? ":" + serverPort : "");
            if (this.isExclusion(requestUri)) {
                chain.doFilter(request, response);
            } else {
                if (referer == null) {
                    chain.doFilter(request, response);
                } else if (referer.trim().startsWith(realpath)) {
                    String csrfToken = httpRequest.getHeader("x-csrf-token");
                    if (csrfToken != null) {
                        AbstractOTPCredentialsMatcher var10000 = this.abstractOTPCredentialsMatcher;
                        this.abstractOTPCredentialsMatcher.getClass();
                        if (!var10000.verify("FCUD3YLMJYG2F72L2NFDXYPL6UJBUUN24BGBK6JDEIKHUA4ZOD2A", csrfToken, 1)) {
                            var10000 = this.abstractOTPCredentialsMatcher;
                            this.abstractOTPCredentialsMatcher.getClass();
                            String code_s = var10000.generate("FCUD3YLMJYG2F72L2NFDXYPL6UJBUUN24BGBK6JDEIKHUA4ZOD2A");
                            LOGGER.error("检测到重放请求,token [" + csrfToken + "]已失效 , 403:  [" + code_s + "][" + csrfToken + "]\nURI:" + requestUri);
                            httpRequest.getRequestDispatcher("/exception/403").forward(request, response);
                            return;
                        }
                    }

                    chain.doFilter(request, response);
                } else {
                    String[] refererWhiteArray = this.refererWhitelist.split(",");
                    boolean checked = false;
                    String[] var14 = refererWhiteArray;
                    int var15 = refererWhiteArray.length;

                    for(int var16 = 0; var16 < var15; ++var16) {
                        String string = var14[var16];
                        if (string.length() > 0 && referer.trim().startsWith(string)) {
                            checked = true;
                            break;
                        }
                    }

                    if (checked) {
                        chain.doFilter(request, response);
                    } else {
                        if (!requestUri.endsWith("/exception/403")) {
                            LOGGER.error("检测到跨站请求！403: \nURI:" + requestUri + "\nREFERER: " + referer);
                            httpRequest.getRequestDispatcher("/exception/403").forward(request, response);
                            return;
                        }

                        chain.doFilter(request, response);
                    }
                }

            }
        }
    }

    private boolean isExclusion(String requestURI) {
        if (this.excludesPattern != null && requestURI != null) {
            if (this.contextPath != null && requestURI.startsWith(this.contextPath)) {
                requestURI = requestURI.substring(this.contextPath.length());
                if (!requestURI.startsWith("/")) {
                    requestURI = "/" + requestURI;
                }
            }

            Iterator var2 = this.excludesPattern.iterator();

            String pattern;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                pattern = (String)var2.next();
            } while(!this.pathMatcher.matches(pattern, requestURI));

            return true;
        } else {
            return false;
        }
    }

    public void destroy() {
    }

    public boolean isCsrfcheck() {
        return this.csrfcheck;
    }

    public void setCsrfcheck(boolean csrfcheck) {
        this.csrfcheck = csrfcheck;
    }

    public String getRefererWhitelist() {
        return this.refererWhitelist;
    }

    public void setRefererWhitelist(String refererWhitelist) {
        this.refererWhitelist = refererWhitelist;
    }
}
