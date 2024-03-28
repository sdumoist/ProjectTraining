package com.jxdinfo.doc.front.docsharemanager.controller;

import com.alibaba.druid.util.StringUtils;
import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author luzhanzhao
 * @date 2018-12-10
 * @description 分享相关的控制层
 */
@Controller
@RequestMapping("/s")
public class ShareResourceController {

    @Value("${extendedFunctions.shareUserConfig}")
    private boolean shareUserConfig;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;


    @Autowired
    private ComponentApplyService componentApplyService;
    /**
     * 文档服务类
     */
    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;

    @Autowired
    private HussarCacheManager hussarCacheManager;
    @Autowired
    private DocGroupService docGroupService;
    protected static String REDIRECT = "redirect:";

    /**
     * @param fileId    文件id
     * @param fileType  文件类型
     * @param pwdFlag   有无提取码
     * @param validTime 有效期（0对应永久）
     * @param request
     * @return 分享结果
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 分享链接
     */
    @RequestMapping("/shareHref")
    @ResponseBody
    public Map shareHref(String fileId, String fileType, @RequestParam(defaultValue = "0") int pwdFlag, @RequestParam(defaultValue = "0") int validTime, @RequestParam(defaultValue = "0") int authority,
                         @RequestParam(defaultValue = "0") String shareUserRadio,@RequestParam(defaultValue = "") String selectShareUsersId,HttpServletRequest request) {
        return shareResourceService.newShareResource(fileId, fileType, pwdFlag, validTime, authority, shareUserRadio,selectShareUsersId,request);
    }

    /**
     * @param hash  分享文件的映射地址
     * @param model
     * @return 分享文件的预览界面
     * @author luzhanzhao
     * @date 2018-12-11
     */
    @RequestMapping("/{hash}")
    public String viewShare(@PathVariable String hash, Model model, String pwd) {
        model.addAttribute("hash", hash);
        //根据映射地址获取分享文件的信息
        Map shareResource = shareResourceService.getShareResource(hash);
        if (shareResource == null) {
            model.addAttribute("error_msg", "该分享链接已失效");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }
        // 获取链接的分享查看人员配置
        String selectUserFlag = StringUtil.getString(shareResource.get("selectUserFlag"));
        // 如果是指定人员，则判断是否已登录，已登录则判断当前登录人是否为指定人员之一，未登录则跳转到登录页
        if (StringUtils.equals("1",selectUserFlag)) {
            // 获取当前登录人
            ShiroUser shiroUser = ShiroKit.getUser();
            if (shiroUser == null) {
                return "/login.html";
            } else {
                // 当前用户
                String userId = shiroUser.getId();
                // 获取当前登陆人的角色
                List<String> roleList = shiroUser.getRolesList();
                // 获取当前登陆人所在的用户组
                List<String> groupList = docGroupService.getPremission(userId);
//                List<String> groupList = new ArrayList<>();
                // 链接的创建人
                String createUserId = StringUtil.getString(shareResource.get("createUserId"));
                // 指定的用户
                String selectUsersId = StringUtil.getString(shareResource.get("selectUsersId"));
                String[] userIdArr = selectUsersId.split(",");
                List<String> userIdList = Arrays.asList(userIdArr);
                boolean viewFlag = false;
                // 查看人在在指定人员范围内或者查看人是链接创建人
                if (userIdList.contains(userId) || StringUtils.equals(userId, createUserId)) {
                    viewFlag = true;
                }
                // 查看人的角色在分享范围内
                for(String item:roleList){
                    if(userIdList.contains(item)){
                        viewFlag = true;
                        break;
                    }
                }
                // 查看人的用户组在分享范围内
                for(String item:groupList){
                    if(userIdList.contains(item)){
                        viewFlag = true;
                        break;
                    }
                }

                // 如果不是指定人员，则跳转到无权限页面
                if (!viewFlag) {
                    model.addAttribute("error_msg", "您无权查看该链接");
                    model.addAttribute("isPersonCenter", false);
                    return "/doc/front/preview/share_error.html";
                }
            }
        }

        //获取文件的可访问状态
        String pwdFlag = shareResource.get("pwdFlag").toString();
        String authority = "";
        if (shareResource.get("authority") == null) {
            authority = "0";
        } else {
            authority = shareResource.get("authority").toString();
        }
        // 该链接的有效性，链接的valid（链接分享者设置）
        String valid = shareResource.get("valid").toString();
        String rightPwd = shareResource.get("pwd").toString();
        String href = shareResource.get("href").toString();
        String docValid = "";
        String shareFlag = "";
        if (shareResource.get("docValid") == null) {
            docValid = "1";
        } else {
            docValid = shareResource.get("docValid").toString();
        }
        if (shareResource.get("shareFlag") == null) {
            shareFlag = "1";
        } else {
            // 该文件是否可分享（文件所有者设置）
            shareFlag = shareResource.get("shareFlag").toString();
        }
        //将有效期转换成日期类型
        Date validTime = StringUtil.stringToDate(shareResource.get("validTime").toString());
        //获取文件名
        String title = "";
        String docId = shareResource.get("docId").toString();
        if (!"".equals(docId) && shareResource.get("title") == null) {
            FsFolder fsFolder = fsFolderService.getById(docId);
            if (fsFolder != null) {
                title = fsFolder.getFolderName();
            }
        } else {
            title = shareResource.get("title").toString();

        }
        model.addAttribute("title", title);
        //获取当前时间
        Date today = new Date();
        model.addAttribute("createTime", shareResource.get("createTime"));
        model.addAttribute("validTime", shareResource.get("validTime"));
        model.addAttribute("validLack", changeValid(validTime));
        model.addAttribute("authority", authority);
        //判断该分享资源是否在有效期内
        if ("0".equals(valid) || today.after(validTime) || "0".equals(docValid) || "0".equals(shareFlag)) {
            //资源失效则返回分享错误页面
            model.addAttribute("error_msg", "该分享链接已失效");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }
        //判断是否有提取码
        if ("1".equals(pwdFlag)) {
            //如果用户输入的提取码为空，则返回提取码验证页面
            if (StringUtil.checkIsEmpty(pwd)) {
                model.addAttribute("isPersonCenter", false);
                return "/doc/front/preview/share_verify.html";
            } else if (!pwd.equals(rightPwd)) {
                //密码错误则返回密码错误页面
                model.addAttribute("pwd_error", "提取码错误");
                model.addAttribute("isPersonCenter", false);
                return "/doc/front/preview/share_verify.html";
            }
        }

        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String,String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("shareUser", shareResource.get("shareUser"));

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

        // 是否显示客户端
        Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
        model.addAttribute("clientShow", clientShowMap.get("configValue"));

        // 是否显示联系方式
        Map<String, String> contactShowMap = frontDocInfoService.getConfigure("contact_show");
        model.addAttribute("contactShow", contactShowMap.get("configValue"));

        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        if (null != shiroUser) {
            if (href.indexOf("toShowFolder") != -1) {
            } else {
                href = href.replaceAll("sharefile", "preview");
            }
        }
        href += "&shareForward=1";
        return "forward:"+ href;
    }

    /**
     * @param fileId   文档id
     * @param fileType 文档类型
     * @param fileName 文档名称
     * @param model
     * @return 确认页面
     * @author luzhanzhao
     * @date 2018-12-11
     * @description 获取分享确认页面
     */
    @RequestMapping("/shareConfirm")
    public String shareConfirm(String fileId, String fileType, String fileName, Model model) {
        fileType = XSSUtil.xss(fileType);
        if (fileType.equals("component") || fileType.equals("folder")) {
            model.addAttribute("shareFlag", "1");
        } else {
            if (!shareResourceService.getShareFlagByDocId(fileId, fileType)) {
                model.addAttribute("shareFlag", "0");
            } else {
                model.addAttribute("shareFlag", "1");

            }
        }
        //封装传到分享链接页面的参数
        model.addAttribute("fileId", fileId);
        model.addAttribute("fileType", fileType);
        if ((fileName == null || fileName.equals("")) && fileType.equals("component")) {
            fileName = componentApplyService.getById(fileId).getComponentName();
        }
        model.addAttribute("fileName", fileName);
        model.addAttribute("isPersonCenter",false);
        model.addAttribute("shareUserConfig",shareUserConfig);
        return"/doc/front/preview/share_confirm.html";
}

    /**
     * @param hash 映射地址
     * @param pwd  用户输入的提取码
     * @return 是否正确
     * @author luzhanzaho
     * @date 2018-12-11
     * @decription 提取码验证
     */
    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String hash, String pwd) {
        //获取正确的提取码
        String rightPwd = shareResourceService.getPwdByHash(hash);
        //提取码验证
        if (rightPwd.equals(pwd)) {
            return "1";
        } else {
            return "提取码错误";
        }
    }

    @RequestMapping("/saveServerAddress")
    @ResponseBody
    public void saveServerAddress() {
        hussarCacheManager.setObject(CacheConstant.SERVER_ADDRESS,
                CacheConstant.PREX_SERVER_ADDRESS, null);
    }

    public String changeValid(Date validDate) {
        String lack = "";
        //跨年的情况会出现问题哦
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
        Calendar aCalendar = Calendar.getInstance();
        Long time1 = validDate.getTime();
        Long time2 = new Date().getTime();
        int day = (int) ((time1 - time2) / (1000 * 60 * 60 * 24));
        int hours = (int) ((time1 - time2) / (1000 * 60 * 60));
        if (day > 10000) {
            lack = "永久有效";
        } else if (day == 0 || day < 1) {

            lack = (hours + 1) + "小时后";
        } else {
            hours = hours - (day * 24);
            lack = day + "天" + (hours + 1) + "小时后";
        }
        return lack;
    }
}
