package com.jxdinfo.doc.front.docsharemanager.service;


import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface ShareResourceService {


    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 新增分享资源
     * @param fileId 文件id
     * @param fileType 文件类型
     * @param pwdFlag 是否需要提取码
     * @param validTime 有效期
     * @param request
     * @return 分享结果
     */
    Map newShareResourceMobile(String fileId, String fileType, int pwdFlag, int validTime, HttpServletRequest request, String userId);
    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 新增分享资源
     * @param fileId 文件id
     * @param fileType 文件类型
     * @param pwdFlag 是否需要提取码
     * @param validTime 有效期
     * @param request
     * @return 分享结果
     */
    Map newShareResource(String fileId, String fileType, int pwdFlag, int validTime,int authority, String shareUserRadio,String selectShareUsersId,HttpServletRequest request);
    Map newShareResourceYYZC(String fileId, String fileType, int pwdFlag, int validTime,int authority, HttpServletRequest request,String userId);
    Map newShareResourceClient(String fileId, String fileType, int pwdFlag, int validTime,int authority, HttpServletRequest request,String userId);
    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 获取分享资源的信息
     * @param hash 映射地址
     * @return 分享资源信息
     */
    Map getShareResource(String hash);

    /**
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 通过映射地址获取提取码
     * @param hash 映射地址
     * @return 提取码
     */
    String getPwdByHash(String hash);

    /**
     * @author luzhanzhao
     * @date 2018-12-14
     * @description 判断当前文档是否可分享
     * @param docId 文档id
     * @return true：可分享，false：不可分享
     */
    boolean getShareFlagByDocId(String docId,String fileType);

    Map getPdfPath(String hash);

    boolean isPdfPathExist(String hash);

    void setPdfPathFast(String hash);

    /**
     * 新增分享资源(非结构化平台)
     * @param fileId
     * @param fileType
     * @param pwdFlag
     * @param validTime
     * @param authority
     * @param shareUserRadio
     * @param selectShareUsersId
     * @param userId
     * @param request
     * @return
     */
    JSONObject newShareResourceXJ(String fileId, String fileType, int pwdFlag, int validTime, int authority, String shareUserRadio, String selectShareUsersId, String userId, HttpServletRequest request);


    /**
     * 根据hash 获取文件id
     * @param hash
     * @return
     */
    String  getDocIdByHash(String hash);

}
