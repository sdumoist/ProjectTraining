package com.jxdinfo.doc.mobileapi.foldermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 目录表 服务类
 * </p>
 * @author zf
 * @since 2018-09-06
 */
public interface IMobileFsFolderService extends IService<FsFolder> {
    /**
     * 动态加载目录树
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    List getTreeDataLazyClient(String id, String type, String userId, String orgId, List<String> listGroup, List<String> roleList);
    List getTreeDataLazyClientMobile(String id, String type, String userId, String orgId, List<String> listGroup, List<String> roleList, HttpServletRequest request);
    Map<String,Object> isUserExtranet(String userId);
}
