package com.jxdinfo.doc.front.docmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文档信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */

public interface FrontDocInfoMapper extends BaseMapper<DocInfo> {

    DocInfo getDocDetail(@Param("ids") String id, @Param("userId") String userId, @Param("groupList") List groupList,
                         @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("roleList") List roleList);

    /**
     * 根据ID集合查出文件详情
     *
     * @param idList    文档主键集合
     * @return      文档实体类集合
     */
    List<DocInfo> getDocInfo(@Param("idList") List<String> idList, @Param("userId") String userId, @Param("groupList") List groupList,
                             @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("roleList") List roleList);
    
    /**
     * @Author zoufeng
     * @Description 查询配置文件
     * @Date 14:16 2018/10/12
     * @Param key
     * @return 有效性，value
     **/
    public Map<String,String> getConfigure(@Param("key") String key);

    /**
     * @Author luzhanzhao
     * @Description 查询该文件夹下的图片
     * @Date 2018-11-2
     * @param folderId
     * @param userId
     * @param groupList
     * @param levelCode
     * @return 文件夹中要查询的图片的相关信息
     */
    List<DocInfo> getFolderIMG(@Param("startIndex") Integer startIndex, @Param("size") Integer size,
                               @Param("folderId") String folderId, @Param("userId") String userId,
                               @Param("groupList") List groupList, @Param("levelCode") String levelCode, @Param("orgId") String orgId, @Param("roleList") List roleList);

    List<DocInfo> getFolderIMGByAdmin(@Param("startIndex") Integer startIndex, @Param("size") Integer size,
                                      @Param("folderId") String folderId, @Param("userId") String userId,
                                      @Param("groupList") List groupList, @Param("levelCode") String levelCode);

    List<DocInfo> getFolderImgForShare(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize, @Param("folderId") String folderId);

    int getFolderImgForShareCount(@Param("folderId") String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-8
     * @param idList 要查询信息的文档集合
     * @param folderId 文件夹ID，过滤用
     * @return 获取到的文档信息集合
     */
    List<Map> getRecommendIMG(@Param("idList") List<String> idList, @Param("folderId") String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-13
     * @describe 当没有相关图片时，返回热门图片
     * @return 热门图片集
     */
    List<Map> getPopularImg(@Param("folderId") String folderId);

    List<DocInfo>  getList(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize);

    List<DocInfo>  getListByAdmin(@Param("pageSize") Integer pageSize);

    List<DocInfo>  getNewList(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize, @Param("groupList") List groupList,
                           @Param("userId") String userId, @Param("orgId") String orgId, @Param("levelCodeString") String levelCodeString ,@Param("roleList") List roleList);

    List<DocInfo>  getListByFolderId(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize, @Param("folderId") String folderId);

    List<DocInfo>  getTopList(@Param("groupList") List groupList,
                              @Param("userId") String userId, @Param("orgId") String orgId, @Param("levelCodeString") String levelCodeString ,@Param("roleList") List roleList);

    List<DocInfo>  getTopListByAdmin();


    List<DocInfo>  getListByTime(@Param("levelCode") String levelCode, @Param("levelCodeString") String levelCodeString,
                                 @Param("pageSize") Integer pageSize, @Param("pageNumber") Integer pageNumber, @Param("sql") String sql, @Param("keyword") String keyword);
int   getListByTimeCount(@Param("levelCode") String levelCode, @Param("levelCodeString") String levelCodeString, @Param("sql") String sql, @Param("keyword") String keyword);

    List<DocInfo>  getListByTimeAll(@Param("levelCode") String levelCode, @Param("levelCodeString") String levelCodeString,
                                    @Param("pageSize") Integer pageSize, @Param("pageNumber") Integer pageNumber,
                                    @Param("sql") String sql, @Param("keyword") String keyword, @Param("typeArr") String[] typeArr);
    int   getListByTimeCountAll(@Param("levelCode") String levelCode, @Param("levelCodeString") String levelCodeString,
                                @Param("sql") String sql, @Param("keyword") String keyword, @Param("typeArr") String[] typeArr);


    List<FsFolderView>  getListByType(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize, @Param("folderId") String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 拿到ES获取的集合获取文件
     * @param idList 要完善信息的集合
     * @param currentId 当前文档的id
     * @return 推荐文档
     */
    List<Map> getRecommendArticle(@Param("idList") List<String> idList, @Param("currentId") String currentId);

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 猜你喜欢的集合获取
     * @param currentId 当前文档的id
     * @return 猜你喜欢
     */
    List<Map> guessYouLike(@Param("currentId") String currentId);

    /**
     * @author luzhanzhao
     * @date 2016-11-28
     * @description 根据下载量和预览量获取热门文档
     * @param size 需要获取的条数
     * @param docList 原先查到的文档id集合
     * @param currentId 当前的文档id
     * @return 根据下载量和预览量获取热门文档集合
     */
    List<Map> getArticleByReadNum(@Param("size") int size, @Param("docList") List docList, @Param("currentId") String currentId);

    Map getDocByHash(String hash);
    /**
     * 根据fileId查询文档信息 手机app
     * @param fileId
     * @return
     */
    Map getDocByFileIdApi(@Param("fileId") String fileId);

    int getFolderIMGByAdminCount(@Param("folderId") String folderId, @Param("userId") String userId,
                                 @Param("groupList") List groupList, @Param("levelCode") String levelCode);

    int getFolderIMGCount(@Param("folderId") String folderId, @Param("userId") String userId,
                          @Param("groupList") List groupList, @Param("levelCode") String levelCode,
                          @Param("orgId") String orgId, @Param("roleList") List roleList);

    String getPdfPathById(@Param("docId") String docId);

     int updatePdfPathByMd5(@Param("docId") String docId);

     List<Map> createThumbList();

    void setNewThumbInfo(Map map);

    String getThumbByIdAndLevel(@Param("fileId") String fileId, @Param("sourceLevel") String level);

    void updateNewThumbInfo(Map map);

    void insertThumbInfoFast(@Param("sourceId") String sourceId, @Param("fileId") String fileId, @Param("md5") String md5);

    FsFile selectDocId(@Param("docId") String docId);

    List<String>   selectPdfPath(@Param("md5") String md5, @Param("docId") String docId);

    List<String>  selectKey(@Param("md5") String md5, @Param("docId") String docId);

    void updatePdfPath(@Param("pdfPath") String pdfPath, @Param("pdfKey") String pdfKey, @Param("docId") String docId);

    List<DocInfo>  getListByPermissionSuper(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize);

    List<DocInfo>  getListByPermission(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize,
                                @Param("levelCode") String levelCode,@Param("levelCodeString") String levelCodeString,
                                @Param("userId")String userId, @Param("groupList") List groupList,
                                @Param("orgId") String orgId, @Param("roleList") List roleList);

    List<Map> hotWord(@Param("beginNum")Integer beginNum,@Param("endNum")Integer endNum);
    List<Map> hotWordMobile(Page page,@Param("list") List<String> folderIds);

    int  hotWordNum();
    List<Map> hotWordByLevelCode( @Param("beginNum")Integer beginNum,@Param("endNum")Integer endNum, @Param("levelCode") String levelCode);
    Integer hotWordCount();
}
