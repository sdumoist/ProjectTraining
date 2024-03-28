package com.jxdinfo.doc.mobileapi.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.MD5Util;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * 客户端登录
 * @author yjs
 */
@CrossOrigin
@RestController
@RequestMapping("/mobile")
public class LoginClientMobileController {
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Resource
    private ISysUsersService iSysUsersService;

    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRecordService integralRecordService;
    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private CacheToolService cacheToolService;

    @Resource
    private SysStruMapper sysStruMapper;
    @Autowired
    private DocConfigService docConfigService;
    @Autowired
    private PersonExtranetAccessService personExtranetAccessService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    @Resource
    private JWTUtil jwtMobileUtil;

    /**
     * 是否开启外网限制
     */
    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    /**
     * 客户端登录方法
     * @param user
     * @return object
     */
    @PostMapping("/login")
    public Object login(SysUsers user, HttpServletRequest request) {
        //获取密码并转化为MD5
        String password = user.getPassword();
        String passwordMd5 = null;
        try {
            passwordMd5 = MD5Util.getMD5New(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setPassword(password);
        Map<String,Object> map = new HashMap<String,Object>();
        SysUsers sysUsers = this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).
                eq("USER_ACCOUNT", user.getUserName()).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        System.out.println("================登录"+user.getUserName() +"================客户端地址"+ RemoteIpMobileUtil.getRemoteIp(request));

        //用户名为空登录失败
        if (sysUsers == null) {
            map.put("userNameMsg", "用户名不存在，请重新输入");
            map.put("pwdMsg", "");
        } else {
            //密码不正确登录失败
            if (!sysUsers.getPassword().toUpperCase().equals(user.getPassword().toUpperCase())) {
                map.put("userNameMsg", "");
                map.put("pwdMsg", "密码错误，请重新输入");
            } else {

                // 开启了外网访问限制
                /*if (!StringUtils.equals(sysUsers.getUserId(),"superadmin")&&StringUtils.equals(openExtranetLimit, "true")) {
                    // 用户是从外网访问的系统
                    if (RemoteIpUtil.isExtranetVisit(request)) {
                        // 检查用户是否有外网访问权限
                        // 没有外网访问权限  返回  userNameMsg: 您没有外网访问权限
                        String name = sysUsers.getUserName();
                       boolean exists = personExtranetAccessService.existsUser(name);
                        if (!exists){
                            map.put("userNameMsg","抱歉!您当前没有外网访问权限！");
                            map.put("pwdMsg", "");
                        }
                    }
                }*/

                //验证通过，获取token值
                Map token = jwtMobileUtil.getToken(sysUsers);
                String userId = sysUsers.getUserId();
                List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
                //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
                Integer adminFlag = CommonUtil.getAdminFlag(roleList);
                map.put("token", token);
                map.put("user", sysUsers);
                map.put("url", fsFolderService.getPersonPic(sysUsers.getUserId()));
                map.put("integral", integralRecordService.showIntegral(sysUsers.getUserId()));
                if(sysStruMapper.selectById(sysUsers.getDepartmentId())!=null){
                    map.put("deptName",  sysStruMapper.selectById(sysUsers.getDepartmentId()).getOrganAlias());
                }else{
                    map.put("deptName",  "金现代");
                }

                map.put("version", docConfigService.getConfigValueByKey("mobile_version"));
                map.put("enterprise",docConfigService.getConfigValueByKey("watermark_company"));
                map.put("isAdmin",adminFlag);
                QueryWrapper<FsFolder> wrapper = new QueryWrapper<>();
                List<FsFolder> list = fsFolderService.list(wrapper.eq("own_id", userId).
                        eq("parent_folder_id", "2bb61cdb2b3c11e8aacf429ff4208431"));
                cacheToolService.updateLevelCodeCache(userId);
                if (list == null||list.size()==0) {

                    FsFolder fsFolder = new FsFolder();

                    fsFolder.setFolderName("我的文件夹");
                    fsFolder.setOwnId(userId);
                    fsFolder.setIsEdit("1");
                    fsFolder.setVisibleRange("0");
                    fsFolder.setParentFolderId("2bb61cdb2b3c11e8aacf429ff4208431");
                    Date date = new Date();
                    Timestamp ts = new Timestamp(date.getTime());
                    fsFolder.setCreateTime(ts);
                    fsFolder.setUpdateTime(ts);
                    String folderId = UUID.randomUUID().toString().replaceAll("-", "");
                    fsFolder.setFolderId(folderId);
                    fsFolder.setCreateUserId(userId);
                    String folderParentId = fsFolder.getParentFolderId();
                    //生成levelCode
                    if (folderParentId != null && !"".equals(folderParentId)) {
                        FsFolder parentFolder = fsFolderService.getById(folderParentId);
                        String parentCode = parentFolder.getLevelCode();
                        String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                        fsFolder.setLevelCode(currentCode);
                    }
                    DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                    docFoldAuthority.setId(IdWorker.get32UUID());
                    docFoldAuthority.setFoldId(folderId);
                    docFoldAuthority.setAuthorType("0");
                    docFoldAuthority.setOperateType("2");
                    docFoldAuthority.setAuthorId(userId);

                    docFoldAuthorityService.save(docFoldAuthority);
                    //生成showOrder
                    String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
                    int num = Integer.parseInt(currentCode);
                    fsFolder.setShowOrder(num);
                    //保存目录信息
                    fsFolderService.save(fsFolder);
                    //保存权限信息
                }
            }
        }
        return map;
    }

}