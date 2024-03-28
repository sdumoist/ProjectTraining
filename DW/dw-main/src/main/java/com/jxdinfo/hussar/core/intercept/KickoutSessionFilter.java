//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jxdinfo.hussar.core.intercept;

import com.alibaba.fastjson.JSON;
import com.jxdinfo.hussar.bsp.constant.Constants;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.sessionlimit.SessionLimit;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import com.jxdinfo.hussar.core.util.ToolUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@DependsOn({"springContextHolder"})
@Component
public class KickoutSessionFilter extends AccessControlFilter {
    private static Logger logger = LoggerFactory.getLogger(KickoutSessionFilter.class);
    private String kickoutUrl = "/login?kickout=true";
    public static final String SYSTEM_MAX_SESSION = "maxSession";
    public static final String KICKOUT = "kickout";
    public static final String LOGIN_NEW_SESSIONID = "loginNewSessionId";
    public static final String LOGIN_NEW_IP = "loginNewIp";
    public static final String CAUSE = "cause";
    public final String ERROR_TIPS = "tips";
    public static final String MSG_SYSTEM_MAX_SESSION = "超过系统最大会话数限制！";
    public static final String MSG_KICKOUT = "您已经在别处登录！";
    private SessionManager sessionManager;
    private SessionDAO sessionDAO;
    private Long maxSession = 1000000L;

    public KickoutSessionFilter() {
    }

    public SessionDAO getSessionDAO() {
        return this.sessionDAO;
    }

    public void setSessionDAO(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
    }

    public Long getMaxSession() {
        return this.maxSession;
    }

    public void setMaxSession(Long maxSession) {
        this.maxSession = maxSession;
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        String requestUri = httpServletRequest.getRequestURI();
        String ua = this.getUsreAgent(httpServletRequest);
        String ip = this.getIP(httpServletRequest);
        logger.debug(String.format("URL:%s | IP: %s | USER-AGENT: %s", requestUri, ip, ua));
        String tips = httpServletRequest.getParameter("tips");
        if (tips != null) {
            httpServletRequest.setAttribute("tips", tips);
        } else {
            httpServletRequest.setAttribute("tips", "");
        }

        Subject subject = this.getSubject(request, response);
        Boolean isRemembered = subject.isRemembered();
        Session session = subject.getSession();
        ShiroUser shiroUser = ShiroKit.getUser();
        String csrfReferer = httpServletRequest.getHeader("Referer");
        String accessLoginStartTime;
        if (csrfReferer == null) {
            if (isRemembered) {
                session.setAttribute("csrfFlag", true);
            }
        } else {
            if (shiroUser != null && session.getAttribute("csrfFlag") == null) {
                session.setAttribute("csrfFlag", true);
                httpServletRequest.getRequestDispatcher(this.kickoutUrl).forward(request, response);
                return false;
            }

            String schme = httpServletRequest.getScheme();
            accessLoginStartTime = httpServletRequest.getServerName();
            int serverPort = httpServletRequest.getServerPort();
//            String realpath = schme + "://" + accessLoginStartTime + ((!"http".equals(schme) || serverPort != 80) && (!"https".equals(schme) || serverPort != 443) ? ":" + serverPort : "");
//            if (!csrfReferer.trim().startsWith(realpath)) {
//                httpServletRequest.getRequestDispatcher(this.kickoutUrl).forward(request, response);
//                return false;
//            }
        }

        GlobalProperties globalProperties = (GlobalProperties)SpringContextHolder.getBean(GlobalProperties.class);
        if (!subject.isAuthenticated() && !isRemembered) {
            httpServletRequest.getRequestURI();
            httpServletRequest.getRemoteHost();
            if (httpServletRequest.getHeader("x-requested-with") != null && "XMLHttpRequest".equalsIgnoreCase(httpServletRequest.getHeader("x-requested-with"))) {
                httpServletResponse.setHeader("sessionstatus", "timeout");
                return false;
            } else {
                accessLoginStartTime = httpServletRequest.getHeader("Referer");
                if (accessLoginStartTime == null) {
                    this.saveRequestAndRedirectToLogin(request, response);
                    return false;
                } else if (session.getAttribute("sessionFlag") == null) {
                    request.setAttribute("tips", session.getAttribute("tips"));
                    httpServletRequest.getRequestDispatcher(this.kickoutUrl).forward(request, response);
                    return false;
                } else {
                    this.saveRequestAndRedirectToLogin(request, response);
                    return false;
                }
            }
        } else {
            accessLoginStartTime = session.getAttribute("startTime") == null ? "" : (String)session.getAttribute("startTime");
            String accessLoginEndTime = session.getAttribute("endTime") == null ? "" : (String)session.getAttribute("endTime");
            if (!Arrays.asList(Constants.MANAGE_USER).contains(shiroUser.getAccount()) && ToolUtil.isNotEmpty(accessLoginStartTime) && ToolUtil.isNotEmpty(accessLoginEndTime)) {
                boolean flag = false;
                Date date = new Date();
                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

                try {
                    Date dt1 = df.parse(accessLoginStartTime);
                    Date dt2 = df.parse(accessLoginEndTime);
                    Date now = df.parse(df.format(date));
                    if (dt1.getTime() <= now.getTime() && now.getTime() <= dt2.getTime()) {
                        flag = true;
                    }
                } catch (ParseException var30) {
                    var30.printStackTrace();
                }

                if (!flag) {
                    subject.logout();
                    httpServletRequest.setAttribute("tips", "当前时间段不可登录！");
                    httpServletRequest.getRequestDispatcher(this.kickoutUrl).forward(request, response);
                    return false;
                }
            }

            if (!globalProperties.isSessionLimit()) {
                return true;
            } else {
                SysUsersMapper usersMapper = (SysUsersMapper)SpringContextHolder.getBean(SysUsersMapper.class);
                SessionLimit sessionLimit = (SessionLimit)SpringContextHolder.getBean(SessionLimit.class);
                SysUsers sysUser = (SysUsers)usersMapper.selectById(shiroUser.getId());
                String userId = shiroUser.getId();
                Serializable sessionId = session.getId();
                String currentIp = httpServletRequest.getRemoteAddr();
                Session kickoutSession;
                if (this.maxSession != -1L && globalProperties.isKickoutAfter()) {
                    Collection<Session> collection = this.sessionDAO.getActiveSessions();
                    Iterator<Session> it = collection.iterator();
                    long now = System.currentTimeMillis();

                    label161:
                    while(true) {
                        Serializable activeId;
                        do {
                            Boolean sessionFlag;
                            do {
                                if (!it.hasNext()) {
                                    collection = this.sessionDAO.getActiveSessions();
                                    if (globalProperties.isSessionLimit() && (long)collection.size() >= this.maxSession) {
                                        try {
                                            kickoutSession = this.sessionManager.getSession(new DefaultSessionKey(sessionId));
                                            if (kickoutSession != null) {
                                                kickoutSession.setAttribute("kickout", true);
                                                kickoutSession.setAttribute("loginNewSessionId", sessionId);
                                                kickoutSession.setAttribute("loginNewIp", currentIp);
                                                kickoutSession.setAttribute("cause", "maxSession");
                                            }
                                        } catch (SessionException var29) {
                                            var29.printStackTrace();
                                        }
                                    }
                                    break label161;
                                }

                                kickoutSession = (Session)it.next();
                                sessionFlag = (Boolean)kickoutSession.getAttribute("sessionFlag");
                            } while(sessionFlag != null && now - kickoutSession.getLastAccessTime().getTime() <= kickoutSession.getTimeout());

                            if (!subject.isRemembered()) {
                                break;
                            }

                            activeId = kickoutSession.getId();
                        } while(sessionId.equals(activeId));

                        this.sessionDAO.delete(kickoutSession);
                    }
                }

                String userName = shiroUser.getName();
                int userMaxSession = sysUser.getMaxSessions().intValue();
                if (userMaxSession != -1) {
                    Deque<Serializable> deque = sessionLimit.getUserSession(userId);
                    if (!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
                        deque.push(sessionId);
                        sessionLimit.addUserSession(userId, deque);
                    }

                    if (deque.size() > userMaxSession) {
                        Serializable kickoutSessionId = null;
                        if (globalProperties.isKickoutAfter()) {
                            kickoutSessionId = (Serializable)deque.removeFirst();
                            logger.debug(String.format("踢出后者操作： 当前用户: %s (%s) 的最大允许会话数为: %s ,已经在别处登录，别处登录的SessionId为：%s，当前SessionId： %s 不允许登录，当前请求地址为: %s , 客户端IP: %s , 浏览器信息: %s", userName, userId, userMaxSession, kickoutSessionId, sessionId, requestUri, ip, ua));
                        } else {
                            kickoutSessionId = (Serializable)deque.removeLast();
                            logger.debug(String.format("踢出前者操作： 当前用户: %s (%s) 的最大允许会话数为: %s ,已经在别处登录，别处登录的SessionId为：%s，已经被踢出，当前SessionId： %s ,当前请求地址为: %s , 客户端IP: %s , 浏览器信息: %s", userName, userId, userMaxSession, kickoutSessionId, sessionId, requestUri, ip, ua));
                        }

                        sessionLimit.addUserSession(userId, deque);

                        try {
                            kickoutSession = this.sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                            if (kickoutSession != null) {
                                kickoutSession.setAttribute("kickout", true);
                                kickoutSession.setAttribute("loginNewSessionId", sessionId);
                                kickoutSession.setAttribute("loginNewIp", currentIp);
                            }
                        } catch (SessionException var28) {
                            var28.printStackTrace();
                        }
                    }
                }

                if ((Boolean)session.getAttribute("kickout") != null && (Boolean)session.getAttribute("kickout")) {
                    String cause = (String)session.getAttribute("cause");
                    logger.debug(String.format("当前会话被踢出： 当前用户: %s (%s) 的最大允许会话数为: %s ,已经在别处登录，当前被踢出的SessionId为：%s，已经被踢出，当前请求地址为: %s , 客户端IP: %s , 浏览器信息: %s", userName, userId, userMaxSession, sessionId, requestUri, ip, ua));
                    subject.logout();
                    if ("XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest)request).getHeader("X-Requested-With"))) {
                        Map<String, String> resultMap = new HashMap();
                        resultMap.put("code", "300");
                        if ("maxSession".equals(cause)) {
                            resultMap.put("message", "超过系统最大会话数限制！");
                        } else {
                            resultMap.put("message", "您已经在别处登录！");
                        }

                        this.out(response, resultMap);
                    } else {
                        if ("maxSession".equals(cause)) {
                            httpServletRequest.setAttribute("tips", "超过系统最大会话数限制！");
                        } else {
                            httpServletRequest.setAttribute("tips", "您已经在别处登录！");
                        }

                        httpServletRequest.getRequestDispatcher(this.kickoutUrl).forward(request, response);
                    }

                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private void out(ServletResponse hresponse, Map<String, String> resultMap) throws IOException {
        hresponse.setCharacterEncoding("UTF-8");
        PrintWriter out = hresponse.getWriter();
        out.println(JSON.toJSONString(resultMap));
        out.flush();
        out.close();
    }

    private String getIP(HttpServletRequest request) {
        StringBuffer ip = new StringBuffer();
        ip.append("X-Requested-For：").append(request.getHeader("X-Requested-For")).append(" , ");
        ip.append("X-Forwarded-For：").append(request.getHeader("X-Forwarded-For")).append(" , ");
        ip.append("Proxy-Client-IP：").append(request.getHeader("Proxy-Client-IP")).append(" , ");
        ip.append("WL-Proxy-Client-IP：").append(request.getHeader("WL-Proxy-Client-IP")).append(" , ");
        ip.append("HTTP_CLIENT_IP：").append(request.getHeader("HTTP_CLIENT_IP")).append(" , ");
        ip.append("HTTP_X_FORWARDED_FOR：").append(request.getHeader("HTTP_X_FORWARDED_FOR")).append(" , ");
        ip.append("RemoteAddr：").append(request.getRemoteAddr());
        return ip.toString();
    }

    private String getUsreAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
