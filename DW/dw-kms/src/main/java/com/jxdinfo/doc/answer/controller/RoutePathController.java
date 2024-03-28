package com.jxdinfo.doc.answer.controller;


import com.jxdinfo.doc.answer.service.RoutePathService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.professional.service.IProfessionalService;
import com.jxdinfo.doc.question.model.QaFile;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.service.QaFileService;
import com.jxdinfo.doc.question.service.QuestionService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 知识问答路径控制器
 * @author sjw
 * @since 2021-02-22
 */
@Controller
@RequestMapping("/routePath")
public class RoutePathController {

    /**
     * 问题操作接口
     */
    @Autowired
    private QuestionService questionService;

    /**
     * 路径操作接口
     */
    @Autowired
    private RoutePathService routePathService;

    /**
     * 附件操作接口
     */
    @Autowired
    private QaFileService qaFileService;


    /**
     * 专业专职接口
     */
    @Autowired
    private IProfessionalService professionalService;

    /**
     *  跳知识库页面
     * @return
     */
    @GetMapping("/knowledgeBase")
    public String knowledgeBase(Model model) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/knowledge.html";
    }

    /**
     *  跳统计分析页面
     * @return
     */
    @GetMapping("/statisticalPage")
    public String statisticalPage(Model model) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/statistics.html";
    }

    /**
     *  跳知识问答列表页面
     * @return
     */
    @GetMapping("/allQuestionList")
    public String allQuestionList(Model model) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/personalcenter/questionAndAnswer.html";
    }

    /**
     *  跳知识库新增页面
     * @return
     */
    @GetMapping("/knowledge_add")
    public String knowledge_add(Model model) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/knowledge/knowledgeAdd.html";
    }

    /**
     *  跳知识问答新增页面
     * @return
     */
    @GetMapping("/question_add")
    public String question_add(Model model) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/questions/questionAdd.html";
    }
    /**
     * 跳知识问答详情页面
     * @param model
     * @param queId 问题id
     * @return
     */
    @GetMapping("/questionDetail")
    public String questionDetail(Model model, String queId) {

        // xss过滤
        queId = XSSUtil.xss(queId);

        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;

        model.addAttribute("userName", shiroUser.getName());
        model.addAttribute("userId", shiroUser.getId());
        // 浏览量加1
        QaQuestion qaQuestion = questionService.getById(queId);
        int readNum = qaQuestion.getReadNum();
        readNum = readNum + 1;
        qaQuestion.setReadNum(readNum);
        qaQuestion.updateById();
        // 处理补充信息
        String supplementStr = qaQuestion.getSupplement();
        List<Map<String,String>> imgPathList = new ArrayList<>();

        String endOfStr = ""; // 末尾字符串
        if(supplementStr.indexOf("[img") == -1 && supplementStr.indexOf("[vid") == -1){
            endOfStr = supplementStr;
        }
        while (supplementStr.indexOf("[img") != -1 || supplementStr.indexOf("[vid") != -1){
            String type = ""; // 判断是图片还是视频
            int imgind = supplementStr.indexOf("[img");
            int vidind = supplementStr.indexOf("[vid");
            int ind = 0;
            if(imgind == -1){
                ind = vidind;
                type = "video";
            } else if(vidind == -1){
                ind = imgind;
                type = "img";
            } else {
                if(imgind >= vidind){
                    ind = vidind;
                    type = "video";
                } else {
                    ind = imgind;
                    type = "img";
                }
            }
            String imgIdStr = supplementStr.substring(ind,ind+37);
            String startSttr = supplementStr.substring(0,ind);
            endOfStr =  supplementStr.substring(ind+37);
            supplementStr =  supplementStr.substring(ind+37);
            String imgId = imgIdStr.substring(4,36);
            QaFile qaFile = qaFileService.getById(imgId);
            if(qaFile != null){
                Map<String,String> map = new HashMap<>();
                map.put("text", startSttr);
                map.put("type", type);
                try {
                    map.put("imgPath", URLEncoder.encode(qaFile.getFile(),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                imgPathList.add(map);
            }
        }
        qaQuestion.setSupplement(supplementStr);
        model.addAttribute("item",qaQuestion);
        Map<String,String> map2 = new HashMap<>();
        map2.put("text", endOfStr);
        map2.put("type", null);
        map2.put("imgPath", null);
        imgPathList.add(map2);
        model.addAttribute("imgPathList",imgPathList);
        // 获取问题的所有有效回答数
        int count = routePathService.getTotleNumAnswers(queId);
        model.addAttribute("count", count);
        // 获取当前登陆人有没有收藏该问题
        Map<String, Object> queDetail = questionService.getQuestionDetail(shiroUser.getId(), queId);
        model.addAttribute("isFollow", queDetail.get("isFollow"));
        // 是跳操作还是详情
        boolean ordinary = false;
        // 答题者还是提问者
        String aOrQ = "1"; // 答题者


        // 专业专职
        if("2".equals(qaQuestion.getAnswerFlag())){
            if(shiroUser.getId().equals(qaQuestion.getQueUserId()) || adminFlag){
                ordinary = true;
                aOrQ = "2"; // 提问者或超管
            } else {
                Map<String, String> majorInfo = professionalService.getProfessionalByMojorId(qaQuestion.getMajorId());
                String majorUser = majorInfo.get("userId");

                List<String> uids = Arrays.asList(majorUser.split(","));
                if(uids.contains(shiroUser.getId())){
                    ordinary = true;
                    aOrQ = "1"; // 答题者
                }
            }
        } else {
            ordinary = true;
            if(shiroUser.getId().equals(qaQuestion.getQueUserId()) || adminFlag){
                aOrQ = "2"; // 提问者或超管
            }
        }
        model.addAttribute("aOrQ", aOrQ);
        String html = "";
        if(ordinary){
            html = "/doc/front/questions/questionDetail.html";
        } else {
            html = "/doc/front/questions/questionDetail_ordinary.html";
        }
        if(ToolUtil.isNotEmpty(qaQuestion) && ToolUtil.isNotEmpty(qaQuestion.getState()) && ToolUtil.equals("2",qaQuestion.getState()) ){
            html = "/doc/front/questions/questionDetail_ordinary.html";
        }
        return html;
    }
}
