package com.jxdinfo.doc.manager.componentmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.componentmanager.dao.ComponentApplyAttachmentMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApplyAttachment;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyAttachmentService;
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
public class ComponentApplyAttachmentServiceImpl extends ServiceImpl<ComponentApplyAttachmentMapper,
        ComponentApplyAttachment>
        implements ComponentApplyAttachmentService {
    /**
     * 成果附件服务类
     */
    @Resource
    private ComponentApplyAttachmentMapper componentApplyAttachmentMapper;

    /**
     *
     * @param componentId 成果Id
     * @return 附件表集合
     */
    @Override
    public List<ComponentApplyAttachment> getAttachmentList(String componentId) {
        return componentApplyAttachmentMapper.getAttachmentList(componentId);
    }
}
