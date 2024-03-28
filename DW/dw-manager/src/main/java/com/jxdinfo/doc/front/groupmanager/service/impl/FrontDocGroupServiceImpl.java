package com.jxdinfo.doc.front.groupmanager.service.impl;

import com.jxdinfo.doc.front.groupmanager.dao.FrontDocGroupMapper;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.groupmanager.dao.DocGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类的用途：前台群组<p>
 * 创建日期：2018年9月18日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月18日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */

@Service
public class FrontDocGroupServiceImpl implements FrontDocGroupService {
    @Autowired
    private FrontDocGroupMapper frontDocGroupMapper;

    /**
     * 获取当前登录人所在的所有群组
     * @param userId
     * @return
     */
    public List<String> getPremission(String userId){
        List<String> list = frontDocGroupMapper.getPremission(userId);
        return list;
    }

}
