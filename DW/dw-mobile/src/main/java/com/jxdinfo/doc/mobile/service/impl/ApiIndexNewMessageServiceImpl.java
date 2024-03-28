package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_NEW_MESSAGE;


/**
 * 主页最新动态显示
 */
@Component
public class ApiIndexNewMessageServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_NEW_MESSAGE;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private ESUtil esUtil;

    @Resource
    private PersonalOperateService operateService;
    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;

    @Autowired
    private FileTool fileTool;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除
     * @Title: execute
     * @author: yjs
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String amount = String.valueOf(params.get("amount"));
            String userId = params.get("userId");

            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            List<DocInfo> list = frontFsFileService.getList(0,Integer.parseInt(amount));

            for (DocInfo docInfo : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                String docId = docInfo.getDocId();
                Map mapContent=esUtil.getIndex(docId);
                map.put("USERID", docInfo.getAuthorId());
                map.put("SHOWTIME", ConvertUtil.changeTime(docInfo.getCreateTime()));
                map.put("DOCID", docInfo.getDocId());
                String fileSuffixName = docInfo.getDocType()
                        .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                map.put("USERNAME", docInfo.getAuthorName());
                map.put("TITLE", docInfo.getTitle());
                if(mapContent!=null){
                    if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                        map.put("CONTENT","");
                    }
                    map.put("CONTENT",mapContent.get("content"));
                }
                map.put("XZCOUNT", docInfo.getDownloadNum());
                map.put("SCCOUNT", docInfo.getCollectNum());
                map.put("YLCOUNT", cacheToolService.getReadNum(docInfo.getDocId()));
                // 转换之后的文件的pdf的路径
                map.put("PDFPATH", docInfo.getFilePdfPath());
                map.put("DOCTYPE", docInfo.getDocType());
                try {

                    double [] data = new double[2];
                    if ("".equals(map.get("PDFPATH")+"" ) ||
                            "undefined".equals(map.get("PDFPATH")+"")){
                        data = null;
                    }
                    if ((map.get("DOCTYPE")+"").equals(".jpg") || (map.get("DOCTYPE")+"").equals(".png") ||
                            (map.get("DOCTYPE")+"").equals(".gif") || (map.get("DOCTYPE")+"").equals(".bmp")){
                        data=   fileTool.getFileData(map.get("PDFPATH")+"","0");
                        if(data!=null){
                            map.put("WIDTH", data[0]);
                            map.put("HEIGHT",data[1]);
                        }else{
                            map.put("WIDTH", 1);
                            map.put("HEIGHT",1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
                int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");

                if("".equals(userId)||userId==null||"null".equals(userId)){
                    map.put("ISSC", "0");
                }else{
                    map.put("ISSC", collection);
                }
                // 真实文件的路径
                map.put("PATH", docInfo.getFilePath());

                finalList.add(map);
            }

            response.setSuccess(true);
            response.setData(finalList);

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
