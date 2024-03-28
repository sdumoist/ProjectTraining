package com.jxdinfo.doc.mobile.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApplyAttachment;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyAttachmentService;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectComponentService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.system.dao.SysUserMapper;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.organ.dao.SysOrganMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.uwyn.jhighlight.fastutil.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.COMPONENT_DETAIL;


/**
 * 主页最新动态显示
 */
@Component
public class ApiComponentDetailImpl extends ApiBaseServiceImpl {


    private static final String businessID = COMPONENT_DETAIL;
    /**
     * 缓存工具类
     */
    @Autowired
    private CacheToolService cacheToolService;


    /** 科研成功服务类 */
    @Autowired
    private ISysUsersService iSysUsersService;

    /** 科研成功服务类 */
    @Autowired
    private ComponentApplyService componentApplyService;

    /** 部门id服务类 */
    @Resource
    private SysUsersMapper sysUsersMapper;

    /** 部门id服务类 */
    @Resource
    private SysOrganMapper sysOrganMapper;

    @Autowired
    private FileTool fileTool;

    /**
     * 组件附件服务类
     */
    @Resource
    private ComponentApplyAttachmentService componentApplyAttachmentService;

    /**
     * 组件项目复用服务类
     */
    @Resource
    private MultiplexProjectService multiplexProjectService;

    /**
     * 组件项目复用服务类
     */
    @Resource
    private DocInfoService docInfoService;

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
            String componentId = params.get("componentId");
            String userId = params.get("userId");
            String userName = sysUsersMapper.selectById(userId).getUserName();
            ComponentApply componentApply = componentApplyService.getById(componentId);
            Map<String,Object> map = new HashMap<>();

            String componentDeptId=iSysUsersService.getById(componentApply.getUserId()).getDepartmentId();
            String componentDept=sysOrganMapper.selectById(componentDeptId).getOrganName();
            map.put("component",componentApply);
            map.put("componentDept",componentDept);
            map.put("userName",userName);
            map.put("readNum",cacheToolService.getComponentReadNum(componentId)+1);
            String str= ConvertUtil.changeTime(componentApply.getCreateTime());
            map.put("componentTime", str);


            List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(componentId);
            for (ComponentApplyAttachment attachment:list) {
                DocInfo docInfo = new DocInfo();
                docInfo =  docInfoService.getDocDetail(attachment.getAttachmentId());
                map.put("PDFPATH", docInfo.getFilePdfPath());
                map.put("title", docInfo.getAuthorName());
                map.put("DOCTYPE", docInfo.getDocType());
                attachment.setPdfPath( docInfo.getFilePdfPath());

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

                            attachment.setHeight(data[0]);
                            attachment.setWidth( data[1]);
                        }else{

                            attachment.setHeight(1);
                            attachment.setWidth(1);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServiceException e) {
                    e.printStackTrace();
                }
            }
            List<MultiplexProject> multiplexList=  multiplexProjectService.componentMultiplexList(componentId);
            for ( MultiplexProject doc:multiplexList) {


                String dateStr= ConvertUtil.changeTime(doc.getCreateTime());
                doc.setCreateTimeStr(dateStr);


            }
            map.put("ComponentAttachmentList", list);
            map.put("multiplexList", multiplexList);
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
    public List<ComponentApply> changeDate(List<ComponentApply> list) {
        for (ComponentApply componentApply : list) {
            Timestamp ts = componentApply.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                componentApply.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {

                componentApply.setShowTime(lackTs/(1000*60)+"分钟前");

            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                componentApply.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                componentApply.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String str= ConvertUtil.changeTime(ts);
                componentApply.setShowTime(str);
            }

        }
        return list;
    }
}
