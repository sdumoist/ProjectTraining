package com.jxdinfo.doc.manager.personextranetaccess.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.personextranetaccess.model.SysPersonnelNetworkPermissions;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PersonExtranetAccessMapper extends BaseMapper<SysPersonnelNetworkPermissions> {


    List<SysPersonnelNetworkPermissions> accessList(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize,@Param("personName") String personName);

    List<String>  selectAllUserId();
    int getAccessListCount(@Param("personName") String personName);
    String existsUser(String userName);
    String existsUserId(String id);

    String getDepartmentName(String parentId);
    String getStruName(String struId);
    String getParentId(String struId);
}
