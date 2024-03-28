package com.jxdinfo.doc.Synchronous.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.Synchronous.dao.IUsersMapper;
import com.jxdinfo.doc.Synchronous.model.SynchronousOrgan;
import com.jxdinfo.doc.Synchronous.service.SysUsersService;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysStaff;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.ISysOrganService;
import com.jxdinfo.hussar.bsp.organ.service.ISysStaffService;
import com.jxdinfo.hussar.bsp.permit.dao.SysPasswordHistMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysPasswordHist;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.common.constant.DataSourceEnum;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.shiro.lock.LoginLock;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

@Service
public class UsersServiceImpl extends ServiceImpl<IUsersMapper, SysUsers> implements SysUsersService {

    @Resource
    IUsersMapper iuserMapper;

    @Resource
    SysUsersMapper sysUsersMapper;

    @Autowired
    ISysOrganService  iSysOrganService;

    @Autowired
    ISysStaffService iSysStaffService;

    @Resource
    private GlobalProperties globalProperties;

    /**
     * 密码生命周期 Mapper 接口
     */
    @Resource
    private SysPasswordHistMapper sysPasswordHistMapper;


    /**
     * 修改密码(oracle数据库)
     *
     * @param sysUsers
     * @Title: updatePwd
     * @author: LiangDong
     */
    @Override
    public void updatePwd(SysUsers sysUsers) {
//
//        // 密码修改时间
//        sysUsers.setPswdUptTime(new Date());
//        // 密码生命周期之后的日期，如3个月后
//        String pwdExtinctTime = DateUtil.getAfterDayDate(globalProperties.getPasswordHist() + "");
//        // 密码失效时间
//        sysUsers.setPswdTime(DateUtil.parseTime(pwdExtinctTime));
//        // 更新密码
//        this.sysUsersService.updatePwdByUserId(sysUsers);

        // 更新密码生命周期表
        List<SysPasswordHist> list = sysPasswordHistMapper.lastCreateTime(sysUsers.getUserId());
        if (list.size() >= globalProperties.getPwdRepeatTime()) {
            // 得到最早的记录并删掉
            SysPasswordHist sysPasswordHist = list.get(list.size() - 1);
            sysPasswordHistMapper.delete(new QueryWrapper<SysPasswordHist>().eq("USER_ID", sysPasswordHist.getUserId())
                    .eq("PASSWORD", sysPasswordHist.getPassword()));
        }
        SysPasswordHist sph = new SysPasswordHist();
        sph.setUserId(sysUsers.getUserId());
        sph.setPassword(sysUsers.getPassword());
        sph.setCreateTime(new Timestamp(System.currentTimeMillis()));
        sysPasswordHistMapper.insert(sph);
        // 锁定判断接口;
        LoginLock loginLock = SpringContextHolder.getBean(LoginLock.class);
        // 清除密码错误缓存
        loginLock.removeUserCache(sysUsers.getUserId());
    }

    /**
     * 根据id获取用户实体(oracle数据库)
     *
     * @param userId
     * @return
     * @Title: getUser
     * @author: LiangDong
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public SysUsers getUser(String userId) {
        return iuserMapper.selectByUserId(userId);
    }

    /**
     * 根据id获取更新用户(oracle数据库)
     *
     * @param sysUsers
     * @return
     * @Title: getUser
     * @author: LiangDong
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public void updatePwdByUserId(SysUsers sysUsers) {
        iuserMapper.updatePwdByUserId(sysUsers);
    }


    /**
     * 获取用户列表(oracle数据库)
     *
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SysUsers> selectOracleUserList() {
        return iuserMapper.selectOracleUserList();
    }

    /**
     * 获取staff表(oracle数据库)
     *
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SysStaff> selectOracleStaffList() {
        return iuserMapper.selectOracleStaffList();
    }

    /**
     * 获取staff表数据
     *
     * @param staffIds
     * @return
     */
    @Override
    public List<SysStaff> selectStaffList(List<String> staffIds) {
        return iuserMapper.selectStaffList(staffIds);
    }

    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     * @param struId
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SysStru> selectUserStruData(String struId){
        return iuserMapper.selectUserStruData(struId);
    }

    /**
     * 查询当前用户的组织机构信息(oracle数据库)
     * @param organIds
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SysOrgan> selectUserOrganData(List<String> organIds){
        return iuserMapper.selectUserOrganData(organIds);
    }

    /**
     * 查询当前用户的staff表信息(oracle数据库)
     * @param struIds
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SysStaff> selectUserStaffData(List<String> struIds){
        return iuserMapper.selectUserStaffData(struIds);
    }



    /**
     * 查询用户信息
     *
     * @param userAccount
     * @param accountStatus
     * @return
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public SysUsers selectUser(String userAccount, String accountStatus, String status) {
        return iuserMapper.selectUser(userAccount, accountStatus, status);
    }

    /**
     * 查询oracle数据库所有组织机构数据
     */
    @Override
    @DS(DataSourceEnum.DATA_SOURCE_ONE)
    public List<SynchronousOrgan> selectOracleOrganList(String isEmployee) {
        return iuserMapper.selectOracleOrganList(isEmployee);
    }

    /**
     * 查询本地数据库所有组织机构数据
     */
    @Override
    public List<SynchronousOrgan> selectOrganList(List<String> struIdList, String isEmployee) {
        return iuserMapper.selectOrganList(struIdList, isEmployee);
    }

    /**
     * 查询本地数据库所有组织机构数据
     */
    @Override
    public List<SysUsers> selectUserList(List<String> userIdList) {
        return iuserMapper.selectUserList(userIdList);
    }

    /**
     * 新增组织机构stru表
     *
     * @param insertStru
     */
    @Override
    public void insertStru(List<SynchronousOrgan> insertStru) {
        iuserMapper.insertStru(insertStru);
    }

    // 新增组织机构organ表
    @Override
    public void insertOrgan(List<SynchronousOrgan> insertOrgan) {
        iuserMapper.insertOrgan(insertOrgan);
    }

    /**
     * 更新组织机构stru表
     *
     * @param insertStru
     */
    @Override
    public void updateStru(List<SynchronousOrgan> insertStru) {
        iuserMapper.updateStru(insertStru);
    }

    // 更新组织机构organ表
    @Override
    public void updateOrgan(List<SynchronousOrgan> insertOrgan) {
        iuserMapper.updateOrgan(insertOrgan);
    }

    @Override
    public void deleteUserRole(List<String> userIdList) {
        iuserMapper.deleteUserRole(userIdList);
    }

    @Override
    public void deleteUser(List<String> userIdList) {
        iuserMapper.deleteUser(userIdList);
    }

}
