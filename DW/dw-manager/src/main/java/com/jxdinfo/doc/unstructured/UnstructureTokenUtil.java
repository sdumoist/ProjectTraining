package com.jxdinfo.doc.unstructured;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.manager.system.service.SysUserService;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.service.PlatformSystemInfoService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UnstructureTokenUtil {

    @Value("${token.invalid}")
    private Long invalid;


    @Value("${token.key}")
    private String key;

    @Autowired
    PlatformSystemInfoService unstrucPlatformService;


    @Autowired
    private ISysUsersService sysUserService;


    /**
     * 根据系统id 和用户id生成token
     * @param username 用户名称
     * @return
     */
    public JSONObject getLoginToken(String username) {
        JSONObject tokenJson = new JSONObject();
        String token = "";
        //获取当前时间毫秒值
        Long nowDate = System.currentTimeMillis();

        Long inValidDateStr = nowDate + invalid*1000; // 半小时
        //设置
        Date inValidDate = new Date(inValidDateStr);
        JwtBuilder result = Jwts.builder()
                //设置JWT头部参数
                .setHeaderParam("typ", "JWT")
                //设置信息
                .claim("username", username)
                // 设置过期时间
                .setExpiration(inValidDate)
                // 设置token时间不在当前时间之前
                .setNotBefore(new Date())
                //设置签名
                .signWith(SignatureAlgorithm.HS256, key);
        //生成token
        token = result.compact();
        tokenJson.put("validDate", inValidDateStr + "");
        tokenJson.put("accessToken", token);
        return tokenJson;
    }

    /**
     * 验证token 登录使用
     * @param accessToken
     * @return
     */
    public JSONObject validLgoinToken(String accessToken) {
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("code","1");
        try {
            Jwts.parser().setSigningKey(key).parse(accessToken);
        } catch (ExpiredJwtException e) {
            tokenInfo.put("code", "3");
            tokenInfo.put("msg","token过期");
            return  tokenInfo;
        }
        catch (IllegalArgumentException e){
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }catch (Exception e){
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }
        try {
            Claims claims =  Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
            if(claims!=null){
                String username = (String) claims.get("username");
                tokenInfo.put("msg","token验证成功");
                tokenInfo.put("username", username);
            }
        } catch (Exception j) {
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }
        return tokenInfo;
    }


    /**
     * 根据系统id 和用户id生成token
     * @param systemId 系统id
     * @param userId 用户id
     * @param userName 用户名称
     * @return
     */
    public JSONObject getToken(String systemId, String userId, String userName) {
        JSONObject tokenJson = new JSONObject();
        String token = "";
        //获取当前时间毫秒值
        Long nowDate = System.currentTimeMillis();

        Long inValidDateStr = nowDate + invalid*1000; // 半小时
        //设置
        Date inValidDate = new Date(inValidDateStr);
        JwtBuilder result = Jwts.builder()
                //设置JWT头部参数
                .setHeaderParam("typ", "JWT")
                //设置信息
                .claim("systemId", systemId)
                .claim("userId", userId)
                .claim("userName", userName)
                // 设置过期时间
                .setExpiration(inValidDate)
                // 设置token时间不在当前时间之前
                .setNotBefore(new Date())
                //设置签名
                .signWith(SignatureAlgorithm.HS256, key);
        //生成token
        token = result.compact();
        tokenJson.put("validDate", inValidDateStr + "");
        tokenJson.put("accessToken", token);
//        System.out.println("根据系统Id和用户Id生成token：" + token);
        return tokenJson;
    }

    /**
     * 验证token
     * @param accessToken
     * @return
     */
    public JSONObject validToken(String accessToken) {
        JSONObject tokenInfo = new JSONObject();
        tokenInfo.put("code","1");
//        System.out.println("校验token：" + accessToken);
        try {
            Jwts.parser().setSigningKey(key).parse(accessToken);
        } catch (ExpiredJwtException e) {
            tokenInfo.put("code", "3");
            tokenInfo.put("msg","token过期");
            return  tokenInfo;
        }
        catch (IllegalArgumentException e){
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }catch (Exception e){
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }
        try {
            Claims claims =  Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
            if(claims!=null){
                String systemId = (String) claims.get("systemId");
                String userId = (String) claims.get("userId");
                String userName = (String) claims.get("userName");
                PlatformSystemInfo system = unstrucPlatformService.getById(systemId);
                if(system!=null){
                    // 验证用户是否存在
                    SysUsers user = sysUserService.getById(userId);
                    if (user == null) {
                        System.out.println("====校验token用户不存在  " + userId);
                        tokenInfo.put("code", "2");
                        tokenInfo.put("msg", "操作用户不存在");
                        return tokenInfo;
                    }
                    tokenInfo.put("msg","token验证成功");
                    tokenInfo.put("systemId", systemId);
                    tokenInfo.put("validTime", claims.getExpiration().getTime());
                    tokenInfo.put("userId", userId);
                    tokenInfo.put("userName", userName);
                }else{
                    tokenInfo.put("code","2");
                    tokenInfo.put("msg","token校验失败,系統不存在");
                }
            }
        } catch (Exception j) {
            tokenInfo.put("code", "2");
            tokenInfo.put("msg","token校验失败");
            return  tokenInfo;
        }
        return tokenInfo;
    }
}
