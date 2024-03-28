package com.jxdinfo.doc.front.docsearch.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.docsearch.dao.DocHistorySearchMapper;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ZhongGuangrui on 2019/1/16.
 * 搜索历史 服务层
 */
@Service
public class DocHistorySearchServiceImpl extends ServiceImpl<DocHistorySearchMapper,DocHistorySearch> implements DocHistorySearchService {

    @Autowired
    private DocHistorySearchMapper docHistorySearchMapper;

    @Override
    public List<Map> selectHotKeywords() {
        return docHistorySearchMapper.selectHotKeywords();
    }

    @Override
    public List<DocHistorySearch> getList(String userId,Integer pageNumber ,Integer pageSize) {
        return docHistorySearchMapper.getList(userId,pageNumber,pageSize);
    }

    @Override
    public Integer updateFlag(String userId,String keyword) {
        return docHistorySearchMapper.updateFlag(userId,keyword);
    }

    /**
     * 搜索联想去重
     * @return
     * @author sjw
     */
    @Override
    public List<String> distinctSuggest(List<String> suggestList) {
        return docHistorySearchMapper.distinctSuggest(suggestList);
    }
}
