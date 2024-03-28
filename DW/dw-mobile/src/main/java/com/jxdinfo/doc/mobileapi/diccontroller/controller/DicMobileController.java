package com.jxdinfo.doc.mobileapi.diccontroller.controller;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName DicController
 * @Description 平台字典
 * @Author yjs
 * @Date 2018/10/30 9:33
 * @Version 1.0
 **/
@CrossOrigin
@Controller
@RequestMapping("/mobile/dic")
public class DicMobileController extends BaseController {

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 根据字典类型获取字典数据List
     * @author      LiangDong
     * @return      java.lang.Object
     * @date        2018/5/28 14:19
     */
    @RequestMapping("/listData")
    @ResponseBody
    public ApiResponse getListData() {
        String dicType = super.getPara("dicType");
        List<Map<String, Object>> list = cacheToolService.getDictListByType(dicType);
        return ApiResponse.data(200,list,"");
    }

}
