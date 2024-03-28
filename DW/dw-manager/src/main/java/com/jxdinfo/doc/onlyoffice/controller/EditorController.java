/*
 *
 * (c) Copyright Ascensio System SIA 2020
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.jxdinfo.doc.onlyoffice.controller;

import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.onlyoffice.helpers.ConfigManager;
import com.jxdinfo.doc.onlyoffice.helpers.DocumentManager;
import com.jxdinfo.doc.onlyoffice.model.FileModel;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.ISysStruService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.encrypt.CryptoUtil;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Controller
public class EditorController extends BaseController
{
    /**
     * 文件 Mapper 接口
     */
    @Autowired
    private FilesMapper filesMapper;

    /** fast服务器服务类  */
    @Autowired
    private FastdfsService fastdfsService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private DocGroupService docGroupService;

    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private ISysUsersService sysUsersService;

    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private ISysStruService sysStruService;

    @Autowired
    private DocFileAuthorityService docFileAuthorityService;

    @Resource
    private SysStruMapper sysStruMapper;

    /**
     *  是否启用fastdfs
     */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    /**
     *  链接有效时间
     */
    @Value("${onlineEdit.validTime}")
    private long validTime;

    /**
     * 原加密文件存放地址
     */
    @Value("${docbase.downloadFileByKey}")
    private String downloadFileByKey;

    /**
     * 原加密文件存放地址
     */
    @Value("${onlineEdit.licenseServer}")
    private String licenseServer;

    @RequestMapping("/EditorServlet")
    protected String processRequest(HttpServletRequest request, HttpServletResponse response, Model model){
        DocumentManager.Init(request, response);

        String fileName = "";
        String downloadFileName = "";
        String params = request.getParameter("p");
        if(ToolUtil.isEmpty(params)){
            return "/doc/onlyoffice/error.html";
        } else {
            params = params.replace(" ","+");
        }
        String fileId = "";
        String uid = "";
        String uname = "";
        boolean emptyFlag = false;
        String jsonData = "";
        try {
            jsonData = CryptoUtil.decode(params);
        }catch (Exception e){
            e.printStackTrace();
            return "/doc/onlyoffice/error.html";
        }
        if(ToolUtil.isNotEmpty(jsonData)){
            com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(jsonData);
            Object fileIdTemp = json.get("fileId");
            Object uidTemp = json.get("uId");
            Object unameTemp = json.get("uName");
            Object timeTemp = json.get("time");
            if(ToolUtil.isNotEmpty(fileIdTemp)) {
                fileId = fileIdTemp.toString();
            }else {
                emptyFlag = true;
            }
            if(ToolUtil.isNotEmpty(uidTemp)) {
                uid = uidTemp.toString();
            }else {
                emptyFlag = true;
            }
            if(ToolUtil.isNotEmpty(unameTemp)) {
                uname = unameTemp.toString();
            }else {
                emptyFlag = true;
            }
            if(ToolUtil.isNotEmpty(timeTemp)) {
                try {
                    String time = timeTemp.toString();
                    long sendTime = Long.parseLong(time);
                    long nowTime = System.currentTimeMillis();
                    long diff = (nowTime - sendTime) / 1000;
                    if (diff > validTime) { // 链接失效
                        return "/doc/onlyoffice/error.html";
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return "/doc/onlyoffice/error.html";
                }
            }else {
                emptyFlag = true;
            }
        }
        // 参数为空
        if(emptyFlag){
            return "/doc/onlyoffice/error.html";
        }
        // 判断用户是否具有文档的管理权限
        boolean editFlag = judgeFileAuthority(fileId,uid);
        if(!editFlag){
            return "/doc/onlyoffice/error.html";
        }
        // 添加日志
        addLog(fileId,uid);
        // 根据文件id查询文件信息
        FsFile fsFileTemp =  filesMapper.selectById(fileId);
        if(ToolUtil.isNotEmpty(fsFileTemp)) {
            String name = fsFileTemp.getFileName();
            String suffix = fsFileTemp.getFileType();
            if(ToolUtil.isNotEmpty(name) && ToolUtil.isNotEmpty(suffix)){
                fileName = name + suffix;
                downloadFileName = fileId + suffix;
            }
        }
        String url = "downLocalFile?path=";
        String path = DocumentManager.GetFilePath(downloadFileName);
        downloadOldFileToLocal(fsFileTemp,path);
        FileModel file = new FileModel(fileName, "zh-CN",uid, uname,url,path,fileId,licenseServer);
        file.changeType(request.getParameter("mode"), request.getParameter("type"));

        if (DocumentManager.TokenEnabled())
        {
            file.BuildToken();
        }

        model.addAttribute("file",FileModel.Serialize(file));
        model.addAttribute("histArray", JSONArray.fromObject(file.GetHistory(url)));
        model.addAttribute("docserviceApiUrl", ConfigManager.GetProperty("files.docservice.url.api"));
        return "/doc/onlyoffice/editor.html";
    }

    @RequestMapping("/downLocalFile")
    @CrossOrigin(origins = "*")
    public void downLocalFile(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("text/html;charset=utf-8");
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        File file = null;
        //获取下载文件所在路径
        String path = request.getParameter("path");
        try {
            if(ToolUtil.isNotEmpty(path)){
                String name = path.substring(path.lastIndexOf("/") + 1);
                if(ToolUtil.isNotEmpty(name)){
                    //文件
                    file = new File(path);
                    //判断文件是否存在
                    if(!file.exists()){
                        String fileId = name.substring(0,name.lastIndexOf("."));
                        // 根据文件id查询文件信息
                        FsFile fsFileTemp =  filesMapper.selectById(fileId);
                        downloadOldFileToLocal(fsFileTemp,path);
                    }
                    //且仅当此对象抽象路径名表示的文件或目录存在时，返回true
                    response.setContentType("application/pdf");
                    //控制下载文件的名字
                    response.addHeader("Content-Disposition", "attachment;filename=" + new String (name.getBytes("gb2312"),"iso-8859-1")+"");

                    byte buf[] = new byte[1024];

                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    os = response.getOutputStream();
                    int i = bis.read(buf);
                    while(i!=-1){
                        os.write(buf,0,i);
                        i = bis.read(buf);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            if (file != null) {
//                file.delete();
//            }

        }
    }

    public void downloadOldFileToLocal(FsFile fsFileTemp, String path){
        byte[] bytes = null;
        FileOutputStream fos = null;
        FileInputStream input = null;
        File file = null;
        File fileKey = null;
        try{
            //        deleteFile(path);
            // 文件路径
            String filePath = fsFileTemp.getFilePath();
            // 文件解密密钥
            String sourceKey = fsFileTemp.getSourceKey();
            if (filePath != null && !"".equals(filePath) && sourceKey != null && !"".equals(sourceKey)) {
                if (!fastdfsUsingFlag) {
                    input = new FileInputStream(filePath);
                    bytes = new byte[input.available()];
                    input.read(bytes);
                } else {
                    bytes = fastdfsService.download(filePath);
                }
                //在本地生成随机文件
                String random = fsFileTemp.getMd5();
                String suffix = fsFileTemp.getFileType();
                file = new File(downloadFileByKey + random + suffix);
                if (!file.getParentFile().exists()) {
                    // 路径不存在,创建
                    file.getParentFile().mkdirs();
                }
                boolean fileExist = false;
                fileKey = new File(path);
                if (!fileKey.getParentFile().exists()) {
                    // 路径不存在,创建
                    fileKey.getParentFile().mkdirs();
                }
                if (!fileKey.exists()) {
                    fos = new FileOutputStream(file);
                    fos.write(bytes, 0, bytes.length);
                    fos.close();
                } else {
                    fileExist = true;
                }
                //文件解密
                if (!fileExist) {
                    FileEncryptUtil.getInstance().decrypt(downloadFileByKey + random + suffix, path, sourceKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                file.delete();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 添加日志
     * @param fileId 文件ID
     * @param userId 用户ID
     */
    public void addLog(String fileId, String userId){
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        docResourceLog.setId(id);
        docResourceLog.setResourceId(fileId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(14);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);
    }

    /**
     * 判断用户是否具有文档的管理权限
     * @param fileId 文档ID
     * @param userId 用户ID
     * @return 是否
     */
    public boolean judgeFileAuthority(String fileId, String userId){
        List<String> listGroup = docGroupService.getPremission(userId);
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        DocInfo docInfo = docInfoService.getById(fileId);
        if(ToolUtil.isNotEmpty(docInfo)){
            String foldId = docInfo.getFoldId();
            //判断是否有可编辑文件的权限
            if (adminFlag == 1) {
                return true;
            }else {
                int isEdits = docFoldAuthorityService.findEditByUploadMobile(foldId, listGroup, userId);
                if(isEdits == 2){
                    return true;
                }
            }
            FsFolder fsFolder = fsFolderService.getById(foldId);
            if(ToolUtil.isNotEmpty(fsFolder)){
                String createUserId = fsFolder.getCreateUserId();
                if (userId.equals(createUserId)) {
                    return true;
                }
            }

            //判断是否是登录者本人上传的文件
            String createDocUserId = docInfo.getUserId();
            if(userId.equals(createDocUserId)){
                return true;
            }
            //判断是否有文档管理权限
            List<String> orgList = new ArrayList<>();
            SysStru sysStru = sysStruMapper.selectById(sysUsersService.getById(userId).getDepartmentId());
            if(sysStru != null){
                while (!"11".equals(sysStru.getParentId())) {
                    orgList.add(sysStru.getStruId());
                    sysStru = sysStruMapper.selectById(sysStru.getParentId());
                    if(sysStru == null){
                        break;
                    }
                }
            }
            List<Integer> result = docFileAuthorityService.judgeFileAuthority(fileId, userId, orgList, listGroup);
            for (int i = 0; i < result.size(); i++) {
                if (!Integer.valueOf("0").equals(result.get(i))){
                    return true;
                }
            }
        }
        return false;
    }
}

