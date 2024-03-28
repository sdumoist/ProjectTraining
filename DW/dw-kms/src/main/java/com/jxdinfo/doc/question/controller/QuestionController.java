package com.jxdinfo.doc.question.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.model.QaMessage;
import com.jxdinfo.doc.answer.service.AnswerService;
import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.XSSUtil;
import com.jxdinfo.doc.knowledge.model.KnowledgeBase;
import com.jxdinfo.doc.knowledge.service.KnowledgeService;
import com.jxdinfo.doc.manager.professional.service.IProfessionalService;
import com.jxdinfo.doc.manager.resourceLog.service.docResourceLogService;
import com.jxdinfo.doc.question.model.QaInviteAnswer;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.model.QaQuestionSupplement;
import com.jxdinfo.doc.question.model.QaText;
import com.jxdinfo.doc.question.service.QaContinueQaService;
import com.jxdinfo.doc.question.service.QuestionService;
import com.jxdinfo.doc.question.service.SupplementService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
 * 知识问答问题控制层
 * @author sjw
 * @since 2021-02-24
 */
@Controller
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QaContinueQaService qaContinueQaService;

    @Autowired
    private SupplementService supplementService;

    @Autowired
    private docResourceLogService docResourceLogService;

    @Autowired
    private IProfessionalService professionalService;

    @Autowired
    private KnowledgeService knowledgeService;

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;

    /**
     * 新增问题
     * title问题标题
     * supplement问题补充
     * text问题补充纯文本
     * rewardPoinits悬赏金额
     * label标签
     * answerFlag可回复者
     * majorId 分类id
     * majorName 分类名称
     * @return  新增结果
     */
    @RequestMapping("/add")
    @ResponseBody
    public String addQuestion(String title, String supplement, String text, String rewardPoinits, String label, String answerFlag, String majorId, String majorName) {
        // xss过滤
        title = XSSUtil.xss(title);
        supplement = XSSUtil.xss(supplement);
        rewardPoinits = XSSUtil.xss(rewardPoinits);
        label = XSSUtil.xss(label);
        answerFlag = XSSUtil.xss(answerFlag);
        majorId = XSSUtil.xss(majorId);
        majorName = XSSUtil.xss(majorName);

        QaQuestion qaQuestion = new QaQuestion();
        String queId = UUID.randomUUID().toString().replaceAll("-", "");
        String userId = ShiroKit.getUser().getId();
        String userName = ShiroKit.getUser().getName();
        qaQuestion.setQueId(queId);
        qaQuestion.setTitle(title);
        qaQuestion.setSupplement(supplement);
        qaQuestion.setState("0");
        qaQuestion.setRewardPoinits(Integer.parseInt(rewardPoinits));
        qaQuestion.setLabel(label);
        qaQuestion.setAnswerFlag(answerFlag);
        qaQuestion.setQueUserId(userId);
        qaQuestion.setQueUserName(userName);
        qaQuestion.setQueTime(new Timestamp(new Date().getTime()));
        qaQuestion.setReadNum(0);
        qaQuestion.setMajorId(majorId);
        qaQuestion.setMajorName(majorName);
        // 新增问题数据
        qaQuestion.insert();
        // 新增 问题文本数据
        QaText qaText = new QaText();
        qaText.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaText.setType("1"); // 问题
        qaText.setQaId(queId);
        qaText.setContent(text);
        qaText.insert();
        // 添加邀我回答表
        // 根据专业ID 获取被邀请人
        if(majorId != null && StringUtils.equals(answerFlag,"2") ){
            Map<String, String> majorInfo = professionalService.getProfessionalByMojorId(majorId);
            QaInviteAnswer qaInviteAnswer = new QaInviteAnswer();
            qaInviteAnswer.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            qaInviteAnswer.setQueId(queId);
            qaInviteAnswer.setInviteUserId(userId);
            qaInviteAnswer.setInviteUserName(userName);
            qaInviteAnswer.setInviteTime(new Timestamp(new Date().getTime()));
            qaInviteAnswer.setByInviteUserId(majorInfo.get("userId"));
            qaInviteAnswer.setByInviteUserName(majorInfo.get("userName"));
            qaInviteAnswer.setAnswerFlag("0"); // 是否回答 0 是否
            qaInviteAnswer.setTimelyFlag("0"); // 是否及时
            qaInviteAnswer.insert();
            // 新增消息
            QaMessage qaMessage = new QaMessage();
            qaMessage.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            qaMessage.setType("3");
            qaMessage.setContent(text);
            qaMessage.setDataId(queId);
            qaMessage.setMessageTime(new Timestamp(new Date().getTime()));
            qaMessage.setState("0");
            // 获取问题的提问人
            qaMessage.setUserId(qaQuestion.getQueUserId());
            qaMessage.insert();
        }
        // 添加问答日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("1");
        qaLog.setOperation("1");
        qaLog.setDataId(queId);
        qaLog.setUserId(userId);
        qaLog.setUserName(userName);
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0");
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 存es
        DocES docES = new DocES();
        docES.setId(queId);
        docES.setTitle(title);
        docES.setTags(label);
        docES.setContent(text);
        docES.setContentType("qa");
        docES.setOwner(userId);
        docES.setOptTs(new Timestamp(new Date().getTime()));
        docES.setFileName(title);
        String[] permissionList = new String[1];
        permissionList[0] = "allpersonflag";
        docES.setPermission(permissionList);
        docES.setRecycle("0");
        /*try {
            esService.createESIndex(docES);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }*/
        return "success";
    }

    /**
     * 结束问题
     * @return  修改结果
     */
    @RequestMapping("/endQuestion")
    @ResponseBody
    public String endQuestion(String queId) {

        // xss过滤
        queId = XSSUtil.xss(queId);

        // 获取原数据
        QaQuestion qaQuestion = questionService.getById(queId);
        qaQuestion.setState("2");  // 状态，0待回答，1已解决，2已结束，3已删除
        qaQuestion.updateById();
        // 添加问答日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType("1"); //类型，1问题，2回答，3评论回复，4追问，5追答
        qaLog.setOperation("11"); // 操作，1新增，2删除，3查看，4点赞，5关注，6取消关注，7分享，8取消分享，9补充，10修改，11结束，12设为最佳，13附件下载
        qaLog.setDataId(queId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState("0"); // 状态，0正常，1删除
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
        // 修改es
        /*try {
            Map<String, Object> es = new HashMap<>();
            es.put("recycle", "1");
            esService.updateIndex(queId, es);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }*/
        return "success";
    }


    /**
     * 问题详情
     * @return  查询结果
     */
    @RequestMapping("/getQuestionDetail")
    @ResponseBody
    public Map<String, Object> getQuestionDetail(String queId, String onAll) {

        // xss过滤
        queId = XSSUtil.xss(queId);
        onAll = XSSUtil.xss(onAll);

        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;

        String userId = ShiroKit.getUser().getId();
        Map<String, Object> queDetail = new HashMap<>();
        boolean ishave = false;
        // 获取问题详细信息
        queDetail = questionService.getQuestionDetail(userId, queId);
        //获取是否已经加入知识库
        List<KnowledgeBase> knowledgeBaseList = knowledgeService.list(new QueryWrapper<KnowledgeBase>().eq("QUE_ID",queId).ne("STATE","2"));
        // 获取问题回答
        List<Map<String, Object>> ansInfo = answerService.getAnswerToQuestion(userId, queId, onAll);
        // 获取追问追答
        for (int i = 0; i < ansInfo.size(); i++) {
            List<Map<String, Object>> continueQa = qaContinueQaService.getContinutQaByAnswer(ansInfo.get(i).get("ansId").toString());
            ansInfo.get(i).put("continueQa", continueQa);
            // 判断页面显示追问还是追答  题主和超管--追问  答主显--追答  其他人不显示
            if(ToolUtil.equals(userId,queDetail.get("userId").toString()) || adminFlag){
                ansInfo.get(i).put("QuestionOrAnswer", "question");
            } else if(ToolUtil.equals(userId,ansInfo.get(i).get("userId").toString())){
                ansInfo.get(i).put("QuestionOrAnswer", "answer");
            } else {
                ansInfo.get(i).put("QuestionOrAnswer", "notshow");
            }
            // 判断是否有最佳答案
            if(ToolUtil.equals("1",ansInfo.get(i).get("bestAnswer").toString())){
                ishave = true;
            }
            if(knowledgeBaseList.size() > 0){
                ansInfo.get(i).put("intoKnowledge", "0");
            } else {
                ansInfo.get(i).put("intoKnowledge", "1");
            }
            if(ishave){ // 已经有最佳答案
                ansInfo.get(i).put("haveTheBest", "1");
            } else {
                ansInfo.get(i).put("haveTheBest", "0");
            }
        }
        if (queDetail != null) {
            queDetail.put("answer", ansInfo);
        }
        return queDetail;
    }

    /**
     * 问题删除
     * @return  删除结果
     */
    @RequestMapping("/delete")
    @ResponseBody
    public String deleteQuestion(String queId) {
        // xss过滤
        queId = XSSUtil.xss(queId);

        supplementService.remove(new QueryWrapper<QaQuestionSupplement>().eq("QUE_ID", queId));
        // 删除问题、回答、追问追答、评论回复
        questionService.delQuestion(queId);
        // 修改es
        Map<String, Object> es = new HashMap<>();
        es.put("recycle", "1");
        /*try {
            esService.updateIndex(queId, es);
        } catch (Exception e) {
            e.printStackTrace();
            return "esError";
        }*/
        return "success";
    }

    /**
     * 首页知识问答查询
     * @return  查询结果
     */
    @RequestMapping("/getQueListByFirstPage")
    @ResponseBody
    public List<Map<String, Object>> getQueListByFirstPage() {
        return questionService.getQueListByFirstPage();
    }

    /**
     * 相关问题数据查询
     * @return  查询结果
     */
    @RequestMapping("/getRelevantQuestion")
    @ResponseBody
    public List<Map<String, Object>> getRelevantQuestion(String queId) {
        // xss过滤
        queId = XSSUtil.xss(queId);
        // 获取label
        QaQuestion qaQuestion = questionService.getById(queId);
        String label = qaQuestion.getLabel();
        return questionService.getRelevantQuestion(label, queId);
    }

    /**
     * 知识问答列表页数据查询
     * @return  查询结果
     */
    @RequestMapping("/getQueTableList")
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
        List<Map<String, Object>> list = questionService.getQueTableList(title, label, state, order, startIndex, pageSize,userId);
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
     * 我的提问数据查询
     * @return  查询结果
     */
    @RequestMapping("/getMyQuestionList")
    @ResponseBody
    public Map getMyQuestionList(String title, String label, String state, String order,
                                                     @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                                     @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        order = XSSUtil.xss(order);

        Integer startIndex = (pageNumber - 1) * pageSize;
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = questionService.getMyQuestionList(title, label, state, order, startIndex, pageSize, userId);
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
     * 我的关注数据查询
     * @return  查询结果
     */
    @RequestMapping("/getMyFollowQuestionList")
    @ResponseBody
    public Map getMyFollowQuestionList(String title, String label, String state, String order,
                                                       @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                                       @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        order = XSSUtil.xss(order);

        Integer startIndex = (pageNumber - 1) * pageSize;
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = questionService.getMyFollowQuestionList(title, label, state, order, startIndex, pageSize, userId);
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
     * 我的回答数据查询
     * @return  查询结果
     */
    @RequestMapping("/getMyAnswerList")
    @ResponseBody
    public Map getMyAnswerList(String title, String label, String state, String order,
                               @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {

        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        order = XSSUtil.xss(order);

        Integer startIndex = (pageNumber - 1) * pageSize;
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = questionService.getMyAnswerList(title, label, state, order, startIndex, pageSize, userId);
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
     * 邀我回答数据查询
     * @return  查询结果
     */
    @RequestMapping("/getInviteMeAnswerList")
    @ResponseBody
    public Map getInviteMeAnswerList(String title, String label, String state, String order,
                               @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        // xss过滤
        title = XSSUtil.xss(title);
        label = XSSUtil.xss(label);
        order = XSSUtil.xss(order);

        Integer startIndex = (pageNumber - 1) * pageSize;
        String userId = ShiroKit.getUser().getId();
        List<Map<String, Object>> list = questionService.getInviteMeAnswerList(title, label, state, order, startIndex, pageSize, userId);
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

    @RequestMapping("/addReadNum")
    @ResponseBody
    public String addReadNum(String queId) {

        // xss过滤
        queId = XSSUtil.xss(queId);

        QaQuestion qaQuestion = questionService.getById(queId);
        int readNum = qaQuestion.getReadNum();
        readNum = readNum + 1;
        qaQuestion.setReadNum(readNum);
        qaQuestion.updateById();
        return "success";
    }

    @RequestMapping("/getThisMonthCount")
    @ResponseBody
    public Map<String, Object> getThisMonthCount(String queId) {
        Map<String, Object> result = new HashMap<>();
        result.put("upload", 0);
        result.put("download", 0);
        result.put("preview", 0);
        List<Map<String, Object>> list = docResourceLogService.getThisMonthCount();
        for (Map<String, Object> map:list) {
            if ("0".equals(map.get("type").toString())) {
                result.put("upload", map.get("num"));
            } else if ("3".equals(map.get("type").toString())) {
                result.put("preview", map.get("num"));
            } else if ("4".equals(map.get("type").toString())) {
                result.put("download", map.get("num"));
            }
        }
        return result;
    }
}
