package com.jxdinfo.doc.manager.componentmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProjectComponent;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 运营支持项目数据库连接层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
public interface MultiplexProjectComponentMapper extends BaseMapper<MultiplexProjectComponent> {

    /**
     * 删除复用项目关联表
     * @param projectId 复用项目列表id
     * @return  int
     */
    int deleteTopic(@Param("projectId") String projectId);
}
