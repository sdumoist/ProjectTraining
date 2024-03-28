/*
 * ApiResponse.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.client.response;

import java.io.Serializable;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.jxdinfo.doc.client.response.constant.ApiConstant;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 类的用途：统一API响应结果封装<p>
 * 创建日期：2019年4月19日 <br>
 * 修改历史：<br>
 * 修改日期：2019年4月19日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
@ApiModel(description = "返回信息")
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态码", required = true)
    private int code;

    @ApiModelProperty(value = "是否成功", required = true)
    private boolean success;

    @ApiModelProperty(value = "承载数据")
    private T data;

    @ApiModelProperty(value = "返回消息", required = true)
    private String msg;

    private ApiResponse(IResultCode resultCode) {
        this(resultCode, null, resultCode.getMessage());
    }

    private ApiResponse(IResultCode resultCode, String msg) {
        this(resultCode, null, msg);
    }

    private ApiResponse(IResultCode resultCode, T data, String msg) {
        this(resultCode.getCode(), data, msg);
    }
    public ApiResponse(){

    }
    private ApiResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = ResultCode.SUCCESS.code == code;
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isSuccess(@Nullable ApiResponse<?> result) {
        return Optional.ofNullable(result).map(x -> ObjectUtils.nullSafeEquals(ResultCode.SUCCESS.code, x.code))
                .orElse(Boolean.FALSE);
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isNotSuccess(@Nullable ApiResponse<?> result) {
        return !ApiResponse.isSuccess(result);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param <T>  T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> data(T data) {
        return data(data, ApiConstant.DEFAULT_SUCCESS_MESSAGE);
    }

    /**
     * 返回R
     *
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> data(T data, String msg) {
        return data(HttpServletResponse.SC_OK, data, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param code 状态码
     * @param data 数据
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> data(int code, T data, String msg) {
        return new ApiResponse<>(code, data, data == null ? ApiConstant.DEFAULT_NULL_MESSAGE : msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(String msg) {
        return new ApiResponse<>(ResultCode.SUCCESS, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param resultCode 业务代码
     * @param <T>        T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(IResultCode resultCode) {
        return new ApiResponse<>(resultCode);
    }

    /**
     * 返回ApiResponse
     *
     * @param resultCode 业务代码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(IResultCode resultCode, String msg) {
        return new ApiResponse<>(resultCode, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param msg 消息
     * @param <T> T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(String msg) {
        return new ApiResponse<>(ResultCode.FAILURE, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param code 状态码
     * @param msg  消息
     * @param <T>  T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(int code, String msg) {
        return new ApiResponse<>(code, null, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param resultCode 业务代码
     * @param <T>        T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(IResultCode resultCode) {
        return new ApiResponse<>(resultCode);
    }

    /**
     * 返回ApiResponse
     *
     * @param resultCode 业务代码
     * @param msg        消息
     * @param <T>        T 泛型标记
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> fail(IResultCode resultCode, String msg) {
        return new ApiResponse<>(resultCode, msg);
    }

    /**
     * 返回ApiResponse
     *
     * @param flag 成功状态
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> status(boolean flag) {
        return flag ? success(ApiConstant.DEFAULT_SUCCESS_MESSAGE) : fail(ApiConstant.DEFAULT_FAILURE_MESSAGE);
    }

    
    /**
     * code 的getter方法
     * @return the code
     */
    public int getCode() {
        return code;
    }

    
    /**
     * code 的setter方法
     * @param code the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

    
    /**
     * success 的getter方法
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    
    /**
     * success 的setter方法
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    
    /**
     * data 的getter方法
     * @return the data
     */
    public T getData() {
        return data;
    }

    
    /**
     * data 的setter方法
     * @param data the data to set
     */
    public void setData(T data) {
        this.data = data;
    }

    
    /**
     * msg 的getter方法
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    
    /**
     * msg 的setter方法
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    
    /**
     * serialversionuid 的getter方法
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

}
