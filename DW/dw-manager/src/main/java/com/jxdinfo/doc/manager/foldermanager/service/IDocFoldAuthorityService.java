package com.jxdinfo.doc.manager.foldermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
public interface IDocFoldAuthorityService extends IService<DocFoldAuthority> {


    public int findEdit(String id, List groupList, String userId);
    public int findEditClient(String id, List groupList, String userId,String orgId);
    public int findEditByUpload(String id, List groupList, String userId);
    public int findEditByUploadClient(String id, List groupList, String userId,String orgId);
    int findEditByUploadMobile(String id, List groupList, String userId);

    /**
     * 保存目录权限
     * @return 是否保存成功
     */
    public boolean saveDocFoldAuthority(FsFolderParams fsFolderEdit);

    /**
     * @Author zoufeng
     * @Description 查询文件是否可编辑 20181009重写
     * @Date 10:39 2018/10/9
     * @Param id 目录id groupList 群组id userid 用户id
     * @return 是否可编辑
     **/
    public int findEditNew(String id, List groupList, String userId);
    /**
     * @Author zoufeng
     * @Description 查询文件是否可编辑 20181009重写
     * @Date 10:39 2018/10/9
     * @Param id 目录id groupList 群组id userid 用户id
     * @return 是否可编辑
     **/
    public int findEditNewClient(String id, List groupList, String userId,String orgId);
    public String getDeptIds(String orgId);
}
