package com.jxdinfo.doc.mobile.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.service.SpecialTopicService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.INDEX_TOPIC_LIST;


/**
 * 获取专题列表(首页)
 */
@Component
public class ApiIndexTopicListServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = INDEX_TOPIC_LIST;

    @Autowired
    private ISysUsersService iSysUsersService;
    @Resource
    private SysStruMapper sysStruMapper;
    /** 前台专题服务类 */
    @Autowired
    private FrontTopicService frontTopicService;

    @Autowired
    private DocGroupService docGroupService;


    /** 文库缓存工具类 */
    @Autowired
    private CacheToolService cacheToolService;
    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;
    /**
     * 专题维护
     */
    @Autowired
    private SpecialTopicService specialTopicService;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数,userId 当前用户
     * @return Response
     * @description: 获取专题列表(首页)
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String userId = String.valueOf(params.get("userId"));
            String name = String.valueOf(params.get("name"));
            List<SpecialTopic> topicList = specialTopicService.getValidTopicList(userId,"1",name);
            List<SpecialTopic> specialList = specialTopicService.getSpecialTopicList(userId,name);
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            List<String> roleList = sysUserRoleMapper.getRolesByUserId(userId);
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            String orgId = iSysUsersService.getById(userId).getDepartmentId();
            String deptName = sysStruMapper.selectById(orgId).getOrganAlias();
            String levelCode = businessService.getLevelCodeByUserUploadMobile(userId,fsFolderParams);
            if (specialList != null ) {
                try {
                    for (int i = 0; i < specialList.size(); i++) {
                        SpecialTopic specialTopic = specialList.get(i);
                        int docCount = frontTopicService.getDocByTopicIdCount(specialTopic.getTopicId(), userId, listGroup, levelCode, adminFlag,deptName,null);
                        String topicCover = URLEncoder.encode(specialTopic.getTopicCover(), "UTF-8");
                        specialTopic.setTopicCover(topicCover);
                        specialTopic.setDocNum(docCount);
                        specialTopic.setDocCount(docCount);

                        //从缓存中读取浏览数
                        specialTopic.setViewNum(cacheToolService.getTopicReadNum(specialTopic.getTopicId()));
                    }
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            int topicCount = frontTopicService.getValidTopicListCount(name);
            Map<String,Object> map = new HashMap<>();
            map.put("availableList",topicList);
            map.put("specialList",specialList);
            map.put("selectedList",specialTopicService.getValidTopicList(userId,null,name));
            map.put("topicCount",topicCount);
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
