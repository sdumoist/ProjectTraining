package com.jxdinfo.doc.unstructured;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.PreviewService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.service.EntryInfoService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.doc.newupload.thread.CreateEsThread;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 类的用途：文件预览控制层
 * 创建日期：2018年9月4日
 * 修改历史：
 * 修改日期：2018年9月6日
 * 修改作者：yjs
 * 修改内容：重构代码
 */
@Controller
@RequestMapping("/previewUnstruct")
public class PreviewUnstructController {

    /** 日志  */
    private static Logger LOGGER = LoggerFactory.getLogger(PreviewUnstructController.class);

    /**
     * 文件 Mapper 接口
     */
    @Resource
    private FsFileMapper fsFileMapper;

    @Autowired
    private IFsFolderService fsFolderService;

    /** 上传路径  */
    @Value("${docbase.uploadPath}")
    private String base;

    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /** 上传标志  */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    /**  文件类型 */
    private String fileType;

    /**  排序类型  */
    private String orderType;

    /** 文件名  */
    private String fileName;

    /** PDF文件ID */
    private String pdfFileId;

    /**  文件预览服务类 */
    @Autowired
    private PreviewService previewService;

    /** fast服务器服务类  */
    @Autowired
    FastdfsService fastdfsService;

    /** 文档服务类  */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /** 缓存工具服务类 */
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private DocGroupService docGroupService;

    /** 平台缓存服务类 */
    @Autowired
    private HussarCacheManager hussarCacheManager;
    
    /** 文件工具类  */
    @Autowired
    private FileTool fileTool;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    /** es服务类  */
    @Autowired
    private ESService esService;

    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;
    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;

    /**
     * 词条服务类
     */
    @Autowired
    private EntryInfoService entryInfoService;

    /**
     * 我的收藏
     */
    @Autowired
    private PersonalCollectionService personalCollectionService;

    @Resource
    private UploadService uploadService;

    @Resource
    private FilesMapper filesMapper;

    @Resource
    private FilesService filesService;

    @Value("${docbase.pdfFile}")
    private String pdfPathDir;
    @Value("${docbase.pdfFileByKey}")
    private String pdfKeyPath;
    @Value("${docbase.downloadPdfFileByKey}")
    private String downloadPdfFileByKey;
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;
    @Value("${docbase.downloadFile}")
    private String downloadFile;

    //加密后pdf
    File pdfKeyDir = null;

    //生成的pdf文件
    File pdfFile = null;

    //源文件
    File sourceFile = null;

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            100,10000, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

    private static final String PREVIEW_ERROR_HTML = "/doc/front/previewunstruct/error.html";

    /**
     * 播放video视频
     *
     * @param request
     * @param response
     * @return void
     */
    @RequestMapping("/video")
    public void getVideo(HttpServletRequest request, HttpServletResponse response) {
        //生产UUID
        String uuid = UUID.randomUUID().toString();
        //初始化参数和流
        RandomAccessFile randomFile = null;
        ServletOutputStream out = null;
        //从前台获取fileId
        pdfFileId = request.getParameter("fileId");
        Map<String, String> readInfo = new HashMap<String, String>();
        // mp4预览   读锁
        Object cacheMap = hussarCacheManager.getObject(CacheConstant.MP4_PREVIEW_READ_LOCK, pdfFileId);
        if (cacheMap != null) {
            readInfo = (Map<String, String>) cacheMap;
        }
        //将信息传入map中
        readInfo.put(uuid, CacheConstant.MP4_PREVIEW_READ_LOCK_FLAG);
        hussarCacheManager.setObject(CacheConstant.MP4_PREVIEW_READ_LOCK, pdfFileId, readInfo);

        File file = null;
        try {
        	file = fileTool.downLoadFile(pdfFileId);
            randomFile = new RandomAccessFile(file, "r");
            long contentLength = file.length();
            String range = request.getHeader("Range");
            int start = 0, end = 0;
            if (range != null && range.startsWith("bytes=")) {
                String[] values = range.split("=")[1].split("-");
                start = Integer.parseInt(values[0]);
                if (values.length > 1) {
                    end = Integer.parseInt(values[1]);
                }
            }
            int requestSize = 0;
            if (end != 0 && end > start) {
                requestSize = end - start + 1;
            } else {
                requestSize = Integer.MAX_VALUE;
            }
            byte[] buffer = new byte[4096];
            //设置消息头
            response.setContentType("video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("ETag", fileName);
            response.setHeader("Last-Modified", new Date().toString());
            //第一次请求只返回content length来让客户端请求多次实际数据
            if (range == null) {
                response.setHeader("Content-length", contentLength + "");
            } else {
                //以后的多次以断点续传的方式来返回视频数据
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);//206
                long requestStart = 0, requestEnd = 0;
                String[] ranges = range.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Integer.parseInt(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Integer.parseInt(rangeDatas[1]);
                    }
                }
                long length = 0;
                if (requestEnd > 0) {
                    length = requestEnd - requestStart + 1;
                    //设置消息头
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + requestEnd + "/" +
                            contentLength);
                } else {
                    length = contentLength - requestStart;
                    //设置消息头
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range", "bytes " + requestStart + "-" + (contentLength - 1) +
                            "/" + contentLength);
                }
            }
            out = response.getOutputStream();
            int needSize = requestSize;
            randomFile.seek(start);
            while (needSize > 0) {
                int len = randomFile.read(buffer);
                if (needSize < buffer.length) {
                    out.write(buffer, 0, needSize);
                } else {
                    out.write(buffer, 0, len);
                    if (len < buffer.length) {
                        break;
                    }
                }
                needSize -= buffer.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } catch (ServiceException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
                    e.printStackTrace();
                }
            }
            cacheMap = hussarCacheManager.getObject(CacheConstant.MP4_PREVIEW_READ_LOCK, pdfFileId);
            if (cacheMap != null) {
                readInfo = (Map<String, String>) cacheMap;
            }
            readInfo.remove(uuid);
            hussarCacheManager.setObject(CacheConstant.MP4_PREVIEW_READ_LOCK, pdfFileId, readInfo);
            if (readInfo.size() == 0 && fastdfsUsingFlag) {
                if (file != null) {
                   file.delete();
                }
            }
        }
    }

    /**
     * @title: 预览文件
     * @description: 预览
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request
     * @param: response
     * @return: void
     */
    @RequestMapping("/list")
    public  synchronized  void getList(HttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("==========进入预览===========");
        String range_size = request.getParameter("range_size");
        String pdfFileId = request.getParameter("fileId");
        String isThumbnails = request.getParameter("isThumbnails");
        String isView = request.getParameter("isView");
        FileInputStream input = null;
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        try {
            //isView 为0，则为预览的文件
            if (isView != null && isView.equals("0")) {
                File file = null;
                LOGGER.info("==========进入分片预览===========");
                List<FsFile> list = fsFileMapper.getInfoByPdfPath(pdfFileId);
                LOGGER.info("==========list查询完毕===========");
                String random = list.get(0).getMd5();
                String suffix = pdfFileId.substring(pdfFileId.lastIndexOf("."));
                file = new File(downloadPdfFile + "" + random + suffix);
                LOGGER.info("==========file创建完成，进行判断===========");
                //文件不存在则重新读取
                if (!file.exists()||file.length()==0) {
                    LOGGER.info("==========文件不存在===========");
                    file = fileTool.chuckPdf(pdfFileId);
                }
                LOGGER.info("==========获得文件===========");
                inputStream = new FileInputStream(file);
                if (file.length() < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                //设置请求头
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Type", "application/pdf");
                String range = request.getHeader("Range");
                LOGGER.info("==========获得文件RANGE==========="+range);
                if (ToolUtil.isNotEmpty(range)) {
                     long t1 = new Date().getTime();
                    LOGGER.info("==========进入206分片===========");
                    range = range.replace("bytes=", "");
                    String[] ranges = range.split("-");
                    int startIndex = Integer.parseInt(ranges[0]);
                    int endIndex = Integer.parseInt(ranges[1]);
                    byte[] buffer = new byte[endIndex - startIndex + 1];

                    randomAccessFile = new RandomAccessFile(file, "r");

                    randomAccessFile.seek(startIndex);
                    LOGGER.info("==========进入206分片 读取文件===========");
                    randomAccessFile.read(buffer);
                    LOGGER.info("==========进入206分片 读取文件结束===========");
                    response.setHeader("Content-Range", "bytes " + startIndex + "-" + (endIndex - 1) + "/" + file.length() + "");
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    LOGGER.info("==========进入206分片 写入文件===========");
                    response.getOutputStream().write(buffer);
                    LOGGER.info("==========进入206分片 写入文件结束===========");
                    long t2 = new Date().getTime();
                    LOGGER.info("==========进入206分片结束==========="+(t1-t2));
                } else {
                    LOGGER.info("==========首次加载===========");
                    if(Long.parseLong(range_size)<file.length()){
                        response.setHeader("Content-Length", file.length() + "");
                        response.getOutputStream().write(new byte[1024]);
                    }else {
                        //分片大于文件，直接加载文件
                        inputStream = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        OutputStream os = response.getOutputStream();
                        int i =0;
                        while ((i=inputStream.read(bytes))!=-1){
                            os.write(bytes,0,i);
                        }
                    }
                }
            } else {
                byte[] bytes = fileTool.downLoadFile(input, pdfFileId, isThumbnails);
                if (bytes.length < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                MagicMatch match = Magic.getMagicMatch(bytes);
                String mimeType = match.getMimeType();
                response.setContentType(mimeType);
                response.addHeader("cache-control", "no-cache");
                response.getOutputStream().write(bytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("没有找到文件：" + ExceptionUtils.getErrorInfo(e));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } catch (ServiceException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } catch (Exception e) {
            LOGGER.error("文件下载失败：" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (input != null) {
                    input.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
            }

        }
    }

    /**
     * excel预览
     *
     * @param request
     * @param response
     * @param id
     * @throws ServiceException
     */
    @RequestMapping("/excel/{id}")
    public void downloadXlxsFile(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws ServiceException {
        FsFile fsFile = fsFileMapper.selectById(id);
        // 获取文件类型
        String pdfFilePath = fsFile.getFilePdfPath();

        //  获取pdf路径  如果是pdf文件 则转换成xlsx
        String pdfPath = fsFile.getFilePdfPath();
        String pdfFileType = pdfPath.substring(pdfPath.lastIndexOf("."));
        if (StringUtils.equals(pdfFileType, ".pdf")) {
            String filePath = fsFile.getFilePath();
            String xlsxPath = pdfPathDir + filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".")) + ".xlsx";
            pdfFilePath = doExcelToXlsx(fsFile, filePath, xlsxPath);
            LOGGER.info("==================转换xlsx文件结束=============");
        }

        File file = fileTool.downLoadFile(pdfFilePath);

        BufferedOutputStream out = null;
        InputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file), 1024 * 10);

            String displayName = URLEncoder.encode("excel预览", "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + displayName);
            response.setContentType("multipart/form-data");
            out = new BufferedOutputStream(response.getOutputStream());
            int len = 0;
            int i = bis.available();
            byte[] buff = new byte[i];
            while ((len = bis.read(buff)) > 0) {
                out.write(buff, 0, len);
                out.flush();
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("文件未找到：" + ExceptionUtils.getErrorInfo(e));
            throw new HussarException(BizExceptionEnum.FILE_NOT_FOUND);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("不支持的编码异常：" + ExceptionUtils.getErrorInfo(e));
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        } catch (IOException e) {
            LOGGER.error("IO异常：" + ExceptionUtils.getErrorInfo(e));
            throw new HussarException(BizExceptionEnum.DOWNLOAD_ERROR);
        } finally {
            try {
                if (null != bis) {
                    bis.close();
                }
            } catch (IOException e) {
                LOGGER.error("流关闭异常：" + ExceptionUtils.getErrorInfo(e));
            }
            try {
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                LOGGER.error("流关闭异常：" + ExceptionUtils.getErrorInfo(e));
            }
        }
    }

    /**
     * 将xls文件 转换成xlsx文件
     *
     * @param fsFile   文件信息
     * @param filePath xls文件路径
     * @param xlsxPath 成xlsx文件路径
     */
    private String doExcelToXlsx(FsFile fsFile, String filePath, String xlsxPath) {
        // 上传文件到fast
        String pdfPath = "";
        try {
            // 文件原来的pdf路径
            String oldPath = fsFile.getFilePdfPath();
            String fileType = fsFile.getFileType();
            // 根据文件md5值获取文件
            List<FsFile> list = fsFileMapper.getInfoByMd5(fsFile.getMd5());

            // xlsx文件 只需要将pdfPath = filePath
            if (StringUtils.equals(fileType, ".xlsx")) {
                for (FsFile file : list) {
                    file.setPdfKey(file.getSourceKey());
                    file.setFilePdfPath(file.getFilePath());
                    file.updateById();
                }
                pdfPath = fsFile.getFilePath();
            } else { // xls文件 需要下载源文件 转换成pdf文件 并加密
                // 下载文件
                File xlsFile = fileTool.chuckAllFile(filePath);
                // xlsx文件
                File xlsxFile = new File(xlsxPath);
                if (!xlsxFile.getParentFile().exists()) {
                    xlsxFile.getParentFile().mkdirs();
                }
                // 将文件转换成xlsx文件
                LibreOfficePDFConvert.doDocToFdpLibre(xlsFile, xlsxFile);

                // 加密文件路径
                String xlsxKeyPath = pdfKeyPath + filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf(".")) + ".xlsx";
                // 加密文件
                File xlsxKeyFile = new File(xlsxKeyPath);
                if (!xlsxKeyFile.getParentFile().exists()) {
                    xlsxKeyFile.getParentFile().mkdirs();
                }
                // 对xlsx文件加密
                //文件加密并取出加密密码存到数据库
                String pdfKey = FileEncryptUtil.getInstance().encrypt(xlsxFile, xlsxKeyFile);


                if (cacheToolService.getFastDFSUsingFlag()) {
                    pdfPath = fastdfsService.uploadFile(xlsxKeyFile);
                    if (xlsxKeyFile != null && xlsxKeyFile.exists()) {
                        xlsxKeyFile.delete();
                    }
                    LOGGER.info("******************xls文件转换成xlxs文件:" + xlsFile.getName() + " 并上传到fast，fast返回地址为" + pdfPath + "******************");
                } else {
                    fsFile.setPdfKey(pdfKey);
                    pdfPath = xlsxKeyPath;
                }

                for (FsFile file : list) {
                    file.setPdfKey(pdfKey);
                    file.setFilePdfPath(pdfPath);
                    file.updateById();
                }

                // 删除xlsx文件
                if (xlsxFile.exists()) {
                    xlsxFile.delete();
                }
                // 删除xls文件
                if (xlsFile != null && xlsFile.exists()) {
                    xlsFile.delete();
                }
            }
            // 删除原来的pdf文件
            if (cacheToolService.getFastDFSUsingFlag()) {
                fastdfsService.removeFile(oldPath);
            } else {
                File oldPdfFile = new File(oldPath);
                if (oldPdfFile.exists()) {
                    oldPdfFile.delete();
                }
            }
            return pdfPath;
        } catch (ServiceException e) {
            LOGGER.error("获取源文件异常：" + ExceptionUtils.getErrorInfo(e));
        } catch (OfficeException e) {
            LOGGER.error("xls文件转换成xlsx文件异常：" + ExceptionUtils.getErrorInfo(e));
        }
        return pdfPath;
    }

    /**
     * @title: 分享链接的预览文件
     * @description: 预览
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request
     * @param: response
     * @return: void
     */
    @RequestMapping("/listForShare")
    public void getListForShare(HttpServletRequest request, HttpServletResponse response) {
        String range_size = request.getParameter("range_size");
        String isThumbnails = request.getParameter("isThumbnails");
        String hash = request.getParameter("hash");
        String docType = "";
        String pdfPath = "";
        Map getHash = shareResourceService.getPdfPath(hash);
        if (null != getHash.get("docType")) {
            docType = getHash.get("docType").toString();
        }
        if (StringUtil.isPdf(docType)) {
            pdfPath = getHash.get("pdfPath").toString();
        } else {
            pdfPath = getHash.get("filePath").toString();
        }
        FileInputStream input = null;
        RandomAccessFile randomAccessFile = null;
        String isView = request.getParameter("isView");
        InputStream inputStream = null;
        try {
            //isView 为0，则为预览的文件
            if (isView != null && isView.equals("0")) {
                File file = null;
                List<FsFile> list = fsFileMapper.getInfoByPdfPath(pdfPath);
                String random = list.get(0).getMd5();
                String suffix = pdfPath.substring(pdfPath.lastIndexOf("."));
                file = new File(downloadPdfFile + "" + random + suffix);
                //文件不存在则重新读取
                if (!file.exists()) {
                    file = fileTool.chuckPdf(pdfPath);
                }
                inputStream = new FileInputStream(file);
                if (file.length() < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                //设置请求头
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Type", "application/pdf");
                String range = request.getHeader("Range");
                if (ToolUtil.isNotEmpty(range)) {
                    range = range.replace("bytes=", "");
                    String[] ranges = range.split("-");
                    int startIndex = Integer.parseInt(ranges[0]);
                    int endIndex = Integer.parseInt(ranges[1]);
                    byte[] buffer = new byte[endIndex - startIndex + 1];
                    randomAccessFile = new RandomAccessFile(file, "r");
                    randomAccessFile.seek(startIndex);
                    randomAccessFile.read(buffer);
                    response.setHeader("Content-Range", "bytes " + startIndex + "-" + (endIndex - 1) + "/" + file.length() + "");
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    response.getOutputStream().write(buffer);
                } else {
                    LOGGER.info("==========首次加载===========");
                    if(Long.parseLong(range_size)<file.length()){
                        response.setHeader("Content-Length", file.length() + "");
                        response.getOutputStream().write(new byte[1024]);
                    }else {
                        //分片大于文件，直接加载文件
                        inputStream = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        OutputStream os = response.getOutputStream();
                        int i =0;
                        while ((i=inputStream.read(bytes))!=-1){
                            os.write(bytes,0,i);
                        }
                    }
                }
            } else {
                byte[] bytes = fileTool.downLoadFile(input, pdfPath, isThumbnails);
                if (bytes.length < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                response.getOutputStream().write(bytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.error("没有找到文件：" + ExceptionUtils.getErrorInfo(e));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } catch (ServiceException e) {
            e.printStackTrace();
            LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
        } catch (Exception e) {
            LOGGER.error("文件下载失败：" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (input != null) {
                    input.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("文件预览失败：" + ExceptionUtils.getErrorInfo(e));
            }

        }
    }



    /**
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowPDF")
    public ModelAndView toShowPDF(String fileType, String keyWords, String id, HttpServletRequest request ,String showType,String shareForward) {
        fileType = XSSUtil.xss(fileType);
        id = XSSUtil.xss(id);
        showType = XSSUtil.xss(showType);
        keyWords = XSSUtil.xss(keyWords);
        if (!StringUtils.equals("1",shareForward)) {
            boolean authorityFlag = filesService.checkFilePreviewAuthority(id);
            if(!authorityFlag){
                return new ModelAndView(PREVIEW_ERROR_HTML);
            }
        }
        ModelAndView mv = new ModelAndView("/doc/front/previewunstruct/showPDF.html");
        String keyword = request.getParameter("keyword");
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            mv.addObject("shareFlag",true);
        } else {
            mv.addObject("shareFlag",false);
        }*/
        //是否添加用户水印
        DocInfo docInfo = frontDocInfoService.getDocDetail(id,userId,listGroup,levelCode,ShiroKit.getUser().getRolesList());
        // 获取pdf路径和文件创建时间，如果路径不存在并且是12小时之前上传的文件，则前台显示手动转换按钮
        String pdfPath = docInfo.getFilePdfPath();
        Timestamp createTime = docInfo.getCreateTime();
        long ct = createTime.getTime();
        long diff = System.currentTimeMillis() - ct;
        if ((pdfPath == null || "".equals(pdfPath)) && diff > 12*60*60*1000) {
            mv.addObject("transFlag", "1");
        } else {
            mv.addObject("transFlag", "0");
        }

        //获取配置文件--是否有公司水印
        Map<String,String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String,String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        mv.addObject("fileType", type);
        mv.addObject("key", docName);
        mv.addObject("id", id);
        mv.addObject("isPersonCenter",false);
        mv.addObject("fileName", keyword);
        mv.addObject("fileType", fileType);
        mv.addObject("category", orderType);
        mv.addObject("userName", userName);
        mv.addObject("favorite",false);
        //判断此文档是否在预览时添加用户水印
        mv.addObject("watermark_user_flag", mapUser.get("configValidFlag"));
        mv.addObject("watermark_company_flag",mapCompany.get("configValidFlag"));
        mv.addObject("companyValue",mapCompany.get("configValue"));
        mv.addObject("showType",showType);
        return mv;
    }

    /**
     * 获取文件路径
     *
     * @param docId
     * @return List<Map<String, String>> 获得路径的map集合
     */
    @RequestMapping("/getFoldPath")
    @ResponseBody
    public List<Map<String, String>> getFoldPathByDocId(String docId,String showType) {
        return previewService.getFoldPathByDocId(docId,showType);
    }


    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @PostMapping("/fileDetail")
    @ResponseBody
    public Map<String, Object> getFileDetail(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null) {
            String userId = UserInfoUtil.getCurrentUser().getId();
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
            //根据ID获得文档详情
            DocInfo docInfo = frontDocInfoService.getDocDetail(id,userId,listGroup,levelCode,ShiroKit.getUser().getRolesList());
            //int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
            int collection = personalCollectionService.getMyCollectionCountByFileId(docInfo.getDocId(),userId);
            List<String> roleList = ShiroKit.getUser().getRolesList();
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            map.put("adminFlag", adminFlag);
            map.put("collection", collection);
            map.put("authority", docInfo.getAuthority());
            map.put("id", docInfo.getDocId());
            map.put("filePath", docInfo.getFilePath());
            map.put("filePdfPath", docInfo.getFilePdfPath());
            map.put("userId", docInfo.getUserId());
            map.put("author", docInfo.getAuthorName());
            map.put("createTime", docInfo.getCreateTime());
            map.put("title", docInfo.getTitle());
            map.put("shareFlag",docInfo.getShareFlag());
            map.put("tags",docInfo.getTags());
            //获得文件的类型字符串（前台根据文件类型判断展示内容）
            String fileSuffixName = docInfo.getDocType().substring(docInfo.getDocType().lastIndexOf(".") + 1);
            map.put("downloadNum", docInfo.getDownloadNum());
            map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()) + 1);
            map.put("fileSuffixName", fileSuffixName);
            map.put("docAbstract", docInfo.getDocAbstract());
            if (StringUtil.checkIsEmpty(docInfo.getFilePdfPath())){
                uploadService.checkUploadState(id);
            }
            map.put("uploadState",!StringUtil.checkIsEmpty(docInfo.getFilePdfPath()));
            if(docInfo.getFilePdfPath()!=null&&!docInfo.getFilePdfPath().endsWith(".mp4")){
                map.put("videoState",false);
            }else {
                map.put("videoState",true);
            }
            if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                //文件大小转化
                map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
            }
        }
        return map;
    }

    /**
     * @title: 跳转预览图片页面
     * @description: 跳转预览图片页面
     * @date: 2018-2-1.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowIMG")
    public ModelAndView toShowIMG(String fileType, String keyWords, String id,String showType,String shareForward) {
        fileType = XSSUtil.xss(fileType);
        id = XSSUtil.xss(id);
        showType = XSSUtil.xss(showType);
        keyWords = XSSUtil.xss(keyWords);
        if (!StringUtils.equals("1",shareForward)) {
            boolean authorityFlag = filesService.checkFilePreviewAuthority(id);
            if(!authorityFlag){
                return new ModelAndView(PREVIEW_ERROR_HTML);
            }
        }
        ModelAndView mv = new ModelAndView("/doc/front/previewunstruct/showImg.html");
        //根据ID获得图片详情
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            mv.addObject("shareFlag",true);
        } else {
            mv.addObject("shareFlag",false);
        }*/
        //获取配置文件--是否有公司水印
        Map<String,String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String,String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //根据ID获得文档详情
        DocInfo docInfo = frontDocInfoService.getDocDetail(id,userId,listGroup,levelCode,ShiroKit.getUser().getRolesList());
        String imgPath = frontDocInfoService.getThumbByIdAndLevel(id,"2") == null ? docInfo.getFilePath() : frontDocInfoService.getThumbByIdAndLevel(id,"2") + "&&isThumbnails=2";
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        String folderId = docInfo.getFoldId();
        mv.addObject("fileType", type);
        mv.addObject("key", docName);
        mv.addObject("isPersonCenter",false);
        mv.addObject("folderId",folderId);
        mv.addObject("id", id);
        mv.addObject("path", docInfo.getFilePath());
        mv.addObject("tags",docInfo.getTags());
        mv.addObject("fileType", fileType);
        mv.addObject("category", orderType);
        mv.addObject("userName", userName);
        mv.addObject("watermark_user_flag", mapUser.get("configValidFlag"));
        mv.addObject("fileName",docInfo.getTitle());
        mv.addObject("imgPath",imgPath);
        mv.addObject("showType",showType);
        //添加水印参数
        mv.addObject("watermark_company_flag",mapCompany.get("configValidFlag"));
        mv.addObject("companyValue",mapCompany.get("configValue"));
        return mv;
    }

    /**
     * @title: 跳转预览视频页面
     * @description: 跳转预览视频页面
     * @date: 2018-2-1.
     * @author: yjs
     * @param: request   response
     * @return: String 文件路径
     */
    @RequestMapping("/toShowVoice")
    public String toShowVoice(String fileType, String keyWords, String id, Model model,String showType,String shareForward) {
/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        if (!StringUtils.equals("1",shareForward)) {
            boolean authorityFlag = filesService.checkFilePreviewAuthority(id);
            if(!authorityFlag){
                return PREVIEW_ERROR_HTML;
            }
        }
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("id", id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("category", orderType);
        model.addAttribute("showType",showType);
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/previewunstruct/showVoice.html";
    }

    /**
     * @title: 跳转预览视频页面
     * @description: 跳转预览视频页面
     * @date: 2018-2-1.
     * @author: yjs
     * @param: request   response
     * @return: String 文件路径
     */
    @RequestMapping("/toShowVideo")
    public String toShowVideo(String fileType, String keyWords, String id, Model model,String showType,String shareForward) {
/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        if (!StringUtils.equals("1",shareForward)) {
            boolean authorityFlag = filesService.checkFilePreviewAuthority(id);
            if(!authorityFlag){
                return PREVIEW_ERROR_HTML;
            }
        }
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("id", id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("showType",showType);
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/previewunstruct/showVideo.html";
    }

    /**
     * @title: 跳转预览其他页面
     * @description: 跳转其他页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return: String 文件路径
     */
    @RequestMapping("/toShowOthers")
    public String toShowOthers(String fileType, String keyWords, String id, Model model,String showType,String shareForward) {
        keyWords = XSSUtil.xss(keyWords);
        fileType = XSSUtil.xss(fileType);
        id = XSSUtil.xss(id);
        showType = XSSUtil.xss(showType);
/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        if (!StringUtils.equals("1",shareForward)) {
            boolean authorityFlag = filesService.checkFilePreviewAuthority(id);
            if(!authorityFlag){
                return PREVIEW_ERROR_HTML;
            }
        }
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        model.addAttribute("fileType", type);
        model.addAttribute("key", docName);
        model.addAttribute("id", id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("showType",showType);
        //获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        model.addAttribute("userName", userName);
        return "/doc/front/previewunstruct/showOthers.html";
    }


    /**
     * @title 查询文件夹中的图片
     * @description 返回该ID文件夹中含有的图片信息
     * @param folderId 传入的要查询的文件夹ID
     * @date 2018-11-02.
     * @author luzhanzhao
     * @return 该文件夹中包含的图片信息
     */
    @PostMapping("/folderIMG")
    @ResponseBody
    public Map<String,Object> folderIMG(Integer page, Integer size, String folderId, String docId){
        Map<String,Object> imgs = new HashMap<>();
        List<Map> list = new ArrayList<>();
        int totalPages = 0;
        int total = 0;
        if (folderId != null) {
            String userId = UserInfoUtil.getCurrentUser().getId();
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
            List<String> roleList = ShiroKit.getUser().getRolesList();
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            //根据文件夹ID获取图片
            String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
            total = frontDocInfoService.getFolderImgCount(folderId,userId,listGroup,levelCode,adminFlag,orgId,roleList);
            List<DocInfo> docInfos = frontDocInfoService.getFolderIMG(page,size,folderId,userId,listGroup,levelCode,adminFlag,orgId,roleList);


/*          lambda表达式实现方式
            docInfos.forEach((docInfo -> {

            }));*/
            for (DocInfo docInfo:docInfos){
/*                if (docInfo.getDocId().equals(docId)){
                    continue;
                }*/
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("adminFlag", adminFlag);
                map.put("authority", docInfo.getAuthority());
                map.put("id", docInfo.getDocId());
                map.put("filePath", docInfo.getFilePath());
                map.put("filePdfPath", docInfo.getFilePdfPath());
                map.put("userId", docInfo.getUserId());
                map.put("author", docInfo.getAuthorName());
                map.put("createTime", docInfo.getCreateTime());
                map.put("title", docInfo.getTitle());
                map.put("docType",docInfo.getDocType().replace(".",""));
                map.put("docId",docInfo.getDocId());
                map.put("thumbPath", docInfo.getThumbPath());
                if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                    //文件大小转化
                    map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
                }
                if (docInfo.getDocId().equals(docId)){
                    map.put("isSelf",true);
                    list.add(map);
                    continue;
                } else {
                    map.put("isSelf", false);
                }
                list.add(map);
            }

        }
        totalPages = total % size == 0 ? total / size : total / size + 1;
        imgs.put("items",list);
        imgs.put("success",true);
        imgs.put("total",total);
        imgs.put("totalPages", totalPages);
        return imgs;

    }

    /**
     * @title 查询推荐的图片
     * @date 2018-11-5
     * @author luzhanzhao
     * @description 查询相关的图片
     * @param keyword 图片的关键字
     * @param page 页面
     * @param size 查询数据长度
     * @param folderId 文件夹ID，过滤用
     * @param tagString 填充字段
     * @return 返回要查询的推荐图片信息
     * @date 2018-11-8 代码重构
     */
    @PostMapping("/recommendIMG")
    @ResponseBody
    public List<Map> recommendIMG(String keyword, Integer page, Integer size, String folderId, String tagString) {
        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        String keywordResult = "";
        try {
            keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ESResponse<Map<String, Object>> sd = esService.search(keywordResult, "image", page, adminFlag,size,tagString,null,null,null,null,null,null);
        List<Map<String, Object>> list = sd.getItems();
        List<Map> docList = new ArrayList<>();
        if (null != list && list.size() > 0) {
            List<String> idList = new ArrayList<String>();
            // for循环拼接文件id
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String id = map.get("id") == null ? "" : map.get("id").toString();
                idList.add(id);
            }
            docList = frontDocInfoService.getRecommendIMG(idList, folderId);
        }
        if (docList.size()==0){
            docList = frontDocInfoService.getPopularImg(folderId);
        }
        docList.forEach(doc -> doc.put("docType", doc.get("docType").toString().replace(".", "")));
        return docList;
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 获取图片预览的推荐文档
     * @param currentId 当前文档的id
     * @param keyword 文档名称
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @param tagString
     * @return 推荐文档集合
     */
    @PostMapping("/recommendArticle")
    @ResponseBody
    public List<Map> recommendArticle(String currentId, String keyword, Integer pageNumber, Integer pageSize, String tagString) {
        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        String keywordResult = "";
        try {
            keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        FsFolderParams fsFolderParamsNew = new FsFolderParams();
        fsFolderParamsNew.setGroupList(listGroup);
        fsFolderParamsNew.setUserId(userId);
        fsFolderParamsNew.setType("2");
        String folderIdString = businessService.getFolderIdByUserUpload(fsFolderParamsNew);
        ESResponse<Map<String, Object>> sd = esService.search(keywordResult, "allword", pageNumber, adminFlag,pageSize,null,null,null,null,null,folderIdString,null);
        List<Map<String, Object>> list = sd.getItems();
        List<Map> docList = new ArrayList<>();
        int size = 0;
        if (null != list && list.size() > 0) {
            List<String> idList = new ArrayList<String>();
            // for循环拼接文件id
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String id = map.get("id") == null ? "" : map.get("id").toString();
                if (!id.equals(currentId)){
                    idList.add(id);
                }
            }
            if (idList.size() != 0){
                docList = frontDocInfoService.getRecommendArticle(idList,currentId);
            }
            size = 5-docList.size();
        }
        docList.addAll(frontDocInfoService.getArticleByReadNum(size, docList,currentId));
        return docList;
    }

    /**
     * 词条相关推荐
     */
    @PostMapping("/recommendEntry")
    @ResponseBody
    public List<EntryInfo> recommendEntry(String currentId, String keyword, Integer pageNumber, Integer pageSize, String tagString) {
        // 判断是不是文库超级管理员
        String keywordResult = "";
        try {
            keywordResult = URLDecoder.decode(keyword.replaceAll("%", "%25"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ESResponse<Map<String, Object>> sd = esService.search(keywordResult, "entry", pageNumber, true, pageSize, null, null, null, null, null, "", null);
        List<Map<String, Object>> list = sd.getItems();
        List<EntryInfo> entryList = new ArrayList<EntryInfo>();
        int size = 0;
        if (null != list && list.size() > 0) {
            List<String> idList = new ArrayList<String>();
            // for循环拼接文件id
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String id = map.get("id") == null ? "" : map.get("id").toString();
                if (!id.equals(currentId)) {
                    idList.add(id);
                }
            }
            if (idList.size() != 0) {
                Page<EntryInfo> page = new Page<EntryInfo>(0, 8);
                QueryWrapper<EntryInfo> qw = new QueryWrapper<EntryInfo>();
                qw.in("id", idList);
                entryList.addAll(entryInfoService.page(page, qw).getRecords());
            }
        }
        if (entryList.size() < 5) {
            Page<EntryInfo> page = new Page<EntryInfo>(0, 5 - entryList.size());
            QueryWrapper<EntryInfo> qw = new QueryWrapper<EntryInfo>();
            qw.orderByDesc("read_num");
            qw.eq("state","1");
            qw.eq("valid_flag","1");
            entryList.addAll(entryInfoService.page(page, qw).getRecords());
        }
        return entryList;
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-28
     * @description 猜你喜欢的文章
     * @param currentId 当前文档的id
     * @return 猜你喜欢的集合
     */
    @PostMapping("/guessYouLike")
    @ResponseBody
    public List<Map> guessYouLike (String currentId){
        if (currentId != null && !currentId.equals("")){
            return frontDocInfoService.guessYouLike(currentId);
        } else {
            return null;
        }
    }

    @RequestMapping("/checkUploadState")
    @ResponseBody
    public boolean checkUploadState(String docId){
        return uploadService.checkUploadStateFromFast(docId);
    }
    @RequestMapping("/checkVideoState")
    @ResponseBody
    public boolean checkVideoState(String docId){
        return uploadService.checkVideoStateFromFast(docId);
    }
    @RequestMapping("/folderViewerFlow")
    public String folderViewerFlow(String fileName,String docId, String folderId, Model model){
        fileName =XSSUtil.xss(fileName);
        docId =XSSUtil.xss(docId);
        folderId =XSSUtil.xss(folderId);
        model.addAttribute("docId", docId);
        model.addAttribute("folderId", folderId);
        // 获取当前的登录用户
        String userName = UserInfoUtil.getUserInfo().get("NAME").toString();
        List<String> roleList = ShiroKit.getUser().getRolesList();

        // 获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String type = fileType == null ? "" : fileType;
        model.addAttribute("fileType", type);
        model.addAttribute("userName", userName);
        model.addAttribute("fileName", fileName);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("isPersonCenter",false);
        return "/doc/front/previewunstruct/folder-imgFlow.html";
    }
    /**
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return: mv
     */
    @RequestMapping("/toShowComponent")
    public ModelAndView toShowPDF(String id, HttpServletRequest request) {

        ModelAndView mv = new ModelAndView("/doc/front/previewunstruct/showComponent.html");
        String keyword = request.getParameter("keyword");
        String userId = UserInfoUtil.getCurrentUser().getId();
        mv.addObject("userId", userId);
        mv.addObject("isPersonCenter", false);
        mv.addObject("userName", UserInfoUtil.getCurrentUser().getName());
        mv.addObject("id", id);
        String url = fsFolderService.getPersonPic(UserInfoUtil.getCurrentUser().getName());
        mv.addObject("url", url);
        return mv;
    }

    /**
     * 手动转换文件
     * @param docId 文件docId
     */
    @PostMapping("/handTransFile")
    @ResponseBody
    public void handTransFile(String docId) {
        byte[] bytes = null;
        FileOutputStream fos = null;
        FileInputStream input = null;
        File file = null;
        File fileKey = null;
        try {
            boolean theadFlag =true;
            Map<String, String> ready = new HashMap<>();
            FsFile fsFileTemp =  filesMapper.selectById(docId);
            LOGGER.info("******************文件:"+fsFileTemp.getFileName()+"进入handTransFile方法，开始转化PDF******************");
            String filePath = fsFileTemp.getFilePath();
            String sourceKey = fsFileTemp.getSourceKey();
            if (filePath != null && !"".equals(filePath) && sourceKey != null && !"".equals(sourceKey)) {
                // ********************************先从fast中下载文件并解密到本地服务器----开始*************************
                if (!fastdfsUsingFlag) {
                    input = new FileInputStream(filePath);
                    bytes = new byte[input.available()];
                    input.read(bytes);
                } else {
                    bytes = fastdfsService.download(filePath);
                }
                //在本地生成随机文件
                String random = fsFileTemp.getMd5();
                String name = fsFileTemp.getFileName();
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
                boolean isDecrypt = false;
                if (!fileExist) {
                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadFileByKey + random + suffix, downloadFile + random + suffix, fsFileTemp.getSourceKey());
                } else {
                    isDecrypt = true;
                }
                LOGGER.info("******************文件:" + name +
                        "手动转换解密完成，路径为" + fileKey.getPath() + ",大小为" + fileKey.length() + "******************");

                // ********************************先从fast中下载文件并解密到本地服务器----结束*************************

                // ********************************手动转换开始****************************************
                String sourcePath = downloadFile + random + suffix;
                ready.put("docId",docId);
                sourceFile = new File(sourcePath);
                if (!sourceFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    sourceFile.getParentFile().mkdirs();
                }
                if (sourceFile.exists()) {
                    //转换成的pdf路径
                    String pdfFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
                    pdfFilePath = pdfPathDir+ pdfFilePath.substring(pdfFilePath.lastIndexOf("/")+1,pdfFilePath.length());
                    String pdfKeyFilePath = pdfKeyPath+ pdfFilePath.substring(pdfFilePath.lastIndexOf("/")+1,pdfFilePath.length());
                    //PDF文件1
                    pdfFile = new File(pdfFilePath);

                    // 文档内容
                    String content = null;
                    //创建文件对象
                    FsFile fsFile = new FsFile();
                    //文件类型
                    String contentType = getContentType(suffix);
                    // 转换标志 （false:未成功转换，true:成功转换ao）
                    boolean flag = false;
                    Map<String, Object> pdfInfo = new HashMap<String, Object>();
                    // 根据文档类型转换格式

                    if (sourcePath.endsWith(".ceb")) {
                        pdfFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
                        ApiClient client = new ApiClient();
                        JSONObject cebName = new JSONObject();
                        cebName.put("cebName", sourcePath);
                        //TODO ceb文件未加水印
                        String ceb = client.cebToPdf(cebName);
                        if (ceb != null && ceb.contains("true")) {
                            Map<String, Object> metadata = TikaUtil.autoParse(pdfFilePath);
                            content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                            ready.put("content",content);
                        }
                    } else if (contentType.contains("word") || contentType.contains("rtf")|| contentType.contains("works")) {
                        LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                    } else if (contentType.contains("text/html") && (sourcePath.endsWith(".doc") || sourcePath.endsWith(".docx"))) {
                        //网络导出的doc格式文件
                        LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                    } else if (contentType.contains("excel") || contentType.contains("spreadsheetml")) {
                        // 获取文件后缀
                        String fileType = fsFileTemp.getFileType();
                        //转换成的pdf路径
                        String xlsxFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".xlsx";
                        // knowledge/pdfFile/fileName.xlsx
                        pdfFilePath = pdfPathDir + xlsxFilePath.substring(xlsxFilePath.lastIndexOf("/") + 1, xlsxFilePath.length());
                        pdfFile = new File(pdfFilePath);
                        pdfKeyFilePath = pdfKeyPath + xlsxFilePath.substring(xlsxFilePath.lastIndexOf("/") + 1, xlsxFilePath.length());

                        // 如果是xls文件  转换成xlsx文件 (因为luckysheet插件只支持 xlsx文件预览)
                        if (StringUtils.equals(fileType, ".xls")) {
                            LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                        } else if (StringUtils.equals(fileType, ".xlsx")) {
                            FileUtils.copyFile(sourceFile, pdfFile);
                        }
                    } else if (contentType.contains("powerpoint") || contentType.contains("presentationml")) {
                        LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                    } else if ((contentType.contains("octet-stream") || contentType.contains("text/plain"))
                            && sourcePath.endsWith(".txt")) {
                        // 只处理txt文件，防止其他文件转换异常
                        LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                        Map<String, Object> metadata = TikaUtil.autoParse(pdfFilePath);
                        content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                        ready.put("content", content);
                    } else {
                        LOGGER.info("******************文件:"+fsFileTemp.getFileName()+"不需要转化PDF，进行下一步******************");
                        theadFlag = false;
                    }
                    if(theadFlag) {
                        //pdf文件路径（上传到FAStFDS上返回的路径）
                        String pdfPath = null;

                        //启用FASTDFS时将文件上传到服务器
                        //如果是pdf则只需要一个文件即可
                        if (contentType == null || contentType.contains("application/pdf")
                                || contentType.contains("audio") || contentType.contains("video")) {
                            pdfPath = fsFileTemp.getFilePath();
                            LOGGER.info("******************视频音频PDF文件:" + fsFileTemp.getFileName() + "不需要转化PDF，");
                        } else {
                            //上传PDF到FastFDS
                            if (!StringUtil.checkIsEmpty(pdfFilePath)) {
                                pdfFile = new File(pdfFilePath);
                                if (!pdfFile.getParentFile().exists()) {
                                    // 路径不存在,创建
                                    pdfFile.getParentFile().mkdirs();
                                }
                                LOGGER.info("******************PDF文件:" + fsFileTemp.getFileName() + "创建成功，路径为"
                                        + pdfFile.getPath() + ",大小为" + pdfFile.length() + "******************");
                                //如果markedpdfPath（打水印后pdf路径） 不为空
                                //启用FASTDFS时将文件上传到服务器
                                if (cacheToolService.getFastDFSUsingFlag()) {
                                    pdfKeyDir = new File(pdfKeyFilePath);
                                    if (!pdfKeyDir.getParentFile().exists()) {
                                        // 路径不存在,创建
                                        pdfKeyDir.getParentFile().mkdirs();
                                    }
                                    //文件加密并取出加密密码存到数据库
                                    String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile, pdfKeyDir);
                                    fsFile.setPdfKey(pdfKey);
                                    pdfPath = fastdfsService.uploadFile(pdfKeyDir);
                                    if(pdfKeyDir != null && pdfKeyDir.exists()  ){
                                        pdfKeyDir.delete();
                                    }
                                    if(pdfFile != null && pdfFile.exists()  ){
                                        pdfFile.delete();
                                    }
                                    LOGGER.info("******************加密PDF文件:" + pdfKeyDir.getName() +
                                            "创建成功，路径为" + pdfKeyDir.getPath() + ",大小为" + pdfKeyDir.length() + "," +
                                            "并上传到fast，fast返回地址为" + pdfPath + "******************");
                                } else {
                                    File pdfKeyDir = new File(pdfKeyFilePath);
                                    if (!pdfKeyDir.getParentFile().exists()) {
                                        // 路径不存在,创建
                                        pdfKeyDir.getParentFile().mkdirs();
                                    }
                                    String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile,pdfKeyDir);
                                    fsFile.setPdfKey(pdfKey);
                                    pdfPath = pdfKeyDir.getPath();
                                }
                            }
                        }
                        // 更新文件信息
                        fsFile.setFileId(docId);
                        fsFile.setFilePdfPath(pdfPath);
                        fsFile.setSize(fsFileTemp.getSize());
                        filesMapper.updateById(fsFile);
                        LOGGER.info("******************文件:" + fsFileTemp.getFileName() + "此线程结束，进行下一流程******************");
                    }
                    // ********************************手动转换结束****************************************
                    // 更新ES
                    threadPoolExecutor.execute(new CreateEsThread(docId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("手动转化PDF失败：" + ExceptionUtils.getErrorInfo(e));
        }finally {
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
            if (file != null && file.exists()) {
                file.delete();
            }
            if (fileKey != null && fileKey.exists()) {
                fileKey.delete();
            }
            if(pdfKeyDir != null && pdfKeyDir.exists()  ){
                pdfKeyDir.delete();
            }
            if(pdfFile != null && pdfFile.exists()  ){
                pdfFile.delete();
            }
        }
    }
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
