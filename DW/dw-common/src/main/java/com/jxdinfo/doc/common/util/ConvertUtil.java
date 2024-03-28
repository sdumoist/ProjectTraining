package com.jxdinfo.doc.common.util;

import org.csource.common.IniFileReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConvertUtil {
    protected static Properties prop = null;

    static {
        prop = getProperties("convert.properties");
    }

    /**
     * 获取配置文件
     *
     * @param propsFilePath 配置文件路径
     * @return 配置信息
     */
    public static Properties getProperties(String propsFilePath) {
        Properties props = new Properties();
        InputStream in = IniFileReader.loadFromOsFileSystemOrClasspathAsStream(propsFilePath);
        try {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static boolean isOSWindows() {
        Properties prop = System.getProperties();
        String os = prop.getProperty("os.name");
        System.out.println("====== current os ：" + os + " =====");
        return os != null && os.toLowerCase().contains("win");
    }
}
