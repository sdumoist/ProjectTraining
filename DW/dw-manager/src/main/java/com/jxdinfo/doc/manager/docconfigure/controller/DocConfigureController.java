package com.jxdinfo.doc.manager.docconfigure.controller;

import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName DocConfigureController
 * @Description 配置信息控制层
 * @Author zoufeng
 * @Date 2018/10/15 11:56
 * @Version 1.0
 **/
@Controller
@RequestMapping("/docConfigure")
public class DocConfigureController {

    /** 配置信息服务层 */
    @Resource
    private DocConfigureService docConfigureService;

    /**
     * PREFIX
     */
    private String prefix = "/doc/manager/docconfigure/";

    /**
     * @Author zoufeng
     * @Description 通用配置菜单
     * @Date 13:31 2018/10/15
     * @Param 
     * @return 
     **/
    @RequiresPermissions("docConfigure:getConfigure")
    @GetMapping("/getConfigure")
    public String getConfigureView(){
        return prefix + "configure.html";
    }
    
    /**
     * @Author zoufeng
     * @Description 获取配置信息
     * @Date 15:06 2018/10/15
     * @Param 
     * @return 
     **/
    @PostMapping("/getConfigureData")
    @ResponseBody
    public List<DocConfigure> getConfigure(){
        return docConfigureService.getConfigure();
    }

    /**
     * @Author zoufeng
     * @Description 保存方法
     * @Date 16:27 2018/10/15
     * @Param [docConfigure]
     * @return boolean
     **/
    @PostMapping("/save")
    @ResponseBody
    public boolean save(DocConfigure docConfigure){
        return docConfigureService.save(docConfigure);
    }
}
