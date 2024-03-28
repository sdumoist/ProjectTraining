//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jxdinfo.hussar.system.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.bsp.menu.model.MenuInfo;
import com.jxdinfo.hussar.bsp.menu.service.ISysMenuManageService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysOnline;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.bsp.theme.service.IThemeService;
import com.jxdinfo.hussar.bsp.welcome.service.ISysWelcomeService;
import com.jxdinfo.hussar.common.constant.enums.Whether;
import com.jxdinfo.hussar.common.constant.factory.ConstantFactory;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.common.exception.InvalidKaptchaException;
import com.jxdinfo.hussar.common.exception.LoginGetParamException;
import com.jxdinfo.hussar.common.exception.TotpKeyException;
import com.jxdinfo.hussar.config.cas.ShiroCasConfiguration;
import com.jxdinfo.hussar.config.properties.EncryptTypeProperties;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.aop.NoRepeatMethod;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.base.tips.Tip;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.encrypt.CryptoUtil;
import com.jxdinfo.hussar.core.log.HussarLogManager;
import com.jxdinfo.hussar.core.log.factory.LogTaskFactory;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.support.StrKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.isc.conf.ShiroIscConfiguration;
import com.jxdinfo.hussar.isc.util.ISCTools;
import com.jxdinfo.hussar.otp.credential.AbstractOTPCredentialsMatcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class LoginController extends BaseController {
    @Resource
    private ISysUsersService iSysUsersService;
    @Resource
    private IThemeService themeService;
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;
    @Resource
    private ISysMenuManageService iSysMenuManageService;
    @Resource
    private ISysWelcomeService iSysWelcomeService;
    @Resource
    private GlobalProperties globalProperties;
    @Resource
    private HussarProperties hussarProperties;
    @Resource
    private SysStruMapper sysStruMapper;
    @Resource
    private DocInfoService docInfoService;
    @Value("${isProject.using}")
    private boolean projectFlag;
    @Value("${docbase.isRole}")
    private boolean isRole;
    @Value("${server.port}")
    private String port;
    @Value("${spring.profiles.active}")
    private String profiles;
    @Resource
    private ShiroCasConfiguration casConf;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private CacheToolService cacheToolService;
    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    @Resource
    private ShiroIscConfiguration iscConf;
    @Autowired
    private AbstractOTPCredentialsMatcher abstractOTPCredentialsMatcher;
    /**
     * 加密算法
     */
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;
    @Resource
    private EncryptTypeProperties encryptTypeProperties;

    @Resource
    private HussarCacheManager hussarCacheManager;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    public LoginController() {
    }
    @RequiresPermissions("sys:manager")
    @RequestMapping(
            value = {"${hussar.welcome-page}"},
            method = {RequestMethod.GET}
    )
    public String index(Model model) {
        ShiroUser shiroUser = ShiroKit.getUser();
        List rolesList;
        String userName;
        if (this.iscConf.isActive()) {
            rolesList = ISCTools.getMenuListByUserId(shiroUser.getId());
             userName = shiroUser.getName();
            userName = shiroUser.getId();
            model.addAttribute("userId", userName);
            model.addAttribute("userName", userName);
            model.addAttribute("menus", rolesList);
            model.addAttribute("changeTheme", this.globalProperties.isChangeTheme());
            model.addAttribute("firstLogin", "NO");
            model.addAttribute("changePwd", "NO");
            return this.iscConf.getWelcomePage();
        } else if (ToolUtil.isEmpty(shiroUser)) {
            // 项目标题
            Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
            model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

            // 是否显示客户端
            Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
            model.addAttribute("clientShow", clientShowMap.get("configValue"));

            return BaseController.REDIRECT + "/login";
        } else {
            rolesList = ConstantFactory.me().getRolesIdByUserId(shiroUser.getId());
            if (rolesList != null && rolesList.size() != 0) {
                List<MenuInfo> menus = this.iSysMenuManageService.getMenuByRoles(shiroUser.getId(), rolesList, profiles);
                userName = shiroUser.getName();
                String userId = shiroUser.getId();
                List<String> roleList = shiroUser.getRolesList();
                roleList.add(userId);
                model.addAttribute("userId", userId);
                model.addAttribute("userName", userName);
                model.addAttribute("menus", menus);
                model.addAttribute("changeTheme", this.globalProperties.isChangeTheme());
                model.addAttribute("welcomePage", this.iSysWelcomeService.getUserIndex(roleList));
                model.addAttribute("htmlpath", super.getPara("htmlpath"));
                model.addAttribute("businessId", super.getPara("businessId"));
                if (this.globalProperties.isForceChangePwd()) {
                    SysUsers sysUser = this.iSysUsersService.getUser(shiroUser.getId());
                    if (Whether.YES.getValue().equals(sysUser.getIsSys())) {
                        model.addAttribute("firstLogin", "NO");
                        model.addAttribute("changePwd", "NO");
                        return "/index.html";
                    }

                    if (this.iSysUsersService.isFirstLogin(shiroUser.getId())) {
                        model.addAttribute("firstLogin", "YES");
                    } else {
                        model.addAttribute("firstLogin", "NO");
                    }

                    if (this.iSysUsersService.isPwdOverdue(shiroUser.getId())) {
                        model.addAttribute("changePwd", "YES");
                    } else {
                        model.addAttribute("changePwd", "NO");
                    }
                } else {
                    model.addAttribute("firstLogin", "NO");
                    model.addAttribute("changePwd", "NO");
                }

                return "/index.html";
            } else {
                ShiroKit.getSubject().logout();
                // 项目标题
                Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
                model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

                // 是否显示客户端
                Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
                model.addAttribute("clientShow", clientShowMap.get("configValue"));
                return BaseController.REDIRECT + "/login";
            }
        }
    }

    @RequestMapping(
            value = {"/login"},
            method = {RequestMethod.GET}
    )
    public String login(Model model) {
        String welcomePage = this.hussarProperties.getWelcomePage();
        String loginHtml = this.hussarProperties.getLoginHtml();
        HttpServletRequest request = this.getHttpServletRequest();
        Enumeration paramNames = request.getParameterNames();

        String paramTips;
        do {
            if (!paramNames.hasMoreElements()) {

                Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
                model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

                // 是否显示客户端
                Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
                model.addAttribute("clientShow", clientShowMap.get("configValue"));

                paramTips = request.getParameter("tips");
                Object tips = request.getAttribute("tips");
                if (ToolUtil.isEmpty(tips)) {
                    if (ToolUtil.isNotEmpty(paramTips)) {
                        request.setAttribute("tips", paramTips);
                    } else {
                        request.setAttribute("tips", "");
                    }
                }

                if (tips == null) {
                    request.setAttribute("tips", "");
                }

                if (ToolUtil.isEmpty(loginHtml)) {
                    loginHtml = "/login.html";
                }

                if (StrKit.isNotEmpty(welcomePage)) {
                    ShiroKit.getSession().setAttribute("welcome_page", welcomePage);
                    ShiroKit.getSession().setAttribute("tips", tips);
                }

                if (!ShiroKit.isAuthenticated() && ShiroKit.getUser() == null) {
                    return loginHtml;
                }

                return REDIRECT + "/";
            }

            paramTips = (String)paramNames.nextElement();
        } while(!"username".equalsIgnoreCase(paramTips) && !"password".equalsIgnoreCase(paramTips) && !"cipher".equalsIgnoreCase(paramTips) && !"encrypted".equalsIgnoreCase(paramTips));

        throw new LoginGetParamException();
    }

    @RequestMapping(
            value = {"/login"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    @NoRepeatMethod(
            timeout = 500L
    )
    public Tip loginVali(@RequestBody Map<String, String> body) {
        String username = (String)body.get("username");
        String password = (String)body.get("cipher");
        String encrypted = (String)body.get("encrypted");
        String remember = (String)body.get("remember");
        if (StringUtils.isEmpty(password)) {
            password = encrypted;
        }

        password = CryptoUtil.decode(password);
        SysUsers sysUsers = (SysUsers)this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ACCOUNT", username).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        if (StringUtils.equals(username, "superadmin")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println("当前登录superadmin的ip地址: " + HttpKit.getIp() + "  操作时间: " + sdf.format(new Date()));
        }
        String totp;
        if (this.hussarProperties.getKaptchaOpen()) {
            totp = (String)body.get("kaptcha");
            String code = (String)super.getSession().getAttribute("KAPTCHA_SESSION_KEY");
            if (ToolUtil.isEmpty(totp) || !totp.equalsIgnoreCase(code)) {
                throw new InvalidKaptchaException(username, "验证码错误");
            }
        }

        if (this.hussarProperties.getTotpOpen()) {
            totp = (String)body.get("totp");
            if (sysUsers != null && !this.abstractOTPCredentialsMatcher.verify(sysUsers.getTotpKey(), totp, this.hussarProperties.getTotpOffsetMin())) {
                throw new TotpKeyException(username, "动态密码错误");
            }
        }

        Subject currentUser = ShiroKit.getSubject();
        Session session = currentUser.getSession();
        LinkedHashMap<Object, Object> attributes = new LinkedHashMap();
        Collection<Object> keys = session.getAttributeKeys();
        Iterator var11 = keys.iterator();

        Object key;
        while(var11.hasNext()) {
             key = var11.next();
            key = session.getAttribute(key);
            if (key != null) {
                attributes.put(key, key);
            }
        }

        currentUser.logout();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());
        if ("on".equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }

        currentUser.login(token);
        session = currentUser.getSession();
        Iterator var19 = attributes.keySet().iterator();

        while(var19.hasNext()) {
            key = var19.next();
            session.setAttribute(key, attributes.get(key));
        }

        ShiroUser shiroUser = ShiroKit.getUser();
        session.setAttribute("sessionFlag", true);
        session.setAttribute("csrfFlag", true);
        session.setAttribute("shiroUser", shiroUser);
        session.setAttribute("userId", shiroUser.getId());
        session.setAttribute("projectFlag", projectFlag);
        session.setAttribute("isRole", isRole);
        List<String> roleList = ShiroKit.getUser().getRolesList();

        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        session.setAttribute("adminFlag", adminFlag);
        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        session.setAttribute("url",url);
        session.setAttribute("theme", this.themeService.getUserTheme());
        if (sysUsers != null && "1".equals(sysUsers.getLoginTimeLimit())) {
            session.setAttribute("startTime", sysUsers.getAccessLoginStartTime());
            session.setAttribute("endTime", sysUsers.getAccessLoginEndTime());
        }

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        session.setAttribute("projectTitle", projectTitleMap.get("configValue"));

        // 是否显示客户端
        Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
        session.setAttribute("clientShow", clientShowMap.get("configValue"));

        // 是否显示联系方式
        Map<String, String> contactShowMap = frontDocInfoService.getConfigure("contact_show");
        session.setAttribute("contactShow", contactShowMap.get("configValue"));

        SysOnline online = this.iSysOnlineHistService.addRecord();
        session.setAttribute("online", online);
        Map<String, String> info = new HashMap();
        String token2 = getToken(sysUsers);
        info.put("token",token2);
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
        return SUCCESS_TIP;
    }

    @RequestMapping(
            value = {"/logout"},
            method = {RequestMethod.GET}
    )
    public String logOut() {
        ShiroKit.getSubject().logout();
        return BaseController.REDIRECT + "/login";

        // cas退出
        // return this.casConf.isActive() ? BaseController.REDIRECT + this.casConf.getServerLogoutUrl() + "?service=" + this.casConf.getLocalUrl() : BaseController.REDIRECT + "/login";

    }

    public String getToken(SysUsers user) {
        String token="";
        token= JWT.create().withAudience(user.getUserId())
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }

    @GetMapping("/getMessage")
    public String getMessage(){
        System.out.println("成功");
        return "";
    }
    public String getDeptIds(){
        List<String> orgList = new ArrayList<>();
        String deptId = ShiroKit.getUser().getDeptId();
        SysStru sysStru = sysStruMapper.selectById(deptId);
        if(sysStru != null){
            while (!"11".equals(sysStru.getParentId())) {
                orgList.add(sysStru.getStruId());
                sysStru = sysStruMapper.selectById(sysStru.getParentId());
                if(sysStru == null){
                    break;
                }
            }
        }
        String resultStr = "";
        for(int i=0;i<orgList.size();i++){
            if(i == orgList.size() -1 ){
                resultStr = resultStr + orgList.get(i);
            } else {
                resultStr = resultStr+ orgList.get(i) + ",";
            }

        }
		/*if(resultStr != null){
			resultStr = "( " + resultStr + " )";
		}*/
        return resultStr;
    }

    /**
     * 免密登录
     * @param username
     */
    private void LoginNoPwd(String username) {
        Subject currentUser = ShiroKit.getSubject();
        String password = username; //密码和用户名相同 ,必填项
        password = credentialsMatcher.passwordEncode(password.getBytes());
        // 免密登录
        String host = encryptTypeProperties.getSecretFreeIp();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), host);
        currentUser.login(token); //登录
        ShiroUser shiroUser = ShiroKit.getUser();
        Session session = ShiroKit.getSession();
        session.setAttribute("sessionFlag", true);
        session.setAttribute("csrfFlag", true);
        session.setAttribute("shiroUser", shiroUser);
        session.setAttribute("userId", shiroUser.getId());
        session.setAttribute("projectFlag", projectFlag);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        session.setAttribute("adminFlag", adminFlag);
        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        session.setAttribute("url",url);
        session.setAttribute("theme", this.themeService.getUserTheme());
        SysUsers sysUsers = (SysUsers)this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ACCOUNT", username).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        if (sysUsers != null && "1".equals(sysUsers.getLoginTimeLimit())) {
            session.setAttribute("startTime", sysUsers.getAccessLoginStartTime());
            session.setAttribute("endTime", sysUsers.getAccessLoginEndTime());
        }


        SysOnline online = this.iSysOnlineHistService.addRecord();
        session.setAttribute("online", online);
        Map<String, String> info = new HashMap();
        String token2 = getToken(sysUsers);
        info.put("token",token2);
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
    }

    @RequestMapping(
            value = {"/shareLogin"},
            method = {RequestMethod.POST}
    )
    @ResponseBody
    public String toLogin(String sharePath, HttpServletRequest request) {
        hussarCacheManager.setObject("shareLoginUrl", request.getSession().getId(), sharePath);
        return "success";
    }
}
