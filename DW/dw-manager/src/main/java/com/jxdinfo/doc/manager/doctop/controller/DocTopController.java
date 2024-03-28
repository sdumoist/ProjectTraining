package com.jxdinfo.doc.manager.doctop.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.doctop.model.DocTop;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/top")
public class DocTopController {



    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
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

    @PostMapping(value = "/add")
    @ResponseBody
    public String add(String ids) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (!DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return "false";
        }
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

    /**
     * 打开专题查看
     */
    @GetMapping("/topListView")
    @RequiresPermissions("top:topListView")
    public String topicListView() {

//        try {
//            in = request.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String body2 = null;
//        try {
//            body2 = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if(StringUtils.isNotBlank(body2)){
//        JSONObject jsonObject = JSON.parseObject(body2);
////        Object userId = jsonObject.get("userId");
//        }
////        InetAddress address = null;
////        try {
////            address = InetAddress.getLocalHost();
////        } catch (UnknownHostException e) {
////            e.printStackTrace();
////        }
////        String hostAddress = address.getHostAddress();
//if(body!=null){
////    Object userId=body.get("userId");
//}

        return "/doc/manager/doctop/top-list.html";
    }

    /**
     * 打开专题查看
     */
    @GetMapping("/topList")
    @ResponseBody
    public JSON topList(String title, int page, int limit) {
        int beginIndex = page * limit - limit;
//        if(isV7==null){return null;}
//        String name = param.get("userId");
     /*   InputStream in = null;
        //开始位置
        try {

            in = request.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String body2 = null;
        try {
            body2 = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotBlank(body2)){
        JSONObject jsonObject = JSON.parseObject(body2);
//        Object userId = jsonObject.get("userId");
        }*/
        String bannerNameStr = StringUtil.transferSqlParam(title);
        List<Map> bannerList = docTopService.topList(bannerNameStr, beginIndex, limit);
        int bannerCount = docTopService.topListCount(bannerNameStr);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", bannerList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }
    @PostMapping("/delTops")
    @ResponseBody
    public int delBannersById(Map<String,String> param,String ids) {


            List<String> list = Arrays.asList(ids.split(","));
            return docTopService.delTops(list);

        //获得Id集合
    }

    @PostMapping("/moveTop")
    @ResponseBody
    public int moveTop(String table,String idColumn,String idOne, String idTwo) {
        int num = docTopService.moveTop(table,idColumn,idTwo,idOne);
        return num;
    }
}
