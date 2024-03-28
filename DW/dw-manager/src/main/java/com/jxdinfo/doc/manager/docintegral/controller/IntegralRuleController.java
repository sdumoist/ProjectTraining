package com.jxdinfo.doc.manager.docintegral.controller;

import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2018-12-03
 * @description 积分规则控制层
 */
@RequestMapping("/integralRule")
@Controller
public class IntegralRuleController extends BaseController {
    /**
     * PREFIX
     */
    private String prefix = "/doc/manager/integral/";

    /**
     * 积分规则的服务
     */
    @Resource
    private IntegralRuleService integralRuleService;

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 积分规则主页面
     * @return 积分规则主页面地址
     */
    @RequestMapping("/view")
    @RequiresPermissions("integralRule:view")
    public String integralRuleView(){
        return prefix + "rule_view.html";
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取积分规则的集合
     * @return 积分规则集合
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map integralRuleList() {
        Map map = new HashMap();
        //获取积分规则总条数
        int count = integralRuleService.getIntegralRuleCount();
        //分页处理
        String page = super.getPara("page");
        int pageNum = Integer.parseInt(page);
        String size = super.getPara("limit");
        int pageSize = Integer.parseInt(size);
        int startIndex = (pageNum - 1) * pageSize;
        //根据分页信息获取当前页的积分规则
        List<Map> list = integralRuleService.getIntegralRule(startIndex,pageSize);
        //返回前台的数据
        map.put("data",list);
        map.put("count",count);
        map.put("msg","success");
        map.put("code",0);
        return map;
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 新增积分规则的方法
     * @param map 需要新增的规则信息
     * @return 是否新增成功
     */
    @RequestMapping("/add")
    @ResponseBody
    public int newIntegralRule(@RequestParam Map map) {
        int check = integralRuleService.inputCheck(map);
        if (check != 0) {
            return 0;
        }
        //给新增的规则添加ID
        map.put("ruleId", StringUtil.getUUID());
        if (StringUtil.checkIsEmpty(map.get("maxTimes").toString())){
            map.put("maxTimes",-1);
        }
        return integralRuleService.newIntegralRule(map);
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取添加积分规则的页面
     * @return 积分规则页面地址
     */
    @RequestMapping("/addView")
    public String addView() {
        return prefix + "rule_add.html";
    }


    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 更新积分规则的方法
     * @param map 要更新积分规则的信息
     * @return
     */
    @RequestMapping("/update")
    @ResponseBody
    public int updateIntegralRule(@RequestParam Map map) {
        Map checkMap = new HashMap();
        checkMap.put("ruleName",map.get("ruleName"));
        checkMap.put("ruleId", map.get("ruleId"));
        int check = integralRuleService.inputCheck(checkMap);
        if (check != 0) {
            return 0;
        }
        if (StringUtil.checkIsEmpty(map.get("maxTimes").toString()) || "无上限".equals(map.get("maxTimes").toString())){
            map.put("maxTimes",-1);
        }
        return integralRuleService.updateIntegralRule(map);
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取编辑积分规则的页面
     * @param ruleId 需要获取的规则ID
     * @param model
     * @return 规则编辑页面
     */
    @RequestMapping("/editView")
    public String updateView(String ruleId, Model model) {
        Map rule = integralRuleService.getIntegralRuleById(ruleId);
        if (null != rule.get("maxTimes") && "-1".equals(rule.get("maxTimes").toString())){
            rule.put("maxTimes","无上限");
        }
        model.addAttribute("rule",rule);
        return prefix + "rule_edit.html";
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @param ids 要删除的id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public int deleteIntegralRule(String[] ids) {
        return integralRuleService.deleteIntegralRule(ids);
    }
}
