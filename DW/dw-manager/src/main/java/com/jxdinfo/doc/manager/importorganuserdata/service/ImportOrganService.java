package com.jxdinfo.doc.manager.importorganuserdata.service;

public interface ImportOrganService {

    /**
     * 验证数据库中是否存在相同的组织机构
     * 根据组织机构名称和父组织机构名称判断
     * @param organName 组织机构名称
     * @param parentOrganName 父组织机构名称
     * @return
     */
    boolean struNameExists(String organName, String parentOrganName);

    /**
     * 验证数据库中是否存在相同的登录账号
     *
     * @param userAccount 登录账号
     * @return
     */
    boolean userAccountExists(String userAccount);
}
