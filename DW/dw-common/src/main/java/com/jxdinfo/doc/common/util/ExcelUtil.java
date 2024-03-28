package com.jxdinfo.doc.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {


    public   synchronized  static void changeExcel(String videoInputPath) throws Exception {

        File file = new File(videoInputPath);
        try {
            InputStream fis = new FileInputStream(file);
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                int pointIndex = videoInputPath.lastIndexOf(".");
                // 后缀名
                String suffix = videoInputPath.substring(pointIndex).toLowerCase();
                if(".xlsx".equals(suffix)){
                XSSFWorkbook workbook = new XSSFWorkbook(bis);

                    Iterator<Sheet> rit = workbook.sheetIterator();
                    while (rit.hasNext()) {
                        Sheet sheet = rit.next();
                        PrintSetup print = sheet.getPrintSetup();
                        print.setLandscape(true); // 打印方向，true：横向，false：纵向(默认)
                        print.setFitWidth((short)1);
                        print.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE); //纸张类型
                        print.setScale((short)55);//自定义缩放①，此处100为无缩放
                        sheet.setAutobreaks(true);

                    }
                    OutputStream os = null;
                    os  = new FileOutputStream(file);
                    workbook.write(os);
                    workbook.close();
                    os.close();

                }else if(".xls".equals(suffix)){
                    HSSFWorkbook workbook = new HSSFWorkbook(fis);

                    Iterator<Sheet> rit = workbook.sheetIterator();
                    while (rit.hasNext()) {
                        Sheet sheet = rit.next();
                        PrintSetup print = sheet.getPrintSetup();
                        print.setLandscape(true); // 打印方向，true：横向，false：纵向(默认)
                        print.setFitWidth((short)1);
                        print.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张类型
                        print.setScale((short)55);//自定义缩放①，此处100为无缩放
                        sheet.setAutobreaks(true);

                    }
                    OutputStream os = null;
                    os  = new FileOutputStream(file);
                    workbook.write(os);
                    workbook.close();
                    os.close();


                }else {
                  return;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {

e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        File file = new File("C:\\Users\\Administrator\\Desktop\\11.xlsx");
        try {
            InputStream fis = new FileInputStream(file);
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                XSSFWorkbook workbook = new XSSFWorkbook(bis);
                Iterator<Sheet> rit = workbook.sheetIterator();
                while (rit.hasNext()) {
                    Sheet sheet = rit.next();
                    PrintSetup print = sheet.getPrintSetup();
                    print.setLandscape(true); // 打印方向，true：横向，false：纵向(默认)
                    print.setFitWidth((short)1);
                    print.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE); //纸张类型
                    print.setScale((short)55);//自定义缩放①，此处100为无缩放
                    sheet.setAutobreaks(true);

                }
                OutputStream os = null;
                os  = new FileOutputStream(file);
                workbook.write(os);
                workbook.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {


        }

    }
}
