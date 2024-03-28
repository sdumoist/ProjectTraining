package com.jxdinfo.doc.common.docutil.service;

import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;

import java.util.List;
import java.util.Map;

/**
 * Created by zoufeng on 2018/9/10.
 */
public interface BusinessService {

    public List<Map<String, Object>> getDictListByType(String typeName);

    /**
     * 获取当前用户及用户组的目录层级码
     * @param groupList 群组id集合
     * @param userId    当前用户id
     * @param type      前后台
     * @return 目录层级码
     */
    public List<String> getlevelCodeList(List groupList, String userId, String type);

    /**
     * 上移下移交换showorder
     * @param table 表名
     * @param cloum 排序字段名
     * @param id 需要交换的id
     * @param nextid 被交换的id
     * @return
     */
    public int changeShowOrder(String table, String cloum, String id, String nextid);

    /**
     * 查询拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserClient(FsFolderParams fsFolderParams,String orgId);
    /**
     * 查询拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUser(FsFolderParams fsFolderParams);

    public String getLevelCodeByUserRecycle(FsFolderParams fsFolderParams);
    /**
     * 查询拥有权限的目录层级码
     * 手机端方法
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserMobile(FsFolderParams fsFolderParams);
    /**
     * 查询后台文件树拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCode(FsFolderParams fsFolderParams);

    /**
     * 查询后台文件树拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeClient(FsFolderParams fsFolderParams,String orgId);

    /**
     * 查询前台文件树拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeFront(FsFolderParams fsFolderParams);
    /**
     * 手机端方法
     * 查询前台文件树拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeFrontMobile(FsFolderParams fsFolderParams);
    /**
     * 查询后台文件树移动时拥有权限的目录层级码
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getMoveFileLevelCode(FsFolderParams fsFolderParams);

//    /**
//     * 查询拥有权限的文件所在目录层级码
//     *
//     * @param fsFolderParams 群组id，用户id，前后台标识
//     * @return string 逗号隔开形式（'2','3'）
//     */
//    public String getFileLevelCodeByUser(FsFolderParams fsFolderParams);

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getUpLevelCodeByUser(FsFolderParams fsFolderParams);
    public String getUpLevelCodeByUserClient(FsFolderParams fsFolderParams,String orgId);
    public String getLevelCodeByUserUpload(FsFolderParams fsFolderParams);
    public String getFolderIdByUserUpload(FsFolderParams fsFolderParams);
    public String getFolderIdByUserUploadClient(FsFolderParams fsFolderParams,String userId);
    public String getLevelCodeByUserUploadClient(FsFolderParams fsFolderParams,String orgId);
    public String getLevelCodeByUserUploadMobile(String userId, FsFolderParams fsFolderParams);

    public String getUploadLevelCodeFront(FsFolderParams fsFolderParams);
    public String getUploadLevelCodeFrontMobile(FsFolderParams fsFolderParams,String orgId);
}
