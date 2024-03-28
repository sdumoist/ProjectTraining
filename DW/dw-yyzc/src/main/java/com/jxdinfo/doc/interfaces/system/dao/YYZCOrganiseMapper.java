package com.jxdinfo.doc.interfaces.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.interfaces.system.model.YYZCOrganise;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface YYZCOrganiseMapper  extends BaseMapper<YYZCOrganise> {
    /**
     * 运营支撑组织机构同步插入
     * @param organisesList
     * @throws Exception
     */
    void insertList(List<YYZCOrganise> organisesList) throws Exception;
    /**
     * organ表插入
     * @param organisesList
     * @throws Exception
     */
    void insertOrganList(List<YYZCOrganise> organisesList) throws Exception;
    /**
     * Stru表插入
     * @param organisesList
     * @throws Exception
     */
    void insertStruList(List<YYZCOrganise> organisesList) throws Exception;
    /**
     * Stru根节点
     * @throws Exception
     */
    void updateStruRoot() throws Exception;
    /*删除离职人员*/
    void delLeave() throws Exception;

    void updateYYZC(List<YYZCOrganise> organisesList) throws Exception;
    void updateOrgan(List<YYZCOrganise> organisesList) throws Exception;
    void updateStru(List<YYZCOrganise> organisesList) throws Exception;


}
