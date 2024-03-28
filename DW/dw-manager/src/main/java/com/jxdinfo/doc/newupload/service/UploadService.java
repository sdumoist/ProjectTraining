package com.jxdinfo.doc.newupload.service;

import java.util.List;
import java.util.Map;

public interface UploadService {
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
     * @description 根据文件信息删除文件上传状态
     * @param map 要删除上传状态的文件信息
     */
    void deleteUploadState(Map map);

    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @description 根据id删除文件上传状态
     * @param docId
     */
    void deleteUploadState(String docId);

    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @descirption 检查文件上传状态：true上传成功，false未上传成功
     * @param docId 文档id
     * @return 是否已上传成功
     */
    boolean checkUploadState(String docId);

    /**
     * @author luzhanzhao
     * @date 2019-1-9
     * @description 从快速转化pdf列表中检查上传状态
     * @param docId 文档id
     * @return 是否已上传成功
     */
    boolean checkUploadStateFromFast(String docId);
    boolean checkVideoStateFromFast(String docId);

    List<Map<String,String>> selectUpload(String docId);
}
