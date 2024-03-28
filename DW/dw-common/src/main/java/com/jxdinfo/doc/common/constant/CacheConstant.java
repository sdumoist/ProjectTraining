package com.jxdinfo.doc.common.constant;

public interface CacheConstant {
	/** 组件预览  */
	public final static String COMPONENT_VIEW_NUM_CACHENAME = "COMPONENTVIEW";
	public final static String PREX_COMPONENT_VIEW_NUM_CACHENAME = "COMPONENTVIEW:";

	/** 文档预览  */
	public final static String DOC_VIEW_NUM_CACHENAME = "DOCVIEWNUM";
	public final static String PREX_DOC_VIEW_NUM_CACHENAME = "DOCVIEWNUM:";

	/** 专题预览  */
	public final static String TOPIC_VIEW_NUM_CACHENAME = "TOPICVIEWNUM";
	public final static String PREX_TOPIC_VIEW_NUM_CACHENAME = "TOPICVIEWNUM:";

	/** mp4预览   读锁*/
	public final static String MP4_PREVIEW_READ_LOCK = "MP4READLOCK";
	public final static String MP4_PREVIEW_READ_LOCK_FLAG = "READING";
	/** mp4预览   读锁*/
	public final static String MP3_PREVIEW_READ_LOCK = "MP3READLOCK";
	public final static String MP3_PREVIEW_READ_LOCK_FLAG = "MP3READING";
	/** 部门已用空间  */
	public final static String DEPT_USED_SPACE_CACHENAME = "DEPTUSEDSPACE";
	public final static String PREX_DEPT_USED_SPACE_CACHENAME = "DEPTUSEDSPACE:";

	/** 个人已用空间  */
	public final static String EMP_USED_SPACE_CACHENAME = "EMPUSEDSPACE";
	public final static String PREX_EMP_USED_SPACE_CACHENAME = "EMPUSEDSPACE:";

	/** 文件拖拽上传  */
	public final static String FILE_UPLOAD = "file_upload";

	public final static String DOC_TOTAL_COUNT = "DOCTOTALCOUNT";
	public final static String PREX_DOC_TOTAL_COUNT = "DOC_TOTAL_COUNT:";

	public final static String TOPIC_DOC_LIST = "TOPICDOCLIST";
	public final static String PREX_TOPIC_DOC_LIST = "TOPICDOCLIST:";

	public final static String HOT_DOC_LIST = "HOTDOCLIST";
	public final static String PREX_HOT_DOC_LIST = "HOTDOCLIST:";

	public final static String UPLOAD_DATA_LIST = "UPLOADDATALIST";
	public final static String PREX_UPLOAD_DATA_LIST = "UPLOADDATALIST:";

	public final static String FILE_LEVEL_CODE = "FILELEVELCODE";
	public final static String PREX_FILE_LEVEL_CODE = "FILELEVELCODE:";

	public final static String USER_LEVEL_CODE = "USERLEVELCODE";
	public final static String PREX_USER_LEVEL_CODE = "USERLEVELCODE:";


	public final static String USER_LEVEL_CODE_UPLOAD = "USERLEVELCODEUPLOAD";
	public final static String PREX_USER_LEVEL_CODE_UPLOAD = "USERLEVELCODEUPLOAD:";

	public final static String UP_LEVEL_CODE = "UPLEVELCODE";
	public final static String PREX_UP_LEVEL_CODE = "UPLEVELCODE:";
	public final static String UP_LEVEL_CODE_UPLOAD = "UPLEVELCODEUPLOAD";
	public final static String PREX_UP_LEVEL_CODE_UPLOAD= "UPLEVELCODEUPLOAD:";

	/** 图片检索类型 **/
	public final static String DIC_DATA_LIST = "DICDATALIST";
	public final static String PREX_DIC_DATA_LIST = "DICDATALIST";

	/** 积分规则配置 **/
	public final static String RULE_CODE_LIST = "RULECODELIST";
	public final static String PREX_RULE_CODE_LIST = "RULECODELIST";

	/** 服务器地址配置 **/
	public final static String SERVER_ADDRESS = "SERVERADDRESS";
	public final static String PREX_SERVER_ADDRESS = "SERVERADDRESS";

	/** 待转化pdf列表 **/
	public final static String READY_TO_PDF_LIST = "READYTOPDFLIST";
	public final static String PREX_READY_TO_PDF_LIST = "READYTOPDFLIST";

	/** 待创建es列表 **/
	public final static String READY_TO_ES_LIST = "READYTOESLIST";
	public final static String PREX_READY_TO_ES_LIST = "READYTOESLIST";

	/** 判断待转化列表是否未空 **/
	public final static String IS_NULL_TO_CHANGE = "ISNULLTOCHANGE";
    public final static String PREX_IS_NULL_TO_CHANGE = "ISNULLTOCHANGE";

    /** fastDFS的启动状态 **/
    public final static String FASTDFS_USING_FLAG = "FASTDFSUSINGFLAG";
    public final static String PREX_FASTDFS_USING_FLAG = "FASTDFSUSINGFLAG";

    /** 准备删除文件的列表 **/
    public final static String READY_DELETE_LIST = "READYDELETELIST";
    public final static String PREX_READY_DELETE_LIST = "READYDELETELIST";

    /** 快速转化pdf列表 **/
    public final static String FAST_CHANGE_LIST = "FASTCHANGELIST";
    public final static String PREX_FAST_CHANGE_LIST = "FASTCHANGELIST";

    /** 上传状态列表 **/
    public final static String UPLOAD_STATE_LIST = "UPLOADSTATELIST";
    public final static String PREX_UPLOAD_STATE_LIST = "UPLOADSTATELIST";

	/** pdf路径 **/
	public final static String PDF_PATH_DIR = "PDFPATHDIR";
	public final static String PREX_PDF_PATH_DIR = "PDFPATHDIR";

	/** 加密后的pdf路径 **/
	public final static String PDF_KEY_PATH_DIR = "PDFKEYPATHDIR";
	public final static String PREX_PDF_KEY_PATH_DIR = "PDFKEYPATHDIR";
}
