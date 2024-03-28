package com.jxdinfo.doc.manager.docintegral.service.impl;

import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.manager.docintegral.dao.IntegralRuleMapper;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class IntegralRuleServiceImpl implements IntegralRuleService {
    @Resource
    private IntegralRuleMapper integralRuleMapper;
    @Resource
    private CacheToolService cacheToolService;
    @Autowired
    private HussarCacheManager hussarCacheManager;
    /**
     * @param map 保存积分规则的map
     * @return 影响条数，判断是否新增成功
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 新增积分规则
     */
    @Override
    public int newIntegralRule(Map map) {
        return integralRuleMapper.newIntegralRule(map);
    }

    /**
     * @param map 需要更新的规则的参数
     * @return 影响条数，判断是否更新成功
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 更新积分规则
     */
    @Override
    public int updateIntegralRule(Map map) {
        if (null != map.get("ruleCode")){
            hussarCacheManager.setObject(CacheConstant.RULE_CODE_LIST,
                    CacheConstant.PREX_RULE_CODE_LIST + map.get("ruleCode").toString(),null);
        }
        return integralRuleMapper.updateIntegralRule(map);
    }

    @Override
    public int getIntegralRuleCount() {
        return integralRuleMapper.getIntegralRuleCount();
    }

    /**
     * @return 积分规则列表
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 获取所有的积分规则
     */
    @Override
    public List<Map> getIntegralRule(int startIndex, int pageSize) {
        return integralRuleMapper.getIntegralRule(startIndex, pageSize);
    }

    /**
     * @param map 需要检查的参数
     * @return 查询得到的个数，不为1则重复
     * @author luzhanzhao
     * @date 2018-12-03
     */
    @Override
    public int inputCheck(Map map) {
        return integralRuleMapper.inputCheck(map);
    }

    /**
     * @param ruleId 要获取的规则的id
     * @return 该id对应的规则
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 通过规则id获取规则
     */
    @Override
    public Map getIntegralRuleById(String ruleId) {
        return integralRuleMapper.getIntegralRuleById(ruleId);
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-03
     * @description 根据id删除积分规则
     * @param ids 要删除的id
     * @return 影响的条数
     */
    @Override
    public int deleteIntegralRule(String[] ids) {
        List<Map> rules = integralRuleMapper.getIntegralRuleByIds(ids);
        rules.forEach(rule -> {
            if (null != rule.get("ruleCode")){
                String ruleCode = rule.get("ruleCode").toString();
                hussarCacheManager.setObject(CacheConstant.RULE_CODE_LIST,
                        CacheConstant.PREX_RULE_CODE_LIST + ruleCode,null);
            }

        });
        return integralRuleMapper.deleteIntegralRule(ids);
    }

    /**
     * @param code 需要获取规则的积分编码
     * @return 积分编码对应的规则
     * @author yjs
     * @date 2018-12-03
     * @description 通过积分编码获取规则
     */
    @Override
    public List<Map<String, Object>> getRuleByCode(String code) {
        return cacheToolService.getRuleByCode(code);
    }
}
