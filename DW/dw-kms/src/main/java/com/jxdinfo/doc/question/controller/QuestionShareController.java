package com.jxdinfo.doc.question.controller;


import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.question.service.QuestionShareService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


@Controller
@RequestMapping("/q")
public class QuestionShareController extends BaseController {

    @Resource
    private QuestionShareService questionShareService;
    /**

     * @description 创建问题分享链接
     * @author yjs
     * @date 2021-03-01
     */
    @RequestMapping("/shareHref")
    @ResponseBody
    public Map shareHref(String questionId, @RequestParam(defaultValue = "0") int validTime, HttpServletRequest request) {

        // xss过滤
        questionId = XSSUtil.xss(questionId);

        return questionShareService.newShareResource(questionId, validTime,request);
    }

    /**
     * @param hash  分享文件的映射地址
     * @param model
     * @return 分享文件的预览界面
     * @author luzhanzhao
     * @date 2018-12-11
     */
    @RequestMapping("/{hash}")
    public String viewShare(@PathVariable String hash, Model model, String pwd) {

        // xss过滤
        hash = XSSUtil.xss(hash);

        model.addAttribute("hash", hash);
        //根据映射地址获取分享文件的信息
        Map shareResource = questionShareService.getShareResource(hash);
        if (shareResource == null) {
            model.addAttribute("error_msg", "该分享链接已失效");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }
        // 获取链接的分享查看人员配置

        //获取文件的可访问状态
        // 该链接的有效性，链接的valid（链接分享者设置）
        String href = shareResource.get("href").toString();
        String docValid = "";
        String shareFlag = "";
        if (shareResource.get("STATE") == null) {
            docValid = "1";
        } else {
            docValid = shareResource.get("STATE").toString();
        }
        //将有效期转换成日期类型
        //Date validTime = StringUtil.stringToDate(shareResource.get("validTime").toString());
        //获取文件名
        String title = "";
        String questionId = shareResource.get("questionId").toString();
        if (!"".equals(questionId) && shareResource.get("title") == null) {

            title = shareResource.get("title").toString();

        }
        model.addAttribute("title", title);
        //获取当前时间
        Date today = new Date();
        model.addAttribute("createTime", shareResource.get("createTime"));
        model.addAttribute("validTime", shareResource.get("validTime"));
        model.addAttribute("validLack", null);
        //判断该分享资源是否在有效期内 today.after(validTime) ||
        if (  "0".equals(docValid) || "0".equals(shareFlag)) {
            //资源失效则返回分享错误页面
            model.addAttribute("error_msg", "该分享链接已失效");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }


        //获取配置文件--是否有公司水印
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("shareUser", shareResource.get("selectUsersName"));

        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        if (null != shiroUser) {
            if (href.indexOf("toShowFolder") != -1) {
            } else {
                href = href.replaceAll("sharefile", "preview");
            }
        }
        href += "&shareForward=1";
        return "forward:"+ href;
    }

    /**

     * @param title 问题名称
     * @param model
     * @return 确认页面
     * @author yjs
     * @date 2021-03-01
     * @description 获取分享确认页面
     */
    @RequestMapping("/shareConfirm")
    public String shareConfirm(String questionId, String title, Model model) {

        // xss过滤
        questionId = XSSUtil.xss(questionId);
        title = XSSUtil.xss(title);

        //封装传到分享链接页面的参数
        model.addAttribute("fileId", questionId);
        model.addAttribute("fileName", title);
        model.addAttribute("isPersonCenter",false);
        /*return"/doc/front/preview/share_confirm.html";*/
        return "/doc/front/questions/q_share_confirm.html";
    }


    public String changeValid(Date validDate) {
        String lack = "";
        //跨年的情况会出现问题哦
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
        Calendar aCalendar = Calendar.getInstance();
        Long time1 = validDate.getTime();
        Long time2 = new Date().getTime();
        int day = (int) ((time1 - time2) / (1000 * 60 * 60 * 24));
        int hours = (int) ((time1 - time2) / (1000 * 60 * 60));
        if (day > 10000) {
            lack = "永久有效";
        } else if (day == 0 || day < 1) {

            lack = (hours + 1) + "小时后";
        } else {
            hours = hours - (day * 24);
            lack = day + "天" + (hours + 1) + "小时后";
        }
        return lack;
    }
}
