package com.jxdinfo.doc.manager.commonFolderManager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.commonFolderManager.model.FsCommonFolder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 常用目录Mapper接口
 */
public interface FsCommonFolderMapper extends BaseMapper<FsCommonFolder> {

    /**
     * 获取用户所有常用目录
     * @param userId
     * @return
     */
    List<FsCommonFolder> selectAllCommonFold(@Param("userId") String userId, @Param("startIndex") int startIndex,
                                             @Param("pageSize") int pageSize, @Param("order") String order);

    /**
     * 获取当前显示顺序最大值
     * @return
     */
    Integer getMaxOrder();

    /**
     * 移动目录
     * @param idOne
     * @param idTwo
     * @return
     */
    void moveFolder(@Param("idOne") String idOne, @Param("idTwo") String idTwo);
}
