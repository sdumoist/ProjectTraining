package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.common.util.FileTool;

import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;

import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;

import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_UPLOAD_RECORD;

/**
 * 获取上传列表
 */
@Component
public class ApiPersonalUploadServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PERSONAL_UPLOAD_RECORD;

    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private ISysUsersService iSysUsersService;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,page当前页、size每页条数、userId当前用户Id
     *               name:文件名，模糊查询
     *               order：排序规则（0：文件名降序；1：文件名升序；2：上传时间升序；3：上传时间降序）
     * @return Response
     * @description: 获取上传列表
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String pageStr = params.get("pageNum");
            String sizeStr = params.get("pageSize");
            String name = params.get("name");
            String order = params.get("order");
            String userId = params.get("userId");
            String fileType = params.get("fileType");
            int pageNumber = Integer.parseInt(pageStr);
            int pageSize = Integer.parseInt(sizeStr);
            Map typeMap = new HashMap();
            //查询规则
            typeMap.put("1", "");
            typeMap.put("2", ".doc,.docx,.ppt,.pptx,.txt,.pdf,.xls,.xlsx");
            typeMap.put("3", ".png,.jpg,.bmp,.psd");
            typeMap.put("4", ".mp4,.avi,.mov");
            typeMap.put("5", ".mp3,.wav");
            String[] typeArr;
            if (fileType == null) {
                fileType = "0";
            }
            if ("0".equals(fileType)) {
                typeArr = null;
            } else {
                String typeResult = (String) typeMap.get(fileType);
                typeArr = typeResult.split(",");
            }
            List<FsFolderView> list = new ArrayList<>();
            //获取用户上传数据列表
            list = fsFolderService.getPersonUpload(userId,(pageNumber - 1) * pageSize, pageSize,name,typeArr,order);
            list = ConvertUtil.changeSize(list);
            //获取上传数据列表的条数
            int num = fsFolderService.getPersonUploadNum(userId,name);
            Map<String, Object> result = new HashMap<>();
            result.put("userName",iSysUsersService.getById(userId).getUserName());
            result.put("pageCount", num);
            result.put("pageNum", pageNumber);
            result.put("pageSize", pageSize);
            result.put("rows", list);
            response.setSuccess(true);
            response.setData(result);
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
