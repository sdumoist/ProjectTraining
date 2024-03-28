package com.jxdinfo.doc.front.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文档信息表 服务实现类
 * </p>
 *
 * @author 
 * @since 2018-07-09
 */
@Service
public class FrontDocInfoServiceImpl extends ServiceImpl<DocInfoMapper, DocInfo> implements FrontDocInfoService {

    /**
     * 文档信息DAO层
     */
    @Resource
    private FrontDocInfoMapper frontDocInfoMapper;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Resource
    private ISysUsersService iSysUsersService;
    /**
     * 根据ID查出文件详情
     *
     * @param id    文档主键
     * @return      文档实体类
     */
    @Override
    public DocInfo getDocDetail(String id,String userId,List groupList,String levelCode,List roleList) {
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        return frontDocInfoMapper.getDocDetail(id,userId,groupList,levelCode,orgId,roleList);
    }
    @Override
    public DocInfo getDocDetailMobile(String id,String userId,List groupList,String levelCode,List roleList) {
        String orgId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());
        return frontDocInfoMapper.getDocDetail(id,userId,groupList,levelCode,orgId,roleList);
    }


    /**
     * 根据ID集合查出文件详情
     *
     * @param idList    文档主键集合
     * @return      文档实体类集合
     */
    @Override
    public List<DocInfo> getDocInfo(List idList,String userId,List groupList,String levelCode,String orgId,List roleList) {
        return frontDocInfoMapper.getDocInfo(idList,userId,groupList,levelCode,orgId,roleList);
    }

    /**
     * @Author zoufeng
     * @Description 查询配置文件
     * @Date 14:16 2018/10/12
     * @Param key
     * @return 有效性，value
     **/
    public Map<String,String> getConfigure(String key){
        return frontDocInfoMapper.getConfigure(key);
    }

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
    @Override
    public List<DocInfo> getFolderIMG(Integer page, Integer size, String folderId, String userId, List groupList, String levelCode, Integer adminFlag,String orgId,List roleList) {
        Integer startIndex = (page - 1) * size;
        if (adminFlag == 1){
            return frontDocInfoMapper.getFolderIMGByAdmin(startIndex, size, folderId,userId,groupList,levelCode);
        } else {

            return frontDocInfoMapper.getFolderIMG(startIndex, size, folderId,userId,groupList,levelCode,orgId,roleList);
        }

    }

    @Override
    public List<DocInfo> getFolderImgForShare(int startIndex, int pageSize, String folderId) {
        return frontDocInfoMapper.getFolderImgForShare(startIndex,pageSize,folderId);
    }

    @Override
    public int getFolderImgCount(String folderId, String userId, List groupList, String levelCode, Integer adminFlag,String orgId,List roleList) {
        if (adminFlag == 1){
            return frontDocInfoMapper.getFolderIMGByAdminCount(folderId,userId,groupList,levelCode);
        } else {

            return frontDocInfoMapper.getFolderIMGCount(folderId,userId,groupList,levelCode,orgId,roleList);
        }
    }

    @Override
    public int getFolderImgForShareCount(String folderId) {
        return frontDocInfoMapper.getFolderImgForShareCount(folderId);
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-8
     * @param idList 要查询信息的文档集合
     * @param folderId 文件夹ID，过滤用
     * @return 获取到的文档信息集合
     */
    @Override
    public List<Map> getRecommendIMG(List<String> idList, String folderId) {
        return frontDocInfoMapper.getRecommendIMG(idList,folderId);
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-13
     * @describe 当没有相关图片时，返回热门图片
     * @return 热门图片集
     */
    @Override
    public List<Map> getPopularImg(String folderId) {
        return frontDocInfoMapper.getPopularImg(folderId);
    }

    @Override
    public List<Map> getRecommendArticle(List<String> idList, String currentId) {
        return frontDocInfoMapper.getRecommendArticle(idList, currentId);
    }

    @Override
    public List<Map> guessYouLike(String currentId) {
        return frontDocInfoMapper.guessYouLike(currentId);
    }

    @Override
    public List<Map> getArticleByReadNum(int size, List docList, String currentId) {
        return frontDocInfoMapper.getArticleByReadNum(size, docList, currentId);
    }

    @Override
    public Map getDocByHash(String hash) {
        return frontDocInfoMapper.getDocByHash(hash);
    }

    @Override
    public Map getDocByFileIdApi(String fileId) {
        return frontDocInfoMapper.getDocByFileIdApi(fileId);
    }

    @Override
    public List<DocInfo> getListByTime(String levelCode, String levelCodeString,Integer pageSize,Integer pageNumber ,String sql,String keyword) {
        return frontDocInfoMapper.getListByTime(levelCode,levelCodeString,pageSize,pageNumber,sql,keyword);
    }

    @Override
    public int getListByTimeCount(String levelCode, String levelCodeString,String sql,String keyword) {
        return  frontDocInfoMapper.getListByTimeCount(levelCode,levelCodeString, sql,keyword);
    }

    @Override
    public String getThumbByIdAndLevel(String fileId, String level) {
        return frontDocInfoMapper.getThumbByIdAndLevel(fileId,level);
    }
    @Override
    public List<DocInfo> getListByTimeAll(String levelCode, String levelCodeString,Integer pageSize,
                                          Integer pageNumber ,String sql,String keyword,String[] typeArr) {
        return frontDocInfoMapper.getListByTimeAll(levelCode,levelCodeString,pageSize,pageNumber,sql,keyword,typeArr);
    }

    @Override
    public int getListByTimeCountAll(String levelCode, String levelCodeString,String sql,String keyword,String[] typeArr) {
        return  frontDocInfoMapper.getListByTimeCountAll(levelCode,levelCodeString, sql,keyword,typeArr);
    }

}
