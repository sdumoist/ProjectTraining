package com.jxdinfo.doc.common.util;

import java.io.*;
import java.net.Socket;

/**
 * Ceb文档转化为PDF
 */
public class CebUtil extends ConvertUtil {
    private static final String OPEN = "1";

    public synchronized static void ceb2Pdf(File sourceFile, File pdfFile) {
        if (OPEN.equals(prop.getProperty("ceb.convert.switch"))) {
            if (isOSWindows()) {
                c2pWindows(sourceFile, pdfFile);
            } else {
                c2pLinux(sourceFile, pdfFile);
            }
        }
    }

    private static void c2pLinux(File sourceFile, File pdfFile) {
        // 创建客户端
        Socket socket = null;
        try {
            // 创建客户端
            socket = new Socket(prop.getProperty("ceb.server.ip"), Integer.valueOf(prop.getProperty("ceb.server.port", "8765")));
//            socket = new Socket("192.168.137.142", 8765);
            // 向服务器发送文件
            BufferedOutputStream bo1 = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream bi1 = new BufferedInputStream(new FileInputStream(sourceFile));
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = bi1.read(buf)) != -1) {
                bo1.write(buf, 0, len);
            }
            bo1.flush();
            bi1.close();
            socket.shutdownOutput();

            // 获取服务器转换后的文件
            BufferedInputStream bi2 = new BufferedInputStream(socket.getInputStream());
            BufferedOutputStream bo2 = new BufferedOutputStream(new FileOutputStream(pdfFile));
            byte[] bytes = new byte[1024];
            int len1;
            while ((len1 = bi2.read(bytes)) != -1) {
                bo2.write(bytes, 0, len1);
            }
            bo2.flush();
            bo2.close();
            bi2.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void c2pWindows(File sourceFile, File pdfFile) {
        try {
            String exePath = prop.getProperty("ceb.c2pWin.dir") + "c2p_win/c2pwin.exe";
            if (new File(exePath).exists()) {
                ProcessUtils.execute(10000, exePath, sourceFile.getAbsolutePath(), pdfFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
