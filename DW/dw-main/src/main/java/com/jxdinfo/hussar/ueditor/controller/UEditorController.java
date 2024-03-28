/*
 * 金现代轻骑兵V8开发平台 
 * UEditorController.java 
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com.jxdinfo.hussar.ueditor.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 类的用途：富文本编辑示例<p>
 * 创建日期：2018年11月14日 <br>
 * 修改历史：<br>
 * 修改日期：2018年11月14日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
@Controller
@RequestMapping("/ueditor")
public class UEditorController extends BaseController {

    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;
    /**
     * 返回富文本编辑框页面
     * @Title: index 
     * @author: WangBinBin
     * @return
     */
    @RequestMapping("/view")
    public String index() {
        return "/hussardemo/ueditorDemo.html";
    }

    /**
     *
     * @Title: 富文本配置
     * @author: yjs
     * @return
     */
    @RequestMapping("/config")
    public void getEditorConfig(HttpServletRequest request , HttpServletResponse response) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        InputStream inputStream = resourceLoader.getResource("classpath:/config.json").getInputStream();
        byte[] b = new byte[inputStream.available()];
        inputStream.read(b);
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(b);
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    /**
     *
     * @Title: 上传图片
     * @author: yjs
     * @return
     */
    @RequestMapping("/imageupload")
    @ResponseBody
    public Object imageupload( @RequestParam("upfile") MultipartFile upfile) {
        String fileName = upfile.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;

        String  filePath = null;
        try {
            filePath = filesService.upload(upfile,fName).replaceAll("\\\\","/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,String> map = new HashMap<String,String>();
        map.put("url", "/sharefile/list?fileId=" + filePath);
        map.put("state", "SUCCESS");
        map.put("original", "");
        return map;
    }

}
