package com.jxdinfo.doc.manager.componentmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.model.YYZCProject;

import java.util.List;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
public interface MultiplexProjectService extends IService<MultiplexProject> {


 /**
  *
  * @param title 项目名称
  * @param deptNameStr 部门名称集合
  * @param beginIndex 初始页码
  * @param limit 每页信息数量
     * @return  部门项目列表
     */
    List<MultiplexProject> multiplexList(String title, String deptNameStr,
                                         Integer beginIndex, Integer limit, String orgId, String dateStart, String dateEnd);


 /**
  *
  * @param title 项目名称
  * @param deptNameStr 部门名称集合
  * @return
     */
   Integer multiplexListCount(String title, String deptNameStr, String orgId,String dateStart,String dateEnd);

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @param beginIndex 初始页码
     * @param limit 每页信息数量
     * @return  部门项目列表
     */
    List<MultiplexProject> multiplexListByDept(String title, String deptNameStr,
                                               Integer beginIndex, Integer limit, String orgId, String caUserName);
    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @return
     */
    Integer multiplexListCountByDept(String title, String deptNameStr, String orgId, String caUserName);
    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param orgName 用户名称
     * @param beginIndex 页数
     * @return 部门信息集合
     */
    List<YYZCProject> projectList(String title, String orgName,
                                  Integer beginIndex, Integer limit);

    /**
     * 项目列表
     * @param title 项目名称
     * @param orgName 用户名称
     * @return Integer
     */
    Integer projectListCount(String title, String orgName);

    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param userId 用户名称
     * @param beginIndex 页数
     * @return 成果表集合
     */
    List<ComponentApply> myComponentList(String title, String userId,
                                         Integer beginIndex, Integer limit);

    /**
     * 组件列表
     * @param title 组件名称
     * @param userId 用户Id
     * @return Integer
     */
    Integer myComponentListCount(String title, String userId);

    /**
     * 组件应用的项目列表
     * @param projectId 项目Id
     * @return 成果表集合
     */
    List<ComponentApply>    getComponentList(String projectId);

 /**
  * 根据成果Id查询复用情况
  * @param componentId 成果Id
  * @param beginIndex 初始页数
  * @param limit 每页信息数量
  * @param projectName 复用项目名称
     * @return 复用表集合
     */
    List<MultiplexProject> selectComponentProject(String componentId, Integer beginIndex, Integer limit,
                                                  String projectName);

 /**
  *
  * @param componentId 成果id
  * @param projectName 部门名称
  * @return Integer
     */
    Integer componentProjectCount(String componentId, String projectName);

 /**
  *
  * @param title 项目名称
  * @param userId 用户名称
  * @param beginIndex 初始页数
  * @param limit 每页信息数量
     * @return 复用表集合
     */
    List<MultiplexProject> myMultiplexList(String title, String userId,
                                           Integer beginIndex, Integer limit);

 /**
  *
  * @param title 项目名称
  * @param userId 用户名称
  * @return integer
     */
    Integer myMultiplexListCount(String title, String userId);



    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<MultiplexProject> multiplexGraphCount(String dateStart, String dateEnd, String date, String order);

    List<String>componentTopCount();
    /**
     * 统计部门发布成果Bu，Gu
     * @return
     */
    List<MultiplexProject> multiplexGraphCountBg(String dateStart, String dateEnd, String date, String order);

    /**
     * 根据成果查询复用项目
     * @param componentId
     * @return
     */
    List<MultiplexProject> componentMultiplexList(String componentId);

    /**
     * 根据复用查成果
     * @param projectId
     * @return
     */
    List<MultiplexProject> componentMultiplexProjectList(String projectId);

}
