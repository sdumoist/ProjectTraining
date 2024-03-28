package com.jxdinfo.doc.front.docmanager.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jxdinfo.doc.front.docmanager.model.DocFeedback;
import com.jxdinfo.doc.front.docmanager.model.FeedbackAttachment;
import com.jxdinfo.doc.front.docmanager.service.DocFeedbackService;
import com.jxdinfo.doc.front.docmanager.service.FeedbackAttachmentService;
import com.jxdinfo.doc.manager.docmanager.service.FilesService;
import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.core.exception.HussarException;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 类的用途：意见反馈跳转、提交相关
 * 创建日期：2018-12-03
 * 创建人：zhongguangrui
 * 修改历史：
 */
@Controller
@RequestMapping("/feedback")
public class FeedbackController{

    /**
     * PREFIX前台
     */
    private String prefixFront = "/doc/front/docmanager/";
    /**
     * PREFIX后台
     */
    private String prefixManager = "/doc/manager/feedbackmanager/";

    /**
     * 反馈表服务bean
     */
    @Autowired
    private DocFeedbackService docFeedbackService;

    /**
     * 反馈附件 服务bean
     */
    @Autowired
    private FeedbackAttachmentService feedbackAttachmentService;
    /**
     * 文件处理bean
     */
    @Autowired
    private FilesService filesService;

    /**
     * 打开用户反馈窗口
     * @return  路径
     */
    @GetMapping("")
    public String openFeedback(){
        return prefixFront + "front-feedback.html";
    }

    /**
     * 添加反馈
     * @param attachmentUrls    路径集
     * @param feedbackType      反馈类型
     * @param feedbackDescribe  反馈描述
     * @param contackWay        联系方式
     * @return  反馈结果
     */
    @PostMapping(value = "/add_feedback")
    @ResponseBody
    public String addFeedback(String feedbackType,String feedbackDescribe,
                              String contackWay,String attachmentUrls){
        DocFeedback feedback = new DocFeedback(feedbackType,contackWay,feedbackDescribe);
        // 获取当前用户信息
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        // 将信息补充完整
        feedback.setFeedbackUserId(userId);
        feedback.setFeedbackUser(userName);
        feedback.setFeedbackTime(new Timestamp(new Date().getTime()));
        // 执行添加反馈
        boolean isAdd1 = docFeedbackService.save(feedback);
        boolean isAdd2 = true;
        if (! "".equals(attachmentUrls) && attachmentUrls != null) {
            String[] urls = attachmentUrls.split(",");
            List<FeedbackAttachment> attachments = new ArrayList<>();
            // 遍历上传的附件地址
            for (String url : urls) {
                FeedbackAttachment attachment = new FeedbackAttachment();
                attachment.setAttachmentType("0");
                attachment.setFeedbackId(feedback.getFeedbackId());
                attachment.setAttachmentUrl(url);
                attachments.add(attachment);
            }
            // 执行添加附件
            isAdd2 = feedbackAttachmentService.saveBatch(attachments);
        }
        return isAdd1 && isAdd2 ? "反馈成功":"反馈失败";
    }

    /**
     * 上传反馈文件
     *
     * @param file 上传的文件
     * @return 文件名
     * @Title: upload
     */
    @RequestMapping(method = RequestMethod.POST, path = "/upload_attachment")
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
     * 权限菜单：反馈管理跳转
     * @return  路径
     */
    @RequiresPermissions("feedback:manager")
    @GetMapping("/manager")
    public String manageFeedback(){
        return prefixManager + "feedback-manager.html";
    }

    /**
     * 查询反馈信息列表
     * @param pageNumber    当前页
     * @param pageSize      每页记录条数
     * @param name          模糊查询参数
     * @return              map集合
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> getList(@RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                      @RequestParam(value = "pageSize", defaultValue = "20") int pageSize,String name){
        List<DocFeedback> list = new ArrayList<>();
        // 判断是否需要模糊查询
        if (name != null && name != ""){
            list = docFeedbackService.list(
                    new QueryWrapper<DocFeedback>()
                            .like("feedback_user","%" + name + "%")
                            .orderBy(true, false, "feedback_time"));
        }else {
            list = docFeedbackService.list(
                    new QueryWrapper<DocFeedback>()
                            .orderBy(true, false, "feedback_time"));
        }

        Map<String, Object> map = new HashMap<>();
        map.put("total", list.size());
        // 修饰所有记录。根据设定参数截断
        map.put("rows", list.stream()
                .skip((pageNumber - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList()));
        return map;
    }

    /**
     * 查看详情页
     * @param feedbackId    要查看的反馈信息ID
     * @param model         model
     * @return              路径
     */
    @GetMapping("/viewFeedback/{feedbackId}")
    public String viewFeedback(@PathVariable String feedbackId,Model model){
        // 查询单条记录（包含图片附件）
        DocFeedback docFeedback = docFeedbackService.selectDetailFeedback(feedbackId);
        // 将图片转换为json格式
        String json = JSONObject.toJSONString(docFeedback.getFeedbackAttachments());
        model.addAttribute("feedback",docFeedback);
        model.addAttribute("imgs",json);
        return prefixManager + "feedback-detail.html";
    }

    /**
     * 删除反馈记录
     * @param feedbackIds   要删除的反馈ID集
     * @return      删除结果（1：成功；0：失败）
     */
    @PostMapping("/delFeedback")
    @ResponseBody
    public int delFeedback(String feedbackIds){
        // 将字符串转换为List集合
        String[] ids = feedbackIds.split(",");
        List<String> idList = Arrays.asList(ids);
        int result = 0;
        // 先删除附件
        boolean attachFlag = feedbackAttachmentService.remove(
                new QueryWrapper<FeedbackAttachment>().in("feedback_id",idList));
        // 再删除反馈记录
        boolean feedbackFlag = docFeedbackService.removeByIds(idList);
        // 原版 result = attachFlag && feedbackFlag ? 1 : 0;  反馈的时候附件不是必填项， attachFlag 删除成功了也不一定是true
        result = feedbackFlag ? 1 : 0;
        return result;
    }
}
