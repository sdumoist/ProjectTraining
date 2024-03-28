package com.jxdinfo.doc.mobileapi.topmanager.controller;

import com.jxdinfo.doc.manager.doctop.model.DocTop;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 分享相关的控制层
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/top")
public class MobileTopController {
    @Autowired
    private DocTopService docTopService;
    @RequestMapping(value = "/addCheck")
    @ResponseBody
    public String addCheck(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        List listTop= docTopService.addCheck(list);
        if(listTop.size()>0){
            return "false";
        }else{
            return "true";
        }
    }

    @RequestMapping(value = "/add")
    @ResponseBody
    public String add(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        try{
            for(int i=0;i<list.size();i++){
                DocTop docTop = new DocTop();
                docTop.setTopId(UUID.randomUUID().toString().replaceAll("-", ""));
                docTop.setDocId(list.get(i));
                docTopService.add(docTop);

            }
            return "true";
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return "false";
        }
    }
}