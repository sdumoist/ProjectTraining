package com.jxdinfo.doc.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.question.model.QaShareInfo;
import io.swagger.models.auth.In;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface QuestionShareService  extends IService<QaShareInfo> {


     public Map newShareResource(String questionId, Integer validTime, HttpServletRequest request);

     public Map getShareResource(String hash);
}
