package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.answer.dao.QaInviteAnswerMapper;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.QaInviteAnswerService;
import com.jxdinfo.doc.question.model.QaInviteAnswer;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 邀我回答service实现层
 * @author cxk
 * @since 2021-05-14
 */
@Service
public class QaInviteAnswerServiceImpl extends ServiceImpl<QaInviteAnswerMapper,QaInviteAnswer> implements QaInviteAnswerService {

    @Autowired
    private QaInviteAnswerMapper mapper;


    /**
     * 评论时处理数据
     * @param queId 问题ID
     * @param ansId ansId
     */
    @Override
    public void addAnswerProcessData(String queId, String ansId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String userId = ShiroKit.getUser().getId();
        // 判断当前登陆人是否被邀请
        QaInviteAnswer qaInviteAnswer = mapper.getQaInviteAnswer(userId, queId);
        if(qaInviteAnswer != null){
            // 获取问题下当前登陆人回答的评论
            List<QaQuestionAnswer> answerList = mapper.getQaAnswer(userId,queId);
            // 没有评论 或者是只有新增的这条评论
            if(answerList.size() == 0 || (answerList.size() ==1 && answerList.get(0).getAnsId().equals(ansId))){
                // 获取问题新增时间
                String queTime = mapper.getQuestionTime(queId);
                String tomorrow = getTomorrowDate(queTime); // 截止时间
                String nowTime = sdf.format(new Date());
                String timelyFlag = "1"; // 不及时
                if(tomorrow.compareTo(nowTime) >= 0){
                    timelyFlag = "2"; //及时
                }
                qaInviteAnswer.setAnswerFlag("1");// 回答
                qaInviteAnswer.setTimelyFlag(timelyFlag);
                qaInviteAnswer.updateById();
            }
        }
    }

    /**
     *  删除评论时处理数据
     * @param ansId ansId
     */
    @Override
    public void delAnswerProcessData(String ansId) {

        // 逻辑删的评论
        QaQuestionAnswer qaQuestionAnswer = mapper.getQaAnswerById(ansId);
        String queId = qaQuestionAnswer.getQueId(); // 问题ID
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String userId = ShiroKit.getUser().getId();
        // 判断当前登陆人是否被邀请
        QaInviteAnswer qaInviteAnswer = mapper.getQaInviteAnswer(userId, queId);
        if(qaInviteAnswer != null){
            // 获取问题下当前登陆人回答的评论 有效的
            List<QaQuestionAnswer> answerList = mapper.getQaAnswer(userId,queId);
            if(answerList.size() == 0){
                qaInviteAnswer.setAnswerFlag("0");// 没回答
                qaInviteAnswer.updateById();
            } else {
                QaQuestionAnswer first = answerList.get(0);
                String timeData = sdf.format(first.getAnsTime()); // 最早的回答时间
                String delTime = sdf.format(qaQuestionAnswer.getAnsTime()); // 删除的回答的回答时间
                String queTime = mapper.getQuestionTime(queId);
                String tomorrow = getTomorrowDate(queTime); // 问题超时时间
                String timelyFlag = "1"; // 不及时
                if(delTime.compareTo(timeData) < 0){ // 删除 回答的时间 比最早的还早
                    if(tomorrow.compareTo(timeData) >= 0 ){
                        timelyFlag = "2"; //及时
                    }
                    qaInviteAnswer.setTimelyFlag(timelyFlag);
                    qaInviteAnswer.updateById();
                }
            }
        }
    }

    @Override
    public QaQuestionAnswer getQaAnswerById(String ansId) {
        return mapper.getQaAnswerById(ansId);
    }

    /**
     * 获取指定时间的第二天 后台
     * @return tomorrow
     */
    public String getTomorrowDate(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 2);
        String tomorrow = sdf.format(cal.getTime());
        return tomorrow;
    }

}
