package com.jxdinfo.doc.manager.consulationmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ConsulationManagerController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/3/6
 * @Version: 1.0
 */
@Controller
@RequestMapping("/consulationManager")
public class ConsulationManagerController {
    /** PREFIX */
    private String PREFIX = "/doc/manager/consulationmanager/";
    /*
  目录服务层
   */
    @Resource
    private IFsFolderService fsFolderService;
    /**
     * 跳转到中台管理页面
     * @param model
     * @param sortId
     * @return
     */
    @GetMapping("/consulationManagerView")
    @RequiresPermissions("consulationManager:consulationManagerView")
    public String toConsulationManagerView(Model model, String sortId) {
        return PREFIX + "consulation_manager.html";
    }


    @GetMapping("/getDeptList")
    @ResponseBody
    public JSON getDeptList() {
        List<Map> list = fsFolderService.getDeptList(0, 8,"1");
        JSONObject json = new JSONObject();
        json.put("count", list.size());
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    @PostMapping("/updateDeptVisibleRange")
    @ResponseBody
    public int updateDeptVisibleRange(String organId,String VisibleRange) {
        List<String> list = Arrays.asList(organId.split(","));
        int count = fsFolderService.updateDeptVisibleRange(list,VisibleRange);
        return count;
    }
}
