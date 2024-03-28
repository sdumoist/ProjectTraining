package com.jxdinfo.doc.front.entry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EnrtyInfoMapper extends BaseMapper<EntryInfo> {

    public EntryInfo getEntryDetail(@Param("id") String id);

    List<EntryInfo> getAuditList(@Param("page") Page page, @Param("name") String name);

    List<Map<String, Object>> selectList(@Param("page") Page page, @Param("order") Integer order, @Param("tagList") List<String> tagList);

    List<EntryInfo> selectListByIds(@Param("idList") List<String> ids);

    /**
     * 获取热门词条
     */
    List<EntryInfo> getHotEntrys();

    List<EntryInfo> getEntryInfoList(@Param("page") Page page, @Param("name") String name,
                                     @Param("status") String status, @Param("userId") String userId);
}
