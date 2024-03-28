package com.jxdinfo.doc.front.docmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文档信息表 服务类
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
public interface FrontDocInfoService extends IService<DocInfo> {

    /**
     * 根据ID查出文件详情
     *
     * @param id    文档主键
     * @return      文档实体类
     */
    DocInfo getDocDetail(String id, String userId, List groupList, String levelCode,List roleList);

    /**
     * 根据ID查出文件详情
     *
     * @param id    文档主键
     * @return      文档实体类
     */
    DocInfo getDocDetailMobile(String id, String userId, List groupList, String levelCode,List roleList);
    /**
     * 根据ID集合查出文件详情
     *
     * @param idList    文档主键集合
     * @return      文档实体类集合
     */
    List<DocInfo> getDocInfo(List idList, String userId, List groupList, String levelCode, String orgId,List roleList);


    /**
     * @Author zoufeng
     * @Description 查询配置文件
     * @Date 14:16 2018/10/12
     * @Param key
     * @return 有效性，value
     **/
    public Map<String,String> getConfigure(String key);

    /**
     * @author luzhanzhao
     * @description 查询该文件夹下的图片
     * @date 2018-11-2
     * @param folderId
     * @param userId
     * @param groupList
     * @param levelCode
     * @return 文件夹中要查询的图片的相关信息
     */
    List<DocInfo> getFolderIMG(Integer page, Integer size, String folderId, String userId,
                               List groupList, String levelCode, Integer adminFlag, String orgId,List roleList);

    List<DocInfo> getFolderImgForShare(int startIndex, int pageSize, String folderId);

    int getFolderImgCount(String folderId, String userId, List groupList, String levelCode, Integer adminFlag, String orgId,List roleList);

    int getFolderImgForShareCount(String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-8
     * @param idList 要查询信息的文档集合
     * @param folderId 文件夹ID，过滤用
     * @return 获取到的文档信息集合
     */
    List<Map> getRecommendIMG(List<String> idList, String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-13
     * @describe 当没有相关图片时，返回热门图片
     * @return 热门图片集
     */
    List<Map> getPopularImg(String folderId);

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 拿到ES获取的集合获取文件
     * @param idList 要完善信息的集合
     * @param currentId 当前文档的id
     * @return 推荐文档
     */
    List<Map> getRecommendArticle(List<String> idList, String currentId);

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 猜你喜欢的集合获取
     * @param currentId 当前文档的id
     * @return 猜你喜欢
     */
    List<Map> guessYouLike(String currentId);

    /**
     * @author luzhanzhao
     * @date 2016-11-28
     * @description 根据下载量和预览量获取热门文档
     * @param size 需要获取的条数
     * @param docList 原先查到的文档id集合
     * @param currentId 当前的文档id
     * @return 根据下载量和预览量获取热门文档集合
     */
    List<Map> getArticleByReadNum(int size, List docList, String currentId);

    Map getDocByHash(String hash);

    /**
     * 根据fileId查询文档信息 手机app
     * @param fileId
     * @return
     */
    Map getDocByFileIdApi(String fileId);

    List<DocInfo>  getListByTime(String levelCode, String levelCodeString, Integer pageSize, Integer pageNumber, String sql, String keyword);
   int  getListByTimeCount(String levelCode, String levelCodeString, String sql, String keyword);

    List<DocInfo>  getListByTimeAll(String levelCode, String levelCodeString, Integer pageSize, Integer pageNumber,
                                    String sql, String keyword, String[] typeArr);
    int  getListByTimeCountAll(String levelCode, String levelCodeString, String sql, String keyword, String[] typeArr);

   String getThumbByIdAndLevel(String fileId, String level);
}
