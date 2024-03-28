package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * 跳转常用目录
 */
@Controller
@RequestMapping("/frontCommonFolder")
public class FrontCommonFolderController {

    /**
     * 跳转常用目录
     *
     * @param model model类
     * @return string 返回路径
     */
    @GetMapping("/list")
    public String index(Model model, String openFileId, String filePath) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userId", userId);
        model.addAttribute("userName", userName);
        model.addAttribute("openFileId", openFileId);
        model.addAttribute("folderName", filePath);
        return "/doc/manager/commonfoldermanager/commonFolderManager.html";
    }

}
