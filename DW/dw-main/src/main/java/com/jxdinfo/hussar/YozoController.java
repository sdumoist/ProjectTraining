package com.jxdinfo.hussar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.doc.newupload.thread.ChangeToPdfThread;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 在线编辑控制层
 * @author xgj
 */
@Controller
@RequestMapping("/yozoOnlineEdit")
public class YozoController {

    /**
     * 线程池
     */
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            100,10000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(YozoController.class);

    /**
     * 本机服务地址
     */
    @Value("${onlineEdit.localServer}")
    private String localServer;

    /**
     * 永中服务地址
     */
    @Value("${onlineEdit.yozoServer}")
    private String yozoServer;

    /**
     *  是否启用fastdfs
     */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    /**
     * 文件存放地址
     */
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;
    /**
     * 文件存放地址
     */
    @Value("${docbase.downloadFile}")
    private String downloadFile;
    /**
     * 文件存放地址
     */
    @Value("${docbase.editSavePath}")
    private String editSavePath;

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
     * 编辑文件
     * @param fileId 文件id
     * @param fileName 文件名称
     * @return 跳转地址
     */
    @RequestMapping("/editFile")
    @ResponseBody
    public JSONObject uploadFile(String fileId, String fileName) {
        try {
            String userId = ShiroKit.getUser().getId();
            String userName = ShiroKit.getUser().getName();
            Map<String,String> params = new HashMap<>();
            params.put("fileId",fileId);
            params.put("userId",userId);
            params.put("userName",userName);
            params.put("filePath",localServer + "/yozoOnlineEdit/openFile?fileId=" + fileId);
            params.put("fileName",fileName);
            params.put("mobileFlag","false");
            params.put("saveFlag","true");
            params.put("userRight","0");
            params.put("callbackUrl",localServer + "/yozoOnlineEdit/callback");
            Map<String,Object> data = new HashMap<>();
            data.put("method",1);
            data.put("params",params);
            String jsonParam = JSONObject.toJSONString(data);
            Map<String,String> jsonParams = new HashMap<>();
            jsonParams.put("jsonParams",jsonParam);
            String url = httpPostWithForm(yozoServer + "/api.do",jsonParams);
            System.out.println(url);
            JSONObject result = JSONObject.parseObject(url);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("在线编辑文件 "+fileName+" 失败：" + ExceptionUtils.getErrorInfo(e));
            return null;
        }
    }

    /**
     * 打开文件
     * @param request 请求
     * @param response 响应
     */
    @RequestMapping("/openFile")
    @ResponseBody
    public void openFile(HttpServletRequest request, HttpServletResponse response){
        byte[] bytes = null;
        FileOutputStream fos = null;
        FileInputStream input = null;
        File file = null;
        File fileKey = null;
        String fileId = request.getParameter("fileId");

        InputStream is = null;
        OutputStream os = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            // 根据文件id查询文件信息
            FsFile fsFileTemp =  filesMapper.selectById(fileId);
            // 文件路径
            String filePath = fsFileTemp.getFilePath();
            // 文件解密密钥
            String sourceKey = fsFileTemp.getSourceKey();
            if (filePath != null && !"".equals(filePath) && sourceKey != null && !"".equals(sourceKey)) {
                if (!fastdfsUsingFlag) {
                    input = new FileInputStream(filePath);
                    bytes = new byte[input.available()];
                    input.read(bytes);
                } else {
                    bytes = fastdfsService.download(filePath);
                }
                //在本地生成随机文件
                String random = fsFileTemp.getMd5();
                String fileName = fsFileTemp.getFileName();
                String suffix = fsFileTemp.getFileType();
                file = new File(downloadFileByKey + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                fileKey = new File(downloadFile + random + suffix);
                if (!fileKey.getParentFile().exists()) {
                    // 路径不存在,创建
                    fileKey.getParentFile().mkdirs();
                }
                if (!fileKey.exists()) {
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                } else {
                    fileExist = true;
                }
                //文件解密
                if (!fileExist) {
                    FileEncryptUtil.getInstance().decrypt(downloadFileByKey + random + suffix, downloadFile + random + suffix, sourceKey);
                }
                response.setContentType("applicaiton/x-download");
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName + suffix);
                is = new FileInputStream(new File(downloadFile + random + suffix));
                bis = new BufferedInputStream(is);
                os = response.getOutputStream();
                bos = new BufferedOutputStream(os);

                byte[] b = new byte[1024];
                int len = 0;
                while((len = bis.read(b)) != -1){
                    bos.write(b,0,len);
                }
                bis.close();
                is.close();
                bos.close();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("在线编辑打开文件失败：" + ExceptionUtils.getErrorInfo(e));
        } finally {
            if (fileKey != null) {
                fileKey.delete();
            }
            if (file != null) {
                file.delete();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 文件保存后回调方法
     * @param backFile 文件
     * @param request 请求
     * @param response 响应
     */
    @RequestMapping("/callback")
    public void callback(@RequestParam("file") MultipartFile backFile, HttpServletRequest request, HttpServletResponse response){
        // 加密后的文件
        File fileEditByKey = null;
        try {
            String userId = request.getParameter("userIds");
            String fileId = request.getParameter("fileId");
            // 根据文件id查询文件信息
            FsFile fsFileTemp =  filesMapper.selectById(fileId);
            String timestamp = String.valueOf(System.currentTimeMillis());
            String suffix = fsFileTemp.getFileType();
            String random = fsFileTemp.getMd5();
//            String fileName = random + "-" + timestamp + suffix;
            String fileName = random + suffix;
            String fileKyeName = random + "_new" + suffix;
            String fileEditPath = editSavePath + File.separator + "file" + File.separator + fileName;
            String fileEditByKeyPath =editSavePath + File.separator + "fileByKey" + File.separator + fileKyeName;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            System.out.println("调用回调方法*******************************************  "+sdf.format(new Date()));

            byte[] bytes = backFile.getBytes();
            // 下载文件到本地
            getFileByBytes(bytes,fileEditPath);

            File fileEdit = new File(fileEditPath);
            fileEditByKey = new File(fileEditByKeyPath);
            FileInputStream fileInputStream = null;
            String newMd5 = "";
            try {
                fileInputStream = new FileInputStream(fileEdit);
                newMd5 = DigestUtils.md5Hex(fileInputStream);
                fsFileTemp.setMd5(newMd5);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            // 如果MD5码不一致，则更新文件
            if (!"".equals(newMd5) && !newMd5.equals(random)) {
                // *******************************上传到fast、转pdf、更新ES*********************************
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

                // 添加编辑记录
                // 将userId转数组
                JSONArray userIdJson = JSON.parseArray(userId);
                if (userIdJson != null && userIdJson.size() > 0) {
                    long time = System.currentTimeMillis();
                    for (int i = 0; i < userIdJson.size(); i++) {
                        List<DocResourceLog> resInfoList = new ArrayList<>();
                        DocResourceLog docResourceLog = new DocResourceLog();
                        String id = UUID.randomUUID().toString().replace("-", "");
                        docResourceLog.setId(id);
                        docResourceLog.setResourceId(fileId);
                        Timestamp ts = new Timestamp(time);
                        docResourceLog.setOperateTime(ts);
                        docResourceLog.setResourceType(0);
                        docResourceLog.setUserId(String.valueOf(userIdJson.get(i)));
                        docResourceLog.setOperateType(1);
                        docResourceLog.setValidFlag("1");
                        docResourceLog.setAddressIp(HttpKit.getIp());
                        resInfoList.add(docResourceLog);
                        // 插入修改记录
                        docInfoService.insertResourceLog(resInfoList);
                    }
                }

                String contentType = getContentType(suffix.toLowerCase());
                //创建文件上传状态并存入缓存
                Map<String, String> toPdf = new HashMap<>();
                toPdf.put("docId", fileId);
                toPdf.put("sourcePath", fileEditPath);
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
            } else {
                // 不需要更新文件，则删除生成的编辑文件
                if (fileEdit != null) {
                    fileEdit.delete();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("在线编辑回调保存文件失败：" + ExceptionUtils.getErrorInfo(e));
        } finally {
            if (fileEditByKey != null) {
                fileEditByKey.delete();
            }
        }
    }

    /**
     * 保存文件到本地
     * @param bytes 文件二进制流
     * @param filePath 文件路径
     */
    public static void getFileByBytes(byte[] bytes, String filePath) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            //输出流
            fos = new FileOutputStream(file);
            //缓冲流
            bos = new BufferedOutputStream(fos);
            //将字节数组写出
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送post请求
     * @param url 地址
     * @param paramsMap 参数
     * @return 返回值
     */
    static String httpPostWithForm(String url,Map<String, String> paramsMap){
        // 用于接收返回的结果
        String resultData ="";
        try {
            HttpPost post = new HttpPost(url);
            List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
            // 迭代Map-->取出key,value放到BasicNameValuePair对象中-->添加到list中
            for (String key : paramsMap.keySet()) {
                pairList.add(new BasicNameValuePair(key, paramsMap.get(key)));
            }
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(pairList, "utf-8");
            post.setEntity(uefe);
            // 创建一个http客户端
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            // 发送post请求
            HttpResponse response = httpClient.execute(post);
            // 状态码为：200
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                // 返回数据：
                resultData = EntityUtils.toString(response.getEntity(),"UTF-8");
            }else{
                throw new RuntimeException("接口连接失败！");
            }
        } catch (Exception e) {
            throw new RuntimeException("接口连接失败！");
        }
        return resultData;
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
