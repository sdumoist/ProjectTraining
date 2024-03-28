package com.jxdinfo.doc.manager.componentmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.componentmanager.dao.MultiplexProjectMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.model.YYZCProject;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 运营支持项目业务层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:45
 */
@Service
public class MultiplexProjectServiceImpl  extends ServiceImpl<MultiplexProjectMapper, MultiplexProject>
        implements MultiplexProjectService {

    /**
     * 复用登记服务类
     */
    @Resource
    private MultiplexProjectMapper multiplexProjectMapper;

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @param beginIndex 初始页码
     * @param limit 每页信息数量
     * @return  部门项目列表
     */
    @Override
    public List<MultiplexProject> multiplexList(String title, String deptNameStr, Integer beginIndex,
                                                Integer limit,String orgId,String dateStart,String dateEnd) {
        return multiplexProjectMapper.multiplexList(title,deptNameStr,beginIndex,limit,orgId,dateStart,dateEnd);
    }

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @return
     */
    @Override
    public Integer multiplexListCount(String title, String deptNameStr,String orgId,String dateStart,String dateEnd) {
        return multiplexProjectMapper.multiplexListCount(title,deptNameStr,orgId,dateStart,dateEnd);
    }

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @param beginIndex 初始页码
     * @param limit 每页信息数量
     * @return  部门项目列表
     */
    @Override
    public List<MultiplexProject> multiplexListByDept(String title, String deptNameStr, Integer beginIndex,
                                                Integer limit,String orgId,String caUserName) {
        return multiplexProjectMapper.multiplexListByDept(title,deptNameStr,beginIndex,limit,orgId,caUserName);
    }

    /**
     *
     * @param title 项目名称
     * @param deptNameStr 部门名称集合
     * @return
     */
    @Override
    public Integer multiplexListCountByDept(String title, String deptNameStr,String orgId,String caUserName) {
        return multiplexProjectMapper.multiplexListCountByDept(title,deptNameStr,orgId,caUserName);
    }
    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param orgName 用户名称
     * @param beginIndex 页数
     * @return 部门信息集合
     */
    @Override
    public List<YYZCProject> projectList(String title,String orgName ,Integer beginIndex, Integer limit) {
        return multiplexProjectMapper.projectList(title,orgName,beginIndex,limit);
    }

    /**
     * 项目列表
     * @param title 项目名称
     * @param orgName 用户名称
     * @return Integer
     */
    @Override
    public Integer projectListCount(String title,String orgName) {
        return multiplexProjectMapper.projectListCount(title,orgName);
    }

    /**
     * 项目列表
     * @param title 项目名称
     * @param limit 每页信息数量
     * @param userId 用户名称
     * @param beginIndex 页数
     * @return 成果表集合
     */
    @Override
    public List<ComponentApply> myComponentList(String title,String userId, Integer beginIndex, Integer limit) {
        return multiplexProjectMapper.myComponentList(title,userId,beginIndex,limit);
    }

    /**
     * 组件列表
     * @param title 组件名称
     * @param userId 用户Id
     * @return Integer
     */
    @Override
    public Integer myComponentListCount(String title,String userId) {
        return multiplexProjectMapper.myComponentListCount(title,userId);
    }

    /**
     * 组件应用的项目列表
     * @param projectId 项目Id
     * @return 成果表集合
     */
    @Override
    public List<ComponentApply> getComponentList(String projectId) {
        return multiplexProjectMapper.getComponentList(projectId);
    }

    /**
     * 根据成果Id查询复用情况
     * @param componentId 成果Id
     * @param beginIndex 初始页数
     * @param limit 每页信息数量
     * @param projectName 复用项目名称
     * @return 复用表集合
     */
    @Override
    public List<MultiplexProject> selectComponentProject(String componentId,Integer beginIndex, Integer limit,
    String projectName) {return multiplexProjectMapper.selectComponentProject(componentId,beginIndex,limit
            ,projectName);
    }

    /**
     *
     * @param componentId 成果id
     * @param projectName 部门名称
     * @return Integer
     */
    @Override
    public Integer componentProjectCount(String componentId,String projectName) {
        return multiplexProjectMapper.componentProjectCount(componentId,projectName);
    }

    /**
     *
     * @param title 项目名称
     * @param userId 用户名称
     * @param beginIndex 初始页数
     * @param limit 每页信息数量
     * @return 复用表集合
     */
    @Override
    public List<MultiplexProject> myMultiplexList(String title, String userId, Integer beginIndex, Integer limit) {
        return multiplexProjectMapper.myMultiplexList(title,userId,beginIndex,limit);
    }

    /**
     *
     * @param title 项目名称
     * @param userId 用户名称
     * @return integer
     */
    @Override
    public Integer myMultiplexListCount(String title, String userId) {
        return multiplexProjectMapper.myMultiplexListCount(title,userId);
    }



    @Override
    public List<MultiplexProject> multiplexGraphCount(String  dateStart,String dateEnd,String date,String order) {
        return multiplexProjectMapper.multiplexGraphCount(dateStart,dateEnd,date,order);
    }

    @Override
    public List<String> componentTopCount() {
        return null;
    }

    @Override
    public List<MultiplexProject> multiplexGraphCountBg(String  dateStart,String dateEnd,String date,String order) {
        return multiplexProjectMapper.multiplexGraphCountBg(dateStart,dateEnd,date,order);
    }

    @Override
    public List<MultiplexProject> componentMultiplexList(String componentId) {
        return  multiplexProjectMapper.componentMultiplexList(componentId);
    }

    @Override
    public List<MultiplexProject> componentMultiplexProjectList(String projectId) {
        return multiplexProjectMapper.componentMultiplexProjectList(projectId);
    }
}
