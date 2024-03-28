package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 类的用途：跳转前台我的预览<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：yjs <br>
 */
@Controller
@RequestMapping("/frontRecycle")
public class FrontRecycleController {
    @GetMapping("/list")
    public String index(Model model) {
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/recycle.html";
    }
}
