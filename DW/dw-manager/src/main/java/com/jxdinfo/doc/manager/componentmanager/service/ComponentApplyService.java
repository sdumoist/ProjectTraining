package com.jxdinfo.doc.manager.componentmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
public interface ComponentApplyService  extends IService<ComponentApply> {

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
    List<ComponentApply> componentList(String componentNameStr, Integer componentType, Integer componentState,
                                       Integer beginIndex, Integer limit, Integer componentOrigin, String userId,
                                       String orgId, String componentRange, String deptName, String awardType
    );

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
   Integer componentListCount(String componentNameStr, Integer componentType, Integer componentState,
                              Integer componentOrigin, String userId
           , String orgId, String componentRange, String deptName, String awardType);

    /**
     * 个人成果数量
     * @return 姓名与成果个数的Map集合
     */
   List<Map> componentCount();
    /**
     * 部门成果数量
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
    List<ComponentApply> componentGraphCount(String dateStart, String dateEnd, String date, Integer state, String order);

    List<String>componentTopCount();
    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<ComponentApply> componentGraphCountBg(String dateStart, String dateEnd, String date, Integer state, String order);

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
    List<ComponentApply> componentListStates(String componentNameStr, Integer componentType, Integer componentState,
                                             Integer beginIndex, Integer limit, Integer componentOrigin, String userId,
                                             String orgId, String componentRange, String[] states, Integer order, String[] dept
    );
    Integer componentListStatesCount(String componentNameStr, Integer componentType, Integer componentState,
                                     Integer componentOrigin, String userId
            , String orgId, String componentRange, String[] states, String[] dept);

    List<ComponentApply> componentListMobile(String componentNameStr, Integer componentType, Integer componentState,
                                             Integer beginIndex, Integer limit, Integer componentOrigin, String userId,
                                             String orgId, String componentRange, String deptName
    );

}
