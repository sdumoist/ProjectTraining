package com.jxdinfo.doc.common.util;

/**
 * @Author 阁楼麻雀
 * @Date 2016-7-4 11:59
 * @Desc 异常信息处理类
 */

public class ExceptionUtils {

	protected ExceptionUtils() {
	}

	/**
	 * 获取错误信息
	 * 
	 * @param //stackTraceElement
	 *            = ExceptionUtils.getThread().getStackTrace()
	 * @param e
	 *            = Exception
	 *            ExceptionUtils.getErrorInfo(ExceptionUtils.getThread().
	 *            getStackTrace(), e)
	 */
	public static String getErrorInfo(Exception e) {
		String errCont = "";
		if (e != null) {
		    StackTraceElement[] stackTraceElement = e.getStackTrace();
			errCont += "异常信息：" + e + "\n";
			if (stackTraceElement != null && stackTraceElement.length > 1) {
			    errCont += "异常位置：";
			    for (int i = 0; i < stackTraceElement.length; i++) {
			        if (stackTraceElement[i].toString().indexOf("Unknown Source") < 0) {
			            errCont += stackTraceElement[i];
			            break;
			        }
			    }
			}
		}
		return errCont;
	}
}