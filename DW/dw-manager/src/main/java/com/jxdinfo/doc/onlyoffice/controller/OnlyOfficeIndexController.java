/*
 *
 * (c) Copyright Ascensio System SIA 2020
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
*/

package com.jxdinfo.doc.onlyoffice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.MathUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.doc.manager.topicmanager.dao.SpecialTopicFilesMapper;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopicFiles;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.doc.newupload.thread.ChangeToPdfThread;
import com.jxdinfo.doc.onlyoffice.model.FileType;
import com.jxdinfo.doc.onlyoffice.helpers.*;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.primeframework.jwt.domain.JWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OnlyOfficeIndexController extends BaseController
{
    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            100,10000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    /** fast服务器服务类  */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * 文件 Mapper 接口
     */
    @Autowired
    private FilesMapper filesMapper;

    /**
     * 缓存工具类接口
     */
    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 文件fs_file_upload接口
     */
    @Autowired
    private UploadService uploadService;

    /**
     * 文档操作记录接口
     */
    @Autowired
    private DocInfoService docInfoService;

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
     * es工具类
     */
    @Autowired
    private ESUtil esUtil;

    @Autowired
    private SpecialTopicFilesMapper specialTopicFilesMapper;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;

    /**
     * 文件存放地址
     */
    @Value("${docbase.editSavePath}")
    private String editSavePath;

    /**
     *  是否启用fastdfs
     */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    private static final String DocumentJwtHeader = ConfigManager.GetProperty("files.docservice.header");

    @RequestMapping("/IndexServlet")
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("type");

        if (action == null)
        {
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        DocumentManager.Init(request, response);
        PrintWriter writer = response.getWriter();

        switch (action.toLowerCase())
        {
            case "upload":
                Upload(request, response, writer);
                break;
            case "convert":
                Convert(request, response, writer);
                break;
            case "track":
                Track(request, response, writer);
                break;
            case "remove":
                Remove(request, response, writer);
                break;
        }
    }


    private static void Upload(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
    {
        response.setContentType("text/plain");

        try
        {
            Part httpPostedFile = request.getPart("file");

            String fileName = "";
            for (String content : httpPostedFile.getHeader("content-disposition").split(";"))
            {
                if (content.trim().startsWith("filename"))
                {
                    fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
                }
            }

            long curSize = httpPostedFile.getSize();
            if (DocumentManager.GetMaxFileSize() < curSize || curSize <= 0)
            {
                writer.write("{ \"error\": \"File size is incorrect\"}");
                return;
            }

            String curExt = FileUtility.GetFileExtension(fileName);
            if (!DocumentManager.GetFileExts().contains(curExt))
            {
                writer.write("{ \"error\": \"File type is not supported\"}");
                return;
            }

            InputStream fileStream = httpPostedFile.getInputStream();

            fileName = DocumentManager.GetCorrectName(fileName);
            String fileStoragePath = DocumentManager.StoragePath(fileName, null);

            File file = new File(fileStoragePath);

            try (FileOutputStream out = new FileOutputStream(file))
            {
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = fileStream.read(bytes)) != -1)
                {
                    out.write(bytes, 0, read);
                }

                out.flush();
            }

            CookieManager cm = new CookieManager(request);
            DocumentManager.CreateMeta(fileName, cm.getCookie("uid"), cm.getCookie("uname"));

            writer.write("{ \"filename\": \"" + fileName + "\"}");

        }
        catch (Exception e)
        {
            writer.write("{ \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private static void Convert(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
    {
        response.setContentType("text/plain");

        try
        {
            String fileName = request.getParameter("filename");
            String fileUri = DocumentManager.GetFileUri(fileName,"","");
            String fileExt = FileUtility.GetFileExtension(fileName);
            FileType fileType = FileUtility.GetFileType(fileName);
            String internalFileExt = DocumentManager.GetInternalExtension(fileType);

            if (DocumentManager.GetConvertExts().contains(fileExt))
            {
                String key = ServiceConverter.GenerateRevisionId(fileUri);

                String newFileUri = ServiceConverter.GetConvertedUri(fileUri, fileExt, internalFileExt, key, true);

                if (newFileUri.isEmpty())
                {
                    writer.write("{ \"step\" : \"0\", \"filename\" : \"" + fileName + "\"}");
                    return;
                }

                String correctName = DocumentManager.GetCorrectName(FileUtility.GetFileNameWithoutExtension(fileName) + internalFileExt);

                URL url = new URL(newFileUri);
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
                InputStream stream = connection.getInputStream();

                if (stream == null)
                {
                    throw new Exception("Stream is null");
                }

                File convertedFile = new File(DocumentManager.StoragePath(correctName, null));
                try (FileOutputStream out = new FileOutputStream(convertedFile))
                {
                    int read;
                    final byte[] bytes = new byte[1024];
                    while ((read = stream.read(bytes)) != -1)
                    {
                        out.write(bytes, 0, read);
                    }

                    out.flush();
                }

                connection.disconnect();

                //remove source file ?
                //File sourceFile = new File(DocumentManager.StoragePath(fileName, null));
                //sourceFile.delete();

                fileName = correctName;

                CookieManager cm = new CookieManager(request);
                DocumentManager.CreateMeta(fileName, cm.getCookie("uid"), cm.getCookie("uname"));
            }

            writer.write("{ \"filename\" : \"" + fileName + "\"}");

        }
        catch (Exception ex)
        {
            writer.write("{ \"error\": \"" + ex.getMessage() + "\"}");
        }
    }

    private void Track(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
    {
        String userAddress = request.getParameter("userAddress");
        String fileName = request.getParameter("fileName");
        String fileId = request.getParameter("fileId");
        String userId = request.getParameter("userId");
        String userName = request.getParameter("userName");
        String prev = FileUtility.GetFileExtension(fileName);
        String downFileName = fileId + prev;

        String storagePath = DocumentManager.StoragePath(downFileName, userAddress);
        String body = "";

        try
        {
            Scanner scanner = new Scanner(request.getInputStream());
            scanner.useDelimiter("\\A");
            body = scanner.hasNext() ? scanner.next() : "";
            scanner.close();
        }
        catch (Exception ex)
        {
            writer.write("get request.getInputStream error:" + ex.getMessage());
            return;
        }

        if (body.isEmpty())
        {
            writer.write("empty request.getInputStream");
            return;
        }

        JSONParser parser = new JSONParser();
        JSONObject jsonObj;

        try
        {
            Object obj = parser.parse(body);
            jsonObj = (JSONObject) obj;
        }
        catch (Exception ex)
        {
            writer.write("JSONParser.parse error:" + ex.getMessage());
            return;
        }

        int status;
        String downloadUri;
        String changesUri;
        String key;

        if (DocumentManager.TokenEnabled())
        {
            String token = (String) jsonObj.get("token");

            if (token == null) {
                String header = (String) request.getHeader(DocumentJwtHeader == null || DocumentJwtHeader.isEmpty() ? "Authorization" : DocumentJwtHeader);
                if (header != null && !header.isEmpty()) {
                    token = header.startsWith("Bearer ") ? header.substring(7) : header;
                }
            }

            if (token == null || token.isEmpty()) {
                writer.write("{\"error\":1,\"message\":\"JWT expected\"}");
                return;
            }

            JWT jwt = DocumentManager.ReadToken(token);
            if (jwt == null)
            {
                writer.write("{\"error\":1,\"message\":\"JWT validation failed\"}");
                return;
            }

            if (jwt.getObject("payload") != null) {
                try {
                    @SuppressWarnings("unchecked") LinkedHashMap<String, Object> payload =
                        (LinkedHashMap<String, Object>)jwt.getObject("payload");

                    jwt.claims = payload;
                }
                catch (Exception ex) {
                    writer.write("{\"error\":1,\"message\":\"Wrong payload\"}");
                    return;
                }
            }

            status = jwt.getInteger("status");
            downloadUri = jwt.getString("url");
            changesUri = jwt.getString("changesurl");
            key = jwt.getString("key");
        }
        else
        {
            status = Math.toIntExact((long) jsonObj.get("status"));
            downloadUri = (String) jsonObj.get("url");
            changesUri = (String) jsonObj.get("changesurl");
            key = (String) jsonObj.get("key");
        }

        int saved = 0;
        if (status == 2 || status == 3)//MustSave, Corrupted
        {
            try
            {
//                String histDir = DocumentManager.HistoryDir(storagePath);
//                String versionDir = DocumentManager.VersionDir(histDir, DocumentManager.GetFileVersion(histDir) + 1);
//                File ver = new File(versionDir);
                File toSave = new File(storagePath);

//                DocumentManager.CreateMeta(downFileName, userId, userName);
//                if (!ver.exists()) ver.mkdirs();

//                toSave.renameTo(new File(versionDir + File.separator + "prev" + prev));

                downloadToFile(downloadUri, toSave);
//                downloadToFile(changesUri, new File(versionDir + File.separator + "diff.zip"));

//                String history = (String) jsonObj.get("changeshistory");
//                if (history == null && jsonObj.containsKey("history")) {
//                    history = ((JSONObject) jsonObj.get("history")).toJSONString();
//                }
//                if (history != null && !history.isEmpty()) {
//                    FileWriter fw = new FileWriter(new File(versionDir + File.separator + "changes.json"));
//                    fw.write(history);
//                    fw.close();
//                }
//
//                FileWriter fw = new FileWriter(new File(versionDir + File.separator + "key.txt"));
//                fw.write(key);
//                fw.close();

                // 更新文档信息
                changeLocalInfo(userId,storagePath,fileId);
                // 上传最新版本
//                changeLocalInfoVersion(userId,storagePath,fileId);
            }
            catch (Exception ex)
            {
                saved = 1;
            }
        }

        writer.write("{\"error\":" + saved + "}");
    }

    private static void Remove(HttpServletRequest request, HttpServletResponse response, PrintWriter writer)
    {
        try
        {
            String fileName = request.getParameter("filename");
            String path = DocumentManager.StoragePath(fileName, null);

            File f = new File(path);
            delete(f);

            File hist = new File(DocumentManager.HistoryDir(path));
            delete(hist);

            writer.write("{ \"success\": true }");
        }
        catch (Exception e)
        {
            writer.write("{ \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private static void delete(File f) throws Exception {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
            delete(c);
        }
        if (!f.delete())
            throw new Exception("Failed to delete file: " + f);
    }

    private static void downloadToFile(String url, File file) throws Exception {
        if (url == null || url.isEmpty()) throw new Exception("argument url");
        if (file == null) throw new Exception("argument path");

        URL uri = new URL(url);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) uri.openConnection();
        InputStream stream = connection.getInputStream();

        if (stream == null)
        {
            throw new Exception("Stream is null");
        }

        try (FileOutputStream out = new FileOutputStream(file))
        {
            int read;
            final byte[] bytes = new byte[1024];
            while ((read = stream.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }

            out.flush();
        }

        connection.disconnect();
    }

    private void changeLocalInfo(String userId, String storagePath, String fileId){
        // 根据文件id查询文件信息
        FsFile fsFileTemp = filesMapper.selectById(fileId);
        String suffix = fsFileTemp.getFileType();
        String random = fsFileTemp.getMd5();
        String fileKyeName = random + "_new" + suffix;
        // 修改后的文件
        File fileEdit = new File(storagePath);
        // 加密后的文件
        File fileEditByKey = null;
        // 修改md5码
        FileInputStream fileInputStream = null;
        String newMd5 = "";
        try{
            fileInputStream = new FileInputStream(fileEdit);
            newMd5 = DigestUtils.md5Hex(fileInputStream);
            fsFileTemp.setMd5(newMd5);
            String fileEditByKeyPath = editSavePath + File.separator + "fileByKey" + File.separator + fileKyeName;
            fileEditByKey = new File(fileEditByKeyPath);
            if (!fileEditByKey.getParentFile().exists()) {
                // 路径不存在,创建
                fileEditByKey.getParentFile().mkdirs();
            }
            //文件加密并取出加密密码存到数据库
            String sourceKey = FileEncryptUtil.getInstance().encrypt(fileEdit, fileEditByKey);
            fsFileTemp.setSourceKey(sourceKey);
            //启用FASTDFS时将文件上传到服务器
            if (fastdfsUsingFlag) {
                // 删除旧文件
                fastdfsService.removeFile(fsFileTemp.getFilePath());
                // 上传新文件
                String fileNewPath = fastdfsService.uploadFile(fileEditByKey);
                fsFileTemp.setFilePath(fileNewPath.replace("\\", "/"));
            } else {
                // 更新到本地
                FileUtils.copyFile(fileEditByKey,new File(fsFileTemp.getFilePath()));
            }
            // 更新文件信息
            filesMapper.updateById(fsFileTemp);

            String contentType = getContentType(suffix.toLowerCase());
            //创建文件上传状态并存入缓存
            Map<String, String> toPdf = new HashMap<>();
            toPdf.put("docId", fileId);
            toPdf.put("sourcePath", storagePath.replace("\\", "/"));
            toPdf.put("contentType", contentType);
            toPdf.put("state", "1");
            String address = InetAddress.getLocalHost().toString().replace(".", "");
            toPdf.put("address", address);
            cacheToolService.setUploadState(toPdf);
            int num = uploadService.updateUploadState(toPdf);
            if (num == 0 ) {
                uploadService.newUploadState(toPdf);
            }
            threadPoolExecutor.execute(new ChangeToPdfThread(fileId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileEditByKey != null) {
                fileEditByKey.delete();
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void changeLocalInfoVersion(String userId, String storagePath, String fileId){
        // 根据文件id查询文件信息
        FsFile fsFileTemp = filesMapper.selectById(fileId);
        String suffix = fsFileTemp.getFileType();
        String random = fsFileTemp.getMd5();
        String fileKyeName = random + "_new" + suffix;
        // 修改后的文件
        File fileEdit = new File(storagePath);
        // 加密后的文件
        File fileEditByKey = null;
        // 修改md5码
        FileInputStream fileInputStream = null;
        String newMd5 = "";
        try{
            fileInputStream = new FileInputStream(fileEdit);
            newMd5 = DigestUtils.md5Hex(fileInputStream);
            fsFileTemp.setMd5(newMd5);
            String fileEditByKeyPath = editSavePath + File.separator + "fileByKey" + File.separator + fileKyeName;
            fileEditByKey = new File(fileEditByKeyPath);
            if (!fileEditByKey.getParentFile().exists()) {
                // 路径不存在,创建
                fileEditByKey.getParentFile().mkdirs();
            }
            //文件加密并取出加密密码存到数据库
            String sourceKey = FileEncryptUtil.getInstance().encrypt(fileEdit, fileEditByKey);
            fsFileTemp.setSourceKey(sourceKey);
            //启用FASTDFS时将文件上传到服务器
            if (fastdfsUsingFlag) {
                // 删除旧文件
//                fastdfsService.removeFile(fsFileTemp.getFilePath());
                // 上传新文件
                String fileNewPath = fastdfsService.uploadFile(fileEditByKey);
                fsFileTemp.setFilePath(fileNewPath.replace("\\", "/"));
            } else {
                // 更新到本地
                FileUtils.copyFile(fileEditByKey,new File(fsFileTemp.getFilePath()));
            }
            int versionNumber = 1;
            // 判断version表中有无旧版本数据
            DocInfo oldDocInfo = docInfoService.getOne(new QueryWrapper<DocInfo>().eq("doc_id", fileId));
            if (docVersionService.count(new QueryWrapper<DocVersion>().eq("doc_id", fileId)) == 0) {
                DocVersion oldVersion = new DocVersion();
                oldVersion.setDocId(fileId);
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
                DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", fileId));
                versionNumber = docVersionService.selectVersionNumber(oldVersion.getVersionReference());
            }

            // 文档信息
            String docId = UUID.randomUUID().toString().replace("-", "");
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            oldDocInfo.setDocId(docId);
            oldDocInfo.setFileId(docId);
            oldDocInfo.setUserId(userId);
            oldDocInfo.setAuthorId(userId);
            oldDocInfo.setContactsId(userId);
            oldDocInfo.setCreateTime(ts);
            oldDocInfo.setUpdateTime(ts);
            // 文件信息
            fsFileTemp.setFileId(docId);
            //大小保留2位小数
            double size = MathUtil.getDecimal(fileEditByKey.length() / 1024, 2);
            fsFileTemp.setFileSize(size + "KB");
            fsFileTemp.setSize(fileEditByKey.length());
            filesMapper.insert(fsFileTemp);
            docInfoService.save(oldDocInfo);

            //继承权限信息
            List<DocFileAuthority> list = new ArrayList<>();
            List<String> indexList = new ArrayList<>();
            list = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id", fileId));
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
            //保存文档权限
            if (list != null && list.size() > 0) {
                //完全公开文档上传时，文档权限记录list为0，不插入
                docFileAuthorityService.saveBatch(list);
            }

            // 复制es索引
            esService.copyIndex(fileId, docId);

            cacheToolService.updateLevelCodeCache(userId);
            // 上传成功后将旧版本设为无效（docinfo表）
            docInfoService.updateValidFlag(fileId, "0");
            // 将es中该文件设为不可检索
            Map map = new HashMap(1);
            map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
            esUtil.updateIndex(fileId, map);
            // es中更新标签信息
            Map mapTip = new HashMap(1);
            mapTip.put("tags", oldDocInfo.getTags());
            esUtil.updateIndex(docId, mapTip);
            // 继承旧文档的专题信息
            List<SpecialTopicFiles> topics = specialTopicFilesMapper.selectTopicsByDocId(fileId);
            if (topics != null && topics.size() != 0) {
                for (int i = 0; i < topics.size(); i++) {
                    topics.get(i).setTopicFileId(UUID.randomUUID().toString().replace("-", ""));
                    topics.get(i).setDocId(docId);
                }
                specialTopicFilesMapper.addSpecialTopicFiles(topics);
            }
            // 上传成功后将新版本的数据插入version表中
            DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", fileId));
            DocVersion newVersion = new DocVersion();
            newVersion.setVersionReference(oldVersion.getVersionReference());
            newVersion.setDocId(docId);
            newVersion.setValidFlag("1");
            newVersion.setApplyTime(ts);
            newVersion.setApplyUserId(userId);
            newVersion.setVersionNumber(versionNumber+1);
            docVersionService.save(newVersion);
            //继承置顶、广告位
            List<String> oldDocIds = Arrays.asList(fileId.split(","));
            List listTop = docTopService.addCheck(oldDocIds);
            if (listTop.size() > 0) {
                docTopService.updateTop(fileId, docId);
            }
            //继承广告位信息
            List listBanner = bannerService.selectBannerById(fileId);
            if (listBanner.size() > 0) {
                String docType = oldDocInfo.getDocType();
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
                bannerService.updateBanner(fileId, docId, bannerHref);
            }

            String contentType = getContentType(suffix.toLowerCase());
            //创建文件上传状态并存入缓存
            Map<String, String> toPdf = new HashMap<>();
            toPdf.put("docId", fileId);
            toPdf.put("sourcePath", storagePath.replace("\\", "/"));
            toPdf.put("contentType", contentType);
            toPdf.put("state", "1");
            String address = InetAddress.getLocalHost().toString().replace(".", "");
            toPdf.put("address", address);
            cacheToolService.setUploadState(toPdf);
            int num = uploadService.updateUploadState(toPdf);
            if (num == 0 ) {
                uploadService.newUploadState(toPdf);
            }
            threadPoolExecutor.execute(new ChangeToPdfThread(fileId));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileEditByKey != null) {
                fileEditByKey.delete();
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件类型
     * @param suffix 文件后缀
     * @return 类型
     */
    private String getContentType(String suffix) {
        String contentType = null;
        if (suffix.equals(".doc") || suffix.equals(".docx")) {
            contentType = "application/msword";
            return contentType;
        } else if (suffix.equals(".ppt") || suffix.equals(".pptx")|| suffix.equals(".ppsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            return contentType;
        } else if (suffix.equals(".xls") || suffix.equals(".xlsx") || suffix.equals(".et")) {
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
        }else if (suffix.equals(".wps")) {
            contentType = "application/vnd.ms-works";
            return contentType;
        } else {
            return null;
        }
    }

}
