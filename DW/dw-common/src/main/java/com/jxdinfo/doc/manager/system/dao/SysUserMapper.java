package com.jxdinfo.doc.manager.system.dao;

import com.jxdinfo.doc.manager.system.model.SysUserInfo;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息接口
 * @Author liuqi
 * @Date 2018-3-12
 */

public interface SysUserMapper {

    /**
    * @Author 阁楼麻雀
    * @Date 2016-7-4 16:33
    * @Desc 通过名字查询用户信息
    * @param usernum 名字
    * @return SysUserInfo
    */
    SysUserInfo selectByUserNum(@Param("usernum") String usernum);

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    List<SysUserInfo> fetchById(String id);

    /**
     * 根据用户ID获取用户工号
     * @param userid 用户ID
     * @return 用户号
     */
    String getEmp_no(@Param("userid") String userid);

    /**
     * 通过员工工号查询员工信息
     * @Title: getUserByNum 
     * @author: XuXinYing
     * @param usernum
     * @return
     */
    SysUsers getUserByNum(@Param("usernum") String usernum);


    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    List<String> checkUser(@Param("username")String username, @Param("password")String password);
}
