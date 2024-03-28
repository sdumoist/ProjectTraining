package com.jxdinfo.doc.manager.componentmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.MD5Util;
import com.jxdinfo.doc.common.util.MathUtil;
import com.jxdinfo.doc.common.util.StateUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApplyAttachment;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProjectComponent;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyAttachmentService;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectComponentService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * <p>
 * 组件提报控制层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
@Controller
@RequestMapping("/component")
public class ComponentApplyController {
    private static Logger logger = LogManager.getLogger(ComponentApplyController.class);

    /**
     * es搜索 服务类
     */
    @Autowired
    private ESUtil esUtil;

    /**
     * 组件项目复用服务类
     */
    @Resource
    private MultiplexProjectService multiplexProjectService;

    /**
     * 组件服务类
     */
    @Resource
    private ComponentApplyService componentApplyService;

    /**
     * 组件附件服务类
     */
    @Resource
    private ComponentApplyAttachmentService componentApplyAttachmentService;

    /**
     * 组件项目复用服务类
     */
    @Resource
    private MultiplexProjectComponentService multiplexProjectComponentService;

    /**
     * 文件系统-文件 服务类
     */
    @Autowired
    private FsFileService fsFileService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;
    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;

    /**
     * 文件工具类
     */
    @Autowired
    private FileTool fileTool;

    /**
     * 缓存工具类
     */
    @Autowired
    private CacheToolService cacheToolService;


    /**
     * 用户接口
     */
    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * 用户业务层
     */
    @Resource
    private ISysUsersService sysUsersService;
    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;

    /**
     * 文件基础业务层
     */
    @Resource
    private DocInfoService docInfoService;


    /**
     * 临时文件夹
     */
    @Value("${docbase.filedir}")
    private String tempdir;

    /**
     * 打开成果提报页面
     */
    @GetMapping("/componentListApplyView")
    public String componentListApply() {
        return "/doc/manager/componentmanager/component-list-apply.html";
    }



    @GetMapping("/componentPersonage")
    public String componentPersonage(String userName,Model model,String style) {
        userName =XSSUtil.xss(userName);
        style =XSSUtil.xss(style);
        model.addAttribute("userName",userName);
        model.addAttribute("style",style);
        return"/doc/front/personalcenter/component.html" ;
    }

    /**
     * 打开成果汇总页面
     */
    @GetMapping("/componentListByALlView")
    @RequiresPermissions("component:componentListByALlView")
    public String componentListByALl() {
        return "/doc/manager/componentmanager/component-list-all.html";
    }

    /**
     * 打开成果预审页面
     */
    @GetMapping("/componentListByDeptView")
    @RequiresPermissions("component:componentListByDeptView")
    public String componentListByDept() {
        return "/doc/manager/componentmanager/component-list-dept.html";
    }

    /**
     * 打开成果统计页面
     */
    @GetMapping("/componentGraphCountView")
    public String componentGraphCountView() {
        return "/doc/manager/componentmanager/component-count.html";
    }

    /**
     * 打开成果统计页面
     */
    @GetMapping("/componentGraphCountViewRear")
    @RequiresPermissions("component:componentGraphCountView")
    public String componentGraphCountViewRear() {
        return "/doc/manager/componentmanager/component-count-rear.html";
    }

    /**
     * 打开成果认定页面
     */
    @GetMapping("/componentListByWYHView")
    @RequiresPermissions("component:componentListByWYHView")
    public String componentListByWYH() {
        return "/doc/manager/componentmanager/component-list-wyh.html";
    }

    /**
     * 科研成果表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页信息数量
     * @param componentType   组件类型
     * @param componentState  组件状态
     * @param componentOrigin 组件来源
     * @param componentRange  应用场景
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/componentList")
    @ResponseBody
    public JSON getTopicList(String componentName, int page, int limit, Integer componentType,
                             Integer componentState, Integer componentOrigin, String componentRange, String deptName) {
        int beginIndex = page * limit - limit;
        //开始位置
        String userId = ShiroKit.getUser().getId();
        String orgId = ShiroKit.getUser().getDeptId();
        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentList(componentNameStr, componentType,
                componentState, beginIndex, limit, componentOrigin, userId, orgId, componentRange, deptName, null);
        int bannerCount = componentApplyService.componentListCount(componentNameStr, componentType,
                componentState, componentOrigin, userId, orgId, componentRange, deptName, null);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 根据ID获取组件
     *
     * @param id 组件ID
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/getComponent")
    @ResponseBody
    public JSON getComponent(String id) {
        ComponentApply componentApply = componentApplyService.getById(id);
        JSONObject json = new JSONObject();
        json.put("componentApply", componentApply);
        return json;
    }

    /**
     * 表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页数据
     * @param componentType   组件类型
     * @param componentState  组件状态
     * @param componentOrigin 组件来源
     * @param componentRange  组件范围
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/componentListByApply")
    @ResponseBody
    public JSON componentListByApply(String componentName, @RequestParam(value = "page", defaultValue = "1")
            int page, @RequestParam(value = "limit", defaultValue = "10") int limit, Integer componentType,
                                     Integer componentState, Integer componentOrigin, String componentRange, String stateStr) {
        String[] states = null;
        if (stateStr == null || stateStr.equals("")) {
            states = null;
        } else {
            states = stateStr.split(",");
        }

        int beginIndex = page * limit - limit;
        //开始位置
        String userId = null;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户

        userId = ShiroKit.getUser().getId();

        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentListStates(componentNameStr, componentType,
                componentState, beginIndex, limit, componentOrigin, userId, null, componentRange, states, null,null);
        int bannerCount = componentApplyService.componentListStatesCount(componentNameStr, componentType,
                componentState, componentOrigin, userId, null, componentRange, states,null);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 成果预审页面表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页数据
     * @param componentType   组件类型
     * @param componentState  组件状态
     * @param componentOrigin 组件来源
     * @param componentRange  组件范围
     * @return JSON
     * @Author yjs
     */
    @GetMapping("/componentListByDept")
    @ResponseBody
    public JSON componentListByDept(String componentName, int page, int limit, Integer componentType,
                                    Integer componentState, Integer componentOrigin, String componentRange, String deptName) {
        int beginIndex = page * limit - limit;
        //开始位置

        String orgId = ShiroKit.getUser().getDeptId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag == 1) {
            orgId = null;
        }
        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentList(componentNameStr, componentType,
                0, beginIndex, limit, componentOrigin, null, orgId, componentRange, deptName, null);
        int bannerCount = componentApplyService.componentListCount(componentNameStr, componentType, 0,
                componentOrigin, null, orgId, componentRange, deptName, null);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 成果认定页面列表表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页数量
     * @param componentType   组件类型
     * @param componentState  组件类型
     * @param componentOrigin 组件来源
     * @param componentRange  组件范围
     * @return JSON
     * @Author yjs
     */
    @GetMapping("/componentListByWYH")
    @ResponseBody
    public JSON componentListByWYH(String componentName, int page, int limit, Integer componentType,
                                   Integer componentState, Integer componentOrigin, String componentRange, String deptName) {
        int beginIndex = page * limit - limit;
        //开始位置
        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentList(componentNameStr, componentType,
                1, beginIndex, limit, componentOrigin, null, null, componentRange, deptName, null);
        int bannerCount = componentApplyService.componentListCount(componentNameStr, componentType, 1,
                componentOrigin, null, null, componentRange, deptName, null);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 成果汇总页面表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页数量
     * @param componentType   组件类型
     * @param componentState  组件类型
     * @param componentOrigin 组件来源
     * @param componentRange  组件范围
     * @return JSON
     * @Author yjs
     */
    @RequestMapping("/componentListByAll")
    @ResponseBody
    public JSON componentListByAll(String componentName, int page, int limit, Integer componentType,
                                   Integer componentState, Integer componentOrigin, String componentRange, String deptName, String awardType) {
        int beginIndex = page * limit - limit;
        //开始位置
        String userId = ShiroKit.getUser().getId();
        String orgId = ShiroKit.getUser().getDeptId();
        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentList(componentNameStr, componentType,
                componentState, beginIndex, limit, componentOrigin, null, null, componentRange, deptName, awardType);
        int bannerCount = componentApplyService.componentListCount(componentNameStr, componentType, componentState,
                componentOrigin, null, null, componentRange, deptName, awardType);
        JSONObject json = new JSONObject();
        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 个人中心成果提报页面
     *
     * @Author yjs
     */
    @GetMapping("/componentApply")
    public String componentApply(Model model, String style,String origin,String state,String type,String dept ) {
        style= XSSUtil.xss(style);
        origin= XSSUtil.xss(origin);
        state= XSSUtil.xss(state);
        dept= XSSUtil.xss(dept);
        type= XSSUtil.xss(type);
        if (style != null) {
            model.addAttribute("style", style);
        } else {
            model.addAttribute("style", "");
        }
        if (origin != null) {
            model.addAttribute("origin", origin);
        } else {
            model.addAttribute("origin", "");
        }
        if (state != null) {
            model.addAttribute("state", state);
        } else {
            model.addAttribute("state", "");
        }
        if (dept != null) {
            model.addAttribute("dept", dept);
        } else {
            model.addAttribute("dept", "");
        }
        if (type != null) {
            model.addAttribute("type", type);
        } else {
            model.addAttribute("type", "");
        }
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("componentId", UUID.randomUUID().toString().replaceAll("-", ""));
        return "/doc/manager/componentmanager/componentApply.html";
    }

    /**
     * 成果汇总页面点击列表详情进入的页面
     *
     * @param model
     * @param componentId
     * @return
     * @Author yjs
     */
    @GetMapping("/componentView")
    public String componentView(Model model, String componentId, String type) {
        ComponentApply componentApply = componentApplyService.getById(componentId);
        String userId = componentApply.getUserId();
      String  componentState=Integer.toString(componentApply.getComponentState());
        SysUsers sysUsers = sysUsersService.getById(userId);
        SysStru sysStru = null;
        if(sysUsers !=null){
             sysStru = sysStruMapper.selectById(sysUsers.getDepartmentId());
        }else{
             sysStru = new SysStru();
            sysStru.setOrganAlias("金现代");
            sysStru.setStruType("3");
        }
        if (type != null && type.equals("share")) {
            model.addAttribute("isShare", "1");
            model.addAttribute("adminFlag", 0);
            model.addAttribute("orgId", "");
        } else if (type != null && type.equals("shareMobile")) {
            model.addAttribute("isShare", "2");
            model.addAttribute("adminFlag", 0);
            model.addAttribute("orgId", "");
        } else {
            model.addAttribute("userName", ShiroKit.getUser().getName());
            model.addAttribute("isShare", "0");
            List<String> roleList = ShiroKit.getUser().getRolesList();

            Integer adminFlag = CommonUtil.getWYHFlag(roleList);
            model.addAttribute("adminFlag", adminFlag);
            String orgId = ShiroKit.getUser().getDeptId();
            String orgType = sysStru.getStruType();
            List<String> listNew = new ArrayList<>();
            if (!orgType .equals("5")) {
                listNew.add(orgId);
                model.addAttribute("orgId", listNew);

            }else{
                List<Map<String, Object>> sysStruList = sysStruMapper.getOrgListByParentId(orgId);
                for( int i=0;i<sysStruList.size();i++){
                    listNew.add(sysStruList.get(i).get("STRUID")+"");

                }
                model.addAttribute("orgId", listNew);
            }
        }
        model.addAttribute("componentId",componentId);
        model.addAttribute("componentState",componentState);

        model.addAttribute("deptName", sysStru.getOrganAlias());
        if(sysUsers !=null) {
            model.addAttribute("deptId", sysUsers.getDepartmentId());
        }else{
            model.addAttribute("deptId", "");
        }
        model.addAttribute("componentApply", componentApply);
        model.addAttribute("readNum", cacheToolService.getComponentReadNum(componentId)+1);
        Timestamp datestr = componentApply.getCreateTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = formatter.format(datestr);
        model.addAttribute("componentTime", str);
        List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(componentId);
        List<MultiplexProject> multiplexList=  multiplexProjectService.componentMultiplexList(componentId);
        int listSize = 0;
        if (list.size() != 0) {
            listSize = list.size();
        }
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag1 = CommonUtil.getZTFlag(roleList);
        model.addAttribute("adminFlag1", adminFlag1);
        model.addAttribute("listSize", listSize);
        model.addAttribute("componentList", list);
        model.addAttribute("multiplexList",multiplexList);
        int multiplexListSize = 0;
        if (multiplexList.size() != 0) {
            multiplexListSize = multiplexList.size();
        }
        model.addAttribute("multiplexListSize", multiplexListSize);
        return "/doc/manager/componentmanager/component-view-from.html";
    }

    /**
     * 附件上传 选择文件后进入该方法
     *
     * @param file        文件
     * @param componentId 组件名称
     * @return String 返回值
     * @Author yjs
     */
    @PostMapping("/componentApplySave")
    @ResponseBody
    public String componentApplySave(@RequestParam("file") MultipartFile file, String componentId) {
        ByteArrayOutputStream baos = null;
        String fileName = file.getOriginalFilename();
        String userId = ShiroKit.getUser().getId();
        try {
            baos = fileTool.cloneInputStream(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream md5InputStream = new ByteArrayInputStream(baos.toByteArray());
        //转MD5
        String md5 = MD5Util.getFileMD5(md5InputStream);
        String json = "";
        int pointIndex = fileName.lastIndexOf(".");
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        // 后缀名
        String suffix = fileName.substring(pointIndex + 1).toLowerCase();
        if (fileName.length() > 64) {
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMELONG.getValue());
            resultMap.put("title", fileName);
            return JSON.toJSONString(resultMap);
        }
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", DocConstant.UPLOADRESULT.NAMEERROR.getValue());
            resultMap.put("title", fileName);
            return JSON.toJSONString(resultMap);
        }
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("code", DocConstant.UPLOADRESULT.ERRORTYPE.getValue());
            resultMap.put("title", fileName);
            return JSON.toJSONString(resultMap);
        }
        suffix = fileName.substring(pointIndex).toLowerCase();

        // 截取文件名的后缀名

        try {
            String random = UUID.randomUUID().toString().replace("-", "");
            File outputFile = new File(tempdir + File.separator + random + suffix);

            try {
                if (!outputFile.getParentFile().exists()) {
                    // 路径不存在,创建
                    outputFile.getParentFile().mkdirs();
                }
                outputFile.createNewFile();
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                json = JSONObject.toJSONString(resultMap);
                return json;
            }
            String idStr;

            FileUtils.copyInputStreamToFile(file.getInputStream(), outputFile);
            List<FsFile> listMd5 = fsFileService.getInfoByMd5(md5);
            if (listMd5 != null && listMd5.size() != 0) {
                FsFile fsFile = listMd5.get(0);

                //秒传
                cacheToolService.updateLevelCodeCache(userId);
                String docId = filesService.uploadFastComponent(componentId, "0", "0", null, null,
                        null, md5, fileName, fsFile, fsFile.getFileSize(), "1", userId, null);
                if (docId == null) {
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                    resultMap.put("title", fileName);
                    json = JSONObject.toJSONString(resultMap);
                    return json;
                }

                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(docId, map);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FASTUPLOAD.getValue());
                resultMap.put("docId", docId);
                resultMap.put("title", fileName);
                json = JSONObject.toJSONString(resultMap);

                return json;
            }

            try {
                String contentType = getContentType(suffix.toLowerCase());

                // 获取当前登录用户的用户名信息

                // 获取上传文档的当前时间
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                DocInfo docInfo = new DocInfo();
                String docId = UUID.randomUUID().toString().replace("-", "");
                idStr = docId;
                docInfo.setDocId(docId);
                //doc_info表中的doc_id，fs_file表中的file_id和索引的id保持一致
                docInfo.setFileId(outputFile.getName());
                docInfo.setUserId(userId);
                docInfo.setAuthorId(userId);
                docInfo.setContactsId(userId);
                docInfo.setCreateTime(ts);

                docInfo.setFoldId(componentId);
                docInfo.setDocType(suffix);
                docInfo.setTitle(fileName.substring(0, fileName.lastIndexOf(".")));
                docInfo.setReadNum(0);
                docInfo.setDownloadNum(0);
                docInfo.setValidFlag("0");
                docInfo.setAuthority("1");
                docInfo.setSetAuthority("0");
                docInfo.setVisibleRange(Integer.parseInt("1"));
                //ValidFlag 默认1 有效
                docInfo.setValidFlag("0");
                // shareFlag 默认1 可分享
                docInfo.setShareFlag("0");
                FsFile fileModel = new FsFile();
                fileModel.setCreateTime(ts);
                fileModel.setFileIcon("");
                fileModel.setFileId(docId);
                //复制出两个输入流

                fileModel.setMd5(md5);
                fileModel.setFileName(fileName.substring(0, fileName.lastIndexOf(".")));
                //大小保留2位小数
                double size = MathUtil.getDecimal(outputFile.length() / 1024, 2);
                fileModel.setFileSize(size + "KB");
//                Map<String, Object> deptSpaceIsFreeMap = filesService.checkDeptSpace(StringUtil.getString(size));
//
//                Boolean deptSpaceIsFree = StringUtil.getBoolean(deptSpaceIsFreeMap.get("flag"));
//                if (!deptSpaceIsFree) {
//                    //部门存储空间不足
//                    Map<String, String> resultMap = new HashMap<>();
//                    resultMap.put("code", DocConstant.UPLOADRESULT.NOSPACE.getValue());
//                    return JSON.toJSONString(resultMap);
//                }
                fileModel.setFileType(suffix);

                //拼装操作历史记录
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(docId);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(0);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(0);
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                //拼装权限信息
                List<DocFileAuthority> list = new ArrayList<>();

                List<String> indexList = new ArrayList<>();
                //0代表是完全公开 ，这时候往索引里面添加一个公开的权限
                String[] groupArr = "allpersonflag".split(",");
                String[] authorTypeStrGroup = "3".split(",");
                String[] operateTypeStrGroup = "1".split(",");
                for (int i = 0; i < groupArr.length; i++) {
                    DocFileAuthority docFileAuthority = new DocFileAuthority();
                    docFileAuthority.setFileAuthorityId(UUID.randomUUID().toString().replaceAll("-", ""));
                    docFileAuthority.setAuthorId(groupArr[i]);
                    //操作者类型（0：userID,1:groupID,2:roleID）
                    docFileAuthority.setAuthorType(StringUtil.getInteger(authorTypeStrGroup[i]));
                    docFileAuthority.setFileId(docId);
                    docFileAuthority.setAuthority(StringUtil.getInteger(operateTypeStrGroup[i]));
                    list.add(docFileAuthority);
                    indexList.add(groupArr[i]);
                }
                indexList.add(userId);

                cacheToolService.updateLevelCodeCache(userId);
                filesService.uploadFile(outputFile, docInfo, fileModel, resInfoList, list, indexList, contentType);
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(docId, map);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
                resultMap.put("docId", docId);
                resultMap.put("title", fileName);
                json = JSONObject.toJSONString(resultMap);
                return json;
            } catch (IOException e) {
                logger.error("IO Exception：", e);
                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("code", DocConstant.UPLOADRESULT.FAIL.getValue());
                resultMap.put("title", fileName);

                json = JSONObject.toJSONString(resultMap);
                return json;
            }
        } catch (Exception e) {
            logger.error("IO Exception：", e);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", DocConstant.UPLOADRESULT.SUCCESS.getValue());
        return json;
    }

    private String getContentType(String suffix) {
        String contentType = null;
        if (suffix.equals(".doc") || suffix.equals(".docx")) {
            contentType = "application/msword";
            return contentType;
        } else if (suffix.equals(".ppt") || suffix.equals(".pptx")) {
            contentType = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            return contentType;
        } else if (suffix.equals(".xls") || suffix.equals(".xlsx")) {
            contentType = "spreadsheetml";
            return contentType;
        } else if (suffix.equals(".png") || suffix.equals(".gif") || suffix.equals(".jpg") ||
                suffix.equals(".bmp")) {
            contentType = "image";
            return contentType;
        } else if (suffix.equals(".txt")) {
            contentType = "text/plain";
            return contentType;
        } else if (suffix.equals(".pdf")) {
            contentType = "application/pdf";
            return contentType;
        } else if (suffix.equals(".mp3")) {
            contentType = "audio/mp3";
            return contentType;
        } else if (suffix.equals(".mp4")) {
            contentType = "video/mp4";
            return contentType;
        } else if (suffix.equals(".wav")) {
            contentType = "audio/wav";
            return contentType;
        } else if (suffix.equals(".avi")) {
            contentType = "video/avi";
            return contentType;
        } else if (suffix.equals(".ceb")) {
            contentType = "ceb";
            return contentType;
        } else {
            return null;
        }
    }

    /**
     * 成果修改
     */
    @GetMapping("/componentApplyUpdate")
    public String componentApplyUpdate(Model model, String componentId,String style,String origin,String state,String type,String dept ) {
        style= XSSUtil.xss(style);
        componentId= XSSUtil.xss(componentId);
        origin= XSSUtil.xss(origin);
        state= XSSUtil.xss(state);
        dept= XSSUtil.xss(dept);
        type= XSSUtil.xss(type);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getZTFlag(roleList);
        model.addAttribute("adminFlag", adminFlag);
        if (style != null) {
            model.addAttribute("style", style);
        } else {
            model.addAttribute("style", "");
        }
        if (dept != null) {
            model.addAttribute("dept", dept);
        } else {
            model.addAttribute("dept", "");
        }
        if (origin != null) {
            model.addAttribute("origin", origin);
        } else {
            model.addAttribute("origin", "");
        }
        if (state != null) {
            model.addAttribute("state", state);
        } else {
            model.addAttribute("state", "");
        }
        if (type != null) {
            model.addAttribute("type", type);
        } else {
            model.addAttribute("type", "");
        }
        ComponentApply componentApply = componentApplyService.getById(componentId);
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("componentId", componentId);
        model.addAttribute("componentState", componentApply.getComponentState());
        model.addAttribute("componentApply", componentApply);
        List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(componentId);

        model.addAttribute("componentList", list);
        return "/doc/manager/componentmanager/componentApplyUpdate.html";
    }

    /**
     * 科研成果提报
     *
     * @param componentApply 组件对象
     * @param idArrStr       文档ID集合
     * @param idArrStr       文档名称集合
     * @param idArrStr       文档类型集合
     * @return 新增结果
     */
    @PostMapping("/componentSave")
    @ResponseBody
    public JSON addTopic(ComponentApply componentApply, String idArrStr, String nameArrStr, String typeArrStr) {
        //专题ID
        componentApply.setDeptId(ShiroKit.getUser().getDeptId());
        componentApply.setDeptName(ShiroKit.getUser().getDeptName());
        componentApply.setUserId(ShiroKit.getUser().getId());
        componentApply.setComponentState(DocConstant.NUMBER.ZERO.getValue());
        componentApply.setUserName(ShiroKit.getUser().getName());
        if (componentApply.getComponentDescText() != null && !"".equals(componentApply.getComponentDescText())) {
            componentApply.setComponentDescText(componentApply.getComponentDescText().trim());
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        componentApply.setCreateTime(ts);
        componentApplyService.save(componentApply);
        JSONObject json = new JSONObject();
        if (!idArrStr.equals("")) {

            String[] idArr = idArrStr.split(",");
            String[] nameArr = nameArrStr.split(",");
            String[] typeArr = typeArrStr.split(",");
            for (int i = 0; i < idArr.length; i++) {
                ComponentApplyAttachment componentApplyAttachment = new ComponentApplyAttachment();
                componentApplyAttachment.setAttachmentId(idArr[i]);
                componentApplyAttachment.setAttachmentName(nameArr[i]);
                componentApplyAttachment.setAttachmentType(typeArr[i]);
                componentApplyAttachment.setComponentId(componentApply.getComponentId());
                componentApplyAttachmentService.save(componentApplyAttachment);
            }

        }
        json.put("result", "1");
        return json;

    }

    /**
     * 成果预审列表详情页面
     *
     * @param model
     * @param componentId 组件ID
     * @return String
     * @Author yjs
     */
    @GetMapping("/componentAudit")
    public String componentAudit(Model model, String componentId) {
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("componentId", componentId);
        ComponentApply componentApply = componentApplyService.getById(componentId);
        model.addAttribute("componentApply", componentApply);

        List<ComponentApplyAttachment> list = componentApplyAttachmentService.list(new QueryWrapper<ComponentApplyAttachment>()
                .eq("component_id", componentId).orderBy(true, true, "attachment_type"));
        model.addAttribute("componentList", list);
        return "/doc/manager/componentmanager/component-audit.html";
    }

    /**
     * 成果认定列表详情页面
     *
     * @param model
     * @param componentId 组件ID
     * @return
     * @Author yjs
     */
    @GetMapping("/componentCognizance")
    public String componentCognizance(Model model, String componentId) {
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("componentId", componentId);
        ComponentApply componentApply = componentApplyService.getById(componentId);
        model.addAttribute("componentApply", componentApply);

        List<ComponentApplyAttachment> list = componentApplyAttachmentService.list(new QueryWrapper
                <ComponentApplyAttachment>()
                .eq("component_id", componentId));
        model.addAttribute("componentList", list);
        return "/doc/manager/componentmanager/component-cognizance.html";
    }

    /**
     * 成果通过/认定
     *
     * @param componentId 文档ID
     * @return 新增结果
     * @Author yjs
     */
    @PostMapping("/componentPass")
    @ResponseBody

    public JSON componentPass(String componentId,String componentState) {

        JSONObject json = new JSONObject();
        ComponentApply componentApply = componentApplyService.getById(componentId);
        Integer i=null;
        if(componentState!=null){

            i = Integer.valueOf(componentState);
            if (componentApply.getComponentState() != i){
                if (componentApply.getComponentState()==1){
                    json.put("result", "3");
                    return json;
                }else if (componentApply.getComponentState()==2){
                    json.put("result", "4");
                    return json;
                }else if (componentApply.getComponentState()==3){
                    json.put("result", "5");
                    return json;
                }else {
                    return json;
                }
            }
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        componentApply.setPublishTime(ts);
        componentApply.setComponentState(StateUtil.pass(componentApply.getComponentState()));
        componentApplyService.updateById(componentApply);
        if (componentApply.getComponentState() == DocConstant.NUMBER.TWO.getValue()) {
            DocES docVO = new DocES();
            //如果是图片则在这个地方存入文件类型信息
            docVO.setId(componentApply.getComponentId());
            docVO.setReadType("2");
            docVO.setTags(componentApply.getTags());
            docVO.setTitle(componentApply.getComponentName());
            docVO.setRecycle("1");
            docVO.setUpDate(new Date());
            List<String> indexList = new ArrayList<>();
            indexList.add("allpersonflag");
            indexList.add(ShiroKit.getUser().getId());
            docVO.setContentType("component");
            docVO.setContent(componentApply.getComponentDescText());
            docVO.setPermission(indexList.toArray(new String[indexList.size()]));
            List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(
                    componentApply.getComponentId());
            for (int j = 0; j < list.size(); j++) {
                DocInfo docInfo = docInfoService.getDocDetail(list.get(j).getAttachmentId());
                docInfo.setValidFlag("1");
                docInfo.setShareFlag("1");
                docInfoService.updateById(docInfo);
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.VALID.getValue());
                esUtil.updateIndex(list.get(j).getAttachmentId(), map);
            }
            try {
                esService.createESIndex(docVO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        json.put("result", "1");
        return json;
    }

    /**
     * 点击成果预审/成果认定按钮，实现多个成果预审,认定
     *
     * @param componentId 文档ID
     * @return 新增结果
     * @Author yjs
     */
    @PostMapping("/componentPassPlus")
    @ResponseBody
    public JSON componentPassPlus(String componentId, String type) {

        String[] ids = componentId.split(",");
        JSONObject json = new JSONObject();
        ComponentApply componentApply = new ComponentApply();
        if (type != null && "2".equals(type)) {
            componentApply.setComponentState(DocConstant.NUMBER.TWO.getValue());
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            componentApply.setPublishTime(ts);
        } else {
            componentApply.setComponentState(DocConstant.NUMBER.ONE.getValue());
        }
        QueryWrapper<ComponentApply> wrapper = new QueryWrapper<ComponentApply>();
        wrapper.in("component_id", ids);
        componentApplyService.update(componentApply, wrapper);
//        componentApplyService.update(componentApply,componentId);
        //  ComponentApply componentApply = componentApplyService.selectBatchIds(componentId);

        //   componentApply.setComponentState(StateUtil.pass(componentApply.getComponentState()));
        //  componentApplyService.updateById(componentApply);
        for (int i = 0; i < ids.length; i++) {

            ComponentApply componentApplyNew = componentApplyService.getById(ids[i]);

            if (type != null && "2".equals(type)) {
                DocES docVO = new DocES();
                //如果是图片则在这个地方存入文件类型信息
                docVO.setId(componentApplyNew.getComponentId());
                docVO.setReadType("2");
                docVO.setTitle(componentApplyNew.getComponentName());
                docVO.setRecycle("1");
                docVO.setTags(componentApplyNew.getTags());
                docVO.setUpDate(new Date());
                List<String> indexList = new ArrayList<>();
                indexList.add("allpersonflag");
                indexList.add(ShiroKit.getUser().getId());
                docVO.setContent(componentApplyNew.getComponentDescText());
                docVO.setContentType("component");
                docVO.setPermission(indexList.toArray(new String[indexList.size()]));
                List<ComponentApplyAttachment> list = componentApplyAttachmentService.
                        getAttachmentList(componentApplyNew.getComponentId());
                for (int j = 0; j < list.size(); j++) {
                    DocInfo docInfo = docInfoService.getDocDetail(list.get(j).getAttachmentId());
                    docInfo.setValidFlag("1");
                    docInfo.setShareFlag("1");
                    docInfoService.updateById(docInfo);
                    Map map = new HashMap(1);
                    //0为无效，1为有效
                    map.put("recycle", DocConstant.VALIDTYPE.VALID.getValue());
                    esUtil.updateIndex(list.get(j).getAttachmentId(), map);
                }
                try {
                    esService.createESIndex(docVO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        json.put("result", "1");
        return json;
    }


    /**
     * 点击驳回按钮，进入该方法
     *
     * @param componentId 成果ID
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/componentBack")
    @ResponseBody
    public JSON componentBack(String componentId, String reason,String componentState) {
        JSONObject json = new JSONObject();
        ComponentApply componentApply = componentApplyService.getById(componentId);
        Integer i=null;
        if(componentState!=null){

            i = Integer.valueOf(componentState);
            if (componentApply.getComponentState() != i){

                if (componentApply.getComponentState()==3){
                    json.put("result", "3");
                    return json;
                }else{
                    return json;
                }
            }
        }
        if (componentApply.getComponentState() == DocConstant.NUMBER.ZERO.getValue()) {
            componentApply.setReturnReasons(reason);
            componentApply.setReturnUserId(ShiroKit.getUser().getName());
            componentApply.setReturnDeptName(ShiroKit.getUser().getDeptName());
            componentApply.setReturnReasonsWyh("");
            componentApply.setWhyUserId("");
            componentApply.setWyhDeptName("");
        }
        if (componentApply.getComponentState() == DocConstant.NUMBER.ONE.getValue()) {
            componentApply.setReturnReasons("");
            componentApply.setReturnUserId("");
            componentApply.setReturnDeptName("");
            componentApply.setReturnReasonsWyh(reason);
            componentApply.setWhyUserId(ShiroKit.getUser().getName());
            componentApply.setWyhDeptName(ShiroKit.getUser().getDeptName());
        }
        if (componentApply.getComponentState() == DocConstant.NUMBER.TWO.getValue()) {
            componentApply.setReturnReasonsWyh(reason);
            componentApply.setReturnReasons("");
            componentApply.setReturnDeptName("");
            componentApply.setReturnUserId("");
            componentApply.setWhyUserId(ShiroKit.getUser().getName());
            componentApply.setWyhDeptName(ShiroKit.getUser().getDeptName());
            List<ComponentApplyAttachment> list = componentApplyAttachmentService.list(new
                    QueryWrapper<ComponentApplyAttachment>()
                    .eq("component_id", componentApply.getComponentId()));
            for (int j = 0; j < list.size(); j++) {
                DocInfo docInfo = docInfoService.getDocDetail(list.get(j).getAttachmentId());
                docInfo.setValidFlag("0");
                docInfo.setShareFlag("0");
                docInfoService.updateById(docInfo);
                Map map = new HashMap(0);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(list.get(j).getAttachmentId(), map);
            }
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex( componentApply.getComponentId(), map);


        }
        componentApply.setComponentState(DocConstant.NUMBER.THREE.getValue());
        componentApplyService.updateById(componentApply);
        json.put("result", "1");
        return json;
    }

    /**
     * 发布（暂时不用）
     *
     * @param componentId 成果ID
     * @return
     * @Author yjs
     */
    @PostMapping("/componentPublish")
    @ResponseBody
    public JSON componentPublish(String componentId) {

        JSONObject json = new JSONObject();
        ComponentApply componentApply = componentApplyService.getById(componentId);
        List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(componentId);
        for (int i = 0; i < list.size(); i++) {
            DocInfo docInfo = docInfoService.getDocDetail(list.get(i).getAttachmentId());
            docInfo.setValidFlag("1");
            docInfoService.updateById(docInfo);
            Map map = new HashMap(1);
            //0为无效，1为有效
            map.put("recycle", DocConstant.VALIDTYPE.VALID.getValue());
            esUtil.updateIndex(list.get(i).getAttachmentId(), map);
        }
        componentApply.setComponentState(DocConstant.NUMBER.THREE.getValue());
        componentApplyService.updateById(componentApply);
        json.put("result", "1");
        DocES docVO = new DocES();
        //如果是图片则在这个地方存入文件类型信息
        docVO.setId(componentApply.getComponentId());
        docVO.setReadType("2");
        docVO.setTitle(componentApply.getComponentName());
        docVO.setRecycle("1");
        docVO.setUpDate(new Date());
        List<String> indexList = new ArrayList<>();
        indexList.add("allpersonflag");
        indexList.add(ShiroKit.getUser().getId());
        docVO.setContent(componentApply.getComponentDesc());
        docVO.setPermission(indexList.toArray(new String[indexList.size()]));
        try {
            esService.createESIndex(docVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 批量删除科研成果
     *
     * @param componentIds 文档Id数组
     * @return
     * @Author yjs
     */
    @PostMapping(value = "/deleteScope")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public int deleteScope(@RequestParam String componentIds) {
        String[] ids = componentIds.split(",");

        for (int i = 0; i < ids.length; i++) {
            List<ComponentApplyAttachment> list = componentApplyAttachmentService.list(new
                    QueryWrapper<ComponentApplyAttachment>()
                    .eq("component_id", ids[i]));
            for (int j = 0; j < list.size(); j++) {
                DocInfo docInfo = docInfoService.getDocDetail(list.get(j).getAttachmentId());
                docInfo.setValidFlag("0");
                docInfo.setShareFlag("0");
                docInfoService.updateById(docInfo);
                Map map = new HashMap(0);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(list.get(j).getAttachmentId(), map);
            }
            ComponentApply componentApply = componentApplyService.getById(ids[i]);
            if (componentApply.getComponentState() == 2) {
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(ids[i], map);
            }
        }
        QueryWrapper<ComponentApplyAttachment> wrapper2 = new QueryWrapper<ComponentApplyAttachment>();
        wrapper2.in("component_id", ids);
        boolean flag2 = componentApplyAttachmentService.remove(wrapper2);

        QueryWrapper<MultiplexProjectComponent> wrapper3 = new QueryWrapper<MultiplexProjectComponent>();
        wrapper3.in("component_id", ids);
        boolean flag3 = multiplexProjectComponentService.remove(wrapper3);
        QueryWrapper<ComponentApply> wrapper = new QueryWrapper<ComponentApply>();
        wrapper.in("component_id", ids);
        boolean flag = componentApplyService.remove(wrapper);
        int num;
        if (flag && flag2 && flag3) {
            num = 1;
        } else {
            num = 0;
        }

        return num;
    }

    /**
     * 修改成果
     *
     * @param componentApply 专题对象
     * @param idArrStr       文档ID集合
     * @param nameArrStr     文档姓名集合
     * @param typeArrStr     文档类型集合
     * @return 新增结果
     * @Author yjs
     */
    @PostMapping("/componentUpdate")
    @ResponseBody
    public JSON addTopicUpdate(ComponentApply componentApply, String idArrStr, String nameArrStr, String typeArrStr) {
        if (componentApply.getComponentDescText() != null && !"".equals(componentApply.getComponentDescText())) {
            componentApply.setComponentDescText(componentApply.getComponentDescText().trim());
        }
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        componentApply.setUpdate_time(ts);

        JSONObject json = new JSONObject();
        if (componentApply.getComponentState() == DocConstant.NUMBER.THREE.getValue()) {
            componentApply.setComponentState(DocConstant.NUMBER.ZERO.getValue());
        }
        componentApplyService.updateById(componentApply);
        if (componentApply.getComponentState() == DocConstant.NUMBER.TWO.getValue()) {

            List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(
                    componentApply.getComponentId());
            for (ComponentApplyAttachment doc : list) {
                DocInfo docInfo = docInfoService.getDocDetail(doc.getAttachmentId());
                docInfo.setValidFlag("0");
                docInfo.setShareFlag("0");
                docInfoService.updateById(docInfo);
                Map map = new HashMap(0);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                esUtil.updateIndex(doc.getAttachmentId(), map);
            }
        }
        componentApplyAttachmentService.remove(new QueryWrapper<ComponentApplyAttachment>().eq("component_id",
                componentApply.getComponentId()));
        if (!idArrStr.equals("")) {
            String[] idArr = idArrStr.split(",");
            String[] nameArr = nameArrStr.split(",");
            String[] typeArr = typeArrStr.split(",");
            for (int i = 0; i < idArr.length; i++) {
                ComponentApplyAttachment componentApplyAttachment = new ComponentApplyAttachment();
                componentApplyAttachment.setAttachmentId(idArr[i]);
                componentApplyAttachment.setAttachmentName(nameArr[i]);
                componentApplyAttachment.setAttachmentType(typeArr[i]);
                componentApplyAttachment.setComponentId(componentApply.getComponentId());
                componentApplyAttachmentService.save(componentApplyAttachment);
                if (componentApply.getComponentState() == DocConstant.NUMBER.TWO.getValue()) {

                    DocInfo docInfo = docInfoService.getDocDetail(idArr[i]);
                    docInfo.setValidFlag("1");
                    docInfo.setShareFlag("1");
                    docInfoService.updateById(docInfo);
                    Map map = new HashMap(1);
                    //0为无效，1为有效
                    map.put("recycle", DocConstant.VALIDTYPE.VALID.getValue());
                    esUtil.updateIndex(idArr[i], map);


                }
            }

        }
        if (componentApply.getComponentState() == DocConstant.NUMBER.TWO.getValue()) {

            Map map = new HashMap(3);
            //0为无效，1为有效
            map.put("title", componentApply.getComponentName());
            map.put("content", componentApply.getComponentDescText());
            map.put("tags", componentApply.getTags());
            esUtil.updateIndex(componentApply.getComponentId(), map);
        }


        json.put("result", "1");
        return json;
    }

    /**
     * 统计个人贡献成果
     *
     * @return json
     */
    @GetMapping("/componentCount")
    @ResponseBody
    public Object componentCount() {
        JSONObject json = new JSONObject();
        List<Map> list = componentApplyService.componentCount();
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 统计部门贡献成果
     *
     * @return json
     */
    @GetMapping("/componentDeptCount")
    @ResponseBody
    public Object componentDeptCount() {
        JSONObject json = new JSONObject();
        List<Map> list = componentApplyService.componentDeptCount();
        json.put("data", list);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * @return json
     */
    @PostMapping("/componentGraphCount")
    @ResponseBody
    public Object componentGraphCount(String dateStart,String dateEnd,String date,Integer state, Integer bu,String order) {
        List<ComponentApply> listCom = new ArrayList<>();
        String str="";
        if(dateEnd!=null&&dateEnd!=""){
            str =dateEnd.concat(" 23:59:59");
        }

        if (bu == null || bu == 0) {
            listCom = componentApplyService.componentGraphCount(dateStart,str,date, state,order);
        /*if(bu==1){
            for(ComponentApply arr:listCom) {
                arr.setOrganAlias(arr.getOrganAliasBu());
            }
        }*/
        } else {
            listCom = componentApplyService.componentGraphCountBg(dateStart,str,date, state,order);
        }
        Map<String, Object> result = new HashMap<>(5);
        List<String> list = new ArrayList();
        int totalNum = 0;
        List<Integer> numList = new ArrayList();

        for (int i = 0; i < listCom.size(); i++) {
            int num = Integer.parseInt(listCom.get(i).getRe_num());
            totalNum = totalNum + num;
            list.add(listCom.get(i).getOrganAlias());
            numList.add(num);
        }
        result.put("total", listCom.size());
        result.put("totalNum", totalNum);
        result.put("rows", listCom);
        result.put("list", list);
        result.put("numList", numList);
        return result;
    }


    /**
     * 统计部门贡献成果
     *
     * @return json
     */
    @PostMapping("/componentTopCount")
    @ResponseBody
    public Object componentTopCount() {

        List<String> list = componentApplyService.componentTopCount();
        return list;
    }

    /**
     * 表格初始化
     *
     * @param componentName   组件名称
     * @param page            页数
     * @param limit           每页数据
     * @param componentType   组件类型
     * @param componentState  组件状态
     * @param componentOrigin 组件来源
     * @param componentRange  组件范围
     * @return JSON
     * @Author yjs
     */
    @PostMapping("/componentListFrontByAll")
    @ResponseBody
    public JSON componentListFrontByAll(String componentName,String userId,@RequestParam(value = "page", defaultValue = "1")
            int page, @RequestParam(value = "limit", defaultValue = "10") int limit, Integer componentType,
                                        Integer componentState, Integer componentOrigin, String componentRange,
                                        String stateStr, String timeStr,String deptStr) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        String[] states = null;
        String[] dept = null;
        if (stateStr == null || stateStr.equals("")) {
            states = null;
        } else {
            states = stateStr.split(",");
        }
        if (states == null) {
            states = new String[]{"0", "1", "2"};

        }
        if (deptStr == null || deptStr.equals("")) {
            dept = null;
        } else {
            dept = deptStr.split(",");
        }
        Integer order = 0;
        if (adminFlag == 3) {
            order = 0;
        } else {
            order = 1;
        }
        int beginIndex = page * limit - limit;
        //开始位置
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        String componentNameStr = StringUtil.transferSqlParam(componentName);
        List<ComponentApply> componentList = componentApplyService.componentListStates(componentNameStr, componentType,
                null, beginIndex, limit, componentOrigin, userId,null, componentRange, states, order,dept);
        int bannerCount = componentApplyService.componentListStatesCount(componentNameStr, componentType,
                null, componentOrigin,  userId, null, componentRange, states,dept);
        String orgId = ShiroKit.getUser().getDeptId();
        SysStru sysStru = sysStruMapper.selectById(orgId);
        String type = sysStru.getStruType();
        JSONObject json = new JSONObject();
        List<String> list = new ArrayList<>();
        if (!type .equals("5")) {
            list.add(orgId);
            json.put("orgId", list);
        }else{
           List<Map<String, Object>> sysStruList = sysStruMapper.getOrgListByParentId(orgId);
           for( int i=0;i<sysStruList.size();i++){
               list.add(sysStruList.get(i).get("STRUID")+"");

           }
           json.put("orgId", list);
        }


        json.put("count", bannerCount);
        json.put("data", componentList);
        json.put("adminFlag", adminFlag);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 缓存专题查看数据数据
     *
     * @author xubin
     * @date 2018-07-10 9:04
     */
    @PostMapping("/cacheViewNum")
    @ResponseBody
    public void saveCache(String componentId, String userName) {

        cacheToolService.getAndUpdateComponentReadNum(componentId);
    }

    /**
     * 成果汇总页面点击列表详情进入的页面
     *
     * @param model
     * @param componentId
     * @return
     * @Author yjs
     */
    @GetMapping("/componentViewShare")
    public String componentViewShare(Model model, String componentId, String type) {
        ComponentApply componentApply = componentApplyService.getById(componentId);
        String userId = componentApply.getUserId();
        SysUsers sysUsers = sysUsersService.getById(userId);
        SysStru sysStru = sysStruMapper.selectById(sysUsers.getDepartmentId());
        if (type != null && type.equals("share")) {
            model.addAttribute("isShare", "1");
            model.addAttribute("adminFlag", 0);
            model.addAttribute("adminFlag1", 0);
            model.addAttribute("orgId", "");
        } else if (type != null && type.equals("shareMobile")) {
            model.addAttribute("isShare", "2");
            model.addAttribute("adminFlag", 0);
            model.addAttribute("adminFlag1", 0);
            model.addAttribute("orgId", "");
        } else {
            model.addAttribute("userName", ShiroKit.getUser().getName());
            model.addAttribute("isShare", "0");
            List<String> roleList = ShiroKit.getUser().getRolesList();

            Integer adminFlag = CommonUtil.getWYHFlag(roleList);
            model.addAttribute("adminFlag", adminFlag);
            Integer adminFlag1 = CommonUtil.getZTFlag(roleList);
            model.addAttribute("adminFlag1", adminFlag1);
            String orgId = ShiroKit.getUser().getDeptId();
            String orgType = sysStru.getStruType();
            List<String> listNew = new ArrayList<>();
            if (!orgType .equals("5")) {
                listNew.add(orgId);
                model.addAttribute("orgId", listNew);

            }else{
                List<Map<String, Object>> sysStruList = sysStruMapper.getOrgListByParentId(orgId);
                for( int i=0;i<sysStruList.size();i++){
                    listNew.add(sysStruList.get(i).get("STRUID")+"");

                }
                model.addAttribute("orgId", listNew);
            }
        }
        model.addAttribute("componentId", componentId);

        model.addAttribute("deptName", sysStru.getOrganAlias());
        model.addAttribute("deptId",sysUsers.getDepartmentId());
        model.addAttribute("componentApply", componentApply);
        model.addAttribute("readNum", cacheToolService.getComponentReadNum(componentId)+1);
        Timestamp datestr = componentApply.getCreateTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = formatter.format(datestr);
        model.addAttribute("componentTime", str);
        List<ComponentApplyAttachment> list = componentApplyAttachmentService.getAttachmentList(componentId);
        List<MultiplexProject> multiplexList=  multiplexProjectService.componentMultiplexList(componentId);
        int listSize = 0;
        if (list.size() != 0) {
            listSize = list.size();
        }

        model.addAttribute("listSize", listSize);
        model.addAttribute("componentList", list);
        model.addAttribute("multiplexList",multiplexList);
        int multiplexListSize = 0;
        if (multiplexList.size() != 0) {
            multiplexListSize = multiplexList.size();
        }
        String  componentState=Integer.toString(componentApply.getComponentState());
        model.addAttribute("multiplexListSize", multiplexListSize);
        model.addAttribute("componentState",componentState);
        return "/doc/manager/componentmanager/component-view-from.html";
    }

    /**
     * 奖项设置
     * @param ids 科研成果ID
     * @return 是否成功
     */
    @PostMapping("/setAwards")
    @ResponseBody
    public boolean setAwards(String ids, String publishFlag, String collectFlag) {
        boolean flag = true;
        if(ToolUtil.isNotEmpty(ids)){
            ComponentApply componentApply = new ComponentApply();
            componentApply.setPublishFlag(publishFlag);
            componentApply.setCollectFlag(collectFlag);
            List<String> list = Arrays.asList(ids.split(","));
            QueryWrapper<ComponentApply> ew = new QueryWrapper<>();
            ew.in("component_id",list);
            flag = componentApplyService.update(componentApply,ew);
        }
        return flag;
    }

}

