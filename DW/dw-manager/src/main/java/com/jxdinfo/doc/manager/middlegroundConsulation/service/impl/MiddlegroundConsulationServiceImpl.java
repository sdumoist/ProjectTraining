package com.jxdinfo.doc.manager.middlegroundConsulation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.middlegroundConsulation.dao.MiddlegroundConsulationMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.MiddlegroundConsulationService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * describe:
 *
 * @author lixin
 * @date 2020/01/08
 */
@Service
public class MiddlegroundConsulationServiceImpl extends ServiceImpl<MiddlegroundConsulationMapper, MiddlegroundConsulation> implements MiddlegroundConsulationService {
    @Resource
    private MiddlegroundConsulationMapper middlegroundConsulationMapper;

    @Override
    public List<JSTreeModel> getDeptTree() {
        return middlegroundConsulationMapper.getDeptTree();
    }

    @Override
    public List<MiddlegroundConsulation> getMiddlegroundList(String userName, String deptId, int pageNum, int pageSize) {

        if (deptId==null){
            deptId = "";
        }
        return middlegroundConsulationMapper.getMiddlegroundList(userName, deptId, pageNum, pageSize);
    }

    @Override
    public int getMiddlegroundCount(String userName, String deptId) {
        return middlegroundConsulationMapper.getMiddlegroundCount(userName, deptId);
    }

    @Override
    public int deleteMiddlegroundConsulation(String middleGroundConsulationId) {

        return middlegroundConsulationMapper.deleteByMiddlegroundConsulationId(middleGroundConsulationId);
    }

    @Override
    public MiddlegroundConsulation selectById(String consulationId) {
        return middlegroundConsulationMapper.selectById(consulationId);
    }
}
