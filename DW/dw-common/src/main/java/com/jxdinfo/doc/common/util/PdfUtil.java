package com.jxdinfo.doc.common.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.exception.TikaException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;


/**
 * 在windows下使用
 */
public class PdfUtil {
    private PdfUtil() {
    }

    private static final Integer WORD_TO_PDF_OPERAND = 17;
    private static final Integer PPT_TO_PDF_OPERAND = 32;
    private static final Integer EXCEL_TO_PDF_OPERAND = 0;

    public  synchronized static void word2pdf(String srcFilePath, String pdfFilePath) {
        ActiveXComponent app = null;
        Dispatch word = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("Word.Application");
            app.setProperty("Visible", false);
            Dispatch docs = app.getProperty("Documents").toDispatch();
            Object[] obj = new Object[]{
                        srcFilePath,
                    new Variant(false),
                    new Variant(false),//是否只读
                    new Variant(false),
                    new Variant("pwd")
            };
            word = Dispatch.invoke(docs, "Open", Dispatch.Method, obj, new int[1]).toDispatch();
//          Dispatch.put(doc, "Compatibility", false);  //兼容性检查,为特定值false不正确
            Dispatch.put(word, "RemovePersonalInformation", false);
            Dispatch.call(word, "ExportAsFixedFormat", pdfFilePath, WORD_TO_PDF_OPERAND); // word保存为pdf格式宏，值为17
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (word != null) {
                Dispatch.call(word, "Close", false);
            }
            if (app != null) {
                app.invoke("Quit", 0);
            }
            ComThread.Release();
        }
    }

    public synchronized static void ppt2pdf(String srcFilePath, String pdfFilePath) throws Exception {
        ActiveXComponent app = null;
        Dispatch ppt = null;
        try {
            ComThread.InitSTA();
            app = new ActiveXComponent("PowerPoint.Application");
            Dispatch ppts = app.getProperty("Presentations").toDispatch();
            /*
             * call
             * param 4: ReadOnly
             * param 5: Untitled指定文件是否有标题
             * param 6: WithWindow指定文件是否可见
             * */
            ppt = Dispatch.call(ppts, "Open", srcFilePath, true, true, false).toDispatch();
            // ppSaveAsPDF为特定值32
            Dispatch.call(ppt, "SaveAs", pdfFilePath, PPT_TO_PDF_OPERAND);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ppt != null) {
                Dispatch.call(ppt, "Close");
            }
            if (app != null) {
                app.invoke("Quit");
            }
            ComThread.Release();
        }
    }

    public synchronized  static void excel2Pdf(String inFilePath, String outFilePath) throws Exception {
        inFilePath= inFilePath.replaceAll("//","/");
        ActiveXComponent ax = null;
        Dispatch excel = null;
        Dispatch sheets = null;
        Dispatch sheet = null;
        
        try {
            ComThread.InitSTA();
            ax = new ActiveXComponent("Excel.Application");
            ax.setProperty("Visible", new Variant(false));
            ax.setProperty("AutomationSecurity", new Variant(3)); // 禁用宏
            Dispatch excels = ax.getProperty("Workbooks").toDispatch();

            Object[] obj = new Object[]{
                    inFilePath,
                    new Variant(false),
                    new Variant(false)
            };
            
            excel = Dispatch.invoke(excels, "Open", Dispatch.Method, obj, new int[9]).toDispatch();
            
            //开始
            sheets = Dispatch.get(excel, "Sheets").toDispatch();
            int count = Dispatch.get(sheets, "Count").getInt();
    		
            //取Excel第一页转换
			sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[] { new Integer(1) }, new int[1]).toDispatch();

			Dispatch.call(sheet, "Activate");
		//	Dispatch.call(sheet, "Select");

			  /*处理excel不正常分页*/
            Dispatch pageSetup = Dispatch.get(sheet, "PageSetup").toDispatch();
            Dispatch.put(pageSetup, "Orientation", new Variant(2));
            Dispatch.put(pageSetup, "Zoom", false); // 值为100或false
            Dispatch.put(pageSetup, "FitToPagesWide", 1); // 所有列为一页(1或false)
			
    		//结束
            
            
            // 转换格式
            Object[] obj2 = new Object[]{
                    new Variant(EXCEL_TO_PDF_OPERAND), // PDF格式=0
                    outFilePath,
                    new Variant(0)  //0=标准 (生成的PDF图片不会变模糊) ; 1=最小文件
            };
            Dispatch.invoke(sheet, "ExportAsFixedFormat", Dispatch.Method, obj2, new int[1]);

        } catch (Exception es) {
            es.printStackTrace();
            throw es;
        } finally {
            if (excel != null) {
                Dispatch.call(excel, "Close", new Variant(false));
            }
            if (ax != null) {
                ax.invoke("Quit", new Variant[]{});
                ax = null;
            }
            ComThread.Release();
        }

    }

    private void s(){
		Dispatch sheet = null;
		Dispatch sheets = null;
		ActiveXComponent actcom = new ActiveXComponent("Excel.Application");

		
		actcom.setProperty("Visible", new Variant(false));
		Dispatch excels = actcom.getProperty("Workbooks").toDispatch();

		Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method,
				new Object[] { "filePath", new Variant(false), new Variant(false) }, new int[9]).toDispatch();

		sheets = Dispatch.get(excel, "Sheets").toDispatch();

		int count = Dispatch.get(sheets, "Count").getInt();
		// System.out.println(count);
		for (int j = 1; j <= count; j++) {
			sheet = Dispatch.invoke(sheets, "Item", Dispatch.Get, new Object[] { new Integer(j) }, new int[1])
					.toDispatch();

			String sheetname = Dispatch.get(sheet, "name").toString();

			Dispatch.call(sheet, "Activate");
			Dispatch.call(sheet, "Select");

			Dispatch.invoke(excel, "SaveAs", Dispatch.Method,
					new Object[] { "outFile", new Variant(57), new Variant(false), new Variant(57), new Variant(57),
							new Variant(false), new Variant(true), new Variant(57), new Variant(false),
							new Variant(true), new Variant(false) },
					new int[1]);
		}

		Dispatch.call(excel, "Close", new Variant(true));

		if (actcom != null) {

			actcom.invoke("Quit", new Variant[] {});

			actcom = null;
		}
                
    }
    
    public synchronized static void txt2pdf(String inFilePath, String outFilePath){
        Document document = new Document(PageSize.A4, 80, 80, 60, 30);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outFilePath));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        FileInputStream fis = null;
        BufferedReader read = null;
        InputStreamReader isr = null;
        BaseFont bfChinese = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font fontChinese = new Font(bfChinese, 18, Font.NORMAL);
            Paragraph t = new Paragraph();
            t.setAlignment(Element.ALIGN_CENTER);
            t.setLeading(30.0f);
            document.add(t);
            fontChinese = new Font(bfChinese, 11, Font.NORMAL);
            fis = new FileInputStream(inFilePath);
            String codingFormat = resolveCode(inFilePath);
            isr = new InputStreamReader(fis,codingFormat);
            read = new BufferedReader(isr);
            String line = null;
            while ((line = read.readLine()) != null) {
                t = new Paragraph(line, fontChinese);
                t.setAlignment(Element.ALIGN_LEFT);
                t.setLeading(20.0f);
                try {
                    document.add(t);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null){
                fis.close();
            }
            if (read != null){
                read.close();
            }
            if (isr != null){
                isr.close();
            }
            if (document != null){
                document.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (read != null){
                try {
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null){
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null){
                document.close();
            }
        }
    }
    public static String resolveCode(String path){
        InputStream inputStream = null;
        AutoDetectReader dr = null;
        String code = "gb2312";
        try {
            inputStream = new FileInputStream(path);            
            dr = new AutoDetectReader(inputStream);           
            
            String drCode =  dr.getCharset().name();
            if( drCode != null && !"".equals(drCode)){
            	code = dr.getCharset().name();      
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
			e.printStackTrace();
		}finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dr != null){
            	try {
            		dr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return code;
    }
}
