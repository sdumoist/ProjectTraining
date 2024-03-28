package com.jxdinfo.doc.manager.importorganuserdata.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.ImportUtil;
import com.jxdinfo.doc.manager.importorganuserdata.service.ImportOrganService;
import com.jxdinfo.hussar.bsp.organ.model.SysOrgan;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.ISysOrganService;
import com.jxdinfo.hussar.bsp.organ.service.ISysStruService;
import com.jxdinfo.hussar.bsp.permit.model.SysUserRole;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 导入组织机构用户
 */
@Controller
@RequestMapping("/importOrgan")
public class ImportOrganDataController extends BaseController {

    @Autowired
    ISysStruService iSysStruService;

    @Autowired
    ISysOrganService iSysOrganService;

    @Autowired
    ISysUsersService iSysUsersService;

    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;

    @Resource
    private GlobalProperties globalProperties;

    @Resource
    ISysUserRoleService iSysUserRoleService;

    @Autowired
    ImportOrganService importOrganService;

    /**
     * 跳转到导入组织机构用户页面
     *
     * @return 同步页面
     * @Title: initSyschronousData
     * @author: XuXinYing
     */
    @RequestMapping("/view")
    public String initSynchronousData() {
         return "/doc/manager/importorganuserdata/import_organ_data.html";
    }

    /**
     * 导入组织机构
     *
     * @param request
     * @return
     */
    @RequestMapping("/organ")
    @ResponseBody
    @Transactional
    public JSONObject importOrganData(HttpServletRequest request) {

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        InputStream inputStream = null;
        // 正常
        JSONObject json = new JSONObject();
        json.put("code", "1");
        // 错误信息
        List<String> exceptionList = new ArrayList<>();

        List<SysStru> struList = new ArrayList<SysStru>();
        List<SysOrgan> organList = new ArrayList<SysOrgan>();

        // 导入用户信息
        String userId = ShiroKit.getUser().getId();

        try {
            if (multipartFile.isEmpty()) {
                json.put("msg", "文件不存在");
            } else {
                // 获取表格
                inputStream = multipartFile.getInputStream();
                Workbook wb = ImportUtil.getWorkbook(inputStream, multipartFile.getOriginalFilename());
                Sheet sheet = wb.getSheetAt(0);
                Row row = null;
                Cell cell = null;

                // 生效、失效时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date nowDate = new Date();
                String nowDateString = sdf.format(nowDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(nowDate);
                cal.add(Calendar.YEAR, 20);
                String endDateString = sdf.format(cal.getTime());

                // 组装数据map<struName, struId> 用来获取父组织机构
                Map<String, String> struMap = new HashMap<>();
                // 用来校验导入的组织机构是否重复
                Map<String, SysStru> struNameMap = new HashMap<String, SysStru>();

                // 循环行 并校验数据是否为空
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    row = sheet.getRow(i);

                    if (row == null ||  i == 0) {
                        continue;
                    }

                    SysStru stru = new SysStru();

                    String dataId = UUID.randomUUID().toString().replaceAll("-", "");

                    // 循环列
                    int iColNum = sheet.getRow(0).getLastCellNum();
                    for (int j = 0; j < iColNum; j++) {
                        cell = row.getCell(j);
                        String cellValue = cell == null ? "" : String.valueOf(ImportUtil.getCellValue(cell));
                        if (j == 0) {
                            if (StringUtils.isEmpty(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行组织机构名称不允许为空! ");
                                continue;
                            }
                            stru.setOrganAlias(cellValue);
                            struMap.put(cellValue, dataId);
                        }

                        if (j == 1) {
                            stru.setParentId(cellValue);
                            String organName = stru.getOrganAlias();

                            // 验证excel表格中的组织机构名称是否重复
                            if (struNameMap.containsKey(organName)) {
                                SysStru struData = struNameMap.get(organName);
                                String struParentName = struData.getParentId();
                                // 组织机构名称和父级组织机构名称都相同
                                if (StringUtils.equals(struParentName, cellValue)) {
                                    exceptionList.add("第" + (i + 1) + "行组织机构名称和第" + (struData.getGlobalOrder().intValue() + 1) + "行组织机构名称重复!");
                                    continue;
                                }
                            }
                            // 验证和数据库中组织机构名称是否相同
                            boolean struNameExists = importOrganService.struNameExists(organName, cellValue);
                            if (struNameExists) {
                                exceptionList.add("第" + (i + 1) + "行组织机构名称和数据库中组织机构名称重复!");
                                continue;
                            }
                        }
                        if (j == 2) {
                            if (StringUtils.isEmpty(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行组织机构类型不允许为空! ");
                                continue;
                            }
                            stru.setStruType(cellValue);
                            stru.setStruLevel(BigDecimal.valueOf(Long.parseLong(cellValue)));
                        }
                    }

                    // 设置信息
                    stru.setStruId(dataId);
                    stru.setOrganId(dataId);
                    stru.setGlobalOrder(BigDecimal.valueOf(i));
                    stru.setStruOrder(BigDecimal.valueOf(i));
                    stru.setCreator(userId);
                    stru.setCreateTime(new Date());
                    struList.add(stru);
                    struNameMap.put(stru.getOrganAlias(), stru);
                }

                // 设置上级部门id并，校验上级部门是否存在
                if (exceptionList.size() == 0 && struList.size() > 0) {
                    for (SysStru stru : struList) {
                        String struParentName = stru.getParentId();

                        if (StringUtils.isEmpty(struParentName)) { // 上级部门没有填写 将部门挂在顶级目录下
                            stru.setParentId("11");
                        } else if (struMap.containsKey(struParentName)) { // 说明excel表格中有此部门的上级目录
                            stru.setParentId(struMap.get(struParentName));
                        } else { // 说明excel表格中没有此部门的上级部门
                            System.out.println("excel表中没有此父级部门=== " + struParentName);
                            // 查询数据库中是否有上级部门
                            QueryWrapper<SysOrgan> organQueryWrapper = new QueryWrapper<SysOrgan>();
                            organQueryWrapper.eq("ORGAN_NAME", struParentName);
                            organQueryWrapper.eq("IN_USE", "1");
                            try {
                                SysOrgan parentOrgan = iSysOrganService.getOne(organQueryWrapper);
                                if (parentOrgan != null) {
                                    QueryWrapper<SysStru> struQueryWrapper = new QueryWrapper<SysStru>();
                                    struQueryWrapper.eq("IN_USE", "1");
                                    struQueryWrapper.eq("ORGAN_ID", parentOrgan.getOrganId());
                                    SysStru parentStru = iSysStruService.getOne(struQueryWrapper);
                                    if (parentStru != null) {
                                        stru.setParentId(parentStru.getStruId());
                                    } else {
                                        exceptionList.add("excel表格和数据库中没有第 " + (stru.getGlobalOrder().intValue() + 1) + " 行数据的父级组织机构【 " + struParentName + " 】! ");
                                        continue;
                                    }
                                } else {
                                    exceptionList.add("excel表格和数据库中没有第 " + (stru.getGlobalOrder().intValue() + 1) + " 行数据的父级组织机构【 " + struParentName + " 】! ");
                                    continue;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                exceptionList.add("无法确定第 " + (stru.getGlobalOrder().intValue() + 1) + " 行数据的父级组织机构【 " + struParentName + " 】,请检查是否存在相同的组织机构名称! ");
                                continue;
                            }
                        }
                        SysOrgan organ = new SysOrgan();
                        organ.setOrganName(stru.getOrganAlias());
                        organ.setShortName(stru.getOrganAlias());
                        organ.setOrganCode(stru.getOrganAlias());
                        organ.setOrganType(stru.getStruType());
                        organ.setOrganId(stru.getStruId());
                        // 设置生效、失效时间
                        organ.setBeginDate(nowDateString);
                        organ.setEndDate(endDateString);
                        organ.setInUse("1");
                        organ.setCreator(userId);
                        organ.setCreateTime(new Date());
                        organList.add(organ);
                    }
                }
                if (exceptionList.size() == 0 && struList.size() > 0) {
                    iSysStruService.saveBatch(struList);
                    iSysOrganService.saveBatch(organList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            json.put("msg", "数据异常，导入失败!" + e.getMessage());
            return json;
        }
        if (ToolUtil.isEmpty(exceptionList)) {
            json.put("code", "0");
            json.put("msg", "导入成功,共导入数据" + struList.size() + "条");
        } else {
            json.put("exceptionList", exceptionList);
        }
        return json;
    }

    /**
     * 导入用户
     *
     * @param request
     * @return
     */
    @RequestMapping("/user")
    @ResponseBody
    @Transactional
    public JSONObject importUserData(HttpServletRequest request) {

        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartHttpServletRequest.getFile("file");
        InputStream inputStream = null;
        // 正常
        JSONObject json = new JSONObject();
        json.put("code", "1");
        // 错误信息
        List<String> exceptionList = new ArrayList<>();

        List<SysStru> struList = new ArrayList<SysStru>();
        List<SysOrgan> organList = new ArrayList<SysOrgan>();
        List<SysUsers> userList = new ArrayList<SysUsers>();
        List<SysUserRole> userRoleList = new ArrayList<SysUserRole>();

        // 导入用户信息
        String userId = ShiroKit.getUser().getId();

        try {
            if (multipartFile.isEmpty()) {
                json.put("msg", "文件不存在");
            } else {
                // 获取表格
                inputStream = multipartFile.getInputStream();
                Workbook wb = ImportUtil.getWorkbook(inputStream, multipartFile.getOriginalFilename());
                Sheet sheet = wb.getSheetAt(0);
                Row row = null;
                Cell cell = null;

                // 生效、失效时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date nowDate = new Date();
                String nowDateString = sdf.format(nowDate);

                Calendar cal = Calendar.getInstance();
                cal.setTime(nowDate);
                cal.add(Calendar.YEAR, 20);
                String endDateString = sdf.format(cal.getTime());

                String pwd = this.credentialsMatcher.passwordEncode(String.valueOf(this.globalProperties.getDefaultPassword()).getBytes());

                // <登录账号,行号>
                Map<String, Integer> userAccountMap = new HashMap<String, Integer>();

                // 循环行
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    row = sheet.getRow(i);

                    if (row == null || i == 0) {
                        continue;
                    }

                    SysStru stru = new SysStru();
                    SysUsers user = new SysUsers();
                    SysOrgan organ = new SysOrgan();

                    String dataId = UUID.randomUUID().toString().replaceAll("-", "");

                    // 循环列
                    int iColNum = sheet.getRow(0).getLastCellNum();
                    for (int j = 0; j < iColNum; j++) {
                        cell = row.getCell(j);

                        String cellValue = cell == null ? "" : String.valueOf(ImportUtil.getCellValue(cell));

                        if (j == 0) {
                            if (StringUtils.isEmpty(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行用户名称不允许为空! ");
                                continue;
                            }
                            stru.setOrganAlias(cellValue);
                            organ.setOrganName(cellValue);
                            organ.setOrganCode(cellValue);
                            user.setUserName(cellValue);
                        }

                        if (j == 1) {
                            if (StringUtils.isEmpty(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行部门名称不允许为空! ");
                                continue;
                            }
                            // 根据部门名称查询部门id
                            try {
                                // 查询数据库中是否有上级部门
                                QueryWrapper<SysOrgan> organQueryWrapper = new QueryWrapper<SysOrgan>();
                                organQueryWrapper.eq("ORGAN_NAME", cellValue);
                                organQueryWrapper.eq("IN_USE", "1");
                                SysOrgan parentOrgan = iSysOrganService.getOne(organQueryWrapper);

                                if (parentOrgan == null) {
                                    exceptionList.add("第" + (i + 1) + "行系统中不存在此部门! ");
                                    continue;
                                } else {
                                    QueryWrapper<SysStru> struQueryWrapper = new QueryWrapper<SysStru>();
                                    struQueryWrapper.eq("IN_USE", "1");
                                    struQueryWrapper.eq("ORGAN_ID", parentOrgan.getOrganId());
                                    SysStru parentStru = iSysStruService.getOne(struQueryWrapper);
                                    stru.setParentId(parentStru.getStruId());
                                    user.setDepartmentId(parentStru.getStruId());
                                    user.setCorporationId(parentStru.getStruId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                exceptionList.add("无法确定第" + (i + 1) + "行部门信息,请检查数据库中是否有相同的组织机构名称! ");
                                continue;
                            }
                        }
                        if (j == 2) {
                            if (StringUtils.isEmpty(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行登录账号不允许为空! ");
                                continue;
                            }
                            // 验证登录账号和excel表格中的其他账号是否重复
                            if (userAccountMap.containsKey(cellValue)) {
                                exceptionList.add("第" + (i + 1) + "行登录账号和第" + (userAccountMap.get(cellValue) + 1) + "行登录账号重复!");
                                continue;
                            }
                            // 验证登录账号和数据库中的其他账号是否重复
                            boolean userAccountExit = importOrganService.userAccountExists(cellValue);
                            if (userAccountExit) {
                                exceptionList.add("第" + (i + 1) + "行登录账号和数据库中登录账号重复!");
                                continue;
                            }
                            user.setUserAccount(cellValue);
                        }
                    }

                    // 设置信息
                    stru.setStruId(dataId);
                    stru.setOrganId(dataId);
                    stru.setGlobalOrder(BigDecimal.valueOf(i));
                    stru.setStruOrder(BigDecimal.valueOf(i));
                    stru.setCreator(userId);
                    stru.setCreateTime(new Date());
                    stru.setStruType("9");
                    stru.setStruLevel(BigDecimal.valueOf(9));
                    stru.setIsEmployee("1");
                    struList.add(stru);

                    organ.setOrganType("9");
                    organ.setOrganId(stru.getStruId());
                    // 设置生效、失效时间
                    organ.setBeginDate(nowDateString);
                    organ.setEndDate(endDateString);
                    organ.setInUse("1");
                    organ.setCreator(userId);
                    organ.setCreateTime(new Date());
                    organList.add(organ);

                    user.setUserId(dataId);
                    user.setEmployeeId(dataId);
                    user.setAccountStatus("1");
                    user.setMaxSessions(BigDecimal.ONE);
                    user.setIsSys("0");
                    user.setCreateTime(new Date());
                    user.setUserOrder(BigDecimal.valueOf(i));
                    user.setTypeProperty("1");
                    user.setLoginIpLimit("0");
                    user.setLoginTimeLimit("0");
                    user.setPassword(pwd);
                    userList.add(user);

                    // 给用户设置共用角色
                    SysUserRole userRole = new SysUserRole();
                    userRole.setUserId(dataId);
                    userRole.setGrantedRole("public_role");
                    userRole.setAdminOption("1");
                    userRoleList.add(userRole);

                    userAccountMap.put(user.getUserAccount(), i);
                }
            }

            if (exceptionList.size() == 0 && struList.size() > 0) {
                iSysStruService.saveBatch(struList);
                iSysOrganService.saveBatch(organList);
                iSysUsersService.saveBatch(userList);
                iSysUserRoleService.saveBatch(userRoleList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            json.put("msg", "数据异常，导入失败!" + e.getMessage());
            return json;
        }
        if (ToolUtil.isEmpty(exceptionList)) {
            json.put("code", "0");
            json.put("msg", "导入成功,共导入数据" + struList.size() + "条");
        } else {
            json.put("exceptionList", exceptionList);
        }
        return json;
    }

}
