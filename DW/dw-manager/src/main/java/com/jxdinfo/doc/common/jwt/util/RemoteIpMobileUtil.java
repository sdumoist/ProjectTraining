package com.jxdinfo.doc.common.jwt.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RemoteIpMobileUtil {

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        System.out.println("================== X-Forwarded-For   "+ip);
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        System.out.println("================== X-Real-IP   "+ip);
        if(StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 用户是否是从外网访问的系统
     */
    public static Boolean isExtranetVisit(HttpServletRequest request) {
        String ip = getRemoteIp(request);
        System.out.println("================== 客户端ip地址   " + ip);
        if (!StringUtils.isEmpty(ip) && (StringUtils.equals(ip,"123.232.10.234") || StringUtils.equals(ip,"124.128.8.219")||
                StringUtils.equals(ip,"124.128.236.75") || StringUtils.equals(ip,"123.233.246.194") ||ip.startsWith("192.168.") ||StringUtils.equals(ip,"100.100.100.100"))) {
            return false;
        } else {
            return true;
        }
    }
}
