package com.jxdinfo.doc.manager.folderextranetauth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.folderextranetauth.model.FolderExtranetAuth;
import com.jxdinfo.doc.manager.folderextranetauth.service.IFolderExtranetAuthService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.base.tips.SuccessTip;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/folderExtranetManager")
public class FolderExtranetAuthController {
    /**
     * PREFIX
     */
    private String PREFIX = "/doc/manager/folderextranetauth/";

    @Autowired
    private IFsFolderService iFsFolderService;
    @Autowired
    private IFolderExtranetAuthService iFolderExtranetAuthService;

    /**
     * 跳转到目录外部访问权限管理页面
     *
     * @param model
     * @return
     */
    @GetMapping("/folderExtranetAuth")
    @RequiresPermissions("folderExtranetManager:folderExtranetAuth")
    public String toConsulationManagerView(Model model) {
        String userName = ShiroKit.getUser().getName();
        String userId = ShiroKit.getUser().getId();

        model.addAttribute("userName", userName);
        model.addAttribute("userId", userId);
        return PREFIX + "folder_extranet_auth.html";
    }

    /**
     * 批量删除目录访问权限
     *
     * @param ids 目录id
     * @return 删除的数量
     */
    @RequestMapping("/delFolderExtranetAuth")
    @ResponseBody
    public boolean delFolderExtranetAuth(String ids) {

        List<String> list = Arrays.asList(ids.split(","));
        if (list != null && list.size() > 0) {
            QueryWrapper ew = new QueryWrapper<FolderExtranetAuth>();
            ew.in("folder_id", list);
            return iFolderExtranetAuthService.remove(ew);
        }
        return true;
    }

    /**
     * 查询目录树
     *
     * @return
     */
    @RequestMapping("/findFolderExtranetAuthTree")
    @ResponseBody
    public List<Map> FolderExtranetAuthTree() {

        // 根目录
        List<FsFolder> folders = iFsFolderService.getRoot();
        FsFolder folder = folders.get(0);
        Map foldMap = new HashMap();
        foldMap.put("id", folder.getFolderId());
        foldMap.put("text", folder.getFolderName());
        foldMap.put("parent", "#");

        JSONObject state = new JSONObject();
        state.put("selected", false);
        state.put("checked", false);
        state.put("opened", true);
        foldMap.put("state", state);

        List<Map> mapList = iFsFolderService.findFolderExtranetAuthTree();
        mapList.add(foldMap);

        return mapList;
    }

    /**
     * 获取左侧目录树
     * @return
     */
    @RequestMapping("/findFolderExtranetAuthTreeLazy")
    @ResponseBody
    public List<Map> FolderExtranetAuthTreeLazy(String id) {
        List<Map> mapList = iFsFolderService.findFolderExtranetAuthTreeLazy(id);
        return mapList;
    }

    /**
     * 查询目录外网设置列表
     *
     * @param page
     * @param limit
     * @param folderName
     * @return
     */
    @RequestMapping("/folderExtranetAuthList")
    @ResponseBody
    public JSON getFolderExtranetAuthList(int page, int limit, String folderName) {
        //int beginIndex = page * limit - limit;
        Page<FolderExtranetAuth> iPage = new Page<>(page, limit);
        List<FolderExtranetAuth> auths = iFolderExtranetAuthService.selectFolderExtranetAuths(iPage, folderName);
        JSONObject json = new JSONObject();
        json.put("count", iPage.getTotal());
        json.put("data", auths);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 目录外网访问权限 保存
     *
     * @param param
     * @return
     */
    @RequestMapping("/saveFolderExtranetAuth")
    @ResponseBody
    public Object saveFolderExtranetAuth(String param) {
        try {
            iFolderExtranetAuthService.saveFolderExtranetAuth(param);
        } catch (Exception e) {
            e.printStackTrace();
            return new ErrorTip(500, "目录外网访问权限配置失败");
        }
        return new SuccessTip();
    }

    @RequestMapping("deleteFolderExtranetAuth")
    @ResponseBody
    public int deleteFolderExtranetAuth(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        iFolderExtranetAuthService.removeByIds(list);
        return 0;
    }


}
