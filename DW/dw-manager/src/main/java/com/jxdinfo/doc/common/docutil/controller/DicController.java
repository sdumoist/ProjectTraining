package com.jxdinfo.doc.common.docutil.controller;

import com.jxdinfo.hussar.common.dicutil.DictionaryUtil;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.sys.vo.DicVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DicController
 * @Description 平台字典
 * @Author yjs
 * @Date 2018/10/30 9:33
 * @Version 1.0
 **/
@Controller
@RequestMapping("/dic")
public class DicController  extends BaseController {

    /**
     * 字典工具接口
     */
    @Resource
    private DictionaryUtil dictionaryUtil;

    /**
     * 根据字典类型获取字典数据List
     * @author      LiangDong
     * @return      java.lang.Object
     * @date        2018/5/28 14:19
     */
    @PostMapping("/listData")
    @ResponseBody
    public Object getListData() {
        String dicType = super.getPara("dicType");
        List<DicVo> listtmp = this.dictionaryUtil.getDictListByType(dicType);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(DicVo vo : listtmp){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("LABEL", vo.getLabel());
            map.put("VALUE", vo.getValue());
            list.add(map);
        }
        return list;
    }

}
