package com.jxdinfo.doc.front.foldermanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
public interface FrontFoldAuthorityMapper extends BaseMapper<DocFoldAuthority> {

    /**
     * 获取是否有可编辑权限
     * @param groupList 群组id
     * @param userId 用户ID
     * @param levelCodeList  目录码ID
     * @return list
     */
    public List<String> findEdit(@Param("levelCodeList") List<String> levelCodeList, @Param("groupList") List groupList, @Param("userId") String userId);
}
