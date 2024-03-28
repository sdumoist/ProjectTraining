package com.jxdinfo.doc.common.docutil.service;

import java.util.Map;

/**
 * 操作PDF服务接口
 * @author wangning
 * @date 2018-8-10 19:45:06
 */
public interface PdfService {

    /**
     * 转换PDF接口
     * @throws Exception 
     */
    public Map<String,Object> changeToPdf(String sourcePath, String targetPath, long fileSapce, String contentTypes, String fileId) throws Exception;
}
