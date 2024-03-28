package com.jxdinfo.doc.manager.professional.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.professional.model.Professional;
import com.jxdinfo.doc.manager.professional.service.IProfessionalService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.dicutil.DictionaryUtil;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.log.LogObjectHolder;
import com.jxdinfo.hussar.core.sys.vo.DicVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专业专职控制器
 *
 * @author cxk
 * @Date 2021-05-08 09:50:00
 */
@Controller
@RequestMapping("/professional")
public class ProfessionalController extends BaseController {

    private String PREFIX = "/doc/manager/professional/";

    @Autowired
    private IProfessionalService professionalService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /**
     * 数据字典工具类
     */
    @Autowired
    private DictionaryUtil dictionaryUtil;

    /**
     * 跳转到专业专职首页
     */
    @RequiresPermissions("professional:view")
    @RequestMapping("/view")
    public String index(Model model) {
        List<DicVo> listtmp = this.dictionaryUtil.getDictListByType("professional");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(DicVo vo : listtmp){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("LABEL", vo.getLabel());
            map.put("VALUE", vo.getValue());
            list.add(map);
        }
        model.addAttribute("professional",list);
        return PREFIX + "professional.html";
    }

    /**
     * 跳转到添加专业专职
     */
    @RequestMapping("/professional_add")
    public String professionalAdd(Model model) {
        String currentCode = this.sysIdtableService.getCurrentCode("TOPIC_NUM", "doc_special_topic");
        int num = Integer.parseInt(currentCode);
        model.addAttribute("showOrder",num);
        return PREFIX + "professional_add.html";
    }

    /**
     * 跳转到修改专业专职
     */
    @RequestMapping("/professional_update/{professionalId}")
    public String professionalUpdate(@PathVariable String professionalId, Model model) {
        Professional professional = professionalService.getById(professionalId);
        model.addAttribute("item",professional);
        LogObjectHolder.me().set(professional);
        return PREFIX + "professional_edit.html";
    }

    /**
     * 获取专业专职列表
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public JSON list(String majorId, String userName, int page, int limit) {
        int beginIndex = page * limit - limit;
        String majorIdStr = StringUtil.transferSqlParam(majorId);
        String userNameStr = StringUtil.transferSqlParam(userName);
        List<Professional> professionalsList = professionalService.professionalList(majorIdStr,userNameStr,beginIndex,limit);
        int professionalsCount = professionalService.getProfessionalListCount(majorIdStr,userNameStr);
        JSONObject json = new JSONObject();
        json.put("count", professionalsCount);
        json.put("data", professionalsList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 新增专业专职
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Object add(Professional professional) {
        professionalService.save(professional);
        return SUCCESS_TIP;
    }

    /**
     * 批量删除专业专职
     *
     * @param ids 专业专职ID
     * @return 删除的数量
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        return professionalService.removeByIds(list);
    }

    /**
     * 修改专业专职
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(Professional professional) {
        professionalService.updateById(professional);
        return SUCCESS_TIP;
    }

    /**
     * 专业专职详情
     */
    @RequestMapping("/detail/{professionalId}")
    public String detail(@PathVariable String professionalId, Model model) {
        Professional professional = professionalService.getById(professionalId);
        model.addAttribute("item",professional);
        LogObjectHolder.me().set(professional);
        return PREFIX + "professional_detail.html";
    }

    /**
     * 操作校验
     * @param majorId 专业Id
     * @param id 主键
     * @return
     */
    @RequestMapping(value = "/operationJudge")
    @ResponseBody
    public Object operationJudge(String majorId, String id) {
        int num = professionalService.operationJudge(majorId,id);
        return num;
    }
}
