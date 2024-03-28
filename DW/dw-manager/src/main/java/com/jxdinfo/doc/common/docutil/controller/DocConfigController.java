package com.jxdinfo.doc.common.docutil.controller;

import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 文库配置项
 * 
 * @author wangning
 */
@Controller
@RequestMapping("/docconfig")
public class DocConfigController extends BaseController {

	@Autowired
	private DocConfigService docConfigService;

	/**
	 * 根据配置项key获取配置项值
	 * 
	 * @author wangning
	 */
	@PostMapping("/getConfigValueByKey")
	@ResponseBody
	public String getConfigValueByKey(String configKey) {
		return docConfigService.getConfigValueByKey(configKey);
	}
}
