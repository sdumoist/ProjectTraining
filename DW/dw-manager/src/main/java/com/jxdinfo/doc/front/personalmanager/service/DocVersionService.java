package com.jxdinfo.doc.front.personalmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;

import java.util.List;

/**
 * Created by ZhongGuangrui on 2019/1/2.
 * 文档版本关联表 业务逻辑层
 */
public interface DocVersionService extends IService<DocVersion> {
    /**
     * 查询同一个文件不同版本的文档id
     * @param versionReference  版本关联uid
     * @return  返回所有不同版本的id集合
     */
    List<String> selectIds(String versionReference);

    /**
     * 批量设置历史版本失效
     * @param docIds    文档id集合
     * @return          设置成功的条数
     */
    int updateValidFlag(String[] docIds);

    /**
     * 根据文档id查询其历史版本
     * @param docId     文档id
     * @param order     排序规则
     * @param name      文件名，模糊查询
     * @return          List
     * @author          ZhongGuangrui
     */
    List<DocInfo> selectVersionHistoriesByDocId(String docId, String order, String name);

    int selectVersionNumber(String VersionReference);
}
