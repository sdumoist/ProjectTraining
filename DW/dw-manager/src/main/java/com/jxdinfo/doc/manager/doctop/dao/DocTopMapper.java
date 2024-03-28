package com.jxdinfo.doc.manager.doctop.dao;

import com.jxdinfo.doc.manager.doctop.model.DocTop;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

public interface DocTopMapper {
    List<DocTop> addCheck(@Param("ids") List ids);

    void add(DocTop docTop);
    int topListCount(@Param("title") String title);
    List<Map> topList(@Param("title") String title, @Param("beginIndex") Integer beginIndex, @Param("limit") Integer limit);

    int delTops(@Param("list") List<String> list);

    int delTopsFile(@Param("list") List<String> list);

    int moveTop(@Param("table") String table, @Param("idColumn") String idColumn, @Param("idOne") String idOne, @Param("idTwo") String idTwo);

    int updateTop(@Param("oldDocId") String oldDocId, @Param("docId") String docId);
}
