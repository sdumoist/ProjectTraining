package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.AesJsUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.system.dao.SysUserMapper;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysOnline;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.theme.service.IThemeService;
import com.jxdinfo.hussar.core.log.HussarLogManager;
import com.jxdinfo.hussar.core.log.factory.LogTaskFactory;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_COLLECTION;
import static com.jxdinfo.doc.mobile.constants.ApiConstants.IS_LOGIN;


/**
 * 主页最新动态显示
 */
@Component
public class ApiLoginServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = IS_LOGIN;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Resource
    private PersonalOperateService operateService;
    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    @Autowired
    private FileTool fileTool;

    @Autowired
    private DocInfoService docInfoService;

    /**
     * 用户主题服务 接口
     */
    @Resource
    private IThemeService themeService;
    /**/
   @Resource
    private SysUsersMapper sysUsersMapper;

    /**
     * 在线用户历史 服务类
     */
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除
     * @Title: execute
     * @author: yjs
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        String userName = params.get("userId");
        String password = params.get("password");
        String testkey = params.get("key");

        try {
            password= AesJsUtil.aesDecrypt(password,testkey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userName, password.toCharArray());
        // 登陆
        try {
            currentUser.login(token);
            response.setSuccess(true);
            response.setData(true);  // 登录成功后复制session数据
            Session session = currentUser.getSession();
            final LinkedHashMap<Object, Object> attributes = new LinkedHashMap<Object, Object>();
            session = currentUser.getSession();
            for (final Object key : attributes.keySet()) {
                session.setAttribute(key, attributes.get(key));
            }

            ShiroUser shiroUser = ShiroKit.getUser();
            Session ses = ShiroKit.getSession();

            ses.setAttribute("sessionFlag", true);
            ses.setAttribute("shiroUser", shiroUser);
            ses.setAttribute("userId", shiroUser.getId());
            ses.setAttribute("theme", themeService.getUserTheme());

            // 更新在线用户(必须放在session中userId设置之后)
            iSysOnlineHistService.addRecord();

            // 添加登录日志(客户端地址、IP、登陆时间)
            Map<String, String> info = new HashMap<String, String>();
            info.put("sessionId", (String) ShiroKit.getSession().getId());// sessionId
            info.put("ip", HttpKit.getIp());// ip
            info.put("port", HttpKit.getPort());// 端口号
            info.put("host", HttpKit.getHost());// 主机名
            info.put("localIp", HttpKit.getLocalIp());// 服务器ip
            info.put("localPort", HttpKit.getLocalPort());// 服务器端口号
            info.put("localHost", HttpKit.getLocalHost());// 服务器主机名
            HussarLogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser,"5", info));

        } catch (Exception e) {
            response.setSuccess(true);
            response.setData(false);
            response.setMsg(e.getLocalizedMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }
}
