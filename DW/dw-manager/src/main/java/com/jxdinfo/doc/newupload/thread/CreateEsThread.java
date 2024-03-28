package com.jxdinfo.doc.newupload.thread;

import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.ExamineProperties;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.TikaUtil;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.docmanager.service.FsFileService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.newupload.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2019-1-9
 * @description 创建Es的线程
 */
public class CreateEsThread extends Thread {
    private String docId = "";
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    static final public Logger LOGGER = LoggerFactory.getLogger(CreateEsThread.class);

    /**
     * ES操作接口
     */
    private ESService esService = appCtx.getBean(ESService.class);

    /**
     * 缓存操作接口
     */
    private CacheToolService cacheToolService = appCtx.getBean(CacheToolService.class);

    /**
     * 文件上传状态接口
     */
    private UploadService uploadService = appCtx.getBean(UploadService.class);
    /**
     * fs_file Mapper 接口
     */
    private FilesMapper filesMapper = appCtx.getBean(FilesMapper.class);
    /**
     * FAST操作接口
     */
    private FsFileService fsFileService = appCtx.getBean(FsFileService.class);

    /**
     * FAST操作接口
     */
    private IFsFolderService fsFolderService = appCtx.getBean(IFsFolderService.class);
    /**
     * FAST操作接口
     */
    private DocInfoService docInfoService = appCtx.getBean(DocInfoService.class);
    private ExamineProperties examineProperties = appCtx.getBean(ExamineProperties.class);

    private FileTool fileTool = appCtx.getBean(FileTool.class);

    private FilesService filesService = appCtx.getBean(FilesService.class);

    private Environment environment = appCtx.getBean(Environment.class);

    public CreateEsThread(String id) {
        docId = id;
    }

    @Override
    public void run() {
        try {
            FsFile fsFileTemp = filesMapper.selectById(docId);
            List<FsFile> list = fsFileService.getInfoByMd5(fsFileTemp.getMd5());
            List<Map<String, String>> uploadList = uploadService.selectUpload(docId);
            String sourcePath = uploadList.get(0).get("sourcePath");

            // 转换次数加一
            String times = uploadList.get(0).get("times");
            int timesInt = null == times || "".equals(times) ? 1 : Integer.parseInt(times);
            String newTimes = Integer.toString(timesInt + 1);

            String title = fsFileTemp.getFileName();
            LOGGER.info("******************文件:" + title + "进入CreateEsThread线程，开始转化PDF******************");
            File sourceFile = new File(sourcePath);
            if (sourceFile.exists()) {

                Map<String, String> ready = new HashMap<>();
                ready.put("docId", docId);
                ready.put("times", newTimes);
                uploadService.updateUploadState(ready);

                // 文档内容
                String content = null;
                //文件类型
                String contentType = "";
                // 转换标志 （false:未成功转换，true:成功转换ao）
                Map<String, Object> pdfInfo = new HashMap<String, Object>();
                try {
                    //获取文件内容
                    //处理ceb文件
                    if (sourcePath.endsWith(".ceb") || sourcePath.endsWith(".txt")) {

                        content = uploadList.get(0).get("content") == null ? "" : uploadList.get(0).get("content");
                        contentType = uploadList.get(0).get("contentType");
                    } else if (sourcePath.endsWith(".rar")) {
                        content = "";
                        contentType = "";
                    } else {
                        Map<String, Object> metadata = TikaUtil.autoParse(sourcePath);
                        content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
                        contentType = metadata.get("contentType").toString();

                        // tif图片需要转换成pdf格式预览 es里面保存为pdf类型
                        if(sourcePath.endsWith(".tif") || sourcePath.endsWith(".tif")){
                            contentType = "application/msword";
                        }else if(sourcePath.endsWith(".m4a")){
                            contentType = "audio/x-m4a";
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("文件"+docId+"ES线程获取文件内容IO异常：f" + ExceptionUtils.getErrorInfo(e));
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error("文件"+docId+"ES线程获取文件内容异常" + ExceptionUtils.getErrorInfo(e));
                }
                pdfInfo.put("contentType", contentType);
                pdfInfo.put("content", content);
                if (cacheToolService.getFastDFSUsingFlag()) {
                    sourceFile.delete();
                }
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        LOGGER.info("******************文件:" + title + "正在生成ES索引******************");
                        //生成ES索引
                        Map<String, Object> docVO = new HashMap();
                        docVO.put("contentType", StringUtil.getString(pdfInfo.get("contentType")));
                        docVO.put("upDate", new Date());
                        docVO.put("content", StringUtil.getString(pdfInfo.get("content")));
                        try {
                            if (esService.getIndex(list.get(i).getFileId()) != null) {
                                esService.updateIndex(list.get(i).getFileId(), docVO);
                            }else {
                                DocInfo docInfo = docInfoService.getById(list.get(i).getFileId());
                                DocES docVOBean = new DocES();
                                String fileId = list.get(i).getFileId();
                                docVOBean.setId(fileId);
                                docVOBean.setContent(StringUtil.getString(pdfInfo.get("content")));
                                docVOBean.setContentType(StringUtil.getString(pdfInfo.get("contentType")));
                                docVOBean.setTitle(docInfo.getTitle());
                                String folderId = docInfo.getFoldId();
                                FsFolder fsFolder = fsFolderService.getById(folderId);
                                if (fsFolder != null) {
                                    docVOBean.setCategory(fsFolder.getFolderName());
                                }
                                if (docInfo.getTags() != null && !"".equals(docInfo.getTags())) {
                                    docVOBean.setTags(docInfo.getTags());
                                }
                                docVOBean.setRecycle(docInfo.getValidFlag());
                                docVOBean.setUpDate(new Date());
                                List<String> indexList = new ArrayList<>();
                                indexList.add("allpersonflag");
                                docVOBean.setPermission(indexList.toArray(new String[indexList.size()]));
                                esService.createESIndex(docVOBean);
                            }
                        } catch (Exception e) {

                            e.printStackTrace();

                            LOGGER.error("创建" + list.get(i).getFileId() + "ES失败：" + ExceptionUtils.getErrorInfo(e));
                        }
                    }
                }
                ready.put("state","3");
                ready.put("times","0");
                uploadService.updateUploadState(ready);
                LOGGER.info("******************文件:" + title + "创建ES结束******************");

                String analysisUsing = environment.getProperty("semanticAnalysis.analysisUsing");
                if ("true".equals(analysisUsing)) {
                    // 转换为txt并更新标签
                    filesService.getAndUpdateTags(docId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("创建ES失败：" + ExceptionUtils.getErrorInfo(e));
        }
    }
}
