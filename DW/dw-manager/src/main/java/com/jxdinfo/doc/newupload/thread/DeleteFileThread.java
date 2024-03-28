package com.jxdinfo.doc.newupload.thread;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.docutil.service.impl.PdfServiceImpl;
import com.jxdinfo.doc.manager.docmanager.dao.FilesMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocDelete;
import com.jxdinfo.doc.manager.docmanager.service.DocDeleteService;
import com.jxdinfo.doc.newupload.service.UploadService;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author luzhanzhao
 * @date 2019-1-9
 * @description 删除文件的线程
 */
public class DeleteFileThread extends Thread {

    static final public Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);

    private CacheToolService cacheToolService = SpringContextHolder.getBean(CacheToolService.class);
    private DocDeleteService docDeleteService = SpringContextHolder.getBean(DocDeleteService.class);
    private UploadService uploadService = SpringContextHolder.getBean(UploadService.class);
    /**
     * fs_file Mapper 接口
     */
    private FilesMapper filesMapper = SpringContextHolder.getBean(FilesMapper.class);
    @Override
    public void run() {
        while (true) {
            try {
                if (!cacheToolService.getFastDFSUsingFlag()) {
                    break;
                }
                String address = InetAddress.getLocalHost().toString().replace(".","");
                QueryWrapper wrapper = new QueryWrapper();
                Date date = new Date();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, -3);//三小时前的时间
                calendar.getTime();

                wrapper.lt("create_time", calendar.getTime());
                wrapper.eq("server_address", address);
                List<DocDelete> list = docDeleteService.list(wrapper);
                //获取带转化列表
                if (null != list && list.size() >= 1) {//判断待转换列表是否为空
                    for (int i = 0; i < list.size(); i++) {
                        String path = list.get(i).getFilePath();
                        File file = new File(path);
                        if (file.exists()&&address.equals(list.get(i).getServerAddress())) {
                            String fileId = list.get(i).getFileId();
                            String title=  filesMapper.selectById(fileId).getFileName();
                            LOGGER.info("******************文件:"+title+"正在执行删除******************");
                            boolean fileIsExist = file.delete();
                                if (fileIsExist) {
                                    LOGGER.info("******************文件:"+title+"删除成功******************");
                                    docDeleteService.removeById(list.get(i).getFileId());
                                }else{
                                   /* LOGGER.info("******************文件:"+title+"删除失败******************");*/
                                }
                        } else {
                            try {
                                address = InetAddress.getLocalHost().toString().replace(".", "");
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                                if (address.equals(list.get(i).getServerAddress())) {
                                    docDeleteService.removeById(list.get(i).getFileId());
                            }
                        }
                    }
                    Thread.sleep(7200000);
                } else {//如果待转化列表为空，则进入休眠，休眠时长为10s
                    try {
                        //休眠1小时
                        Thread.sleep(7200000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
              /*  LOGGER.error("删除文件失败：" + ExceptionUtils.getErrorInfo(e));*/
            }
        }
    }
}