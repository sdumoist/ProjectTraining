package com.jxdinfo.doc.front.groupmanager.service;

import com.jxdinfo.doc.manager.groupmanager.dao.DocGroupMapper;
import com.jxdinfo.doc.manager.groupmanager.model.DocGroup;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * 类的用途：前台群组<p>
 * 创建日期：2018年9月18日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月18日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
public interface FrontDocGroupService {

    /**
     * 获取当前登录人所在的所有群组
     * @param userId
     * @return
     */
    public List<String> getPremission(String userId);

}
