package com.jxdinfo.doc.manager.importorganuserdata.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ImportOrganMapper {

    /**
     * 验证数据库中是否存在相同的组织机构
     * 根据组织机构名称和父组织机构名称判断
     *
     * @param organName       组织机构名称
     * @param parentOrganName 父组织机构名称
     * @return
     */
    List<Map> getStruByNameAndParentName(@Param("organName") String organName, @Param("parentOrganName") String parentOrganName);

    /**
     * 验证数据库中根目录下是否存在相同的组织机构
     * 根据组织机构名称
     *
     * @param organName 组织机构名称
     * @return
     */
    List<Map> getStruByNameAndParentId(@Param("organName") String organName);


    List<Map> getUserByUserAccount(@Param("userAccount") String userAccount);


}
