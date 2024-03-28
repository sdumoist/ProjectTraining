package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.statistics.dao.FileStatisticsMapper;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_HOT_DOC;


/**
 * 主页热门文档获取
 */
@Component
public class ApiIndexHotDocServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_HOT_DOC;

    /** 文库缓存工具类 */
    @Autowired
    private CacheToolService cacheToolService;


    @Resource
    private FileStatisticsMapper fileStatisticsMapper;

    @Resource
    private PersonalOperateService operateService;

    @Autowired
    private ESUtil esUtil;

    @Autowired
    private FileTool fileTool;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数：pageNumber、pageSize、opType（3）、dataType（0：总排行；1：周排行；2：月排行）
     * @return Response
     * @description: 获取热门文档
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            Integer pageNumber =Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            String opType = params.get("opType");
            String userId = params.get("userId");
            String dateType = params.get("dateType");

            List<Map> list =new ArrayList<>();
            Integer count=0;
            if("0".equals(dateType)){
              list = fileStatisticsMapper.getFileListDataByAll(pageNumber,pageSize);

                for (Map map : list) {
                    Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                    try {
                        int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");
                        if(mapContent!=null){
                            if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                                map.put("CONTENT","");
                            }
                            map.put("CONTENT",mapContent.get("content"));
                        }
                        map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
                        if("".equals(userId)||userId==null||"null".equals(userId)){
                            map.put("ISSC", "0");
                        }else{
                            map.put("ISSC", collection);
                        }
                        double [] data = new double[2];
                        if ("".equals(map.get("PDFPATH")+"" ) ||
                                "undefined".equals(map.get("PDFPATH")+"")){
                            data = null;
                        }
                        data=   fileTool.getFileData(map.get("PDFPATH")+"","0");
                        if ((map.get("DOCTYPE")+"").equals(".jpg") || (map.get("DOCTYPE")+"").equals(".png") ||
                                (map.get("DOCTYPE")+"").equals(".gif") ||(map.get("DOCTYPE")+"").equals(".bmp")){
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
                }
                count = fileStatisticsMapper.getFileListDataByAllCount();
            }else if("1".equals(dateType)){

                list = fileStatisticsMapper.getFileListDataByWeek(pageNumber,pageSize);
                for (Map map : list) {
                    try {
                        Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                        if(mapContent!=null){
                            if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                                map.put("CONTENT","");
                            }
                            map.put("CONTENT",mapContent.get("content"));
                        }
                        int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");

                        if("".equals(userId)||userId==null||"null".equals(userId)){
                            map.put("ISSC", "0");
                        }else{
                            map.put("ISSC", collection);
                        }
                        double [] data = new double[2];
                        if ("".equals(map.get("PDFPATH")+"" ) ||
                                "undefined".equals(map.get("PDFPATH")+"")){
                            data = null;
                        }
                        map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
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
                }
                count = fileStatisticsMapper.getFileListDataByWeekCount();
            }else{

                list = fileStatisticsMapper.getFileListDataByMonth(pageNumber,pageSize);
                for (Map map : list) {
                    Map mapContent=esUtil.getIndex(map.get("DOCID")+"");
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        map.put("CONTENT",mapContent.get("content"));
                    }
                    try {
                        int collection = operateService.getMyHistoryCountByFileId(map.get("DOCID")+"",userId,"5");

                        if("".equals(userId)||userId==null||"null".equals(userId)){
                            map.put("ISSC", "0");
                        }else{
                            map.put("ISSC", collection);
                        }
                        map.put("YLCOUNT", cacheToolService.getReadNum(map.get("DOCID")+""));
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
                }
                count = fileStatisticsMapper.getFileListDataByMonthCount();
            }
            list = ConvertUtil.changeMapTime(list);
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("list",list);
            map.put("pageCount",count);
            map.put("pageSize",pageSize);
            map.put("pageNum",pageNumber);
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
