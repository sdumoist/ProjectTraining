package com.jxdinfo.doc.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 类的用途：CodeUtil
 * 创建日期：2018年7月7日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月7日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public final class CodeUtil {

    /**
     * 日期格式
     */
    public static final String LOCATE_DATE_FORMAT = "yyyyMMddHHmmss";

    /** 
     * @Title:CodeUtil 
     */
    private CodeUtil() {
        super();
    }

    /**
     * 获取日期加随机ID
     * @Title: getKid 
     * @author: XuXinYing
     * @return String
     */
    public static String getKid() {
        String kid = generateShortUuid();
        kid = getCurrentDateTime() + kid;
        return kid;
    }

    /**
     * getKKid
     * @Title: getKKid 
     * @author: XuXinYing
     * @return String
     */
    public static String getKKid() {
        String kid = generateShortUuid();
        kid = "k" + getCurrentDateTime() + kid;
        return kid;
    }

    /**
     * ud
     * @Title: getUUID 
     * @author: XuXinYing
     * @return String
     */
    public static String getUUID() {
        String kid = generateShortUuid();
        kid = getCurrentDateTime() + "ud" + kid;
        return kid;
    }

    /**
     * isUUID
     * @Title: isUUID 
     * @author: XuXinYing
     * @param uuid
     * @return String
     */
    public static boolean isUUID(String uuid) {
        if (uuid != null) {
            if (uuid.length() == 24) {
                final String time = uuid.substring(0, 14);
                try {
                    parseDate(time, LOCATE_DATE_FORMAT);
                    if ("ud".equals(uuid.substring(14, 16))) {
                        return true;
                    }
                } catch (final Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * chars
     */
    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z" };

    /**
     * generateShortUuid
     * @Title: generateShortUuid 
     * @author: XuXinYing
     * @return String
     */
    public static String generateShortUuid() {
        final StringBuffer shortBuffer = new StringBuffer();
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            final String str = uuid.substring(i * 4, i * 4 + 4);
            final int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    public static String getCurrentDateTime() {
        return formatDate(new Date(), LOCATE_DATE_FORMAT);
    }

    /**
     * 日期转换
     * @Title: parseDate 
     * @author: XuXinYing
     * @param date 日期
     * @param pattern 日期格式
     * @return Date
     * @throws ParseException 异常
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(date);
    }

    /**
     * 日期格式化
     * @Title: formatDate 
     * @author: XuXinYing
     * @param date 日期
     * @param pattern 日期格式
     * @return String
     */
    public static String formatDate(Date date, String pattern) {
        final SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

}
