package com.jxdinfo.doc.front.personalmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ZhongGuangrui on 2019/1/2.
 * 文档版本关联表 数据访问层
 */
public interface DocVersionMapper extends BaseMapper<DocVersion> {
    /**
     * 查询同一个文件不同版本的文档id
     * @param versionReference  版本关联uid
     * @return  返回所有不同版本的id集合
     */
    List<String> selectIds(@Param("versionReference") String versionReference);
    /**
     * 批量设置历史版本失效
     * @param docIds    文档id集合
     * @return          设置成功的条数
     */
    int updateValidFlag(@Param("docIds") String[] docIds);

    int selectVersionNumber(@Param("VersionReference") String VersionReference);
}
