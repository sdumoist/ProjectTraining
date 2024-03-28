package com.jxdinfo.doc.unstructured;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.*;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.system.service.SysUserService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.doc.newupload.thread.ChangeToPdfThread;
import com.jxdinfo.doc.newupload.thread.ToUpload;
import com.jxdinfo.doc.unstructured.model.PlatformSystemInfo;
import com.jxdinfo.doc.unstructured.service.PlatformSystemInfoService;
import com.jxdinfo.doc.unstructured.service.UnstructurePlatformApiService;
import com.jxdinfo.hussar.bsp.permit.model.SysOnline;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.bsp.permit.service.ISysOnlineHistService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.bsp.theme.service.IThemeService;
import com.jxdinfo.hussar.common.constant.state.UserStatus;
import com.jxdinfo.hussar.config.properties.EncryptTypeProperties;
import com.jxdinfo.hussar.core.log.HussarLogManager;
import com.jxdinfo.hussar.core.log.factory.LogTaskFactory;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.shiro.encrypt.AbstractCredentialsMatcher;
import com.jxdinfo.hussar.core.support.HttpKit;
import dm.jdbc.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/unstruct")
public class UnstructuredPlatformApi {


    /**
     * 日志
     */
    private static Logger LOGGER = LoggerFactory.getLogger(UnstructuredPlatformApi.class);


    /**
     * 目录管理服务类
     */
    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private IFsFolderService iFsFolderService;

    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;


    /**
     * 文件服务
     */
    @Autowired
    private FsFileService fsFileService;

    @Autowired
    private ESUtil esUtil;


    /**
     * 配置信息服务层
     */
    @Resource
    private DocConfigureService docConfigureService;

    /**
     * 文件处理
     */
    @Autowired
    private FilesService filesService;

    /**
     * 非结构化平台系统注册服务
     */
    @Autowired
    private PlatformSystemInfoService platformSystemInfoService;

    /**
     * token工具
     */
    @Autowired
    private UnstructureTokenUtil unstructureTokenUtil;

    @Autowired
    private UnstructurePlatformApiService unstructurePlatformApiService;

    @Autowired
    private ISysUsersService sysUserService;

    @Value("${docbase.filedir}")
    private String tempdir;

    @Autowired
    private ShareResourceService shareResourceService;

    /**
     * 文档服务类
     */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    @Autowired
    private CacheToolService cacheToolService;

    @Resource
    private EncryptTypeProperties encryptTypeProperties;
    /**
     * 加密算法
     */
    @Resource
    private AbstractCredentialsMatcher credentialsMatcher;
    @Resource
    private ISysUsersService iSysUsersService;
    @Resource
    private IThemeService themeService;
    @Resource
    private ISysOnlineHistService iSysOnlineHistService;
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

    /**
     * 运营支撑接口服务类
     */
    @Autowired
    private SysUserService userService;

    @Value("${docbase.breakdir}")
    private String breakdir;

    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;
    @Value("${isProject.using}")
    private boolean projectFlag;
    @Value("${docbase.isRole}")
    private boolean isRole;

    @Resource
    private UploadService uploadService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    private static ThreadPoolExecutor threadPoolExecutor = ToUpload.getPdfThreadPoolExecutor();

    /**
     * 交接文件接口
     * @param oldUserId 原来的用户id
     * @param newUserId  新用户id
     */
    @RequestMapping("/handOverFile")
    public void handOverFile(String oldUserId, String newUserId) {
        System.out.println("文件交接开始: 将用户: " + oldUserId + " 的文件，交接给用户: " + newUserId);
        QueryWrapper<DocInfo> infoQueryWrapper = new QueryWrapper<>();
        infoQueryWrapper.eq("USER_ID", oldUserId);
        List<DocInfo> files = docInfoService.list(infoQueryWrapper);
        if (files != null && files.size() > 0) {
            int count = 1;
            for (DocInfo file : files) {
                DocInfo info = new DocInfo();
                info.setDocId(file.getDocId());
                info.setUserId(newUserId);
                info.setAuthorId(newUserId);
                info.setContactsId(newUserId);
                docInfoService.updateById(info);

                // 同步es权限
                List<String> fileIds = new ArrayList<>();
                fileIds.add(file.getDocId());
                docFileAuthorityService.generateFileAuthorityToEs(fileIds);
                System.out.println("处理文件: " + file.getDocId() +"  交接进度: " + count + " / " + files.size());
                count ++;
            }
        }
        System.out.println("文件交接结束，共处理文件: " + files.size());
    }

    /**
     * 文件夹打包成zip下载
     *
     * @param downloadFoldIds 文件夹id
     * @param response        响应
     */
    @RequestMapping("/foldDown")
    public void foldDown(String downloadFoldIds, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("========文件夹下载开始=============目录id: " + downloadFoldIds + "  下载者: " + ShiroKit.getUser().getName());
        long time1 = System.currentTimeMillis();
        try {
            filesService.foldDownload(downloadFoldIds, request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time2 = System.currentTimeMillis();
        System.out.println("========文件夹下载结束=============用时: " + (time2 - time1));
    }

    /**
     * 获取token 单点登录用
     * username 系统名称
     * password 用户id
     * 出参: result -> token
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/getLoginTokenApi")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JSONObject getLoginTokenApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();

        // 获取加密参数
        String username = requestBody.get("username");
        String password = requestBody.get("password");

        // 校验参数
        if (StringUtils.isAnyEmpty(username, password)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        try {
            // 参数解密
            String keyword = "docbase";
            byte[] usernameByte = AesUtil.parseHexStr2Byte(username);
            String usernameContent = new String(AesUtil.decrypt(usernameByte, keyword), "utf-8");
            System.out.println("==================用户获取token===================" + usernameContent);
            byte[] passwordByte = AesUtil.parseHexStr2Byte(password);
            String passwordContent = new String(AesUtil.decrypt(passwordByte, keyword), "utf-8");

            // 校验用户是否存在
            String flag = userService.checkUser(usernameContent, passwordContent).get(0);
            if ("0".equals(flag)) {
                jsonResult.put("code", "3");
                jsonResult.put("msg", "用户名或密码不存在");
                return jsonResult;
            }

            // 生成token
            jsonResult = unstructureTokenUtil.getLoginToken(username);
            jsonResult.put("code", "1");
            jsonResult.put("msg", "success");
        } catch (Exception e) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "获取token异常");
            e.printStackTrace();
        }
        return jsonResult;
    }


    /**
     * 注册系统
     * 入参: systemId 系统id
     * systemName 系统名称
     * userId 用户id
     * userName 用户名称
     * 出参: systemId 系统id
     * systemKey 系统key
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/systemRegisterApi")
    @ResponseBody
    public JSONObject systemRegisterApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String systemId = requestBody.get("systemId");
        String systemName = requestBody.get("systemName");
        String userId = requestBody.get("userId");
        String userName = requestBody.get("userName");

        // 校验参数
        if (StringUtils.isAnyEmpty(systemName, userId)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        // 验证用户
        SysUsers user = sysUserService.getById(userId);
        if (user == null) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "操作用户不存在");
            return jsonResult;
        }

        if (StringUtils.isEmpty(systemId)) {
            systemId = UUID.randomUUID().toString().replace("-", "");
        }

        List<PlatformSystemInfo> systems = platformSystemInfoService.list(new QueryWrapper<PlatformSystemInfo>().eq("valid_flag", "1").eq("system_id", systemId).or().eq("system_name", systemName));

        if (systems != null && systems.size() > 0) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "系统已存在");
            return jsonResult;
        }
        // 生成系统秘钥
        String systemKey = RandomUtil.randomString(6);
        try {
            unstructurePlatformApiService.systemRegister(systemId, systemName, systemKey, userId);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("code", "4");
            jsonResult.put("msg", "注册系统异常");
            return jsonResult;
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("systemId", systemId);
        jsonData.put("systemKey", systemKey);
        jsonResult.put("result", jsonData);
        return jsonResult;
    }


    /**

     * @return
     */
    @RequestMapping(value = "/trans/pdf")
    @ResponseBody
    public JSONObject transPdf(String type, String ids) {
        // 返回信息
        JSONObject jsonResult = new JSONObject();
        System.out.println("transPdf " + type + " " +ids);

        if (StringUtils.isNotEmpty(type) && StringUtils.equals(type, "all")) {
            //从数据库中获取上传状态的列表
            List<Map<String, String>> list = uploadService.getUploadState();

            // 循环待转换文件列表
            for (Map map : list) {
                String times = null;
                if (map.containsKey("times")) {
                    times = map.get("times").toString();
                }

                String docId = "";
                if (map.containsKey("docId")) {
                    docId = map.get("docId").toString();
                }
                if (null != map.get("state") && times!=null) {
                    //获得每个文件信息的上传状态，并进行判断
                    String state = map.get("state").toString();
                    int time = Integer.parseInt(times);
                    if (StringUtils.isNotEmpty(docId) && time < 4) {
                        try {
                            switch (state) {
                                case "1"://如果状态为1，则为已上传未转化pdf
                                    threadPoolExecutor.execute(new ChangeToPdfThread(docId));
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception ex) {
                            LOGGER.info(docId + "transPdf转换线程异常");
                            ex.printStackTrace();
                        }
                    }
                }
            }
        } else {
            if (StringUtils.isNotEmpty(ids)) {
                String[] id = ids.split(",");
                for (String docId : id) {
                    threadPoolExecutor.execute(new ChangeToPdfThread(docId));
                }
            }
        }
        return jsonResult;
    }


    /**
     * 获取token
     * 入参: systemId 系统id
     * systemName 系统名称
     * userId 用户id
     * userName 用户名称
     * 出参: result -> token
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/getAccessTokenApi")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JSONObject getAccessTokenApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String systemId = requestBody.get("systemId");
        String systemKey = requestBody.get("systemKey");
        String userId = requestBody.get("userId");
        String userName = requestBody.get("userName");


        // 校验参数
        if (StringUtils.isAnyEmpty(systemId, systemKey, userId, userName)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        // 验证操作
        SysUsers user = sysUserService.getById(userId);
        if (user == null) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "操作用户不存在");
            return jsonResult;
        }

        // 验证系统是否存在
        List<PlatformSystemInfo> systems = platformSystemInfoService.list(new QueryWrapper<PlatformSystemInfo>().eq("system_id", systemId).eq("system_key", systemKey));
        if (systems == null || systems.size() == 0) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "系统不存在");
            return jsonResult;
        }

        // 获取token
        JSONObject token = unstructureTokenUtil.getToken(systemId, userId, userName);

        jsonResult.put("result", token);
        return jsonResult;
    }

    /**
     * 验证token
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/testAccessTokenApi")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JSONObject testAccessTokenApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        // 获取token
        JSONObject token = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(token.getString("code"), "1")) {
            JSONObject result = new JSONObject();
            result.put("validTime", token.getLongValue("validTime"));
            jsonResult.put("result", result);
        } else {
            return token;
        }
        return jsonResult;
    }


    /**
     * 新增目录(单独)
     * 权限说明: 继承父级目录的权限
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/folderAddApi")
    @ResponseBody
    public JSONObject addFolder(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String parentId = requestBody.get("parentId");
        String folderName = requestBody.get("folderName");
        String folderId = requestBody.get("folderId");

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, folderName, parentId)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }
        FsFolder parentFolder = fsFolderService.getById(parentId);
        if (parentFolder == null) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "上级目录不存在");
            return jsonResult;
        }

        // 目录id
        if (StringUtils.isEmpty(folderId)) {
            folderId = UUID.randomUUID().toString().replace("-", "");
        }

        FsFolder fsFolderOld = fsFolderService.getById(folderId);
        if (fsFolderOld != null) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "目录已存在");
            return jsonResult;
        }

        List<FsFolder> fsFolderOlds = fsFolderService.list(new QueryWrapper<FsFolder>().eq("folder_name", folderName).eq("parent_folder_id", parentId));

        if (fsFolderOlds != null && fsFolderOlds.size() > 0) {
            jsonResult.put("code", "5");
            jsonResult.put("msg", "父级目录下存在同名目录");
            return jsonResult;
        }
        try {
            unstructurePlatformApiService.folderAdd(folderId, folderName, parentId, userId);
        } catch (Exception e) {
            jsonResult.put("code", "6");
            jsonResult.put("msg", "新增目录异常");
            return jsonResult;
        }
        JSONObject jsonData = new JSONObject();
        jsonData.put("folderId", folderId);
        jsonData.put("folderName", folderName);
        jsonData.put("parentId", parentId);
        jsonResult.put("result", jsonData);
        return jsonResult;
    }


    /**
     * 新增目录(集合)
     * 权限说明: 继承父级目录的权限
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/foldersAddApi")
    @ResponseBody
    public JSONObject addFolders(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String folderList = requestBody.get("folderList");

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        JSONArray folders = JSONArray.parseArray(folderList);
        JSONArray successFoldList = new JSONArray();
        JSONArray errorFoldIdList = new JSONArray();
        JSONArray successFoldIdList = new JSONArray();
        StringBuilder errorMsg = new StringBuilder();

        try {
            if (folders != null && folders.size() > 0) {
                unstructurePlatformApiService.foldersAdd(folders, successFoldList, errorFoldIdList, successFoldIdList, errorMsg, userId);
            } else {
                jsonResult.put("code", "4");
                jsonResult.put("msg", "传入的目录集合为空");
                return jsonResult;
            }
        } catch (Exception e) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "新增目录异常");
            return jsonResult;
        }

        if (successFoldList.size() > 0) {
            JSONObject foldInfo = new JSONObject();
            foldInfo.put("foldrList", successFoldList);
            foldInfo.put("count", successFoldList.size());
            jsonResult.put("result", foldInfo);
        }
        // 组装返回信息
        if (errorMsg.length() > 0) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", errorMsg.toString());
            jsonResult.put("errorFoldIds", errorFoldIdList);
        }
        if (successFoldIdList.size() > 0) {
            jsonResult.put("sucFoldIds", successFoldIdList);
        }
        return jsonResult;
    }

    /**
     * 修改目录
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/folderEditApi")
    @ResponseBody
    public JSONObject editFolder(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String parentId = requestBody.get("parentId");
        String folderName = requestBody.get("folderName");
        String folderId = requestBody.get("folderId");


        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, folderName, parentId, folderId)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }
        FsFolder parentFolder = fsFolderService.getById(parentId);
        if (parentFolder == null) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "上级目录不存在");
            return jsonResult;
        }
        List<FsFolder> fsFolderOlds = fsFolderService.list(new QueryWrapper<FsFolder>().eq("folder_name", folderName).eq("parent_folder_id", parentId).ne("folder_id", folderId));

        if (fsFolderOlds != null && fsFolderOlds.size() > 0) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "父级目录下存在同名目录");
            return jsonResult;
        }

        // 目录id
        if (StringUtils.isEmpty(folderId)) {
            folderId = UUID.randomUUID().toString().replace("-", "");
        }

        // 目录基本信息
        FsFolder fsFolder = new FsFolder();
        fsFolder.setFolderId(folderId);
        fsFolder.setFolderName(folderName);
        fsFolder.setParentFolderId(parentId);
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        fsFolder.setUpdateTime(ts);

        //保存目录信息
        fsFolderService.updateById(fsFolder);

        // 添加操作记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(folderId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(1); // 0:FILE,1:FOLD,2:topic
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(1); // 0：上传,1:编辑,2:删除,3:预览,4:下载,5收藏,6分享,7修改,8重命名
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);

        JSONObject jsonData = new JSONObject();
        jsonData.put("folderId", folderId);
        jsonData.put("folderName", folderName);
        jsonData.put("parentId", parentId);
        jsonResult.put("result", jsonData);
        return jsonResult;
    }

    /**
     * 目录删除
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/foldersDelApi")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JSONObject deleteFolder(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String folderIds = requestBody.get("folderIds");
        String cascadeType = requestBody.get("cascadeType"); // 是否级联删除 1：是2：否
        String fileDelType = requestBody.get("fileDelType"); // 是否删除文件 1：是2：否

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, folderIds, cascadeType, fileDelType)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        try {
            unstructurePlatformApiService.foldersDel(folderIds, cascadeType, fileDelType, userId);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("code", "3");
            jsonResult.put("msg", "目录删除异常:" + e.toString());
            return jsonResult;
        }
        return jsonResult;
    }


    /**
     * 文件上传(单文件上传, 参数是base64文件)
     * 权限说明: 继承目录的权限 但是权限类型为下载
     *
     * @return
     */
    @RequestMapping("/fileUploadApi")
    @ResponseBody
    public JSONObject upload(@RequestBody JSONObject requestBody) {
        System.out.println("====================文件上传接口===============");

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.getString("accessToken");
        String fileStream = requestBody.getString("fileStream");
        String fileId = requestBody.getString("fileId");
        String folderId = requestBody.getString("folderId");
        String fileName = requestBody.getString("fileName");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileId, folderId, fileName, fileStream)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        byte[] byteFile = null;
        String userId = "";
        try {
            fileId = StringUtil.isEmpty(fileId) ? fileId : new String(new BASE64Decoder().decodeBuffer(fileId));
            folderId = StringUtil.isEmpty(folderId) ? folderId : new String(new BASE64Decoder().decodeBuffer(folderId));
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));

            // 验证token
            JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
            if (StringUtils.equals(validToken.getString("code"), "1")) {
                userId = validToken.getString("userId");
            } else {
                return validToken;
            }

            fileName = StringUtil.isEmpty(fileName) ? fileName : new String(new BASE64Decoder().decodeBuffer(fileName), "UTF-8");
            System.out.println("====================上传文件名称===============" + fileName);
            long time1 = System.currentTimeMillis();
            System.out.println("fileStream大小============" + (StringUtils.isEmpty(fileStream) ? "0" : fileStream.length()));
            byteFile = new BASE64Decoder().decodeBuffer(fileStream);
            long time2 = System.currentTimeMillis();
        } catch (IOException e) {
            System.out.println("====================文件=" + fileName + "上传异常");
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }

        // 校验文件夹是否存在
        FsFolder folder = fsFolderService.getById(folderId);
        if (null == folder) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "目录不存在");
            return jsonResult;
        }
        // 校验文档是否存在
        DocInfo docInfo = docInfoService.getById(fileId);
        if (docInfo != null) {
            jsonResult.put("code", "0");
            jsonResult.put("msg", "文件已存在");
            return jsonResult;
        }

        // 校验文件名称长度
        if (fileName.length() > 200) {
            jsonResult.put("code", "0");
            jsonResult.put("msg", "文件名称长度超出限制");
            return jsonResult;
        }

        // 校验文件名称是否合法
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            jsonResult.put("code", "0");
            jsonResult.put("message", "文件名称不合法");
            return jsonResult;
        }

        // 校验文件格式是否支持
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            jsonResult.put("code", "0");
            jsonResult.put("msg", "文档格式不支持");
            return jsonResult;
        }

        // 校验文件名称是否存在
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        List<String> nameList = docInfoService.checkFileExist(docNameList, folderId);
        if (nameList != null && nameList.size() != 0) {
            jsonResult.put("code", "0");
            jsonResult.put("msg", "目录下存在同名文件");
            return jsonResult;
        }

        try {
            // 创建文件
            String random = UUID.randomUUID().toString().replace("-", "");
            File sourceFile = new File(tempdir + File.separator + random + suffix);
            if (!sourceFile.getParentFile().exists()) {
                sourceFile.getParentFile().mkdirs();
            }
            sourceFile.createNewFile();

            // 将字节中的内容 写入文件
            if (byteFile != null) {
                FileOutputStream fos = new FileOutputStream(sourceFile);
                fos.write(byteFile, 0, byteFile.length);
                fos.close();
            }
            // 上传文件
            System.out.println("上传文件开始");
            filesService.uploadFileApi(sourceFile, fileId, fileName, folderId, userId);
            System.out.println("上传文件完成");
        } catch (Exception e) {
            LOGGER.error("文件上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "上传失败");
            return jsonResult;
        }
        JSONObject docIdJson = new JSONObject();
        docIdJson.put("fileId", fileId);
        jsonResult.put("result", docIdJson);
        return jsonResult;
    }

    /**
     * 上传文件 适合调用者是File格式文件
     *
     * @param file
     * @param fileId
     * @param folderId
     * @param accessToken
     * @param fileName
     * @return
     */
    @PostMapping("/uploadFileApi")
    @ResponseBody
    public JSONObject uploadFileAp(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "fileId", required = false) String fileId, @RequestParam(value = "folderId") String folderId, @RequestParam(value = "accessToken") String accessToken, @RequestParam(value = "fileName") String fileName) {
        System.out.println("文件上传uploadFileApi");

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileId, folderId, fileName) || file == null) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        /*try {
            fileId = StringUtil.isEmpty(fileId) ? fileId : new String(new BASE64Decoder().decodeBuffer(fileId));
            folderId = StringUtil.isEmpty(folderId) ? folderId : new String(new BASE64Decoder().decodeBuffer(folderId));
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));
            fileName = StringUtil.isEmpty(fileName) ? fileName : new String(new BASE64Decoder().decodeBuffer(fileName), "UTF-8");
            System.out.println("解码后的fileName" + fileName);
        } catch (IOException e) {
            System.out.println("上传文件异常" + e);
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }*/

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验文件夹是否存在
        FsFolder folder = fsFolderService.getById(folderId);
        if (null == folder) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "目录不存在");
            return jsonResult;
        }
        // 校验文档是否存在
        DocInfo docInfo = docInfoService.getById(fileId);
        if (docInfo != null) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "文件已存在");
            return jsonResult;
        }

        // 校验文件名称长度
        if (fileName.length() > 200) {
            jsonResult.put("code", "5");
            jsonResult.put("msg", "文件名称长度超出限制");
            return jsonResult;
        }

        // 校验文件名称是否合法
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            jsonResult.put("code", "6");
            jsonResult.put("message", "文件名称不合法");
            return jsonResult;
        }

        // 校验文件格式是否支持
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            jsonResult.put("code", "7");
            jsonResult.put("msg", "文档格式不支持");
            return jsonResult;
        }

        // 校验文件名称是否存在
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        List<String> nameList = docInfoService.checkFileExist(docNameList, folderId);
        if (nameList != null && nameList.size() != 0) {
            jsonResult.put("code", "8");
            jsonResult.put("msg", "目录下存在同名文件");
            return jsonResult;
        }

        try {
            // 创建文件
            String random = UUID.randomUUID().toString().replace("-", "");
            File sourceFile = new File(tempdir + File.separator + random + suffix);
            if (!sourceFile.getParentFile().exists()) {
                sourceFile.getParentFile().mkdirs();
            }
            sourceFile.createNewFile();
            file.transferTo(sourceFile);

            // 上传文件
            System.out.println("File上传文件开始");
            filesService.uploadFileApi(sourceFile, fileId, fileName, folderId, userId);
            System.out.println("File上传文件完成");
        } catch (Exception e) {
            LOGGER.error("文件上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            jsonResult.put("code", "9");
            jsonResult.put("msg", "上传失败");
            return jsonResult;
        }
        JSONObject docIdJson = new JSONObject();
        docIdJson.put("fileId", fileId);
        jsonResult.put("result", docIdJson);
        return jsonResult;
    }


    /**
     * 文件上传(单文件上传 文件为MultipartFile)
     * 权限说明: 继承目录的权限 但是权限类型为下载
     *
     * @return
     */
    @RequestMapping("/multiPartFileUploadApi")
    @ResponseBody
    public JSONObject upload(@RequestParam(value = "file") MultipartFile file, @RequestParam(value = "fileId") String fileId, @RequestParam(value = "accessToken") String accessToken, @RequestParam(value = "folderId") String folderId) {
        System.out.println("文件上传multiPartFileUploadApi");

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileId, folderId) || file == null) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        /*try {
            fileId = StringUtil.isEmpty(fileId) ? fileId : new String(new BASE64Decoder().decodeBuffer(fileId));
            folderId = StringUtil.isEmpty(folderId) ? folderId : new String(new BASE64Decoder().decodeBuffer(folderId));
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));
            fileName = StringUtil.isEmpty(fileName) ? fileName : new String(new BASE64Decoder().decodeBuffer(fileName), "UTF-8");
            System.out.println("解码后的fileName" + fileName);
        } catch (IOException e) {
            System.out.println("上传文件异常" + e);
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }*/

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验文件夹是否存在
        FsFolder folder = fsFolderService.getById(folderId);
        if (null == folder) {
            jsonResult.put("code", "3");
            jsonResult.put("msg", "目录不存在");
            return jsonResult;
        }
        // 校验文档是否存在
        DocInfo docInfo = docInfoService.getById(fileId);
        if (docInfo != null) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "文件已存在");
            return jsonResult;
        }

        String fileName = file.getOriginalFilename();

        // 校验文件名称长度
        if (fileName.length() > 200) {
            jsonResult.put("code", "5");
            jsonResult.put("msg", "文件名称长度超出限制");
            return jsonResult;
        }

        // 校验文件名称是否合法
        String regex = "^[^'\"\\|\\\\]*$";
        if (Pattern.compile(regex).matcher(fileName).find() == false) {
            jsonResult.put("code", "6");
            jsonResult.put("message", "文件名称不合法");
            return jsonResult;
        }

        // 校验文件格式是否支持
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        List<DocConfigure> typeList = docConfigureService.getConfigure();
        if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
            jsonResult.put("code", "7");
            jsonResult.put("msg", "文档格式不支持");
            return jsonResult;
        }

        // 校验文件名称是否存在
        List<String> docNameList = new ArrayList<String>();
        docNameList.add(fileName);
        List<String> nameList = docInfoService.checkFileExist(docNameList, folderId);
        if (nameList != null && nameList.size() != 0) {
            jsonResult.put("code", "8");
            jsonResult.put("msg", "目录下存在同名文件");
            return jsonResult;
        }

        try {
            // 创建文件
            String random = UUID.randomUUID().toString().replace("-", "");
            File sourceFile = new File(tempdir + File.separator + random + suffix);
            if (!sourceFile.getParentFile().exists()) {
                sourceFile.getParentFile().mkdirs();
            }
            sourceFile.createNewFile();
            System.out.println("multipartFile转换file开始");
            long time1 = System.currentTimeMillis();
            file.transferTo(sourceFile);
            long time2 = System.currentTimeMillis();
            System.out.println("multipartFile转换file结束 " + (time2 - time1));

            // 上传文件
            System.out.println("multipartFile上传文件开始");
            filesService.uploadFileApi(sourceFile, fileId, fileName, folderId, userId);
            System.out.println("multipartFile上传文件完成");
        } catch (Exception e) {
            LOGGER.error("文件上传失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            jsonResult.put("code", "9");
            jsonResult.put("msg", "上传失败");
            return jsonResult;
        }
        JSONObject docIdJson = new JSONObject();
        docIdJson.put("fileId", fileId);
        jsonResult.put("result", docIdJson);
        return jsonResult;
    }


    /**
     * 文件上传(分片上传)
     * 权限说明: 继承目录的权限 但是权限类型为下载
     *
     * @return
     */
    @RequestMapping("/chunkFileUploadApi")
    @ResponseBody
    public JSONObject chunkFileUploadApi(@RequestBody JSONObject requestBody) {
        System.out.println("====================chunkFileUploadApi===============");

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.getString("accessToken");
        String fileId = requestBody.getString("fileId");
        String folderId = requestBody.getString("folderId");
        String fileName = requestBody.getString("fileName");
        String chunkType = requestBody.getString("chunkType");
        String identification = requestBody.getString("identification");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileId, folderId, fileName)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        String userId = "";
        try {
            // 验证token
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));
            JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
            if (StringUtils.equals(validToken.getString("code"), "1")) {
                userId = validToken.getString("userId");
            } else {
                return validToken;
            }

            // 解密参数
            fileId = StringUtil.isEmpty(fileId) ? fileId : new String(new BASE64Decoder().decodeBuffer(fileId));
            folderId = StringUtil.isEmpty(folderId) ? folderId : new String(new BASE64Decoder().decodeBuffer(folderId));
            fileName = StringUtil.isEmpty(fileName) ? fileName : new String(new BASE64Decoder().decodeBuffer(fileName), "UTF-8");
            chunkType = StringUtil.isEmpty(chunkType) ? chunkType : new String(new BASE64Decoder().decodeBuffer(chunkType));
            identification = StringUtil.isEmpty(identification) ? identification : new String(new BASE64Decoder().decodeBuffer(identification));
        } catch (IOException e) {
            System.out.println("====================文件=" + fileName + "上传异常");
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }
        // 分片上传
        if (StringUtils.equals(chunkType, "chunk")) {
            String chunkS = requestBody.getString("chunk");
            String chunkSizeS = requestBody.getString("chunkSize");

            byte[] fileByte = null;
            try {
                chunkS = StringUtil.isEmpty(chunkS) ? chunkS : new String(new BASE64Decoder().decodeBuffer(chunkS));
                chunkSizeS = StringUtil.isEmpty(chunkSizeS) ? chunkSizeS : new String(new BASE64Decoder().decodeBuffer(chunkSizeS));
                System.out.println("====================分片上传=============== " + chunkS + " " + chunkSizeS);
                fileByte = requestBody.getBytes("fileStream");
            } catch (IOException e) {
                e.printStackTrace();
                jsonResult.put("code", "0");
                jsonResult.put("msg", "参数解析失败");
                return jsonResult;
            }
            // 获取当前分片数 和分片大小
            int chunk = Integer.valueOf(chunkS);
            int chunkSize = Integer.valueOf(chunkSizeS);

            // 如果当前分片数为1 说明是第一次传送文件 验证文件信息
            if (chunk == 1) {
                // 校验文件夹是否存在
                FsFolder folder = fsFolderService.getById(folderId);
                if (null == folder) {
                    jsonResult.put("code", "4");
                    jsonResult.put("msg", "目录不存在");
                    return jsonResult;
                }
                // 校验文档是否存在
                DocInfo docInfo = docInfoService.getById(fileId);
                if (docInfo != null) {
                    jsonResult.put("code", "0");
                    jsonResult.put("msg", "文件已存在");
                    return jsonResult;
                }

                // 校验文件名称长度
                if (fileName.length() > 200) {
                    jsonResult.put("code", "0");
                    jsonResult.put("msg", "文件名称长度超出限制");
                    return jsonResult;
                }

                // 校验文件名称是否合法
                String regex = "^[^'\"\\|\\\\]*$";
                if (Pattern.compile(regex).matcher(fileName).find() == false) {
                    jsonResult.put("code", "0");
                    jsonResult.put("message", "文件名称不合法");
                    return jsonResult;
                }

                // 校验文件格式是否支持
                String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                List<DocConfigure> typeList = docConfigureService.getConfigure();
                if (typeList.get(0).getConfigValue().contains(suffix.toLowerCase())) {
                    jsonResult.put("code", "0");
                    jsonResult.put("msg", "文档格式不支持");
                    return jsonResult;
                }

                // 校验文件名称是否存在
                List<String> docNameList = new ArrayList<String>();
                docNameList.add(fileName);
                List<String> nameList = docInfoService.checkFileExist(docNameList, folderId);
                if (nameList != null && nameList.size() != 0) {
                    jsonResult.put("code", "0");
                    jsonResult.put("msg", "目录下存在同名文件");
                    return jsonResult;
                }
            }

            // 文件地址
            File file = new File(breakdir + File.separator + identification + File.separator + chunk);
            if (!file.getParentFile().exists()) {
                // 路径不存在,创建
                file.getParentFile().mkdirs();
            }
            // 校验文件是否存在
            if (file.exists() && file.length() == chunkSize) {
                // 上传过 不在上传  直接返回上传成功
                return jsonResult;
            } else {
                try {
                    file.createNewFile();
                    // 没有上传过
                    // 读取字节数组到文件
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(fileByte, 0, chunkSize);
                    fos.close();
                    return jsonResult;
                } catch (Exception e) {
                    e.printStackTrace();
                    jsonResult.put("code", "0");
                    jsonResult.put("msg", "上传失败");
                    return jsonResult;
                }
            }
            // 合并文件
        } else if (StringUtils.equals(chunkType, "merge")) {
            System.out.println("====================合并文件===============");

            try {
                // 文件目录
                File file = new File(breakdir + File.separator + identification);
                // 获取目录里面的文件
                File[] fileArray = file.listFiles();
                // 文件排序
                List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
                            return -1;
                        }
                        return 1;
                    }
                });
                // 创建文件
                String random = UUID.randomUUID().toString().replace("-", "");
                String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                File sourceFile = new File(tempdir + File.separator + random + suffix);
                if (!sourceFile.getParentFile().exists()) {
                    sourceFile.getParentFile().mkdirs();
                }
                sourceFile.createNewFile();

                // 将分片文件 复制到文件
                FileChannel inChannel = null;
                FileOutputStream outPutStream = new FileOutputStream(sourceFile);
                FileChannel outChannel = outPutStream.getChannel();
                for (File perFile : fileList) {
                    FileInputStream inputStream = new FileInputStream(perFile);
                    inChannel = inputStream.getChannel();
                    try {
                        inChannel.transferTo(0, inChannel.size(), outChannel);
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsonResult.put("code", "0");
                        jsonResult.put("msg", "上传失败");
                        return jsonResult;
                    } finally {
                        inChannel.close();
                        inputStream.close();
                    }
                    // 删除分片
                    perFile.delete();
                }

                System.out.println("******************文件:" + fileName + "创建成功，路径为" + sourceFile.getPath() + ",大小为" + sourceFile.length() + "******************");

                outChannel.close();
                outPutStream.close();

                // 上传文件
                System.out.println("==============上传文件开始===============");
                filesService.uploadFileApi(sourceFile, fileId, fileName, folderId, userId);
                System.out.println("==============上传文件完成===============");

                // 删除文件夹
                if (file.isDirectory() && file.exists()) {
                    file.delete();
                }

                JSONObject docIdJson = new JSONObject();
                docIdJson.put("fileId", fileId);
                jsonResult.put("result", docIdJson);
            } catch (Exception e) {
                LOGGER.error("文件上传失败" + ExceptionUtils.getErrorInfo(e));
                e.printStackTrace();
                jsonResult.put("code", "0");
                jsonResult.put("msg", "上传失败");
                return jsonResult;
            }
        }
        return jsonResult;
    }

    /**
     * 文件下载 返回base64文件
     */
    @RequestMapping("/filesDownloadApi")
    @ResponseBody
    public JSONObject fileDown(@RequestBody Map<String, String> requestBody) {
        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fileIds = requestBody.get("fileIds");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileIds)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        try {
            fileIds = StringUtil.isEmpty(fileIds) ? fileIds : new String(new BASE64Decoder().decodeBuffer(fileIds));
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        try {
            long time1 = System.currentTimeMillis();
            byte[] fileByte = filesService.fileDownload(fileIds, userId);
            long time2 = System.currentTimeMillis();
            System.out.println("从fast下载文件时间" + (time2 - time1));

            if (fileByte != null) {
                JSONObject fileResult = new JSONObject();
                long time3 = System.currentTimeMillis();
                fileResult.put("fileStream", new BASE64Encoder().encodeBuffer(fileByte));
                long time4 = System.currentTimeMillis();
                System.out.println("文件转换成base64时间" + (time4 - time3));
                jsonResult.put("result", fileResult);
            }
        } catch (Exception e) {
            LOGGER.error("文件下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "系统异常:" + e.toString());
            return jsonResult;
        }
        return jsonResult;
    }

    /**
     * 文件下载 返回文件字节数组
     */
    @RequestMapping("/filesByteDownloadApi")
    @ResponseBody
    public JSONObject testFilesByteDownloadApi(@RequestBody Map<String, String> requestBody) {
        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fileIds = requestBody.get("fileIds");

        // 校验输入的参数
        if (StringUtils.isAnyEmpty(accessToken, fileIds)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        /*try {
            fileIds = StringUtil.isEmpty(fileIds) ? fileIds : new String(new BASE64Decoder().decodeBuffer(fileIds));
            accessToken = StringUtil.isEmpty(accessToken) ? accessToken : new String(new BASE64Decoder().decodeBuffer(accessToken));
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "参数解析失败");
            return jsonResult;
        }*/

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        try {
            long time1 = System.currentTimeMillis();
            byte[] fileByte = filesService.fileDownload(fileIds, userId);
            long time2 = System.currentTimeMillis();
            System.out.println("从fast下载文件时间" + (time2 - time1));

            if (fileByte != null) {
                JSONObject fileResult = new JSONObject();
                fileResult.put("fileStream", fileByte);
                jsonResult.put("result", fileResult);
            }
        } catch (Exception e) {
            LOGGER.error("文件下载失败" + ExceptionUtils.getErrorInfo(e));
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "文件下载失败:" + e.toString());
            return jsonResult;
        }
        return jsonResult;
    }


    /**
     * 文件删除
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/filesDelApi")
    @ResponseBody
    public JSONObject filesDelApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fields = requestBody.get("fields");

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, fields)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        List<String> list = new ArrayList();
        int num = 0;
        try {
            String[] strArr = fields.split(",");
            list.addAll(Arrays.asList(strArr));

            num = fsFileService.deleteReally(list, userId);

            for (String id : strArr) {
                Map map = new HashMap(1);
                //0为无效，1为有效
                map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
                Map<String, Object> esMap = esUtil.getIndex(id);
                if (esMap != null) {
                    esUtil.updateIndex(id, map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("code", "3");
            jsonResult.put("msg", "文件删除异常:" + e.toString());
            return jsonResult;
        }
        jsonResult.put("delNum", num);
        return jsonResult;
    }

    /**
     * 添加文件的预览次数(单个)
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/previewNumApi")
    @ResponseBody
    public JSONObject previewNumApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fileId = requestBody.get("fileId");
        String previewNum = requestBody.get("previewNum");

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, fileId, previewNum)) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        try {

            int num = cacheToolService.getAndUpdateReadNum(fileId, Integer.parseInt(previewNum));
            JSONObject result = new JSONObject();
            result.put("previewNum", num);
            jsonResult.put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "系统异常:" + e.toString());
            return jsonResult;
        }
        return jsonResult;
    }

    /**
     * 添加文件的预览次数(集合)
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/previewNumsApi")
    @ResponseBody
    public JSONObject previewNumsApi(@RequestBody Map<String, String> requestBody) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fileList = requestBody.get("fileList");

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        JSONArray folders = JSONArray.parseArray(fileList);

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken, fileList) || folders == null || folders.size() == 0) {
            jsonResult.put("code", "4");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        try {

            JSONArray previewList = new JSONArray();
            for (int i = 0; i < folders.size(); i++) {
                JSONObject jsonObject = folders.getJSONObject(i);
                String fileId = jsonObject.getString("fileId");
                String preview = jsonObject.getString("preview");
                int num = cacheToolService.getAndUpdateReadNum(fileId, Integer.parseInt(preview));

                JSONObject previewResult = new JSONObject();
                previewResult.put(fileId, num);
                previewList.add(previewResult);
            }
            JSONObject result = new JSONObject();
            result.put("previewNums", previewList);
            jsonResult.put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
            jsonResult.put("code", "0");
            jsonResult.put("msg", "系统异常:" + e.toString());
            return jsonResult;
        }
        return jsonResult;
    }

    /**
     * 文件预览
     *
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "/webPreviewApi")
    @ResponseBody
    public JSONObject webPreviewApi(@RequestBody Map<String, String> requestBody, HttpServletRequest request) {

        // 返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 获取参数
        String accessToken = requestBody.get("accessToken");
        String fileId = requestBody.get("fileId");
//        System.out.println("生成文件预览链接时token：" + accessToken);

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        DocInfo docInfo = docInfoService.getById(fileId);
        if (docInfo == null) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "资源不存在");
            return jsonResult;
        }
        String fileType = docInfo.getDocType().substring(1);

        // 生成分享信息
        JSONObject shareResult = shareResourceService.newShareResourceXJ(fileId, fileType, 0, 0, 0, "", "", userId, request);
        if (StringUtils.equals(shareResult.getString("status"), "0")) {
            return shareResult;
        }

        JSONObject hrefJson = new JSONObject();
        hrefJson.put("filepath", shareResult.getString("mapping_url"));
        jsonResult.put("result", hrefJson);
        return jsonResult;
    }

    /**
     * @param hash  分享文件的预览
     * @param model
     */
    @GetMapping("/{hash}")
    public String viewShare(@PathVariable String hash, Model model, String accessToken) {

        // 验证token
        String userId = "";
        String userName = "";
//        System.out.println("文件预览时token：" + accessToken);
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
            userName = validToken.getString("userName");
        } else {
            model.addAttribute("error_msg", "您没有权限查看文件");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }

        model.addAttribute("hash", hash);
        //根据映射地址获取分享文件的信息
        Map shareResource = shareResourceService.getShareResource(hash);
        if (shareResource == null) {
            model.addAttribute("error_msg", "文件已删除");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }
        //获取文件的可访问状态
        String authority = "";
        if (shareResource.get("authority") == null) {
            authority = "0";
        } else {
            authority = shareResource.get("authority").toString();
        }

        String href = shareResource.get("href").toString();
        // 验证文件的有效性
        String docValid = "";
        if (shareResource.get("docValid") == null) {
            docValid = "1";
        } else {
            docValid = shareResource.get("docValid").toString();
        }
        if ("0".equals(docValid)) {
            //资源失效则返回分享错误页面
            model.addAttribute("error_msg", "文件已删除");
            model.addAttribute("isPersonCenter", false);
            return "/doc/front/preview/share_error.html";
        }

        //将有效期转换成日期类型
        Date validTime = com.jxdinfo.doc.common.util.StringUtil.stringToDate(shareResource.get("validTime").toString());
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
        model.addAttribute("createTime", shareResource.get("createTime"));
        model.addAttribute("validTime", shareResource.get("validTime"));
        model.addAttribute("validLack", changeValid(validTime));
        model.addAttribute("authority", authority);

        //获取配置文件--是否有公司水印
        Map<String, String> mapCompany = frontDocInfoService.getConfigure("watermark_company");
        //获取配置文件--是否有用户水印
        Map<String, String> mapUser = frontDocInfoService.getConfigure("watermark_user");
        model.addAttribute("watermark_company_flag", mapCompany.get("configValidFlag"));
        model.addAttribute("companyValue", mapCompany.get("configValue"));
        model.addAttribute("watermark_user_flag", mapUser.get("configValidFlag"));
        model.addAttribute("isPersonCenter", false);
        model.addAttribute("shareUser", shareResource.get("shareUser") == null ? "" : shareResource.get("shareUser"));

        // 项目标题
        Map<String, String> projectTitleMap = frontDocInfoService.getConfigure("project_title");
        model.addAttribute("projectTitle", projectTitleMap.get("configValue"));

        // 是否显示客户端
        Map<String, String> clientShowMap = frontDocInfoService.getConfigure("client_show");
        model.addAttribute("clientShow", clientShowMap.get("configValue"));

        // 是否显示联系方式
        Map<String, String> contactShowMap = frontDocInfoService.getConfigure("contact_show");
        model.addAttribute("contactShow", contactShowMap.get("configValue"));

        // 免密登录
        LoginNoPwd(userName);

        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        if (null != shiroUser) {
            if (href.indexOf("toShowFolder") != -1) {
            } else {
                href = href.replaceAll("sharefile", "previewUnstruct");
            }
        }
        href += "&shareForward=1";
        return "forward:" + href;
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

    /**
     * 查询目录树
     * folderId 如果folderId参数不为空 则递归查询子级目录
     * 如果folderId参数为空 则全部目录信息
     *
     * @return
     */
    @RequestMapping(value = "/getFolderListApi")
    @ResponseBody
    public JSONObject getFolderTree(@RequestBody(required = false) Map<String, String> requestBody) {
        //返回信息
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("code", "1");
        jsonResult.put("msg", "success");

        // 如果传了folderId参数 则查询此目录下的所有自子级目录
        String folderId = requestBody.get("folderId");
        String accessToken = requestBody.get("accessToken");

        // 校验参数
        if (StringUtils.isAnyEmpty(accessToken)) {
            jsonResult.put("code", "2");
            jsonResult.put("msg", "请将参数填写完整");
            return jsonResult;
        }

        // 验证token
        String userId = "";
        JSONObject validToken = unstructureTokenUtil.validToken(accessToken);
        if (StringUtils.equals(validToken.getString("code"), "1")) {
            userId = validToken.getString("userId");
        } else {
            return validToken;
        }

        List<Map> folderList = unstructurePlatformApiService.getFoldList(folderId);

        jsonResult.put("result", folderList);
        return jsonResult;
    }

    /**
     * 免密登录
     *
     * @param username
     */
    private void LoginNoPwd(String username) {
        Subject currentUser = ShiroKit.getSubject();
        String password = username; //密码和用户名相同 ,必填项
        password = credentialsMatcher.passwordEncode(password.getBytes());
        // 免密登录
        String host = encryptTypeProperties.getSecretFreeIp();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), host);
        currentUser.login(token); //登录
        ShiroUser shiroUser = ShiroKit.getUser();
        Session session = ShiroKit.getSession();
        session.setAttribute("sessionFlag", true);
        session.setAttribute("csrfFlag", true);
        session.setAttribute("shiroUser", shiroUser);
        session.setAttribute("userId", shiroUser.getId());
        session.setAttribute("projectFlag", projectFlag);
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        session.setAttribute("adminFlag", adminFlag);
        String url = fsFolderService.getPersonPic( UserInfoUtil.getCurrentUser().getName());
        session.setAttribute("url",url);
        session.setAttribute("theme", this.themeService.getUserTheme());
        SysUsers sysUsers = (SysUsers)this.iSysUsersService.getOne((new QueryWrapper<SysUsers>()).eq("USER_ACCOUNT", username).eq("ACCOUNT_STATUS", UserStatus.OK.getCode()));
        if (sysUsers != null && "1".equals(sysUsers.getLoginTimeLimit())) {
            session.setAttribute("startTime", sysUsers.getAccessLoginStartTime());
            session.setAttribute("endTime", sysUsers.getAccessLoginEndTime());
        }

        SysOnline online = this.iSysOnlineHistService.addRecord();
        session.setAttribute("online", online);
        Map<String, String> info = new HashMap();
        String token2 = getToken(sysUsers);
        info.put("token",token2);
        info.put("sessionId", (String)ShiroKit.getSession().getId());
        info.put("ip", HttpKit.getIp());
        info.put("port", HttpKit.getPort());
        info.put("host", HttpKit.getHost());
        info.put("localIp", HttpKit.getLocalIp());
        info.put("localPort", HttpKit.getLocalPort());
        info.put("localHost", HttpKit.getLocalHost());
        HussarLogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser, "05", info));
        SysUsers user = new SysUsers();
        user.setUserId(shiroUser.getId());
        user.setLastLoginTime(new Date());
        this.iSysUsersService.updateById(user);
        String userId = sysUsers.getUserId();
        QueryWrapper<FsFolder> wrapper = new QueryWrapper<>();
        List<FsFolder> list = fsFolderService.list(wrapper.eq("own_id", userId).
                eq("parent_folder_id", "2bb61cdb2b3c11e8aacf429ff4208431"));
        cacheToolService.updateLevelCodeCache(userId);
        if (list == null||list.size()==0) {

            FsFolder fsFolder = new FsFolder();

            fsFolder.setFolderName("我的文件夹");
            fsFolder.setOwnId(userId);
            fsFolder.setIsEdit("1");
            fsFolder.setVisibleRange("0");
            fsFolder.setParentFolderId("2bb61cdb2b3c11e8aacf429ff4208431");
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
            }
            DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
            docFoldAuthority.setId(IdWorker.get32UUID());
            docFoldAuthority.setFoldId(folderId);
            docFoldAuthority.setAuthorType("0");
            docFoldAuthority.setOperateType("2");
            docFoldAuthority.setAuthorId(userId);

            docFoldAuthorityService.save(docFoldAuthority);
            //生成showOrder
            String currentCode = this.sysIdtableService.getCurrentCode("FOLDER_NUM", "fs_folder");
            int num = Integer.parseInt(currentCode);
            fsFolder.setShowOrder(num);
            //保存目录信息
            fsFolderService.save(fsFolder);
            //保存权限信息
        }
        DocResourceLog docResourceLog = new DocResourceLog();
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(11);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);//添加登录
    }

    public String getToken(SysUsers user) {
        String token="";
        token= JWT.create().withAudience(user.getUserId())
                .sign(Algorithm.HMAC256(user.getPassword()));
        return token;
    }
}
