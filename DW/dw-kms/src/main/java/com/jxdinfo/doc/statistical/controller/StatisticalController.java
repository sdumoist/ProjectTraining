package com.jxdinfo.doc.statistical.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.statistical.service.StatisticalService;
import com.jxdinfo.hussar.common.dicutil.DictionaryUtil;
import com.jxdinfo.hussar.core.sys.vo.DicVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制层
 * @author cxk
 * @since 2021-05-14
 */
@Controller
@RequestMapping("/statistical")
public class StatisticalController {

    @Autowired
    private StatisticalService statisticalService;

    /**
     * 字典工具接口
     */
    @Resource
    private DictionaryUtil dictionaryUtil;



    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    @RequestMapping("/getQueTableList")
    @ResponseBody
    public Map getQueTableList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<DicVo> listtmp= this.dictionaryUtil.getDictListByType("professional");
        for(DicVo vo : listtmp){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(vo.getLabel(), vo.getValue());
            list.add(map);
        }
        List<String> majorIdList = new ArrayList<>();
        for (Map<String, Object> item:list) {
            for(Map.Entry<String, Object> vo : item.entrySet()){
                majorIdList.add(vo.getValue().toString());
            }

        }
        Map map = statisticalService.getEchartData(majorIdList);
        return map;
    }


    /**
     * 根据文件夹查询文档
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/getTableData")
    @ResponseBody
    public JSON getTableData(HttpServletRequest request, HttpServletResponse response) {
        List tableList = statisticalService.getTableData();
        JSONObject json = new JSONObject();
        json.put("count", tableList.size());
        json.put("data", tableList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }
}
