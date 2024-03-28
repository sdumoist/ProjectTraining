package com.jxdinfo.doc.common.util;

import org.springframework.boot.SpringApplication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * MD5加密工具类
 * @author
 * @Date 2018/4/2 0002
 */
public class MD5Util {
    private MD5Util() {
    }

    /**
     * 对字符串md5加密(小写字母+数字)
     *
     * @param str 传入要加密的字符串
     * @return  MD5加密后的字符串
     */
    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 加密解密算法 执行一次加密，两次解密
     */
    public static String convertMD5(String inStr){

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++){
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }

    /**
     * 对字符串md5加密(大写字母+数字)
     *
     * @param s 传入要加密的字符串
     * @return  MD5加密后的字符串
     */

    public static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String md5(File file) {
        MessageDigest digest = null;
        FileInputStream fis = null;
        byte[] buffer = new byte[1024];

        try {
            if (!file.isFile()) {
                return "";
            }

            digest = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);

            while (true) {
                int len;
                if ((len = fis.read(buffer, 0, 1024)) == -1) {
                    fis.close();
                    break;
                }

                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger var5 = new BigInteger(1, digest.digest());
        return String.format("%1$032x", new Object[]{var5});
    }
    /**
     * 获取文件的MD5
     * @param inputStream
     * @return
     */
    public  static String getFileMD5(InputStream inputStream) {
        String MD5 = "";
        try{
            BufferedInputStream fis = new BufferedInputStream(inputStream);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024*8];
            int length = -1;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            BigInteger bigInt = new BigInteger(1, md.digest());
            MD5 = bigInt.toString(16);
        }catch (Exception e){
            e.printStackTrace();
        }
        return MD5;
    }

    public static void main(String[] args) throws Exception {
        String a=getMD5New("duwei123");
        System.out.println(a);
    }
    public static String toMD5(String plainText) {
              StringBuffer buf = new StringBuffer("");
                try {
                        // 生成实现指定摘要算法的 MessageDigest 对象。
                        MessageDigest md = MessageDigest.getInstance("MD5");
                        // 使用指定的字节数组更新摘要。
                      md.update(plainText.getBytes());
                         // 通过执行诸如填充之类的最终操作完成哈希计算。
                         byte b[] = md.digest();
                        // 生成具体的md5密码到buf数组(32位小写)
                        int i;

            for (int offset = 0; offset < b.length; offset++) {
                                 i = b[offset];
                                if (i < 0){
                                        i += 256;
                                    }
                               if (i < 16){
                                        buf.append("0");
                                     }else{
                                        buf.append(Integer.toHexString(i));
                                    }
                             }
                     } catch (Exception e) {
                         e.printStackTrace();
                    }
                 return buf.toString();
           }
    public static String getMD5New(String str) throws Exception {
              /** 创建MD5加密对象 */
                 MessageDigest md5 = MessageDigest.getInstance("MD5");
                /** 进行加密 */md5.update(str.getBytes("GBK"));
        /** 获取加密后的字节数组 */
                byte[] md5Bytes = md5.digest();
                 String res = "";
                 for (int i = 0; i < md5Bytes.length; i++) {
                         int temp = md5Bytes[i] & 0xFF;
                        if (temp <= 0XF) { // 转化成十六进制不够两位，前面加零
                                 res += "0";
                            }
                        res += Integer.toHexString(temp);
                   }
                return res;
            }
}
