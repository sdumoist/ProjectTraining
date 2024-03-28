package com.jxdinfo.doc.manager.personalcenter.service.impl;

import com.jxdinfo.doc.manager.personalcenter.dao.PersonalOperateMapper;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
/**
 * 个人中心数据处理 服务实现类
 * @author luzhanzhao
 * @date 2018-11-19
 */
@Service
public class PersonalOperateServiceImpl implements PersonalOperateService {
    @Resource
    private PersonalOperateMapper operateMapper;
    @Override
    public List<Map> getMyHistory(String userId, String opType, int startIndex, int pageSize, String name
            ,String[] typeArr,String order,String levelCode,String orgId) {
        return operateMapper.getMyHistory(userId,opType,startIndex,pageSize, name,typeArr,order,levelCode,orgId);
    }
    @Override
    public List<Map> getMyHistoryMobile(String userId, String opType, int startIndex, int pageSize, String name
            ,String[] typeArr,String order,String levelCode,String orgId,List fodlerIds) {
        return operateMapper.getMyHistoryMobile(userId,opType,startIndex,pageSize, name,typeArr,order,levelCode,orgId,fodlerIds);
    }
    @Override
    public List<Map> getMyHistoryClient(String userId, String opType, int startIndex, int pageSize, String name
            ,String[] typeArr,String order,String levelCode,String orgId, String timeType) {
        return operateMapper.getMyHistoryClient(userId,opType,startIndex,pageSize, name,typeArr,order,levelCode,orgId,timeType);
    }
    @Override
    public int getMyHistoryCount(String userId, String opType, String name) {
        return operateMapper.getMyHistoryCount(userId,opType,name);
    }

    @Override
    public int getMyHistoryCountMobile(String userId, String opType, String name,List folderIds) {
        return operateMapper.getMyHistoryCountMobile(userId,opType,name,folderIds);
    }

    @Override
    public int getMyHistoryCountClient(String userId, String opType, String name, String timeType) {
        return operateMapper.getMyHistoryCountClient(userId,opType,name,timeType);
    }

    @Override
    public int deleteHistory(String[] histories, String userId, String opType) {
        return operateMapper.deleteHistory(histories, userId, opType);
    }

    @Override
    public int clearHistory(String userId, String opType) {
        return operateMapper.clearHistory(userId, opType);
    }

    @Override
    public int  getMyHistoryCountByFileId(String docId, String userId, String opType) {
        return operateMapper.getMyHistoryCountByFileId(docId,userId,opType);
    }

    @Override
    public int cancelCollection(String docId, String userId, String opType) {
        return operateMapper.cancelCollection( docId, userId, opType);
    }

    @Override
    public int deleteCollection(String[] ids) {
        return operateMapper.deleteCollection(ids);
    }
}
