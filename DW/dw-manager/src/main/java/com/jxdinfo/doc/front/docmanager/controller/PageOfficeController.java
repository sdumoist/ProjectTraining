package com.jxdinfo.doc.front.docmanager.controller;

import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.LibreOfficePDFConvert;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.TikaUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docmanager.service.PageOfficeService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.hussar.bsp.permit.dao.SysUsersMapper;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OfficeVendorType;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import com.zhuozhengsoft.pageoffice.PageOfficeLink;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.exception.TikaException;
import org.jodconverter.office.OfficeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/editDoc")
public class PageOfficeController {
    static final public Logger logger = LoggerFactory.getLogger(PageOfficeController.class);

    @Value("${docbase.filedir}")
    private String tempdir;

    @Value("${docbase.editdir}")
    private String editdir;

    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;

    /**
     * es服务类
     */
    @Autowired
    private ESService esService;

    @Resource
    private FsFileService fsFileService;

    @Resource
    private PageOfficeService pageOfficeService;


    /**
     * FAST操作接口
     */
    @Autowired
    private FastdfsService fastdfsService;


    @Autowired
    private DocGroupService docGroupService;

    @Resource
    private SysUsersMapper sysUsersMapper;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Resource
    private DocInfoService docInfoService;
    @RequestMapping(value="/getWord", method= RequestMethod.GET)
    public String getWord(HttpServletRequest request,String docId){
        String link= PageOfficeLink.openWindow(request,"/editDoc/word?docId=" + docId + "&" + Math.random(),"width=800px;height=800px;");
   return link;
    }

    @RequestMapping(value="/word", method= RequestMethod.GET)
    public ModelAndView showWord(HttpServletRequest request, Map<String,Object> map,String docId,String fileType, String keyWords,String userId,String userName){
        try {


            String keyword = request.getParameter("keyword");
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setType("2");
            userName = sysUsersMapper.selectById(userId).getUserName();
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            FsFile fsFile = fsFileService.getById(docId);
            String filePath = docInfo.getFilePath();
            String suffix = filePath.substring(filePath.lastIndexOf("."));
            String random = UUID.randomUUID().toString().replace("-", "");
            File fileNew = new File(downloadPdfFile + "" +
                    random + suffix);
            boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(docInfo.getFilePath(),fileNew.getPath(), fsFile.getSourceKey());
            String path ="";
            if (!fastdfsUsingFlag) {
             path = fileNew.getPath();
             path=path.replaceAll("/","\\\\");
            }else{
                path =pageOfficeService.getEditFileByFast(docId);
                path=path.replaceAll("/","\\\\");
            }
            logger.info("******************本地路径:" + path + "，******************");
            //--- PageOffice的调用代码 开始 -----
            PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
            poCtrl.setOfficeVendor(OfficeVendorType.WPSOffice);
            poCtrl.setServerPage("/poserver.zz");//设置授权程序servlet
            poCtrl.setJsFunction_AfterDocumentOpened("AfterDocOpened");
            poCtrl.addCustomToolButton("确定", "Save", 1); //添加自定义按钮
            poCtrl.setSaveFilePage("/editDoc/save?docId="+docId);//设置保存的action
            if(path.endsWith(".doc")||path.endsWith("docx")){
                poCtrl.webOpen(path, OpenModeType.docAdmin, userName);
            }else if(path.endsWith(".ppt")||path.endsWith("pptx")){
                poCtrl.webOpen(path, OpenModeType.pptNormalEdit, userName);
            }
            else if(path.endsWith(".xls")||path.endsWith("xlsx")){
                poCtrl.webOpen(path, OpenModeType.xlsNormalEdit, userName);
            }
            map.put("pageoffice", poCtrl.getHtmlCode("PageOfficeCtrl1"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //--- PageOffice的调用代码 结束 -----
        ModelAndView mv = new ModelAndView("/doc/front/docmanager/pageOffice2.html");
        String type = fileType == null ? "" : fileType;
        String docName = keyWords == null ? "" : keyWords;
        mv.addObject("fileType", type);
        mv.addObject("key", docName);
        mv.addObject("id", docId);
        mv.addObject("isPersonCenter",false);
        mv.addObject("fileName", keyWords);
        mv.addObject("fileType", fileType);
        mv.addObject("userName", userName);
        mv.addObject("favorite",false);
        //判断此文档是否在预览时添加用户水印
        return mv;
    }

    @PostMapping("/save")
    public void saveFile(HttpServletRequest request, HttpServletResponse response,String docId){
        FileSaver fs = new FileSaver(request, response);
        String sourcePath="";

        File  newFile = null;
            sourcePath=tempdir + fs.getFileName();
        logger.info("******************编辑后路径:" + sourcePath + "，******************");
            if(sourcePath.indexOf("_edit")==-1){
                sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_edit1" + sourcePath.substring(sourcePath.lastIndexOf("."));
            }else{
                String subPath=sourcePath.substring(sourcePath.lastIndexOf("_edit"),sourcePath.lastIndexOf("."));
              subPath = subPath.substring(subPath.indexOf("t")+1,subPath.length());
                Integer editNum = 1;
              if("".equals(subPath)){
                  editNum=1;
              }else{
                  editNum =Integer.parseInt(subPath)+1;
              }
                sourcePath = sourcePath.substring(0,sourcePath.indexOf("_edit"))+sourcePath.substring(sourcePath.indexOf("."),sourcePath.length());
                sourcePath = sourcePath.substring(0, sourcePath.lastIndexOf(".")) + "_edit"+editNum + sourcePath.substring(sourcePath.lastIndexOf("."));
            }

        logger.info("******************编辑后edit路径:" + sourcePath + "，******************");
          fs.saveToFile(sourcePath);
           File oldFile = new File(sourcePath);
            Map<String, Object> metadata = null;
            try {
                metadata = TikaUtil.autoParse(sourcePath);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (TikaException e) {
                e.printStackTrace();
            }

            String  content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
            Map<String, Object> docVO = new HashMap();
            docVO.put("upDate", new Date());
            docVO.put("content", StringUtil.getString(content));
            try {
                esService.updateIndex(docId, docVO);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String pdfFilePath = sourcePath.substring(0, (sourcePath).
                    lastIndexOf(".")) + ".pdf";
        logger.info("******************编辑后edit的pdf路径:" + pdfFilePath + "，******************");
        File sourceFile = new File(sourcePath);
        File pdfFile = new File(pdfFilePath);
            if(fs.getFileName().endsWith(".doc")||fs.getFileName().endsWith(".docx")){
                try {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } catch (OfficeException e) {
                    e.printStackTrace();
                }
            }else if(fs.getFileName().endsWith(".xls")||fs.getFileName().endsWith(".xlsx")){
                try {
                    logger.info("******************编辑后edit的pdf路径:" + pdfFilePath + "，和"+sourcePath+"******************");
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(fs.getFileName().endsWith(".ppt")||fs.getFileName().endsWith(".pptx")){
                try {
                    LibreOfficePDFConvert.doDocToFdpLibre(sourceFile, pdfFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(fs.getFileName().endsWith(".pdf")){
                pdfFilePath = sourcePath;
            }else{
                pdfFilePath=null;
            }
            if(pdfFilePath!=null){

                if (!fastdfsUsingFlag) {

                    File file = new File(sourcePath);
                    FsFile fsFile = new FsFile();
                    try {
                        String md5 = DigestUtils.md5Hex(new FileInputStream(file));
                        fsFile.setMd5(md5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String sourceKey = FileEncryptUtil.getInstance().encrypt(oldFile);
                    fsFile.setSourceKey(sourceKey);
                    fsFile.setFileId(docId);
                    String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile);
                    fsFile.setPdfKey(pdfKey);
                    fsFile.setFilePdfPath(pdfFilePath);
                    fsFile.setFilePath(oldFile.getPath());
                    fsFileService.updateById(fsFile);
                }else{
                    newFile= new File(oldFile.getParentFile() + File.separator
                            + oldFile.getName().substring(0, oldFile.getName().lastIndexOf(".")) + "_new"
                            + oldFile.getName().substring(oldFile.getName().indexOf("."), oldFile.getName().length())) ;
                    FsFile fsFile = new FsFile();
                    String sourceKey = FileEncryptUtil.getInstance().encrypt(oldFile, newFile);
                    fsFile.setSourceKey(sourceKey);
                    try {
                        String   filePath = fastdfsService.uploadFile(newFile);
                        fsFile.setFileId(docId);
                        try {
                            String md5 = DigestUtils.md5Hex(new FileInputStream(newFile));
                            fsFile.setMd5(md5);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        fsFile.setFilePath(filePath);

                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }
                    String pdfKey = FileEncryptUtil.getInstance().encrypt(pdfFile);
                    fsFile.setPdfKey(pdfKey);
                    try {
                        String pdfPath = fastdfsService.uploadFile(pdfFile);
                        fsFile.setFilePdfPath(pdfPath);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                    }

                    fsFileService.updateById(fsFile);
                }
            }
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(docId);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(0);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(1);
        docResourceLog.setValidFlag("1");
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);//添加修改记录
        fs.close();
    }
    @Bean
    public ServletRegistrationBean servletRegistrationOfficeBean() {
        com.zhuozhengsoft.pageoffice.poserver.Server poserver = new com.zhuozhengsoft.pageoffice.poserver.Server();
        //设置PageOffice注册成功后,license.lic文件存放的目录

        poserver.setSysPath(editdir);
        ServletRegistrationBean srb = new ServletRegistrationBean(poserver);
        srb.addUrlMappings("/poserver.zz");
        srb.addUrlMappings("/posetup.exe");
        srb.addUrlMappings("/pageoffice.js");
        srb.addUrlMappings("/jquery.min.js");
        srb.addUrlMappings("/pobstyle.css");
        srb.addUrlMappings("/sealsetup.exe");
        return srb;//
    }

    public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) !=-1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}
