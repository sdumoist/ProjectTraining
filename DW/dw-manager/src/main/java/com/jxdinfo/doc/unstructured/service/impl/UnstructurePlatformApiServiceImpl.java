package com.jxdinfo.doc.unstructured.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.service.PlatformSystemInfoService;
import com.jxdinfo.doc.unstructured.service.UnstructurePlatformApiService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UnstructurePlatformApiServiceImpl implements UnstructurePlatformApiService {

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;

    /**
     * 非结构化平台系统注册服务
     */
    @Autowired
    private PlatformSystemInfoService unstrucPlatformService;

    /**
     * 目录管理服务类
     */
    @Autowired
    private IFsFolderService fsFolderService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /**
     * 文件服务
     */
    @Autowired
    private FsFileService fsFileService;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ESUtil esUtil;

    /**
     * 获取目录信息
     * folderId 如果folderId参数不为空 则递归查询子级目录
     * 如果folderId参数为空 则全部目录信息
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Map> getFoldList(String folderId) {
        List<Map> folderList = null;
        if (StringUtils.isNotEmpty(folderId)) {
            String ids = fsFolderService.getChildFsFolder(folderId);
            if (StringUtils.isNotEmpty(ids)) {
                String[] childArr = ids.split(",");
                List<String> folderIds = Arrays.asList(childArr);
                folderList = fsFolderService.findFolderTree(folderIds, folderId);
            }
        } else {
            // 根目录
            List<FsFolder> folders = fsFolderService.getRoot();
            FsFolder folder = folders.get(0);
            Map foldMap = new HashMap();
            foldMap.put("id", folder.getFolderId());
            foldMap.put("text", folder.getFolderName());
            foldMap.put("parent", "#");

            folderList = fsFolderService.findFolderTree(null, "");
            folderList.add(foldMap);
        }
        return folderList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void systemRegister(String systemId, String systemName, String systemKey, String userId) {

        PlatformSystemInfo unstrucPlatform = new PlatformSystemInfo();
        unstrucPlatform.setSystemId(systemId);
        unstrucPlatform.setSystemName(systemName);
        unstrucPlatform.setValidFlag("1");
        unstrucPlatform.setSystemKey(systemKey);
        unstrucPlatform.setCreateTime(new Timestamp(new Date().getTime()));
        unstrucPlatformService.save(unstrucPlatform);

        // 创建系统目录
        FsFolder parentFolder = fsFolderService.getRoot().get(0);
        FsFolder fsFolder = new FsFolder();
        fsFolder.setFolderId(systemId);
        fsFolder.setFolderName(systemName);
        fsFolder.setVisibleRange("1");
        fsFolder.setParentFolderId(parentFolder.getFolderId());
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        fsFolder.setCreateTime(ts);
        fsFolder.setUpdateTime(ts);
        fsFolder.setCreateUserId(userId);

        // 生成levelCode
        String parentCode = parentFolder.getLevelCode();
        String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
        fsFolder.setLevelCode(currentCode);
        String localName = "";
        for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
            String levelCodeString = currentCode.substring(0, j * 4);
            String subFolderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
            localName = localName + "\\" + subFolderName;
        }
        localName = localName + "\\" + fsFolder.getFolderName();
        fsFolder.setFolderPath(localName);

        //生成showOrder
        String shorOrder = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
        int num = Integer.parseInt(shorOrder);
        fsFolder.setShowOrder(num);
        //保存目录信息
        fsFolderService.save(fsFolder);

        // 添加操作记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(systemId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(1); // 0:FILE,1:FOLD,2:topic
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(0); // 0：上传,1:编辑,2:删除,3:预览,4:下载,5收藏,6分享,7修改,8重命名
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void folderAdd(String folderId, String folderName, String parentId, String userId) {
        // 目录基本信息
        FsFolder fsFolder = new FsFolder();
        fsFolder.setFolderId(folderId);
        fsFolder.setFolderName(folderName);
        fsFolder.setVisibleRange("1");
        fsFolder.setParentFolderId(parentId);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        fsFolder.setCreateTime(ts);
        fsFolder.setUpdateTime(ts);
        fsFolder.setCreateUserId(userId);

        // 生成levelCode
        FsFolder parentFolder = fsFolderService.getById(parentId);
        String parentCode = parentFolder.getLevelCode();
        String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
        fsFolder.setLevelCode(currentCode);
        String localName = "";
        for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
            String levelCodeString = currentCode.substring(0, j * 4);
            String subFolderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
            localName = localName + "\\" + subFolderName;
        }
        localName = localName + "\\" + fsFolder.getFolderName();
        fsFolder.setFolderPath(localName);

        //生成showOrder
        String shorOrder = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
        int num = Integer.parseInt(shorOrder);
        fsFolder.setShowOrder(num);
        //保存目录信息
        fsFolderService.save(fsFolder);

        // 继承上级目录权限
        List<DocFoldAuthority> authorities = inheritParentFolderAuthority(folderId, parentFolder.getFolderId());
        if (authorities != null && authorities.size() > 0) {
            docFoldAuthorityService.saveBatch(authorities);
        }

        // 添加操作记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(folderId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(1); // 0:FILE,1:FOLD,2:topic
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(0); // 0：上传,1:编辑,2:删除,3:预览,4:下载,5收藏,6分享,7修改,8重命名
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);
    }

    /**
     * 删除目录
     *
     * @param folderIds   删除目录id
     * @param cascadeType 是否级联删除 1：是2：否
     * @param fileDelType 是否删除文件 1：是2：否
     * @param userId    用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void foldersDel(String folderIds, String cascadeType, String fileDelType, String userId) {
        String[] folderIdArr = folderIds.split(",");
        List<String> list = new ArrayList();
        for (int i = 0; i < folderIdArr.length; i++) {
            String folderId = folderIdArr[i];
            FsFolder fsFolder = fsFolderService.getById(folderId);
            if (fsFolder != null) {
                // 获取目录 及目录下的子目录id集合
                String ids = fsFolderService.getChildFsFolder(folderId);
                String[] childArr = ids.split(",");
                list.addAll(Arrays.asList(childArr));

                // 如果是级联删除 并且 删除文件
                if (StringUtils.equals(cascadeType, "1") && StringUtils.equals(fileDelType, "1")) {
                    List<DocInfo> docInfos = docInfoService.list(new QueryWrapper<DocInfo>().in("fold_id", list).eq("valid_flag", "1"));
                    if (docInfos != null && docInfos.size() > 0) {
                        List<String> docIds = docInfos.stream().map(docInfo -> docInfo.getDocId()).collect(Collectors.toList());
                        fsFileService.deleteReally(docIds, userId);

                        for (String id : docIds) {
                            Map map = new HashMap(1);
                            //0为无效，1为有效
                            map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                            esUtil.updateIndex(id, map);
                        }
                    }
                }

                // 如果是级联删除
                if (StringUtils.equals(cascadeType, "1")) {
                    //删除目录
                    fsFolderService.deleteInIds(list);
                    docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().in("folder_id", list));
                } else {
                    fsFolderService.removeById(folderId);
                    docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", list));
                }

                // 操作记录
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                Date date = new Date();
                Timestamp ts = new Timestamp(date.getTime());
                String resId = UUID.randomUUID().toString().replace("-", "");
                DocResourceLog docResourceLog = new DocResourceLog();
                docResourceLog.setId(resId);
                docResourceLog.setResourceId(folderId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(1);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(2);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);
            }
        }
    }

    /**
     * 新增目录(集合)
     *
     * @param folders           目录集合信息
     * @param successFoldList   成功新增的目录集合
     * @param errorFoldIdList   新增失败的目录id集合
     * @param successFoldIdList 新增成功的目录id集合
     * @param errorMsg          新增失败的目录信息
     * @param userId           用户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void foldersAdd(JSONArray folders, JSONArray successFoldList, JSONArray errorFoldIdList, JSONArray successFoldIdList, StringBuilder errorMsg, String userId) {
        for (int i = 0; i < folders.size(); i++) {
            JSONObject folder = folders.getJSONObject(i);
            String folderId = folder.getString("folderId");
            String folderName = folder.getString("folderName");
            String parentId = folder.getString("parentId");

            // 校验参数
            if (StringUtils.isAnyEmpty(folderId, folderName, parentId)) {
                errorFoldIdList.add(folderId);
                errorMsg.append(folderName);
                errorMsg.append("的参数填写不完整");
                errorMsg.append("\r\n");
                continue;
            }

            FsFolder parentFolder = fsFolderService.getById(parentId);
            if (parentFolder == null) {
                errorFoldIdList.add(folderId);
                errorMsg.append(folderName);
                errorMsg.append("的上级目录不存在");
                errorMsg.append("\r\n");
                continue;
            }

            FsFolder fsFolderOld = fsFolderService.getById(folderId);
            if (fsFolderOld != null) {
                errorFoldIdList.add(folderId);
                errorMsg.append(folderName);
                errorMsg.append("目录已存在");
                errorMsg.append("\r\n");
                continue;
            }

            List<FsFolder> fsFolderOlds = fsFolderService.list(new QueryWrapper<FsFolder>().eq("folder_name", folderName).eq("parent_folder_id", parentId));

            if (fsFolderOlds != null && fsFolderOlds.size() > 0) {
                errorFoldIdList.add(folderId);
                errorMsg.append(folderName);
                errorMsg.append("父级目录下存在同名目录");
                errorMsg.append("\r\n");
                continue;
            }


            // 目录id
            if (StringUtils.isEmpty(folderId)) {
                folderId = UUID.randomUUID().toString().replace("-", "");
            }
            // 目录基本信息
            FsFolder fsFolder = new FsFolder();
            fsFolder.setFolderId(folderId);
            fsFolder.setFolderName(folderName);
            fsFolder.setVisibleRange("1");
            fsFolder.setParentFolderId(parentId);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            fsFolder.setCreateUserId(userId);

            // 生成levelCode
            String parentCode = parentFolder.getLevelCode();
            String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
            fsFolder.setLevelCode(currentCode);
            String localName = "";
            for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
                String levelCodeString = currentCode.substring(0, j * 4);
                String subFolderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                localName = localName + "\\" + subFolderName;
            }
            localName = localName + "\\" + fsFolder.getFolderName();
            fsFolder.setFolderPath(localName);

            //生成showOrder
            String shorOrder = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(shorOrder);
            fsFolder.setShowOrder(num);
            //保存目录信息
            fsFolderService.save(fsFolder);

            // 继承上级目录权限 设置权限为查看权限
            List<DocFoldAuthority> authorities = inheritParentFolderAuthority(folderId, parentFolder.getFolderId());
            if (authorities != null && authorities.size() > 0) {
                docFoldAuthorityService.saveBatch(authorities);
            }

            // 添加操作记录
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(folderId);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1); // 0:FILE,1:FOLD,2:topic
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(0); // 0：上传,1:编辑,2:删除,3:预览,4:下载,5收藏,6分享,7修改,8重命名
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);
            JSONObject jsonData = new JSONObject();
            jsonData.put("folderId", folderId);
            jsonData.put("folderName", folderName);
            jsonData.put("parentId", parentId);
            successFoldList.add(jsonData);
            successFoldIdList.add(folderId);
        }
    }

    /**
     * 目录继承上级目录权限
     *
     * @param foldId
     * @param parentFoldId
     */
    private List<DocFoldAuthority> inheritParentFolderAuthority(String foldId, String parentFoldId) {
        // 查询父级目录的权限
        List<DocFoldAuthority> authoritys = docFoldAuthorityService.list(new QueryWrapper<DocFoldAuthority>().eq("folder_id", parentFoldId));
        if (authoritys != null && authoritys.size() > 0) {
            for (int i = 0; i < authoritys.size(); i++) {
                DocFoldAuthority authority = authoritys.get(i);
                authority.setId(UUID.randomUUID().toString().replace("-", ""));
                authority.setFoldId(foldId);
            }
        }
        return authoritys;
    }


}
