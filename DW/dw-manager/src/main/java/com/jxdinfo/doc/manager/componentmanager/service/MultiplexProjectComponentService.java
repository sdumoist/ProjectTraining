package com.jxdinfo.doc.manager.componentmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProjectComponent;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
public interface MultiplexProjectComponentService extends IService<MultiplexProjectComponent> {

    /**
     * 删除复用项目关联表
     * @param projectId 复用项目列表id
     * @return  int
     */
    int deleteTopic(String projectId);

}
