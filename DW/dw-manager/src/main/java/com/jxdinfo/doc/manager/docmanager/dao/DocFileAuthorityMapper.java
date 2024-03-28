package com.jxdinfo.doc.manager.docmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
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
public interface DocFileAuthorityMapper extends BaseMapper<DocFileAuthority> {

    List<DocFileAuthority> selectAuthorityList(@Param("fileId") String fileId);

    void deleteAuthByFileIds(@Param("fileIds") List<String> fileIds);

    List<Integer> judgeFileAuthority(@Param("fileId") String fileId, @Param("userId") String userId, @Param("parentOrganList") List<String> parentOrganList, @Param("listGroup") List<String> listGroup);
}
