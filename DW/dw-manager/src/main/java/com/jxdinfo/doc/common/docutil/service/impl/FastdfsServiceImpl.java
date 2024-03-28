package com.jxdinfo.doc.common.docutil.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.docutil.service.FileValidateService;
import com.jxdinfo.doc.common.model.MasterAndSlave;
import com.jxdinfo.doc.common.util.CodeUtil;
import com.jxdinfo.doc.common.util.FdfsFileUtil;
import com.jxdinfo.doc.common.util.FilenameUtils;
import com.jxdinfo.doc.manager.docmanager.dto.FileInfo;
import com.jxdinfo.doc.manager.docmanager.dto.ImageWH;
import com.jxdinfo.doc.manager.docmanager.ex.ParamException;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 类的用途：FastdfsService实现类
 * 创建日期：2018年7月7日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月7日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 *
 * @author XuXinYing
 * @version 1.0
 */
@Service
public class FastdfsServiceImpl implements FastdfsService {

    static final public Logger logger = LogManager.getLogger(FastdfsServiceImpl.class);
    /**
     * fileValidateService
     */
    @Autowired
    private FileValidateService fileValidateService;

    /**
     * fdfsFileUtil
     */
    @Autowired
    FdfsFileUtil fdfsFileUtil;

    /**
     * 上传文件
     *
     * @param groupName
     * @param file
     * @return
     * @throws ServiceException
     * @Title: uploadFile
     */
    @Override
    public String uploadFile(String groupName, File file) throws ServiceException {
        String path = "";
        FileInputStream fileIO = null;
        		
        try {
            // fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            fileIO = new FileInputStream(file);
            path = fdfsFileUtil.upload(groupName, fileIO, file.getName());
            if (path == null) {
                throw new ServiceException("upload error.");
            }
        } catch (final FileNotFoundException e) {
            throw new ServiceException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
        	if (fileIO != null){
	        	try{
	        		fileIO.close();
	        	} catch (IOException e){
	        		e.printStackTrace();
	        	}
        	}
        }
        return path;
    }


    @Override
    public String uploadFile(File file) throws ServiceException {
        String path = "";
        FileInputStream fis = null;
        try {
            fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            fis = new FileInputStream(file);
            path = fdfsFileUtil.upload(file.getPath(), file.getName());
            if (path == null) {
                throw new ServiceException("upload error.");
            }
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ServiceException("upload error.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("upload error.");
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String uploadSlaveFile(String masterFileId, File file) throws ServiceException {
    	FileInputStream fis = null;
        try {
            fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            final String fileName = file.getName();
            // 随机生成从文件前缀,防止重名异常
            final String filePrefixName = FilenameUtils.getPrefixRandom(fileName);
            fis = new FileInputStream(file);
            final String path = fdfsFileUtil.uploadSlave(masterFileId, fis, filePrefixName,
                    fileName);
            if (path == null) {
                throw new ServiceException("slave upload error.");
            }
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
        	if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    @Override
    public String uploadSlaveFile(String masterFilename, String prefixName, File file) throws ServiceException {
    	FileInputStream fis = null;
        try {
            fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            prefixName = prefixName + "_" + FilenameUtils.generateShortUuid();
            fis = new FileInputStream(file);
            final String path = fdfsFileUtil.uploadSlave(masterFilename, fis, prefixName,
                    file.getName());
            if (path == null) {
                throw new ServiceException("slave upload error.");
            }
            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } finally {
        	if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return "";
    }

    @Override
    public MasterAndSlave uploadImageAndThumb(String groupName, String cutSize, File file) throws ServiceException {
        FileInputStream fis = null;
        try {
            fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            if (StringUtils.isEmpty(cutSize)) {
                throw new ParamException("cutSize is null.");
            }
            if (StringUtils.isEmpty(groupName)) {
                throw new ParamException("groupName is null.");
            }
            final String ext = FilenameUtils.getExtension(file.getName());
            // 复制一份主图
            final File sourceFile = new File(CodeUtil.getUUID());
            fis = new FileInputStream(file);
            FileUtils.copyInputStreamToFile(fis, sourceFile);
            // 生成缩略图
            final List<ImageWH> whs = loadCutSize(cutSize);
            if (whs != null) {
                final int len = whs.size();
                final InputStream[] slaveInputs = new InputStream[len];
                final List<String> slaveNames = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final ImageWH wh = whs.get(i);
                    Thumbnails.of(sourceFile).size(wh.getW(), wh.getH()).toOutputStream(out);
                    slaveInputs[i] = new ByteArrayInputStream(out.toByteArray());
                    slaveNames.add(FilenameUtils.merge(String.format("%dx%d", wh.getW(), wh.getH()), ext));
                }
                fis = new FileInputStream(file);
                final MasterAndSlave uploadMasterAndSlave = fdfsFileUtil.uploadMasterAndSlave(groupName,
                        fis, file.getName(), slaveNames, slaveInputs);
                return uploadMasterAndSlave;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new MasterAndSlave();
    }

    @Override
    public MasterAndSlave uploadImageAndThumb(String cutSize, File file) throws ServiceException {
        FileInputStream fis = null;
        try {
            fileValidateService.validateFile(file);
            final long fileSize = file.length();
            if (fileSize <= 0) {
                throw new ParamException("file is null.");
            }
            if (StringUtils.isEmpty(cutSize)) {
                throw new ParamException("cutSize is null.");
            }
            final String ext = FilenameUtils.getExtension(file.getName());
            // 复制一份主图
            final File sourceFile = new File(CodeUtil.getUUID());
            FileUtils.copyInputStreamToFile(new FileInputStream(file), sourceFile);

            // 生成缩略图
            final List<ImageWH> whs = loadCutSize(cutSize);
            if (whs != null) {
                final int len = whs.size();
                final InputStream[] slaveInputs = new InputStream[len];
                final List<String> slaveNames = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    final ImageWH wh = whs.get(i);
                    Thumbnails.of(sourceFile).size(wh.getW(), wh.getH()).toOutputStream(out);
                    slaveInputs[i] = new ByteArrayInputStream(out.toByteArray());
                    slaveNames.add(String.format("%dx%d", wh.getW(), wh.getH()) + "." + ext);
                }
                fis = new FileInputStream(file);
                final MasterAndSlave uploadMasterAndSlave = fdfsFileUtil.uploadMasterAndSlave(null,
                        fis, file.getName(), slaveNames, slaveInputs);
                // 删除临时
                FileUtils.deleteQuietly(sourceFile);
                return uploadMasterAndSlave;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new MasterAndSlave();
    }

    @Override
    public boolean cutImage(String fileName, String cutSize) throws ServiceException {
        if (StringUtils.isEmpty(fileName)) {
            throw new ParamException("fileName is null");
        }
        if (StringUtils.isEmpty(cutSize)) {
            throw new ParamException("cutSize is null");
        }
        final File tmpFile = new File(CodeUtil.getUUID());
        try {
            final List<ImageWH> whs = loadCutSize(cutSize);
            final String ext = FilenameUtils.getExtension(fileName);
        } catch (final NullPointerException e) {
            throw new ServiceException(e);
        } finally {
            tmpFile.delete();
        }
        return true;
    }

    @Override
    public boolean removeFile(String fileName) throws ServiceException {
        if (StringUtils.isEmpty(fileName)) {
            throw new ParamException("fileName is null");
        }
        try {
            fdfsFileUtil.delete(fileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void download(String fileId, String destFile) throws ServiceException {
        if (StringUtils.isEmpty(fileId)) {
            throw new ParamException("fileId is null");
        }
        try {
            fdfsFileUtil.download(fileId, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void download(String fileId, File destFile) throws ServiceException {
        if (StringUtils.isEmpty(fileId)) {
            throw new ParamException("fileId is null");
        }
        try {
            fdfsFileUtil.download(fileId, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 文件下载
     *
     * @param fileId
     * @throws ServiceException
     */
    @Override
    public byte[] download(String fileId) throws ServiceException {
        if (StringUtils.isEmpty(fileId)) {
            throw new ParamException("fileId is null");
        }
        try {
            return  fdfsFileUtil.downloadFile(fileId);
        } catch (Exception e) {
            logger.error("********************"+fileId+"下载文件从FastDFS异常********************");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * loadCutSize
     *
     * @param cutSize 大小
     * @return 图片
     * @throws ParamException 异常
     * @Title: loadCutSize
     * @author: XuXinYing
     */
    private List<ImageWH> loadCutSize(String cutSize) throws ParamException {
        List<ImageWH> whs = null;
        if (!StringUtils.isEmpty(cutSize)) {
            final List<String> sizes = Arrays.asList(cutSize.split(","));
            whs = new ArrayList<>();
            for (final String size : sizes) {
                final String[] vals = size.split("x");
                final int w = Integer.parseInt(vals[0]);
                final int h = Integer.parseInt(vals[1]);
                whs.add(new ImageWH(w, h));
            }
        }
        return whs;
    }

    @Override
    public FileInfo getFileInfo(String fileId) throws ServiceException {
        if (StringUtils.isEmpty(fileId)) {
            throw new ParamException("fileId is null");
        }
        try {
            final org.csource.fastdfs.FileInfo fileInfo = fdfsFileUtil.getFileInfo(fileId);
            if (null == fileInfo) {
                throw new ServiceException("文件不存在");
            }
            final FileInfo info = new FileInfo();
            info.setCrc32(fileInfo.getCrc32());
            info.setCreate_timestamp(fileInfo.getCreateTimestamp().getTime());
            info.setFile_size(fileInfo.getFileSize());
            return info;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
        return new FileInfo();
    }

}
