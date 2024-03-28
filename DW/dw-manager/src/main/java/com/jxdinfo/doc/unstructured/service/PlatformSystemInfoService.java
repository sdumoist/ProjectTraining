package com.jxdinfo.doc.unstructured.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlatformSystemInfoService extends IService<PlatformSystemInfo> {
    /**
     * 系统接入配置列表查询
     * @param startIndex  开始位置
     * @param pageSize    页面大小   
     * @param systemName    系统名称
     * @return  list
     */
    List<PlatformSystemInfoVO> platformSystemInfoList(String systemName, int startIndex, int pageSize);
}
