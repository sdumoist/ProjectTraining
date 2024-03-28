package com.jxdinfo.doc.common.docutil.service;

import java.io.File;

import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;

/**
 * 允许文件校验
 * create by lorne on 2017/9/28
 */
public interface FileValidateService {

    void validateFile(File file) throws ServiceException;

}
