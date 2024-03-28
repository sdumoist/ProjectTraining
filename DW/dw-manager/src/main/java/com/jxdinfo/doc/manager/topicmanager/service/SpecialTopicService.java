package com.jxdinfo.doc.manager.topicmanager.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.jxdinfo.doc.manager.topicmanager.dao.SpecialTopicFilesMapper;
import com.jxdinfo.doc.manager.topicmanager.dao.SpecialTopicMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopicFiles;


/**
 * 专题维护
 * @author zhangzhen
 * @date 2018/4/9
 */
@Service
public class SpecialTopicService {
    /**
     * 专题维护
     */
    @Autowired
    private SpecialTopicMapper mapper;
    /**
     * 专题文档
     */
    @Autowired
    private SpecialTopicFilesMapper specialTopicFilesMapper;

    @Autowired
    private SysUsersMapper sysUsersMapper;
    @Autowired
    private SysStruMapper sysStruMapper;

    /**
     * @title: 搜索专题文件详情
     * @description: 搜索专题文件详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords topicId
     * @return:
     */
    public List searchTopicFilesDetail(String[] fileTypeArry, int page, String keyWords, String topicId,String fileType){
        return specialTopicFilesMapper.searchTopicFilesDetail(fileTypeArry,page,keyWords,topicId,fileType);
    }
    /**
     * @title: 统计专题文件详情数量
     * @description: 统计专题文件详情数量
     * @date: 2018-4-13
     * @author: rxy
     * @param: fileType  page  keyWords topicId
     * @return:
     */
    public int countTopicFilesDetail(String[] fileTypeArry, int page, String keyWords, String topicId,String fileType){
        return specialTopicFilesMapper.countTopicFilesDetail(fileTypeArry,page,keyWords,topicId,fileType);
    }
    /**
     * @title: 搜索专题文件详情
     * @description: 搜索专题文件详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords categoryCode
     * @return:
     */
    public SpecialTopic searchTopicDetail(String id){
        return mapper.searchTopicDetail(id);
    }
    /**
     * @title: 计算专题文档数量
     * @description: 计算专题文档数量
     * @date: 2018-4-13.
     * @author: rxy
     * @param: id
     * @return:
     */
    public int countTopicFiles(String id){
        return specialTopicFilesMapper.countTopicFiles(id);
    }
    /**
     * @title: 计算预览专题文档次数
     * @description: 计算预览专题文档次数
     * @date: 2018-4-13
     * @author: rxy
     * @param: id
     * @return:
     */
    public int countPreviewTopicFiles(String id){
        return specialTopicFilesMapper.countPreviewTopicFiles(id);
    }
    /**
     * 查询专题及专题下的文档
     * @return 查询专题及专题下的文档
     * @date 2018-4-11
     */
    public List getSpecialTopicFiles(){
        return specialTopicFilesMapper.getSpecialTopicFiles();
    }
    /**
     * 专题列表查询
     * @param topicName 专题名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    public List<SpecialTopic> topicList(String topicName, int startIndex, int pageSize){
        return mapper.topicList(topicName,startIndex,pageSize);
    }
    /**
     * 在有效期内的专题列表查询
     *
     * @return list
     */
    public List<SpecialTopic> getValidTopicList(String userId,String isValid,String name){
        return mapper.getValidTopicList(userId,isValid,name);
    }
    /**
     * 手机端个性化定制有效期内的专题列表查询
     *
     * @return list
     */
    public List<SpecialTopic> getSpecialTopicList(String userId,String name ){
        return mapper.getSpecialTopicList(userId,name);
    }
    /**
     * 专题列表总数查询
     * @param topicName 专题名称
     * @return 专题总数
     */
    public int getTopicListCount(String topicName){
        return mapper.getTopicListCount(topicName);
    }

    /**
     * 新增专题
     * @param specialTopic 专题对象
     * @param docIds 专题下的文档ID
     * @return 新增数量
     */
    public int addTopic(SpecialTopic specialTopic,String docIds){
        List<SpecialTopicFiles> filesList = new ArrayList<>();
        if(sysUsersMapper.selectById(specialTopic.getAuthorId())==null){

        }else{
            specialTopic.setAuthorId(sysUsersMapper.selectById(specialTopic.getAuthorId()).getUserId());

        }

        if(!StringUtils.isEmpty(docIds)){
            List<String> docIdsList = Arrays.asList(docIds.split(","));
            int i = 0;
            //依次增加showOrder的排序
            for(String id:docIdsList){
                SpecialTopicFiles specialTopicFiles = new SpecialTopicFiles();
                specialTopicFiles.setTopicFileId(UUID.randomUUID().toString());
                specialTopicFiles.setDocId(id);
                specialTopicFiles.setTopicId(specialTopic.getTopicId());
                specialTopicFiles.setShowOrder(i++);
                filesList.add(specialTopicFiles);
            }
            specialTopicFilesMapper.addSpecialTopicFiles(filesList);
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        specialTopic.setCreateTime(ts);
        return mapper.addSpecialTopic(specialTopic);
    }

    /**editTopic
     * 编辑专题
     * @param specialTopic 专题对象
     * @param docIds 专题文档ID
     * @return 更新的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int editTopic(SpecialTopic specialTopic,String docIds){
        if(sysUsersMapper.selectById(specialTopic.getAuthorId())==null){

        }else{
            specialTopic.setAuthorId(sysUsersMapper.selectById(specialTopic.getAuthorId()).getUserId());
        }
        List<SpecialTopicFiles> filesList = new ArrayList<>();
        List<String> topicIdList = new ArrayList<>();
        topicIdList.add(specialTopic.getTopicId());
        if(!StringUtils.isEmpty(docIds)){
            List<String> docIdsList = Arrays.asList(docIds.split(","));
            int i = 0;
            //依次增加showOrder的排序
            for(String id:docIdsList){
                SpecialTopicFiles specialTopicFiles = new SpecialTopicFiles();
                specialTopicFiles.setTopicFileId(UUID.randomUUID().toString());
                specialTopicFiles.setDocId(id);
                specialTopicFiles.setTopicId(specialTopic.getTopicId());
                specialTopicFiles.setShowOrder(i++);
                filesList.add(specialTopicFiles);
            }
            specialTopicFilesMapper.delDoc(topicIdList);
            specialTopicFilesMapper.addSpecialTopicFiles(filesList);
        }

        return mapper.updateSpecialTopic(specialTopic);
    }

    /**
     * 检查专题名称是否存在
     * @param specialTopic 专题对象
     * @return 存在的数量
     */
    public int checkTopicExist(SpecialTopic specialTopic){
        return  mapper.checkTopicExist(specialTopic);
    }

    /**
     * 删除专题下的文档
     * @param list 文档ID
     * @param topicId 专题ID
     * @return 删除的数量
     */
    public int delDocById(List<String> list,String topicId){
        return specialTopicFilesMapper.delDocById(list, topicId);
    }

    /**
     * 批量删除专题
     * @param list 专题ID集合
     * @return 删除的数量
     * @throws Exception 异常
     */
    public int delDocs(List<String> list){
        specialTopicFilesMapper.delDoc(list);
        return mapper.delSpecialTopic(list);
    }

    /**
     * 发布专题
     * @return 发布的数量
     */
    public int publishTopics(List<String> list,Integer topicShow){
        return mapper.publishTopic(list,topicShow);
    }

    public int delDoc(String id, String topicId) {
        return mapper.delDoc(id,topicId);
    }

    public int updateViewNum(String topicId, Integer num) {
        return mapper.updateViewNum(topicId,num) ;
    }

    public int getMaxOrder(){
       return  mapper.getMaxOrder();
    }

    public int moveTopic(String table, String idColumn,String idOne,String idTwo){
        return mapper.moveTopic(table,idColumn,idOne,idTwo);
    };

}
