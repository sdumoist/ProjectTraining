package com.jxdinfo.doc.client.login;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.bsp.menu.service.ISysMenuManageService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.bsp.theme.service.IThemeService;

import com.jxdinfo.hussar.config.properties.EncryptTypeProperties;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.log.HussarLogManager;
import com.jxdinfo.hussar.core.log.factory.LogTaskFactory;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping(value = "/LoginCheck")
public class LoginCheck extends BaseController {
    @Resource
    private ISysUsersService iSysUsersService;
    @Resource
    private IThemeService themeService;
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;
    @Resource
    private ISysMenuManageService iSysMenuManageService;
    @Resource
    private GlobalProperties globalProperties;
    @Resource
    private EncryptTypeProperties encryptTypeProperties;
    @Autowired
    private CacheToolService cacheToolService;
    @Autowired
    private IFsFolderService fsFolderService;

    @Resource
    private DocInfoService docInfoService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    /**
     * 引入加密算法
     */
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;//存储加密算法抽象，密码需要通过这个进行加密后传值

    @RequestMapping(
            value = {"/login"}
    )
    public String loginVali(Model model) {
        String token =super.getPara("token");
        if(ToolUtil.isEmpty(token)){
            return BaseController.REDIRECT + "/login";
        }
        String flag =super.getPara("flag");

        LoginClient client = new LoginClient();
        String returnBody = client.getUserInfo(token);

        JSONObject jo = JSONObject.parseObject(returnBody);



        String username="";
        if(ToolUtil.isNotEmpty(jo.get("account").toString())){
            username = jo.get("account").toString();
        }else{
            ShiroKit.getSubject().logout();
            return BaseController.REDIRECT + "/login";
        }


        String host = encryptTypeProperties.getSecretFreeIp();
        //免密登录
        LoginNoPwd(username, host);
        ShiroUser shiroUser = ShiroKit.getUser();
        ShiroKit.getSession().setAttribute("sessionFlag", true);
        ShiroKit.getSession().setAttribute("shiroUser", shiroUser);
        ShiroKit.getSession().setAttribute("csrfFlag", true);
        ShiroKit.getSession().setAttribute("userId", shiroUser.getId());
        ShiroKit.getSession().setAttribute("theme", this.themeService.getUserTheme());
        this.iSysOnlineHistService.addRecord();
        Map<String, String> info = new HashMap();
        info.put("sessionId", (String)ShiroKit.getSession().getId());

        info.put("ip", HttpKit.getIp());
        info.put("port", HttpKit.getPort());
        info.put("host", HttpKit.getHost());
        info.put("localIp", HttpKit.getLocalIp());
        info.put("localPort", HttpKit.getLocalPort());
        info.put("localHost", HttpKit.getLocalHost());
        HussarLogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser, "05", info));
        SysUsers user = new SysUsers();
        user.setUserId(shiroUser.getId());
        user.setLastLoginTime(new Date());
        this.iSysUsersService.updateById(user);
        String userId = user.getUserId();
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
        DocResourceLog docResourceLog = new DocResourceLog();
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(11);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);//添加登录
        if(flag==null||flag.equals("")){
            return REDIRECT + "/";
        }else {
            String url = "";
            if(flag.equals("1")){

                try {
                    return  "forward:"+ "/personalcenter?menu=11&folderId=0808&folderName="+URLEncoder.encode("轻骑兵V8","utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return REDIRECT + "/";
                }
            }else if(flag.equals("2")){
                url ="/frontTopic/topicDetail?topicId=34dd69033e434e74af7725d76c316426&page=1&size=10";
               return  "forward:"+ url;
            }else{
                return REDIRECT + "/";
            }

        }

    }

    @RequestMapping(
            value = {"/logout"}
    )
    public void logOut() {
        ShiroKit.getSubject().logout();
    }


    public void LoginNoPwd(String username, String host) {//执行这个方法就是免密登录啦
        Subject currentUser = ShiroKit.getSubject();
        String password = username; //密码和用户名相同 ,必填项
        password = credentialsMatcher.passwordEncode(password.getBytes());
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), host);
        currentUser.login(token); //登录
        ShiroUser shiroUser = ShiroKit.getUser();
        ShiroKit.getSession().setAttribute("sessionFlag", true);
        ShiroKit.getSession().setAttribute("shiroUser", shiroUser);
        ShiroKit.getSession().setAttribute("userId", shiroUser.getId());
        ShiroKit.getSession().setAttribute("theme", themeService.getUserTheme());

    }


}
