package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName: FrontShareController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/11/4
 * @Version: 1.0
 */
@Controller
@RequestMapping("/frontShare")
public class FrontShareController {
    @GetMapping("/list")
    public String index(Model model) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);

        return "/doc/front/personalcenter/myShare.html";
    }
}
