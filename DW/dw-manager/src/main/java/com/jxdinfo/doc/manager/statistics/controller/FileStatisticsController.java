package com.jxdinfo.doc.manager.statistics.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.hussar.core.util.DateUtil;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;

/**
 * 
 * 类的用途：<p>
 * 创建日期：2018年9月25日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月25日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
@Controller
@RequestMapping("/fileStatistics")
public class FileStatisticsController {

    /**
     * 文件统计接口
     */
    @Autowired
    private FileStatisticsService fileStatisticsService;

    /**
     * 访问前缀
     */
    private static final String PREFIX = "/doc/manager/statistics/";

    /**
     * 跳转到文件统计系统-文件首页
     * @return 路径
     */
    @RequiresPermissions("fileStatistics:view")
    @GetMapping("/view")
    public String index() {
        return PREFIX + "fileStatistics.html";
    }



    /**
     * 跳转到文件下载统计系统-文件列表页
     * @return 路径
     */
    @RequiresPermissions("fileStatistics:viewFileList")
    @GetMapping("/viewFileList")
    public String viewFileList() {
        return PREFIX + "fileDownloadStatistics.html";
    }

    /**
     * 跳转到文件上传统计页面
     * @return 路径
     */
    @RequiresPermissions("fileStatistics:viewFileUploadList")
    @GetMapping("/viewFileUploadList")
    public String viewFileUploadList() {
        return PREFIX + "fileUploadStatistics.html";
    }

    /**
     * 用户上传文件预览数量
     * @Title: getUserPreviewData 
     * @author: XuXinYing
     * @return 数据
     */
    @PostMapping("/getUserPreviewData")
    @ResponseBody
    public Object getUserPreviewData() {
        return this.fileStatisticsService.getUserPreviewData("3");
    }

    /**
     * 用户上传文件下载数量
     * @Title: getUserDownloadData 
     * @author: XuXinYing
     * @return 数据
     */
    @PostMapping("/getUserDownloadData")
    @ResponseBody
    public Object getUserDownloadData() {
        return this.fileStatisticsService.getUserPreviewData("4");
    }

    /**
     * 部门上传文件预览数量
     * @Title: getDeptData 
     * @author: XuXinYing
     * @return 数据
     */
    @PostMapping("/getDeptData")
    @ResponseBody
    public Object getDeptData() {
        return this.fileStatisticsService.getDeptData("3");
    }

    /**
     * 部门上传文件下载数量
     * @Title: getDeptDownloadData 
     * @author: XuXinYing
     * @return 数据
     */
    @PostMapping("/getDeptDownloadData")
    @ResponseBody
    public Object getDeptDownloadData() {
        return this.fileStatisticsService.getDeptData("4");
    }

    /**
     * 获取文件列表数据
     * @Title: getFileListData 
     * @author: XuXinYing
     * @param opType 操作类型 预览下载
     * @param pageNumber 页码
     * @param pageSize 页面显示数量
     * @return 数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping(value = "/getFileListData")
    @ResponseBody
    public Object getFileListData(String opType, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "8") int pageSize) {
        pageSize = 8;
        Page<T> page = new Page<>(pageNumber, pageSize);
        JSONObject json = new JSONObject();
        List<Map> list = new ArrayList<>();
        if("3".equals(opType)) {
            //查询文件预览排行
            list = this.fileStatisticsService.getPreviewRankListData(page);
        } else if ("4".equals(opType)) {
            //查询文件下载排行
            list = this.fileStatisticsService.getDownloadRankListData(page);
        } else {
            list = this.fileStatisticsService.getFileListData(page, opType);
        }
        json.put("count", page.getTotal());
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 部门上传文件数量
     * @Title: getDeptUploadData 
     * @author: XuXinYing
     * @return 数据
     */
    @GetMapping("/getDeptUploadData")
    @ResponseBody
    public Object getDeptUploadData() {
        JSONObject json = new JSONObject();
        List<Map> list = this.fileStatisticsService.getUploadData("dept");
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 员工上传文件数量
     * @Title: getUserUploadData 
     * @author: XuXinYing
     * @return 数据
     */
    @GetMapping("/getUserUploadData")
    @ResponseBody
    public Object getUserUploadData() {
        JSONObject json = new JSONObject();
        List<Map> list = this.fileStatisticsService.getUploadData("user");
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 前台跳转到文件统计页面
     * @return 路径
     */
    @GetMapping("/statisticalAnalysis")
    public String statisticalAnalysis() {
        return PREFIX + "statisticalAnalysis.html";
    }


    /**
     * 后台跳转到文件统计页面
     * @return 路径
     */
    @RequiresPermissions("fileStatistics:statisticalAnalysisRear")
    @GetMapping("/statisticalAnalysisRear")
    public String statisticalAnalysisRear() {
        return PREFIX + "statisticalAnalysis-rear.html";
    }

    /**
     * 跳转到文件统计页面
     * @return 路径
     */
    @PostMapping("/getFileNum")
    @ResponseBody
    public  Object getFileNum() {
        List<String> list = this.fileStatisticsService.getFileNums();
        return list;
    }

    /**
     * 获取图表数据
     * @author      bjj
     * @return      java.lang.Object
     * @date        2018/10/09 14:09
     */
    @PostMapping("/getDeptActive")
    @ResponseBody
    public Object getDeptActive(String dateStr) {
        dateStr = getStartTime(dateStr)+"";
        List<Map> data = fileStatisticsService.getDeptActive(dateStr);
         Map<String, Object> result = new HashMap<>();
        //单位名称
        List xdata = new ArrayList();
        //登录数据
        List ydata = new ArrayList();
        //预览数据
        List wdata = new ArrayList();
        //下载数据
        List zdata = new ArrayList();
        for ( Map map : data) {
            //将查询结果放入两个坐标list中
            xdata.add(map.get("NAME"));
            ydata.add(map.get("LOGINNUM"));
            wdata.add(map.get("PREVIEWNUM"));
            zdata.add(map.get("DOWNLOADNUM"));
        }
        result.put("xdata",xdata);
        result.put("ydata",ydata);
        result.put("wdata",wdata);
        result.put("zdata",zdata);
        return result;
    }

    /**
     * 根据时间范围获取查询数据的开始时间
     * @author      LiangDong
     * @param date_range
     * @return      java.sql.Timestamp
     * @date        2018/8/13 18:41
     */
    public Timestamp getStartTime(String date_range ) {
        //获取当天零点时间戳
        String todayTime = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        todayTime += " 00:00:00";
        Date date = DateUtil.parse( todayTime, "yyyy-MM-dd HH:mm:ss");

        if ("today".equals(date_range)) {
            //今天
        } else if ("threeday".equals(date_range)) {
            //三天
            date = DateUtil.addDay(date,-3);
        } else if ("week".equals(date_range)) {
            //一周
            date = DateUtil.addWeek(date, -1);
        } else if ("month".equals(date_range)) {
            date = DateUtil.addMonth(date, -1);
        }

        return new Timestamp(date.getTime());
    }
    /**
     * 获取图表数据
     * @author      bjj
     * @return      java.lang.Object
     * @date        2018/10/09 14:09
     */
    @PostMapping("/getOrigin")
    @ResponseBody
    public Object getOrigin(String dateStr) {
        dateStr = getStartTime(dateStr)+"";

        List<Map> data = fileStatisticsService.getOrigin(dateStr);
        Map<String, Object> result = new HashMap<>();
        //单位名称
        List xdata = new ArrayList();
        //登录数据
        List ydata = new ArrayList();
        //预览数据
        List wdata = new ArrayList();

        for ( Map map : data) {
            //将查询结果放入两个坐标list中
            xdata.add(map.get("NAME"));
            ydata.add(map.get("PCNUM"));
            wdata.add(map.get("MOBILENUM"));
        }
        result.put("xdata",xdata);
        result.put("ydata",ydata);
        result.put("wdata",wdata);
        return result;
    }
}
