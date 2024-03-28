package com.jxdinfo.doc.front.docsearch.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import com.jxdinfo.doc.front.docsearch.service.SeachStrengthenService;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.service.EntryInfoService;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.historymanager.service.SearchHistoryService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 类的用途：检索页面<p>
 * 创建日期：2018年9月4日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
@Controller
public class SearchController extends BaseController {

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;
    /**
     * 上传路径
     */
    @Value("${docbase.uploadPath}")
    private String uploadPath;

    @Autowired
    private IFsFolderService fsFolderService;

    /**
     * es服务类
     */
    @Autowired
    private ESService esService;

    @Autowired
    private FrontFolderService frontFolderService;
    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * 文档群组服务类
     */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    /**
     * 文档群组服务类
     */
    @Autowired
    private DocHistorySearchService docHistorySearchService;

    /**
     * 历史记录服务类
     **/
    @Resource
    private SearchHistoryService historyService;

    /**
     * 词条服务类
     */
    @Autowired
    private EntryInfoService entryInfoService;

    @Resource
    private SeachStrengthenService seachStrengthenService;

    /**
     * zip下载目录
     */
    @Value("${docbase.picCommond}")
    private boolean picCommond;
    /**
     * 文件类型字典（静态块）
     */
    private static final Map<String, String> fileTypeMap = new HashMap<String, String>();

    static {
        fileTypeMap.put("6", "all");//全部
        fileTypeMap.put("8", "image");//图片
        fileTypeMap.put("9", "video");//视频
        fileTypeMap.put("10", "audio");//音频
        fileTypeMap.put("11", "notimage");//不含图片的全部文档
        fileTypeMap.put("12", "component");//不含图片的全部文档
        fileTypeMap.put("13", "qa");//不含图片的全部文档
        fileTypeMap.put("7", "allword");//全部文档
        fileTypeMap.put("1", "word"); //word
        fileTypeMap.put("2", "presentationml");//ppt
        fileTypeMap.put("3", "plain");//txt
        fileTypeMap.put("4", "pdf");//pdf
        fileTypeMap.put("5", "spreadsheetml");//excel
        fileTypeMap.put("14", "entry");// 词条


    }

    /**
     * @title: 文件搜索
     * @description: 跳转文件搜索页面
     * @date: 2018-9-7.
     * @author: yjs
     * @param: request   response
     * @return: string 路径
     */
    @GetMapping("/searchView")
    public String showSearchView(String fileType, String keyWords, Model model) {
       // long time1 = System.currentTimeMillis();
        keyWords = XSSUtil.xss(keyWords);
        fileType = XSSUtil.xss(fileType);
        // 获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        // 获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        Map history = new HashMap();
        history.put("keywords", keyWords);
        history.put("userId", ShiroKit.getUser().getId());
        history.put("searchTime", new Date());
        historyService.insertIntoSearchHistory(history);
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("userName", userName);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("picCommond", picCommond);
        model.addAttribute("isPersonCenter",false);
        //long time2 = System.currentTimeMillis();
        //System.out.println("=================searchView"+(time2-time1));

        return "/doc/front/homemanager/searchResult.html";
    }

    /**
     * @title: 图片搜索
     * @description: 跳转图片搜索页面
     * @date: 2018-11-2.
     * @author: yjs
     * @param: request   response
     * @return: string 路径
     */
    @GetMapping("/searchAuthor")
    public String searchAuthor(String fileType, String keyWords, Model model) {
        fileType = XSSUtil.xss(fileType);
        keyWords = XSSUtil.xss(keyWords);
        // 获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();

        // 获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        Map history = new HashMap();
        history.put("keywords", keyWords);
        history.put("userId", ShiroKit.getUser().getId());
        history.put("searchTime", new Date());
        historyService.insertIntoSearchHistory(history);
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("userName", userName);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("isPersonCenter",false);
        return "/doc/front/homemanager/searchAuthor.html";
    }

    /**
     * @title: 图片搜索
     * @description: 跳转图片搜索页面
     * @date: 2018-11-2.
     * @author: yjs
     * @param: request   response
     * @return: string 路径
     */
    @GetMapping("/searchPic")
    public String shoSsearchPic(String fileType, String keyWords, Model model, String UIFolder) {
        // 获取当前的登录用户
        keyWords = XSSUtil.xss(keyWords);
        fileType = XSSUtil.xss(fileType);
        UIFolder = XSSUtil.xss(UIFolder);
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();

        // 获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        Map history = new HashMap();
        history.put("keywords", keyWords);
        history.put("userId", ShiroKit.getUser().getId());
        history.put("searchTime", new Date());
        historyService.insertIntoSearchHistory(history);
        model.addAttribute("UIFolder", UIFolder);
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("userName", userName);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("isPersonCenter",false);
        return "/doc/front/docmanager/front-imgFlow.html";
    }

    /**
     * @title: 搜索
     * @description: 搜索
     * @date: 2018-9-7.
     * @author: yjs
     * @param: keyword 查询关键字
     * @param: page 分页
     * @param: fileType 文件类型
     * @return:
     */
    @PostMapping("/search")
    @ResponseBody
    public ESResponse<Map<String, Object>> search(String keyword, Integer page, String fileType, Integer size,
                                                  String tagString, String UIFolder,Integer titlePower,Integer contentPower,Integer tagsPower,Integer categoryPower, Integer order) {
        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        // 获取用户所在的群组
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        String keywordResult = "";
        String fileTypeResult = "";
        try {
            keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
            // 0: 全部 7:文档  8:图片 9:视频 10:音频 15:作者
            fileTypeResult = URLDecoder.decode(fileType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if ("".equals(keyword)&&UIFolder != null && !"".equals(UIFolder)) {
            ESResponse<Map<String, Object>> sd = new ESResponse<Map<String, Object>>();
            List<Map<String, Object>> list = sd.getItems();
            FsFolder folder = fsFolderService.getById(UIFolder);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("front");
            fsFolderParams.setLevelCodeString(folder.getLevelCode());
            fsFolderParams.setId(UIFolder);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
            String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
            //  String levelCode = businessService.getLevelCodeByUser(fsFolderParams);
            if (adminFlag) {
                levelCodeString = null;
            }
            String sql="";
            if(tagString!=null&&!"".equals(tagString)&& tagString.split("\\|").length!=0) {
            String[] strs = tagString.split("\\|");

            for (int i = 0; i < strs.length; i++) {

                // 记录一个分类中多个标签的情况

                // 将标签按照逗号分隔开
                String[] strs_tags = strs[i].split(",");
                if (strs_tags.equals("")) {
                continue;
                }

                sql+=" and (";
                // 将一个分类中的多个标签拼装，达到OR的效果
                for (int j = 0; j < strs_tags.length; j++) {
                    if (j == 0) {
                        String strs_tag = strs_tags[j];
                        sql += " D.tags like '%"+strs_tag+"%'" ;
                    }else{
                        String strs_tag = strs_tags[j];
                        sql += " or D.tags like '%"+strs_tag+"%'";
                    }

                }
                sql+=")";
                //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
            }
            }
            List<DocInfo> docList = frontDocInfoService.getListByTime(folder.getLevelCode(), levelCodeString,(page-1)*size,page*size,sql,null);
            int  count = frontDocInfoService.getListByTimeCount(folder.getLevelCode(), levelCodeString,sql,null);
            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            if (docList != null && docList.size() > 0) {
                // 按照ES检索结果的顺序进行排序展示

                // 查询文档详细信息
                docList:for (DocInfo docInfo : docList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String docId = docInfo.getDocId();
                    map.put("createTime", docInfo.getCreateTime());
                    map.put("docId", docInfo.getDocId());
                    String fileSuffixName = docInfo.getDocType()
                            .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                    map.put("author", docInfo.getAuthorName());
//                    map.put("user", docInfo.getUserName());
                    map.put("contacts", docInfo.getContactsName());
                    map.put("fileName", docInfo.getTitle());
//                        map.put("content", contentMap.get(docId));
//                        map.put("authority", docInfo.getAuthority());
                    map.put("title", docInfo.getTitle());
                    map.put("downloadNum", docInfo.getDownloadNum());
                    map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()));
                    map.put("fileSuffixName", fileSuffixName);// 后缀名
                    // 转换之后的文件的pdf的路径
                    map.put("filePdfPath", docInfo.getFilePdfPath());
                    // 真实文件的路径
                    map.put("filePath", docInfo.getFilePath());
//                        map.put("mobile", docInfo.getMobile());
                    map.put("docType", docInfo.getDocType().replace(".", ""));
                    finalList.add(map);
                }
                sd.setItems(finalList);
                sd.setTotal((long) count);
                return sd;
            }


        }

        String docType = "";
        ESResponse<Map<String, Object>> sd = new ESResponse<Map<String, Object>>();
        FsFolderParams fsFolderParamsNew = new FsFolderParams();
        fsFolderParamsNew.setGroupList(listGroup);
        fsFolderParamsNew.setUserId(userId);
        fsFolderParamsNew.setType("2");
        // 有管理权限的目录id
        //long  time1 = System.currentTimeMillis();
       // System.out.println("====================有管理权限的目录id开始===");
        String folderIdString = businessService.getFolderIdByUserUpload(fsFolderParamsNew);
       // long  time2 = System.currentTimeMillis();
       // System.out.println("=====================有管理权限的目录id时间==="+(time2-time1));

        // 搜索全部的时候 如果picCommond配置的为true  则页面上面显示"相关图片" 下面显示"相关文档"
       // long  time3 = System.currentTimeMillis();
       // System.out.println("===================查询es开始==="+fileTypeResult);
        if ("0".equals(fileTypeResult)) {
            if (picCommond == true) {
                docType = fileTypeMap.get("11");
                // es搜索强化， 以下参数为空或报错， 先设置一个大数，搜索时 再把等于10000的参数处理一下
                if(size == null){
                    size = 10;
                }
                if(titlePower == null){
                    titlePower = 10000;
                }
                if(contentPower == null){
                    contentPower = 10000;
                }
                if(tagsPower == null){
                    tagsPower = 10000;
                }
                if(categoryPower == null){
                    categoryPower = 10000;
                }
                if(order == null){
                    order = 10000;
                }
                //sd = esService.search(keywordResult, docType, page, adminFlag, size, tagString,titlePower,contentPower,categoryPower,tagsPower,folderIdString,order);
                sd = seachStrengthenService.seachStrengthen(keywordResult, docType, page, adminFlag, size, tagString,titlePower,contentPower,categoryPower,tagsPower,folderIdString,order);
            } else {
                sd = esService.search(keywordResult, page, adminFlag, size,titlePower,contentPower,categoryPower,tagsPower,folderIdString);
            }
            // 从es中获取文档的信息map

        } else {

            // 选择了文件类型的ES查询
            docType = fileTypeMap.get(fileTypeResult);
            // es搜索强化， 以下参数为空或报错， 先设置一个大数，搜索时 再把等于10000的参数处理一下
            if(size == null){
                size = 10;
            }
            if(titlePower == null){
                titlePower = 10000;
            }
            if(contentPower == null){
                contentPower = 10000;
            }
            if(tagsPower == null){
                tagsPower = 10000;
            }
            if(categoryPower == null){
                categoryPower = 10000;
            }
            if(order == null){
                order = 10000;
            }
            // sd = esService.search(keywordResult, docType, page, adminFlag, size, tagString,titlePower,contentPower,categoryPower,tagsPower,folderIdString,order);
            sd = seachStrengthenService.seachStrengthen(keywordResult, docType, page, adminFlag, size, tagString,titlePower,contentPower,categoryPower,tagsPower,folderIdString,order);
        }
       // long  time4 = System.currentTimeMillis();
       // System.out.println("=====================查询es==="+(time4-time3));

       // long  time5 = System.currentTimeMillis();
        // list: 从es查询的数据
        List<Map<String, Object>> list = sd.getItems();
        if (null != list && list.size() > 0) {
            List<String> idList = new ArrayList<String>();
            // 优化代码逻辑，高亮的title和content放到map里，避免嵌套循环逻辑
            Map<String, String> titleMap = new HashMap<String, String>();
            Map<String, String> contentMap = new HashMap<String, String>();
            // for循环拼接文件id
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String id = map.get("id") == null ? "" : map.get("id").toString();
                idList.add(id);
                String title = map.get("title") == null ? "" : map.get("title").toString();
                String content = map.get("content") == null ? "" : map.get("content").toString();
                titleMap.put(id, title);
                contentMap.put(id, content);
            }
            // 根据ID，从数据库查询出文件的详细数据
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
          //  long  time6 = System.currentTimeMillis();
           // System.out.println("===================查询数据库获取管理目录levelCode==="+(time6-time5));

            String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());

           // long  time10 = System.currentTimeMillis();
            // 查询文件的详细信息 下载次数、预览次数等
            List<DocInfo> docList = frontDocInfoService.getDocInfo(idList, userId, listGroup, levelCode,orgId,rolesList);
           // long  time11 = System.currentTimeMillis();
           // System.out.println("===================查询数据库信息==="+(time11-time10));

            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            int amount=0;
           // long time7= System.currentTimeMillis();
            if (docList != null && docList.size() > 0) {
                // 按照ES检索结果的顺序进行排序展示
                idList:
                for (String id : idList) {
                    // 查询文档详细信息
                    docList:
                    for (DocInfo docInfo : docList) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        String docId = docInfo.getDocId();
                        if (!docId.equals(id)) {
                            continue docList;
                        }
                        if("12".equals(fileType)){
                            if(docInfo.getReadNum()==null){
                                docInfo.setReadNum(0);
                            }
                            amount=amount+docInfo.getReadNum();
                        }
                        map.put("createTime", docInfo.getCreateTime());
                        map.put("docId", docInfo.getDocId());
                        String fileSuffixName = docInfo.getDocType()
                                .substring(docInfo.getDocType().lastIndexOf(".") + 1);
                        map.put("author", docInfo.getAuthorName());
                        if(docInfo.getDeptName()==null||docInfo.getDeptName().equals("")){
                            String dept = (String)ShiroKit.getSubject().getSession().getAttribute("projectTitle");
                            map.put("deptName",dept);
                        }else{
                        map.put("deptName", docInfo.getDeptName());}
                        map.put("folderName", docInfo.getFolderName());
                        map.put("collection",  personalCollectionService.getMyCollectionCountByFileId(docInfo.getDocId(),userId));
                        map.put("user", docInfo.getUserName());
                        map.put("contacts", docInfo.getContactsName());
                        map.put("fileName", titleMap.get(docId));
                        map.put("authorId", docInfo.getAuthorId());
                        map.put("content", contentMap.get(docId));
                        map.put("authority", docInfo.getAuthority());
                        map.put("url", docInfo.getUrl());
                        map.put("shareFlag", docInfo.getShareFlag());
                        map.put("title", docInfo.getTitle());
                        map.put("downloadNum", docInfo.getDownloadNum());
                        map.put("readNumComponent", cacheToolService.getComponentReadNum(docInfo.getDocId()));
                        map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()));
                        map.put("fileSuffixName", fileSuffixName);// 后缀名
                        // 转换之后的文件的pdf的路径
                        map.put("filePdfPath", docInfo.getFilePdfPath());
                        // 真实文件的路径
                        map.put("filePath", docInfo.getFilePath());
                        map.put("mobile", docInfo.getMobile());
                        map.put("docType", docInfo.getDocType().replace(".", ""));
                        finalList.add(map);
                        continue idList;
                    }
                }

            }
           // long  time8 = System.currentTimeMillis();
           // System.out.println("===================查询数据库其他信息==="+(time8-time7));
            sd.setItems(finalList);
        }
        return sd;

    }


    /**
     * 搜索词条
     *
     * @param keyword       关键词
     * @param page          分页参数
     * @param fileType
     * @param size          分页参数
     * @param tagString
     * @param titlePower
     * @param contentPower
     * @param tagsPower
     * @param categoryPower
     * @param order
     * @return
     */
    @PostMapping("/searchEntry")
    @ResponseBody
    public ESResponse<Map<String, Object>> searchEntry(String keyword, Integer page, String fileType, Integer size,
                                                       String tagString, Integer titlePower, Integer contentPower, Integer tagsPower, Integer categoryPower, Integer order) {
        String keywordResult = "";
        String fileTypeResult = "";
        try {
            if(StringUtils.isNotEmpty(keyword)){
                keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
            }
            // 0: 全部 7:文档  8:图片 9:视频 10:音频 15:作者
            fileTypeResult = URLDecoder.decode(fileType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ESResponse<Map<String, Object>> sd = new ESResponse<Map<String, Object>>();

        // 关键字不为空 按照关键字查询词条
        if(StringUtils.isNotEmpty(keywordResult)){
            // 选择了文件类型的ES查询
            String docType = fileTypeMap.get(fileTypeResult);
            // es 查询
            sd = esService.search(keywordResult, docType, page, true, size, tagString, titlePower, contentPower, categoryPower, tagsPower, "", order);

            List<Map<String, Object>> list = sd.getItems();
            if (null != list && list.size() > 0) {
                List<String> idList = new ArrayList<String>();
                // 优化代码逻辑，高亮的title和content放到map里，避免嵌套循环逻辑
                Map<String, String> titleMap = new HashMap<String, String>();
                Map<String, String> contentMap = new HashMap<String, String>();
                // for循环拼接文件id
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map = list.get(i);
                    String id = map.get("id") == null ? "" : map.get("id").toString();
                    idList.add(id);
                    String title = map.get("title") == null ? "" : map.get("title").toString();
                    String content = map.get("content") == null ? "" : map.get("content").toString();
                    titleMap.put(id, title);
                    contentMap.put(id, content);
                }

                // 从数据库详细信息
                List<EntryInfo> infos = entryInfoService.selectListByIds(idList);

                // 组装参数
                List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
                int amount = 0;
                if (infos != null && infos.size() > 0) {
                    // 按照ES检索结果的顺序进行排序展示
                    idList:
                    for (String id : idList) {
                        // 查询文档详细信息
                        entryList:
                        for (EntryInfo info : infos) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            String entryId = info.getId();
                            if (!entryId.equals(id)) {
                                continue entryList;
                            }
                            map.put("id", info.getId());
                            map.put("summaryText", info.getSummaryText());
                            if (StringUtils.isNotEmpty(info.getImgUrl())) {
                                try {
                                    map.put("imgUrl", URLEncoder.encode(info.getImgUrl(), "UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            map.put("imgTitle", info.getImgTitle());
                            map.put("tag", info.getTag());
                            map.put("url", info.getUrl());
                            map.put("createTime", info.getCreateTime());
                            map.put("content", contentMap.get(id));
                            map.put("name", titleMap.get(id));
                            if (info.getDeptName() == null || info.getDeptName().equals("")) {
                                String dept = (String) ShiroKit.getSubject().getSession().getAttribute("projectTitle");
                                map.put("deptName", dept);
                            } else {
                                map.put("deptName", info.getDeptName());
                            }
                            map.put("createUserName", info.getCreateUserName());

                            finalList.add(map);
                            continue idList;
                        }
                    }

                }
                sd.setItems(finalList);
            }
        } else {
            // 关键字为空 查询全部词条
            Page<EntryInfo> page1 = new Page<EntryInfo>(page, size);
            List<String> tagList = new ArrayList<String>();
            if (StringUtils.isNotEmpty(tagString)) {
                String[] tagArr = tagString.split(",");
                for (String tag : tagArr) {
                    tagList.add(tag);
                }
            }
            List<Map<String, Object>> infos = entryInfoService.selectList(page1, order, tagList);
            sd.setTotal(page1.getTotal());
            sd.setItems(infos);
        }
        return sd;
    }


    @RequestMapping("/suggestList")
    @ResponseBody
    public Object suggestList() {
        String keywords = super.getPara("keywords");
        String size = super.getPara("size");
        Integer count = 10;
        if(size!=null){
            count = Integer.parseInt(size);
        }
        List<String> list =esService.suggestList(keywords,count);
       /* if (list.size() > 0) {
            list = docHistorySearchService.distinctSuggest(list);
        }*/
        return list;
    }
    /**
     * 根据字典类型获取字典数据List
     *
     * @return java.lang.Object
     * @author LiangDong
     * @date 2018/5/28 14:19
     */
    @PostMapping("/dicList")
    @ResponseBody
    public Object getListData() {
        String dicType = super.getPara("dicType");
        List<Map<String, Object>> list = cacheToolService.getDictListByType(dicType);
        return list;
    }

    @PostMapping("/searchAuthorList")
    @ResponseBody
    public ESResponse<Map<String, Object>> searchAuthor(String keyword, Integer page, String fileType, Integer size, String tagString, String UIFolder, Integer order)
    {
        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        String keywordResult = "";
        String fileTypeResult = "";
        try {
            keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
            fileTypeResult = URLDecoder.decode(fileType, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ESResponse<Map<String, Object>> sd = new ESResponse<Map<String, Object>>();
        List<Map<String, Object>> list = sd.getItems();
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        Map typeMap = new HashMap();
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
        String[] typeArr;
        if (fileType == null||"".equals(fileType)||"15".equals(fileType)) {
            fileType = "0";
        }
        if ("0".equals(fileType)) {
            typeArr = null;
        } else {
            String typeResult = (String) typeMap.get(fileType);
            typeArr = typeResult.split(",");
        }
        List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
        List<DocInfo> docList = frontFolderService.getFileByAuthor((page - 1) * size, size, keyword,
                listGroup, userId, adminFlag, levelCode, orgId,typeArr,rolesList,order);
//           list  = changeSize(list);
        Integer count = frontFolderService.getFileByAuthorCount(keyword, listGroup, userId,
                adminFlag, levelCode, orgId,typeArr,rolesList);
        if (docList != null && docList.size() > 0) {
            // 按照ES检索结果的顺序进行排序展示

            // 查询文档详细信息
            docList:
            for (DocInfo docInfo : docList) {
                Map<String, Object> map = new HashMap<String, Object>();
                String docId = docInfo.getDocId();
                map.put("createTime", docInfo.getCreateTime());
                map.put("docId", docInfo.getDocId());
                String fileSuffixName = docInfo.getFileType()
                        .substring(docInfo.getFileType().lastIndexOf(".") + 1);
//                    map.put("author", docInfo.getAuthorName());
                 map.put("user", docInfo.getUserName());
                map.put("contacts", docInfo.getContactsName());
                map.put("shareFlag", docInfo.getShareFlag());
                map.put("fileName", docInfo.getTitle());
                map.put("deptName", docInfo.getDeptName());
                map.put("folderName", docInfo.getFolderName());
                map.put("url", docInfo.getUrl());
                map.put("collection",  personalCollectionService.getMyCollectionCountByFileId(docInfo.getDocId(),userId));
//                        map.put("content", contentMap.get(docId));
                     map.put("authority", docInfo.getAuthority());
                map.put("title", docInfo.getTitle());
                map.put("downloadNum", docInfo.getDownloadNum());
                map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()));
                map.put("fileSuffixName", fileSuffixName);// 后缀名
                // 转换之后的文件的pdf的路径
                map.put("filePdfPath", docInfo.getFilePdfPath());
                // 真实文件的路径
                map.put("filePath", docInfo.getFilePath());

//                        map.put("mobile", docInfo.getMobile());
                map.put("docType", docInfo.getFileType().replace(".", ""));
                finalList.add(map);
            }
        }
        sd.setItems(finalList);
        sd.setTotal((long) count);
        return sd;
    }
}
