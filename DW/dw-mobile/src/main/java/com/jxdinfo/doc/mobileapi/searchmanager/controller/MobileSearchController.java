package com.jxdinfo.doc.mobileapi.searchmanager.controller;

import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.historymanager.service.SearchHistoryService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.doc.manager.videomanager.model.DocVideoThumb;
import com.jxdinfo.doc.manager.videomanager.service.DocVideoThumbService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping("/mobile/search")
public class MobileSearchController extends BaseController {

    /**
     * 文档群组服务类
     */
    @Autowired
    private FrontDocGroupService frontDocGroupService;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * es服务类
     */
    @Autowired
    private ESService esService;
    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Resource
    private DocVideoThumbService docVideoThumbService;
    @Resource
    private PersonalOperateService operateService;

    /**
     * 历史记录服务类
     **/
    @Resource
    private SearchHistoryService historyService;
    @Autowired
    private FileTool fileTool;
    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 是否开启外网限制
     */
    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;
    @Autowired
    private PersonExtranetAccessService personExtranetAccessService;

    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    /**
     * 检测根据文件的MD5值判断文件已经上传了多少分片
     *
     * @author: yjs
     * @return json
     */
    @Value("${docbase.breakdir}")
    private String breakdir;
    private static final Map<String, String> fileTypeMap = new HashMap<String, String>();

    static {
        fileTypeMap.put("6", "all");//全部
        fileTypeMap.put("8", "image");//图片
        fileTypeMap.put("9", "video");//视频
        fileTypeMap.put("10", "audio");//音频
        fileTypeMap.put("11", "notimage");//不含图片的全部文档

        fileTypeMap.put("7", "allword");//全部文档
        fileTypeMap.put("1", "word"); //word
        fileTypeMap.put("2", "presentationml");//ppt
        fileTypeMap.put("3", "plain");//txt
        fileTypeMap.put("4", "pdf");//pdf
        fileTypeMap.put("5", "spreadsheetml");//excel


    }

    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse checkBreakByMd5(String keyword, String tagString, Integer page, String fileType, Integer size, HttpServletRequest request) {
        String userId = jwtUtil.getSysUsers().getUserId();

        List<String> rolesList = sysUserRoleService.getRolesByUserId(userId);
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        Map history = new HashMap();
        history.put("keywords", keyword);
        history.put("userId", userId);
        history.put("searchTime", new Date());
        historyService.insertIntoSearchHistory(history);
        String docType = "";
        ESResponse<Map<String, Object>> sd = new ESResponse<>();
        ESResponse<Map<String, Object>> imgSd = null;

        // 获取用户所在的群组
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        // 查询有管理权限的目录id
        FsFolderParams fsFolderParamsNew = new FsFolderParams();
        fsFolderParamsNew.setGroupList(listGroup);
        fsFolderParamsNew.setUserId(userId);
        fsFolderParamsNew.setType("2");
        // 有管理权限的目录id
        String folderIdString = businessService.getFolderIdByUserUploadClient(fsFolderParamsNew, userId);

        // 开启了外网访问限制
        List<String> folderExtranetIds = null;
        if (!adminFlag && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (!adminFlag  && RemoteIpMobileUtil.isExtranetVisit(request)) {

                Map<String,Object> userExtranet = fsMobileFolderService.isUserExtranet(userId);
                int code = (Integer)userExtranet.get("code");
                String msg = (String)userExtranet.get("msg");
                if (code == 203){
                    Map<String, Object> resultMap = new HashMap<>();
                    return ApiResponse.data(code, resultMap, msg);
                }

                // 查询外网可以访问的目录
                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("docList", sd.getItems());
                    resultMap.put("pageCount", sd.getTotal());
                    resultMap.put("pageSize", size);
                    resultMap.put("pageNum", page);
                    return ApiResponse.data(200, resultMap, "");
                }
            }
        }

        //long time3 = System.currentTimeMillis();
        //System.out.println("1111searchMobile开始");

        // 0 查询全部
       // System.out.println("=====================fileType"+fileType);
        if ("0".equals(fileType)) {
            sd = esService.searchMobile(keyword, page, adminFlag, size,userId, folderIdString, folderExtranetIds);
            // 从es中获取文档的信息map
        } else if ("7".equals(fileType)){ // 和pc端查询保持一致
            // 选择了文件类型的ES查询你们
            docType = fileTypeMap.get("11");
            sd = esService.searchMobile(keyword, docType, page, adminFlag, size, tagString, userId, folderIdString, folderExtranetIds);
        }else{
            docType = fileTypeMap.get(fileType);
            sd = esService.searchMobile(keyword, docType, page, adminFlag, size, tagString, userId, folderIdString, folderExtranetIds);
        }

        //long time4 = System.currentTimeMillis();
        //System.out.println("1111searchMobile结束" + (time4-time3));


        //long time5 = System.currentTimeMillis();
        //System.out.println("111111getLocalData开始");
        sd = getLocalData(sd, userId);
        //long time6 = System.currentTimeMillis();
        //System.out.println("111111getLocalData结束" + (time6-time5));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("docList", sd.getItems());
        if (imgSd != null) {
            imgSd = getLocalData(imgSd, userId);
            resultMap.put("imgList", imgSd.getItems());
        }
        resultMap.put("pageCount", sd.getTotal());
        resultMap.put("pageSize", size);
        resultMap.put("pageNum", page);
        return ApiResponse.data(200, resultMap, "");
    }

    public ESResponse<Map<String, Object>> getLocalData(ESResponse<Map<String, Object>> sd, String userId) {
        List<String> listGroup = frontDocGroupService.getPremission(userId);
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
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            String orgId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());
            List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
            List<DocInfo> docList = frontDocInfoService.getDocInfo(idList, userId, listGroup, levelCode, orgId, roleList);
            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
                        map.put("USERID", docInfo.getAuthorId());
                        map.put("shareFlag", docInfo.getShareFlag());
                        map.put("SHOWTIME", sdf.format(docInfo.getCreateTime()));
                        map.put("DOCID", docInfo.getDocId());
                        String fileSuffixName = docInfo.getDocType()
                                .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                        map.put("USERNAME", docInfo.getAuthorName());
                        map.put("TITLE", docInfo.getTitle());
                        map.put("authority", docInfo.getAuthority());
                        map.put("CONTENT", StringUtils.isEmpty(contentMap.get(docId)) ? "" : contentMap.get(docId));
                        map.put("XZCOUNT", docInfo.getDownloadNum());
                        map.put("SCCOUNT", docInfo.getCollectNum());
                        String deptName = "";
                        if (iSysUsersService.getById(docInfo.getUserId()) == null) {
                             deptName = "金现代";
                        } else {
                            String deptId = iSysUsersService.getById(docInfo.getUserId()).getDepartmentId();
                            SysStru stru =  sysStruMapper.selectById(deptId);
                            if(stru!=null){
                                deptName = stru.getOrganAlias();
                            }
                        }
                        if (docInfo.getDocType().equals(".mp4") || docInfo.getDocType().equals(".avi") || docInfo.getDocType().equals(".wmv")
                                || docInfo.getDocType().equals("mpg") || docInfo.getDocType().equals("mov") ||
                                docInfo.getDocType().equals("swf")) {
                            DocVideoThumb docVideoThumb = docVideoThumbService.getById(docInfo.getDocId());
                            if (docVideoThumb != null) {
                                String path = docVideoThumbService.getById(docInfo.getDocId()).getPath();
                                map.put("videoPath", path);
                            } else {
                                map.put("videoPath", "");
                            }
                        }
                        map.put("DEPTNAME", deptName);
                        map.put("URL", fsFolderService.getPersonPath(docInfo.getAuthorName()));
                        map.put("YLCOUNT", cacheToolService.getReadNum(docInfo.getDocId()));
                        map.put("YLCOUNTCOMPONENT", cacheToolService.getComponentReadNum(docInfo.getDocId()));
                        map.put("fileSuffixName", fileSuffixName);// 后缀名
                        // 转换之后的文件的pdf的路径
                        map.put("PDFPATH", docInfo.getFilePdfPath());
                        int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(), userId, "5");
                        // 真实文件的路径
                        map.put("PATH", docInfo.getFilePath());
                        map.put("ISSC", collection);
                        map.put("DOCTYPE", docInfo.getDocType());

                        double[] data = new double[2];
                       /*try {
                            data = fileTool.getFileData(docInfo.getFilePdfPath(), "0");
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
                        // 真实文件的路径
                        finalList.add(map);
                        continue idList;
                    }
                }
            }
            sd.setItems(finalList);
        }
        return sd;
    }
}
