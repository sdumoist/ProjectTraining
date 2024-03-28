package com.jxdinfo.doc.client.newversionmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.client.response.ApiResponse;
import com.jxdinfo.doc.common.constant.DocConstant;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.personalmanager.model.DocVersion;
import com.jxdinfo.doc.front.personalmanager.service.DocVersionService;
import com.jxdinfo.doc.manager.docbanner.service.BannerService;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.doctop.service.DocTopService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by ZhongGuangrui on 2019/1/2.
 * 版本管理相关 控制器
 */
@Controller
@RequestMapping("/client/newVersion")
public class ClientNewVersionController {
    /**
     * 版本管理 服务层
     */
    @Autowired
    private DocVersionService docVersionService;
    /**
     * 文档管理 服务层
     */
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Resource
    private JWTUtil jwtUtil;

    /**
     * 文档权限管理 服务层
     */
    @Autowired
    private DocFileAuthorityService docFileAuthorityService;
    /**
     * es工具类
     */
    @Autowired
    private ESUtil esUtil;

    /**
     * 广告位service
     */
    @Autowired
    private BannerService bannerService;
    /**
     * 置顶service
     */
    @Autowired
    private DocTopService docTopService;
    /**
     * 打开上传新版页面
     * @param model
     * @param openFileId    打开的路径ID
     * @param path          上传路径
     * @param oldDocId      旧版文档id
     * @param type          文件类型
     * @return              页面路径
     * @author              ZhongGuangrui
     */
    @RequestMapping("/viewUpload")
    public String viewUploadVersion(Model model, String openFileId, String path,String oldDocId,String type){
        try {
            String filePath = null;
            if( path != null ){
                filePath = URLDecoder.decode(path, "UTF-8");
            }else{
                filePath = path;
            }
            String userId = UserInfoUtil.getUserInfo().get("ID").toString();
            String userName = ShiroKit.getUser().getName();
            model.addAttribute("userName", userName);
            model.addAttribute("userId", userId);
            model.addAttribute("openFileId",openFileId);
            model.addAttribute("path",filePath);
            model.addAttribute("oldDocId",oldDocId);
            model.addAttribute("fileType",type);
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "/doc/front/personalcenter/uploadVersion.html";
    }

    /**
     * 打开查看历史版本页面
     * @param model
     * @param oldDocId      入口文档ID
     * @return              页面路径
     * @author              ZhongGuangrui
     */
    @RequestMapping("/viewHistory")
    public String viewHistoryVersions(Model model,String oldDocId){
        model.addAttribute("oldDocId",oldDocId);
        return "/doc/front/personalcenter/versionHistory.html";
    }

    /**
     * 文档历史版本列表获取
     * @param name          文件名，模糊查询
     * @param pageNumber    当前页
     * @param pageSize      每页条数
     * @param oldDocId      旧版文档ID
     * @param order         排序规则（2：时间升序；3：时间降序）
     * @return              Map
     * @author              ZhongGuangrui
     */
    @RequestMapping("/list")
    @ResponseBody
    public ApiResponse list(String name, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "7") int pageSize, String oldDocId , String order){
        int beginIndex = pageNumber * pageSize - pageSize;
        String userId = jwtUtil.getSysUsers().getUserId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);

        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        List<DocInfo> list = docVersionService.selectVersionHistoriesByDocId(oldDocId,order,name);
        int count = list.size();
        list = list.stream()
                .skip(beginIndex)
                .limit(pageSize)
                .collect(Collectors.toList());

        Map histories = new HashMap();
        histories.put("msg","success");
        histories.put("code",0);
        histories.put("adminFlag",adminFlag);
        histories.put("rows",list);
        histories.put("count",count);
        return ApiResponse.data(200,histories,"");
    }

    /**
     * 删除历史版本
     * @param docIds     历史版本的文档id
     * @return      删除成功 >0  删除失败  ==0
     */
    @RequestMapping("/deleteVersion")
    @ResponseBody
    public ApiResponse deleteVersion(String docIds){
        String[] docId = docIds.split(",");
        return ApiResponse.data(200,docVersionService.updateValidFlag(docId),"");
    }

    /**
     * 设置最新版本
     * @param oldDocId  当前最新版本文档id
     * @param docId     要设为最新版本的文档id
     * @return
     */
    @RequestMapping("/setVersion")
    @ResponseBody
    public ApiResponse setVersion(String oldDocId,String docId){
        // 获取当前用户
        String userId = jwtUtil.getSysUsers().getUserId();
        // 查找两个版本的文件
        DocInfo oldDoc = docInfoService.getById(oldDocId);
        DocInfo doc = docInfoService.getById(docId);
        // 更新下载量、浏览量等数据
        doc.setDownloadNum(oldDoc.getDownloadNum());
        doc.setReadNum(oldDoc.getReadNum());
        doc.setTags(oldDoc.getTags());
        doc.setShareFlag(oldDoc.getShareFlag());
        // 设置有效值
        doc.setValidFlag("1");
        oldDoc.setValidFlag("0");
        // 设置es有效值
        Map map = new HashMap(1);
        //0为无效，1为有效
        map.put("recycle", DocConstant.VALIDTYPE.INVALID.getValue());
        boolean validOld = esUtil.updateIndex(oldDocId, map) != 0;
        map.put("recycle",DocConstant.VALIDTYPE.VALID.getValue());
        boolean validNew = esUtil.updateIndex(docId, map) != 0;
        List<DocInfo> docInfos = new ArrayList<>();
        docInfos.add(doc);
        docInfos.add(oldDoc);
        boolean infoFlag = docInfoService.updateBatchById(docInfos);
        // 继承旧版权限
        docFileAuthorityService.remove(new QueryWrapper<DocFileAuthority>().eq("file_id", docId));
        List<String> indexList = new ArrayList<>();
        List<DocFileAuthority> list = docFileAuthorityService.list(new QueryWrapper<DocFileAuthority>().eq("file_id",oldDocId));
        // 操作者类型（0：userID,1:groupID,2:organID，3:全体成员）
        for (int i = 0; i < list.size(); i ++){
            DocFileAuthority item = list.get(i);
            String esId = item.getAuthorId();
            if (item.getAuthorType() == 2){
                esId = item.getOrganId();
            }
            indexList.add(esId);
            // 将list中旧版本docId换成新的docId
            list.get(i).setFileId(docId);
            list.get(i).setFileAuthorityId(null);
        }
        indexList.add(userId);
        Map mapEs = new HashMap(1);
        // 更新es权限
        mapEs.put("permission", indexList.toArray(new String[indexList.size()]));
        boolean esFlag = esUtil.updateIndex(docId, mapEs) != 0;
        boolean authorityFlag = true;
        if (list.size() > 0) {
            authorityFlag = docFileAuthorityService.saveBatch(list);
        }
        // 新增版本信息
        DocVersion oldVersion = docVersionService.getOne(new QueryWrapper<DocVersion>().eq("doc_id", oldDocId));
        int versionNumber = docVersionService.selectVersionNumber(oldVersion.getVersionReference());
        DocVersion newVersion = new DocVersion();
        newVersion.setVersionReference(oldVersion.getVersionReference());
        newVersion.setDocId(doc.getDocId());
        newVersion.setValidFlag("1");
        newVersion.setApplyTime(doc.getCreateTime());
        newVersion.setApplyUserId(doc.getUserId());
        newVersion.setVersionNumber(versionNumber+1);
        docVersionService.save(newVersion);
        /*DocVersion docVersion = new DocVersion();
        docVersion.setApplyTime(new Timestamp(System.currentTimeMillis()));
        docVersion.setApplyUserId(userId);
        boolean timeFlag = docVersionService.update(docVersion,new EntityWrapper<DocVersion>().eq("doc_id",docId));*/
        //更新置顶、广告位、专题文件
        List<String> oldDocIds =  Arrays.asList(oldDocId.split(","));
        List listTop = docTopService.addCheck(oldDocIds);
        boolean TopFlag = true;
        if(listTop.size()>0){
            TopFlag = docTopService.updateTop(oldDocId,docId);
        }
        List listBanner = bannerService.selectBannerById(oldDocId);
        boolean BannerFlag = true;
        if (listBanner.size()>0){
            String docType = doc.getDocType();
            String bannerHref;
            if(".png".equals(docType)||".jpg".equals(docType)||".gif".equals(docType)||".bmp".equals(docType)||".jpeg".equals(docType)){
                bannerHref = "/preview/toShowIMG?id="+docId;
            }else if(".mp4".equals(docType)||".wmv".equals(docType)){
                bannerHref = "/preview/toShowVideo?id="+docId;
            } else if(".mp3".equals(docType)||".m4a".equals(docType)){
                bannerHref = "/preview/toShowVoice?id="+docId;
            } else if(".pdf".equals(docType)
                    || ".doc".equals(docType) || ".docx".equals(docType) || ".dot".equals(docType)
                    || ".wps".equals(docType) || ".wpt".equals(docType)
                    || ".xls".equals(docType) || ".xlsx".equals(docType) || ".xlt".equals(docType)
                    || ".et".equals(docType) || ".ett".equals(docType)
                    || ".ppt".equals(docType) || ".pptx".equals(docType) || ".ppts".equals(docType)
                    || ".pot".equals(docType) || ".dps".equals(docType) || ".dpt".equals(docType)
                    || ".txt".equals(docType)
                    || ".ceb".equals(docType)){
                bannerHref = "/preview/toShowPDF?id="+docId;
            }
            else {
                bannerHref = "/preview/toShowOthers?id="+docId;
            }
            BannerFlag = bannerService.updateBanner(oldDocId,docId,bannerHref);
        }


        return ApiResponse.data(200,(infoFlag && esFlag && authorityFlag  && validOld && validNew && TopFlag && BannerFlag) + "","");
    }
}
