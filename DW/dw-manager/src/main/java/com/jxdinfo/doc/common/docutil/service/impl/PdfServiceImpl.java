package com.jxdinfo.doc.common.docutil.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfReader;
import com.jxdinfo.doc.common.docutil.service.PdfService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.PdfUtil;
import com.jxdinfo.doc.common.util.TikaUtil;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 操作PDF服务
 * @author wangning
 * @date 2018-8-10 19:45:06
 */
@Service
public class PdfServiceImpl implements PdfService {
	
	static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    
	/**
	 * 转换生成PDF文档
	 *
	 * @param sourcePath
	 *            源路径
	 * @param targetPath
	 *            目标路径
	 * @param fileSapce
	 *            文件大小
	 * @param contentTypes
	 *            文件类型
	 * @param fileId
	 *            文件ID（含文件后缀名）
	 * @return Map<String,String> content:文件内容
	 *         flag：转换pdf标志（false:未成功转换，true:成功转换）
	 * @throws Exception 
	 */
	@Override
	public Map<String, Object> changeToPdf(String sourcePath, String targetPath, long fileSapce, String contentTypes,
			String fileId) throws Exception {
		// 文档内容
		String content = null;
		//文件类型
		String contentType;
		// 转换标志 （false:未成功转换，true:成功转换ao）
		boolean flag = false;
		Map<String, Object> returnMap = new HashMap<String, Object>();
		PdfReader reader = null;
		try {
			//获取文件内容
			//处理ceb文件
			if (fileSapce > 1024 * 1024 * 50 || fileId.endsWith(".ceb")) {
				// 因效率问题，判断文件如果大于50M，暂时不添加针对内容的全文检索
				//ceb文件转pdf之前不读取全文内容
				// 此时文件类型根据request内contentType获取
				// TODO： 注意条件成熟后，此处要重新进行处理
				content = "";
				contentType = contentTypes;
			} else {
				Map<String, Object> metadata = TikaUtil.autoParse(sourcePath);
				content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
				contentType = metadata.get("contentType").toString();
			}
			// 如果是ppt格式按pptx格式处理
			if (contentType.contains("powerpoint")){
				contentType = contentType + ".presentationml";
			}
			if (contentType.contains("ms-excel")){
				contentType=contentType+".spreadsheetml";
			}
			// 如果是txt ansi格式按txt普通格式处理
			if (contentType.contains("octet-stream")){
				contentType=contentType+".text/plain";
			}
			// 根据文档类型转换格式
			if (fileId.endsWith(".ceb")) {
				ApiClient client = new ApiClient();
				JSONObject cebName = new JSONObject();
				cebName.put("cebName", sourcePath);
				//TODO ceb文件未加水印
				String ceb = client.cebToPdf(cebName);
				if (ceb != null && ceb.contains("true")) {
					flag = true;
					Map<String, Object> metadata = TikaUtil.autoParse(targetPath);
					content = metadata.get("content").toString().replaceAll("<", "<&nbsp;");
				}
			} else if (contentType.contains("word")||contentType.contains("rtf")) {
				PdfUtil.word2pdf(sourcePath, targetPath);
				flag = true;
			}else if (contentType.contains("text/html") && (fileId.endsWith(".doc") || fileId.endsWith(".docx"))) {
				//网络导出的doc格式文件
				PdfUtil.word2pdf(sourcePath, targetPath);
				flag = true;
			} else if (contentType.contains("excel") || contentType.contains("spreadsheetml")) {
				PdfUtil.excel2Pdf(sourcePath, targetPath);
				flag = true;
			} else if (contentType.contains("powerpoint") || contentType.contains("presentationml")) {
				PdfUtil.ppt2pdf(sourcePath, targetPath);
				flag = true;
			} else if ((contentType.contains("octet-stream") || contentType.contains("text/plain"))
					&& fileId.endsWith(".txt")) {
				// 只处理txt文件，防止其他文件转换异常
				PdfUtil.txt2pdf(sourcePath, targetPath);
				flag = true;
			}else{
				if(contentTypes!=null){
					if(contentTypes.contains("image")){
						double toWidth=290;

						BufferedImage imageList = getImageList(sourcePath,new String[] {"jpg","png","gif","bmp"});
					    double oldWidth=	imageList.getWidth();
						double oldHeight=	imageList.getHeight();
						if(oldWidth/oldHeight>4){
							double toHeight=200;
							 toWidth= toHeight/oldHeight*oldWidth;
							writeHighQuality(targetPath,zoomImage(imageList,(int)toWidth,(int)toHeight));
						}else {
						double toHeight=toWidth/oldWidth*oldHeight;
						writeHighQuality(targetPath,zoomImage(imageList,(int)toWidth,(int)toHeight));}
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("转换PDF文档IO异常：" + ExceptionUtils.getErrorInfo(e));
			throw new Exception("转换PDF文档IO异常");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("转换PDF格式出错" + ExceptionUtils.getErrorInfo(e));
			throw new Exception("转换PDF格式出错");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					System.out.println("关闭流错误" + ExceptionUtils.getErrorInfo(e));
				}
			}
		}

		returnMap.put("targetPath", targetPath);
		returnMap.put("contentType", contentType);
		returnMap.put("content", content);
		returnMap.put("flag", flag);

		return returnMap;
	}

	/**
	 *
	 * @Description: 生成图片
	 * @param
	 * @date 2017年5月7  日10:48:27
	 */
	public boolean writeHighQuality(String path , BufferedImage im) throws IOException {
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
	public BufferedImage zoomImage(BufferedImage im, int toWidth , int toHeight) {
		BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
		result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
		return result;
	}
	/**
	 *
	 * @Description: 取得图片对象
	 * @param
	 * @date 2017年5月7日10:48:27要转化的图像的文件夹,就是存放图像的文件夹路径
	 */
	public BufferedImage getImageList(String ImgList, String[] type) throws IOException{
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

	/**
	 * 把图片写到磁盘上
	 * @param im
	 * @param path     eg: C://home// 图片写入的文件夹地址
	 * @param fileName DCM1987.jpg  写入图片的名字
	 * @date 2017年5月7日10:48:27
	 */

	public boolean writeToDisk(BufferedImage im, String path, String fileName) {
		File f = new File(path + fileName);
		String fileType = getExtension(fileName);
		if (fileType == null)
			return false;
		try {
			ImageIO.write(im, fileType, f);
			im.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public String getExtension(String fileName) {
		try {
			return (fileName.split("\\.")[fileName.split("\\.").length - 1]).toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}
}
