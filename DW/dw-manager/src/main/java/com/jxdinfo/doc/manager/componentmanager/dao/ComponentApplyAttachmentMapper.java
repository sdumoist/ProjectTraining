package com.jxdinfo.doc.manager.componentmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApplyAttachment;
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
public interface ComponentApplyAttachmentMapper extends BaseMapper<ComponentApplyAttachment> {

    /**
     *
     * @param componentId 成果Id
     * @return 附件表集合
     */
    List<ComponentApplyAttachment>    getAttachmentList(@Param("componentId") String componentId);

}
