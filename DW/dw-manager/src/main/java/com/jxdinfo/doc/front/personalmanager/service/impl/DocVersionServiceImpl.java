package com.jxdinfo.doc.front.personalmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.personalmanager.dao.DocVersionMapper;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongGuangrui on 2019/1/2.
 * 文档版本关联表 业务逻辑层实现类
 */
@Service
public class DocVersionServiceImpl extends ServiceImpl<DocVersionMapper,DocVersion> implements DocVersionService{
    @Autowired
    private DocVersionMapper docVersionMapper;
    @Autowired
    private DocInfoService docInfoService;

    @Override
    public List<String> selectIds(String versionReference) {
        return docVersionMapper.selectIds(versionReference);
    }

    @Override
    public int updateValidFlag(String[] docIds) {
        return docVersionMapper.updateValidFlag(docIds);
    }

    @Override
    public List<DocInfo> selectVersionHistoriesByDocId(String docId, String order, String name) {
        List<DocInfo> list = new ArrayList<>();
        DocInfo docInfo = docInfoService.getDocDetail(docId);
        if ("".equals(name)) {
            list.add(docInfo);
        }else if(name != null && docInfo.getTitle().indexOf(name) != -1){
            list.add(docInfo);
        }
        // 判断是否存在历史版本
        if (count(new QueryWrapper<DocVersion>().eq("doc_id",docId)) != 0) {
            // 若存在
            String versionReference = getOne(new QueryWrapper<DocVersion>().eq("doc_id", docId)).getVersionReference();
            List<String> idList = selectIds(versionReference);
            list = docInfoService.selectDocInfosByIdList(idList, order, name);
        }
        return list;
    }

    @Override
    public int selectVersionNumber(String VersionReference) {
        return docVersionMapper.selectVersionNumber(VersionReference);
    }
}
