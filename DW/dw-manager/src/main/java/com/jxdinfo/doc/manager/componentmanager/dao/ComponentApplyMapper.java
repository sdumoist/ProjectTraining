package com.jxdinfo.doc.manager.componentmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
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
public interface ComponentApplyMapper extends BaseMapper<ComponentApply> {

    /**
     * 前台页面表格初始化
     * @param componentNameStr 成果名称
     * @param componentType 成果类型
     * @param componentState 成果状态
     * @param beginIndex 初始页码
     * @param limit 每页数量
     * @param componentOrigin 项目来源
     * @param userId 发布人
     * @param componentRange 应用场景
     * @return
     */
    List<ComponentApply> componentList(@Param("componentNameStr") String componentNameStr,
                                       @Param("componentType") Integer componentType,
                                       @Param("componentState") Integer componentState,
                                       @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit,
                                       @Param("componentOrigin") Integer componentOrigin, @Param("userId")
                                               String userId, @Param("orgId") String orgId,
                                       @Param("componentRange") String componentRange,
                                       @Param("deptName") String deptName, @Param("awardType") String awardType);

    /**
     * 后台页面表格初始化
     * @param componentNameStr 成果名称
     * @param componentType 成果类型
     * @param componentState 成果状态
     * @param componentOrigin 项目来源
     * @param userId 发布人
     * @param componentRange 应用场景
     * @return
     */
    Integer componentListCount(@Param("componentNameStr") String componentNameStr,
                               @Param("componentType") Integer componentType,
                               @Param("componentState") Integer componentState,
                               @Param("componentOrigin") Integer componentOrigin, @Param("userId")
                                       String userId, @Param("orgId") String orgId, @Param("componentRange")
                                       String componentRange,@Param("deptName") String deptName,  @Param("awardType") String awardType);

    /**
     * 个人成果数量统计
     * @return 姓名与成果个数的Map集合
     */
    List<Map> componentCount();
    /**
     * 部门成果数量统计
     * @return 姓名与成果个数的Map集合
     */
   List<Map> componentDeptCount();

    /**
     * 按天数统计成果发布
     * @return
     */
    Integer componentDateCount();

    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<ComponentApply> componentGraphCount(@Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd, @Param("date") String date, @Param("state") Integer state, @Param("order") String order);

    List<String>componentTopCount();
    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<ComponentApply> componentGraphCountBg(@Param("dateStart") String dateStart, @Param("dateEnd") String dateEnd, @Param("date") String date, @Param("state") Integer state, @Param("order") String order);

    /**
     * 前台页面表格初始化
     * @param componentNameStr 成果名称
     * @param componentType 成果类型
     * @param componentState 成果状态
     * @param beginIndex 初始页码
     * @param limit 每页数量
     * @param componentOrigin 项目来源
     * @param userId 发布人
     * @param componentRange 应用场景
     * @return
     */
    List<ComponentApply> componentListStates(@Param("componentNameStr") String componentNameStr,
                                             @Param("componentType") Integer componentType,
                                             @Param("componentState") Integer componentState,
                                             @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit,
                                             @Param("componentOrigin") Integer componentOrigin, @Param("userId")
                                                     String userId, @Param("orgId") String orgId,
                                             @Param("componentRange") String componentRange,
                                             @Param("states") String[] states, @Param("order") Integer order, @Param("dept") String[] dept);

    /**
     * 后台页面表格初始化
     * @param componentNameStr 成果名称
     * @param componentType 成果类型
     * @param componentState 成果状态
     * @param componentOrigin 项目来源
     * @param userId 发布人
     * @param componentRange 应用场景
     * @return
     */
    Integer componentListStatesCount(@Param("componentNameStr") String componentNameStr,
                                     @Param("componentType") Integer componentType,
                                     @Param("componentState") Integer componentState,
                                     @Param("componentOrigin") Integer componentOrigin, @Param("userId")
                                             String userId, @Param("orgId") String orgId, @Param("componentRange")
                                             String componentRange, @Param("states") String[] states, @Param("dept") String[] dept);

    /**
     * 前台页面表格初始化
     * @param componentNameStr 成果名称
     * @param componentType 成果类型
     * @param componentState 成果状态
     * @param beginIndex 初始页码
     * @param limit 每页数量
     * @param componentOrigin 项目来源
     * @param userId 发布人
     * @param componentRange 应用场景
     * @return
     */
    List<ComponentApply> componentListMobile(@Param("componentNameStr") String componentNameStr,
                                             @Param("componentType") Integer componentType,
                                             @Param("componentState") Integer componentState,
                                             @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit,
                                             @Param("componentOrigin") Integer componentOrigin, @Param("userId")
                                                     String userId, @Param("orgId") String orgId,
                                             @Param("componentRange") String componentRange,
                                             @Param("deptName") String deptName);

}
