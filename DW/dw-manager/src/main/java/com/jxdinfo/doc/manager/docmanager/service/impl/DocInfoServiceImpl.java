package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.util.IdUtil;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.DocUpload;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 文档信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
@Service
public class DocInfoServiceImpl extends ServiceImpl<DocInfoMapper, DocInfo> implements DocInfoService {

    /**
     * 文档信息DAO层
     */
    @Resource
    private DocInfoMapper docInfoMapper;

    /**
     * 根据文件id删除文档信息
     * @Title: deleteDocInfoByFileIds 
     * @author: XuXinYing
     * @param idlist
     * @return
     */
    @Override
    public void deleteDocInfoByFileIds(List<String> idlist) {
        String ids = IdUtil.idListConverts(idlist);
        docInfoMapper.deleteDocInfoByFileIds(ids);
    };

    /**
     * 检查是否重复提交
     * @param docNameList 文档名称集合
     * @return 重复的文件名
     */
    @Override
    public List<String> checkFileExist(List<String> docNameList, String pid) {
        return docInfoMapper.checkFileExist(docNameList, pid);
    }

    @Override
    public List<String> selectExistId(List<String> docNameList, String pid) {
        return docInfoMapper.selectExistId(docNameList, pid);
    }

    @Override
    public DocInfo getDocDetail(String id) {
        return docInfoMapper.getDocDetail(id);
    }

    /**
     * 下载文档记录日志
     * @Title: insertResourceLog
     * @author: XuXinYing
     * @param docdownloadList
     */
    @Override
    public void insertResourceLog(List<DocResourceLog> docdownloadList) {
        docInfoMapper.insertResourceLog(docdownloadList);
    }

    /** 
     * 更新文档信息表下载次数
     * @Title: updateDownloadNum 
     * @author: XuXinYing
     * @param docDownloadInfoList
     */
    @Override
    public void updateDownloadNum(List<DocResourceLog> docDownloadInfoList) {
        for (int i = 0; i < docDownloadInfoList.size(); i++) {
            docInfoMapper.updateDownloadNumOne(docDownloadInfoList.get(i));
        }
    }

    /**
     * 更新文档信息表下载次数
     * @Title: updateDownloadNum
     * @author: XuXinYing
     * @param docDownloadInfo
     */
    @Override
    public void updateDownloadNumOne(DocResourceLog docDownloadInfo) {
        docInfoMapper.updateDownloadNumOne(docDownloadInfo);
    }

    /**
     * 上传文档记录日志
     * @Title: insertUploadLog 
     * @author: XuXinYing
     * @param docUpload
     */
    @Override
    public void insertUploadLog(DocUpload docUpload) {
        docInfoMapper.insertUploadLog(docUpload);
    }

    /**
     * 上传文档记录日志
     * @Title: insertUploadLog 
     * @author: XuXinYing
     * @param docUploadList
     */
    @Override
    public void insertUploadLogList(List<DocUpload> docUploadList) {
        docInfoMapper.insertUploadLogList(docUploadList);
    }

    @Override
    public int updateDocViewNum(@Param("docId")String docId, @Param("num")Integer num) {
        return docInfoMapper.updateDocViewNum(docId,num);
    }
    @Override
    public int updateDocName(@Param("fsFileId") String fsFileId,@Param("title") String title){
        return docInfoMapper.updateDocName(fsFileId,title);
    }

    @Override
    public void updateOneDataDownloadNum(DocResourceLog docResourceLog){
        docInfoMapper.updateOneDataDownloadNum(docResourceLog);
    }

    @Override
    public int updateValidFlag(String docId, String validFlag) {
        return docInfoMapper.updateValidFlag(docId,validFlag);
    }

    @Override
    public int updateDocFolder(@Param("fsFileId") String fsFileId,@Param("foldId") String foldId){
        return docInfoMapper.updateDocFolder(fsFileId,foldId);
    }
    
    /**
     * 统计文件的数量
     */
    @Override
    public int getCount(String foldId) {
        return docInfoMapper.getCount(foldId);
    }

    @Override
    public List<DocInfo> getDocList(int pageNumber, int pageSize, String id, String[] typeArr, String name, String orderResult, List groupList, String userId, Integer adminFlag) {
        List<DocInfo> list = null;
        if (adminFlag == 1) {
            list =  docInfoMapper.getChildrenBySuperAdmin(pageNumber, pageSize, id,typeArr, name, orderResult);
        } else if (adminFlag == 2) {
            list = docInfoMapper.getChildren(pageNumber, pageSize, id,typeArr, name, orderResult, groupList, userId);
        } else {
            list = docInfoMapper.getChildren(pageNumber, pageSize, id,typeArr, name, orderResult, groupList, userId);
        }
        return list;
    }

    @Override
    public List<DocInfo> getDocInfo(List idList) {
        return docInfoMapper.getDocInfo(idList);
    }

    @Override
    public List<DocInfo> selectDocInfosByIdList(List idList, String order, String name) {
        return docInfoMapper.selectDocInfosByIdList(idList,order,name);
    }

    /**
     *获取文档预览数 
     */
    @Override
    public Integer getDocReadNum(String docId) {
        return docInfoMapper.getDocReadNum(docId);
    }
    
    /**
     *获取专题预览数 
     */
    @Override
    public Integer getTopicReadNum(String topicId) {
    	return docInfoMapper.getTopicReadNum(topicId);
}

    @Override
    public List<DocInfo> getListByFolderId(String folderLevelCode,Integer length) {
        return docInfoMapper.getListByFolderId(folderLevelCode,length);
    }

    @Override
    public List<FsFolderView> getChangeFile(String folderLevelCode, Integer length, String startTime) {
        return docInfoMapper.getChangeFile(folderLevelCode,length,startTime);
    }

    /**
     * 检查目录下是否存在重名待审核文件
     *
     * @param docNameList 文档名称集合
     * @param pid
     * @return 重复的文件名
     */
    @Override
    public List<String> checkAuditFileExist(List<String> docNameList, String pid) {
        return docInfoMapper.checkAuditFileExist(docNameList, pid);
    }

    /**
     * 查询目录下重名待审核文件主键
     *
     * @param docNameList 文档名称集合
     * @param pid
     * @return 重复的文件id
     */
    @Override
    public List<String> selectAuditExistId(List<String> docNameList, String pid) {
        return docInfoMapper.selectAuditExistId(docNameList, pid);
    }
}
