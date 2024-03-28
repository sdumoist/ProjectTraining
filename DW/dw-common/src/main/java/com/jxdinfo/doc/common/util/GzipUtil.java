package com.jxdinfo.doc.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 *
 * @author wenqi5
 *
 */
public class GzipUtil {

    public static final String GZIP_ENCODE_UTF_8 = "UTF-8";

    /**
     * 字符串进行Gzip压缩
     * @Title: gzip
     * @param primStr
     * @return String
     */
    public static String gzip(String primStr)
    {
        if (primStr == null || primStr.length() == 0)
        {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try
        {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes(GZIP_ENCODE_UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (gzip != null)
            {
                try
                {
                    gzip.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return  new BASE64Encoder().encode(out.toByteArray());
    }

    /**
     * <p>
     * Description:使用gzip进行解压缩
     * </p>
     *
     * @param compressedStr
     * @return
     */
    public static String gunzip(String compressedStr)
    {
        if (compressedStr == null)
        {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            compressed = decoder.decodeBuffer(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1)
            {
                out.write(buffer, 0, offset);
            }
            decompressed = new String(out.toByteArray(), GZIP_ENCODE_UTF_8);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (ginzip != null)
            {
                try
                {
                    ginzip.close();
                }
                catch (IOException e)
                {
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return decompressed;
    }
}