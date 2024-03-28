package com.jxdinfo.doc.front.docmanager.controller;

import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.DeviceUtil;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.LibreOfficePDFConvert;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.PreviewService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 类的用途：分享文件控制层<p>
 * 创建日期：2018年9月28日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月28日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 *
 * @author XuXinYing
 * @version 1.0
 */
@Controller
@RequestMapping("/sharefile")
public class ShareFileController {
    @Autowired
    private IFsFolderService fsFolderService;

    @Resource
    private UploadService uploadService;

    /** 配置信息服务层 */
    @Resource
    private DocConfigureService docConfigureService;
    @Resource
    private ISysUsersService sysUsersService;
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private DocConfigService docConfigService;
    @Autowired
    private ISysUserRoleService sysUserRoleService;

    private static Logger LOGGER = LoggerFactory.getLogger(ShareFileController.class);


    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;

    @Value("${docbase.pdfFile}")
    private String pdfPathDir;
    @Value("${docbase.pdfFileByKey}")
    private String pdfKeyPath;

    /**
     * 文件 Mapper 接口
     */
    @Resource
    private FsFileMapper fsFileMapper;
    /**
     * 上传标志
     */
    private boolean fastdfsUsingFlag = true;

    @Autowired
    private DocGroupService docGroupService;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 排序类型
     */
    private String orderType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * PDF文件ID
     */
    private String pdfFileId;

    /**
     * 文件预览服务类
     */
    @Autowired
    private PreviewService previewService;

    /**
     * fast服务器服务类
     */
    @Autowired
    FastdfsService fastdfsService;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    /**
     * 平台缓存服务类
     */
    @Autowired
    private HussarCacheManager hussarCacheManager;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private ShareResourceService shareResourceService;

    /**
     * 播放video视频
     *
     * @param request
     * @param response
     * @return void
     */
    @RequestMapping("/video")
    public void getVideo(HttpServletRequest request, HttpServletResponse response) {
        // 生产UUID
        String uuid = UUID.randomUUID().toString();
        // 初始化参数和流
        RandomAccessFile randomFile = null;
        ServletOutputStream out = null;
        // 从前台获取fileId
        String hash = request.getParameter("hash");
        Map getHash = shareResourceService.getPdfPath(hash);
        pdfFileId = getHash.get("pdfPath").toString();
        Map<String, String> readInfo = new HashMap<String, String>();
        // mp4预览 读锁
        Object cacheMap = hussarCacheManager.getObject(CacheConstant.MP4_PREVIEW_READ_LOCK, pdfFileId);
        if (cacheMap != null) {
            readInfo = (Map<String, String>) cacheMap;
        }
        // 将信息传入map中
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
            // 设置消息头
            response.setContentType("video/mp4");
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("ETag", fileName);
            response.setHeader("Last-Modified", new Date().toString());
            // 第一次请求只返回content length来让客户端请求多次实际数据
            if (range == null) {
                response.setHeader("Content-length", contentLength + "");
            } else {
                // 以后的多次以断点续传的方式来返回视频数据
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);// 206
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
                    // 设置消息头
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range",
                            "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
                } else {
                    length = contentLength - requestStart;
                    // 设置消息头
                    response.setHeader("Content-length", "" + length);
                    response.setHeader("Content-Range",
                            "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
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
    @CrossOrigin
    @RequestMapping("/list")
    public void getList(HttpServletRequest request, HttpServletResponse response) {
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
                List<FsFile> list = fsFileMapper.getInfoByPdfPath(pdfFileId);
                String random = list.get(0).getMd5();
                String suffix = pdfFileId.substring(pdfFileId.lastIndexOf("."));
                file = new File(downloadPdfFile + "" + random + suffix);
                //文件不存在则重新读取
                if (!file.exists()||file.length()==0) {
                    file = fileTool.chuckPdf(pdfFileId);

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
                byte[] bytes = fileTool.downLoadFile(input, pdfFileId, isThumbnails);
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
     * excel预览
     *
     * @param request
     * @param response
     * @param hash
     * @throws ServiceException
     */
    @RequestMapping("/excel/{id}")
    public void downloadXlxsFileForShare(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws ServiceException {
        // 获取id
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
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowFolder")
    public String toShowFolder(String id, HttpServletRequest request, Model model) {
        String keyword = request.getParameter("keyword");
        model.addAttribute("fileName", keyword);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("openFileId",id);
        model.addAttribute("folderName",fsFolderService.getById(id).getFolderName());

        return "/doc/front/preview/shareFolder.html";
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
    public String toShowPDF(String id, HttpServletRequest request, Model model) {
        String keyword = request.getParameter("keyword");
        model.addAttribute("fileName", keyword);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);
        String requestHeader = request.getHeader("user-agent");
        if (DeviceUtil.isMobileDevice(requestHeader)){
            return "/doc/front/preview/sharePDF_mobile.html";
        }
        return "/doc/front/preview/sharePDF.html";
    }

    /**
     * 获取文件路径
     *
     * @param docId
     * @return List<Map < String ,   String>> 获得路径的map集合
     */
    @RequestMapping("/getFoldPath")
    @ResponseBody
    public List<Map<String, String>> getFoldPathByDocId(String docId,String showType) {
        return previewService.getFoldPathByDocId(docId,showType);
    }
    /**
     * 获取文件夹路径
     *
     * @param folderId  文件夹id
     * @param folderName 文件夹名
     * @return List<Map < String ,   String>> 获得路径的map集合
     */
    @RequestMapping("/getFolderPath")
    @ResponseBody
    public List<Map<String, String>> getFolderPathByDocId(String folderId,String folderName) {
        Map<String,String> map = new HashMap<>();
        map.put("foldId",folderId);
        map.put("foldName",folderName);
        return previewService.getFoldPathByFolder(map);
    }
    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/fileDetail")
    @ResponseBody
    public Map<String, Object> getFileDetail(String hash) {
        boolean pdfFlag = shareResourceService.isPdfPathExist(hash);
        if (!pdfFlag){
            shareResourceService.setPdfPathFast(hash);
        }
        Map map = frontDocInfoService.getDocByHash(hash);
        map.put("fileSuffixName", map.get("fileSuffixName").toString().substring(map.get("fileSuffixName").
                toString().lastIndexOf(".") + 1));
        map.put("fileSize",FileTool.longToString(map.get("fileSize").toString()));
        map.put("uploadState",pdfFlag);
        map.put("docId",map.get("docId"));
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
    public ModelAndView toShowIMG(String id,HttpServletRequest request) {
        ModelAndView mv;

        String requestHeader = request.getHeader("user-agent");
        if (DeviceUtil.isMobileDevice(requestHeader)){
            mv = new ModelAndView("/doc/front/preview/shareImg_mobile.html");
        } else {
            mv = new ModelAndView("/doc/front/preview/shareImg.html");
        }
        mv.addObject("fileType", fileType);
        mv.addObject("isPersonCenter", false);
        mv.addObject("category", orderType);
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
    public String toShowVoice(String id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("userName", "分享用户");
        model.addAttribute("isPersonCenter",false);
        return "/doc/front/preview/shareVoice.html";
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
    public String toShowVideo(String id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("userName", "测试");
        return "/doc/front/preview/shareVideo.html";
    }

    @RequestMapping("/toShowOthers")
    public String toShowOthers(String id, Model model) {
        id = XSSUtil.xss(id);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);

        model.addAttribute("id", "id");
        return "/doc/front/preview/shareOthers.html";
    }

    /**
     * 分享文件预览
     *
     * @param id      文件ID
     * @param request
     * @param type    文件类型
     * @return
     * @Title: fileView
     * @author: XuXinYing
     */
    @RequestMapping("/fileView")
    public String shareFile(String type, String id, HttpServletRequest request, Model model) {
        //   测试地址 http://localhost:8080/sharefile/fileView?type=mp4&id=d9cb8232e0cf41288c2fc7606abd6598
        String path = "";
        if ("pdf".equals(type)) {
            path = toShowPDF(id, request, model);
        }
        if ("mp4".equals(type)) {
            path = toShowVideo(id, model);
        }
        return path;
    }

    /**
     * @title 查询文件夹中的图片
     * @description 返回该ID文件夹中含有的图片信息
     * @param folderId 传入的要查询的文件夹ID
     * @date 2018-11-02.
     * @author luzhanzhao
     * @return 该文件夹中包含的图片信息
     */
    @RequestMapping("/folderIMG")
    @ResponseBody
    public Map<String,Object> folderIMG(String page, String limit, String folderId, String docId){
        Map<String,Object> imgs = new HashMap<>();
        List<Map> list = new ArrayList<>();
        folderId = "8e4abfa7c6bb470dafb90947966efc6f";
        //分页处理
        int pageNum = Integer.parseInt(page);
        int pageSize = Integer.parseInt(limit);
        int startIndex = (pageNum - 1) * pageSize;
        int count =  frontDocInfoService.getFolderImgForShareCount(folderId);
        if (folderId != null) {
            //根据文件夹ID获取图片
            List<DocInfo> docInfos = frontDocInfoService.getFolderImgForShare(startIndex,pageSize,folderId);


/*          lambda表达式实现方式
            docInfos.forEach((docInfo -> {

            }));*/
            for (DocInfo docInfo:docInfos){
/*                if (docInfo.getDocId().equals(docId)){
                    continue;
                }*/
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("authority", docInfo.getAuthority());
                map.put("id", docInfo.getDocId());
                map.put("filePath", docInfo.getFilePath());
                map.put("filePdfPath", docInfo.getFilePdfPath());
                map.put("title", docInfo.getTitle());
                map.put("docType",docInfo.getDocType().replace(".",""));
                map.put("docId",docInfo.getDocId());
                if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                    //文件大小转化
                    map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
                }
                if (docInfo.getDocId().equals(docId)){
                    map.put("isSelf",true);
                    list.add(0,map);
                    continue;
                } else {
                    map.put("isSelf", false);
                }
                list.add(map);
            }

        }
        imgs.put("items",list);
        imgs.put("success",true);
        imgs.put("total",list.size());
        imgs.put("count",count);
        return imgs;

    }


    @RequestMapping("/checkUploadState")
    @ResponseBody
    public boolean checkUploadState(String hash){
        return shareResourceService.isPdfPathExist(hash);
    }
    @RequestMapping("/voices")
    public void getVoices(HttpServletRequest request, HttpServletResponse response) {
        //生产UUID
        String uuid = UUID.randomUUID().toString();
        //初始化参数和流
        RandomAccessFile randomFile = null;
        ServletOutputStream out = null;
        //从前台获取fileId
        pdfFileId = request.getParameter("fileId");
        Map<String, String> readInfo = new HashMap<String, String>();
        // mp4预览   读锁
        Object cacheMap = hussarCacheManager.getObject(CacheConstant.MP3_PREVIEW_READ_LOCK, pdfFileId);
        if (cacheMap != null) {
            readInfo = (Map<String, String>) cacheMap;
        }
        //将信息传入map中
        readInfo.put(uuid, CacheConstant.MP3_PREVIEW_READ_LOCK_FLAG);
        hussarCacheManager.setObject(CacheConstant.MP3_PREVIEW_READ_LOCK, pdfFileId, readInfo);

        File file = null;
        try {
            file = fileTool.downLoadFileForMp3(pdfFileId);
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
            response.setContentType("audio/mp3");
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
    @RequestMapping("/videos")
    public void getVideos(HttpServletRequest request, HttpServletResponse response) {
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
     * @title: 查询下级节点
     * @description: 查询下级节点（文件和目录）
     * @date: 2018-8-12.
     * @author: yjs
     */
    @RequestMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(@RequestParam String id,
                              @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                              @RequestParam(value = "pageSize", defaultValue = "60") int pageSize, String order, String name,
                              String type, String nameFlag, String operateType) {
        Map orderMap = new HashMap();
        Map typeMap = new HashMap();
        String  isDesc="0";
        if("1".equals(order)||"3".equals(order)){
            isDesc = "1";
        }
        //排序和查询规则
        orderMap.put("0", "fileName");
        orderMap.put("1", "fileName");
        orderMap.put("2", "createTime");
        orderMap.put("3", "createTime");
        typeMap.put("1", ".doc,.docx");
        typeMap.put("2", ".ppt,.pptx");
        typeMap.put("3", ".txt");
        typeMap.put("4", ".pdf");
        typeMap.put("5", ".xls,.xlsx");
        String orderResult = (String) orderMap.get(order);
        Map<String, Object> result = new HashMap<>(5);
        List<FsFolderView> list = new ArrayList<>();
        int num = 0;
        //判断是否为子级目录（只能在子文件夹上传文件）
        boolean isChild = fsFileService.isChildren(id);
        String userId =null;
        List<String> listGroup = null;
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag =1;
        FsFolder fsFolder = fsFolderService.getById(id);
        String[] typeArr;
        if (type == null) {
            type = "0";
        }
        if ("0".equals(type)) {
            typeArr = null;
        } else {
            String typeResult = (String) typeMap.get(type);
            typeArr = typeResult.split(",");
        }

        name = StringUtil.transferSqlParam(name);
        FsFolder folder=fsFolderService.getById(id);
        FsFolderParams fsFolderParams = new FsFolderParams();
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(operateType);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        fsFolderParams.setId(id);
        fsFolderParams.setRoleList(roleList);
//        List<String> levelCodeList = folderService.getlevelCodeList(listGroup, userId, type);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        //获得下一级文件和目录
        list = fsFolderService.getFilesAndFloderByFolderShare((pageNumber - 1) * pageSize, pageSize, id, typeArr, name,
                orderResult, listGroup, userId, adminFlag, operateType, null, null,isDesc,null);
        list = changeSize(list);

        //获得下一级文件和目录数量
        num = fsFolderService.getFilesAndFloderNumByFolderShare(id, typeArr, name, orderResult, listGroup, userId,
                adminFlag, operateType, null, null,null);
        //显示前台的文件数量
        int amount = fsFolderService.getFileNumByFolderShare(id, typeArr, name, listGroup, userId, adminFlag, operateType, null,null);
        //判断是否有可编辑文件的权限
        result.put("noChildPower", 2);
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
            folderAmount="4";
        }
        result.put("folderAmount", folderAmount);

        result.put("noChildPowerFolder", 1);

        result.put("isAdmin", adminFlag);
        result.put("total", num);
        result.put("rows", list);
//        result.put("isChild", isChild);
        result.put("amount", amount);

        return result;
    }
    /**
     * 转化文件大小的方法
     */
    public List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
        }
        return list;
    }

    /**
     * @return 获得标签
     * @Author yjs
     * @Description 跳转到新增标签页面
     * @Date 14:36 2019/6/19
     * @Param []
     **/
    @RequestMapping("/getPreviewType")
    @ResponseBody
    public Map<String, String>  getPreviewType(String docId, String suffix) {
        String json= "";
        suffix = suffix.substring(1,suffix.length());
        List<DocConfigure> typeList =  docConfigureService.getConfigure();
        if(typeList.get(5).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","1");
            return resultMap;
        } if(typeList.get(6).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","2");
            return resultMap;
        }
        if(typeList.get(7).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","3");
            return resultMap;
        }
        if(typeList.get(8).getConfigValue().contains(suffix.toLowerCase())){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","4");
            return resultMap;
        } if(suffix.equals("component")){
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","10");
            return resultMap;
        }else{
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code","5");
            return resultMap;
        }
    }
    @RequestMapping("/checkUploadStateFolder")
    @ResponseBody
    public boolean checkUploadStateFloder(String id){
        return uploadService.checkUploadStateFromFast(id);
    }
    /**
     * @title: 跳转预览视频页面
     * @description: 跳转预览视频页面
     * @date: 2018-2-1.
     * @author: yjs
     * @param: request   response
     * @return: String 文件路径
     */
    @RequestMapping("/toShowVideoByFolder")
    public String toShowVideoByFolder(String id, Model model) {
        ModelAndView mv;

/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        //是否添加用户水印
        DocInfo docInfo =frontDocInfoService.getById(id);
        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String, String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //获取当前的登录用户
        String type = fileType == null ? "" : fileType;
        model.addAttribute("fileType", type);
        model.addAttribute("id", id);
        model.addAttribute("url", "");
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        String userId =docInfo.getUserId();
        String name =  sysUsersService.getById(userId).getUserName();
        model.addAttribute("shareUser", name);
        model.addAttribute("favorite", false);
        //判断此文档是否在预览时添加用户水印
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

        return "/doc/front/preview/shareVideoFolder.html";
    }
    /**
     * @title: 跳转预览视频页面
     * @description: 跳转预览视频页面
     * @date: 2018-2-1.
     * @author: yjs
     * @param: request   response
     * @return: String 文件路径
     */
    @RequestMapping("/toShowVoiceByFolder")
    public String toShowVoiceByFolder(String id, Model model) {
        ModelAndView mv;

/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        //是否添加用户水印
        DocInfo docInfo =frontDocInfoService.getById(id);
        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String, String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //获取当前的登录用户
        String type = fileType == null ? "" : fileType;
        model.addAttribute("fileType", type);
        model.addAttribute("id", id);
        model.addAttribute("url", "");
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        String userId =docInfo.getUserId();
        String name =  sysUsersService.getById(userId).getUserName();
        model.addAttribute("shareUser", name);
        model.addAttribute("favorite", false);
        //判断此文档是否在预览时添加用户水印
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

        return "/doc/front/preview/shareVoiceFolder.html";
    }
    /**
     * @title: 跳转预览图片页面
     * @description: 跳转预览图片页面
     * @date: 2018-2-1.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowIMGByFolder")
    public ModelAndView toShowIMGByFolder(String id,HttpServletRequest request,Model model) {
        ModelAndView mv;
        String keyword  = request.getParameter("keyword");

/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        //是否添加用户水印
        DocInfo docInfo =frontDocInfoService.getById(id);
        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String, String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //获取当前的登录用户
        String type = fileType == null ? "" : fileType;
        model.addAttribute("fileType", type);
        model.addAttribute("key", keyword);
        model.addAttribute("id", id);
        model.addAttribute("url", "");
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("fileName", keyword);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        String userId =docInfo.getUserId();
        String name =  sysUsersService.getById(userId).getUserName();
        model.addAttribute("shareUser", name);
        model.addAttribute("favorite", false);
        //判断此文档是否在预览时添加用户水印
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));
        String requestHeader = request.getHeader("user-agent");
        if (DeviceUtil.isMobileDevice(requestHeader)){
            mv = new ModelAndView("/doc/front/preview/shareImg_mobileFolder.html");
        } else {
            mv = new ModelAndView("/doc/front/preview/shareImgFolder.html");
        }

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        mv.addObject("projectTitle", projectTitleMap.get("configValue"));

        mv.addObject("url", "");
        mv.addObject("fileType", fileType);
        mv.addObject("isPersonCenter", false);
        mv.addObject("category", orderType);
        return mv;
    }
    @RequestMapping("/getFolderPathByFolder")
    @ResponseBody
    public List<Map<String, String>> getFolderPathByFolder(String folderId,String folderName) {
        Map<String,String> map = new HashMap<>();
        map.put("foldId",folderId);
        map.put("foldName",folderName);
        map.put("shareFolder","1");
        return previewService.getFoldPathByFolder(map);
    }
    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/getFilePath")
    @ResponseBody
    public String getFilePath(HttpServletRequest request, HttpServletResponse response) {
        String pdfFileId = request.getParameter("fileId");
        File file = null;
        try {
            file = fileTool.downLoadFileMobile(pdfFileId);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }
    @RequestMapping("/fileDetailFolder")
    @ResponseBody
    public Map<String, Object> fileDetailFloder(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null) {

            //根据ID获得文档详情
            DocInfo docInfo = frontDocInfoService.getById(id);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            map.put("id", docInfo.getDocId());
            FsFile fsFile = fsFileService.getById(id);
            map.put("filePath", fsFile.getFilePath());
            map.put("filePdfPath", fsFile.getFilePdfPath());
            map.put("userId", docInfo.getUserId());
            map.put("author", docInfo.getAuthorName());
            map.put("createTime", docInfo.getCreateTime());
            map.put("title", docInfo.getTitle());
            map.put("shareFlag", docInfo.getShareFlag());
            map.put("tags", docInfo.getTags());
            //获得文件的类型字符串（前台根据文件类型判断展示内容）
            String fileSuffixName = docInfo.getDocType().substring(docInfo.getDocType().lastIndexOf(".") + 1);
            map.put("downloadNum", docInfo.getDownloadNum());
            map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()) + 1);
            map.put("fileSuffixName", fileSuffixName);
            map.put("docAbstract", docInfo.getDocAbstract());
            if (StringUtil.checkIsEmpty(fsFile.getFilePdfPath())) {
                uploadService.checkUploadState(id);
            }
            map.put("uploadState", !StringUtil.checkIsEmpty(fsFile.getFilePdfPath()));
            if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                //文件大小转化
                map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
            }
        }
        return map;
    }
    /**
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowPDFByFolder")
    public String toShowPDFByFolder(String id, HttpServletRequest request, Model model) {
//        FsFolder fsFolder = fsFolderService.selectById(frontDocInfoService.selectById(id).getFoldId());
//        String levelCode =fsFolder.getLevelCode();
//        fsFolderService.getbyl

        String keyword  = request.getParameter("keyword");

/*        //获取可分享权限
        boolean shareFlag = shareResourceService.getShareFlagByDocId(id);
        if (shareFlag){
            model.addAttribute("shareFlag",true);
        } else {
            model.addAttribute("shareFlag",false);
        }*/
        //是否添加用户水印
        DocInfo docInfo =frontDocInfoService.getById(id);
        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String, String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        //获取当前的登录用户
        String type = fileType == null ? "" : fileType;
        model.addAttribute("fileType", type);
        model.addAttribute("key", keyword);
        model.addAttribute("id", id);
        model.addAttribute("url", "");
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("fileName", keyword);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        String userId =docInfo.getUserId();
        String name =  sysUsersService.getById(userId).getUserName();
        model.addAttribute("shareUser", name);
        model.addAttribute("favorite", false);
        //判断此文档是否在预览时添加用户水印
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));
        String requestHeader = request.getHeader("user-agent");

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

        if (DeviceUtil.isMobileDevice(requestHeader)){
            return "/doc/front/preview/sharePDF_mobileFolder.html";
        }
        return "/doc/front/preview/sharePDFFolder.html";
    }
    /**
     * 获取根节点
     */
    @RequestMapping(value = "/getRoot")
    @ResponseBody
    public Map getRoot() {
        List<FsFolder> list = fsFileService.getRoot();
        Map map = new HashMap();
        FsFolder fsFile = list.get(0);
        map.put("root", fsFile.getFolderId());
        map.put("rootName", fsFile.getFolderName());
        return map;
    }
    /**
     * @title: 跳转预览pdf页面
     * @description: 跳转预览pdf页面
     * @date: 2018-9-6.
     * @author: yjs
     * @param: request   response
     * @return:
     */
    @RequestMapping("/toShowComponent")
    public String toShowComponent(String id, HttpServletRequest request, Model model,String type) {
        String keyword = request.getParameter("keyword");
        model.addAttribute("id", id);
        model.addAttribute("fileName", keyword);
        model.addAttribute("fileType", fileType);
        model.addAttribute("category", orderType);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("url", "");
        String requestHeader = request.getHeader("user-agent");
        if (DeviceUtil.isMobileDevice(requestHeader)){
            return "/doc/front/preview/shareComponent_mobile.html";
        }
        return "/doc/front/preview/shareComponent.html";

    }
    @RequestMapping("/videoNew")
    public void videoNew(HttpServletRequest request, HttpServletResponse response) {
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

}
