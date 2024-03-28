package com.jxdinfo.doc.manager.docbanner.service.impl;

import com.jxdinfo.doc.manager.docbanner.dao.BannerMapper;
import com.jxdinfo.doc.manager.docbanner.model.DocBanner;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerMapper mapper;
    @Override
    public List<DocBanner> bannerList(String bannerName, Integer beginIndex, Integer limit) {
        return mapper.bannerList(bannerName,beginIndex,limit);
    }

    @Override
    public int bannerListCount(String bannerName) {
        return mapper.bannerListCount(bannerName);
    }

    @Override
    public int checkBannerExist(DocBanner docBanner) {
        return mapper.checkBannerExist(docBanner);
    }

    @Override
    public int addBanner(DocBanner docBanner) {

        return mapper.addBanner(docBanner);
    }

    @Override
    public int editBanner(DocBanner docBanner) {
        return  mapper.editBanner(docBanner);
    }

    @Override
    public DocBanner searchBannerDetail(String id) {
        return  mapper.searchBannerDetail(id);
    }

    @Override
    public int getMaxOrder() {
        return mapper.getMaxOrder();
    }

    @Override
    public int delBanners(List<String> list) {
       return  mapper.delBanners(list);
    }

    @Override
    public int moveBanner(String table, String idColumn, String idOne, String idTwo) {
        return mapper.moveBanner(table,idColumn,idOne,idTwo);
    }

    @Override
    public boolean updateBanner(String oldDocId, String docId, String bannerHref) {
        if(mapper.updateBanner(oldDocId,docId,bannerHref)>0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public List<DocBanner> selectBannerById(String docId) {
        return mapper.selectBannerById(docId);
    }
}
