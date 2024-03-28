package com.jxdinfo.doc.manager.foldermanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.DocConfigService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.docaudit.model.FsFolderAuditor;
import com.jxdinfo.doc.manager.docaudit.service.IDocInfoAuditService;
import com.jxdinfo.doc.manager.docaudit.service.IFsFolderAuditorService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.hussar.bsp.organ.service.SysOrgManageService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.treemodel.JSTreeModel;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.base.tips.SuccessTip;
import com.jxdinfo.hussar.core.base.tips.Tip;
import com.jxdinfo.hussar.core.constant.HttpCode;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * 目录管理控制器。
 * @author zf
 * @date 2018-09-06 15:01:50
 */
@Controller
@RequestMapping("/fsFolder")
public class FsFolderController extends BaseController {

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;
    /**
     * PREFIX
     */
    private String prefix = "/doc/manager/foldermanager/";

    /**
     * 目录管理服务类
     */
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private ITopicDocManagerService topicDocManagerService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;

    @Autowired
    private DocConfigService docConfigService;
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private DocGroupService docGroupService;

    @Value("${fileAudit.using}")
    private String using;

    @Value("${fileAudit.auditType}")
    private String auditType;

    @Value("${fileAudit.auditorRange}")
    private String auditorRange;

    @Value("${fileAudit.auditRole}")
    private String auditRole;

    @Value("${fileAudit.workflowUsing}")
    private boolean workflowUsing;

    @Autowired
    private IFsFolderAuditorService fsFolderAuditorService;

    @Autowired
    private SysOrgManageService sysOrgManageService;

    @Autowired
    private IDocInfoAuditService docInfoAuditService;

    @Autowired
    private ESUtil esUtil;

    @Resource
    private FilesService filesService;

    /**
     * @return 目录维护页面
     * @Author zoufeng
     * @Description 跳转到目录维护页面
     * @Date 14:35 2018/9/7
     * @Param []
     **/
    @RequiresPermissions("fsFolder:manager")
    @GetMapping("/manager")
    public String index() {
        return prefix + "folderManager.html";
    }

    /**
     * @return 目录授权页面
     * @Author zoufeng
     * @Description 跳转到目录授权页面
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @GetMapping("/powerManager")
    public String indexAuth() {
        return prefix + "powerManager.html";
    }

    /**
     * @return 目录权限页面
     * @Author zoufeng
     * @Description 跳转到权限设置页面
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @GetMapping("/authority")
    public String authority(String chooseFolder) {
        return "/doc/manager/docmanager/authority.html";
    }

    /**
     * @return 后台目录授权页面
     * @Author zoufeng
     * @Description 跳转到后台目录授权页面
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @GetMapping("/authorityPower")
    public String authorityPower() {
        return prefix + "authorityPower.html";
    }

    /**
     * 跳转选择目录审批人页面
     * @param folderId 目录id
     * @param model 返回model
     * @return 页面url
     */
    @RequestMapping("/chooseAuditor")
    public String chooseAuditor(String folderId, Model model){
        // 添加返回参数
        model.addAttribute("folderId", folderId);
        // 返回页面
        return "/doc/manager/docmanager/judgeChoose.html";
    }

    /**
     * @return 根节点id和名称
     * @Author zoufeng
     * @Description 获取根节点
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @PostMapping(value = "/getRoot")
    @ResponseBody
    public Map getRoot() {
        List<FsFolder> list = fsFolderService.getRoot();
        Map<String, String> map = new HashMap<>();
        FsFolder fsFolder = list.get(0);
        map.put("root", fsFolder.getFolderId());
        map.put("rootName", fsFolder.getFolderName());
        return map;
    }

    /**
     * @param fsFolderParams 目录信息及权限信息，祥看FsFolderParams
     * @return java.lang.Object
     * @Author zoufeng
     * @Description 新增文件夹的保存方法
     * @Date 17:53 2018/9/7
     **/
    @PostMapping(value = "/add")
    @ResponseBody
    public Object add(FsFolderParams fsFolderParams) {
        String foldId = fsFolderParams.getParentFolderId();
        List<String> idList = new ArrayList<String>();
        idList.add(foldId);
       /* boolean authorityFlag = filesService.checkFoldManageAuthority(idList);
        if(!authorityFlag){
            SuccessTip tip = new SuccessTip();
            tip.setMessage("权限不足");
            return tip;
        }*/
        FsFolder fsFolder = new FsFolder();
        String userId = ShiroKit.getUser().getId();
        String auditFlag = fsFolderParams.getAuditFlag();
        cacheToolService.updateLevelCodeCache(userId);
        fsFolder.setFolderId(fsFolderParams.getFolderId());
        fsFolder.setFolderName(fsFolderParams.getFolderName());
        fsFolder.setVisibleRange(fsFolderParams.getVisible());
        fsFolder.setParentFolderId(fsFolderParams.getParentFolderId());
        fsFolder.setAuditFlag(auditFlag);
        FsFolder fsFolderParent =  new FsFolder();
        fsFolderParent = fsFolderService.getById(fsFolderParams.getParentFolderId());
        String ownId = fsFolderParent.getOwnId();
        if(ownId!=null&&!"".equals(ownId)){
            fsFolder.setOwnId(ownId);
        }
        if (ToolUtil.isNotEmpty(fsFolderParams.getFolderId())) {
            fsFolderService.updateFsFolder(fsFolder);
        } else {
            //新增一下主表
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");

            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 4-1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
                    String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName;
                }
                localName=localName+"\\"+fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            // 删除目录审核人信息
            QueryWrapper<FsFolderAuditor> wrapper = new QueryWrapper<>();
            wrapper.eq("folder_id",folderId);
            fsFolderAuditorService.remove(wrapper);
            // 保存目录审核人信息
            if ("1".equals(auditFlag) && !workflowUsing) { // 需要审核 并且没有开启工作流审核
                String auditorIds = fsFolderParams.getAuditorIds();
                String auditorNames = fsFolderParams.getAuditorNames();
                fsFolderAuditorService.addFolderAuditor(folderId, auditorIds, auditorNames);
            }
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();

            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(folderId);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(0);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }

        return SUCCESS_TIP;
    }

    /**
     * 新增重名检测
     *
     * @param name           目录名称
     * @param parentFolderId 打开文件的id
     * @return 是否存在重名
     */
    @PostMapping(value = "/addCheck")
    @ResponseBody
    public String addCheck(String name, String parentFolderId,String folderId) {
        String res = "true";
        List<FsFolder> list = fsFolderService.addCheck(parentFolderId, name,folderId);
        if (list.size() > 0) {
            return "false";
        }
        return res;
    }

    /**
     * 级联删除文件目录
     *
     * @param fsFolderIds 选中目录id
     * @return 删除条数
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public int delete(@RequestParam String fsFolderIds) {
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        String[] strArr = fsFolderIds.split(",");
        List foldIdList = new ArrayList();
        foldIdList.addAll(Arrays.asList(strArr));
       /* boolean authorityFlag = filesService.checkParentFoldManageAuthority(foldIdList);
        if(!authorityFlag){
            return 0;
        }*/
        List<String> list = new ArrayList();
        for (String element : strArr) {
//            String ids = fsFolderService.getChildFsFolder(element);
//            String[] childArr = ids.split(",");
//            list.addAll(Arrays.asList(childArr));
            // 获取目录
            FsFolder fsFolder = fsFolderService.getById(element);
            if(fsFolder == null){
                return 0;
            }
            list = fsFolderService.getFsFolderBylevelOrder(fsFolder.getLevelCode());
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            docResourceLog.setId(id);
            docResourceLog.setResourceId(element);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(2);
                docResourceLog.setDeletePath(fsFolder.getFolderPath());
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
                //

        }
        //删除目录
        int num = fsFolderService.deleteInIds(list);
        docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().in("folder_id", list));
        topicDocManagerService.delTopicFile(list);
        return num;
    }

    /**
     * 粘贴时检查目标目录是否为粘贴内容的子目录
     *
     * @param fsFolderIds 需要粘贴内容的id集合
     * @param id          打开目录的id
     * @return 是否为子目录
     * @author zf
     * @date 2018-09-06
     */
    @PostMapping(value = "/checkChild")
    @ResponseBody
    public String checkChild(@RequestParam String fsFolderIds, @RequestParam String id) {
        String res = "success";
        String[] strArr = fsFolderIds.split(",");
        for (String element : strArr) {
            String ids = fsFolderService.getChildFsFolder(element);
            String[] childArr = ids.split(",");
            for (int i = 0; i < childArr.length; i++) {
                if (childArr[i].equals(id)) {
                    res = "have";
                }
            }
        }
        return res;
    }

    /**
     * 检查目录下是否存在文件
     *
     * @param ids 选中文件的id
     * @return 是否存在文件
     */
    @PostMapping(value = "/checkFolderType")
    @ResponseBody
    public Object checkFolderType(@RequestParam String ids) {
        String flag = "success";
        //目录下面的文件数
        String num = fsFolderService.checkChildType(ids);
        int fileNum = Integer.parseInt(num);
        if (fileNum > 0) {
            return "haveFile";
        }
        return flag;
    }

    /**
     * @return 目录树信息
     * @title: 查询下级目录
     * @description: 查询下级节点（目录）
     * @param fsFolderParams
     * @param nameFlag
     * @date: 2018-8-15
     * @author: yjs
     */
    @PostMapping(value = "/getChildren")
    @ResponseBody
    public Object getChildren(FsFolderParams fsFolderParams, String nameFlag) {
        return fsFolderService.getChildren(fsFolderParams, nameFlag);
    }

    /**
     * 修改更新文件--只修改名字或粘贴
     * @param ids            修改或粘贴文件的id
     * @param parentFolderId 打开文件的id
     * @param folderName     文件名称
     * @return
     */
    @PostMapping(value = "/update")
    @ResponseBody
    public String update(String ids, String parentFolderId, String folderName) {
        List<String> idList = new ArrayList<String>();
        idList.add(parentFolderId);
      /*  boolean authorityFlag = filesService.checkFoldManageAuthority(idList);
        if(!authorityFlag){
            return "success";
        }*/
        String res = "success";
        String parentLevelCode = fsFolderService.getById(parentFolderId).getLevelCode();
        //粘贴或改名
        if (folderName == null || "".equals(folderName)) {

            String userId = ShiroKit.getUser().getId();
            cacheToolService.updateLevelCodeCache(userId);
            String[] strArr = ids.split(",");
            List<FsFolder> listFs = new ArrayList<>();
            for (String element : strArr) {
                int codeNum = fsFolderService.getChildCodeCount(element);
                int totalCode = parentLevelCode.length() / 4 + codeNum;
                String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
                if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
                    folderAmount="4";
                }
                if (totalCode > Integer.parseInt(folderAmount)) {
                    res = folderAmount;
                    return res;
                }
                FsFolder fsFolder = new FsFolder();
                fsFolder.setFolderId(element);
                FsFolder oldFolder = fsFolderService.getById(element);
                String beforeId = oldFolder.getParentFolderId();
                fsFolder.setParentFolderId(parentFolderId);
                FsFolder parentFolder = fsFolderService.getById(parentFolderId);
                if(parentFolder!=null&&parentFolder.getOwnId()!=null&&parentFolder.getOwnId().equals(userId)){
                    fsFolder.setOwnId(userId);
                    docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", element));
                }else {
                    fsFolder.setOwnId("");
                }
                Date date = new Date();
                Timestamp ts = new Timestamp(date.getTime());
                fsFolder.setUpdateTime(ts);
                listFs.add(fsFolder);

                // 判断是否需要审核
                boolean auditState = false;
                List<DocInfo> auditDocList = new ArrayList<>();
                if("true".equals(using)){
                    if(ToolUtil.isNotEmpty(parentFolder)) {
                        String nowAuditFlag = parentFolder.getAuditFlag();
                        if("1".equals(nowAuditFlag)) { // 新目录需要审核
                            auditState = true;
                            if("2".equals(auditType)){ // 审核类型为2
                                auditState = false;
                            }
                            if(ToolUtil.isNotEmpty(oldFolder)){
                                String oldAuditFlag = oldFolder.getAuditFlag();
                                // 旧目录下已审核，判断新目录下是否仍需审核
                                if("1".equals(oldAuditFlag) && "1".equals(auditorRange)){
                                    auditState = false;
                                }
                            }
                            // 查询移动目录及其子目录
                            String levelCode = oldFolder.getLevelCode();
                            QueryWrapper<FsFolder> ew = new QueryWrapper<>();
                            ew.like("level_code",levelCode + "%");
                            ew.ne("folder_id",parentFolderId);
                            List<FsFolder> auditFolderList = fsFolderService.list(ew);
                            List<String> auditFolderIdList = new ArrayList<>();
                            for(FsFolder auditFolder:auditFolderList){
                                auditFolderIdList.add(auditFolder.getFolderId());
                            }
                            if(ToolUtil.isNotEmpty(auditFolderIdList)){
                                QueryWrapper<DocInfo> wr = new QueryWrapper<>();
                                wr.in("fold_id",auditFolderIdList);
                                wr.in("valid_flag","1");
                                auditDocList = docInfoService.list(wr);
                            }
                            if("3".equals(auditType)){ // 类型为3
                                // 本目录及其子目录均需审核
                                FsFolder temp = new FsFolder();
                                temp.setAuditFlag("1");
                                fsFolderService.update(temp,ew);
                                // 查询新目录审批人
                                QueryWrapper<FsFolderAuditor> wrapper = new QueryWrapper<>();
                                wrapper.eq("folder_id",parentFolderId);
                                List<FsFolderAuditor> fsFolderAuditorList = fsFolderAuditorService.list(wrapper);
                                // 添加目录审核人
                                if(ToolUtil.isNotEmpty(auditFolderIdList) && ToolUtil.isNotEmpty(fsFolderAuditorList)){
                                    List<FsFolderAuditor> folderAuditorList = new ArrayList<>();
                                    for(String auditFolderId:auditFolderIdList){
                                        // 删除旧目录审核人信息
                                        QueryWrapper<FsFolderAuditor> wp = new QueryWrapper<>();
                                        wp.eq("folder_id",auditFolderId);
                                        fsFolderAuditorService.remove(wp);
                                        for(FsFolderAuditor fsFolderAuditor:fsFolderAuditorList){
                                            FsFolderAuditor folderAuditor = new FsFolderAuditor();
                                            folderAuditor.setFolderId(auditFolderId);
                                            folderAuditor.setAuditUserId(fsFolderAuditor.getAuditUserId());
                                            folderAuditor.setAuditUserName(fsFolderAuditor.getAuditUserName());
                                            folderAuditor.setCreator(userId);
                                            folderAuditor.setCreateTime(ts);
                                            folderAuditorList.add(folderAuditor);
                                        }
                                    }
                                    if(ToolUtil.isNotEmpty(folderAuditorList)){
                                        fsFolderAuditorService.saveBatch(folderAuditorList);
                                    }
                                }
                            }
                        }
                    }
                }

                if(auditState && ToolUtil.isNotEmpty(auditDocList)){
                    // 添加文件审核信息
                    for(DocInfo auditDoc:auditDocList){
                        String docId = auditDoc.getDocId();
                        // 更新文档审核状态
                        DocInfo docInfo = new DocInfo();
                        docInfo.setDocId(docId);
                        docInfo.setValidFlag("2");
                        docInfoService.updateById(docInfo);
                        // 更新文档索引
                        Map map = new HashMap();
                        map.put("recycle","2");
                        esUtil.updateIndex(docId, map);
                        // 添加审核信息
                        docInfoAuditService.addDocInfoAudit(parentFolderId,docId);
                    }
                }

                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(element);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(1);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(12);
                docResourceLog.setValidFlag("1");
                docResourceLog.setBeforeId(beforeId);
                docResourceLog.setAfterId(parentFolderId);
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);   //插入预览记录
            }
            fsFolderService.saveOrUpdateBatch(listFs);
            //根节点生成层级码
            fsFolderService.addLevel(parentFolderId);
        } else {
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            FsFolder fsFolder = new FsFolder();
            fsFolder.setFolderId(ids);
            fsFolder.setFolderName(folderName);
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            fsFolderService.updateById(fsFolder);
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(ids);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            String userId = ShiroKit.getUser().getId();
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(8);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        return res;
    }

    @PostMapping(value = "/updatePath")
    @ResponseBody
    public void updateLevelAndPath(String parentFolderId) {
        //根节点生成目录层级
        fsFolderService.addPath(parentFolderId);
    }

    /**
     * 粘贴重命名检测
     *
     * @param nameStr   目录名称
     * @param folderPid 打开文件夹的id
     * @return 是否存在重名
     */
    @PostMapping(value = "/checkName")
    @ResponseBody
    public String checkName(String nameStr, String folderPid) {
        String res = "success";
        String[] nameArr = nameStr.split("\\*");
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < nameArr.length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("name", nameArr[i]);
            list.add(map);
        }
        List listFs = fsFolderService.countFolderName(folderPid, list);
        if (listFs.size() > 0) {
            res = "fail";
        }
        return res;
    }

    /**
     * 动态加载目录树
     *
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    @GetMapping(value = "/getTreeDataLazy")
    @ResponseBody
    public List getTreeDataLazy(String id, String type) {
        return fsFolderService.getTreeDataLazy(id, type);
    }

    /**
     * @return java.util.List 权限集合
     * @Author zoufeng
     * @Description 查询目录权限
     * @Date 10:26 2018/9/10
     * @Param [folderId] 目录id
     **/
    @PostMapping("/getAuthority")
    @ResponseBody
    public List getAuthority(String folderId) {
        return fsFolderService.getAuthority(folderId);
    }

    /**
     * @return boolean 是否修改成功
     * @Author zoufeng
     * @Description 修改目录结构
     * @Date 10:25 2018/9/10
     * @Param [fsFolderEdit]
     **/
    @RequestMapping("/editAuthority")
    @ResponseBody
    public boolean editAuthority(FsFolderParams fsFolderEdit, String authType) {
        String userId = ShiroKit.getUser().getId();
        cacheToolService.updateLevelCodeCache(userId);
        String folderIds = fsFolderEdit.getFolderId();
        String folderNames = fsFolderEdit.getFolderName();
        boolean flag = false;
        Date date = new Date();
        String[] foldIds = folderIds.split(",");
        List foldIdList = new ArrayList();
        foldIdList.addAll(Arrays.asList(foldIds));
       /* boolean authorityFlag = filesService.checkParentFoldManageAuthority(foldIdList);
        if(!authorityFlag){
            return false;
        }*/
        String delAuth = "";
        String group = fsFolderEdit.getGroup();
        String person = fsFolderEdit.getPerson();
        String role = fsFolderEdit.getRole();
        if (group.length() > 0 && person.length() > 0) {
            delAuth = group + "," + person;
        } else {
            delAuth = group + person;
        }
        if (role.length() > 0 && delAuth.length() > 0) {
            delAuth = delAuth + "," + role;
        } else {
            delAuth = delAuth + role;
        }
        if (folderIds.length() > 0){
            String[] folderId = folderIds.split(",");
            String[] folderName = folderNames.split(",");
            for(int i = 0;i < folderId.length ; i ++){
                FsFolder fsFolder = new FsFolder();

                fsFolder.setUpdateUserId(userId);
                fsFolder.setFolderId(folderId[i]);
                fsFolder.setFolderName(folderName[i]);
                fsFolder.setVisibleRange(fsFolderEdit.getVisible());
                Timestamp ts = new Timestamp(date.getTime());
                fsFolder.setUpdateTime(ts);
                fsFolderEdit.setFolderId(folderId[i]);
                //更新目录信息
                fsFolderService.updateById(fsFolder);
                if (!"add".equals(authType)) {
                    //删除权限信息
                    docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", folderId[i]));
                } else {
                    if (!"".equals(delAuth)) {
                        //删除权限信息
                        docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", folderId[i]).in("author_id", delAuth));
                    }
                }
                //保存权限信息
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                flag = docFoldAuthorityService.saveDocFoldAuthority(fsFolderEdit);
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(folderId[i]);
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(1);
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(21);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);   //插入预览记录
            }
        }else{
            FsFolder fsFolder = new FsFolder();
            fsFolder.setUpdateUserId(userId);
            fsFolder.setFolderId(fsFolderEdit.getFolderId());
            fsFolder.setFolderName(fsFolderEdit.getFolderName());
            fsFolder.setVisibleRange(fsFolderEdit.getVisible());
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setUpdateTime(ts);
            //更新目录信息
            fsFolderService.updateById(fsFolder);
            if (!"add".equals(authType)) {
                //删除权限信息
                docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", fsFolderEdit.getFolderId()));
            } else {
                //删除权限信息
                docFoldAuthorityService.remove(new QueryWrapper<DocFoldAuthority>().eq("folder_id", fsFolderEdit.getFolderId()).in("author_id", delAuth));
            }
            //保存权限信息
            flag = docFoldAuthorityService.saveDocFoldAuthority(fsFolderEdit);
            //保存权限信息
            List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
            flag = docFoldAuthorityService.saveDocFoldAuthority(fsFolderEdit);
            DocResourceLog docResourceLog = new DocResourceLog();
            String id = UUID.randomUUID().toString().replace("-", "");
            docResourceLog.setId(id);
            docResourceLog.setResourceId(fsFolderEdit.getFolderId());
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(1);
            docResourceLog.setUserId(userId);
            docResourceLog.setOperateType(22);
            docResourceLog.setValidFlag("1");
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            docInfoService.insertResourceLog(resInfoList);   //插入预览记录
        }
        return flag;
    }

    /**
     * 生成目录层级码
     *
     * @return
     */
    @RequiresPermissions("fsFolder:addLevel")
    @GetMapping("/addLevel")
    public void addLevel() {
        fsFolderService.addLevel(null);
    }

    /**
     * @return 目录权限页面调整后
     * @Author zoufeng
     * @Description 跳转到权限设置页面
     * @Date 14:36 2018/9/7
     * @Param []
     **/
    @GetMapping("/folderAuthority")
    public String folderAuthority(Model model) {
        String authType = super.getPara("authType");
        if (!"add".equals(authType)) {
            authType = "set";
        }
        model.addAttribute("authType", authType);
        return "/doc/manager/foldermanager/folderAuthority.html";
    }

    @GetMapping("/folderAuthority_manager")
    public String folderAuthorityManager() {
        return "/doc/manager/foldermanager/folderAuthority_manager.html";
    }
    
    /**
     * @Author zoufeng
     *
     * @Description 查询选中目录是否有可编辑权限
     * @Date 15:03 2018/10/16
     * @Param 
     * @return 
     **/
    @PostMapping("/checkIsEdit")
    @ResponseBody
    public boolean checkIsEdit(String chooseFolder){
        return fsFolderService.getIsEdit(chooseFolder);
    }

    /**
     * @param fsFolderParams 目录信息及权限信息，祥看FsFolderParams
     * @return java.lang.Object
     * @Author zoufeng
     * @Description 新增文件夹的保存方法
     * @Date 17:53 2018/9/7
     **/
    @PostMapping(value = "/addFolder")
    @ResponseBody
    public Object addFolder(FsFolderParams fsFolderParams) {

        Map<String, Object> result = new HashMap<>(5);
        FsFolder fsFolder = new FsFolder();
        String userId = ShiroKit.getUser().getId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        cacheToolService.updateLevelCodeCache(userId);
        fsFolder.setFolderId(fsFolderParams.getFolderId());
        fsFolder.setFolderName(fsFolderParams.getFolderName());
        fsFolder.setVisibleRange(fsFolderParams.getVisible());
        FsFolder fsFolderParent =  new FsFolder();
        fsFolderParent = fsFolderService.getById(fsFolderParams.getParentFolderId());
        String ownId = fsFolderParent.getOwnId();
        fsFolder.setParentFolderId(fsFolderParams.getParentFolderId());
        List<String> listGroup = docGroupService.getPremission(userId);
        String parentId=fsFolderParams.getParentFolderId();
        FsFolder pFolder = fsFolderService.getById(parentId);
        if(ownId!=null&&!"".equals(ownId)){
            fsFolder.setOwnId(ownId);
        }
       String parentLevelCode = pFolder.getLevelCode();
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
            folderAmount="4";
        }
        if((parentLevelCode.length()/4)>=Integer.parseInt(folderAmount)+1){
            result.put("fail", 1);
            result.put("folderAmount", folderAmount);
            return  result;
        }
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(parentId, listGroup, userId);
           if(isEdits!=2){
               result.put("fail", 2);
               return  result;
           }

        }

        if (ToolUtil.isNotEmpty(fsFolderParams.getFolderId())) {
            fsFolderService.updateFsFolder(fsFolder);
        } else {
            //新增一下主表
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");

            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 4-1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
                    String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName;
                }
                localName=localName+"\\"+fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            String  levelCode = fsFolder.getLevelCode();
            String localName="";
            for(int i=2;i<=levelCode.length()/4;i++){
                String levelCodeString = levelCode.substring(0,i*4);
                String folderName=  fsFolderService.getFolderNameByLevelCode(levelCodeString);
                localName=  localName+">"+folderName;
            }
            localName=   localName.substring(1,localName.length());
            result.put("localName", localName);
            result.put("folderId", folderId);

        }

        return result;
    }

    @PostMapping(value = "/isOwn")
    @ResponseBody
    public Object addFolders(String categoryId) {
        FsFolder fsFolder = new FsFolder();
        String isOwn = fsFolderService.getById(categoryId).getOwnId();
        if(isOwn==null||isOwn.equals("")){
            return "0";
        }else{
            return "1";
        }

    }

    @PostMapping(value = "/addFolders")
    @ResponseBody
    public Object addFolders(String pathContent,String categoryId) {
        Map<String, Object> result = new HashMap<>(5);
        FsFolder fsFolder = new FsFolder();
        String userId = ShiroKit.getUser().getId();
        List<String> roleList = ShiroKit.getUser().getRolesList();
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);

        String[] list=pathContent.split(",");
        List<String> listGroup = docGroupService.getPremission(userId);
        int max=0;
        for(int i=0;i<list.length;i++){

            String[] folderList=  list[i].split("/");
            if(folderList.length-1>max){
              max=  folderList.length-1;
            }
        }
        FsFolder parentFolder = fsFolderService.getById(categoryId);
        String parentLevelCode = parentFolder.getLevelCode();
        // 查询上级目录审核信息
        String parentAuditFlag = "0";
        List<FsFolderAuditor> folderAuditorList = new ArrayList<>();
        if("true".equals(using)){
            String auditFlag = parentFolder.getAuditFlag();
            if("1".equals(auditFlag)){
                parentAuditFlag = "1";
                // 查询目录审批人
                QueryWrapper<FsFolderAuditor> ew = new QueryWrapper<>();
                ew.eq("folder_id",categoryId);
                folderAuditorList = fsFolderAuditorService.list(ew);
            }
        }
        String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
        if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
            folderAmount="4";
        }
        if((parentLevelCode.length()/4)+max>Integer.parseInt(folderAmount)+1){
            result.put("fail", 1);
            result.put("folderAmount", folderAmount);
            return  result;
        }
        if (adminFlag != 1) {
            int isEdits = docFoldAuthorityService.findEditByUpload(categoryId, listGroup, userId);
            if(isEdits!=2){
                result.put("fail", 2);
                return  result;
            }

        }
        List<String> idList =new ArrayList<>();
        List<String> nameList =new ArrayList<>();
        for(int i=0;i<list.length;i++){

           String[] folderList=  list[i].split("/");
            String folderId=categoryId;
            for(int j=0;j<folderList.length-1;j++){
                folderId=  addFolder(folderList[j],folderId,parentAuditFlag,folderAuditorList);

            }
           String  levelCode = fsFolderService.getById(folderId).getLevelCode();
            String localName="";
            for(int n=2;n<=levelCode.length()/4;n++){
                String levelCodeString = levelCode.substring(0,n*4);
                String folderName=  fsFolderService.getFolderNameByLevelCode(levelCodeString);
                localName=  localName+">"+folderName;
            }
            localName=   localName.substring(1,localName.length());
            nameList.add(localName);

            idList.add(folderId);
        }
        result.put("idList",idList);
        result.put("localName", nameList);
        return  result;

    }
    public String addFolder(String name,String id,String auditFlag,List<FsFolderAuditor> fsFolderAuditorList){
        List<FsFolder> list = fsFolderService.addCheck(id, name,null);
        if(list.size()>0){
            return  list.get(0).getFolderId();
        }else{
            Date date = new Date();
            FsFolder fsFolder=new FsFolder();
            fsFolder.setFolderName(name);

            fsFolder.setParentFolderId(id);
             FsFolderParams fsFolderParams =new FsFolderParams();
            Timestamp ts = new Timestamp(date.getTime());
            fsFolder.setCreateTime(ts);
            fsFolder.setUpdateTime(ts);
            String folderId = UUID.randomUUID().toString().replaceAll("-", "");
            String userId = ShiroKit.getUser().getId();
            fsFolder.setFolderId(folderId);
            fsFolder.setCreateUserId(userId);
            String folderParentId = fsFolder.getParentFolderId();
            FsFolder fsFolderParent =  new FsFolder();
            fsFolderParent = fsFolderService.getById(fsFolder.getParentFolderId());
            if(fsFolderParent!=null){
            String ownId = fsFolderParent.getOwnId();
            if(ownId!=null&&!"".equals(ownId)){
                fsFolder.setOwnId(ownId);
            }
            }
            //生成levelCode
            if (folderParentId != null && !"".equals(folderParentId)) {
                FsFolder parentFolder = fsFolderService.getById(folderParentId);
                String parentCode = parentFolder.getLevelCode();
                String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
                fsFolder.setLevelCode(currentCode);
                String localName = "";
                for (int j = 1; j <= currentCode.length() / 4-1; j++) {
                    String levelCodeString = currentCode.substring(0, j * 4);
                    String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                    localName = localName + "\\" + folderName;
                }
                localName=localName+"\\"+fsFolder.getFolderName();
                fsFolder.setFolderPath(localName);
            }
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            fsFolderParams.setFolderId(folderId);
            fsFolderParams.setFolderName(name);
            fsFolder.setAuditFlag(auditFlag);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
            docFoldAuthorityService.saveDocFoldAuthority(fsFolderParams);
            // 添加目录审核人
            if("1".equals(auditFlag)){
                if(ToolUtil.isNotEmpty(fsFolderAuditorList)){
                    List<FsFolderAuditor> folderAuditorList = new ArrayList<>();
                    for(FsFolderAuditor fsFolderAuditor:fsFolderAuditorList){
                        FsFolderAuditor folderAuditor = new FsFolderAuditor();
                        folderAuditor.setFolderId(folderId);
                        folderAuditor.setAuditUserId(fsFolderAuditor.getAuditUserId());
                        folderAuditor.setAuditUserName(fsFolderAuditor.getAuditUserName());
                        folderAuditor.setCreator(userId);
                        folderAuditor.setCreateTime(ts);
                        folderAuditorList.add(folderAuditor);
                    }
                    if(ToolUtil.isNotEmpty(folderAuditorList)){
                        fsFolderAuditorService.saveBatch(folderAuditorList);
                    }
                }
            }
            return   folderId;
        }
    }
    @GetMapping("/addPath")
    public void addPath() {
        fsFolderService.addPath(null);
    }

    /**
     * 目录层级码扩位
     * @return 操作结果
     */
    @RequestMapping(value = "/updateFoldLevelCode")
    @ResponseBody
    public Tip updateFoldLevelCode(){
        Boolean flag = true;
        QueryWrapper<FsFolder> ew = new QueryWrapper<>();
        List<FsFolder> fsFolderList = fsFolderService.list(ew);
        try {
            if(ToolUtil.isNotEmpty(fsFolderList)){
                for(FsFolder fsFolder:fsFolderList){
                    String levelCode = fsFolder.getLevelCode();
                    StringBuilder sb = new StringBuilder();
                    if(ToolUtil.isNotEmpty(levelCode)){
                        int judge = levelCode.length() % 3;
                        if(judge != 0){
                            continue;
                        } else {
                            for(int i = 0;i < levelCode.length() / 3;i++){
                                int start = i * 3;
                                int end = (i + 1) * 3;
                                String code = levelCode.substring(start, end);
                                String newCode = "0" + code;
                                sb.append(newCode);
                            }
                        }
                    }
                    fsFolder.setLevelCode(sb.toString());
                }
                flag = fsFolderService.updateBatchById(fsFolderList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(flag){
            Tip tip = new SuccessTip();
            tip.setMessage("目录层级码更新成功");
            return tip;
        }else {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "目录层级码更新失败");
        }
    }

    /**
     * 查询上级目录审核信息
     * @param folderId 目录ID
     * @return 目录审核信息
     */
    @RequestMapping(value = "/getFoldAuditInfo")
    @ResponseBody
    public Map<String,Object> getFoldAuditInfo(String folderId) {
        Map<String,Object> result = new HashMap<>();
        if("true".equals(using)){ // 判断是否需要文件审核
            result = fsFolderAuditorService.getFoldAuditInfo(folderId);
            if(ToolUtil.isNotEmpty(result)){
                result.put("auditType",auditType);
                result.put("auditorRange",auditorRange);
                String deptId = ShiroKit.getUser().getDeptId();
//                // 是否需要审核
//                boolean flag = false;
//                if(null != result.get("auditFlag")){
//                    String auditFlag = result.get("auditFlag").toString();
//                    if("1".equals(auditFlag)){
//                        flag = true;
//                    }
//                }
//                if(!flag){
                    Map<String,Object> auditorMap = new HashMap<>();
                    if("1".equals(auditorRange)){
                        auditorMap = fsFolderAuditorService.getAuditor("",auditRole);
                    } else if("2".equals(auditorRange)){
                        auditorMap = fsFolderAuditorService.getAuditor(deptId,auditRole);
                    }
                    if(ToolUtil.isNotEmpty(auditorMap)){
                        String auditUserIds = auditorMap.get("auditUserIds").toString();
                        String auditUserNames = auditorMap.get("auditUserNames").toString();
                        result.put("auditUserIds",auditUserIds);
                        result.put("auditUserNames",auditUserNames);
                    }
//                }
                String parentFolderId = result.get("parentFolderId").toString();
                    FsFolder parentFolder = fsFolderService.getById(parentFolderId);
                    if(ToolUtil.isNotEmpty(parentFolder)){
                        String parentAuditFlag = parentFolder.getAuditFlag();
                        result.put("parentAuditFlag",parentAuditFlag);
                    }

            }
        }
        return result;
    }

    /**
     * 查询左侧树
     * @return 树数据
     */
    @RequestMapping({"/usersTree"})
    @ResponseBody
    public List tree(String folderId, String ids, String types) {
        // 定义返回值
        List<JSTreeModel> resultList= new ArrayList();
        List<JSTreeModel> result = new ArrayList();
        if("3".equals(auditorRange)){
            // 全体人员管理权限标志
            boolean allPersonFlag = false;
            // 查询有目录管理权限的人员
            List<DocFoldAuthority> docList = fsFolderAuditorService.queryFolderAuthority(folderId);
            FsFolder fsFolder = fsFolderService.getById(folderId);
            // 定义人员数据list
            List<Map<String, String>> userList = new ArrayList<>();
            // 添加当前登录人
            Map<String, String> own = new HashMap<>(16);
            own.put("userId", ShiroKit.getUser().getId());
            userList.add(own);
            if(ToolUtil.isNotEmpty(fsFolder)){
                String createUserId = fsFolder.getCreateUserId();
                // 添加目录创建人
                Map<String, String> creator = new HashMap<>(16);
                creator.put("userId", createUserId);
                userList.add(creator);
            }
            // 定义群组list
            List<String> groupStrList = new ArrayList<>();
            // 定义部门list
            List<String> organStrList = new ArrayList<>();
            if (docList != null && docList.size() > 0) {
                // 有目录管理权限的人，则查这些人
                for (DocFoldAuthority docFoldAuthority : docList ) {
                    // 区分管理权限是人员、群组、部门还是全体人员
                    String authorType = docFoldAuthority.getAuthorType();
                    if ("0".equals(authorType)) {
                        // 是人员
                        Map<String, String> map = new HashMap<>(16);
                        map.put("userId", docFoldAuthority.getAuthorId());
                        userList.add(map);
                    } else if ("1".equals(authorType)) {
                        // 是群组
                        // 群组id
                        String groupId = docFoldAuthority.getAuthorId();
                        groupStrList.add(groupId);
                    } else if ("2".equals(authorType)) {
                        // 是部门
                        // 部门id
                        String organId = docFoldAuthority.getOrganId();
                        organStrList.add(organId);
                    } else if ("3".equals(authorType)) {
                        // 是全体人员，则查所有人
                        allPersonFlag = true;
                        break;
                    }
                }
            }
            String[] idArr = ids.split(",");
            String[] typeArr = types.split(",");
            if(idArr.length == typeArr.length){
                for(int m = 0;m < idArr.length;m++){
                    String id = idArr[m];
                    String type = typeArr[m];
                    if ("0".equals(type)) {
                        // 是人员
                        Map<String, String> map = new HashMap<>(16);
                        map.put("userId", id);
                        userList.add(map);
                    } else if ("1".equals(type)) {
                        // 是群组
                        // 群组id
                        groupStrList.add(id);
                    } else if ("2".equals(type)) {
                        // 是部门
                        // 部门id
                        organStrList.add(id);
                    } else if ("3".equals(type)) {
                        // 是全体人员，则查所有人
                        allPersonFlag = true;
                        break;
                    }
                }
            }
            // 如果没有全体人员，则查询群组和部门下的所有人员
            if (!allPersonFlag) {
                // 查询群组下的人员
                if (groupStrList.size() > 0) {
                    // 根据群组id查询群组下的人员
                    List<Map<String, String>> groupUserList = fsFolderAuditorService.getGroupUser(groupStrList);
                    if (groupUserList != null && groupUserList.size() > 0) {
                        for(Map<String, String> userMap : groupUserList) {
                            Map<String, String> map = new HashMap<>(16);
                            map.put("userId", userMap.get("userId"));
                            userList.add(map);
                        }
                    }
                }
                // 查询部门下的人员
                if (organStrList.size() > 0) {
                    // 先查询所有部门的编码
                    String organIds = fsFolderAuditorService.getOrganIds(organStrList);
                    if (ToolUtil.isNotEmpty(organIds)) {
                        // 查询部门下的人员
                        List<Map<String, String>> organUsersList = fsFolderAuditorService.getOrganUsers(organIds);
                        if (organUsersList != null && organUsersList.size() > 0) {
                            for(Map<String, String> userMap : organUsersList) {
                                Map<String, String> map = new HashMap<>(16);
                                map.put("userId", userMap.get("userId"));
                                userList.add(map);
                            }
                        }
                    }
                }
            }
            // 全体人员有管理权限，则查所有人
            if (allPersonFlag) {
                result = sysOrgManageService.getUserTree();
            } else {
                // 获取选择审批人用户数据
                List<JSTreeModel> list = fsFolderAuditorService.getUserTree(userList);
                List<JSTreeModel> retList = this.computUserByRole(list);
                this.listOrder(retList);
                result = retList;
            }
        }

        // 添加根节点，并过滤掉管理员用户
        JSTreeModel jsTreeModel = new JSTreeModel();
        // 设置id
        jsTreeModel.setId("11");
        // 设置Code
        jsTreeModel.setCode("11");
        // 设置Text
        jsTreeModel.setText("系统用户");
        // 设置Parent
        jsTreeModel.setParent("#");
        // 设置Type
        jsTreeModel.setType("isRoot");
        result.add(jsTreeModel);

//        for ( int i=0;i<result.size();i++){
//            if(result.get(i).getId().equals("superadmin")||result.get(i).getId().equals("wkadmin")||
//                    result.get(i).getId().equals("auditadmin")||result.get(i).getId().equals("reviewadmin")||result.get(i).getId().equals("systemadmin")||result.get(i).getId().equals("businessadmin")||result.get(i).getId().equals("hussar")){
//                continue;
//            }else{
//                resultList.add(result.get(i));
//            }
//        }
        // 返回数据
        return result;
    }

    /**
     * 查询目录审批人
     * @param folderId 目录ID
     * @return 审批人数据
     */
    @RequestMapping("/getApprovalUser")
    @ResponseBody
    public List getApprovalUser(String folderId) {
        // 查询目录审批人
        QueryWrapper<FsFolderAuditor> ew = new QueryWrapper<>();
        ew.eq("folder_id",folderId);
        List<FsFolderAuditor> folderAuditorList = fsFolderAuditorService.list(ew);
        // 返回数据
        return folderAuditorList;
    }

    /**
     * 修改目录审核信息
     * @param folderId 目录ID
     * @param auditFlag 是否审核
     * @param auditorIds 审核人主键
     * @param auditorNames 审核人姓名
     * @return 操作结果
     */
    @PostMapping(value = "/updateFolderAudit")
    @ResponseBody
    public Object updateFolderAudit(String folderId, String auditFlag, String auditorIds, String auditorNames) {
        // 修改目录是否审核
        FsFolder fsFolder = new FsFolder();
        fsFolder.setFolderId(folderId);
        fsFolder.setAuditFlag(auditFlag);
        fsFolderService.updateById(fsFolder);

        if (!workflowUsing) {
            // 删除目录审核人信息
            QueryWrapper<FsFolderAuditor> wrapper = new QueryWrapper<>();
            wrapper.eq("folder_id", folderId);
            fsFolderAuditorService.remove(wrapper);
            // 保存目录审核人信息
            if ("1".equals(auditFlag)) { // 需要审核
                fsFolderAuditorService.addFolderAuditor(folderId, auditorIds, auditorNames);
            }
            if ("3".equals(auditType)) { // 审核类型为3
                FsFolder oldFolder = fsFolderService.getById(folderId);
                if (ToolUtil.isNotEmpty(oldFolder)) {
                    String oldAuditFlag = oldFolder.getAuditFlag();
                    if ("0".equals(oldAuditFlag) && "1".equals(auditFlag)) { // 由无需审核改为需要审核
                        // 更新子目录是否审核
                        String levelCode = oldFolder.getLevelCode();
                        QueryWrapper<FsFolder> ew = new QueryWrapper<>();
                        ew.like("level_code", levelCode + "%");
                        FsFolder temp = new FsFolder();
                        temp.setAuditFlag("1");
                        fsFolderService.update(temp, ew);
                    }
                }
            }
        }
        return SUCCESS_TIP;
    }

    /**
     * 判断当前目录审核信息是否可以修改
     * @param folderId 上级目录ID
     * @return 是否
     */
    @RequestMapping("/judgeToEditAudit")
    @ResponseBody
    public Boolean judgeToEditAudit(String folderId) {
        boolean flag = true;
        if (workflowUsing) {
            FsFolder fsFolder = fsFolderService.getById(folderId);
            if (ToolUtil.isNotEmpty(fsFolder)) {
                String auditFlag = fsFolder.getAuditFlag();
                if ("0".equals(auditFlag)) {
                    flag = false;
                }
            }
        } else {
            if ("1".equals(auditorRange) || "2".equals(auditorRange)) {
                if ("1".equals(auditType)) {
                    flag = false;
                } else if ("3".equals(auditType)) {
                    // 判断上级目录是否需要审核
                    FsFolder fsFolder = fsFolderService.getById(folderId);
                    if (ToolUtil.isNotEmpty(fsFolder)) {
                        String auditFlag = fsFolder.getAuditFlag();
                        if ("1".equals(auditFlag)) {
                            flag = false;
                        }
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 判断目录下是否存在待审核文件
     * @param fsFolderIds 目录ID
     * @return 是否
     */
    @PostMapping(value = "/checkAuditDoc")
    @ResponseBody
    public Boolean checkAuditDoc(@RequestParam String fsFolderIds) {
        boolean flag = false;
        String[] folderIdArr = fsFolderIds.split(",");
        for(String fsFolderId:folderIdArr){
            String auditDocIds = fsFolderService.checkAuditDoc(fsFolderId);
            if(ToolUtil.isNotEmpty(auditDocIds)){
                flag = true;
                break;
            }
        }
        return flag;
    }

    private List<JSTreeModel> computUserByRole(List<JSTreeModel> list) {
        List<JSTreeModel> resultList = new ArrayList();
        this.computeUserRecursion(list, resultList, "");
        // 返回数据
        return resultList;
    }

    private void computeUserRecursion(List<JSTreeModel> listData, List<JSTreeModel> resultList, String pId) {
        for(int i = 0; i < listData.size(); ++i) {
            if (i > listData.size()) {
                i = 0;
            }

            JSTreeModel tmp = (JSTreeModel)listData.get(i);
            if ("USER".equals(tmp.getType()) || pId.equals(tmp.getId())) {
                resultList.add(tmp);
                listData.remove(tmp);
                this.computeUserRecursion(listData, resultList, tmp.getParent());
            }
        }
    }
    private void listOrder(List<JSTreeModel> list) {
        Collections.sort(list, new Comparator<JSTreeModel>() {
            public int compare(JSTreeModel o1, JSTreeModel o2) {
                if (formateObj(o1.getFirstOrder()).compareTo(formateObj(o2.getFirstOrder())) == 0) {
                    if (formateObj(o1.getStruLevel()).compareTo(formateObj(o2.getStruLevel())) == 0) {
                        // 返回数据
                        return formateObj(o1.getStruOrder()).compareTo(formateObj(o2.getStruOrder())) == 0 ? formateObj(o1.getText()).compareTo(formateObj(o2.getText())) : formateObj(o1.getStruOrder()).compareTo(formateObj(o2.getStruOrder()));
                    } else {
                        // 返回数据
                        return formateObj(o1.getStruLevel()).compareTo(formateObj(o2.getStruLevel()));
                    }
                } else {
                    // 返回数据
                    return formateObj(o1.getFirstOrder()).compareTo(formateObj(o2.getFirstOrder()));
                }
            }
        });
    }

    private String formateObj(Object object) {
        // 返回数据
        return object == null ? "" : object.toString();
    }

    @RequestMapping("/copyDire")
    @ResponseBody
    public boolean copyDire(String folderIds, String pId, String isCopyAuth){
        return  fsFolderService.copyDire(folderIds, pId, isCopyAuth);
    }

    @RequestMapping(value = "/layerNum")
    @ResponseBody
    public String layerNum(String ids, String parentFolderId) {
        String res = "success";
        String parentLevelCode = fsFolderService.getById(parentFolderId).getLevelCode();
        String[] folderIds = ids.split(",");
        for (int i = 0; i < folderIds.length; i++) {
            String id = folderIds[i];
            int codeNum = fsFolderService.getChildCodeCount(id);
            int totalCode = parentLevelCode.length() / 3 + codeNum;
            String folderAmount = docConfigService.getConfigValueByKey("folder_amount");
            if(folderAmount ==null||Integer.parseInt(folderAmount)<4){
                folderAmount="4";
            }
            if (totalCode > Integer.parseInt(folderAmount)) {
                res = folderAmount;
                return res;
            }
        }
        return res;
    }
}
