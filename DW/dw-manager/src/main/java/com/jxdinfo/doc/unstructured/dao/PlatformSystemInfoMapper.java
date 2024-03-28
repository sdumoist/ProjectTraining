package com.jxdinfo.doc.unstructured.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlatformSystemInfoMapper extends BaseMapper<PlatformSystemInfo>{
    
    /**
     * 系统接入配置列表查询
     * @param startIndex  开始位置
     * @param pageSize    页面大小               
     * @param systemName    系统名称
     * @return  list
     */
    List<PlatformSystemInfoVO> platformSystemInfoList(@Param("systemName") String systemName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);
}
