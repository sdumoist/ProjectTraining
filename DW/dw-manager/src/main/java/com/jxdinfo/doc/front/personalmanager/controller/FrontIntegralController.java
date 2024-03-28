package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 类的用途：跳转前台我的积分<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：zhongguangrui <br>
 */
@Controller
@RequestMapping("/frontIntegral")
public class FrontIntegralController {

    /**
     * 获取用户信息并跳转
     * @param model
     * @return
     */
    @GetMapping("/list")
    public String index(Model model) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);

        return "/doc/front/personalcenter/integral.html";
    }
}
