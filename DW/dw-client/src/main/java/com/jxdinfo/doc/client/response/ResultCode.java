/*
 * ResultCode.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.client.response;

import javax.servlet.http.HttpServletResponse;

/**
 * 类的用途：
 * <p>
 * 创建日期：2019年4月19日 <br>
 * 修改历史：<br>
 * 修改日期：2019年4月19日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * 
 * @author WangBinBin
 * @version 1.0
 */
public enum ResultCode implements IResultCode {

	/**
	 * 操作成功
	 */
	SUCCESS(HttpServletResponse.SC_OK, "操作成功"),

	/**
	 * 业务异常
	 */
	FAILURE(HttpServletResponse.SC_BAD_REQUEST, "业务异常"),

	/**
	 * 请求未授权
	 */
	UN_AUTHORIZED(HttpServletResponse.SC_UNAUTHORIZED, "请求未授权"),

	/**
	 * 404 没找到请求
	 */
	NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, "404 没找到请求"),

	/**
	 * 消息不能读取
	 */
	MSG_NOT_READABLE(HttpServletResponse.SC_BAD_REQUEST, "消息不能读取"),

	/**
	 * 不支持当前请求方法
	 */
	METHOD_NOT_SUPPORTED(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "不支持当前请求方法"),

	/**
	 * 不支持当前媒体类型
	 */
	MEDIA_TYPE_NOT_SUPPORTED(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "不支持当前媒体类型"),

	/**
	 * 请求被拒绝
	 */
	REQ_REJECT(HttpServletResponse.SC_FORBIDDEN, "请求被拒绝"),

	/**
	 * 服务器异常
	 */
	INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器异常"),

	/**
	 * 缺少必要的请求参数
	 */
	PARAM_MISS(HttpServletResponse.SC_BAD_REQUEST, "缺少必要的请求参数"),

	/**
	 * 请求参数类型错误
	 */
	PARAM_TYPE_ERROR(HttpServletResponse.SC_BAD_REQUEST, "请求参数类型错误"),

	/**
	 * 请求参数绑定错误
	 */
	PARAM_BIND_ERROR(HttpServletResponse.SC_BAD_REQUEST, "请求参数绑定错误"),

	/**
	 * 参数校验失败
	 */
	PARAM_VALID_ERROR(HttpServletResponse.SC_BAD_REQUEST, "参数校验失败"),

	/**
	 * 授权校验失败
	 */
	LICENSE_VALID_ERROR(423, "授权校验失败"),;

	/**
	 * code编码
	 */
	final int code;

	/**
	 * 中文信息描述
	 */
	final String message;

	/**
	 * @Title: getMessage
	 * @return
	 */
	@Override
	public String getMessage() {
		return message;
	}

	/**
	 * @Title: getCode
	 * @return
	 */
	@Override
	public int getCode() {
		return code;
	}

	private ResultCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
