package com.jxdinfo.doc.manager.collectionmanager.service.impl;

import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.collectionmanager.dao.PersonalCollectionMapper;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

/**
 * @ClassName: PersonalConllectionServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/3/9
 * @Version: 1.0
 */

@Service
public class PersonalCollectionServiceImpl implements PersonalCollectionService {

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;
    @Resource
    private PersonalCollectionMapper personalCollectionMapper;

    @Override
    public List<Map> getCollectionList(String userId, int startIndex, int pageSize, String name, String[] typeArr, String order, String levelCode, String orgId, String parentFolderId) {
        return personalCollectionMapper.getCollectionList(userId, startIndex, pageSize, name, typeArr, order, levelCode, orgId, parentFolderId);
    }
    @Override
    public List<Map> getCollectionListMobile(String userId, int startIndex, int pageSize, String name, String[] typeArr, String order, String levelCode, String orgId, String parentFolderId,List folderIds) {
        return personalCollectionMapper.getCollectionListMobile(userId, startIndex, pageSize, name, typeArr, order, levelCode, orgId, parentFolderId,folderIds);
    }

    @Override
    public List<Map> getCollectionToFolderList(String userId, String levelCode, String parentFolderId) {
        return personalCollectionMapper.getCollectionToFolderList(userId, levelCode, parentFolderId);
    }

    @Override
    public void insertCollection(List<DocCollection> DocCollection) {
        personalCollectionMapper.insertCollection(DocCollection);
    }

    @Override
    public void add(DocCollection DocCollection) {
        personalCollectionMapper.insertCollectionFolder(DocCollection);
    }

    @Override
    public int deleteCollection(String[] ids, String fileType, String userId) {
        int count = 0;
        if ("folder".equals(fileType)) {
            for (String s : ids) {
                DocCollection docCollection = personalCollectionMapper.selectByCollectionId(s, userId);
                List<DocCollection> docCollectionList = personalCollectionMapper.selectByLevelCode(docCollection.getLevelCode(), userId);
                String[] idStr = new String[docCollectionList.size()];
                for (int i = 0; i < docCollectionList.size(); i++) {
                    idStr[i] = docCollectionList.get(i).getCollectionId();
                    if ("0".equals(docCollectionList.get(i).getResourceType())) {
                        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                        Timestamp ts = new Timestamp(System.currentTimeMillis());
                        String id = UUID.randomUUID().toString().replace("-", "");
                        DocResourceLog docResourceLog = new DocResourceLog();
                        docResourceLog.setId(id);
                        docResourceLog.setResourceId(docCollectionList.get(i).getResourceId());
                        docResourceLog.setOperateTime(ts);
                        docResourceLog.setResourceType(0);
                        docResourceLog.setUserId(userId);
                        docResourceLog.setOperateType(9);
                        docResourceLog.setValidFlag("1");
                        docResourceLog.setAddressIp(HttpKit.getIp());
                        resInfoList.add(docResourceLog);
                        //将操作记录插入操作记录表中
                        docInfoService.insertResourceLog(resInfoList);
                    }
                }
                count = count + personalCollectionMapper.deleteCollection(idStr);
            }

        } else {
            for (String resourceId : ids) {
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                String id = UUID.randomUUID().toString().replace("-", "");
                DocResourceLog docResourceLog = new DocResourceLog();
                docResourceLog.setId(id);
                docResourceLog.setResourceId(resourceId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(9);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                //将操作记录插入操作记录表中
                docInfoService.insertResourceLog(resInfoList);
            }
            count = count + personalCollectionMapper.deleteCollection(ids);
        }
        return count;
    }

    @Override
    public int cancelCollection(String docId, String userId, String parentFolderId) {
        return personalCollectionMapper.cancelCollection(docId, userId, parentFolderId);
    }

    @Override
    public int getMyCollectionCount(String userId, String parentFolderId, String name) {
        return personalCollectionMapper.getMyCollectionCount(userId, parentFolderId, name);
    }

    @Override
    public int getMyCollectionCountMobile(String userId, String parentFolderId, String name,List folderIds) {
        return personalCollectionMapper.getMyCollectionCountMobile(userId, parentFolderId, name,folderIds);
    }

    @Override
    public List<DocCollection> selectByResourceId(String resourceId, String userId, String parentFolderId) {
        return personalCollectionMapper.selectByResourceId(resourceId, userId, parentFolderId);
    }

    @Override
    public String getCurrentLevelCode(String parentCode, String parentId) {
        if (parentCode != null) {
            String currentMaxCode = getCurrentMaxLevelCode(parentId);
            if (currentMaxCode != null && !"".equals(currentMaxCode)) {
                currentMaxCode = currentMaxCode.substring(currentMaxCode.length() - 3, currentMaxCode.length());
                Integer currCodeInt = Integer.parseInt(currentMaxCode) + 1;
                if (currCodeInt < 10) {
                    return parentCode + "00" + currCodeInt;
                } else if (currCodeInt >= 10 && currCodeInt < 100) {
                    return parentCode + "0" + currCodeInt;
                } else {
                    return parentCode + "" + currCodeInt;
                }

            } else {
                return parentCode + "" + "001";
            }
        }
        return parentCode;
    }

    /**
     * 获取最大的层级码
     *
     * @param parentId 父节点id
     * @return 最大的层级码
     */

    public String getCurrentMaxLevelCode(String parentId) {
        return personalCollectionMapper.getCurrentMaxLevelCode(parentId);
    }

    @Override
    public List<DocCollection> addCheck(String pid, String name, String id, String userId, String fileType) {
        List<DocCollection> list = new ArrayList<>();
        if ("0".equals(fileType)) {
            list = personalCollectionMapper.addCheckFile(pid, name, id, userId);
        } else {
            list = personalCollectionMapper.addCheck(pid, name, id, userId);
        }

        return list;
    }

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    public List getTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        //人员Id
        String userId = ShiroKit.getUser().getId();

        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id)) {
            id = "root";
            List<DocCollection> list = getTreeData(id, userId);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                DocCollection docCollection = list.get(i);
                firstList.add(docCollection.getResourceId());
            }
            //获取第一级
            List<DocCollection> childList = getChildList(firstList, userId);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                DocCollection docCollection = childList.get(i);
                secondList.add(docCollection.getResourceId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountList(secondList, userId);
            List<Map> childResultList = checkChildCount(childList, childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                DocCollection docCollection = list.get(i);
                parentMap.put("id", docCollection.getResourceId());
                parentMap.put("text", docCollection.getResourceName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (docCollection.getResourceId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            //非首次访问， 获取点击节点的下级
            List<DocCollection> list = getTreeData(id, userId);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                DocCollection docCollection = list.get(i);
                firstList.add(docCollection.getResourceId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountList(firstList, userId);
            resultList = checkChildCount(list, childCountList);
        }
        return resultList;
    }

    /**
     * 动态加载文件树
     *
     * @return 目录信息
     */
    public List<DocCollection> getTreeData(String folderId, String userId) {
        List<DocCollection> list = null;
        //获取节点的信息
        list = personalCollectionMapper.getTreeData(folderId, userId);

        return list;
    }

    public List<DocCollection> getChildList(@Param("list") List list,
                                            @Param("UserId") String userId) {
        List<DocCollection> listFsFolder = new ArrayList<>();

        listFsFolder = personalCollectionMapper.getChildList(list, userId);

        return listFsFolder;
    }

    public List<Map> getChildCountList(@Param("list") List list,
                                       @Param("UserId") String userId) {

        List<Map> listMap = null;

        listMap = personalCollectionMapper.getChildCountList(list, userId);

        return listMap;
    }

    public List<Map> checkChildCount(List<DocCollection> list, List<Map> childCountList) {
        String userId = ShiroKit.getUser().getId();
        List<Map> resultList = new ArrayList<>();

        for (int j = 0; j < list.size(); j++) {
            DocCollection docCollectionChild = list.get(j);
            Map childMap = new HashMap();
            childMap.put("id", docCollectionChild.getResourceId());
            childMap.put("text", docCollectionChild.getResourceName());
            childMap.put("pid", docCollectionChild.getParentFolderId());
            for (int i = 0; i < childCountList.size(); i++) {

                Map map = childCountList.get(i);
                if (docCollectionChild.getResourceId().equals(map.get("id"))) {

                    if (Integer.valueOf(map.get("num").toString()) > 0) {
                        childMap.put("children", true);
                    } else {
                        childMap.put("children", false);

                    }

                }

            }

            childMap.put("result", true);
            resultList.add(childMap);
        }
        return resultList;
    }

    @Override
    public int updateFolder(String ids, String parentFolderId, String userId) {
        return personalCollectionMapper.updateFolder(ids, parentFolderId, userId);
    }

    @Override
    public int updateFile(String ids, String parentFolderId, String userId) {
        return personalCollectionMapper.updateFile(ids, parentFolderId, userId);
    }

    @Override
    public void addLevel(String rootId) {
        String userId = ShiroKit.getUser().getId();
        List<DocCollection> docCollections = new ArrayList<>();
        DocCollection root = new DocCollection();
        //如果根节点为空，则取出系统的根节点
        if (rootId == null) {
            root = personalCollectionMapper.selectByResourceId("abcde4a392934742915f89a586989292", userId, null).get(0);
            root.setLevelCode("001");
        } else {
            root = personalCollectionMapper.selectByResourceId(rootId, userId, null).get(0);
        }
        //此ID为根目录ID
        docCollections.add(root);
        //循环遍历根节点
        List<DocCollection> docCollectionChildList = personalCollectionMapper.getChildByParentId(root.getResourceId(), userId);

        docCollections = updateLevelCode(docCollectionChildList, root.getLevelCode());
        //存到数组中执行更新操作
        for (DocCollection d : docCollections) {
            personalCollectionMapper.updateLevelCodeById(d.getResourceId(), d.getLevelCode(), userId);
        }
    }

    @Override
    public String[] getChildFsFolder(String s) {
        String userId = ShiroKit.getUser().getId();
        DocCollection docCollection = new DocCollection();
         docCollection = personalCollectionMapper.selectByCollectionId(s, userId);
        if (docCollection == null) {
             docCollection = personalCollectionMapper.selectByResourceId(s, userId, null).get(0);
        }
        List<DocCollection> docCollectionList = personalCollectionMapper.selectByLevelCode(docCollection.getLevelCode(), userId);
        String[] idStr = new String[docCollectionList.size()];
        for (int i = 0; i < docCollectionList.size(); i++) {
            idStr[i] = docCollectionList.get(i).getResourceId();
        }
        return idStr;
    }

    public List<DocCollection> updateLevelCode(List<DocCollection> docCollectionList, String levelCode) {
        List<DocCollection> docCollections = new ArrayList<>();
        String userId = ShiroKit.getUser().getId();
        if (docCollectionList != null && docCollectionList.size() > 0) {
            for (int i = 0; i < docCollectionList.size(); i++) {
                int newM = i + 1;
                if (newM < 10) {
                    levelCode = levelCode + "00" + newM;
                } else if (newM >= 10 && newM < 100) {
                    levelCode = levelCode + "0" + newM;
                } else {
                    levelCode = levelCode + "" + newM;
                }
                docCollectionList.get(i).setLevelCode(levelCode);
                docCollections.add(docCollectionList.get(i));
                List<DocCollection> docCollectionChildList = personalCollectionMapper.getChildByParentId(docCollectionList.get(i).getResourceId(), userId);
                docCollections.addAll(updateLevelCode(docCollectionChildList, levelCode));
            }
        }
        return docCollections;
    }

    public int getMyCollectionCountByFileId(String resourceId, String userId) {
        return personalCollectionMapper.getMyCollectionCountByFileId(resourceId, userId);
    }

    @Override
    public int updateFolderName(String collectionId, String folderName, String synopsis) {
        return personalCollectionMapper.updateFolderName(collectionId, folderName, synopsis);
    }

    @Override
    public int getChildFileCount(String userId, String parentFolderId) {
        return personalCollectionMapper.getChildFileCount(userId, parentFolderId);

    }
}
