package com.jxdinfo.doc.manager.historymanager.service.impl;

import com.jxdinfo.doc.manager.historymanager.dao.RelationHistoryMapper;
import com.jxdinfo.doc.manager.historymanager.service.RelationHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RelationHistoryServiceImpl implements RelationHistoryService {
    @Resource
    private RelationHistoryMapper relationHistoryMapper;

    @Override
    public List<Map> getUsers() {
        return relationHistoryMapper.getUsers();
    }

    @Override
    public List<Map> getUserResourceLog(String userId, String isEmpty) {
        return relationHistoryMapper.getUserResourceLog(userId, isEmpty);
    }

    @Override
    public int selectCount(Map map) {
        return relationHistoryMapper.selectCount(map);
    }

    @Override
    public int insertIntoDocRelation(Map map) {
        return relationHistoryMapper.insertIntoDocRelation(map);
    }

    @Override
    public int updateDocRelation(Map map) {
        return relationHistoryMapper.updateDocRelation(map);
    }

    @Override
    public int selectRelationCount() {
        return relationHistoryMapper.selectRelationCount();
    }
}
