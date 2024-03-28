package com.jxdinfo.doc.manager.personextranetaccess.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.personextranetaccess.model.SysPersonnelNetworkPermissions;
import com.jxdinfo.doc.manager.personextranetaccess.service.PersonExtranetAccessService;
import com.jxdinfo.hussar.bsp.organ.service.SysOrgManageService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.base.tips.SuccessTip;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/access")
public class PersonExtranetAccessController extends BaseController {

    private String PREFIX = "/doc/manager/personextranetaccess/";
    private String PERSONAL_PREFIX = "/doc/manager/personextranetaccess/";

    @Autowired
    private PersonExtranetAccessService personExtranetAccessService;

    @Resource
    private UserUtil userUtil;

    @Resource
    private SysOrgManageService sysOrgManageService;


    @RequestMapping("/accessListView")
    @RequiresPermissions("access:accessListView")
    public String accessListView(Model model) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        String loginId = "superadmin";
        if (adminFlag != 1) {
            loginId = ShiroKit.getUser().getId();
        }
        model.addAttribute("createUserName", loginId);
        return PREFIX + "access_view.html";
    }


    @RequestMapping(value = "/accessList")
    @ResponseBody
    public JSON accessList(int page, int limit, String personName){
        int beginIndex = page * limit - limit;
        List<SysPersonnelNetworkPermissions> accList = personExtranetAccessService.accessList(beginIndex, limit, personName);
        int counts = personExtranetAccessService.getAccessListCount(personName);
        JSONObject json = new JSONObject();
        json.put("count", counts);
        json.put("data", accList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }


    @RequestMapping("/userTree")
    @ResponseBody
    public List<JSTreeModel> tree() {
        List<JSTreeModel> result = sysOrgManageService.getUserTree();
        List<String>    allId = personExtranetAccessService.selectAllUserId();
        JSTreeModel jsTreeModel = new JSTreeModel();
        jsTreeModel.setId("11");
        jsTreeModel.setCode("11");
        jsTreeModel.setText("系统用户");
        jsTreeModel.setParent("#");
        jsTreeModel.setType("isRoot");
        result.add(jsTreeModel);
        List<JSTreeModel> result2= new ArrayList();
        for ( int i=0;i<result.size();i++){
            if(result.get(i).getId().equals("superadmin")||result.get(i).getId().equals("wkadmin")||
                    result.get(i).getId().equals("auditadmin")||result.get(i).getId().equals("reviewadmin")||result.get(i).getId().equals("systemadmin")||result.get(i).getId().equals("businessadmin")||result.get(i).getId().equals("hussar")){
                continue;
            }else{

                for (String id:allId) {
                    if (id.equals(result.get(i).getId())){
                        result.get(i).setState(true,true,false);
                    }
                }

                result2.add(result.get(i));
            }
        }
        return result2;
    }
    @RequestMapping(value = "/getDepartment")
    @ResponseBody
    public String getDepartmentName(String parentId){
        return personExtranetAccessService.getDepartmentName(parentId);
    }
    @RequestMapping(value = "/getStru")
    @ResponseBody
    public String getStruName(String id){
        return personExtranetAccessService.getStruName(id);
    }

    @RequestMapping("/savePersonExtranetAccess")
    @ResponseBody
    public Object saveFolderExtranetAuth(String param) {

            try {
                personExtranetAccessService.savePersonExtrannetAccess(param);
            } catch (Exception e) {
                e.printStackTrace();
                return new ErrorTip(500, "人员外网访问权限配置失败");
            }

        return new SuccessTip();
    }

    @RequestMapping(value = "/deletePersonExtranetAccess")
    @ResponseBody
    public int deleteFolderExtranetAuth(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        personExtranetAccessService.removeByIds(list);
        return 0;
    }

    /**
     * 批量删除目录访问权限
     *
     * @param ids 目录id
     * @return 删除的数量
     */
    @RequestMapping("/delPersonExtranetAuth")
    @ResponseBody
    public boolean delPersonExtranetAuth(String ids) {

        List<String> list = Arrays.asList(ids.split(","));
        if (list != null && list.size() > 0) {
            QueryWrapper ew = new QueryWrapper<SysPersonnelNetworkPermissions>();
            ew.in("user_id", list);
            return personExtranetAccessService.remove(ew);
        }
        return true;
    }

}
