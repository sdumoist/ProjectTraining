package com.jxdinfo.doc.manager.professional.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.manager.professional.model.Professional;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 专业专职表 Mapper 接口
 * </p>
 *
 * @author cxk
 * @since 2021-05-08
 */
public interface ProfessionalMapper extends BaseMapper<Professional> {


    /**
     * 列表查询
     * @param majorId 专业Id
     * @param userName 专职名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<Professional> professionalList(@Param("majorId") String majorId, @Param("userName") String userName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);



    /**
     *  列表数量
     * @param majorId 专业Id
     * @param userName 专职名称
     * @return
     */
    int getProfessionalListCount(@Param("majorId") String majorId, @Param("userName") String userName);

    /**
     * 操作校验
     * @param majorId 专业Id
     * @param id 主键
     * @return
     */
    int operationJudge(@Param("majorId") String majorId, @Param("id") String id);

    /**
     * 根据专业ID 获取数据
     * @param majorId 专业ID 正常数据 每一个专业只会有一条数据
     * @return
     */
    List<Professional> getProfessionalByMojorId(@Param("majorId") String majorId);
}
