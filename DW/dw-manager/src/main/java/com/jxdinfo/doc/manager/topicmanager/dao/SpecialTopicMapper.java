package com.jxdinfo.doc.manager.topicmanager.dao;

import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 专题信息维护
 * @author zhangzhen
 * @date 2018/4/9
 */
public interface SpecialTopicMapper {
    /**
     * 专题列表查询
     * @param topicName 专题名称
     * @param startIndex 开始位置
     * @param pageSize  每页数据条数
     * @return list
     */
    List<SpecialTopic> topicList(@Param("topicName") String topicName, @Param("startIndex") int startIndex, @Param("pageSize") int pageSize);
    /**
     * 有效期内专题列表查询
     *
     * @return list
     */
    List<SpecialTopic> getValidTopicList(@Param("userId") String userId, @Param("isValid") String isValid,@Param("name") String name);
    /**
     * 手机端个性化定制专题列表查询
     *
     * @return list
     */
    List<SpecialTopic> getSpecialTopicList(@Param("userId") String userId,@Param("name") String name);
    /**
     * @title: 搜索专题文件详情
     * @description: 搜索专题文件详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords categoryCode
     * @return:
     */
    List searchTopicFilesDetail(String fileType, int page, String keyWords, String categoryCode);
    /**
     * @title: 搜索专题详情
     * @description: 搜索专题详情
     * @date: 2018-4-12.
     * @author: rxy
     * @param: fileType  page  keyWords categoryCode
     * @return:
     */
    SpecialTopic searchTopicDetail(String id);
    /**
     * 获取专题列表总数
     * @param topicName 专题名称
     * @return 总数
     */
    int getTopicListCount(@Param("topicName") String topicName);
    /**
     * 新增专题
     * @param specialTopic 专题对象
     * @return 新增数量
     */
    int addSpecialTopic(SpecialTopic specialTopic);
    /**
     * 检查专题是否存在
     * @param specialTopic 专题对象
     * @return 存在的数量
     */
    int checkTopicExist(SpecialTopic specialTopic);
    /**
     * 更新专题
     * @param specialTopic 专题对象
     * @return 更新的数量
     */
    int updateSpecialTopic(SpecialTopic specialTopic);
    /**
     * 删除专题
     * @param list id集合
     * @return 删除的数量
     */
    int delSpecialTopic(@Param("list") List<String> list);
    /**
     * 发布专题
     * @param list id集合
     * @return 发布的数量
     */
    int publishTopic(@Param("list") List<String> list, @Param("publishTopic") Integer publishTopic);

    int delDoc(@Param("id") String id, @Param("topicId") String topicId);

    int updateViewNum(@Param("topicId") String topicId, @Param("num") Integer num);
    int moveTopic(@Param("table") String table, @Param("idColumn") String idColumn, @Param("idOne") String idOne, @Param("idTwo") String idTwo);

    int getMaxOrder();

}
