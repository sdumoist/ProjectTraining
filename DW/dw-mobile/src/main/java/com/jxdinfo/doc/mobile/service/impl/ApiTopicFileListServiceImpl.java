package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.service.SpecialTopicService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.TOPIC_FILE_LIST;


/**
 * 获取专题下的文件列表
 */
@Component
public class ApiTopicFileListServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = TOPIC_FILE_LIST;

    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;
    /** 文库缓存工具类 */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private DocGroupService docGroupService;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private SysStruMapper sysStruMapper;

    @Autowired
    private ESUtil esUtil;

    @Autowired
    private FileTool fileTool;
    @Autowired
    private ISysUsersService iSysUsersService;
    @Resource
    private PersonalOperateService operateService;
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
     * @param params 参数,page当前页、size每页条数、topicId专题id、userId当前用户Id
     * @return Response
     * @description: 获取专题下文件列表
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String pageStr = String.valueOf(params.get("pageNum"));
            String sizeStr = String.valueOf(params.get("pageSize"));
            String topicId = String.valueOf(params.get("topicId"));
            String userId = String.valueOf(params.get("userId"));
            int page = Integer.parseInt(pageStr);
            int size = Integer.parseInt(sizeStr);
            int startNum = page * size - size;
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            //获取当前的登录用户
            String userName = iSysUsersService.getById(userId).getUserName();
            List<String> listGroup = docGroupService.getPremission(userId);
            //根据专题ID取出专题的信息
            SpecialTopic specialTopic = frontTopicService.getTopicDetailById(topicId);

            try {
                String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
                specialTopic.setTopicCover(topicCover);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //从缓存中取出专题的浏览次数
            specialTopic.setViewNum(cacheToolService.getTopicReadNum(topicId) + 1);
            //测试不考虑专题权限,取专题下面的文档
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
            fsFolderParams.setType("2");
            String deptName = sysStruMapper.selectById(orgId).getOrganAlias();
            String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
            List<Map>  docList = frontTopicService.getDocByTopicId(topicId, "create_time", startNum, size,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,null);
            int docCount = frontTopicService.getDocByTopicIdCount(topicId,userId,listGroup,levelCode,adminFlag,deptName,null);
            List<Map<String,Object>> docListWithReadNum = new ArrayList<Map<String,Object>>();
            List<Map<String,Object>> docListWithReadNumNew = new ArrayList<Map<String,Object>>();
            if (docList != null) {
                for (int i = 0, j = docList.size(); i < j; i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    Map mapContent=esUtil.getIndex(docList.get(i).get("doc_id")+"");
                    map.put("USERID", docList.get(i).get("author_id"));
                    map.put("DOCID",  docList.get(i).get("doc_id"));

//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                    map.put("USERNAME",  docList.get(i).get("authorName"));
                    map.put("TITLE",   docList.get(i).get("title"));
                    if(mapContent!=null){
                        if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                            map.put("CONTENT","");
                        }
                        map.put("CONTENT",mapContent.get("content"));
                    }
                    map.put("SCCOUNT",  docList.get(i).get("collectNum"));
                    map.put("XZCOUNT",  docList.get(i).get("downloadNum"));
                    map.put("YLCOUNT", cacheToolService.getReadNum( docList.get(i).get("doc_id")+""));
                    // 转换之后的文件的pdf的路径
                    map.put("PDFPATH",  docList.get(i).get("PDFPATH"));
                    int collection = operateService.getMyHistoryCountByFileId( docList.get(i).get("doc_id")+"",userId,"5");
                    // 真实文件的路径
                    map.put("PATH", docList.get(i).get("PATH"));
                    map.put("ISSC", collection);
                    map.put("DOCTYPE",  docList.get(i).get("fileType"));

                    double [] data = new double[2];
                    try {
                        data=   fileTool.getFileData( docList.get(i).get("PDFPATH")+"","0");
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
                    Timestamp createTime = (Timestamp)  docList.get(i).get("createTime");
                    if (System.currentTimeMillis() - createTime.getTime() <= 604800000){
                        map.put("isNew", true);
                    }
                    map.put("SHOWTIME", ConvertUtil.changeTime(createTime));
                    docListWithReadNumNew.add(map);
                }
            }
            specialTopic.setDocCount(docCount);
//            specialTopic.setDocList(docListWithReadNumNew);
            Map<String,Object> map = new HashMap<>();
            map.put("userName",userName);
            map.put("topic",specialTopic);
            map.put("docList",docListWithReadNumNew);
            map.put("pageCount",docCount);
            map.put("pageNum",page);
            map.put("pageSize",size);
            map.put("adminFlag",adminFlag);
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
