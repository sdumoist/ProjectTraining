package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_PREVIEW_DETAIL;


/**
 * 文档详情预览（暂时不用，url太长。前台转义无法识别）
 */
@Component
public class ApiDocPreviewServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = DOC_PREVIEW_DETAIL;

    @Autowired
    private FsFileService fsFileService;
    /** 文档服务类  */
    @Autowired
    private FrontDocInfoService frontDocInfoService;
    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private ISysUsersService iSysUsersService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数：
     * @return Response
     * @description: 文档详情预览
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String fileId = params.get("fileId");
            String userId = params.get("userId");
            String type = params.get("fileType");
            String fileName = params.get("fileName");
            String showTime = params.get("showTime");
            String fileAuthor = params.get("userName");
            String username = iSysUsersService.getById(userId).getUserName();
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            //获取配置文件--是否有公司水印
            Map<String,String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
            //获取配置文件--是否有用户水印
            Map<String,String> mapUser = frontDocInfoService.getConfigure("watermark_user");
            String filePdfPath = fsFileService.getById(fileId).getFilePdfPath();
            String watermark_user_flag = mapUser.get("configValidFlag");
            String watermark_company_flag = mapCompany.get("configValidFlag");
            String companyValue = mapCompany.get("configValue");
            // TODO: 2019/1/17 url域名暂时写死，正式环境写死线上的域名
            String url = "192.168.137.161:8080/static/resources/pdf/web/viewer_mobile.html?file="
                    + URLEncoder.encode("/preview/list?fileId=" + URLEncoder.encode(filePdfPath,"UTF-8"),"UTF-8")
                    + "&username=" + URLEncoder.encode(username,"UTF-8")
                    + "&watermark_user_flag=" + URLEncoder.encode(watermark_user_flag,"UTF-8")
                    + "&watermark_company_flag=" + URLEncoder.encode(watermark_company_flag,"UTF-8")
                    + "&companyValue= " + URLEncoder.encode(companyValue,"UTF-8");
            Map<String,Object> map = new HashMap<>();
            map.put("docUrl",url);
            map.put("fileType", type);
            map.put("fileName", fileName);
            map.put("fileId", fileId);
            //map.put("category", orderType);
            map.put("userName", fileAuthor);
            map.put("showTime",showTime);
            response.setSuccess(true);
            response.setData(map);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }
}
