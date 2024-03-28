package com.jxdinfo.doc.manager.docmanager.controller;

import com.jxdinfo.doc.common.util.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 后台管理
 * @author xb
 * @Description:
 * @Date: 2018/7/24 10:11
 * @modify wangning
 */
@Controller
public class DesktopController {

    @Autowired
    private ESUtil esUtil;
    /**
     * 获取后台首页
     */
    @GetMapping("/desktop")
    public String desktop(Model model) {
//        Date d1 = new Date();
//        long l1 = d1.getTime();
//        String oldDocId = "00b67dbc305c4c26b7f8c039cfb6d8a4";
//        String newDocId = "test0928003";
//        int flag = esUtil.copyIndex(oldDocId,newDocId);
//        System.out.println("***************************复制成功标志"+newDocId+"："+flag);
//        Date d2 = new Date();
//        long l2 = d2.getTime();
//        System.out.println("***************************复制时间："+(l2-l1));

        //更新索引测试
//        String oldDocId = "b1e256550cec458aa9956e668066c33e";
//        Map<String, Object> sourceMap  = esUtil.getIndex(oldDocId);
//        System.out.println(sourceMap);
//        HashMap<String,Object> data=new HashMap<>();
//        String[] permission = new String[]{"1","2"};
//        data.put("permission",permission);
//        int flag  = esUtil.updateIndex(oldDocId,data);
//        System.out.println("更新完成："+flag);
//        Map<String, Object> sourceMap2  = esUtil.getIndex(oldDocId);
//        System.out.println(sourceMap2);
        return   "/doc/manager/managerindex/desktop.html";
    }
}
