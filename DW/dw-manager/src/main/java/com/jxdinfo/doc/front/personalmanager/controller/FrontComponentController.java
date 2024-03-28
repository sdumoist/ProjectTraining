package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 类的用途：跳转前台我的预览<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：yjs <br>
 */
@Controller
@RequestMapping("/frontComponent")
public class FrontComponentController {
    @GetMapping("/list")
    public String index(Model model, String style) {
        if(style!=null){
            model.addAttribute("style", style);
        }else {
            model.addAttribute("style", "");
        }
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/component.html";
    }
    @GetMapping("/myList")
    public String myList(Model model, String style, String check, String origin, String state, String type, String dept) {
        style = XSSUtil.xss(style);
        origin = XSSUtil.xss(origin);
        type = XSSUtil.xss(type);
        dept = XSSUtil.xss(dept);
        state = XSSUtil.xss(state);
        String regex = "^[-,0-9]+$";
        if(style!=null&&!"".equals(style)){
        if (Pattern.compile(regex).matcher(style).find() == false) {
         return null;
        }
        }
        if(origin!=null&&!"".equals(origin)){
        if (Pattern.compile(regex).matcher(origin).find() == false) {
            return null;
        }
        }
        if(type!=null&&!"".equals(type)) {
            if (Pattern.compile(regex).matcher(type).find() == false) {
                return null;
            }
        }
        if(style!=null){
            model.addAttribute("style", style);
        }else {
            model.addAttribute("style", "");
        }
        if(dept!=null){
            model.addAttribute("dept", dept);
        }else {
            model.addAttribute("dept", "");
        }
        if (origin != null) {
            model.addAttribute("origin", origin);
        } else {
            model.addAttribute("origin", "");
        }
        if (state != null) {
            model.addAttribute("state", state);
        } else {
            model.addAttribute("state", "");
        }
        if (type != null) {
            model.addAttribute("type", type);
        } else {
            model.addAttribute("type", "");
        }
        if(check!=null){
            model.addAttribute("check", check);
        }else {
            model.addAttribute("check", "");
        }
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/componentMyList.html";
    }
    @GetMapping("/myComponent")
    public String myConponent(Model model) {
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/myComponent.html";
    }

}
