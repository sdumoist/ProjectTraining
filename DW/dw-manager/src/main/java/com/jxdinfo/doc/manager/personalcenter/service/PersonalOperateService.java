package com.jxdinfo.doc.manager.personalcenter.service;

import java.util.List;
import java.util.Map;

public interface PersonalOperateService {
    /**
     * @author luzhazhao
     * @date 2018-11-16
     * @description 获取历史记录的方法
     * @param userId 当前登录用户id
     * @param opType 要查询的记录类型
     * @param startIndex 历史记录的起始下标
     * @param pageSize 每页记录长度
     * @param name 搜索关键字
     * @return 查询到的历史记录列表
     */
    List<Map> getMyHistory(String userId, String opType, int startIndex, int pageSize, String name, String[] typeArr, String order, String levelCode, String orgId);
    List<Map> getMyHistoryMobile(String userId, String opType, int startIndex, int pageSize, String name, String[] typeArr, String order, String levelCode, String orgId,List folderIds);

    /**
     * @author lishilin
     * @date 2018-11-16
     * @description 获取历史记录的方法
     * @param userId 当前登录用户id
     * @param opType 要查询的记录类型
     * @param startIndex 历史记录的起始下标
     * @param pageSize 每页记录长度
     * @param name 搜索关键字
     * @return 查询到的历史记录列表
     */
    List<Map> getMyHistoryClient(String userId, String opType, int startIndex, int pageSize, String name, String[] typeArr, String order, String levelCode, String orgId, String timeType);

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 根据条件获取历史记录总数
     * @param userId 当前登录的用户id
     * @param opType 要查询的记录的类型
     * @param name 要查询记录的关键字
     * @return 查询到的记录条数
     */
    int getMyHistoryCount(String userId, String opType, String name);

    int getMyHistoryCountMobile(String userId, String opType, String name,List folderIds);
    /**
     * @author lishilin
     * @date 2018-11-16
     * @description 根据条件获取历史记录总数
     * @param userId 当前登录的用户id
     * @param opType 要查询的记录的类型
     * @param name 要查询记录的关键字
     * @return 查询到的记录条数
     */
    int getMyHistoryCountClient(String userId, String opType, String name, String timeType);

    /**
     * @author luzhanzhao
     * @description 删除历史记录的服务
     * @date 2018-11-19
     * @param deleteHistory 要删除的历史记录对应的相关文件id
     * @param userId 当前登录的用户id
     * @param opType 操作类型（3：预览；4：下载）
     * @return 删除结果
     */
    int deleteHistory(String[] deleteHistory, String userId, String opType);


    /**
     * @author luzhanzhao
     * @description 清空相关历史记录的服务
     * @date 2018-11-19
     * @param userId 当前登录的用户id
     * @param opType 操作类型（3：预览；4：下载）
     * @return 结果
     */
    int clearHistory(String userId, String opType);

    /**
     * @author luzhanzhao
     * @date 2018-11-16
     * @description 根据文件id查询历史记录的数量
     * @param docId 要查询的文件id
     * @param userId 当前登录的用户id
     * @param opType 查询记录的类型
     * @return 查询到的条数
     */
    int getMyHistoryCountByFileId(String docId, String userId, String opType);

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 取消收藏
     * @param docId 要取消收藏的文档id
     * @param userId 要进行操作的用户id
     * @param opType 操作类型，5对应收藏类型
     * @return 取消收藏的结果
     */
    int cancelCollection(String docId, String userId, String opType);

    /**
     * @author yjs
     * @date 2018-11-19
     * @description 删除收藏记录
     * @param ids 要删除的收藏记录对应的id
     * @return 删除状态
     */
    int deleteCollection(String[] ids);
}
