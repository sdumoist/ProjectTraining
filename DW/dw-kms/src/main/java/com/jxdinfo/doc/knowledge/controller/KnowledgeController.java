package com.jxdinfo.doc.knowledge.controller;


import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.knowledge.model.KnowledgeBase;
import com.jxdinfo.doc.knowledge.service.KnowledgeService;
import com.jxdinfo.doc.question.service.QuestionService;
import com.jxdinfo.hussar.common.dicutil.DictionaryUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.support.HttpKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 知识库表控制器
 * @author cxk
 * @since 2021-05-12
 */
@Controller
@RequestMapping("/knowledge")
public class KnowledgeController {

    /**
     * 问题操作接口
     */
    @Autowired
    private QuestionService questionService;

    /**
     * 问题操作接口
     */
    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;

    /**
     * 数据字典工具类
     */
    @Autowired
    private DictionaryUtil dictionaryUtil;

    /**
     * 新增知识库
     * @param paramMap 参数map 因为富文本可能有图片的问题 容易图片路径丢失
     * @return 新增结果
     */
    @RequestMapping("/add")
    @ResponseBody
    public String addKnowledge(@RequestParam Map<String,String> paramMap) {

        String title = paramMap.get("title");
        String label = paramMap.get("label");
        String content = paramMap.get("content");
        String text = paramMap.get("text");
        String inputType = paramMap.get("inputType");
        String queId = paramMap.get("queId");

        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        inputType = XSSUtil.xss(inputType);
        queId = XSSUtil.xss(queId);

        KnowledgeBase knowledgeBase = new KnowledgeBase();
        String knowId = UUID.randomUUID().toString().replaceAll("-", "");
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        knowledgeBase.setKnowId(knowId);
        knowledgeBase.setTitle(title);
        knowledgeBase.setState("0");
        knowledgeBase.setLabel(label);
        knowledgeBase.setInputUserId(userId);
        knowledgeBase.setInputUserName(userName);
        knowledgeBase.setInputTime(new Timestamp(new Date().getTime()));
        knowledgeBase.setReadNum(0);
        knowledgeBase.setInputType(inputType);
        knowledgeBase.setQueId(queId);
        knowledgeBase.setContent(content);
        knowledgeBase.setContentText(text);
        // 新增知识库数据
        knowledgeBase.insert();
        // 添加知识库日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("6"); // 知识库
        qaLog.setOperation("1");
        qaLog.setDataId(knowId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 存es
        DocES docES = new DocES();
        docES.setId(knowId);
        docES.setTitle(title);
        docES.setTags(label);
        docES.setContent(text);
        docES.setContentType("kn");
        docES.setOwner(userId);
        docES.setOptTs(new Timestamp(new Date().getTime()));
        docES.setFileName(title);
        String[] permissionList = new String[1];
        permissionList[0] = "allpersonflag";
        docES.setPermission(permissionList);
        docES.setRecycle("0");
        try {
            esService.createESIndex(docES);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }
        return "success";
    }

    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    @RequestMapping("/getKnowledgeList")
    @ResponseBody
    public Map getQueTableList(String title, String label, String state, String order,
                               @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        order = XSSUtil.xss(order);

        Integer startIndex = (pageNumber - 1) * pageSize;
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = knowledgeService.getKnowledgeList(title, label, state, order, startIndex, pageSize,userId);
        int count = list.size();
        list = list.stream()
                .skip(startIndex)
                .limit(pageSize)
                .collect(Collectors.toList());
        Map map = new HashMap();
        map.put("rows",list);
        map.put("count",count);
        return map;
    }

    /**
     * 删除知识库  本质是逻辑删除
     * @param knowId 知识库id
     * @return 删除结果
     */
    @RequestMapping("/delete")
    @ResponseBody
    public String deleteQuestion(String knowId) {

        // xss过滤
        knowId = XSSUtil.xss(knowId);

        KnowledgeBase knowledgeBase = knowledgeService.getById(knowId);
        knowledgeBase.setState("2");
        knowledgeBase.updateById();
        // 添加知识库日志
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("6"); // 知识库
        qaLog.setOperation("2"); // 删除
        qaLog.setDataId(knowId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 修改es
        Map<String, Object> es = new HashMap<>();
        es.put("recycle", "1");
        try {
            esService.updateIndex(knowId, es);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }
        return "success";
    }

    /**
     * 跳知识库修改页面
     * @param model
     * @param knowId 知识库id
     * @return 修改页面
     */
    @GetMapping("/knowledge_update")
    public String knowledgeupdate(Model model, String knowId) {
        // xss过滤
        knowId = XSSUtil.xss(knowId);

        KnowledgeBase knowledgeBase = knowledgeService.getById(knowId);
        // 浏览量加1
        int readNum = knowledgeBase.getReadNum();
        readNum = readNum + 1;
        knowledgeBase.setReadNum(readNum);
        knowledgeBase.updateById();
        model.addAttribute("item",knowledgeBase);
        model.addAttribute("userName",ShiroKit.getUser().getName());
        return "/doc/front/knowledge/knowledge_update.html";
    }

    /**
     * 修改知识库
     * @param paramMap 参数map 因为富文本可能有图片的问题 容易图片路径丢失
     * @return 新增结果
     */
    @RequestMapping("/edit")
    @ResponseBody
    public String editKnowledge(@RequestParam Map<String,String> paramMap) {

        String knowId = paramMap.get("knowId"); // 知识库ID
        String title = paramMap.get("title"); //标题
        String label = paramMap.get("label"); //标签
        String content = paramMap.get("content"); //内容
        String text = paramMap.get("text"); //内容纯文本

        // xss过滤
        knowId = XSSUtil.xss(knowId);
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);

        KnowledgeBase knowledgeBase = knowledgeService.getById(knowId);

        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        knowledgeBase.setTitle(title);
        knowledgeBase.setLabel(label);
        knowledgeBase.setContent(content);
        knowledgeBase.setContentText(text);
        // 修改知识库数据
        knowledgeBase.updateById();
        // 添加知识库日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("6"); // 知识库
        qaLog.setOperation("10"); // 修改
        qaLog.setDataId(knowId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 修改es
        Map<String, Object> es = new HashMap<>();
        es.put("recycle", "1");
        try {
            esService.updateIndex(knowId, es);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }
        return "success";
    }

    /**
     * 跳知识库修改页面
     * @param model
     * @param knowId 知识库id
     * @return 修改页面
     */
    @GetMapping("/knowledge_detail")
    public String knowledgeDetail(Model model, String knowId) {

        // xss过滤
        knowId = XSSUtil.xss(knowId);

        KnowledgeBase knowledgeBase = knowledgeService.getById(knowId);
        // 浏览量加1
        int readNum = knowledgeBase.getReadNum();
        readNum = readNum + 1;
        knowledgeBase.setReadNum(readNum);
        knowledgeBase.updateById();
        model.addAttribute("item",knowledgeBase);
        model.addAttribute("userName",ShiroKit.getUser().getName());
        return "/doc/front/knowledge/knowledge_detail.html";
    }
}
