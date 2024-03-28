package com.jxdinfo.doc.manager.middlegroundConsulation.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MiddlegroundConsulationMapper extends BaseMapper<MiddlegroundConsulation> {

    List<JSTreeModel> getDeptTree();

    List<MiddlegroundConsulation> getMiddlegroundList(@Param("userName") String userName,@Param("deptId") String deptId
            ,@Param("pageNum") int pageNum,@Param("pageSize") int pageSize);

    int getMiddlegroundCount(@Param("userName")String userName,@Param("deptId") String deptId);

    int deleteByMiddlegroundConsulationId(@Param("middleGroundConsulationId")String middleGroundConsulationId);

    MiddlegroundConsulation selectById(@Param("consulationId")String consulationId);
}
