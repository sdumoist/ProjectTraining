package com.jxdinfo.doc.manager.docrecycle.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.docrecycle.model.DocRecycle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 回收站 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2018-08-09
 */
public interface DocRecycleMapper extends BaseMapper<DocRecycle> {

    /**
     * 加载回收站文件列表
     * @param pages
     * @param fileName
     * @return
     */
    List<DocRecycle> getDocRecycleList(Page pages, @Param("fileName") String fileName, @Param("userId") String userId);
    /**
     * 加载回收站文件列表
     * @param pages
     * @param fileName
     * @param order
     * @param userId
     * @return
     */
    List<DocRecycle> getDocRecycleOrderedList(Page pages, @Param("fileName") String fileName, @Param("userId") String userId, @Param("order") String order, @Param("levelCodes") String levelCodes);

    List<DocRecycle> getDocRecycleOrderedListMobile(Page pages, @Param("fileName") String fileName, @Param("userId") String userId, @Param("order") String order, @Param("levelCodes") String levelCodes,@Param("folderIds") List folderIds);
    /**
     * 验证文件是否存在
     * @param foldId
     * @param fileName
     * @return
     */
    int checkDocExist(@Param("foldId") String foldId, @Param("fileName") String fileName);

    /**
     * 清除回收站
     * @param
     * @return
     */
    int updateDocRecycle(@Param("deleteUserId") String deleteUserId, @Param("levelCodes") String levelCodes);

    /**
     * 判断目录下是否存在同名待审核文件
     *
     * @param foldId 目录ID
     * @param fileName 文件名
     * @return
     */
    int checkAuditDocExist(@Param("foldId") String foldId, @Param("fileName") String fileName);
}
