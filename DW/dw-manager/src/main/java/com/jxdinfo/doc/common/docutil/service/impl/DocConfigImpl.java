package com.jxdinfo.doc.common.docutil.service.impl;


import com.jxdinfo.doc.common.docutil.dao.DocConfigMapper;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文库配置项服务
 *
 * @author wangning
 */
@Service
public class DocConfigImpl implements DocConfigService {

    @Resource
    private DocConfigMapper docConfigMapper;

    /**
     * 根据key值获取配置项值
     *
     * @return String
     * @author wangning
     */
    @Override
    public String getConfigValueByKey(String configKey) {
        return docConfigMapper.getConfigValueByKey(configKey);
    }

    @Override
    public List<Map> select(String table, String[] columns, Map params) {
        return docConfigMapper.select(table, columns, params);
    }

    @Override
    public int delete(String table, Map params) {
        if (ToolUtil.isEmpty(params)) return 0;
        return docConfigMapper.delete(table, params);
    }

    @Override
    public int insertOrUpdate(List<Map> userPicList) {
        if (ToolUtil.isEmpty(userPicList)) return 0;
        // 定义新增集合
        List<Map> insertList = new ArrayList<>();
        // 定义更新集合
        List<Map> updateList = new ArrayList<>();
        // 查询所有已存在图片的用户id
        List<String> existIds = new ArrayList<>();
        List<Map> existMapList = select("jxd7_xt_headpicture", new String[]{"USERID"}, null);
        if(ToolUtil.isNotEmpty(existMapList)){
            for(Map map : existMapList){
                existIds.add(map.get("USERID").toString());
            }
        }
        // 根据是否在库中已存在分为新增集合和更新集合
        for(Map map : userPicList){
            String userId = map.get("userid").toString();
            if(existIds.contains(userId)){
                updateList.add(map);
            }else{
                insertList.add(map);
            }
        }
        // 更新表
        if(ToolUtil.isNotEmpty(insertList)){
            docConfigMapper.addUserPicList(insertList);
        }
        if(ToolUtil.isNotEmpty(updateList)){
            docConfigMapper.updateUserPicList(userPicList);
        }
        return 1;
    }

}
