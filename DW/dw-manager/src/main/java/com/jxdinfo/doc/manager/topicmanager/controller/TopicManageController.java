package com.jxdinfo.doc.manager.topicmanager.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.doc.manager.topicmanager.service.ITopicDocManagerService;
import com.jxdinfo.doc.manager.topicmanager.service.SpecialTopicService;
import com.jxdinfo.hussar.bsp.permit.service.ISysIdtableService;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 专题维护
 *
 * @author zhangzhen
 * @date 2018/4/9
 */
@Controller
@RequestMapping("/topic")
public class TopicManageController {




    @Value("${docbase.uploadPath}")
    private String base;
    @Value("${docbase.filedir}")
    private String uploadPath;


    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;
    /**
     * 文件处理ESUtil
     */
    @Autowired
    private FilesService filesService;

    @Autowired
    private DocGroupService docGroupService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;

    /**
     * 专题维护
     */
    @Autowired
    private SpecialTopicService specialTopicService;

    @Autowired
    private ITopicDocManagerService topicDocManagerService;
    /**
     * 文档信息
     */
    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private FrontTopicService frontTopicService;
    
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private ITopicDocManagerService iTopicDocManagerService;
    /**
     * 获取编号公共方法
     */
    @Autowired
    private ISysIdtableService sysIdtableService;
    /**
     * 查询专题及专题下的文档
     *
     * @return 查询专题及专题下的文档
     * @date 2018-4-11
     */
    @RequestMapping("/getSpecialTopicFiles")
    @ResponseBody
    public List getSpecialTopicFiles() {
        List<Map<String, String>> list = new ArrayList<>();
        list = specialTopicService.getSpecialTopicFiles();
        return list;
    }

    /**
     * 打开专题查看
     */

    @RequiresPermissions("topic:topicListView")
    @RequestMapping("/topicListView")
    public String topicListView() {
        return "/doc/manager/topicmanager/topic-list.html";
    }

    /**
     * 打开专题新增
     */
    @RequestMapping("/topicAdd")
    public String topicAdd(Model model) {
        String currentCode = this.sysIdtableService.getCurrentCode("TOPIC_NUM", "doc_special_topic");
        int num = Integer.parseInt(currentCode);
        //int num = specialTopicService.getMaxOrder();
        model.addAttribute("lastNum", num);
        model.addAttribute("num", num + 1);
        return "/doc/manager/topicmanager/topic-add.html";
    }

    /**
     * 专题信息列表查询
     *
     * @return 专题列表
     */
    @RequestMapping("/topicList")
    @ResponseBody
    public JSON getTopicList(String topicName, int page, int limit) {
        int beginIndex = page * limit - limit;
        //开始位置
        
        String topicNameStr = StringUtil.transferSqlParam(topicName);
        List<SpecialTopic> topicList = specialTopicService.topicList(topicNameStr, beginIndex, limit);
        int topicCount = frontTopicService.getTopicListCount(topicNameStr);
        JSONObject json = new JSONObject();
        json.put("count", topicCount);
        json.put("data", topicList);
        json.put("msg", "success");
        json.put("code", 0);
        return json;
    }

    /**
     * 新增专题
     *
     * @param specialTopic 专题对象
     * @param docIds       文档ID
     * @return 新增结果
     */
    @RequestMapping("/addTopic")
    @ResponseBody
    public JSON addTopic(SpecialTopic specialTopic, String docIds) {
        String topicId = UUID.randomUUID().toString().replaceAll("-", "");
        //专题ID
        specialTopic.setTopicId(topicId);


        specialTopic.setCreateUserId(ShiroKit.getUser().getId());

        JSONObject json = new JSONObject();
        //检查专题名称是否已经存在
        int num = specialTopicService.checkTopicExist(specialTopic);
        //得到查重的数量
        if (num > 0) {
            json.put("result", "0");
        } else {
            int addNum = specialTopicService.addTopic(specialTopic, docIds);
            if (addNum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(topicId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(30);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return json;
    }

    /**
     * 删除专题下的文档
     *
     * @param delIds  文档ID集合
     * @param topicId 专题Id
     * @return 删除的数量
     */
    @RequestMapping("/delDocById")
    @ResponseBody
    public int delDocById(String delIds, String topicId) {
        List<String> idList = Arrays.asList(delIds.split(","));
        //删除的文件ID集合
        int num = specialTopicService.delDocById(idList, topicId);
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(topicId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(34);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return num;
    }

    /**
     * 编辑专题
     *
     * @param specialTopic 专题对象
     * @param docIds       文档ID
     * @return 编辑的数量
     */
    @RequestMapping("/editTopic")
    @ResponseBody
    public JSON editTopic(SpecialTopic specialTopic, String docIds) {
        JSONObject json = new JSONObject();
        int num = specialTopicService.checkTopicExist(specialTopic);
        specialTopic.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        specialTopic.setUpdateUserId(ShiroKit.getUser().getId());
        if (num > 0) {
            json.put("result", "0");
        } else {
            int editNum = specialTopicService.editTopic(specialTopic, docIds);
            //获取新增的数据条数
            if (editNum == 1) {
                json.put("result", "1");
            } else {
                json.put("result", "2");
            }
        }
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(specialTopic.getTopicId());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(31);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return json;
    }

    /**
     * 批量删除专题
     *
     * @param ids 专题ID
     * @return 删除的数量
     */
    @RequestMapping("/delTopics")
    @ResponseBody
    public int delTopicsById(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        if (list.size()==1){
            String topcid = list.toString();
            //拼装操作历史记录
            docResourceLog.setId(id);
            docResourceLog.setResourceId(topcid);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(32);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
        }else {
            docResourceLog.setId(id);
            docResourceLog.setOperateTime(ts);
            docResourceLog.setResourceType(2);
            docResourceLog.setUserId(ShiroKit.getUser().getId());
            docResourceLog.setOperateType(36);
            docResourceLog.setAddressIp(HttpKit.getIp());
            resInfoList.add(docResourceLog);
            iTopicDocManagerService.insertResourceLog(resInfoList);
        }
        //获得Id集合
        return specialTopicService.delDocs(list);
    }

    /**
     * 发布专题
     *
     * @param ids 专题ID集合
     * @return 发布的数量
     */
    @RequestMapping("/publishTopic")
    @ResponseBody
    public int publishTopic(String ids, Integer topicShow) {
        List<String> list = Arrays.asList(ids.split(","));
        //获得Id集合
        return specialTopicService.publishTopics(list, topicShow);
    }

    /**
     * 打开专题修改页面
     */
    @RequestMapping("/topicEdit")
    public String topicEdit(Model model, String topicId) {
        SpecialTopic specialTopic = specialTopicService.searchTopicDetail(topicId);
        
        try {
			String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
			specialTopic.setTopicCover(topicCover);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
        int num = specialTopicService.getMaxOrder();
        model.addAttribute("lastNum", num);
        
        String userName = specialTopic.getAuthorName();
        model.addAttribute("users", userName);
        
        model.addAttribute("specialTopic", specialTopic);
        return "/doc/manager/topicmanager/topic-edit.html";
    }

    /**
     * 打开专题修改页面
     */
    @RequestMapping("/topicView")
    public String topicView(Model model, String topicId) {
        SpecialTopic specialTopic = specialTopicService.searchTopicDetail(topicId);
        
        try {
			String topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
			specialTopic.setTopicCover(topicCover);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
        int num = specialTopicService.getMaxOrder();
        model.addAttribute("lastNum", num);
        
        String userName = specialTopic.getAuthorName();
        model.addAttribute("users", userName);
        
        model.addAttribute("specialTopic", specialTopic);
        return "/doc/manager/topicmanager/topic-view.html";
    }


    /**
     * 查看专题下文件页面
     */
    @RequestMapping("/topicDocList")
    public String topicDocList(Model model, String topicId) {
        SpecialTopic specialTopic = specialTopicService.searchTopicDetail(topicId);
        
        int num = specialTopicService.getMaxOrder();
        model.addAttribute("lastNum", num);
        
        String userName = specialTopic.getAuthorName();
        model.addAttribute("users", userName);
        
        model.addAttribute("specialTopic", specialTopic);
        return "/doc/manager/topicmanager/topic_doc_list.html";
    }

    /**
     * 查看专题下文件列表
     */
    @RequestMapping("/topicShowDoc")
    @ResponseBody
    public JSON topicShowDoc(Model model, String topicId,int page ,int limit) {
        int startNum = page * limit - limit;
        String userId = UserInfoUtil.getCurrentUser().getId();
        List<String> listGroup = docGroupService.getPremission(userId);
        FsFolderParams fsFolderParams = new FsFolderParams();
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("2");
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        List<String> roleList= ShiroKit.getUser().getRolesList();
        // 获取当前文库权限
        Integer adminFlag= CommonUtil.getAdminFlag(roleList);;
        String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
        fsFolderParams.setType("2");
        String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
        List docList = frontTopicService.getDocByTopicId(topicId, "create_time", startNum, limit,userId,
                listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
        int docCount=topicDocManagerService.getDocCountTopicId(topicId);
        docList=   changeSize(docList);
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(topicId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(33);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        JSONObject json = new JSONObject();
        json.put("count", docCount);
        json.put("data", docList);
        json.put("msg", "success");
        json.put("code", 0);
        return  json;
    }



    /**
     * 删除专题下的文件
     *
     * @param id      文档ID
     * @param topicId 专题ID
     * @return 发布的数量
     */
    @RequestMapping("/delDoc")
    @ResponseBody
    public int delDoc(String id, String topicId) {
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String uid = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(uid);
        docResourceLog.setResourceId(topicId);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(34);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return specialTopicService.delDoc(id, topicId);
    }

    /**
     * 批量删除专题下的文件
     *
     * @param ids      文档ID
     * @param topicId 专题ID
     * @return 发布的数量
     */
    @RequestMapping("/delDocs")
    @ResponseBody
    public int delDocs(String ids, String topicId) {
        List<String> list = Arrays.asList(ids.split(","));
        //获得Id集合
        return specialTopicService.delDocById(list,topicId);

    }

    /**
     * 缓存专题查看数据数据
     *
     * @author xubin
     * @date 2018-07-10 9:04
     */
    @RequestMapping("/cacheViewNum")
    @ResponseBody
    public void saveCache(String topicId) {
       
        //获取缓存中的数据
    	
    	cacheToolService.getAndUpdateTopicReadNum(topicId);

        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        String id = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(id);
        docResourceLog.setResourceId(topicId);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        docResourceLog.setUserId(userId);
        docResourceLog.setOperateType(3);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        docInfoService.insertResourceLog(resInfoList);     //专题预览记录

    }


    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件名
     * @Title: upload
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload")
    @ResponseBody
    public JSONObject upload(@RequestPart("file") MultipartFile file) {
        JSONObject json = new JSONObject();
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        String fName = IdWorker.get32UUID() + prefix;
        try {
        	String  filePath = filesService.upload(file,fName);
            json.put("fName", filePath);
            json.put("fileName", fileName);
        } catch (IOException e) {
            throw new HussarException(BizExceptionEnum.UPLOAD_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 预览图片
     *
     * @return 文件名
     * @Title: upload
     */
    @RequestMapping("/viewTopicPic")
    public void getList(HttpServletResponse response, String fName) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(uploadPath + fName);
            data = new byte[input.available()];
            input.read(data);
            response.getOutputStream().write(data);
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

/*    *//**
     * 专题图推送金企信
     *//*
    @RequestMapping("/sentTopicToJQX")
    @ResponseBody
    public int sentTopicToJQX(String topicId) {
        SpecialTopic specialTopic = specialTopicService.searchTopicDetail(topicId);
        JqxWeChatProxy.pushWeChatArticleMessageToJqx(specialTopic, "66D272B1-6967-E936-E671-EBE4E574BBFE",
                APPID, APPNAME);
        return 1;
    }  //TODO 需要优化*/

    /**
     * 移动专题
     * @return 编辑的数量
     */
    @RequestMapping("/moveTopic")
    @ResponseBody
    public int moveTopic(String table,String idColumn,String idOne, String idTwo) {
        int num = specialTopicService.moveTopic(table,idColumn,idTwo,idOne);
        //拼装操作历史记录
        List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
        DocResourceLog docResourceLog = new DocResourceLog();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String uid = UUID.randomUUID().toString().replace("-", "");
        docResourceLog.setId(uid);
        docResourceLog.setResourceId(idOne);
        docResourceLog.setOperateTime(ts);
        docResourceLog.setResourceType(2);
        docResourceLog.setUserId(ShiroKit.getUser().getId());
        docResourceLog.setOperateType(35);
        docResourceLog.setAddressIp(HttpKit.getIp());
        resInfoList.add(docResourceLog);
        iTopicDocManagerService.insertResourceLog(resInfoList);
        return num;
    }
    
    /**
     * 转化文件大小的方法
     */
    public List<Map<String, String>> changeSize(List<Map<String, String>> list) {
        for (Map<String, String> map: list) {
            if (map.get("fileSize") != null&&!"".equals(map.get("fileSize"))) {
            	map.put("fileSize",FileTool.longToString(String.valueOf((map.get("fileSize")))));
            }
        }
        return list;
    }
}
