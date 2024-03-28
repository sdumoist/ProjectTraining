package com.jxdinfo.doc.newupload.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UploadMapper {
    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @description 往数据库中插入文件上传状态
     * @param map 文件上传状态
     */
    void newUploadState(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-1-9
     * @description 获取数据库中的文件上传状态列表
     * @return 文件上传状态列表
     */
    List<Map<String,String>> getUploadState();

    /**
     * @author luzhanzhao
     * @date 2018-1-9
     * @description 更新文件上传状态
     * @param map 待更新的文件上传状态
     * @return 影响的条数
     */
    int updateUploadState(Map map);

    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @description 根据文档id，删除文件上传状态
     * @param docId 文档id
     */
    void deleteUploadState(@Param("docId") String docId);

    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @description 根据文档id，删除文件上传状态
     * @param docId 文档id
     */
    List<Map<String,String>> selectUpload(@Param("docId") String docId);
}
