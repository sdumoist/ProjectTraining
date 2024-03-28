package com.jxdinfo.doc.front.foldermanager.controller;

import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 类的用途：跳转前台资源管理器
 * 创建日期：2018年9月4日
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
@Controller
@RequestMapping("/frontFolder")
public class FrontFsFolderController {


    /**目录管理服务类*/
    @Autowired
    private FrontFolderService frontFolderService;

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    @GetMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public List getTreeDataLazy(String id, String type) {
        return frontFolderService.getTreeDataLazy(id,type);
    }
}
