package com.jxdinfo.doc.manager.commonFolderManager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.commonFolderManager.model.FsCommonFolder;

import java.util.List;
import java.util.Map;

/**
 * 常用目录表Service接口
 */
public interface IFsCommonFolderService extends IService<FsCommonFolder> {

    /**
     * 获取用户所有常用目录
     * @param userId
     * @return
     */
    public List<FsCommonFolder> selectAllCommonFold(String userId, int startIndex,int pageSize, String order);

    /**
     * 批量增加常用目录
     * @param ids
     */
    Map<String,Object> addCommonFold(String ids);

    /**
     * 批量删除常用目录
     * @param ids
     */
    void deleteCommonFold(String ids);

    /**
     * 获取当前显示顺序最大值
     * @return
     */
    Integer getMaxOrder();

    /**
     * 更新常用目录
     */
    void updateCommonFold(String commonFolderId, String commonFolderName);

    /**
     * 移动目录
     * @param idOne
     * @param idTwo
     * @return
     */
    void moveFolder(String idOne,String idTwo);
}
