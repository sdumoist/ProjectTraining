/*
 * 金现代轻骑兵V8开发平台
 * SysUsersServiceImpl.java
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com.jxdinfo.hussar.bsp.permit.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.jxdinfo.doc.manager.statistics.dao.DocSpaceMapper;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.hussar.bsp.audit.constant.AuditConstant;
import com.jxdinfo.hussar.bsp.audit.dao.SysUsersAuditMapper;
import com.jxdinfo.hussar.bsp.audit.model.SysStruAudit;
import com.jxdinfo.hussar.bsp.audit.model.SysUserIpAudit;
import com.jxdinfo.hussar.bsp.audit.model.SysUsersAudit;
import com.jxdinfo.hussar.bsp.audit.service.ISysStruAuditService;
import com.jxdinfo.hussar.bsp.audit.service.ISysUserIpAuditService;
import com.jxdinfo.hussar.bsp.audit.util.CopyPropertieUtils;
import com.jxdinfo.hussar.bsp.baseconfig.model.SysBaseConfig;
import com.jxdinfo.hussar.bsp.baseconfig.service.ISysBaseConfigService;
import com.jxdinfo.hussar.bsp.baseconfig.util.SysBaseConfigConstant;
import com.jxdinfo.hussar.bsp.constant.Constants;
import com.jxdinfo.hussar.bsp.messagepush.AbstractPushMsgMatcher;
import com.jxdinfo.hussar.bsp.organ.CreateUserSendMailUtil;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.ISysEmployeeService;
import com.jxdinfo.hussar.bsp.organ.service.SysOrgManageService;
import com.jxdinfo.hussar.bsp.permit.constants.PermitTipConstants;
import com.jxdinfo.hussar.bsp.permit.dao.*;
import com.jxdinfo.hussar.bsp.permit.model.*;
import com.jxdinfo.hussar.bsp.permit.service.*;
import com.jxdinfo.hussar.bsp.sysuserip.model.SysUserIp;
import com.jxdinfo.hussar.bsp.sysuserip.service.ISysUserIpService;
import com.jxdinfo.hussar.common.constant.cache.Cache;
import com.jxdinfo.hussar.common.constant.cache.CacheKey;
import com.jxdinfo.hussar.common.constant.enums.SysUserAndRole;
import com.jxdinfo.hussar.common.constant.enums.Whether;
import com.jxdinfo.hussar.common.constant.state.UserRoleStatus;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.base.tips.SuccessTip;
import com.jxdinfo.hussar.core.base.tips.Tip;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.constant.HttpCode;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.shiro.lock.LoginLock;
import com.jxdinfo.hussar.core.util.*;
import com.jxdinfo.hussar.otp.credential.AbstractOTPCredentialsMatcher;
import com.jxdinfo.hussar.system.controller.util.GeneratePassword;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 类的用途：用户表 服务实现类<p>
 * 创建日期：2018年3月1日 <br>
 * 修改历史：<br>
 * 修改日期：2018年3月1日 <br>
 * 修改作者：LiangDong <br>
 * 修改内容：修改内容 <br>
 *
 * @author LiangDong
 * @version 1.0
 */
@Service
@DependsOn("springContextHolder")
public class SysUsersServiceImpl extends ServiceImpl<SysUsersMapper, SysUsers> implements ISysUsersService {

    /**
     * 用户表 Mapper 接口
     */
    @Resource
    private SysUsersMapper sysUsersMapper;

    @Resource
    private DocSpaceMapper docSpaceMapper;

    /**
     * 用户表 Mapper 接口
     */
    @Resource
    private SysUsersAuditMapper sysUsersAuditMapper;

    /**
     * 用户角色对应表 Mapper 接口
     */
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 账户密码找回表 Mapper 接口
     */
    @Resource
    private SysGetBackPasswordMapper sysGetBackPasswordMapper;

    /**
     * 密码生命周期 Mapper 接口
     */
    @Resource
    private SysPasswordHistMapper sysPasswordHistMapper;

    /**
     * 用户代理表 Mapper 接口
     */
    @Resource
    private SysUserProxyMapper sysUserProxyMapper;

    /**
     * 用户角色审核表 Mapper 接口
     */
    @Resource
    private SysUserroleAuditMapper sysUserroleAuditMapper;

    /**
     * 组织机构表 Mapper 接口
     */
    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * global 配置
     */
    @Resource
    private GlobalProperties globalProperties;

    /**
     * hussar 配置
     */
    @Resource
    private HussarProperties hussarProperties;

    /**
     * 用户角色审核表 Service接口
     */
    @Resource
    private ISysUserroleAuditService sysUserroleAuditService;

    /**
     * 用户角色关联表 Service接口
     */
    @Resource
    private ISysUserRoleService sysUserRoleService;

    /**
     * 角色 服务类
     */
    @Resource
    private ISysRolesService sysRolesService;

    /**
     * 用户角色修改类型 ：添加角色
     */
    private static final String ADD = "1";

    /**
     * 用户角色修改类型 ：删除角色
     */
    private static final String DELETE = "2";

    /**
     * 用户角色审核表 服务类
     */
    @Resource
    private ISysUserroleAuditService isAuditService;

    /**
     * 基础配置接口
     */
    @Resource
    private ISysBaseConfigService iSysBaseConfigService;

    /**
     * 用户ip管理
     */
    @Resource
    private ISysUserIpService iSysUserIpService;

    /**
     * 用户ip管理
     */
    @Resource
    private ISysUserIpAuditService iSysUserIpAuditService;

    /**
     * 不相容角色 服务类
     */
    @Resource
    private ISysConfRolesService iSysConfRolesService;

    @Autowired
    private AbstractOTPCredentialsMatcher abstractOTPCredentialsMatcher;

    @Resource
    private HussarCacheManager hussarCacheManager;

    /**
     * 存储加密算法抽象
     */
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;

    @Autowired
    private ISysGetBackPasswordService iSysGetBackPasswordService;

    @Resource
    private ISysEmployeeService sysEmployeeService;
    @Resource
    private SysOrgManageService sysOrgManageService;

    @Resource
    private ISysStruAuditService iSysStruAuditService;

    @Resource
    private AbstractPushMsgMatcher abstractPushMsgMatcher;

    @Value("${SPACE.SIZE}")
    private double SpaceSize;

    /**
     * 新增用户
     *
     * @param map
     * @Title: addUser
     * @author: LiangDong
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String addUser(Map<String, Object> map) {
        // 角色id
        String[] roleIds = map.get("roleIds").toString().split(",");
        // 用户 实体
        SysUsers sysUsers = (SysUsers) map.get("sysUsers");
        String pass = "";
        // 判断如果使用密码规则生成的密码且发送邮件时（防止生成的密码无法得知），使用生成的密码，否则使用默认密码
        if (globalProperties.getCreateUserSendEmail() && !globalProperties.getCreateUserUseDefaultPass()) {
            pass = GeneratePassword.generate(globalProperties.getPwdRule());
        } else {
            pass = globalProperties.getDefaultPassword();
        }
        // 存储加密通用逻辑
        String pwd = credentialsMatcher.passwordEncode(String.valueOf(pass).getBytes());
        // 存储加密通用逻辑
        sysUsers.setPassword(pwd);
        // 直接插入用户表中
        sysUsersMapper.insert(sysUsers);
        DocSpace docSpace = new DocSpace();
        docSpace.setOrganId(sysUsers.getUserId());
        docSpace.setSpaceSize(SpaceSize);
        docSpaceMapper.insert(docSpace);
        sysUsers.setAccountStatus(UserStatus.LOCKED.getCode());

        // 保存用户允许登录的IP
        this.saveUserIP(sysUsers.getUserId(), sysUsers.getAccessLoginIp());

        // 得到刚插入的用户记录的用户
        String userId = sysUsers.getUserId();

        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isRoleAudit()) {
            // **************如果开启了用户角色审核**************
            // 默认角色直接插入用户角色关联表
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setGrantedRole(SysUserAndRole.PUBLIC_ROLE.getValue());
            sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
            sysUserRole.setCreateTime(new Date());
            sysUserRole.setLastTime(new Date());
            sysUserRoleMapper.insert(sysUserRole);

            // 往用户角色审核表中插入记录
            List<SysUserroleAudit> sysUserroleAuditList = new ArrayList<SysUserroleAudit>();
            if (roleIds.length > 0) {
                for (String roleId : roleIds) {
                    if (ToolUtil.isEmpty(roleId)) {
                        // 如果为空，跳出本次循环
                        continue;
                    }
                    if (SysUserAndRole.PUBLIC_ROLE.getValue().equals(roleId)) {
                        // 如果是默认角色，不用审核，跳出本次循环
                        continue;
                    }

                    SysUserroleAudit sysUserroleAudit = new SysUserroleAudit();
                    sysUserroleAudit.setUserId(userId);
                    sysUserroleAudit.setRoleId(roleId);
                    sysUserroleAudit.setStatus(UserStatus.LOCKED.getCode());
                    sysUserroleAudit.setCreator(ShiroKit.getUser().getAccount());
                    sysUserroleAudit.setCreateTime(new Date());
                    sysUserroleAudit.setLastTime(new Date());
                    sysUserroleAudit.setOperationType(ADD);

                    sysUserroleAuditList.add(sysUserroleAudit);
                }
            }
            if (ToolUtil.isNotEmpty(sysUserroleAuditList)) {
                // 批量插入的list不能为空
                sysUserroleAuditService.saveBatch(sysUserroleAuditList, sysUserroleAuditList.size());
            }
        } else {
            // **************如果关闭了用户角色审核**************
            // 直接删掉关联表记录再新增
            sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("USER_ID", userId));
            List<SysUserRole> userRoleList = new ArrayList<SysUserRole>();
            // 当前操作时间
            Date currentDate = new Date();
            // 当前操作人
            String currentUser = ShiroKit.getUser().getAccount();
            for (String roleId : roleIds) {
                if (ToolUtil.isEmpty(roleId)) {
                    continue;
                }
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setGrantedRole(roleId);
                sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                sysUserRole.setCreateTime(currentDate);
                sysUserRole.setLastTime(currentDate);
                sysUserRole.setCreator(currentUser);
                sysUserRole.setLastEditor(currentUser);

                userRoleList.add(sysUserRole);
            }
            if (ToolUtil.isNotEmpty(userRoleList)) {
                sysUserRoleService.saveBatch(userRoleList, userRoleList.size());
            }
        }
        // 如果需要发送邮件
        if (globalProperties.getCreateUserSendEmail() && sysUsers.getEMail() != null) {
            CreateUserSendMailUtil.sendMail(sysUsers, pass);
        }
        return userId;
    }

    /**
     * 修改用户
     *
     * @param map
     * @Title: editUser
     * @author: LiangDong
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tip editUser(Map<String, Object> map) {
        // 用户id
        String userId = map.get("userId").toString();
        // 角色id
       // String[] roleIds = map.get("roleIds").toString().split(",");
        // 角色id
        String[] roleIds = (String[])((String[])map.get("roleIds"));
        // 用户 实体
        SysUsers sysUsers = (SysUsers) map.get("sysUsers");
        //提示信息
        Tip tip = new SuccessTip();

        // 更新角色信息
        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isRoleAudit()) {
            // **************如果开启了用户角色审核**************
            // 找到用户在审核表中的记录
            List<SysUserroleAudit> auditList = sysUserroleAuditMapper.getHasReviewList(userId);
            // 找到用户在关联表中的有效记录
            List<SysUserRole> connList = sysUserRoleMapper.selectList(
                    new QueryWrapper<SysUserRole>().eq("USER_ID", userId).eq("ADMIN_OPTION", Whether.YES.getValue()));
            // 找出新增的，删除的，不变的
            List<String> adds = new ArrayList<String>();
            List<String> dels = new ArrayList<String>();
            // 先循环这次修改之前关联表中的记录
            for (SysUserRole sysUserRole : connList) {
                // 是否相同
                boolean isSame = false;
                for (String roleId : roleIds) {
                    if (ToolUtil.isEmpty(roleId)) {
                        // 如果是空，跳过本次循环
                        continue;
                    }
                    if (roleId.equals(sysUserRole.getGrantedRole())) {
                        // 如果相同
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    // 如果不相同
                    dels.add(sysUserRole.getGrantedRole());
                }
            }

            // 先循环这次修改后选中的角色id
            for (String roleId : roleIds) {
                if (ToolUtil.isEmpty(roleId)) {
                    continue;
                    // 如果是空，说明之前关联的都删除了，在上面的循环中已经添加了，跳过本次循环
                }
                boolean isSame = false;
                for (SysUserRole sysUserRole : connList) {
                    if (roleId.equals(sysUserRole.getGrantedRole())) {
                        // 相同的都一样
                        isSame = true;
                        break;
                    }
                }
                if (!isSame) {
                    // 如果不同，说明是新增的
                    if (!SysUserAndRole.PUBLIC_ROLE.getValue().equals(roleId)) {
                        // 如果不是公用角色
                        adds.add(roleId);
                    }
                }
            }

            // 对于新增的
            if (ToolUtil.isNotEmpty(adds)) {
                // 要判断审核表中是否存在
                boolean isExist = false;
                for (String add : adds) {
                    for (SysUserroleAudit sysUserroleAudit : auditList) {
                        if (add.equals(sysUserroleAudit.getRoleId())) {
                            isExist = true;
                            // 如果审核表中存在
                            // 就将操作更新为添加，状态更新为未审核
                            sysUserroleAudit.setOperationType(ADD);
                            sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                            sysUserroleAudit.setLastTime(new Date());
                            sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                            sysUserroleAuditMapper.update(sysUserroleAudit, new QueryWrapper<SysUserroleAudit>()
                                    .eq("USER_ID", userId).eq("ROLE_ID", sysUserroleAudit.getRoleId()));
                            break;
                        }
                    }

                    if (!isExist) {
                        // 如果审核表中不存在
                        // 就要新增一条记录
                        SysUserroleAudit sysUserroleAudit = new SysUserroleAudit();
                        sysUserroleAudit.setUserId(userId);
                        sysUserroleAudit.setRoleId(add);
                        sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                        sysUserroleAudit.setOperationType(ADD);
                        sysUserroleAudit.setCreateTime(new Date());
                        sysUserroleAudit.setLastTime(new Date());
                        sysUserroleAudit.setCreator(ShiroKit.getUser().getAccount());
                        sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                        sysUserroleAuditMapper.insert(sysUserroleAudit);
                    }

                }

            }

            // 对于删除的
            if (ToolUtil.isNotEmpty(dels)) {
                // 要判断审核表中是否存在
                boolean isExist = false;
                for (String del : dels) {
                    for (SysUserroleAudit sysUserroleAudit : auditList) {
                        if (del.equals(sysUserroleAudit.getRoleId())) {
                            isExist = true;
                            // 如果审核表中存在
                            // 就将操作更新为删除，状态更新为未审核
                            sysUserroleAudit.setOperationType(DELETE);
                            sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                            sysUserroleAudit.setLastTime(new Date());
                            sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                            sysUserroleAuditMapper.update(sysUserroleAudit, new QueryWrapper<SysUserroleAudit>()
                                    .eq("USER_ID", userId).eq("ROLE_ID", sysUserroleAudit.getRoleId()));
                            break;
                        }
                    }

                    if (!isExist) {
                        // 如果审核表中不存在
                        // 就要新增一条记录
                        SysUserroleAudit sysUserroleAudit = new SysUserroleAudit();
                        sysUserroleAudit.setUserId(userId);
                        sysUserroleAudit.setRoleId(del);
                        sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                        sysUserroleAudit.setOperationType(DELETE);
                        sysUserroleAudit.setCreateTime(new Date());
                        sysUserroleAudit.setLastTime(new Date());
                        sysUserroleAudit.setCreator(ShiroKit.getUser().getAccount());
                        sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                        sysUserroleAuditMapper.insert(sysUserroleAudit);
                    }
                }
            }

        } else {
            // **************如果关闭了用户角色审核**************
            // 删掉用户的缓存
            sysRolesService.delRedisAuthInfo(userId);
            // 直接删掉关联表记录再新增
            sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("USER_ID", userId));
            List<SysUserRole> userRoleList = new ArrayList<SysUserRole>();
            // 当前操作时间
            Date currentDate = new Date();
            // 当前操作人
            String currentUser = ShiroKit.getUser().getAccount();
            for (String roleId : roleIds) {
                if (ToolUtil.isEmpty(roleId)) {
                    continue;
                }
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setGrantedRole(roleId);
                sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                sysUserRole.setCreateTime(currentDate);
                sysUserRole.setLastTime(currentDate);
                sysUserRole.setCreator(currentUser);
                sysUserRole.setLastEditor(currentUser);

                userRoleList.add(sysUserRole);
            }
            if (ToolUtil.isNotEmpty(userRoleList)) {
                sysUserRoleService.saveBatch(userRoleList, userRoleList.size());
            }
        }
        // 如果开启了全局审核和用户审核
        SysUsersAudit sysUsersAudit = new SysUsersAudit();
        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isUserAudit()) {
            CopyPropertieUtils.copyProperties(sysUsersAudit, sysUsers);
            sysUsersAudit.setCurrentStatus(AuditConstant.USER_AUDIT_EDIT);
            sysUsersAudit.setIsAudit(AuditConstant.USER_NOT_AUDIT);
            sysUsersAudit.setCreateTime(new Date());
            sysUsersAuditMapper.insert(sysUsersAudit);
            // ip审核表中存入数据
            this.saveUserAuditIP(userId, sysUsers.getAccessLoginIp(), AuditConstant.USER_AUDIT_EDIT);
            tip.setMessage(PermitTipConstants.UPDATE_SUCCESS_WAIT_REVIEW);
            return tip;
        } else {
            //先查询一下老数据,如果原来不是激活状态，修改后变为激活状态。那么将最后一次登录时间设置为当前时间
            SysUsers old = sysUsersMapper.selectById(sysUsers.getUserId());
            if (!AuditConstant.ORGAN_PASS.equals(old.getAccountStatus()) &&
                    AuditConstant.ORGAN_PASS.equals(sysUsers.getAccountStatus())) {
                sysUsers.setLastLoginTime(new Date());
            }
            // 更新用户表
            sysUsersMapper.updateById(sysUsers);
            // 先刪除所有的用戶ip
            iSysUserIpService.remove(new QueryWrapper<SysUserIp>().eq("USER_ID", userId));
            this.saveUserIP(userId, sysUsers.getAccessLoginIp());
            tip.setMessage(PermitTipConstants.UPDATE_SUCCESS);
            return tip;
        }
    }

    /**
     * 变更所属部门
     *
     * @param map
     * @Title: changeOrg
     * @author: LiangDong
     */
    @Override
    public Tip changeOrg(Map<String, Object> map) {
        // 用户名
        String userId = map.get("userId").toString();
        // 所属公司结构编码
        String corporationId = map.get("corporationId").toString();
        // 所属权限组织编码
        String permitId = map.get("permitId").toString();
        // 提示信息
        Tip tip = new SuccessTip();
        SysUsers sysUsers = sysUsersMapper.selectById(userId);
        sysUsers.setCorporationId(corporationId);
        sysUsers.setDepartmentId(corporationId);
        sysUsers.setPermissionStruId(permitId);
        // 如果开启了审核
        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isUserAudit()) {
            SysUsersAudit sysUsersAudit = new SysUsersAudit();
            CopyPropertieUtils.copyProperties(sysUsersAudit, sysUsers);
            // 设置为转移，未审核
            sysUsersAudit.setIsAudit(AuditConstant.USER_NOT_AUDIT);
            sysUsersAudit.setCreateTime(new Date());
            sysUsersAudit.setCurrentStatus(AuditConstant.USER_AUDIT_TRANS);
            if (sysUsersAuditMapper.insert(sysUsersAudit) == 1) {

                tip.setMessage(PermitTipConstants.MOVE_SUCCESS_WAIT_REVIEW);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "变更失败！");
            }
        } else {
            if (sysUsersMapper.updateById(sysUsers) == 1) {
                tip.setMessage(PermitTipConstants.MOVE_SUCCESS);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "变更失败！");
            }
        }
    }

    /**
     * 删除用户
     *
     * @param sysUser
     * @Title: delUser
     * @author: LiangDong
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = Cache.AuthorizationInfo, key = "'" + CacheKey.CACHE_AUTH_INFO + "'+#sysUser.userId")
    public Tip delUser(SysUsers sysUser) {
        Tip tip = new SuccessTip();
        // 如果开启了用户审核
        SysUsersAudit sysUsersAudit = new SysUsersAudit();
        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isUserAudit()) {
            CopyPropertieUtils.copyProperties(sysUsersAudit, sysUser);
            sysUsersAudit.setIsAudit(AuditConstant.USER_NOT_AUDIT);
            sysUsersAudit.setCurrentStatus(AuditConstant.USER_AUDIT_DEL);
            sysUsersAudit.setCreateTime(new Date());
            if (sysUsersAuditMapper.insert(sysUsersAudit) == 1) {
                tip.setMessage(PermitTipConstants.DELETE_SUCCESS_WAIT_REVIEW);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), PermitTipConstants.DELETE_FAIL);
            }
        } else {
            // 做逻辑删除，不做物理删除
            // 将用户状态改为删除
            sysUser.setAccountStatus(UserStatus.DELETE.getCode());
            sysUser.setEmployeeId("");
            // 修改用户组织机构
            sysStruMapper.updatePrincipal(sysUser.getUserId());
            // 删除用户角色审核
            this.isAuditService.remove(new QueryWrapper<SysUserroleAudit>().eq("USER_ID", sysUser.getUserId()));
            // 删除用户ip
            iSysUserIpService.remove(new QueryWrapper<SysUserIp>().eq("USER_ID", sysUser.getUserId()));

            if (sysUsersMapper.updateById(sysUser) == 1) {
                tip.setMessage(PermitTipConstants.DELETE_SUCCESS);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), PermitTipConstants.DELETE_FAIL);
            }
        }
    }

    /**
     * 判断用户名是否已存在
     *
     * @param userAccount
     * @return
     * @Title: isExistAccount
     * @author: LiangDong
     */
    @Override
    public boolean isExistAccount(String userAccount) {
        boolean flag = false;
        List<Map<String, Object>> result = sysUsersMapper.isExistAccount(userAccount);
        if (result.size() > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 获取指定用户的信息
     *
     * @param userId
     * @return
     * @Title: getUserInfo
     * @author: LiangDong
     */
    @Override
    public ArrayList<Object> getUserInfo(String userId) {
        ArrayList<Object> result = new ArrayList<>();
        // 用户一般信息
        SysUsers user = sysUsersMapper.selectById(userId);
        // 用户关联角色
        List<SysUserRole> userRole = sysUserRoleMapper.selectList(
                new QueryWrapper<SysUserRole>().eq("USER_ID", userId).eq("ADMIN_OPTION", UserStatus.OK.getCode()));
        // 代理用户
        List<SysUserProxy> userProxy = sysUserProxyMapper
                .selectList(new QueryWrapper<SysUserProxy>().eq("USER_ID", userId));
        // 被代理用户
        List<SysUserProxy> byProxy = sysUserProxyMapper
                .selectList(new QueryWrapper<SysUserProxy>().eq("PROXY_USER_ID", userId));
        List<String> userIps = iSysUserIpService.selectUserIp(userId);
        String ips = "";
        if (userIps != null && userIps.size() > 0) {
            for (String sysUserIp : userIps) {
                ips = ips + sysUserIp + ",";
            }
            ips = ips.substring(0, ips.length() - 1);
        }
        result.add(user);
        result.add(userRole);
        result.add(userProxy);
        result.add(byProxy);
        result.add(userIps);
        result.add(ips);
        return result;
    }

    /**
     * 判断用户是否是第一次登录
     *
     * @param userId
     * @return
     * @Title: isFirstLogin
     * @author: LiangDong
     */
    @Override
    public boolean isFirstLogin(String userId) {
        boolean flag = false;
        List<SysGetBackPassword> getBackPwd = sysGetBackPasswordMapper
                .selectList(new QueryWrapper<SysGetBackPassword>().eq("USER_ID", userId));
        if (ToolUtil.isEmpty(getBackPwd)) {
            // 如果没有设置找回密码问题，说明是第一次登录
            flag = true;
        }
        return flag;
    }

    /**
     * 判断用户是否需要修改密码
     *
     * @param userId
     * @return
     * @Title: isPwdOverdue
     * @author: LiangDong
     */
    @Override
    public boolean isPwdOverdue(String userId) {
        boolean flag = false;
        List<SysPasswordHist> list = sysPasswordHistMapper.lastCreateTime(userId);
        if (list.size() >= 1 && ToolUtil.isNotEmpty(list)) {
            // 最近一次的密码修改记录
            SysPasswordHist sysPasswordHist = list.get(0);
            String currentTime = DateUtil.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            // 得到最近一次修改密码的时间和当前时间的天数差
            long days = DateUtil.getDaySub(sdf.format(sysPasswordHist.getCreateTime()), currentTime);
            if (days >= globalProperties.getPasswordHist()) {
                flag = true;
            }
        } else {
            // 说明是第一次登录，要修改密码
            flag = true;
        }
        return flag;
    }

    /**
     * 根据id获取用户实体
     *
     * @param userId
     * @return
     * @Title: getUser
     * @author: LiangDong
     */
    @Override
    public SysUsers getUser(String userId) {
        return sysUsersMapper.selectById(userId);
    }

    /**
     * 获取用户之前设置的密码
     *
     * @param userId
     * @return
     * @Title: getPwdHist
     * @author: LiangDong
     */
    @Override
    public List<SysPasswordHist> getPwdHist(String userId) {
        return sysPasswordHistMapper.selectList(new QueryWrapper<SysPasswordHist>().eq("USER_ID", userId));
    }

    /**
     * 修改密码
     *
     * @param sysUsers
     * @Title: updatePwd
     * @author: LiangDong
     */
    @Override
    public void updatePwd(SysUsers sysUsers) {
        // 更新用户表
        SysUsers user = sysUsersMapper.selectById(sysUsers.getUserId());
        // 密码
        user.setPassword(sysUsers.getPassword());
        // 密码修改时间
        user.setPswdUptTime(new Date());
        // 密码生命周期之后的日期，如3个月后
        String pwdExtinctTime = DateUtil.getAfterDayDate(globalProperties.getPasswordHist() + "");
        // 密码失效时间
        user.setPswdTime(DateUtil.parseTime(pwdExtinctTime));
        user.updateById();

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
     * 设置找回密码问题和答案
     *
     * @param map
     * @Title: setGetBackPwd
     * @author: LiangDong
     */
    @Override
    public boolean setGetBackPwd(Map<String, Object> map) {
        String userId = map.get("userId").toString();
        String ques = map.get("ques").toString();
        String key = map.get("key").toString();

        SysGetBackPassword sysGetBackPassword = new SysGetBackPassword();
        sysGetBackPassword.setUserId(userId);
        sysGetBackPassword.setpQues(ques);
        sysGetBackPassword.setpKey(key);
        return iSysGetBackPasswordService.saveOrUpdate(sysGetBackPassword);
    }

    /**
     * 重置用户密码
     *
     * @param map
     * @return
     * @Title: resetAllPwd
     * @author: LiangDong
     */

    @Override
    public long resetAllPwd(Map<String, Object> map) {
        // 重置密码
        String newPwd = map.get("newPwd").toString();
        String[] userIds = (String[]) map.get("userIds");
        long count = 0;
        // 锁定判断接口;
        LoginLock loginLock = SpringContextHolder.getBean(LoginLock.class);
        for (String userId : userIds) {
            if (ToolUtil.isNotEmpty(userId)) {
                SysUsers sysUsers = new SysUsers();
                sysUsers.setPassword(newPwd);
                count += sysUsersMapper.update(sysUsers, new QueryWrapper<SysUsers>().eq("USER_ID", userId));
                // 清除密码错误缓存
                loginLock.removeUserCache(userId);
            }
        }

        return count;
    }

    /**
     * 保存用户排序
     *
     * @param treeArr
     * @Title: saveUserOrder
     * @author: LiangDong
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveUserOrder(JSONArray treeArr) {
        List<SysUsers> sysUsersList = new ArrayList<SysUsers>();
        for (int i = 0; i < treeArr.size(); i++) {
            JSONObject json = new JSONObject();
            json = treeArr.getJSONObject(i);
            String id = json.getString("id");
            String order = json.getString("struOrder");
            SysUsers sysUsers = new SysUsers();
            sysUsers.setUserId(id);
            sysUsers.setUserOrder(new BigDecimal(order));
            sysUsersList.add(sysUsers);
        }
        // 批量操作的list不能为空
        return ToolUtil.isNotEmpty(sysUsersList) && super.updateBatchById(sysUsersList, sysUsersList.size());
    }

    /**
     * 获取当前组织机构下最大排序值
     *
     * @param corporationId
     * @return
     * @Title: getMaxOrder
     * @author: LiangDong
     */
    @Override
    public Long getMaxOrder(String corporationId) {
        return sysUsersMapper.getMaxOrder(corporationId);
    }

    /**
     * 复制角色
     *
     * @param copyFrom
     * @param copyTo
     * @Title: copyRole
     * @author: LiangDong
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean copyRole(String copyFrom, String copyTo) {
        // 查出要复制角色的用户所拥有的角色
        List<SysUserRole> rolesFrom = sysUserRoleMapper.selectList(
                new QueryWrapper<SysUserRole>().eq("USER_ID", copyFrom).eq("ADMIN_OPTION", Whether.YES.getValue()));

        // 查出要复制到的用户所拥有的角色
        List<SysUserRole> rolesTo = sysUserRoleMapper.selectList(
                new QueryWrapper<SysUserRole>().eq("USER_ID", copyTo).eq("ADMIN_OPTION", Whether.YES.getValue()));

        /**
         * 找出需要新增的角色
         */
        List<String> adds = new ArrayList<String>();
        for (SysUserRole from : rolesFrom) {
            if (ToolUtil.isEmpty(from.getGrantedRole())) {
                continue;
                // 如果是空，说明之前关联的都删除了，在上面的循环中已经添加了，跳过本次循环
            }
            boolean isSame = false;
            for (SysUserRole to : rolesTo) {
                if (from.getGrantedRole().equals(to.getGrantedRole())) {
                    // 相同的都一样
                    isSame = true;
                    break;
                }
            }
            if (!isSame) {
                // 如果不同，说明是新增的
                if (!SysUserAndRole.PUBLIC_ROLE.getValue().equals(from.getGrantedRole())) {
                    // 如果不是公用角色
                    adds.add(from.getGrantedRole());
                }
            }
        }

        /**
         * 判断用户的角色是否包含不相容角色
         * 且同属于一个不相容角色集中的个数超过基数-1
         */
        int size=adds.size();
        String[] toRolesArray = new String[rolesTo.size()];
        String[] addArray = (String[])adds.toArray(new String[size]);
        for (int i = 0;i < rolesTo.size(); i++){
            toRolesArray[i] = rolesTo.get(i).getGrantedRole();
        }
        int arryLen1=addArray.length;//获取第一个数组长度
        int arryLen2=toRolesArray.length;//获取第二个数组长度
        addArray= Arrays.copyOf(addArray,arryLen1+ arryLen2);//把第一个数组扩大
        System.arraycopy(toRolesArray, 0, addArray, arryLen1,arryLen2 );//将两个数组进行合并
        if (iSysConfRolesService.isIncludeConfRole(addArray)) {
            return false;
        }

        if (globalProperties.isRoleAudit()) {
            // **************如果开启了用户角色审核**************
            // 将要新增的角色保存到审核表中
            // 找到用户在审核表中的记录
            List<SysUserroleAudit> auditList = sysUserroleAuditMapper.getHasReviewList(copyTo);
            if (ToolUtil.isNotEmpty(adds)) {
                // 要判断审核表中是否存在
                boolean isExist = false;
                for (String add : adds) {
                    for (SysUserroleAudit sysUserroleAudit : auditList) {
                        if (add.equals(sysUserroleAudit.getRoleId())) {
                            isExist = true;
                            // 如果审核表中存在
                            // 就将操作更新为添加，状态更新为未审核
                            sysUserroleAudit.setOperationType(ADD);
                            sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                            sysUserroleAudit.setLastTime(new Date());
                            sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                            sysUserroleAuditMapper.update(sysUserroleAudit, new QueryWrapper<SysUserroleAudit>()
                                    .eq("USER_ID", copyTo).eq("ROLE_ID", sysUserroleAudit.getRoleId()));
                            break;
                        }
                    }
                    if (!isExist) {
                        // 如果审核表中不存在
                        // 就要新增一条记录
                        SysUserroleAudit sysUserroleAudit = new SysUserroleAudit();
                        sysUserroleAudit.setUserId(copyTo);
                        sysUserroleAudit.setRoleId(add);
                        sysUserroleAudit.setStatus(UserRoleStatus.LOCKED.getCode());
                        sysUserroleAudit.setOperationType(ADD);
                        sysUserroleAudit.setCreateTime(new Date());
                        sysUserroleAudit.setLastTime(new Date());
                        sysUserroleAudit.setCreator(ShiroKit.getUser().getAccount());
                        sysUserroleAudit.setLastEditor(ShiroKit.getUser().getAccount());
                        sysUserroleAuditMapper.insert(sysUserroleAudit);
                    }
                }
            }
        } else {
            // **************如果关闭了用户角色审核**************
            List<SysUserRole> userRoleList = new ArrayList<SysUserRole>();
            // 当前操作时间
            Date currentDate = new Date();
            // 当前操作人
            String currentUser = ShiroKit.getUser().getAccount();
            for (String roleId : adds) {
                if (ToolUtil.isEmpty(roleId)) {
                    continue;
                }
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(copyTo);
                sysUserRole.setGrantedRole(roleId);
                sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                sysUserRole.setCreateTime(currentDate);
                sysUserRole.setLastTime(currentDate);
                sysUserRole.setCreator(currentUser);
                sysUserRole.setLastEditor(currentUser);

                userRoleList.add(sysUserRole);
            }
            if (ToolUtil.isNotEmpty(userRoleList)) {
                sysUserRoleService.saveBatch(userRoleList, userRoleList.size());
            }
        }
        return true;
    }

    /**
     * 获取用户列表
     *
     * @param page
     * @param userAccount
     * @param userName
     * @return
     * @Title: getUserList
     */
    @Override
    public Page<SysUsers> getUserList(Page<SysUsers> page, String userAccount, String userName) {
        page.setRecords(sysUsersMapper.getUserList(page, userAccount, userName));
        return page;
    }

    /**
     * 根据角色id获取用户数据
     * 返回数数据 带有组织机构
     *
     * @param roleId
     * @return
     */
    @Override
    public List<JSTreeModel> getUserTreeByRole(String roleId) {
        return sysUsersMapper.getUserTreeByRole(roleId);
    }

    @Override
    public List<JSTreeModel> getLazyUserTreeByRole(String roleId) {
        return sysUsersMapper.getLazyUserTreeByRole(roleId);
    }

    /**
     * 更新长时间不登录的用户状态
     */
    @Override
    public void updateUserStatus() {
        SysBaseConfig noLogin = iSysBaseConfigService.getSysBaseConfig(SysBaseConfigConstant.NOLOGIN_DAY);
        SysBaseConfig userAccountStatus = iSysBaseConfigService
                .getSysBaseConfig(SysBaseConfigConstant.USER_ACCOUNT_STATUS);
        String days = noLogin.getConfigValue();
        String status = userAccountStatus.getConfigValue();
        Instant now = Instant.now();
        Instant before = now.minus(Duration.ofDays(Integer.valueOf(days)));
        Date dateBefore = Date.from(before);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp previous = Timestamp.valueOf(df.format(dateBefore));
        sysUsersMapper.updateUserStatus(previous, status);
    }

    /**
     * 验证二次验证的密码是否正确
     *
     * @param userName
     * @param pass
     * @return
     */
    @Override
    public boolean reChecking(String reUrl, String userName, String pass) {
        SysUsers sysUsers = super.getOne(new QueryWrapper<SysUsers>().eq("USER_ACCOUNT", userName));
        String totpKey = sysUsers.getTotpKey();
        return abstractOTPCredentialsMatcher.verify(totpKey, pass, hussarProperties.getTotpOffsetMin());
    }

    /**
     * 注销用户
     *
     * @param sysUser 用户实体
     * @Title: cancelUser
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = Cache.AuthorizationInfo, key = "'" + CacheKey.CACHE_AUTH_INFO + "'+#sysUser.userId")
    public Tip cancelUser(SysUsers sysUser) {
        // 如果开启了用户审核
        SysUsersAudit sysUsersAudit = new SysUsersAudit();
        //提示信息
        Tip tip = new SuccessTip();
        if (globalProperties.isCriticalOperationsAudit() && globalProperties.isUserAudit()) {
            CopyPropertieUtils.copyProperties(sysUsersAudit, sysUser);
            sysUsersAudit.setIsAudit(AuditConstant.USER_NOT_AUDIT);
            sysUsersAudit.setCurrentStatus(AuditConstant.USER_AUDIT_CANCEL);
            sysUsersAudit.setCreateTime(new Date());
            if (sysUsersAuditMapper.insert(sysUsersAudit) == 1) {
                tip.setMessage(PermitTipConstants.CANCEL_SUCCESS_WAIT_REVIEW);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), PermitTipConstants.CANCEL_FAIL);
            }
        } else {
            sysUser.setAccountStatus(UserStatus.CANCEL.getCode());
            sysUser.setEmployeeId("");
            // 删除用户角色审核
            this.isAuditService.remove(new QueryWrapper<SysUserroleAudit>().eq("USER_ID", sysUser.getUserId()));
            if (sysUsersMapper.updateById(sysUser) == 1) {
                tip.setMessage(PermitTipConstants.CANCEL_SUCCESS);
                return tip;
            } else {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), PermitTipConstants.CANCEL_FAIL);
            }
        }
    }

    @Override
    public void exportData(List<String> idList, HttpServletResponse response) {
        List<SysUsers> users = listByIds(idList);
        List<String> employeeIds = new ArrayList();
        for (SysUsers sysUsers : users)
        {
            String employeeId = sysUsers.getEmployeeId();
            if (employeeId != null) {
                employeeIds.add(employeeId);
            }
        }
        Object strus = new ArrayList();
        if (employeeIds.size() > 0) {
            strus = this.sysStruMapper.selectBatchIds(employeeIds);
        }
        List<SysUserRole> userRoles = this.sysUserRoleService.listByIds(idList);
        Map<String, Object> data = new HashMap();
        data.put("users", users);
        data.put("userRoles", userRoles);
        data.put("strus", strus);
        data.put("export_type", "user");

        byte[] serializeData = SerializeUtils.serialize(data);

        String fileName = "user_" + DateUtil.format(new Date(), "yyyyMMdd_HHmmss") + ".hussar";

        DataExportUtils.byteToFile(serializeData, fileName, response);
    }

    /**
     * 用户数据导入
     * @Title: importData
     * @author: WangBinBin
     * @param content
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tip importData(byte[] content) {
        // 反序列化获取文件内容
        Map<String, Object> data = (Map<String, Object>) SerializeUtils.deserialize(content);
        String export_type = (String) data.get(Constants.EXPORT_TYPE);
        if (!Constants.USER_TYPE.equals(export_type)) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "您导入的是" + Constants.EXPORT_MAP.get(export_type)
                    + ",请导入" + Constants.EXPORT_MAP.get(Constants.USER_TYPE));
        }
        List<SysUsers> users = (List<SysUsers>) data.get("users");
        List<SysUserRole> userRoles = (List<SysUserRole>) data.get("userRoles");// 用户角色数据
        List<SysStru> strus = (List<SysStru>) data.get("strus");// 人员数据
        // 数据保存到数据库
        Map<String, Integer> userCount = insertOrUpdateList(users);
        Map<String, Integer> userRoleCount = insertOrUpdateList(userRoles);
        Map<String, Integer> strusCount = insertOrUpdateList(strus);
        Tip success = new SuccessTip();
        success.setMessage("导入成功。" + "用户新增数据" + userCount.get("insert") + "条，更新数据" + userCount.get("update")
                + "条；用户角色关系新增数据" + userRoleCount.get("insert") + "条，更新数据" + userRoleCount.get("update") + "条。");
        return success;
    }

    /**
     * 获取组织机构用户树
     * @param isExport 是否导出
     * @param parentId 节点id
     * @return
     */

    @Override
    public List<JSTreeModel> getUserTree(String isExport, String parentId) {

        String newParentId = parentId;
        ShiroUser shiroUser = ShiroKit.getUser();
        String empolyeeId = shiroUser.getEmployeeId();
        int level = 0; // 结构层级
        boolean isGradeAdmin = shiroUser.isGradeadmin();
        if (isGradeAdmin) { // 当前用户是分级管理员时
            String struLevel = this.sysStruMapper.selectStruLevel(empolyeeId);
            if (ToolUtil.isNotEmpty(struLevel)) {
                level = Integer.valueOf(struLevel).intValue();
            }
        }

        List<JSTreeModel> result = new ArrayList<>();
        // 拼入根节点
        JSTreeModel jsTreeModel = new JSTreeModel();
        jsTreeModel.setId(Constants.ROOT_NODE_ID);
        jsTreeModel.setCode(Constants.ROOT_NODE_ID);
        jsTreeModel.setText("系统用户");
        jsTreeModel.setParent(Constants.ROOT_NODE_PARENT);
        jsTreeModel.setType("isRoot");
        if (isGradeAdmin) {
            jsTreeModel.getState().put("gradeDisabled", true);
        }

        // 节点id
        // String parentId = super.getPara("parentId");

        // 删选过后的树信息
        List<JSTreeModel> list = new ArrayList<>();
        // 数据库里存的树信息
        List<JSTreeModel> list1 = new ArrayList<>();

        // 是否为懒加载
        if (globalProperties.isTreeLazyLoad() && ToolUtil.isNotEmpty(newParentId)) {
            // 首次加载将#改为11，因为根节点是手动拼接，没有存入数据库
            if (ToolUtil.equals(Constants.ROOT_NODE_PARENT, newParentId)) {
                newParentId = Constants.ROOT_NODE_ID;
                result.add(jsTreeModel);
            }
            list1 = this.sysOrgManageService.getUserTree(newParentId);
        } else {
            result.add(jsTreeModel);
            list1 = sysOrgManageService.getUserTree();
        }

        // 当前用户分级管理员时，删选树信息
        if (isGradeAdmin) { // 当前用户是分级管理员时
            list = this.sysOrgManageService.getGradeStruTree(list1, level);// 获取当前分级管理员能看到的组织机构树
        } else {
            list = list1;
        }

        result.addAll(list);

        // String isExport = super.getPara("isExport");
        if (Boolean.parseBoolean(isExport)) {
            for (JSTreeModel model : result) {
                String type = model.getType();
                if (type != null && "USER".equals(type)) {
                    model.setState(false, false, false);
                } else {
                    model.setState(false, false, true);
                }
            }

        }

        return result;
    }

    /**
     *  新增用户处理
     * @param sysUsers 人员信息
     * @param roleIds  关联的角色id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tip addUser(SysUsers sysUsers, String roleIds) {
        // 登录时不区分大小写
        if (!hussarProperties.getLoginUpperOpen()) {
            sysUsers.setUserAccount(sysUsers.getUserAccount().toUpperCase());
        }

        /**
         * 判断登录账号是否已存在
         */
        if (this.isExistAccount(sysUsers.getUserAccount())) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), PermitTipConstants.ADD_FAIL_USER_EXIST);
        }

        // 判断人员有没有在审核中
        QueryWrapper<SysStruAudit> wrapper = new QueryWrapper<>();
        wrapper.eq("real_stru_id", sysUsers.getEmployeeId()).eq("state", AuditConstant.ORGAN_NOT_AUDITED);
        List<SysStruAudit> list = this.iSysStruAuditService.list(wrapper);
        if (list.size() > 0) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(),
                    PermitTipConstants.ADD_FAIL_THIS_PERSON_PROHIBIT_UPDATE);
        }

        /**
         * 判断用户的角色是否包含不相容角色
         * 且同属于一个不相容角色集中的个数超过基数-1
         */
        String[] ids = roleIds.split(",");
        if (iSysConfRolesService.isIncludeConfRole(ids)) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(),
                    PermitTipConstants.ADD_FAIL_THE_ROLE_NO_ALLOW_EXCEED_ROLE_SETS);
        }

        /**
         * 完善用户信息
         */
        sysUsers.setDepartmentId(sysUsers.getCorporationId());
        // 用户排序字段填充
        // 获取当前机构下的最大排序值
        Long order = this.getMaxOrder(sysUsers.getCorporationId());
        if (ToolUtil.isEmpty(order)) {
            order = 1L;
        } else {
            order = order + 1L;
        }
        BigDecimal userOrder = BigDecimal.valueOf(order);
        sysUsers.setUserOrder(userOrder);
        // 用户密码填充
        sysUsers.setCreateTime(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        sysUsers.setTotpKey(abstractOTPCredentialsMatcher.getRandomSecretBase32());
        // 主键
        sysUsers.setUserId(IdWorker.get32UUID());
        Map<String, Object> map = new HashMap<>(5);
        map.put("sysUsers", sysUsers);
        map.put("roleIds", roleIds);
        // 将新增用户的id返回前台
        String userId = this.addUser(map);
        Tip successTip = new SuccessTip();
        successTip.setMessage(userId);
        return successTip;
    }

    @Override
    public Tip addUserRole(String userId, String roleIds) {
        String[] ids = roleIds.split(",");
        if (this.iSysConfRolesService.isIncludeConfRole(ids)) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "新增失败！（所选角色不允许超过不相容角色集的基数！）");
        } else {
            int var8;
            if (this.globalProperties.isCriticalOperationsAudit() && this.globalProperties.isRoleAudit()) {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setGrantedRole(SysUserAndRole.PUBLIC_ROLE.getValue());
                sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setLastTime(new Date());
                this.sysUserRoleMapper.insert(sysUserRole);
                List<SysUserroleAudit> sysUserroleAuditList = new ArrayList();
                if (ids.length > 0) {
                    String[] var15 = ids;
                    int var16 = ids.length;

                    for(var8 = 0; var8 < var16; ++var8) {
                        String roleId = var15[var8];
                        if (!ToolUtil.isEmpty(roleId) && !SysUserAndRole.PUBLIC_ROLE.getValue().equals(roleId)) {
                            SysUserroleAudit sysUserroleAudit = new SysUserroleAudit();
                            sysUserroleAudit.setUserId(userId);
                            sysUserroleAudit.setRoleId(roleId);
                            sysUserroleAudit.setStatus(UserStatus.LOCKED.getCode());
                            sysUserroleAudit.setCreator(ShiroKit.getUser().getAccount());
                            sysUserroleAudit.setCreateTime(new Date());
                            sysUserroleAudit.setLastTime(new Date());
                            sysUserroleAudit.setOperationType("1");
                            sysUserroleAuditList.add(sysUserroleAudit);
                        }
                    }
                }

                if (ToolUtil.isNotEmpty(sysUserroleAuditList)) {
                    this.sysUserroleAuditService.saveBatch(sysUserroleAuditList, sysUserroleAuditList.size());
                }
            } else {
                this.sysUserRoleMapper.delete((new QueryWrapper<SysUserRole>()).eq("user_id", userId));
                List<SysUserRole> userRoleList = new ArrayList();
                Date currentDate = new Date();
                String currentUser = ShiroKit.getUser().getAccount();
                String[] var7 = ids;
                var8 = ids.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    String roleId = var7[var9];
                    if (!ToolUtil.isEmpty(roleId)) {
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setGrantedRole(roleId);
                        sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                        sysUserRole.setCreateTime(currentDate);
                        sysUserRole.setLastTime(currentDate);
                        sysUserRole.setCreator(currentUser);
                        sysUserRole.setLastEditor(currentUser);
                        userRoleList.add(sysUserRole);
                    }
                }

                if (ToolUtil.isNotEmpty(userRoleList)) {
                    this.sysUserRoleService.saveBatch(userRoleList, userRoleList.size());
                    this.abstractPushMsgMatcher.insertOperation("userRole", "add", userRoleList, "");
                }
            }

            Tip successTip = new SuccessTip();
            successTip.setMessage("保存成功！");
            return successTip;
        }
    }

    @Override
    public Tip addUserInfo(SysUsers sysUsers) {
        if (!this.hussarProperties.getLoginUpperOpen()) {
            sysUsers.setUserAccount(sysUsers.getUserAccount().toUpperCase());
        }

        if (this.isExistAccount(sysUsers.getUserAccount())) {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "新增失败！（该用户名已存在！）");
        } else {
            QueryWrapper<SysStruAudit> wrapper = new QueryWrapper();
            wrapper.eq("REAL_STRU_ID", sysUsers.getEmployeeId()).eq("STATE", "0");
            List<SysStruAudit> list = this.iSysStruAuditService.list(wrapper);
            if (list.size() > 0) {
                return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "当前人员存在未审核的申请，禁止新增！");
            } else {
                sysUsers.setDepartmentId(sysUsers.getCorporationId());
                sysUsers.setSecurityLevel(new BigDecimal(1));
                Long order = this.getMaxOrder(sysUsers.getCorporationId());
                if (ToolUtil.isEmpty(order)) {
                    order = 1L;
                } else {
                    order = order + 1L;
                }

                BigDecimal userOrder = BigDecimal.valueOf(order);
                sysUsers.setUserOrder(userOrder);
                sysUsers.setCreateTime(new Date());
                sysUsers.setTotpKey(this.abstractOTPCredentialsMatcher.getRandomSecretBase32());
                sysUsers.setUserId(IdWorker.get32UUID());
                Map<String, Object> map = new HashMap(5);
                map.put("sysUsers", sysUsers);
                String pass = "";
                if (this.globalProperties.getCreateUserSendEmail() && !this.globalProperties.getCreateUserUseDefaultPass()) {
                    pass = GeneratePassword.generate(this.globalProperties.getPwdRule());
                } else {
                    pass = this.globalProperties.getDefaultPassword();
                }

                String pwd = this.credentialsMatcher.passwordEncode(String.valueOf(pass).getBytes());
                sysUsers.setPassword(pwd);
                this.sysUsersMapper.insert(sysUsers);
                this.abstractPushMsgMatcher.insertOperation("user", "add", sysUsers, "");
                sysUsers.setAccountStatus(UserStatus.LOCKED.getCode());
                this.saveUserIP(sysUsers.getUserId(), sysUsers.getAccessLoginIp());
                String userId = sysUsers.getUserId();
                if (this.globalProperties.getCreateUserSendEmail() && sysUsers.getEMail() != null) {
                    CreateUserSendMailUtil.sendMail(sysUsers, pass);
                }

                Tip successTip = new SuccessTip();
                successTip.setMessage(userId);
                return successTip;
            }
        }
    }

    private Map<String, Integer> insertOrUpdateList(List<?> objects) {
        Map<String, Integer> result = new HashMap();
        Integer update = 0;
        Integer insert = 0;
        if (CollectionUtils.isEmpty(objects)) {
            result.put("update", update);
            result.put("insert", insert);
            return result;
        } else {
            try {
                SqlSession batchSqlSession = this.sqlSessionBatch();
                Throwable var6 = null;

                try {
                    int size = objects.size();

                    for(int i = 0; i < size; ++i) {
                        Object obj = objects.get(i);
                        if (obj instanceof SysUsers) {
                            SysUsers user = (SysUsers)obj;
                            if (this.updateById(user)) {
                                update = update + 1;
                            } else {
                                this.save(user);
                                insert = insert + 1;
                            }
                        }

                        if (obj instanceof SysUserRole) {
                            SysUserRole userRole = (SysUserRole)obj;
                            if (this.sysUserRoleService.update(userRole, (new QueryWrapper<SysUserRole>()).eq("USER_ID", userRole.getUserId()).eq(true, "GRANTED_ROLE", userRole.getGrantedRole()))) {
                                update = update + 1;
                            } else {
                                this.sysUserRoleService.save(userRole);
                                insert = insert + 1;
                            }
                        }

                        if (obj instanceof SysStru) {
                            SysStru sysStru = (SysStru)obj;
                            if (SqlHelper.retBool(this.sysStruMapper.updateById(sysStru))) {
                                update = update + 1;
                            } else {
                                this.sysStruMapper.insert(sysStru);
                                insert = insert + 1;
                            }
                        }

                        if (i >= 1 && i % size == 0) {
                            batchSqlSession.flushStatements();
                        }
                    }

                    batchSqlSession.flushStatements();
                } catch (Throwable var21) {
                    var6 = var21;
                    throw var21;
                } finally {
                    if (batchSqlSession != null) {
                        if (var6 != null) {
                            try {
                                batchSqlSession.close();
                            } catch (Throwable var20) {
                                var6.addSuppressed(var20);
                            }
                        } else {
                            batchSqlSession.close();
                        }
                    }

                }
            } catch (Throwable var23) {
                throw new MybatisPlusException("Error: Cannot execute insertOrUpdateBatch Method. Cause", var23);
            }

            result.put("update", update);
            result.put("insert", insert);
            return result;
        }
    }

    /**
     * 保存用户允许登录的IP
     *
     * @param userId        用户id
     * @param accessLoginIp 允许登录的ip
     * @return
     */
    private boolean saveUserIP(String userId, String accessLoginIp) {
        boolean flag = false;
        List<SysUserIp> sysUserIps = new ArrayList<>();
        if (StringUtils.isNotEmpty(accessLoginIp)) {
            List<String> list = Arrays.asList(accessLoginIp.split(","));
            for (String ip : list) {
                SysUserIp sysUserIp = new SysUserIp();
                sysUserIp.setUserId(userId);
                sysUserIp.setUserIp(ip);
                sysUserIps.add(sysUserIp);
            }
        }
        if (sysUserIps.size() > 0) {
            flag = iSysUserIpService.saveBatch(sysUserIps);
        }
        return flag;
    }

    /**
     * 保存用户允许登录的IP到审核表中
     *
     * @param userId        用户id
     * @param accessLoginIp 允许登录的ip
     * @return
     */
    private boolean saveUserAuditIP(String userId, String accessLoginIp, String currentStatus) {
        boolean flag = false;
        List<SysUserIpAudit> listAudit = new ArrayList<>();
        if (StringUtils.isNotEmpty(accessLoginIp)) {
            List<String> list = Arrays.asList(accessLoginIp.split(","));
            for (String ip : list) {
                SysUserIpAudit sysUserIpAudit = new SysUserIpAudit();
                sysUserIpAudit.setUserId(userId);
                sysUserIpAudit.setUserIp(ip);
                // 设置为未审核
                sysUserIpAudit.setIsAudit(AuditConstant.USER_NOT_AUDIT);
                // 设置状态
                sysUserIpAudit.setCurrentStatus(currentStatus);
                listAudit.add(sysUserIpAudit);
            }
        }
        if (listAudit.size() > 0) {
            flag = iSysUserIpAuditService.saveBatch(listAudit);
        }
        return flag;
    }
}
