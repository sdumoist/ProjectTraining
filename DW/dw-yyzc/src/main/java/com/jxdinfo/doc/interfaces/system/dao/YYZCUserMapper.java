package com.jxdinfo.doc.interfaces.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.interfaces.system.model.HeadPhoto;
import com.jxdinfo.doc.interfaces.system.model.YYZCUser;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xuxinying
 * @since 2018-06-27
 */
public interface YYZCUserMapper extends BaseMapper<YYZCUser> {

    /**
     * 
     * @return List<YYZCUserEntity>
     */
    List<YYZCUser> getYyzcUserList();

    /**
     * 运营支撑员工同步插入
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertList(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工同步到用户表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertSysUsers(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工同步到用户表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertSysOrgan(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工同步到用户表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertSysStru(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工同步到权限关联表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertSysUserRole(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工同步更新
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateList(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工更新到用户表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateSysUsers(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工更新到机构表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateSysOrgan(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工更新到结构表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateSysStru(List<YYZCUser> userList) throws Exception;

    /**
     * 运营支撑员工更新到权限关联表
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateSysUserRole(List<YYZCUser> userList) throws Exception;

    /**
     * 
     * @return List<getYyzcUserHeadPhotos>
     */
    List<HeadPhoto> getYyzcUserHeadPhotos();

    /**
     * 运营支撑员工照片同步插入
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void insertUserPhotoList(List<HeadPhoto> userList) throws Exception;

    /**
     * 运营支撑员工照片同步更新
     * @param userList 用户集合
     * @throws Exception 异常
     */
    void updateUserPhotoList(List<HeadPhoto> userList) throws Exception;

    /**
     * 获取运营支撑组织机构Id
     * @Title: getDeptidlist 
     * @author: XuXinYing
     * @return
     */
    List<String> getDeptidlist();

    /*删除离职人员相关信息*/
    void delYYZCList(List<String> list);
    void delSysOrgan(List<String> list);
    void delSysStru(List<String> list);
    void delUserRole(List<String> list);
    void delSysUsers(List<String> list);
    /* 删除文件权限 */
    void delDocFileAuthority(List<String> list);
    /* 删除目录权限 */
    void delDocFoldAuthority(List<String> list);

    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    List<String> checkUser(String username1,String password);
}
