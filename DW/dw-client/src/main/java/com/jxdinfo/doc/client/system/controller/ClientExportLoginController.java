package com.jxdinfo.doc.client.system.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.AesUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.doc.manager.system.service.SysUserService;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 运营支撑中直接登录方法
 * 2018-10-10
 * bjj
 */
@Controller
@RequestMapping("/client/export")
public class ClientExportLoginController extends BaseController {

    static final public Logger LOGGER = LogManager.getLogger(ClientExportLoginController.class);

    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;//存储加密算法抽象，密码需要通过这个进行加密后传值

    @Autowired
    private IFsFolderService fsFolderService;

    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    @Resource
    private DocInfoService docInfoService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * 在线用户历史 服务类
     */
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;
    /**
     * 文件统计接口
     */
    @Autowired
    private FileStatisticsService fileStatisticsService;

    /** 运营支撑接口服务类 */
    @Autowired
    private SysUserService sysUserService;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    //通过这个方法实现运营支撑中通过链接登录的方式
    @RequestMapping("/exportLogin")
    public String exportLogin(HttpServletRequest request, Model model)  throws Exception{
        //通过秘钥将获取的内容转化为用户名和密码

        String us = request.getParameter("us");
        LOGGER.info("找资源传输的参数：" + us);
        System.out.println("找资源传输的参数：" + us);
        us = URLDecoder.decode(us,"utf-8");
        LOGGER.info("找资源传输的参数：" + us);
        System.out.println("找资源传输的参数：" + us);
        LOGGER.info("找资源传输的参数：" + us);
        System.out.println("找资源传输的参数：" + us);
        String cotent = AesUtil.aesDecrypt(us);
        System.out.println("找资源传输的参数解析：" + cotent);
        String username = cotent.split("\\|\\|")[0];
        String password = cotent.split("\\|\\|")[1];
        String flag = sysUserService.checkUser(username,password.toUpperCase()).get(0);
        if("0".equals(flag)){
            return BaseController.REDIRECT + "/login";
        }
        Subject currentUser = ShiroKit.getSubject();
        // 重置Session
        // 获取session数据
        Session session = currentUser.getSession();
        final LinkedHashMap<Object, Object> attributes = new LinkedHashMap<Object, Object>();
        final Collection<Object> keys = session.getAttributeKeys();
        for (Object key : keys) {
            final Object value = session.getAttribute(key);
            if (value != null) {
                attributes.put(key, value);
            }
        }
        session.stop();
        //String username = "传入用户名";//必填项
        String passwordFlag = username ; //密码和用户名相同 ,必填项
        passwordFlag = credentialsMatcher.passwordEncode(passwordFlag.getBytes());
        String host = "192.168.1.1"; //必填项，和服务器上配置的  hussar.encryptType.secret-free-ip 一致

        UsernamePasswordToken token = new UsernamePasswordToken(username, passwordFlag.toCharArray(),host);
        token.setRememberMe(false);
        currentUser.login(token); //登录

        // 登录成功后复制session数据
        session = currentUser.getSession();
        for (final Object key : attributes.keySet()) {
            session.setAttribute(key, attributes.get(key));
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


        // 在session中放值
        ShiroUser shiroUser = ShiroKit.getUser();
        ShiroKit.getSession().setAttribute("sessionFlag", true);
        ShiroKit.getSession().setAttribute("shiroUser", shiroUser);
        ShiroKit.getSession().setAttribute("userId", shiroUser.getAccount());

        //如果没登录，就跳转到登录请求
        if (ToolUtil.isEmpty(ShiroKit.getUser())) {
            return BaseController.REDIRECT + "/login";
        }
        LOGGER.info("用户通过运营支撑登录：" + username);
        iSysOnlineHistService.addRecord();


        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        session.setAttribute("url",url);
        DocResourceLog docResourceLog = new DocResourceLog();
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(11);
        docResourceLog.setOrigin("client");
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);//添加登录
        return BaseController.REDIRECT + "/";
    }

}
