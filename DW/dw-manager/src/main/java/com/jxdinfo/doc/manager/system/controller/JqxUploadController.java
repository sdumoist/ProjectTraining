package com.jxdinfo.doc.manager.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.controller.BreakpointUploadController;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

import static com.jxdinfo.doc.common.constant.CacheConstant.FILE_UPLOAD;

@Controller
@RequestMapping("/JqxUpload")
public class JqxUploadController extends BaseController {
    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(JqxUploadController.class);

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /**
     * 缓存管理
     */
    @Autowired
    private HussarCacheManager cacheManager;

    @Autowired
    private CacheToolService cacheToolService;
    /** 配置信息服务层 */
    @Resource
    private DocConfigureService docConfigureService;

    @Autowired
    private FsFileService fsFileService;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService idocInfoService;

    /** 前台专题服务类 */
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

    /**目录管理服务类*/
    @Autowired
    private FrontFolderService frontFolderService;


    @RequestMapping(value = "/uploadFile")
    @ResponseBody
    public String mergeOrCheckVersionChunks(@RequestParam("file") MultipartFile file){
        String userName=super.getPara("userName");
        String userId="";
        if(userName==null||"admin".equals(userName)){
            userId = "superadmin";
        }else{
            userId=userName;
        }

        String topicId=super.getPara("topicId");
        String categoryId=super.getPara("categoryId");
        String fileName=super.getPara("fileName");
        ByteArrayOutputStream baos = null;
        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        //转MD5
        String md5 =MD5Util.getFileMD5(md5InputStream);
        String json = "";
        int pointIndex = fileName.lastIndexOf(".");
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        if(fileName.length()>64){
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            return JSON.toJSONString(resultMap);
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if( Pattern.compile(regex).matcher(fileName).find()==false){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code",DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            return JSON.toJSONString(resultMap);
        }
        List<DocConfigure> typeList =  docConfigureService.getConfigure();
        if(typeList.get(0).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code",DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            return JSON.toJSONString(resultMap);
        }

        List<String> nameList = idocInfoService.checkFileExist(docNameList, categoryId);
        if (nameList != null&& nameList.size()!=0){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("name",nameList.get(0));
            resultMap.put("code",DocConstant.UPLOADRESULT.FILEEXIST.getValue());
            resultMap.put("docId",idocInfoService.selectExistId(docNameList,categoryId).get(0));
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
            List<FsFile> listMd5 = fsFileService.getInfoByMd5(md5);
            if (listMd5 != null && listMd5.size() != 0) {
                FsFile fsFile = listMd5.get(0);
//                Map<String, Object> deptSpaceIsFreeMap = filesService.checkDeptSpace(StringUtil.getString(fsFile.getFileSize()));

                //秒传
                cacheToolService.updateLevelCodeCache(userId);
                String docId=    filesService.uploadFastJqx(categoryId, "0", "1", null, null,
                        null,md5,fileName,fsFile,fsFile.getFileSize(),"1",userId,topicId);
                if(docId==null){
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
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
                idStr=docId;
                docInfo.setDocId(docId);
                //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
                docInfo.setFileId(outputFile.getName());
                docInfo.setUserId(userId);
                docInfo.setAuthorId(userId);
                docInfo.setContactsId(userId);
                docInfo.setCreateTime(ts);


                docInfo.setFoldId(categoryId);
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
                if(("555dd41471c2461aa3391af9e8282fc1".equals(categoryId))
                        ||"1a5168f3a50f4f32b57bf454c5895457".equals(categoryId)
                        ||"2d8ed2e02fc34be2a587f7e68a848e91".equals(categoryId)
                        ||"339d8cd61b3746e1839ebf725aacb824".equals(categoryId)
                        ||"5e5d93580f3a443eb3057627be68ffa6".equals(categoryId)
                        ||"5f7bf94d27224db7a41e6c694def6e46".equals(categoryId)
                        ||"784ba4775a734014a8f251783606f23d".equals(categoryId)
                        ||"795aa5765d9d49e4805a88ac7fefbc8d".equals(categoryId)
                        ||"831f8b19eee44ad094ae21d7a7242578".equals(categoryId)
                        ||"85eb0d9b8b1a428f847e7f5bc8ff43b9".equals(categoryId)
                        ||"9670e7b36ce549ffa074f6592bbda284".equals(categoryId)
                        ||"a18c3d4a7fbe4de5868bb6b45eaad1e2".equals(categoryId)
                        ||"a63df52c36584f8c9378ebc801499977".equals(categoryId)
                        ||"bbb99e2f9a15474cbdcaba2d8da63db9".equals(categoryId)
                        ||"c4960ef211d745a497a31e7e9e2c3db0".equals(categoryId)
                        ||"add316b125a84d519578694955a5af36".equals(categoryId)
                        ||"caf4bd7d5f3e42a9aff6beae6154576f".equals(categoryId)
                        ||"e4daf328be6648f99281fb33b282bf62".equals(categoryId)
                        ||"645e6a2e155740949cdb81df3fa350b1".equals(categoryId)
                        ||"8c56ad616c674d7eba191e79f0cda882".equals(categoryId)){
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId("allpersonflag");
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(3);
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(1);
                    list.add(docFileAuthority);
                    indexList.add("allpersonflag");
                    indexList.add(userId);
                }else if("6b46e675e46a4a69a9c2fc93af143aae".equals(categoryId)){
                    String[] groupArr = "6091a4f767e24ba29f87719bd1b2cef8,allpersonflag,2817292a04944e3c96a800c7ad2c3857,6a7206343e4246f9b21db680dbcf1516,337babeee7a8453290146c0ce8a96478".split(",");
                    String[] authorTypeStrGroup = "1,3,1,1,1".split(",");
                    String[] operateTypeStrGroup ="1,0,1,1,1".split(",");
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
                }else if("0404".equals(categoryId)){
                    String[] groupArr = ("8d6e50d29336428f8964c42248510c6d,6091a4f767e24ba29f87719bd1b2cef8," +
                            "6a7206343e4246f9b21db680dbcf1516,52ed3715582245a48ad633052ce289d3,30e66bdf2a66453cba0d38e5e7af4ae7").split(",");
                    String[] authorTypeStrGroup = "1,1,1,1,1".split(",");
                    String[] operateTypeStrGroup ="0,0,0,0,0".split(",");
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
                }else if("0101".equals(categoryId)){
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
                }else if("0403".equals(categoryId)){
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
                }  else if("508bf3dd2d4b408684313051a487fdcd".equals(categoryId)){
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
                }else if("0304".equals(categoryId)) {
                    String[] personArr = "公司领导".split(",");
                    String[] personOrganArr = "5A154DE6E1F94FBA9D7C48A11EF7F1C6".split(",");
                    String[] authorTypeStrPerson = "2".split(",");
                    String[] operateTypeStrPerson = "0".split(",");
                    for (int i = 0; i < personArr.length; i++) {
                        DocFileAuthority docFileAuthority = new DocFileAuthority();
                        docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                        docFileAuthority.setAuthorId(personArr[i]);
                        //操作者类型（0：userID,1:groupID,2:roleID）
                        docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrPerson[i]));
                        docFileAuthority.setFileId(docId);
                        docFileAuthority.setAuthority(Integer.parseInt(operateTypeStrPerson[i]));
                        docFileAuthority.setOrganId(personOrganArr[i]);
                        list.add(docFileAuthority);
                        if(StringUtil.getInteger(authorTypeStrPerson[i])==0){
                            indexList.add(personArr[i]);
                        }
                        if(StringUtil.getInteger(authorTypeStrPerson[i])==2){
                            indexList.add(personOrganArr[i]);
                        }
                    }
                }else if("900f060aa5ad49e981da10f5d619ec2a".equals(categoryId)) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId("allpersonflag");
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(3);
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(1);
                    list.add(docFileAuthority);
                    indexList.add("allpersonflag");
                    indexList.add(userId);

                } else if("0301".equals(categoryId)) {
                    String[] groupArr = "fcfcf9b538444c719916ab339b5b30e1".split(",");
                    String[] authorTypeStrGroup = "1".split(",");
                    String[] operateTypeStrGroup ="0".split(",");
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

                }else if("1085dd96c43f4036b0654b66e9f163af".equals(categoryId)) {
                    String[] groupArr = "6a7206343e4246f9b21db680dbcf1516,30e66bdf2a66453cba0d38e5e7af4ae7".split(",");
                    String[] authorTypeStrGroup = "1,1".split(",");
                    String[] operateTypeStrGroup ="1,1".split(",");
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

                }else if("8bea2cb2184c44c4a296710bad0d9673".equals(categoryId)) {
                    indexList.add(userId);

                }else if("0521e2de1a93438d9fa5ecd512fc0105".equals(categoryId)) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId("allpersonflag");
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(3);
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(1);
                    list.add(docFileAuthority);
                    indexList.add("allpersonflag");
                    indexList.add(userId);

                }else if("5c8cee3afd0f47aba7ed4893d6ed9e66".equals(categoryId)) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId("allpersonflag");
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(3);
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(1);
                    list.add(docFileAuthority);
                    indexList.add("allpersonflag");
                    indexList.add(userId);

                }

                else if("0504".equals(categoryId)||"050402".equals(categoryId)||"050403".equals(categoryId)||"7d9f267b319741ca90844efc7108db87".equals(categoryId)) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId("allpersonflag");
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(3);
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(1);
                    list.add(docFileAuthority);
                    indexList.add("allpersonflag");
                    indexList.add(userId);

                }else{
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
                cacheToolService.updateLevelCodeCache(userId);
                filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
                TopicFile topicFile = new TopicFile();
                topicFile.setTopicFileId(UUID.randomUUID().toString().replaceAll("-", ""));
                topicFile.setDocId(docId);
                topicFile.setSpecialTopicId(topicId);
                String currentCode = this.sysIdtableService.getCurrentCode("TOPIC_FILE_NUM", "doc_special_topic_files");
                int bigNum = Integer.parseInt(currentCode);
                topicFile.setShowOrder(bigNum);
                List<TopicFile> topicList = new ArrayList();
                topicList.add(topicFile);
                if (topicList != null && topicList.size() > 0) {
                    iTopicDocManagerService.saveTopicDoc(topicId, topicList);
                }
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
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
        } else if (suffix.equals(".png") || suffix.equals(".gif")|| suffix.equals(".jpg")|| suffix.equals(".bmp")) {
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
        }else if (suffix.equals(".wav")) {
            contentType = "audio/wav";
            return contentType;
        }else if (suffix.equals(".avi")) {
            contentType = "video/avi";
            return contentType;
        }else if (suffix.equals(".ceb")) {
            contentType = "ceb";
            return contentType;
        }else{
            return  null;
        }
    }

    /**
     * 跳转到文件系统-文件首页
     */
    @RequestMapping("/getTopic")
    @ResponseBody
    public void getTopic(HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        List<SpecialTopic> topicList = frontTopicService.getTopicList(0, 200);
        Map<String, Object> resultMap = new HashMap<>();
        String json="";
        resultMap.put("total", topicList.size());
        resultMap.put("data",topicList);
        response.getWriter().write(JSON.toJSONString(resultMap));
    }

    /**
     * 跳转到文件系统-文件首页
     */
    @RequestMapping("/getFolderTree")
    @ResponseBody
    public List getFolderTree(String id,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String type="0";
        return frontFolderService.getTreeDataLazyJqx(id,type);
    }
}