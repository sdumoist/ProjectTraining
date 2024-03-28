package com.jxdinfo.doc.manager.handovermanager.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.handovermanager.model.DocHandOver;
import com.jxdinfo.hussar.core.sys.model.DicSingle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HandOverMapper extends BaseMapper<DocHandOver> {
    List<DocHandOver> getMessageList(@Param("beginNum") Integer beginNum, @Param("limit")Integer limit,
                                   @Param("handovername")String handovername, @Param("acceptName")String acceptName,
                                   @Param("deptName")String deptName, @Param("acceptDeptName")String acceptDeptName,
                                     @Param("state") Integer state,@Param("orgId")String orgId,@Param("adminFlag") Integer adminFlag, @Param("order")String order, @Param("isDesc")String isDesc);
    Integer getMessageListCount( @Param("handovername")String handovername, @Param("acceptName")String acceptName,
                                 @Param("deptName")String deptName, @Param("acceptDeptName")String acceptDeptName,
                                 @Param("state") Integer state,@Param("orgId")String orgId,@Param("adminFlag") Integer adminFlag);
    List<FsFolderView> getList( @Param("userId")String userId,  @Param("pageNumber")Integer pageNumber,
                                @Param("pageSize") Integer pageSize, @Param("name") String name,@Param("order") String order, @Param("type") String type,@Param("isDesc") String isDesc);

    Integer getCount( @Param("userId")String userId , @Param("name") String name, @Param("type") String type);
}
