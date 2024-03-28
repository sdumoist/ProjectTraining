package com.jxdinfo.doc.manager.system.service.impl;

import com.jxdinfo.doc.manager.system.dao.SysUserMapper;
import com.jxdinfo.doc.manager.system.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {
    /**
     * Mapper
     */
    @Resource
    private SysUserMapper sysUserMapper;
    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    public List<String> checkUser(String username, String password){
        return sysUserMapper.checkUser( username,password);
    }

}
