package com.jxdinfo.doc.common.docutil.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * 操作Cache服务接口
 * @author wangning
 * @date 2018-8-10 19:45:06
 */
public interface CacheToolService {

	/**
	 * 获取并更新缓存中预览次数
	 *
	 * @param docId  文档id
	 * @param addNum 预览追加的次数
	 * @return 预览总次数
	 */
	public Integer getAndUpdateReadNum(String docId, int addNum);

    /**
     * 获取并更新缓存中预览数
     */
	public Integer getAndUpdateReadNum(String docId);
	
	/**
     * 首页获得文档总数
     */
	public Integer getNum();

	/**
	 * 获取缓存中预览数
	 */
	public Integer getReadNum(String docId);
	
	/**
     * 获取并更新缓存中专题预览数
     */
	public Integer getAndUpdateTopicReadNum(String docId);
	
	/**
     * 获取缓存中专题预览数
     */
	public Integer getTopicReadNum(String topicId);
	
	/**
     * 获取缓存中部门已用空间
     * @param orgId  组织机构ID
     * @author wangning 
     */
	public Double getDeptUsedSpace(String orgId);
	
	/**
     * 
     * 更新缓存中部门已用空间
     * @param userId  组织机构ID
     * @author wangning 
     */
	public void updateDeptUsedSpace(String userId, Double usedSpace);

	/**
	 * 清除缓存
	 * @param userId
	 */
	public void deleteEmpUsedSpace(String userId);


	/**
	 * 获取缓存中专题列表及文章
	 */
	public List<SpecialTopic> getTopicList();



	/**
	 * 获取缓存中热门文档
	 */
	public  List<Map>  getHotDoc(Page<T> page, String opType);

	/**
	 * 获取缓存中贡献榜
	 */
	public  List<Map>  getUploadData(String type);

	public String getFileUpLevelCodeCache(String groupIds, String userId, String levelCode, String orgId, String type,String roleIds);

	public String getLevelCodeByUserCache(String groupIds, String userId, String type, String orgId,String roleIds);
	public String getLevelCodeByUserByUploadCache(String groupIds, String userId, String type, String orgId,String roleIds);

	public String getUpLevelCodeByUserByUploadCache(String groupIds, String userId, String type, String orgId,String roleIds);

	public String getUpLevelCodeByUserCache(String groupIds, String userId, String type, String orgId,String roleIds);


	public void updateLevelCodeCache(String userId);

	/**
	 * 获取缓存中的筛选条件
	 */
	public List<Map<String,Object>> getDictListByType(String dicType);

	/**
	 * @author luzhanzhao
	 * @date 2018-12-07
	 * @description 获取缓存中的积分规则配置
	 * @param code 积分规则编码
	 * @return 积分规则配置列表
	 */
	public List<Map<String,Object>> getRuleByCode(String code);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @decription 分享功能中服务器的配置的ip地址
	 * @return ip地址的信息
	 */
	public Map getServerAddress();

	/**
	 * 更新缓存中保存的分享ip地址
	 * @param newAddress 新地址
	 */
	void updateServerAddress(String newAddress);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @decription
	 * @return
	 */
	List<Map<String,String>> getReadyToChangePdf(String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 设置
	 * @param map 要插入缓存的待转化PDF的文件信息
	 */
	public void setReadyToChangePdf(Map<String, String> map, String address);


	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 对待转化pdf的集合进行替换
	 * @param readys 要存放的待转化pdf的文件集合
	 */
	public void setReadyToChangePdf(List<Map<String, String>> readys, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 更新缓存中待转化pdf的文件信息
	 * @param uploadState 要更新的文件信息
	 */
	public void updateChangePdf(Map<String, String> uploadState, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 根据文件id将文件信息从待转化pdf的集合中移除
	 * @param docId 要移除信息的文件id
	 */
	public void removeFromChangePdfById(String docId, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 获取待创建Es文件信息集合
	 * @return 待创建Es文件信息集合
	 */
	List<Map<String,String>> getReadyToCreateEs(String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 往缓存中放置待创建Es的集合
	 * @param readys 待创建Es的集合
	 */
	public void setReadyToCreateEs(List<Map<String, String>> readys, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 更新缓存中待创建Es集合中的上传状态信息
	 * @param map 要更新上传状态的上传状态信息
	 */
	public void updateReadyToCreateEs(Map<String, String> map, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 往缓存待创建Es的集合中放置要创建Es的文件信息
	 * @param map 要创建Es的文件信息
	 */
    public void setReadyToCreateEs(Map<String, String> map, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 根据文件id将文档上传状态从待创建Es列表中移除
	 * @param docId 文件id
	 */
	public void removeFromCreateEsById(String docId, String address);

	public void setIsNullToChange(boolean flag);

    public boolean getIsNullToChange();

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 设置是否启用fastDFS服务器的状态
	 * @param flag 是否启用fastDFS服务器
	 */
	public void setFastDFSUsingFlag(boolean flag);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @descirpiton 获取是否启用fastDFS服务器的状态
	 * @return 是否启用fastDFS服务器
	 */
    public boolean getFastDFSUsingFlag();

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 往待删除列表中放入待删除文件的信息
	 * @param map 待删除文件的信息
	 */
	public void setReadyDeleteList(Map<String, String> map, String address);


	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 将需要快速转化的文件信息放到缓存中
	 * @param map 需要快速转化的文件信息
	 */
	public void setReadyToFastChange(Map<String, String> map, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 获得缓存中的快速转化列表
	 * @return 快速转化列表
	 */
	List<Map<String,String>> getFastChangeList(String address);

	public void setReadyToFastChange(List<Map<String, String>> readys, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 根据文件id，将文档信息从快速转化列表中移除
	 * @param docId
	 */
	public void removeFromFastChange(String docId, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 更新快速转化文件的上传状态
	 * @param uploadState 要更新的文件上传状态
	 */
	public void updateFastChange(Map<String, String> uploadState, String address);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description
	 * @param uploadState
	 */
	public void setUploadState(Map<String, String> uploadState);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @description 更新文件上传状态
	 * @param uploadState
	 */
	public void updateUploadState(Map<String, String> uploadState);

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @descirption 获取文件上传状态列表
	 * @return
	 */
	List<Map<String,String>> getUploadStateList();

	/**
	 * @author luzhanzhao
	 * @date 2019-1-9
	 * @descirption 根据文档id将文档信息从上传状态中移除
	 * @param docId 要移除的信息的文档id
	 */
	void removeFromUploadStateListById(String docId);

	public Integer getComponentReadNum(String componentId);
	/**
	 * 获取并更新缓存中预览数
	 */
	public Integer getAndUpdateComponentReadNum(String component);
}
