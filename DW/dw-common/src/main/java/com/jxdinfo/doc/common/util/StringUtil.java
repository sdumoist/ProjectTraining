package com.jxdinfo.doc.common.util;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * String工具类
 * @author 王宁
 *
 */
public class StringUtil{
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replace("-","");
	}
	
	/**
	 * obj转为string
	 * @param obj
	 * @return
	 */
	public static String getString(Object obj){
		if (obj != null){
			return obj.toString();
		}
		return "";
	}
	
	/**
	 * obj转为int
	 * @param obj
	 * @return
	 */
	public static Integer getInteger(Object obj){
		if (obj != null){
			return Integer.valueOf(obj.toString());
		}
		return 0;
	}
	
	/**
	 * obj转为Double
	 * @param obj
	 * @return
	 */
	public static Double getDouble(Object obj){
		if (obj != null){
			return Double.valueOf(obj.toString());
		}
		return 0d;
	}
	
	/**
	 * obj转为Boolean
	 * @param obj
	 * @return
	 */
	public static Boolean getBoolean(Object obj){
		if (obj != null){
			return Boolean.valueOf(obj.toString());
		}
		return false;
	}
	
	public static boolean checkIsEmpty(String str){
		return (str == null || "".equals(str) || "null".equals(str));
	}
	
	/**
	 * 处理查询参数中的 % 及 _
	 * @param paramStr
	 * @return
	 */
	public static String transferSqlParam(String paramStr){
		if (paramStr == null){
			return null;
		}
		return paramStr.replaceAll("\\%", "\\\\%").replaceAll("\\_", "\\\\_");
	}

	/**
	 * 处理字符串中特殊字符
	 * @param str
	 * @return
	 */
	public static String transferSpecialChar(String str) {
		if (str == null) {
			return str;
		} else if ("".equals(str)) {
			return str;
		} else {
			return str.replaceAll("\\+", "%20").replaceAll("%28", "\\(").replaceAll("%29", "\\)").replaceAll("%3B", ";")
					.replaceAll("%40", "@").replaceAll("%23", "\\#").replaceAll("%26", "\\&").replaceAll("%2C", "\\,")
					.replaceAll("%2B", "\\+").replaceAll("%7D", "\\}").replaceAll("%7B", "\\{").replaceAll("%24", "\\$")
					.replaceAll("%5E", "\\^").replaceAll("%25", "\\%").replaceAll("%7E", "\\~").replaceAll("%3D", "\\=")
					.replaceAll("%60", "\\`");
		}
	}

	/**
	 * @author luzhanzhao
	 * @date 2018-12-10
	 * @description 将字符串处理为hash加密
	 * @param input 要加密的字符串
	 * @return 加密之后的字符串a
	 */
	public static String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @author luzhanzhao
	 * @date 2018-12-12
	 * @description 根据所需长度获取随机码
	 * @param n 随机码长度
	 * @return 随机码
	 */
	public static String getRandomCode( int n ) {
		String val = "";
		Random random = new Random();
		for ( int i = 0; i < n; i++ )
		{
			String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
			if ( "char".equalsIgnoreCase( str ) )
			{ // 产生字母
				int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
				// System.out.println(nextInt + "!!!!"); 1,0,1,1,1,0,0
				val += (char) ( nextInt + random.nextInt( 26 ) );
			}
			else if ( "num".equalsIgnoreCase( str ) )
			{ // 产生数字
				val += String.valueOf( random.nextInt( 10 ) );
			}
		}
		return val;
	}

    /**
     * @author luzhanzhao
     * @date 2018-12-13
     * @description 将字符串转换成时间
     * @param dateString 要转换的字符串
     * @return 转换得到的日期
     */
	public static Date stringToDate(String dateString){
		try{
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = sdf.parse(dateString);
			return date;
		}catch(ParseException e){
			System.out.println(e.getMessage());
			return null;
		}
	}

	/**
	 * @author luzhanzhao
	 * @date 2018-12-25
	 * @description 将字符串的首字母转化为大写
	 * @param name 待转化字符串
	 * @return 转化为首字母大写的字符串
	 */
	public static String captureName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}

	/**
	 * @author luzhanzhao
	 * @date 2019-01-04
	 * @description 判断文件是否为文档
	 * @param docType 文件类型
	 * @return true：是文档，false：不是文档
	 */
	public static boolean isPdf(String docType){
		docType = docType.replace(".","");
		switch (docType){
			case "doc": case "docx": case "dot": case "xls": case "wps": case "xlt": case "et":
			case "ett": case "ppts": case "pot": case "dps": case "dpt": case "xlsx": case "txt":
			case "pdf": case "ceb": case "ppt": case "pptx": case "ppsx": case "pps": case "dotx":
			case "rtf":case "tif":
				return true;
				default:
					return false;
		}
	}

}
