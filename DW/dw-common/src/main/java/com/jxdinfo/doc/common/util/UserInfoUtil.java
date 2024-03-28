package com.jxdinfo.doc.common.util;


import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.jxdinfo.doc.manager.system.dao.SysUserMapper;
import com.jxdinfo.doc.manager.system.model.SysUserInfo;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;

/**
 * 用户信息静态工具类
 *
 * @author
 * @Date 2018/3/22 0022
 */

public class UserInfoUtil {
    private UserInfoUtil() {
    }

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    private static SysUserMapper sysUserMapper = appCtx.getBean(SysUserMapper.class);// 这样直接调用就好

    /**
     * 获取用户信息
     *
     * @return
     */
    public static Map<String, Object> getUserInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        String userName = ShiroKit.getUser().getName();
        String userId = ShiroKit.getUser().getId();
        map.put("ID", userId);
        map.put("NAME", userName);
        return map;
    }
    
    /**
     * 获取用户信息
     *
     * @return
     */
    public static ShiroUser getCurrentUser() {
    	return ShiroKit.getUser();
    }

    /**
     * 通过工号获取用户信息
     *
     * @return
     */
    public static Map<String, Object> getUserInfoByNum(String usernum) {
        Map<String, Object> map = new HashMap<String, Object>();
        SysUserInfo user = sysUserMapper.selectByUserNum(usernum);
        map.put("ID", user.getId());
        map.put("NAME", user.getUsername());
        map.put("DEPT_NAME", user.getDeptName());
        map.put("EMP_NO", user.getEmployeeNo());
        /* map.put("ID", "7043f3642ce911e89b87429ff4208430");
        map.put("NAME", "管理员");
        map.put("DEPT_NAME","5AA3D70B-14F9-4A44-8652-3C29DACB70DE");
        map.put("EMP_NO","JXD-00001");*/
        return map;
    }

    /**
     * 通过工号获取用户信息
     *
     * @return
     */
    public static SysUsers getUserByNum(String usernum) {
        SysUsers user = sysUserMapper.getUserByNum(usernum);
        return user;
    }
}
