/*
 * LibreOffice.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.common.util;

import org.jodconverter.OfficeDocumentConverter;
import org.jodconverter.office.DefaultOfficeManagerBuilder;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/**
 * 类的用途：<p>
 * 创建日期：2020年1月13日 <br>
 * 修改历史：<br>
 * 修改日期：2020年1月13日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 *
 * @author WangBinBin
 * @version 1.0
 */
public class LibreOfficePDFConvert {


    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();
    private final static Logger logger = LoggerFactory.getLogger(LibreOfficePDFConvert.class);
    private static String officeHomeDir = null;
    private static Environment environment = appCtx.getBean(Environment.class);

    /**
     * @param sourceFile 需要转换的原文件
     * @param tarPdfFile 转换后的目标pdf文件
     * @return
     * @throws OfficeException
     * @name 文档转换为pdf工具类
     * @description 相关说明 支持：xls，xlsx，ppt，pptx，txt，其中doc，docx转换与原文件有较大差异,libreOffice 默认安装路径
     * Linux：/opt/libreoffice6.0
     * Windows：C:/Program Files (x86)/LibreOffice
     * Mac：/Application/openOfficeSoft
     * @time 创建时间:2018年9月17日下午1:49:18
     * @author myflea@163.com
     * @history 修订历史（历次修订内容、修订人、修订时间等）
     */
    public synchronized static String doDocToFdpLibre(File sourceFile, File tarPdfFile) throws OfficeException {
        File inputFile = sourceFile;
        officeHomeDir = environment.getProperty("docbase.officeHomeDir");
        ;
        String libreOfficePath = getOfficeHome();

        DefaultOfficeManagerBuilder builder = new DefaultOfficeManagerBuilder();
        builder.setOfficeHome(new File(libreOfficePath));
        // 端口号
        builder.setPortNumber(8100);
        // 设置任务执行超时为50分钟
        builder.setTaskExecutionTimeout(1000 * 60 * 50L);
        // 设置任务队列超时为240小时
        builder.setTaskQueueTimeout(1000 * 60 * 60 * 240L);

        OfficeManager officeManager = builder.build();
        File outputFile =null;
        try {
            startService(officeManager);
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
             outputFile = tarPdfFile;
            converter.convert(inputFile, outputFile);
            stopService(officeManager);
        } catch (Exception e) {
            if(officeManager.isRunning()){
                stopService(officeManager);
            }
        }

        String pdfPath = outputFile.getPath();
        return pdfPath;
    }

    private static String getOfficeHome() {

        if (null != officeHomeDir) {
            return officeHomeDir;
        } else {
            String osName = System.getProperty("os.name");
            if (Pattern.matches("Windows.*", osName)) {
                officeHomeDir = "C:\\Program Files\\LibreOffice";
                return officeHomeDir;
            } else if (Pattern.matches("Linux.*", osName)) {
                officeHomeDir = "/opt/libreoffice6.0";
                return officeHomeDir;
            } else if (Pattern.matches("Mac.*", osName)) {
                officeHomeDir = "/Application/openOfficeSoft";
                return officeHomeDir;
            }
            return null;
        }

    }

    private static void stopService(OfficeManager officeManager) throws OfficeException {
        if (null != officeManager) {
            officeManager.stop();
        }
        logger.info("关闭office转换成功!");
    }

    private static void startService(OfficeManager officeManager) {

        try {
            // 准备启动服务
            officeManager.start(); // 启动服务
            logger.info("office转换服务启动成功");
        } catch (Exception ce) {
            logger.error("office转换服务启动失败!详细信息:{}", ce);
        }
    }

    /**
     * @param officeHome
     * @name 设置libreOffice安装目录
     * @description 相关说明：如果libreOffice安装目录为默认目录，则不需要设置，否则需要设置
     * @time 创建时间:2018年9月17日下午1:52:36
     * @author 作者
     * @history 修订历史（历次修订内容、修订人、修订时间等）
     */
    public static void setOfficeHome(String officeHome) {
        officeHomeDir = officeHome;
    }

    /**
     * 测试的方法
     *
     * @param args
     */
    public static void main(String[] args) {
        // 使用Files类遍历图片文件夹的文件
        Path path = Paths.get("D:\\pdfconvert\\printsource");

        File file1 = new File("C:\\Users\\Administrator\\Desktop\\1.xlsx");
        File file2 = new File("C:\\Users\\Administrator\\Desktop\\1.html");
        File file3 = new File("C:\\Users\\Administrator\\Desktop\\1.pdf");
        try {
            doDocToFdpLibre(file1, file2);
        } catch (OfficeException e) {
            e.printStackTrace();
        }
        try {
            doDocToFdpLibre(file2, file3);
        } catch (OfficeException e) {
            e.printStackTrace();
        }
    }


}
