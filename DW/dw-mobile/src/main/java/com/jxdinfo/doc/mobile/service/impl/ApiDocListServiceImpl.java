package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.model.DocUserTopic;
import com.jxdinfo.doc.front.topicmanager.service.DocUserTopicService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_LIST;


/**
 * 定制显示专题、顺序
 */
@Component
public class ApiDocListServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = DOC_LIST;
    /**
     * 用户专题关联表服务
     */
    @Autowired
    private DocUserTopicService docUserTopicService;

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private ESUtil esUtil;

    @Autowired
    private FileTool fileTool;

    @Resource
    private PersonalOperateService operateService;


    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    /** 文档群组服务类 */
    @Autowired
    private IFsFolderService iFsFolderService;


    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,userId 当前用户;topicIds:按顺序排的topicId，逗号分隔
     * @return Response
     * @description: 定制显示专题、顺序
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String keyword = String.valueOf(params.get("keyword"));
            String tagString = String.valueOf(params.get("tagString"));
            Integer page= Integer.parseInt(String.valueOf(params.get("pageNum")));
            String folderId = String.valueOf(params.get("folderId"));
            String fileType = String.valueOf(params.get("fileCode"));
            String userId = params.get("userId");
            if(tagString.equals("null")){
                tagString=null;
            }     if(keyword.equals("null")){
                keyword=null;
            }
            Map typeMap = new HashMap();
            typeMap.put("1", ".doc,.docx");
            typeMap.put("2", ".ppt,.pptx");
            typeMap.put("3", ".txt");
            typeMap.put("4", ".pdf");
            typeMap.put("5", ".xls,.xlsx");
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

            Integer size =Integer.parseInt(String.valueOf(params.get("pageSize")));
            Map<String,Object> mapNew = new HashMap<>();
            List<String> listGroup = frontDocGroupService.getPremission(userId);
            List<String> rolesList = sysUserRoleMapper.getRolesByUserId(userId);
            Map<String, Object> result = new HashMap<>(5);
            Integer adminFlag = CommonUtil.getAdminFlag(rolesList);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            int count =0;
            fsFolderParams.setLevelCodeString("0001");
            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
            FsFolder folder = iFsFolderService.getById(folderId);
            String levelCode = folder.getLevelCode();
            if (adminFlag==1) {
                levelCodeString = null;
            }
            if ("UI".equals(folder.getFolderName())){
                String sql="";
                if(tagString!=null&&!"".equals(tagString)&& tagString.split("\\|").length!=0) {
                    String[] strs = tagString.split("\\|");
                    for (int i = 0; i < strs.length; i++) {
                        // 记录一个分类中多个标签的情况
                        // 将标签按照逗号分隔开
                        String[] strs_tags = strs[i].split(",");
                        if (strs_tags.equals("")) {
                            continue;
                        }
                        sql+=" and (";
                        // 将一个分类中的多个标签拼装，达到OR的效果
                        for (int j = 0; j < strs_tags.length; j++) {
                            if (j == 0) {
                                String strs_tag = strs_tags[j];
                                sql += " D.tags like '%"+strs_tag+"%'" ;
                            }else{
                                String strs_tag = strs_tags[j];
                                sql += " or D.tags like '%"+strs_tag+"%'";
                            }
                        }
                        sql+=")";
                        //将拼装后的标签添加到boolQueryBuilder中，达到AND的效果
                    }
                }
                List<DocInfo> docList = frontDocInfoService.getListByTime(folder.getLevelCode(),
                        levelCodeString,size*(page-1),size,sql,keyword);
                for (DocInfo docInfo : docList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String docId = docInfo.getDocId();
                    map.put("USERID", docInfo.getAuthorId());
                    map.put("SHOWTIME", ConvertUtil.changeTime(docInfo.getCreateTime()));
                    map.put("DOCID", docInfo.getDocId());
                    String fileSuffixName = docInfo.getDocType()
                            .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                    map.put("USERNAME", docInfo.getAuthorName());
                    map.put("TITLE", docInfo.getTitle());
                    Map mapContent=esUtil.getIndex(docInfo.getDocId());
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
                    int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                    // 真实文件的路径
                    map.put("PATH", docInfo.getFilePath());
                    map.put("ISSC", collection);
                    map.put("DOCTYPE", docInfo.getDocType());

                    double [] data = new double[2];
                    try {
                        data=   fileTool.getFileData(docInfo.getFilePdfPath(),"0");
                        if(data!=null){
                            map.put("WIDTH", data[0]);
                            map.put("HEIGHT",data[1]);
                        }else{
                            map.put("WIDTH", 1);
                            map.put("HEIGHT",1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    // 真实文件的路径
                    finalList.add(map);
                }
                 count = frontDocInfoService.getListByTimeCount(folder.getLevelCode(), levelCodeString,sql,keyword);


            }else{

                List<DocInfo> docList = frontDocInfoService.getListByTimeAll(folder.getLevelCode(), levelCodeString,
                        size*(page-1),size,"",keyword,typeArr);
                  count = frontDocInfoService.getListByTimeCount(folder.getLevelCode(), levelCodeString,"",keyword);
                for (DocInfo docInfo : docList) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    String docId = docInfo.getDocId();
                    map.put("USERID", docInfo.getAuthorId());
                    map.put("SHOWTIME", ConvertUtil.changeTime(docInfo.getCreateTime()));
                    map.put("DOCID", docInfo.getDocId());
                    String fileSuffixName = docInfo.getDocType()
                            .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                    map.put("USERNAME", docInfo.getUserName());
                    map.put("TITLE", docInfo.getTitle());
                    Map mapContent=esUtil.getIndex(docInfo.getDocId());
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
                    int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                    // 真实文件的路径
                    map.put("PATH", docInfo.getFilePath());
                    map.put("ISSC", collection);
                    map.put("DOCTYPE", docInfo.getDocType());

                    double [] data = new double[2];
                    try {
                        if ("".equals(docInfo.getFilePdfPath()) || docInfo.getFilePdfPath() == null ||
                                "undefined".equals(docInfo.getFilePdfPath())) {
                           data = null;
                        }
                        if (docInfo.getDocType().equals(".jpg") || docInfo.getDocType().equals(".png") ||
                                docInfo.getDocType().equals(".gif") || docInfo.getDocType().equals(".bmp")){
                            data=   fileTool.getFileData(docInfo.getFilePdfPath(),"0");
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
                    // 真实文件的路径
                    finalList.add(map);
                }
            }
            mapNew.put("count",count);
            mapNew.put("docList",finalList);
            response.setSuccess(true);
            response.setData(mapNew);
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
