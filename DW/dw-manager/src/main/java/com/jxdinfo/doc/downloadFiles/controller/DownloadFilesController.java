package com.jxdinfo.doc.downloadFiles.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.base.tips.ErrorTip;
import com.jxdinfo.hussar.core.base.tips.SuccessTip;
import com.jxdinfo.hussar.core.base.tips.Tip;
import com.jxdinfo.hussar.core.constant.HttpCode;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/downloadFiles")
public class DownloadFilesController extends BaseController {

    @Autowired
    private IFsFolderService fsFolderService;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ESUtil esUtil;

    private static final String FOLDER_PREFIX = "D:\\test";

    private int fileNum = 0;

    private int repeatedFileNum = 0;

    @RequestMapping(value = "/downloadGroupByFolders")
    @ResponseBody
    public Tip downloadGroupByFolders() {
        boolean flag = true;
        // 查询所有一级目录
        QueryWrapper<FsFolder> ew = new QueryWrapper<>();
        ew.eq("parent_folder_id","2bb61cdb2b3c11e8aacf429ff4208431");
        ew.isNull("is_edit");
        List<FsFolder> firstFolderList = fsFolderService.list(ew);
        try {
            if (ToolUtil.isNotEmpty(firstFolderList)) {
                for (FsFolder fsFolder : firstFolderList) {
                    String folderPath = "";
                    String firstFolderName = fsFolder.getFolderName();
                    if (ToolUtil.isNotEmpty(firstFolderName)) {
                        folderPath = FOLDER_PREFIX + "\\" + firstFolderName;
                        createFolderAndFiles(fsFolder,folderPath);
                        // 查询一级目录下二级目录
                        QueryWrapper<FsFolder> ew1 = new QueryWrapper<>();
                        ew1.eq("parent_folder_id",fsFolder.getFolderId());
                        List<FsFolder> secondFolderList = fsFolderService.list(ew1);
                        for (FsFolder fsFolder1 : secondFolderList) {
                            String secondFolderName = fsFolder1.getFolderName();
                            if (ToolUtil.isNotEmpty(secondFolderName)) {
                                String secondFolderPath = FOLDER_PREFIX + "\\" + secondFolderName;
                                createFolderAndFiles(fsFolder1,secondFolderPath);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }

        if(flag){
            System.out.println("文件数目："+fileNum);
            System.out.println("重复文件数目："+repeatedFileNum);
            Tip tip = new SuccessTip();
            tip.setMessage("文件下载到本地（目录分组方式）成功");
            return tip;
        }else {
            return new ErrorTip(HttpCode.INTERNAL_SERVER_ERROR.value(), "文件下载到本地（目录分组方式）失败");
        }
    }

    /**
     * 创建文件夹及文件
     * @param fsFolder 文件夹信息
     * @param folderPath 文件夹路径
     */
    private void createFolderAndFiles(FsFolder fsFolder, String folderPath){
        String levelCode = fsFolder.getLevelCode();
        // 本地创建文件夹
        if (ToolUtil.isNotEmpty(folderPath)) {
            createFolder(folderPath);
        }
        if (ToolUtil.isNotEmpty(levelCode)) {
            // 查询目录及目录下所有子目录
            QueryWrapper<FsFolder> ew1 = new QueryWrapper<>();
            ew1.like("level_code", levelCode + "%");
            List<FsFolder> firstChildrenList = fsFolderService.list(ew1);
            if (ToolUtil.isNotEmpty(firstChildrenList)) {
                List<String> childrenFolderIds = new ArrayList<>();
                for (FsFolder fsFolder1 : firstChildrenList) {
                    childrenFolderIds.add(fsFolder1.getFolderId());
                }
                if (ToolUtil.isNotEmpty(childrenFolderIds)) {
                    // 查询目录及其所有子目录的文件
                    QueryWrapper<DocInfo> ew2 = new QueryWrapper<>();
                    ew2.eq("valid_flag", "1");
                    ew2.in("fold_id", childrenFolderIds);
                    List<DocInfo> docInfoList = docInfoService.list(ew2);
                    if (ToolUtil.isNotEmpty(docInfoList)) {
                        for (DocInfo docInfo : docInfoList) {
                            // 获取文件索引信息
                            Map<String, Object> sourceMap = esUtil.getIndex(docInfo.getDocId());
                            if (ToolUtil.isNotEmpty(sourceMap)) {
                                // 获取文件内容
                                Object content = sourceMap.get("content");
                                if (ToolUtil.isNotEmpty(content)) {
                                    String docContent = content.toString();
                                    String title = docInfo.getTitle();
                                    if (ToolUtil.isNotEmpty(title)) {
                                        String docPath = folderPath + "\\" + title + ".txt";
                                        // 本地创建文件
                                        createFile(docPath, docContent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 创建文件夹
     * @param path 文件夹路径
     */
    private void createFolder(String path){
        File file=new File(path);
        if(!file.exists()){//如果文件夹不存在
            file.mkdirs();//创建文件夹
        }
    }

    /**
     * 创建文件
     * @param path 文件路径
     * @param content 文件内容
     */
    private void createFile(String path,String content){
        File file = new File(path);
        if (file.exists()) {
            try {
                path = renameFile(path);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try{
            BufferedWriter bw=new BufferedWriter(new FileWriter(path));
            bw.write(content);
            bw.close();
            fileNum++;
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 重名文件重命名
     * @param path 文件路径
     * @return 新文件路径
     */
    private String renameFile(String path){
        int index = path.lastIndexOf("\\");
        String folderPath = path.substring(0,index);
        String fileNameTotal = path.substring(index+1);
        String newName = fileNameTotal;
        int suffixIndex = fileNameTotal.lastIndexOf(".");
        String fileName = fileNameTotal.substring(0,suffixIndex);
        String suffix = fileNameTotal.substring(suffixIndex);
        File file = new File(folderPath);
        File files[] = file.listFiles(new MyFilenameFilter(fileName,suffix));
        if(files != null) {
            int size = files.length;
            if(size > 0) {
                newName = fileName + "(" + size + ")" + suffix;
            }
            System.out.println(path+": "+size);
        }
        repeatedFileNum++;
        return folderPath + "\\" + newName;
    }
}
