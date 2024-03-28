package com.jxdinfo.doc.Synchronous.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.Synchronous.model.SynchronousOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysStaff;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;

import java.util.List;

public interface SysUsersService extends IService<SysUsers> {

    /**
     * 更新密码
     *
     * @param var1
     */
    void updatePwd(SysUsers var1);

    /**
     * 根据用户id获取用户信息
     *
     * @param userId
     * @return
     */
    SysUsers getUser(String userId);

    /**
     * 更新用户信息
     *
     * @param sysUsers
     */
    void updatePwdByUserId(SysUsers sysUsers);

    /**
     * 获取用户列表(oracle数据库)
     *
     * @return
     */
    List<SysUsers> selectOracleUserList();

    /**
     * 获取staff表(oracle数据库)
     *
     * @return
     */
    List<SysStaff> selectOracleStaffList();

    /**
     * 获取staff表数据
     *
     * @param staffIds
     * @return
     */
    List<SysStaff> selectStaffList(List<String> staffIds);


    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     * @param organIds
     * @return
     */
    List<SysOrgan> selectUserOrganData(List<String> organIds);

    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     * @param struId
     * @return
     */
    List<SysStru> selectUserStruData(String struId);

    List<SysStaff> selectUserStaffData(List<String> struIds);


    /**
     * 查询用户信息
     *
     * @param userAccount
     * @param accountStatus
     * @return
     */
    SysUsers selectUser(String userAccount, String accountStatus, String status);

    /**
     * 查询oracle数据库所有组织机构数据
     */
    List<SynchronousOrgan> selectOracleOrganList(String isEmployee);

    /**
     * 查询本地数据库所有组织机构数据
     */
    List<SynchronousOrgan> selectOrganList(List<String> struIdList, String isEmployee);


    /**
     * 查询本地数据库所有组织机构数据
     */
    List<SysUsers> selectUserList(List<String> userIdList);

    /**
     * 新增组织机构stru表
     *
     * @param insertStru
     */
    void insertStru(List<SynchronousOrgan> insertStru);

    // 新增组织机构organ表
    void insertOrgan(List<SynchronousOrgan> insertOrgan);

     /**
     * 更新组织机构stru表
     *
     * @param updateStru
     */
    void updateStru(List<SynchronousOrgan> updateStru);

    // 更新组织机构organ表
    void updateOrgan(List<SynchronousOrgan> updateOrgan);

    void deleteUserRole(List<String> userIdList);

    void deleteUser(List<String> userIdList);

}
