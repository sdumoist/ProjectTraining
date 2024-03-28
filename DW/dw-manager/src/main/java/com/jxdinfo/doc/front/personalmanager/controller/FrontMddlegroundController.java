package com.jxdinfo.doc.front.personalmanager.controller;


import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * describe:
 * 跳转中台咨询会议记录
 * @author lixin
 * @date 2020/01/08
 */
@Controller
@RequestMapping("/FrontMeetingRecord")
public class FrontMddlegroundController {

    @GetMapping("/meetingRecordList")
    public String toMeetingRecord(Model model){
        boolean hasMiddleGroundAuthority = false;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        // TODO 查询是否具有中台咨询委员会的角色
        Integer ztFlag = CommonUtil.getZTFlag(roleList);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        hasMiddleGroundAuthority = ztFlag == 7;
        if(!hasMiddleGroundAuthority&&adminFlag!=1){
            return "/403.html";
        }
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("style", "");
        model.addAttribute("dept", "");
        model.addAttribute("origin", "");
        model.addAttribute("state", "");
        model.addAttribute("type", "");
        model.addAttribute("check", "");
        model.addAttribute("userName", userName);

        return "/doc/front/personalcenter/middlegroundConsulation.html";
    }
}
