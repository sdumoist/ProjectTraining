package com.jxdinfo.doc.manager.middlegroundConsulation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;

import java.util.List;

public interface MiddlegroundConsulationService extends IService<MiddlegroundConsulation> {
    /**
     * 获取部门组织机构树
     * @return
     */
    List<JSTreeModel> getDeptTree();

    List<MiddlegroundConsulation> getMiddlegroundList(String userName, String deptId, int pageNum, int pageSize);

    int getMiddlegroundCount(String searchName, String deptId);

    int deleteMiddlegroundConsulation(String middleGroundConsulationId);

    MiddlegroundConsulation selectById(String consulationId);

}
