package com.jxdinfo.doc.front.topicmanager.service;

import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;

import java.util.List;

/**
 * 类的用途：前台专题展示<p>
 * 创建日期：<br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
public interface FrontTopicService {

    /**
     * 首页专题列表
     *
     * @param startNum 页号
     * @param size     数量
     * @return List<SpecialTopic> 专题集合
     */
    public List<SpecialTopic> getTopicList(int startNum, int size);


    /**
     * 首页专题文档信息
     *
     * @param topicId   专题ID
     * @param orderType 排序
     * @param startNum  页号
     * @param size       数量
     * @return List 文档集合
     */
    public List getDocByTopicId(String topicId, String orderType, int startNum, int size, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList);
    public List getDocByTopicIdMobile(String topicId, String orderType, int startNum, int size, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList,List folderIds);
    public List getDocByTopicIdIndex(String topicId, String orderType, int startNum, int size, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList);

    /**
     * 首页最新专题文档信息
     *
     * @param topicId   专题ID
     * @param orderType 排序
     * @param startNum  页号
     * @param size       数量
     * @return List 文档集合
     */
    public List getNewTopicFileList(String orderType,String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList);
    public List getNewTopicFileListIndex(String orderType,String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList);



    public int getDocByTopicIdCount(String topicId, String userId, List groupList, String levelCode, Integer adminFlag,String deptName,List roleList);

    public int getDocByTopicIdAllCount(String topicId, String orderType, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList);

    /**
     * 首页专题文档信息
     *
     * @param topicId   专题ID
     * @return SpecialTopic 专题的信息（实体类）
     */
    public SpecialTopic getTopicDetailById(String topicId);

    /**
     * 首页专题数量
     *
     * @return int 专题数量
     */
    public int getTopicListCount(String name);
    /**
     * 有效专题数量
     *
     * @return int 专题数量
     */
    int getValidTopicListCount(String name);
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
    public List getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                  List groupList, String userId, Integer adminFlag, String operateType,
                                  String levelCodeString, String levelCode, String isDesc, String orgId,List roleList);

    /**
     *获取下级目录个数
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
    public int getFilesAndFloderCount(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult,
                                  List groupList, String userId, Integer adminFlag, String operateType,
                                  String levelCodeString, String levelCode, String isDesc, String orgId,List roleList);
}

