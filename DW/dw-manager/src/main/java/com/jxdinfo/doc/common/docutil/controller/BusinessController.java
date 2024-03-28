package com.jxdinfo.doc.common.docutil.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName BusinessController
 * @Description 文库业务层
 * @Author zoufeng
 * @Date 2018/9/10 9:33
 * @Version 1.0
 **/
@Controller
@RequestMapping("/business")
public class BusinessController extends BaseController{

    /** 业务服务类 */
    @Autowired
    private BusinessService businessService;

    /**
     * 列表及数的上移下移
     * @param table 表名
     * @param idColumn 排序字段名
     * @param idOne 点击数据id
     * @param idTwo 被交换的数据id
     * @return
     */
    @RequestMapping("/changeShowOrder")
    @ResponseBody
    public int changeShowOrder(String table, String idColumn,String idOne,String idTwo){
        return businessService.changeShowOrder(table,idColumn,idOne,idTwo);
    }
}
