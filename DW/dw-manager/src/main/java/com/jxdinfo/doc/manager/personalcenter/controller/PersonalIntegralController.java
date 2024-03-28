package com.jxdinfo.doc.manager.personalcenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by ZhongGuangrui on 2018/12/6.
 * 后台积分相关控制
 */
@Controller
@RequestMapping("/personalIntegral")
public class PersonalIntegralController {
    /**
     * 积分 服务
     */
    @Resource
    private IntegralRecordService integralRecordService;

    /**
     * 用户管理服务 接口
     */
    @Resource
    private ISysUsersService iSysUsersService;

    /**
     * 积分规则的服务
     */
    @Resource
    private IntegralRuleService integralRuleService;


    /**
     * 获取积分明细
     *
     * @param ruleCodes  操作类型（多个以逗号分隔）
     * @param pageNumber 当前页
     * @param pageSize   页面条数
     * @return map
     */
    @PostMapping("/list")
    @ResponseBody
    public Map list(String ruleCodes, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize) {
        //获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        String[] ruleCodesArray = null;
        if (ruleCodes != null && ruleCodes != "") {
            ruleCodesArray = ruleCodes.split(",");
        }
        List<Map<String, Object>> list = integralRecordService.getIntegralHistories(userId, "1", ruleCodesArray);
        int count = list.size();
        // 将积分统计情况一并返回
        Map<String, Integer> integrals = new HashMap<>();
        integrals.put("total", integralRecordService.showIntegral(userId));
        integrals.put("upload", integralRecordService.getIntegralByType(userId, new String[]{"upload"}));
        integrals.put("beDownloaded", integralRecordService.getIntegralByType(userId, new String[]{"beDownloaded"}));
        integrals.put("rewards", integralRecordService.getIntegralByType(userId, new String[]{"login", "search", "defaultBonus", "preview", "share"}));
        integrals.put("download", integralRecordService.getIntegralByType(userId, new String[]{"download"}));
        integrals.put("bePreviewed", integralRecordService.getIntegralByType(userId, new String[]{"bePreviewed"}));
        integrals.put("beShared", integralRecordService.getIntegralByType(userId, new String[]{"beShared"}));
        Integer rankTry = integralRecordService.getRankNum(userId);
        double total = (double) iSysUsersService.count(new QueryWrapper<SysUsers>().ne("ACCOUNT_STATUS", "2"));
        double rank = total;
        if (rankTry != null) {
            rank = (double) rankTry;
        }
        double present = 100 - (rank / total * 100);
        int presentint = (int) present;
        Map histories = new HashMap();
        histories.put("msg", "success");
        histories.put("code", 0);
        histories.put("rows", list.stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()));
        histories.put("count", count);
        histories.put("integrals", integrals);
        histories.put("present", presentint + "%");
        return histories;
    }

    @PostMapping("/ruleList")
    @ResponseBody
    public Map ruleList() {
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        List<Map> list = integralRuleService.getIntegralRule(0, 30);
        List<String> listNew = new ArrayList<String>();
        String times = "";
        String start="";
        for (int i = 0; i < list.size(); i++) {
            if ((list.get(i).get("valid") + "").equals("1")&&!(list.get(i).get("ruleCode") + "").equals("defaultBonus")&&!(list.get(i).get("ruleCode") + "").equals("dayLimit")) {
                String desc = "";
                times = list.get(i).get("maxTimes") + "";
                if (!"-1".equals(times)) {
                    desc = "每日" + desc;
                    if ("1".equals(times)) {
                        desc=desc+"第&nbsp;<span >"+times+"</span>&nbsp;次";
                        if (!(list.get(i).get("ruleCode") + "").equals("login")) {
                            desc = desc+"文件";
                        }
                        desc = desc+(list.get(i).get("ruleName") )+ "";
                    }else{
                        desc=desc+"前&nbsp;<span >"+times+"</span>&nbsp;次";
                        if (!(list.get(i).get("ruleCode") + "").equals("login")) {
                            desc = desc+"文件";
                        }
                        desc = desc+(list.get(i).get("ruleName") )+ "";
                    }
                }else{
                    if (!(list.get(i).get("ruleCode") + "").equals("login")) {
                        desc = desc+"文件";
                    }
                    desc = desc+(list.get(i).get("ruleName") )+ "";
                }
                Integer rank= (Integer) list.get(i).get("integral");
                String rankStr="";
                if(rank<0){
                    rankStr=rank+"";
                    desc= desc +" 积分 <span style='color:#F86842'>"+rankStr+"</span>";
                } else{
                    rankStr="+"+rank;
                    desc= desc +" 积分 <span style='color:#4DBF86'>"+rankStr+"</span>";
                }

                listNew.add(desc);
            }
            if((list.get(i).get("ruleCode") + "").equals("dayLimit")){
                start="每日积分上限为："+list.get(i).get("integral");
            }
        }
        Map histories = new HashMap();
        histories.put("limit",start);
        histories.put("list",listNew);
        return histories;
    }

    @GetMapping("/ruleShow")
    public String ruleShow(String fileId, String fileType, String fileName, Model model){


        return "/doc/front/personalcenter/integral_rule.html";

    }

    @PostMapping("/rulesList")
    @ResponseBody
    public Map rulesList() {
        ShiroUser shiroUser = ShiroKit.getUser();
        String userId = shiroUser.getId();
        List<Map> list = integralRuleService.getIntegralRule(0, 30);
        List<String> listNew = new ArrayList<String>();
        String times = "";
        String start="";
        Map histories = new HashMap();
//        histories.put("limit",start);
        histories.put("list",list);
        return histories;
    }
}
