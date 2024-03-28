package com.jxdinfo.doc.common.util;

import java.util.UUID;

/**
 * @author dushitaoyuan
 * 为了不依赖commonsio 自己重写工具类
 */
public final class FilenameUtils {

    /**
     * 默认构造方法
     * @Title:FilenameUtils
     */
    private FilenameUtils() {
    }

    private static final String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z" };

    /** 生成8位短id
     * @return 生成8位短id
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

    /**
     * filePattern
     */
    private static final String FILEPATTERN = "%s.%s";

    /** 从文件名获取扩展名
     * @param fileName 文件名称
     * @return String
     */
    public static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件前缀
     * @param fileName 文件名称
     * @return String
     */
    public static String getPrefix(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /** 防止从文件重名
     * @param fileName 文件名称
     * @return String
     */
    public static String getPrefixRandom(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf(".")) + "_" + generateShortUuid();
    }

    /**合并
     * @param prefix prefix
     * @param ext ext
     * @return String
     */
    public static String merge(String prefix, String ext) {
        return String.format(FILEPATTERN, prefix, ext);
    }


}
