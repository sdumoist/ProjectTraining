package com.jxdinfo.doc.manager.doctop.service;

import com.jxdinfo.doc.manager.doctop.model.DocTop;

import java.util.List;
import java.util.Map;

public interface DocTopService {
    List<DocTop> addCheck(List ids);

    void add(DocTop docTop);

    List<Map> topList(String bannerName, Integer beginIndex, Integer limit);

    int topListCount(String bannerName);

    int delTops(List<String> list);

    int moveTop(String table, String idColumn, String idOne, String idTwo);

    boolean updateTop(String oldDocId, String DocId);
}
