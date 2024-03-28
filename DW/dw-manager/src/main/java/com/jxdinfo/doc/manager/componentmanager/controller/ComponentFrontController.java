package com.jxdinfo.doc.manager.componentmanager.controller;/**
 * Created by HP on 2019/6/24.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentFrontService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 科研成果跳转预览
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
@Controller
@RequestMapping("/toShowComponent")
public  class  ComponentFrontController {

    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 组件前台跳转服务类
     */
    @Resource
    private ComponentFrontService componentFrontService;

    /**
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return: mv
     */
    @GetMapping("/toShowPDF")
    public  ModelAndView toShowPDF(String id, HttpServletRequest request) {

        ModelAndView mv = new ModelAndView("/doc/front/preview/showComponent.html");
        String keyword = request.getParameter("keyword");
        String userId = UserInfoUtil.getCurrentUser().getId();
        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        mv.addObject("url", url);
        mv.addObject("userId", userId);
        mv.addObject("isPersonCenter",false);
        mv.addObject("userName", UserInfoUtil.getCurrentUser().getName());
        mv.addObject("id",id);
        return mv;
    }

    /**
     *
     * @return json
     */
    @PostMapping("/componentList")
    @ResponseBody
    public JSON getTopicList() {
        List<ComponentApply> componentList = componentFrontService.componentList();
        JSONObject json = new JSONObject();
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;

    }
}
