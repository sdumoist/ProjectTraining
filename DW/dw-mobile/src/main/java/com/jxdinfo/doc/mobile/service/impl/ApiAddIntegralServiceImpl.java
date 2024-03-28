package com.jxdinfo.doc.mobile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.front.docmanager.service.FrontFsFileService;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.personalcenter.service.PersonalOperateService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.ADD_INTEGRAL;


/**
 * 主页最新动态显示
 */
@Component
public class ApiAddIntegralServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = ADD_INTEGRAL;
    /**
     * 缓存工具服务类
     */
    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRecordService integralRecordService;

    /**
     * 文档信息服务类
     */
    @Resource
    private DocInfoService idocInfoService;
    /** 前台文件服务类 */

    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params 参数
     * @return Response
     * @description: 删除
     * @Title: execute
     * @author: yjs
     */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            String docId = String.valueOf(params.get("docId"));
            String userId = params.get("userId");
            String ruleCode = params.get("ruleCode");
            Map <String ,Object> map = new HashMap<>();
            String docAuthorId = "";
            if (!StringUtil.checkIsEmpty(docId)){
                docAuthorId = idocInfoService.getDocDetail(docId).getAuthorId();
            }
            Integer integral = 0;
            String msg = "";
            Map result = new HashMap();
            //获取当前登录用户
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
                        msg="当前用户是作者";
                        map.put("msg",msg);
                        map.put("integral",0);
                        response.setSuccess(true);
                        response.setData(map);
                    } else if (integralRecordService.count(
                            new QueryWrapper<IntegralRecord>()
                                    .eq("doc_id",docId)
                                    .eq("user_id",userId)
                                    .eq("operate_rule_code",ruleCode)) > 0){// 如果当前用户存在对本文档的下载记录，则不作处理
                        msg="存在下载记录";
                        map.put("msg",msg);
                        map.put("integral",0);
                        response.setSuccess(true);
                        response.setData(map);


                    }else {
                        //先为当前用户执行积分变动
                        integral = integralRecordService.addIntegral(docId, userId, ruleCode);
                        if (integral == null){
                            msg="积分不扣除";
                            map.put("msg",msg);
                            map.put("integral",0);
                            response.setSuccess(true);
                            response.setData(map);
                            return response;
                        }
                         else if (integral == 0 && "download".equals(ruleCode)) {//如果积分变动情况为0，则说明积分不足，返回提示信息
                            msg += "积分不足";
                        } else if(integral == 0){
                            msg += integral + "";
                        } else {//为被下载用户执行增加积分操作
                            String captureName = StringUtil.captureName(ruleCode);
                            String beRuleCoded = captureName.substring(captureName.length()-1).
                                    equals("e") ? "be" + captureName + "d" : "be" + captureName + "ed";


                            integralRecordService.addIntegral(docId, docAuthorId, beRuleCoded);
                            //因为从数据库中取到的值为负数，所以不需要加负号
                            if (integral > 0){
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
            if(!msg.equals("")){
                map.put("msg",msg);
                if(integral>0){
                    map.put("integral","+"+integral);
                }else{
                    map.put("integral",integral+"");
                }
                response.setSuccess(true);
                response.setData(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setData(false);
            response.setMsg(e.getMessage());
        }
        response.setBusinessID(businessID);
        return response;
    }
}
