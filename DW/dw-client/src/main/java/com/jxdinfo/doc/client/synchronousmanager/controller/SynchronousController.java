package com.jxdinfo.doc.client.synchronousmanager.controller;

import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client/synchronous")
public class SynchronousController extends BaseController {

    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private FsFileService fsFileService;
    @Autowired
    private IFsFolderService fsFolderService;
    @Autowired
    private FileTool fileTool;
    /**
     * 检测根据文件的MD5值判断文件已经上传了多少分片
     *
     * @author: yjs
     * @return json
     */
    @Value("${docbase.breakdir}")
    private String breakdir;

    @RequestMapping("/checkBreakByMd5")
    @ResponseBody
    public ApiResponse checkBreakByMd5(String md5) {
        if (md5 == null || "".equals(md5)) {
            return ApiResponse.fail(200, "md5值为空");
        }
        File file = new File(breakdir + md5);
        if (!file.exists()) {
            return ApiResponse.fail(200, "文件不存在");
        } else {
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                return ApiResponse.data(200, true, "");
            } else {
                return ApiResponse.data(200, false, "");
            }
        }
    }

    @RequestMapping("/getFileById")
    @ResponseBody
    public ApiResponse getFileById(String folderId) {
        FsFolder fsFolder = fsFolderService.getById(folderId);
        if (fsFolder == null){
            return ApiResponse.data(200, null, "");
        }
        String folderLevelCode = fsFolder.getLevelCode();
        Integer length = fsFolder.getFolderPath().length();
        List<DocInfo> list = docInfoService.getListByFolderId(folderLevelCode, length);
        for(int i=0;i<list.size();i++){
            DocInfo docInfo = list.get(i);
            String foldId = docInfo.getFoldId();
            FsFolder parentFolder = fsFolderService.getById(foldId);
            String parentCode = parentFolder.getLevelCode();
            String currentCode = fsFolderService.getCurrentLevelCode(parentCode, parentFolder.getFolderId());
            fsFolder.setLevelCode(currentCode);
            String localName = "";
            int  levelLength = folderLevelCode.length()/4;
            for (int j = 1; j <= currentCode.length() / 4-1; j++) {
                String levelCodeString = currentCode.substring(0, j * 4);
                String folderName = fsFolderService.getFolderNameByLevelCode(levelCodeString);
                if(j>levelLength){
                    localName = localName + "\\" + folderName;
                }
            }
            localName=localName+"\\";
            String size = docInfo.getSize();
            if ("0".equals(size)) {
                size = String.valueOf(Integer.parseInt(docInfo.getFileSize().substring(0, docInfo.getFileSize().length() - 4))*1024);
            }
            docInfo.setSize(size);
            docInfo.setLocalPath(localName);
        }
        Map<String,Object> map  = new HashMap<>();

        List<FsFolder> folderList = fsFolderService.selectFoldersByLevelCode(folderLevelCode,length);
        map.put("docList",list);
        map.put("folderList",folderList);
        return ApiResponse.data(200, map, "");
    }

    @RequestMapping("/breakDownload")
    public void breakDownload(String fileId, Integer start, Integer end, HttpServletResponse response) throws IOException {
        FsFile fsFile = fsFileService.getById(fileId);
        RandomAccessFile randomAccessFile = null;
        try {
            File file = fileTool.chuckAllFile(fsFile.getFilePath());
            byte[] buffer = new byte[end - start];
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(start);
            randomAccessFile.read(buffer);
            response.setHeader("Content-Range", "bytes " + start + "-" + (end-1) + "/" + file.length() + "");
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            response.getOutputStream().write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } finally {
            if (response.getOutputStream() != null) {
                response.getOutputStream().close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }

        }


    }

    @RequestMapping("/checkFolder")
    @ResponseBody
    public ApiResponse checkFolder(String folderId) {
        FsFolder fsFolder = fsFolderService.getById(folderId);
        if (fsFolder == null) {
            return ApiResponse.fail(200, "目录不存在");
        } else {
            return ApiResponse.data(200, true, "目录存在");
        }

    }
}
