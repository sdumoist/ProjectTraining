package com.jxdinfo.doc.client.login;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.MD5Util;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 客户端登录
 * @author yjs
 */
@CrossOrigin
@RestController
@RequestMapping("/client")
public class LoginClientController {

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Resource
    private ISysUsersService iSysUsersService;

    @Resource
    private DocInfoService docInfoService;
    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private DocConfigService docConfigService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    @Resource
    private JWTUtil jwtUtil;

    /**
     * 客户端登录方法
     * @param user
     * @return object
     */
    @PostMapping("/login")
    public Object login(@RequestBody SysUsers user) {
        //获取密码并转化为MD5
        String password = user.getPassword();
        String passwordMd5 = null;
        try {
            passwordMd5 = MD5Util.getMD5New(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setPassword(passwordMd5);
        Map<String,Object> map = new HashMap<String,Object>();
        SysUsers sysUsers = this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).
                eq("USER_ACCOUNT", user.getUserName()).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
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
                //验证通过，获取token值
                Map token = jwtUtil.getToken(sysUsers);
                map.put("token", token);
                map.put("user", sysUsers);
                map.put("version", docConfigService.getConfigValueByKey("client_version"));
                map.put("enterprise",docConfigService.getConfigValueByKey("watermark_company"));
                String userId = sysUsers.getUserId();
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
        if(sysUsers!=null) {
            DocResourceLog docResourceLog = new DocResourceLog();
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(sysUsers.getUserId());
            docResourceLog.setOperateType(11);
            docResourceLog.setValidFlag("1");
            docResourceLog.setOrigin("client");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);//添加登录
        }
        return map;
    }

}