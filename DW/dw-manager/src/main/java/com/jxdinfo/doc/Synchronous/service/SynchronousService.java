package com.jxdinfo.doc.Synchronous.service;

import com.alibaba.fastjson.JSON;

/**
 * 同步组织机构和用户
 */
public interface SynchronousService {



    /**
     * 同步用户
     *
     * @return
     */

    public JSON synchronousUser();

    /**
     * 同步staff表
     *
     * @return
     */
    public JSON synchronousStaffList();

    /**
     * 同步人员users表
     *
     * @return
     */
    public JSON synchronousUserList();

    /**
     * 同步组织机构
     * isEmployee 1:同步组织机构  2:同步用户
     *
     * @return
     */
    public JSON synchronousOrgan(String isEmployee);

    /**
     * 同步单个用户数据
     *
     * @return
     */
    public JSON synchronousOneUserDate(String userId, String account);

}
