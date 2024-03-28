package com.jxdinfo.doc.answer.dao;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.answer.model.QaQuestionAnswer;
import org.apache.ibatis.annotations.Param;

/***
 *  问答接口外自定义mapper
 * @author cxk
 * @since 2021-05-07
 */
public interface RoutePathMapper extends BaseMapper<QaQuestionAnswer> {

    /**
     * 获取问题有效回答的个数
     * @param queId 问题ID
     * @return 有效回答的个数
     */
    int getTotleNumAnswers(@Param("queId") String queId);
}
