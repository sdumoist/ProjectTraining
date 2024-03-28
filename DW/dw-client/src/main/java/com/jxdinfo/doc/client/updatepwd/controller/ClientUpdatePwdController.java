package com.jxdinfo.doc.client.updatepwd.controller;

import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.hussar.bsp.permit.model.SysPasswordHist;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.constant.HttpCode;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * @ClassName: ClientUpdataPwdController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/16
 * @Version: 1.0
 */
@CrossOrigin
@RestController
@RequestMapping("/client/update")
public class ClientUpdatePwdController extends BaseController{

    @Resource
    private JWTUtil jwtUtil;

    /**
     * 用户管理服务 接口
     */
    @Resource
    private ISysUsersService iSysUsersService;

    /**
     * global 配置
     */
    @Resource
    private GlobalProperties globalProperties;

    /**
     * 存储加密算法抽象
     */
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;


    @RequestMapping(value = "/updatePwd")
    @ResponseBody
    public ApiResponse updatePwd(String oldPwd,String newPwd,String cPwd){

        /*String data = super.getPara("data").trim();

        data = CryptoUtil.decode(data);

        String[] params = data.split("&");

        //原密码
        String oldPwd = "";
        //新密码
        String newPwd = "";
        //确认密码
        String cPwd = "";

        for (String param : params) {
            String[] p = param.split("=");
            if ("old_pwd".equals(p[0])) {
                oldPwd = p[1];
            }
            if ("new_pwd".equals(p[0])) {
                newPwd = p[1];
            }
            if ("c_pwd".equals(p[0])) {
                cPwd = p[1];
            }
        }*/

        String regular = globalProperties.getPwdComplexityRegular().replaceAll("/", "");
        String hint = globalProperties.getUnmatchedHint();

        if (!newPwd.matches(regular)) {
            return ApiResponse.data(200,new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), hint),"");
        }

        if (!newPwd.equals(cPwd)) {
            return ApiResponse.data(200,new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "修改失败！（两次输入的密码不一致）"),"");
        }

        // 存储加密通用逻辑
        String oPwd = credentialsMatcher.passwordEncode(oldPwd.getBytes());
        // 存储加密通用逻辑
        String nPwd = credentialsMatcher.passwordEncode(newPwd.getBytes());

        String userId = jwtUtil.getSysUsers().getUserId();
        SysUsers user = iSysUsersService.getUser(userId);

        String userAccount = user.getUserAccount();

        if (newPwd.toLowerCase().contains(userAccount.toLowerCase())) {
            return ApiResponse.data(200,new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "修改失败！（密码包含用户名）"),"") ;
        }

        if (!oPwd.equals(user.getPassword())) {
            return ApiResponse.data(200,new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "修改失败！（原密码输入错误）"),"") ;
        }

        List<SysPasswordHist> list = iSysUsersService.getPwdHist(userId);
        if (ToolUtil.isEmpty(list) && globalProperties.getDefaultPassword().equals(newPwd)) {
            return ApiResponse.data(200,new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "修改失败！（新密码不能和默认密码相同）"),"") ;
        } else {
            for (SysPasswordHist sysPasswordHist : list) {
                if (nPwd.equals(sysPasswordHist.getPassword())) {
                    return ApiResponse.data(200, new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(),
                            "修改失败！（新密码不能和前" + globalProperties.getPwdRepeatTime() + "次的密码相同）"),"") ;

                }
            }
        }

        // 以上错误都没出现的话，才更新密码
        user.setPassword(nPwd);
        iSysUsersService.updatePwd(user);

        return ApiResponse.data(200,SUCCESS_TIP,"");
    }
}
