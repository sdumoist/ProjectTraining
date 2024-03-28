package com.jxdinfo.doc.manager.historymanager.dao;

import java.util.Map;

public interface SearchHistoryMapper {
    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 将用户的搜索记录插入搜索记录表中
     * @param history
     * @return
     */
    int insertIntoSearchHistory(Map history);
}
