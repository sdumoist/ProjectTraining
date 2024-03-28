package com.jxdinfo.doc.common.jwt.util;

import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jwt公共方法类
 * @author yjs
 */
@Service
public class JWTUtil {

    @Value("${token.invalid}")
    private Long invalid;
    @Value("${token.key}")
    private String key;
    private  SysUsers sysUsers;

    /**
     * 通过user 获取token值
     * @param  user
     * @return  MAP
     */
    public  Map<String, String> getToken(SysUsers user) {
        Map<String, String> tokenMap = new HashMap<>();
        String token = "";
        //获取当前时间毫秒值
        Long nowDate = System.currentTimeMillis();
        Long inValidDateStr = nowDate + invalid*1000;
        //设置
        Date inValidDate = new Date(inValidDateStr);
        JwtBuilder result = Jwts.builder()
                //设置JWT头部参数
                .setHeaderParam("typ", "JWT")
                //设置用户名
                .claim("userId", user.getUserId())
                // 设置过期时间
                .setExpiration(inValidDate)
                // 设置token时间不在当前时间之前
                .setNotBefore(new Date())
                //设置签名
                .signWith(SignatureAlgorithm.HS256, key);
        //生成token
        token = result.compact();
        tokenMap.put("validDate", inValidDateStr + "");
        tokenMap.put("token", token);
        return tokenMap;
    }

    public SysUsers getSysUsers() {
        return sysUsers;
    }

    public void setSysUsers(SysUsers sysUsers) {
        this.sysUsers = sysUsers;
    }
}
