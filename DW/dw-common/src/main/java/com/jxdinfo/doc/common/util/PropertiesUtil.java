/*
 * PropertiesUtil.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类的用途：配置文件工具类<p>
 * 创建日期：2019年1月16日 <br>
 * 修改历史：<br>
 * 修改日期：2019年1月16日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public class PropertiesUtil {

    private Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static final int DEFAULT_PATH = 0;

    public static final int RELATIVE_PATH = 1;

    public static final int ABSOLUTE_PATH = 2;

    private static Object clockObj = PropertiesUtil.class;

    private static final String PROPERTY_FILE_PATH = "/";

    private static Map<String, PropertiesUtil> propertyUtilMap = new HashMap<String, PropertiesUtil>();

    private String filePath = null;

    private static Properties properties = null;

    private static Map<String, Properties> proMap = new HashMap<String, Properties>();

    private long modifyTime = 0L;

    private static boolean success = false;

    public PropertiesUtil(String propertyName, String path, int bln) {
        success = false;
        if (bln == DEFAULT_PATH)
            this.filePath = (getClassPath() + propertyName + ".properties");
        else if (RELATIVE_PATH == bln)
            this.filePath = (getClassPath() + path + "/" + propertyName + ".properties");
        else if (ABSOLUTE_PATH == bln) {
            this.filePath = (path + "/" + propertyName + ".properties");
        }
        InputStream instream = null;
        try {
            if ((this.filePath != null) && (!"".equals(this.filePath))) {
                File file = new File(this.filePath);
                if (file.exists()) {
                    instream = new FileInputStream(this.filePath);
                    properties = new Properties();
                    properties.load(instream);
                    proMap.put(propertyName, properties);
                    instream.close();

                    success = true;
                } else {
                    instream = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertyName + ".properties");
                    if (instream != null) {
                        properties = new Properties();
                        properties.load(instream);
                        proMap.put(propertyName, properties);
                        instream.close();

                        success = true;
                    } else {
                        this.log.error("属性文件不存在! filePath = " + this.filePath);
                    }
                }
            } else {
                this.log.info("属性文件路径为空!");
            }
        } catch (IOException e) {
            throw new RuntimeException("前置节点配置文件加载失败，" + e.getMessage());
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                    instream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getClassPath() {
        URL url = getClass().getResource(PROPERTY_FILE_PATH);
        String path = null;
        try {
            path = URLDecoder.decode(url.getPath(), "UTF-8");
        } catch (IOException localIOException) {
        }
        return path;
    }

    public static PropertiesUtil newInstance(String propertyName) {
        return initPropertyUtil(propertyName, "", 0);
    }

    public static PropertiesUtil newInstance(String propertyName, String path, int bln) {
        return initPropertyUtil(propertyName, path, bln);
    }

    private static PropertiesUtil initPropertyUtil(String propertyName, String path, int bln) {
        synchronized (clockObj) {
            PropertiesUtil propertyUtil = (PropertiesUtil) propertyUtilMap.get(propertyName);
            if (propertyUtil == null) {
                propertyUtil = new PropertiesUtil(propertyName, path, bln);
                if (success) {
                    propertyUtilMap.put(propertyName, propertyUtil);
                }

            }
            return propertyUtil;
        }
    }

    public static String getMsg(String key) {
        if (properties != null) {
            return properties.getProperty(key);
        }
        return null;
    }

    public static void setSnapMsg(String key, String value) {
        if (properties != null)
            properties.put(key, value);
    }

    public static Properties getSnapProps() {
        if (properties != null) {
            return properties;
        }
        return null;
    }

    public static void setSnapProps(Properties props) {
        properties = props;
    }

    public static String getMsg(String propertyName, String key) {
        if (proMap.get(propertyName) != null) {
            return ((Properties) proMap.get(propertyName)).getProperty(key);
        }
        return null;
    }

    public String getMsg(String key, Object[] objs) {
        if ((properties == null) || (properties.getProperty(key) == null)) {
            return null;
        }
        return MessageFormat.format(properties.getProperty(key), objs);
    }

    public void setMsg(String key, String value) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(this.filePath);
            properties.setProperty(key, value);
            properties.store(out, null);

        } catch (IOException e) {
            this.log.info("occur error when upate the property! filePath = " + this.filePath);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public static Map<String, PropertiesUtil> getPropertyUtilMap() {
        return propertyUtilMap;
    }

    protected long getModifyTime() {
        return this.modifyTime;
    }

    protected Properties getProperties() {
        return properties;
    }

}
