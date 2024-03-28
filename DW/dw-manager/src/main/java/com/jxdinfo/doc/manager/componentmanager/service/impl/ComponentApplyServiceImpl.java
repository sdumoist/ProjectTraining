package com.jxdinfo.doc.manager.componentmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.componentmanager.dao.ComponentApplyMapper;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
@Service
public class ComponentApplyServiceImpl  extends ServiceImpl<ComponentApplyMapper, ComponentApply>
        implements ComponentApplyService {

    /**
     * 科研成果服务类
     */
    @Resource
    ComponentApplyMapper componentApplyMapper;

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
    @Override
    public List<ComponentApply> componentList(String componentNameStr, Integer componentType,
                                              Integer componentState, Integer beginIndex, Integer limit,
                                              Integer componentOrigin,String userId,  String orgId,String componentRange,String deptName, String awardType
          ) {
        return componentApplyMapper.componentList(componentNameStr,componentType,componentState,beginIndex,limit,
                componentOrigin,userId,orgId, componentRange,deptName, awardType);
    }


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
    @Override
    public Integer componentListCount(String componentNameStr, Integer componentType, Integer componentState,
                                      Integer componentOrigin, String userId,String orgId,String componentRange, String deptName, String awardType) {
        return componentApplyMapper.componentListCount(componentNameStr,componentType,
                componentState,componentOrigin,userId,orgId,componentRange, deptName, awardType);
    }

    /**
     * 个人成果数量
     * @return 姓名与成果个数的Map集合
     */
    @Override
    public List<Map> componentCount() {
        return componentApplyMapper.componentCount();
    }

    /**
     * 部门成果数量
     * @return 姓名与成果个数的Map集合
     */
    @Override
    public List<Map> componentDeptCount() {
        return componentApplyMapper.componentDeptCount();
    }

    @Override
    public Integer componentDateCount() {
        return componentApplyMapper.componentDateCount();
    }

    @Override
    public List<ComponentApply> componentGraphCount(String dateStart,String dateEnd,String date,Integer state,String order) {
        return componentApplyMapper.componentGraphCount(dateStart,dateEnd,date,state,order);
    }

    @Override
    public List<String> componentTopCount() {
        return componentApplyMapper.componentTopCount();
    }

    @Override
    public List<ComponentApply> componentGraphCountBg(String dateStart,String dateEnd,String date, Integer state,String order) {
        return componentApplyMapper.componentGraphCountBg(dateStart,dateEnd,date,state,order);
    }

    @Override
    public List<ComponentApply> componentListStates(String componentNameStr, Integer componentType,
                                                    Integer componentState, Integer beginIndex, Integer limit,
                                                    Integer componentOrigin, String userId, String orgId,
                                                    String componentRange, String[] states,Integer order,String [] dept) {
        return componentApplyMapper.componentListStates(componentNameStr,componentType,componentState,
                beginIndex,limit,componentOrigin,userId,orgId,componentRange,states,order,dept);
    }

    @Override
    public Integer componentListStatesCount(String componentNameStr, Integer componentType, Integer componentState,
                                            Integer componentOrigin, String userId, String orgId, String componentRange, String[] states,String [] dept) {
        return componentApplyMapper.componentListStatesCount(componentNameStr,componentType,
                componentState,componentOrigin,userId,orgId,componentRange,states,dept);
    }


    @Override
    public List<ComponentApply> componentListMobile(String componentNameStr, Integer componentType,
                                              Integer componentState, Integer beginIndex, Integer limit,
                                              Integer componentOrigin,String userId,  String orgId,String componentRange,String deptName
    ) {
        return componentApplyMapper.componentListMobile(componentNameStr,componentType,componentState,beginIndex,limit,
                componentOrigin,userId,orgId, componentRange,deptName);
    }
}
