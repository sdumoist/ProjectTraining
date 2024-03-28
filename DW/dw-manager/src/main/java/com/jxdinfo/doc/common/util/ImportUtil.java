package com.jxdinfo.doc.common.util;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ImportUtil {

    private final static Logger LOG = LoggerFactory.getLogger(ImportUtil.class);

    private static FormulaEvaluator evaluator;


    /**
     * @param is 输入流
     * @return Workbook
     * @throws IOException
     */
    public static Workbook getWorkbook(InputStream is, String fileName)
            throws IOException {
        Workbook wb = null;
        if (fileName.endsWith("xls")) {// HSSFWorkbook:是操作Excel2003以前（包括2003）的版本，扩展名是.xls
            wb = new HSSFWorkbook(is);
        } else if (fileName.endsWith("xlsx")) {// XSSFWorkbook:是操作Excel2007的版本，扩展名是.xlsx
            wb = new XSSFWorkbook(is);
        }
        return wb;
    }


    /**
     * 得到每个单元格的值
     *
     * @param cell 单元格
     * @return 单元格的值
     */
    public static Object getCellValue(Cell cell) {
        Object value = null;
        if (cell == null) {
            value = "";
            return value;
        }
        DecimalFormat df = new DecimalFormat("0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        DecimalFormat df2 = new DecimalFormat("0.0000");
        if (cell.toString().indexOf(".") != -1) {
            String cellvalue = cell.toString();
            String[] cells = cellvalue.split("\\.");//split以小数点分割的时候需要转义
            if (cells[1].length() == 1) {
                df2 = new DecimalFormat("0.0");
            } else if (cells[1].length() == 2) {
                df2 = new DecimalFormat("0.00");
            } else if (cells[1].length() == 3) {
                df2 = new DecimalFormat("0.000");
            } else {
                df2 = new DecimalFormat("0.0000");
            }
            // DecimalFormat df2 = new DecimalFormat("0.00");
        }

        // DecimalFormat df2 = new DecimalFormat("0.00");
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    value = sdf.format(cell.getDateCellValue());
                } else {
                    value = df2.format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = String.valueOf(cell.getNumericCellValue());
                break;
            default:
                break;
        }
        return value;
    }

    /**
     *  判断是否为空行
     * @param row
     * @return
     */
    public static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
                return false;
        }
        return true;

    }


}
