package com.jxdinfo.doc.front.foldermanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
public interface FrontFoldAuthorityService extends IService<DocFoldAuthority> {


    public int findEdit(String id, List groupList, String userId);

}
