package com.jxdinfo.doc.manager.docintegral.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docintegral.model.ExemptIntegral;
import com.jxdinfo.doc.manager.docintegral.service.ExemptIntegralService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Controller
@RequestMapping("/exempt")
public class ExemptIntegralController extends BaseController {

    @Resource
    private ExemptIntegralService exemptIntegralService;

    @Resource
    private FilesService filesService;
    /**
     * @return
     * @author wz
     * @description 获取积分免除主页面
     */
    @GetMapping("/exemptView")
    @RequiresPermissions("exempt:exemptView")
    public String exempt() {
        return "/doc/manager/docexempt/exempt.html";
    }

    /**
     * 打开专题查看
     */
    @GetMapping("/exemptList")
    @ResponseBody
    public JSON exemptList(String title, int page, int limit) {
        int beginIndex = page * limit - limit;
        List<ExemptIntegral> exemptList = exemptIntegralService.show(title, beginIndex, limit);
        int exemptCount = exemptIntegralService.showCount(title);
        JSONObject json = new JSONObject();
        json.put("count", exemptCount);
        json.put("data", exemptList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 新增
     */
    @PostMapping("/exemptAdd")
    @ResponseBody
    public String exemptAdd(String fsFolderIds, String type,String fileName) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (!DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return "false";
        }

        String[] strArr = fsFolderIds.split(",");
        String[] typeArr = type.split(",");
        String[] fileNames = fileName.split(",");
        try {
            List list = new ArrayList();
            list.addAll(Arrays.asList(strArr));
            for (int i=0;i<strArr.length;i++){
                ExemptIntegral exemptIntegral = new ExemptIntegral();
                exemptIntegral.setFileId( UUID.randomUUID().toString().replace("-", ""));
                exemptIntegral.setFileType(typeArr[i]);
                exemptIntegral.setDocId(strArr[i]);
                exemptIntegral.setDocName(fileNames[i]);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                exemptIntegral.setCreateTime(ts);
                exemptIntegralService.save(exemptIntegral);
            }
            return "true";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "false";
        }

    }

    @PostMapping("/addCheck")
    @ResponseBody
    public String addCheck(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        List listTop= exemptIntegralService.addCheck(list);
        if(listTop.size()>0){
            return "false";
        }else{
            return "true";
        }
    }


    @PostMapping("/delExempt")
    @ResponseBody
    public int delExempt(Map<String,String> param,String ids) {
        boolean falg=false;
        int count = 0;
        String[] id  =ids.split(",");
        for (String l:id
             ) {
          falg= exemptIntegralService.removeById(l);
          if(falg){
              count++;
          }
        }
        return count;

        //获得Id集合
    }
}