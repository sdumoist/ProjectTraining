package com.jxdinfo.doc.manager.docconfigure.dao;/**
 * Created by zoufeng on 2018/10/15.
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName DocConfigMapper
 * @Description TODO
 * @Author zoufeng
 * @Date 2018/10/15 11:39
 * @Version 1.0
 **/
public interface DocConfigureMapper extends BaseMapper<DocConfigure> {

    /**
     * @Author zoufeng
     * @Description 获取配置信息
     * @Date 11:43 2018/10/15
     * @Param []
     * @return java.util.List<com.jxdinfo.doc.manager.docconfigure.model.DocConfigure>
     **/
    List<DocConfigure> getConfigure();

    /**
     * @Author lishilin
     * @Description 配置目录名称
     * @param folderName 目录名称
     * @return
     */
    int upDateFolderName(@Param("folderName") String folderName);
}
