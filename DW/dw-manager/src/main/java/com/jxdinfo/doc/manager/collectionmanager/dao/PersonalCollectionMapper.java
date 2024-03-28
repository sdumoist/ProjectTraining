package com.jxdinfo.doc.manager.collectionmanager.dao;

import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PersonalCollectionMapper
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/3/9
 * @Version: 1.0
 */
public interface PersonalCollectionMapper {
    List<Map> getCollectionList(@Param("userId") String userId,
                                @Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                                @Param("name") String name, @Param("typeArr") String[] typeArr,
                                @Param("order") String order, @Param("levelCode") String levelCode,
                                @Param("orgId") String orgId,
                                @Param("parentFolderId") String parentFolderId);
    List<Map> getCollectionListMobile(@Param("userId") String userId,
                                @Param("startIndex") int startIndex, @Param("pageSize") int pageSize,
                                @Param("name") String name, @Param("typeArr") String[] typeArr,
                                @Param("order") String order, @Param("levelCode") String levelCode,
                                @Param("orgId") String orgId,
                                @Param("parentFolderId") String parentFolderId,@Param("folderIds") List fodlerIds);

    List<Map> getCollectionToFolderList(@Param("userId") String userId,
                                        @Param("levelCode") String levelCode,
                                        @Param("parentFolderId") String parentFolderId);

    void insertCollection(@Param("list") List<DocCollection> DocCollection);

    void insertCollectionFolder(DocCollection DocCollection);

    int deleteCollection(@Param("ids") String[] ids);

    int cancelCollection(@Param("docId") String docId, @Param("userId") String userId,@Param("parentFolderId") String parentFolderId);

    List<DocCollection>  selectByResourceId(@Param("resourceId") String resourceId,
                                     @Param("userId") String userId,
                                     @Param("parentFolderId") String parentFolderId);

    int getMyCollectionCount(@Param("userId") String userId,
                             @Param("parentFolderId") String parentFolderId,
                             @Param("name") String name);

    int getMyCollectionCountMobile(@Param("userId") String userId,
                             @Param("parentFolderId") String parentFolderId,
                             @Param("name") String name,@Param("folderIds") List folderIds);

    String getCurrentMaxLevelCode(@Param("parentId") String parentId);

    List<DocCollection> addCheck(@Param("pid") String pid, @Param("name") String name,
                                 @Param("folderId") String folderId, @Param("userId") String userId);

    List<DocCollection> addCheckFile(@Param("pid") String pid, @Param("name") String name,
                                     @Param("fileId") String fileId, @Param("userId") String userId);

    List<DocCollection> getTreeData(@Param("id") String id, @Param("userId") String userId);

    List<DocCollection> getChildList(@Param("docCollectionList") List docCollection,
                                     @Param("userId") String userId);

    List<Map> getChildCountList(@Param("list") List list, @Param("userId") String userId);

    int updateFolder(@Param("ids") String ids, @Param("parentFolderId") String parentFolderId,
                     @Param("userId") String userId);

    int updateFile(@Param("ids") String ids, @Param("parentFolderId") String parentFolderId,
                   @Param("userId") String userId);

    List<DocCollection> getChildByParentId(@Param("parentFolderId") String parentFolderId,
                                           @Param("userId") String userId);

    int updateLevelCodeById(@Param("resourceId") String resourceId,
                            @Param("levelCode") String levelCode ,@Param("userId") String userId);

    List<DocCollection> selectByLevelCode(@Param("levelCode") String levelCode,
                                          @Param("userId") String userId);

    DocCollection selectByCollectionId(@Param("collectionId") String collectionId,
                                       @Param("userId") String userId);

    int getMyCollectionCountByFileId(@Param("resourceId") String resourceId,
                                     @Param("userId") String userId);
    int updateFolderName(@Param("collectionId")String collectionId,@Param("folderName")String folderName,@Param("synopsis")String synopsis);

    int getChildFileCount(@Param("userId")String userId,@Param("parentFolderId")String parentFolderId);
}
