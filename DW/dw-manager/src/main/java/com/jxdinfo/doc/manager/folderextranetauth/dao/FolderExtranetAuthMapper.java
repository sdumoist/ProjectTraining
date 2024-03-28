package com.jxdinfo.doc.manager.folderextranetauth.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.folderextranetauth.model.FolderExtranetAuth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FolderExtranetAuthMapper extends BaseMapper<FolderExtranetAuth> {
    String getFoldId(String docId);
    String existsFold(String foldIder);


    List<FolderExtranetAuth> selectFolderExtranetAuths(Page<FolderExtranetAuth> page,@Param("folderName") String folderName);
}
