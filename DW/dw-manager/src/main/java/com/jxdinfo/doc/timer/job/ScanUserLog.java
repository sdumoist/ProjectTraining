package com.jxdinfo.doc.timer.job;

import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.historymanager.service.RelationHistoryService;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author luzhanzhao
 * @descriiption 每天定时扫描当天的用户操作记录
 * @date 2018-11-27
 */
public class ScanUserLog implements BaseJob {
    /**
     * appCtx
     */
    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
    /**
     * 用户操作记录服务
     */
    private RelationHistoryService relationHistoryService = appCtx.getBean(RelationHistoryService.class);// 这样直接调用就好;


    /**
     * 执行代码
     *
     * @param context
     * @throws JobExecutionException
     * @Title: execute
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        scan();
        System.out.println("===========定时器执行===========扫描用户操作记录");
    }

    /**
     * @author luzhanzhao
     * @date 2018-11-27
     * @description 扫描用户当天的操作记录
     */
    public void scan() {
        //查询关系表是否为空
        int relationCount = relationHistoryService.selectRelationCount();
        //定义一个标识符，默认为null
        String isEmpty = null;
        if (relationCount != 0) {
            //如果关系表不为空，则为isEmpty赋值为not，此时isEmpty不为null
            isEmpty = "not";
        }
        //获取用户操作记录表中的所有用户id
        List<Map> users = relationHistoryService.getUsers();
        //对用户id进行遍历
        for (Map user : users) {
            String userId = user.get("userId").toString();
            //根据用户id和标识符isEmpty查询用户操作记录
            List<Map> list = relationHistoryService.getUserResourceLog(userId, isEmpty);
            //对查询的记录进行遍历
            for (int i = 0; i < list.size() - 1; i++) {
                //遍历到的当前文档的id
                String currentId = list.get(i).get("docId").toString();
                //遍历到的当前文档的下一篇文档的id
                String childId = list.get(i + 1).get("docId").toString();
                //新建一个map存储该上下篇关系
                Map map = new HashMap();
                map.put("currentId", currentId);
                map.put("childId", childId);
                //查询关系表中是否存在该上下篇关系
                int count = relationHistoryService.selectCount(map);
                if (count == 0) {//如果不存在，则新建该关系，并设置默认值为1，插入关系表中
                    if (!currentId.equals(childId)) {
                        map.put("id", StringUtil.getUUID());
                        map.put("times", 1);
                        relationHistoryService.insertIntoDocRelation(map);
                    }
                } else {//如果存在，则执行更新该关系命令，默认为在当前次数上加1
                    relationHistoryService.updateDocRelation(map);
                }
            }
        }
    }
}
