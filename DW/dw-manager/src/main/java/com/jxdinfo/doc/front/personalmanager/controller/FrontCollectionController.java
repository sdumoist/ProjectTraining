package com.jxdinfo.doc.front.personalmanager.controller;

import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.collectionmanager.dao.PersonalCollectionMapper;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * 类的用途：跳转前台我的收藏<p>
 * 创建日期：2018年12月6日 <br>
 * 作者：yjs <br>
 */
@Controller
@RequestMapping("/frontPersonalCollection")
public class FrontCollectionController {
    /**
     * 跳转个人中心
     *
     * @param model model类
     * @return string 返回路径
     */
    @Resource
    private PersonalCollectionMapper personalCollectionMapper;
    @GetMapping("/list")
    public String index(Model model,String openFileId,String filePath) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        if (personalCollectionMapper.selectByResourceId("abcde4a392934742915f89a586989292",userId,null).size()==0){
            DocCollection docCollection = new DocCollection();
            String collectionId = UUID.randomUUID().toString().replaceAll("-", "");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            docCollection.setCollectionId(collectionId);
            docCollection.setResourceId("abcde4a392934742915f89a586989292");
            docCollection.setParentFolderId("root");
            docCollection.setResourceType("1");
            docCollection.setCreateTime(ts);
            docCollection.setCreateUserId(userId);
            docCollection.setLevelCode("001");
            docCollection.setResourceName("我的收藏");
            personalCollectionMapper.insertCollectionFolder(docCollection);
        }
        String userName = ShiroKit.getUser().getName();
        model.addAttribute("userName", userName);
        model.addAttribute("openFileId", openFileId);
        model.addAttribute("folderName", filePath);
        return "/doc/front/personalcenter/collection.html";
    }
}
