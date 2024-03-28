package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docaudit.dao.FsFolderAuditorMapper;
import com.jxdinfo.doc.manager.docaudit.service.IDocInfoAuditService;
import com.jxdinfo.doc.manager.docaudit.service.IFsFolderAuditorService;
import com.jxdinfo.doc.manager.docbanner.dao.BannerMapper;
import com.jxdinfo.doc.manager.docmanager.dao.DocFileAuthorityMapper;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.doctop.dao.DocTopMapper;
import com.jxdinfo.doc.manager.foldermanager.dao.DocFoldAuthorityMapper;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.topicmanager.dao.TopicDocMapper;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.twelvemonkeys.io.FileUtil;
import dm.jdbc.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * <p>
 * 文件系统-文件 服务实现类
 * </p>
 *
 * @author smallcat
 * @since 2018-06-30
 */
@Service
public class FsFileServiceImpl extends ServiceImpl<FsFileMapper, FsFile> implements FsFileService {

    @Resource
    private DocFileAuthorityMapper docFileAuthorityMapper;

    @Resource
    private DocFoldAuthorityMapper docFoldAuthorityMapper;

    @Resource
    private FsFolderMapper fsFolderMapper;

    @Resource
    private FsFileMapper fsFileMapper;

    @Resource
    private TopicDocMapper topicDocMapper;

    @Resource
    private DocInfoMapper docInfoMapper;

    @Resource
    private BannerMapper bannerMapper;

    @Resource
    private DocTopMapper docTopMapper;
    @Resource
    private DocInfoService docInfoService;

    @Resource
    private IFsFolderService fsFolderService;

    @Resource
    private IFsFolderAuditorService fsFolderAuditorService;

    @Resource
    private IDocInfoAuditService docInfoAuditService;

    @Resource
    private DocConfigService docConfigService;

    @Resource
    private FastdfsService fastdfsService;


    @Autowired
    private ESUtil esUtil;

    @Value("${fileAudit.using}")
    private String using;

    @Value("${fileAudit.auditorRange}")
    private String auditorRange;

    @Resource
    private DocVersionService docVersionService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /**
     * 获取子目录(不含文件)
     */
    public List<FsFile> getChildrenFolder(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                          String orderResult, List groupList, String userId, Integer adminFlag) {

        List<FsFile> list = null;
        if (adminFlag == 1) {
            list = fsFileMapper.getChildrenFolderBySuperAdmin(pageNumber, pageSize, id, typeArr, name, orderResult);

        } else if (adminFlag == 2) {
            list = fsFileMapper.getChildrenFolder(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);
        } else {
            list = fsFileMapper.getChildrenFolder(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);

        }
        return list;
    }

    /**
     * 获取子目录
     */
    public List<FsFile> getChildren(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                    String orderResult, List groupList, String userId, Integer adminFlag) {
        List<FsFile> list = null;
        if (adminFlag == 1) {
            list = fsFileMapper.getChildrenBySuperAdmin(pageNumber, pageSize, id, typeArr, name, orderResult);

        } else if (adminFlag == 2) {
            list = fsFileMapper.getChildren(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);
        } else {
            list = fsFileMapper.getChildren(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);

        }
        return list;
    }

    /**
     * 获取子目录
     */
    public List<FsFile> getChildrenTable(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                         String orderResult, List groupList, String userId, Integer adminFlag) {
        // List lists = PrivilegeUtil.getPrivilegeList();
        List<FsFile> list = null;
        if (adminFlag == 1) {
            list = fsFileMapper.getChildrenTableBySuperAdmin(pageNumber, pageSize, id, typeArr, name, orderResult);

        } else if (adminFlag == 2) {
            list = fsFileMapper.getChildrenTable(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);
        } else {
            list = fsFileMapper.getChildrenTable(pageNumber, pageSize, id, typeArr, name, orderResult, groupList, userId);

        }
        return list;
    }

    /**
     * 获取子目录数量
     */
    public int getNum(String id, String[] typeArr, String name, List groupList, String userId, Integer adminFlag) {
        //List lists = PrivilegeUtil.getPrivilegeList();
        int num = 0;
        if (adminFlag == 1) {
            num = fsFileMapper.getNumBySuperAdmin(id, typeArr, name);

        } else if (adminFlag == 2) {
            num = fsFileMapper.getNum(id, typeArr, name, groupList, userId);
        } else {
            num = fsFileMapper.getNum(id, typeArr, name, groupList, userId);

        }

        return num;
    }

    /**
     * 动态加载文件树
     */
    public List<FsFolder> getTreeDataLazy(String id, Integer adminFlag, List groupList, String userId, String type) {
        List<FsFolder> list = null;
        if (adminFlag == 1) {
            list = fsFileMapper.getTreeDataLazyBySuper(id);
        } else if (adminFlag == 2) {
            list = fsFileMapper.getTreeDataLazy(id, groupList, userId, type);
        } else {
            list = fsFileMapper.getTreeDataLazy(id, groupList, userId, type);
        }
        return list;
    }

    /**
     * 获取根节点
     */
    public List<FsFolder> getRoot() {
        List<FsFolder> list = fsFileMapper.getRoot();
        return list;
    }

    @Override
    public boolean isChildren(String id) {
        int num = fsFileMapper.getNumByChildFloder(id);
        if (num == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 新增重名检测
     */
    public List<FsFile> addCheck(String pid, String name) {
        List<FsFile> list = fsFileMapper.addCheck(pid, name);
        return list;
    }

    public List<FsFolder> getChildList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String UserId, Integer adminFlag, String type) {
        //List lists = PrivilegeUtil.getPrivilegeList();//文档权限列表
        List<FsFolder> listFsFile = null;
        if (adminFlag == 1) {
            listFsFile = fsFileMapper.getChildListForSuperAdmin(list);
        } else {
            listFsFile = fsFileMapper.getChildList(list, groupList, UserId, type);

        }
        return listFsFile;
    }

    public List<Map> getChildCountList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String UserId, Integer adminFlag, String type) {

        List<Map> listMap = null;
        if (adminFlag == 1) {
            listMap = fsFileMapper.getChildCountListForSuperAdmin(list);
        } else {
            listMap = fsFileMapper.getChildCountList(list, groupList, UserId, type);
        }

        return listMap;
    }


    /**
     * 新增重名检测
     */
    public List<FsFile> countFileName(@Param("pid") String pid, @Param("list") List list) {
        List<FsFile> listFsFile = fsFileMapper.countFileName(pid, list);
        return listFsFile;
    }

    /**
     * 删除文件（级联删除）
     */
    public int deleteInIds(List ids) {
        int amount = fsFileMapper.deleteInIds(ids);
        return amount;
    }

    /**
     * 删除权限表
     */
    public int deleteScopeClient(List ids, String userId) {
        int amount = fsFileMapper.deleteScope(ids);
        topicDocMapper.delTopicFile(ids);
        bannerMapper.delBannerFile(ids);
        docTopMapper.delTopsFile(ids);
        for (Object fileId : ids) {
            String docId = fileId.toString();
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docId);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(2);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);//添加分享记录
        }
        fsFileMapper.insertDocRecycle(ids, userId);
        return amount;
    }

    /**
     * 删除权限表
     */
    public int deleteScope(List ids) {
        String userId = ShiroKit.getUser().getId();
        List validIds = new ArrayList();
        for (Object fileId : ids) {
            String docId = fileId.toString();
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docId);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(2);
            docResourceLog.setAddressIp(HttpKit.getIp());
            docResourceLog.setValidFlag("1");
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);//添加分享记录
            if ("true".equals(using)) {
                DocInfo docInfo = docInfoService.getById(docId);
                if (ToolUtil.isNotEmpty(docInfo)) {
                    String validFlag = docInfo.getValidFlag();
                    if (!"3".equals(validFlag)) { // 排除驳回文件
                        validIds.add(fileId);
                    }
                }
            }
        }
        if ("true".equals(using)) {
            // 驳回文件无需放入回收站
            if (ToolUtil.isNotEmpty(validIds)) {
                fsFileMapper.insertDocRecycle(validIds, userId);
            }
        } else {
            fsFileMapper.insertDocRecycle(ids, userId);
        }
        int amount = fsFileMapper.deleteScope(ids);
        topicDocMapper.delTopicFile(ids);
        bannerMapper.delBannerFile(ids);
        docTopMapper.delTopsFile(ids);
        return amount;
    }

    /**
     * 回收权限
     *
     * @param fsFileIds  回收的文件或目录id (多个用,号隔开)
     * @param chooseType 回收权限类型 folder：目录     file:文件
     */
    @Override
    @Transactional
    public void backAuth(String fsFileIds, String chooseType) {
        if (StringUtils.isNotEmpty(fsFileIds)) {
            List<String> ids = Arrays.asList(fsFileIds.split(","));
            List<String> fileIds = new ArrayList<>();
            // 如果是目录 查询目录和子目录  以及目录下的文件id
            if (StringUtils.equals(chooseType, "folder")) {
                // 根据foldIds 获取所有子目录
                List<String> allFoldIds = fsFolderMapper.getChildFolders(ids);
                // 获取目录下的文件id
                if (allFoldIds != null && allFoldIds.size() > 0) {
                    // 删除目录权限
                    docFoldAuthorityMapper.deleteAuthByFoldId(allFoldIds);
                    fileIds = docInfoMapper.getDocIdsByFoldIds(allFoldIds);
                }
            } else {
                fileIds = Arrays.asList(fsFileIds.split(","));
            }

            if (fileIds.size() > 0) {
                // 删除文件权限
                docFileAuthorityMapper.deleteAuthByFileIds(fileIds);
                // 删除文件在es中的权限
                for (String docId : fileIds) {
                    Map<String, Object> docInfo = esUtil.getIndex(docId);
                    docInfo.put("permission", "");
                    esUtil.updateIndex(docId, docInfo);
                }
            }
        }
    }

    /**
     * 删除权限表
     */
    public int deleteScopeYYZC(List ids, String userId) {
        int amount = fsFileMapper.deleteScope(ids);
        topicDocMapper.delTopicFile(ids);
        fsFileMapper.insertDocRecycle(ids, userId);
        return amount;
    }

    /**
     * 删除文件及文件相关信息(真删除)
     *
     * @param ids    文件id
     * @param userId 用户id
     * @return
     */
    public int deleteReally(List ids, String userId) {
        docVersionService.remove(new QueryWrapper<DocVersion>().in("doc_id", ids)); // 删除doc_version表
        int amount = fsFileMapper.deleteScopeReally(ids); // 真删fs_file表
        fsFileMapper.deleteDocInfoReally(ids);  // 真删doc_info表
        fsFileMapper.deleteFileUploadReally(ids);  // 真删fs_file_upload表
        topicDocMapper.delTopicFile(ids); // 真删
        bannerMapper.delBannerFile(ids);// 真删
        docTopMapper.delTopsFile(ids);// 真删
        docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().in("file_id", ids));
        for (Object fileId : ids) {
            String docId = fileId.toString();
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(docId);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(0);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(2);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);//添加删除记录
        }
        return amount;
    }


    /**
     * 获取某个节点下所有子节点ID及本身ID
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String getChildFsFile(String rootId) {
        String ids = fsFileMapper.getChildFsFile(rootId);
        return ids;
    }

    /**
     * 判断目录下是否有文件
     *
     * @param rootId 子节点ID
     * @return 节点ID字符串
     */
    public String checkChildType(String rootId) {
        String ids = fsFileMapper.getChildFsFileType(rootId);
        return ids;
    }

    /**
     * 获取下载次数等信息
     */
    public List<Map> getInfo(List ids,String userId ,List<String> listGroup,String levelCode, String orgId,List roleList) {
        List<Map> list = fsFileMapper.getInfo(ids,userId,listGroup, levelCode, orgId, roleList);
        return list;
    }

    public List<Map> getDocId(String ids) {
        List<Map> list = fsFileMapper.getDocId(ids);
        return list;
    }

    @Override
    public List searchLevel() {
        return fsFileMapper.searchLevel();
    }

    @Override
    public List downloadAble() {
        return fsFileMapper.downloadAble();
    }


    @Override
    public List getFsFolderDetail(String fsFileId) {
        return fsFileMapper.getFsFolderDetail(fsFileId);
    }


    @Override
    public List getFsfileDetail(String fsFileId) {
        return fsFileMapper.getFsfileDetail(fsFileId);
    }

    @Override
    public void updateFileAuthor(String fileId, String authorId, String contactsId) {
        fsFileMapper.updateFileAuthor(fileId, authorId, contactsId);
    }

    @Override
    public List<Map> getPersonList(int pageNumber, int pageSize, String name, String deptId) {
        List<Map> list = fsFileMapper.getPersonList(pageNumber, pageSize, name, deptId);
        return list;
    }

    @Override
    public int getPersonNum(String name, String deptId) {
        int num = fsFileMapper.getPersonNum(name, deptId);
        return num;
    }

    @Override
    public List getAuthority(String fileId) {
        return fsFileMapper.getAuthority(fileId);
    }

    /**
     * 根据MD5获取文件
     *
     * @param md5 md5值
     * @return 节点ID字符串
     */
    @Override
    public List<FsFile> getInfoByMd5(String md5) {
        return fsFileMapper.getInfoByMd5(md5);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public boolean remove(String fileId, String folderId, String userId) {
        DocInfo docInfo = new DocInfo();
        String beforeId = docInfoService.getById(fileId).getFoldId();
        boolean auditState = false;
        if ("true".equals(using)) {
            // 判断是否需要审核
            FsFolder nowFolder = fsFolderService.getById(folderId);
            if (ToolUtil.isNotEmpty(nowFolder)) {
                String nowAuditFlag = nowFolder.getAuditFlag();
                if ("1".equals(nowAuditFlag)) { // 当前目录需要审核
                    auditState = true;
                    FsFolder oldFolder = fsFolderService.getById(beforeId);
                    if (ToolUtil.isNotEmpty(oldFolder)) {
                        String oldAuditFlag = oldFolder.getAuditFlag();
                        // 旧目录下已审核，判断新目录下是否仍需审核
                        if ("1".equals(oldAuditFlag) && "1".equals(auditorRange)) {
                            auditState = false;
                        }
                    }
                }
            }
        }
        if (auditState) {
            // 待审核
            docInfo.setValidFlag("2");
        }
        //更新目录
        docInfo.setFoldId(folderId);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docInfo.setUpdateTime(ts);
        //更新为有效状态
        String[] fileIds = fileId.split(",");
        //删除回收站文件
        //更新文档信息表，更改目录
        docInfoMapper.update(docInfo, new QueryWrapper<DocInfo>().in("file_id", fileIds));
        if (auditState) {
            for (String docId : fileIds) {
                // 更新文档索引
                Map map = new HashMap();
                map.put("recycle", "2");
                esUtil.updateIndex(docId, map);
                // 添加审核信息
                docInfoAuditService.addDocInfoAudit(folderId, docId);
            }
        }
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(fileId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(12);
        docResourceLog.setBeforeId(beforeId);
        docResourceLog.setAfterId(folderId);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        return true;
    }

    @Override
    public List<Map> getDeletedFiles(Page page, Map params) {
        return fsFileMapper.getDeletedFiles(page, params);
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public int delDoc(String[] ids) {
        if (ToolUtil.isEmpty(ids)) {
            return 1;
        }
        // 循环删除
        for (String id : ids) {
            String md5 = fsFileMapper.getMd5(id);
            // 文件秒传 会导致多个文件使用同一个文件链接
            // fileNum ==1 说明只有本文件在使用 文件的链接
            int fileNum = fsFileMapper.getFileNum(md5);

            // 删除图片缩略图
            String imgThumbPath = "";
            List<Map> imgThumbList = docConfigService.select("doc_img_thumb", new String[]{"source_path"}, new HashMap<String, String>() {{
                put("file_id", id);
            }});
            if (ToolUtil.isNotEmpty(imgThumbList)) {
                imgThumbPath = imgThumbList.get(0).get("source_path").toString();
                if (fileNum == 1) {
                    delete(imgThumbPath);
                }
            }
            // 删除视频缩略图
            String videoThumbPath = "";
            List<Map> videoThumbList = docConfigService.select("doc_video_thumb", new String[]{"path"}, new HashMap<String, String>() {{
                put("doc_id", id);
            }});
            if (ToolUtil.isNotEmpty(videoThumbList)) {
                videoThumbPath = videoThumbList.get(0).get("path").toString();
                if (fileNum == 1) {
                    delete(videoThumbPath);
                }
            }
            // 删除源文件
            String sourcePath = "";
            List<Map> sourcePathList = docConfigService.select("fs_file_upload", new String[]{"source_path"}, new HashMap<String, String>() {{
                put("file_id", id);
            }});
            if (ToolUtil.isNotEmpty(sourcePathList)) {
                sourcePath = sourcePathList.get(0).get("source_path").toString();
                delete(sourcePath);
            }
            // 删除文件和转换后的PDF文件
            String localFilePath = "";
            String localFilePdfPath = "";
            FsFile fsFile = getById(id);
            if (ToolUtil.isNotEmpty(fsFile)) {
                localFilePath = fsFile.getFilePath();
                localFilePdfPath = fsFile.getFilePdfPath();
                if (fileNum == 1){
                    delete(localFilePath);
                    delete(localFilePdfPath);
                }

            }

            // 删除表中相关数据
            // doc_img_thumb
            if(ToolUtil.isNotEmpty(imgThumbList)){
                docConfigService.delete("doc_img_thumb",new HashMap<String,String>(){{
                    put("file_id",id);
                }});
            }
            // doc_video_thumb
            if(ToolUtil.isNotEmpty(videoThumbList)){
                docConfigService.delete("doc_video_thumb",new HashMap<String,String>(){{
                    put("doc_id",id);
                }});
            }
            // doc_info
            docInfoService.removeById(id);
            // fs_file
            removeById(id);
            // fs_file_upload
            docConfigService.delete("fs_file_upload",new HashMap<String,String>(){{
                put("file_id",id);
            }});
            // doc_recycle
            docConfigService.delete("doc_recycle",new HashMap<String,String>(){{
                put("file_id",id);
            }});
            // doc_special_topic_files 删除专题
            docConfigService.delete("doc_special_topic_files",new HashMap<String,String>(){{
                put("doc_id",id);
            }});
            // doc_banner_file 删除广告位
            docConfigService.delete("doc_banner_file",new HashMap<String,String>(){{
                put("doc_id",id);
            }});
            // doc_top_file 删除置顶文件
            docConfigService.delete("doc_top_file",new HashMap<String,String>(){{
                put("doc_id",id);
            }});
        }
        return 1;
    }

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    private void delete(String path) {
        if (StringUtil.isEmpty(path)) return;
        try {
            if (path.contains("group")) {
                // 如果是fast文件，调用fast的删除方法
                fastdfsService.removeFile(path);
            } else {
                // 否则，调用删除本地文件方法
                FileUtil.delete(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
