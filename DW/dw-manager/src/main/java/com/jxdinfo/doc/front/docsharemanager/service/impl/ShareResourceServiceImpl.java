package com.jxdinfo.doc.front.docsharemanager.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docsharemanager.dao.ShareResourceMapper;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShareResourceServiceImpl implements ShareResourceService {

    @Resource
    private ShareResourceMapper shareResourceMapper;

    @Resource
    private CacheToolService cacheToolService;
    @Resource
    private DocInfoService docInfoService;

    /**
     * 对外接口分享链接地址
     */
    @Value("${unstructUrl}")
    private String unstructUrl;

    @Override
    public Map newShareResourceClient(String fileId, String fileType, int pwdFlag, int validTime,int authority, HttpServletRequest request,String userId) {
        Map result = new HashMap();
        //判断文件是否可分享
        if(!fileType.equals("component")){
            if (!getShareFlagByDocId(fileId,fileType)){
                result.put("msg","文件不可分享");
                result.put("status",2);
                return result;
            }
        }
        try {
            String href = "";
            Map map = new HashMap();
            //对文件后缀名进行处理
            fileType = fileType.replace(".","");
            //匹配文件类型，得到不同的预览链接
            switch (fileType){
                case "png": case "jpg": case "gif": case "bmp": case "jpeg":
                    href = "/sharefile/toShowIMG?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
                case "ett": case "ppts": case "pot": case "dps": case "dpt": case "xlsx": case "txt":
                case "pdf": case "ceb": case "ppt": case "pptx": case "dotx": case "tif": case "rtf": case "pps":
                    href = "/sharefile/toShowPDF?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp4": case "wmv":
                    href = "/sharefile/toShowVideo?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp3": case "m4a":
                    href = "/sharefile/toShowVoice?id="+fileId+"&fileType=0&keyWords=";
                    break;

                case "component":
                    href = "/sharefile/toShowComponent?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "folder":
                    href = "/sharefile/toShowFolder?id="+fileId+"&fileType=0&keyWords=";
                    break;
                default:
                    href = "/sharefile/toShowOthers?id="+fileId+"&fileType=0&keyWords=";
                    break;
            }
            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0,23);
            String pwd = "";
            //如果需要提取码，则生成提取码
            if (pwdFlag == 1){
                pwd = StringUtil.getRandomCode(4);
            }
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365*100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
            map.put("hash", hash);
            map.put("docId",fileId);
            map.put("creatorId", userId);
            map.put("pwd", pwd);
            map.put("pwdFlag", pwdFlag);
            map.put("validTime",validTime);
            map.put("authority",authority);
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = shareResourceMapper.newShareResource(map);
            String mappingUrl = "";
            //读取缓存的服务器地址
            Map serverAddress = cacheToolService.getServerAddress();
            if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
                mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/s/" + hash;
            } else {
                mappingUrl = "http://" + serverAddress.get("address").toString() + "/s/" + hash;
            }

            if (isShare == 1){//分享资源生成成功，返回分享资源的信息
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(fileId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(6);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);//添加分享记录

                result.put("mapping_url",mappingUrl);
                result.put("pwd_flag",pwdFlag);
                result.put("valid_time",validTime);
                result.put("msg","将链接发送给小伙伴");
                result.put("status",1);
                if (pwdFlag == 1){
                    result.put("pwd",pwd);
                }
            } else {
                result.put("msg","分享失败");
                result.put("status",-1);
            }
            return result;
        } catch (Exception e){
            result.put("msg","分享失败");
            result.put("status",-1);
            return result;
        }
    }
    @Override
    public Map newShareResource(String fileId, String fileType, int pwdFlag, int validTime,int authority, String shareUserRadio,String selectShareUsersId,HttpServletRequest request) {
        Map result = new HashMap();
        //判断文件是否可分享
        if(!fileType.equals("component")&&!fileType.equals("folder")){
        if (!getShareFlagByDocId(fileId,fileType)){
            result.put("msg","文件不可分享");
            result.put("status",2);
            return result;
        }
        }
        try {
            String href = "";
            Map map = new HashMap();
            //对文件后缀名进行处理
            fileType = fileType.replace(".","");
            //匹配文件类型，得到不同的预览链接
            switch (fileType){
                case "png": case "jpg": case "gif": case "bmp": case "jpeg": case "ai":case "pdd":
                    href = "/sharefile/toShowIMG?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
                case "ett": case "ppts": case "pot": case "dps": case "dpt":case "ppsx": case "xlsx": case "txt":
                case "pdf": case "ceb": case "ppt": case "pptx": case "dotx": case "tif": case "rtf": case "pps":
                case "html": case "csv":case "xml":case "log":case "cmd": case "tiff":case "ini":case "dat":
                    href = "/sharefile/toShowPDF?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp4": case "wmv": case "avi": case "mov": case "flv":case "mkv":case "vob":case "swf":
                    href = "/sharefile/toShowVideo?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp3": case "m4a":
                    href = "/sharefile/toShowVoice?id="+fileId+"&fileType=0&keyWords=";
                    break;

                case "component":
                    href = "/sharefile/toShowComponent?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "folder":
                    href = "/sharefile/toShowFolder?id="+fileId+"&fileType=0&keyWords=";
                    break;
                default:
                    href = "/sharefile/toShowOthers?id="+fileId+"&fileType=0&keyWords=";
                    break;
            }
            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0,23);
            String pwd = "";
            //如果需要提取码，则生成提取码
            if (pwdFlag == 1){
                pwd = StringUtil.getRandomCode(4);
            }
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365*100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
            map.put("hash", hash);
            map.put("docId",fileId);
            map.put("creatorId", ShiroKit.getUser().getId());
            map.put("pwd", pwd);
            map.put("pwdFlag", pwdFlag);
            map.put("validTime",validTime);
            map.put("authority",authority);
            map.put("shareUserRadio",shareUserRadio);
            map.put("selectShareUsersId",selectShareUsersId);
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = shareResourceMapper.newShareResource(map);
            String mappingUrl = "";
            //读取缓存的服务器地址
            Map serverAddress = cacheToolService.getServerAddress();
            if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
                mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/s/" + hash;
            } else {
                mappingUrl = "http://" + serverAddress.get("address").toString() + "/s/" + hash;
            }

            if (isShare == 1){//分享资源生成成功，返回分享资源的信息
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(fileId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                String userId = UserInfoUtil.getUserInfo().get("ID").toString();
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(6);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);//添加分享记录

                result.put("mapping_url",mappingUrl);
                result.put("pwd_flag",pwdFlag);
                result.put("valid_time",validTime);
                result.put("msg","将链接发送给小伙伴");
                result.put("status",1);
                if (pwdFlag == 1){
                    result.put("pwd",pwd);
                }
            } else {
                result.put("msg","分享失败");
                result.put("status",-1);
            }
            return result;
        } catch (Exception e){
            result.put("msg","分享失败");
            result.put("status",-1);
            return result;
        }
    }

    @Override
    public Map newShareResourceYYZC(String fileId, String fileType, int pwdFlag, int validTime,int authority, HttpServletRequest request,String userId) {
        Map result = new HashMap();
        //判断文件是否可分享
        if(!fileType.equals("component")&&!fileType.equals("folder")){
            if (!getShareFlagByDocId(fileId,fileType)){
                result.put("msg","文件不可分享");
                result.put("status",2);
                return result;
            }
        }
        try {
            String href = "";
            Map map = new HashMap();
            //对文件后缀名进行处理
            fileType = fileType.replace(".","");
            //匹配文件类型，得到不同的预览链接
            switch (fileType){
                case "png": case "jpg": case "gif": case "bmp": case "jpeg":
                    href = "/sharefile/toShowIMG?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
                case "ett": case "ppts": case "pot": case "dps": case "dpt": case "xlsx": case "txt":
                case "pdf": case "ceb": case "ppt": case "pptx":
                    href = "/sharefile/toShowPDF?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp4": case "wmv":
                    href = "/sharefile/toShowVideo?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp3": case "m4a":
                    href = "/sharefile/toShowVoice?id="+fileId+"&fileType=0&keyWords=";
                    break;

                case "component":
                    href = "/sharefile/toShowComponent?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "folder":
                    href = "/sharefile/toShowFolder?id="+fileId+"&fileType=0&keyWords=";
                    break;
                default:
                    href = "/sharefile/toShowOthers?id="+fileId+"&fileType=0&keyWords=";
                    break;
            }
            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0,23);
            String pwd = "";
            //如果需要提取码，则生成提取码
            if (pwdFlag == 1){
                pwd = StringUtil.getRandomCode(4);
            }
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365*100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
            map.put("hash", hash);
            map.put("docId",fileId);
            map.put("creatorId", userId);
            map.put("pwd", pwd);
            map.put("pwdFlag", pwdFlag);
            map.put("validTime",validTime);
            map.put("authority",authority);
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = shareResourceMapper.newShareResource(map);
            String mappingUrl = "";
            //读取缓存的服务器地址
            Map serverAddress = cacheToolService.getServerAddress();
            if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
                mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/s/" + hash;
            } else {
                mappingUrl = "http://" + serverAddress.get("address").toString() + "/s/" + hash;
            }

            if (isShare == 1){//分享资源生成成功，返回分享资源的信息
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(fileId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(6);
                docResourceLog.setValidFlag("1");
                    docResourceLog.setAddressIp(HttpKit.getIp());

                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);//添加分享记录

                result.put("mapping_url",mappingUrl);
                result.put("pwd_flag",pwdFlag);
                result.put("valid_time",validTime);
                result.put("msg","将链接发送给小伙伴");
                result.put("status",1);
                if (pwdFlag == 1){
                    result.put("pwd",pwd);
                }
            } else {
                result.put("msg","分享失败");
                result.put("status",-1);
            }
            return result;
        } catch (Exception e){
            result.put("msg","分享失败");
            result.put("status",-1);
            return result;
        }
    }
    @Override
    public Map getShareResource(String hash) {
        return shareResourceMapper.getShareResource(hash);
    }

    @Override
    public String getPwdByHash(String hash) {
        return shareResourceMapper.getPwdByHash(hash);
    }

    @Override
    public boolean getShareFlagByDocId(String docId,String fileType) {
        if(!fileType.equals("folder")){
        //获取可分享状态标识
        Map shareCheck = shareResourceMapper.getShareFlagByDocId(docId);
        //获取文件的有效性
        String validFlag = "";
        if (null == shareCheck.get("validFlag")){
            validFlag = "0";
        } else {
            validFlag = shareCheck.get("validFlag").toString();
        }
        //获取文件的分享权限
        String shareFlag = "";
        if (null == shareCheck.get("shareFlag")){
            shareFlag = "0";
        } else {
            shareFlag = shareCheck.get("shareFlag").toString();
        }
        //如果文件有效，且可分享返回true
        return "1".equals(shareFlag) && "1".equals(validFlag);
        }else {
            return true;
        }

    }

    @Override
    public Map getPdfPath(String hash) {
        return shareResourceMapper.getPdfPath(hash);
    }

    @Override
    public boolean isPdfPathExist(String hash) {
        if (getPdfPath(hash).get("pdfPath") == null){
         return false;
        } else return !getPdfPath(hash).get("pdfPath").toString().equals("");
    }

    @Override
    public void setPdfPathFast(String hash) {
        String docId = shareResourceMapper.getDocIdByHash(hash);
        List<Map<String,String>> changePdf = cacheToolService.getUploadStateList();
        if (null != changePdf) {
            for (Map<String, String> pdf : changePdf) {
                if (null != pdf.get("docId")) {
                    String pdfId = pdf.get("docId");
                    if (docId.equals(pdfId)) {
                        if (null != pdf.get("state")) {
                            String state = pdf.get("state");
                            if ("1".equals(state)) {
                                cacheToolService.setReadyToFastChange(pdf, pdf.get("address"));
                                cacheToolService.removeFromChangePdfById(docId, pdf.get("address"));
                            }
                        }
                    }
                }
            }
        }
    }
    public Map newShareResourceMobile(String fileId, String fileType, int pwdFlag, int validTime, HttpServletRequest request,String userId) {
        Map result = new HashMap();
        //判断文件是否可分享
        if (!getShareFlagByDocId(fileId,fileType)){
            result.put("msg","文件不可分享");
            result.put("status",2);
            return result;
        }
        try {
            String href = "";
            Map map = new HashMap();
            //对文件后缀名进行处理
            fileType = fileType.replace(".","");
            //匹配文件类型，得到不同的预览链接
            switch (fileType){
                case "png": case "jpg": case "gif": case "bmp": case "jpeg":
                    href = "/sharefile/toShowIMG?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
                case "ett": case "ppts": case "pot": case "dps": case "dpt": case "xlsx": case "txt":
                case "pdf": case "ceb": case "ppt": case "pptx":
                    href = "/sharefile/toShowPDF?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp4": case "wmv":
                    href = "/sharefile/toShowVideo?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp3": case "m4a":
                    href = "/sharefile/toShowVoice?id="+fileId+"&fileType=0&keyWords=";
                    break;
                default:
                    href = "/sharefile/toShowOthers?id="+fileId+"&fileType=0&keyWords=";
                    break;
            }
            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0,23);
            String pwd = "";
            //如果需要提取码，则生成提取码
            if (pwdFlag == 1){
                pwd = StringUtil.getRandomCode(4);
            }
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365*100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
            map.put("hash", hash);
            map.put("docId",fileId);
            map.put("creatorId", userId);
            map.put("pwd", pwd);
            map.put("pwdFlag", pwdFlag);
            map.put("validTime",validTime);
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = shareResourceMapper.newShareResource(map);
            String mappingUrl = "";
            //读取缓存的服务器地址
            Map serverAddress = cacheToolService.getServerAddress();
            if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
                mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/s/" + hash;
            } else {
                mappingUrl = "http://" + serverAddress.get("address").toString() + "/s/" + hash;
            }

            if (isShare == 1){//分享资源生成成功，返回分享资源的信息
                result.put("mapping_url",mappingUrl);
                result.put("pwd_flag",pwdFlag);
                result.put("valid_time",validTime);
                result.put("msg","将链接发送给小伙伴");
                result.put("status",1);
                if (pwdFlag == 1){
                    result.put("pwd",pwd);
                }
            } else {
                result.put("msg","分享失败");
                result.put("status",-1);
            }
            return result;
        } catch (Exception e){
            result.put("msg","分享失败");
            result.put("status",-1);
            return result;
        }
    }

    /**
     * 非机构化平台
     * @param fileId
     * @param fileType
     * @param pwdFlag
     * @param validTime
     * @param authority
     * @param shareUserRadio
     * @param selectShareUsersId
     * @param userId
     * @param request
     * @return
     */
    @Override
    public JSONObject newShareResourceXJ(String fileId, String fileType, int pwdFlag, int validTime, int authority, String shareUserRadio, String selectShareUsersId, String userId, HttpServletRequest request) {
        JSONObject result = new JSONObject();
        try {
            String href = "";
            Map map = new HashMap();
            //对文件后缀名进行处理
            fileType = fileType.replace(".", "");
            //匹配文件类型，得到不同的预览链接
            switch (fileType){
                case "png": case "jpg": case "gif": case "bmp": case "jpeg": case "ai":case "pdd":
                    href = "/sharefile/toShowIMG?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
                case "ett": case "ppts": case "pot": case "dps": case "dpt":case "ppsx": case "xlsx": case "txt":
                case "pdf": case "ceb": case "ppt": case "pptx": case "dotx": case "tif": case "rtf": case "pps":
                case "html": case "csv":case "xml":case "log":case "cmd": case "tiff":case "ini":case "dat":
                    href = "/sharefile/toShowPDF?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp4": case "wmv": case "avi": case "mov": case "flv":case "mkv":case "vob":case "swf":
                    href = "/sharefile/toShowVideo?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "mp3": case "m4a":
                    href = "/sharefile/toShowVoice?id="+fileId+"&fileType=0&keyWords=";
                    break;

                case "component":
                    href = "/sharefile/toShowComponent?id="+fileId+"&fileType=0&keyWords=";
                    break;
                case "folder":
                    href = "/sharefile/toShowFolder?id="+fileId+"&fileType=0&keyWords=";
                    break;
                default:
                    href = "/sharefile/toShowOthers?id="+fileId+"&fileType=0&keyWords=";
                    break;
            }
            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0, 23);
            String pwd = "";
            //如果需要提取码，则生成提取码
            if (pwdFlag == 1) {
                pwd = StringUtil.getRandomCode(4);
            }
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365 * 100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
            map.put("hash", hash);
            map.put("docId", fileId);
            map.put("creatorId", userId);
            map.put("pwd", "00000000000");
            map.put("pwdFlag", pwdFlag);
            map.put("validTime", validTime);
            map.put("authority", authority);
            map.put("shareUserRadio", shareUserRadio);
            map.put("selectShareUsersId", selectShareUsersId);
            InetAddress address = InetAddress.getLocalHost();
            String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = shareResourceMapper.newShareResource(map);
            String mappingUrl = "";
            if (unstructUrl == null || "".equals(unstructUrl)) {
                //读取缓存的服务器地址
                Map serverAddress = cacheToolService.getServerAddress();
                if (serverAddress == null || serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())) {
                    mappingUrl = "http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/unstruct/" + hash;
                } else {
                    mappingUrl = "http://" + serverAddress.get("address").toString() + "/unstruct/" + hash;
                }
            } else {
                mappingUrl = unstructUrl + "/unstruct/" + hash;
            }

            if (isShare == 1) {//分享资源生成成功，返回分享资源的信息
                /*List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(fileId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(6);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);//添加分享记录*/

                result.put("mapping_url", mappingUrl);
                result.put("msg", "创建预览链接成功");
                result.put("code", "1");
                if (pwdFlag == 1) {
                    result.put("pwd", pwd);
                }
            } else {
                result.put("msg", "创建预览链接失败");
                result.put("code", "0");
            }
            return result;
        } catch (Exception e) {
            result.put("msg", "创建预览链接失败");
            result.put("code", "0");
            return result;
        }
    }

    @Override
    public String getDocIdByHash(String hash) {
      return   shareResourceMapper.getDocIdByHash(hash);
    }

}
