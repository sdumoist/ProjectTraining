package com.jxdinfo.doc.manager.system.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.*;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.system.model.YYZCUserEntity;
import com.jxdinfo.doc.manager.system.service.SysUserService;
import com.jxdinfo.doc.manager.system.service.YYZCUserEntityService;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.doc.timer.constants.ApiURL;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @ClassName YYZCUploadController
 * @Description TODO
 * @Author yjs
 * @Date 2019/5/16 9:07
 * @Version 1.0
 */
@Controller
@RequestMapping("/YYZCUpload")
public class YYZCUploadController extends BaseController {
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;//存储加密算法抽象，密码需要通过这个进行加密后传值

    @Resource
    private DocInfoService docInfoService;

    /**
     * 在线用户历史 服务类
     */
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;
    /** 运营支撑接口服务类 */
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private DocConfigService docConfigService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private BusinessService businessService;
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private DocGroupService docGroupService;
    @Resource
    private ShareResourceService shareResourceService;
    @Resource
    private YYZCUserEntityService yyzcUserService;
    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(JqxUploadController.class);

    @Value("${isProject.using}")
    private boolean projectFlag;
    @Resource
    private SysStruMapper sysStruMapper;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private ESUtil esUtil;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    @Autowired
    private CacheToolService cacheToolService;
    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;

    @Autowired
    private FsFileService fsFileService;

    @Autowired
    private IFsFolderService iFsFolderService;

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    @Resource
    private ISysUsersService iSysUsersService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService idocInfoService;

    /**
     * 前台专题服务类
     */
    @Autowired
    protected FrontTopicService frontTopicService;
    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;
    @Value("${docbase.breakdir}")
    private String breakdir;
    @Value("${docbase.filedir}")
    private String tempdir;

    /**
     * 目录管理服务类
     */
    @Autowired
    private FrontFolderService frontFolderService;

    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public String mergeOrCheckVersionChunks(@RequestParam("file") MultipartFile file, String userId, String typeId,
                                            String fileName, String tags, String proId, String proName) {

        if (userId == null || "管理员".equals(userId)) {
            userId = "superadmin";
        }
        String parentId = "";
        if ("1".equals(typeId)) {
            parentId = "0601";
        } else if ("2".equals(typeId)) {
            parentId = "0602";
        } else if ("3".equals(typeId)) {
            parentId = "0603";
        } else if ("4".equals(typeId)) {
            parentId = "0604";
        } else if ("5".equals(typeId)) {
            parentId = "0605";
        } else {
            parentId = "0606";
        }
        FsFolder folder = iFsFolderService.getById(proId);
        if (folder != null) {
            FsFolder fsFolder = new FsFolder();
            cacheToolService.updateLevelCodeCache(userId);
            fsFolder.setFolderId(proId);
            fsFolder.setFolderName(proName);
            folder.setParentFolderId(parentId);
            String folderParentId = fsFolder.getParentFolderId();

            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = iFsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = iFsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);

            folder.setFolderName(proName);
            iFsFolderService.updateById(folder);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", proId).eq("author_id", userId));
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFoldAuthority.setAuthorId(userId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setFoldId(proId);
            docFoldAuthority.setIsEdit("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthorityService.save(docFoldAuthority);
        } else {
//            FsFolderParams fsFolderParams = new FsFolderParams();

            FsFolder fsFolder = new FsFolder();
            cacheToolService.updateLevelCodeCache(userId);
            fsFolder.setFolderId(proId);
            fsFolder.setFolderName(proName);
            fsFolder.setIsEdit("0");
            fsFolder.setVisibleRange("0");
            fsFolder.setParentFolderId(parentId);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setCreateUserId(userId);

            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = iFsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = iFsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
//            fsFolderParams.setFolderId(categoryId);
//            fsFolderParams.setFolderName(categoryName);
//            fsFolderParams.setUserId(userId);
            //保存目录信息
            iFsFolderService.save(fsFolder);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", proId).eq("author_id", userId));
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFoldAuthority.setAuthorId(userId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setFoldId(proId);
            docFoldAuthority.setIsEdit("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthorityService.save(docFoldAuthority);
            //保存权限信息
//            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
        }
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
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                return json;
            }
            String idStr;

            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            List<FsFile> listMd5 = fsFileService.getInfoByMd5(md5);
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
                    json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                    return json;
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
                resultMap.put("docId", docId);
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);

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
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                //拼装权限信息
                List<DocFileAuthority> list = new ArrayList<>();

                List<String> indexList = new ArrayList<>();
                //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                String[] groupArr = "allpersonflag,6a7206343e4246f9b21db680dbcf1516".split(",");
                String[] authorTypeStrGroup = "3,1".split(",");
                String[] operateTypeStrGroup = "0,1".split(",");
                for (int i = 0; i < groupArr.length; i++) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId(groupArr[i]);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrGroup[i]));
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeStrGroup[i]));
                    list.add(docFileAuthority);
                    indexList.add(groupArr[i]);
                }
                indexList.add(userId);

                cacheToolService.updateLevelCodeCache(userId);
                filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("docId", docId);
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                return json;
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                return json;
            }
        } catch (Exception e) {
            logger.error("IO Exception：", e);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        return json;
    }

    @RequestMapping(value = "/uploadFileForInterview")
    @ResponseBody
    public String uploadFileForMS(@RequestParam("file") MultipartFile file, String userId,
                                  String fileName, String password) {
        SysUsers sysUsers = this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).
                eq("USER_ACCOUNT", userId));
        if (sysUsers == null) {
            return null;
        }

        if ("管理员".equals(userId)) {
            userId = "superadmin";
        }
        String tags = null;
        String parentId = "yyzcmszl";
        String proId = "mszl";
        String proName = "面试资料";
        FsFolder folder = iFsFolderService.getById(proId);
        if (folder != null) {
            FsFolder fsFolder = new FsFolder();
            cacheToolService.updateLevelCodeCache(userId);
            fsFolder.setFolderId(proId);
            fsFolder.setFolderName(proName);
            folder.setParentFolderId(parentId);
            String folderParentId = fsFolder.getParentFolderId();

            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = iFsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = iFsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);

            folder.setFolderName(proName);
            iFsFolderService.updateById(folder);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", proId).eq("author_id", userId));
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFoldAuthority.setAuthorId(userId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setFoldId(proId);
            docFoldAuthority.setIsEdit("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthorityService.save(docFoldAuthority);
        } else {
//            FsFolderParams fsFolderParams = new FsFolderParams();

            FsFolder fsFolder = new FsFolder();
            cacheToolService.updateLevelCodeCache(userId);
            fsFolder.setFolderId(proId);
            fsFolder.setFolderName(proName);
            fsFolder.setIsEdit("0");
            fsFolder.setVisibleRange("0");
            fsFolder.setParentFolderId(parentId);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setCreateUserId(userId);

            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = iFsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = iFsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
//            fsFolderParams.setFolderId(categoryId);
//            fsFolderParams.setFolderName(categoryName);
//            fsFolderParams.setUserId(userId);
            //保存目录信息
            iFsFolderService.save(fsFolder);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", proId).eq("author_id", userId));
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFoldAuthority.setAuthorId(userId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setFoldId(proId);
            docFoldAuthority.setIsEdit("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthorityService.save(docFoldAuthority);
            //保存权限信息
//            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
        }
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
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                return json;
            }
            String idStr;

            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            List<FsFile> listMd5 = fsFileService.getInfoByMd5(md5);
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
                    json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                    return json;
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
                resultMap.put("docId", docId);
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);

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
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                //拼装权限信息
                List<DocFileAuthority> list = new ArrayList<>();

                List<String> indexList = new ArrayList<>();
                //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                String[] groupArr = "".split(",");
                String[] authorTypeStrGroup = "3,1".split(",");
                String[] operateTypeStrGroup = "0,1".split(",");
                for (int i = 0; i < groupArr.length; i++) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId(groupArr[i]);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrGroup[i]));
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeStrGroup[i]));
                    list.add(docFileAuthority);
                    indexList.add(groupArr[i]);
                }
                indexList.add(userId);

                cacheToolService.updateLevelCodeCache(userId);
                filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("docId", docId);
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
                return json;
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = com.alibaba.fastjson.JSONObject.toJSONString(resultMap);
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
        } else if (suffix.equals(".wav")) {
            contentType = "audio/wav";
            return contentType;
        } else if (suffix.equals(".avi")) {
            contentType = "video/avi";
            return contentType;
        } else if (suffix.equals(".ceb")) {
            contentType = "ceb";
            return contentType;
        } else {
            return null;
        }
    }


    /**
     * 下载
     */
    @RequestMapping("/preview")
    @ResponseBody
    public String preview(HttpServletRequest request, HttpServletResponse response, String userId, String password, String id) {
        SysUsers sysUsers = this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).
                eq("USER_ACCOUNT", userId).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        if (sysUsers != null) {
            String fileType = fsFileService.getById(id).getFileType();
            return (String) shareResourceService.newShareResourceYYZC(id, fileType, 0, 0, 0, request, userId).get("mapping_url");
        } else {
            return null;
        }
    }

    /**
     * 下载
     */
    @RequestMapping("/download")
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response, String userId, String id) {
        byte[] bytes = null;
        try {
            bytes = filesService.downloadYYZC(id, null, request, response, userId);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/@RequestMapping")
    @ResponseBody
    public void download(HttpServletRequest request, HttpServletResponse response, String userId, String id, String password) {
        SysUsers sysUsers = this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).
                eq("USER_ACCOUNT", userId).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        if (sysUsers != null) {
            byte[] bytes = null;
            try {
                bytes = filesService.downloadYYZC(id, null, request, response, userId);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除文件（级联删除）
     */
    @PostMapping(value = "/updatePassword")
    @ResponseBody
    public boolean updatePassword(@RequestBody(required = false) List<Map<String, String>> userpass) {
        Boolean isUpdated = false;
        if (userpass != null && userpass.size() > 0) {
            System.out.println(userpass.toString());
            for (int i = 0; i < userpass.size(); i++) {
                Map<String, String> map = userpass.get(i);
                String userId = map.get("username");
                String password = map.get("passwd");
                SysUsers sysUsers = (SysUsers) this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ACCOUNT", userId));
                if (sysUsers != null) {
                    sysUsers.setPassword(password);
                     iSysUsersService.updateById(sysUsers);
                    isUpdated= true;
                }
               YYZCUserEntity yyzcUser  =  this.yyzcUserService.getOne((new QueryWrapper<YYZCUserEntity>()).eq("USERID", userId));
                if (yyzcUser != null) {
                    yyzcUser.setPasswd(password);
                    yyzcUserService.updateById(yyzcUser);
                    isUpdated= true;
                }

            }
        }

        return isUpdated;
    }


    /**
     * 删除文件（级联删除）
     */
    @RequestMapping(value = "/deleteScope")
    @ResponseBody
    public int deleteScope(String ids, String userId) {
        cacheToolService.updateLevelCodeCache(userId);
        String[] strArr = ids.split(",");
        List list = new ArrayList();
        list.addAll(Arrays.asList(strArr));
        int num = fsFileService.deleteScopeYYZC(list, userId);

        for (String id : strArr) {
            Map map = new HashMap(1);
            //0为无效，1为有效
            map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
            esUtil.updateIndex(id, map);
        }
        return num;
    }


    /**
     * 删除文件（级联删除）
     */
    @RequestMapping(value = "/changeFolder")
    @ResponseBody
    public boolean changeFolder(String ids, String userId, String proId, String proName, String typeId) {
        if (userId == null || "管理员".equals(userId)) {
            userId = "superadmin";
        }
        String parentId = "";
        if ("1".equals(typeId)) {
            parentId = "0601";
        } else if ("2".equals(typeId)) {
            parentId = "0602";
        } else if ("3".equals(typeId)) {
            parentId = "0603";
        } else if ("4".equals(typeId)) {
            parentId = "0604";
        } else if ("5".equals(typeId)) {
            parentId = "0605";
        } else {
            parentId = "0606";
        }
        FsFolder folder = iFsFolderService.getById(proId);
        if (folder != null) {
            FsFolder fsFolder = new FsFolder();
            cacheToolService.updateLevelCodeCache(userId);
            fsFolder.setFolderId(proId);
            fsFolder.setFolderName(proName);
            fsFolder.setParentFolderId(parentId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = iFsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = iFsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs _folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolder.setFolderName(proName);
            fsFolder.setCreateUserId(userId);
            iFsFolderService.updateById(fsFolder);
            docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", proId).eq("author_id", userId));

            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            docFoldAuthority.setAuthorId(userId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setFoldId(proId);
            docFoldAuthority.setIsEdit("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthorityService.save(docFoldAuthority);
            return true;
        } else {
            return false;
        }

    }

    @RequestMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(@RequestParam String id,
                              @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "60") int pageSize, String order, String name,
                              String type, String nameFlag, String operateType, String userId) {
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        String isDesc = "0";
        if ("1".equals(order) || "3".equals(order) || "5".equals(order) || "7".equals(order)) {
            isDesc = "1";
        }
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
        String orderResult = (String) orderMap.get(order);
        Map<String, Object> result = new HashMap<>(5);
        List<FsFolderView> list = new ArrayList<>();
        int num = 0;
        //判断是否为子级目录（只能在子文件夹上传文件）
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        FsFolder fsFolder = fsFolderService.getById(id);
        String[] typeArr;
        if (type == null) {
            type = "0";
        }
        if ("0".equals(type)) {
            typeArr = null;
        } else {
            String typeResult = (String) typeMap.get(type);
            typeArr = typeResult.split(",");
        }

        name = StringUtil.transferSqlParam(name);
        FsFolder folder = fsFolderService.getById(id);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(operateType);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId, fsFolderParams);
        //获得下一级文件和目录
        String deptId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(userId).getDepartmentId());
        list = fsFolderService.getFilesAndFloder((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, levelCodeString, levelCode, isDesc, deptId,roleList);
        list = changeSize(list);

        //获得下一级文件和目录数量
        num = fsFolderService.getFilesAndFloderNum(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, levelCodeString, levelCode, deptId,roleList);
        //显示前台的文件数量
        int amount = fsFolderService.getFileNum(id, typeArr, name, listGroup, userId, adminFlag, operateType, levelCode, deptId,roleList);
        //判断是否有可编辑文件的权限

        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUploadClient(id, listGroup, userId, deptId);
            result.put("noChildPower", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPower", 2);
        }
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if (folderAmount == null || Integer.parseInt(folderAmount) < 4) {
            folderAmount = "4";
        }
        result.put("folderAmount", folderAmount);

        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditNewClient(id, listGroup, userId, deptId);
            result.put("noChildPowerFolder", isEdits);
        }
        if (userId.equals(fsFolder.getCreateUserId())) {
            result.put("noChildPowerFolder", 1);
        }
        result.put("userId", userId);
        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
        FsFolder fsfolder = new FsFolder();
        fsfolder = fsFolderService.getById(id);
        if (fsfolder.getOwnId() == null || "".equals(fsfolder.getOwnId())) {
            result.put("isOwn", "0");
        } else {
            result.put("isOwn", "1");
        }
//        result.put("isChild", isChild);
        result.put("amount", amount);

        return result;
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

/*
    */
/**
     * 删除文件（级联删除）
     *//*

    @RequestMapping(value = "/getMessage")
    @ResponseBody
    public String getMessage(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Map<String,String> result = new HashMap();
        String token = httpServletRequest.getParameter("token");
        String docId =  httpServletRequest.getParameter("docId");
        if (token == null) {
            throw new RuntimeException("无token，请重新登录");
        }else {
            final ApiClient client = new ApiClient();
            String url = ApiURL.GETTOKEN.getUrl() + "?token=" + token;
            final String userString = client.messageList(url.replaceAll(" ", "%20"));
            JSONObject jasonObject = JSONObject.fromObject(userString);
            Map<String ,Object> map = (Map)jasonObject;
            String errorCode = map .get("errorCode")+"";
            if(!"200".equals(errorCode)){
                return BaseController.REDIRECT + "/login";
            }else {

                String  keyword = "docbase";
                Map<String,String> data = (Map<String, String>) map.get("data");
                String userNameStr = data.get("userName");
                String passwordStr = data.get("password");
                byte[] nameDecode = AesUtil.parseHexStr2Byte(userNameStr);
                byte[] pwdDecode = AesUtil.parseHexStr2Byte(passwordStr);
                String userName ="";
                String password = "";
                try {
                     userName = new String(AesUtil.decrypt(nameDecode, keyword), "utf-8");
                     password = new String(AesUtil.decrypt(pwdDecode, keyword), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String flag = sysUserService.checkUser(userName,password).get(0);
                if("0".equals(flag)){
                    return BaseController.REDIRECT + "/login";
                }
                if(ShiroKit.getUser()==null|| !ShiroKit.getUser().getName().equals(userName)) {
                    Subject currentUser = ShiroKit.getSubject();

                    // 重置Session
                    // 获取session数据
                    Session session = currentUser.getSession();
                    final LinkedHashMap<Object, Object> attributes = new LinkedHashMap<Object, Object>();
                    final Collection<Object> keys = session.getAttributeKeys();
                    for (Object key : keys) {
                        final Object value = session.getAttribute(key);
                        if (value != null) {
                            attributes.put(key, value);
                        }
                    }
                    session.stop();
                    //String username = "传入用户名";//必填项
                    String passwordFlag = userName; //密码和用户名相同 ,必填项
                    passwordFlag = credentialsMatcher.passwordEncode(passwordFlag.getBytes());
                    String host = "192.168.1.1"; //必填项，和服务器上配置的  hussar.encryptType.secret-free-ip 一致

                    UsernamePasswordToken token2 = new UsernamePasswordToken(userName, passwordFlag.toCharArray(), host);
                    token2.setRememberMe(false);
                    currentUser.login(token2); //登录

                    // 登录成功后复制session数据
                    session = currentUser.getSession();
                    for (final Object key : attributes.keySet()) {
                        session.setAttribute(key, attributes.get(key));
                    }

                    // 在session中放值
                    ShiroUser shiroUser = ShiroKit.getUser();
                    ShiroKit.getSession().setAttribute("sessionFlag", true);
                    ShiroKit.getSession().setAttribute("shiroUser", shiroUser);
                    ShiroKit.getSession().setAttribute("userId", shiroUser.getAccount());
                    ShiroKit.getSession().setAttribute("projectFlag", projectFlag);
                    List<String> roleList = ShiroKit.getUser().getRolesList();
                    Integer adminFlag = CommonUtil.getAdminFlag(roleList);
                    session.setAttribute("adminFlag", adminFlag);
                    String wkurl = fsFolderService.getPersonPic(UserInfoUtil.getCurrentUser().getName());
                    session.setAttribute("url", wkurl);
                    //如果没登录，就跳转到登录请求
                    if (ToolUtil.isEmpty(ShiroKit.getUser())) {
                        return BaseController.REDIRECT + "/login";
                    }
                    iSysOnlineHistService.addRecord();
                    DocResourceLog docResourceLog = new DocResourceLog();
                    List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                    String id = UUID.randomUUID().toString().replace("-", "");
                    docResourceLog.setId(id);
                    Timestamp ts = new Timestamp(System.currentTimeMillis());
                    docResourceLog.setOperateTime(ts);
                    docResourceLog.setResourceType(0);
                    docResourceLog.setUserId(ShiroKit.getUser().getId());
                    docResourceLog.setOperateType(11);
                    docResourceLog.setValidFlag("1");
                    resInfoList.add(docResourceLog);
                    docInfoService.insertResourceLog(resInfoList);//添加登录
                    String userId = ShiroKit.getUser().getId();
                    cacheToolService.updateLevelCodeCache(userId);

                }

            }
        }
    }

*/

}
