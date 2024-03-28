package com.jxdinfo.doc.common.docutil.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.common.constant.CacheConstant;
import com.jxdinfo.doc.common.docutil.dao.BusinessMapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.docsharemanager.dao.ShareResourceMapper;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.front.topicmanager.service.FrontTopicService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.docintegral.dao.IntegralRuleMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.doc.manager.topicmanager.model.SpecialTopic;
import com.jxdinfo.hussar.common.dicutil.DictionaryUtil;
import com.jxdinfo.hussar.core.cache.HussarCacheManager;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import net.sf.ehcache.ObjectExistsException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 文库缓存工具类
 * @author 王宁
 *
 */
@Service
public class CacheToolServiceImpl implements CacheToolService {
	
	@Autowired
	private HussarCacheManager hussarCacheManager;
	@Autowired
	private IDocFoldAuthorityService docFoldAuthorityService;
	@Autowired
	private DocInfoService docInfoService;
	@Resource
	private ComponentApplyService componentApplyService;

	/** 文档群组服务类 */
	@Autowired
	private FrontDocGroupService frontDocGroupService;

	/** 前台专题服务类 */
	@Autowired
	private FrontTopicService frontTopicService;

	/**
	 * 目录服务类
	 */
	@Resource
	private BusinessService businessService;
	
	/** 部门占用空间统计服务 */
	@Autowired
	private EmpStatisticsService empStatisticsService;

	/** 目录dao层 */
	@Resource
	private BusinessMapper businessMapper;

	/**
	 * 文件统计接口
	 */
	@Autowired
	private FileStatisticsService fileStatisticsService;


	static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
	/**
	 * 字典工具接口
	 */
	@Resource
	private DictionaryUtil dictionaryUtil;

	/**
	 * 积分规则配置dao层
	 */
	@Resource
	private IntegralRuleMapper integralRuleMapper;

	/**
	 * 分享服务dao层
	 */
	@Resource
	private ShareResourceMapper shareResourceMapper;

	/**
	 * 获取并更新缓存中预览次数
	 *
	 * @param docId  文档id
	 * @param addNum 预览追加的次数
	 * @return 预览总次数
	 */
	public Integer getAndUpdateReadNum(String docId, int addNum) {
		Integer num = 1;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
				CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId);
		// 如果缓存中不存在这个key，从数据库读取
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj) + addNum;
			hussarCacheManager.setObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
					CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId, num);
		} else {
			num = docInfoService.getDocReadNum(docId) == null ? addNum : docInfoService.getDocReadNum(docId) + addNum;
			hussarCacheManager.setObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
					CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId, num);
		}

		return num;
	}

	/**
     * 获取并更新缓存中文档预览数
     */
	public synchronized Integer getAndUpdateReadNum(String docId) {
		Integer num = 1;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
														CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId);
		// 如果缓存中不存在这个key，从数据库读取
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj) + 1;
//			DocInfo docInfo = new DocInfo();
//			docInfo.setDocId(docId);
//			docInfo.setFileId(docId);
//			docInfo.setReadNum(num);
//			docInfoService.updateById(docInfo);
			hussarCacheManager.setObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
											CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId, num);
		} else {
			num = docInfoService.getDocReadNum(docId) + 1;
			hussarCacheManager.setObject(CacheConstant.DOC_VIEW_NUM_CACHENAME,
												CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId, num);
		}

		return num;
	}

	@Override
	public Integer getNum() {
		Integer num = 0;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.DOC_TOTAL_COUNT, CacheConstant.PREX_DOC_TOTAL_COUNT) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj);
		} else {
			num = fileStatisticsService.getFilesCount();
			hussarCacheManager.setObject(CacheConstant.DOC_TOTAL_COUNT, CacheConstant.PREX_DOC_TOTAL_COUNT, num);
		}

		return num;


}



	/**
     * 获取缓存中文档预览数
     */
	public Integer getReadNum(String docId) {
		Integer num = 0;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.DOC_VIEW_NUM_CACHENAME, CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj);
		} else {
			num = docInfoService.getDocReadNum(docId);
			hussarCacheManager.setObject(CacheConstant.DOC_VIEW_NUM_CACHENAME, CacheConstant.PREX_DOC_VIEW_NUM_CACHENAME + docId, num);
		}

		return num;
	}
	
	/**
     * 获取并更新缓存中专题预览数
     */
	public synchronized Integer getAndUpdateTopicReadNum(String topicId) {
		Integer num = 1;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
														CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId);
		// 如果缓存中不存在这个key，从数据库读取
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj) + 1;
			hussarCacheManager.setObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
											CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId, num);
		} else {
			num = docInfoService.getTopicReadNum(topicId) + 1;
			hussarCacheManager.setObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
												CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId, num);
		}

		return num;
	}
	
	/**
     * 获取缓存中专题预览数
     */
	public Integer getTopicReadNum(String topicId) {
		Integer num = 0;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
														CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj);
		} else {
			num = docInfoService.getTopicReadNum(topicId);
			hussarCacheManager.setObject(CacheConstant.TOPIC_VIEW_NUM_CACHENAME,
												CacheConstant.PREX_TOPIC_VIEW_NUM_CACHENAME + topicId, num);
		}

		return num;
	}
	
	/**
     * 获取缓存中个人已用空间
     * @param UserId  组织机构ID
     * @author lishilin
     */
	public Double getDeptUsedSpace(String UserId){
		Double usedSpace = 0d;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.EMP_USED_SPACE_CACHENAME,
														CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + UserId);
		// 如果缓存中不存在这个key，从数据库读取
		if (ObjectUtils.allNotNull(obj)) {
			usedSpace = StringUtil.getDouble(obj);
		} else {
			usedSpace = empStatisticsService.getUsedSpaceByUserId(UserId);
			hussarCacheManager.setObject(CacheConstant.EMP_USED_SPACE_CACHENAME,
												CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + UserId, usedSpace);
		}

		return usedSpace;
	}
	
	/**
     * 
     * 更新缓存中部门已用空间
     * @param userId  组织机构ID
     * @author wangning 
     */
	public void updateDeptUsedSpace(String userId, Double usedSpace){
		Object obj = hussarCacheManager.getObject(CacheConstant.EMP_USED_SPACE_CACHENAME,
				CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + userId);
		Double newSpace = StringUtil.getDouble(obj) + usedSpace;
		hussarCacheManager.setObject(CacheConstant.EMP_USED_SPACE_CACHENAME,
											CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + userId, newSpace);
	}

	/**
	 * 清除缓存
	 * @param userId
	 */
	@Override
	public void deleteEmpUsedSpace(String userId){
		if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.EMP_USED_SPACE_CACHENAME,
				CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + userId)))){
			hussarCacheManager.delete(CacheConstant.EMP_USED_SPACE_CACHENAME,
					CacheConstant.PREX_EMP_USED_SPACE_CACHENAME + userId);
		}
	}

	@Override
	public List<SpecialTopic> getTopicList() {
		List<SpecialTopic> topicList =new ArrayList<>();
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.TOPIC_DOC_LIST,
				CacheConstant.PREX_TOPIC_DOC_LIST ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			topicList = (List<SpecialTopic>) obj;
		} else {
			topicList = frontTopicService.getTopicList(0, 5);
			String userId = UserInfoUtil.getUserInfo().get("ID").toString();
			List<String> listGroup = frontDocGroupService.getPremission(userId);
			if (topicList != null && topicList.size() > 0) {
				for (SpecialTopic specialTopic : topicList) {
					String topicId = specialTopic.getTopicId();
					String topicCover = null;
					try {
						topicCover = URLEncoder.encode(specialTopic.getTopicCover(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					specialTopic.setTopicCover(topicCover);
					FsFolderParams fsFolderParams = new FsFolderParams();
					fsFolderParams.setGroupList(listGroup);
					fsFolderParams.setUserId(userId);
					fsFolderParams.setType("2");
					String levelCode = businessService.getLevelCodeByUserUpload(fsFolderParams);
					List<String> roleList= ShiroKit.getUser().getRolesList();
					// 获取当前文库权限
					Integer adminFlag= CommonUtil.getAdminFlag(roleList);;
					//测试不考虑专题权限,取专题下面的文档
					String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
					fsFolderParams.setType("2");
					String levelCodeString  = businessService.getLevelCodeByUserUpload(fsFolderParams);
					List<FsFile> docList = frontTopicService.getDocByTopicId(topicId, "create_time",
							0, 4,userId,listGroup,levelCode,adminFlag,orgId,levelCodeString,roleList);
					specialTopic.setDocList(docList);
				}
			}
			hussarCacheManager.setObject(CacheConstant.TOPIC_DOC_LIST,
					CacheConstant.PREX_TOPIC_DOC_LIST , topicList);
		}

		return topicList;
	}

	@Override
	public List<Map> getHotDoc(Page<T> page, String opType) {
		List<Map>  hotDocList =new ArrayList<>();
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.HOT_DOC_LIST,
				CacheConstant.PREX_HOT_DOC_LIST ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			hotDocList = (List<Map>) obj;
		} else {
			hotDocList=fileStatisticsService.getFileListDataAllPerson(page,opType);
			hussarCacheManager.setObject(CacheConstant.HOT_DOC_LIST,
					CacheConstant.PREX_HOT_DOC_LIST , hotDocList);
		}
		return  hotDocList;
	}

	@Override
	public List<Map> getUploadData(String type) {
		List<Map>    uploadDataList=new ArrayList<>();

			uploadDataList=fileStatisticsService.getUploadData(type);
			hussarCacheManager.setObject(CacheConstant.UPLOAD_DATA_LIST,
					CacheConstant.PREX_UPLOAD_DATA_LIST , uploadDataList);

		return  uploadDataList;
	}

	// 查询 文件所有权限的目录 和 目录的查看、上传权限
	@Override
	public String getFileUpLevelCodeCache(String groupIds, String userId, String levelCode, String orgId, String type,String roleIds) {
		String fileUpLevelCode="";


			Object obj = hussarCacheManager.getObject(CacheConstant.FILE_LEVEL_CODE + userId+type,
					CacheConstant.PREX_FILE_LEVEL_CODE + userId+type ) ;
			// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			fileUpLevelCode = (String) obj;
			//System.out.println("====================getLevelCodeByUserCache走缓存");
		} else {
			//System.out.println("====================getLevelCodeByUserCache不走缓存");
			fileUpLevelCode = businessMapper.getFileUpLeveCode(groupIds, userId, "0001", orgId, type, roleIds);
			//long time1 = System.currentTimeMillis();
			fileUpLevelCode = addLevelCode(fileUpLevelCode);
			//long time2 = System.currentTimeMillis();
			//System.out.println("===============addLevelCode"+(time2-time1));
			hussarCacheManager.setObject(CacheConstant.FILE_LEVEL_CODE + userId + type,
					CacheConstant.PREX_FILE_LEVEL_CODE + userId + type, fileUpLevelCode);
		}


		return  fileUpLevelCode;
	}

	public  String addLevelCode(String levelCode){
//		String levelCodeString ="";
//
//		String levelCodeStr [] =levelCode.split(",");
//		for(int i=0;i<levelCodeStr.length;i++){
//			for(int j=levelCodeStr[i].length()/3;j>0;j--){
//				levelCodeString=levelCodeString+","+levelCodeStr[i].substring(0,j*3);
//			}
//		}
		Map<String,String> map = new HashMap<>();
		Set<String> set = new HashSet<>();
		StringBuffer levelCodeString = new StringBuffer(1024);
		if(levelCode!=null){
		String[] levelCodeStr  = levelCode.split(",");
		for(int i=0;i<levelCodeStr.length;i++) {
			if(levelCodeStr[i].equals("000100050005")){
System.out.println(1);
			}
			for (int j = levelCodeStr[i].length() / 4; j > 0; j--) {
				String b = levelCodeStr[i].substring(0, j * 4);
				if (set.contains(b)) {
					break;
				} else {
					levelCodeString.append(",").append(b);
					set.add(b);
				}
			}
		}
			return levelCodeString.toString();
		}else {
			return  null;
		}

	}
	// 获取有管理权限的目录 并向上、向下发散
	@Override
	public String getLevelCodeByUserCache(String groupIds, String userId,  String type, String orgId,String roleIds) {
		String UserLevelCode="";
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.USER_LEVEL_CODE + userId+type,
				CacheConstant.PREX_USER_LEVEL_CODE + userId+type ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			UserLevelCode = (String) obj;
			//System.out.println("====================getLevelCodeByUserCache走缓存");
		} else {
			try{
			//	System.out.println("====================getLevelCodeByUserCache不走缓存");
			UserLevelCode=businessMapper.getLevelCodeByUser(groupIds,userId,type,orgId,roleIds);
			hussarCacheManager.setObject(CacheConstant.USER_LEVEL_CODE + userId+type,
					CacheConstant.PREX_USER_LEVEL_CODE +userId+type , UserLevelCode);
			}catch (Exception e){
				e.printStackTrace();
				LOGGER.error("" + ExceptionUtils.getErrorInfo(e));
			}
		}
		return  UserLevelCode;
	}

	// 查询用户管理权限的目录levelCode  并且向下发散
	@Override
	public String getLevelCodeByUserByUploadCache(String groupIds, String userId,  String type, String orgId,String roleIds) {
		String UserLevelCode="";
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.USER_LEVEL_CODE_UPLOAD + userId+type,
				CacheConstant.PREX_USER_LEVEL_CODE_UPLOAD + userId+type ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			//System.out.println("getLevelCodeByUserByUploadCache走缓存");
			UserLevelCode = (String) obj;
		} else {
			//System.out.println("getLevelCodeByUserByUploadCache不走缓存");
			UserLevelCode=businessMapper.getLevelCodeByUserByUpload(groupIds,userId,type,orgId,roleIds);
			try{
				hussarCacheManager.setObject(CacheConstant.USER_LEVEL_CODE_UPLOAD + userId+type,
						CacheConstant.PREX_USER_LEVEL_CODE_UPLOAD +userId+type , UserLevelCode);
			}catch (ObjectExistsException e){
				e.printStackTrace();
			}
		}
		return  UserLevelCode;
	}

	@Override
	public String getUpLevelCodeByUserCache(String groupIds, String userId,  String type, String orgId,String roleIds) {
		String UpLevelCode="";
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UP_LEVEL_CODE + userId+type,
				CacheConstant.PREX_UP_LEVEL_CODE + userId+type ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			UpLevelCode = (String) obj;
		} else {
			UpLevelCode=businessMapper.getUpLevelCodeByUser(groupIds, userId, type,orgId,roleIds);
			try {
				hussarCacheManager.setObject(CacheConstant.UP_LEVEL_CODE + userId+type,
						CacheConstant.PREX_UP_LEVEL_CODE +userId+type , UpLevelCode);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return  UpLevelCode;
	}

	@Override
	public String getUpLevelCodeByUserByUploadCache(String groupIds, String userId,  String type, String orgId,String roleIds) {
		String UpLevelCode="";
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UP_LEVEL_CODE_UPLOAD + userId+type,
				CacheConstant.PREX_UP_LEVEL_CODE_UPLOAD + userId+type ) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			UpLevelCode = (String) obj;
		} else {
			UpLevelCode=businessMapper.getUpLevelCodeByUserByUpload(groupIds, userId, type,orgId,roleIds);
			hussarCacheManager.setObject(CacheConstant.UP_LEVEL_CODE_UPLOAD + userId+type,
					CacheConstant.PREX_UP_LEVEL_CODE_UPLOAD +userId+type , UpLevelCode);
		}
		return  UpLevelCode;
	}


	@Override
    public void updateLevelCodeCache(String userId) {
        if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.UP_LEVEL_CODE + userId+"1",
                CacheConstant.PREX_UP_LEVEL_CODE + userId+"1" )))){
            hussarCacheManager.delete(CacheConstant.UP_LEVEL_CODE + userId+"1",
                    CacheConstant.PREX_UP_LEVEL_CODE + userId+"1");
        }
		if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.UP_LEVEL_CODE_UPLOAD + userId+"1",
				CacheConstant.PREX_UP_LEVEL_CODE_UPLOAD + userId+"1" )))){
			hussarCacheManager.delete(CacheConstant.UP_LEVEL_CODE_UPLOAD + userId+"1",
					CacheConstant.PREX_UP_LEVEL_CODE_UPLOAD + userId+"1");
		}
        if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.FILE_LEVEL_CODE + userId+ "front",
                CacheConstant.PREX_FILE_LEVEL_CODE + userId+"front" )))){
            hussarCacheManager.delete(CacheConstant.FILE_LEVEL_CODE + userId+ "front",
                    CacheConstant.PREX_FILE_LEVEL_CODE + userId+"front" );
        }
        if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.FILE_LEVEL_CODE + userId+"",
                CacheConstant.PREX_FILE_LEVEL_CODE + userId+"")))){
            hussarCacheManager.delete(CacheConstant.FILE_LEVEL_CODE + userId+"",
                    CacheConstant.PREX_FILE_LEVEL_CODE + userId+"");
        }
        if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.USER_LEVEL_CODE + userId+"2",
                CacheConstant.PREX_USER_LEVEL_CODE + userId+"2" )))){
            hussarCacheManager.delete(CacheConstant.USER_LEVEL_CODE + userId+"2",
                    CacheConstant.PREX_USER_LEVEL_CODE + userId+"2");
        }

		if((ObjectUtils.allNotNull(hussarCacheManager.getObject(CacheConstant.USER_LEVEL_CODE_UPLOAD + userId+"2",
				CacheConstant.PREX_USER_LEVEL_CODE_UPLOAD + userId+"2" )))){
			hussarCacheManager.delete(CacheConstant.USER_LEVEL_CODE_UPLOAD + userId+"2",
					CacheConstant.PREX_USER_LEVEL_CODE_UPLOAD + userId+"2");
		}
    }

	/**
	 * 获取缓存中的筛选条件
	 *
	 * @param dicType
	 */
	@Override
	public List<Map<String, Object>> getDictListByType(String dicType) {
		List<Map<String,Object>> dicDataList = new ArrayList<>();
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.DIC_DATA_LIST,
				CacheConstant.PREX_DIC_DATA_LIST + dicType ) ;
		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			dicDataList = (List<Map<String, Object>>) obj;
		} else {
			dicDataList = businessService.getDictListByType(dicType);
			hussarCacheManager.setObject(CacheConstant.DIC_DATA_LIST,
					CacheConstant.PREX_DIC_DATA_LIST + dicType, dicDataList);
		}
		return dicDataList;
	}

	/**
	 * 获取缓存中的积分规则配置
	 *
	 * @param code
	 */
	@Override
	public List<Map<String, Object>> getRuleByCode(String code) {
		List<Map<String, Object>> ruleCodeList = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.RULE_CODE_LIST,
				CacheConstant.PREX_RULE_CODE_LIST + code);
		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull((obj))) {
			ruleCodeList = (List<Map<String, Object>>) obj;
		} else {
			ruleCodeList = integralRuleMapper.getRuleByCode(code);
			hussarCacheManager.setObject(CacheConstant.RULE_CODE_LIST,
					CacheConstant.PREX_RULE_CODE_LIST + code, ruleCodeList);
		}
		return ruleCodeList;
	}

	@Override
	public Map getServerAddress() {
		Map serverAddress = new HashMap();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.SERVER_ADDRESS,
				CacheConstant.PREX_SERVER_ADDRESS);
		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)){
			serverAddress = (Map) obj;
		} else {
			serverAddress = shareResourceMapper.getServerAddress();
			hussarCacheManager.setObject(CacheConstant.SERVER_ADDRESS,
					CacheConstant.PREX_SERVER_ADDRESS,serverAddress);
		}
		return serverAddress;
	}

	/**
	 * 更新缓存中保存的分享地址
	 *
	 * @param newAddress 新地址
	 */
	public void updateServerAddress(String newAddress) {
		Map serverAddress = new HashMap();
		Object obj = hussarCacheManager.getObject(CacheConstant.SERVER_ADDRESS,
				CacheConstant.PREX_SERVER_ADDRESS);
		if (ObjectUtils.allNotNull(obj)) {
			serverAddress = (Map) obj;
			if (serverAddress.containsKey("address")) {
				String oldAddress = serverAddress.get("address").toString();
				if (!StringUtils.equals(oldAddress, newAddress)) {
					serverAddress.put("address", newAddress);
					hussarCacheManager.setObject(CacheConstant.SERVER_ADDRESS,
							CacheConstant.PREX_SERVER_ADDRESS, serverAddress);
				}
			}
		}
	}

	@Override
	public List<Map<String,String>> getReadyToChangePdf(String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address);

		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
		} else {
			return null;
		}
		return readys;

	}

	@Override
	public void setReadyToChangePdf(Map<String, String> map,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address);
        if (ObjectUtils.allNotNull((obj))) {
            readys = (List<Map<String, String>>) obj;
        }
		readys.add(map);
		hussarCacheManager.setObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address,readys);
	}

	@Override
	public void setReadyToChangePdf(List<Map<String, String>> readys,String address) {
		hussarCacheManager.setObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address,readys);
	}

	@Override
	public void updateChangePdf(Map<String, String> uploadState,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
			for (Map<String,String> map : readys){
				if (map.get("docId").equals(uploadState.get("docId"))){
					readys.set(readys.indexOf(map),uploadState);
				}
			}
		} else {
			readys.add(uploadState);
		}
		hussarCacheManager.setObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address,readys);
	}

	@Override
	public void removeFromChangePdfById(String docId,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address);
		readys = (List<Map<String, String>>) obj;
		Map<String,String> toRemove = null;
		if(readys!=null){
		for (Map<String,String> map : readys){
			if (map.get("docId").equals(docId)){
				toRemove = map;
			}
		}
		if (null != toRemove){
			readys.remove(toRemove);
		}
		hussarCacheManager.setObject(CacheConstant.READY_TO_PDF_LIST,
				CacheConstant.PREX_READY_TO_PDF_LIST + address,readys);
		}
	}

	@Override
	public List<Map<String, String>> getReadyToCreateEs(String address) {
		List<Map<String,String>> toEs = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address);

		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull((obj))) {
			toEs = (List<Map<String, String>>) obj;
		} else {
			return null;
		}
		return toEs;
	}

	@Override
	public void setReadyToCreateEs(List<Map<String, String>> toEsLists,String address) {
		hussarCacheManager.setObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address,toEsLists);
	}

	@Override
	public void updateReadyToCreateEs(Map<String, String> uploadState,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
			for (Map<String,String> map : readys){
				if (map.get("docId").equals(uploadState.get("docId"))){
					readys.set(readys.indexOf(map),uploadState);
				}
			}
		} else {
			readys.add(uploadState);
		}
		hussarCacheManager.setObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address,readys);
	}

	@Override
    public void setReadyToCreateEs(Map<String, String> map,String address) {
        List<Map<String,String>> readys = new ArrayList<>();
        //先获取缓存中的数据，如果没有则创建缓存
        Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_ES_LIST,
                CacheConstant.PREX_READY_TO_ES_LIST + address);
        if (ObjectUtils.allNotNull((obj))) {
            readys = (List<Map<String, String>>) obj;
        }
        readys.add(map);
        hussarCacheManager.setObject(CacheConstant.READY_TO_ES_LIST,
                CacheConstant.PREX_READY_TO_ES_LIST + address,readys);
    }

    @Override
	public void removeFromCreateEsById(String docId,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address);
		readys = (List<Map<String, String>>) obj;
		Map<String,String> toRemove = null;
		for (Map<String,String> map : readys){
			if (map.get("docId").equals(docId)){
				toRemove = map;
			}
		}
		if (null != toRemove){
			readys.remove(toRemove);
		}
		hussarCacheManager.setObject(CacheConstant.READY_TO_ES_LIST,
				CacheConstant.PREX_READY_TO_ES_LIST + address,readys);
	}

    @Override
    public void setIsNullToChange(boolean flag) {
        hussarCacheManager.setObject(CacheConstant.IS_NULL_TO_CHANGE,
                CacheConstant.PREX_IS_NULL_TO_CHANGE,flag);
    }

    @Override
    public boolean getIsNullToChange() {
	    boolean flag;
        //先获取缓存中的数据，如果没有则创建缓存
        Object obj = hussarCacheManager.getObject(CacheConstant.IS_NULL_TO_CHANGE,
                CacheConstant.PREX_IS_NULL_TO_CHANGE);

        //如果缓存中不存在这个key，则读取数据库
        if (ObjectUtils.allNotNull((obj))) {
            flag = (boolean)obj;
        } else {
            return false;
        }
        return flag;
    }

    @Override
    public void setFastDFSUsingFlag(boolean flag) {
        hussarCacheManager.setObject(CacheConstant.FASTDFS_USING_FLAG,
                CacheConstant.PREX_FASTDFS_USING_FLAG,flag);
    }

    @Override
    public boolean getFastDFSUsingFlag() {
        boolean flag;
        //先获取缓存中的数据，如果没有则创建缓存
        Object obj = hussarCacheManager.getObject(CacheConstant.FASTDFS_USING_FLAG,
                CacheConstant.PREX_FASTDFS_USING_FLAG);

        //如果缓存中不存在这个key，则读取数据库
        if (ObjectUtils.allNotNull((obj))) {
            flag = (boolean)obj;
        } else {
            return false;
        }
        return flag;
    }

	@Override
	public void setReadyDeleteList(Map<String, String> map,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.READY_DELETE_LIST,
				CacheConstant.PREX_READY_DELETE_LIST + address);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
		}
		readys.add(map);
		hussarCacheManager.setObject(CacheConstant.READY_DELETE_LIST,
				CacheConstant.PREX_READY_DELETE_LIST + address,readys);
	}



	@Override
	public void setReadyToFastChange(Map<String, String> map,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.FAST_CHANGE_LIST,
				CacheConstant.PREX_FAST_CHANGE_LIST + address);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
		}
		if (!readys.contains(map)) {
            readys.add(map);
            hussarCacheManager.setObject(CacheConstant.FAST_CHANGE_LIST,
                    CacheConstant.PREX_FAST_CHANGE_LIST + address, readys);
        }
	}

	@Override
	public List<Map<String, String>> getFastChangeList(String address) {
		List<Map<String,String>> fastChangeList = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.FAST_CHANGE_LIST,
				CacheConstant.PREX_FAST_CHANGE_LIST + address);

		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull((obj))) {
			fastChangeList = (List<Map<String, String>>) obj;
		} else {
			return null;
		}
		return fastChangeList;
	}

	@Override
	public void setReadyToFastChange(List<Map<String, String>> readys, String address) {
		hussarCacheManager.setObject(CacheConstant.FAST_CHANGE_LIST,
				CacheConstant.PREX_FAST_CHANGE_LIST + address,readys);
	}

	@Override
    public void removeFromFastChange(String docId,String address) {
        List<Map<String,String>> readys = new ArrayList<>();
        //先获取缓存中的数据，如果没有则创建缓存
        Object obj = hussarCacheManager.getObject(CacheConstant.FAST_CHANGE_LIST,
                CacheConstant.PREX_FAST_CHANGE_LIST + address);
        readys = (List<Map<String, String>>) obj;
        Map<String,String> toRemove = null;
        for (Map<String,String> map : readys){
            if (map.get("docId").equals(docId)){
                toRemove = map;
            }
        }
        if (null != toRemove){
            readys.remove(toRemove);
        }
        hussarCacheManager.setObject(CacheConstant.FAST_CHANGE_LIST,
                CacheConstant.PREX_FAST_CHANGE_LIST + address,readys);
    }

	@Override
	public void updateFastChange(Map<String, String> uploadState,String address) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.FAST_CHANGE_LIST,
				CacheConstant.PREX_FAST_CHANGE_LIST + address);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
			for (Map<String,String> map : readys){
				if (map.get("docId").equals(uploadState.get("docId"))){
					readys.set(readys.indexOf(map),uploadState);
				}
			}
		} else {
			readys.add(uploadState);
		}
		hussarCacheManager.setObject(CacheConstant.FAST_CHANGE_LIST,
				CacheConstant.PREX_FAST_CHANGE_LIST + address,readys);
	}

	@Override
	public void setUploadState(Map<String, String> uploadState) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
		}
		readys.add(uploadState);
		hussarCacheManager.setObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST,readys);
	}

	@Override
	public void updateUploadState(Map<String, String> uploadState) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
			for (Map<String,String> map : readys){
				if (map.get("docId").equals(uploadState.get("docId"))){
					readys.set(readys.indexOf(map),uploadState);
				}
			}
		} else {
			readys.add(uploadState);
		}
		hussarCacheManager.setObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST,readys);
	}

	@Override
	public List<Map<String, String>> getUploadStateList() {
		List<Map<String,String>> fastChangeList = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST);

		//如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull((obj))) {
			fastChangeList = (List<Map<String, String>>) obj;
		} else {
			return null;
		}
		return fastChangeList;
	}

	@Override
	public void removeFromUploadStateListById(String docId) {
		List<Map<String,String>> readys = new ArrayList<>();
		//先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.UPLOAD_STATE_LIST,
				CacheConstant.PREX_UPLOAD_STATE_LIST);
		if (ObjectUtils.allNotNull((obj))) {
			readys = (List<Map<String, String>>) obj;
			Map<String, String> toRemove = null;
			for (Map<String, String> map : readys) {
				if (map.get("docId").equals(docId)) {
					toRemove = map;
				}
			}
			if (null != toRemove) {
				readys.remove(toRemove);
			}
			hussarCacheManager.setObject(CacheConstant.UPLOAD_STATE_LIST,
					CacheConstant.PREX_UPLOAD_STATE_LIST, readys);
		}
	}

	@Override
	public Integer getAndUpdateComponentReadNum(String componentId) {
		Integer num = 1;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.COMPONENT_VIEW_NUM_CACHENAME,
				CacheConstant.PREX_COMPONENT_VIEW_NUM_CACHENAME + componentId);
		// 如果缓存中不存在这个key，从数据库读取
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj) + 1;

			hussarCacheManager.setObject(CacheConstant.COMPONENT_VIEW_NUM_CACHENAME,
					CacheConstant.PREX_COMPONENT_VIEW_NUM_CACHENAME + componentId, num);
		} else {
			ComponentApply componentApply = componentApplyService.getById(componentId);
			if(componentApply.getReadNum()==null){
				num =1;
			}else {
				num=componentApply.getReadNum()+1;
			}
			hussarCacheManager.setObject(CacheConstant.COMPONENT_VIEW_NUM_CACHENAME,
					CacheConstant.PREX_COMPONENT_VIEW_NUM_CACHENAME + componentId, num);
		}

		return num;
	}
	@Override
	public Integer getComponentReadNum(String componentId) {
		Integer num = 0;
		// 先获取缓存中的数据，如果没有则创建缓存
		Object obj = hussarCacheManager.getObject(CacheConstant.COMPONENT_VIEW_NUM_CACHENAME, CacheConstant.PREX_COMPONENT_VIEW_NUM_CACHENAME + componentId) ;
		// 如果缓存中不存在这个key，则读取数据库
		if (ObjectUtils.allNotNull(obj)) {
			num = StringUtil.getInteger(obj);
		} else {
			ComponentApply componentApply = componentApplyService.getById(componentId);
			if(componentApply==null){
				return 0;
			}
			if(componentApply.getReadNum()==null){
				num =0;
			}else {
				num=componentApply.getReadNum()+1;
			}
			hussarCacheManager.setObject(CacheConstant.COMPONENT_VIEW_NUM_CACHENAME, CacheConstant.PREX_COMPONENT_VIEW_NUM_CACHENAME + componentId, num);
		}

		return num;
	}

	public static void main ( String[] args ){
		String a = "001018002029,001001001,001001004,001018002039,001017002001001,001005018006,001018001001,001006005001001001009,001006005001001001004,001005001001,001005001002,001005003,001005004,001018004015,001009009003,001005012,001008001,001008002,001008004,001008005,001008006,001006005002007002,001005002001,001004005,001005008002002016,001009001,001009002,001009003,001009004,001009005,001009006,001009007,001009013,001006005002003006,001018001018,001013005001,001006005002004005,001018008,001015012001001004001,001014001009,001015010,001010005019002,001018003006,001018001017,001006005002001007,001006006001001,001015014003002002,001015012002001002003001,001005018035,001006005002006001,001003008,001006005003002002,001005016001,001005008002002012,001018003004,001010012005005,001009008003,001018005002,001005008002002014,001010005027001,001006005002001027,001006005002007006002,001015014001009,001015012001001007001,001018001004,001009010004,001005007002,001001007,001012006002,001013004002,001009017001,001018005010,001005002009,001012005006,001006005002001008,001009008002,001018004037,001015004,001018002056,001010005009002,001018004020,001005018020,001018002006,001013006003,001018010,001005008002003016,001014001015,001005006002,001013002004,001018011001,001009010005,001012006002001,001018002053,001010005048001,001010009001,001005018024,001012005007001,001008004001,001017004,001017003001,001015014001008,001012004001,001005016003,001005018014,001010011,001005002011,001005005002,001018001019,001010009002,001018002041,001013003001,001005002010,001018004017,001005002012,001010005002003,001010003001001,001005008002001022,001015012002001002004,001005018002,001012004001001,001015012002001005,001005002004,001015014002007,001014001005,001005016004,001010008003,001015012002002003,001006005002005,001015012001001006,001005002007,001006005001001001017,001016001004004,001018002054,001018002013,001018009,001013002001,001010005049002,001006005002003009,001018002049,001006005002001021,001018005014,001006005003002001,001015012001001005003,001005006016,001005018033,001018002073,001006005002001003,001010005039001,001018004012,001006005001001001008,001005002003,001010005005001,001015012002002001,001010003001003,001018001007,001010006,001018004007,001005008002001018,001010005022001,001006005002007006003,001010015001,001005008002001013,001010005025001,001011001002,001010015004,001018005019,001018005001,001005006003,001018002045,001006005002004003,001006005001002001008,001018002061,001014002,001001010,001018002026,001010005016002,001014001011,001005016002,001015014002004,001018004036,001018002058,001005010,001010010003,001010005004002,001018004022,001005008002003013,001005008002002017,001009010001,001005018022,001010005001002,001005008002001016,001018001012,001018005017,001010005008002,001013002007,001006005001001001013,001006005002003007,001010016,001006005002008001,001005008002001012,001018001011,001006005002004007,001010005022003,001016001003004,001005002002,001010012004,001018004029,001010005001001,001015014001007,001018002074,001010005011002,001015012001001005002,001006005002001020,001005008002003012,001014001010,001010005025002,001006005002001024,001005001005,001013003004,001018002005,001005018005,001018004009,001006005001001001010,001014001015001,001005018030,001013004001,001005008002002013,001005002015,001016001004003001,001006005002002003,001005008002001020,001005008002003002,001005019,001011002,001018004023,001005016005,001006005001001001007,001006005001001001019,001013002008,001018007007,001005018019,001005008002003015,001013,001015011001,001014001014,001013002005,001009009002,001018002070001,001010005018003,001010012005002,001013003009,001002001001,001015012002001002003,001005011,001018002044,001014001001,001005008002004006,001015012001002003003,001010010001,001010005004003,001006005002007006007,001018005020,001015012001002002,001005018029,001007002,001005001006,001006005002007004,001014001013,001006005002007006001,001010005002001,001018004032,001006005002003008,001010005009001,001018006004,001006005001001001006,001015014001004,001018001014,001006005003001,001006005001002001005,001010005003002,001018002068,001006005002008004,001006005002008002,001008005003,001018002048,001006005002003005,001018002071,001015014003002001,001014001004,001005008002004003,001014001006,001005018028,001009008001,001002003,001006005002004002,001005018016,001015012001001006001,001005016008,001005006009,001010005003001,001005006007,001016001003003,001006005001002001,001005018026,001006005001002001004,001018004028,001018004027,001006005002001014,001010005075002,001010005077002,001010005046002,001018005011,001009010003,001018002035,001005008002003017,001010005022002,001018005016,001018002057,001018004008,001006005001002001003,001018001008,001018001015,001005006015,001018005024,001018004002,001010005049001,001010015007,001005005003,001006005002004008,001018002040,001018002072,001006005002001010,001006005002003001,001005008002001017,001005008002002003,001005008002002021,001009011,001006005001001001018,001013003012,001006005002001015,001010002,001018002025,001018001006,001015012001001004002,001014001003,001005002016,001006005002006003,001018002069003,001015012001002003002,001010005024002,001005008002003011,001008001004,001005008002003014,001010005024001,001018002060,001010012005001,001009012,001005008002004005,001006005001001001003,001012008,001015014001003001,001015007,001006005002007001,001015012002001002003002,001006005002001006,001015012001002005001,001018002033,001005018017,001013006002,001006005002001009,001005009,001005018013,001015001,001018004010,001018002059,001011001001001,001006005002001023,001006005002007007,001005018009,001018002023,001018004014,001015014001004001,001006005001001001,001018006003,001005006012,001006005001002001014,001018002017,001018002051,001012005,001005006019,001006005002001013,001010012005003,001018002022,001005008002002015,001011005003,001006005002001012,001010005027002,001005008002002006,001005016009,001013002010,001010008002,001010005036003,001014003,001005008002003001,001018002062,001010005035001,001010005015001,001011001001003,001018002065,001006004002,001018007003,001006005002007003,001013004003,001005008002004002,001014001002,001006005002001002,001005006010,001006005002007006006,001006005002003004,001006004001,001005008002003004,001015012002001004,001006005001001001015,001006005001002001017,001015012003001,001018002001,001006005002001005,001016003,001005018032,001005006011,001005008002002020,001006005002001019,001005018004,001006005001001001001,001005018031,001005008002001023,001006005002004004,001016005001,001006005001002001009,001015012001002003001,001015014002002,001018003002,001009009001,001018005007,001005018018,001005006006,001010014001,001017002001003,001014001010001001,001018004031,001018002037,001005018036,001006005002001011,001015012001001006002,001015015001,001010005007001,001018002069001,001005013,001016001004002,001015006001,001006003,001006005001002001011,001010005037001,001018002034,001010005023001,001009014,001012004002,001018005009,001018005022,001011001001002,001005016012,001006005003006001,001005006014,001018004021,001005017008,001013003010,001010005028001,001018004024,001002001002,001015014001001,001010010,001018002050,001006005001002001010,001006005001001001016,001015011002,001005008002004007,001018001013003,001010005004001,001006005001002001012,001018005013,001010012,001018002038,001006005002008003,001005018021,001005002006,001012004,001013003008,001006005002001018,001005018007,001006005003003,001005006001,001006005001002001016,001005008002001019,001016006,001006005001001001014,001018005008,001005006018,001005001007,001018004026,001010005034001,001018002030,001015012001001003001,001006005002002002,001018004033,001010005008001,001015012002001006001,001013003011,001018007002,001005008002003018,001016001002,001005018008,001009015,001005018003,001010010002,001005006017,001010005023002,001010007001,001018003005,001009017002,001016006001001,001006005001002001013,001010005006001,001010005014001,001005008002003019,001018001013004002,001010005012001,001006005002004006,001018004034,001005008002003020,001005018027,001006005002001025,001005014,001013005002,001010005018001,001010005033001,001018005005,001013006004,001010015003,001018002046,001018002043,001015014002003,001015008,001010005028002,001006005002001016,001008001002,001018005006,001012005001,001005007004,001016006001003,001018005,001010005014002,001006002001,001006005001001001011,001005006013,001010005017001,001014001012,001018004030,001006005001002001015,001010005007002,001015014003001,001014001008,001018002003,001001008,001018004018,001015005,001006005002001022,001006005001001001020,001005008002003010,001018001010,001005006005,001010005016001,001018006002,001010005030001,001006005002001001,001013003002,001006005002006002,001016001004001,001005017009,001015011003,001011001001004001,001006005002007005,001013003007,001006005001002001002,001006005002001029,001016001001,001018002063,001006005002003003,001011005001,001015014002005,001005006008,001006005002007006004,001018002047,001005002014,001016006001002001,001014001010001,001006005002001017,001010003002001,001013002003,001006005002002001,001015010001,001015010001001,001018004005,001018004019,001015012001001005001,001010005044001,001018002036,001015014002001,001018004006,001015002,001018005003,001018002042,001005008002002002,001017001001,001010003001002,001018005012,001018001009,001010005011003,001005008001,001018002027,001005005001,001018002032,001018002015,001018004025,001005008002004008,001009010002,001006005003004,001005008002002018,001018004003,001001009001,001010008001,001009016001,001016001004003002,001014001007,001010005013001,001005008002004004,001006002002,001008001003,001010003002002,001010015006,001018005015,001018007005,001013007002,001005002017,001005018015,001018004001004,001005016,001006005002001004,001010005021001,001018007008,001005008002002019,001018003001,001015012001002004001,001018002021,001018002031,001018002052,001011005002,001006005001001001012,001010005036001,001018001005,001018002028,001018004035001,001006005002004009,001005006004,001018004001003,001018012,001012010001,001018004011,001005008002001015,001018001013004001,001015014001002,001005018023,001006005002003002,001015012002001002004001,001018003007,001010005049003,001015012002001002004002,001013006001,001006005002001028,001012005005,001006005001001001005,001015012001002004002,001006005001002001001,001005018025,001018002002,001010005008003,001006005003005,001006004003,001016001003001,001005018034,001006005002001026,001010012001,001010005040001,001011001001005,001006005001002001007,001013003005,001006005002004001,001018006001,001010005047001,001018002067,001010005036002,001001011,001010015002,001015009,001018003003,001006005001001001002,001005018001,001002001003,001005008002002001,001018005018,001015014002006,001018007006,001005008002001021,001005016007,001013006005,001018002024,001013002006,001015012002001001,001005016010,001006005002007006005,001015012002002002,001005002018,001005002013,001010005020001,001018002055,001017002001002,001010015003001,001005018011,001015011006,001010007002,001018004016,001005016011,001006005001002001006,001006005002004010,001018004003,001018002039,001005017001,001005002005,001018005004,001005018035,001005002007,001018002055,001005002011,001018002026,001008002,001018004007,001018005005,001018004023,001018007007,001018004021,001018002053,001009015,001005018003,001018004002,001005018007,001010012,001005018022,001018002057,001009014,001005007002,001009008,001018002073,001010012003,001005018014,001018004020,001018004016,001009013,001005018029,001005018011,001018004034,001018002059,001018004005,001005002010,001009003,001005016,001018001013,001005002013,001018005023,001018001008,001018005003,001005002016,001005018023,001005018018,001018002028,001018007006,001010014,001010012002,001018004014,001018002042,001018004017,001008003,001018004011,001005018006,001018002049,001005018005,001018002044,001018001013004,001018001015,001018002046,001018004008,001005018032,001018002069001,001005002014,001005018026,001001011,001018002024,001005008,001018005010,001010015,001005018021,001018004006,001009007,001005018033,001005002004,001018002030,001018002060,001008006,001018002033,001009001,001012010,001005018015,001009016,001018002047,001018003005,001018004032,001005002015,001018002041,001018002045,001018002037,001005018019,001018002031,001018002040,001018005006,001018005011,001018006002,001018004026,001018002072,001005005002,001005002001,001005018020,001005005003,001005018008,001018005007,001018004036,001018002067,001018004018,001009008003,001018002075,001018004009,001018002032,001018004001003,001005018034,001018002029,001018007003,001018004001,001018002034,001018005001,001008004,001018001007,001018001011,001005018036,001005007004,001005002002,001018002062,001010016,001018002025,001005018012,001018004022,001005002,001018003006,001005018002,001005002018,001018004015,001018004013,001018002036,001010013,001018004012,001018004035,001018007005,001018004027,001018005013,001018011001,001005002006,001012006002,001018002070,001008001,001018004010,001018006001,001005002012,001018002065,001018004029,001005018031,001018005020,001018001006,001005018030,001005002003,001018005018,001018004001001,001005018017,001018002061,001005018004,001018005014,001005002009,001005018001,001005018025,001018004028,001018005022,001005018013,001005018024,001010005001003,001010012001,001018002027,001005005001,001018002054,001018002043,001002001003,001018001013003,001005018,001018002063,001018001014,001018004004,001005018010,001018002035,001005018016,001018001019,001005002017,001008005,001018007008,001009004,001005018009,001018004030,001005018028,001018001012,001018002038,001018002058,001018004033,001018005002,001005018027,001005002008,001018002052,001012010001,001009002";
//		Map<String,String> map = new HashMap<>();
//		StringBuffer sbf = new StringBuffer(1024);
//		String[] levelCodeStr  = a.split(",");
//		for(int i=0;i<levelCodeStr.length;i++){
//			for( int j = levelCodeStr[i].length()/3 ; j>0; j--){
////				levelCodeString=levelCodeString+","+levelCodeStr[i].substring(0,j*3);
//				String b = levelCodeStr[i].substring(0,j*3);
//				if(map.containsKey(b)){
//					break;
//				}else{
//					sbf.append(b).append(",");
//					map.put("b","");
//				}
//			}
//		}
	}
}
