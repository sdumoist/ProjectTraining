package com.jxdinfo.doc.mobile.service.impl;


import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PIC_COLLECTION;

/**
 * 获取下载、预览列表
 */
@Component
public class ApiPictureServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PIC_COLLECTION;

    /*个人中心数据处理的服务*/
    @Autowired
    private PersonalOperateService operateService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private ISysUsersService iSysUsersService;

    /** 文档服务类  */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    @Resource
    private DocInfoService docInfoService;
    /**
     * 缓存工具服务类
     */
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private FileTool fileTool;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,page当前页、size每页条数、userId当前用户Id
     *               name:文件名，模糊查询
     *               order：排序规则（0：文件名降序；1：文件名升序；2：上传时间升序；3：上传时间降序）
     *               opType:操作类型（3：预览；4：下载）
     * @return Response
     * @description: 获取下载、预览列表
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            int totalPages = 0;
            int total = 0;
            Integer pageNumber =Integer.parseInt(params.get("pageNum")) ;
            Integer pageSize = Integer.parseInt(params.get("pageSize"));
            String docId = params.get("docId");
           DocInfo fileInfo= docInfoService.getDocDetail(docId);
         String folderId=  fileInfo.getFoldId();

            String userId =  params.get("userId");

            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            List<Map> list = new ArrayList<>();
            String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            //根据文件夹ID获取图片
            Map<String,Object> mapNew = new HashMap<>();

            total = frontDocInfoService.getFolderImgCount(folderId,userId,listGroup,levelCode,adminFlag,orgId,null);
            List<DocInfo> docInfos = frontDocInfoService.getFolderIMG(pageNumber,
          pageSize,folderId,userId,listGroup,levelCode,adminFlag,orgId,null);
            for (DocInfo docInfo:docInfos){
/*                if (docInfo.getDocId().equals(docId)){
                    continue;
                }*/
                Map<String, Object> map = new HashMap<String, Object>();

                map.put("USERID", docInfo.getAuthorId());
                map.put("SHOWTIME", ConvertUtil.changeTime(docInfo.getCreateTime()));
                map.put("DOCID", docInfo.getDocId());
                String fileSuffixName = docInfo.getDocType()
                        .substring(docInfo.getDocType().lastIndexOf(".") + 1);
//                int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
                map.put("USERNAME", docInfo.getAuthorName());
                map.put("TITLE", docInfo.getTitle());
                map.put("CONTENT",docInfo.getDocId());
                map.put("XZCOUNT", docInfo.getDownloadNum());
                map.put("SCCOUNT", docInfo.getCollectNum());
                map.put("YLCOUNT", cacheToolService.getReadNum(docInfo.getDocId()));
                // 转换之后的文件的pdf的路径
                if(docInfo.getFilePdfPath()==null){
                    map.put("PDFPATH","");
                }else{
                    map.put("PDFPATH", docInfo.getFilePdfPath());
                }

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
                if (docInfo.getDocId().equals(docId)){
//                    map.put("isSelf",true);
//                    list.add(map);
                    continue;
                }
                list.add(map);
            }
            totalPages = total %  pageSize == 0 ? (total-1) / pageSize : (total-1) /  pageSize + 1;
            mapNew.put("picList",list);
            mapNew.put("totalPages",totalPages);
            mapNew.put("total",total-1);
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
