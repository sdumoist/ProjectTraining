package com.jxdinfo.doc.manager.componentmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProject;
import com.jxdinfo.doc.manager.componentmanager.model.MultiplexProjectComponent;
import com.jxdinfo.doc.manager.componentmanager.model.YYZCProject;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectComponentService;
import com.jxdinfo.doc.manager.componentmanager.service.MultiplexProjectService;
import com.jxdinfo.doc.manager.componentmanager.util.MultiplexExportExcel;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.export.bean.ExcelEntity;
import com.jxdinfo.hussar.common.export.bean.ExcelTitle;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 组件复用控制层
 * </p>
 *
 * @author yjs
 * @since 2019/6/24 10:55
 */
@Controller
@RequestMapping("/multiplex")
public class MultiplexProjectController {
    /*
    项目服务层
     */
    @Resource
    private MultiplexProjectService multiplexProjectService;


    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    /*
    组件项目关联业务层
     */
    @Resource
    private MultiplexProjectComponentService multiplexProjectComponentService;

    /*
    组件服务层
     */
    @Resource
    private ComponentApplyService componentApplyService;

    /*
    目录服务层
     */
    @Resource
    private IFsFolderService fsFolderService;

    /**
     * 打开组件复用查看
     */
    @GetMapping("/multiplexListDeptView")
    @RequiresPermissions("multiplex:multiplexListDeptView")
    public String multiplexListDeptView() {
        return "/doc/manager/multiplexmanager/multiplex-list-dept.html";
    }

    /**
     * 打开组件复用查看
     */
    @GetMapping("/multiplexListDept")
    @RequiresPermissions("multiplex:multiplexListDept")
    public String multiplexListDept() {
        return "/doc/manager/multiplexmanager/multiplex-list-dept-view.html";
    }

    /**
     * 打开组件复用查看
     */
    @GetMapping("/multiplexListView")
    public String multiplexListView() {
        return "/doc/manager/multiplexmanager/multiplex-list.html";
    }

    /**
     *
     * @param model 模型
     * @return 复用登记页面
     */
    @GetMapping("/multiplexApply")
    public String multiplexApply(Model model) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getZTFlag(roleList);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("multiplexId", UUID.randomUUID().toString().replaceAll("-", ""));
        return "/doc/manager/multiplexmanager/multiplexApply.html";
    }

    /**
     *
     * @param model 模型
     * @return 复用登记页面
     */
    @GetMapping("/multiplexUpdateView")
    public String multiplexUpdateView(Model model,String projectId) {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getZTFlag(roleList);
        model.addAttribute("adminFlag", adminFlag);
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("multiplexId", UUID.randomUUID().toString().replaceAll("-", ""));
        MultiplexProject mp=multiplexProjectService.getById(projectId);
        model.addAttribute("mp",mp);
        model.addAttribute("userName",mp.getUserId());
        List<MultiplexProject> multiplexProject=  multiplexProjectService.componentMultiplexProjectList(projectId);

        model.addAttribute("multiplexProject",multiplexProject);
        String multiplexId= UUID.randomUUID().toString().replaceAll("-", "");

        model.addAttribute("projectId",projectId);
        model.addAttribute("componentList",multiplexProject);


        return "/doc/manager/multiplexmanager/multiplexUpdate.html";
    }

    @GetMapping("/multiplexApplyBackground")
    public String multiplexApplyBackground(Model model) {
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("multiplexId", UUID.randomUUID().toString().replaceAll("-", ""));
        return "/doc/manager/multiplexmanager/multiplexApplyBackground.html";
    }




    /**
     * 组件复用列表页（按照权限）
     * @param deptName 项目名称
     * @param page 页数
     * @param limit 每页数据量
     * @param title 项目名称
     * @return Json
     */
    @GetMapping("/multiplexListByDept")
    @ResponseBody
    public JSON multiplexListByDept(String deptName, int page, int limit, String title,String dateStart,String dateEnd) {
        String str="";
        if(dateEnd!=""&&dateEnd!=""){
            str =dateEnd.concat(" 23:59:59");
        }
        int beginIndex = page * limit - limit;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        String orgId = null;
        if (adminFlag != 4&&adminFlag!=6) {
            orgId = ShiroKit.getUser().getDeptId();
        }
        //开始位置
        String deptNameStr = StringUtil.transferSqlParam(deptName);
        List<MultiplexProject> multiplexList = multiplexProjectService.multiplexList(title, deptNameStr,
                beginIndex, limit, orgId,dateStart,str);
        int multiplexCount = multiplexProjectService.multiplexListCount(title, deptNameStr, orgId,dateStart,str);
        JSONObject json = new JSONObject();
        json.put("count",multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 组件复用列表页（按照权限）
     * @param deptName 项目名称
     * @param page 页数
     * @param limit 每页数据量
     * @param title 项目名称
     * @return Json
     */
    @GetMapping("/multiplexListByDeptView")
    @ResponseBody
    public JSON multiplexListByDeptView(String deptName, int page, int limit, String title,String caUserName) {
        int beginIndex = page * limit - limit;
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        String orgId = null;
        if (adminFlag != 4&&adminFlag!=6) {
            orgId = ShiroKit.getUser().getDeptId();
        }
        //开始位置
        String deptNameStr = StringUtil.transferSqlParam(deptName);
        List<MultiplexProject> multiplexList = multiplexProjectService.multiplexListByDept(title, deptNameStr,
                beginIndex, limit, orgId,caUserName);
        int multiplexCount = multiplexProjectService.multiplexListCountByDept(title, deptNameStr, orgId,caUserName);
        JSONObject json = new JSONObject();
        json.put("count",multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     *
     * 组件复用列表（之前写的，暂时未用）
     * @param deptName 项目名称
     * @param page 页数
     * @param limit 每页数据量
     * @param title 项目名称
     * @return Json
     */
    @PostMapping("/multiplexList")
    @ResponseBody
    public JSON multiplexList(String deptName, int page, int limit, String title) {
        int beginIndex = page * limit - limit;
        //开始位置
        String deptNameStr = StringUtil.transferSqlParam(deptName);
        List<MultiplexProject> multiplexList = multiplexProjectService.multiplexList(title, deptNameStr,
                beginIndex, limit, null,null,null);
        int multiplexCount = multiplexProjectService.multiplexListCount(title, deptNameStr, null,null,null);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }


    /**
     * 组件登记成果选择
     * @param model
     * @param projectId 项目Id
     * @return String
     */
    @GetMapping("/projectView")
    public String projectView(Model model, String projectId) {
        model.addAttribute("userName", ShiroKit.getUser().getName());
        model.addAttribute("projectId", projectId);
        MultiplexProject multiplexProject = multiplexProjectService.getById(projectId);
        model.addAttribute("multiplexProject", multiplexProject);
        List<ComponentApply> list = multiplexProjectService.getComponentList(projectId);
        model.addAttribute("componentList", list);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getZTFlag(roleList);
        model.addAttribute("adminFlag", adminFlag);
        return "/doc/manager/multiplexmanager/project-view.html";
    }

    /**
     * 获取组件和项目信息
     * @param title 组件名称
     * @param page  页码
     * @param limit 每页数据条数
     * @return Json
     */
    @PostMapping("/myComponentList")
    @ResponseBody
    public JSON myComponentList(String title, @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "limit", defaultValue = "60") int limit) {
        int beginIndex = page * limit - limit;
        String userId = ShiroKit.getUser().getId();
        //开始位置
        List<ComponentApply> multiplexList = multiplexProjectService.myComponentList(title, userId,
                beginIndex, limit);
        int multiplexCount = multiplexProjectService.myComponentListCount(title, userId);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 项目列表
     *
     * @param title 项目名称
     * @param page  页数
     * @param limit 每页信息数量
     * @return Json
     */
    @RequestMapping("/projectList")
    @ResponseBody
    public JSON projectList(String title, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置
        String orgName = ShiroKit.getUser().getDeptName();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag == 1) {
            orgName = null;
        }
        List<YYZCProject> multiplexList = multiplexProjectService.projectList(title, orgName,
                beginIndex, limit);
        int multiplexCount = multiplexProjectService.projectListCount(title, orgName);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 跳转到我的成果（暂时删除）
     *
     * @return String
     */
    @GetMapping("/myComponentView")
    public String myComponentView() {
        return "/doc/manager/multiplexmanager/myComponentView.html";
    }

    /**
     * 复用登记选择项目
     *
     * @return 项目选择页面
     */
    @GetMapping("/projectListView")
    public String projectListView() {
        return "/doc/manager/multiplexmanager/projectListView.html";
    }
    /**
     * 复用登记选择项目
     *
     * @return 项目选择页面
     */
    @GetMapping("/projectListViewBackground")
    public String projectListViewBackground() {
        return "/doc/manager/multiplexmanager/projectListViewBackground.html";
    }


    /**
     * 我的复用列表
     *
     * @return 页面地址
     */
    @GetMapping("/myMultiplexView")
    public String myMultiplexView() {
        return "/doc/manager/multiplexmanager/myMultiplexView.html";
    }


    /**
     * 复用登记选择成果
     *
     * @return 复用登记选择成果
     */
    @GetMapping("/selectComponentView")
    public String selectComponentView(String adminFlag,Model model) {

        model.addAttribute("adminFlag",adminFlag);
        return "/doc/manager/multiplexmanager/selectComponentView.html";
    }
    /**
     * 复用登记选择成果
     *
     * @return 复用登记选择成果
     */
    @GetMapping("/selectComponentViewBackground")
    public String selectComponentViewBackground() {
        return "/doc/manager/multiplexmanager/selectComponentViewBackground.html";
    }

    /**
     * 复用登记选择成果，初始化成果信息
     *
     * @param title 项目名称
     * @param page  页数
     * @param limit 每页信息数量
     * @return 成果信息Json
     */
    @RequestMapping("/selectComponent")
    @ResponseBody
    public JSON selectComponentView(String title, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置
        List<ComponentApply> multiplexList = componentApplyService.componentList(title, null, 2,
                beginIndex, limit, null, null, null, null,null, null);
        int multiplexCount = componentApplyService.componentListCount(title, null, 2, null, null, null, null, null, null);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 新增复用登记
     *
     * @param multiplexProject 项目，用户，组件，复用，部门，表关联参数实体类
     * @param componentIdStr   附件Id
     * @return Json
     */
    @PostMapping("/multiplexSave")
    @ResponseBody
    public JSON addTopic(MultiplexProject multiplexProject, String componentIdStr) {
        //专题ID
        multiplexProject.setUserId(ShiroKit.getUser().getId());


        Timestamp ts = new Timestamp(System.currentTimeMillis());
        multiplexProject.setCreateTime(ts);
        multiplexProjectService.saveOrUpdate(multiplexProject);
        JSONObject json = new JSONObject();
        String[] idArr = componentIdStr.split(",");

        for (int i = 0; i < idArr.length; i++) {
            MultiplexProjectComponent multiplexProjectComponent = new MultiplexProjectComponent();
            multiplexProjectComponent.setMultiplexId(UUID.randomUUID().toString().replaceAll("-", ""));
            multiplexProjectComponent.setComponentId(idArr[i]);
            multiplexProjectComponent.setProjectId(multiplexProject.getProjectId());
            multiplexProjectComponentService.saveOrUpdate(multiplexProjectComponent);
        }
        json.put("result", "1");

        return json;
    }

    /**
     * 根据字典类型获取字典数据List
     *
     * @return java.lang.Object
     * @author LiangDong
     * @date 2018/5/28 14:19
     * @return Object
     */
    @PostMapping("/getDept")
    @ResponseBody
    public Object getDept(String visibleRange) {
        List<Map<String, Object>> list = fsFolderService.getDeptList(0, 8,visibleRange);
        return list;
    }

    /**
     *
     * @param model 模型
     * @param componentId 成果Id
     * @return String
     */
    @GetMapping("/selectComponentProject")
    public String selectComponentProject(Model model, String componentId) {
        model.addAttribute("componentId", componentId);
        return "/doc/manager/multiplexmanager/selectComponentProject.html";
    }

    /**
     *
     * @param model
     * @param componentId 成果id
     * @return String
     */
    @GetMapping("/viewReason")
    public String viewReason(Model model, String componentId) {
       ComponentApply componentApply=  componentApplyService.getById(componentId);
       if(componentApply.getReturnReasons()!=null){
           componentApply.setReturnReasons(componentApply.getReturnReasons().trim());
       }
       if(componentApply.getReturnReasonsWyh()!=null){
           componentApply.setReturnReasonsWyh(componentApply.getReturnReasonsWyh().trim());
       }

        model.addAttribute("componentApply", componentApply);
        return "/doc/manager/multiplexmanager/viewReason.html";
    }

    /**
     *
     * @param componentId 成果ID
     * @param page 页数
     * @param limit 每页数据量
     * @param projectName 项目名称
     * @return Json
     */
    @RequestMapping("/selectComponentProjectView")
    @ResponseBody
    public JSON selectComponentProject(String componentId, int page, int limit, String projectName) {
        int beginIndex = page * limit - limit;
        //开始位置
        List<MultiplexProject> multiplexList = multiplexProjectService.selectComponentProject(componentId,
                beginIndex, limit, projectName);
        int multiplexCount = multiplexProjectService.componentProjectCount(componentId, projectName);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 获取我的复用列表
     *
     * @param title 组件名称
     * @param page  页码
     * @Author yjs
     * @param limit 每页数据条数
     * @return Json
     */
    @PostMapping("/myMultiplexList")
    @ResponseBody
    public JSON myMultiplexList(String title, @RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "limit", defaultValue = "10") int limit) {
        int beginIndex = page * limit - limit;
        String userId = ShiroKit.getUser().getId();
        //开始位置
        List<MultiplexProject> multiplexList = multiplexProjectService.myMultiplexList(title, userId,
                beginIndex, limit);
        int multiplexCount = multiplexProjectService.myMultiplexListCount(title, userId);
        JSONObject json = new JSONObject();
        json.put("count", multiplexCount);
        json.put("data", multiplexList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;

    }

    /**
     *删除复用登记
     * @param projectId 复用列表Id
     * @returnJson
     */
    @PostMapping("/deleteMultiplexSave")
    @ResponseBody
    public JSON deleteTopic(String projectId) {
        //专题ID
        String[] ids = projectId.split(",");

        int j=0;
        boolean str=false;
        for (int i = 0; i <ids.length ; i++) {
            j+=multiplexProjectComponentService.deleteTopic(ids[i]);
            str=multiplexProjectService.removeById(ids[i]);

        }
        JSONObject json = new JSONObject();
        if(j!=0&&str){
            json.put("result", "1");
        }
        return json;
    }




    /**
     *
     * @return json
     */
    @PostMapping("/multiplexGraphCount")
    @ResponseBody
    public Object multiplexGraphCount(String dateStart,String dateEnd,String date,Integer bu,String order) {
        List<MultiplexProject> listCom= new ArrayList<>();
        String str="";
        if(dateEnd!=null&&dateEnd!=""){
            str =dateEnd.concat(" 23:59:59");
        }
        if(bu==null||bu==0){
            listCom = multiplexProjectService.multiplexGraphCount(dateStart,str,date,order);
        /*if(bu==1){
            for(ComponentApply arr:listCom) {
                arr.setOrganAlias(arr.getOrganAliasBu());
            }
        }*/
        }else {
            listCom = multiplexProjectService.multiplexGraphCountBg(dateStart,str,date,order);
        }
        Map<String, Object> result = new HashMap<>(5);
        List<String >list =new ArrayList();
        int totalNum=0;
        List<Integer> numList=new ArrayList();

        for(int i=0;i<listCom.size();i++){
            int num=Integer.parseInt(listCom.get(i).getRe_num());
            totalNum=totalNum+num;
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
     * 新增复用登记
     *
     * @param multiplexProject 项目，用户，组件，复用，部门，表关联参数实体类
     * @param componentIdStr   附件Id
     * @return Json
     */
    @PostMapping("/multiplexSaveUpdate")
    @ResponseBody
    public JSON multiplexSaveUpdate(MultiplexProject multiplexProject, String componentIdStr,String economizeStr) {
        //专题ID
        multiplexProject.setUserId(ShiroKit.getUser().getId());


        Timestamp ts = new Timestamp(System.currentTimeMillis());
        multiplexProject.setCreateTime(ts);

        multiplexProjectService.saveOrUpdate(multiplexProject);
        JSONObject json = new JSONObject();
        String[] idArr = componentIdStr.split(",");
        String[] str=null;
        if(economizeStr!=null&&economizeStr!=""){
            str = economizeStr.split(",");
        }


        multiplexProjectComponentService.remove(new QueryWrapper<MultiplexProjectComponent>().eq("project_id",
                multiplexProject.getProjectId()));

        for (int i = 0; i < idArr.length; i++) {
            MultiplexProjectComponent multiplexProjectComponent = new MultiplexProjectComponent();
            multiplexProjectComponent.setMultiplexId(UUID.randomUUID().toString().replaceAll("-", ""));
            multiplexProjectComponent.setComponentId(idArr[i]);
            String currentCode = this.sysIdtableService.getCurrentCode("COMPONENTIDORDER", "multiplex_project_component");
            int bigNum = Integer.parseInt(currentCode);
            multiplexProjectComponent.setShowOrdet(bigNum);
            if(str!=null&&str.length!=0){
                multiplexProjectComponent.setEconomize(str[i]);
            }

            multiplexProjectComponent.setProjectId(multiplexProject.getProjectId());
            multiplexProjectComponentService.saveOrUpdate(multiplexProjectComponent);
        }
        json.put("result", "1");

        return json;
    }

    /**
     * 导出Excel
     *
     * @param
     * @return 导出Excel
     * @Title: exportLog
     * @author: qiuyuanlong
     */
    @GetMapping("/export")
    @ResponseBody
    public void exportMultiplex(HttpServletResponse response,String deptName, String title,String caUserName) throws Exception {
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getWYHFlag(roleList);
        String orgId = null;
        if (adminFlag != 4&&adminFlag!=6) {
            orgId = ShiroKit.getUser().getDeptId();
        }
        //开始位置
        String deptNameStr = StringUtil.transferSqlParam(deptName);
        List<MultiplexProject> multiplexList = multiplexProjectService.multiplexListByDept(title, deptNameStr,
                null, null, orgId,caUserName);

        //excel标题
        List<ExcelTitle> excelTitles = new ArrayList<ExcelTitle>();
        String[] titleExcel = {"成果名称", "提报人", "提报部门","复用次数", "复用项目", "登记部门",
                "登记人", "登记时间"};
        String[] idTitle = {"componentName", "caUserName", "organAlias","componentCount", "projectName", "projectDept"
                , "userName", "createTimeStr"};
        for (int i = 0; i < titleExcel.length; i++) {
            ExcelTitle excelTitle = new ExcelTitle();
            excelTitle.setTitle_id(idTitle[i]);
            excelTitle.setTitle_text(titleExcel[i]);
            excelTitles.add(excelTitle);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

        Date date = new Date(System.currentTimeMillis());

        //excel文件名
        String fileName = "科研成果复用表" + formatter.format(date) + ".xlsx";

        List<Map<String, Object>> querys = new ArrayList<>();
        for (int i = 0; i < multiplexList.size(); i++) {
            Map<String, Object> querysMap = new HashMap<>(10);
            querysMap.put("no", i + 1);

            querysMap.put("componentName", multiplexList.get(i).getComponentName());
            querysMap.put("caUserName", multiplexList.get(i).getCaUserName());
            querysMap.put("organAlias", multiplexList.get(i).getOrganAlias());
            querysMap.put("componentCount", multiplexList.get(i).getComponentCount());
            querysMap.put("projectName", multiplexList.get(i).getProjectName());
            querysMap.put("projectDept", multiplexList.get(i).getProjectDept());
            querysMap.put("userName", multiplexList.get(i).getUserName());
            querysMap.put("createTimeStr", multiplexList.get(i).getCreateTimeStr());

            querys.add(querysMap);
        }


        ExcelEntity excelEntity = new ExcelEntity();
        excelEntity.setQuerys(querys);
        excelEntity.setTitles(excelTitles);
        excelEntity.setBlankLeft(true);
        excelEntity.setBlankTop(true);
        MultiplexExportExcel exportExcel = new MultiplexExportExcel();

        //响应到客户端
        try {
            this.setResponseHeader(response, fileName);
            OutputStream os = response.getOutputStream();
            exportExcel.exportExcel(os, excelEntity);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //发送响应流方法
    public void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
