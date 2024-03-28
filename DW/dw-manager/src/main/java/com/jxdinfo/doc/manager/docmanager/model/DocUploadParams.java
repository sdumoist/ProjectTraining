package com.jxdinfo.doc.manager.docmanager.model;

import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serializable;


/**
 * 文档上传参数类
 * @author wangning
 * @since  2018-09-19 add by wangning
 */
public class
DocUploadParams extends Model<DocUploadParams> {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** 文档ID  */
	private String docId;

	/** 上传数据  */
	private String uploadData;
	
	/** 目录ID */
	private String foldId;
	
	/** 可下载标识  */
	private String downloadAble;
	
	/** 文件状态 */
	private String visible;
	
	/** 群组权限 */
	private String group;
	
	/** 人员权限 */
	private String person;
	
	/** 用户水印 （1:添加; 0:不添加）*/
	private String watermarkUser;
	
	/** 公司水印 */
	private String watermarkCompany;
	

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getUploadData() {
		return uploadData;
	}

	public void setUploadData(String uploadData) {
		this.uploadData = uploadData;
	}

	public String getFoldId() {
		return foldId;
	}

	public void setFoldId(String foldId) {
		this.foldId = foldId;
	}

	public String getDownloadAble() {
		return downloadAble;
	}

	public void setDownloadAble(String downloadAble) {
		this.downloadAble = downloadAble;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getWatermarkUser() {
		return watermarkUser;
	}

	public void setWatermarkUser(String watermarkUser) {
		this.watermarkUser = watermarkUser;
	}

	public String getWatermarkCompany() {
		return watermarkCompany;
	}

	public void setWatermarkCompany(String watermarkCompany) {
		this.watermarkCompany = watermarkCompany;
	} 

	@Override
	protected Serializable pkVal() {
		return docId;
	}

	
}
