package com.jxdinfo.doc.manager.historymanager.service.impl;

import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.historymanager.dao.SearchHistoryMapper;
import com.jxdinfo.doc.manager.historymanager.service.SearchHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
@Service
public class SearchHistoryServiceImpl implements SearchHistoryService {

    @Resource
    private SearchHistoryMapper searchHistoryMapper;
    @Override
    public int insertIntoSearchHistory(Map history) {
        history.put("historyId", StringUtil.getUUID());
        history.put("validFlag", "1");
        return searchHistoryMapper.insertIntoSearchHistory(history);
    }
}
