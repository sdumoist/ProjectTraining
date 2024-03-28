package com.jxdinfo.doc.manager.statistics.service;

import com.jxdinfo.doc.manager.statistics.model.DocSpace;

/**
 * 部门文件总量业务层
 * 作者：yjs ;
 * 修改内容：
 *
 * @author yjs ;
 * @version 1.0
 */
public interface DocSpaceService {

    /**
     * 通过ID得到该部门空间实体
     *
     * @param deptId 部门ID
     * @return DocSpac 部门空间实体
     * @author yjs
     * @date 2018/9/13
     */
    DocSpace getDocSpaceByDeptId(String deptId);
}
