package com.jxdinfo.doc.answer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.answer.dao.RoutePathMapper;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import com.jxdinfo.doc.answer.service.RoutePathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/***
 *  问答接口外自定义service层实现类
 * @author cxk
 * @since 2021-05-07
 */
@Service
public class RoutePathServiceImpl  extends ServiceImpl<RoutePathMapper,QaQuestionAnswer> implements RoutePathService {

    @Autowired
    private RoutePathMapper routePathMapper;


    /**
     * 获取问题有效回答的个数
     * @param queId 问题ID
     * @return 有效回答的个数
     */
    @Override
    public int getTotleNumAnswers(String queId) {
        return routePathMapper.getTotleNumAnswers(queId);
    }
}
