package com.jxdinfo.doc.client.previewmanager.controller;

import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontDocInfoService;
import com.jxdinfo.doc.front.docmanager.service.PreviewService;
import com.jxdinfo.doc.front.docsharemanager.service.ShareResourceService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.collectionmanager.model.DocCollection;
import com.jxdinfo.doc.manager.collectionmanager.service.PersonalCollectionService;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/client/preview")
public class ClientPreviewController extends BaseController {

    @Resource
    private SysStruMapper sysStruMapper;


    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 文件 Mapper 接口
     */
    @Resource
    private FsFileMapper fsFileMapper;
    @Autowired
    private IFsFolderService fsFolderService;
    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /** 上传路径  */
    @Value("${docbase.uploadPath}")
    private String base;

    @Resource
    private JWTUtil jwtUtil;
    @Resource
    private PersonalOperateService operateService;

    /** 上传标志  */
    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;


    @Autowired
    private ISysUserRoleService sysUserRoleService;
    /**  文件类型 */
    private String fileType;

    /**  排序类型  */
    private String orderType;

    /** 文件名  */
    private String fileName;

    /** PDF文件ID */
    private String pdfFileId;

    /**  文件预览服务类 */
    @Autowired
    private PreviewService previewService;

    /** fast服务器服务类  */
    @Autowired
    FastdfsService fastdfsService;

    /** 文档服务类  */
    @Autowired
    private FrontDocInfoService frontDocInfoService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /** 缓存工具服务类 */
    @Autowired
    private CacheToolService cacheToolService;


    @Autowired
    private DocGroupService docGroupService;


    /** 文件工具类  */
    @Autowired
    private FileTool fileTool;

    /** 文档群组服务类 */
    @Autowired
    private FrontDocGroupService frontDocGroupService;

    /** es服务类  */
    @Autowired
    private ESService esService;

    @Autowired
    PersonalCollectionService personalCollectionService;

    /**
     * 分享服务
     */
    @Resource
    private ShareResourceService shareResourceService;
    @Value("${docbase.downloadPdfFile}")
    private String downloadPdfFile;


    @Resource
    private UploadService uploadService;

    @RequestMapping("/list")
    public void getList(HttpServletRequest request, HttpServletResponse response) {
        String range_size = request.getParameter("range_size");
        String pdfFileId = request.getParameter("fileId");
        String isThumbnails = request.getParameter("isThumbnails");
        String isView = request.getParameter("isView");
        FileInputStream input = null;
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        try {
            //isView 为0，则为预览的文件
            if (isView != null && isView.equals("0")) {
                File file = null;
                List<FsFile> list = fsFileMapper.getInfoByPdfPath(pdfFileId);
                String random = list.get(0).getMd5();
                String suffix = pdfFileId.substring(pdfFileId.lastIndexOf("."));
                file = new File(downloadPdfFile + "\\" + random + suffix);
                //文件不存在则重新读取
                if (!file.exists()||file.length()==0) {
                    file = fileTool.chuckPdf(pdfFileId);
                }
                inputStream = new FileInputStream(file);
                if (file.length() < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                //设置请求头
                response.setHeader("Accept-Ranges", "bytes");
                response.setHeader("Content-Type", "application/pdf");
                String range = request.getHeader("Range");
                if (ToolUtil.isNotEmpty(range)) {
                    range = range.replace("bytes=", "");
                    String[] ranges = range.split("-");
                    int startIndex = Integer.parseInt(ranges[0]);
                    int endIndex = Integer.parseInt(ranges[1]);
                    byte[] buffer = new byte[endIndex - startIndex + 1];
                    randomAccessFile = new RandomAccessFile(file, "r");
                    randomAccessFile.seek(startIndex);
                    randomAccessFile.read(buffer);
                    response.setHeader("Content-Range", "bytes " + startIndex + "-" + (endIndex - 1) + "/" + file.length() + "");
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    response.getOutputStream().write(buffer);
                } else {
                    if(Long.parseLong(range_size)<file.length()){
                        response.setHeader("Content-Length", file.length() + "");
                        response.getOutputStream().write(new byte[1024]);
                    }else {
                        //分片大于文件，直接加载文件
                        inputStream = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        OutputStream os = response.getOutputStream();
                        int i =0;
                        while ((i=inputStream.read(bytes))!=-1){
                            os.write(bytes,0,i);
                        }
                    }
                }
            } else {
                byte[] bytes = fileTool.downLoadFile(input, pdfFileId, isThumbnails);
                if (bytes.length < 1024 * 500) {
                    response.setDateHeader("expires", System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
                }
                response.getOutputStream().write(bytes);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }

                if (input != null) {
                    input.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @title: 获取文档详情
     * @description: 获取文档详情
     * @date: 2018-1-20.
     * @author: rxy
     * @param: request   response
     * @return:
     */
    @RequestMapping("/fileDetail")
    @ResponseBody
    public ApiResponse getFileDetail(String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (id != null) {
            String userId = jwtUtil.getSysUsers().getUserId();
            List<String> listGroup = docGroupService.getPremission(userId);
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(listGroup);
            List roleList=sysUserRoleService.getRolesByUserId(userId);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("2");
            fsFolderParams.setRoleList(roleList);
            String deptId = iSysUsersService.getById(userId).getDepartmentId();
            String orgId = null ;
            SysStru stru  = sysStruMapper.selectById(deptId);
            if (stru != null) {
                orgId = stru.getOrganAlias();
            }

            String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,orgId);
            //根据ID获得文档详情
            DocInfo docInfo = frontDocInfoService.getDocDetailMobile(id,userId,listGroup,levelCode,roleList);
            int collection = 0;
            List<DocCollection>  list =  personalCollectionService.selectByResourceId(docInfo.getDocId(), userId,"");
            if(list!=null && list.size()>0){
                collection = list.size();
            }
            //int collection = operateService.getMyHistoryCountByFileId(docInfo.getDocId(),userId,"5");
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            map.put("adminFlag", adminFlag);
            map.put("collection", collection);
            map.put("authority", docInfo.getAuthority());
            map.put("id", docInfo.getDocId());
            map.put("filePath", docInfo.getFilePath());
            map.put("filePdfPath", docInfo.getFilePdfPath());
            map.put("userId", docInfo.getUserId());
            map.put("author", docInfo.getAuthorName());
            map.put("createTime", docInfo.getCreateTime());
            map.put("title", docInfo.getTitle());
            map.put("shareFlag",docInfo.getShareFlag());
            map.put("tags",docInfo.getTags());
            //获得文件的类型字符串（前台根据文件类型判断展示内容）
            String fileSuffixName = docInfo.getDocType().substring(docInfo.getDocType().lastIndexOf(".") + 1);
            map.put("downloadNum", docInfo.getDownloadNum());
            map.put("readNum", cacheToolService.getReadNum(docInfo.getDocId()) + 1);
            map.put("fileSuffixName", fileSuffixName);
            map.put("docAbstract", docInfo.getDocAbstract());
            if (StringUtil.checkIsEmpty(docInfo.getFilePdfPath())){
                uploadService.checkUploadState(id);
            }
            map.put("uploadState",!StringUtil.checkIsEmpty(docInfo.getFilePdfPath()));
            if (docInfo.getFileSize() != null && !"".equals(docInfo.getFileSize())) {
                //文件大小转化
                map.put("fileSize", (FileTool.longToString(docInfo.getFileSize())));
            }
        }
        return ApiResponse.data(200,map,"");
    }

    /**
     * @author luzhanzhao
     * @param name 关键字
     * @param pageNumber 当前页数
     * @param pageSize 每页长度
     * @return 获取到的个人下载记录集合
     */
    @RequestMapping("/getMyPreviewList")
    @ResponseBody
    public Map list(String name, String[] typeArr, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "60") int pageSize, String order){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        String deptId =docFoldAuthorityService.getDeptIds( iSysUsersService.getById(userId).getDepartmentId());

        //获取当前登录人
        FsFolderParams fsFolderParams = new FsFolderParams();
        List<String> listGroup = docGroupService.getPremission(userId);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setGroupList(listGroup);
        List roleList = sysUserRoleService.getRolesByUserId(userId);
        fsFolderParams.setRoleList(roleList);
        //获得目录管理权限层级码
        fsFolderParams.setType("2");
        String levelCode = businessService.getLevelCodeByUserUploadClient(fsFolderParams,deptId);
        List<Map> list = operateService.getMyHistory(userId, "3", beginIndex, pageSize, name,typeArr,order,levelCode,deptId);
        int count = operateService.getMyHistoryCount(userId, "3", name);
        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("rows",list);
        histories.put("count",count);
        return histories;
    }
    /**
     * @author luzhanzhao
     * @date 2018-11-19
     * @param opType 要清空记录对应的操作类型（3：预览；4：下载）
     * @return 结果
     */
    @RequestMapping("/clearHistory")
    @ResponseBody
    public int clearHistory(@RequestParam(defaultValue = "3") String opType){
        //获取当前登录人
        String userId = jwtUtil.getSysUsers().getUserId();
        return operateService.clearHistory(userId, opType);
    }
}
