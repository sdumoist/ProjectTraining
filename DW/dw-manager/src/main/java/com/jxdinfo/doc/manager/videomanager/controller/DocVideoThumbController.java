package com.jxdinfo.doc.manager.videomanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.ffmpegUtil;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.videomanager.model.DocVideoThumb;
import com.jxdinfo.doc.manager.videomanager.service.DocVideoThumbService;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/dovVideo")
public class DocVideoThumbController {
    @Resource
    private FsFileService fsFileService;
    @Resource
    private DocVideoThumbService docVideoThumbService;
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;
    @Value("${docbase.downloadFile}")
    private String downloadFile;
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;
    @Value("${docbase.videoDir}")
    private String videoPath;
    @Value("${docbase.ffmpegDir}")
    private String ffmpegDir;
    /**
     * fast服务器服务类
     */
    @Autowired
    private FastdfsService fastdfsService;
    @Resource
    private FsFileMapper fsFileMapper;

    @GetMapping("/addPath")
    public void addPath() {
        List<FsFile> fileList = this.fsFileService.list((new QueryWrapper<FsFile>()).eq("file_type", ".mp4"));
        for (int i = 0; i < fileList.size(); i++) {
            FsFile fsFile = fileList.get(i);
            String docId = fsFile.getFileId();
            byte[] bytes = null;
            FileInputStream input = null;
            byte[] buffer = null;
            FileOutputStream fos = null;
            DocVideoThumb docVideoThumb = docVideoThumbService.getById(docId);
            if (docVideoThumb == null) {
                List<FsFile> list = fsFileMapper.getInfoByPath(fsFile.getFilePath());
                if (!fastdfsUsingFlag) {
                    try {
                        input = new FileInputStream(fsFile.getFilePath());
                        bytes = new byte[input.available()];
                        input.read(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        bytes = fastdfsService.download(fsFile.getFilePath());
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                }
                if (list != null && list.size() > 0) {
//                    if (list.get(0).getSourceKey() == null) {
//                        return bytes;
//                    }
                    //在本地生成随机文件
                    String random = list.get(0).getMd5();
                    if (random == null || random.equals("")) {
                        random = UUID.randomUUID().toString().replaceAll("-", "");
                    }
                    String name = list.get(0).getFileName();
                    File file = new File(downloadFileByKey + "" + random + fsFile.getFileType());
                    if (!file.getParentFile().exists()) {
                        // 路径不存在,创建
                        file.getParentFile().mkdirs();
                    }
                    boolean fileExist = false;
                    File fileKey = new File(downloadFile + "" +
                            random + fsFile.getFileType());
                    if (!fileKey.getParentFile().exists()) {
                        // 路径不存在,创建
                        fileKey.getParentFile().mkdirs();
                    }
                    if (!fileKey.exists()) {
                        if (bytes == null) {
                            continue;
                        } else {
                            try {
                                fos = new FileOutputStream(file);
                                fos.write(bytes, 0, bytes.length);
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        fileExist = true;
                    }
                    if (bytes != null) {
                        //文件解密
                        boolean isDecrypt = false;
                        if (!fileExist) {
                            isDecrypt = FileEncryptUtil.getInstance().decrypt(downloadFileByKey + "" + random + fsFile.getFileType(), downloadFile + "" + random + fsFile.getFileType(), list.get(0).getSourceKey());

                        } else {
                            isDecrypt = true;
                        }
                        ffmpegUtil ffmpeg = new ffmpegUtil(ffmpegDir);
                        String videoImgPath = videoPath + (downloadFile + "" + random + fsFile.getFileType()).substring((downloadFile + "" + random + fsFile.getFileType()).lastIndexOf("/") + 1, (downloadFile + "" + random + fsFile.getFileType()).length());
                        videoImgPath = videoImgPath.substring(0, videoImgPath.lastIndexOf(".")) + ".png";
                        File imgFile = new File(videoImgPath);
                        if (!imgFile.getParentFile().exists()) {
                            // 路径不存在,创建
                            imgFile.getParentFile().mkdirs();
                        }
                        String videoKeyPath = null;
                        String pathKey = null;
                        try {
                            ffmpegUtil.getThumb((downloadFile + "" + random + fsFile.getFileType()), videoImgPath, 800, 800, 0, 0, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (imgFile.length() != 0) {
                            if (fastdfsUsingFlag) {
                                //文件加密并取出加密密码存到数据库
                                pathKey = FileEncryptUtil.getInstance().encrypt(videoImgPath);
                                try {
                                    videoKeyPath = fastdfsService.uploadFile(imgFile);
                                } catch (ServiceException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                pathKey = FileEncryptUtil.getInstance().encrypt(videoImgPath);
                                videoKeyPath = videoImgPath;
                            }
                            DocVideoThumb docVideoThumb2 = new DocVideoThumb();
                            docVideoThumb2.setDocId(docId);
                            docVideoThumb2.setPath(videoKeyPath);
                            docVideoThumb2.setPathKey(pathKey);
                            docVideoThumbService.saveOrUpdate(docVideoThumb2);
                        }
                    }
                }

            }


        }
    }
}
