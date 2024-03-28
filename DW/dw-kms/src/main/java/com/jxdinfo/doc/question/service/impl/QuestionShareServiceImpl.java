package com.jxdinfo.doc.question.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.manager.docmanager.model.DocResourceLog;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.question.dao.QuestionShareMapper;
import com.jxdinfo.doc.question.model.QaShareInfo;
import com.jxdinfo.doc.question.service.QuestionShareService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class QuestionShareServiceImpl  extends ServiceImpl<QuestionShareMapper,QaShareInfo> implements QuestionShareService {

    @Resource
    private DocInfoService docInfoService;
    @Resource
    private CacheToolService cacheToolService;


    @Resource
    private QuestionShareMapper questionShareMapper;

    /**
     * @author yjs
     *
     * @date 2021-3-1
     * @description 新增分享问题
     */

    @Override
    public Map newShareResource(String questionId, Integer validTime, HttpServletRequest request) {
        //判断文件是否可分享
        Map result = new HashMap();
            String href = "";
            Map map = new HashMap();
            try{
            //对文件后缀名进行处理

                    href = "/sharefile/toShowQuestion?id="+questionId+"&fileType=0&keyWords=";

            //对文档原始地址进行加密
            String hash = StringUtil.applySha256(href + System.currentTimeMillis());
            hash = hash.substring(0,23);
            String pwd = "";
            //如果没有设置有效期，默认永久
            if (validTime == 0) {
                validTime = 365*100;
            }
            //生成分享资源的信息
            map.put("shareId", StringUtil.getUUID());
            map.put("href", href);
                map.put("creatorName", ShiroKit.getUser().getName());
            map.put("hash", hash);
            map.put("questionId",questionId);
            map.put("creatorId", ShiroKit.getUser().getId());
            map.put("pwd", pwd);
            map.put("validTime",validTime);
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        String hostAddress = address.getHostAddress();
            //将分享资源插入数据库
            int isShare = questionShareMapper.newShareResource(map);
            String mappingUrl = "";
            //读取缓存的服务器地址
            Map serverAddress = cacheToolService.getServerAddress();
            if (serverAddress == null ||serverAddress.get("addressValid") == null || "0".equals(serverAddress.get("addressValid").toString())){
                mappingUrl = "http://" + request.getLocalAddr() +  ":" + request.getLocalPort() + "/q/" + hash;
            } else {
                mappingUrl = "http://" + serverAddress.get("address").toString() + "/q/" + hash;
            }

            if (isShare == 1){//分享资源生成成功，返回分享资源的信息
                List<DocResourceLog> resInfoList = new ArrayList<DocResourceLog>();
                DocResourceLog docResourceLog = new DocResourceLog();
                String id = UUID.randomUUID().toString().replace("-", "");
                docResourceLog.setId(id);
                docResourceLog.setResourceId(questionId);
                Timestamp ts = new Timestamp(System.currentTimeMillis());
                docResourceLog.setOperateTime(ts);
                docResourceLog.setResourceType(3);
                String userId = UserInfoUtil.getUserInfo().get("ID").toString();
                docResourceLog.setUserId(userId);
                docResourceLog.setOperateType(20);
                docResourceLog.setValidFlag("1");
                docResourceLog.setAddressIp(HttpKit.getIp());
                resInfoList.add(docResourceLog);
                docInfoService.insertResourceLog(resInfoList);//添加分享记录

                result.put("mapping_url",mappingUrl);
                result.put("valid_time",validTime);
                result.put("msg","将链接发送给小伙伴");
                result.put("status",1);

            } else {
                result.put("msg","分享失败");
                result.put("status",-1);
            }
            return result;
        } catch (Exception e){
            result.put("msg","分享失败");
            result.put("status",-1);
            return result;
        }
    }

    @Override
    public Map getShareResource(String hash) {
        return questionShareMapper.getShareResource(hash);
    }
}
