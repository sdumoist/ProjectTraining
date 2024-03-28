/*
package com.jxdinfo.doc.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.Document;



*/
/**
 *
 * 使用的aspose包把文档转换成pdf工具类
 * @author dwm
 *2019-07-03
 *//*

public  class AsposeUtil {



    */
/**
     * 获取pdf文档的页数
     * @param pdfFilePath
     * @return
     *//*

    public static int getPdfPage(String pdfFilePath){
        try{
            // 判断输入文件是否存在
            File file = new File(pdfFilePath);
            if (!file.exists()) {
                return 0;
            }



            PDDocument pdf = PDDocument.load(file);
            return pdf.getPages().getCount();		//返回页数

        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    */
/**
     * 使用aspose转换成pdf文件
     * @param inputFile
     * @param pdfFile
     * @return
     *//*

    public static boolean convertToPdfAsAspose(String inputFile, String pdfFile) {
        int pointIndex = inputFile.lastIndexOf(".");
        // 后缀名
        String suffix = inputFile.substring(pointIndex).toLowerCase();
        File file = new File(inputFile) ;
        if(!file.exists()) {
            return false ;
        }
        if("pdf".equalsIgnoreCase(suffix)) {
            return false ;
        }

        //根据不同的文件转换成pdf文件
        if(".doc".equalsIgnoreCase(suffix) || ".docx".equalsIgnoreCase(suffix) || ".txt".equalsIgnoreCase(suffix)) {
            return doc2pdf(inputFile,pdfFile) ;
        } else if(".xls".equalsIgnoreCase(suffix) || ".xlsx".equalsIgnoreCase(suffix)) {
            return excel2Pdf(inputFile, pdfFile);
        } else if(".ppt".equalsIgnoreCase(suffix) || ".pptx".equalsIgnoreCase(suffix)) {
             ppt2pdf(inputFile, pdfFile);
             return true;
        }  else {
            return false;
        }
    }

    */
/**
     * aspose.word包获取配置
     * @return
     *//*

    public static boolean getWordLicense() {
        boolean result = false;
        InputStream is = null ;
        try {
            is = AsposeUtil.class.getClassLoader().getResourceAsStream("license.xml"); // license.xml应放在..\WebRoot\WEB-INF\classes路径下
            com.aspose.words.License aposeLic = new com.aspose.words.License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            if(is != null) {
                try {
                    is.close() ;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return result;
    }

    */
/**
     * word文档转pdf
     * @param inPath
     * @param outPath
     *//*

    public static boolean doc2pdf(String inPath, String outPath) {
        if (!getWordLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return false;
        }
        FileOutputStream os = null ;
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath); // 新建一个空白pdf文档
            os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, com.aspose.words.SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            // EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
            os.close();
        } catch (Exception e) {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            return false ;
        }
        return true ;
    }

    public static boolean getExcelLicense() {
        boolean result = false;
        InputStream is = null ;
        try {
            is = AsposeUtil.class.getClassLoader().getResourceAsStream("license.xml"); // license.xml应放在..\WebRoot\WEB-INF\classes路径下
            com.aspose.cells.License aposeLic = new com.aspose.cells.License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            if(is != null) {
                try {
                    is.close() ;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return result;
    }

    */
/**
     * asponse:excel转pdf
     * @param excelPath
     * @param pdfPath
     *//*

    public static boolean excel2Pdf(String excelPath, String pdfPath) {
        long old = System.currentTimeMillis();
        // 验证License
        if (!getExcelLicense()) {
            return false;
        }
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            File excelFile = new File(excelPath);
            if (excelFile.exists()) {
                fileInputStream = new FileInputStream(excelFile);
                Workbook workbook = new Workbook(fileInputStream);
                File pdfFile = new File(pdfPath);
                fileOutputStream = new FileOutputStream(pdfFile);
                workbook.save(fileOutputStream, com.aspose.cells.SaveFormat.PDF);
                long now = System.currentTimeMillis();
                System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒\n\n" + "文件保存在:" + pdfFile.getPath());
                return true ;
            } else {
                System.out.println("文件不存在");
                return false ;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false ;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static boolean getPptLicense() {
        boolean result = false;
        InputStream is = null ;
        try {
            is = AsposeUtil.class.getClassLoader().getResourceAsStream("license.xml"); // license.xml应放在..\WebRoot\WEB-INF\classes路径下
            com.aspose.slides.License aposeLic = new com.aspose.slides.License();
            aposeLic.setLicense(is);
            result = true;
            is.close();
        } catch (Exception e) {
            if(is != null) {
                try {
                    is.close() ;
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return result;
    }
    */
/**
     * aspose:ppt转pdf
     * @param inPath
     * @param outPath
     *//*

    public static void ppt2pdf(String inPath,String outPath) {

        // 验证LicensePresentation
        if (!getPptLicense()) {
            return;
        }
        FileOutputStream fileOS = null ;
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath);// 输出pdf路径
            Presentation pres = new Presentation(inPath);//输入pdf路径
            fileOS = new FileOutputStream(file);
            pres.save(fileOS, com.aspose.slides.SaveFormat.Pdf);
            fileOS.close();

            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒\n\n" + "文件保存在:" + file.getPath()); //转化过程耗时
        } catch (Exception e) {
            if(fileOS != null) {
                try {
                    fileOS.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        convertToPdfAsAspose("C:\\Users\\Administrator\\Desktop\\1.pptx","C:\\Users\\Administrator\\Desktop\\345.pdf");
        System.out.println("over");


    }




}*/
