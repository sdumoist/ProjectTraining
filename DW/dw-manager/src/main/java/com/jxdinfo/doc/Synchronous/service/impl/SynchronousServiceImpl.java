package com.jxdinfo.doc.Synchronous.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.Synchronous.model.SynchronousOrgan;
import com.jxdinfo.doc.Synchronous.service.SynchronousService;
import com.jxdinfo.doc.Synchronous.service.SysUsersService;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysStaff;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.ISysOrganService;
import com.jxdinfo.hussar.bsp.organ.service.ISysStaffService;
import com.jxdinfo.hussar.bsp.organ.service.ISysStruService;
import com.jxdinfo.hussar.bsp.permit.model.SysUserRole;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.common.constant.enums.SysUserAndRole;
import com.jxdinfo.hussar.common.constant.state.UserRoleStatus;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 同步组织机构和用户
 */
@Service
public class SynchronousServiceImpl implements SynchronousService {

    @Autowired
    private SysUsersService sysUsersService;

    @Resource
    private ISysUserRoleService ISysUserRoleService;

    @Autowired
    ISysOrganService iSysOrganService;

    @Autowired
    ISysStaffService iSysStaffService;

    @Autowired
    ISysStruService sysStruService;

    /**
     * 同步用户
     *
     * @return
     */
    @ResponseBody
    public JSON synchronousUser() {
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        try {

            // 同步stru、organ表中的用户
            synchronousOrgan("2");

            // 同步人员users表
            synchronousUserList();

            // 同步staff表
            synchronousStaffList();

        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("同步用户异常！");
        }
        JSONObject json = new JSONObject();
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步用户的时间是：" + (time2 - time1) + "ms");
        return json;
    }

    /**
     * 同步staff表
     *
     * @return
     */
    @ResponseBody
    public JSON synchronousStaffList() {
        System.out.print("==================同步staff表开始==================");
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        try {
            // 获取oracle数据库staff表人员数据
            List<SysStaff> oracleStaffList = sysUsersService.selectOracleStaffList();
            // 定义oracle表中staff表中的 id集合
            List<String> staffIdList = new ArrayList<>();

            // 定义oracle表中staff数据map <userId, SysStaff>
            Map<String, SysStaff> oracleStaffMap = new HashMap<String, SysStaff>();
            for (SysStaff staff : oracleStaffList) {
                staffIdList.add(staff.getStaffId());
                oracleStaffMap.put(staff.getStaffId(), staff);
            }

            // 根据id 获取本地数据staff表数据 (这些数据是oracle和本地数据库都有的)
            List<SysStaff> staffList = sysUsersService.selectStaffList(staffIdList);
            // 定义本地数据库人员数据map <userId, SysStaff>
            Map<String, SysStaff> staffMap = new HashMap<String, SysStaff>();

            // 循环组装数据
            for (SysStaff staff1 : staffList) {
                staffMap.put(staff1.getStaffId(), staff1);
            }

            // 需要插入的数据
            List<SysStaff> insertStaff = new ArrayList<SysStaff>();
            // 需要更新的数据
            List<SysStaff> updateStaff = new ArrayList<SysStaff>();

            // 循环人员数据
            for (Map.Entry<String, SysStaff> m : oracleStaffMap.entrySet()) {
                // 当oracle数据库中的数据 在本地数据库中也存在时 判断md5值是否相同
                // 当oracle数据库中的数据 在本地数据库中不存在时 新增数据
                String staffId = m.getKey();
                if (staffMap.containsKey(staffId)) {
                    // md5值不相同 则需要更新本地数据库
                    String oracleStaffMd5 = JSONObject.toJSONString(m.getValue());
                    String staffMd5 = JSONObject.toJSONString(staffMap.get(staffId));
                    if (!StringUtils.equals(oracleStaffMd5, staffMd5)) {
                        updateStaff.add(m.getValue());
                    }
                } else {
                    insertStaff.add(m.getValue());
                }
            }

            // 新增
            if (insertStaff != null && insertStaff.size() > 0) {
                iSysStaffService.saveOrUpdateBatch(insertStaff);
            }

            // 更新
            if (updateStaff != null && updateStaff.size() > 0) {
                iSysStaffService.saveOrUpdateBatch(updateStaff);
            }

            // 删除多余的数据
            iSysStaffService.remove(new QueryWrapper<SysStaff>().notIn("STAFF_ID", staffIdList));
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("同步staff表异常！");
        }
        JSONObject json = new JSONObject();
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步staff表的时间是：" + (time2 - time1) + "ms");
        return json;
    }

    /**
     * 同步人员users表
     *
     * @return
     */
    @ResponseBody
    public JSON synchronousUserList() {
        System.out.print("==================同步人员开始==================");
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        try {
            // 获取oracle数据库users表人员数据
            List<SysUsers> oracleUserList = sysUsersService.selectOracleUserList();
            // 定义oracle表中user表中的 id集合
            List<String> userIdList = new ArrayList<>();

            // 定义oracle表中用户数据map <userId, organ>
            Map<String, SysUsers> oracleUserMap = new HashMap<String, SysUsers>();
            for (SysUsers user : oracleUserList) {
                userIdList.add(user.getUserId());
                oracleUserMap.put(user.getUserId(), user);
            }

            // 根据id 获取本地数据库users人员数据 (这些数据是oracle和本地数据库都有的)
            List<SysUsers> userList = sysUsersService.selectUserList(userIdList);
            // 定义本地数据库人员数据map <userId, SysUsers>
            Map<String, SysUsers> userMap = new HashMap<String, SysUsers>();

            // 循环组装数据
            for (SysUsers user1 : userList) {
                userMap.put(user1.getUserId(), user1);
            }

            // 需要插入的人员
            List<SysUsers> insertUser = new ArrayList<SysUsers>();
            // 需要更新的人员
            List<SysUsers> updateUser = new ArrayList<SysUsers>();
            // 需要插入的人员角色
            List<SysUserRole> insertUserRole = new ArrayList<SysUserRole>();

            // 循环人员数据
            for (Map.Entry<String, SysUsers> m : oracleUserMap.entrySet()) {
                // 当oracle数据库中的数据 在本地数据库中也存在时 判断md5值是否相同
                // 当oracle数据库中的数据 在本地数据库中不存在时 新增数据
                String userId = m.getKey();
                if (userMap.containsKey(userId)) {
                    // md5值不相同 则需要更新本地数据库
                    String oracleUserMd5 = JSONObject.toJSONString(m.getValue());
                    String userMd5 = JSONObject.toJSONString(userMap.get(userId));
                    if (!StringUtils.equals(oracleUserMd5, userMd5)) {
                           updateUser.add(m.getValue());
                    }

                } else {
                    SysUsers sysUsers = m.getValue();
                    insertUser.add(sysUsers);
                    if ("superadmin".equals(sysUsers.getUserAccount())) {
                        // 如果新增的用户是superadmin，则赋予超级管理员的角色
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setGrantedRole(SysUserAndRole.SUPERADMIN_ROLE.getValue());
                        sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                        sysUserRole.setCreateTime(new Date());
                        sysUserRole.setLastTime(new Date());
                        insertUserRole.add(sysUserRole);
                    } else {
                        // 给新增的用户一个公用的角色
                        SysUserRole sysUserRole = new SysUserRole();
                        sysUserRole.setUserId(userId);
                        sysUserRole.setGrantedRole(SysUserAndRole.PUBLIC_ROLE.getValue());
                        sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                        sysUserRole.setCreateTime(new Date());
                        sysUserRole.setLastTime(new Date());
                        insertUserRole.add(sysUserRole);
                    }
                }
            }

            // 新增人员和角色
            if (insertUser != null && insertUser.size() > 0) {
                sysUsersService.saveOrUpdateBatch(insertUser);
                ISysUserRoleService.saveOrUpdateBatch(insertUserRole);
            }

            // 更新人员
            if (updateUser != null && updateUser.size() > 0) {
                sysUsersService.saveOrUpdateBatch(updateUser);
            }

            // 删除多余的用户、角色
            sysUsersService.deleteUserRole(userIdList);
            sysUsersService.deleteUser(userIdList);
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("同步人员users表异常！");
        }
        JSONObject json = new JSONObject();
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步人员users表的时间是：" + (time2 - time1) + "ms");
        return json;
    }

    /**
     * 同步组织机构
     * isEmployee 1:同步组织机构  2:同步用户
     *
     * @return
     */
    @ResponseBody
    public JSON synchronousOrgan(String isEmployee) {
        if(StringUtils.equals(isEmployee,"1")){
            System.out.print("===================同步组织机构开始==============");
        }else{
            System.out.print("===================同步组织机构表中的人员开始==============");
        }
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        try {
            // 获取oracle数据库组织机构数据
            List<SynchronousOrgan> oracleOrganList = sysUsersService.selectOracleOrganList(isEmployee);
            // 循环oracle数据库数据 组装成map<id,SynchronousOrgan> 并设置md5值 组装idList
            List<String> struIdList = new ArrayList<>();
            List<String> organIdList = new ArrayList<>();
            Map<String, SynchronousOrgan> oracleOrganMap = new HashMap<String, SynchronousOrgan>();
            for (SynchronousOrgan organ : oracleOrganList) {
                struIdList.add(organ.getStruId());
                organIdList.add(organ.getOrganId());
                String organMd5 = md5Password(organ.toString());
                organ.setMd5(organMd5);
                oracleOrganMap.put(organ.getStruId(), organ);
            }
            // 根据id组装查询现有数据库数据  循环数据 组装map<id, SynchronousOrgan> 并设置md5值
            List<SynchronousOrgan> OrganList = sysUsersService.selectOrganList(struIdList, isEmployee);
            Map<String, SynchronousOrgan> organMap = new HashMap<String, SynchronousOrgan>();

            for (SynchronousOrgan organ1 : OrganList) {
                String md5 = md5Password(organ1.toString());
                organ1.setMd5(md5);
                organMap.put(organ1.getStruId(), organ1);
            }

            List<SynchronousOrgan> insertOrgan = new ArrayList<SynchronousOrgan>();
            List<SynchronousOrgan> updateOrgan = new ArrayList<SynchronousOrgan>();

            // 循环oracle数据库数据 组织新增数据 更新数据
            for (Map.Entry<String, SynchronousOrgan> m : oracleOrganMap.entrySet()) {
                // 当oracle数据库中的数据 在本地数据库中也存在时 判断md5值是否相同
                // 当oracle数据库中的数据 在本地数据库中不存在时 新增数据
                String struId = m.getKey();
                if (organMap.containsKey(m.getValue().getStruId())) {
                    // md5值不相同 则需要更新本地数据库
                    if (!StringUtils.equals(organMap.get(struId).getMd5(), m.getValue().getMd5())) {
                         updateOrgan.add(m.getValue());
                    }
                } else {
                    insertOrgan.add(m.getValue());
                }
            }

            // 新增本地组织机构
            if (insertOrgan != null && insertOrgan.size() > 0) {
                sysUsersService.insertStru(insertOrgan);
                sysUsersService.insertOrgan(insertOrgan);
            }
            // 更新本地组织机构
            if (updateOrgan != null && updateOrgan.size() > 0) {
                sysUsersService.updateStru(updateOrgan);
                sysUsersService.updateOrgan(updateOrgan);
            }
            // 删除多余的数据
            if (StringUtils.equals(isEmployee, "1")) {
                sysStruService.remove(new QueryWrapper<SysStru>().ne("STRU_TYPE", "9").notIn("STRU_ID", struIdList));
                iSysOrganService.remove(new QueryWrapper<SysOrgan>().ne("ORGAN_TYPE", "9").notIn("ORGAN_ID", organIdList));
            } else {
                sysStruService.remove(new QueryWrapper<SysStru>().eq("STRU_TYPE", "9").notIn("STRU_ID", struIdList));
                iSysOrganService.remove(new QueryWrapper<SysOrgan>().eq("ORGAN_TYPE", "9").notIn("ORGAN_ID", organIdList));
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("组织机构插入异常！");
        }
        JSONObject json = new JSONObject();
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        if(StringUtils.equals(isEmployee,"1")){
            System.out.println("同步组织机构的时间是：" + (time2 - time1) + "ms");
        }else{
            System.out.println("同步组织机构表中的人员时间是：" + (time2 - time1) + "ms");
        }

        return json;
    }

    /**
     * 同步单个用户数据
     *
     * @return
     */
    @ResponseBody
    public JSON synchronousOneUserDate(String userId,String account) {
        System.out.print("===================同步单个用户数据开始==============");
        long time1 = System.currentTimeMillis();
        boolean flag = true;
        try {

            // 根据id查询当前用户信息(oracle数据库)
            SysUsers sysUsers = (SysUsers) this.sysUsersService.selectUser(account, UserStatus.DELETE.getCode(), "2");
            // 如果有employeeid
            if (StringUtils.isNotEmpty(sysUsers.getEmployeeId())) {
                // 根据id查询当前用户的组织机构信息、staff表信息(oracle数据库)
                List<SysStru> strus = sysUsersService.selectUserStruData(sysUsers.getEmployeeId());
                if (strus != null && strus.size() > 0) {
                    List<String> struIds = new ArrayList<String>();
                    List<String> organIds = new ArrayList<String>();

                    for (SysStru stru : strus) {
                        struIds.add(stru.getStruId());
                        organIds.add(stru.getOrganId());
                    }
                    List<SysOrgan> organs = sysUsersService.selectUserOrganData(organIds);
                    List<SysStaff> staffs = sysUsersService.selectUserStaffData(struIds);

                    // 本地数据库插入或者更新
                    if(strus!=null && strus.size()>0){
                        sysStruService.saveOrUpdateBatch(strus);
                        iSysOrganService.saveOrUpdateBatch(organs);
                    }
                    if(staffs!=null && staffs.size()>0){
                        iSysStaffService.saveOrUpdateBatch(staffs);
                    }
                }
            }
            // 如果本地没有此用户 同步数据
            SysUsers suser = (SysUsers) this.sysUsersService.getById(userId);
            if(suser == null){
                sysUsersService.save(sysUsers);
                // 给新增用户一个公用角色
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(sysUsers.getUserId());
                sysUserRole.setGrantedRole(SysUserAndRole.PUBLIC_ROLE.getValue());
                sysUserRole.setAdminOption(UserRoleStatus.OK.getCode());
                sysUserRole.setCreateTime(new Date());
                sysUserRole.setLastTime(new Date());
                ISysUserRoleService.save(sysUserRole);
            }else{
                sysUsersService.saveOrUpdate(sysUsers);
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("同步单个用户信息异常！");
        }
        JSONObject json = new JSONObject();
        json.put("isSuccess", flag);
        long time2 = System.currentTimeMillis();
        System.out.println("同步单个用户信息时间是：" + (time2 - time1) + "ms");
        return json;
    }

    /**
     * 生成32位md5码
     *
     * @return
     */
    public static String md5Password(String strAll) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(strAll.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }
            // 标准的md5加密后的结果
            return buffer.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}
