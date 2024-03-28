package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.constant.QuestionConstant;
import com.jxdinfo.doc.answer.dao.CommentMapper;
import com.jxdinfo.doc.answer.model.QaCommentReply;
import com.jxdinfo.doc.answer.model.QaLog;
import com.jxdinfo.doc.answer.service.CommentService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.question.dao.QaAgreeMapper;
import com.jxdinfo.doc.question.model.QaAgree;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.jxdinfo.hussar.core.support.HttpKit;
import dm.jdbc.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 评论回复service层实现类
 * @author sjw
 * @since 2021-02-23
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper,QaCommentReply> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QaAgreeMapper agreeMapper;

    /**
     * 回复评论数据查询
     * @return  查询结果
     */
    @Override
    public List<Map<String, Object>> getCommentDetail(String userId, String byReplyId) {
        // 获取当前登录人
        ShiroUser shiroUser = ShiroKit.getUser();
        // 获取当前登录人角色集合
        List<String> rolesList = shiroUser.getRolesList();
        // 判断是不是文库超级管理员
        Boolean adminFlag = CommonUtil.getAdminFlag(rolesList) == 1;
        List<Map<String, Object>> result = commentMapper.getCommentDetail(userId, byReplyId);
        if(adminFlag){
            for(Map<String, Object> item:result){
                item.put("isDlete","1");
            }
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAgreeState(String commentPeplyId, String agreeState) {

        // 获取原有点赞数
        QaCommentReply qaCommentReply = commentMapper.selectById(commentPeplyId);
        int agreeNum = qaCommentReply.getAgreeNum();

        // 点赞数
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            agreeNum--;
        } else {
            agreeNum++;
        }
        qaCommentReply.setAgreeNum(agreeNum);
        qaCommentReply.updateById();

        // 点赞表数据
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            agreeMapper.delete(new QueryWrapper<QaAgree>().eq("ANS_ID", commentPeplyId).eq("USER_ID", ShiroKit.getUser().getId()));
        } else {
            QaAgree qaAgree = new QaAgree();
            qaAgree.setAgreeId(UUID.randomUUID().toString().replaceAll("-", ""));
            qaAgree.setAnsId(commentPeplyId);
            qaAgree.setUserId(ShiroKit.getUser().getId());
            qaAgree.insert();
        }

        // 添加日志
        QaLog qaLog = new QaLog();
        qaLog.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        qaLog.setType(QuestionConstant.QALOG_TYPE_COMMENT);
        if (StringUtil.equals(agreeState, QuestionConstant.QA_AGREE)) {
            qaLog.setOperation(QuestionConstant.QALOG_OPERATION_CANCEL_AGREE);
        } else {
            qaLog.setOperation(QuestionConstant.QALOG_OPERATION_AGREE);
        }
        qaLog.setDataId(commentPeplyId);
        qaLog.setUserId(ShiroKit.getUser().getId());
        qaLog.setUserName(ShiroKit.getUser().getName());
        qaLog.setTime(new Timestamp(new Date().getTime()));
        qaLog.setState(QuestionConstant.VALID_FLAG_NORMAL);
        qaLog.setIp(HttpKit.getIp());
        qaLog.insert();
    }
}
