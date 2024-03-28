package com.jxdinfo.doc.front.homemanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.service.EntryInfoService;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.util.DateUtil;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类的用途：跳转首页<p>
 * 创建日期：2018年9月4日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
@Controller
public class IndexController extends BaseController {

    /** 日志记录 */
    private static Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    /** 科研成功服务类 */
    @Autowired
    private ComponentApplyService componentApplyService;

    @Autowired
    private DocConfigService docConfigService;

    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;


    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private DocHistorySearchService docHistorySearchService;
    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;


    /** 缓存工具服务类 */
    @Autowired
    private CacheToolService cacheToolService;

    /** 前台专题服务类 */
    @Autowired
    private FrontFolderService frontFolderService;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;



    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * 文件统计接口
     */
    @Autowired
    private FileStatisticsService fileStatisticsService;

    @Autowired
    private DocGroupService docGroupService;

    @Resource
    private HussarCacheManager hussarCacheManager;

    @Autowired
    private EntryInfoService entryInfoService;

    @Value("${company.using}")
    private boolean companyFlag;
    @Value("${isProject.using}")
    private boolean projectFalg;
    @Value("${extendedFunctions.newState}")
    private boolean newState;
    @Value("${extendedFunctions.top}")
    private boolean top;

    /**
     * 跳转到首页
     *
     * @param model model类
     * @return string 返回路径
     */
    @GetMapping("/")
    public String showIndexView(Model model, HttpServletRequest request) {
        //如果没登录，就跳转到登录请求
        if (ToolUtil.isEmpty(ShiroKit.getUser())) {
            return BaseController.REDIRECT + "/login";
        }
        String id = UserInfoUtil.getUserInfo().get("ID").toString();
        // 获取当前登录人
        String username = UserInfoUtil.getUserInfo().get("NAME").toString();
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        fsFolderParams.setLevelCodeString("0001");

        int fileCount =  fileStatisticsService.getFilesCount();
        model.addAttribute("userId", id);
        model.addAttribute("userName", username);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("fileCount",fileCount);
        model.addAttribute("projectFlag",projectFalg);
        if(userId.equals("wkadmin")||userId.equals("superadmin")){
            model.addAttribute("adminFlag",1);
        }else{
            model.addAttribute("adminFlag",2);
         //   businessService.getFileLevelCodeFront(fsFolderParams);
        }

/*
        *//**
         *   单点登录，由工作台直接跳转到文库中，设置登录session
         *//*
        SysUsers sysUsers = (SysUsers)this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ID", userId).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        Subject currentUser = ShiroKit.getSubject();
        Session session = currentUser.getSession();
        ShiroUser shiroUser = ShiroKit.getUser();
        session.setAttribute("sessionFlag", true);
        session.setAttribute("csrfFlag", true);
        session.setAttribute("shiroUser", shiroUser);
        session.setAttribute("userId", shiroUser.getId());
        session.setAttribute("projectFlag", projectFalg);
        session.setAttribute("isRole", isRole);
//        List<String> roleList = ShiroKit.getUser().getRolesList();

        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        session.setAttribute("adminFlag", adminFlag);
        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        session.setAttribute("url",url);
        session.setAttribute("theme", this.themeService.getUserTheme());
        if (sysUsers != null && "1".equals(sysUsers.getLoginTimeLimit())) {
            session.setAttribute("startTime", sysUsers.getAccessLoginStartTime());
            session.setAttribute("endTime", sysUsers.getAccessLoginEndTime());
        }

        // 窗口最上方显示的项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        session.setAttribute("projectTitle", projectTitleMap.get("configValue"));


        // 是否显示客户端
        Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
        session.setAttribute("clientShow", clientShowMap.get("configValue"));

        // 是否显示联系方式
        Map<String, String> contactShowMap = frontDocInfoService.getConfigure("contact_show");
        session.setAttribute("contactShow", contactShowMap.get("configValue"));

        SysOnline online = this.iSysOnlineHistService.addRecord();
        session.setAttribute("online", online);

        QueryWrapper<FsFolder> wrapper = new QueryWrapper<FsFolder>();
        List<FsFolder> list = fsFolderService.list(wrapper.eq("own_id", userId).
                eq("parent_folder_id", "2bb61cdb2b3c11e8aacf429ff4208431"));
        cacheToolService.updateLevelCodeCache(userId);
        if (list == null||list.size()==0) {

            FsFolder fsFolder = new FsFolder();

            fsFolder.setFolderName("我的文件夹");
            fsFolder.setOwnId(userId);
            fsFolder.setIsEdit("1");
            fsFolder.setVisibleRange("0");
            fsFolder.setParentFolderId("2bb61cdb2b3c11e8aacf429ff4208431");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");
            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(IdWorker.get32UUID());
            docFoldAuthority.setFoldId(folderId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthority.setAuthorId(userId);

            docFoldAuthorityService.save(docFoldAuthority);
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
        }
        */


        //若是从分享的页面登录，登录后需回到分享的页面
        String shareUrl = (String) hussarCacheManager.getObject("shareLoginUrl", request.getSession().getId());
        if (ToolUtil.isNotEmpty(shareUrl)){
            return BaseController.REDIRECT + shareUrl;
        } else {
            if(companyFlag){
                return "/doc/front/homemanager/index.html";
            }
            else{
                return "/doc/front/homemanager/index2.html";
            }
        }

    }

    /**
     * 跳转到更改头像界面
     * @return 页面
     */
    @GetMapping("/changeHeadIconView")
    public String changeHeadIconView() {
        return "/doc/front/homemanager/changeHeadIcon.html";
    }

    /**
     * 更改头像
     * @param base64Img base64编码后的头像
     * @return 更改结果
     */
    @RequestMapping("/changeHeadIcon")
    @ResponseBody
    public String changeHeadIcon(String base64Img){
        Session session = ShiroKit.getSubject().getSession();
        session.setAttribute("url",base64Img);
        List<Map> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        ShiroUser user = ShiroKit.getUser();
        String userId = user.getId();
        String userName = user.getName();
        map.put("userid",userId);
        map.put("username",userName);
        map.put("createdate", DateUtil.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        map.put("picture64",base64Img);
        list.add(map);
        docConfigService.insertOrUpdate(list);
        return "";
    }


    /**
     * 查询根目录下的目录
     *
     * @return List 目录集合
     */
    @PostMapping("/getNav")
    @ResponseBody
    public List getNav() {
        // 获取当前登录人
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        // 查询首页的文件目录
        List<FsFolder> fsList = frontFsFileService.getFsFileList(userId, listGroup, adminFlag);
        return fsList;
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-07
     * @param opType 根据操作类型进行查询：3-预览；4-下载；
     * @param pageNumber 当前查询页数
     * @param pageSize 每页数据长度
     * @return 排名信息
     */
    @PostMapping("/getOpTypeRank")
    @ResponseBody
    public Object getRank(String opType, @RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "8") int pageSize){
        Map<String ,Object> resultMap = new HashMap<String ,Object> ();
        List<Map> list = frontFsFileService.hotWord((pageNumber-1)*pageSize,pageSize);
        int total = frontFsFileService.hotWordNum();
        list.forEach( doc -> {
            doc.put("fileSize",FileTool.longToString(doc.get("fileSize")+""));
        });
        resultMap.put("total",total);
        resultMap.put("list",list);
        resultMap.put("pageNumber",pageNumber);
        return resultMap;
    }

    /**
     * 获取热门词条
     *
     * @return
     */
    @PostMapping("/getHotEntry")
    @ResponseBody
    public List<EntryInfo> getHotEntry() {
        List<EntryInfo> infos = entryInfoService.getHotEntrys();
        return infos;
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-07
     * @param opType 根据操作类型进行查询：3-预览；4-下载；
     * @param pageNumber 当前查询页数
     * @param pageSize 每页数据长度
     * @return 排名信息
     */
    @PostMapping("/getOpTypeRankByFolderId")
    @ResponseBody
    public Object ByFolderId(String opType, @RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "8") int pageSize,String folderId){
        Map<String ,Object> resultMap = new HashMap<String ,Object> ();
        String levelCode = frontFolderService.getById(folderId).getLevelCode();
        List<Map> list = frontFsFileService.hotWordByLevelCode((pageNumber-1)*pageSize,pageSize,levelCode);
        list.forEach( doc -> {
            doc.put("YLCOUNT",cacheToolService.getReadNum(doc.get("DOCID").toString()));
        });
        resultMap.put("list",list);
        return resultMap;
    }
    /**
     * @describe 员工上传数量排行
     * @author luzhanzhao
     * @date 2018-11-12
     * @return 员工上传数量数据
     */
    @PostMapping("/getUploadRank")
    @ResponseBody
    public List<Map> getUploadRank(){
        String type="user";
        List<Map> list =cacheToolService.getUploadData(type);
        return list;
    }
    /**
     * @describe 最新动态
     * @author yjs
     * @date 2018-11-14
     * @return 员工上传数量数据
     */
    @PostMapping("/newMessge")
    @ResponseBody
    public List<DocInfo> newMessge(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize){
        List<DocInfo> list = new ArrayList<>();
        if (newState) {
            List<String> roleList = ShiroKit.getUser().getRolesList();
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            if (adminFlag == 1) {
                list = frontFsFileService.getListByAdmin(pageSize);
            } else {
                String userId = ShiroKit.getUser().getId();
                List<String> listGroup = docGroupService.getPremission(userId);
                FsFolderParams fsFolderParams = new FsFolderParams();
                fsFolderParams.setGroupList(listGroup);
                fsFolderParams.setUserId(userId);
                fsFolderParams.setRoleList(roleList);
                fsFolderParams.setType("2");
                fsFolderParams.setLevelCodeString("0001");
                fsFolderParams.setId("2bb61cdb2b3c11e8aacf429ff4208431");
                String levelCodeString = businessService.getLevelCodeByUserUpload(fsFolderParams);;
                String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
                list = frontFsFileService.getNewList(pageNumber,pageSize,listGroup,userId,orgId,levelCodeString,roleList);
            }
        } else {
            list = frontFsFileService.getList(pageNumber,pageSize);
        }
        list = changeTime(list);
        return list;
    }

    @GetMapping("/newMessgeFolder")
    @ResponseBody
    public List<DocInfo> newMessgeFolder(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "4") int pageSize,String folderId){
        int start = (pageNumber-1)*pageSize;
        List<DocInfo> list = new ArrayList<>();
        String top_folder = docConfigService.getConfigValueByKey("top_folder");
        if(top_folder==null){
            top_folder=folderId;
        }
        if (top) {
            List<String> roleList = ShiroKit.getUser().getRolesList();
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            if (adminFlag == 1) {
                list = frontFsFileService.getTopListByAdmin();
            } else {
                String userId = ShiroKit.getUser().getId();
                List<String> listGroup = docGroupService.getPremission(userId);
                FsFolderParams fsFolderParams = new FsFolderParams();
                fsFolderParams.setGroupList(listGroup);
                fsFolderParams.setUserId(userId);
                fsFolderParams.setRoleList(roleList);
                fsFolderParams.setType("2");
                fsFolderParams.setLevelCodeString("0001");
                fsFolderParams.setId("2bb61cdb2b3c11e8aacf429ff4208431");
                String levelCodeString = businessService.getLevelCodeByUserUpload(fsFolderParams);
                String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
                list = frontFsFileService.getTopList(listGroup, userId, orgId, levelCodeString,roleList);
            }
        } else {
            list = frontFsFileService.getListByFolderId(start,pageSize,top_folder);
        }
        list = changeTime(list);
        return list;
    }

    @GetMapping("/hotHistorySearch")
    @ResponseBody
    public List<Map> hotHistorySearch(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize,String folderId){
        List<Map> hotSearches = docHistorySearchService.selectHotKeywords();
        return hotSearches;
    }
    @PostMapping("/docHistorySearch")
    @ResponseBody
    public List<DocHistorySearch> docHistorySearch(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize,String folderId){
        String userId = ShiroKit.getUser().getId();
        List<DocHistorySearch> historySearches = docHistorySearchService.getList(userId,0,8);
        return historySearches;
    }
    /**
     * @describe 最新动态
     * @author yjs
     * @date 2018-11-14
     * @return 员工上传数量数据
     */
    @PostMapping("/typeCommand")
    @ResponseBody
    public List<FsFolderView> typeCommand(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "6") int pageSize,String folderId){
        List<FsFolder> list= frontFolderService.getFsFolderByName(folderId);
        if(list!=null&&list.size()>0){
            String levelCode = frontFolderService.getFsFolderByName(folderId).get(0).getLevelCode();

            List<FsFolderView> viewlist = frontFsFileService.getListByType(pageNumber,pageSize,levelCode);
            viewlist = changeSize(viewlist);
            return viewlist;
        }else{
            return  null;

        }


    }

    /**
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
        }
        return list;
    }

    /**
     * @author luzhanzhao
     * @describe 查询专题推荐的数据
     * @date 2018-11-14
     * @return
     */
    @PostMapping("/getTopicData")
    @ResponseBody
    public List getTopicData(){
        List<SpecialTopic> topicList = frontTopicService.getTopicList(0, 10);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("front");
        fsFolderParams.setLevelCodeString("0001");

        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String levelCode = null;
        if(adminFlag!=1){
         levelCode = businessService.getFileLevelCodeFront(fsFolderParams);
        }
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        try{

            if (topicList != null && topicList.size() > 0) {
                for (SpecialTopic specialTopic : topicList) {
                    String topicId = specialTopic.getTopicId();
                    String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
                    specialTopic.setTopicCover(topicCover);

                    fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
                    fsFolderParams.setType("2");
                    String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
                    //测试不考虑专题权限,取专题下面的文档
                    List<Map> docList = frontTopicService.getDocByTopicIdIndex(topicId, "create_time", 0, 8,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
                    docList.forEach( doc -> {
                        Timestamp createTime = (Timestamp) doc.get("createTime");
                        doc.put("readNum",cacheToolService.getReadNum(doc.get("doc_id").toString()));
                        if(doc.get("fileSize")!=null) {
                            doc.put("fileSize", FileTool.longToString(doc.get("fileSize") + ""));
                        }
                        if (System.currentTimeMillis() - createTime.getTime() <= 604800000){
                            doc.put("isNew", true);
                        }
                    });
                    specialTopic.setDocList(docList);
                }
            }
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
//        List<SpecialTopic> topicList = cacheToolService.getTopicList();
        for (int i = 0; i < topicList.size(); i++){
            topicList.get(i).setTopicCover("/preview/list?fileId="+ topicList.get(i).getTopicCover());
        }
        return topicList;
    }

    /**
     * @author lishilin
     * @describe 查询专题推荐的数据
     * @date 2018-11-14
     * @return
     */
    @PostMapping("/getNewTopicFileList")
    @ResponseBody
    public List getNewTopicFileList(){
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);

        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("front");
        fsFolderParams.setLevelCodeString("0001");
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String levelCode = null;
        if(adminFlag !=1) {
             levelCode = businessService.getFileLevelCodeFront(fsFolderParams);
        } fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        fsFolderParams.setType("2");
        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
        //测试不考虑专题权限,取专题下面的文档
        List<Map> docList = frontTopicService.getNewTopicFileListIndex("create_time",userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
        docList.forEach( doc -> {
            Timestamp createTime = (Timestamp) doc.get("createTime");
            doc.put("readNum",cacheToolService.getReadNum(doc.get("doc_id").toString()));
            if (System.currentTimeMillis() - createTime.getTime() <= 604800000){
                doc.put("isNew", true);
            }
        });
        return docList;
    }

    @PostMapping("/getFolderData")
    @ResponseBody
    public List getFolderData(){
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        //long time1 = System.currentTimeMillis();
        //System.out.println("===============查询pFolders开始");
        List<Map> pFolders =frontFolderService.getFolderList("2bb61cdb2b3c11e8aacf429ff4208431", 0 , 50,userId, listGroup, adminFlag);
        //long time2 = System.currentTimeMillis();
        //System.out.println("===============查询pFolders结束"+(time2-time1));


        //long time3 = System.currentTimeMillis();
       // System.out.println("===============查询childFolders开始");
        pFolders.forEach(pFolder -> {
            String pId = pFolder.get("FOLDERID").toString();
            List<Map> childFolders = frontFolderService.getFolderList(pId, 0 , ' ',userId, listGroup, adminFlag);
            pFolder.put("childs",childFolders);
        });
        //long time4 = System.currentTimeMillis();
       // System.out.println("===============查询childFolders结束"+(time4-time3));
        return pFolders;
    }

    /**
     * 转化时间的方法
     */
    public List<DocInfo> changeTime(List<DocInfo> list) {
        for (DocInfo docInfo : list) {
            Timestamp ts = docInfo.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                docInfo.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {

                docInfo.setShowTime(lackTs/(1000*60)+"分钟前");

            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                docInfo.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                docInfo.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String time =ts+"";
                time = time.substring(0,time.indexOf(" "));
                docInfo.setShowTime(time);
            }
        }
        return list;
    }

    /**
     * @describe 手机端最新动态
     * @author yjs
     * @date 2019-1-8
     * @return map
     */
    @PostMapping("/newMessagePhone")
    @ResponseBody
    public Object newMessagePhone(Integer amount){
        Map<String, Object> result = new HashMap<>(3);
        try{
            List<DocInfo> list = frontFsFileService.getList(0,amount);
            list = changeTime(list);
            result.put("success",true);
            result.put("msg","");
            result.put("data",list);
        }catch (Exception e){
            result.put("success",false);
        }
        return result;
    }

    /**
     * @describe 最新动态
     * @author yjs
     * @date 2018-11-14
     * @return 员工上传数量数据
     */
    @PostMapping("/newMessageByPermission")
    @ResponseBody
    public List<DocInfo> newMessgeDehua(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize){
        List<DocInfo> list = frontFsFileService.getListByPermission(pageNumber,pageSize);
        list = changeTime(list);
        return list;
    }
    /**
     * @describe 科研成果最新动态
     * @author yjs
     * @date 2018-11-14
     * @return 员工上传数量数据
     */
    @PostMapping("/newComponentMessage")
    @ResponseBody
    public   Map<String,Object> newComponentMessage(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize){
        List<ComponentApply> list = componentApplyService.componentList(null,null,2,pageSize*(pageNumber-1),pageSize,null,null,null,null,null, null);
        list = changeDate(list);
        int count = componentApplyService.componentListCount(null,null,2,
                null,null,null,null, null, null);
        Map<String,Object> map =new  HashMap();
        map.put("list",list);
        map.put("count",count);
        return map;
    }
    public List<ComponentApply> changeDate(List<ComponentApply> list) {
        for (ComponentApply componentApply : list) {
            Timestamp ts = componentApply.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                componentApply.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {

                componentApply.setShowTime(lackTs/(1000*60)+"分钟前");

            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                componentApply.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                componentApply.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String time =ts+"";
                time = time.substring(0,time.indexOf(" "));
                componentApply.setShowTime(time);
            }

        }
        return list;
    }
    @PostMapping("/showChildrenFolder")
    @ResponseBody
    public   Map<String,Object> showChildrenFolder(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "9") int pageSize, String folderId){
        String userId = ShiroKit.getUser().getId();

        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = 1;
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("1");
        fsFolderParams.setLevelCodeString("0001");
        String levelCodeString = "";
        List<FsFolder> list = fsFolderService.getFolderByLevelCodeStringFirstByFolderId(levelCodeString,adminFlag,folderId);
        int count = componentApplyService.componentListCount(null,null,2,
                null,null,null,null, null, null);
        Map<String,Object> map =new  HashMap();
        map.put("list",list);
        map.put("folderName",fsFolderService.getById(folderId).getFolderName());
        map.put("count",count);
        return map;
    }
    @GetMapping("/chpwd")
    public String chpwd(Model model) {
            return "/common/changePwdNew.html";
    }
    /**
     * @author luzhanzhao
     * @describe 查询专题推荐的数据
     * @date 2018-11-14
     * @return
     */
    @PostMapping("/getTopicList")
    @ResponseBody
    public List getTopicList(){
        List<SpecialTopic> topicList = frontTopicService.getTopicList(0, 10);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setLevelCodeString("0001");

        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
//        List<SpecialTopic> topicList = cacheToolService.getTopicList();
        for (int i = 0; i < topicList.size(); i++){
            topicList.get(i).setTopicCover("/preview/list?fileId="+ topicList.get(i).getTopicCover());
        }
        return topicList;
    }
    @PostMapping("/getTopicDetail")
    @ResponseBody
    public SpecialTopic getTopicList(String id){
        SpecialTopic specialTopic = frontTopicService.getTopicDetailById(id);
        String topicId = specialTopic.getTopicId();
        String topicCover = null;
        try {
            topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        specialTopic.setTopicCover(topicCover);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setLevelCodeString("0001");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);

        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String levelCode = null;
        fsFolderParams.setType("front");
        if(adminFlag!=1){
            levelCode = businessService.getFileLevelCodeFront(fsFolderParams);
        }
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        //测试不考虑专题权限,取专题下面的文档
        List<Map> docList = frontTopicService.getDocByTopicIdIndex(topicId, "create_time", 0, 8,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
        docList.forEach( doc -> {
            Timestamp createTime = (Timestamp) doc.get("createTime");
            doc.put("readNum",cacheToolService.getReadNum(doc.get("doc_id").toString()));
            if(doc.get("fileSize")!=null) {
                doc.put("fileSize", FileTool.longToString(doc.get("fileSize") + ""));
            }
            if(createTime!=null){
                if (System.currentTimeMillis() - createTime.getTime() <= 604800000){
                    doc.put("isNew", true);
                }
            }
        });
        specialTopic.setDocList(docList);
        return  specialTopic;
    }
}
