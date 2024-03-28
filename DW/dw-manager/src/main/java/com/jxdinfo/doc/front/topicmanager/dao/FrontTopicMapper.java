package com.jxdinfo.doc.front.topicmanager.dao;

import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 类的用途：前台专题展示<p>
 * 创建日期：<br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
public interface FrontTopicMapper {

    /**
     * 首页专题列表
     * @param startNum 页号
     * @param size 数量
     * @return List<SpecialTopic> 专题集合
     */
    List<SpecialTopic> getTopicList(@Param("startNum") int startNum, @Param("size") int size);

    /**
     * 首页最新专题文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    List getNewTopicFileList(@Param("orderType") String orderType,
                         @Param("userId") String userId, @Param("groupList") List groupList,
                             @Param("levelCode") String levelCode, @Param("orgId") String orgId,
                             @Param("levelCodeString")String levelCodeString, @Param("roleList") List roleList);
    List getNewTopicFileListIndex(@Param("orderType") String orderType,
                             @Param("userId") String userId, @Param("groupList") List groupList,
                             @Param("levelCode") String levelCode, @Param("orgId") String orgId,
                             @Param("levelCodeString")String levelCodeString
            , @Param("roleList") List roleList);

    /**
     * 首页专题文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    List getDocByTopicId(@Param("topicId") String topicId, @Param("orderType") String orderType,
                         @Param("startNum") int startNum, @Param("size") int size, @Param("userId") String userId,
                         @Param("groupList") List groupList, @Param("levelCode") String levelCode, @Param("orgId") String orgId,@Param("levelCodeString")String levelCodeString
            , @Param("roleList") List roleList);
    List getDocByTopicIdMobile(@Param("topicId") String topicId, @Param("orderType") String orderType,
                         @Param("startNum") int startNum, @Param("size") int size, @Param("userId") String userId,
                         @Param("groupList") List groupList, @Param("levelCode") String levelCode, @Param("orgId") String orgId,@Param("levelCodeString")String levelCodeString
            , @Param("roleList") List roleList,@Param("folderIds") List folderIds);
    List getDocByTopicIdIndex(@Param("topicId") String topicId, @Param("orderType") String orderType,
                         @Param("startNum") int startNum, @Param("size") int size, @Param("userId") String userId,
                         @Param("groupList") List groupList, @Param("levelCode") String levelCode, @Param("orgId") String orgId,@Param("levelCodeString")String levelCodeString
            , @Param("roleList") List roleList);
    int getDocByTopicIdCount(@Param("topicId") String topicId, @Param("userId") String userId,
                             @Param("groupList") List groupList, @Param("levelCode") String levelCode,@Param("deptName") String deptName
            , @Param("roleList") List roleList);

    int getDocByTopicIdAllCount(@Param("topicId") String topicId, @Param("orderType") String orderType, @Param("userId") String userId,
                             @Param("groupList") List groupList, @Param("levelCode") String levelCode, @Param("orgId") String orgId,
                             @Param("levelCodeString")String levelCodeString
            , @Param("roleList") List roleList);
    /**
     * 首页专题超管文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    List getDocByTopicIdBySuperAdminIndex(@Param("topicId") String topicId, @Param("orderType") String orderType,
                                     @Param("startNum") int startNum, @Param("size") int size);

    /**
     * 首页最新专题超管文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    List getDocByTopicIdBySuperAdmin(@Param("topicId") String topicId, @Param("orderType") String orderType,
                                     @Param("startNum") int startNum, @Param("size") int size);

    /**
     * 首页最新专题超管文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    List getNewTopicFileListSuperAdmin(@Param("orderType") String orderType);
    List getNewTopicFileListSuperAdminIndex(@Param("orderType") String orderType);
    int getDocByTopicIdBySuperAdminCount(@Param("topicId") String topicId);

    int getDocByTopicIdBySuperAdminAllCount(@Param("topicId") String topicId, @Param("orderType") String orderType);
    /**
     * 首页专题文档信息
     *
     * @param topicId   专题ID
     * @return SpecialTopic 专题的信息（实体类）
     */
    SpecialTopic getTopicDetailById(@Param("topicId") String topicId);

    /**
     * 首页专题数量
     *
     * @return int 专题数量
     */
    int getTopicListCount(@Param("name") String name);
    /**
     * 有效专题数量
     *
     * @return int 专题数量
     */
    int getValidTopicListCount(@Param("name") String name);

    /**
     * 获取子目录（含文件）
     */
    List getFilesAndFloderBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                       @Param("id") String id, @Param("name") String name,
                                       @Param("orderResult") String orderResult,
                                       @Param("typeArr") String[] typeArr, @Param("isDesc") String isDesc);
    /**
     * 管理员获取子目录
     */
    List getFilesAndFloderByAdmin(@Param("levelCodeString") String levelCodeString, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                  @Param("id") String id, @Param("name") String name,
                                  @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                  @Param("operateType") String operateType,
                                  @Param("levelCode") String levelCode, @Param("isDesc") String isDesc, @Param("orgId") String orgId, @Param("roleList") List roleList);


    /**
     * 获取子目录（含文件）个数
     */
    Integer getFilesAndFloderBySuperAdminCount(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                       @Param("id") String id, @Param("name") String name,
                                       @Param("orderResult") String orderResult,
                                       @Param("typeArr") String[] typeArr, @Param("isDesc") String isDesc);
    /**
     * 管理员获取子目录个数
     */
    Integer getFilesAndFloderByAdminCount(@Param("levelCodeString") String levelCodeString, @Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize,
                                  @Param("id") String id, @Param("name") String name,
                                  @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId, @Param("typeArr") String[] typeArr,
                                  @Param("operateType") String operateType,
                                  @Param("levelCode") String levelCode, @Param("isDesc") String isDesc, @Param("orgId") String orgId,@Param("roleList") List roleList);


}
