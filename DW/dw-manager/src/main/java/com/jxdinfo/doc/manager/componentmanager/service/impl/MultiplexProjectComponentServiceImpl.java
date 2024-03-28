package com.jxdinfo.doc.manager.componentmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.componentmanager.dao.MultiplexProjectComponentMapper;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProjectComponent;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectComponentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@Service
public class MultiplexProjectComponentServiceImpl extends ServiceImpl<MultiplexProjectComponentMapper,
        MultiplexProjectComponent> implements MultiplexProjectComponentService {

    /**
     * 复用登记与组件的管理服务类
     */
    @Resource
    private MultiplexProjectComponentMapper multiplexProjectComponentMapper;

    /**
     * 删除复用项目关联表
     * @param projectId 复用项目列表id
     * @return  int
     */
    @Override
    public int deleteTopic(String projectId) {
        return multiplexProjectComponentMapper.deleteTopic(projectId);
    }
}
