package com.jxdinfo.doc.manager.componentmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 运营支持项目数据库连接层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
public interface ComponentFrontMapper extends BaseMapper<ComponentApply> {

    /**
     *
     * @return 成果表集合
     */
    List<ComponentApply> componentList();
}
