package com.jxdinfo.doc.manager.componentmanager.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.componentmanager.dao.ComponentFrontMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentFrontService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@Service
public class ComponentFrontServiceImpl extends ServiceImpl<ComponentFrontMapper, ComponentApply>
        implements ComponentFrontService {
    /**
     * 科研成果跳转预览服务类
     */
    @Resource
   private ComponentFrontMapper componentFrontMapper;

    /**
     *
     * @return 成果表集合
     */
    public List<ComponentApply> componentList() {
        return componentFrontMapper.componentList();
    }
}
