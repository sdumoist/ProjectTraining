package com.jxdinfo.doc.manager.doctop.service.impl;

import com.jxdinfo.doc.manager.doctop.dao.DocTopMapper;
import com.jxdinfo.doc.manager.doctop.model.DocTop;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocTopServiceImpl implements DocTopService {

    @Autowired
    private DocTopMapper docTopMapper;
    @Override
    public List<DocTop> addCheck(List ids) {
        return docTopMapper.addCheck(ids);
    }

    @Override
    public void add(DocTop docTop) {
        docTopMapper.add(docTop);
    }

    @Override
    public List<Map> topList(String bannerName, Integer beginIndex, Integer limit) {
        return docTopMapper.topList(bannerName,beginIndex,limit);
    }

    @Override
    public int topListCount(String bannerName) {
        return docTopMapper.topListCount(bannerName);
    }

    @Override
    public int delTops(List<String> list) {
        return docTopMapper.delTops(list);
    }

    @Override
    public int moveTop(String table, String idColumn, String idOne, String idTwo) {
        return docTopMapper.moveTop(table,idColumn,idOne,idTwo);
    }

    @Override
    public boolean updateTop(String oldDocId, String DocId) {
        if(docTopMapper.updateTop(oldDocId,DocId)>0){
            return true;
        }else {
            return false;
        }

    }
}
