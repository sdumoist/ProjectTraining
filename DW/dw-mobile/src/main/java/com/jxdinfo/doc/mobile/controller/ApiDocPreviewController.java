package com.jxdinfo.doc.mobile.controller;

import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 手机浏览文档详情页
 */
@Controller
@RequestMapping("/apiDocPreview")
public class ApiDocPreviewController {

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    @Autowired
    private DocInfoService docInfoService;


    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @param fileId 
     * @param model
     * @return
     * @edit zgrpre
     */
    @RequestMapping("/{fileId}")
    public String viewDocMobile(@PathVariable String fileId, Model model, String userName){
        String docType;
        String returnPath;
        if(docInfoService.getDocDetail(fileId)!=null){
            docType = docInfoService.getDocDetail(fileId).getDocType();
        }else{
            docType = "";
        }
        model.addAttribute("title",docInfoService.getById(fileId).getTitle());
        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));
        model.addAttribute("watermark_user_flag", 1);
        model.addAttribute("userName", userName);
        model.addAttribute("fileName", null);
        model.addAttribute("fileType", null);
        model.addAttribute("category", null);
        model.addAttribute("fileId",fileId);
        if(".mp3".equals(docType) || ".m4a".equals(docType)){
            returnPath = "/doc/front/preview/showVoice_app.html";
        }else if(".mp4".equals(docType) || ".wmv".equals(docType)){
            returnPath = "/doc/front/preview/showVideo_app.html";
        }else{
            returnPath = "/doc/front/preview/showPDF_app.html";
        }
        return returnPath;
    }
    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/fileDetail")
    @ResponseBody
    public Map<String, Object> getFileDetail(String fileId) {
        Map map = frontDocInfoService.getDocByFileIdApi(fileId);
        map.put("fileSuffixName", map.get("fileSuffixName").toString().substring(map.get("fileSuffixName").
                toString().lastIndexOf(".") + 1));
        map.put("fileSize", FileTool.longToString(map.get("fileSize").toString()));
        return map;
    }
}