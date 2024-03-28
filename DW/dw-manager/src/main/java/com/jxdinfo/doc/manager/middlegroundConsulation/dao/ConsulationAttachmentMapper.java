package com.jxdinfo.doc.manager.middlegroundConsulation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulationAttachment;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConsulationAttachmentMapper extends BaseMapper<MiddlegroundConsulationAttachment> {

    List<MiddlegroundConsulationAttachment> getAttachmentList(@Param("consulationId")String consulationId);

    int deleteByConsulationId(@Param("consulationId")String consulationId);
}
