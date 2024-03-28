package com.jxdinfo.doc.manager.esdicmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.exception.HussarException;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

@Controller
@RequestMapping("/ESDicController")
public class ESDicController {
    @Value("${docbase.mydic}")
    private String path;

    @RequiresPermissions("ESDicController:view")
    @RequestMapping(value = "/view")
    public String view() {
        return "/doc/manager/esdicmanager/esDicView.html";
    }

    /**
     * 上传文件
     * @Title: upload
     * @author: WangBinBin
     * @param file 上传的文件
     * @return  文件名
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    @ResponseBody
    public JSONObject upload(@RequestPart("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {

            String fileSavePath = path+"my.dic";
            File oldFile = new File(fileSavePath);
            if(oldFile.exists()){
                oldFile.delete();
            }
            File newFile = new File(fileSavePath);
            FileUtils.copyInputStreamToFile(file.getInputStream(), newFile);
        } catch (IOException e) {
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        }
        JSONObject json = new JSONObject();

        json.put("code", "0");
        json.put("msg", "");
        return json;
    }
    @RequestMapping(path = "/download")
    public String upload(HttpServletRequest request, HttpServletResponse response) {

            String  fileName ="my.dic";;

            FileInputStream input = null;
            String fileSavePath = path+"my.dic";
            // 下载*/
            try {
                File file = new File(fileSavePath);
                if(!file.exists()){
                    return "error";
                }
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = fis.read(b)) != -1)
                {
                    bos.write(b, 0, n);
                }
                fis.close();
                bos.close();
                byte[] bytes = bos.toByteArray();
                String realName = "";

                //处理FireFox下载时文件名未转化成中文问题
                String userAgent = request.getHeader("User-Agent");
                if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                    // name.getBytes("UTF-8")处理safari的乱码问题
                    fileName ="my.dic";
                    byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                    // 各浏览器基本都支持ISO编码
                    realName = new String(sbytes, "ISO-8859-1");
                } else {
                    realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName , "UTF-8"));
                }

                response.setContentType(request.getServletContext().getMimeType(realName));
                response.setHeader("Content-type", "application/octet-stream");
                response.setHeader("Content-Length", String.valueOf(bytes.length));
                response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", realName));
                response.getOutputStream().write(bytes);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }
    /**
     * 上传文件
     * @Title: upload
     * @author: WangBinBin
     * @param file 上传的文件
     * @return  文件名
     */
    @RequestMapping(method = RequestMethod.POST, path = "/uploadStop")
    @ResponseBody
    public JSONObject uploadStop(@RequestPart("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {

            String fileSavePath = path+"stop.dic";
            File oldFile = new File(fileSavePath);
            if(oldFile.exists()){
                oldFile.delete();
            }
            File newFile = new File(fileSavePath);
            FileUtils.copyInputStreamToFile(file.getInputStream(), newFile);
        } catch (IOException e) {
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        }
        JSONObject json = new JSONObject();

        json.put("code", "0");
        json.put("msg", "");
        return json;
    }
    @RequestMapping(path = "/downloadStop")
    public String uploadStop(HttpServletRequest request, HttpServletResponse response) {

        String  fileName ="stop.dic";;

        FileInputStream input = null;
        String fileSavePath = path+"stop.dic";
        // 下载*/
        try {
            File file = new File(fileSavePath);
            if(!file.exists()){
                return "error";
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            byte[] bytes = bos.toByteArray();
            String realName = "";

            //处理FireFox下载时文件名未转化成中文问题
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                // name.getBytes("UTF-8")处理safari的乱码问题
                fileName ="stop.dic";
                byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                // 各浏览器基本都支持ISO编码
                realName = new String(sbytes, "ISO-8859-1");
            } else {
                realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName , "UTF-8"));
            }

            response.setContentType(request.getServletContext().getMimeType(realName));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(bytes.length));
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", realName));
            response.getOutputStream().write(bytes);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }
    /**
     * 上传文件
     * @Title: upload
     * @author: WangBinBin
     * @param file 上传的文件
     * @return  文件名
     */
    @RequestMapping(method = RequestMethod.POST, path = "/uploadSynonymous")
    @ResponseBody
    public JSONObject uploadSynonymous(@RequestPart("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {

            String fileSavePath = path+"synonymous.dic";
            File oldFile = new File(fileSavePath);
            if(oldFile.exists()){
                oldFile.delete();
            }
            File newFile = new File(fileSavePath);
            FileUtils.copyInputStreamToFile(file.getInputStream(), newFile);
        } catch (IOException e) {
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        }
        JSONObject json = new JSONObject();

        json.put("code", "0");
        json.put("msg", "");
        return json;
    }
    @RequestMapping(path = "/downloadSynonymous")
    public String uploadSynonymous(HttpServletRequest request, HttpServletResponse response) {

        String  fileName ="synonymous.dic";;

        FileInputStream input = null;
        String fileSavePath = path+"synonymous.dic";
        // 下载*/
        try {
            File file = new File(fileSavePath);
            if(!file.exists()){
                return "error";
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            byte[] bytes = bos.toByteArray();
            String realName = "";

            //处理FireFox下载时文件名未转化成中文问题
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                // name.getBytes("UTF-8")处理safari的乱码问题
                fileName ="synonymous.dic";
                byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                // 各浏览器基本都支持ISO编码
                realName = new String(sbytes, "ISO-8859-1");
            } else {
                realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName , "UTF-8"));
            }

            response.setContentType(request.getServletContext().getMimeType(realName));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(bytes.length));
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", realName));
            response.getOutputStream().write(bytes);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }
}
