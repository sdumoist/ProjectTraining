package com.jxdinfo.doc.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocDelete;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocDeleteService;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 获取文件信息
 *
 * @author zhangzhen
 * @date 2018/3/12
 */
@Component
public class FileTool {

    static final public Logger logger = LogManager.getLogger(FileTool.class);

    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;


    /**
     * fast服务器服务类
     */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * fast服务器服务类
     */
    @Autowired
    private DocDeleteService docDeleteService;

    @Value("${docbase.downloadFile}")
    private String downloadFile;
    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;
    @Value("${docbase.downloadPdfFileByKey}")
    private String downloadPdfFileByKey;
    @Value("${docbase.ThumbnailsDir}")
    private String ThumbnailsDir;
    @Value("${docbase.videoDir}")
    private String videoDir;
    /**
     * 文件 Mapper 接口
     */
    @Resource
    private FsFileMapper fsFileMapper;

    /**
     * 返回上传文件信息
     * fileUploadInfo
     *
     * @param rootPath 上传路径
     * @param fileId   文件ID
     * @return Map
     */
    public static Map<String, String> fileUploadInfo(MultipartFile file, String rootPath, String fileId) {

        Map<String, String> map = new HashMap<String, String>(16);
        String originalFileName = file.getOriginalFilename();
        if (originalFileName.contains("\\")) {
            originalFileName = originalFileName.substring(originalFileName.lastIndexOf("\\") + 1);
        }
        // 原始名称
        String oldName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        // 原始的文件名(不含扩展名)
        String id = UUID.randomUUID().toString();
        // 文件ID
        String extendName = originalFileName.substring(originalFileName.lastIndexOf("."));
        // 上传文件的扩展名
        String fileName = id + extendName;
        // 文件的上传路径
        long fileLength = file.getSize();
        // 文件大小
        String fileSize = longToString(fileLength * 1024);
        // 带单位的文件大小
        map.put("docId", id);
        map.put("fileId", fileId);
        map.put("pdfFileId", "");
        map.put("fileName", fileName);
        map.put("docName", oldName);
        map.put("type", extendName);
        map.put("size", fileSize);

        return map;
    }

    /**
     * 返回批量上传文件信息
     * fileUploadInfo
     *
     * @param files    批量文件
     * @param rootPath 上传路径
     * @param fileId   文件ID
     * @return List
     */
    public static List<Map<String, String>> fileUploadInfo(List<MultipartFile> files, String rootPath, String fileId) {
        List<Map<String, String>> docInfoList = new ArrayList<Map<String, String>>();
        for (MultipartFile file : files) {
            docInfoList.add(fileUploadInfo(file, rootPath, fileId));
        }

        return docInfoList;
    }

    /**
     * 文件大小单位转换（保留两位小数）
     *
     * @return 带单位的文件大小
     */
    public static String longToString(String fileSize) {
        return doubleToString(Double.parseDouble(fileSize.substring(0, fileSize.length() - 2)));
    }

    /**
     * 文件大小单位转换
     *
     * @param size 文件大小
     * @return 带单位的文件大小
     */
    public static String longToString(long size) {
        return doubleToString((double) size);
    }

    /**
     * 文件大小单位转换
     *
     * @param size 文件大小(kb为单位)
     * @return 带单位的文件大小
     */
    public static String doubleToString(double size) {
        double kb = 1;
        double mb = kb * 1024;
        double gb = mb * 1024;
        String ret = "";

        DecimalFormat df = new DecimalFormat("0.0");
        DecimalFormat df1 = new DecimalFormat("0.00");
        if (size >= gb) {
            ret = df1.format(size / (gb * 1.0)) + " GB";
        } else if (size >= mb) {
            ret = df.format(size / (mb * 1)) + " MB";
        } else if (size >= 0) {
            ret = df.format(size / (kb * 1)) + " KB";
        }
        return ret;
    }

    /**
     * 将文件压缩成zip
     *
     * @param inputFile       要压缩的文件
     * @param zipoutputStream 压缩输出流
     * @param fileName        打包文件名
     */
    public static void zipFile(File inputFile, ZipOutputStream zipoutputStream, String fileName) {
        if (inputFile.exists()) {
            // 判断文件是否存在
            if (inputFile.isFile()) {
                // 判断是否属于文件，还是文件夹
                // 创建输入流读取文件
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(inputFile);
                    bis = new BufferedInputStream(fis);
                    // 将文件写入zip内，即将文件进行打包
                    ZipEntry ze = new ZipEntry(fileName);
                    // 获取文件名
                    zipoutputStream.putNextEntry(ze);
                    // 20180717 增加 编码 格式 解决 中文文件 在压缩包里乱码问题
                    zipoutputStream.setEncoding("GBK");
                    // 写入文件的方法，同上
                    byte [] b=new byte[1024 * 10 *10];
                    int l = 0;
                    while ((l = bis.read(b)) != -1) {
                        zipoutputStream.write(b);
                    }
                    // 关闭输入输出流
                    bis.close();
                    fis.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bis != null) {
                            bis.close();
                        }
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 文件下载
     *
     * @param input
     * @param filePath
     * @throws IOException
     * @throws ServiceException
     */
    public byte[] downLoadFile(FileInputStream input, String filePath, String isThumbnails) throws IOException, ServiceException {
        byte[] bytes = null;
        if ("".equals(filePath) || filePath == null || "undefined".equals(filePath)) {
            return bytes;
        }
        //首先取出服务器路径后缀进行判断
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        //是否启用fast服务器

        FileOutputStream fos = null;
        byte[] buffer = null;

        //如果是pdf文件则走预览的方法
        if (".pdf".equals(suffix) || "0".equals(isThumbnails)) {
            //通过PDF路径取出文件信息
            List<FsFile> list = fsFileMapper.getInfoByPdfPath(filePath);

            if (!fastdfsUsingFlag) {
                input = new FileInputStream(filePath);
                bytes = new byte[input.available()];
                input.read(bytes);
            } else {
                bytes = fastdfsService.download(filePath);
            }

            if (list != null && list.size() > 0) {
                String name = list.get(0).getFileName();
                logger.info("******************文件:" + name + "进入文件预览，******************");
                if (list.get(0).getPdfKey() == null && list.get(0).getSourceKey() == null) {
                    return bytes;
                }
                //在本地文件
                String random = list.get(0).getMd5();
                File file = new File(downloadPdfFileByKey + "" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                File fileKey = new File(downloadPdfFile + "" +
                        random + suffix);
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
                logger.info("******************文件:" + name +
                        "从fast下载完成，路径为" + file.getPath() + ",大小为" + file.length() + "******************");
                String key = list.get(0).getPdfKey();
                if (key == null) {
                    key = list.get(0).getSourceKey();
                }

                //文件解密
                boolean isDecrypt = false;

                if (fileExist == false) {

                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "" +
                            random + suffix, downloadPdfFile + "" +
                            random + suffix, key);
                } else {
                    isDecrypt = true;
                }

                logger.info("******************文件:" + name +
                        "解密完成，路径为" + fileKey.getPath() + ",大小为" + fileKey.length() + "******************");
                //判断是否解密成功
                if (isDecrypt) {
                    if (!fastdfsUsingFlag) {
                        FileInputStream fis = new FileInputStream(fileKey);
                        bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fis.close();
                        return bytes;

                    }
                    //解密成功则读取解密后的流显示在前台
                    FileInputStream fis = new FileInputStream(fileKey);
                    ByteArrayOutputStream bus = new ByteArrayOutputStream(bytes.length);
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bus.write(b, 0, n);
                    }
                    fis.close();
                    bus.close();
                    buffer = bus.toByteArray();
                    file.delete();
                    fileKey.delete();
                    logger.info("******************文件:" + name + "文件预览完成，******************");
                    return buffer;
                }

            }
        } else if ("2".equals(isThumbnails)) {
            //通过资源路径取出文件信息
            List<FsFile> list = fsFileMapper.getThumbByPath(filePath);
            if (!fastdfsUsingFlag) {
                input = new FileInputStream(filePath);
                bytes = new byte[input.available()];
                input.read(bytes);

            } else {
                bytes = fastdfsService.download(filePath);
            }
            if (list != null && list.size() > 0) {
                String name = list.get(0).getFileName();
                logger.info("******************文件:" + name + "进入文件下载，******************");
                if (list.get(0).getSourceKey() == null) {
                    return bytes;
                }
                //在本地生成随机文件
                String random = list.get(0).getMd5();
                File file = new File(ThumbnailsDir + "" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                if (!file.exists()) {
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                } else {
                    fileExist = true;
                }
                logger.info("******************文件:" + name +
                        "从fast下载完成，路径为" + file.getPath() + ",大小为" + file.length() + "******************");
                //文件解密
                boolean isDecrypt = false;
                if (!fileExist) {
                    isDecrypt = FileEncryptUtil.getInstance().decrypt(ThumbnailsDir + "" + random + suffix, list.get(0).getSourceKey());
                } else {
                    isDecrypt = true;
                }
                //判断是否解密成功
                if (isDecrypt) {
                    if (!fastdfsUsingFlag) {
                        FileInputStream fis = new FileInputStream(file);
                        bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fis.close();
                        return bytes;

                    }
                    //解密成功则读取解密后的流显示在前台
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bus = new ByteArrayOutputStream(bytes.length);
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bus.write(b, 0, n);
                    }
                    fis.close();
                    bus.close();
                    buffer = bus.toByteArray();
                    file.delete();
                    return buffer;
                }
                //删除临时文件

            }
        }else if ("3".equals(isThumbnails)) {
            //通过资源路径取出文件信息
            List<FsFile> list = fsFileMapper.getVideoByPath(filePath);
            if (!fastdfsUsingFlag) {
                input = new FileInputStream(filePath);
                bytes = new byte[input.available()];
                input.read(bytes);

            } else {
                bytes = fastdfsService.download(filePath);
            }
            if (list != null && list.size() > 0) {
                String name = list.get(0).getFileName();
                logger.info("******************文件:" + name + "进入文件下载，******************");
                if (list.get(0).getSourceKey() == null) {
                    return bytes;
                }
                //在本地生成随机文件
                String random = list.get(0).getMd5();
                File file = new File(videoDir + "" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                if (!file.exists()) {
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                } else {
                    fileExist = true;
                }
                logger.info("******************文件:" + name +
                        "从fast下载完成，路径为" + file.getPath() + ",大小为" + file.length() + "******************");
                //文件解密
                boolean isDecrypt = false;
                if (!fileExist) {
                    isDecrypt = FileEncryptUtil.getInstance().decrypt(videoDir + "" + random + suffix, list.get(0).getSourceKey());
                } else {
                    isDecrypt = true;
                }
                //判断是否解密成功
                if (isDecrypt) {
                    if (!fastdfsUsingFlag) {
                        FileInputStream fis = new FileInputStream(file);
                        bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fis.close();
                        return bytes;

                    }
                    //解密成功则读取解密后的流显示在前台
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bus = new ByteArrayOutputStream(bytes.length);
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bus.write(b, 0, n);
                    }
                    fis.close();
                    bus.close();
                    buffer = bus.toByteArray();
                    file.delete();
                    return buffer;
                }
                //删除临时文件

            }
        } else {
            //通过资源路径取出文件信息
            List<FsFile> list = fsFileMapper.getInfoByPath(filePath);
            if (!fastdfsUsingFlag) {
                input = new FileInputStream(filePath);
                bytes = new byte[input.available()];
                input.read(bytes);

            } else {
                bytes = fastdfsService.download(filePath);
            }
            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null) {
                    return bytes;
                }
                //在本地生成随机文件
                String random = list.get(0).getMd5();
                String name = list.get(0).getFileName();
                logger.info("******************文件:" + name + "进入文件下载，******************");
                File file = new File(downloadFileByKey + "" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                File fileKey = new File(downloadFile + "" +
                        random + suffix);
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
                logger.info("******************文件:" + name +
                        "从fast下载完成，路径为" + file.getPath() + ",大小为" + file.length() + "******************");
                //文件解密
                boolean isDecrypt = false;
                if (!fileExist) {
                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadFileByKey + "" + random + suffix, downloadFile + "" + random + suffix, list.get(0).getSourceKey());

                } else {
                    isDecrypt = true;
                }

                logger.info("******************文件:" + name +
                        "解密完成，路径为" + fileKey.getPath() + ",大小为" + fileKey.length() + "******************");
                //判断是否解密成功
                if (isDecrypt) {
                    if (!fastdfsUsingFlag) {
                        FileInputStream fis = new FileInputStream(fileKey);
                        bytes = new byte[fis.available()];
                        fis.read(bytes);
                        fis.close();
                        return bytes;

                    }
                    //解密成功则读取解密后的流显示在前台
                    FileInputStream fis = new FileInputStream(fileKey);
                    ByteArrayOutputStream bus = new ByteArrayOutputStream(bytes.length);
                    byte[] b = new byte[1024];
                    int n;
                    while ((n = fis.read(b)) != -1) {
                        bus.write(b, 0, n);
                    }
                    fis.close();
                    bus.close();
                    buffer = bus.toByteArray();
                    file.delete();
                    fileKey.delete();
                    return buffer;
                }
                //删除临时文件

            }

        }
        return bytes;
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @throws ServiceException
     */
    public File chuckPdf(String filePath) throws ServiceException {
        byte[] bytes = null;
        //首先取出服务器路径后缀进行判断
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        //是否启用fast服务器
        FileOutputStream fos = null;
        byte[] buffer = null;
        //通过PDF路径取出文件信息
        logger.info("*********开始取出list**********");
        List<FsFile> list = fsFileMapper.getInfoByPdfPath(filePath);
        logger.info("*********取出list完毕***********");
        if (!fastdfsUsingFlag) {
            FileInputStream input = null;
            try {
                input = new FileInputStream(filePath);
                bytes = new byte[input.available()];
                input.read(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int i = 0;
            while (i < 10 ) {
                long t1=new Date().getTime();
                logger.info("*********开始从fast下载文件***********");
                bytes = fastdfsService.download(filePath);
                long t2=new Date().getTime();
                logger.info("*********fast下载文件完毕***********时间为"+(t2-t1));
                if(bytes==null||bytes.length==0){
                    i++;;
                }else {
                    break;
                }
            }
        }

        if (list != null && list.size() > 0) {
            if (list.get(0).getPdfKey() == null && list.get(0).getSourceKey() == null) {
                String random = list.get(0).getMd5();
                File file = new File(downloadPdfFileByKey + "" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                try {
                    logger.info("*********开始读取文件***********");
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                    logger.info("*********读取文件完毕***********");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!fastdfsUsingFlag) {
                    InsertDeleteFile(file.getPath(), random);
                }
                return file;
            }
            //在本地生成随机文件
            //String random = UUID.randomUUID().toString().replaceAll("-", "");
            String random = list.get(0).getMd5();
            File file = new File(downloadPdfFileByKey + "" + random + suffix);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            boolean fileExist = false;
            if (!file.exists()) {
                logger.info("*********开始写入文件***********");
                try {
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                    logger.info("*********写入文件完毕***********");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                fileExist = true;
            }
            String key = list.get(0).getPdfKey();
            if (key == null) {
                key = list.get(0).getSourceKey();
            }
            logger.info("*********开始解密文件***********");
            //文件解密
            File fileNew = new File(downloadPdfFile + "" +
                    random + suffix);
            if (!fileNew.getParentFile().exists()) {
                // 路径不存在,创建
                fileNew.getParentFile().mkdirs();
            }
            boolean isDecrypt = false;
            if (fileExist == false) {
                isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "" +
                        random + suffix, fileNew.getPath(), key);
                if (file != null && file.exists()) {
                    file.delete();
                }
            } else {
                isDecrypt = true;
            }
            logger.info("*********解密文件完毕***********");
            //判断是否解密成功
            if (!fastdfsUsingFlag) {
                InsertDeleteFile(file.getPath(), random);
                InsertDeleteFile(fileNew.getPath(), random);
            }
/*            String   waterPath = fileNew.getPath().substring(0, fileNew.getPath().lastIndexOf(".")) + "_water.pdf";
            PdfWaterUtil.waterMark(fileNew.getPath(),waterPath, ShiroKit.getUser().getName(),waterPrint);*/
            return fileNew;
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @throws ServiceException
     */
    public File chuckAllFile(String filePath) throws ServiceException {
        byte[] bytes = new byte[1024 * 1024 * 5];
        //首先取出服务器路径后缀进行判断
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        //是否启用fast服务器
        FileOutputStream fos = null;
        InputStream fis = null;
        //通过PDF路径取出文件信息
        List<FsFile> list = fsFileMapper.getInfoByPath(filePath);
        if (!fastdfsUsingFlag) {
            try {
                fis = new FileInputStream(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            bytes = fastdfsService.download(filePath);
            fis = new ByteArrayInputStream(bytes);
        }

        if (list != null && list.size() > 0) {
            if (list.get(0).getPdfKey() == null && list.get(0).getSourceKey() == null) {
                String random = list.get(0).getMd5();
                File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                if (file.exists()) {
                    return file;
                }
                try {
                    int i =0;
                    fos = new FileOutputStream(file);
                    while ((i=fis.read(bytes))!=-1){
                        fos.write(bytes,0,i);
                    }
                    fos.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!fastdfsUsingFlag) {
                    InsertDeleteFile(file.getPath(), random);
                }
                return file;
            }
            //在本地生成随机文件
            String random = list.get(0).getMd5();
            File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                try {
                    fos = new FileOutputStream(file);
                    int len = 0;
                    while ((len = fis.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                    fos.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //return file;
            //文件解密
            File fileNew = new File(downloadPdfFile + "\\" +
                    random + suffix);
            if (!fileNew.getParentFile().exists()) {
                // 路径不存在,创建
                fileNew.getParentFile().mkdirs();
            }
            //判断是否解密成功
            if (!fastdfsUsingFlag) {
                InsertDeleteFile(file.getPath(), random);
                InsertDeleteFile(fileNew.getPath(), random);
            }
            FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "\\" +
                    random + suffix, fileNew.getPath(), list.get(0).getSourceKey());
            return fileNew;
        }
        return null;
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @throws IOException
     * @throws ServiceException
     */
    public double[] getFileData(String filePath, String isThumbnails) throws IOException, ServiceException {
        byte[] bytes = null;
        if ("".equals(filePath) || filePath == null || "undefined".equals(filePath) || "null".equals(filePath)) {
            return null;
        }
        //首先取出服务器路径后缀进行判断
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        //是否启用fast服务器

        FileOutputStream fos = null;
        byte[] buffer = null;

        //如果是pdf文件则走预览的方法
        if (".pdf".equals(suffix) || "0".equals(isThumbnails)) {
            //通过PDF路径取出文件信息
            List<FsFile> list = fsFileMapper.getInfoByPdfPath(filePath);

            bytes = fastdfsService.download(filePath);
            if (bytes == null) {
                return null;
            }
            if (list != null && list.size() > 0) {
                if (list.get(0).getPdfKey() == null && list.get(0).getSourceKey() == null) {
                    return null;
                }
                //在本地生成随机文件
                String random = list.get(0).getMd5();
                boolean fileExist = false;
                File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
                File fileKey = new File(downloadFile + "\\" +
                        random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
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
                String key = list.get(0).getPdfKey();
                if (key == null) {
                    key = list.get(0).getSourceKey();
                }
                boolean isDecrypt = false;
                //文件解密
                if (!fileExist) {

                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "\\" +
                            random + suffix, downloadPdfFile + "\\" +
                            random + suffix, key);
                    fileKey.delete();
                } else {
                    isDecrypt = true;
                }

                //判断是否解密成功
                if (isDecrypt) {
                    try {
                        double[] data = new double[2];
                        if (suffix.equals(".jpg") || suffix.equals(".png") || suffix.equals(".gif") || suffix.equals(".bmp")) {
                            Map<String, Double> map = ThumbnailsUtil.getHeightAndWidth(downloadPdfFile + "\\" + random + suffix);
                            data[0] = (double) map.get("width");

                            data[1] = (double) map.get("height");
                            file.delete();

                            return data;

                        } else {
                            return null;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

        }


        //删除临时文件
        return null;
    }


    /**
     * 文件下载
     *
     * @param filePath
     * @throws ServiceException
     */
    public File downLoadFile(String filePath) throws ServiceException {
        //拼接文件路径

        byte[] buffer = null;
        String realPath =   filePath;
        List<FsFile> list = fsFileMapper.getInfoByPdfPath(filePath);
        String random = list.get(0).getMd5();
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        boolean fileExist = false;
        File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
        File fileKey = new File(
                downloadPdfFile + "\\" +
                        random + suffix);
        if (!fileKey.getParentFile().exists()) {
            // 路径不存在,创建
            fileKey.getParentFile().mkdirs();
        }
        if (!file.getParentFile().exists()) {
            // 路径不存在,创建
            file.getParentFile().mkdirs();
        }
        if (!fastdfsUsingFlag) {
            realPath = filePath;

            file = new File(realPath);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null&&list.get(0).getPdfKey() ==null) {
                    return file;
                }
                Boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(file.getPath(), downloadPdfFile + "\\" + random + suffix, list.get(0).getPdfKey());

                //在本地生成随机文件
            }
        } else {
            if (!fileKey.exists()) {
                fastdfsService.download(filePath, file);
            } else {
                fileExist = true;
            }

            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null) {
                    return file;
                }
                boolean isDecrypt = false;
                if (!fileExist) {

                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "\\" + random + suffix, downloadPdfFile + "\\" + random + suffix, list.get(0).getPdfKey());
                    //在本地生成随机文件
                } else {
                    isDecrypt = true;
                }

            }
        }
        return fileKey;
    }

    /**
     * 文件下载
     *
     * @param filePath
     * @throws ServiceException
     */
    public File downLoadFileForMp3(String filePath) throws ServiceException {
        //拼接文件路径

        byte[] buffer = null;
        String realPath =   filePath;
        List<FsFile> list = fsFileMapper.getInfoByPdfPath(filePath);
        String random = list.get(0).getMd5();
        String suffix = filePath.substring(filePath.lastIndexOf("."));
        boolean fileExist = false;
        File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
        File fileKey = new File(
                downloadPdfFile + "\\" +
                        random + suffix);
        if (!fileKey.getParentFile().exists()) {
            // 路径不存在,创建
            fileKey.getParentFile().mkdirs();
        }
        if (!file.getParentFile().exists()) {
            // 路径不存在,创建
            file.getParentFile().mkdirs();
        }
        if (!fastdfsUsingFlag) {
            realPath = filePath;

            file = new File(realPath);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null&&list.get(0).getPdfKey() ==null) {
                    return file;
                }
                Boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(file.getPath(), downloadPdfFile + "\\" + random + suffix, list.get(0).getSourceKey());

                //在本地生成随机文件
            }
        } else {
            if (!fileKey.exists()) {
                fastdfsService.download(filePath, file);
            } else {
                fileExist = true;
            }

            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null) {
                    return file;
                }
                boolean isDecrypt = false;
                if (!fileExist) {

                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "\\" + random + suffix, downloadPdfFile + "\\" + random + suffix, list.get(0).getSourceKey());
                    //在本地生成随机文件
                } else {
                    isDecrypt = true;
                }

            }
        }
        return fileKey;
    }

    public File downLoadFileMobile(String filePdfPath) throws ServiceException {
        //拼接文件路径

        byte[] buffer = null;
        String realPath =   filePdfPath;
        List<FsFile> list = fsFileMapper.getInfoByPath(filePdfPath);
        String random = list.get(0).getMd5();
        String suffix = filePdfPath.substring(filePdfPath.lastIndexOf("."));
        boolean fileExist = false;
        File file = new File(downloadPdfFileByKey + "\\" + random + suffix);
        File fileKey = new File(
                downloadPdfFile + "\\" +
                        random + suffix);
        if (!fileKey.getParentFile().exists()) {
            // 路径不存在,创建
            fileKey.getParentFile().mkdirs();
        }
        if (!file.getParentFile().exists()) {
            // 路径不存在,创建
            file.getParentFile().mkdirs();
        }
        if (!fastdfsUsingFlag) {
            realPath = filePdfPath;

            file = new File(realPath);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null&&list.get(0).getPdfKey() ==null) {
                    return file;
                }
                Boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(file.getPath(), downloadPdfFile + "\\" + random + suffix, list.get(0).getPdfKey());

                //在本地生成随机文件
            }
        } else {
            if (!fileKey.exists()) {
                fastdfsService.download(filePdfPath, file);
            } else {
                fileExist = true;
            }

            if (list != null && list.size() > 0) {
                if (list.get(0).getSourceKey() == null) {
                    return file;
                }
                boolean isDecrypt = false;
                if (!fileExist) {

                    isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadPdfFileByKey + "\\" + random + suffix, downloadPdfFile + "\\" + random + suffix, list.get(0).getPdfKey());
                    //在本地生成随机文件
                } else {
                    isDecrypt = true;
                }

            }
        }
        return fileKey;
    }
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 克隆文件流
     *
     * @param input
     * @return
     */
    public static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean InsertDeleteFile(String path, String docId) {
        DocDelete docDelete = new DocDelete();
        docDelete.setFileId(docId);
        docDelete.setFilePath(path);
        String address = "";
        try {
            address = InetAddress.getLocalHost().toString().replace(".", "");
            docDelete.setServerAddress(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("file_path", path);
        wrapper.eq("server_address", address);
        List<DocDelete> list = docDeleteService.list(wrapper);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            DocDelete docDeleteNew = docDeleteService.getById(docId);
            if (docDeleteNew != null) {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docDeleteNew.setCreateTime(ts);
                return docDeleteService.updateById(docDeleteNew);
            } else {
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docDelete.setCreateTime(ts);
                return docDeleteService.save(docDelete);
            }
        }

    }
}
