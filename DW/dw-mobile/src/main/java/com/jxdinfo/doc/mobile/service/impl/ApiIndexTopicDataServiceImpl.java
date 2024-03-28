package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
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

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_TOPIC_DATA;


/**
 * 获取专题文件列表(首页)
 */
@Component
public class ApiIndexTopicDataServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_TOPIC_DATA;

    /** 前台文件服务类 */
    @Autowired
    private FrontFsFileService frontFsFileService;
    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;
    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;

    @Resource
    private PersonalOperateService operateService;


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

    @Autowired
    private ISysUsersService iSysUsersService;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,userId 当前用户
     * @return Response
     * @description: 获取专题文件列表(首页)
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            List<SpecialTopic> topicList = frontTopicService.getTopicList(0, 5);
            String userId = params.get("userId");
            List<String> listGroup = frontDocGroupService.getPremission(userId);
            try{

                if (topicList != null && topicList.size() > 0) {
                    for (SpecialTopic specialTopic : topicList) {
                        String topicId = specialTopic.getTopicId();
                        String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
                        specialTopic.setTopicCover(topicCover);
                        FsFolderParams fsFolderParams = new FsFolderParams();
                        fsFolderParams.setGroupList(listGroup);
                        fsFolderParams.setUserId(userId);
                        fsFolderParams.setType("2");
                        String levelCode = businessService.getLevelCodeByUserMobile(fsFolderParams);
                        List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
                        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
                        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
                        String orgId = iSysUsersService.getById(userId).getDepartmentId();
                        fsFolderParams.setType("2");
                        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
                        //测试不考虑专题权限,取专题下面的文档
                        List<Map> docList = frontTopicService.getDocByTopicId(topicId, "create_time", 0, 4,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,null);
                        List<Map> docListNew =new ArrayList<>();
                        docList.forEach( doc -> {
                            Map<String, Object> map = new HashMap<String, Object>();
                            Map mapContent=esUtil.getIndex(doc.get("doc_id")+"");
                            map.put("USERID", doc.get("author_id"));
                            map.put("DOCID", doc.get("doc_id"));

//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                            map.put("USERNAME", doc.get("authorName"));
                            map.put("TITLE",  doc.get("title"));
                            if(mapContent!=null){
                                if(mapContent.get("content")==null||mapContent.get("content").equals("null")){
                                    map.put("CONTENT","");
                                }
                                map.put("CONTENT",mapContent.get("content"));
                            }
                            map.put("SCCOUNT", doc.get("collectNum"));
                            map.put("XZCOUNT", doc.get("downloadNum"));
                            map.put("YLCOUNT", doc.get("readNum"));
                            // 转换之后的文件的pdf的路径
                            map.put("PDFPATH", doc.get("PDFPATH"));
                            int collection = operateService.getMyHistoryCountByFileId(doc.get("doc_id")+"",userId,"5");
                            // 真实文件的路径
                            map.put("PATH",doc.get("PATH"));
                            map.put("ISSC", collection);
                            map.put("DOCTYPE", doc.get("doc_id"));


                            double [] data = new double[2];
                            try {
                                data=   fileTool.getFileData(doc.get("PDFPATH")+"","0");
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
                            Timestamp createTime = (Timestamp) doc.get("createTime");
                            if (System.currentTimeMillis() - createTime.getTime() <= 604800000){
                                map.put("isNew", true);
                            }
                            map.put("SHOWTIME", ConvertUtil.changeTime(createTime));
                            docListNew.add(map);
                        });
                        specialTopic.setDocList(docListNew);
                    }
                }
            } catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
//        List<SpecialTopic> topicList = cacheToolService.getTopicList();
            for (int i = 0; i < topicList.size(); i++){
                switch (i){
                    case 0:
                        topicList.get(i).setTopicCover("/static/resources/img/front/index/theme3.png");
                        break;
                    case 1:
                        topicList.get(i).setTopicCover("/static/resources/img/front/index/theme5.png");
                        break;
                    case 2:
                        topicList.get(i).setTopicCover("/static/resources/img/front/index/theme4.png");
                        break;
                    case 3:
                        topicList.get(i).setTopicCover("/static/resources/img/front/index/theme6.png");
                        break;
                    case 4:
                        topicList.get(i).setTopicCover("/static/resources/img/front/index/theme7.png");
                        break;
                    default:
                        break;
                }
            }
            response.setSuccess(true);
            response.setData(topicList);
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
