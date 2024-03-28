package com.jxdinfo.doc.newupload.thread;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.CadUtil;
import com.jxdinfo.doc.common.util.CebUtil;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.LibreOfficePDFConvert;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.TikaUtil;
import com.jxdinfo.doc.common.util.ffmpegUtil;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.videomanager.model.DocVideoThumb;
import com.jxdinfo.doc.manager.videomanager.service.DocVideoThumbService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2019-1-9
 * @description 将文档转换成pdf的线程
 */
public class ChangeToPdfThread extends Thread {
    private String docId = "";
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
    private static ThreadPoolExecutor threadPoolExecutor = ToUpload.getEsThreadPoolExecutor();
    static final public Logger LOGGER = LogManager.getLogger(ChangeToPdfThread.class);

    private Environment environment = appCtx.getBean(Environment.class);

    /**
     * FAST操作接口
     */
    private FastdfsService fastdfsService = appCtx.getBean(FastdfsService.class);
    /**
     * FAST操作接口
     */
    private FsFileService fsFileService = appCtx.getBean(FsFileService.class);
    /**
     * fs_file Mapper 接口
     */
    private FilesMapper filesMapper = appCtx.getBean(FilesMapper.class);

    private CacheToolService cacheToolService = appCtx.getBean(CacheToolService.class);

    private UploadService uploadService = appCtx.getBean(UploadService.class);

    private DocVideoThumbService docVideoThumbService = appCtx.getBean(DocVideoThumbService.class);
    private FileTool fileTool = appCtx.getBean(FileTool.class);

    public ChangeToPdfThread(String id) {
        docId = id;
    }

    //加密后pdf
    File pdfKeyDir = null;

    //生成的pdf文件
    File pdfFile = null;

    //源文件
    File sourceFile = null;

    @Override
    public void run() {
        try {
            boolean theadFlag = true;
            Map<String, String> ready = new HashMap<>();
            FsFile fsFileTemp = filesMapper.selectById(docId);

            if(fsFileTemp == null){
                LOGGER.info("============从数据库获取文件信息为空: "+docId);
                Thread.sleep(2000);
                fsFileTemp = filesMapper.selectById(docId);
            }

            List<FsFile> list = fsFileService.getInfoByMd5(fsFileTemp.getMd5());
            LOGGER.info("******************文件:" + fsFileTemp.getFileName() + "进入ChangeToPdfThread线程，开始转化PDF******************");
            String pdfPathDir = environment.getProperty("docbase.pdfFile");
            ;
            String pdfKeyPath = environment.getProperty("docbase.pdfFileByKey");
            ;
            List<Map<String, String>> uploadList = uploadService.selectUpload(docId);
            String sourcePath = uploadList.get(0).get("sourcePath");

            // 转换次数加一
            String times = uploadList.get(0).get("times");
            int timesInt = null == times || "".equals(times) ? 1 : Integer.parseInt(times);
            String newTimes = Integer.toString(timesInt + 1);

            ready.put("docId", docId);
            sourceFile = new File(sourcePath);
            if (!sourceFile.getParentFile().exists()) {
                // 路径不存在,创建
                sourceFile.getParentFile().mkdirs();
            }
            if (sourceFile.exists()) {

                // 更新转换次数
                ready.put("times", newTimes);
                uploadService.updateUploadState(ready);

                //转换成的pdf路径
                String pdfFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
                pdfFilePath = pdfPathDir + pdfFilePath.substring(pdfFilePath.lastIndexOf("/") + 1, pdfFilePath.length());
                String pdfKeyFilePath = pdfKeyPath + pdfFilePath.substring(pdfFilePath.lastIndexOf("/") + 1, pdfFilePath.length());
                //PDF文件1
                pdfFile = new File(pdfFilePath);

                // 文档内容
                String content = null;
                //创建文件对象
                FsFile fsFile = new FsFile();
                //文件类型
                String contentType = uploadList.get(0).get("contentType");
                // 转换标志 （false:未成功转换，true:成功转换ao）
                boolean flag = false;
                Map<String, Object> pdfInfo = new HashMap<String, Object>();
                // 根据文档类型转换格式

                if (sourcePath.endsWith(".ceb")) {
                    /*pdfFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".pdf";
                    ApiClient client = new ApiClient();
                    JSONObject cebName = new JSONObject();
                    cebName.put("cebName", sourcePath);
                    //TODO ceb文件未加水印
                    String ceb = client.cebToPdf(cebName);
                    if (ceb != null && ceb.contains("true")) {
                        Map<String, Object> metadata = TikaUtil.autoParse(pdfFilePath);
                        content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                        ready.put("content", content);
                    }*/
                    // 连接socket转化pdf
                    CebUtil.ceb2Pdf(sourceFile, pdfFile);
                    if (pdfFile.exists()) {
                        Map<String, Object> metadata = TikaUtil.autoParse(pdfFilePath);
                        content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                        ready.put("content", content);
                    }
                } else if (contentType != null && contentType.contains("cad")) {
                    CadUtil.cad2Pdf(sourceFile, pdfFile);
                } else if (contentType != null && (contentType.contains("text/html") || contentType.contains("text/x-log") || contentType.contains("text/xml"))) {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } else if (contentType != null && (contentType.contains("word") || contentType.contains("rtf") || contentType.contains("works") )) {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } else if (contentType != null && (contentType.contains("text/html") && (sourcePath.endsWith(".doc") || sourcePath.endsWith(".docx")))) {
                    //网络导出的doc格式文件
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } else if (contentType != null && (contentType.contains("excel") || contentType.contains("spreadsheetml"))) {
                    // 获取文件后缀
                    String fileType = fsFileTemp.getFileType();
                    //转换成的pdf路径
                    String xlsxFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + ".xlsx";
                    // knowledge/pdfFile/fileName.xlsx
                    pdfFilePath = pdfPathDir + xlsxFilePath.substring(xlsxFilePath.lastIndexOf("/") + 1, xlsxFilePath.length());
                    pdfFile = new File(pdfFilePath);
                    pdfKeyFilePath = pdfKeyPath + xlsxFilePath.substring(xlsxFilePath.lastIndexOf("/") + 1, xlsxFilePath.length());

                    // 如果是xls文件  转换成xlsx文件 (因为luckysheet插件只支持 xlsx文件预览)
                    if (StringUtils.equals(fileType, ".xls") || StringUtils.equals(fileType, ".csv") || StringUtils.equals(fileType, ".et")) {
                        LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                    } else if (StringUtils.equals(fileType, ".xlsx")) {
                        FileUtils.copyFile(sourceFile, pdfFile);
                    }
                    /*ExcelUtil.changeExcel(sourcePath);
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);*/
                } else if (contentType != null && (contentType.contains("powerpoint") || contentType.contains("presentationml"))) {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } else if (contentType != null && ((contentType.contains("octet-stream") || contentType.contains("text/plain")))
                        && sourcePath.endsWith(".txt")) {
                    String type = getFilecharset(sourceFile);
                    if (type.equals("GBK") || type.equals("ANSI")) {
                        String str = FileUtils.readFileToString(sourceFile, "GBK");
                        FileUtils.writeStringToFile(sourceFile, str, "UTF-8");
                        LOGGER.info("*************转码结束***************");
                    }
                    // 只处理txt文件，防止其他文件转换异常
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                    Map<String, Object> metadata = TikaUtil.autoParse(pdfFilePath);
                    content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                    ready.put("content", content);
                } else if (contentType != null && (contentType.contains("octet-stream") && ( sourcePath.endsWith(".tif") || sourcePath.endsWith(".tiff")))) {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } else {
                    if (contentType != null && (contentType.contains("mp4") || contentType.contains("avi") || contentType.contains("wmv")
                            || contentType.contains("mpg") || contentType.contains("mpeg") || contentType.contains("mov")
                            || contentType.contains("swf") || contentType.contains("flv") || contentType.contains("ram")
                            || contentType.contains("rm")) || contentType.contains("x-matroska")
                            || contentType.contains("x-shockwave-flash")) {
                        String videoPath = environment.getProperty("docbase.videoDir");
                        ffmpegUtil ffmpeg = new ffmpegUtil(environment.getProperty("docbase.ffmpegDir"));
                        String videoImgPath = videoPath + pdfFilePath.substring(pdfFilePath.lastIndexOf("/") + 1, pdfFilePath.length());
                        videoImgPath = videoImgPath.substring(0, sourcePath.lastIndexOf(".")) + ".png";
                        File file = new File(videoImgPath);
                        if (!file.getParentFile().exists()) {
                            // 路径不存在,创建
                            file.getParentFile().mkdirs();
                        }
                        String videoKeyPath = null;
                        String pathKey = null;
                        ffmpegUtil.getThumb(sourcePath, videoImgPath, 800, 800, 0, 0, 1);
                        if (cacheToolService.getFastDFSUsingFlag()) {
                            //文件加密并取出加密密码存到数据库
                            pathKey = FileEncryptUtil.getInstance().encrypt(videoImgPath);
                            videoKeyPath = fastdfsService.uploadFile(file);
                        } else {
                            pathKey = FileEncryptUtil.getInstance().encrypt(videoImgPath);
                            videoKeyPath = videoImgPath;
                        }
                        DocVideoThumb docVideoThumb = new DocVideoThumb();
                        docVideoThumb.setDocId(docId);
                        docVideoThumb.setPath(videoKeyPath);
                        docVideoThumb.setPathKey(pathKey);
                        docVideoThumbService.saveOrUpdate(docVideoThumb);
                        if (cacheToolService.getFastDFSUsingFlag()) {
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                        Encoder encoder = new Encoder();
                        String decoder = "";
                        // 对swf格式略过操作 因为swf格式会报错
                        if(contentType!=null && !contentType.contains("x-shockwave-flash")){
                            MultimediaInfo info = encoder.getInfo(sourceFile);
                            VideoInfo video = info.getVideo();
                            decoder = video.getDecoder();
                        }
                        String pdfPath = null;

                        if (!decoder.equals("h264") || contentType.contains("mov") || contentType.contains("avi")
                                || contentType.contains("flv")  || contentType.contains("wmv")  || contentType.contains("x-matroska")
                                || contentType.contains("x-shockwave-flash")|| contentType.contains("mpeg")) {
                            pdfFilePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_new.mp4";
                            pdfKeyFilePath = pdfKeyPath + pdfFilePath.substring(pdfFilePath.lastIndexOf("/") + 1, pdfFilePath.length());
                            ffmpegUtil.convetor(sourcePath, pdfFilePath);
                            if (!StringUtil.checkIsEmpty(pdfFilePath)) {
                                pdfFile = new File(pdfFilePath);
                                if (!pdfFile.getParentFile().exists()) {
                                    // 路径不存在,创建
                                    pdfFile.getParentFile().mkdirs();
                                }
                                LOGGER.info("******************MP4文件:" + fsFileTemp.getFileName() + "创建成功，路径为"
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
                                    if (pdfKeyDir != null && pdfKeyDir.exists()) {
                                        pdfKeyDir.delete();
                                    }
                                    if (pdfFile != null && pdfFile.exists()) {
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
                                    String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile, pdfKeyDir);
                                    fsFile.setPdfKey(pdfKey);
                                    pdfPath = pdfKeyDir.getPath();
                                }
                            }
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    fsFile.setFileId(list.get(i).getFileId());
                                    fsFile.setFilePdfPath(pdfPath);
                                    fsFile.setSize(fsFileTemp.getSize());
                                    filesMapper.updateById(fsFile);
                                }
                            }
                            //更新缓存中的文件上传状态
                            //更新数据库中的文件上传状态
                            LOGGER.info("******************文件:" + fsFileTemp.getFileName() + "此线程结束，进行下一流程******************");
                            ready.put("state", "2");
                            ready.put("times", "0");
                            uploadService.updateUploadState(ready);
                        } else {
                            if (list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    fsFile.setFilePdfPath(fsFileTemp.getFilePath());
                                    fsFile.setPdfKey(fsFileTemp.getSourceKey());
                                    fsFile.setFileId(list.get(i).getFileId());
                                    filesMapper.updateById(fsFile);
                                }
                            }
                        }
                        // 文件信息插入数据库
                    }
                    LOGGER.info("******************文件:" + fsFileTemp.getFileName() + "不需要转化PDF，进行下一步******************");
                    ready.put("state", "2");
                    ready.put("times", "0");
                    uploadService.updateUploadState(ready);
                    theadFlag = false;
                }
                if (theadFlag) {
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
                                if (pdfKeyDir != null && pdfKeyDir.exists()) {
                                    pdfKeyDir.delete();
                                }
                                if (pdfFile != null && pdfFile.exists()) {
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
                                String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile, pdfKeyDir);
                                fsFile.setPdfKey(pdfKey);
                                pdfPath = pdfKeyDir.getPath();
                            }
                        }
                    }
                    // 文件信息插入数据库
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            fsFile.setFileId(list.get(i).getFileId());
                            fsFile.setFilePdfPath(pdfPath);
                            fsFile.setSize(fsFileTemp.getSize());
                            filesMapper.updateById(fsFile);
                        }
                    }
                    //更新缓存中的文件上传状态
                    //更新数据库中的文件上传状态
                    LOGGER.info("******************文件:" + fsFileTemp.getFileName() + "此线程结束，进行下一流程******************");
                    ready.put("state", "2");
                    ready.put("times", "0");
                    uploadService.updateUploadState(ready);
                }
                threadPoolExecutor.execute(new CreateEsThread(docId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("文件【" + docId + "】转化PDF失败: " + ExceptionUtils.getErrorInfo(e));
        } finally {
            if (pdfKeyDir != null && pdfKeyDir.exists()) {
                pdfKeyDir.delete();
            }
            if (pdfFile != null && pdfFile.exists()) {
                pdfFile.delete();
            }
        }

    }

    private static String getFilecharset(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset; //文件编码为 ANSI
            } else if (first3Bytes[0] == (byte) 0xFF
                    && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE"; //文件编码为 Unicode
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE"; //文件编码为 Unicode big endian
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8"; //文件编码为 UTF-8
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
}
