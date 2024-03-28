package com.jxdinfo.doc.common.docutil.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.docutil.service.FileValidateService;
import com.jxdinfo.doc.common.util.FilenameUtils;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docmanager.ex.ParamException;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;

/**
 * 类型校验
 * @author  ...
 * @modify wangning  
 * 
 */
@Service
public class FileValidateServiceImpl implements FileValidateService {
	
	/** 数据库配置的文件校验类型 key值 */
	private final static String validateTypeKey = "fileValidType";
	
	/** 文库配置类服务 */
	@Autowired
	private DocConfigService docConfigService;
    
	/** 允许的文件类型 */
    private List<String> types;

    /** 配置加载标识位 */
    private boolean hasLoad = false;

    private void init() {
        if (!hasLoad) {
        	String validateTypeCValue = docConfigService.getConfigValueByKey(validateTypeKey);
            if (!StringUtil.checkIsEmpty(validateTypeCValue)){
            	types = Arrays.asList(validateTypeCValue.split(","));
            } else {
            	types = new ArrayList<String>();
            }
        	//TODO: 在配置表前台功能开发完成前，此处暂时注释掉
            //hasLoad = true;
        }
    }

    @Override
    public void validateFile(File file) throws ServiceException {
        init();

        final String ext = FilenameUtils.getExtension(file.getName());

        if (types.contains(ext.toLowerCase())) {
            throw new ParamException("file type error." + ext);
        }
    }
}
