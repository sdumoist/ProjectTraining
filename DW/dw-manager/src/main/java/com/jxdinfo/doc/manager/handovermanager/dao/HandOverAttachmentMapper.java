package com.jxdinfo.doc.manager.handovermanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOverAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HandOverAttachmentMapper extends BaseMapper<DocHandOverAttachment> {
    List<FsFolderView> getAttachmentList(@Param("beginNum") Integer beginNum, @Param("limit") Integer limit, @Param("name") String name , @Param("id") String id);
    Integer getAttachmentCount(@Param("name") String name ,@Param("id") String id);
}
