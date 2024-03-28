/*
 * ApiDownLoadController
 * 版权所有：金现代信息产业股份有限公司  2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.mobile.controller;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 类的用途：统一下载接口（通过比较businessID来跳转具体接口）<p>
 * 创建日期：2018-9-13 <br>
 * 修改日期：2018-9-13 <br>
 * 修改作者：zlz <br>
 * 修改内容：修改内容 <br>
 *
 * @author zlz
 * @version 1.0
 */
@Controller
@CrossOrigin
@RequestMapping("/downloadServices")
public class ApiDownLoadController {
    private static Logger logger = LoggerFactory.getLogger(ApiDownLoadController.class);

    /** 文件处理 */
    @Autowired
    private FilesService filesService;

    @Resource
    private ISysUsersService iSysUsersService;

    @Autowired
    private DocInfoService docInfoService;

    @RequestMapping("/download")
    public void getFile(HttpServletRequest request, HttpServletResponse response) {
        try {
            String docId = request.getParameter("docId");
            String userId = request.getParameter("userId");
            String orgId = iSysUsersService.getById(userId).getDepartmentId();

            if(docId != null){
            DocInfo docInfo = docInfoService.getDocDetail(docId);
            if(docInfo!=null){
            filesService.downloadMobile(docId, docInfo.getTitle(), request, response,userId,orgId);

            //获取附件的地址

             }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("下载异常", e.getMessage());
        }
    }

/*    *//**
     * 下载时用到的流
     *//*
    private static void outPutStreamBuff(String path,String filename,HttpServletResponse response,String fileId) throws IOException {
        if(path!=null){
            String prefix = filename.substring(filename.lastIndexOf("."));
            String path1=path+fileId+prefix;
            FileInputStream  fileInputStream = new FileInputStream(path1);
            FileChannel fc = fileInputStream.getChannel();
            String filesize = fc.size() + "";
            int i = fileInputStream.available();
            byte[] buff = new byte[i];
            fileInputStream.read(buff);
            fileInputStream.close();
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Length", filesize);
            //设置头部
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename, "UTF-8"));
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(buff);
            outputStream.flush();
            outputStream.close();
        } else {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setStatus(404);
            response.sendError(404);
        }
    }
    */

}
