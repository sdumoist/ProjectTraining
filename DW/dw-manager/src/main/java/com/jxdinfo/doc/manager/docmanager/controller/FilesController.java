package com.jxdinfo.doc.manager.docmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocUploadParams;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by lorne on 2017/9/26
 *
 * @author XuXinYing
 */
@RestController
@RequestMapping("/files")
public class FilesController {

    /** 日志 */
    private static Logger LOGGER = LoggerFactory.getLogger(FilesController.class);

    /** 是否启用fastdfs存储文件 */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    /** 文档信息  */
    @Autowired
    private DocInfoService docInfoService;

    /** 文件处理 */
    @Autowired
    private FilesService filesService;

    /** 缓存工具类接口 */
    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 文件上传
     *
     * @param file             上传的文件
     * @param uploadParams     上传的附加信息
     * @return 上传结果
     */
    @PostMapping("/uploadData")
    @ResponseBody
    public String uploadData(MultipartFile file, DocUploadParams uploadParams) {
        String uploadData = uploadParams.getUploadData();
        String userId = ShiroKit.getUser().getId();
        //上传文件
        Map<String, Object> resultMap = new HashMap<String, Object>(16);

        //文档信息为空 ： 前台文件校验不通过时会走该逻辑
        if (uploadData == null) {
            resultMap.put("code", DocConstant.UPLOADRESULT.EMPTY.getValue());
            resultMap.put("successful", 0);
            return JSON.toJSONString(resultMap);
        }

        Double releaseSize = 0d;
        String docId = StringUtil.getUUID();

        try {
            uploadParams.setDocId(docId);
            //上传文件的全部方法，包括上传至服务器，转化pdf，添加索引，doc_info和fs_file表中添加数据
            resultMap = filesService.upload(file, uploadParams);

            Object obj = resultMap.get("releaseSize");
            if (obj == null){
                //空间不足
                resultMap.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
                resultMap.put("successful", 0);
                return JSON.toJSONString(resultMap);
            } else {
                releaseSize = StringUtil.getDouble(obj);
            }
        } catch (Exception e) {
            LOGGER.error("文件上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
            resultMap.put("successful", 0);
            return JSON.toJSONString(resultMap);
        }

        // 上传文件的文档ID集合
        resultMap.put("docId", new ArrayList<String>(){
            private static final long serialVersionUID = 1L; {
                add(docId);
            }
        });

        // 返回上传结果
        String resultKey = "code";

        // 返回结果的KEY值
        if (resultMap.get(resultKey) == null) {
            resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
            resultMap.put("successful", DocConstant.NUMBER.ONE.getValue());
        } else if (resultMap.get(resultKey) == DocConstant.UPLOADRESULT.FAIL.getValue()){
            //releaseSize
            cacheToolService.updateDeptUsedSpace(userId, releaseSize);
        }
        return JSON.toJSONString(resultMap);
    }

    /**
     * 不包含积分操作的后台文件批量下载
     *
     * @param docIds   文件标识
     * @param response 响应
     * @param docName  文档名称
     * @param request  系统请求
     */
    @PostMapping("/fileDownNew")
    public Object fileDownNew(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) {
        try{
            filesService.download(docIds, docName, request, response);
        } catch (Exception e){
            LOGGER.error("文件下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            return "error";
        }
        return "success";
    }
    @RequestMapping("/fileDownNew1")
    @ResponseBody
    public JSONObject fileDownNew1(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> map = new HashMap();
        try {
            map = filesService.download1(docIds, docName, request, response);
        } catch (Exception e) {
            LOGGER.error("文件下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            return null;
        }
        JSONObject itemJSONObj = JSONObject.parseObject(JSON.toJSONString(map));
        return itemJSONObj;
    }

    @RequestMapping("/returnFile")
    @ResponseBody
    public void returnFile(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,String> map){
        String filePath = map.get("filePath");
        String zipName = map.get("zipName");
        try {
            int a = 0;
            filesService.returnFile(request,response,filePath,zipName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 上传项目logo
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/uploadProjectLogo")
    @ResponseBody
    public JSONObject uploadProjectLogo(MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            result = filesService.uploadProjectLogo(file);
        } catch (Exception e) {
            LOGGER.error("logo上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            result.put("code", "1");
            return result;
        }
        return result;
    }

    /**
     * 上传使用手册
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/uploadHandbookRoot")
    @ResponseBody
    public JSONObject uploadHandbookRoot(MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            result = filesService.uploadHandbookRoot(file);
        } catch (Exception e) {
            LOGGER.error("logo上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            result.put("code", "1");
            return result;
        }
        return result;
    }

    /**
     * 上传使用手册
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/uploadHandbook")
    @ResponseBody
    public JSONObject uploadHandbook(MultipartFile file) {
        JSONObject result = new JSONObject();
        try {
            result = filesService.uploadHandbook(file);
        } catch (Exception e) {
            LOGGER.error("logo上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            result.put("code", "1");
            return result;
        }
        return result;
    }


    /**
     * 下载项目logo
     */
    @PostMapping("/downProjectLogo")
    public Object downProjectLogo(HttpServletRequest request, HttpServletResponse response) {
        try {
            filesService.downloadProjectLogo(request, response);
        } catch (Exception e) {
            LOGGER.error("logo下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    /**
     * 下载使用手册
     */
    @PostMapping("/downHandbook")
    public Object downHandbook(HttpServletRequest request, HttpServletResponse response) {
        try {
            filesService.downHandbook(request, response);
        } catch (Exception e) {
            LOGGER.error("logo下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    /**
     * 检查上传的文件中是否有重名的文件
     *
     * @param uploadData 上传的信息
     * @return 检查结果
     */
    @PostMapping("/checkFileExist")
    @ResponseBody
    public JSON checkFileExist(String uploadData, String pid) {
        List<Map<String, Object>> dataList = JSON.parseObject(uploadData,
                new TypeReference<List<Map<String, Object>>>() {
                });
        // 上传的附加信息集合
        JSONObject json = new JSONObject();
        List<String> docNameList = new ArrayList<String>();
        // 文件名称集合
        for (Map<String, Object> map : dataList) {
            String filename = map.get("docName").toString();
            String extendName = filename.substring(filename.lastIndexOf("."));
            String needCheckFileName = map.get("title").toString() + extendName;
            docNameList.add(needCheckFileName);
        }
        List<String> nameList = docInfoService.checkFileExist(docNameList, pid);
        json.put("result", nameList);
        return json;
    }

    /**
     * @return
     * @Title: getLoginUser
     * @author: XuXinYing
     */
    @PostMapping("/getLoginUser")
    @ResponseBody
    public JSON getLoginUser() {
        JSONObject json = new JSONObject();
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        json.put("userId", userId);
        json.put("userName", userName);
        return json;
    }
    /**
     * 不包含积分操作的后台文件批量下载
     *
     * @param docIds   文件标识
     * @param response 响应
     * @param docName  文档名称
     * @param request  系统请求
     */
    @PostMapping("/fileDownNewByShare")
    public Object fileDownNewByShare(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) {
        try{
            filesService.downloadByShare(docIds, docName, request, response);
        } catch (Exception e){
            LOGGER.error("文件下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    /**
     * 获取服务器地址
     * @param fileId 文档ID
     * @return 服务器地址等信息集合
     */
    @PostMapping("/getServerAddress")
    @ResponseBody
    public JSON getServerAddress(String fileId) {
        JSONObject json = new JSONObject();
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
//        // 获取token值
//        Map<String, String> tokenMap = filesService.getToken(userId,userName);
        // 获取服务地址
        String serverAddress = filesService.getServerName(fileId);
//        json.put("tokenMap", tokenMap);
        // 获取当前时间
        long sendTime = System.currentTimeMillis();
        json.put("userId", userId);
        json.put("userName", userName);
        json.put("sendTime", sendTime);
        json.put("serverAddress", serverAddress);
        return json;
    }
}
