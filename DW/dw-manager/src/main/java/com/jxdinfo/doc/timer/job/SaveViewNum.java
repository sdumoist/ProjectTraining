package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.topicmanager.service.SpecialTopicService;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author xb
 * @Description:
 * @Date: 2018/7/10 10:00
 */
public class SaveViewNum implements BaseJob {


    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    /**
     * hussarCacheManager
     */
    private HussarCacheManager hussarCacheManager = appCtx.getBean(HussarCacheManager.class);// 这样直接调用就好

    /**
     * 文档信息
     */
    private DocInfoService docInfoService = appCtx.getBean(DocInfoService.class);// 这样直接调用就好;

    /**
     * 专题维护
     */
    private SpecialTopicService specialTopicService = appCtx.getBean(SpecialTopicService.class);// 这样直接调用就好;

    /**
     * 执行代码
     *
     * @param context
     * @throws JobExecutionException
     * @Title: execute
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        saveTopicViewNum();
        saveDocViewNum();
        System.out.println("===========定时器执行===========" + CacheConstant.TOPIC_VIEW_NUM_CACHENAME);
    }


    public void saveTopicViewNum() {
        //获取所有缓存中的数据
        List<Object> cacheList = hussarCacheManager.getKeys(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
        															CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME);
        
        if (cacheList != null && cacheList.size() > 0) {
            for (Object obj : cacheList) {            	
                String topicId = String.valueOf(obj).replace(CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME , "");
                
                //Integer num = StringUtil.getInteger(mapObj.get("value"));
                Integer num = StringUtil.getInteger(hussarCacheManager.getObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
                																		CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId));
                if (num > 0){
	                int k = specialTopicService.updateViewNum(topicId, num);
	                if (k == 1){
	                //执行成功后，将缓存中数据清除
	                	hussarCacheManager.delete(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
	                									CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId);
	                }
                }
            }
        }
    }
    
    public void saveDocViewNum() {
        //获取所有缓存中的数据
        List<Object> cacheList = hussarCacheManager.getKeys(CacheConstant.DOC_VIEW_NUM_CACHENAME,
        														CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME);
        
        if (cacheList != null && cacheList.size() > 0) {
            for (Object obj : cacheList) {            	
                String docId = String.valueOf(obj).replace(CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME , "");;
                
                //Integer num = StringUtil.getInteger(mapObj.get("value"));
                Integer num = StringUtil.getInteger(hussarCacheManager.getObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
                																	CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId));
                if (num > 0){
                	 int k = docInfoService.updateDocViewNum(docId,num);
                     if (k == 1){
                     //执行成功后，将缓存中数据清除
                     	hussarCacheManager.delete(CacheConstant.DOC_VIEW_NUM_CACHENAME, CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId);
                     }
                }
               
            }
        }
    }
}
