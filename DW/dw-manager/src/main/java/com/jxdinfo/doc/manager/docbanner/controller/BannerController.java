package com.jxdinfo.doc.manager.docbanner.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docbanner.model.DocBanner;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;


    @Value("${docbase.uploadPath}")
    private String base;
    @Value("${docbase.filedir}")
    private String uploadPath;

    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;

    /**
     * 类型获取
     */
    @Autowired
    private FsFileService fsFileService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;
    /**
     * 打开专题查看
     */
    @RequestMapping("/bannerListView")
    @RequiresPermissions("banner:bannerLIstView")
    public String topicListView() {
        return "/doc/manager/docbanner/banner-list.html";
    }

    /**
     * 专题信息列表查询
     *
     * @return 专题列表
     */
    @RequestMapping("/bannerList")
    @ResponseBody
    public JSON getTopicList(String bannerName, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置

        String bannerNameStr = StringUtil.transferSqlParam(bannerName);
        List<DocBanner> bannerList = bannerService.bannerList(bannerNameStr, beginIndex, limit);
        int bannerCount = bannerService.bannerListCount(bannerNameStr);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", bannerList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }
    /**
     * 打开专题新增
     */
    @RequestMapping("/bannerAdd")
    public String bannerAdd(Model model) {
        String currentCode = this.sysIdtableService.getCurrentCode("BANNER_NUM", "doc_banner_file");
        int num = Integer.parseInt(currentCode);
        //int num = specialTopicService.getMaxOrder();
        model.addAttribute("lastNum", num);
        model.addAttribute("num", num + 1);
        return "/doc/manager/docbanner/banner-add.html";
    }
    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件名
     * @Title: upload
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    @ResponseBody
    public JSONObject upload(@RequestPart("file") MultipartFile file) {
        JSONObject json = new JSONObject();
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {
            String  filePath = filesService.upload(file,fName);
            json.put("fName", filePath);
            json.put("fileName", fileName);
        } catch (IOException e) {
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 新增banner
     *
     *
     * @param docBanner banner对象
     * @param docIds       文档ID
     * @return 新增结果
     */
    @PostMapping("/addBanner")
    @ResponseBody
    public JSON addBanner(DocBanner docBanner, String docIds) {
        String bannerId = UUID.randomUUID().toString().replaceAll("-", "");
        String bannerHref = docBanner.getBannerHref();
        try {
            bannerHref =  new String (new BASE64Decoder().decodeBuffer(bannerHref));
        } catch (IOException e) {
            e.printStackTrace();
        }
        docBanner.setBannerHref(bannerHref);

        //专题ID
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docBanner.setBannerId(bannerId);
        docBanner.setCreateTime(ts);

        JSONObject json = new JSONObject();
        //检查专题名称是否已经存在
        int num = bannerService.checkBannerExist(docBanner);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int addNum = bannerService.addBanner(docBanner);
            if (addNum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(37);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return json;
    }

    /**
     * 打开专题修改页面
     */
    @GetMapping("/bannerEdit")
    public String bannerEdit(Model model, String bannerId) {
        DocBanner docBanner = bannerService.searchBannerDetail(bannerId);

        try {
            String topicCover = URLEncoder.encode(docBanner.getBannerPath(),"UTF-8");
            docBanner.setBannerPath(topicCover);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int num = bannerService.getMaxOrder();
        model.addAttribute("lastNum", num);

//        String userName = docBanner.getAuthorName();
//        model.addAttribute("users", userName);

        model.addAttribute("docBanner", docBanner);
        return "/doc/manager/docbanner/banner-edit.html";
    }

    @PostMapping("/editBanner")
    @ResponseBody
    public JSON editBanner(DocBanner docBanner, String docIds) {
        String bannerHref = docBanner.getBannerHref();
        if(bannerHref!=null) {
            try {
                bannerHref = new String(new BASE64Decoder().decodeBuffer(bannerHref));
            } catch (IOException e) {
                e.printStackTrace();
            }
            docBanner.setBannerHref(bannerHref);
            //拼装操作历史记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(38);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
        }
        JSONObject json = new JSONObject();
        int num = bannerService.checkBannerExist(docBanner);
        if (num > 0) {
            json.put("result", "0");
        } else {
            int editNum = bannerService.editBanner(docBanner);
            //获取新增的数据条数
            if (editNum == 1) { 
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        return json;
    }

    /**
     * 打开专题修改页面
     */
    @GetMapping("/bannerView")
    public String topicView(Model model, String bannerId) {
        DocBanner docBanner  = bannerService.searchBannerDetail(bannerId);

        try {
            String topicCover = URLEncoder.encode(docBanner.getBannerPath(),"UTF-8");
            docBanner.setBannerPath(topicCover);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int num = bannerService.getMaxOrder();
        model.addAttribute("lastNum", num);

        model.addAttribute("docBanner", docBanner);
        return "/doc/manager/docbanner/banner-view.html";
    }

    @PostMapping("/delBanners")
    @ResponseBody
    public int delBannersById(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(39);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        //获得Id集合
        return bannerService.delBanners(list);
    }

    @PostMapping("/moveBanner")
    @ResponseBody
    public int moveBanner(String table,String idColumn,String idOne, String idTwo) {
        int num = bannerService.moveBanner(table,idColumn,idTwo,idOne);
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(40);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return num;
    }
    @GetMapping("/openDoc")
    public String index() {
        return "/doc/manager/docbanner/banner-open-doc.html";
    }


    @PostMapping("/bannerName")
    @ResponseBody
    public String  bannerName(String docId) {

        FsFile fsFile=fsFileService.getById(docId);
        String str=fsFile.getFileType();

        return str;
    }

}
