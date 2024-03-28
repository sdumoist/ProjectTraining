/*
 * IdUtil.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.common.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 类的用途：<p>
 * 创建日期：2018年7月10日 <br>
 * 修改历史：<br>
 * 修改日期：2018年7月10日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
public class PdfWaterUtil {
    private PdfWaterUtil() {
    }
public static void waterMark(String inputFile,String outputFile, String waterMarkName,String componyMarkName) {
        try {
            PdfReader reader = new PdfReader(inputFile);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(
                    outputFile));

            BaseFont base = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",   BaseFont.EMBEDDED);

            Rectangle pageRect = null;
            PdfGState gs = new PdfGState();
            gs.setFillOpacity(0.3f);
            gs.setStrokeOpacity(0.4f);
            int total = reader.getNumberOfPages() + 1;

            JLabel label = new JLabel();
            FontMetrics metrics;
            int textH = 0;
            int textW = 0;
            label.setText(waterMarkName);
            metrics = label.getFontMetrics(label.getFont());
            textH = metrics.getHeight();
            textW = metrics.stringWidth(label.getText());

            PdfContentByte under;
            for (int i = 1; i < total; i++) {
                under = stamper.getOverContent(i);// 在内容上方加水印
                //content = stamper.getUnderContent(i);//在内容下方加水印
                gs.setFillOpacity(0.2f);
                // content.setGState(gs);
                under.beginText();
                under.setFontAndSize(base, 23);
                BaseColor baseColor = new BaseColor( 191,191,192);
                under.setColorFill(baseColor);
                under.setTextMatrix(70, 200);
                under.showTextAligned(Element.ALIGN_CENTER, waterMarkName, 400,350, 16);
                under.showTextAligned(Element.ALIGN_CENTER, componyMarkName, 400,400, 16);
                under.endText();
            }

            //一定不要忘记关闭流
            stamper.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        waterMark("C:\\Users\\Administrator\\Desktop\\123.pdf","C:\\Users\\Administrator\\Desktop\\345.pdf","杨金石","金现代");
        System.out.println("over");


    }

}
