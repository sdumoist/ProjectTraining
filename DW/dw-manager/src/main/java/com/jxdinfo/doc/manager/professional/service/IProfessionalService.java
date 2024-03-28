package com.jxdinfo.doc.manager.professional.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.professional.model.Professional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专业专职表 服务类
 * </p>
 *
 * @author cxk
 * @since 2021-05-08
 */
public interface IProfessionalService extends IService<Professional> {



    /**
     * 列表查询
     * @param majorId 专业Id
     * @param userName 专职名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<Professional> professionalList(String majorId, String userName, int startIndex, int pageSize);


    /**
     * 列表数量
     * @return int 列表数量
     */
    int getProfessionalListCount(String majorId, String userName);

    /**
     * 操作校验
     * @param majorId 专业Id
     * @param id 主键
     * @return
     */
    int operationJudge(String majorId, String id);

    /**
     * 根据专业ID 获取数据
     * @param majorId 专业ID 正常数据 每一个专业只会有一条数据
     * @return
     */
    Map getProfessionalByMojorId(String majorId);
}
