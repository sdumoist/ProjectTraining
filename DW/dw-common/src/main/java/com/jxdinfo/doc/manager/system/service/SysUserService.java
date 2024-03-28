package com.jxdinfo.doc.manager.system.service;

import java.util.List;

public interface SysUserService {
    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    List<String> checkUser(String username, String password);
}
