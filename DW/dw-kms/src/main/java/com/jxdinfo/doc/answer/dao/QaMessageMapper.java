package com.jxdinfo.doc.answer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.answer.model.QaMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 消息提醒mapper
 * @author sjw
 * @since 2021-02-22
 */
public interface QaMessageMapper extends BaseMapper<QaMessage> {

    /**
     * 首页消息提醒查询
     * @return  查询结果
     */
    List<Map<String, Object>> getMessageList(@Param("userId") String userId);

    /**
     * 未读消息数查询
     * @return  查询结果
     */
    int getMessageListCount(@Param("userId") String userId);

    /**
     * 消息设置为已读
     * @return  更新结果
     */
    int setRead(@Param("userId") String userId);
}
