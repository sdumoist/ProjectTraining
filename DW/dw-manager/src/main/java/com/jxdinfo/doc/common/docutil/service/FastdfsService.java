package com.jxdinfo.doc.common.docutil.service;

import java.io.File;

import com.jxdinfo.doc.common.model.MasterAndSlave;
import com.jxdinfo.doc.manager.docmanager.dto.FileInfo;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;

public interface FastdfsService {

    /**
     * @param groupName 存储组名称
     * @param file      文件
     * @return
     * @throws ServiceException
     */
    String uploadFile(String groupName, File file) throws ServiceException;

    /**
     * @param file 文件
     * @return
     * @throws ServiceException
     */
    String uploadFile(File file) throws ServiceException;

    /**
     * @param masterFileId 主文件地址
     * @param file         文件
     * @return
     * @throws ServiceException
     */
    String uploadSlaveFile(String masterFileId, File file) throws ServiceException;

    /**
     * @param masterFileId
     * @param prefixName   指定文件名称
     * @param file
     * @return
     * @throws ServiceException
     */
    String uploadSlaveFile(String masterFileId, String prefixName, File file) throws ServiceException;

    /**
     * @param groupName 存储组名称
     * @param cutSize   生成缩略图尺寸
     * @param file      文件
     * @return
     * @throws ServiceException
     */
    com.jxdinfo.doc.common.model.MasterAndSlave uploadImageAndThumb(String groupName, String cutSize, File file) throws ServiceException;

    /**
     * @param cutSize 生成缩略图尺寸
     * @param file    文件
     * @return
     * @throws ServiceException
     */
    MasterAndSlave uploadImageAndThumb(String cutSize, File file) throws ServiceException;

    /**
     * 裁剪图片
     *
     * @param fileId  已存在文件服务中,不删除源文件
     * @param cutSize
     * @return
     * @throws ServiceException
     */
    boolean cutImage(String fileId, String cutSize) throws ServiceException;

    /**
     * @param fileId 删除文件 ,如果存在从文件,从文件也删除
     * @return
     * @throws ServiceException
     */
    boolean removeFile(String fileId) throws ServiceException;

    /**
     * 文件下载
     *
     * @param fileId
     * @param destFile
     * @throws ServiceException
     */
    void download(String fileId, String destFile) throws ServiceException;
    /**
     * 文件下载
     *
     * @param fileId
     * @param outputStream
     * @throws ServiceException
     */
    byte [] download(String fileId) throws ServiceException;

    /**
     * 通过文件id 获取文件信息
     *
     * @param fileId
     * @return
     * @throws ServiceException
     */
    FileInfo getFileInfo(String fileId) throws ServiceException;

	void download(String fileId, File destFile) throws ServiceException;

}
