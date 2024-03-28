package com.jxdinfo.doc.manager.docrecycle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docrecycle.model.DocRecycle;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 回收站 服务类
 * </p>
 *
 * @author 
 * @since 2018-08-09
 */
public interface IDocRecycleService extends IService<DocRecycle> {

    /**
     * 加载回收站文件列表
     * @param pageNum
     * @param limitNum
     * @param fileName
     * @return
     */
    Map<String,Object> getDocRecycleList(String pageNum, String limitNum, String fileName);
    /**
     * 加载回收站文件列表
     * @param pageNum
     * @param limitNum
     * @param fileName
     * @param order
     * @return
     */
    Map<String,Object> getDocRecycleList(String pageNum, String limitNum, String fileName, String order);
    Map<String,Object> getDocRecycleListClient(String pageNum, String limitNum, String fileName, String order,String userId,String orgId);
    Map<String,Object> getDocRecycleListMobile(String pageNum, String limitNum, String fileName, String order, String userId, String orgId, List folderIds);
    /**
     * @author: ChenXin
     * @Param: fileId
     * @Param: folderId
     * @return boolean
     */

    boolean restore(String fileId, String folderId);

     /**
      * @author: ChenXin
      * @return  boolean
      */
    boolean clear();
    boolean clearClient(String userId,String orgId);
    boolean checkDocExist(String folderId, String fileName);

    /**
     * 判断目录下是否存在同名待审核文件
     * @param folderId 目录ID
     * @param fileName 文件名
     * @return
     */
    boolean checkAuditDocExist(String folderId, String fileName);
}
