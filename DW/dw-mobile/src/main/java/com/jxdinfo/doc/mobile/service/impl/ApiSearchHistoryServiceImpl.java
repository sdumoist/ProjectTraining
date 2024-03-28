package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docsearch.model.DocHistorySearch;
import com.jxdinfo.doc.front.docsearch.service.DocHistorySearchService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.DOC_VIEW_DATA;
import static com.jxdinfo.doc.mobile.constants.ApiConstants.SEARCH_HISTORY_RECORD;


/**
 * 获取搜索历史记录/热门搜索
 */
@Component
public class ApiSearchHistoryServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = SEARCH_HISTORY_RECORD;

    @Autowired
    private DocHistorySearchService docHistorySearchService;

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数：
     * @return Response
     * @description: 查询搜索记录/热门搜索
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String userId = params.get("userId");
            List<DocHistorySearch> historySearches = docHistorySearchService.getList(userId,0,8);

            List<Map> hotSearches = docHistorySearchService.selectHotKeywords();
            Map<String,Object> map = new HashMap<>();
            map.put("histories",historySearches);
            map.put("hot",hotSearches);
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
