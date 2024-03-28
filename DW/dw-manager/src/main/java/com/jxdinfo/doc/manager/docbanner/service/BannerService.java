package com.jxdinfo.doc.manager.docbanner.service;

import com.jxdinfo.doc.manager.docbanner.model.DocBanner;

import java.util.List;

/**
 * banner维护
 * @author yjs
 * @date 2018/4/9
 */
public interface BannerService {
    /**
     * banner列表
     */
  List<DocBanner> bannerList(String bannerName, Integer beginIndex, Integer limit);

   int  bannerListCount(String bannerName);

    int checkBannerExist(DocBanner docBanner);

    int addBanner(DocBanner docBanner);
    int editBanner(DocBanner docBanner);

    DocBanner searchBannerDetail(String id);
    int getMaxOrder();

    int delBanners(List<String> list);

    int moveBanner(String table, String idColumn, String idOne, String idTwo);

    boolean updateBanner(String oldDocId, String docId, String bannerHref);

    List<DocBanner> selectBannerById(String docId);
}
