package com.jxdinfo.doc.manager.docmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.log.LogObjectHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件权限控制器
 *
 * @author lyq
 * @Date 2018-08-07 09:34:43
 */
@Controller
@RequestMapping("/docFileAuthority")
public class DocFileAuthorityController extends BaseController {
	
	/** PREFIX  */
    private String PREFIX = "/system/docFileAuthority/";

    /** 文件权限服务接口  */
    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /**
     * 跳转到文件权限首页
     */
    @GetMapping("")
    public String index() {
        return PREFIX + "docFileAuthority.html";
    }

    /**
     * 跳转到添加文件权限
     */
    @GetMapping("/docFileAuthority_add")
    public String docFileAuthorityAdd() {
        return PREFIX + "docFileAuthority_add.html";
    }

    /**
     * 跳转到修改文件权限
     */
    @GetMapping("/docFileAuthority_update/{docFileAuthorityId}")
    public String docFileAuthorityUpdate(@PathVariable String docFileAuthorityId, Model model) {
        DocFileAuthority docFileAuthority = docFileAuthorityService.getById(docFileAuthorityId);
        model.addAttribute("item",docFileAuthority);
        LogObjectHolder.me().set(docFileAuthority);
        return PREFIX + "docFileAuthority_edit.html";
    }

    /**
     * 获取文件权限列表
     */
    @PostMapping(value = "/list")
    @ResponseBody
    public Object list(String condition,
                       @RequestParam(value="pageNumber", defaultValue="1")int pageNumber,
                       @RequestParam(value="pageSize", defaultValue="20") int pageSize) {
        Page<DocFileAuthority> page = new Page<>(pageNumber, pageSize);
        QueryWrapper<DocFileAuthority> ew = new QueryWrapper<>();
        Map<String, Object> result = new HashMap<>(5);
        List<DocFileAuthority> list = docFileAuthorityService.page(page, ew).getRecords();
        result.put("total", page.getTotal());
        result.put("rows", list);
        return result;
    }

    /**
     * 新增文件权限
     */
    @PostMapping(value = "/add")
    @ResponseBody
    public Object add(DocFileAuthority docFileAuthority) {
        docFileAuthorityService.save(docFileAuthority);
        return SUCCESS_TIP;
    }

    /**
     * 删除文件权限
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public Object delete(@RequestParam String docFileAuthorityId) {
        docFileAuthorityService.removeById(docFileAuthorityId);
        return SUCCESS_TIP;
    }

    /**
     * 修改文件权限
     */
    @PostMapping(value = "/update")
    @ResponseBody
    public Object update(DocFileAuthority docFileAuthority) {
        docFileAuthorityService.updateById(docFileAuthority);
        return SUCCESS_TIP;
    }

    /**
     * 文件权限详情
     */
    @PostMapping(value = "/detail/{docFileAuthorityId}")
    @ResponseBody
    public Object detail(@PathVariable("docFileAuthorityId") String docFileAuthorityId) {
        return docFileAuthorityService.getById(docFileAuthorityId);
    }
}
