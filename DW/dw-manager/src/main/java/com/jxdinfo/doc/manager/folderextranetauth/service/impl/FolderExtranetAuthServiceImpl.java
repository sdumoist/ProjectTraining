package com.jxdinfo.doc.manager.folderextranetauth.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.folderextranetauth.dao.FolderExtranetAuthMapper;
import com.jxdinfo.doc.manager.folderextranetauth.model.FolderExtranetAuth;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderExtranetAuthServiceImpl extends ServiceImpl<FolderExtranetAuthMapper, FolderExtranetAuth> implements IFolderExtranetAuthService {

    @Autowired
    private FolderExtranetAuthMapper folderExtranetAuthMapper;

    @Autowired
    private IFsFolderService iFsFolderService;

    /**
     * 查询所有有外网访问权限的目录id集合
     *
     * @return
     */
    @Override
    public List<String> getFolderExtranetListMobile() {
        List<String> folderIds = folderExtranetAuthMapper.selectList(new QueryWrapper<>()).
                stream().map(FolderExtranetAuth::getFolderId).collect(Collectors.toList());
        return folderIds;
    }

    @Override
    public String getFoldId(String id) {

        return folderExtranetAuthMapper.getFoldId(id);
    }

    /**
     * 分页查询目录外网访问权限
     *
     * @param page       分页对象
     * @param folderName 目录名称
     * @return 分页对象
     */
    @Override
    public List<FolderExtranetAuth> selectFolderExtranetAuths(Page<FolderExtranetAuth> page, String folderName) {
        List<FolderExtranetAuth> auths = folderExtranetAuthMapper.selectFolderExtranetAuths(page, folderName);
        return auths;
    }

    @Override
    public boolean  exis(String id) {
        String folderId = getFoldId(id);
        String exisit = folderExtranetAuthMapper.existsFold(id);
        if (exisit == null){
            String doc = folderExtranetAuthMapper.existsFold(folderId);
            if (doc  == null  ){
                return false;
            }
        }
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFolderExtranetAuth(String param) {
        // 删除
        folderExtranetAuthMapper.delete(new QueryWrapper<FolderExtranetAuth>());

        // 批量添加
        if (ToolUtil.isNotEmpty(param)) {
            JSONArray jsonArray = JSONArray.parseArray(param);

            List<FolderExtranetAuth> folderExtranetAuths = new ArrayList<FolderExtranetAuth>();
            Timestamp ts = new Timestamp(System.currentTimeMillis());

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject folderExtranetObj = jsonArray.getJSONObject(i);
                FolderExtranetAuth folderExtranet = new FolderExtranetAuth();
                folderExtranet.setFolderId(String.valueOf(folderExtranetObj.get("folderId")));
                folderExtranet.setFolderName(String.valueOf(folderExtranetObj.get("folderName")));
                folderExtranet.setCreateUserName(ShiroKit.getUser().getName());
                folderExtranet.setCreateUserId(ShiroKit.getUser().getId());
                folderExtranet.setCreateTime(ts);
                folderExtranetAuths.add(folderExtranet);
            }
            if (folderExtranetAuths != null && folderExtranetAuths.size() > 0) {
                this.saveBatch(folderExtranetAuths);
            }
        }
    }

}
