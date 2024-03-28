package com.jxdinfo.doc.manager.docmanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;

import java.util.List;

/**
 * <p>
 *  文件权限服务类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
public interface DocFileAuthorityService extends IService<DocFileAuthority> {
    List<Integer> judgeFileAuthority(String fileId, String userId, List<String> parentOrganList, List<String> listGroup);

    /**
     * 更新文件es权限
     *
     * @param fileIds 文件id集合
     */
    public void generateFileAuthorityToEs(List<String> fileIds);
}
