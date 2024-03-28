package com.jxdinfo.doc.manager.middlegroundConsulation.controller;

import com.alibaba.fastjson.JSON;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.middlegroundConsulation.dao.ConsulationAttachmentMapper;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulationAttachment;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.ConsulationAttachmentService;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.MiddlegroundConsulationService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.regex.Pattern;

/**
 * @ClassName: ConsulationApplyController
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2020/2/24
 * @Version: 1.0
 */

@Controller
@RequestMapping("/Consulation")
public class ConsulationApplyController {
    @Resource
    private ISysUsersService sysUsersService;
    @Resource
    private MiddlegroundConsulationService middlegroundConsulationService;
    @Resource
    private ConsulationAttachmentService consulationAttachmentService;
    /**
     * es搜索 服务类
     */
    @Autowired
    private ESUtil esUtil;


    /**
     * 文件系统-文件 服务类
     */
    @Autowired
    private FsFileService fsFileService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;
    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    /**
     * 缓存工具类
     */
    @Autowired
    private CacheToolService cacheToolService;


    /**
     * 用户接口
     */
    @Resource
    private SysStruMapper sysStruMapper;
    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;

    /**
     * 文件基础业务层
     */
    @Resource
    private DocInfoService docInfoService;

    /**
     * 临时文件夹
     */
    @Value("${docbase.filedir}")
    private String tempdir;

    /**
     * 成果汇总页面点击列表详情进入的页面
     *
     * @param model
     * @return
     * @Author yjs
     */
    @GetMapping("/ConsulationView")
    public String consulationView(Model model, String consulationId) {
        MiddlegroundConsulation middlegroundConsulation = middlegroundConsulationService.selectById(consulationId);
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("isShare", "0");
        List<String> roleList = ShiroKit.getUser().getRolesList();

        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("consulationId",consulationId);

        model.addAttribute("middlegroundConsulation", middlegroundConsulation);

        List<MiddlegroundConsulationAttachment> list = consulationAttachmentService.getAttachmentList(consulationId);
        int listSize = 0;
        if (list.size() != 0) {
            listSize = list.size();
        }
        model.addAttribute("listSize", listSize);
        model.addAttribute("consulationList", list);
        return "/doc/manager/middlegroundConsulation/consulation-view-from.html";
    }
    /**
     * 附件上传 选择文件后进入该方法
     *
     * @param file        文件
     * @return String 返回值
     * @Author yjs
     */
    @PostMapping("/consulationApplySave")
    @ResponseBody
    public String consulationApplySave(@RequestParam("file") MultipartFile file,String consulationId,String type) {
        ByteArrayOutputStream baos = null;
        String fileName = file.getOriginalFilename();
        String userId = ShiroKit.getUser().getId();
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
        String suffix = fileName.substring(pointIndex + 1).toLowerCase();
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            resultMap.put("title", fileName);
            return JSON.toJSONString(resultMap);
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            resultMap.put("title", fileName);
            return JSON.toJSONString(resultMap);
        }
        if ("0".equals(type)){
            String typeList[] = {"doc","docx","txt","ppt","pptx","pdf"};
            boolean flag = false;
            for (String s:typeList){
                if (s.contains(suffix.toLowerCase())) {
                    flag = true;
                }
            }
            if (!flag){
                Map<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
                resultMap.put("title", fileName);
                return JSON.toJSONString(resultMap);
            }
        }else {
            String typeList[] = {"mp3","real","cd","ogg","asf","wav","ape","module","midi"};
            boolean flag = false;
            for (String s:typeList){
                if (s.contains(suffix.toLowerCase())) {
                    flag = true;
                }
            }
            if (!flag){
                Map<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
                resultMap.put("title", fileName);
                return JSON.toJSONString(resultMap);
            }
        }

        suffix = fileName.substring(pointIndex).toLowerCase();

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

                //秒传
                cacheToolService.updateLevelCodeCache(userId);
                String docId = filesService.uploadFastComponent(consulationId, "0", "0", null, null,
                        null, md5, fileName, fsFile, fsFile.getFileSize(), "1", userId, null);
                if (docId == null) {
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    resultMap.put("title", fileName);
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }

                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(docId, map);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
                resultMap.put("docId", docId);
                resultMap.put("title", fileName);
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

                docInfo.setFoldId(consulationId);
                docInfo.setDocType(suffix);
                docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                docInfo.setReadNum(0);
                docInfo.setDownloadNum(0);
                docInfo.setValidFlag("0");
                docInfo.setAuthority("1");
                docInfo.setSetAuthority("0");
                docInfo.setVisibleRange(Integer.parseInt("1"));
                //ValidFlag 默认1 有效
                docInfo.setValidFlag("0");
                // shareFlag 默认1 可分享
                docInfo.setShareFlag("0");
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
                docResourceLog.setAddressIp(HttpKit.getIp());
                docResourceLog.setOperateType(0);
                resInfoList.add(docResourceLog);
                //拼装权限信息
                List<DocFileAuthority> list = new ArrayList<>();

                List<String> indexList = new ArrayList<>();
                //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                String[] groupArr = "allpersonflag".split(",");
                String[] authorTypeStrGroup = "3".split(",");
                String[] operateTypeStrGroup = "1".split(",");
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
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(docId, map);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("docId", docId);
                resultMap.put("title", fileName);
                json = JSONObject.toJSONString(resultMap);
                return json;
            } catch (IOException e) {
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                resultMap.put("title", fileName);

                json = JSONObject.toJSONString(resultMap);
                return json;
            }
        } catch (Exception e) {
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        return json;
    }
    /**
     * 根据ID获取组件
     *
     * @param id 组件ID
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/getConsulation")
    @ResponseBody
    public JSON getConsulation(String consulationId) {
        MiddlegroundConsulation middlegroundConsulation = middlegroundConsulationService.selectById(consulationId);
        JSONObject json = new JSONObject();
        json.put("middlegroundConsulation", middlegroundConsulation);
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
        } else if (suffix.equals(".png") || suffix.equals(".gif") || suffix.equals(".jpg") ||
                suffix.equals(".bmp")) {
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
}
