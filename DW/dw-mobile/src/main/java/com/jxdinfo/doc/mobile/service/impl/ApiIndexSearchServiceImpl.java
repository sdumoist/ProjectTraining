package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.historymanager.service.SearchHistoryService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_SEARCH_DOC;

/**
 * 主页搜索接口
 */
@Component
public class ApiIndexSearchServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_SEARCH_DOC;

    @Resource
    private PersonalOperateService operateService;
    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 文档群组服务类
     */
    @Autowired
    private FrontDocGroupService frontDocGroupService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private FileTool fileTool;

    /**
     * 历史记录服务类
     **/
    @Resource
    private SearchHistoryService historyService;

    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private ISysUsersService iSysUsersService;

    /**
     * es服务类
     */
    @Autowired
    private ESService esService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;


    @Override
    public String getBusinessID() {
        return businessID;
    }
    private static final Map<String, String> fileTypeMap = new HashMap<String, String>();

    static {
        fileTypeMap.put("6", "all");//全部
        fileTypeMap.put("8", "image");//图片
        fileTypeMap.put("9", "video");//视频
        fileTypeMap.put("10", "audio");//音频
        fileTypeMap.put("11", "notimage");//不含图片的全部文档

        fileTypeMap.put("7", "allword");//全部文档
        fileTypeMap.put("1", "word"); //word
        fileTypeMap.put("2", "presentationml");//ppt
        fileTypeMap.put("3", "plain");//txt
        fileTypeMap.put("4", "pdf");//pdf
        fileTypeMap.put("5", "spreadsheetml");//excel


    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除
     * @Title: execute
     * @author: zhaoAi
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String keyword = String.valueOf(params.get("keyword"));
            String tagString = String.valueOf(params.get("tagString"));
            Integer page= Integer.parseInt(String.valueOf(params.get("pageNum")));
            String userId = String.valueOf(params.get("userId"));
            String fileType = String.valueOf(params.get("fileCode"));
            Integer size =Integer.parseInt(String.valueOf(params.get("pageSize")));
            List<String> rolesList = sysUserRoleMapper.getRolesByUserId(userId);
            Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
            Map history = new HashMap();
            history.put("keywords", keyword);
            history.put("userId", userId);
            history.put("searchTime", new Date());
            historyService.insertIntoSearchHistory(history);
            String docType = "";
            ESResponse<Map<String, Object>> sd = new ESResponse<>();
            ESResponse<Map<String, Object>> imgSd = null;
            if ("0".equals(fileType)) {
//                    sd = esService.searchMobile(keyword, page, adminFlag, size,userId);
                // TODO: 2019/1/21 过滤不掉图片 
                sd = esService.searchMobile(keyword, "notimage", page, adminFlag, size, tagString,userId,null, null);
                imgSd = esService.searchMobile(keyword, "image", 1, adminFlag, 8, tagString,userId,null,null);
                // 从es中获取文档的信息map
            } else {
                // 选择了文件类型的ES查询你们
                docType = fileTypeMap.get(fileType);
                sd = esService.searchMobile(keyword, docType, page, adminFlag, size, tagString,userId,null,null);
            }

            sd = getLocalData(sd,userId);
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("docList",sd.getItems());
            if (imgSd != null){
                imgSd = getLocalData(imgSd,userId);
                resultMap.put("imgList",imgSd.getItems());
            }
            resultMap.put("pageCount",sd.getTotal());
            resultMap.put("pageSize",size);
            resultMap.put("pageNum",page);
            response.setSuccess(true);
            response.setData(resultMap);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }

    public ESResponse<Map<String, Object>> getLocalData(ESResponse<Map<String, Object>> sd,String userId){
        List<String> listGroup = frontDocGroupService.getPremission(userId);
        List<Map<String, Object>> list = sd.getItems();
        if (null != list && list.size() > 0) {
            List<String> idList = new ArrayList<String>();
            // 优化代码逻辑，高亮的title和content放到map里，避免嵌套循环逻辑
            Map<String, String> titleMap = new HashMap<String, String>();
            Map<String, String> contentMap = new HashMap<String, String>();
            // for循环拼接文件id
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = list.get(i);
                String id = map.get("id") == null ? "" : map.get("id").toString();
                idList.add(id);
                String title = map.get("title") == null ? "" : map.get("title").toString();
                String content = map.get("content") == null ? "" : map.get("content").toString();
                titleMap.put(id, title);
                contentMap.put(id, content);
            }
            // 根据ID，从数据库查询出文件的详细数据
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            List<DocInfo> docList = frontDocInfoService.getDocInfo(idList, userId, listGroup, levelCode,orgId,null);
            List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
            if (docList != null && docList.size() > 0) {
                // 按照ES检索结果的顺序进行排序展示
                idList:
                for (String id : idList) {
                    // 查询文档详细信息
                    docList:
                    for (DocInfo docInfo : docList) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        String docId = docInfo.getDocId();
                        if (!docId.equals(id)) {
                            continue docList;
                        }
                        map.put("USERID", docInfo.getAuthorId());
                        map.put("SHOWTIME", ConvertUtil.changeTime(docInfo.getCreateTime()));
                        map.put("DOCID", docInfo.getDocId());
                        String fileSuffixName = docInfo.getDocType()
                                .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                        map.put("USERNAME", docInfo.getAuthorName());
                        map.put("TITLE", docInfo.getTitle());
                        map.put("CONTENT",contentMap.get(docId).replaceAll("<em>","").replaceAll("</em>",""));
                        map.put("XZCOUNT", docInfo.getDownloadNum());
                        map.put("SCCOUNT", docInfo.getCollectNum());
                        map.put("YLCOUNT", cacheToolService.getReadNum(docInfo.getDocId()));
                        map.put("YLCOUNTCOMPONENT", cacheToolService.getComponentReadNum(docInfo.getDocId()));
                        map.put("fileSuffixName", fileSuffixName);// 后缀名
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
                        continue idList;
                    }
                }
            }
            sd.setItems(finalList);
        }
        return sd;
    }
}
