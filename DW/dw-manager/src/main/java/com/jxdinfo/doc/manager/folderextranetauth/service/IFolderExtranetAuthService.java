package com.jxdinfo.doc.manager.folderextranetauth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.folderextranetauth.model.FolderExtranetAuth;

import java.util.List;

public interface IFolderExtranetAuthService extends IService<FolderExtranetAuth> {
    void saveFolderExtranetAuth(String ids);

    /**
     * 查询所有有外网访问权限的目录id集合
     * @return
     */
    List<String>  getFolderExtranetListMobile();
    String getFoldId(String id);
    boolean exis(String id);

    /**
     * 分页查询目录外网访问权限
     *
     * @param page       分页对象
     * @param folderName 用户名称
     * @return 分页对象
     */
    List<FolderExtranetAuth> selectFolderExtranetAuths(Page<FolderExtranetAuth> page, String folderName);

}
