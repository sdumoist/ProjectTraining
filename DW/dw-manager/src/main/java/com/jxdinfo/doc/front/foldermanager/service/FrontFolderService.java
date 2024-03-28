package com.jxdinfo.doc.front.foldermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前台目录表 服务类
 * </p>
 * @author zf
 * @since 2018-09-06
 */
public interface FrontFolderService extends IService<FsFolder> {

    /**
     * 获取根节点信息
     * @return 目录信息
     */
    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams);

    /**
     * 动态加载目录树b
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    List getTreeDataLazy(String id, String type);
    List getTreeDataLazyJqx(String id, String type);

    /**
     *获取下级目录信息
     * @param pageNumber 页数
     * @param pageSize 每页多少条
     * @param id 文件id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return
     */
    public List<FsFolderView> getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                                List groupList, String userId, Integer adminFlag,
                                                String operateType, String levelCodeString, String levelCode, String orgId,List roleList);

    /**
     * 获取下级文件及目录的数量
     * @param id 节点id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param orderResult
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    public int getFilesAndFloderNum(String id, String[] typeArr, String name, String orderResult,
                                    List groupList, String userId, Integer adminFlag, String operateType,
                                    String levelCodeString, String levelCode, String orgId,List roleList);

    /**
     * 获取子文件的数量
     * @param id 节点id
     * @param typeArr 文件类型
     * @param name 文件名称
     * @param groupList 群组id
     * @param userId 用户id
     * @param adminFlag 管理员标识
     * @param type 前后台标识
     * @return 文件的数量
     */
    public int getFileNum(String id, String[] typeArr, String name, List groupList, String userId,
                          Integer adminFlag, String type, String levelCodeList, String orgId,List roleList);


    public List<FsFolder> getFsFolderByName(String name);

    List<Map> getFolderList(String pFolderId, int startNum, int size,String userId, List groupList, Integer adminFlag);

    public List<DocInfo> getFileByAuthor(Integer pageNumber, Integer pageSize, String userName, List<String> groupList, String userId, Boolean adminFlag,
                                         String levelCode, String orgId, String[] typeArr,List roleList, Integer order);
    public int getFileByAuthorCount(String userName, List<String> groupList, String userId, Boolean adminFlag,
                                    String levelCode, String orgId, String[] typeArr,List roleList);
}
