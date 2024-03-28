package com.jxdinfo.doc.manager.docintegral.service;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface IntegralRuleService {
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
     * @return
     */
    int getIntegralRuleCount();

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取所有的积分规则
     * @return 积分规则列表
     */
    List<Map> getIntegralRule(int startIndex, int pageSize);

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
     * @description 通过规则id获取规则
     * @param ruleId 要获取的规则的id
     * @return 该id对应的规则
     */
    Map getIntegralRuleById(String ruleId);

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 根据id删除积分规则
     * @param ids 要删除的id
     * @return 影响的条数
     */
    int deleteIntegralRule(String[] ids);


    /**
     * @author yjs
     * @date 2018-12-03
     * @description 通过积分编码获取规则
     * @param code 需要获取规则的积分编码
     * @return 积分编码对应的规则
     */
    List<Map<String,Object>> getRuleByCode(String code);
}
