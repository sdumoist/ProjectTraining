package com.jxdinfo.doc.manager.handovermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.dao.HandOverMapper;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOver;
import com.jxdinfo.doc.manager.handovermanager.service.HandOverService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class HandOverServiceImpl extends ServiceImpl<HandOverMapper,
        DocHandOver> implements HandOverService {
    @Resource
    private  HandOverMapper handOverMapper;
     public List<DocHandOver> getMessageList(Integer beginNum, Integer limit, String handovername, String acceptName,
                                             String deptName, String acceptDeptName,Integer state,String
                                                     orgId,Integer adminFlag,String order,String isDesc){
        return  handOverMapper.getMessageList(beginNum,limit,handovername,acceptName,deptName,acceptDeptName,state, orgId, adminFlag,order,isDesc);
     }
    public Integer getMessageListCount( String handovername, String acceptName, String deptName, String acceptDeptName,Integer state,String orgId,Integer adminFlag){
       return  handOverMapper.getMessageListCount(handovername,acceptName,deptName,acceptDeptName,state,orgId,adminFlag);
    }

    @Override
    public List<FsFolderView> getList(String userId, Integer pageNumber, Integer pageSize, String name, String order, String type,String isDesc) {
        return handOverMapper.getList(userId,pageNumber,pageSize,name,order,type,isDesc);
    }

    @Override
    public Integer getCount(String userId, String name, String type) {
        return handOverMapper.getCount(userId,name,type);
    }
}
