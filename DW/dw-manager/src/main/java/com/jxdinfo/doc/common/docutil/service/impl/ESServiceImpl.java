package com.jxdinfo.doc.common.docutil.service.impl;

import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.model.ESResponse;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.ESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 操作FASTDFS 服务
 * 
 * @author wangning
 * @date 2018-8-10 19:45:06
 */
@Service
public class ESServiceImpl implements ESService {

	static final public Logger LOGGER = LoggerFactory.getLogger(ESServiceImpl.class);

	@Autowired
	private ESUtil esUtil;

	/**
	 * 创建ES索引
	 * 
	 * @return 返回创建索引数
	 * @throws Exception
	 */
	@Override
	public int createESIndex(DocES docES) throws Exception {
		return esUtil.index(docES.getId(), docES.toMap());
	}

	/**
	 * 查询并返回查询结果
	 *
	 * @param keyword 查询的关键词
	 * @param page 页数
	 * @return 查询结果
	 */
	@Override
	public ESResponse<Map<String, Object>> search(String keyword, int page, Boolean adminFlag, Integer size,Integer titlePower,Integer contentPower,Integer tagsPower,Integer categoryPower,String folderIds) {
		return esUtil.multiMatchQuery(keyword, page, adminFlag,size,titlePower,contentPower,categoryPower,tagsPower,folderIds);
	}
	
	 /**
     * 根据类型查询并返回查询结果
     *
     * @param keyword     关键词
     * @param contentType 文档类型
     * @param page        页数
     * @return 查询结果
     */
	@Override
    public ESResponse<Map<String, Object>> search(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString,Integer titlePower,Integer contentPower,Integer tagsPower,Integer categoryPower,String folderIds, Integer order) {
//        String type = "";
//        if ("excel".equals(contentType)) {
//            type = "spreadsheetml";
//        } else if ("powerpoint".equals(contentType)) {
//            type = "presentationml";
//        } else if ("word".equals(contentType)) {
//            type = "word";
//        } else if ("plain".equals(contentType)) {
//            type = "plain";
//        } else if ("pdf".equals(contentType)) {
//            type = "pdf";
//        }else if ("pic".equals(contentType)) {
//			type = "image";
//		}else if ("video".equals(contentType)) {
//			type = "audio";
//		}else if ("voice".equals(contentType)) {
//			type = "video";
//		}
        return esUtil.boolQuery(keyword, contentType, page,adminFlag,size,tagString,titlePower,contentPower,categoryPower,tagsPower,folderIds,order);
    }
	/**
	 * 根据类型查询并返回查询结果
	 *
	 * @param keyword     关键词
	 * @param contentType 文档类型
	 * @param page        页数
	 * @return 查询结果
	 */
	@Override
	public ESResponse<Map<String, Object>> searchMobile(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString, String userId,String folderIds, List<String> folderExtranetIds) {
//        String type = "";
//        if ("excel".equals(contentType)) {
//            type = "spreadsheetml";
//        } else if ("powerpoint".equals(contentType)) {
//            type = "presentationml";
//        } else if ("word".equals(contentType)) {
//            type = "word";
//        } else if ("plain".equals(contentType)) {
//            type = "plain";
//        } else if ("pdf".equals(contentType)) {
//            type = "pdf";
//        }else if ("pic".equals(contentType)) {
//			type = "image";
//		}else if ("video".equals(contentType)) {
//			type = "audio";
//		}else if ("voice".equals(contentType)) {
//			type = "video";
//		}
		return esUtil.boolQueryMobile(keyword, contentType, page,adminFlag,size,tagString,userId,folderIds,folderExtranetIds);
	}
	/**
	 * 查询并返回查询结果
	 *
	 * @param keyword 查询的关键词
	 * @param page 页数
	 * @return 查询结果
	 */
	@Override
	public ESResponse<Map<String, Object>> searchMobile(String keyword, int page, Boolean adminFlag, Integer size, String userId,String folderIds, List<String> folderExtranetIds) {
		return esUtil.multiMatchQueryMobile(keyword, page, adminFlag,size,userId, folderIds, folderExtranetIds);
	}
	/**
	 * 将一个文件索引复制到另一个文件中
	 * *@param oldDocId  已经存在的docID
	 * @param newDocId  新上传的docID
	 * @return 201表示复制索引成功
	 */
	public int copyIndex(String oldDocId ,String newDocId){
		Map mapContent= esUtil.getIndex(oldDocId);
		if(mapContent == null || mapContent.get("content")==null||mapContent.get("content").equals("null")){
		return -1;
		}
		int reslutStatus = esUtil.copyIndex(oldDocId,newDocId);
		return  reslutStatus;
	}

	/**
	 * 更新索引
	 * 部分更新功能，前提是索引和该条数据已经存在，否则会抛出对应的异常，只要任何一个不满足，都会更新失败。
	 * *@param docId  已经存在的docID
	 * @param source  要更新的内容
	 * @return 200表示更新成功
	 */
	public int updateIndex(String docId , Map<String, Object> source){
		int updateStatus  = esUtil.updateIndex(docId,source);
		return updateStatus;
	}
	public Map<String, Object> getIndex(String docId) {
		return esUtil.getIndex(docId);
	}
	/**
	 * 根据关键词分页查询
	 *
	 * @param keyword
	 */
	@Override
	public ESResponse<Map<String, Object>> searchWord(String keyword, int size) {
		return esUtil.onlyMatchWordQuery(keyword,size);
	}

	@Override
	public List<String> suggestList(String keyword, Integer size) {
		return esUtil.getCompletionSuggest(size,keyword);
	}
}
