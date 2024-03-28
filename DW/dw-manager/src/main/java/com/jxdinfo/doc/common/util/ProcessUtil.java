package com.jxdinfo.doc.common.util;

import com.alibaba.fastjson.JSONArray;
import com.jxdinfo.hussar.bpm.engine.model.Result;
import com.jxdinfo.hussar.bpm.engine.service.DefinitionEngineService;
import com.jxdinfo.hussar.bpm.engine.service.InstanceEngineService;
import com.jxdinfo.hussar.bpm.engine.service.TaskEngineService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessUtil {

    @Autowired
    private DefinitionEngineService definitionEngineService;

    @Autowired
    private InstanceEngineService instanceEngineService;

    @Autowired
    private TaskEngineService taskEngineService;

    @Value("${fileAudit.processDefinitionKey}")
    private String processDefinitionKey;


    /**
     * 启动流程实例
     *
     * @param docId  文档ID
     * @param userId 用户ID
     * @return 任务ID
     */
    public Map startProcess(String flowName, String docId, String userId) {
        Map<String, String> resultMap = new HashMap<String, String>();

        Result bpmResult = instanceEngineService.startProcessInstanceByKey(processDefinitionKey, userId, docId, null);
        String msg = bpmResult.getMsg();

        if ("success".equals(msg)) {
            JSONArray jsonArray2 = bpmResult.getResult();
            if (jsonArray2.size() > 0) {
                resultMap = (Map<String, String>) jsonArray2.get(0);
                resultMap.put("result", "true");
            }
        }
        return resultMap;
    }

    /**
     * 办理任务
     *
     * @param taskId 任务ID
     * @param userId 用户ID
     * @param map    任务参数
     * @return 任务ID
     */
    public Map<String, String> completeProcess(String taskId, String userId, Map<String, Object> map, Map<String, String> assigneeMap) {
        Map<String, String> result = new HashMap<>();
        Result bpmResult2;
        if (map == null) {
            bpmResult2 = taskEngineService.completeTask(taskId, userId);
        } else {
            if (assigneeMap == null) {
                bpmResult2 = taskEngineService.completeTask(taskId, userId, map);
            } else {
                bpmResult2 = taskEngineService.completeTask(taskId, userId, assigneeMap, map);
            }
        }
        String msg2 = bpmResult2.getMsg();
        String nextExamine_state = "0";
        if ("success".equals(msg2)) {
            JSONArray jsonArray2 = bpmResult2.getResult();
            if (jsonArray2.size() > 0) {
                result = (Map<String, String>) jsonArray2.get(0);
                String definitionKey = result.get("definitionKey");
                if (StringUtils.equals(definitionKey, "approval")) {
                    result.put("nextExamineState", "2"); // 审核状态（0:未审核;1:审核通过;2:审核中;3:审核不通过；）
                }
            } else { // 流程走完
                result.put("nextExamineState", "1"); // 审核通过
            }
            result.put("result", "true");
        }
        return result;
    }


    /**
     * 驳回流程
     *
     * @param taskId 任务id
     * @param userId 用户ID
     * @return 任务ID
     */
    public Map rejectProcess(String taskId, String userId) {
        Map<String, String> resultMap = new HashMap<String, String>();

        Result bpmResult = taskEngineService.rejectToFristTask(taskId, userId, null);
        String msg = bpmResult.getMsg();

        if ("success".equals(msg)) {
            JSONArray jsonArray2 = bpmResult.getResult();
            if (jsonArray2.size() > 0) {
                resultMap = (Map<String, String>) jsonArray2.get(0);
                resultMap.put("result", "true");
            }
        }
        return resultMap;
    }

}
