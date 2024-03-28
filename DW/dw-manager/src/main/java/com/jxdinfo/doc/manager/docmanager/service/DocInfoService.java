package com.jxdinfo.doc.manager.docmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.DocUpload;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;

import java.util.List;

/**
 * <p>
 * 文档信息表 服务类
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
public interface DocInfoService extends IService<DocInfo> {

    /**
     * 根据文件id删除文档信息
     * @Title: deleteDocInfoByFileIds 
     * @author: XuXinYing
     * @param idlist
     * @return
     */
    void deleteDocInfoByFileIds(List<String> idlist);

    /**
     * 检查是否重复提交
     * @param docNameList 文档名称集合
     * @return 重复的文件名
     */
    public List<String> checkFileExist(List<String> docNameList, String pid);
    /**
     * 检查是否重复提交
     * @param docNameList 文档名称集合
     * @return 重复的文件id
     */
    List<String> selectExistId(List<String> docNameList, String pid);

    DocInfo getDocDetail(String id);

    /**
     * 下载文档记录日志
     * @Title: insertResourceLog
     * @author: XuXinYing
     * @param docdownloadList
     */
    void insertResourceLog(List<DocResourceLog> docdownloadList);

    /** 
     * 更新文档信息表下载次数
     * @Title: updateDownloadNum 
     * @author: XuXinYing
     * @param docDownloadInfoList
     */
    void updateDownloadNum(List<DocResourceLog> docDownloadInfoList);

    /**
     * 更新文档信息表下载次数
     * @Title: updateDownloadNum
     * @author: XuXinYing
     * @param docDownloadInfo
     */
    void updateDownloadNumOne(DocResourceLog docDownloadInfo);

    /**
     * 上传文档记录日志
     * @Title: insertUploadLog 
     * @author: XuXinYing
     * @param docUpload
     */
    void insertUploadLog(DocUpload docUpload);

    /**
     * 上传文档记录日志
     * @Title: insertUploadLog 
     * @author: XuXinYing
     * @param docUploadList
     */
    void insertUploadLogList(List<DocUpload> docUploadList);

    int updateDocViewNum(String docId, Integer num);

    List<DocInfo> getDocInfo(List idList);

    /**
     * 根据docId集合多表查询文档信息
     * @param idList    文档id集合
     * @param order     顺序（0:名称降序；1：名称升序；2：时间升序；3：时间降序）
     * @param name      文件名，用于模糊查询
     * @return          返回文档集合
     * @author          ZhongGuangrui
     */
    List<DocInfo> selectDocInfosByIdList(List idList, String order, String name);

    int updateDocName(String fsFileId, String title);

    /**
     * 更新文档信息表下载次数
     * @Title: updateDownloadNum
     * @author: XuXinYing
     */
    void updateOneDataDownloadNum(DocResourceLog docResourceLog);

    /**
     * 更新文档有效值
     * @param docId     文档主键
     * @param validFlag 有效值
     * @return  更新条数
     * @author  ZhongGuangrui
     */
    int updateValidFlag(String docId, String validFlag);

    /**
     * 统计文件的数量
     */
    public int getCount(String foldId);

    int updateDocFolder(String fsFileId, String foldId);
    public List<DocInfo> getDocList(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                    String orderResult, List groupList, String userId, Integer adminFlag);
    public Integer getDocReadNum(String docId);
    public Integer getTopicReadNum(String topicId);
    public List<DocInfo>  getListByFolderId(String folderLevelCode,Integer length);


    List<FsFolderView> getChangeFile(String folderLevelCode, Integer length, String startTime);

    /**
     * 检查目录下是否存在重名待审核文件
     * @param docNameList 文档名称集合
     * @return 重复的文件名
     */
    List<String> checkAuditFileExist(List<String> docNameList, String pid);

    /**
     * 查询目录下重名待审核文件主键
     * @param docNameList 文档名称集合
     * @return 重复的文件id
     */
    List<String> selectAuditExistId(List<String> docNameList, String pid);
}
