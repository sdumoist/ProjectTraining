package com.jxdinfo.doc.common.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailsUtil {


    public static boolean createThumbnails(String sourcePath, String targetPath, double maxHeight, double maxWidth) throws Exception{
        BufferedImage imageList = getImageList(sourcePath,new String[] {"jpg","png","gif","bmp","jpeg","ai","pdd"});
        double oldWidth=	imageList.getWidth();
        double oldHeight=	imageList.getHeight();
        if (oldWidth/oldHeight > 1){
            if (oldWidth > maxWidth){
                double toWidth = maxWidth;
                double toHeight = toWidth/oldWidth*oldHeight;
                writeHighQuality(targetPath,zoomImage(imageList,(int)toWidth,(int)toHeight));
                return true;
            }
        } else {
            if (oldHeight > maxHeight) {
                double toHeight = maxHeight;
                double toWidth = toHeight / oldHeight * oldWidth;
                writeHighQuality(targetPath, zoomImage(imageList, (int) toWidth, (int) toHeight));
                return true;
            }
        }
        return false;
    }
    public static Map getHeightAndWidth(String sourcePath) throws Exception{
        BufferedImage imageList = getImageList(sourcePath,new String[] {"jpg","png","gif","bmp","jpeg"});
        double width=	imageList.getWidth();
        double height=	imageList.getHeight();
        Map info = new HashMap();
        info.put("width", width);
        info.put("height", height);
        return info;

    }

    /**
     * 获取本地图片的字节数
     * @param imgPath
     * @return
     */
    public static String pathSize(String imgPath) {
        File file = new File(imgPath);
        FileInputStream fis;
        int fileLen = 0;
        try {
            fis = new FileInputStream(file);
            fileLen = fis.available();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return Integer.toString(fileLen);
    }
    /**
     *
     * @Description: 取得图片对象
     * @param
     * @date 2017年5月7日10:48:27要转化的图像的文件夹,就是存放图像的文件夹路径
     */
    private static BufferedImage getImageList(String ImgList, String[] type) throws IOException {
        Map<String,Boolean> map = new HashMap<String, Boolean>();
        for(String s : type) {
            map.put(s,true);
        }
        BufferedImage imageList = null;
        File file = null;
        file = new File(ImgList);
        if (!file.exists()) {
            file.mkdir();
        }
        try{
            if(file.length() != 0 && map.get(getExtension(file.getName())) != null ){
                imageList = ImageIO.read(file);
            }
        }catch(Exception e){
            imageList = null;
        }

        return imageList;
    }

    private  static  String getExtension(String fileName) {
        try {
            return (fileName.split("\\.")[fileName.split("\\.").length - 1]).toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @Description: 生成图片
     * @param
     * @date 2017年5月7  日10:48:27
     */
    private static boolean writeHighQuality(String path , BufferedImage im) throws IOException {
        //return true;
        FileOutputStream newimage = null;
        try {
            // 输出到文件流
            newimage = new FileOutputStream(path);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
            // 压缩质量
            jep.setQuality(1f, true);
            encoder.encode(im, jep);
            //近JPEG编码
            newimage.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @Description: 取得图片对象
     * @param
     * @date 2017年5月7日10:48:27
     */
    private static BufferedImage zoomImage(BufferedImage im, int toWidth , int toHeight) {
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return result;
    }
}
