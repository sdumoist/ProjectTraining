package com.jxdinfo.doc.front.thread.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.front.thread.model.threadCheck;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 线程监控
 */
@Controller
@RequestMapping("/ThreadController")
public class ThreadController {
    @RequestMapping("/list")
    public String index(Model model) {
        Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
       model.addAttribute("threadNum",maps.size());
        return  "/doc/front/thread/thread.html";
    }

    @RequestMapping("/getThread")
    @ResponseBody
    public JSON getThread(String bannerName, int page, int limit) {
        int beginIndex = page * limit - limit;
        Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("threadNum",maps.size());
        List list=new ArrayList();

        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet())
        {
            threadCheck threadCheck =new threadCheck();
            Thread thread = (Thread) stackTrace.getKey();
            StackTraceElement[] stack = (StackTraceElement[]) stackTrace.getValue();

            String str=thread.getName();
            String strState=thread.getState().name();

            threadCheck.setThreadName(str);
            threadCheck.setThreadState(strState);
            map.put("threadName",thread.getName());

            for (StackTraceElement stackTraceElement : stack) {

                //调用线程方法的类名、方法名、文件名以及调用的行数
               /* map.put("stackTraceElement",stackTraceElement);*/

          threadCheck.setStackTraceElement(stackTraceElement);
            }
            list.add(threadCheck);
        }
        JSONObject json = new JSONObject();


        json.put("data", list);
        json.put("count", 20);

        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }








}
