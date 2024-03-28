package com.jxdinfo.doc.mobile.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.manager.docintegral.service.IntegralRecordService;
import com.jxdinfo.doc.mobile.model.Response;
import com.jxdinfo.doc.mobile.util.ConvertUtil;
import com.jxdinfo.hussar.bsp.permit.model.SysUsers;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jxdinfo.doc.mobile.constants.ApiConstants.PERSONAL_INTEGRAL_RECORD;

/**
 * 获取积分明细
 */
@Component
public class ApiPersonalIntegralServiceImpl extends ApiBaseServiceImpl {


    private static final String businessID = PERSONAL_INTEGRAL_RECORD;

    /**
     * 积分 服务
     */
    @Resource
    private IntegralRecordService integralRecordService;

    /**
     * 用户管理服务 接口
     */
    @Resource
    private ISysUsersService iSysUsersService;

    /**
     * 目录服务类
     */
    @Resource
    private BusinessService businessService;
    @Override
    public String getBusinessID() {
        return businessID;
    }

    /**
     * @param params
     * //@param ruleCodes     操作类型（多个以逗号分隔）
     * //@param pageNumber    当前页
     * //@param pageSize      页面条数
     * @return Response
     * @description: 获取积分明细
     * @Title: execute
     * @author:zhongguangrui
    */
    @Override
    public Response execute(HashMap<String,String> params) {
        Response response = new Response();
        try {
            Integer pageNumber =Integer.parseInt(String.valueOf(params.get("pageNum")));
            Integer pageSize = Integer.parseInt(String.valueOf(params.get("pageSize")));
            String ruleCodes = params.get("ruleCodes");
            String userId = params.get("userId");
            String[] ruleCodesArray = null;
            if (ruleCodes != null && !"".equals(ruleCodes)){
                ruleCodesArray = ruleCodes.split(",");
            }
            List<Map<String,Object>> list = integralRecordService.getIntegralHistories(userId,"1",ruleCodesArray);
            int count = list.size();
            // 将积分统计情况一并返回
            Map<String,Integer> integrals = new HashMap<>();
            integrals.put("total",integralRecordService.showIntegral(userId));
            integrals.put("upload",integralRecordService.getIntegralByType(userId,new String[]{"upload"}));
            integrals.put("beDownloaded",integralRecordService.getIntegralByType(userId,new String[]{"beDownloaded"}));
            integrals.put("rewards",integralRecordService.getIntegralByType(userId,new String[]{"login","search","defaultBonus","preview","share"}));
            integrals.put("download",integralRecordService.getIntegralByType(userId,new String[]{"download"}));
            integrals.put("bePreviewed",integralRecordService.getIntegralByType(userId,new String[]{"bePreviewed"}));
            integrals.put("beShared",integralRecordService.getIntegralByType(userId,new String[]{"beShared"}));
            Integer rankTry = integralRecordService.getRankNum(userId);
            double total= (double)iSysUsersService.count(new QueryWrapper<SysUsers>().ne("ACCOUNT_STATUS","2"));
            double rank = total;
            if (rankTry != null){
                rank = (double) rankTry;
            }
            double present =100-(rank/total*100) ;
            int presentint =(int) present;
            for(int i=0;i<list.size();i++){
                list.get(i).put("operate_time",ConvertUtil.changeTime((Timestamp) list.get(i).get("operate_time")));
                if(list.get(i).get("integral")!=null && !"".equals(list.get(i).get("integral"))){
                    if(Integer.parseInt(list.get(i).get("integral").toString()) > 0){
                        list.get(i).put("integral","+"+list.get(i).get("integral"));
                    }else if(Integer.parseInt(list.get(i).get("integral").toString()) < 0){
                        list.get(i).put("integral",""+list.get(i).get("integral"));
                    }
                }
            }
            Map histories = new HashMap();
            histories.put("rows",list.stream()
                    .skip((pageNumber - 1) * pageSize)
                    .limit(pageSize)
                    .collect(Collectors.toList()));
            histories.put("pageCount",count);
            histories.put("pageNum",pageNumber);
            histories.put("pageSize",pageSize);
            histories.put("integrals",integrals);
            histories.put("present",presentint+"%");
            response.setSuccess(true);
            response.setData(histories);
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
