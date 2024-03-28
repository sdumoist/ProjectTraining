package com.jxdinfo.doc.manager.docaudit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docaudit.model.DocInfoAudit;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;

import java.util.List;

/**
 * <p>
 * 文件审核表 服务类
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
public interface IDocInfoAuditService extends IService<DocInfoAudit> {

    /**
     * 添加文档审核信息
     * @param folderId 目录主键
     * @param docId 文档主键
     * @return 是否成功
     */
    boolean addDocInfoAudit(String folderId, String docId);

    /**
     * 查询待我审批列表数据
     *
     * @param userId 用户ID
     * @param pageNumber 页码
     * @param pageSize 条数
     * @param order 排序
     * @param name 文档名称
     * @return 人员数据
     */
    List<FsFolderView> getApprovalList(String userId, int pageNumber, int pageSize, String order, String name);

    /**
     * 查询待我审批列表数据条数
     * @param userId 用户ID
     * @param name 文档名称
     * @return 数目
     */
    int getApprovalListCount(String userId, String name);

}
