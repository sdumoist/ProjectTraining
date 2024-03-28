package com.jxdinfo.doc.manager.sharemanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.sharemanager.model.DocShare;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonalShareMapper  extends BaseMapper<DocShare> {
    /**
     * 获取个人分享记录
     * @param userId 当前登录用户id
     * @param page
     * @param name 搜索关键字
     * return 查询到得分享记录列表
     */
    List<DocShare> getMyShareHistory(@Param("userId") String userId, @Param("name") String name, @Param("order") String order,
                                     @Param("beginIndex") int beginIndex, @Param("pageSize") int pageSize, @Param("timeType") String timeType,@Param("levelCode")String levelCode,@Param("orgId")String orgId, @Param("roleList") List roleList);

    List<DocShare> getMyShareHistoryMobile(@Param("userId") String userId, @Param("name") String name, @Param("order") String order,
                                     @Param("beginIndex") int beginIndex, @Param("pageSize") int pageSize, @Param("timeType") String timeType,
                                     @Param("levelCode")String levelCode,@Param("orgId")String orgId, @Param("roleList") List roleList,@Param("folderIds") List foderIds);


    /**
     * 获取个人分享记录总数
     * @param userId
     * @param name
     * @return
     */
    int getMyShareHistoryCount(@Param("userId") String userId, @Param("name") String name, @Param("timeType") String timeType);

    int getMyShareHistoryCountMobile(@Param("userId") String userId, @Param("name") String name, @Param("timeType") String timeType,@Param("folderIds") List folderIds);
}
