package com.jxdinfo.doc.common.docutil.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxdinfo.doc.common.docutil.model.DocConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 文库配置项
 * 
 * @author wangning
 */
public interface DocConfigMapper extends BaseMapper<DocConfig> {
	
	/**
	 * 根据配置项key获取配置项值
	 * @param configKey
	 * @return
	 */
	String getConfigValueByKey(@Param("configKey") String configKey);

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
	int addUserPicList(List<Map> userPicList);

	/**
	 * 更新用户头像
	 * @param userPicList 用户头像数据集合
	 * @return 更新结果
	 */
	int updateUserPicList(List<Map> userPicList);
}
