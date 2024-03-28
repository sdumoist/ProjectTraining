package com.jxdinfo.doc.mobileapi.indexmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import com.jxdinfo.doc.front.topicmanager.model.DocUserTopic;
import com.jxdinfo.doc.front.topicmanager.service.DocUserTopicService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.doc.manager.statistics.dao.FileStatisticsMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.service.SpecialTopicService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.impl.FsMobileFolderServiceImpl;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.util.ConvertUtil.changeTime;

/**
 * 个人收藏
 * @author yjs
 * @date 2018-11-13
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/index")
public class MobileIndexController {
    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;

    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private DocUserTopicService docUserTopicService;
    @Autowired
    private DocHistorySearchService docHistorySearchService;
    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private ISysUsersService iSysUsersService;

    /** 文库缓存工具类 */
    @Autowired
    private CacheToolService cacheToolService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    /**
     * 专题维护
     */
    @Autowired
    private SpecialTopicService specialTopicService;

    @Autowired
    private ESUtil esUtil;


    @Autowired
    private FileStatisticsMapper fileStatisticsMapper;

    @Resource
    private PersonalOperateService operateService;
    @Resource
    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    @Autowired
    private FrontDocInfoMapper frontDocInfoMapper;

    @Autowired
    private FileTool fileTool;

    @Resource
    private JWTUtil jwtMobileUtil;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Autowired
    private PersonExtranetAccessService personExtranetAccessService;

    @Autowired
    private FsMobileFolderServiceImpl fsMobileFolderService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 是否开启外网限制
     */
    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    @RequestMapping("/topicList")
    @ResponseBody
    public ApiResponse topicList(String name){
        System.out.println("222===========/mobile/index/topicList 开始====");
        long time = System.currentTimeMillis();
        String userId = jwtMobileUtil.getSysUsers().getUserId();
        List<SpecialTopic> topicList = specialTopicService.getValidTopicList(userId,"1",name);
        List<SpecialTopic> specialList = specialTopicService.getSpecialTopicList(userId,name);
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);;
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        String orgId = iSysUsersService.getById(userId).getDepartmentId();
        String deptName = "";
        SysStru stru = sysStruMapper.selectById(orgId);
        if(stru!=null){
            deptName = stru.getOrganAlias();
        }
        if (specialList != null ) {
            try {
                for (int i = 0; i < specialList.size(); i++) {
                    SpecialTopic specialTopic = specialList.get(i);
                  /*  int docCount = frontTopicService.getDocByTopicIdCount(specialTopic.getTopicId(), userId, listGroup, levelCode, adminFlag,deptName);*/
                    String topicCover = URLEncoder.encode(specialTopic.getTopicCover(), "UTF-8");
                    specialTopic.setTopicCover(topicCover);
                /*    specialTopic.setDocNum(docCount);
                    specialTopic.setDocCount(docCount);*/

                    //从缓存中读取浏览数
                    specialTopic.setViewNum(cacheToolService.getTopicReadNum(specialTopic.getTopicId()));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        int topicCount = frontTopicService.getValidTopicListCount(name);
        Map<String,Object> map = new HashMap<>();
        map.put("availableList",topicList);
        map.put("specialList",specialList);
        map.put("selectedList",specialTopicService.getValidTopicList(userId,null,name));
        map.put("topicCount",topicCount);

        long time2 = System.currentTimeMillis();
        System.out.println("222===========/mobile/index/topicList 结束====" + (time2-time));

        return ApiResponse.data(200,map,"");
    }

    /*@RequestMapping("/hotWord")
    @ResponseBody
    public ApiResponse hotWord(Integer pageNumber, Integer pageSize, String dateType){
        System.out.println("333===========/mobile/index/hotWord 开始====");
        System.out.println("333===========hotWord=====dateType===="+dateType);
        long time = System.currentTimeMillis();
        String userId = jwtUtil.getSysUsers().getUserId();
        List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
        List<Map> list =new ArrayList<>();
        Integer count=0;

        if("0".equals(dateType)){
            long time3 = System.currentTimeMillis();
            list = fileStatisticsMapper.getFileListDataByAll((pageNumber-1)*pageSize,pageSize);
            long time4 = System.currentTimeMillis();
            System.out.println("333===========查询list的时间===="+(time4-time3));

            for (Map map : list) {
                long time5 = System.currentTimeMillis();
                Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                long time6 = System.currentTimeMillis();
                System.out.println("333===========查询es的时间===="+(time6-time5));
                try {
                    // 获取收藏数量
                    long time7 = System.currentTimeMillis();
                    int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");
                    long time8 = System.currentTimeMillis();
                    System.out.println("333===========查询collection的时间===="+(time8-time7));

                    // 设置文件内容
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        else if((mapContent.get("content")+"").length()>100) {
                            map.put("CONTENT",(mapContent.get("content") + "").substring(0, 100));
                        }else{
                            map.put("CONTENT", mapContent.get("content") + "");
                        }
                    }
                    String docId = map.get("DOCID")+"";
                    // 获取目录id
                    String folderId = docInfoService.selectById(docId).getFoldId();
                    String creator =  map.get("USERID")+"";
                    String deptId = iSysUsersService.selectById(creator).getDepartmentId();
                    String orgId =sysStruMapper.selectById(deptId).getOrganAlias();
                    map.put("ORGNAME", orgId);
                    FsFolder  fsFolder =new FsFolder();
                    // 获取目录
                    fsFolder = fsFolderService.selectById(folderId);
                    if(fsFolder!=null){
                        map.put("FOLDERNAME", fsFolder.getFolderName());
                    }else {
                        map.put("FOLDERNAME","");
                    }
                    // 获取预览数量
                    map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
                    if("".equals(userId)||userId==null||"null".equals(userId)){
                        map.put("ISSC", "0");
                    }else{
                        map.put("ISSC", collection);
                    }
                    double [] data = new double[2];
                    if ("".equals(map.get("PDFPATH")+"" ) ||
                            "undefined".equals(map.get("PDFPATH")+"")){
                        data = null;
                    }
                    if ((map.get("DOCTYPE")+"").equals(".jpg") || (map.get("DOCTYPE")+"").equals(".png") ||
                            (map.get("DOCTYPE")+"").equals(".gif") ||(map.get("DOCTYPE")+"").equals(".bmp")){



                    // 下载文件 并生成缩略图 获取文件宽高
                        long time9 = System.currentTimeMillis();
                    data=   fileTool.getFileData(map.get("PDFPATH")+"","0");

                        long time10 = System.currentTimeMillis();
                        System.out.println("333===========查询data的时间===="+(time10-time9));

                        if(data!=null){
                            map.put("WIDTH", data[0]);
                            map.put("HEIGHT",data[1]);
                        }else{
                            map.put("WIDTH", 1);
                            map.put("HEIGHT",1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            count = fileStatisticsMapper.getFileListDataByAllCount();
        }else if("1".equals(dateType)){
             // 获取一周内的文档
            list = fileStatisticsMapper.getFileListDataByWeek((pageNumber-1)*pageSize,pageSize);
            for (Map map : list) {
                try {
                    Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        else if((mapContent.get("content")+"").length()>100) {
                            map.put("CONTENT",(mapContent.get("content") + "").substring(0, 100));
                        }else{
                            map.put("CONTENT", mapContent.get("content") + "");
                        }
                    }
                    int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");

                    if("".equals(userId)||userId==null||"null".equals(userId)){
                        map.put("ISSC", "0");
                    }else{
                        map.put("ISSC", collection);
                    }
                    double [] data = new double[2];
                    if ("".equals(map.get("PDFPATH")+"" ) ||
                            "undefined".equals(map.get("PDFPATH")+"")){
                        data = null;
                    }
                    String creator =  map.get("USERID")+"";
                    String deptId = iSysUsersService.selectById(creator).getDepartmentId();
                    String orgId =sysStruMapper.selectById(deptId).getOrganAlias();
                    map.put("ORGNAME", orgId);
                    String docId = map.get("DOCID")+"";
                    String folderId = docInfoService.selectById(docId).getFoldId();
                    FsFolder  fsFolder =new FsFolder();
                    fsFolder = fsFolderService.selectById(folderId);
                    if(fsFolder!=null){
                        map.put("FOLDERNAME", fsFolder.getFolderName());
                    }else {
                        map.put("FOLDERNAME","");
                    }
                    map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
                    if ((map.get("DOCTYPE")+"").equals(".jpg") || (map.get("DOCTYPE")+"").equals(".png") ||
                            (map.get("DOCTYPE")+"").equals(".gif") || (map.get("DOCTYPE")+"").equals(".bmp")){
                        data=   fileTool.getFileData(map.get("PDFPATH")+"","0");
                        if(data!=null){
                            map.put("WIDTH", data[0]);
                            map.put("HEIGHT",data[1]);
                        }else{
                            map.put("WIDTH", 1);
                            map.put("HEIGHT",1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            count = fileStatisticsMapper.getFileListDataByWeekCount();
        }else{
             // 获取一个月内的数据
            list = fileStatisticsMapper.getFileListDataByMonth((pageNumber-1)*pageSize,pageSize);
            for (Map map : list) {
                Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                if(mapContent!=null){
                    if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                        map.put("CONTENT","");
                    }
                    else if((mapContent.get("content")+"").length()>100) {
                        map.put("CONTENT",(mapContent.get("content") + "").substring(0, 100));
                    }else{
                        map.put("CONTENT", mapContent.get("content") + "");
                    }
                }
                try {
                    String creator =  map.get("USERID")+"";
                    int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");
                    String deptId = iSysUsersService.selectById(creator).getDepartmentId();
                    String orgId =sysStruMapper.selectById(deptId).getOrganAlias();
                    map.put("ORGNAME", orgId);
                    String docId = map.get("DOCID")+"";
                    String folderId = docInfoService.selectById(docId).getFoldId();
                    FsFolder  fsFolder =new FsFolder();
                    fsFolder = fsFolderService.selectById(folderId);
                    if(fsFolder!=null){
                    map.put("FOLDERNAME", fsFolder.getFolderName());
                    }else {
                        map.put("FOLDERNAME","");
                    }
                    if("".equals(userId)||userId==null||"null".equals(userId)){
                        map.put("ISSC", "0");
                    }else{
                        map.put("ISSC", collection);
                    }
                    map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
                    double [] data = new double[2];
                    if ("".equals(map.get("PDFPATH")+"" ) ||
                            "undefined".equals(map.get("PDFPATH")+"")){
                        data = null;
                    }
                    if ((map.get("DOCTYPE")+"").equals(".jpg") || (map.get("DOCTYPE")+"").equals(".png") ||
                            (map.get("DOCTYPE")+"").equals(".gif") || (map.get("DOCTYPE")+"").equals(".bmp")){
                        data=   fileTool.getFileData(map.get("PDFPATH")+"","0");
                        if(data!=null){
                            map.put("WIDTH", data[0]);
                            map.put("HEIGHT",data[1]);
                        }else{
                            map.put("WIDTH", 1);
                            map.put("HEIGHT",1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            count = fileStatisticsMapper.getFileListDataByMonthCount();
        }
        list = ConvertUtil.changeMapTime(list);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("list",list);
        map.put("pageCount",count);
        map.put("pageSize",pageSize);
        map.put("pageNum",pageNumber);

        long time2 = System.currentTimeMillis();
        System.out.println("333===========/mobile/index/hotWord 结束====" + (time2-time));

        return ApiResponse.data(200,map,"");
    }*/

    /**
     * 查询热门文档
     *
     * @return
     */
    @RequestMapping("/hotWord")
    @ResponseBody
    public ApiResponse getRank(int pageNumber, int pageSize, HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Page<Map<String, String>> page = new Page<>(pageNumber, pageSize);
        List<String> folderIds = null;
        String userId = jwtMobileUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {
                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, resultMap, msg);
                }

                // 查询外网可以访问的目录
                folderIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderIds == null || folderIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    resultMap.put("list", null);
                    resultMap.put("pageCount", page.getTotal());
                    resultMap.put("pageSize", pageSize);
                    resultMap.put("pageNum", pageNumber);
                    return ApiResponse.data(200, resultMap, "");
                }
            }
        }

        List<Map> list = frontDocInfoMapper.hotWordMobile(page, folderIds);

        resultMap.put("list", list);
        resultMap.put("pageCount", page.getTotal());
        resultMap.put("pageSize", pageSize);
        resultMap.put("pageNum", pageNumber);

        return ApiResponse.data(200, resultMap, "");
    }


    @RequestMapping("/topWord")
    @ResponseBody
    public ApiResponse top(Integer pageNumber, Integer pageSize, HttpServletRequest request){
        System.out.println("444===========/mobile/index/topWord 开始====");
        System.out.println("================客户端地址"+ RemoteIpMobileUtil.getRemoteIp(request));
        long time = System.currentTimeMillis();
        List<DocInfo> list = frontFsFileService.getListByFolderId(pageNumber,pageSize,null);
        list = changeTime(list);

        long time2 = System.currentTimeMillis();
        System.out.println("444===========/mobile/index/topWord 结束====" + (time2-time));

        return ApiResponse.data(200,list,"");
    }


    @RequestMapping("/docHistorySearch")
    @ResponseBody
    public ApiResponse docHistorySearch(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize, String folderId){
        System.out.println("888===========/mobile/index/docHistorySearch 开始====");
        long time = System.currentTimeMillis();
        String userId =jwtMobileUtil.getSysUsers().getUserId();
        List<DocHistorySearch> historySearches = docHistorySearchService.getList(userId,0,8);

        List<Map> hotSearches = docHistorySearchService.selectHotKeywords();
        Map map = new HashMap();
        map.put("historySearches",historySearches);
        map.put("hotSearches",hotSearches);
        long time2 = System.currentTimeMillis();
        System.out.println("888===========/mobile/index/docHistorySearch 结束====" + (time2-time));
        return ApiResponse.data(200,map,"");
    }

    @RequestMapping("/deleteHistorySearch")
    @ResponseBody
    public ApiResponse deleteHistorySearch(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize, String folderId,String keyword){
        System.out.println("999===========/mobileApi/index/deleteHistorySearch 开始====");
        long time = System.currentTimeMillis();
        String userId =jwtMobileUtil.getSysUsers().getUserId();
        docHistorySearchService.updateFlag(userId,keyword);
        long time2 = System.currentTimeMillis();
        System.out.println("999===========/mobileApi/index/deleteHistorySearch 结束====" + (time2-time));
        return ApiResponse.data(200,true,"");

    }

    @RequestMapping("/topicDetail")
    @ResponseBody
    public ApiResponse topicDetail(String topicId,Integer pageSize,Integer pageNumber ,HttpServletRequest request){
        System.out.println("555===========/mobile/index/topicDetail 开始====");
        long time = System.currentTimeMillis();
        Map result = new HashMap();
        String userId = jwtMobileUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
        List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String orgId = iSysUsersService.getById(userId).getDepartmentId();
        String deptName = "";
        SysStru stru = sysStruMapper.selectById(orgId);
        if(stru!=null){
            deptName = sysStruMapper.selectById(orgId).getOrganAlias();
        }
        String levelCodeString  = businessService.getLevelCodeByUserUploadClient(fsFolderParams,deptName);
        List<String> folderExtranetIds = null;
        //判断是否有外网权限
        if (StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 &&RemoteIpMobileUtil.isExtranetVisit(request)) {
                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, result, msg);
                }

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    result.put("count", 0);
                    result.put("list", null);
                    return ApiResponse.data(200, result, "");
                }
            }

        }
        //测试不考虑专题权限,取专题下面的文档
        List<Map> docList = frontTopicService.getDocByTopicIdMobile(topicId, "create_time", pageSize*(pageNumber-1), pageSize,userId,listGroup,levelCode,adminFlag,deptName,levelCodeString,roleList,folderExtranetIds);
        List<Map> docListNew =new ArrayList<>();
        for (Map doc : docList) {
            Map<String, Object> map = new HashMap<>();
            String docId = doc.get("doc_id") + "";
//                        Map mapContent=esUtil.getIndex(doc.get("doc_id")+"");
            map.put("USERID", doc.get("author_id"));
            map.put("DOCID", doc.get("doc_id"));

//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
            map.put("USERNAME", doc.get("authorName"));
            map.put("TITLE", doc.get("title"));
                     /*   if(mapContent!=null){
                            if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                                map.put("CONTENT","");
                            }
                            map.put("CONTENT",mapContent.get("content"));
                        }*/
            map.put("SCCOUNT", doc.get("collectNum"));
            map.put("XZCOUNT", doc.get("downloadNum"));
            map.put("YLCOUNT", doc.get("readNum"));
            // 转换之后的文件的pdf的路径
            map.put("PDFPATH", doc.get("PDFPATH"));
            int collection = operateService.getMyHistoryCountByFileId(doc.get("doc_id") + "", userId, "5");
            // 真实文件的路径
            map.put("PATH", doc.get("PATH"));
            map.put("ISSC", collection);
            map.put("DOCTYPE", doc.get("fileType"));

            DocInfo docInfo = docInfoService.getById(docId);
            if (docInfo != null) {
                String folderId = docInfo.getFoldId();
                FsFolder fsFolder = new FsFolder();
                fsFolder = fsFolderService.getById(folderId);
                if (fsFolder != null) {
                    map.put("FOLDERNAME", fsFolder.getFolderName());
                } else {
                    map.put("FOLDERNAME", "");
                }
            }
            String creator = doc.get("author_id") + "";
            String deptId = "";
            SysUsers user = iSysUsersService.getById(creator);
            if (user != null) {
                deptId = iSysUsersService.getById(creator).getDepartmentId();
            }
            String orgName = "";
            SysStru stru1 = sysStruMapper.selectById(deptId);
            if (stru1 != null) {
                orgName = sysStruMapper.selectById(deptId).getOrganAlias();
            }
            map.put("ORGNAME", orgName);


           /* double[] data = new double[2];
            try {
                data = fileTool.getFileData(doc.get("PDFPATH") + "", "0");
                if (data != null) {
                    map.put("WIDTH", data[0]);
                    map.put("HEIGHT", data[1]);
                } else {
                    map.put("WIDTH", 1);
                    map.put("HEIGHT", 1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }*/
            Timestamp createTime = (Timestamp) doc.get("createTime");
            if (System.currentTimeMillis() - createTime.getTime() <= 604800000) {
                map.put("isNew", true);
            }
            map.put("SHOWTIME", changeTime(createTime));
            docListNew.add(map);
        }
        Integer docCount = frontTopicService.getDocByTopicIdAllCount(topicId, "create_time",userId,listGroup,levelCode,adminFlag,orgId,levelCodeString, roleList);

        result.put("count",docCount);//专题下目录及文件数量
        result.put("list",docListNew);
        //   List<SpecialTopic> topicList = cacheToolService.getTopicList();
        long time2 = System.currentTimeMillis();
        System.out.println("555===========/mobile/index/topicDetail 结束====" + (time2-time));
        return ApiResponse.data(200,result,"");
    }

    @RequestMapping("/getChildren")
    @ResponseBody
    public ApiResponse getChildren(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize, String id, HttpServletRequest request){
        System.out.println("121212===========/mobile/index/getChildren 开始====");
        long time = System.currentTimeMillis();
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        String  isDesc="0";
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        orderMap.put("4", "createUserName");
        orderMap.put("5", "createUserName");
        orderMap.put("6", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        orderMap.put("7", "SUBSTRING_INDEX(fileSize,'k',1)+0");
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
        String orderResult = (String) orderMap.get(2);
        Map<String, Object> result = new HashMap<>(5);
        List<FsFolderView> list = new ArrayList<>();
        int num = 0;
        //判断是否为子级目录（只能在子文件夹上传文件）
        String userId = jwtMobileUtil.getSysUsers().getUserId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList =  sysUserRoleMapper.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        FsFolder fsFolder = fsFolderService.getById(id);
        String[] typeArr;

        String   type = "0";

        if ("0".equals(type)) {
            typeArr = null;
        } else {
            String typeResult = (String) typeMap.get(type);
            typeArr = typeResult.split(",");
        }

        FsFolder folder=fsFolderService.getById(id);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("1");
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru =  sysStruMapper.selectById(deptId);
        if (stru != null) {
            orgId = stru.getOrganAlias();
        }
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        //获得下一级文件和目录


        List<String> folderExtranetIds = null;
        //判断是否有外网权限
        if (StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if ( adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    return ApiResponse.data(code, result, msg);
                }

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    Map<String, Object> resultMap = new HashMap<>();
                    result.put("userId", userId);
                    result.put("isAdmin", adminFlag);
                    result.put("total", num);
                    result.put("rows", null);
                    result.put("amount", 0);
                    return ApiResponse.data(200, resultMap, "");
                }
            }
        }
        list = fsFolderService.getFilesAndFloderMobile((pageNumber - 1) * pageSize, pageSize, id, typeArr, null,
                orderResult, listGroup, userId, adminFlag, "0", levelCodeString, levelCode,isDesc,orgId,roleList,folderExtranetIds);
        //获得下一级文件和目录数量
        num = fsFolderService.getFilesAndFloderNumMobile(id, typeArr, null, orderResult, listGroup, userId,
                adminFlag, "0", levelCodeString, levelCode,orgId,roleList,folderExtranetIds);
        //显示前台的文件数量
        int amount = fsFolderService.getFileNum(id, typeArr, null, listGroup, userId, adminFlag, "0", levelCode,orgId,roleList);
        //判断是否有可编辑文件的权限

        result.put("userId", userId);
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
//        result.put("isChild", isChild);
        result.put("amount", amount);


        long time2 = System.currentTimeMillis();
        System.out.println("121212===========/mobile/index/getChildren 结束====" + (time2-time));

        return ApiResponse.data(200,result,"");
    }

    @RequestMapping("/insertTopic")
    @ResponseBody
    public ApiResponse insertTopic(Integer pageNumber, Integer pageSize, String topicIds){
        System.out.println("777===========/mobile/index/insertTopic 开始====");
        long time = System.currentTimeMillis();
        String userId = jwtMobileUtil.getSysUsers().getUserId();
        String[] topicIdArray = topicIds.split(",");
        List<DocUserTopic> list = new ArrayList<>();
        for (int i = 0; i < topicIdArray.length; i ++){
            if(topicIdArray[i]!=null&&!"".equals(topicIdArray[i])){
                DocUserTopic docUserTopic = new DocUserTopic(userId,topicIdArray[i],i + 1);
                list.add(docUserTopic);
            }
        }
        boolean deleteFlag = docUserTopicService.remove(new QueryWrapper<DocUserTopic>()
                .eq("user_id",userId));
        boolean insertFlag = true;
        if(topicIds!=null&&!"".equals(topicIds)){
            insertFlag = docUserTopicService.saveBatch(list);
        }
        Map<String,Object> map = new HashMap<>();

        long time2 = System.currentTimeMillis();
        System.out.println("777===========/mobile/index/insertTopic 结束====" + (time2-time));

        return ApiResponse.data(200,insertFlag&&insertFlag,"");
    }
}
