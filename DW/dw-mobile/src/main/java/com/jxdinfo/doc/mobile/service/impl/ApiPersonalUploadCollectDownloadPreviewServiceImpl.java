package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.UPLOAD_COLLECT_DOWNLOAD_PREVIEW_LIST;

/**
 * 获取上传、收藏、下载、预览列表
 */
@Component
public class ApiPersonalUploadCollectDownloadPreviewServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = UPLOAD_COLLECT_DOWNLOAD_PREVIEW_LIST;

    /*个人中心数据处理的服务*/
    @Autowired
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUsersService iSysUsersService;

    /*文件服务*/
    @Autowired
    private IFsFolderService fsFolderService;

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;
    @Autowired
    private ESUtil esUtil;

    @Autowired
    private FileTool fileTool;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,page当前页、size每页条数、userId当前用户Id
     *               name:文件名，模糊查询
     *               order：排序规则（0：文件名降序；1：文件名升序；2：上传时间升序；3：上传时间降序）
     *               opType:操作类型（1:上传；2：收藏；3：预览；4：下载）
     * @return Response
     * @description: 获取上传、收藏、下载、预览列表
     * @Title: execute
     * @author:wst
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            Integer pageNumber =Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            String name = params.get("name");
            String order = params.get("order");
            String userId = params.get("userId");
            String opType = params.get("opType");
            String fileType = params.get("fileType");
            int beginIndex = pageNumber * pageSize - pageSize;
            Map typeMap = new HashMap();
            //查询规则
            typeMap.put("1", "");
            typeMap.put("2", ".doc,.docx,.ppt,.pptx,.txt,.pdf,.xls,.xlsx");
            typeMap.put("3", ".png,.jpg,.bmp,.psd");
            typeMap.put("4", ".mp4,.avi,.mov");
            typeMap.put("5", ".mp3,.wav");
            String[] typeArr;
            if (fileType == null||fileType.equals("")) {
                fileType = "1";
            }
            if ("1".equals(fileType)) {
                typeArr = null;
            } else {
                String typeResult = (String) typeMap.get(fileType);
                typeArr = typeResult.split(",");
            }
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            FsFolderParams fsFolderParams = new FsFolderParams();

            List<String> listGroup = docGroupService.getPremission(userId);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(roleList);
            fsFolderParams.setGroupList(listGroup);
            //获得目录管理权限层级码
            fsFolderParams.setType("2");
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            List<FsFolderView> uploadlist = new ArrayList<>();
            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> commonList = new ArrayList<Map<String, Object>>();
            List<Map> list = new ArrayList<>();
            int count = 0;
            if("1".equals(opType)){
                //获取用户上传数据列表
                uploadlist = fsFolderService.getPersonUpload(userId,(pageNumber - 1) * pageSize, pageSize,name,typeArr,order);
                uploadlist = ConvertUtil.changeSize(uploadlist);
                int flag = -1;
                for (FsFolderView fsFolderView : uploadlist) {
                    flag = flag + 1;
                    Map<String, Object> map = new HashMap<String, Object>();
                    String docId = fsFolderView.getFileId();
                    Map mapContent=esUtil.getIndex(docId);
                    map.put("USERID", fsFolderView.getCreateUserId());
                    map.put("SHOWTIME", ConvertUtil.changeTime2(uploadlist).get(flag).getShowTime());
                    map.put("DOCID", docId);
                    String fileSuffixName = fsFolderView.getFileType()
                            .substring(fsFolderView.getFileType().lastIndexOf(".") + 1);
                    map.put("USERNAME", fsFolderView.getCreateUserName());
                    map.put("TITLE", fsFolderView.getFileName());
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        map.put("CONTENT",mapContent.get("content"));
                    }
                    map.put("XZCOUNT", fsFolderView.getDownloadNum());
                    map.put("SCCOUNT", fsFolderView.getCollectnum());
                    map.put("YLCOUNT", cacheToolService.getReadNum(docId));
                    // 转换之后的文件的pdf的路径
                    map.put("PDFPATH", fsFolderView.getPdfPath());
                    map.put("DOCTYPE", fsFolderView.getFileType());
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
            map.put("PATH", fsFolderView.getFolderLocal());

            finalList.add(map);
        }
                //获取上传数据列表的条数
                count = fsFolderService.getPersonUploadNum(userId,name);
            }else if("5".equals(opType)){
                //获取我的操作记录，5对应收藏
                list = operateService.getMyHistory(userId, opType, beginIndex, pageSize, name,typeArr,order,levelCode,orgId);
                //获取当前用户收藏记录的总条数
                count = operateService.getMyHistoryCount(userId, opType, name);
            }else if("3".equals(opType)||"4".equals(opType)){
                list = operateService.getMyHistory(userId, opType, beginIndex, pageSize, name,typeArr,order,levelCode,orgId);
                count = operateService.getMyHistoryCount(userId, opType, name);
            }
            for (Map map : list) {
                Map mapContent=esUtil.getIndex(map.get("fileId")+"");
                try {
                    int collection = operateService.getMyHistoryCountByFileId(map.get("fileId")+"",userId,opType);
                    map.put("DOCID",map.get("fileId"));
                    map.put("SCCOUNT",map.get("collectNum"));
                    map.put("XZCOUNT",map.get("downloadNum"));
                    map.put("USERID",map.get("author"));
                    map.put("USERNAME",map.get("authorName"));
                    map.put("TITLE",map.get("fileName"));
                    map.put("DOCTYPE",map.get("fileType"));
                    Timestamp uploadTime = (Timestamp) map.get("uploadTime");
                    String str=ConvertUtil.changeTime3(uploadTime);
                    map.put("SHOWTIME",ConvertUtil.changeTime3(uploadTime));
                    if(map.get("fileId")==null||map.get("fileId").equals("null")){
                        map.put("YLCOUNT","");
                    }else {
                        map.put("YLCOUNT",cacheToolService.getReadNum(map.get("fileId").toString()));
                    }

                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        map.put("CONTENT",mapContent.get("content"));
                    }
                    map.put("PDFPATH",map.get("file_pdf_path"));
                    map.put("PATH",map.get("filePath"));
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        map.put("CONTENT",mapContent.get("content"));
                    }
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
                    commonList.add(map);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            Map histories = new HashMap();
            if("1".equals(opType)){
                histories.put("userName",iSysUsersService.getById(userId).getUserName());
                histories.put("rows",finalList);
            }else{
                histories.put("adminFlag",adminFlag);
                histories.put("rows",commonList);
            }
            histories.put("pageCount",count);
            histories.put("pageSize",pageSize);
            histories.put("pageNum",pageNumber);
            response.setSuccess(true);
            response.setData(histories);
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
