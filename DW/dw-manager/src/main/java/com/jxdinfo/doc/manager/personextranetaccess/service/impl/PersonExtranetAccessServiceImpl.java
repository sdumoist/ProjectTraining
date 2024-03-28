package com.jxdinfo.doc.manager.personextranetaccess.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.folderextranetauth.model.FolderExtranetAuth;
import com.jxdinfo.doc.manager.personextranetaccess.dao.PersonExtranetAccessMapper;
import com.jxdinfo.doc.manager.personextranetaccess.model.SysPersonnelNetworkPermissions;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@Service
public class PersonExtranetAccessServiceImpl extends ServiceImpl<PersonExtranetAccessMapper,SysPersonnelNetworkPermissions> implements PersonExtranetAccessService {

    @Resource
    private PersonExtranetAccessMapper personExtranetAccessMapper;


    @Override
    public List<SysPersonnelNetworkPermissions> accessList(int startIndex, int pageSize, String personName) {
        return personExtranetAccessMapper.accessList(startIndex, pageSize, personName);
    }



    @Override
    public int getAccessListCount(String personName) {
        return personExtranetAccessMapper.getAccessListCount(personName);
    }

    @Override
    public String getDepartmentName(String parentId) {
        return personExtranetAccessMapper.getDepartmentName(parentId);
    }

    @Override
    public String getStruName(String struId) {
        return personExtranetAccessMapper.getStruName(struId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePersonExtrannetAccess(String param) {
        //删除
        personExtranetAccessMapper.delete(new QueryWrapper<SysPersonnelNetworkPermissions>());
        // 批量添加
        if (ToolUtil.isNotEmpty(param)) {
            JSONArray jsonArray = JSONArray.parseArray(param);

            List<SysPersonnelNetworkPermissions> permissions = new ArrayList<SysPersonnelNetworkPermissions>();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject personObject = jsonArray.getJSONObject(i);
                SysPersonnelNetworkPermissions networkPermissions = new SysPersonnelNetworkPermissions();
                String departmentName = String.valueOf(personObject.get("department"));
                networkPermissions.setUserId(String.valueOf(personObject.get("userId")));
                networkPermissions.setUserName(String.valueOf(personObject.get("userName")));
                networkPermissions.setDepartment(getStruName(departmentName));
                networkPermissions.setCreateUserName(ShiroKit.getUser().getName());
                networkPermissions.setCreateUserId(ShiroKit.getUser().getId());
                networkPermissions.setCreateTime(ts);
                permissions.add(networkPermissions);
            }
            if (permissions != null && permissions.size() > 0) {
                this.saveBatch(permissions);
            }
        }

    }

    @Override
    public List<String>  selectAllUserId() {
        return personExtranetAccessMapper.selectAllUserId();
    }

    @Override
    public boolean existsUser(String userName) {
        String name = this.personExtranetAccessMapper.existsUser(userName);
        return name != null && name != "";
    }
    @Override
    public boolean existsUserId(String userId) {
        String id = this.personExtranetAccessMapper.existsUserId(userId);
        return id != null && id != "";
    }

}
