package com.jxdinfo.doc.mobileapi.docupload.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.jwt.util.RemoteIpMobileUtil;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.*;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.topicmanager.dao.SpecialTopicFilesMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopicFiles;
import com.jxdinfo.doc.mobileapi.docmanager.service.MobileFilesService;
import com.jxdinfo.doc.mobileapi.foldermanager.service.IMobileFsFolderService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 类的用途：文件上传（分片上传）
 * 创建日期：2018/6/6  ;
 * 修改历史：
 * 修改日期：2018/9/21 ;
 * 修改作者：yjs ;
 * 修改内容：
 *
 * @author yjs ;
 * @version 1.0
 */
@CrossOrigin
@Controller
@RequestMapping("/mobile/upload")
public class MobileUploadController extends BaseController {

    @Autowired
    private IFsFolderService iFsFolderService;

    @Autowired
    private FileTool fileTool;
    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;

    /**
     * 日志
     */
    static final public Logger logger = LogManager.getLogger(MobileUploadController.class);

    /**
     * 配置文件
     */
    @Autowired
    private HussarProperties hussarProperties;



    @Resource
    private UploadService uploadService;
    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService idocInfoService;
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private DocInfoService docInfoService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**
     * 版本控制 服务层
     */
    @Autowired
    private DocVersionService docVersionService;

    /**
     * 权限控制 服务层
     */
    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /**
     * es工具类
     */
    @Autowired
    private ESUtil esUtil;

    @Value("${docbase.filedir}")
    private String tempdir;

    @Value("${docbase.breakdir}")
    private String breakdir;

    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;
    /**
     * 文件处理
     */
    @Autowired
    private MobileFilesService clientFilesService;

    /**
     * 缓存管理
     */
    @Autowired
    private HussarCacheManager cacheManager;


    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private SpecialTopicFilesMapper specialTopicFilesMapper;
    /**
     * 广告位service
     */
    /**
     * 广告位service
     */
    @Autowired
    private BannerService bannerService;
    /**
     * 置顶service
     */
    @Autowired
    private DocTopService docTopService;
    /**
     * 专题service
     */

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /**
     * 控制外网访问权限
     */
    @Autowired
    private IMobileFsFolderService fsMobileFolderService;

    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    @Value("${openExtranetLimit}")
    private String openExtranetLimit;

    @RequestMapping(value = "/uploadVersionFile")
    @ResponseBody
    public String uploadVersionFile(@RequestParam("file") MultipartFile file, String userId, String folderId,
                                    String fileName, String tags, String proName,String oldDocId,
                                    String downloadAble, String visible, String group, String person,String path,
                                    String watermarkUser, String watermarkCompany,String shareable,String categoryId) {

        categoryId = folderId;
        if (userId == null || "管理员".equals(userId)) {
            userId = "superadmin";
        }
        String proId = folderId;
        // InputStream sbs = new ByteArrayInputStream(fileBytes);
        ByteArrayOutputStream baos = null;

        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        //转MD5
        String fileMd5 = MD5Util.getFileMD5(md5InputStream);
        JSONObject json = new JSONObject();
        List<String> docNameList = new ArrayList<String>();
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex);

        // 文件名称集合
        docNameList.add(fileName);
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            return JSON.toJSONString(json);
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            return JSON.toJSONString(json);
        }
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            return JSON.toJSONString(json);
        }

//        if(fileName.indexOf("--")!=-1){
//            Map<String, String> resultMap = new HashMap<String, String>();
//            json.put("code",DocConstant.UPLOADRESULT.NAMEERROR.getValue());
//            return json;
//        }
        List<String> existIdList = idocInfoService.selectExistId(docNameList, folderId);
        // 重名文件列表不为空&&与旧版本文档不重名，则说明有其他重名文件-->拦截
        if (existIdList != null && existIdList.size() != 0 && !oldDocId.equals(existIdList.get(0))) {
            List<String> nameList = idocInfoService.checkFileExist(docNameList, folderId);
            if (nameList != null && nameList.size() != 0) {
                Map<String, String> resultMap = new HashMap<String, String>();
                json.put("name", nameList.get(0));
                json.put("code", DocConstant.UPLOADRESULT.FILEEXIST.getValue());
                return JSON.toJSONString(json);
            }
        }
        List<FsFile> list = fsFileService.getInfoByMd5(fileMd5);
        if (list == null || list.size() == 0) {
            String random = UUID.randomUUID().toString().replace("-", "");
            File outputFile = new File(tempdir + File.separator + random + suffix);

            try {
                if (!outputFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    outputFile.getParentFile().mkdirs();
                }
                outputFile.createNewFile();
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());;
                return JSON.toJSONString(json);
            }
            String idStr;

            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String contentType = getContentType(suffix.toLowerCase());
                int versionNumber = 1;
                // 判断version表中有无旧版本数据
                DocInfo oldDocInfo = idocInfoService.getOne(new QueryWrapper<DocInfo>().eq("doc_id", oldDocId));
                if (docVersionService.count(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId)) == 0) {
                    DocVersion oldVersion = new DocVersion();
                    oldVersion.setDocId(oldDocId);
                    // 随机生成UUID作为版本关联字段
                    oldVersion.setVersionReference(UUID.randomUUID().toString().replace("-", ""));
                    // 版本有效性，默认有效
                    oldVersion.setValidFlag("1");
                    oldVersion.setApplyTime(oldDocInfo.getCreateTime());
                    oldVersion.setApplyUserId(oldDocInfo.getUserId());
                    oldVersion.setVersionNumber(1);
                    // 将旧版本信息插入版本关联表
                    docVersionService.save(oldVersion);
                }else {
                    DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId));
                    versionNumber = docVersionService.selectVersionNumber(oldVersion.getVersionReference());
                }
                // 获取上传文档的当前时间
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                DocInfo docInfo = new DocInfo();
                String docId = UUID.randomUUID().toString().replace("-", "");
                docInfo.setDocId(docId);
                //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
                docInfo.setFileId(outputFile.getName());
                docInfo.setUserId(userId);
                docInfo.setAuthorId(userId);
                docInfo.setContactsId(userId);
                docInfo.setCreateTime(ts);
                docInfo.setUpdateTime(ts);
                docInfo.setFoldId(categoryId);
                docInfo.setDocType(suffix);
                docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                // 继承旧版本的浏览量、下载量、标签数据
                docInfo.setReadNum(oldDocInfo.getReadNum());
                docInfo.setDownloadNum(oldDocInfo.getDownloadNum());
                docInfo.setTags(oldDocInfo.getTags());
                docInfo.setAuthority(downloadAble);
                docInfo.setSetAuthority("0");
                docInfo.setVisibleRange(Integer.parseInt(visible));
                docInfo.setWatermarkUser(watermarkUser);
                docInfo.setWatermarkCompany(watermarkCompany);
                //ValidFlag 默认1 有效
                docInfo.setValidFlag("1");
                // shareFlag 默认继承旧版本
                docInfo.setShareFlag(oldDocInfo.getShareFlag());
                FsFile fileModel = new FsFile();
                fileModel.setCreateTime(ts);
                fileModel.setFileIcon("");
                fileModel.setFileId(docId);
                if (watermarkCompany == null || "".equals(watermarkCompany)) {
                    fileModel.setMd5(fileMd5);
                }
                fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                //大小保留2位小数
                double size = MathUtil.getDecimal(outputFile.length() / 1024, 2);
                fileModel.setFileSize(size + "KB");
                fileModel.setSize(outputFile.length());
                Map<String, Object> deptSpaceIsFreeMap = clientFilesService.checkEmpSpace(StringUtil.getString(size));

                Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
                if (!deptSpaceIsFree) {
                    //部门存储空间不足
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                    return JSON.toJSONString(resultMap);
                }
                fileModel.setFileType(suffix);

                //拼装操作历史记录
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(0);
                resInfoList.add(docResourceLog);
                List<DocResourceLog> oldResInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog oldDocResourceLog = new DocResourceLog();
                String id_old = UUID.randomUUID().toString().replace("-", "");
                oldDocResourceLog.setId(id_old);
                oldDocResourceLog.setResourceId(oldDocId);
                oldDocResourceLog.setOperateTime(ts);
                oldDocResourceLog.setResourceType(0);
                oldDocResourceLog.setUserId(userId);
                oldDocResourceLog.setOperateType(7);
                oldResInfoList.add(oldDocResourceLog);
                //继承权限信息
                List<DocFileAuthority> listAuthority = new ArrayList<>();
                List<String> indexList = new ArrayList<>();
                listAuthority = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id", oldDocId));
                //操作者类型（0：userID,1:groupID,2:organID，3:全体成员）
                for (int i = 0; i < listAuthority.size(); i++) {
                    DocFileAuthority item = listAuthority.get(i);
                    String esId = item.getAuthorId();
                    if (item.getAuthorType() == 2) {
                        esId = item.getOrganId();
                    }
                    indexList.add(esId);
                    // 将list中旧版本docId换成新的docId
                    listAuthority.get(i).setFileId(docId);
                    listAuthority.get(i).setFileAuthorityId(null);
                }
                indexList.add(userId);

                cacheToolService.updateLevelCodeCache(userId);
                docInfoService.insertResourceLog(oldResInfoList);
                try {
                    filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, listAuthority, indexList, contentType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 上传成功后将旧版本设为无效（docinfo表）
                idocInfoService.updateValidFlag(oldDocId, "0");
                // 将es中该文件设为不可检索
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(oldDocId, map);
                // es中更新标签信息
                Map mapTip = new HashMap(1);
                //0为无效，1为有效
                mapTip.put("tags", oldDocInfo.getTags());
                esUtil.updateIndex(docId, mapTip);
                // 继承旧文档的专题信息
                List<SpecialTopicFiles> topics = specialTopicFilesMapper.selectTopicsByDocId(oldDocId);
                if (topics != null && topics.size() != 0) {
                    for (int i = 0; i < topics.size(); i++) {
                        topics.get(i).setTopicFileId(UUID.randomUUID().toString().replace("-", ""));
                        topics.get(i).setDocId(docId);
                    }
                    specialTopicFilesMapper.addSpecialTopicFiles(topics);
                }
                // 上传成功后将新版本的数据插入version表中
                DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId));
                DocVersion newVersion = new DocVersion();
                newVersion.setVersionReference(oldVersion.getVersionReference());
                newVersion.setDocId(docInfo.getDocId());
                newVersion.setValidFlag("1");
                newVersion.setApplyTime(docInfo.getCreateTime());
                newVersion.setApplyUserId(docInfo.getUserId());
                newVersion.setVersionNumber(versionNumber+1);
                docVersionService.save(newVersion);
                //继承置顶、广告位
                List<String> oldDocIds = Arrays.asList(oldDocId.split(","));
                List listTop = docTopService.addCheck(oldDocIds);
                if (listTop.size() > 0) {
                    docTopService.updateTop(oldDocId, docId);
                }
                //继承广告位信息
                List listBanner = bannerService.selectBannerById(oldDocId);
                if (listBanner.size() > 0) {
                    String docType = docInfo.getDocType();
                    String bannerHref;
                    if (".png".equals(docType) || ".jpg".equals(docType) || ".gif".equals(docType) || ".bmp".equals(docType) || ".jpeg".equals(docType)) {
                        bannerHref = "/preview/toShowIMG?id=" + docId;
                    } else if (".mp4".equals(docType) || ".wmv".equals(docType)) {
                        bannerHref = "/preview/toShowVideo?id=" + docId;
                    } else if (".mp3".equals(docType) || ".m4a".equals(docType)) {
                        bannerHref = "/preview/toShowVoice?id=" + docId;
                    } else if (".pdf".equals(docType)
                            || ".doc".equals(docType) || ".docx".equals(docType) || ".dot".equals(docType)
                            || ".wps".equals(docType) || ".wpt".equals(docType)
                            || ".xls".equals(docType) || ".xlsx".equals(docType) || ".xlt".equals(docType)
                            || ".et".equals(docType) || ".ett".equals(docType)
                            || ".ppt".equals(docType) || ".pptx".equals(docType) || ".ppts".equals(docType)
                            || ".pot".equals(docType) || ".dps".equals(docType) || ".dpt".equals(docType)
                            || ".txt".equals(docType)
                            || ".ceb".equals(docType)) {
                        bannerHref = "/preview/toShowPDF?id=" + docId;
                    } else {
                        bannerHref = "/preview/toShowOthers?id=" + docId;
                    }
                    bannerService.updateBanner(oldDocId, docId, bannerHref);
                }
            } catch (Exception e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                return JSON.toJSONString(resultMap);
            }
            json.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
            return JSON.toJSONString(json);
        } else {
            FsFile fsFile = list.get(0);
            Map<String, Object> deptSpaceIsFreeMap = clientFilesService.checkEmpSpace(StringUtil.getString(fsFile.getFileSize()));
            Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
            if (!deptSpaceIsFree) {
                //部门存储空间不足
                json.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                return JSON.toJSONString(json);
            }
            // 查出所有历史版本
            List<DocInfo> histories = docVersionService.selectVersionHistoriesByDocId(oldDocId, "", "");
            boolean mult = false;// 重复标识
            loop:
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < histories.size(); j++) {
                    if (histories.get(j).getDocId().equals(list.get(i).getFileId())) {
                        // 上传的文件与已存在的历史版本完全相同
                        mult = true;
                        break loop;
                    }
                }
            }
            if (mult) {
                json.put("code", DocConstant.UPLOADRESULT.HISTORYEXIST.getValue());
                return JSON.toJSONString(json);
            }
            //秒传
            cacheToolService.updateLevelCodeCache(userId);
            String docId = filesService.uploadVersionFastClient(categoryId, downloadAble, visible, group, person,
                    watermarkUser, fileMd5, fileName, fsFile, fsFile.getFileSize(), shareable, oldDocId,userId);
            List<SpecialTopicFiles> topics = specialTopicFilesMapper.selectTopicsByDocId(oldDocId);
            //继承专题信息
            if (topics != null && topics.size() != 0) {
                for (int i = 0; i < topics.size(); i++) {
                    topics.get(i).setTopicFileId(UUID.randomUUID().toString().replace("-", ""));
                    topics.get(i).setDocId(docId);
                }
                specialTopicFilesMapper.addSpecialTopicFiles(topics);
            }
            //继承置顶信息
            List<String> oldDocIds = Arrays.asList(oldDocId.split(","));
            List listTop = docTopService.addCheck(oldDocIds);
            if (listTop.size() > 0) {
                docTopService.updateTop(oldDocId, docId);
            }
            //继承广告位信息
            List listBanner = bannerService.selectBannerById(oldDocId);
            DocInfo docInfo = idocInfoService.getById(docId);
            boolean BannerFlag = true;
            if (listBanner.size() > 0) {
                String docType = docInfo.getDocType();
                String bannerHref = "";
                if (docType == ".png" || docType == ".jpg" || docType == ".gif" || docType == ".bmp" || docType == ".jpeg") {
                    bannerHref = "/preview/toShowIMG?id=" + docId;
                } else if (docType == ".mp4" || docType == ".wmv") {
                    bannerHref = "/preview/toShowVideo?id=" + docId;
                } else if (docType == ".mp3" || docType == ".m4a") {
                    bannerHref = "/preview/toShowVoice?id=" + docId;
                } else if (docType == ".pdf"
                        || docType == ".doc" || docType == ".docx" || docType == ".dot"
                        || docType == ".wps" || docType == ".wpt"
                        || docType == ".xls" || docType == ".xlsx" || docType == ".xlt"
                        || docType == ".et" || docType == ".ett"
                        || docType == ".ppt" || docType == ".pptx" || docType == ".ppts"
                        || docType == ".pot" || docType == ".dps" || docType == ".dpt"
                        || docType == ".txt"
                        || docType == ".ceb") {
                    bannerHref = "/preview/toShowPDF?id=" + docId;
                } else {
                    bannerHref = "/preview/toShowOthers?id=" + docId;
                }
                bannerService.updateBanner(oldDocId, docId, bannerHref);
            }

            json.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
            json.put("id", docId);
            json.put("authorId", userId);
            return JSON.toJSONString(json);
        }
    }

    @RequestMapping("/getPath")
    @ResponseBody
    public Object getList(  String id) {
        DocInfo docInfo =docInfoService.getById(id);
        FsFolder fsFolder=   fsFolderService.getById(docInfo.getFoldId());
        List<String> idList = new ArrayList<>();
        List<String> nameList= new ArrayList<>();
        String levelCode = fsFolder.getLevelCode();
        for (int m = 0; m <levelCode.length() / 4; m++) {
            String code = levelCode.substring(0, (m + 1) * 4);
            FsFolder   fsFolderChild = fsFolderService.getOne(new QueryWrapper<FsFolder>().eq("level_code", code));
            idList.add(fsFolderChild.getFolderId());
            nameList.add(fsFolderChild.getFolderName());
        }
        Map<String,Object> map = new HashMap<>();
        map.put("idList",idList);
        map.put("nameList",nameList);
        return  map;
    }

    /**
     * @author yjs
     * @description 跳转到文件系统获得列表
     * @param pageNumber 当前页数
     * @param pageSize 每页数据条数
     * @param name 模糊查询关键字
     * @return 查询结果
     */
    @RequestMapping("/getlist")
    @ResponseBody
    public Object getList(  @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(value = "pageSize", defaultValue = "60") int pageSize,
                            String name,String[] typeArr,String order, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(5);
        //获取当前登录用户id
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<FsFolderView> list = new ArrayList<>();
        List<String> folderExtranetIds = null;
        // 开启了外网访问限制
        if (adminFlag != 1 && StringUtils.equals(openExtranetLimit, "true")) {
            // 用户是从外网访问的系统
            if (adminFlag!=1 && RemoteIpMobileUtil.isExtranetVisit(request)) {

                folderExtranetIds = iFolderExtranetAuthService.getFolderExtranetListMobile();
                if (folderExtranetIds == null || folderExtranetIds.size() == 0) { // 没有外网可以访问的目录
                    System.out.println("===================没有配置外网可以访问的目录=====================");
                    result.put("rows", null);
                    return result;
                }

            }
        }
        //获取用户上传数据列表
        list = fsFolderService.getPersonUploadMobile(userId,(pageNumber - 1) * pageSize, pageSize,name,typeArr,order,folderExtranetIds);
        list = changeSize(list);
        //获取上传数据列表的条数
        int num = fsFolderService.getPersonUploadNumMobile(userId,name,folderExtranetIds);

        result.put("total", num);
        result.put("rows", list);
        return result;
    }

    /**
     * @author yjs
     * @description 转化文件大小的方法
     * @param list 待转换数据的列表
     * @return
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
            if (!"folder".equals(fsFolderView.getFileType())) {
                fsFolderView.setCollection(personalCollectionService.getMyCollectionCountByFileId(fsFolderView.getFileId(),jwtUtil.getSysUsers().getUserId())+"");
            }
        }
        return list;
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 检查结果
     */
    @RequestMapping("/checkMd5ExistFile")
    @ResponseBody
    public JSON checkMd5ExistFile(String fileName, String path,
                                  String downloadAble, String visible, String group, String person,
                                  String watermarkUser, String watermarkCompany, String shareable) {
        logger.info("******************进入检测md5******************");
        JSONObject json = new JSONObject();
        String userId = jwtUtil.getSysUsers().getUserId();
        String fileMd5 = super.getPara("fileMd5");
        List<String> docNameList = new ArrayList<String>();
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex + 1);

        // 文件名称集合
        docNameList.add(fileName);
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            return json;
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            return json;
        }
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            return json;
        }

//        if(fileName.indexOf("--")!=-1){
//            Map<String, String> resultMap = new HashMap<String, String>();
//            json.put("code",DocConstant.UPLOADRESULT.NAMEERROR.getValue());
//            return json;
//        }
        List<FsFile> list = fsFileService.getInfoByMd5(fileMd5);;
        if (list == null || list.size() == 0 || !"".equals(watermarkCompany)) {
            logger.info("******************进入上传******************");
            json.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        }
        return json;
    }

    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public String mergeOrCheckVersionChunks(@RequestParam("file") MultipartFile file, String userId, String folderId,
                                            String fileName, String tags, String proName) {

        if (userId == null || "管理员".equals(userId)) {
            userId = "superadmin";
        }
        String proId = folderId;
        // InputStream sbs = new ByteArrayInputStream(fileBytes);
        ByteArrayOutputStream baos = null;

        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        //转MD5
        String md5 = MD5Util.getFileMD5(md5InputStream);
        String json = "";
        int pointIndex = fileName.lastIndexOf(".");
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            return JSON.toJSONString(resultMap);
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            return JSON.toJSONString(resultMap);
        }
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            return JSON.toJSONString(resultMap);
        }

        List<String> nameList = idocInfoService.checkFileExist(docNameList, proId);
        if (nameList != null && nameList.size() != 0) {

            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("name", nameList.get(0));
            resultMap.put("code", DocConstant.UPLOADRESULT.FILEEXIST.getValue());
            resultMap.put("docId", idocInfoService.selectExistId(docNameList, proId).get(0));
            if (tags != null) {
                DocInfo docInfo = new DocInfo();
                docInfo.setDocId(idocInfoService.selectExistId(docNameList, proId).get(0));
                docInfo.setTags(tags);
                idocInfoService.updateById(docInfo);
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("tags", tags);
                esUtil.updateIndex(idocInfoService.selectExistId(docNameList, proId).get(0), map);
            }
            return JSON.toJSONString(resultMap);
        }
        // 截取文件名的后缀名
        try {
            String random = UUID.randomUUID().toString().replace("-", "");
            File outputFile = new File(tempdir + File.separator + random + suffix);

            try {
                if (!outputFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    outputFile.getParentFile().mkdirs();
                }
                outputFile.createNewFile();
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = JSONObject.toJSONString(resultMap);
                return json;
            }
            String idStr;

            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            List<FsFile> listMd5 = fsFileService.getInfoByMd5(md5);;
            if (listMd5 != null && listMd5.size() != 0) {
                FsFile fsFile = listMd5.get(0);
//                Map<String, Object> deptSpaceIsFreeMap = filesService.checkDeptSpace(StringUtil.getString(fsFile.getFileSize()));

                //秒传
                cacheToolService.updateLevelCodeCache(userId);
                String docId = filesService.uploadFastYYZC(proId, "0", "1", null, null,
                        null, md5, fileName, fsFile, fsFile.getFileSize(), "1", userId, tags);
                if (docId == null) {
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
                resultMap.put("docId", docId);
                json = JSONObject.toJSONString(resultMap);

                return json;
            }
            try {
                String contentType = getContentType(suffix.toLowerCase());

                // 获取当前登录用户的用户名信息

                // 获取上传文档的当前时间
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                DocInfo docInfo = new DocInfo();
                String docId = UUID.randomUUID().toString().replace("-", "");
                idStr = docId;
                docInfo.setDocId(docId);
                //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
                docInfo.setFileId(outputFile.getName());
                docInfo.setUserId(userId);
                docInfo.setAuthorId(userId);
                docInfo.setContactsId(userId);
                docInfo.setCreateTime(ts);

                if (tags != null) {
                    docInfo.setTags(tags);
                }
                docInfo.setFoldId(proId);
                docInfo.setDocType(suffix);
                docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                docInfo.setReadNum(0);
                docInfo.setDownloadNum(0);
                docInfo.setValidFlag("1");
                docInfo.setAuthority("1");
                docInfo.setSetAuthority("0");
                docInfo.setVisibleRange(Integer.parseInt("1"));
                //ValidFlag 默认1 有效
                docInfo.setValidFlag("1");
                // shareFlag 默认1 可分享
                docInfo.setShareFlag("1");
                FsFile fileModel = new FsFile();
                fileModel.setCreateTime(ts);
                fileModel.setFileIcon("");
                fileModel.setFileId(docId);
                //复制出两个输入流

                fileModel.setMd5(md5);
                fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                //大小保留2位小数
                double size = MathUtil.getDecimal(outputFile.length() / 1024, 2);
                fileModel.setFileSize(size + "KB");
//                Map<String, Object> deptSpaceIsFreeMap = filesService.checkDeptSpace(StringUtil.getString(size));
//
//                Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
//                if (!deptSpaceIsFree) {
//                    //部门存储空间不足
//                    Map<String, String> resultMap = new HashMap<>();
//                    resultMap.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
//                    return JSON.toJSONString(resultMap);
//                }
                fileModel.setFileType(suffix);

                //拼装操作历史记录
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(0);
                resInfoList.add(docResourceLog);
                //拼装权限信息
                List<DocFileAuthority> list = new ArrayList<>();

                List<String> indexList = new ArrayList<>();
                //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                FsFolder fsFolder = fsFolderService.getById(proId);
                if (fsFolder != null ) {
                    if (fsFolder.getOwnId() == null || "".equals(fsFolder.getOwnId())) {
                        DocFileAuthority docFileAuthority = new DocFileAuthority();
                        docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                        docFileAuthority.setAuthorId("allpersonflag");
                        //操作者类型（0：userID,1:groupID,2:roleID）
                        docFileAuthority.setAuthorType(3);
                        docFileAuthority.setFileId(docId);
                        docFileAuthority.setAuthority(0);
                        list.add(docFileAuthority);
                        indexList.add("allpersonflag");
                        indexList.add(userId);
                    }
                }
                indexList.add(userId);

                cacheToolService.updateLevelCodeCache(userId);
                filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("docId", docId);
                json = JSONObject.toJSONString(resultMap);
                return json;
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = JSONObject.toJSONString(resultMap);
                return json;
            }
        } catch (Exception e) {
            logger.error("IO Exception：", e);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        return json;
    }

    private String getContentType(String suffix) {
        String contentType = null;
        if (suffix.equals(".doc") || suffix.equals(".docx")) {
            contentType = "application/msword";
            return contentType;
        } else if (suffix.equals(".ppt") || suffix.equals(".pptx")) {
            contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            return contentType;
        } else if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            contentType = "spreadsheetml";
            return contentType;
        } else if (suffix.equals(".png") || suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".bmp")) {
            contentType = "image";
            return contentType;
        } else if (suffix.equals(".txt")) {
            contentType = "text/plain";
            return contentType;
        } else if (suffix.equals(".pdf")) {
            contentType = "application/pdf";
            return contentType;
        } else if (suffix.equals(".mp3")) {
            contentType = "audio/mp3";
            return contentType;
        } else if (suffix.equals(".mp4")) {
            contentType = "video/mp4";
            return contentType;
        } else if (suffix.equals(".mov")) {
            contentType = "video/mov";
            return contentType;
        }else if (suffix.equals(".wav")) {
            contentType = "audio/wav";
            return contentType;
        } else if (suffix.equals(".avi")) {
            contentType = "video/avi";
            return contentType;
        } else if (suffix.equals(".ceb")) {
            contentType = "ceb";
            return contentType;
        } else if (suffix.equals(".zip")) {
            contentType = "application/x-zip-compressed";
            return contentType;
        } else if (suffix.equals(".sql")) {
            contentType = "text/x-sql";
            return contentType;
        } else if (suffix.equals(".rar")) {
            contentType = "application/octet-stream";
            return contentType;
        } else if (suffix.equals(".xml")) {
            contentType = "text/xml";
            return contentType;
        } else {
            return null;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 检查结果
     */
    @RequestMapping("/checkMd5Exist")
    @ResponseBody
    public JSON checkMd5Exist(String fileName, String categoryId, String path,
                              String downloadAble, String visible, String group, String person,
                              String watermarkUser, String watermarkCompany, String shareable) {
        logger.info("******************进入检测md5******************");
        JSONObject json = new JSONObject();
        String userId = jwtUtil.getSysUsers().getUserId();
        String fileMd5 = super.getPara("fileMd5");
        List<String> docNameList = new ArrayList<String>();
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex + 1);
        if (!StringUtil.checkIsEmpty(path)) {
            String[] folderList = path.split("\\\\");
            String folderId = categoryId;
            for (int j = 0; j < folderList.length; j++) {
                folderId = addFolder(folderList[j], folderId);
            }
            categoryId = folderId;
        }
        // 文件名称集合
        docNameList.add(fileName);
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            return json;
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            return json;
        }
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            return json;
        }

//        if(fileName.indexOf("--")!=-1){
//            Map<String, String> resultMap = new HashMap<String, String>();
//            json.put("code",DocConstant.UPLOADRESULT.NAMEERROR.getValue());
//            return json;
//        }
        List<String> nameList = idocInfoService.checkFileExist(docNameList, categoryId);
        if (nameList != null && nameList.size() != 0) {
            String docId = idocInfoService.selectExistId(docNameList, categoryId).get(0);
            Map<String, String> resultMap = new HashMap<String, String>();
            json.put("name", nameList.get(0));
            json.put("code", DocConstant.UPLOADRESULT.FILEEXIST.getValue());
            json.put("docId", docId);
            json.put("fileMd5", fsFileService.getById(docId).getMd5());
            json.put("folderId", categoryId);
            return json;
        }
        List<FsFile> list = fsFileService.getInfoByMd5(fileMd5);;
        if (list == null || list.size() == 0 || !"".equals(watermarkCompany)) {
            logger.info("******************进入上传******************");
            json.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        } else {
            logger.info("******************进入秒传******************");
            FsFile fsFile = list.get(0);
            Map<String, Object> deptSpaceIsFreeMap = clientFilesService.checkEmpSpace(StringUtil.getString(fsFile.getFileSize()));
            Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
            if (!deptSpaceIsFree) {
                //部门存储空间不足
                Map<String, String> resultMap = new HashMap<String, String>();
                json.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                return json;
            }

            //秒传
            cacheToolService.updateLevelCodeCache(userId);
            String docId = filesService.uploadFast(categoryId, downloadAble, visible, group, person,
                    watermarkUser, fileMd5, fileName, fsFile, fsFile.getFileSize(), shareable, userId,"1");
            if (docId == null) {
                json.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json.put("id", docId);
                json.put("authorId", userId);
                return json;
            }
            json.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
            json.put("docId", docId);
            json.put("folderId", categoryId);
            json.put("authorId", userId);
        }

        return json;
    }

    public synchronized String addFolder(String name, String id) {
        List<FsFolder> list = fsFolderService.addCheck(id, name, null);
        if (list.size() > 0) {
            return list.get(0).getFolderId();
        } else {
            Date date = new Date();
            FsFolder fsFolder = new FsFolder();
            fsFolder.setFolderName(name);

            fsFolder.setParentFolderId(id);
            FsFolderParams fsFolderParams = new FsFolderParams();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");
            String userId = jwtUtil.getSysUsers().getUserId();
            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            FsFolder fsFolderParent = new FsFolder();
            fsFolderParent = fsFolderService.getById(fsFolder.getParentFolderId());
            String ownId = fsFolderParent.getOwnId();
            if (ownId != null && !"".equals(ownId)) {
                fsFolder.setOwnId(ownId);
            }
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 3 - 1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 3);
                    String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName;
                }
                localName = localName + "\\" + fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            fsFolderParams.setFolderName(name);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            return folderId;
        }
    }

    @RequestMapping("/checkUploadState")
    @ResponseBody
    public boolean checkUploadState(String docId){
        return uploadService.checkUploadStateFromFast(docId);
    }
}