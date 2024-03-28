package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.docutil.service.impl.PdfServiceImpl;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateThumbnails implements BaseJob {

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    private FastDFSProperties fastDFSProperties = appCtx.getBean(FastDFSProperties.class);

    private DocbaseProperties docbaseProperties = appCtx.getBean(DocbaseProperties.class);

    private FrontDocInfoMapper frontDocInfoMapper = appCtx.getBean(FrontDocInfoMapper.class);

    private FastdfsService fastdfsService =  appCtx.getBean(FastdfsService.class);

    static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);

    private FileTool fileTool =  appCtx.getBean(FileTool.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        createThumbnails();
    }

    private void createThumbnails(){
        String tempdir = docbaseProperties.getTempdir();
        List<Map> list = frontDocInfoMapper.createThumbList();
        for (int i = 0; i < list.size(); i++){
            Map docInfo = list.get(i);
            if (null != docInfo){
                String fileId = "";
                if (null != docInfo.get("fileId")) fileId = docInfo.get("fileId").toString();
                String filePath = "";
                if (null != docInfo.get("filePath")) filePath = docInfo.get("filePath").toString();
                String sourceKey = "";
                if (null != docInfo.get("sourceKey")) sourceKey = docInfo.get("sourceKey").toString();
                String suffix = "";
                if (null != docInfo.get("suffix")) suffix = docInfo.get("suffix").toString();
                FileOutputStream fos = null;
                byte[] bytes = null;
                FileInputStream input = null;
                try {
                    if (!fastDFSProperties.isUsing()) {
                        input = new FileInputStream(filePath);
                        bytes = new byte[input.available()];
                        input.read(bytes);

                    } else {
                        bytes = fastdfsService.download(filePath);
                    }
                    if (list != null && list.size() > 0) {
                        if (sourceKey == null) {
                        }
                        //在本地生成随机文件
                        String random = UUID.randomUUID().toString().replaceAll("-", "");
                        String localPath = tempdir + "\\" + random + suffix;
                        String newThumbPath = tempdir + "\\" + random + "_thumb" +suffix;
                        File file = new File(localPath);
                        File newSource = new File(newThumbPath);
                        fos = new FileOutputStream(file);
                        fos.write(bytes, 0, bytes.length);
                        fos.close();
                        //文件解密
                        boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(localPath, sourceKey);
                        //判断是否解密成功
                        if (isDecrypt) {
                            Map fsFile  = new HashMap();
                            //生成缩略图
                            boolean isCreateThumb = ThumbnailsUtil.createThumbnails(localPath,newThumbPath,1080,857);
                            String sourcePath = null;
                            if (isCreateThumb){
                                Map newThumbInfo = ThumbnailsUtil.getHeightAndWidth(newThumbPath);
                                fsFile.putAll(newThumbInfo);
                                String size = ThumbnailsUtil.pathSize(newThumbPath);
                                fsFile.put("sourceSize",size);
                                if (fastDFSProperties.isUsing()) {
                                    //文件加密并取出加密密码存到数据库
                                    String newSourceKey = FileEncryptUtil.getInstance().encrypt(newThumbPath);
                                    fsFile.put("sourceKey",newSourceKey);
                                    sourcePath = fastdfsService.uploadFile(newSource);
                                    fsFile.put("sourcePath", sourcePath);
                                } else {
                                    String newSourceKey = FileEncryptUtil.getInstance().encrypt(newThumbPath);
                                    fsFile.put("sourceKey",newSourceKey);
                                    fsFile.put("sourcePath", newThumbPath);
                                }
                            } else {
                                Map newThumbInfo = ThumbnailsUtil.getHeightAndWidth(localPath);
                                fsFile.putAll(newThumbInfo);
                                String size = ThumbnailsUtil.pathSize(localPath);
                                fsFile.put("sourceSize",size);
                                fsFile.put("sourceKey",sourceKey);
                                fsFile.put("sourcePath", filePath);

                            }

                            fsFile.put("sourceLevel",2);
                            fsFile.put("sourceId", StringUtil.getUUID());
                            fsFile.put("fileId",fileId);
                            frontDocInfoMapper.setNewThumbInfo(fsFile);
                            if (null !=  input){
                                input.close();
                            }

                            file.delete();
                            newSource.delete();
                        }
                    }
                    //删除临时文件
                } catch (Exception e){
                    e.printStackTrace();
                    LOGGER.error("生成缩略图异常：" + ExceptionUtils.getErrorInfo(e));
                }

            }
        }

        }

    }

