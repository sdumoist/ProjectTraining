package com.jxdinfo.doc.manager.docintegral.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docintegral.model.IntegralRecord;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRuleService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.hussar.core.base.controller.BaseController;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @date 2018-12-06
 * @description 积分记录相关的控制层
 */
@Controller
@RequestMapping("/integral")
public class IntegralRecordController extends BaseController {

    /**
     * PREFIX
     */
    private String prefix = "/doc/manager/integral/";

    /**
     * 积分记录服务
     */
    @Resource
    private IntegralRecordService integralRecordService;
    /**
     * 积分规则服务
     */
    @Resource
    private IntegralRuleService integralRuleService;
    /**
     * 文档信息服务类
     */
    @Resource
    private DocInfoService idocInfoService;


    /**
     * @return
     * @author luzhanzhao
     * @date 2018-12-07
     * @description 获取积分排名主页面
     */
    @RequestMapping("/rankView")
    @RequiresPermissions("integral:rankView")
    public String view() {
        return prefix + "integral_rank.html";
    }

    /**
     * @return 积分排名列表
     * @author luzhanzhao
     * @date 2018-12-07
     * @description 获取积分排名列表
     */
    @RequestMapping("/rankList")
    @ResponseBody
    public Map rankList() {
        Map map = new HashMap();
        //获取积分规则总条数
        int count = integralRecordService.getIntegralUserCount();
        //分页处理
        String page = super.getPara("page");
        int pageNum = Integer.parseInt(page);
        String size = super.getPara("limit");
        int pageSize = Integer.parseInt(size);
        int startIndex = (pageNum - 1) * pageSize;
        List<Map<String, Object>> list = integralRecordService.getIntegralRank(startIndex, pageSize);
        //返回前台的数据
        map.put("data", list);
        map.put("count", count);
        map.put("msg", "success");
        map.put("code", 0);
        return map;
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
        String userId = ShiroKit.getUser().getId();
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

    /**
     * @param ids      需要打包下载的文档ids
     * @param ruleCode 规则编码
     * @return 积分状态
     * @author luzhanzhao
     */
    @RequestMapping("/integralForMulDownLoad")
    @ResponseBody
    public Map integralForMulDownLoad(String[] ids, String ruleCode) {
        Map result = new HashMap();
        //获取当前登录用户
        String userId = ShiroKit.getUser().getId();
        //获取批量下载的文档中属于当前登录用户的文档数量
        Integer myDocs = integralRecordService.selectDocCountByUser(ids, userId);
        //获取待下载文档中登录用户下载过的文档数
        int downloadedNum = integralRecordService.selectDownloadedCount(ids, userId);
        //获取当前登录用户的积分总数
        Integer integralCount = integralRecordService.showIntegral(userId);
        //定义一个状态位（-1：不允许下载，1：允许下载，并提示扣分，2：允许下载，不提示扣分）
        String status = "";
        Integer ruleIntegral = 0;
        Integer maxTimes = 0;
        //获取下载一个文档所需的积分

        if (userId.equals("superadmin") || userId.equals("wkadmin")) {
            status = "2";
            result.put("status", status);
            return result;
        }
        if (!"".equals(ruleCode) && ruleCode != null) {
            //根据用户操作编码去规则表查找积分
            List<Map<String, Object>> list = integralRuleService.getRuleByCode(ruleCode);
            //没有规则记录允许直接下载
            if (list == null || list.size() == 0) {
                status = "2";
                result.put("status", status);
                return result;
            }
            Map<String, Object> map = list.get(0);
            //无效的规则直接允许直接下载
            if (map.get("valid").equals("0")) {
                status = "2";
                result.put("status", status);
                return result;
            } else {
                ruleIntegral = (Integer) map.get("ruleIntegral");
                maxTimes = (Integer) map.get("max_times");
            }
        }
        //获取下载需要消耗的积分数量
        int amount = ids.length - (myDocs + downloadedNum);
        Integer integral = 0;
        if (amount * Math.abs(ruleIntegral) > integralCount) {//判断是否有足够的积分可以下载文档
            status = "-1";
            result.put("status", status);
            result.put("msg", "积分不足");
            result.put("integral", 0);
            return result;
        } else {
            //判断是否达到今日最大下载次数
            Integer todayTimes = integralRecordService.getIntegralTimesByToday(userId, ruleCode);
            if (maxTimes != -1 && maxTimes < todayTimes + ids.length) {
                status = "-1";
                result.put("msg", "已达上限");
                result.put("status", status);
                return result;
            }
            status = "2";
            for (String id : ids) {
                Map map = addIntegral(id, ruleCode);
                if (null != map) {
                    status = "1";
                    integral += Integer.parseInt(map.get("integral").toString());
                }
            }
        }
        result.put("status", status);
        result.put("msg", integral);
        result.put("integral", integral);
        return result;
    }

    /**
     * 获取实时积分
     *
     * @return
     */
    @RequestMapping("/getTotalIntegral")
    @ResponseBody
    public Integer getTotalIntegral() {
        return integralRecordService.showIntegral(ShiroKit.getUser().getId());
    }

    @RequestMapping("/downloadIntegral")
    @ResponseBody
    public Map downloadIntegral(String docId, String ruleCode) {
        List<Map<String,Object>>  list=  integralRuleService.getRuleByCode("download");
        Map result = new HashMap();
        Integer rank=0;
        Integer addRank =Integer.parseInt(list.get(0).get("ruleIntegral")+"") ;

        if(!list.get(0).get("valid").equals("1")){
            result.put("status", "0");
            return result;
        }
        String[] docIds = docId.split(",");
        String userId = UserInfoUtil.getUserInfo().get("ID").toString();
        String docAuthorId = "";

        boolean flag=true;
        for (int i = 0; i < docIds.length; i++) {

            String foldId = idocInfoService.getDocDetail(docIds[i]).getFoldId();
            if (!StringUtil.checkIsEmpty(docIds[i])) {
                docAuthorId = idocInfoService.getDocDetail(docIds[i]).getAuthorId();
            }
            if (userId.equals("wkadmin") || userId.equals("superadmin")) {
                result.put("status", "0");
                return result;
            } else {

                if (docAuthorId.equals(userId)) {//如果当前用户是作者，则不做处理

                }
                else if (integralRecordService.count(
                        new QueryWrapper<IntegralRecord>()
                                .eq("doc_id", docIds[i])
                                .eq("user_id", userId)
                                .eq("operate_rule_code", ruleCode)) > 0) {// 如果当前用户存在对本文档的下载记录，则不作处理

                }
                else if(integralRecordService.checkInDoc(docIds[i])>0){

                }else if(integralRecordService.checkInDoc(foldId)>0){

                }else{
                    flag=false;
                    rank=addRank+rank;
                }

            }
        }
        if (flag == true) {
            result.put("status", "0");
            return result;
        }else {
            String integral=rank+"";
            if(integral.substring(0,1).equals("-")){
                integral=integral.substring(1);
            }
            result.put("integral", integral);
            result.put("status", "1");
            return result;
        }


    }
}
