package com.jxdinfo.doc.manager.componentmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApplyAttachment;

import java.util.List;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
public interface ComponentApplyAttachmentService extends IService<ComponentApplyAttachment> {

    /**
     *
     * @param componentId 成果Id
     * @return 附件表集合
     */
    List<ComponentApplyAttachment>    getAttachmentList(String componentId);
}
