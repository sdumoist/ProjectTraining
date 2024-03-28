package com.jxdinfo.doc.mobileapi.Integralmanager;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docintegral.model.ExemptIntegral;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import com.jxdinfo.doc.manager.docintegral.service.ExemptIntegralService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.hussar.config.front.common.response.ApiResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

@CrossOrigin
@Controller
@RequestMapping("/mobile/integral")
public class IntegralController {

    /**
     * 目录服务类
     */
    @Resource
    private JWTUtil jwtUtil;
    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRecordService integralRecordService;

    @Resource
    private ExemptIntegralService exemptIntegralService;

    /**
     * 文档信息服务类
     */
    @Resource
    private DocInfoService idocInfoService;

    /**
     * 获取用户积分
     * @return 积分查询结果
     */
    @RequestMapping("/showIntegral")
    @ResponseBody
    public ApiResponse showIntegral(){
        // 用户id
        String userId = jwtUtil.getSysUsers().getUserId();
        JSONObject json = new JSONObject();
        Integer integral = integralRecordService.showIntegral(userId);
        json.put("integral", integral);
        return ApiResponse.data(200,json,"");
    }

    @PostMapping("/addCheck")
    @ResponseBody
    public String addCheck(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        List listTop= exemptIntegralService.addCheck(list);
        if(listTop.size()>0){
            return "false";
        }else{
            return "true";
        }
    }

    /**
     * 新增
     */
    @PostMapping("/exemptAdd")
    @ResponseBody
    public String exemptAdd(String fsFolderIds, String type,String fileName) {

        String[] strArr = fsFolderIds.split(",");
        String[] fileNames = fileName.split(",");
        String[] types = fileName.split(",");
        try {
            List list = new ArrayList();
            list.addAll(Arrays.asList(strArr));
            for (int i=0;i<strArr.length;i++){
                ExemptIntegral exemptIntegral = new ExemptIntegral();
                exemptIntegral.setFileId( UUID.randomUUID().toString().replace("-", ""));
                exemptIntegral.setFileType(types[i]);
                exemptIntegral.setDocId(strArr[i]);
                exemptIntegral.setDocName(fileNames[i]);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                exemptIntegral.setCreateTime(ts);
                exemptIntegralService.save(exemptIntegral);
            }
            return "true";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "false";
        }
    }

    /**
     * @param docId    下载或上传的文档id
     * @param ruleCode 积分编码
     * @return 积分增加结果信息
     * @author luzhanzhao
     * @date 2018-12-07
     */
    @RequestMapping("/addIntegral")
    @ResponseBody
    public Map addIntegral(String docId, String ruleCode) {
        //获取该文档的作者信息
        String docAuthorId = "";
        if (!StringUtil.checkIsEmpty(docId)) {
            docAuthorId = idocInfoService.getDocDetail(docId).getAuthorId();
        }
        Integer integral = 0;
        String msg = "";
        Map result = new HashMap();
        //获取当前登录用户
        String userId = jwtUtil.getSysUsers().getUserId();
        //判断积分规则编码
        switch (ruleCode) {
            case "login":
            case "search":
                //添加积分
                integral = integralRecordService.addIntegral(null, userId, ruleCode);
                //返回前台的提示信息
                msg += "+" + integral + "";
                break;
            case "download":
            case "share":
            case "preview":
                if (docAuthorId.equals(userId)) {//如果当前用户是作者，则不做处理
                    return null;
                } else if (integralRecordService.count(
                        new QueryWrapper<IntegralRecord>()
                                .eq("doc_id", docId)
                                .eq("user_id", userId)
                                .eq("operate_rule_code", ruleCode)) > 0) {// 如果当前用户存在对本文档的下载记录，则不作处理
                    return null;

                } else {
                    //先为当前用户执行积分变动
                    integral = integralRecordService.addIntegral(docId, userId, ruleCode);
                    if (integral == null) {
                        return null;
                    }
                    if (integral == 0 && "download".equals(ruleCode)) {//如果积分变动情况为0，则说明积分不足，返回提示信息
                        msg += "积分不足";
                    } else if (integral == 0) {
                        msg += integral + "";
                    } else {//为被下载用户执行增加积分操作
                        String captureName = StringUtil.captureName(ruleCode);
                        String beRuleCoded = captureName.substring(captureName.length() - 1).
                                equals("e") ? "be" + captureName + "d" : "be" + captureName + "ed";


                        integralRecordService.addIntegral(docId, docAuthorId, beRuleCoded);
                        //因为从数据库中取到的值为负数，所以不需要加负号
                        if (integral > 0) {
                            msg += "+" + integral + "";
                        } else {
                            msg += integral + "";
                        }
                    }
                }
                break;
            case "upload":
                //插入积分规则到数据库中
                integral = integralRecordService.addIntegral(docId, userId, ruleCode);
                msg += "+" + integral + "";
                break;
            default:
                break;
        }
        result.put("msg", msg);
        result.put("integral", integral);
        return result;
    }
}
