package com.jxdinfo.doc.manager.docmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.DocUpload;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 文档信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
public interface DocInfoMapper extends BaseMapper<DocInfo> {

    List<String> getDocIdsByFoldIds(@Param("allFoldIds") List<String> allFoldIds);

    /**
     * 通过文件id删除文档信息
     * @Title: deleteDocInfoByFileIds 
     * @author: XuXinYing
     * @param ids
     */
    void deleteDocInfoByFileIds(@Param("ids") String ids);

    /**
     * 检查文档是否重复
     * @param docNameList 文档详情
     * @param pid 父文件夹ID
     * @return 重复的文件名
     */
    List<String> checkFileExist(@Param("list") List<String> docNameList, @Param("pid") String pid);
    /**
     * 检查文档是否重复
     * @param docNameList 文档详情
     * @param pid 父文件夹ID
     * @return 重复的文件Id
     */
    List<String> selectExistId(@Param("list") List<String> docNameList, @Param("pid") String pid);

    DocInfo getDocDetail(@Param("ids") String id);

    /**
     * 下载文档记录日志
     * @Title: insertResourceLog
     * @author: XuXinYing
     * @param docdownloadList
     */
    public void insertResourceLog(List<DocResourceLog> docdownloadList);

    /**
     * 更新文档信息表下载次数字段
     * @Title: updateDownloadNum 
     * @author: XuXinYing
     * @param docdownloadList
     */
    public void updateDownloadNum(List<DocResourceLog> docdownloadList);

    /**
     * 更新文档信息表下载次数字段
     * @Title: updateDownloadNum
     * @author: XuXinYing
     * @param docdownload
     */
    public void updateDownloadNumOne(DocResourceLog docdownload);


    /**
     * 上传文档记录日志
     * @Title: insertUploadLogList 
     * @author: XuXinYing
     * @param docUploadList
     */
    public void insertUploadLogList(List<DocUpload> docUploadList);

    /**
     * 上传文档记录日志
     * @Title: insertUploadLog 
     * @author: XuXinYing
     * @param docUpload
     */
    public void insertUploadLog(DocUpload docUpload);

    int updateDocViewNum(@Param("docId") String docId, @Param("num") Integer num);

    List<DocInfo> getDocInfo(@Param("idList") List<String> idList);
    /**
     * 根据docId集合多表查询文档信息
     * @param idList    文档id集合
     * @param order     顺序（0:名称降序；1：名称升序；2：时间升序；3：时间降序）
     * @param name      文件名，用于模糊查询
     * @return          返回文档集合
     * @author          ZhongGuangrui
     */
    List<DocInfo> selectDocInfosByIdList(@Param("idList") List<String> idList, @Param("order") String order, @Param("name") String name);


    int updateDocName(@Param("fsFileId") String fsFileId, @Param("title") String title);

    public void updateOneDataDownloadNum(DocResourceLog docResourceLog);
    
    int updateDocFolder(@Param("fsFileId") String fsFileId, @Param("foldId") String foldId);
    /**
     * 更新文档有效值
     * @param docId     文档主键
     * @param validFlag 有效值
     * @return  更新条数
     * @author  ZhongGuangrui
     */
    int updateValidFlag(@Param("docId") String docId, @Param("validFlag") String validFlag);

    /**
     * 统计文件的数量
     */
    public int getCount(@Param("foldId") String foldId);

    /**
     * 获取子目录
     */
    List<DocInfo> getChildren(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("typeArr") String[] typeArr, @Param("name") String name, @Param("orderResult") String orderResult
            , @Param("groupList") List groupList, @Param("userId") String userId);
    /**
     * 获取超级管理员子目录(不含文件夹)
     */
    List<DocInfo> getChildrenBySuperAdmin(@Param("pageNumber") int pageNumber, @Param("pageSize") int pageSize, @Param("id") String id
            , @Param("typeArr") String[] typeArr, @Param("name") String name, @Param("orderResult") String orderResult);

    public Integer getDocReadNum(@Param("docId") String docId);
    
    public Integer getTopicReadNum(@Param("topicId") String docId);

    public  List<DocInfo> getListByFolderId(@Param("folderLevelCode") String folderLevelCode, @Param("length")Integer length);

    List<FsFolderView> getChangeFile(@Param("folderLevelCode") String folderLevelCode, @Param("length") Integer length, @Param("startTime") String startTime);

    /**
     * 检查目录下是否存在重名待审核文件
     * @param docNameList 文档名称集合
     * @return 重复的文件名
     */
    List<String> checkAuditFileExist(@Param("list") List<String> docNameList, @Param("pid") String pid);

    /**
     * 查询目录下重名待审核文件主键
     * @param docNameList 文档详情
     * @param pid 父文件夹ID
     * @return 重复的文件Id
     */
    List<String> selectAuditExistId(@Param("list") List<String> docNameList, @Param("pid") String pid);

}
