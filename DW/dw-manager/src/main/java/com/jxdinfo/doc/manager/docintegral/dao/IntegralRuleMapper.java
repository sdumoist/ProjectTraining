package com.jxdinfo.doc.manager.docintegral.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 积分规则Mapper
 * @author luzhanzhao
 * @date 2018-12-3
 */
public interface IntegralRuleMapper {
    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 新增积分规则
     * @param map 保存积分规则的map
     * @return 影响条数，判断是否新增成功
     */
    int newIntegralRule(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 更新积分规则
     * @param map 需要更新的规则的参数
     * @return 影响条数，判断是否更新成功
     */
    int updateIntegralRule(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取所有的积分规则
     * @return 积分规则列表
     */
    List<Map> getIntegralRule(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 通过规则id获取规则
     * @param ruleId 要获取的规则的id
     * @return 该id对应的规则
     */
    Map getIntegralRuleById(@Param("ruleId") String ruleId);

    /**
     * @author yjs
     * @date 2018-12-03
     * @description 通过积分编码获取规则
     * @param code 需要获取规则的积分编码
     * @return 积分编码对应的规则
     */
    List<Map<String,Object>> getRuleByCode(@Param("code") String code);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @param map 需要检查的参数
     * @return 查询得到的个数，不为1则重复
     */
    int inputCheck(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取积分规则总条数
     * @return 总条数
     */
    int getIntegralRuleCount();

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 根据id删除积分规则
     * @param ids 要删除的id
     * @return 影响的条数
     */
    int deleteIntegralRule(String[] ids);

    /**
     * @author luzhanzhao
     * @date 2018-12-22
     * @description 通过id获取积分规则
     * @param ids 要获取积分规则详情的规则id
     * @return 积分规则详情
     */
    List<Map> getIntegralRuleByIds(@Param("ids") String[] ids);

}
