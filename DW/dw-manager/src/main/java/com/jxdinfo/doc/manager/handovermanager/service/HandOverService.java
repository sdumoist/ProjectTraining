package com.jxdinfo.doc.manager.handovermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOver;

import java.util.List;

public interface HandOverService extends IService<DocHandOver> {
    List<DocHandOver>  getMessageList(Integer beginNum,Integer limit,String handovername,String acceptName,String deptName,String acceptDeptName,Integer state,String orgId,Integer adminFlag,String order,String isDesc);
    Integer getMessageListCount(String handovername,String acceptName,String deptName,String acceptDeptName,Integer state,String orgId,Integer adminFlag);
    List<FsFolderView> getList(String userId,Integer pageNumber,Integer pageSize,String name,String order,String type,String isDesc);
            Integer getCount(String userId,String name,String type);
}
