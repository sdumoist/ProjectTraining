package com.jxdinfo.doc.front.entry.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.entry.model.EntryBody;
import com.jxdinfo.doc.front.entry.model.EntryImgs;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.model.EntryInfoBar;
import com.jxdinfo.doc.front.entry.service.EntryBodyService;
import com.jxdinfo.doc.front.entry.service.EntryImgsService;
import com.jxdinfo.doc.front.entry.service.EntryInfoBarService;
import com.jxdinfo.doc.front.entry.service.EntryInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import dm.jdbc.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/entry")
public class EntryInfoController extends BaseController {

    @Autowired
    private EntryInfoService entryInfoService;

    @Autowired
    IFsFolderService fsFolderService;


    @Autowired
    private FilesService filesService;


    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    @Autowired
    private EntryInfoBarService entryInfoBarService;

    @Autowired
    private EntryBodyService entryBodyService;

    @Autowired
    private EntryImgsService entryImgsService;

    @GetMapping({"/entryList"})
    public String entryList() {
        return "/doc/front/entry/entry_list.html";
    }

    @GetMapping({"/auditList"})
    public String auditList() {
        return "/doc/front/entry/entry_audit_list.html";
    }

    @GetMapping({"/entryAdd"})
    public String entryAdd() {
        return "/doc/front/entry/entry_add.html";
    }
    @GetMapping({"/entryEdit/{id}"})
    public String entryEdit(@PathVariable String id, Model model) {
        model.addAttribute("entryId", id);
        return "/doc/front/entry/entry_edit.html";
    }


    @GetMapping({"/allEntryList"})
    public String allEntryList(Model model, String keyWords, String fileType) {
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        // 获取当前的登录用户
        if(StringUtils.isNotEmpty(keyWords)){
            keyWords = XSSUtil.xss(keyWords);
        }
        model.addAttribute("key", keyWords);
        model.addAttribute("fileType", fileType);
        model.addAttribute("userName", userName);
        return "/doc/front/entry/all_entrys.html";
    }

    /**
     * 查询待审核数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/auditListData")
    public Map auditListData(int pageNumber, int pageSize, String name) {
        Page<EntryInfo> page = new Page<EntryInfo>(pageNumber, pageSize);
        List<EntryInfo> list = entryInfoService.getAuditList(page, name);

        // 设置所属部门
        String projectTitle = String.valueOf(ShiroKit.getSession().getAttribute("projectTitle"));
        if (list != null && list.size() > 0) {
            for (EntryInfo entry : list) {
                if (StringUtil.isEmpty(entry.getDeptName())) {
                    entry.setDeptName(projectTitle);
                }
            }
        }

        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        Map map = new HashMap();
        map.put("userName", userName);
        map.put("rows", list);
        map.put("total", page.getTotal());
        map.put("count", list.size());
        return map;
    }

    /**
     * 词条预览
     * @return
     */
    @GetMapping({"/entryPreview"})
    public ModelAndView entryPreview(String id) {
        ModelAndView mv = new ModelAndView("/doc/front/entry/entry_preview.html");
        String userId = UserInfoUtil.getCurrentUser().getId();
        mv.addObject("userName", UserInfoUtil.getCurrentUser().getName());
        String url = fsFolderService.getPersonPic(UserInfoUtil.getCurrentUser().getName());
        mv.addObject("url", url);
        mv.addObject("userId", userId);
        mv.addObject("id", id);

        EntryInfo info = entryInfoService.getEntryDetail(id);
        if(info!=null && StringUtils.isNotEmpty(info.getImgUrl())){
            String imgeUrl = info.getImgUrl();
            try {
                String img = URLEncoder.encode(imgeUrl,"UTF-8");
                info.setImgUrl(img);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        mv.addObject("info", info);
        mv.addObject("imgUrl", info.getImgUrl());
        return mv;
    }

    /**
     * 获取词条详细信息
     *
     * @return
     */
    @RequestMapping({"/entryDetail"})
    @ResponseBody
    public EntryInfo getEntryDetail(String id) {
        EntryInfo info = new EntryInfo();
        // 查询词条信息栏内容
        QueryWrapper barQw = new QueryWrapper<EntryInfoBar>();
        barQw.eq("entry_id", id);
        barQw.orderByAsc("show_order");
        List<EntryInfoBar> infoBars = entryInfoBarService.list(barQw);
        // 查询词条正文
        QueryWrapper barBody = new QueryWrapper<EntryBody>();
        barBody.eq("entry_id", id);
        barBody.orderByAsc("show_order");
        List<EntryBody> entryBodys = entryBodyService.list(barBody);
      /*  // 查询词条图册
        QueryWrapper barImgs = new QueryWrapper<EntryImgs>();
        barImgs.eq("entry_id", id);
        barImgs.orderByAsc("show_order");
        List<EntryImgs> entryImgs = entryImgsService.list(barImgs);*/

        if (infoBars != null && infoBars.size() > 0) {
            info.setInfoBars(infoBars);
        }

        if (entryBodys != null && entryBodys.size() > 0) {
            info.setEntryBodys(entryBodys);
        }
        return info;
    }

    /**
     * 查询词条图册
     *
     * @return
     */
    @RequestMapping({"/getEntryImgs"})
    @ResponseBody
    public List<EntryImgs> getEntryImgs(String id) {
        // 查询词条图册
        QueryWrapper barImgs = new QueryWrapper<EntryImgs>();
        barImgs.eq("entry_id", id);
        barImgs.orderByAsc("show_order");
        List<EntryImgs> entryImgs = entryImgsService.list(barImgs);
        if(entryImgs!=null && entryImgs.size()>0){
            for(EntryImgs img: entryImgs){
                try{
                    img.setImgUrl(URLEncoder.encode(img.getImgUrl(),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return entryImgs;
    }

    /**
     * 更新预览次数
     *
     * @return
     */
    @RequestMapping({"/updateReadNum"})
    @ResponseBody
    public void updateReadNum(String id) {
        EntryInfo info = entryInfoService.getById(id);
        Integer readNum = info.getReadNum();
        if (readNum == null) {
            info.setReadNum(1);
        } else {
            readNum++;
            info.setReadNum(readNum);
        }
        entryInfoService.updateById(info);
    }


    /**
     * 上传图册的接口
     *
     * @param file 文件
     * @return
     */
    @ResponseBody
    @RequestMapping("/upload")
    public JSONObject upload(@RequestPart("file") MultipartFile file) {
        JSONObject json = new JSONObject();
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {
            String filePath = this.filesService.upload(file, fName);
            json.put("code", "0");
            json.put("fName", filePath);
            json.put("fileName", fileName);
        } catch (IOException var7) {
            json.put("code", "1");
            var7.printStackTrace();
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        } catch (Exception var8) {
            var8.printStackTrace();
        }
        return json;
    }

    /**
     * 新增词条
     * @return
     */
    @ResponseBody
    @RequestMapping("/addEntryInfo")
    public Object addEntryInfo(@RequestBody JSONObject obj) {
        JSONObject json = new JSONObject();
        EntryInfo entryInfo = JSON.parseObject(obj.get("entryInfo").toString(), EntryInfo.class);
        List<EntryBody> entryBodyList = JSON.parseArray(obj.get("entryBodyList").toString(), EntryBody.class);
        List<EntryInfoBar>  entryInfoBarList = JSON.parseArray(obj.get("entryInfoBarList").toString(), EntryInfoBar.class);
        List<EntryImgs>  entryImgsList = JSON.parseArray(obj.get("entryImgsList").toString(), EntryImgs.class);
        int result = entryInfoService.addEntryInfo(entryInfo, entryBodyList, entryInfoBarList, entryImgsList);
        if (result == 1) {
            json.put("code", "1");
            json.put("msg", "新增成功");
        } else {
            json.put("code", "0");
            json.put("msg", "新增失败");
        }
        return json;
    }

    /**
     * 修改词条
     * @return
     */
    @ResponseBody
    @RequestMapping("/updateEntryInfo")
    public Object updateEntryInfo(@RequestBody JSONObject obj) {
        JSONObject json = new JSONObject();
        EntryInfo entryInfo = JSON.parseObject(obj.get("entryInfo").toString(), EntryInfo.class);
        List<EntryBody> entryBodyList = JSON.parseArray(obj.get("entryBodyList").toString(), EntryBody.class);
        List<EntryInfoBar>  entryInfoBarList = JSON.parseArray(obj.get("entryInfoBarList").toString(), EntryInfoBar.class);
        List<EntryImgs>  entryImgsList = JSON.parseArray(obj.get("entryImgsList").toString(), EntryImgs.class);
        int result = entryInfoService.updateEntryInfo(entryInfo, entryBodyList, entryInfoBarList, entryImgsList);
        if (result == 1) {
            json.put("code", "1");
            json.put("msg", "修改成功");
        } else {
            json.put("code", "0");
            json.put("msg", "修改失败");
        }
        return json;
    }

    /**
     * 删除词条
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/deleteEntryInfo")
    public Object deleteEntryInfo(String id) {
        JSONObject json = new JSONObject();
        EntryInfo entryInfo = entryInfoService.getById(id);
        if (entryInfo != null) {
            int result = entryInfoService.deleteEntryInfo(entryInfo);
            if (result == 1) {
                json.put("code", "1");
                json.put("msg", "删除成功");
            } else {
                json.put("code", "0");
                json.put("msg", "删除失败");
            }
        } else {
            json.put("code", "0");
            json.put("msg", "该词条不存在");
        }
        return json;
    }

    /**
     * 查询词条数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/searchEntryInfoList")
    public Map searchEntryInfoList(int pageNumber, int pageSize, String name, String status) {
        Page<EntryInfo> page = new Page<EntryInfo>(pageNumber, pageSize);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<EntryInfo> list = entryInfoService.getEntryInfoList(page, name, status, userId);
        Map map = new HashMap();
        map.put("rows", list);
        map.put("total", page.getTotal());
        map.put("count", list.size());
        return map;
    }

    /**
     * 根据Id获取词条详细信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/getEntryInfoDetailById")
    public Map getEntryInfoDetailById(String id) {
        Map map = new HashMap();
        EntryInfo entryInfo = entryInfoService.getById(id);
        if (entryInfo != null) {
            map.put("code", "1");
            map.put("data", entryInfoService.getEntryInfoDetailById(id));
        } else {
            map.put("code", "0");
            map.put("msg", "该词条不存在");
        }
        return map;
    }

    /**
     * 词条审核通过
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/entryInfoApproved")
    public Object entryInfoApproved(String id) {
        JSONObject json = new JSONObject();
        try{

            EntryInfo entryInfo = entryInfoService.getById(id);
            if (entryInfo != null) {
                int result = entryInfoService.entryInfoApproved(entryInfo);
                if (result == 1) {
                    json.put("code", "1");
                    json.put("msg", "审核成功");
                } else {
                    json.put("code", "0");
                    json.put("msg", "审核失败");
                }
            } else {
                json.put("code", "0");
                json.put("msg", "该词条不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

    /**
     * 词条审核驳回
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/entryInfoReject")
    public Object entryInfoReject(String id) {
        JSONObject json = new JSONObject();
        EntryInfo entryInfo = entryInfoService.getById(id);
        if (entryInfo != null) {
            int result = entryInfoService.entryInfoReject(entryInfo);
            if (result == 1) {
                json.put("code", "1");
                json.put("msg", "驳回成功");
            } else {
                json.put("code", "0");
                json.put("msg", "驳回失败");
            }
        } else {
            json.put("code", "0");
            json.put("msg", "该词条不存在");
        }
        return json;
    }

}
