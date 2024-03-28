package com.jxdinfo.doc.manager.statistics.service.impl;

import com.jxdinfo.doc.manager.statistics.dao.DocSpaceMapper;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.doc.manager.statistics.service.DocSpaceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 部门文件总量业务层
 * 作者：yjs ;
 * 修改内容：
 *
 * @author yjs ;
 * @version 1.0
 */
@Service
public class DocSpaceServiceImpl implements DocSpaceService {

    /**  部门空间Mapper */
    @Resource
    private DocSpaceMapper docSpaceMapper;

    /**
     * 通过ID得到该部门空间实体
     *
     * @param deptId 部门ID
     * @return DocSpace 部门空间实体
     * @author yjs
     * @date 2018/9/13
     */
    @Override
    public DocSpace getDocSpaceByDeptId(String deptId) {
        return docSpaceMapper.selectById(deptId);
    }
}
