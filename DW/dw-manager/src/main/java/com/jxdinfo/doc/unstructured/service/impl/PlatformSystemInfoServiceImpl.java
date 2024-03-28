package com.jxdinfo.doc.unstructured.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.unstructured.dao.PlatformSystemInfoMapper;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfoVO;
import com.jxdinfo.doc.unstructured.service.PlatformSystemInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformSystemInfoServiceImpl extends ServiceImpl<PlatformSystemInfoMapper, PlatformSystemInfo> implements PlatformSystemInfoService {
    @Autowired
    private PlatformSystemInfoMapper platformSystemInfoMapper;


    @Override
    public List<PlatformSystemInfoVO> platformSystemInfoList(String systemName, int startIndex, int pageSize) {
        return platformSystemInfoMapper.platformSystemInfoList(systemName,startIndex,pageSize);
    }
}
