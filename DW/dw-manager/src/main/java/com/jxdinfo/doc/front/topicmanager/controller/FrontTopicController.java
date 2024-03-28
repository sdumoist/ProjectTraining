package com.jxdinfo.doc.front.topicmanager.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.topicmanager.model.Message;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.doc.manager.topicmanager.service.MessageService;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 类的用途：前台专题<p>
 * 创建日期：2018年9月4日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
@Controller
@RequestMapping(value = "frontTopic")
public class FrontTopicController {

    /**  专题路径前缀 */
    private String PREFIX = "/doc/front/topicmanager/";

    /** 前台专题服务类 */
    @Autowired
    protected FrontTopicService frontTopicService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /** 文库缓存工具类 */
    @Autowired
    private CacheToolService cacheToolService;
    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private MessageService messageService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    /**
     * 专题列表页
     *
     * @param model model类
     * @param page 页码
     * @param size 每页数量
     * @return string 返回路径
     */
    @GetMapping("topicList")
    public String topicList(Model model,int page,int size) {
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //获取当前的登录用户
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        fsFolderParams.setType("1");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString("0001");
        String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        //查询所有专题
        int startNum = page * size - size;
        List<SpecialTopic> topicList = frontTopicService.getTopicList(startNum, size);
        if (topicList != null ){
            try {
                for (int i=0, j=topicList.size(); i<j; i++){
                    SpecialTopic specialTopic = topicList.get(i);
                    List<FsFile> docAllList = frontTopicService.getDocByTopicIdIndex(specialTopic.getTopicId(), "create_time", 0,
                            0,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);

                 /*   int docCount =  getFileCount(docAllList,specialTopic.getTopicId(), userId, listGroup, levelCode, adminFlag, orgId , levelCodeString, deptName);
                    //int docCount = frontTopicService.getDocByTopicIdCount(specialTopic.getTopicId(),userId,listGroup,levelCode,adminFlag,deptName);*/
                    String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
                    specialTopic.setTopicCover(topicCover);
                    topicList.get(i).setDocNum(0);
                    topicList.get(i).setDocCount(0);

                    //从缓存中读取浏览数
                    specialTopic.setViewNum(cacheToolService.getTopicReadNum(specialTopic.getTopicId()));
                    topicList.set(i, specialTopic);
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(29);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        //查询专题数量
        int topicCount = frontTopicService.getTopicListCount(null);
        model.addAttribute("userName", userName);
        model.addAttribute("topicCount", topicCount);
        model.addAttribute("topicList", topicList);
        //查询当前页
        model.addAttribute("curr", page);
        model.addAttribute("isPersonCenter",false);
        return PREFIX+"front_topic_list.html";
    }

    /**
     * 专题下的文档详情页
     *
     * @param model model类
     * @param page 页码
     * @param size 每页数量
     * @param topicId 专题ID
     * @return string 返回路径
     */
    @GetMapping("topicDetail")
    public String topicDetail(Model model, String topicId,int page ,int size,String folderId,String pathName,String pathId) {
        int startNum = page * size - size;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);;
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        //根据专题ID取出专题的信息
        SpecialTopic specialTopic = frontTopicService.getTopicDetailById(topicId);

        try {
            String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
            specialTopic.setTopicCover(topicCover);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //从缓存中取出专题的浏览次数
        specialTopic.setViewNum(cacheToolService.getTopicReadNum(topicId) + 1);
        //测试不考虑专题权限,取专题下面的文档
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("front");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString("0001");
        String levelCode = businessService.getFileLevelCodeFront(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);

        List<FsFile> docList;
        int docCount = 0;
        if (folderId!=null&&!"".equals(folderId)){
            docList = frontTopicService.getFilesAndFloder(startNum, size, folderId, null, "",
                    null, listGroup, userId, adminFlag, "1", levelCode, levelCodeString,null,orgId,roleList);
            docCount = frontTopicService.getFilesAndFloderCount(startNum, size, folderId, null, "",
                    null, listGroup, userId, adminFlag, "1", levelCode, levelCodeString,null,orgId,roleList);
        }else {
            docList = frontTopicService.getDocByTopicId(topicId, "create_time", startNum,
                    size,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
            docCount = frontTopicService.getDocByTopicIdAllCount(topicId, "create_time",userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
        }
//        List<FsFile> docAllList = frontTopicService.getDocByTopicId(topicId, "create_time", 0,
//                0,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString);
    /*    int count = 0;
        count =  getFileCount(docAllList,topicId, userId, listGroup, levelCodeString, adminFlag, orgId , levelCode, deptName);*/
        List<Map<String,Object>> docListWithReadNum = new ArrayList<Map<String,Object>>();
        if (docList != null) {
            for (int i = 0, j = docList.size(); i < j; i++) {
                Map<String, Object> docInfo = (Map<String, Object>) docList.get(i);
                //从缓存中取出文档的预览次数
                docInfo.put("readNum", cacheToolService.getReadNum(StringUtil.getString(docInfo.get("doc_id"))));
                //int collection = operateService.getMyHistoryCountByFileId(StringUtil.getString(docInfo.get("doc_id")),userId,"5");
                List<DocCollection> docCollections = personalCollectionService.selectByResourceId(StringUtil.getString(docInfo.get("doc_id")),userId,null);
                docInfo.put("collection",docCollections.size());
                docListWithReadNum.add(docInfo);
            }
        }
        if (pathName==null){
            pathName = "";
            pathId = "";
        }else {

            pathName = pathName + ",";
            pathId = pathId + ",";

        }
        specialTopic.setDocCount(docCount);
        specialTopic.setDocList(docListWithReadNum);
        model.addAttribute("userName", userName);
        model.addAttribute("topic",specialTopic);
        model.addAttribute("docCount",docList);
        model.addAttribute("curr", page);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("path",pathName);
        model.addAttribute("pathId",pathId);
/*        model.addAttribute("count",count);*/
        model.addAttribute("folderId",folderId);
        return PREFIX+"front_topic_detail.html";
    }
    public int getFileCount(List docList,String topicId,String userId,List<String> listGroup,String levelCode,Integer adminFlag,String orgId ,String levelCodeString,String deptName){
        int docCount = 0 ;
        for (Object ffTemp: docList){
            Map<String, String> ff = (Map) ffTemp;
            if ("folder".equals(ff.get("fileType"))){
                docList = frontTopicService.getFilesAndFloder(0, 0, ff.get("doc_id"), null, "",
                        null, listGroup, userId, adminFlag, "1", levelCodeString, levelCode,null,orgId,ShiroKit.getUser().getRolesList());
                docCount = docCount + getFileCount(docList,topicId, userId, listGroup, levelCode, adminFlag, orgId , levelCodeString, deptName);
            }else {
                docCount = docCount+1;
            }

        }
        return docCount;
    }
    /**
     * 专题列表页
     *
     * @param model model类
     * @param page 页码
     * @param size 每页数量
     * @return string 返回路径
     */
    @GetMapping("messageList")
    public String messageList(Model model,int page,int size,String name,String year,String month) {
        year = XSSUtil.xss(year);
        name = XSSUtil.xss(name);
        month = XSSUtil.xss(month);
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        String userId = UserInfoUtil.getCurrentUser().getId();
        //查询所有专题

        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);;
        int startNum = page * size - size;
        List<Message> topicList = messageService.getList(name,month,year,startNum,size);

        //查询专题数量
        int topicCount =messageService.getListCount(name,month,year);
        model.addAttribute("topicCount", topicCount);
        model.addAttribute("topicList", topicList);
        model.addAttribute("userName", userName);
        model.addAttribute("name", name);
        model.addAttribute("year", year);
        model.addAttribute("month", month);

        //查询当前页
        model.addAttribute("curr", page);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("isPersonCenter",false);
        return PREFIX+"front_message_list.html";
    }
    @RequestMapping("/getCount")
    @ResponseBody
    public Integer getCount(String topicId){
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);;
        //获取当前的登录用户
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("1");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString("0001");
        String levelCode = businessService.getFileLevelCodeFront(fsFolderParams);
        String orgId =docFoldAuthorityService.getDeptIds( ShiroKit.getUser().getDeptId());
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
        List<FsFile> docAllList = frontTopicService.getDocByTopicId(topicId, "create_time", 0,
                0,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
        String deptName = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        return getFileCount(docAllList,topicId, userId, listGroup, levelCodeString, adminFlag, orgId , levelCode, deptName);
    }
}
