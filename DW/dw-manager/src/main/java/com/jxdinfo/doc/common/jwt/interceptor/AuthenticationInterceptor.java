package com.jxdinfo.doc.common.jwt.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文库PC客户端拦截器
 * @author yjs
 */
public class AuthenticationInterceptor implements HandlerInterceptor {
    static final public Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Resource
    SysUsersMapper sysUsersMapper;
    @Resource
    private JWTUtil jwtUtil;
    @Value("${token.key}")
    private String key;
    /**
     *
     * 进入Handler方法执行之前执行此方法
     * @param httpServletRequest
     * @param httpServletResponse
     * @param object
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        // 从 http 请求头中取出 token
        String token = httpServletRequest.getHeader("token");
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        // token如果为空则抛出异常
        if (token == null) {
            throw new RuntimeException("无token，请重新登录");
        }
        String userId = null;
        // token认证，伪造token抛出异常
        try {
              Jwts.parser().setSigningKey(key).parse(token);
        } catch (ExpiredJwtException   e) {
            logger.error("token过期");
            httpServletResponse.setStatus(401);
            return  false;
        }
        catch (IllegalArgumentException e){
            logger.error("token不合法");
            httpServletResponse.setStatus(401);
            return  false;

        }catch (SignatureException e){
            logger.error("token签名错误");
            httpServletResponse.setStatus(401);
            return  false; }
        try {
            userId = JWT.decode(token).getClaims().get("userId").asString();
        } catch (JWTDecodeException j) {
            return  false;
        }
        //通过userId 获取USER
        SysUsers user = sysUsersMapper.selectById(userId);
        jwtUtil.setSysUsers(user);
        return true;
    }

    /**
     *
     * 该方法在进入Handler方法之后，返回ModelAndView之前执行
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 该方法在handler方法执行完之后执行
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }
}
