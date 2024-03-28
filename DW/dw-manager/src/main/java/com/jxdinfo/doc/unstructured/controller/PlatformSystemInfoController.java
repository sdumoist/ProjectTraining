package com.jxdinfo.doc.unstructured.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfoVO;
import com.jxdinfo.doc.unstructured.service.PlatformSystemInfoService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName PlatformSystemInfoController
 * @Description TODO
 * @Author liuwei
 * @Version 1.0
 */
@Controller
@RequestMapping("/platformSystemInfo")
public class PlatformSystemInfoController {
    
    @Autowired
    private PlatformSystemInfoService platformSystemInfoService;

    /**
     * 打开系统接入配置
     * @return
     */
    @RequiresPermissions("platformSystemInfo:platformSystemInfoListListView")
    @RequestMapping("/platformSystemInfoListListView")
    public String topicListView() {
        return "/doc/manager/platformsysteminfo/platform-list.html";
    }

    /**
     * 打开新增系统接入配置页面
     * @param model
     * @return
     */
    @RequestMapping("/platformSystemInfoAdd")
    public String platformSystemInfoAdd(Model model){
        return "/doc/manager/platformsysteminfo/platform-add.html";
    }

    /**
     * 打开系统接入配置修改页面
     * @param model
     * @param systemId
     * @return
     */
    @RequestMapping("/platformSystemInfoEdit")
    public String platformSystemInfoEdit(Model model, String systemId){
        PlatformSystemInfo platformSystemInfo = platformSystemInfoService.getById(systemId);
        model.addAttribute("platformSystemInfo", platformSystemInfo);
        return "/doc/manager/platformsysteminfo/platform-edit.html";
    }
    
    /**
     * 系统接入配置列表查询
     * @param systemName    系统名称
     * @param page  当前页
     * @param limit 页面大小
     * @return  json对象
     */
    @RequestMapping("/getPlatformSystemInfoList")
    @ResponseBody
    public JSON getPlatformSystemInfoList(String systemName, int page, int limit){
        int beginIndex = (page - 1) * limit;
        
        List<PlatformSystemInfoVO> platformSystemInfoList = platformSystemInfoService.platformSystemInfoList(systemName, beginIndex, limit);
        // 总数量
        int count = 0;
        if(systemName == null){
            count = platformSystemInfoService.count();
        }else{
            QueryWrapper<PlatformSystemInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.like("system_name",systemName);
            count = platformSystemInfoService.count(queryWrapper);
        }
        
        JSONObject json = new JSONObject();
        json.put("count", count);
        json.put("data", platformSystemInfoList);
        json.put("msg", "success");
        json.put("code", 0);
        
        return json;
    }

    /**
     * 新增系统接入配置
     * @param platformSystemInfo 系统接入配置实体类
     * @return json对象
     */
    @RequestMapping("/addPlatformSystemInfo")
    @ResponseBody
    public JSON addPlatformSystemInfo(PlatformSystemInfo platformSystemInfo){
        //系统ID
        String systemId = UUID.randomUUID().toString().replaceAll("-", "");
        platformSystemInfo.setSystemId(systemId);
        //系统key密码
        String systemKey = UUID.randomUUID().toString().replaceAll("-", "");
        platformSystemInfo.setSystemKey(systemKey);
        //当前操作者id
        platformSystemInfo.setCreateUserId(ShiroKit.getUser().getId());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        //创建时间
        platformSystemInfo.setCreateTime(ts);
        //更新时间
        platformSystemInfo.setUpdateTime(ts);
        
        JSONObject json = new JSONObject();
        boolean isOk = platformSystemInfoService.save(platformSystemInfo);
        if (isOk){
            json.put("result", "1");
        }else{
            json.put("result", "2");
        }
        
        return json;
    }

    /**
     * 更新系统接入配置
     * @param platformSystemInfo 系统接入配置实体类
     * @return json对象
     */
    @RequestMapping("/updatePlatformSystemInfo")
    @ResponseBody
    public JSON updatePlatformSystemInfo(PlatformSystemInfo platformSystemInfo){
        //当前操作者id
        platformSystemInfo.setCreateUserId(ShiroKit.getUser().getId());
        //更新时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        platformSystemInfo.setUpdateTime(ts);
        
        JSONObject json = new JSONObject();
        boolean isOk = platformSystemInfoService.updateById(platformSystemInfo);
        if (isOk){
            json.put("result", "1");
        }else{
            json.put("result", "2");
        }

        return json;
    }

    /**
     * 删除系统接入配置
     * @param ids 系统id
     * @return 是否删除成功
     */
    @RequestMapping("/delPlatformSystemInfoByIds")
    @ResponseBody
    public boolean delPlatformSystemInfoByIds(String ids){
        List<String> list = Arrays.asList(ids.split(","));
        
        return platformSystemInfoService.removeByIds(list);
    }
}
