package com.jxdinfo.doc.common.docutil.service;

import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.model.ESResponse;

import java.util.List;
import java.util.Map;

/**
 * 操作 ES 服务接口
 * @author wangning
 * @date 2018-8-10 19:45:06
 */
public interface ESService {
	
	/** 创建ES索引 */
	public int createESIndex(DocES docES) throws Exception;
	
	/** 根据关键词分页查询 */
	public ESResponse<Map<String, Object>> search(String keyword, int page, Boolean adminFlag, Integer size,Integer titlePower,Integer contentPower,Integer tagsPower,Integer categoryPower,String folderIds);

	/** 根据类型查询并返回查询结果 */
	public ESResponse<Map<String, Object>> search(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString,Integer titlePower,Integer contentPower,Integer tagsPower,Integer categoryPower,String folderIds, Integer order);
	/** 根据类型查询并返回查询结果(手机端) */
	public ESResponse<Map<String, Object>> searchMobile(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString, String userId,String folderIds, List<String> folderExtranetIds);
	/** 根据关键词分页查询 */
	public ESResponse<Map<String, Object>> searchMobile(String keyword, int page, Boolean adminFlag, Integer size, String userId,String folderIds, List<String> folderExtranetIds);
	/**
	 * 将一个文件索引复制到另一个文件中
	 * *@param oldDocId  已经存在的docID
	 * @param newDocId  新上传的docID
	 * @return 201表示复制索引成功
	 */
	public int copyIndex(String oldDocId, String newDocId);

	/**
	 * 更新索引
	 * 部分更新功能，前提是索引和该条数据已经存在，否则会抛出对应的异常，只要任何一个不满足，都会更新失败。
	 * *@param docId  已经存在的docID
	 * @param source  要更新的内容
	 * @return 200表示更新成功
	 */
	public int updateIndex(String docId, Map<String, Object> source);
	public Map<String, Object> getIndex(java.lang.String docId);
	/** 根据关键词分页查询 */
	public ESResponse<Map<String, Object>> searchWord(String keyword, int size);
	public List<String>  suggestList(String  keyword,Integer size);
}
