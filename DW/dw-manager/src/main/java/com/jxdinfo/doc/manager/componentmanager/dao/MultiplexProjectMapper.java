package com.jxdinfo.doc.manager.componentmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.model.YYZCProject;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运营支持项目数据库连接层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
public interface MultiplexProjectMapper   extends BaseMapper<MultiplexProject> {

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @param beginIndex 初始页码
     * @param limit 每页信息数量
     * @return  部门项目列表
     */
    List<MultiplexProject> multiplexList(@Param("title") String title, @Param("deptNameStr") String deptNameStr,
                                         @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit,
                                         @Param("orgId") String orgId, @Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd);

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @return
     */
    Integer multiplexListCount(@Param("title") String title, @Param("deptNameStr") String deptNameStr,
                               @Param("orgId") String orgId, @Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd);


    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @param beginIndex 初始页码
     * @param limit 每页信息数量
     * @return  部门项目列表
     */
    List<MultiplexProject> multiplexListByDept(@Param("title") String title, @Param("deptNameStr") String deptNameStr,
                                               @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit,
                                               @Param("orgId") String orgId, @Param("caUserName") String caUserName);


    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @return
     */
    Integer multiplexListCountByDept(@Param("title") String title, @Param("deptNameStr") String deptNameStr,
                                     @Param("orgId") String orgId, @Param("caUserName") String caUserName);

    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param orgName 用户名称
     * @param beginIndex 页数
     * @return 部门信息集合
     */
    List<YYZCProject> projectList(@Param("title") String title, @Param("orgName") String orgName,
                                  @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit);

    /**
     * 项目列表
     * @param title 项目名称
     * @param orgName 用户名称
     * @return Integer
     */
    Integer projectListCount(@Param("title") String title, @Param("orgName") String orgName);


    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param userId 用户名称
     * @param beginIndex 页数
     * @return 成果表集合
     */
    List<ComponentApply> myComponentList(@Param("title") String title, @Param("userId") String userId,
                                         @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit);

    /**
     * 组件列表
     * @param title 组件名称
     * @param userId 用户Id
     * @return Integer
     */
    Integer myComponentListCount(@Param("title") String title, @Param("userId") String userId);
    /**
     * 组件应用的项目列表
     * @param projectId 项目Id
     * @return 成果表集合
     */
    List<ComponentApply> getComponentList(@Param("projectId") String projectId);

    /**
     * 根据成果Id查询复用情况
     * @param componentId 成果Id
     * @param beginIndex 初始页数
     * @param limit 每页信息数量
     * @param projectName 复用项目名称
     * @return 复用表集合
     */
    List<MultiplexProject> selectComponentProject(@Param("componentId") String componentId, @Param("beginIndex")
            Integer beginIndex, @Param("limit") Integer limit, @Param("projectName") String projectName);

    /**
     *
     * @param componentId 成果id
     * @param projectName 部门名称
     * @return Integer
     */
    Integer componentProjectCount(@Param("componentId") String componentId, @Param("projectName") String projectName);

    /**
     *
     * @param title 项目名称
     * @param userId 用户名称
     * @param beginIndex 初始页数
     * @param limit 每页信息数量
     * @return 复用表集合
     */
    List<MultiplexProject> myMultiplexList(@Param("title") String title, @Param("userId") String userId,
                                           @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit);

    /**
     *
     * @param title 项目名称
     * @param userId 用户名称
     * @return integer
     */
    Integer myMultiplexListCount(@Param("title") String title, @Param("userId") String userId);




    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<MultiplexProject> multiplexGraphCount(@Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd, @Param("date") String date, @Param("order") String order);


    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<MultiplexProject> multiplexGraphCountBg(@Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd, @Param("date") String date, @Param("order") String order);
    /**
     * 根据成果查询复用项目
     * @param componentId
     * @return
     */
    List<MultiplexProject> componentMultiplexList(@Param("componentId") String componentId);

    /**
     * 根据复用查成果
     * @param projectId
     * @return
     */
    List<MultiplexProject> componentMultiplexProjectList(@Param("projectId") String projectId);
}
