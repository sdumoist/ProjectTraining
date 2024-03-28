package com.jxdinfo.doc.common.util;

import com.jxdinfo.doc.common.constant.DocConstant;

import java.util.List;

/**
 * 公共工具类
 *
 * @author zhangzhen
 * @date 2018/1/30
 */
public class CommonUtil {

	/**
	 * 获取角色标识
	 * 
	 * @return 1：超级管理 ,文库管理 2:团队管理 3:普通用户
	 */
	public static Integer getAdminFlag(List<String> roleList) {
		Integer adminFlag = 3;
		for (int i = 0; i < roleList.size(); i++) {
		//TODO 此处需要继续优化处理，写死的方式不合适

			if (DocConstant.ROLEID.WKUSER.getValue().equals(roleList.get(i))
					|| DocConstant.ROLEID.SUPERUSER.getValue().equals(roleList.get(i))) {
				adminFlag = 1;
				break;
			}
			if(DocConstant.ROLEID.JJFZR.getValue().equals(roleList.get(i))){
				adminFlag = 4;
			}

		}
		return adminFlag;
	}
	public static Integer getWYHFlag(List<String> roleList) {
		Integer adminFlag = 3;
		Integer flag=0;
		for (int i = 0; i < roleList.size(); i++) {
			//TODO 此处需要继续优化处理，写死的方式不合适

			if (DocConstant.ROLEID.WYH.getValue().equals(roleList.get(i)))
			{
				adminFlag = 4;

				flag=flag+1;

			}
			if (DocConstant.ROLEID.FZR.getValue().equals(roleList.get(i)))
			{
				adminFlag = 5;
				flag=flag+1;
			}
			if (DocConstant.ROLEID.WKUSER.getValue().equals(roleList.get(i))
					|| DocConstant.ROLEID.SUPERUSER.getValue().equals(roleList.get(i)))
			{
				adminFlag = 6;
				break;
			}

		}
		if(flag==2){
			adminFlag=6;
		}
		return adminFlag;
	}

	/**
	 * 获取当前登录用户角色标识
	 * 
	 * @return 1：超级管理 ,文库管理 2:团队管理 3:普通用户
	 */
	public static Integer getAdminFlag() {
		List<String> roleList = UserInfoUtil.getCurrentUser().getRolesList();
		return getAdminFlag(roleList);
	}
	public static Integer getZTFlag(List<String> roleList) {
		Integer adminFlag = 0;
		Integer flag=0;
		for (int i = 0; i < roleList.size(); i++) {
			//TODO 此处需要继续优化处理，写死的方式不合适

			if (DocConstant.ROLEID.ZTWYH.getValue().equals(roleList.get(i)))
			{
				adminFlag = 7;
			}

		}
		return adminFlag;
	}
}
