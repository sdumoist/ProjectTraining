package com.jxdinfo.doc.client.docupload.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.docmanager.service.ClientFilesService;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.MathUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.topicmanager.dao.SpecialTopicFilesMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopicFiles;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.config.properties.HussarProperties;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.util.DateUtil;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

import static com.jxdinfo.doc.common.constant.CacheConstant.FILE_UPLOAD;

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
@Controller
@RequestMapping("/client/upload")
public class ClientUploadController extends BaseController {

    /**
     * 日志
     */
    static final public Logger logger = LogManager.getLogger(ClientUploadController.class);



    @Resource
    private UploadService uploadService;
    /**
     * 配置文件
     */
    @Autowired
    private HussarProperties hussarProperties;

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
    private ClientFilesService clientFilesService;

    /**
     * 缓存管理
     */
    @Resource
    private HussarCacheManager cacheManager;


    @Autowired
    private CacheToolService cacheToolService;

    @Resource
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
     * 当有文件添加进队列时 通过文件名查看该文件是否上传过 上传进度是多少
     *
     * @param fileName
     * @return java.lang.String
     * @author yjs
     * @date 2018/9/4 11:06
     */
    @RequestMapping(value = "selectProgressByFileName")
    @ResponseBody
    public String selectProgressByFileName(String fileName) {
        String present = "0";
        if (ToolUtil.isNotEmpty(fileName)) {
            present = ObjectUtils.toString(cacheManager.getObject(FILE_UPLOAD, "jindutiao_" + fileName));
        }
        return "{jindutiao :'" + present + "'}";
    }
    @RequestMapping(method = RequestMethod.POST, path = "/getMd5")
    @ResponseBody
    public String getMd5(@RequestPart(value = "file", required = false) MultipartFile file) {
        InputStream ins = null;
        String md5 = "";
        try {
            ins = file.getInputStream();

            md5 = DigestUtils.md5Hex(ins);
        } catch (IOException e) {

            e.printStackTrace();
            return "";
        }
        return md5;
    }
    /**
     * 保存上传分片
     *
     * @return void
     * @author yjs
     * @date 2018/9/4 11:32
     */
    @RequestMapping(method = RequestMethod.POST, path = "/fileSave")
    @ResponseBody
    public void fileSave(@RequestPart(value = "file", required = false) MultipartFile uploadFile) {
        String savePath = breakdir;

        String fileMd5 = super.getPara("fileMd5");
        // 小于10m的文件FileItem.getFieldName()是得不到chunk的，所以此处不能是空字符串，否则创建文件时会出错
        String chunk = super.getPara("chunk");
        String fileName = uploadFile.getOriginalFilename();
        try {
            File file = new File(savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
            if (!file.exists()) {
                file.mkdir();
            }
            File chunkFile = new File(
                    savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName) + File.separator + chunk);
            FileUtils.copyInputStreamToFile(uploadFile.getInputStream(), chunkFile);
        } catch (Exception e) {
            logger.error("IO Exception：", e);
        }
    }

    /**
     * 检查分片是否已存在 & 合并分片
     *
     * @param request
     * @param response
     * @return java.lang.String
     * @author yjs
     * @date 2018/9/4 19:48
     */
    @RequestMapping(value = "mergeOrCheckChunks")
    @ResponseBody
    public String mergeOrCheckChunks(HttpServletRequest request, HttpServletResponse response, String categoryId,
                                     String downloadAble, String visible, String group, String person,String path,
                                     String watermarkUser, String watermarkCompany, String fileName, String shareable) {
        logger.info("******************文件:" + fileName + "开始上传******************");
//        fileName = super.getPara("fileName");
        String fileMd5 = super.getPara("fileMd5");
        String param = super.getPara("param");
        String json = "";
        //上传文件保存路径
        String savePath = breakdir;
        logger.info("******************fileMd5参数:" + fileMd5 + "param="+param+"分片******************");
        if ("mergeChunks".equals(param)) {
            logger.info("******************文件:" + fileName + "开始合并******************");
            if (!StringUtil.checkIsEmpty(path)) {
                String[] folderList = path.split("\\\\");
                String folderId = categoryId;
                for (int j = 0; j < folderList.length; j++) {
                    folderId = addFolder(folderList[j], folderId);
                }
                categoryId = folderId;
            }
            // 合并文件
            try {
                // 读取目录里的所有文件
                File f = new File(savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
                // 排除目录,只要文件
                File[] fileArray = f.listFiles();
                // 转成集合，便于排序
                List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                            return -1;
                        }
                        return 1;
                    }
                });

                // 截取文件名的后缀名
                int pointIndex = fileName.lastIndexOf(".");
                // 后缀名
                String suffix = fileName.substring(pointIndex).toLowerCase();
                // 合并后的文件
//                Double random = Math.random();
                String random = UUID.randomUUID().toString().replace("-", "");
                File outputFile = new File(tempdir + File.separator + random + suffix);
                // 创建文件
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

                FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
                FileInputStream fileInputStream = new FileInputStream(outputFile);
                // 合并
                FileChannel inChannel;
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    byte[] data = null;
                    data = new byte[(int) inChannel.size()];
                    fileInputStream.read(data);
                    try {
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    } catch (Exception e) {
                        logger.error(" Exception:", e);
                        Map<String, String> resultMap = new HashMap<>();
                        resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                        json = JSONObject.toJSONString(resultMap);
                        return json;
                    }
                    try {
                        inChannel.close();
                    } catch (IOException e) {
                        logger.error("IO Exception：", e);
                        Map<String, String> resultMap = new HashMap<>();
                        resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                        json = JSONObject.toJSONString(resultMap);
                        return json;
                    }
                    // 删除分片
                    file.delete();
                }
                fileInputStream.close();
                logger.info("******************文件:" + fileName + "创建成功，路径为" + outputFile.getPath() + ",大小为" + outputFile.length() + "******************");
                String idStr;
                String userId = jwtUtil.getSysUsers().getUserId();
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
                    docInfo.setUpdateTime(ts);
                    docInfo.setFoldId(categoryId);
                    docInfo.setDocType(suffix);
                    docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                    docInfo.setReadNum(0);
                    docInfo.setDownloadNum(0);
                    docInfo.setValidFlag(visible);
                    docInfo.setAuthority(downloadAble);
                    docInfo.setSetAuthority("0");
                    docInfo.setVisibleRange(Integer.parseInt(visible));
                    docInfo.setWatermarkUser(watermarkUser);
                    docInfo.setWatermarkCompany(watermarkCompany);
                    //ValidFlag 默认1 有效
                    docInfo.setValidFlag("1");
                    // shareFlag 默认1 可分享
                    docInfo.setShareFlag(shareable);
                    FsFile fileModel = new FsFile();
                    fileModel.setCreateTime(ts);
                    fileModel.setFileIcon("");
                    fileModel.setFileId(docId);
                    if (watermarkCompany == null || "".equals(watermarkCompany)) {
                        fileModel.setMd5(fileMd5);
                    }
                    fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                    //大小保留2位小数
                    double size = MathUtil.getDecimal(outputFile.length() / 1024, 3);
                    long fileSize = outputFile.length();
                    fileModel.setFileSize(size + "KB");
                    fileModel.setSize(fileSize);
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
                    docResourceLog.setOrigin("client");
                    resInfoList.add(docResourceLog);
                    //拼装权限信息
                    List<DocFileAuthority> list = new ArrayList<>();
                    List<String> indexList = new ArrayList<>();
                    FsFolder fsFolder = fsFolderService.getById(categoryId);
                    //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                    if (fsFolder != null) {
                        if (fsFolder.getOwnId() == null || "".equals(fsFolder.getOwnId())) {
                            //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                            DocFileAuthority docFileAuthority = new DocFileAuthority();
                            docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                            docFileAuthority.setAuthorId("allpersonflag");
                            //操作者类型（0：userID,1:groupID,2:roleID）
                            docFileAuthority.setAuthorType(3);
                            docFileAuthority.setFileId(docId);
                            docFileAuthority.setAuthority(0);
                            list.add(docFileAuthority);

                            indexList.add("allpersonflag");
                        }
                    }

                    indexList.add(userId);
                    outChannel.close();
                    fileInputStream.close();

                    cacheToolService.updateLevelCodeCache(userId);
                    filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);

                } catch (IOException e) {
                    logger.error("IO Exception：", e);
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }

                // 清除文件夹
                File tempFile = new File(savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
                if (tempFile.isDirectory() && tempFile.exists()) {
                    tempFile.delete();
                }
                // outputFile.delete();
                Map<String, String> resultMap = new HashMap<>();
                // 将文件的最后上传时间和生成的文件名返回
                resultMap.put("lastUploadTime",
                        ObjectUtils.toString(cacheManager.getObject(FILE_UPLOAD, "lastUploadTime_" + fileName)));
                resultMap.put("pathFileName", cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName) + suffix);
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("id", idStr);
                resultMap.put("folderId", categoryId);
                resultMap.put("authorId", userId);
                // 合并成功后删除缓存中的进度信息
                cacheManager.delete(FILE_UPLOAD, "jindutiao_" + fileName);
                // 合并成功后删除缓存中的最后上传时间，只存没上传完成的
                cacheManager.delete(FILE_UPLOAD, "lastUploadTime_" + fileName);
                // 合并成功后删除文件名称与该文件上传时生成的存储分片的临时文件夹的名称在缓存中的信息
                cacheManager.delete(FILE_UPLOAD, "fileName_" + fileName);

                json = JSONObject.toJSONString(resultMap);

            } catch (Exception e) {
                logger.error("file merge failure:", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = JSONObject.toJSONString(resultMap);
                return json;
            }

        } else if ("checkChunk".equals(param)) {
            logger.info("******************文件:" + fileName + "分片******************");
            Map<String, String> resultMap = new HashMap<>();

            // 检查当前分块是否上传成功
            String chunk = super.getPara("chunkNumber");
            String chunkSize = super.getPara("chunkSize");
            // 文件上传的实时进度
            String jindutiao = super.getPara("jindutiao");
            try {
                //将当前进度存入缓存
                cacheManager.setObject(FILE_UPLOAD, "jindutiao_" + fileName, jindutiao);
                //将最后上传时间存入缓存
                String lastUploadTime = DateUtil.getTime();
                cacheManager.setObject(FILE_UPLOAD, "lastUploadTime_" + fileName, lastUploadTime);
                // 将文件名与该文件上传时生成的存储分片的临时文件夹的名称存入缓存
                // 文件上传时生成的存储分片的临时文件夹的名称由MD5和时间戳组成
                String tempFileName = ObjectUtils.toString(System.currentTimeMillis());
                if (ToolUtil.isEmpty(cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName))) {
                    cacheManager.setObject(FILE_UPLOAD, "fileName_" + fileName, fileMd5 + tempFileName);
                }

                File checkFile = new File(
                        savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_"
                                + fileName) + File.separator + chunk);

                if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
                    // 上传过
                    resultMap.put("ifExist", "1");
                    json = JSONObject.toJSONString(resultMap);
                } else {
                    // 没有上传过
                    resultMap.put("ifExist", "0");
                    json = JSONObject.toJSONString(resultMap);
                }
            } catch (Exception e) {
                logger.error("file check failure:", e);
            }

        }
        return json;
    }

    /**
     * 用于更新版本
     * 检查分片是否已存在 & 合并分片
     *
     * @param request
     * @param response
     * @return java.lang.String
     * @author yjs
     * @date 2018/9/4 19:48
     * 修改：钟广睿
     */
    @RequestMapping(value = "mergeOrCheckVersionChunks")
    @ResponseBody
    public String mergeOrCheckVersionChunks(HttpServletRequest request, HttpServletResponse response, String categoryId,
                                            String downloadAble, String visible, String group, String person, String path,
                                            String watermarkUser, String watermarkCompany, String fileName, String shareable, String oldDocId) {
        String fileMd5 = super.getPara("fileMd5");
        String param = super.getPara("param");
        //  String fileName = super.getPara("fileName");
        String json = "";
        //上传文件保存路径
        String savePath = breakdir;

        if ("mergeChunks".equals(param)) {
            if (!StringUtil.checkIsEmpty(path)) {
                String[] folderList = path.split("\\\\");
                String folderId = categoryId;
                for (int j = 0; j < folderList.length; j++) {
                    folderId = addFolder(folderList[j], folderId);
                }
                categoryId = folderId;
            }
            // 合并文件
            try {
                // 读取目录里的所有文件
                File f = new File(savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
                // 排除目录,只要文件
                File[] fileArray = f.listFiles();
                // 转成集合，便于排序
                List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                            return -1;
                        }
                        return 1;
                    }
                });

                // 截取文件名的后缀名
                int pointIndex = fileName.lastIndexOf(".");
                // 后缀名
                String suffix = fileName.substring(pointIndex).toLowerCase();
                // 合并后的文件
//                Double random = Math.random();
                String random = UUID.randomUUID().toString().replace("-", "");
                File outputFile = new File(tempdir + File.separator + random + suffix);

                // 创建文件
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

                FileChannel outChannel = new FileOutputStream(outputFile).getChannel();
                FileInputStream fileInputStream = new FileInputStream(outputFile);

                // 合并
                FileChannel inChannel;
                for (File file : fileList) {
                    inChannel = new FileInputStream(file).getChannel();
                    byte[] data = null;
                    data = new byte[(int) inChannel.size()];
                    fileInputStream.read(data);
                    try {
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    } catch (Exception e) {
                        logger.error(" Exception：", e);
                        Map<String, String> resultMap = new HashMap<>();
                        resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                        json = JSONObject.toJSONString(resultMap);
                        return json;
                    }
                    try {
                        inChannel.close();
                    } catch (IOException e) {
                        logger.error("IO Exception：", e);
                        Map<String, String> resultMap = new HashMap<>();
                        resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                        json = JSONObject.toJSONString(resultMap);
                        return json;
                    }
                    // 删除分片
                    file.delete();
                }
                fileInputStream.close();
                logger.info("******************文件:" + fileName + "创建成功，路径为" + outputFile.getPath() + ",大小为" + outputFile.length() + "******************");
                String idStr;
                String userId = jwtUtil.getSysUsers().getUserId();
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
                    idStr = docId;
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
                    docResourceLog.setOrigin("client");
                    resInfoList.add(docResourceLog);
                    List<DocResourceLog> oldResInfoList = new ArrayList<DocResourceLog>();
                    DocResourceLog oldDocResourceLog = new DocResourceLog();
                    String id_old = UUID.randomUUID().toString().replace("-", "");
                    oldDocResourceLog.setId(id_old);
                    oldDocResourceLog.setResourceId(oldDocId);
                    oldDocResourceLog.setOperateTime(ts);
                    oldDocResourceLog.setResourceType(0);
                    oldDocResourceLog.setUserId(userId);
                    docResourceLog.setOrigin("client");
                    oldDocResourceLog.setOperateType(7);
                    oldResInfoList.add(oldDocResourceLog);
                    //继承权限信息
                    List<DocFileAuthority> list = new ArrayList<>();
                    List<String> indexList = new ArrayList<>();
                    list = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id", oldDocId));
                    //操作者类型（0：userID,1:groupID,2:organID，3:全体成员）
                    for (int i = 0; i < list.size(); i++) {
                        DocFileAuthority item = list.get(i);
                        String esId = item.getAuthorId();
                        if (item.getAuthorType() == 2) {
                            esId = item.getOrganId();
                        }
                        indexList.add(esId);
                        // 将list中旧版本docId换成新的docId
                        list.get(i).setFileId(docId);
                        list.get(i).setFileAuthorityId(null);
                    }
                    indexList.add(userId);
                    outChannel.close();
                    fileInputStream.close();

                    cacheToolService.updateLevelCodeCache(userId);
                    docInfoService.insertResourceLog(oldResInfoList);
                    filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
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
                } catch (IOException e) {
                    logger.error("IO Exception：", e);
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }

                // 清除文件夹
                File tempFile = new File(savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
                if (tempFile.isDirectory() && tempFile.exists()) {
                    tempFile.delete();
                }
                // outputFile.delete();
                Map<String, String> resultMap = new HashMap<>();
                // 将文件的最后上传时间和生成的文件名返回
                resultMap.put("lastUploadTime",
                        ObjectUtils.toString(cacheManager.getObject(FILE_UPLOAD, "lastUploadTime_" + fileName)));
                resultMap.put("pathFileName", cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName) + suffix);
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("id", idStr);
                resultMap.put("folderId", categoryId);
                resultMap.put("authorId", userId);
                // 合并成功后删除缓存中的进度信息
                cacheManager.delete(FILE_UPLOAD, "jindutiao_" + fileName);
                // 合并成功后删除缓存中的最后上传时间，只存没上传完成的
                cacheManager.delete(FILE_UPLOAD, "lastUploadTime_" + fileName);
                // 合并成功后删除文件名称与该文件上传时生成的存储分片的临时文件夹的名称在缓存中的信息
                cacheManager.delete(FILE_UPLOAD, "fileName_" + fileName);

                json = JSONObject.toJSONString(resultMap);

            } catch (Exception e) {
                logger.error("file merge failure:", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = JSONObject.toJSONString(resultMap);
                return json;
            }

        } else if ("checkChunk".equals(param)) {
            // 检查当前分块是否上传成功
            String chunk = super.getPara("chunk");
            String chunkSize = super.getPara("chunkSize");
            // 文件上传的实时进度
            String jindutiao = super.getPara("jindutiao");
            try {
                //将当前进度存入缓存
                cacheManager.setObject(FILE_UPLOAD, "jindutiao_" + fileName, jindutiao);
                //将最后上传时间存入缓存
                String lastUploadTime = DateUtil.getTime();
                cacheManager.setObject(FILE_UPLOAD, "lastUploadTime_" + fileName, lastUploadTime);
                // 将文件名与该文件上传时生成的存储分片的临时文件夹的名称存入缓存
                // 文件上传时生成的存储分片的临时文件夹的名称由MD5和时间戳组成
                String tempFileName = ObjectUtils.toString(System.currentTimeMillis());
                if (ToolUtil.isEmpty(cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName))) {
                    cacheManager.setObject(FILE_UPLOAD, "fileName_" + fileName, fileMd5 + tempFileName);
                }

                File checkFile = new File(
                        savePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_"
                                + fileName) + File.separator + chunk);

                if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
                    // 上传过
                    json = "{\"ifExist\":1}";
                } else {
                    // 没有上传过
                    json = "{\"ifExist\":0}";
                }
            } catch (Exception e) {
                logger.error("file check failure:", e);
            }

        }
        return json;
    }

    /**
     * 检查上传的文件中是否有重名的文件
     *
     * @param fileName 文件名
     * @return 检查结果
     */
    @RequestMapping("/checkFileExist")
    @ResponseBody
    public JSON checkFileExist(String fileName, String pid) {
        JSONObject json = new JSONObject();
        List<String> docNameList = new ArrayList<String>();
        // 文件名称集合
        docNameList.add(fileName);
        List<String> nameList = idocInfoService.checkFileExist(docNameList, pid);
        json.put("result", nameList);
        return json;
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
        //  上传文件的大小（Bytes）
        String fileSize = super.getPara("fileSize");
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
        if (fileName.length() > 140) {
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
        List<FsFile> list = fsFileService.getInfoByMd5(fileMd5);
        if (list == null || list.size() == 0 || !"".equals(watermarkCompany)) {
            // 校验md5时提前判断剩余空间是否足够
            Map<String, Object> deptSpaceIsFreeMap = clientFilesService.checkEmpSpace(StringUtil.getString(fileSize));
            Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
            if (!deptSpaceIsFree) {
                //  存储空间不足
                Map<String, String> resultMap = new HashMap<String, String>();
                json.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                return json;
            }
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
                    watermarkUser, fileMd5, fileName, fsFile, fsFile.getFileSize(), shareable, userId,null);
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

    /**
     * 上传新版本时的方法
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 检查结果
     */
    @RequestMapping("/checkVersionMd5Exist")
    @ResponseBody
    public JSON checkVersionMd5Exist(String fileName, String categoryId,
                                     String downloadAble, String visible, String group, String person,
                                     String watermarkUser, String watermarkCompany, String shareable,
                                     String oldDocId) {
        JSONObject json = new JSONObject();
        String userId = jwtUtil.getSysUsers().getUserId();
        String fileMd5 = super.getPara("fileMd5");
        List<String> docNameList = new ArrayList<String>();
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex + 1);

        // 文件名称集合
        docNameList.add(fileName);
        if (fileName.length() > 140) {
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
        List<String> existIdList = idocInfoService.selectExistId(docNameList, categoryId);
        // 重名文件列表不为空&&与旧版本文档不重名，则说明有其他重名文件-->拦截
        if (existIdList != null && existIdList.size() != 0 && !oldDocId.equals(existIdList.get(0))) {
            List<String> nameList = idocInfoService.checkFileExist(docNameList, categoryId);
            if (nameList != null && nameList.size() != 0) {
                Map<String, String> resultMap = new HashMap<String, String>();
                json.put("name", nameList.get(0));
                json.put("code", DocConstant.UPLOADRESULT.FILEEXIST.getValue());
                return json;
            }
        }
        List<FsFile> list = fsFileService.getInfoByMd5(fileMd5);
        if (list == null || list.size() == 0 || !"".equals(watermarkCompany)) {

            json.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        } else {
            FsFile fsFile = list.get(0);
            Map<String, Object> deptSpaceIsFreeMap = clientFilesService.checkEmpSpace(StringUtil.getString(fsFile.getFileSize()));
            Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
            if (!deptSpaceIsFree) {
                //部门存储空间不足
                json.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                return json;
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
                return json;
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
        }

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
        } else if (suffix.equals(".wav")) {
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
                for (int j = 1; j <= currentCode.length() / 4 - 1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
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

    /**
     *
     */
    @RequestMapping("/getFileSize")
    @ResponseBody
    public long getFileSize(@RequestPart(value = "file", required = false) String fileName) {
        String filePath = breakdir;

        // 读取目录里的所有文件
        File f = new File(filePath + File.separator + cacheManager.getObject(FILE_UPLOAD, "fileName_" + fileName));
        // 排除目录,只要文件
        File[] fileArray = f.listFiles();
        // 转成集合，便于排序
        List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
        long fileSize = 0;
        for (File file : fileList) {
            fileSize = fileSize + file.length();
        }
        return fileSize;
    }

    @RequestMapping("/checkUploadState")
    @ResponseBody
    public boolean checkUploadState(String docId){
        return uploadService.checkUploadStateFromFast(docId);
    }
}
