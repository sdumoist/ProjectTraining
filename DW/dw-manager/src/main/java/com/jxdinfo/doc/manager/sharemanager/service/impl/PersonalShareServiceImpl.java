package com.jxdinfo.doc.manager.sharemanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.sharemanager.dao.PersonalShareMapper;
import com.jxdinfo.doc.manager.sharemanager.model.DocShare;
import com.jxdinfo.doc.manager.sharemanager.service.IPersonalShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PersonalShareServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/10/30
 * @Version: 1.0
 */
@Service
public class PersonalShareServiceImpl  extends ServiceImpl<PersonalShareMapper, DocShare> implements IPersonalShareService {

    @Resource
    private PersonalShareMapper shareMapper;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Override
    public Map<String,Object> getMyShareHistory(String userId, String name, String order, int beginIndex, int pageSize, String timeType,String levelCode,String orgId, List roleList) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<DocShare> list = shareMapper.getMyShareHistory(userId,name,order,beginIndex,pageSize,timeType,levelCode,orgId,roleList);
        int count = shareMapper.getMyShareHistoryCount(userId,name,timeType);
        for (DocShare docShare : list){
            if (docShare.getFileSize() != null&&!"".equals(docShare.getFileSize())) {
                docShare.setFileSize(FileTool.longToString(docShare.getFileSize()));
                docShare.setView(cacheToolService.getReadNum(docShare.getDocId()));
            }
        }
        map.put("count", count);
        map.put("data", list);
        return map;
    }

    @Override
    public Map<String,Object> getMyShareHistoryMobile(String userId, String name, String order, int beginIndex, int pageSize, String timeType,String levelCode,String orgId, List roleList,List folderIds) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<DocShare> list = shareMapper.getMyShareHistoryMobile(userId,name,order,beginIndex,pageSize,timeType,levelCode,orgId,roleList,folderIds);
        int count = shareMapper.getMyShareHistoryCountMobile(userId,name,timeType,folderIds);
        for (DocShare docShare : list){
            if (docShare.getFileSize() != null&&!"".equals(docShare.getFileSize())) {
                docShare.setFileSize(FileTool.longToString(docShare.getFileSize()));
                docShare.setView(cacheToolService.getReadNum(docShare.getDocId()));
            }
        }
        map.put("count", count);
        map.put("data", list);
        return map;
    }
}
