package com.jxdinfo.doc.manager.personextranetaccess.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.personextranetaccess.model.SysPersonnelNetworkPermissions;

import java.util.List;

public interface PersonExtranetAccessService extends IService<SysPersonnelNetworkPermissions> {

    List<SysPersonnelNetworkPermissions> accessList(int startIndex, int pageSize, String personName);


    int getAccessListCount(String personName);

    String getDepartmentName(String parentId);

    String getStruName(String struId);

    void savePersonExtrannetAccess(String param);

    List<String>  selectAllUserId();
    boolean existsUser(String userName);

    boolean existsUserId(String userId);

}
