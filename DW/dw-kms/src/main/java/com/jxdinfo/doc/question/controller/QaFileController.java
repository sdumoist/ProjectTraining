package com.jxdinfo.doc.question.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.question.model.QaFile;
import com.jxdinfo.doc.question.service.QaFileService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/qaFile")
public class QaFileController extends BaseController {

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    @Value("${docbase.filedir}")
    private String tempdir;
    /**
     * FAST操作接口
     */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * FAST操作接口
     */
    @Autowired
    private QaFileService qaFileService;


    @RequestMapping(value = "/upload")
    @ResponseBody
    public String uploadFileForMS(@RequestParam("file") MultipartFile file, Integer type,String fileName) {

        // xss过滤
        fileName = XSSUtil.xss(fileName);

        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();

        //InputStream sbs = new ByteArrayInputStream(fileBytes);
        ByteArrayOutputStream baos = null;

        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());

        // 截取文件名的后缀名
        String json = "";

        File outputFile = new File(tempdir + File.separator + fileName);

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

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int pointIndex = fileName.lastIndexOf(".");
        String suffix = fileName.substring(pointIndex).toLowerCase();
        String path = "";
        if (fastdfsUsingFlag) {
            //启用FASTDFS时将文件上传到服务器
            try {
                path = fastdfsService.uploadFile(outputFile);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        } else {
            path = outputFile.getPath();
        }
        QaFile qaFile = new QaFile();
        qaFile.setId(UUID.randomUUID().toString().replace("-", ""));
        qaFile.setFile(path);
        qaFile.setLocation(outputFile.getPath());
        qaFile.setType(type.toString());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        qaFile.setUploadTime(ts);
        qaFile.setUploadUserId(userId);
        qaFile.setUploadUserName(userName);
        qaFileService.saveOrUpdate(qaFile);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        return json;
    }

    /**
     * 新增问题上传
     * @param file 文件
     * @param type 问答类型 1 提问 2 回答
     * @param fileName 文件名称 从文件中取，可以为空
     * @return
     */
    @RequestMapping(value = "/uploadJson")
    @ResponseBody
    public JSON uploadJson(@RequestParam("file") MultipartFile file, Integer type,String fileName) {

        // xss过滤
        fileName = XSSUtil.xss(fileName);

        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();

        // 返回值
        JSONObject json = new JSONObject();

        //InputStream sbs = new ByteArrayInputStream(fileBytes);
        ByteArrayOutputStream baos = null;

        if(file != null){
            fileName = file.getOriginalFilename();
        }
        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());


        File outputFile = new File(tempdir + File.separator + fileName);

        try {
            if (!outputFile.getParentFile().exists()) {
                // 路径不存在,创建
                outputFile.getParentFile().mkdirs();
            }
            outputFile.createNewFile();
        } catch (IOException e) {
            Map<String, String> dataMap = new HashMap<>();
            json.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
            json.put("msg", "上传错误");
            dataMap.put("src",null);
            json.put("data", dataMap);
            return json;
        }
        String idStr;

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int pointIndex = fileName.lastIndexOf(".");
        String suffix = fileName.substring(pointIndex).toLowerCase();
        String path = "";
        if (fastdfsUsingFlag) {
            //启用FASTDFS时将文件上传到服务器
            try {
                path = fastdfsService.uploadFile(outputFile);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        } else {
            path = outputFile.getPath();
        }
        QaFile qaFile = new QaFile();
        qaFile.setId(UUID.randomUUID().toString().replace("-", ""));
        qaFile.setFile(path);
        qaFile.setLocation(outputFile.getPath());
        qaFile.setType(type.toString());
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        qaFile.setUploadTime(ts);
        qaFile.setUploadUserId(userId);
        qaFile.setUploadUserName(userName);
        qaFileService.saveOrUpdate(qaFile);

        Map<String, String> dataMap = new HashMap<>();
        json.put("code", "0");
        json.put("msg", "上传成功");
        dataMap.put("src",path);
        dataMap.put("location",outputFile.getPath());
        dataMap.put("fileId",qaFile.getId());
        json.put("data", dataMap);
        return json;
    }

    /**
     * 问题回答图片上传
     * @param file 文件
     * @return
     */
    @RequestMapping(value = "/uploadPic")
    @ResponseBody
    public JSON uploadPic(@RequestParam("file") MultipartFile file) {
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();

        // 返回值
        JSONObject json = new JSONObject();

        //InputStream sbs = new ByteArrayInputStream(fileBytes);
        ByteArrayOutputStream baos = null;

        String fileName = null;
        if(file != null){
            fileName = file.getOriginalFilename();
        }
        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());


        File outputFile = new File(tempdir + File.separator + fileName);

        try {
            if (!outputFile.getParentFile().exists()) {
                // 路径不存在,创建
                outputFile.getParentFile().mkdirs();
            }
            outputFile.createNewFile();
        } catch (IOException e) {
            Map<String, String> dataMap = new HashMap<>();
            json.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
            json.put("msg", "上传错误");
            dataMap.put("src",null);
            dataMap.put("title",fileName);
            json.put("data", dataMap);
            return json;
        }
        String idStr;

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int pointIndex = fileName.lastIndexOf(".");
        String suffix = fileName.substring(pointIndex).toLowerCase();
        String path = "";
        if (fastdfsUsingFlag) {
            //启用FASTDFS时将文件上传到服务器
            try {
                path = fastdfsService.uploadFile(outputFile);
            } catch (ServiceException e) {
                e.printStackTrace();
            }
        } else {
            path = outputFile.getPath();
        }
        QaFile qaFile = new QaFile();
        qaFile.setId(UUID.randomUUID().toString().replace("-", ""));
        qaFile.setFile(path);
        qaFile.setLocation(outputFile.getPath());
        qaFile.setType("2"); // 回答
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        qaFile.setUploadTime(ts);
        qaFile.setUploadUserId(userId);
        qaFile.setUploadUserName(userName);
        qaFileService.saveOrUpdate(qaFile);

        Map<String, String> dataMap = new HashMap<>();
        json.put("code", "0");
        json.put("msg", "上传成功");
        try {
            dataMap.put("src", "/preview/list?fileId=" + URLEncoder.encode(path, "UTF-8"));
        }catch (Exception e){
            json.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
            json.put("msg", "上传错误");
            dataMap.put("src",null);
            dataMap.put("title",fileName);
            json.put("data", dataMap);
            return json;
        }
        dataMap.put("title",fileName);
        dataMap.put("location",outputFile.getPath());
        dataMap.put("fileId",qaFile.getId());
        json.put("data", dataMap);
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

}
