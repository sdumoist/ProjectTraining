package com.jxdinfo.doc.common.util;

import com.jxdinfo.doc.common.model.MasterAndSlave;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;

/**
 * FastDFS工具类
 */
@Component
public class FdfsFileUtil {
	static final public Logger logger = LogManager.getLogger(FdfsFileUtil.class);
    static {
        try {
            ClientGlobal.initByProperties("fdfs.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }catch(org.csource.common.MyException e){
            e.printStackTrace();
        }
    }

    /**
     * tracker
     */
    static TrackerClient tracker = new TrackerClient();

    /**
     * getClient
     * @Title: getClient 
     * @author: XuXinYing
     * @return StorageClient1
     * @throws Exception 异常
     */
    public StorageClient1 getClient11() throws IOException {
    	TrackerServer trackerServer = tracker.getConnection();
        return new StorageClient1(trackerServer, null);
    }

    /**上传文件
     * @param data data
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String upload(byte[] data, String fileName) throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
       
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(data, fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**上传文件
     * @param group group
     * @param data data
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String upload(String group, byte[] data, String fileName) throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
       
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(group, data, fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**上传文件
     * @param input 输入流
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String upload(InputStream input, String fileName) throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
       
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(streamToArray(input), fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){        		
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 上传文件
     * @param group group
     * @param input 输入
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String upload(String group, InputStream input, String fileName) throws IOException, MyException {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
       
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(group, streamToArray(input), fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 上传从文件
     * @Title: uploadSlave 
     * @author: XuXinYing
     * @param masterFileId masterFileId
     * @param input 输入
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String uploadSlave(String masterFileId, InputStream input, String fileName) throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
        final String filePrefixName = FilenameUtils.getPrefix(fileName);
        
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(masterFileId, filePrefixName, streamToArray(input), fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 上传从文件
     * @Title: uploadSlave 
     * @author: XuXinYing
     * @param masterFileId masterFileId
     * @param input 输入
     * @param filePrefixName filePrefixName
     * @param fileName 文件名
     * @return String
     * @throws Exception 异常
     */
    public String uploadSlave(String masterFileId, InputStream input, String filePrefixName, String fileName) throws IOException, MyException {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
                
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(masterFileId, filePrefixName, streamToArray(input), fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * uploadSlave
     * @Title: uploadSlave 
     * @author: XuXinYing
     * @param masterFileId masterFileId
     * @param input 输入
     * @param filePrefixName filePrefixName
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String uploadSlave(String masterFileId, byte[] input, String filePrefixName, String fileName)
            throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        final String fileExtName = FilenameUtils.getExtension(fileName);
        
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(masterFileId, filePrefixName, input, fileExtName, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 上传主从文件
     * @Title: uploadMasterAndSlave 
     * @author: XuXinYing
     * @param localMaster localMaster
     * @param localSlave localSlave
     * @return MasterAndSlave
     * @throws Exception 异常
     */
    public MasterAndSlave uploadMasterAndSlave(String localMaster, String... localSlave) throws Exception {
        final File localMasterFile = new File(localMaster);
        File localSlaveFile = null;
        String fileName = localMasterFile.getName();
        NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        String fileExtName = FilenameUtils.getExtension(fileName);
        final MasterAndSlave ms = MasterAndSlave.create(localSlave.length);
        
        TrackerServer trackerServer = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        
	        // 上传主
	        final String master = client.upload_file1(fileName, fileExtName, metaList);
	        ms.setMaster(master);
	        // 上传从
	        for (final String s : localSlave) {
	            localSlaveFile = new File(s);
	            fileName = localSlaveFile.getName();
	            metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
	            fileExtName = FilenameUtils.getExtension(fileName);
	            final String filePrefixName = FilenameUtils.getExtension(fileName);
	            ms.addSlave(client.upload_file1(master, filePrefixName, s, fileExtName, metaList));
	        }

        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return ms;
    }

    /**
     * 上传住从文件
     * @Title: uploadMasterAndSlave 
     * @author: XuXinYing
     * @param group group
     * @param masterInput masterInput
     * @param masterName masterName
     * @param slaveNames slaveNames
     * @param slaveInputs slaveInputs
     * @return  MasterAndSlave
     * @throws Exception 异常
     */
    public MasterAndSlave uploadMasterAndSlave(String group, InputStream masterInput, String masterName,
            List<String> slaveNames, InputStream... slaveInputs) throws Exception {
        //final StorageClient1 client = getClient();
        String fileName = masterName;
        NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        String fileExtName = FilenameUtils.getExtension(fileName);
        final MasterAndSlave ms = MasterAndSlave.create(slaveNames.size());
        // 上传主
        String master = null;
        
        TrackerServer trackerServer = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        
	        if (StringUtils.isEmpty(group)) {
	            master = client.upload_file1(streamToArray(masterInput), fileExtName, metaList);
	        } else {
	            master = client.upload_file1(group, streamToArray(masterInput), fileExtName, metaList);
	        }
	        ms.setMaster(master);
	        // 上传从
	        for (int i = 0, len = slaveNames.size(); i < len; i++) {
	            fileName = slaveNames.get(i);
	            metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
	            fileExtName = FilenameUtils.getExtension(fileName);
	            final String filePrefixName = FilenameUtils.getPrefix(fileName);
	            ms.addSlave(client.upload_file1(master, filePrefixName, streamToArray(slaveInputs[i]), fileExtName,
	                        metaList));
	        }
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return ms;
    }

    /**
     * 上传本地文件
     * @Title: upload 
     * @author: XuXinYing
     * @param file 文件
     * @param fileName 文件名称
     * @return String
     * @throws Exception 异常
     */
    public String upload(String file, String fileName) throws Exception {
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };
        
        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(file, null, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 上传本地文件
     * @Title: upload 
     * @author: XuXinYing
     * @param file 文件
     * @return String
     * @throws Exception 异常
     */
    public String upload(String file) throws Exception {
        final String fileName = new File(file).getName();
        final NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName) };

        TrackerServer trackerServer = null;
        String result = null;
        
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        result = client.upload_file1(file, null, metaList);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("上传文件到FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
        return result;
    }

    /**
     * 下载文件
     * @Title: download 
     * @author: XuXinYing
     * @param fileId 文件id
     * @param out 输出
     * @throws Exception 异常
     */
    public void download(String fileId, OutputStream out) throws Exception {
        final byte[] file;
        
        TrackerServer trackerServer = null;
        try {
	        TrackerClient trackerClient = new TrackerClient();
	        trackerServer = trackerClient.getConnection();  
	        StorageClient1 client = new StorageClient1(trackerServer, null);
	        file = client.download_file1(fileId);
	        out.write(file);
        } catch (IOException e){
        	e.printStackTrace();
        	throw  new RuntimeException("下载文件从FastDFS异常。");
        } finally {
        	if (trackerServer != null){
        		
        		try{
        			trackerServer.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        	if (out != null){
        		try{
        			out.close();
        		} catch (IOException e){
        			e.printStackTrace();
        		}
        	}
        }
    }
	/**
	 * 下载文件
	 * @Title: download
	 * @author: XuXinYing
	 * @param fileId 文件id
	 * @param out 输出
	 * @throws Exception 异常
	 */
	public byte[] downloadFile(String fileId) throws IOException, MyException {

		TrackerServer trackerServer = null;
		byte[] result;

		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, null);
			result = client.download_file1(fileId);
			if(result!=null){
				logger.info(":*************"+fileId+"****字节流长度为"+result.length+"**********");
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("********************"+fileId+"下载文件从FastDFS异常********************");
			throw new RuntimeException("下载文件从FastDFS异常。");
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
    /**
     * 下载文件
     * @Title: download 
     * @author: XuXinYing
     * @param fileId 文件id
     * @param destFile destFile
     * @return int
     * @throws Exception 异常
     */
    public int download(String fileId, String destFile) throws IOException, MyException {// 0成功
		TrackerServer trackerServer = null;
		int result;

		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, null);
			result = client.download_file1(fileId, destFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("下载文件从FastDFS异常。");
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result;
    }

    /**
     * 下载文件
     * @Title: download 
     * @author: XuXinYing
     * @param fileId 文件ID
     * @param destFile destFile
     * @return int
     * @throws Exception 异常
     */
    public int download(String fileId, File destFile) throws Exception {// 0成功
        TrackerServer trackerServer = null;
		int result;
		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, null);
			result = client.download_file1(fileId, destFile.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("下载文件从FastDFS异常。");
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result;
    }

    /**
     * 删除
     * @Title: delete 
     * @author: XuXinYing
     * @param fileId 文件id
     * @return int
     * @throws Exception 异常
     */
    public int delete(String fileId) throws IOException, MyException {// 0成功
		TrackerServer trackerServer = null;
		int result;

		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, null);
			result = client.delete_file1(fileId);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("删除文件从FastDFS异常。");
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
    }

    /**
     * 文件流转字节数组
     * @param input 文件输入
     * @return 字节
     * @throws IOException
     */
    public byte[] streamToArray(InputStream input) throws IOException {
        if(input==null){
            return null;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        final byte[] buf = new byte[1024];
        int len = 0;
        try {
        	 while ((len = input.read(buf)) != -1) {
                 out.write(buf, 0, len);
             }
        } catch (IOException e){
        	e.printStackTrace();
        	throw new RuntimeException("获取文件信息从FastDFS异常。");
        } finally {
        	if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        }
        return out.toByteArray();
    }

    /**
     * 获取文件信息
     * @param fileId 文件id
     * @return FileInfo
     * @throws Exception 异常
     */
    public FileInfo getFileInfo(String fileId) throws IOException, MyException {
    	FileInfo fileInfo;
    	TrackerServer trackerServer = null;

		try {
			TrackerClient trackerClient = new TrackerClient();
			trackerServer = trackerClient.getConnection();
			StorageClient1 client = new StorageClient1(trackerServer, null);
			fileInfo = client.get_file_info1(fileId);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("获取文件信息从FastDFS异常。");
		} finally {
			if (trackerServer != null) {
				try {
					trackerServer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return fileInfo;
    }
}
