package com.jxdinfo.doc.front.docsharemanager.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface ShareResourceMapper {

    /**
     * 删除由(非结构化平台接口)预览文件生成的分享数据
     */
    void deletePreviewShareData();

    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 新增分享资源
     * @param map 要新增的资源信息
     * @return 影响的数量
     */
    int newShareResource(Map map);

    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 获取分享资源的信息
     * @param hash 映射地址
     * @return 获取到的分享资源信息
     */
    Map getShareResource(@Param("hash") String hash);

    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 获取提取码，验证用
     * @param hash 映射地址
     * @return 获取到的提取码
     */
    String getPwdByHash(String hash);

    /**
     * @author luzhanzhao
     * @date 2018-12-14
     * @decription 获取文件的可分享状态
     * @param docId 文件id
     * @return 可分享状态（文件有效性，是否可分享）
     */
    Map getShareFlagByDocId(String docId);

    /**
     * @author luzhanzhao
     * @date 2018-12-14
     * @description 获取服务器地址，拼接分享链接地址时用
     * @return 访问地址
     */
    Map getServerAddress();

    Map getPdfPath(@Param("hash") String hash);

    String getDocIdByHash(@Param("hash") String hash);
}
