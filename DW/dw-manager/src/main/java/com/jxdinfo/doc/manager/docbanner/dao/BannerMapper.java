package com.jxdinfo.doc.manager.docbanner.dao;

import com.jxdinfo.doc.manager.docbanner.model.DocBanner;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * banner信息维护
 * @author yjs
 * @date 2018/4/9
 */
public interface BannerMapper {
    /**
     * banner列表查询
     * @param bannerName banner名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<DocBanner> bannerList(@Param("bannerName") String bannerName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);
   int  bannerListCount(@Param("bannerName") String bannerName);

    int checkBannerExist(DocBanner docBanner);

    /**
     * 新增banner
     * @param docBanner
     * @return 新增数量
     */
    int addBanner(DocBanner docBanner);

    /**
     * 修改banner
     * @param docBanner
     * @return 新增数量
     */
    int editBanner(DocBanner docBanner);

    int getMaxOrder();


    /**
     * @title: 搜索banner详情
     * @description: 搜索banner详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords categoryCode
     * @return:
     */
    DocBanner searchBannerDetail(String id);

    /**
     * 删除专题
     * @param list id集合
     * @return 删除的数量
     */
    int delBanners(@Param("list") List<String> list);

    int moveBanner(@Param("table") String table, @Param("idColumn") String idColumn, @Param("idOne") String idOne, @Param("idTwo") String idTwo);

    int delBannerFile(@Param("list") List<String> list);

    int updateBanner(@Param("oldDocId") String oldDocId, @Param("docId") String docId, @Param("bannerHref") String bannerHref);

    List<DocBanner> selectBannerById(@Param("docId") String docId);
}
