package com.jxdinfo.doc.manager.collectionmanager.service;

import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

import java.util.List;
import java.util.Map;

public interface PersonalCollectionService {
    List<Map> getCollectionList(String userId, int startIndex,int pageSize, String name,
                                String[] typeArr,String order, String levelCode,String orgId,
                                String parentFolderId);

    List<Map> getCollectionListMobile(String userId, int startIndex,int pageSize, String name,
                                String[] typeArr,String order, String levelCode,String orgId,
                                String parentFolderId,List folderIds);

    List<Map> getCollectionToFolderList(String userId, String levelCode,String parentFolderId);

    void insertCollection(List<DocCollection> DocCollection);

    void add(DocCollection DocCollection);

    int deleteCollection(String[] ids,String fileType,String userId);

    int cancelCollection(String docId, String userId,String parentFolderId);

    int getMyCollectionCount(String userId, String parentFolderId,String name);

    int getMyCollectionCountMobile(String userId, String parentFolderId,String name,List folderIds);

    List<DocCollection> selectByResourceId(String resourceId,String userId,String parentFolderId);

    String getCurrentLevelCode(String parentCode, String parentId);

    String getCurrentMaxLevelCode(String parentId);

    List<DocCollection> addCheck(String pid, String name, String folderId,String userId,String resourceType);

    List getTreeDataLazy(String id, String type);

    List<DocCollection> getTreeData(String folderId,String userId);

    List<Map> checkChildCount(List<DocCollection> list, List<Map> childCountList);

    int updateFolder(String ids ,String parentFolderId,String userId);

    int updateFile(String ids,String parentFolderId,String userId);

    void addLevel(String rootId);

    String[] getChildFsFolder(String s);

    int getMyCollectionCountByFileId(String resourceId,String userId);

    int updateFolderName(String collectionId,String folderName,String synopsis);

    int getChildFileCount(String userId,String parentFolderId);
}

