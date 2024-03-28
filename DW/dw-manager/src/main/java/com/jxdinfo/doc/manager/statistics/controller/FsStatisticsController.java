package com.jxdinfo.doc.manager.statistics.controller;

import java.util.*;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;

/**
 * 目录统计数量
 *
 * @author yjs
 * @Date 2018-08-10 19:01:50
 */
@Controller
@RequestMapping("/statistics")
public class FsStatisticsController {
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private DocInfoService docInfoService;
    
    private static final String PREFIX = "/doc/manager/statistics/";

    /**
     * 跳转到文件统计系统-文件首页
     */
    @RequiresPermissions("statistics:view")
    @GetMapping("/view")
    public String index() {
        return PREFIX + "statistics.html";
    }

    /**
     * 获取文件统计的e-charts图
     */

    @PostMapping("/list")
    @ResponseBody
    public Object index(
                        @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                        @RequestParam(value = "pageSize", defaultValue = "300") int pageSize, String order, String name,
                        String type, String nameFlag) {
        if (order == null || order.equals("dept")) {
            Map<String, Object> result = new HashMap<>(5);
            List<String> list = new ArrayList();
            int totalNum = 0;
            List<Integer> numList = new ArrayList();
            List<FsFolder> foldIds = fsFolderService.getChildrenByRoot(pageNumber, pageSize);
            for (int i = 0; i < foldIds.size(); i++) {
                int num = docInfoService.getCount(foldIds.get(i).getLevelCode());
                totalNum = totalNum + num;
                list.add(foldIds.get(i).getFolderName());
                numList.add(num);
            }
            result.put("total", foldIds.size());
            result.put("totalNum", totalNum);
            result.put("rows", foldIds);
            result.put("list", list);
            result.put("numList", numList);
            return result;
        } else {
            Map<String, Object> result = new HashMap<>(5);
            List<Map> listMap = new ArrayList();
            List<String> list = new ArrayList();
            int totalNum = 0;
            List<Integer> numList = new ArrayList();
            List<FsFolder> foldIds = fsFolderService.getChildrenByRoot(pageNumber, pageSize);
            for (int i = 0; i < foldIds.size(); i++) {
                int num = docInfoService.getCount(foldIds.get(i).getLevelCode());
                totalNum = totalNum + num;
                Map map = new HashMap();
                map.put("name", foldIds.get(i).getFolderName());
                map.put("num", num);
                listMap.add(map);
            }
            result.put("total", foldIds.size());
            result.put("totalNum", totalNum);
            result.put("rows", foldIds);

            Collections.sort(listMap, new Comparator<Map>() {

                /*
                 * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                 * 返回负数表示：o1 小于o2，
                 * 返回0 表示：o1和o2相等，
                 * 返回正数表示：o1大于o2。
                 */
                public int compare(Map o1, Map o2) {

                    return Integer.valueOf(o2.get("num") + "")
                            .compareTo(Integer.valueOf(o1.get("num") + ""));
                }
            });
            for (int i = 0; i < listMap.size(); i++) {
                list.add(listMap.get(i).get("name")+"");
                numList.add(Integer.parseInt(listMap.get(i).get("num") + ""));
            }
            result.put("list", list);
            result.put("numList", numList);
            return result;
        }
    }
}
