package com.jxdinfo.doc.manager.middlegroundConsulation.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentFrontService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: ConsulationFrontController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/2/24
 * @Version: 1.0
 */
@Controller
@RequestMapping("/toShowConsulation")
public class ConsulationFrontController {
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
    public ModelAndView toShowPDF(String id, HttpServletRequest request) {
        id = XSSUtil.xss(id);
        ModelAndView mv = new ModelAndView("/doc/front/preview/showConsulation.html");
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
}
