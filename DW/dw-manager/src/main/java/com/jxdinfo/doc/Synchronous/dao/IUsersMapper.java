package com.jxdinfo.doc.Synchronous.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.Synchronous.model.SynchronousOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysStaff;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IUsersMapper extends BaseMapper<SysUsers> {

    /**
     * 更新用户密码
     *
     * @param sysUsers
     */
    void updatePwdByUserId(@Param("sysUsers") SysUsers sysUsers);

    /**
     * 获取用户列表(oracle数据库)
     * @return
     */
    List<SysUsers> selectOracleUserList();

    /**
     * 获取staff表数据
     * @param staffIds
     * @return
     */
    List<SysStaff> selectStaffList(@Param("list") List<String> staffIds);

    /**
     * 获取用户列表(oracle数据库)
     * @return
     */
    List<SysStaff> selectOracleStaffList();

    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     *
     * @param struId
     * @return
     */
    List<SysStru> selectUserStruData(@Param("struId") String struId);

    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     *
     * @param organIds
     * @return
     */
    List<SysOrgan> selectUserOrganData(@Param("organIds") List<String> organIds);

    /**
     * 查询staff表数据(oracle数据库)
     * @param struIds
     * @return
     */
    List<SysStaff> selectUserStaffData(@Param("struIds") List<String> struIds);

    /**
     * 查询用户信息
     * @param userAccount
     * @param accountStatus
     * @param status
     * @return
     */
    SysUsers selectUser(@Param("userAccount") String userAccount, @Param("accountStatus") String accountStatus, @Param("status") String status);

    /**
     * 根据用户id查询用户信息
     *
     * @param userId
     * @return
     */
    SysUsers selectByUserId(@Param("userId") String userId);

    /**
     * 查询oracle数据库组织机构数据
     * @return
     */
    List<SynchronousOrgan> selectOracleOrganList(@Param("isEmployee") String isEmployee);


    /**
     * 查询本地数据库用户数据
     * @param struIdList
     * @return
     */
    List<SynchronousOrgan> selectOrganList(@Param("list") List<String> struIdList, @Param("isEmployee") String isEmployee);

    /**
     * 查询本地用户数据
     * @param userIdList
     * @return
     */
    List<SysUsers> selectUserList(@Param("list") List<String> userIdList);

    /**
     * 新增组织机构stru表
     *
     * @param insertStru
     */
    void insertStru(@Param("list") List<SynchronousOrgan> insertStru);

    // 新增组织机构organ表
    void insertOrgan(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 新增staff表
     * @param insertOrgan
     */
    void insertStaff(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 新增user表
     * @param insertOrgan
     */
    void insertUser(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 更新组织机构stru表
     *
     * @param insertStru
     */
    void updateStru(@Param("list") List<SynchronousOrgan> insertStru);


    // 更新组织机构organ表
    void updateOrgan(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 更新staff表
     * @param insertOrgan
     */
    void updateStaff(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 更新用户表
     * @param insertOrgan
     */
    void updateUser(@Param("list") List<SynchronousOrgan> insertOrgan);

    /**
     * 删除staff表
     * @param struIdList
     */
    void deleteStaff(@Param("list") List<String> struIdList);

    /**
     * 删除用户角色表
     * @param userIdList
     */
    void deleteUserRole(@Param("list") List<String> userIdList);

    /**
     * 删除用户表
     * @param deleteUser
     */
    void deleteUser(@Param("list") List<String> deleteUser);

  }
