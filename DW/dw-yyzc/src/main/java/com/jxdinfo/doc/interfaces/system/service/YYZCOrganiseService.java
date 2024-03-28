package com.jxdinfo.doc.interfaces.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.interfaces.system.model.YYZCOrganise;

import java.util.List;

public interface YYZCOrganiseService  extends IService<YYZCOrganise> {
    /**
     * 更新运营支撑
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param
     * @return true or false
     */
    boolean insertOrUpdateYYZCOrganise(List<YYZCOrganise> organiseList);
}
