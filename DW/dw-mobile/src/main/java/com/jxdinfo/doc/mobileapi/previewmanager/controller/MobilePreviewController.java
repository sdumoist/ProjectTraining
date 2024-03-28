package com.jxdinfo.doc.mobileapi.previewmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.newupload.service.UploadService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/mobile/preview")
public class MobilePreviewController extends BaseController {

    /**
     * 版本管理 服务
     */
    @Autowired
    private DocVersionService docVersionService;

    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private ISysUsersService iSysUsersService;

    @Autowired
    private FsFileService fsFileService;
    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;

    /** 上传路径  */
    @Value("${docbase.uploadPath}")
    private String base;

    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private PersonalOperateService operateService;

    /** 缓存工具服务类 */
    @Autowired
    private CacheToolService cacheToolService;

    /** 上传标志  */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;


    /** fast服务器服务类  */
    @Autowired
    FastdfsService fastdfsService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Autowired
    private DocGroupService docGroupService;

    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;
    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;


    @Resource
    private UploadService uploadService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    /**
     * @author luzhanzhao
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return 获取到的个人下载记录集合
     */
    @RequestMapping("/getMyPreviewList")
    @ResponseBody
    public Map list(String name, String[] typeArr, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order, HttpServletRequest request){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru!=null){
            orgId = stru.getOrganAlias();
        }
        Map histories = new HashMap();
        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(listGroup);
        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");

                    histories.put("message", "没有配置外网可以访问的目录");
                    histories.put("rows", null);
                    histories.put("count", 0);
                    return histories;
                }

            }
        }

        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        List<Map> list = operateService.getMyHistoryMobile(userId, "3", beginIndex, pageSize, name,typeArr,order,levelCode,orgId,folderExtranetIds);
        int count = operateService.getMyHistoryCountMobile(userId, "3", name,folderExtranetIds);
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }

    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/fileDetail")
    @ResponseBody
    public ApiResponse getFileDetail(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null) {
            String userId = jwtUtil.getSysUsers().getUserId();
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            String deptId = iSysUsersService.getById(userId).getDepartmentId();
            SysStru stru = sysStruMapper.selectById(deptId);
            String orgId = "";
            if( sysStruMapper.selectById(deptId)!=null){
                orgId = stru.getOrganAlias();
            }
            String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,orgId);
            List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
            //根据ID获得文档详情
            DocInfo docInfo = frontDocInfoService.getDocDetailMobile(id,userId,listGroup,levelCode, roleList);
            int collection = personalCollectionService.getMyCollectionCountByFileId(docInfo.getDocId(),userId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            map.put("adminFlag", adminFlag);
            map.put("collection", collection);
            map.put("authority", docInfo.getAuthority());
            map.put("id", docInfo.getDocId());
            map.put("filePath", docInfo.getFilePath());
            map.put("filePdfPath", docInfo.getFilePdfPath());
            map.put("userId", docInfo.getUserId());
            map.put("author", docInfo.getAuthorName());
            map.put("createTime", docInfo.getCreateTime());
            map.put("title", docInfo.getTitle());
            map.put("shareFlag",docInfo.getShareFlag());
            map.put("tags",docInfo.getTags());
            //获得文件的类型字符串（前台根据文件类型判断展示内容）
            String fileSuffixName = docInfo.getDocType().substring(docInfo.getDocType().lastIndexOf(".") + 1);
            map.put("downloadNum", docInfo.getDownloadNum());
            map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()) + 1);
            map.put("fileSuffixName", fileSuffixName);
            map.put("docAbstract", docInfo.getDocAbstract());
            if (StringUtil.checkIsEmpty(docInfo.getFilePdfPath())){
                uploadService.checkUploadState(id);
            }
            map.put("uploadState",!StringUtil.checkIsEmpty(docInfo.getFilePdfPath()));
            if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                //文件大小转化
                map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
            }
        }
        return ApiResponse.data(200,map,"");
    }

    @RequestMapping(value = "/getInfo")
    @ResponseBody
    public Object getInfo(@RequestParam String ids) {
        String[] strArr = ids.split(",");
        List<String> idList = new ArrayList<String>();
        for (String element : strArr) {
            idList.add(element);
        }
        String userId = jwtUtil.getSysUsers().getUserId();
        Map<String, Object> result = new HashMap<>(5);
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
//        fsFolderParams.setLevelCodeString(folder.getLevelCode());
//        fsFolderParams.setId(id);
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        String orgId = "";
        SysStru stru = sysStruMapper.selectById(deptId);
        if(stru != null){
            orgId = sysStruMapper.selectById(deptId).getOrganAlias();
        }

        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        List<Map> list = fsFileService.getInfo(idList,userId,listGroup,levelCode,orgId, roleList);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        int fileState = 0;
        if(list!=null&&list.size()!=0){
            Integer validFlag = Integer.parseInt (list.get(0).get("validFlag").toString());
            if(validFlag == 0){
                if (docVersionService.count(new QueryWrapper<DocVersion>()
                        .eq("doc_id",list.get(0).get("fileId").toString())) == 0){
                    result.put("result","1");
                }else {
                    result.put("result","5");// 已被覆盖的文档版本
                }
            }else{
                if(adminFlag==1){
                    result.put("result","4");
                    return result;
                }
                if(list.get(0).get("authority")==null){
                    result.put("result","2");
                }else{
                    Integer power =Integer.parseInt (list.get(0).get("authority").toString()) ;
                    if(power<1){
                        result.put("result","3");
                    }
                }
            }

        }
        return result;
    }
}
