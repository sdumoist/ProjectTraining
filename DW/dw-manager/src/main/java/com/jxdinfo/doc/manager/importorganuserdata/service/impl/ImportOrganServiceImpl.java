package com.jxdinfo.doc.manager.importorganuserdata.service.impl;

import com.jxdinfo.doc.manager.importorganuserdata.dao.ImportOrganMapper;
import com.jxdinfo.doc.manager.importorganuserdata.service.ImportOrganService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ImportOrganServiceImpl implements ImportOrganService {

    @Resource
    private ImportOrganMapper importOrganMapper;

    /**
     * 验证数据库中是否存在相同的组织机构
     * 根据组织机构名称和父组织机构名称判断
     *
     * @param organName       组织机构名称
     * @param parentOrganName 父组织机构名称
     * @return
     */
    @Override
    public boolean struNameExists(String organName, String parentOrganName) {
        // 父级组织机构为空 说明是挂在顶级目录下的
        List<Map> struMap = null;
        if (StringUtils.isEmpty(parentOrganName)) {
            struMap = importOrganMapper.getStruByNameAndParentId(organName);
        } else {
            struMap = importOrganMapper.getStruByNameAndParentName(organName, parentOrganName);
        }
        if (struMap != null && struMap.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 验证数据库中是否存在相同的登录账号
     *
     * @param userAccount 登录账号
     * @return
     */
    @Override
    public boolean userAccountExists(String userAccount) {

        List<Map> userMap = importOrganMapper.getUserByUserAccount(userAccount);
        if (userMap != null && userMap.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
