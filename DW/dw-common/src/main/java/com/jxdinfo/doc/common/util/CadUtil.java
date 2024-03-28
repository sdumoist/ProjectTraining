package com.jxdinfo.doc.common.util;

import java.io.File;

/**
 * cad 图纸文件转PDF
 */
public class CadUtil extends ConvertUtil {

    private static final String OPEN = "1";

    /**
     * CAD图纸转为PDF
     * 目前支持格式：dwg，dwf，dxf
     *
     * @param inputFilePath  CAD图纸文件
     * @param outputFilePath 转换后的PDF文件
     */
    public synchronized static void cad2Pdf(File inputFilePath, File outputFilePath) {
        if (OPEN.equals(prop.getProperty("cad.convert.switch"))) {
            String jarPath = "";
            if (isOSWindows()) {
                jarPath = prop.getProperty("cad.jarWin.dir") + "cad2pdf.jar";
            } else {
                jarPath = prop.getProperty("cad.jarLinux.dir") + "cad2pdf.jar";
            }
            if(!new File(jarPath).exists()){
                return;
            }
            String sourcePath = inputFilePath.getAbsolutePath();
            String targetPath = outputFilePath.getAbsolutePath();
            String width = "1600";
            String height = "1600";
            try {
                ProcessUtils.execute(15000, "java", "-jar", jarPath, sourcePath, targetPath, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
