package com.jxdinfo.doc.manager.topicmanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.topicmanager.model.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface MessageMapper  extends BaseMapper<Message> {

    void  insertMessageList(@Param("messageList") List<Message> messageList);

   void deleteMessage();
    List<Message> getList(@Param("name") String name, @Param("month") String month, @Param("year") String year,
                          @Param("pageNum") Integer pageNum, @Param("pageSize") Integer pageSize);
    int getListCount(@Param("name") String name, @Param("month") String month, @Param("year") String year
    );
}
