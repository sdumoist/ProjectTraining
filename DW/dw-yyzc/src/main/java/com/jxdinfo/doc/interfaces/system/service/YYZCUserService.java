package com.jxdinfo.doc.interfaces.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.interfaces.system.model.HeadPhoto;
import com.jxdinfo.doc.interfaces.system.model.YYZCUser;

import java.util.List;

/**
 * 运营支撑用户service接口
 * @author XuXinYing
 * @Date 2018/7/3 
 */
public interface YYZCUserService extends IService<YYZCUser> {

    /**
     * 更新运营支撑用户表信息 
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param userList 用戶信息集合
     * @return true or false
    */
    boolean insertOrUpdateYyzcUser(List<YYZCUser> userList);

    /**
     * 更新运营支撑用户图片表信息 
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param userphotoList 用戶图片信息集合
     * @return true or false
    */
    boolean insertOrUpdateYyzcUserPhoto(List<HeadPhoto> userphotoList);

    /**
     * 更新员工照片信息
     * @Title: getUserPhotoInfo 
     * @author: XuXinYing
     * @return true or false
     */
    boolean getUserPhotoInfo();

    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    List<String> checkUser(String username,String password);
}
