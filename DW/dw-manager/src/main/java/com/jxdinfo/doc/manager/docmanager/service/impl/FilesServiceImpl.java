package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.*;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docaudit.service.IDocInfoAuditService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.*;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.dao.DocSpaceMapper;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.doc.manager.statistics.service.DocSpaceService;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.doc.manager.topicmanager.model.TopicFile;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.doc.manager.videomanager.model.DocVideoThumb;
import com.jxdinfo.doc.manager.videomanager.service.DocVideoThumbService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.doc.newupload.thread.ChangeToPdfThread;
import com.jxdinfo.doc.newupload.thread.ToUpload;
import com.jxdinfo.doc.semanticAnalysis.service.SemanticAnalysisService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 操作文件接口实现类
 * Created by lenovo on 2018/8/10.
 */
@Service
public class FilesServiceImpl implements FilesService {
    private static ThreadPoolExecutor threadPoolExecutor = ToUpload.getPdfThreadPoolExecutor();

    /**
     * 日志
     */
    private static Logger logger = LogManager.getLogger(FilesServiceImpl.class);
    @Value("${docbase.filedir}")
    private String tempdir;
    @Value("${docbase.fileByKey}")
    private String fileByKey;
    @Value("${docbase.pdfFile}")
    private String pdfFileDir;
    @Value("${docbase.pdfFileByKey}")
    private String pdfFileByKey;

    @Value("${docbase.ThumbnailsDir}")
    private String ThumbnailsDir;
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    @Value("${thread.core-pool-size}")
    private Integer poolSize;
    @Value("${thread.max-pool-size}")
    private Integer maxSize;
    @Value("${thread.keep-alive-time}")
    private Integer aliveTime;
    /**
     * zip下载目录
     */
    @Value("${docbase.zipDownloadPath}")
    private String zipDownloadPath;

    @Value("${examine.using}")
    private boolean examineUsingFlag;

    @Value("${fileAudit.using}")
    private String using;

    @Value("${fileAudit.workflowUsing}")
    private boolean workflowUsing;

    private static final int  BUFFER_SIZE = 2 * 1024;


    @Autowired
    private ProcessUtil processUtil;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;


    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;

    @Autowired
    private DocVideoThumbService docVideoThumbService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private IFsFolderService iFsFolderService;

    @Autowired
    private ESUtil esUtil;
    @Resource
    private UploadService uploadService;

    @Resource
    private FrontDocInfoMapper frontDocInfoMapper;

    /** 配置信息服务层 */
    @Resource
    private DocConfigureService docConfigureService;


    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    /**
     * fs_file Mapper 接口
     */
    @Autowired
    private FilesMapper filesMapper;

    @Autowired
    private FsFileService fsFileService;

    /**
     * doc_info Mapper 接口
     */
    @Autowired
    private DocInfoMapper docInfoMapper;

    /**
     * 目录操作接口
     */
    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * FAST操作接口
     */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;

    /**
     * PDF操作接口
     */
    @Autowired
    private PdfService pdfService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    /**
     * 文件 Mapper 接口
     */
    @Autowired
    private FsFileMapper fsFileMapper;

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;
    /**
     * 版本管理 服务层
     */
    @Autowired
    private DocVersionService docVersionService;

    /**
     * 部门空间类接口
     */
    @Autowired
    private DocSpaceService docSpaceService;

    @Resource
    private DocSpaceMapper docSpaceMapper;
    /**
     *
     */
    @Autowired
    private EmpStatisticsService empStatisticsService;
    /**
     * 缓存工具类接口
     */
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private SysStruMapper sysStruMapper;


    @Value("${SPACE.SIZE}")
    private double SpaceSize;

    @Autowired
    private IDocInfoAuditService docInfoAuditService;

    @Autowired
    private SemanticAnalysisService semanticAnalysisService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 加密文件存放地址
     */
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;
    /**
     * 文件存放地址
     */
    @Value("${docbase.downloadFile}")
    private String downloadFile;
    /**
     * 语义分析：生成标签-是否开启
     */
    @Value("${semanticAnalysis.analysisUsing}")
    private String analysisUsing;

    @Value("${token.invalid}")
    private Long invalid;

    @Value("${token.key}")
    private String key;

    @Value("${onlineEdit.serverNum}")
    private Integer serverNum;

    @Value("${handBook.addressList}")
    private String handBookAddressList;

    private Environment environment = SpringContextHolder.getBean(Environment.class);

    /**
     * 文件夹下载
     *
     * @param downloadFoldIds
     * @param request
     * @param response
     * @return
     */
    @Override
    public void foldDownload(String downloadFoldIds, HttpServletRequest request, HttpServletResponse response) {

        String[] foldIds = downloadFoldIds.split(",");
        // 压缩文件路径
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String rootName = df.format(new Date()) + StringUtil.getString(Math.round(Math.random() * 100));

        String rootZipPath = "";
        String zipName = "";
        File zipFold = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            // 创建一个下载目录
            File rootFold = new File(zipDownloadPath + File.separator + rootName);
            if (!rootFold.getParentFile().exists()) {
                rootFold.getParentFile().mkdirs();
            }
            rootFold.mkdir();

            // 创建一个压缩目录
            zipName = rootName + ".zip";
            rootZipPath = zipDownloadPath + File.separator + zipName;
            zipFold = new File(rootZipPath);

            if (!zipFold.getParentFile().exists()) {
                zipFold.getParentFile().mkdirs();
            }
            zipFold.createNewFile();
            fos = new FileOutputStream(zipFold);
            zos = new ZipOutputStream(fos);
            zos.setEncoding("GBK");

            for (String foldId : foldIds) {
                //  下载目录及子目录和文件
                FsFolder fsFolder = fsFolderService.getById(foldId);
                String foldPath = zipDownloadPath + File.separator + rootName + File.separator + fsFolder.getFolderName();
                File foldFile = new File(foldPath);
                foldFile.mkdir();

                System.out.println("");
                System.out.println("============创建目录开始: " + fsFolder.getFolderName());
                long time1 = System.currentTimeMillis();
                foldDownloads(foldId, foldPath);
                long time2 = System.currentTimeMillis();
                System.out.println("============创建目录结束: " + fsFolder.getFolderName() + " ===用时: " + (time2 - time1));
                System.out.println("");

                // 压缩
                System.out.println("");
                System.out.println("============压缩目录开始: " + fsFolder.getFolderName());
                long time3 = System.currentTimeMillis();
                foldZip(foldFile, zos, fsFolder.getFolderName());
                long time4 = System.currentTimeMillis();
                System.out.println("============压缩目录结束: " + fsFolder.getFolderName() + " ===用时: " + (time4 - time3));
                System.out.println("");
            }

            if (zos != null) {
                zos.close();
            }
            if (fos != null) {
                fos.close();
            }

            // 删除生成的目录
            System.out.println("============删除目录开始: " + rootName);
            long time5 = System.currentTimeMillis();
            try {
                deleteDir(rootFold);
            } catch (Exception e) {
                System.out.println("=================删除目录异常==============" + rootName);
            }
            long time6 = System.currentTimeMillis();
            System.out.println("============删除目录结束: " + rootName + " ===用时: " + (time6 - time5));

            // 返回文件
            downloadFileToBrowser(zipFold, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 文件夹递归下载
     *
     * @param foldId
     */
    private void foldDownloads(String foldId, String foldPath) throws ServiceException {
        // 根据目录id 查询目录下的子目录或文件
        List<FsFolderView> foldAndFileList = fsFolderService.getFilesAndFloderAll(foldId);
        if (foldAndFileList != null && foldAndFileList.size() > 0) {
            for (FsFolderView fsFolderView : foldAndFileList) {
                String id = fsFolderView.getFileId();
                String fileName = fsFolderView.getFileName();
                String fileType = fsFolderView.getFileType();
                // 如果是目录则创建目录
                if (StringUtils.equals(fileType, "folder")) {
                    String path = foldPath + File.separator + fileName;
                    File foldFile = new File(path);
                    foldFile.mkdir();
                    System.out.println("================创建目录成功: " + fileName);
                    // 递归文件夹
                    foldDownloads(id, path);
                } else { // 若果是文件则下载文件
                    downloadFileToFoldPath(foldPath, fsFolderView);
                    System.out.println("================下载文件成功: " + fileName);
                }
            }
        }
    }

    /**
     * 下载文件到指定目录
     *
     * @param foldPath
     * @param fsFolderView
     */
    private void downloadFileToFoldPath(String foldPath, FsFolderView fsFolderView) throws ServiceException {
        String fileName = fsFolderView.getFileName();
        String fileType = fsFolderView.getFileType();
        try {
            File targetFile = new File(foldPath + File.separator + fileName);
            String sourceFilePath = fsFolderView.getFolderLocal();
            String sourceKey = fsFolderView.getSourceKey();

            if (!fastdfsUsingFlag) {
                // 加密保存的文件
                File sourceFile = new File(sourceFilePath);
                // 文件解密
                Boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(sourceFile, targetFile, sourceKey);
            } else {
                // 从fast下载文件
                fastdfsService.download(sourceFilePath, targetFile);
                Boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(foldPath + File.separator + fileName, sourceKey);
            }
        } catch (Exception e) {
            System.out.println("===================文件下载异常====================" + foldPath + File.separator + fileName);
        }
    }

    /**
     * 文件夹递归压缩
     *
     * @param sourceFile
     */
    private void foldZip(File sourceFile, ZipOutputStream zos, String name) throws IOException {

        byte[] buf = new byte[BUFFER_SIZE];
        if (sourceFile.isFile()) {
            try {

                // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
                zos.putNextEntry(new org.apache.tools.zip.ZipEntry(name));

                // copy文件到zip输出流中
                int len;
                FileInputStream in = new FileInputStream(sourceFile);

                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                // Complete the entry
                zos.closeEntry();
                in.close();
                System.out.println("================压缩文件成功: " + name);
            } catch (Exception e) {
                System.out.println("======================压缩异常===========" + name);
            }
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                // 空文件夹的处理
                zos.putNextEntry(new org.apache.tools.zip.ZipEntry(name + "/"));
                // 没有文件，不需要文件的copy
                zos.closeEntry();
                System.out.println("================压缩目录成功: " + name);
            } else {
                for (File file : listFiles) {

                    // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                    // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                    foldZip(file, zos, name + "/" + file.getName());


                }
            }
        }

    }

    /**
     * 返回文件到浏览器
     * @param file
     * @param request
     * @param response
     */
    private void downloadFileToBrowser(File file, HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        FileInputStream inputStream = null;
        try {
            String filename = file.getName();
            String userAgent = request.getHeader("User-Agent");
            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                filename = java.net.URLEncoder.encode(filename, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                // filename = URLEncoder.encode(filename, "UTF-8");
                filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=\"%s\"", filename));
            response.setContentType("application/download");
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 10];
            int lenth = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((lenth = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, lenth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) out.close();
                if (null != inputStream) inputStream.close();
                // 删除压缩文件
                file.delete();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 上传文件接口
     *
     * @param file 文件表实体
     * @param
     * @return void
     * @throws IOException
     * @throws IllegalStateException
     * @author LiangDong
     * @date 2018/8/10 18:54
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> upload(MultipartFile file, DocUploadParams uploadParams) throws Exception {
        List<DocResourceLog> resInfoList = new ArrayList<>();
        List<DocFileAuthority> authoritylist = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        DocInfo docInfo = new DocInfo();
        FsFile fsFile = new FsFile();

        Map<String, Object> resultMap = getUploadInfo(file, uploadParams, docInfo, fsFile, resInfoList, authoritylist, indexList);

        if (resultMap.get("releaseSize") == null) {
            //部门存储空间不足
            return resultMap;
        } else {
            uploadFile(file, docInfo, fsFile, resInfoList, authoritylist, indexList);
        }

        return resultMap;
    }

    /**
     * 上传文件（实际上只有专题图片上传会用到了）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String upload(MultipartFile file, String fName) throws Exception {
        // 临时文件
        File tempFile = null;

        try {
            // 临时文件生成
            tempFile = new File(tempdir, fName);
            if (!tempFile.getParentFile().exists()) {
                // 路径不存在,创建
                tempFile.getParentFile().mkdirs();
            }

            //将MultipartFile 转换生成 File
            transFile(file, tempFile);

            //上传源文件到fastdfs
            String filePath = "";

            if (fastdfsUsingFlag) {
                //启用FASTDFS时将文件上传到服务器
                filePath = fastdfsService.uploadFile(tempFile);
            } else {
                filePath = tempFile.getAbsolutePath();
            }

            return filePath;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件上传失败。");
        } finally {
            deleteFile(tempFile);
        }
    }

    /**
     * 文件下载
     *
     * @param docIds
     * @param docName
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public void download(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        fileDownNew(docIds, docName, request, response);
    }
    @Override
    public Map   download1(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        Map map = fileDownNew1(docIds, docName, request, response);
        return map;
    }

    /**
     * 下载项目logo
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public void downloadProjectLogo(HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        String[] pathAddr = {
                "/static/logo/logo.png",
                "/static/logo/logo-search.png",
                "/static/logo/logo_new3.png",
                "/static/logo/login.svg",
                "/static/logo/welcom_1366.png",
                "/static/assets/img/welcom.png",
                "/static/resources/img/front/favicon.ico",
                "/static/resources/img/front/manger-index-bg1.png"
        };

        // 获取项目根目录
        String classpath = this.getClass().getResource("/").getPath();
        System.out.println("classpath" + classpath);
        String webappRoot = classpath.replaceAll("WEB-INF/classes/", "");
        System.out.println("webappRoot" + webappRoot);

        // 组装压缩文件
        String zipName = generateZipFileName();
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipName;
        // 根据临时的zip压缩包路径，创建zip文件
        File zip = new File(zipDownloadPath);
        // 如果压缩文件不存在的话创建它
        if (!zip.exists()) {
            if (!zip.getParentFile().exists()) {
                zip.getParentFile().mkdirs();
            }
            zip.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zos.setEncoding("GBK");


        for (String path : pathAddr) {
            // logo文件
            File logoFile = new File(webappRoot + path);
            // logo名称
            String fileName = path.substring(path.lastIndexOf("/"));
            FileTool.zipFile(logoFile, zos, fileName);
        }

        //关闭文件流
        closeFileIO(zos);
        closeFileIO(fos);

        try {
            response.setContentType(request.getServletContext().getMimeType(zip.getName()));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(zipName, "UTF-8"));
            response.getOutputStream().write(FileUtils.readFileToByteArray(zip));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            zip.delete();
        }
    }

    /**
     * 下载使用手册
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public void downHandbook(HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        // 获取项目根目录
        String classpath = this.getClass().getResource("/").getPath();
        String webappRoot = classpath.replaceAll("target/classes/", "/src/main/webapp/");

        DocConfigure docConfigure = docConfigureService.getById("zddddddd2b3c11e8aacf429ff426666");
        if (docConfigure != null) {
            String fileName = docConfigure.getConfigValue();
            String downloadPath = webappRoot + "static/handbook" + File.separator + docConfigure.getConfigValue();
            File downFile = new File(downloadPath);
            ServletOutputStream out = null;
            FileInputStream input = null;

            String realName = "";

            //处理FireFox下载时文件名未转化成中文问题
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                // name.getBytes("UTF-8")处理safari的乱码问题
                byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                // 各浏览器基本都支持ISO编码
                realName = new String(sbytes, "ISO-8859-1");
            } else {
                realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName, "UTF-8"));
            }

            try {
                response.setHeader("Content-disposition",
                        String.format("attachment; filename=\"%s\"", realName));
                response.setContentType("application/download");
                response.setCharacterEncoding("UTF-8");
                out = response.getOutputStream();
                input = new FileInputStream(downFile);
                byte[] buffer = new byte[1024 * 10];
                int lenth = -1;
                // 通过循环将读入的Word文件的内容输出到浏览器中
                while ((lenth = input.read(buffer)) != -1) {
                    out.write(buffer, 0, lenth);
                }
            } catch (Exception e) {
                logger.info("============下载使用手册异常");
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    } else {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上传项目logo
     *
     * @param file 压缩文件
     * @return
     */
    @Override
    public JSONObject uploadProjectLogo(MultipartFile file) {
        JSONObject result = new JSONObject();
        result.put("code", "0");
        Boolean successAll = true;
        int successCount = 0;
        List<String> errorList = new ArrayList<>();

        // 定义文件名称对应的图标
        Map<String, String> logoPathMap = new HashMap<String, String>();
        logoPathMap.put("logo.png", "/static/logo/logo.png");
        logoPathMap.put("logo-search.png", "/static/logo/logo-search.png");
        logoPathMap.put("logo_new3.png", "/static/logo/logo_new3.png");
        logoPathMap.put("login.svg", "/static/logo/login.svg");
        logoPathMap.put("welcom_1366.png", "/static/logo/welcom_1366.png");
        logoPathMap.put("welcom.png", "/static/assets/img/welcom.png");
        logoPathMap.put("favicon.ico", "/static/resources/img/front/favicon.ico");
        logoPathMap.put("manger-index-bg1.png", "/static/resources/img/front/manger-index-bg1.png");

        // 获取项目根目录
        String classpath = this.getClass().getResource("/").getPath();
        String webappRoot = classpath.replaceAll("target/classes/", "/src/main/webapp/");

        // 定义每一个压缩文件
        ZipEntry entry = null;
        File entryFile = null;
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        // zip文件路径
        String zipFileName = System.currentTimeMillis() + ".zip";
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipFileName;
        File zipDownFile = new File(zipDownloadPath);

        if (!zipDownFile.exists()) {
            if (!zipDownFile.getParentFile().exists()) {
                zipDownFile.getParentFile().mkdirs();
            }
        } else {
            zipDownFile.delete();
        }

        try {

            file.transferTo(zipDownFile);

            // 解压缩zip文件
            ZipFile zip = new ZipFile(zipDownFile, 1, Charset.forName("GBK"));
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

            int count = 0;
            byte[] buffer = new byte[1024];

            //循环对压缩包里的每一个文件进行解压
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                String entryFileName = entry.getName();
                if (!entry.isDirectory()) {
                    entryFileName = entryFileName.substring(entryFileName.lastIndexOf("/") + 1);
                    if (!logoPathMap.containsKey(entryFileName)) {
                        errorList.add(entryFileName + " 未找到相应路径  ");
                        successAll = false;
                        continue;
                    }

                    // 根据文件名称获取文件路径
                    String logoFilePath = webappRoot + logoPathMap.get(entryFileName);

                    // 创建logo文件  并删除原来的logo文件
                    entryFile = new File(logoFilePath);
                    if (entryFile.exists()) {
                        entryFile.delete();
                    }

                    //写入文件
                    bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                    bis = new BufferedInputStream(zip.getInputStream(entry));

                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        bos.write(buffer, 0, count);
                    }
                    bos.flush();
                    bos.close();
                    bis.close();
                    successCount++;
                }
            }
        } catch (IOException e) {
            result.put("code", "1");
            successAll = false;
            e.printStackTrace();
        } finally {
            zipDownFile.delete();
        }
        result.put("successAll", successAll);
        result.put("successCount", successCount);
        result.put("errorList", errorList);
        return result;
    }

    /**
     * 上传使用手册路由
     *
     * @param file 压缩文件
     * @return
     */
    public JSONObject uploadHandbookRoot(MultipartFile file) {
        JSONObject result = new JSONObject();
        result.put("code", "0");

        if (StringUtils.isNotEmpty(handBookAddressList)) {
            logger.info("============上传使用文件路由: " + handBookAddressList);
            String[] handBookArr = handBookAddressList.split(",");

            FileHttpClient client = new FileHttpClient();

            // 上传文件的参数  使用base64编码
            Map<String, String> params = new HashMap<>();
            params.put("fileName", file.getOriginalFilename());

            // 将文件上传到配置文件配置的服务器上
            for (String handBookAddress : handBookArr) {
                String url = "http://" + handBookAddress + "/files/uploadHandbook";
                String decryptResult = client.postMultipartFile(url, file, params);
                if (StringUtils.isNotEmpty(decryptResult)) {
                    logger.info("============上传使用文件路由: " + decryptResult);
                    JSONObject res = JSON.parseObject(decryptResult);
                    if (!StringUtils.equals(res.getString("code"), "0")) {
                        result.put("code", "1");
                        return result;
                    }
                }
            }

            // 将文件名称存放到表中
            DocConfigure config = new DocConfigure();
            config.setId("zddddddd2b3c11e8aacf429ff426666");
            config.setConfigValue(file.getOriginalFilename());
            docConfigureService.updateById(config);
        }
        return result;
    }

    /**
     * 上传使用手册
     *
     * @param file 压缩文件
     * @return
     */
    @Override
    public JSONObject uploadHandbook(MultipartFile file) {
        JSONObject result = new JSONObject();
        result.put("code", "0");

        // 获取项目根目录
        String classpath = this.getClass().getResource("/").getPath();
        String webappRoot = classpath.replaceAll("target/classes/", "/src/main/webapp/");

        String logoFilePath = webappRoot + "static/handbook" +  File.separator + file.getOriginalFilename();
        File localFile = new File(logoFilePath);
        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }

        // 删除原来的文件
        if (localFile.exists()) {
            localFile.delete();
        } else { // 如果现在上传的文件和原来的文件名称不相同,从数据库取出原来文件的名称
            DocConfigure docConfigure = docConfigureService.getById("zddddddd2b3c11e8aacf429ff426666");
            if (docConfigure != null) {
                String oldFileName = docConfigure.getConfigValue();
                String oldFilePath = webappRoot + File.separator + oldFileName;
                File oldFile = new File(oldFilePath);
                if (oldFile.exists()) {
                    oldFile.delete();
                }
            }
        }

        try {
            file.transferTo(localFile);
        } catch (IOException e) {
            result.put("code", "1");
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public byte[] downloadYYZC(String docIds, String docName, HttpServletRequest request,
                               HttpServletResponse response, String userId) throws IOException, ServiceException {
        return fileDownYYZC(docIds, docName, request, response, userId);
    }

    @Override
    public void downloadMobile(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException {
        fileDownNewMobile(docIds, docName, request, response, userId, orgId);
    }

    @Override
    public void downloadClient(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException {
        fileDownNewClient(docIds, docName, request, response, userId, orgId);
    }

    /**
     * 文件下载
     *
     * @param docIds
     * @param docName
     * @param request
     * @param response
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public void downloadByShare(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        fileDownNewByShare(docIds, docName, request, response);
    }

    private Object fileDownNewByShare(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            return "error";
        }
        if (docInfoList.size() > 1) {
            //打包下载
            downLoadZipFile(request, response, docInfoList);
        } else if (docInfoList.size() == 1) {
            downLoadFile(request, response, docInfoList.get(0));
        }

        return "sunccess";
    }

    /**
     * 检查部门存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author wangning
     */
    @Override
    public Map<String, Object> checkDeptSpace(String fileSize) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String userId = ShiroKit.getUser().getId();
        ShiroUser shiroUser = UserInfoUtil.getCurrentUser();
        Integer adminFlag = CommonUtil.getAdminFlag(shiroUser.getRolesList());
        if (adminFlag != 1) {
            String deptId = shiroUser.getDeptId();
            SysStru sysStru = sysStruMapper.selectById(deptId);
            String orgId = sysStru.getOrganId();
            Double deptSpace = 0.0;
            if (docSpaceService.getDocSpaceByDeptId(orgId) == null) {
                deptSpace = 50.0;
            } else {
                deptSpace = docSpaceService.getDocSpaceByDeptId(orgId).getSpaceSize();
            }

            Double newSize = StringUtil.getDouble(fileSize.substring(0, fileSize.length() - 2));

            Double emptySize = 0d;
            double usedSpace = cacheToolService.getDeptUsedSpace(orgId);
            if (deptSpace == null) {
                //空间不足
                deptSpace = 0d;
            }
            emptySize = deptSpace * 1024 * 1024 - usedSpace - newSize;
            if (emptySize < 0) {
                //空间不足
                resultMap.put("flag", false);
                resultMap.put("size", 0d);
            } else {
                //更新缓存中已用空间数
                cacheToolService.updateDeptUsedSpace(userId, newSize);
                resultMap.put("flag", true);
                resultMap.put("size", 0d - newSize);
            }
        } else {
            resultMap.put("flag", true);
            resultMap.put("size", 0d);
        }

        return resultMap;
    }


    /**
     * 检查个人存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author lishilin
     */
    @Override
    public Map<String, Object> checkEmpSpace(String fileSize) {
        String userId = ShiroKit.getUser().getId();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        ShiroUser shiroUser = UserInfoUtil.getCurrentUser();
        Integer adminFlag = CommonUtil.getAdminFlag(shiroUser.getRolesList());
        if (adminFlag != 1) {
            String deptId = shiroUser.getDeptId();
            SysStru sysStru = sysStruMapper.selectById(deptId);
            String orgId = sysStru.getOrganId();
            DocSpace docSpace;
            Double deptSpace;
            if (docSpaceService.getDocSpaceByDeptId(userId) == null) {
                docSpace = new DocSpace();
                docSpace.setOrganId(userId);
                docSpace.setSpaceSize(SpaceSize);
                docSpaceMapper.insert(docSpace);
                deptSpace = SpaceSize;
            } else {
                deptSpace = docSpaceService.getDocSpaceByDeptId(userId).getSpaceSize();
            }

            Double newSize = StringUtil.getDouble(fileSize.substring(0, fileSize.length() - 2));

            Double emptySize = 0d;
            double usedSpace = cacheToolService.getDeptUsedSpace(userId);
            if (deptSpace == null) {
                //空间不足
                deptSpace = 0d;
            }
            emptySize = deptSpace * 1024 - usedSpace - newSize;
            if (emptySize < 0) {
                //空间不足
                resultMap.put("flag", false);
                resultMap.put("size", 0d);
            } else {
                //更新缓存中已用空间数
                cacheToolService.updateDeptUsedSpace(userId, newSize);
                resultMap.put("flag", true);
                resultMap.put("size", 0d - newSize);
            }
        } else {
            resultMap.put("flag", true);
            resultMap.put("size", 0d);
        }

        return resultMap;
    }

    /**
     * 文档下载方法
     *
     * @param docIds   文档ID（多个）
     * @param docName
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    private Object fileDownNew(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId =docFoldAuthorityService.getDeptIds( ShiroKit.getUser().getDeptId());
        List<Map> list = fsFileService.getInfo(docIdList, userId, listGroup, levelCode, orgId,ShiroKit.getUser().getRolesList());

        if (!checkDownLoadAuthority(list, userId)) {
            //LOGGER.error("没有权限下载此文件");
            return "error";
        }

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            return "error";
        }

        if (docInfoList.size() > 1) {
            //打包下载
            downLoadZipFile(request, response, docInfoList);
        } else if (docInfoList.size() == 1) {
            downLoadFile(request, response, docInfoList.get(0));
        }

        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            userId = UserInfoUtil.getUserInfo().get("ID").toString();
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            // 循环修改文件下载次数
            if(docDownloadInfoList!=null && docDownloadInfoList.size()>0){
                for (DocResourceLog docResourceLog: docDownloadInfoList){
                    docInfoService.updateOneDataDownloadNum(docResourceLog);
                }
            }
        }

        return "sunccess";
    }
    private Map fileDownNew1(String docIds, String docName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Map<String,Object> map = new HashMap();
        map.put("code","0");//0,失败   1，成功  2,没有下载权限  3，文件未找到
        map.put("fileData",null);
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(roleList);
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = ShiroKit.getUser().getDeptName();
        List<Map> list = fsFileService.getInfo(docIdList, userId, listGroup, levelCode, orgId, roleList);

        if (!checkDownLoadAuthority(list, userId)) {
            //LOGGER.error("没有权限下载此文件");
            map.put("code","2");
            return map;
        }

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            map.put("code","3");
            return map;
        }

        if (docInfoList.size() > 1) {
            //打包下载
            Map map1 = downLoadZipFileNew(request, response, docInfoList);
            map.put("code","0");
            map.put("fileData",map1);
        } else if (docInfoList.size() == 1) {
            downLoadFile(request, response, docInfoList.get(0));
            map.put("code","0");
        }

        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            userId = UserInfoUtil.getUserInfo().get("ID").toString();
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            // 循环修改文件下载次数
            if(docDownloadInfoList!=null && docDownloadInfoList.size()>0){
                for (DocResourceLog docResourceLog: docDownloadInfoList){
                    docInfoService.updateOneDataDownloadNum(docResourceLog);
                }
            }
        }
        return map;
    }

    private byte[] fileDownYYZC(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            return null;
        }

        byte[] bytes = null;

        bytes = downLoadFileYYZC(request, response, docInfoList.get(0));


        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }

        return bytes;
    }

    private Object fileDownNewClient(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        List<String> listGroup = docGroupService.getPremission(userId);
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(roleList);
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId, fsFolderParams);
        List<Map> list = fsFileService.getInfo(docIdList, userId, listGroup, levelCode, orgId,roleList);

        if (!checkDownLoadAuthorityClient(list, userId)) {
            //LOGGER.error("没有权限下载此文件");
            return "error";
        }

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            return "error";
        }

        downLoadFile(request, response, docInfoList.get(0));

        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLogClient(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }

        return "success";
    }

    private Object fileDownNewMobile(String docIds, String docName, HttpServletRequest request, HttpServletResponse response, String userId, String orgId) throws IOException, ServiceException {
        List<String> docIdList = Arrays.asList(docIds.split(","));
        List<String> listGroup = docGroupService.getPremission(userId);
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadMobile(userId, fsFolderParams);
        List<Map> list = fsFileService.getInfo(docIdList, userId, listGroup, levelCode, orgId,sysUserRoleService.getRolesByUserId(userId));

//        if (!checkDownLoadAuthority(list,userId)) {
//            //LOGGER.error("没有权限下载此文件");
//            return "error";
//        }

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            return "error";
        }

        downLoadFile(request, response, docInfoList.get(0));

        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }

        return "sunccess";
    }

    /**
     * 上传文件
     *
     * @param file    文件表实体
     * @param docInfo 文档信息表实体
     * @return void
     */
    public void uploadFile(MultipartFile file, DocInfo docInfo, FsFile fsFile, List<DocResourceLog> resInfoList,
                           List<DocFileAuthority> list, List<String> indexList) throws Exception {
        //上传到应用服务器后的文件路径
        String sourcePath = null;
        //转换成的pdf路径
        String targetPath = null;
        // 临时文件
        File tempFile = null;
        //PDF文件
        File pdfFile = null;
        //PDF临时文件
        File pdfTempFile = null;

        //文件ID
        String fileId = docInfo.getFileId();
        String docId = docInfo.getDocId();

        try {
            // 临时文件生成
            tempFile = new File(tempdir, fileId);
            if (!tempFile.getParentFile().exists()) {
                // 路径不存在,创建
                tempFile.getParentFile().mkdirs();
            }
            //将MultipartFile 转换生成 File
            transFile(file, tempFile);

            sourcePath = tempFile.getAbsolutePath();
            targetPath = tempFile.getParentFile() + File.separator
                    + tempFile.getName().substring(0, tempFile.getName().lastIndexOf(".")) + ".pdf";

            //转PDF文件并添加水印
            //pdfInfo中  content:文件内容
            Map<String, Object> pdfInfo = pdfService.changeToPdf(sourcePath, targetPath, file.getSize(), file.getContentType(), fileId);
            pdfTempFile = new File(targetPath);
            if (!pdfTempFile.getParentFile().exists()) {
                // 路径不存在,创建
                pdfTempFile.getParentFile().mkdirs();
            }
            //pdf文件路径（上传到FAStFDS上返回的路径）
            String pdfPath = null;
            //添加水印后的PDF路径markedpdfPath
            String markedpdfPath = String.valueOf(pdfInfo.get("markPdfPath"));
            //转化标志 boolean pdfFlag = Boolean.valueOf(pdfInfo.get("flag").toString());
            String targetTempPath = String.valueOf(pdfInfo.get("targetPath"));

            //上传源文件到fastdfs
            String filePath = "";

            if (fastdfsUsingFlag) {
                //启用FASTDFS时将文件上传到服务器
                filePath = fastdfsService.uploadFile(tempFile);
            } else {
                filePath = sourcePath;
            }

            //上传PDF到FastFDS -- 暂时注释打水印的上传
            if (!StringUtil.checkIsEmpty(markedpdfPath)) {
                pdfFile = new File(markedpdfPath);
                if (!pdfFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    pdfFile.getParentFile().mkdirs();
                }
                //如果markedpdfPath（打水印后pdf路径） 不为空
                if (fastdfsUsingFlag) {
                    //启用FASTDFS时将文件上传到服务器
                    pdfPath = fastdfsService.uploadFile(pdfFile);
                } else {
                    pdfPath = markedpdfPath;
                }
            }

            // 文件信息插入数据库
            fsFile.setFilePath(filePath);
            fsFile.setFilePdfPath(pdfPath);
            filesMapper.insert(fsFile);

            // 文档信息插入数据库
            docInfo.setFileId(docId);

            // 校验敏感词后再插入   added by  wangbinbin  begin
            String content = StringUtil.getString(pdfInfo.get("content"));

            // 不为空，则包含敏感词
            // 校验敏感词后再插入   added by  wangbinbin  end
            docInfoMapper.insert(docInfo);

            //保存文档权限
            if (list != null && list.size() > 0) {
                //完全公开文档上传时，文档权限记录list为0，不插入
                docFileAuthorityService.saveBatch(list);
            }
            //保存对文件的操作历史
            docInfoService.insertResourceLog(resInfoList);    //上传记录

            //生成ES索引

            // 校验敏感词后再插入   modified by  wangbinbin  begin
            DocES docEs = generateDocES(docInfo, pdfInfo, indexList);

            esService.createESIndex(docEs);
            // 校验敏感词后再插入   modified by  wangbinbin  end
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件上传失败。");
        } finally {
            //删除临时文件
            deleteFile(tempFile);
            deleteFile(pdfTempFile);
            deleteFile(pdfFile);
        }
    }

    /**
     * 文档上传相关信息组装
     *
     * @param file
     * @param uploadParams
     * @param docInfo
     * @param fsFile
     * @param resInfoList
     * @param authoritylist
     * @param indexList
     * @return
     */
    private Map<String, Object> getUploadInfo(MultipartFile file, DocUploadParams uploadParams, DocInfo docInfo, FsFile fsFile,
                                              List<DocResourceLog> resInfoList, List<DocFileAuthority> authoritylist, List<String> indexList) {
        Map<String, Object> resultMap = new HashMap<String, Object>(16);

        String fileId = file.getOriginalFilename();
        if (fileId.contains(DocConstant.SPECIALCHAR.DOUBLESLASH.getValue())) {
            fileId = fileId.substring(fileId.lastIndexOf(DocConstant.SPECIALCHAR.DOUBLESLASH.getValue()) + 1);
        }
        // 获取上传文件有关信息
        Map<String, String> docInfoMap = FileTool.fileUploadInfo(file, "", fileId);

        List<Map<String, Object>> dataList = JSON.parseObject(uploadParams.getUploadData(), new TypeReference<List<Map<String, Object>>>() {
        });

        // 上传的附加信息集合
        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            if (map.get("docName").toString().equals(docInfoMap.get("docName") + docInfoMap.get("type"))) {
                dataMap = dataList.get(i);
            }
        }

        Map<String, Object> deptSpaceIsFreeMap = checkEmpSpace(StringUtil.getString(dataMap.get("size")));

        Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
        Double releaseSize = StringUtil.getDouble(deptSpaceIsFreeMap.get("size"));

        if (!deptSpaceIsFree) {
            //部门存储空间不足
            resultMap.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
            return resultMap;
        }

        // 获取当前登录用户的用户名信息
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        //生成文档ID
        String docId = uploadParams.getDocId();

        //组装docInfo信息
        generateDocInfo(docId, userId, fileId, ts, dataMap, docInfoMap, uploadParams, docInfo);

        //拼装fs_file表中的信息
        generateFsFile(docId, ts, dataMap, docInfoMap, fsFile);

        //拼装操作历史记录
        DocResourceLog docResourceLog = generateDocResourceLog(docId, userId, ts, 0, "1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);

        //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
        if (DocConstant.NUMBER.ZERO.getName().equals(uploadParams.getVisible())) {
            indexList.add("1");
        } else {
            generateAuthorityList(docId, authoritylist, indexList, uploadParams.getGroup(), 1);
            generateAuthorityList(docId, authoritylist, indexList, uploadParams.getPerson(), 0);
        }

        resultMap.put("releaseSize", releaseSize);
        return resultMap;
    }

    /**
     * 文件打包下载
     *
     * @param request
     * @param response
     * @param docInfoList 文档信息list
     * @throws IOException 压缩文件创建异常
     */
    private void downLoadZipFile(HttpServletRequest request, HttpServletResponse response, List<DocInfo> docInfoList)
            throws IOException {
        // 压缩文件的文件名
        String zipName = generateZipFileName();
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipName;
        // 根据临时的zip压缩包路径，创建zip文件
        File zip = new File(zipDownloadPath);
        // 如果压缩文件不存在的话创建它
        if (!zip.exists()) {
            if (!zip.getParentFile().exists()) {
                zip.getParentFile().mkdirs();
            }
            zip.createNewFile();
        }


        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zos.setEncoding("GBK");
        // 循环读取文件
        for (int i = 0; i < docInfoList.size(); i++) {
            DocInfo docInfo = docInfoList.get(i);
            String fileType = docInfo.getDocType();
            String fileName = docInfo.getTitle() + fileType;

            if (!fastdfsUsingFlag) {
                File tempFile = new File(downloadFile, fileName);

                // 标识文件是否已存在
                boolean fileExists = false;
                if(!tempFile.exists()){
                    FileUtils.copyFile(new File(docInfo.getFilePath()), tempFile);
                }else{
                    fileExists = true;
                }

                // 将每一个文件写入zip文件包内，即进行打包
                if (!tempFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    tempFile.getParentFile().mkdirs();
                }
                // 文件不存在,解密
                if(!fileExists){
                    List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                    if (list.size() != 0) {
                        if (list.get(0).getSourceKey() != null) {
                            boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                        }
                    }
                }


                FileTool.zipFile(tempFile, zos, fileName);
                tempFile.delete();
            } else {
                try {
                    File tempFile = new File(tempdir, fileName);
                    if (!tempFile.getParentFile().exists()) {
                        // 路径不存在,创建
                        tempFile.getParentFile().mkdirs();
                    }
                    if (!tempFile.exists()) {
                        // 文件不存在,下载
                        fastdfsService.download(docInfo.getFilePath(), tempFile.getAbsolutePath());
                        List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                        if (list.size() != 0) {
                            if (list.get(0).getSourceKey() != null) {
                                boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                            }
                        }
                    }
                    // 将每一个文件写入zip文件包内，即进行打包
                    FileTool.zipFile(tempFile, zos, fileName);
                    tempFile.delete();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }

        //关闭文件流
        closeFileIO(zos);
        closeFileIO(fos);

        try {
            response.setContentType(request.getServletContext().getMimeType(zip.getName()));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(zipName, "UTF-8"));
            response.getOutputStream().write(FileUtils.readFileToByteArray(zip));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            zip.delete();
        }
    }
    /**
     * 文件打包下载
     *
     * @param request
     * @param response
     * @param docInfoList 文档信息list
     * @throws IOException 压缩文件创建异常
     */
    private Map downLoadZipFileNew(HttpServletRequest request, HttpServletResponse response, List<DocInfo> docInfoList)
            throws IOException {
        // 压缩文件的文件名
        String zipName = generateZipFileName();
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipName;
        Map<String,Object> map = new HashMap();
        // 根据临时的zip压缩包路径，创建zip文件
        File zip = new File(zipDownloadPath);
        // 如果压缩文件不存在的话创建它
        if (!zip.exists()) {
            if (!zip.getParentFile().exists()) {
                zip.getParentFile().mkdirs();
            }
            zip.createNewFile();
        }
        map.put("filePath",zip.getPath());
        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(fos);

        String zipName1  = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("sss");
        zos.setEncoding("GBK");

        map.put("zipName",zipName);
        // 循环读取文件
        for (int i = 0; i < docInfoList.size(); i++) {
            DocInfo docInfo = docInfoList.get(i);
            String fileType = docInfo.getDocType();
            String fileName = docInfo.getTitle() + fileType;

            if (!fastdfsUsingFlag) {
                // 源文件
                File file = new File(docInfo.getFilePath());
                // 临时文件
                File tempFile = new File(tempdir, fileName);
                // 路径不存在,创建
                if (!tempFile.getParentFile().exists()) {
                    tempFile.getParentFile().mkdirs();
                }
                long copyStartTime = System.currentTimeMillis();
                // 将源文件copy到临时文件
                FileUtils.copyFile(file, tempFile);
                long copyEndTime = System.currentTimeMillis();
                long copyTime = copyEndTime - copyStartTime;
                System.out.println("复制文件用时" + dateFormat.format(copyTime) + "秒");
                // 文件解密
                List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                if (list.size() != 0) {
                    if (list.get(0).getSourceKey() != null) {
                        boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                    }
                }

                // 下载
                FileTool.zipFile(tempFile, zos, fileName);

                tempFile.delete();

            } else {
                try {
                    File tempFile = new File(tempdir, fileName);
                    if (!tempFile.getParentFile().exists()) {
                        // 路径不存在,创建
                        tempFile.getParentFile().mkdirs();
                    }
                    if (!tempFile.exists()) {
                        // 文件不存在,下载
                        fastdfsService.download(docInfo.getFilePath(), tempFile.getAbsolutePath());
                        List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                        if (list.size() != 0) {
                            if (list.get(0).getSourceKey() != null) {
                                boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                            }
                        }
                    }
                    // 将每一个文件写入zip文件包内，即进行打包
                    FileTool.zipFile(tempFile, zos, fileName);
                    tempFile.delete();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }
        //关闭文件流
        zos.closeEntry();
        closeFileIO(zos);
        closeFileIO(fos);
        return map;
    }
    public void returnFile(HttpServletRequest request, HttpServletResponse response,String filePath,String fileName) throws IOException {
        ServletOutputStream out = null;
        FileInputStream inputStream = null;
        File file = new File(filePath);
        try {

            String userAgent = request.getHeader("User-Agent");
            // 针对IE或者以IE为内核的浏览器：
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else {
                // 非IE浏览器的处理：
                // filename = URLEncoder.encode(filename, "UTF-8");
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }

            response.setContentType(request.getServletContext().getMimeType(fileName));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));

            out = response.getOutputStream();
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 10];
            int lenth = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((lenth = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, lenth);
            }
            System.out.println("发送到浏览器");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) out.close();
                if (null != inputStream) inputStream.close();
                // 删除压缩文件
                file.delete();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 单个文件下载
     *
     * @param request
     * @param response
     * @throws IOException      压缩文件创建异常
     * @throws ServiceException fast下载文件异常
     */
    private byte[] downLoadFileYYZC(HttpServletRequest request, HttpServletResponse response, DocInfo docInfo) throws IOException, ServiceException {
        String filePath = docInfo.getFilePath();
        String fileName = docInfo.getTitle();
        String docType = docInfo.getDocType();
//		String userId = UserInfoUtil.getUserInfo().get("ID").toString();
//		List<String> roleList = ShiroKit.getUser().getRolesList();
//		Integer adminFlag = CommonUtil.getAdminFlag(roleList);
//		if(adminFlag!=1){
//			Integer integral = integralRecordService.addIntegral(docInfo.getDocId(),userId,"download");
//			Integer totalIntegral =integralRecordService.showIntegral(userId);
//			List<Map<String,Object>> list =integralRecordService.getIntegralRank();
//		}
        FileInputStream input = null;
        // 下载*/
        try {
            byte[] bytes = fileTool.downLoadFile(input, filePath, null);

            String realName = "";

            //处理FireFox下载时文件名未转化成中文问题
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                // name.getBytes("UTF-8")处理safari的乱码问题
                fileName = fileName + docType;
                byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                // 各浏览器基本都支持ISO编码
                realName = new String(sbytes, "ISO-8859-1");
            } else {
                realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName + docType, "UTF-8"));
            }

            response.setContentType(request.getServletContext().getMimeType(realName));
            response.setHeader("Content-type", "application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(bytes.length));
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", realName));
            response.getOutputStream().write(bytes);
            return bytes;
        } catch (IOException e) {
            //LOGGER.error("IO异常：" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            throw new IOException("文件下载失败IO");
        } finally {
            try {
                if (input != null) {
                    input.close();
                } else {
                    response.getOutputStream().flush();
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单个文件下载
     *
     * @param request
     * @param response
     * @throws IOException      压缩文件创建异常
     * @throws ServiceException fast下载文件异常
     */
    private void downLoadFile(HttpServletRequest request, HttpServletResponse response, DocInfo docInfo) throws IOException, ServiceException {
        System.out.println("===downLoadFile===");
        String filePath = docInfo.getFilePath();
        String fileName = docInfo.getTitle();
        String docType = docInfo.getDocType();

        FileInputStream input = null;
        ServletOutputStream out = null;

        File fileByKey = null;
        File downFile = null;

        // 下载*/
        try {
            FsFile file = fsFileMapper.selectById(docInfo.getDocId());
            String md5Random = file.getMd5();

            // 加密文件  downloadFileByKey//md5.type
            String downFileByKeyPath = downloadFileByKey + md5Random + docType;
            fileByKey = new File(downFileByKeyPath);
            // 路径不存在,创建
            if (!fileByKey.getParentFile().exists()) {
                fileByKey.getParentFile().mkdirs();
            }
            // 下载加密文件
            if (!fileByKey.exists()) {
                if (!fastdfsUsingFlag) { // 不使用fast
                    long time1 = System.currentTimeMillis();
                    FileUtils.copyFile(new File(filePath), fileByKey);
                    long time2 = System.currentTimeMillis();
                    System.out.println("===不使用fast下载文件=== " + (time2 - time1));
                } else {
                    long time1 = System.currentTimeMillis();
                    fastdfsService.download(filePath, fileByKey.getAbsolutePath());
                    long time2 = System.currentTimeMillis();
                    System.out.println("===使用fast下载文件=== " + (time2 - time1));
                }
            }

            // 解密后的文件
            String downFilePath = downloadFile + md5Random + docType;

            // 如果没有解密秘钥 则不用解密
            if (StringUtils.isEmpty(file.getSourceKey())) {
                System.out.println("===sourceKey为空===");
                downFile = fileByKey;
            } else {
                downFile = new File(downFilePath);
                // 路径不存在,创建
                if (!downFile.getParentFile().exists()) {
                    downFile.getParentFile().mkdirs();
                }
                if (!downFile.exists()) {
                    // 解密文件
                    long time1 = System.currentTimeMillis();
                    FileEncryptUtil.getInstance().decrypt(downFileByKeyPath, downFilePath, file.getSourceKey());
                    long time2 = System.currentTimeMillis();
                    System.out.println("===解密文件=== " + (time2 - time1));
                }
            }

            String realName = "";

            //处理FireFox下载时文件名未转化成中文问题
            String userAgent = request.getHeader("User-Agent");
            if (!StringUtil.checkIsEmpty(userAgent) && userAgent.toLowerCase().indexOf("firefox") > 0) {
                // name.getBytes("UTF-8")处理safari的乱码问题
                fileName = fileName + docType;
                byte[] sbytes = userAgent.contains("MSIE") ? fileName.getBytes() : fileName.getBytes("UTF-8");

                // 各浏览器基本都支持ISO编码
                realName = new String(sbytes, "ISO-8859-1");
            } else {
                realName = StringUtil.transferSpecialChar(URLEncoder.encode(fileName + docType, "UTF-8"));
            }

            long time1 = System.currentTimeMillis();
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=\"%s\"", realName));
            response.setContentType("application/download");
            response.setCharacterEncoding("UTF-8");
            out = response.getOutputStream();
            input = new FileInputStream(downFile);
            byte[] buffer = new byte[1024 * 10];
            int lenth = -1;
            // 通过循环将读入的Word文件的内容输出到浏览器中
            while ((lenth = input.read(buffer)) != -1) {
                out.write(buffer, 0, lenth);
            }
            long time2 = System.currentTimeMillis();
            System.out.println("===返回=== " + (time2 - time1));
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("文件下载失败IO");
        } finally {
            try {
                if (null != out) out.close();
                if (null != input) input.close();
                // 删除
                if (downFile != null && downFile.exists()) {
                    downFile.delete();
                }
                if (fileByKey != null && fileByKey.exists()) {
                    fileByKey.delete();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    /**
     * 文件转换
     *
     * @param fromFile
     * @param toFile
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    private File transFile(MultipartFile fromFile, File toFile) throws IllegalStateException, IOException {
        // 临时文件生成
        // 获得上传的文件
        if (!toFile.getParentFile().exists()) {// 路径不存在,创建
            toFile.getParentFile().mkdirs();
        }

        fromFile.transferTo(toFile);

        return toFile;
    }

    /**
     * 删除文件
     *
     * @param file
     */
    private void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (fastdfsUsingFlag) {
            try {
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 组装文件权限对象
     *
     * @param docId    文档ID
     * @param authorId 用户或群组ID
     * @param type     0：用户， 1：群组
     * @return
     */
    private DocFileAuthority createFileAuthority(String docId, String authorId, Integer type) {
        DocFileAuthority docFileAuthority = new DocFileAuthority();
        docFileAuthority.setFileAuthorityId(StringUtil.getUUID());
        docFileAuthority.setAuthorId(authorId);
        docFileAuthority.setAuthorType(type);
        docFileAuthority.setFileId(docId);

        return docFileAuthority;
    }

    /**
     * 组装文件权限及索引List
     *
     * @param docId         文档ID
     * @param authoritylist 权限List
     * @param indexList     索引List
     * @param authorityStr  群组或人员字符串
     * @param type          0：人员，1：群组
     */
    private void generateAuthorityList(String docId, List<DocFileAuthority> authoritylist, List<String> indexList, String authorityStr, Integer type) {
        if (!StringUtil.checkIsEmpty(authorityStr)) {
            String[] authorityArr = authorityStr.split(",");
            for (String authorId : authorityArr) {
                authoritylist.add(createFileAuthority(docId, authorId, type));
                indexList.add(authorId);
            }
        }
    }

    /**
     * 组装文档信息对象
     *
     * @param docId        文档ID
     * @param userId       用户ID
     * @param fileId       文件ID（实际上是含路径的文件名）
     * @param ts           时间戳
     * @param dataMap      上传的附加信息
     * @param docInfoMap   上传文件有关信息
     * @param uploadParams 前台传递到后台的参数信息
     * @return
     */
    private void generateDocInfo(String docId, String userId, String fileId, Timestamp ts,
                                 Map<String, Object> dataMap, Map<String, String> docInfoMap, DocUploadParams uploadParams,
                                 DocInfo docInfo) {
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(fileId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(dataMap.get("authorId").toString());

        docInfo.setContactsId(dataMap.get("contactsId").toString());
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(uploadParams.getFoldId());
        docInfo.setDocAbstract(dataMap.get("brief").toString());
        docInfo.setDocType(docInfoMap.get("type"));
        docInfo.setTitle(dataMap.get("title").toString());
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag(uploadParams.getVisible());
        docInfo.setAuthority(uploadParams.getDownloadAble());
        docInfo.setVisibleRange(Integer.parseInt(uploadParams.getVisible()));
        docInfo.setWatermarkUser(uploadParams.getWatermarkUser());
        docInfo.setWatermarkCompany(uploadParams.getWatermarkCompany());
        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");
    }

    /**
     * 组装文件信息对象
     *
     * @param docId      文档ID
     * @param ts         时间戳
     * @param dataMap    上传的附加信息
     * @param docInfoMap 上传文件有关信息
     * @return
     */
    private void generateFsFile(String docId, Timestamp ts, Map<String, Object> dataMap, Map<String, String> docInfoMap, FsFile fsFile) {
        fsFile.setCreateTime(ts);
        fsFile.setFileIcon("");
        fsFile.setFileId(docId);
        fsFile.setFileName(dataMap.get("title").toString());
        fsFile.setFileSize(dataMap.get("size").toString());
        fsFile.setFileType(docInfoMap.get("type"));
    }

    /**
     * 组装文档资源日志对象
     *
     * @param docId  文档ID
     * @param userId 用户ID
     * @param ts     时间戳
     * @return
     */
    private DocResourceLog generateDocResourceLogClient(String docId, String userId, Timestamp ts, Integer operateType, String validFlag) {
        DocResourceLog docResourceLog = new DocResourceLog();
        docResourceLog.setId(StringUtil.getUUID());
        docResourceLog.setResourceId(docId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setOrigin("client");
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(operateType);
        docResourceLog.setValidFlag(validFlag);

        return docResourceLog;
    }

    /**
     * 组装文档资源日志对象
     *
     * @param docId  文档ID
     * @param userId 用户ID
     * @param ts     时间戳
     * @return
     */
    private DocResourceLog generateDocResourceLog(String docId, String userId, Timestamp ts, Integer operateType, String validFlag) {
        DocResourceLog docResourceLog = new DocResourceLog();
        docResourceLog.setId(StringUtil.getUUID());
        docResourceLog.setResourceId(docId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(operateType);
        docResourceLog.setValidFlag(validFlag);

        return docResourceLog;
    }

    private DocES generateDocES(DocInfo docInfo, Map<String, Object> pdfInfo, List<String> indexList) {
        DocES docES = new DocES();
        docES.setId(docInfo.getDocId());
        docES.setTitle(docInfo.getTitle());
        docES.setRecycle("1");
        docES.setContentType(StringUtil.getString(pdfInfo.get("contentType")));
        docES.setUpDate(new Date());
        //	docES.setPermission(indexList.toArray(new String[indexList.size()]));
        docES.setContent(StringUtil.getString(pdfInfo.get("content")));

        return docES;
    }

    /**
     * 检查是否有下载权限
     *
     * @param docfileIdList 文档List
     * @return true:有，false:无
     */
    private Boolean checkDownLoadAuthorityClient(List<Map> docfileIdList, String userId) {
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag == DocConstant.ADMINFLAG.WKADMIN.getValue()) {
            return true;
        }


        boolean authorityAble = true;
        // 遍历下载权限问题
        for (Map map : docfileIdList) {
            String folderId = (String) map.get("folderId");
            FsFolder ff = iFsFolderService.getById(folderId);
            if (ff.getCreateUserId().equals(userId)) {
                authorityAble = true;
                break;
            }
            if (map.get("authority") == null) {
                authorityAble = false;
                break;
            }
            if (map.get("authority").equals("0")) {
                authorityAble = false;
                break;
            }
        }
        return authorityAble;
    }

    /**
     * 检查是否有下载权限
     *
     * @param docfileIdList 文档List
     * @return true:有，false:无
     */
    private Boolean checkDownLoadAuthority(List<Map> docfileIdList, String userId) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (adminFlag == DocConstant.ADMINFLAG.WKADMIN.getValue()) {
            return true;
        }


        boolean authorityAble = true;
        // 遍历下载权限问题
        for (Map map : docfileIdList) {
            String folderId = (String) map.get("folderId");
            FsFolder ff = iFsFolderService.getById(folderId);
            if (ff == null) {
                authorityAble = true;
                break;
            }
            if (ff.getCreateUserId().equals(userId)) {
                authorityAble = true;
                break;
            }
            if (map.get("authority") == null) {
                authorityAble = false;
                break;
            }
            if (map.get("authority").equals("0")) {
                authorityAble = false;
                break;
            }
        }
        return authorityAble;
    }

    /**
     * 组装zip文件文件名
     *
     * @return
     */
    private String generateZipFileName() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date()) + StringUtil.getString(Math.round(Math.random() * 100)) + ".zip";
    }

    /**
     * 关闭文件流
     *
     * @param io
     */
    private void closeFileIO(Object io) {
        if (io == null) {
            return;
        }
        if (io instanceof ZipOutputStream) {
            try {
                ((ZipOutputStream) io).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (io instanceof FileOutputStream) {
            try {
                ((FileOutputStream) io).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传文件
     *
     * @return void
     */
    public void uploadFile(File file, DocInfo docInfo, FsFile fsFile, List<DocResourceLog> resInfoList,
                           List<DocFileAuthority> list, List<String> indexList, String contentType) throws Exception {
        //上传到应用服务器后的文件路径
        String sourcePath = null;
        //转换成的pdf路径
        String pdfFilePath = null;
        //PDF文件
        File pdfFile = null;

        String pdfPath = null;

        File newFile = null;
        //新生成的缩略图
        String thumbNewPath = null;
        File newThumb = null;
        String thumbNewPathFast = null;
        boolean isCreateThumb = false;
        String recycle = "1";
        // 判断文档是否需要审核
        boolean auditState = false;
        //文件ID
        String docId = docInfo.getDocId();
        boolean isImg = false;
        try {
            DocES docVO = new DocES();

            //如果是图片则在这个地方存入文件类型信息
            if (isImg) {
                docVO.setContentType(contentType);
            }
            docVO.setId(docId);
            docVO.setTitle(docInfo.getTitle());
//            docVO.setTitleSuggest(docInfo.getTitle());
//            FsFolder fsFolder = new FsFolder();
//            if (docInfo.getFoldId() != null) {
//                fsFolder = iFsFolderService.selectById(docInfo.getFoldId());
//            }
            String folderId = docInfo.getFoldId();
            FsFolder fsFolder = fsFolderService.getById(folderId);
            if ("true".equals(using)) {
                if (ToolUtil.isNotEmpty(fsFolder)) {
                    String auditFlag = fsFolder.getAuditFlag();
                    if ("1".equals(auditFlag)) {
                        // 待审核
                        docInfo.setValidFlag("2");
                        recycle = "2";
                        auditState = true;
                    }
                }
            }
            if (fsFolder != null) {
                docVO.setCategory(fsFolder.getFolderName());
            }
            if (docInfo.getTags() != null && !"".equals(docInfo.getTags())) {
                docVO.setTags(docInfo.getTags());
            }
            docVO.setRecycle(recycle);
            docVO.setFolderId(folderId);
            docVO.setUpDate(new Date());
            if(indexList!=null && indexList.size()>0){
                docVO.setPermission(indexList.toArray(new String[indexList.size()]));
            }
            esService.createESIndex(docVO);
            logger.info("******************文件:" + docInfo.getTitle() + "生成es索引结束******************");


            sourcePath = file.getAbsolutePath().replace("\\", "/");
            pdfFilePath = pdfFileDir + File.separator
                    + file.getName().substring(0, file.getName().lastIndexOf(".")) + ".pdf";
            if (contentType != null) {
                try{
                    if (contentType.contains("image")) {
                        isImg = true;
                        pdfFilePath = pdfFileDir + File.separator
                                + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_Thumbnails.jpg";
                        createThumbnails(sourcePath, pdfFilePath);

                        thumbNewPath = ThumbnailsDir + File.separator
                                + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_Thumbnails_level_2.jpg";
                        isCreateThumb = ThumbnailsUtil.createThumbnails(sourcePath, thumbNewPath, 1080, 857);
                        if (!StringUtil.checkIsEmpty(pdfFilePath)) {
                            pdfFile = new File(pdfFilePath);
                            if (!pdfFile.getParentFile().exists()) {
                                // 路径不存在,创建
                                pdfFile.getParentFile().mkdirs();
                            }
                            logger.info("******************图片文件:" + pdfFile.getName() + "创建成功，路径为" + pdfFile.
                                    getPath() + ",大小为" + pdfFile.length() + "******************");
                            newThumb = new File(thumbNewPath);
                            if (!newThumb.getParentFile().exists()) {
                                // 路径不存在,创建
                                newThumb.getParentFile().mkdirs();
                            }
                            logger.info("******************图片缩略图文件:" + newThumb.getName() + "创建成功，路径为"
                                    + newThumb.getPath() + ",大小为" + newThumb.length() + "******************");
                            Map map = new HashMap();
                            map.put("sourceId", StringUtil.getUUID());
                            map.put("sourceLevel", "2");
                            map.put("fileId", docId);
                            if (isCreateThumb) {
                                Map newThumbInfo = ThumbnailsUtil.getHeightAndWidth(thumbNewPath);
                                String size = ThumbnailsUtil.pathSize(thumbNewPath);
                                map.put("sourceSize", size);
                                map.putAll(newThumbInfo);
                            } else {
                                Map newThumbInfo = ThumbnailsUtil.getHeightAndWidth(sourcePath);
                                String size = ThumbnailsUtil.pathSize(sourcePath);
                                map.put("sourceSize", size);
                                map.putAll(newThumbInfo);
                            }
                            //如果markedpdfPath（打水印后pdf路径） 不为空
                            //启用FASTDFS时将文件上传到服务器
                            if (fastdfsUsingFlag) {
                                String pdfFileKeyStr = pdfFileByKey + pdfFile.getName();
                                File pdfFileKey = new File(pdfFileKeyStr);
                                if (!pdfFileKey.getParentFile().exists()) {
                                    // 路径不存在,创建
                                    pdfFileKey.getParentFile().mkdirs();
                                }
                                //文件加密并取出加密密码存到数据库
                                String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile, pdfFileKey);
                                fsFile.setPdfKey(pdfKey);

                                pdfPath = fastdfsService.uploadFile(pdfFileKey);
                                logger.info("******************加密图片文件:" + pdfFileKey.getName() +
                                        "创建成功，路径为" + pdfFileKey.getPath() + ",大小为" + pdfFileKey.length() + "," +
                                        "并上传到fast，fast返回地址为" + pdfPath + "******************");

                                if (fastdfsUsingFlag) {
                                    pdfFileKey.delete();
                                    pdfFile.delete();
                                }
                                if (isCreateThumb) {
                                    String newThumbKeyStr = pdfFileByKey + newThumb.getName();
                                    File newThumbKeyFile = new File(pdfFileKeyStr);
                                    if (!newThumbKeyFile.getParentFile().exists()) {
                                        // 路径不存在,创建
                                        newThumbKeyFile.getParentFile().mkdirs();
                                    }
                                    String newThumbKey = FileEncryptUtil.getInstance().encrypt(newThumb, newThumbKeyFile);
                                    thumbNewPathFast = fastdfsService.uploadFile(newThumbKeyFile);
                                    map.put("sourceKey", newThumbKey);
                                    map.put("sourcePath", thumbNewPathFast);
                                    logger.info("******************加密缩略图文件:" + newThumbKeyFile.getName() +
                                            "创建成功，路径为" + newThumbKeyFile.getPath() + ",大小为" + newThumbKeyFile.length() + "," +
                                            "并上传到fast，fast返回地址为" + thumbNewPathFast + "******************");
                                    if(newThumbKeyFile.exists()){
                                        newThumbKeyFile.delete();
                                    }
                                    if(newThumb.exists()){
                                        newThumb.delete();
                                    }
                                }
                            } else {
                                String pdfFileKeyStr = pdfFileByKey + pdfFile.getName();
                                File pdfFileKey = new File(pdfFileKeyStr);
                                String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile, pdfFileKey);
                                fsFile.setPdfKey(pdfKey);
                                pdfPath = pdfFileKey.getPath();
                                if (isCreateThumb) {
                                    String newThumbKey = FileEncryptUtil.getInstance().encrypt(newThumb);
                                    map.put("sourceKey", newThumbKey);
                                    map.put("sourcePath", newThumb.getPath().replace("\\", "/"));
                                }
                            }
                            frontDocInfoMapper.setNewThumbInfo(map);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    logger.info("图片生成缩略图异常");
                }
            }

            //上传源文件到fastdfs
            String filePath = "";
            String newFilePath = fileByKey + File.separator
                    + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_new"
                    + file.getName().substring(file.getName().indexOf("."), file.getName().length());
            newFile = new File(newFilePath);
            if (!newFile.getParentFile().exists()) {
                // 路径不存在,创建
                newFile.getParentFile().mkdirs();
            }
            //启用FASTDFS时将文件上传到服务器
            if (fastdfsUsingFlag) {
                //文件加密并取出加密密码存到数据库
                String sourceKey = FileEncryptUtil.getInstance().encrypt(file, newFile);
                fsFile.setSourceKey(sourceKey);
                filePath = fastdfsService.uploadFile(newFile);
                logger.info("******************加密文件:" + newFile.getName() +
                        "创建成功，路径为" + newFile.getPath() + ",大小为" + newFile.length() + "," +
                        "并上传到fast，fast返回地址为" + filePath + "******************");
                if (!isCreateThumb) {
                    Map map = new HashMap();
                    map.put("fileId", docId);
                    map.put("sourceKey", sourceKey);
                    map.put("sourcePath", filePath);
                    frontDocInfoMapper.updateNewThumbInfo(map);
                }
            } else {
                if (contentType != null) {
                    if (contentType.contains("image")) {
                        String sourceKey = FileEncryptUtil.getInstance().encrypt(file, newFile);
                        fsFile.setSourceKey(sourceKey);
                        filePath = newFilePath;
                        fsFile.setFilePath(filePath);
                        if (!isCreateThumb) {
                            Map map = new HashMap();
                            map.put("fileId", docId);
                            map.put("sourceKey", sourceKey);
                            map.put("sourcePath", newFile.getPath().replace("\\", "/"));
                            frontDocInfoMapper.updateNewThumbInfo(map);
                        }
                    } else {
                        String sourceKey = FileEncryptUtil.getInstance().encrypt(file, newFile);
                        fsFile.setSourceKey(sourceKey);
                        filePath = newFilePath;
                        fsFile.setFilePath(filePath);
                    }
                } else {
                    String sourceKey = FileEncryptUtil.getInstance().encrypt(file, newFile);
                    fsFile.setSourceKey(sourceKey);
                    filePath = newFile.getPath();
                    fsFile.setFilePath(filePath);
                    Map map = new HashMap();
                    map.put("fileId", docId);
                    map.put("sourceKey", sourceKey);
                    map.put("sourcePath", newFile.getPath().replace("\\", "/"));
                    frontDocInfoMapper.updateNewThumbInfo(map);
                }
            }
            //如果是pdf则只需要一个文件即可
            if (contentType == null || contentType.contains("application/pdf")
                    || contentType.contains("audio") || contentType.contains("video")) {
                pdfPath = filePath;
                fsFile.setPdfKey(fsFile.getSourceKey());
            }
            // 文件信息插入数据库
            fsFile.setFilePath(filePath.replace("\\", "/"));
            //如果图片生成了pdfPath则做存储
            if (null != pdfPath) {
                fsFile.setFilePdfPath(pdfPath.replace("\\", "/"));
            }
            filesMapper.insert(fsFile);


            // 文档信息插入数据库
            docInfo.setFileId(docId);

            if("true".equals(analysisUsing)) {
                String docType = docInfo.getDocType();
                String fileName = docInfo.getTitle() + docType;
                if (".txt".equals(docType)) {
                    // 获取语义分析生成的标签
                    String label = semanticAnalysisService.getLabelAnalysis(fileName, file.getPath());
                    docInfo.setTags(label);
                }
            }

            Random random = new Random();
            int ends = random.nextInt(99);
            docInfo.setRandomNum(ends);
            docInfoMapper.insert(docInfo);
            //保存文档权限
            if (list != null && list.size() > 0) {
                //完全公开文档上传时，文档权限记录list为0，不插入
                docFileAuthorityService.saveBatch(list);
            }

            if (auditState) {
                if (workflowUsing) {
                    String userId = ShiroKit.getUser().getId();
                    // 开启流程 key-20210224110410326
                    Map<String, String> flowMap = processUtil.startProcess("文件审核", docId, ShiroKit.getUser().getId());
                    if ("true".equals(flowMap.get("result"))) {
                        System.out.println("==========文件: " + docInfo.getTitle() + "======开启流程成功======");
                        String taksId = flowMap.get("taskId");
                        //  完成上传文件任务
                        Map<String, String> flowMapTow = processUtil.completeProcess(taksId, userId, null, null);
                        if ("true".equals(flowMapTow.get("result"))) {
                            System.out.println("==========文件: " + docInfo.getTitle() + "======提交上传文件流程成功======");
                            DocInfo temp = new DocInfo();
                            temp.setDocId(docId);
                            temp.setProcessInstanceId(flowMap.get("processInstanceId"));// 流程实例id
                            temp.setTaskId(flowMapTow.get("taskId"));            // 任务id
                            temp.setExamineState(flowMapTow.get("nextExamineState")); // 审核员审核
                            temp.setValidFlag("2"); // 审核中
                            docInfoService.updateById(temp);
                        }
                    }
                } else {
                    // 添加文档审核信息
                    docInfoAuditService.addDocInfoAudit(folderId, docId);
                }
            }

            //保存对文件的操作历史
            docInfoService.insertResourceLog(resInfoList);
            logger.info("******************文件:" + docInfo.getTitle() + "开始生成es索引******************");
            //生成ES索引

            logger.info("******************文件:" + docInfo.getTitle() + "开始存储上传状态******************");
            //创建文件上传状态并存入缓存
            Map<String, String> toPdf = new HashMap();
            toPdf.put("docId", docId);
            toPdf.put("sourcePath", sourcePath);
            toPdf.put("contentType", contentType);
            toPdf.put("state", "1");
            toPdf.put("times", "0");
            String address = InetAddress.getLocalHost().toString().replace(".", "");
            toPdf.put("address", address);
            cacheToolService.setUploadState(toPdf);
            uploadService.newUploadState(toPdf);
            logger.info("******************文件:" + docInfo.getTitle() + "存储上传状态结束，此流程结束******************");
            if (fastdfsUsingFlag) {
                newFile.delete();
            }
            threadPoolExecutor.execute(new ChangeToPdfThread(docId));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("文件上传失败。");
        } finally {
            if (fastdfsUsingFlag) {

                if (pdfFile != null && pdfFile.exists()) {
                    pdfFile.delete();

                }
            }
        }
    }

    @Override
    public String uploadFastJqx(String categoryId, String downloadAble, String visible, String group, String person,
                                String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                String shareFlag, String userId, String topicId) {

        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag("1");
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");


        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

        // share_flag 是否可分享
        docInfo.setShareFlag(shareFlag);
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        //拼装权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
        if (("555dd41471c2461aa3391af9e8282fc1".equals(categoryId))
                || "1a5168f3a50f4f32b57bf454c5895457".equals(categoryId)
                || "2d8ed2e02fc34be2a587f7e68a848e91".equals(categoryId)
                || "339d8cd61b3746e1839ebf725aacb824".equals(categoryId)
                || "5e5d93580f3a443eb3057627be68ffa6".equals(categoryId)
                || "5f7bf94d27224db7a41e6c694def6e46".equals(categoryId)
                || "784ba4775a734014a8f251783606f23d".equals(categoryId)
                || "795aa5765d9d49e4805a88ac7fefbc8d".equals(categoryId)
                || "831f8b19eee44ad094ae21d7a7242578".equals(categoryId)
                || "85eb0d9b8b1a428f847e7f5bc8ff43b9".equals(categoryId)
                || "9670e7b36ce549ffa074f6592bbda284".equals(categoryId)
                || "a18c3d4a7fbe4de5868bb6b45eaad1e2".equals(categoryId)
                || "a63df52c36584f8c9378ebc801499977".equals(categoryId)
                || "bbb99e2f9a15474cbdcaba2d8da63db9".equals(categoryId)
                || "c4960ef211d745a497a31e7e9e2c3db0".equals(categoryId)
                || "add316b125a84d519578694955a5af36".equals(categoryId)
                || "caf4bd7d5f3e42a9aff6beae6154576f".equals(categoryId)
                || "e4daf328be6648f99281fb33b282bf62".equals(categoryId)
                || "645e6a2e155740949cdb81df3fa350b1".equals(categoryId)
                || "8c56ad616c674d7eba191e79f0cda882".equals(categoryId)) {
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
        } else if ("6b46e675e46a4a69a9c2fc93af143aae".equals(categoryId)) {
            String[] groupArr = "6091a4f767e24ba29f87719bd1b2cef8,allpersonflag,2817292a04944e3c96a800c7ad2c3857,6a7206343e4246f9b21db680dbcf1516,337babeee7a8453290146c0ce8a96478".split(",");
            String[] authorTypeStrGroup = "1,3,1,1,1".split(",");
            String[] operateTypeStrGroup = "1,0,1,1,1".split(",");
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
        } else if ("0404".equals(categoryId)) {
            String[] groupArr = ("8d6e50d29336428f8964c42248510c6d,6091a4f767e24ba29f87719bd1b2cef8," +
                    "6a7206343e4246f9b21db680dbcf1516,52ed3715582245a48ad633052ce289d3,30e66bdf2a66453cba0d38e5e7af4ae7").split(",");
            String[] authorTypeStrGroup = "1,1,1,1,1".split(",");
            String[] operateTypeStrGroup = "0,0,0,0,0".split(",");
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
        } else if ("0101".equals(categoryId)) {
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
        } else if ("0403".equals(categoryId)) {
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
        } else if ("508bf3dd2d4b408684313051a487fdcd".equals(categoryId)) {
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
        } else if ("0304".equals(categoryId)) {
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
                if (StringUtil.getInteger(authorTypeStrPerson[i]) == 0) {
                    indexList.add(personArr[i]);
                }
                if (StringUtil.getInteger(authorTypeStrPerson[i]) == 2) {
                    indexList.add(personOrganArr[i]);
                }
            }
        } else if ("900f060aa5ad49e981da10f5d619ec2a".equals(categoryId)) {
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

        } else if ("1085dd96c43f4036b0654b66e9f163af".equals(categoryId)) {
            String[] groupArr = "6a7206343e4246f9b21db680dbcf1516,30e66bdf2a66453cba0d38e5e7af4ae7".split(",");
            String[] authorTypeStrGroup = "1,1".split(",");
            String[] operateTypeStrGroup = "1,1".split(",");
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

        } else if ("8bea2cb2184c44c4a296710bad0d9673".equals(categoryId)) {
            indexList.add(userId);

        } else if ("0521e2de1a93438d9fa5ecd512fc0105".equals(categoryId)) {
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

        } else if ("5c8cee3afd0f47aba7ed4893d6ed9e66".equals(categoryId)) {
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

        } else if ("0504".equals(categoryId) || "050402".equals(categoryId) || "050403".equals(categoryId) || "7d9f267b319741ca90844efc7108db87".equals(categoryId)) {
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

        } else {
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
        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());

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
        filesMapper.insert(fileModel);

        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }


        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        int state = esService.copyIndex(fsFile.getFileId(), docId);
        if (state == -1) {
            return null;
        }
        Map map = new HashMap(4);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("tags", "");

        FsFolder fsFolder = new FsFolder();
        if (docInfo.getFoldId() != null) {
            fsFolder = iFsFolderService.getById(docInfo.getFoldId());
        }
//        case "png": case "jpg": case "gif": case "bmp": case "jpeg":
        if (".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)) {
            frontDocInfoMapper.insertThumbInfoFast(StringUtil.getUUID(), docId, md5);
        }
        map.put("folderId", fsFolder.getFolderId());
        map.put("category", fsFolder.getFolderName());
        esUtil.updateIndex(docId, map);
        return docId;

    }

    @Override
    public String uploadFastYYZC(String categoryId, String downloadAble, String visible, String group, String person,
                                 String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                 String shareFlag, String userId, String tags) {

        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        FsFolder fsFolder = fsFolderService.getById(categoryId);
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        docInfo.setReadNum(0);
        if (tags != null) {
            docInfo.setTags(tags);
        }
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag("1");
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");

        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

        // share_flag 是否可分享
        docInfo.setShareFlag(shareFlag);
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
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
        docResourceLog.setValidFlag("1");
        resInfoList.add(docResourceLog);
        //拼装权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
        if (fsFolder != null) {
            if (fsFolder.getOwnId() == null || "".equals(fsFolder.getOwnId())) {
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
        }
        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());


        filesMapper.insert(fileModel);

        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }


        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        int state = esService.copyIndex(fsFile.getFileId(), docId);
        if (state == -1) {
            return null;
        }
        Map map = new HashMap(4);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("tags", tags);
        if (docInfo.getFoldId() != null) {
            fsFolder = iFsFolderService.getById(docInfo.getFoldId());
        }
//        case "png": case "jpg": case "gif": case "bmp": case "jpeg":
        if (".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)) {
            frontDocInfoMapper.insertThumbInfoFast(StringUtil.getUUID(), docId, md5);
        }
        if (fsFolder != null) {
            map.put("category", fsFolder.getFolderName());
            map.put("folderId", fsFolder.getFolderId());
        }

        esUtil.updateIndex(docId, map);
        return docId;

    }

    @Override
    public String uploadFast(String categoryId, String downloadAble, String visible, String group, String person,
                             String watermarkUser, String md5, String fileName, FsFile fsFile, String size, String shareFlag, String userId, String fileOpen) {

        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String validFlag = "1";
        String recycle = "1";
        // 判断文档是否需要审核
        boolean auditState = false;
        FsFolder fsFolder = fsFolderService.getById(categoryId);
        if ("true".equals(using)) {
            if (ToolUtil.isNotEmpty(fsFolder)) {
                String auditFlag = fsFolder.getAuditFlag();
                if ("1".equals(auditFlag)) {
                    validFlag = "2"; // 待审核
                    recycle = "2";
                    auditState = true;
                }
            }
        }
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag(validFlag);
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");

        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

        // share_flag 是否可分享
        docInfo.setShareFlag(shareFlag);
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
        fileModel.setSize(fsFile.getSize());
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());

        resInfoList.add(docResourceLog);
        //拼装权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
        if (fsFolder != null && fileOpen != null && "1".equals(fileOpen)) {
            if (fsFolder.getOwnId() == null || "".equals(fsFolder.getOwnId())) {
                DocFileAuthority docFileAuthority = new DocFileAuthority();
                docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                docFileAuthority.setAuthorId("allpersonflag");
                //操作者类型（0：userID,1:groupID,2:roleID）
                docFileAuthority.setAuthorType(3);
                docFileAuthority.setFileId(docId);
                docFileAuthority.setAuthority(0);
                list.add(docFileAuthority);
            }
        }
        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());

        if("true".equals(analysisUsing)) {
            if (".txt".equals(suffix)) {
                // 下载文件并解密
                String path = downloadFile(fileModel);
                if (path != null) {
                    // 获取语义分析生成的标签
                    String label = semanticAnalysisService.getLabelAnalysis(fileName, path);
                    docInfo.setTags(label);
                }
            }
        }

        filesMapper.insert(fileModel);


        Random random = new Random();
        int ends = random.nextInt(99);
        docInfo.setRandomNum(ends);
        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }

        if (auditState) {
            if (workflowUsing) {
                // 开启流程
                Map<String, String> flowMap = processUtil.startProcess("文件审核", docId, ShiroKit.getUser().getId());
                if ("true".equals(flowMap.get("result"))) {
                    System.out.println("==========文件: " + docInfo.getTitle() + "======开启流程成功======");
                    String taksId = flowMap.get("taskId");
                    //  完成上传文件任务
                    Map<String, String> flowMapTow = processUtil.completeProcess(taksId, userId, null, null);
                    if ("true".equals(flowMapTow.get("result"))) {
                        System.out.println("==========文件: " + docInfo.getTitle() + "======提交上传文件流程成功======");
                        DocInfo temp = new DocInfo();
                        temp.setDocId(docId);
                        temp.setProcessInstanceId(flowMap.get("processInstanceId"));// 流程实例id
                        temp.setTaskId(flowMapTow.get("taskId"));            // 任务id
                        temp.setExamineState(flowMapTow.get("nextExamineState")); // 审核员审核
                        temp.setValidFlag("2"); // 审核中
                        docInfoService.updateById(temp);
                    }
                }
            } else {
                // 添加文档审核信息
                docInfoAuditService.addDocInfoAudit(categoryId, docId);
            }
        }

        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        int state = esService.copyIndex(fsFile.getFileId(), docId);
        if (state == -1) {
            return null;
        }


        Map map = new HashMap(4);
        //0为无效，1为有效

        if (docInfo.getTags() != null && !"".equals(docInfo.getTags())) {
            map.put("tags", docInfo.getTags());
        }

        // 图片缩略图
        if (".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)) {
            frontDocInfoMapper.insertThumbInfoFast(StringUtil.getUUID(), docId, md5);
        }

        // 视频的图
        if(".mp4".equals(suffix) || ".avi".equals(suffix) || ".mov".equals(suffix) || ".flv".equals(suffix)){
            DocVideoThumb thumb = docVideoThumbService.getById(fsFile.getFileId());
            if(thumb!=null){
                thumb.setDocId(docId);
                docVideoThumbService.save(thumb);
            }
        }

        if (fsFolder != null) {
            if (fileOpen != null && "1".equals(fileOpen)) {
                if (fsFolder.getOwnId() == null || "".equals(fsFolder.getOwnId())) {

                    indexList.add("allpersonflag");
                }
            }
            map.put("category", fsFolder.getFolderName());
            map.put("folderId", fsFolder.getFolderId());
        }
        indexList.add(userId);
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("recycle", recycle);
        map.put("id",docId);
        map.put("title",fileName.substring(0, fileName.lastIndexOf(".")));
        map.put("fileName",fileName);
        esUtil.updateIndex(docId, map);
        //esUtil.index(docId,map);

        if("true".equals(analysisUsing)) {
            if (!".txt".equals(suffix)) {
                // 转换为txt并更新标签
                getAndUpdateTags(docId);
            }
        }

        return docId;

    }

    @Override
    public String uploadFastComponent(String categoryId, String downloadAble, String visible, String group, String person,
                                      String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                      String shareFlag, String userId, String tags) {

        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致

        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        docInfo.setReadNum(0);
        if (tags != null) {
            docInfo.setTags(tags);
        }
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag("0");
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");
        // share_flag 是否可分享
        docInfo.setShareFlag("0");
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        //拼装权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        //0代表是完全公开 ，这时候往索引里面添加一个公开的权限

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

        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());


        filesMapper.insert(fileModel);

        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }


        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        int state = esService.copyIndex(fsFile.getFileId(), docId);
        if (state == -1) {
            return null;
        }
        Map map = new HashMap(4);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("tags", tags);
        FsFolder fsFolder = new FsFolder();
        if (docInfo.getFoldId() != null) {
            fsFolder = iFsFolderService.getById(docInfo.getFoldId());
        }
//        case "png": case "jpg": case "gif": case "bmp": case "jpeg":
        if (".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)) {
            frontDocInfoMapper.insertThumbInfoFast(StringUtil.getUUID(), docId, md5);
        }
        if (fsFolder != null) {
            map.put("category", fsFolder.getFolderName());
            map.put("folderId", fsFolder.getFolderId());
        }

        esUtil.updateIndex(docId, map);
        return docId;

    }

    @Override
    public String uploadVersionFastClient(String categoryId, String downloadAble, String visible, String group, String person,
                                          String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                          String shareFlag, String oldDocId, String userId) {
        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 判断version表中有无旧版本数据
        DocInfo oldDocInfo = docInfoService.getOne(new QueryWrapper<DocInfo>().eq("doc_id", oldDocId));
        if (docVersionService.count(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId)) == 0) {
            DocVersion oldVersion = new DocVersion();
            oldVersion.setDocId(oldDocId);
            // 随机生成UUID作为版本关联字段
            oldVersion.setVersionReference(UUID.randomUUID().toString().replace("-", ""));
            // 版本有效性，默认有效
            oldVersion.setValidFlag("1");
            oldVersion.setApplyTime(oldDocInfo.getCreateTime());
            oldVersion.setApplyUserId(oldDocInfo.getUserId());
            oldVersion.setVersionNumber(1);
            // 将旧版本信息插入版本关联表
            docVersionService.save(oldVersion);
        }
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        // 继承旧版本的浏览量、下载量、标签数据
        docInfo.setReadNum(oldDocInfo.getReadNum());
        docInfo.setDownloadNum(oldDocInfo.getDownloadNum());
        docInfo.setTags(oldDocInfo.getTags());

        docInfo.setValidFlag(visible);
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");
        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");
        // share_flag 是否可分享
        docInfo.setShareFlag(oldDocInfo.getShareFlag());
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        //继承权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        list = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id", oldDocId));
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

        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());

        filesMapper.insert(fileModel);

        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }

        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        esService.copyIndex(fsFile.getFileId(), docId);
        Map map = new HashMap(4);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("recycle", "1");
        map.put("tags", "");
        FsFolder fsFolder = new FsFolder();
        if (docInfo.getFoldId() != null) {
            fsFolder = iFsFolderService.getById(docInfo.getFoldId());
        }
        map.put("category", fsFolder.getFolderName());
        map.put("folderId", fsFolder.getFolderId());
        esUtil.updateIndex(docId, map);
        // 秒传成功后将旧版本设为无效（docinfo表）
        docInfoService.updateValidFlag(oldDocId, "0");
        // 将es中该文件设为不可检索
        Map map1 = new HashMap(1);
        //0为无效，1为有效
        map1.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
        esUtil.updateIndex(oldDocId, map1);
        // 上传成功后将新版本的数据插入version表中
        DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId));
        int versionNumber = docVersionService.selectVersionNumber(oldVersion.getVersionReference());
        DocVersion newVersion = new DocVersion();
        newVersion.setVersionReference(oldVersion.getVersionReference());
        newVersion.setDocId(docInfo.getDocId());
        newVersion.setValidFlag("1");
        newVersion.setApplyTime(docInfo.getCreateTime());
        newVersion.setApplyUserId(docInfo.getUserId());
        newVersion.setVersionNumber(versionNumber + 1);
        docVersionService.save(newVersion);
        return docId;
    }

    @Override
    public String uploadVersionFast(String categoryId, String downloadAble, String visible, String group, String person,
                                    String watermarkUser, String md5, String fileName, FsFile fsFile, String size,
                                    String shareFlag, String oldDocId) {
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 判断version表中有无旧版本数据
        DocInfo oldDocInfo = docInfoService.getOne(new QueryWrapper<DocInfo>().eq("doc_id", oldDocId));
        if (docVersionService.count(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId)) == 0) {
            DocVersion oldVersion = new DocVersion();
            oldVersion.setDocId(oldDocId);
            // 随机生成UUID作为版本关联字段
            oldVersion.setVersionReference(UUID.randomUUID().toString().replace("-", ""));
            // 版本有效性，默认有效
            oldVersion.setValidFlag("1");
            oldVersion.setApplyTime(oldDocInfo.getCreateTime());
            oldVersion.setApplyUserId(oldDocInfo.getUserId());
            oldVersion.setVersionNumber(1);
            // 将旧版本信息插入版本关联表
            docVersionService.save(oldVersion);
        }
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        DocInfo docInfo = new DocInfo();
        String docId = UUID.randomUUID().toString().replace("-", "");
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        // 继承旧版本的浏览量、下载量、标签数据
        docInfo.setReadNum(oldDocInfo.getReadNum());
        docInfo.setDownloadNum(oldDocInfo.getDownloadNum());
        docInfo.setTags(oldDocInfo.getTags());

        docInfo.setValidFlag(visible);
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");
        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");
        // share_flag 是否可分享
        docInfo.setShareFlag(oldDocInfo.getShareFlag());
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        //继承权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        list = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id", oldDocId));
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

        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());

        filesMapper.insert(fileModel);

        Random random = new Random();
        int ends = random.nextInt(99);
        docInfo.setRandomNum(ends);
        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }

        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        esService.copyIndex(fsFile.getFileId(), docId);
        Map map = new HashMap(4);
        //0为无效，1为有效
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("recycle", "1");
        map.put("tags", "");
        FsFolder fsFolder = new FsFolder();
        if (docInfo.getFoldId() != null) {
            fsFolder = iFsFolderService.getById(docInfo.getFoldId());
        }
        map.put("category", fsFolder.getFolderName());
        map.put("folderId", fsFolder.getFolderId());
        esUtil.updateIndex(docId, map);
        // 秒传成功后将旧版本设为无效（docinfo表）
        docInfoService.updateValidFlag(oldDocId, "0");
        // 将es中该文件设为不可检索
        Map map1 = new HashMap(1);
        //0为无效，1为有效
        map1.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
        esUtil.updateIndex(oldDocId, map1);
        // 上传成功后将新版本的数据插入version表中
        DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId));
        int versionNumber = docVersionService.selectVersionNumber(oldVersion.getVersionReference());
        DocVersion newVersion = new DocVersion();
        newVersion.setVersionReference(oldVersion.getVersionReference());
        newVersion.setDocId(docInfo.getDocId());
        newVersion.setValidFlag("1");
        newVersion.setApplyTime(docInfo.getCreateTime());
        newVersion.setApplyUserId(docInfo.getUserId());
        newVersion.setVersionNumber(versionNumber + 1);
        docVersionService.save(newVersion);
        return docId;
    }

    private void createThumbnails(String sourcePath, String targetPath) throws Exception {
        double toWidth = 290;

        BufferedImage imageList = getImageList(sourcePath, new String[]{"jpg", "png", "gif", "bmp","jpeg","ai","pdd"});
        double oldWidth = imageList.getWidth();
        double oldHeight = imageList.getHeight();
        if (oldWidth / oldHeight > 4) {
            double toHeight = 200;
            toWidth = toHeight / oldHeight * oldWidth;
            writeHighQuality(targetPath, zoomImage(imageList, (int) toWidth, (int) toHeight));
        } else {
            double toHeight = toWidth / oldWidth * oldHeight;
            writeHighQuality(targetPath, zoomImage(imageList, (int) toWidth, (int) toHeight));
        }

    }

    /**
     * @param
     * @Description: 取得图片对象
     * @date 2017年5月7日10:48:27要转化的图像的文件夹,就是存放图像的文件夹路径
     */
    private BufferedImage getImageList(String ImgList, String[] type) throws IOException {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (String s : type) {
            map.put(s, true);
        }
        BufferedImage imageList = null;
        File file = null;
        file = new File(ImgList);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            if (file.length() != 0 && map.get(getExtension(file.getName())) != null) {
                imageList = ImageIO.read(file);
            }
        } catch (Exception e) {
            imageList = null;
        }

        return imageList;
    }

    private String getExtension(String fileName) {
        try {
            return (fileName.split("\\.")[fileName.split("\\.").length - 1]).toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @param
     * @Description: 生成图片
     * @date 2017年5月7  日10:48:27
     */
    private boolean writeHighQuality(String path, BufferedImage im) throws IOException {
        //return true;
        FileOutputStream newimage = null;
        try {
            // 输出到文件流
            newimage = new FileOutputStream(path);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
            // 压缩质量
            jep.setQuality(1f, true);
            encoder.encode(im, jep);
            //近JPEG编码
            newimage.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @param
     * @Description: 取得图片对象
     * @date 2017年5月7日10:48:27
     */
    private BufferedImage zoomImage(BufferedImage im, int toWidth, int toHeight) {
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return result;
    }

    /**
     * 下载文件并解密
     *
     * @param fsFileTemp 文件信息
     */
    @Override
    public String downloadFile(FsFile fsFileTemp) {
        String path = null;
        byte[] bytes = null;
        FileOutputStream fos = null;
        FileInputStream input = null;
        File file = null;
        File fileKey = null;
        try {
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
                path = downloadFile + random + suffix;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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
        }
        return path;
    }

    /**
     * 文档转pdf
     *
     * @param docId 文档id
     */
    public String changeToTxt(String docId) {
        String path = null;
        String typeRange = ".doc,.docx,.ppt,.pptx,.pdf,.xls,.xlsx";
        DocInfo docInfo = docInfoService.getById(docId);
        if (ToolUtil.isNotEmpty(docInfo)) {
            String docType = docInfo.getDocType();
            if (typeRange.contains(docType)) {
                Map<String, Object> result = esUtil.getIndex(docId);
                if (ToolUtil.isNotEmpty(result)) {
                    String content = result.get("content").toString();
                    if (ToolUtil.isNotEmpty(content)) {
                        path = saveTxt(docType, content);
                    }
                }
            }
        }
        return path;
    }

    /**
     * 存储txt文件
     *
     * @param docType 文件类型
     * @param content 文件内容
     * @return 文件路径
     */
    private String saveTxt(String docType, String content) {
        String path = null;
        try {
            String random = UUID.randomUUID().toString().replace("-", "");
            String tempPath = tempdir + random + ".txt";
            File writeName = new File(tempPath); // 相对路径，如果没有则要建立一个新的output.txt文件
            if (!writeName.getParentFile().exists()) {
                // 路径不存在,创建
                writeName.getParentFile().mkdirs();
            }
            if (!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            FileWriter writer = new FileWriter(writeName);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(content);
            out.flush(); // 把缓存区内容压入文件
            out.close();
            path = tempPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 转换为txt并更新标签
     * @param docId 文档id
     * @return 是否成功
     */
    @Override
    public boolean getAndUpdateTags(String docId) {
        boolean flag = false;
        DocInfo temp = docInfoService.getById(docId);
        if (ToolUtil.isNotEmpty(temp)) {
            String docName = temp.getTitle() + temp.getDocType();
            String path = changeToTxt(docId);
            if(path != null) {
                // 获取语义分析生成的标签
                String label = semanticAnalysisService.getLabelAnalysis(docName, path);
                if(ToolUtil.isNotEmpty(label)) {
                    // 更新文档标签
                    DocInfo docInfo = new DocInfo();
                    docInfo.setTags(label);
                    docInfo.setDocId(docId);
                    flag = docInfoService.updateById(docInfo);
                    // 更新索引标签
                    Map map = new HashMap(1);
                    map.put("tags", label);
                    esUtil.updateIndex(docId, map);
                }
            }
        }
        return flag;
    }

    /**
     * 获取token值
     * @param userId 用户ID
     * @param userName 用户名称
     * @return token
     */
    @Override
    public Map<String, String> getToken(String userId, String userName) {
        Map<String, String> tokenMap = new HashMap<>();
        String token = "";
        //获取当前时间毫秒值
        Long nowDate = System.currentTimeMillis();
        Long inValidDateStr = nowDate + invalid * 1000;
        //设置
        Date inValidDate = new Date(inValidDateStr);
        JwtBuilder result = Jwts.builder()
                //设置JWT头部参数
                .setHeaderParam("typ", "JWT")
                //设置用户ID
                .claim("userId", userId)
                //设置用户名
                .claim("userId", userName)
                // 设置过期时间
                .setExpiration(inValidDate)
                // 设置token时间不在当前时间之前
                .setNotBefore(new Date())
                //设置签名
                .signWith(SignatureAlgorithm.HS256, key);
        //生成token
        token = result.compact();
        tokenMap.put("validDate", inValidDateStr + "");
        tokenMap.put("token", token);
        return tokenMap;
    }

    /**
     * 获取服务器名称
     * @param fileId 文档ID
     * @return 服务器名称
     */
    @Override
    public String getServerName(String fileId){
        String serverAddress = "";
        DocInfo docInfo = docInfoService.getById(fileId);
        if(ToolUtil.isNotEmpty(docInfo)){
            Integer randomNum = docInfo.getRandomNum();
            if(randomNum != null){
                Integer size = randomNum % serverNum;
                String serverSize = "serverAddress_" + size;
                serverAddress = environment.getProperty("onlineEdit." + serverSize);
            } else {
                serverAddress = environment.getProperty("onlineEdit.serverAddress_0");
            }
        }
        return serverAddress;
    }

    /**
     * 检查用户是否有文件的查看权限
     *
     * @param fileId 文件id
     * @return 是否
     */
    @Override
    public boolean checkFilePreviewAuthority(String fileId) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return true;
        }
        List<String> idList = new ArrayList<String>();
        idList.add(fileId);
        List<Map> list = getFileAuthority(idList);
        boolean authorityAble = false;
        if(list.size() > 0){
            Map temp = list.get(0);
            if (temp.get("authority") != null) {
                authorityAble = true;
            }
        }
        return authorityAble;
    }

    /**
     * 检查用户是否有文件的管理权限
     *
     * @param idList 文件id集合
     * @return 是否
     */
    @Override
    public boolean checkFileManageAuthority(List<String> idList) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return true;
        }
        List<Map> list = getFileAuthority(idList);
        if(list.size() == 0){
            return false;
        }
        boolean authorityAble = true;
        for(Map temp:list){
            Object t = temp.get("authority");
            if (t != null) {
                String authority = t.toString();
                if(!"2".equals(authority)) {
                    authorityAble = false;
                    break;
                }
            } else {
                authorityAble = false;
                break;
            }
        }
        return authorityAble;
    }

    /**
     * 检查用户是否有文件夹的上传权限
     *
     * @param foldId 文件夹id
     * @return 是否
     */
    @Override
    public boolean checkFoldUploadAuthority(String foldId) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return true;
        }
        boolean authorityAble = false;
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        int isEdits = docFoldAuthorityService.findEditByUpload(foldId, listGroup, userId);
        if(isEdits != 0){
            authorityAble = true;
        }
        return authorityAble;
    }

    /**
     * 检查用户是否有文件夹的管理权限
     *
     * @param idList 文件夹id集合
     * @return 是否
     */
    @Override
    public boolean checkFoldManageAuthority(List<String> idList) {
        Integer adminFlag = CommonUtil.getAdminFlag();
        if (DocConstant.ADMINFLAG.WKADMIN.getValue().equals(adminFlag)) {
            return true;
        }
        if(idList.size() == 0){
            return false;
        }
        boolean authorityAble = true;
        String userId = ShiroKit.getUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        for(String foldId:idList) {
            int isEdits = docFoldAuthorityService.findEditByUpload(foldId, listGroup, userId);
            if (isEdits != 2) {
                authorityAble = false;
                break;
            }
        }
        return authorityAble;
    }

    /**
     * 检查用户是否有父文件夹的管理权限
     *
     * @param idList 文件夹id集合
     * @return 是否
     */
    @Override
    public boolean checkParentFoldManageAuthority(List<String> idList) {
        List<String> parentIdList = filesMapper.getParentIdByFoldId(idList);
        return checkFoldManageAuthority(parentIdList);
    }

    private List<Map> getFileAuthority(List<String> idList){
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        return fsFileService.getInfo(idList, userId, listGroup, levelCode, orgId,ShiroKit.getUser().getRolesList());
    }

    @Override
    public JSONObject fileDownload(String docId, String userId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        return fileDown(docId, userId, request, response);
    }

    /**
     * 文档下载方法
     *
     * @param docId    文档id
     * @param userId
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    private JSONObject fileDown(String docId, String userId, HttpServletRequest request, HttpServletResponse response) throws IOException, ServiceException {
        JSONObject resultMap = new JSONObject();

        List<String> docIdList = Arrays.asList(docId.split(","));

        //文档信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);


        // 校验文件是否存在
        if (ToolUtil.isEmpty(docInfoList)) {
            resultMap.put("code", "4");
            resultMap.put("msg", "文件不存在");
            return resultMap;
        }

        if (docIdList.size() > 1) {
            downLoadZipFile(request, response, docInfoList);
        } else if (docIdList.size() == 1) {
            downLoadFile(request, response, docInfoList.get(0));
        }

        // 将下载记录存入数据库
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            //userId = UserInfoUtil.getUserInfo().get("ID").toString();
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }
        resultMap.put("code", "1");
        resultMap.put("msg", "操作成功");
        return resultMap;
    }

    /**
     * 单文件上传
     *
     * @param file   文件
     * @param docId  文档id
     * @param foldId 目录id
     * @param userId 用户id
     * @return JSONObject
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFileApi(MultipartFile file, String docId, String foldId, String userId) throws Exception {

        // 文件信息
        List<DocResourceLog> resInfoList = new ArrayList<>();
        List<DocFileAuthority> authoritylist = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        DocInfo docInfo = new DocInfo();
        FsFile fsFile = new FsFile();

        // 文件md5值
        ByteArrayOutputStream baos = null;
        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        String md5 = MD5Util.getFileMD5(md5InputStream);

        // 获取文件类型
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // 判断是否为秒传
        List<FsFile> list = fsFileService.getInfoByMd5(md5);
        Map<String, Object> esInfo = null;
        if (list != null && list.size() > 0) {
            esInfo = esService.getIndex(list.get(0).getFileId());
        }
        if (list == null || list.size() == 0 || esInfo == null) {
            // 正常上传文件
            getUploadInfoByDocId(file, docId, foldId, docInfo, fsFile, resInfoList, authoritylist, indexList, userId, md5);

            String contentType = getContentType(suffix.toLowerCase());

            //将MultipartFile 转换生成 File
            String random = UUID.randomUUID().toString().replace("-", "");
            File tempFile = new File(tempdir + File.separator + random + suffix);
            if (!tempFile.getParentFile().exists()) {
                tempFile.getParentFile().mkdirs();
            }
            transFile(file, tempFile);

            // 清除缓存
            cacheToolService.updateLevelCodeCache(userId);

            // 上传文件
            uploadFile(tempFile, docInfo, fsFile, resInfoList, authoritylist, indexList, contentType);

        } else {
            // 秒传
            cacheToolService.updateLevelCodeCache(userId);
            FsFile oldFile = list.get(0);
            uploadFastByDocId(docId, foldId, "0", "1", "", "", "",
                    md5, fileName, oldFile, oldFile.getFileSize(), "1", userId, "0");
        }
    }

    /**
     * 文档上传相关信息组装
     *
     * @param file
     * @param docId
     * @param foldId
     * @param docInfo
     * @param fsFile
     * @param resInfoList
     * @param authoritylist
     * @param indexList
     * @return
     */
    private String getUploadInfoByDocId(MultipartFile file, String docId, String foldId, DocInfo docInfo, FsFile fsFile,
                                        List<DocResourceLog> resInfoList, List<DocFileAuthority> authoritylist, List<String> indexList, String userId, String md5) {

        String fileId = file.getOriginalFilename();
        if (fileId.contains(DocConstant.SPECIALCHAR.DOUBLESLASH.getValue())) {
            fileId = fileId.substring(fileId.lastIndexOf(DocConstant.SPECIALCHAR.DOUBLESLASH.getValue()) + 1);
        }
        // 获取上传文件有关信息
        Map<String, String> docInfoMap = FileTool.fileUploadInfo(file, "", fileId);

        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        // 组装上传文件信息
        DocUploadParams uploadParams = new DocUploadParams();
        uploadParams.setFoldId(foldId);
        uploadParams.setVisible("1");

        //组装docInfo信息
        generateDocInfoApi(docId, userId, fileId, ts, docInfoMap, foldId, docInfo);

        //拼装fs_file表中的信息
        generateFsFileApi(docId, ts, docInfoMap, fsFile, md5);

        //拼装操作历史记录
        DocResourceLog docResourceLog = generateDocResourceLog(docId, userId, ts, 0, "1");
        resInfoList.add(docResourceLog);

        //  权限信息
        indexList.add(userId);
        return docId;
    }

    /**
     * 组装文件信息对象
     *
     * @param docId      文档ID
     * @param ts         时间戳
     * @param docInfoMap 上传文件有关信息
     * @return
     */
    private void generateFsFileApi(String docId, Timestamp ts, Map<String, String> docInfoMap, FsFile fsFile, String md5) {
        fsFile.setCreateTime(ts);
        fsFile.setFileIcon("");
        fsFile.setFileId(docId);
        fsFile.setFileName(docInfoMap.get("docName").toString());
        fsFile.setFileSize(docInfoMap.get("fileSize").toString());
        fsFile.setSize(Long.valueOf(docInfoMap.get("size")));
        fsFile.setMd5(md5);
        fsFile.setFileType(docInfoMap.get("type"));
    }

    /**
     * 组装文档信息对象
     *
     * @param docId        文档ID
     * @param userId       用户ID
     * @param fileId       文件ID（实际上是含路径的文件名）
     * @param ts           时间戳
     * @param docInfoMap   上传文件有关信息
     * @return
     */
    private void generateDocInfoApi(String docId, String userId, String fileId, Timestamp ts, Map<String, String> docInfoMap, String foldId,
                                    DocInfo docInfo) {
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(fileId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(foldId);
        docInfo.setDocType(docInfoMap.get("type"));
        docInfo.setTitle(docInfoMap.get("docName").toString());
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setSetAuthority("0");
        docInfo.setShareFlag("1"); // 是否可分享（0：不可  1：可分享）
        docInfo.setVisibleRange(1); // 可见范围(0:完全公开,1:部分可见)
        docInfo.setWatermarkUser("");
        docInfo.setWatermarkCompany("");
        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

    }

    private String uploadFastByDocId(String docId, String categoryId, String downloadAble, String visible, String group, String person,
                                     String watermarkUser, String md5, String fileName, FsFile fsFile, String size, String shareFlag, String userId, String fileOpen) {

        // 截取文件名的后缀名
        int pointIndex = fileName.lastIndexOf(".");
        // 后缀名
        String suffix = fileName.substring(pointIndex).toLowerCase();
        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String validFlag = "1";
        String recycle = "1";

        DocInfo docInfo = new DocInfo();
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(categoryId);
        docInfo.setDocType(suffix);
        docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setValidFlag(validFlag);
        docInfo.setAuthority(downloadAble);
        docInfo.setVisibleRange(Integer.parseInt(visible));
        docInfo.setWatermarkUser(watermarkUser);
        docInfo.setSetAuthority("0");

        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

        // share_flag 是否可分享
        docInfo.setShareFlag(shareFlag);
        FsFile fileModel = new FsFile();
        fileModel.setCreateTime(ts);
        fileModel.setFileIcon("");
        fileModel.setFileId(docId);
        fileModel.setMd5(md5);
        fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
        //大小保留2位小数
        fileModel.setFileSize(size);
        fileModel.setSize(fsFile.getSize());
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
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());

        resInfoList.add(docResourceLog);
        //拼装权限信息
        List<DocFileAuthority> list = new ArrayList<>();
        List<String> indexList = new ArrayList<>();

        FsFolder fsFolder = fsFolderService.getById(categoryId);

        fileModel.setFilePath(fsFile.getFilePath());
        fileModel.setFilePdfPath(fsFile.getFilePdfPath());
        fileModel.setPdfKey(fsFile.getPdfKey());
        fileModel.setSourceKey(fsFile.getSourceKey());

        if("true".equals(analysisUsing)) {
            if (".txt".equals(suffix)) {
                // 下载文件并解密
                String path = downloadFile(fileModel);
                if (path != null) {
                    // 获取语义分析生成的标签
                    String label = semanticAnalysisService.getLabelAnalysis(fileName, path);
                    docInfo.setTags(label);
                }
            }
        }

        filesMapper.insert(fileModel);


        Random random = new Random();
        int ends = random.nextInt(99);
        docInfo.setRandomNum(ends);
        // 文档信息插入数据库
        docInfoMapper.insert(docInfo);

        //保存文档权限
        if (list != null && list.size() > 0) {
            //完全公开文档上传时，文档权限记录list为0，不插入
            docFileAuthorityService.saveBatch(list);
        }

        //保存对文件的操作历史
        docInfoService.insertResourceLog(resInfoList);
        int state = esService.copyIndex(fsFile.getFileId(), docId);
        if (state == -1) {
            return null;
        }

        Map map = new HashMap(4);
        //0为无效，1为有效

        if (docInfo.getTags() != null && !"".equals(docInfo.getTags())) {
            map.put("tags", docInfo.getTags());
        }
        // 图片缩略图
        if (".png".equals(suffix) || ".jpg".equals(suffix) || ".gif".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)) {
            frontDocInfoMapper.insertThumbInfoFast(StringUtil.getUUID(), docId, md5);
        }

        // 视频的图
        if(".mp4".equals(suffix) || ".avi".equals(suffix) || ".mov".equals(suffix) || ".flv".equals(suffix)){
            DocVideoThumb thumb = docVideoThumbService.getById(fsFile.getFileId());
            if(thumb!=null){
                thumb.setDocId(docId);
            }
            docVideoThumbService.save(thumb);
        }

        if (fsFolder != null) {
            map.put("category", fsFolder.getFolderName());
            map.put("folderId", fsFolder.getFolderId());
        }
        indexList.add(userId);
        map.put("permission", indexList.toArray(new String[indexList.size()]));
        map.put("recycle", recycle);
        map.put("title",fileName.substring(0, fileName.lastIndexOf(".")));
        esUtil.updateIndex(docId, map);

        if("true".equals(analysisUsing)) {
            if (!".txt".equals(suffix)) {
                // 转换为txt并更新标签
                getAndUpdateTags(docId);
            }
        }

        return docId;

    }



    /**
     * (非结构化平台)
     * @param docId
     * @param userId
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public byte[] fileDownload(String docId, String userId) throws IOException, ServiceException {
        return fileDown(docId, userId);
    }

    /**
     * 文档下载方法(非结构化平台)
     *
     * @param docId    文档id
     * @param userId
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    private byte[] fileDown(String docId, String userId) throws IOException, ServiceException {
        JSONObject resultMap = new JSONObject();

        List<String> docIdList = Arrays.asList(docId.split(","));

        //文档信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);

        byte[] bytes = null;
        if (docIdList.size() > 1) {
            if (docInfoList.size() > 0) {
                bytes = downLoadZipFileToByte(docInfoList);
            }
        } else if (docIdList.size() == 1) {
            FileInputStream input = null;
            if (docInfoList.size() > 0) {
                bytes = fileTool.downLoadFile(input, docInfoList.get(0).getFilePath(), null);
            }
            // downLoadFile(request, response, docInfoList.get(0));
        }

        // 将下载记录存入数据库
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            //userId = UserInfoUtil.getUserInfo().get("ID").toString();
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }
        resultMap.put("code", "1");
        resultMap.put("msg", "操作成功");
        return bytes;
    }


    /**
     * 单文件上传(非结构化平台)
     *
     * @param file   文件
     * @param docId  文档id
     * @param foldId 目录id
     * @param userId 用户id
     * @return JSONObject
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadFileApi(File file, String docId, String fileName, String foldId, String userId) throws Exception {

        // 文件信息
        List<DocResourceLog> resInfoList = new ArrayList<>();
        List<DocFileAuthority> authoritylist = new ArrayList<>();
        List<String> indexList = new ArrayList<>();
        DocInfo docInfo = new DocInfo();
        FsFile fsFile = new FsFile();

        // 文件md5值
        ByteArrayOutputStream baos = null;
        try {
            baos = fileTool.cloneInputStream(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        String md5 = MD5Util.getFileMD5(md5InputStream);

        md5InputStream.close();
        baos.close();

        // 获取文件类型
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();

        // 判断是否为秒传
        List<FsFile> list = fsFileService.getInfoByMd5(md5);
        Map<String, Object> esInfo = null;
        if (list != null && list.size() > 0) {
            esInfo = esService.getIndex(list.get(0).getFileId());
        }
        if (list == null || list.size() == 0 || esInfo == null) {
            // 正常上传文件
            getUploadInfoByDocId(file, docId, fileName, foldId, docInfo, fsFile, resInfoList, authoritylist, indexList, userId, md5);

            String contentType = getContentType(suffix.toLowerCase());


            // 清除缓存
            cacheToolService.updateLevelCodeCache(userId);

            // 上传文件
            uploadFile(file, docInfo, fsFile, resInfoList, authoritylist, indexList, contentType);

        } else {
            System.out.println("=======秒传=========");
            // 秒传
            cacheToolService.updateLevelCodeCache(userId);
            FsFile oldFile = list.get(0);
            uploadFastByDocId(docId, foldId, "0", "1", "", "", "",
                    md5, fileName, oldFile, oldFile.getFileSize(), "1", userId, "0");
            if(file.exists()){
                file.delete();
            }
        }
    }

    /**
     * 文档上传相关信息组装
     *
     * @param file
     * @param docId
     * @param foldId
     * @param docInfo
     * @param fsFile
     * @param resInfoList
     * @param authoritylist
     * @param indexList
     * @return
     */
    private String getUploadInfoByDocId(File file, String docId, String fileName, String foldId, DocInfo docInfo, FsFile fsFile,
                                        List<DocResourceLog> resInfoList, List<DocFileAuthority> authoritylist, List<String> indexList, String userId, String md5) {

        // 文件信息
        String oldName = fileName.substring(0, fileName.lastIndexOf("."));
        String extendName = fileName.substring(fileName.lastIndexOf("."));
        double size = MathUtil.getDecimal(file.length() / 1024, 2);

        // 获取上传文件有关信息
        Map<String, String> docInfoMap = new HashMap<String, String>(16);
        docInfoMap.put("docName", oldName);
        docInfoMap.put("type", extendName);
        docInfoMap.put("size", String.valueOf(file.length()));
        docInfoMap.put("fileSize", size + "KB");

        // 获取上传文档的当前时间
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        // 组装上传文件信息
        DocUploadParams uploadParams = new DocUploadParams();
        uploadParams.setFoldId(foldId);
        uploadParams.setVisible("1");

        //组装docInfo信息
        generateDocInfoApi(docId, userId, ts, docInfoMap, foldId, docInfo);

        //拼装fs_file表中的信息
        generateFsFileApi(docId, ts, docInfoMap, fsFile, md5);

        //拼装操作历史记录
        DocResourceLog docResourceLog = generateDocResourceLog(docId, userId, ts, 0, "1");
        resInfoList.add(docResourceLog);

        // 继承上级目录权限 设置权限为下载
        inheritParentFolderAuthority(docId, foldId, authoritylist, indexList);

        //  权限信息
        indexList.add(userId);
        return docId;
    }

    /**
     * 组装文档信息对象
     *
     * @param docId        文档ID
     * @param userId       用户ID
     * @param ts           时间戳
     * @param docInfoMap   上传文件有关信息
     * @return
     */
    private void generateDocInfoApi(String docId, String userId, Timestamp ts, Map<String, String> docInfoMap, String foldId,
                                    DocInfo docInfo) {
        docInfo.setDocId(docId);
        //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
        docInfo.setFileId(docId);
        docInfo.setUserId(userId);
        docInfo.setAuthorId(userId);
        docInfo.setContactsId(userId);
        docInfo.setCreateTime(ts);
        docInfo.setUpdateTime(ts);
        docInfo.setFoldId(foldId);
        docInfo.setDocType(docInfoMap.get("type"));
        docInfo.setTitle(docInfoMap.get("docName").toString());
        docInfo.setReadNum(0);
        docInfo.setDownloadNum(0);
        docInfo.setSetAuthority("0");
        docInfo.setShareFlag("1"); // 是否可分享（0：不可  1：可分享）
        docInfo.setVisibleRange(1); // 可见范围(0:完全公开,1:部分可见)
        docInfo.setWatermarkUser("");
        docInfo.setWatermarkCompany("");
        //ValidFlag 默认1 有效
        docInfo.setValidFlag("1");

    }

    /**
     * 文件继承上级目录权限(权限类型设置为下载)
     *
     * @param foldId
     */
    private void inheritParentFolderAuthority(String docId, String foldId, List<DocFileAuthority> authoritylist, List<String> indexList) {
        // 查询父级目录的权限
        List<DocFoldAuthority> authoritys = docFoldAuthorityService.list(new QueryWrapper<DocFoldAuthority>().eq("folder_id", foldId));
        if (authoritys != null && authoritys.size() > 0) {
            for (int i = 0; i < authoritys.size(); i++) {
                DocFoldAuthority authority = authoritys.get(i);
                DocFileAuthority fileAuthority = new DocFileAuthority();
                fileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replace("-", ""));
                fileAuthority.setFileId(docId);
                fileAuthority.setAuthorId(authority.getAuthorId());
                fileAuthority.setAuthorType(Integer.valueOf(authority.getAuthorType())); //操作者类型（0：userID,1:groupID,2:organID;3:全体成员）
                fileAuthority.setAuthority(1); //操作权限（0:查看,1:下载,2:管理）
                fileAuthority.setOrganId(authority.getOrganId());
                authoritylist.add(fileAuthority);
                if (StringUtils.equals(authority.getAuthorType(), "2")) {
                    indexList.add(authority.getOrganId());
                } else {
                    indexList.add(authority.getAuthorId());
                }
            }
        }
    }

    /**
     * 文件打包下载(非结构化平台)
     *
     * @param docInfoList 文档信息list
     * @throws IOException 压缩文件创建异常
     */
    private byte[] downLoadZipFileToByte(List<DocInfo> docInfoList)
            throws IOException {
        // 压缩文件的文件名
        String zipName = generateZipFileName();
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipName;
        // 根据临时的zip压缩包路径，创建zip文件
        File zip = new File(zipDownloadPath);
        // 如果压缩文件不存在的话创建它
        if (!zip.exists()) {
            if (!zip.getParentFile().exists()) {
                zip.getParentFile().mkdirs();
            }
            zip.createNewFile();
        }


        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zos.setEncoding("GBK");
        // 循环读取文件
        for (int i = 0; i < docInfoList.size(); i++) {
            DocInfo docInfo = docInfoList.get(i);
            String fileType = docInfo.getDocType();
            String fileName = docInfo.getTitle() + fileType;

            if (!fastdfsUsingFlag) {
                File tempFile = new File(docInfo.getFilePath());
                // 将每一个文件写入zip文件包内，即进行打包
                if (!tempFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    tempFile.getParentFile().mkdirs();
                }
                // 文件不存在,下载

                List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                if (list.size() != 0) {
                    if (list.get(0).getSourceKey() != null) {
                        boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                    }
                }

                FileTool.zipFile(tempFile, zos, fileName);
//                tempFile.delete();
            } else {
                try {
                    File tempFile = new File(tempdir, fileName);
                    if (!tempFile.getParentFile().exists()) {
                        // 路径不存在,创建
                        tempFile.getParentFile().mkdirs();
                    }
                    if (!tempFile.exists()) {
                        // 文件不存在,下载
                        fastdfsService.download(docInfo.getFilePath(), tempFile.getAbsolutePath());
                        List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                        if (list.size() != 0) {
                            if (list.get(0).getSourceKey() != null) {
                                boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                            }
                        }
                    }
                    // 将每一个文件写入zip文件包内，即进行打包
                    FileTool.zipFile(tempFile, zos, fileName);
                    tempFile.delete();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }

        //关闭文件流
        closeFileIO(zos);
        closeFileIO(fos);

        try {
            return FileUtils.readFileToByteArray(zip);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            zip.delete();
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param docIds
     * @throws IOException
     * @throws IOException
     * @throws ServiceException
     */
    @Override
    public  Map<String,String>  downloadToBase64(String docIds) throws IOException, ServiceException {
        return  fileDownNewToBase64(docIds);
    }
    /**
     * 文档下载方法
     *
     * @param docIds   文档ID（多个）
     * @return
     * @throws IOException
     * @throws ServiceException
     */
    private  Map<String,String>  fileDownNewToBase64(String docIds) throws IOException, ServiceException {
        Map<String,String> map = new HashMap<String,String>();

        List<String> docIdList = Arrays.asList(docIds.split(","));
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        //查询文件的详细信息
        List<DocInfo> docInfoList = docInfoService.getDocInfo(docIdList);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = ShiroKit.getUser().getDeptName();
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        List<Map> list = fsFileService.getInfo(docIdList, userId, listGroup, levelCode, orgId,roleList);

        if (!checkDownLoadAuthority(list, userId)) {
            //LOGGER.error("没有权限下载此文件");
            map.put("data","");
            return map;
        }

        //文件信息未找到
        if (ToolUtil.isEmpty(docInfoList)) {
            //LOGGER.error("没有查找到此文件");
            map.put("data","");
            return map;
        }

        Map<String,String> base64Str = new HashMap<>();
        if (docInfoList.size() > 1) {
            //打包下载
            base64Str=  downLoadZipFileBase64( docInfoList);
        } else if (docInfoList.size() == 1) {
            base64Str =   downLoadFileBase64( docInfoList.get(0));
        }

        // 下载信息表  暂时先把下载记录的去掉
        List<DocResourceLog> docDownloadInfoList = new ArrayList<DocResourceLog>();
        if (docInfoList != null && docInfoList.size() > 0) {
            userId = UserInfoUtil.getUserInfo().get("ID").toString();
            Timestamp ts = new Timestamp(new Date().getTime());
            // 获取下载文档的当前时间
            for (DocInfo docInfo : docInfoList) {
                // 用户下载记录
                docDownloadInfoList.add(generateDocResourceLog(docInfo.getDocId(), userId, ts, 4, "1"));
            }
            docInfoService.insertResourceLog(docDownloadInfoList);
            docInfoService.updateDownloadNum(docDownloadInfoList);
        }

        return base64Str;
    }
    /**
     * 文件打包下载
     *
     * @param docInfoList 文档信息list
     * @throws IOException 压缩文件创建异常
     */
    private Map<String,String> downLoadZipFileBase64( List<DocInfo> docInfoList)
            throws IOException {

        Map<String,String> map = new HashMap<String,String>();

        // 压缩文件的文件名
        String zipName = generateZipFileName();
        String zipDownloadPath = this.zipDownloadPath + File.separator + zipName;
        // 根据临时的zip压缩包路径，创建zip文件
        File zip = new File(zipDownloadPath);
        // 如果压缩文件不存在的话创建它
        if (!zip.exists()) {
            if (!zip.getParentFile().exists()) {
                zip.getParentFile().mkdirs();
            }
            zip.createNewFile();
        }


        FileOutputStream fos = new FileOutputStream(zip);
        ZipOutputStream zos = new ZipOutputStream(fos);

        zos.setEncoding("GBK");
        // 循环读取文件
        for (int i = 0; i < docInfoList.size(); i++) {
            DocInfo docInfo = docInfoList.get(i);
            String fileType = docInfo.getDocType();
            String fileName = docInfo.getTitle() + fileType;

            if (!fastdfsUsingFlag) {
                File tempFile = new File(docInfo.getFilePath());
                // 将每一个文件写入zip文件包内，即进行打包

                // 文件不存在,下载

                List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                if (list.size() != 0) {
                    if (list.get(0).getSourceKey() != null) {
                        boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                    }
                }

                FileTool.zipFile(tempFile, zos, fileName);
//                tempFile.delete();
            } else {
                try {
                    File tempFile = new File(tempdir, fileName);
                    if (!tempFile.getParentFile().exists()) {
                        // 路径不存在,创建
                        tempFile.getParentFile().mkdirs();
                    }
                    if (!tempFile.exists()) {
                        // 文件不存在,下载
                        fastdfsService.download(docInfo.getFilePath(), tempFile.getAbsolutePath());
                        List<FsFile> list = fsFileMapper.getInfoByPath(docInfo.getFilePath());
                        if (list.size() != 0) {
                            if (list.get(0).getSourceKey() != null) {
                                boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempFile.getAbsolutePath(), list.get(0).getSourceKey());
                            }
                        }
                    }
                    // 将每一个文件写入zip文件包内，即进行打包
                    FileTool.zipFile(tempFile, zos, fileName);
                    tempFile.delete();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
        }

        //关闭文件流
        closeFileIO(zos);
        closeFileIO(fos);

        try {
            String contentType=getContentType("zip");
            map.put("data","data:"+contentType+";base64,"+new BASE64Encoder().encode(FileUtils.readFileToByteArray(zip)));
            map.put("name",generateZipFileName());
            return map;

        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        } finally {
            zip.delete();
        }
    }



    /**
     * 单个文件下载
     *
     * @throws IOException      压缩文件创建异常
     * @throws ServiceException fast下载文件异常
     */
    private Map<String,String> downLoadFileBase64( DocInfo docInfo) throws IOException, ServiceException {
        Map<String,String> map = new HashMap<String,String>();
        String filePath = docInfo.getFilePath();
        String fileName = docInfo.getTitle();
        String docType = docInfo.getDocType();
//		String userId = UserInfoUtil.getUserInfo().get("ID").toString();
//		List<String> roleList = ShiroKit.getUser().getRolesList();
//		Integer adminFlag = CommonUtil.getAdminFlag(roleList);
//		if(adminFlag!=1){
//			Integer integral = integralRecordService.addIntegral(docInfo.getDocId(),userId,"download");
//			Integer totalIntegral =integralRecordService.showIntegral(userId);
//			List<Map<String,Object>> list =integralRecordService.getIntegralRank();
//		}
        FileInputStream input = null;
        // 下载*/
        try {
            byte[] bytes = fileTool.downLoadFile(input, filePath, null);
            String contentType=getContentType(docType);

            map.put("data","data:"+contentType+";base64,"+new BASE64Encoder().encode(bytes));
            map.put("name",fileName+docType);
            return map ;

        } catch (IOException e) {
            //LOGGER.error("IO异常：" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            throw new IOException("文件下载失败IO");
        } finally {
            try {
                if (input != null) {
                    input.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String getContentType(String suffix) {
        String contentType = null;
        if (suffix.equals(".doc") || suffix.equals(".docx") || suffix.equals(".dotx") || suffix.equals(".tif")) {
            contentType = "application/msword";
            return contentType;
        } else if (suffix.equals(".ppt") || suffix.equals(".pptx") || suffix.equals(".ppsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            return contentType;
        } else if (suffix.equals(".xls") || suffix.equals(".xlsx") || suffix.equals(".et")) {
            contentType = "spreadsheetml";
            return contentType;
        } else if (suffix.equals(".png") || suffix.equals(".gif") || suffix.equals(".jpg") || suffix.equals(".bmp") || suffix.equals(".jpeg")) {
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
        } else if (suffix.equals(".mov")) {
            contentType = "audio/mov";
            return contentType;
        } else if (suffix.equals(".mp4")) {
            contentType = "video/mp4";
            return contentType;
        } else if (suffix.equals(".wav")) {
            contentType = "audio/wav";
            return contentType;
        } else if (suffix.equals(".wmv")) {
            contentType = "audio/wmv";
            return contentType;
        } else if (suffix.equals(".mpg")) {
            contentType = "audio/mpg";
            return contentType;
        } else if (suffix.equals(".avi")) {
            contentType = "video/avi";
            return contentType;
        }else if (suffix.equals(".flv")) {
            contentType = "video/flv";
            return contentType;
        }  else if (suffix.equals(".ceb")) {
            contentType = "ceb";
            return contentType;
        } else if (suffix.equals(".dwg") || suffix.equals(".dwf") || suffix.equals(".dxf")) {
            contentType = "cad";
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
        } else if (suffix.equals(".wps")) {
            contentType = "application/vnd.ms-works";
            return contentType;
        } else {
            return null;
        }
    }



}
