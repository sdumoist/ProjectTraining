package com.jxdinfo.doc.common.docutil.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文库配置项服务接口
 * @author wangning
 */
public interface DocConfigService {
	
	/**
     * 根据key值获取配置项值
     *
     * @return String
     * @author wangning
     */
    public String getConfigValueByKey(String configKey);

    /**
     * 查询单表
     * @param table 表名
     * @param columns 要查询的字段数组
     * @param params 查询的参数
     * @return 查询结果
     */
    List<Map> select(@Param("table") String table, @Param("columns") String[] columns, @Param("params") Map params);

    /**
     * 删除单表
     * @param table 表名
     * @param params 删除的参数
     * @return 删除结果
     */
    int delete(@Param("table") String table, @Param("params") Map params);

    /**
     * 新增用户头像
     * @param userPicList 用户头像数据集合
     * @return 新增结果
     */
    int insertOrUpdate(List<Map> userPicList);
}
