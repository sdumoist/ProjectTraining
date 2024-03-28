package com.jxdinfo.doc.common.util;

import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class PrivilegeUtil {

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
    private static DocGroupService docGroupService = appCtx.getBean(DocGroupService.class);
    private static ISysUserRoleService sysUserRoleService = appCtx.getBean(ISysUserRoleService.class);
    private static ISysUsersService iSysUsersService = appCtx.getBean(ISysUsersService.class);

    private PrivilegeUtil(){

    };
    /**
     * 获取当前人所属于的所有群组及部门信息集合
     * @return
     */
    public static String[] getPremission() {
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String[] per = {};
        List<String> list = new ArrayList<String>();
        //查询当前登录人所在的所有群组
        list.addAll(docGroupService.getPremission(shiroUser.getId()));
        List<String> roleList = shiroUser.getRolesList();
        //添加人员ID
        list.addAll(roleList);
        list.add(shiroUser.getId());
        list.add(shiroUser.getName());
        //添加部门ID
        list.add(shiroUser.getDeptId());
        //添加全体成员的ID
        list.add("allpersonflag");
        //加入一个公开的权限，为了能查询到历史数据，暂时保留
        list.add("1");
        per = list.toArray(new String[list.size()]);
        
        return per;
    }
    /**
     * 获取当前人所属于的所有群组及部门信息集合
     * @return
     */
    public static String[] getPremission(String userId) {
        SysUsers user = iSysUsersService.getById(userId);
        String[] per = {};
        List<String> list = new ArrayList<String>();
        //查询当前登录人所在的所有群组
        list.addAll(docGroupService.getPremission(userId));
        //添加人员ID
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //添加人员ID
        list.addAll(roleList);
        list.add(user.getUserId());
        list.add(user.getUserName());
        //添加部门ID
        list.add(user.getDepartmentId());
        //添加全体成员的ID
        list.add("allpersonflag");
        //加入一个公开的权限，为了能查询到历史数据，暂时保留
        list.add("1");
        per = list.toArray(new String[list.size()]);

        return per;
    }
    /**
     * 获取当前人所属于的所有群组
     * @return
     */
    public static List getPrivilegeList() {
        //获取当前登录人
         ShiroUser shiroUser = ShiroKit.getUser();
//        String[] per = {};
        //查询当前登录人所在的所有群组
        List<String> list = docGroupService.getPremission(shiroUser.getId());
        list.add(shiroUser.getId());
        //加入一个公开的权限
        list.add("publicPermission");
        return list;
    }
}
