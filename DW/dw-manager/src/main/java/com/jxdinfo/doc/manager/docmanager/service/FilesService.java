package com.jxdinfo.doc.manager.docmanager.service;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.DocUploadParams;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 操作文件服务接口
 * Created by bjj on 2018/8/10.
 */
public interface FilesService {

    /**
     * 文件夹下载
     *
     * @param downloadFoldIds
     * @param request
     * @param response
     * @return
     */
    public void foldDownload(String downloadFoldIds, HttpServletRequest request, HttpServletResponse response);


    /**
     * 非结构化平台接口
     * 文件批量下载接口  直接返回文件流
     *
     * @param docId   文档id 多个文件用,隔开
     * @param userId   下载人id
     * @param request  系统请求
     * @param response 响应
     */
    public JSONObject fileDownload(String docId, String userId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;

    /**
     * 非结构化平台接口
     * 上传文件(文件类型MultipartFile) 没有处理权限
     * @param file 文件
     * @param docId 文件id
     * @param foldId 目录id
     * @param userId 用户id
     * @throws Exception
     */
    public void uploadFileApi(MultipartFile file, String docId, String foldId, String userId) throws Exception;

    /**
     * 非结构化平台接口
     * 上传文件(文件类型File) 自动继承上级目录权限 权限类型为下载
     * @param file 文件
     * @param docId 文件id
     * @param fileName 文件名称
     * @param foldId 目录id
     * @param userId 用户id
     * @throws Exception
     */
    public void uploadFileApi(File file, String docId, String fileName, String foldId, String userId) throws Exception;

    /**
     * 非结构化平台接口
     * 文件批量下载接口 返回文件字节数组
     *
     * @param docId   文档id
     * @param userId   下载人id
     */
    public byte[] fileDownload(String docId, String userId) throws IOException, ServiceException;

    /**
     * 非结构化平台接口
     * 下载文件接口 将文件转换成base64
     */
    public Map<String,String> downloadToBase64(String docIds) throws IOException, ServiceException;

    /**
     * 上传文件接口
     */
    public Map<String, Object> upload(MultipartFile file, DocUploadParams uploadParams) throws Exception;

    /**
     * 上传文件接口
     */
    public String upload(MultipartFile file, String fName) throws Exception;
    /**
     * 下载文件接口
     */
    public byte[] downloadYYZC(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServiceException;


    /**
     * 客户端下载文件接口
     */
    public void downloadClient(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException;

    /**
     * 下载文件接口
     */
    public void downloadByShare(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;
    /**
     * 下载文件接口
     */
    public void download(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;
    /*
    * 下载文件接口
    */
    public Map download1(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;
    public void returnFile(HttpServletRequest request, HttpServletResponse response,String filePath,String fileName) throws IOException;
    /**
     * 下载项目logo
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    public void downloadProjectLogo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;

    /**
     * 下载使用手册
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    public void downHandbook(HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException;

    /**
     * 上传项目logo
     *
     * @param file 压缩文件
     * @return
     */
    public JSONObject uploadProjectLogo(MultipartFile file);

    /**
     * 上传使用手册路由
     *
     * @param file 压缩文件
     * @return
     */
    public JSONObject uploadHandbookRoot(MultipartFile file);

    /**
     * 上传使用手册
     *
     * @param file 压缩文件
     * @return
     */
    public JSONObject uploadHandbook(MultipartFile file);

    /**
     * 手机下载文件接口
     */
    public void downloadMobile(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException;

    /**
     * 检查部门存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author wangning
     */
    public Map<String,Object> checkDeptSpace(String fileSize);

    /**
     * 检查个人存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author lishilin
     */
    public Map<String,Object> checkEmpSpace(String fileSize);

    /**
     * 文件上传接口
     */
    public void uploadFile(MultipartFile file, DocInfo docInfo, FsFile fsFile, List<DocResourceLog> resInfoList,
                           List<DocFileAuthority> list, List<String> indexList) throws Exception;
    /**
     * 文件上传接口
     */
    public void uploadFile(File file, DocInfo docInfo, FsFile fsFile, List<DocResourceLog> resInfoList,
                           List<DocFileAuthority> list, List<String> indexList, String contentType) throws Exception;
    /**
     * 文件秒传接口
     */
    public String uploadFast(String categoryId, String downloadAble, String visible, String group, String person,
                             String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                             String shareFlag, String userId, String fileOpen);

    /**
     * 金企信文件秒传接口
     */
    public String uploadFastJqx(String categoryId, String downloadAble, String visible, String group, String person,
                                String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                String shareFlag, String userId, String topicId);

    /**
     * 运营支撑文件秒传接口
     */
    public String uploadFastYYZC(String categoryId, String downloadAble, String visible, String group, String person,
                                 String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                 String shareFlag, String userId, String tags);

    /**
     * 文件版本秒传接口
     */
    String uploadVersionFast(String categoryId, String downloadAble, String visible, String group, String person,
                             String watermarkUser, String md5, String fileName, FsFile fsFile,
                             String size, String shareFlag, String oldDocId);
    String uploadVersionFastClient(String categoryId, String downloadAble, String visible, String group, String person,
                                   String watermarkUser, String md5, String fileName, FsFile fsFile,
                                   String size, String shareFlag, String oldDocId, String userId);
    public String uploadFastComponent(String categoryId, String downloadAble, String visible, String group, String person,
                                      String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                      String shareFlag, String userId, String tags);
    /**
     * 下载文件并解密
     * @param fsFileTemp 文件信息
     */
    String downloadFile(FsFile fsFileTemp);

    /**
     * 文档转pdf
     * @param docId 文档id
     */
    String changeToTxt(String docId);

    /**
     * 转换为txt并更新标签
     * @param docId 文档id
     * @return 是否成功
     */
    boolean getAndUpdateTags(String docId);

    /**
     * 获取token值
     * @param userId 用户ID
     * @param userName 用户名称
     * @return token
     */
    Map<String, String> getToken(String userId, String userName);

    /**
     * 获取服务器名称
     * @param fileId 文档ID
     * @return 服务器名称
     */
    String getServerName(String fileId);

    /**
     * 检查用户是否有文件的查看权限
     * @param fileId 文件id
     * @return 是否
     */
    boolean checkFilePreviewAuthority(String fileId);

    /**
     * 检查用户是否有文件的管理权限
     * @param idList 文件id集合
     * @return 是否
     */
    boolean checkFileManageAuthority(List<String> idList);

    /**
     * 检查用户是否有文件夹的上传权限
     * @param foldId 文件夹id
     * @return 是否
     */
    boolean checkFoldUploadAuthority(String foldId);

    /**
     * 检查用户是否有文件夹的管理权限
     * @param idList 文件夹id集合
     * @return 是否
     */
    boolean checkFoldManageAuthority(List<String> idList);

    /**
     * 检查用户是否有父文件夹的管理权限
     * @param idList 文件夹id集合
     * @return 是否
     */
    boolean checkParentFoldManageAuthority(List<String> idList);
}
