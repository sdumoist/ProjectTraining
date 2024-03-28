package com.jxdinfo.doc.front.topicmanager.service.impl;

import com.jxdinfo.doc.front.topicmanager.dao.FrontTopicMapper;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类的用途：前台专题展示<p>
 * 创建日期：<br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
@Service
public class FrontTopicServiceImpl implements FrontTopicService {

    /** 前台专题mapper */
    @Autowired
    private FrontTopicMapper frontTopicMapper;

    /**
     * 首页专题列表
     *
     * @param startNum 页号
     * @param size     数量
     * @return List<SpecialTopic> 专题集合
     */
    public List<SpecialTopic> getTopicList(int startNum, int size) {
        return frontTopicMapper.getTopicList(startNum, size);
    }

    /**
     * 首页专题文档信息
     *
     * @param topicId 专题ID
     * @param orderType     排序
     * @param startNum 页号
     * @param size     数量
     * @return List 文档集合
     */
    public List getDocByTopicId(String topicId, String orderType, int startNum, int size,String userId,
                                List groupList,String levelCode,Integer adminFlag,String orgId,String levelCodeString,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getDocByTopicId(topicId, orderType, startNum, size,userId,groupList,levelCode,orgId,levelCodeString,roleList);

        }
        return  frontTopicMapper.getDocByTopicIdBySuperAdmin(topicId, orderType, startNum, size);

    }
    public List getDocByTopicIdMobile(String topicId, String orderType, int startNum, int size,String userId,
                                List groupList,String levelCode,Integer adminFlag,String orgId,String levelCodeString,List roleList,List folderIds) {
        if(adminFlag !=1){
            return  frontTopicMapper.getDocByTopicIdMobile(topicId, orderType, startNum, size,userId,groupList,levelCode,orgId,levelCodeString,roleList,folderIds);

        }
        return  frontTopicMapper.getDocByTopicIdBySuperAdmin(topicId, orderType, startNum, size);

    }

    public List getDocByTopicIdIndex(String topicId, String orderType, int startNum, int size,String userId,
                                List groupList,String levelCode,Integer adminFlag,String orgId,String levelCodeString,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getDocByTopicIdIndex(topicId, orderType, startNum, size,userId,groupList,levelCode,orgId,levelCodeString,roleList);

        }
        return  frontTopicMapper.getDocByTopicIdBySuperAdminIndex(topicId, orderType, startNum, size);

    }

    @Override
    public List getNewTopicFileList(String orderType, String userId, List groupList, String levelCode, Integer adminFlag, String orgId, String levelCodeString,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getNewTopicFileList(orderType, userId,groupList,levelCode,orgId,levelCodeString,roleList);

        }
        return  frontTopicMapper.getNewTopicFileListSuperAdmin(orderType);
    }
    @Override
    public List getNewTopicFileListIndex(String orderType, String userId, List groupList, String levelCode, Integer adminFlag, String orgId, String levelCodeString,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getNewTopicFileListIndex(orderType, userId,groupList,levelCode,orgId,levelCodeString,roleList);

        }
        return  frontTopicMapper.getNewTopicFileListSuperAdminIndex(orderType);
    }

    @Override
    public int getDocByTopicIdCount(String topicId, String userId, List groupList, String levelCode, Integer adminFlag,String deptName,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getDocByTopicIdCount(topicId,userId,groupList,levelCode,deptName,roleList);

        }
        return  frontTopicMapper.getDocByTopicIdBySuperAdminCount(topicId);
    }

    @Override
    public int getDocByTopicIdAllCount(String topicId, String orderType, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,String levelCodeString,List roleList) {
        if(adminFlag !=1){
            return  frontTopicMapper.getDocByTopicIdAllCount(topicId, orderType,userId,groupList,levelCode,orgId,levelCodeString,roleList);

        }
        return  frontTopicMapper.getDocByTopicIdBySuperAdminAllCount(topicId, orderType);
    }

    /**
     * 首页专题文档信息
     *
     * @param topicId   专题ID
     * @return SpecialTopic 专题的信息（实体类）
     */
    public SpecialTopic getTopicDetailById(String topicId) {
        return frontTopicMapper.getTopicDetailById(topicId);
    }

    /**
     * 首页专题数量
     *
     * @return int 专题数量
     */
    public int getTopicListCount(String name) {
        return frontTopicMapper.getTopicListCount(name);
    }

    @Override
    public int getValidTopicListCount(String name) {
        return frontTopicMapper.getValidTopicListCount(name);
    }

    @Override
    public List getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag, String operateType, String levelCodeString, String levelCode, String isDesc, String orgId, List roleList) {
        List list = null;
        if (adminFlag == 1) {
            list = frontTopicMapper.getFilesAndFloderBySuperAdmin(pageNumber, pageSize, id, name, orderResult, typeArr, isDesc);
        } else {
            list = frontTopicMapper.getFilesAndFloderByAdmin(levelCodeString, pageNumber, pageSize, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, isDesc, orgId,roleList);
        }
        return list;
    }

    @Override
    public int getFilesAndFloderCount(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag, String operateType, String levelCodeString, String levelCode, String isDesc, String orgId, List roleList) {
        int count = 0;
        if (adminFlag == 1) {
            count = frontTopicMapper.getFilesAndFloderBySuperAdminCount(pageNumber, pageSize, id, name, orderResult, typeArr, isDesc);
        } else {
            count = frontTopicMapper.getFilesAndFloderByAdminCount(levelCodeString, pageNumber, pageSize, id, name, orderResult, groupList, userId,
                    typeArr, operateType, levelCode, isDesc, orgId,roleList);
        }
        return count;
    }
}
