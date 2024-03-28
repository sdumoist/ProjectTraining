package com.jxdinfo.doc.manager.docmanager.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;

import java.util.List;

/**
 * <p>
 * 文件系统-文件 Mapper 接口
 * </p>
 *
 * @author LiangDong
 * @since 2018-08-10
 */
public interface FilesMapper extends BaseMapper<FsFile> {

    /**
     * 根据目录ID获取父级目录ID
     * @param idList 目录ID集合
     * @return 父级目录ID集合
     */
    List<String> getParentIdByFoldId(@Param("idList") List<String> idList);
}
