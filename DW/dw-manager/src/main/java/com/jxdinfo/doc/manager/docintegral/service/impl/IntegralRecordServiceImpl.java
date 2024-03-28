package com.jxdinfo.doc.manager.docintegral.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docintegral.dao.IntegralRecordMapper;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 积分接口
 *
 * </p>
 *
 * @author yjs
 * @since 2018-12-3
 */
@Service
public class IntegralRecordServiceImpl extends ServiceImpl<IntegralRecordMapper,IntegralRecord> implements IntegralRecordService {

    /**
     * 文档信息DAO层
     */
    @Resource
    private IntegralRuleService integralRuleService;

    @Resource
    private IntegralRecordMapper integralRecordMapper;

    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private DocGroupService docGroupService;
    /**
     * 增加积分的接口
     * @author: yjs
     * @param docId 文档ID
     * @param userId 用户ID
     * @param ruleCode 用户操作类型
     * @return
     */
    @Override
    public Integer addIntegral(String docId, String userId, String ruleCode) {
        if(userId.equals("superadmin")||userId.equals("wkadmin")){
            return null;
        }
        if (!"".equals(ruleCode) && ruleCode != null) {
            //根据用户操作编码去规则表查找积分
            List<Map<String, Object>> list = integralRuleService.getRuleByCode(ruleCode);
            Integer dayLimit = (Integer)integralRuleService.getRuleByCode("dayLimit").get(0).get("ruleIntegral");
            //没有规则记录的直接返回0
            if (list == null || list.size() == 0) {
                return null;
            }
            if(ruleCode.equals("download")){
                int count=   integralRecordMapper .checkInDoc(docId);
                if(count!=0){
                    return null;
                }
                DocInfo docInfo= docInfoService.getDocDetail(docId);
                String foldId = docInfo.getFoldId();
                int folderCount=   integralRecordMapper .checkInDoc(foldId);
                if(folderCount!=0){
                    return null;
                }

            }
            Map<String, Object> map = list.get(0);
            //无效的规则直接返回0
            if (map.get("valid").equals("0")) {
                return null;
            } else {
                Integer userIntegralCount=  integralRecordMapper.getTotalIntegral(userId);
                if (null == userIntegralCount){
                    Map defaultBonus = integralRuleService.getRuleByCode("defaultBonus").get(0);
                    //插入积分操作表
                    IntegralRecord integralRecord = new IntegralRecord();

                    integralRecord.setDocId(docId);
                    integralRecord.setRecordId(UUID.randomUUID().toString().replaceAll("-", ""));
                    integralRecord.setUserId(userId);
                    integralRecord.setIntegralState("1");
                    integralRecord.setIntegral((Integer)defaultBonus.get("ruleIntegral"));
                    integralRecord.setOperateRuleCode("defaultBonus");
                    integralRecord.setRuleName((String) defaultBonus.get("ruleName"));
                    integralRecord.setRuleDes((String) defaultBonus.get("ruleDes"));
                    Timestamp ts = new Timestamp(System.currentTimeMillis() - 24*60*60*1000);
                    integralRecord.setOperateTime(ts);
                    integralRecordMapper.insertRecord(integralRecord);
                }
                Integer realIntegral = 0;
                Integer integral = (Integer) map.get("ruleIntegral");
                Integer times = (Integer) map.get("max_times");
                // 查看今天一天总计获得的积分
                Integer todayIntegral = integralRecordMapper.getIntegralByToday(userId);
                Integer todayTimes= integralRecordMapper.getIntegralTimesByToday(userId,ruleCode);
                if(todayTimes!=null){
                    if(todayTimes+1>times && times != -1){
                        realIntegral=0;
                        return realIntegral;
                    }
                }
                if(todayIntegral==null){
                    todayIntegral=0;
                }
                if(integral<0){
                  Integer totalntegral=  integralRecordMapper.getTotalIntegral(userId);
                    if((totalntegral+integral)<0){
                        realIntegral = 0;
                        return realIntegral;
                    }

                }
                //一天积分不能超过50
                if(integral+todayIntegral>dayLimit){
                    realIntegral = dayLimit-todayIntegral;
                }else{
                    realIntegral = integral;
                }
                if(realIntegral !=0){
                    //插入积分操作表
                    IntegralRecord integralRecord = new IntegralRecord();
                    integralRecord.setDocId(docId);
                    integralRecord.setRecordId(UUID.randomUUID().toString().replaceAll("-", ""));
                    integralRecord.setUserId(userId);
                    integralRecord.setIntegralState("1");
                    integralRecord.setIntegral(realIntegral);
                    integralRecord.setOperateRuleCode(ruleCode);
                    integralRecord.setRuleName((String) map.get("ruleName"));
                    integralRecord.setRuleDes((String) map.get("ruleDes"));
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    integralRecord.setOperateTime(ts);
                    integralRecordMapper.insertRecord(integralRecord);
                }
                return  realIntegral;
            }
        }
        return  0;
    }

    /**
     * 查看用户所以积分
     * @author: yjs
     * @param userId 用户ID
     * @return Integer 积分
     */
    @Override
    public Integer showIntegral(String userId) {
        Integer Integral=0;
        Integral =integralRecordMapper.getTotalIntegral(userId);
        if(Integral==null){
            Integral=0;
        }
        return Integral;
    }
    /**
     * 查看用户特定操作类型的积分和
     * @author: zgr
     * @param userId    用户ID
     * @param ruleCodes 操作类型
     * @return  积分和
     */
    @Override
    public Integer getIntegralByType(String userId, String[] ruleCodes) {
        return integralRecordMapper.getIntegralByType(userId, ruleCodes);
    }

    /**
     * 查看积分排行
     * @author: yjs
     * @return List<Map<String,Object>>
     */
    @Override
    public List<Map<String,Object>> getIntegralRank() {

        return integralRecordMapper.getIntegralRank();
    }
    /**
     * 查看用户积分明细
     * @author: zgr
     * @param userId 用户ID
     * @param integralState  积分状态（1）
     * @return 集合
     */
    @Override
    public List<Map<String,Object>> getIntegralHistories(String userId,String integralState,String[] ruleCodes) {
        return integralRecordMapper.getIntegralHistories(userId,integralState,ruleCodes);
    }
    @Override
    public List<Map<String, Object>> getIntegralRank(int startIndex, int pageSize) {
        return integralRecordMapper.getIntegralRankByPage(startIndex,pageSize);
    }

    /**
     * @author luzhanzhao
     * @date 2018-12-06
     * @description 获取需要排行的用户数
     * @returniconUpdateNameFolder
     */
    @Override
    public int getIntegralUserCount() {
        return integralRecordMapper.getIntegralUserCount();
    }

    @Override
    public int selectDocCountByUser(String[] docIds, String userId) {
        return integralRecordMapper.selectDocCountByUser(docIds,userId);
    }

    @Override
    public Integer getIntegralTimesByToday(String userId, String ruleCode) {
        return integralRecordMapper.getIntegralTimesByToday(userId,ruleCode);
    }

    @Override
    public int selectDownloadedCount(String[] ids, String userId) {
        return integralRecordMapper.selectDownloadedCount(ids,userId);
    }

    @Override
    public Integer getRankNum(String userId) {
        return integralRecordMapper.getRankNum(userId);
    }

    @Override
    public int recordIsNull(String userId) {
        return integralRecordMapper.recordIsNull(userId);
    }

    public int checkInDoc(String docId){
    return     integralRecordMapper.checkInDoc(docId);
    }

}
