package com.jxdinfo.doc.manager.docaudit.dao;

import com.jxdinfo.doc.manager.docaudit.model.DocInfoAudit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 文件审核表 Mapper 接口
 * </p>
 *
 * @author zn
 * @since 2020-08-25
 */
public interface DocInfoAuditMapper extends BaseMapper<DocInfoAudit> {
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
    List<FsFolderView> getApprovalList(@Param("userId") String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("order") String order, @Param("name") String name);

    /**
     * 查询待我审批列表数据条数
     * @param userId 用户ID
     * @param name 文档名称
     * @return 数目
     */
    int getApprovalListCount(@Param("userId") String userId, @Param("name") String name);

    /**
     * 查询待我审批列表数据-管理员
     *
     * @param userId 用户ID
     * @param pageNumber 页码
     * @param pageSize 条数
     * @param order 排序
     * @param name 文档名称
     * @return 人员数据
     */
    List<FsFolderView> getApprovalListByAdmin(@Param("userId") String userId, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("order") String order, @Param("name") String name);

    /**
     * 查询待我审批列表数据条数-管理员
     * @param userId 用户ID
     * @param name 文档名称
     * @return 数目
     */
    int getApprovalListCountByAdmin(@Param("userId") String userId, @Param("name") String name);
}
