/*
 * 金现代轻骑兵V8开发平台 
 * ExportExcel.java 
 * 版权所有：金现代信息产业股份有限公司  Copyright (c) 2018-2023 .
 * 金现代信息产业股份有限公司保留所有权利,未经允许不得以任何形式使用.
 */
package com.jxdinfo.doc.manager.componentmanager.util;

import com.jxdinfo.hussar.common.exception.BizExceptionEnum;
import com.jxdinfo.hussar.common.export.bean.ExcelEntity;
import com.jxdinfo.hussar.common.export.bean.ExcelTitle;
import com.jxdinfo.hussar.core.exception.HussarException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类的用途： 导出Excel.<p>
 * 创建日期：2018年4月12日 <br>
 * 修改历史：<br>
 * 修改日期：2018年4月12日 <br>
 * 修改作者：duwei2 <br>
 * 修改内容：修改内容 <br>
 * @author duwei2
 * @version 1.0
 */
@Component
public class MultiplexExportExcel<T> {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     *
     * @param outputStream
     */
    public void exportExcel(OutputStream outputStream , ExcelEntity excelEntity){
        try{
            this.exportExcelMap(outputStream ,excelEntity.getQuerys() , excelEntity.getTitles() , excelEntity.getBlankTop() , excelEntity.getBlankLeft());
        }catch( IOException exp ){
            log.error("excel文件导出报错："+exp.getMessage());
            throw new HussarException(BizExceptionEnum.REQUEST_NULL);
        }

    }


    /**
     * @param fileName 文件名称
     * @param querys   数据集
     * @param titles   导出标题
     */
    public void exportExcel(String fileName , List<T> querys , List<ExcelTitle> titles ) throws IOException {


        for( ExcelTitle excelTitle : titles ){
            String tileid = excelTitle.getTitle_id();
        }


    }

    /**
     * 导出的实际方法
     * @param querys
     * @param titles
     * @param blankTop
     * @param blankLeft
     * @throws IOException
     */
    private void exportExcelMap(OutputStream outputStream  , List<Map<String,Object>> querys , List<ExcelTitle> titles , boolean blankTop , boolean blankLeft ) throws IOException{

        int rowIndex = 0;

        //创建一个Excel文件
        Workbook xssfWorkbook = new XSSFWorkbook();
        //创建一个工作表
        Sheet sheet = xssfWorkbook.createSheet("科研成果复用");



        for (int i=0;i<titles.size();i++){
            int max = titles.get(i).getTitle_text().toString().length();
            for(Map m:querys){
                if (m.get(titles.get(i).getTitle_id()).toString().length()>max){
                    max = m.get(titles.get(i).getTitle_id()).toString().length();
                }
            }
            if (max*512>255*256){
                sheet.setColumnWidth(i+1,255*256);
            }else {
                sheet.setColumnWidth(i+1,max*512);
            }

        }
        //头部留白
        if( blankTop ){
            sheet.createRow(rowIndex++);
        }
        //左侧索引
        int leftInde = 0;
        if( blankLeft ){
            leftInde++;
        }
        //生成标题
        Row headRow = sheet.createRow(rowIndex++);
        for( int i = 0; i < titles.size(); i++  ){
            ExcelTitle excelTitle = titles.get(i);
            Cell cell = headRow.createCell(leftInde+i);
            cell.setCellValue(excelTitle.getTitle_text());
            CellStyle headCellStyle = xssfWorkbook.createCellStyle();


            //居中
            headCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font font = xssfWorkbook.createFont();
            //加粗
            font.setBold(true);
            headCellStyle.setFont(font);
            //设置边框
            headCellStyle.setBorderLeft(BorderStyle.THIN);
            headCellStyle.setBorderTop(BorderStyle.THIN);
            headCellStyle.setBorderRight(BorderStyle.THIN);
            headCellStyle.setBorderBottom(BorderStyle.THIN);
            cell.setCellStyle(headCellStyle);
        }
        //生成文档体
        CellStyle bodyCellStyle = xssfWorkbook.createCellStyle();
        bodyCellStyle.setAlignment(HorizontalAlignment.CENTER);
        bodyCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyCellStyle.setBorderLeft(BorderStyle.THIN);
        bodyCellStyle.setBorderTop(BorderStyle.THIN);
        bodyCellStyle.setBorderRight(BorderStyle.THIN);
        bodyCellStyle.setBorderBottom(BorderStyle.THIN);
        CellStyle bodyCellStyle1 = xssfWorkbook.createCellStyle();
        bodyCellStyle1.setAlignment(HorizontalAlignment.LEFT);
        bodyCellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyCellStyle1.setBorderLeft(BorderStyle.THIN);
        bodyCellStyle1.setBorderTop(BorderStyle.THIN);
        bodyCellStyle1.setBorderRight(BorderStyle.THIN);
        bodyCellStyle1.setBorderBottom(BorderStyle.THIN);
        int rowStart = 0;
        int rowEnd = 0;
        for( int i = 0; i < querys.size(); i++ ){
            if(i==querys.size()-1){
                if(rowStart!=rowEnd){
                    CellRangeAddress componentName = new CellRangeAddress(rowStart+2,rowEnd+2,1,1);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(componentName);
                    CellRangeAddress caUserName = new CellRangeAddress(rowStart+2,rowEnd+2,2,2);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(caUserName);
                    CellRangeAddress organAlias = new CellRangeAddress(rowStart+2,rowEnd+2,3,3);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(organAlias);
                    CellRangeAddress componentCount = new CellRangeAddress(rowStart+2,rowEnd+2,4,4);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(componentCount);
                }
            }else if(!querys.get(i).get(titles.get(0).getTitle_id()).equals(querys.get(i+1).get(titles.get(0).getTitle_id()))||
                    !querys.get(i).get(titles.get(1).getTitle_id()).equals(querys.get(i+1).get(titles.get(1).getTitle_id()))||
                    !querys.get(i).get(titles.get(2).getTitle_id()).equals(querys.get(i+1).get(titles.get(2).getTitle_id()))){
                //合并单元格
                if(rowStart!=rowEnd){
                    CellRangeAddress componentName = new CellRangeAddress(rowStart+2,rowEnd+2,1,1);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(componentName);
                    CellRangeAddress caUserName = new CellRangeAddress(rowStart+2,rowEnd+2,2,2);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(caUserName);
                    CellRangeAddress organAlias = new CellRangeAddress(rowStart+2,rowEnd+2,3,3);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(organAlias);
                    CellRangeAddress componentCount = new CellRangeAddress(rowStart+2,rowEnd+2,4,4);//起始行,结束行,起始列,结束列
                    sheet.addMergedRegion(componentCount);
                }
                rowEnd = i+1;
                rowStart = i+1;
            }else {
                rowEnd = i+1;
            }
            Map<String,Object>  map = querys.get(i);
            Row rowBody = sheet.createRow(rowIndex + i );
            for( int j = 0;j < titles.size(); j++  ){
                ExcelTitle excelTitle = titles.get(j);
                String textId = excelTitle.getTitle_id();
                Cell bodyCell = rowBody.createCell(j+leftInde);
                if (j==0||j==4){
                    bodyCell.setCellStyle(bodyCellStyle1);
                }else {
                    bodyCell.setCellStyle(bodyCellStyle);
                }
                Object obj = map.get(textId);
                if( obj instanceof String ){
                    bodyCell.setCellValue((String)obj );
                }else if( obj instanceof Double ){
                    bodyCell.setCellValue((Double) obj );
                }else if( obj instanceof Date){
                    bodyCell.setCellValue((Date) obj);
                }else if( obj instanceof Calendar){
                    bodyCell.setCellValue((Calendar) obj);
                }else if( obj instanceof Boolean ){
                    bodyCell.setCellValue((Boolean) obj);
                }else if( obj instanceof Integer ) {
                    bodyCell.setCellValue((Integer)obj);
                }else{
                    bodyCell.setCellValue((String) obj);
                }

            }
        }

        xssfWorkbook.write(outputStream);
        xssfWorkbook.close();
    }

}
