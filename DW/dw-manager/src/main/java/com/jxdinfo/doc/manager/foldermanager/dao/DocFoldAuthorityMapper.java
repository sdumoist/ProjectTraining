package com.jxdinfo.doc.manager.foldermanager.dao;

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
public interface DocFoldAuthorityMapper extends BaseMapper<DocFoldAuthority> {

    /**
     * 根据目录id 删除目录权限
     * @param allFoldIds
     */
    void deleteAuthByFoldId(@Param("allFoldIds") List<String> allFoldIds);

    /**
     * 查询所属权限
     * @param groupList 群组id
     * @param userId 用户ID
     * @return list
     */
    public List<DocFoldAuthority> selectUserRole(@Param("groupList") List groupList, @Param("userId") String userId, @Param("type") String type);

    /**
     * 获取是否有可编辑权限
     * @param groupList 群组id
     * @param userId 用户ID
     * @param levelCodeList  目录码ID
     * @return list
     */
    public List<String> findEdit(@Param("levelCodeList") List<String> levelCodeList, @Param("groupList") List groupList, @Param("userId") String userId,@Param("roleList") List roleList);

    /**
     * 获取是否有可编辑权限重写
     * @param groupList 群组id
     * @param userId 用户ID
     * @param levelCodeList  目录码ID
     * @return list
     */
    public int findEditNew(@Param("levelCodeList") List<String> levelCodeList, @Param("groupList") List groupList, @Param("userId") String userId, @Param("orgName") String orgName, @Param("roleList") List roleList);
}
