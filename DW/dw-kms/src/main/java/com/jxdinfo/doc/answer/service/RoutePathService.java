package com.jxdinfo.doc.answer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;

/***
 *  问答接口外自定义service层
  * @author cxk
  * @since 2021-05-07
 */
public interface RoutePathService extends IService<QaQuestionAnswer> {

    /**
     * 获取问题有效回答的个数
     * @param queId 问题ID
     * @return 有效回答的个数
     */
    int getTotleNumAnswers(String queId);
}
