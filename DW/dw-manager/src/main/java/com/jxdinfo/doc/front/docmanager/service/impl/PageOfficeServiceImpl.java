package com.jxdinfo.doc.front.docmanager.service.impl;

import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.front.docmanager.service.PageOfficeService;
import com.jxdinfo.doc.manager.docmanager.dao.DocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.dao.FsFileMapper;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.hussar.encrypt.file.FileEncryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PageOfficeServiceImpl implements PageOfficeService {

    @Autowired
    private DocInfoMapper docInfoMapper;

    /**
     * fast服务器服务类
     */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * 文件 Mapper 接口
     */
    @Resource
    private FsFileMapper fsFileMapper;

    /**
     * 临时文件夹
     */
    @Value("${docbase.filedir}")
    private String tempdir;

    @Override
    public String getEditFileByFast(String docId) {
        FileOutputStream fos = null;
        byte[] buffer = null;
        byte[] bytes = null;
        DocInfo docInfo = docInfoMapper.getDocDetail(docId);
        String path = docInfo.getFilePath();
        String suffix = path.substring(path.lastIndexOf("."));
        try {
            bytes = fastdfsService.download(path);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        List<FsFile> list = fsFileMapper.getInfoByPath(path);
        if (list != null && list.size() > 0) {
            if (list.get(0).getSourceKey() == null) {
                return null;
            }
            String random = UUID.randomUUID().toString().replaceAll("-", "");
            File file = new File(tempdir + "\\" + random + suffix);
            try {
                fos = new FileOutputStream(file);
                fos.write(bytes, 0, bytes.length);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //文件解密
            boolean isDecrypt = FileEncryptUtil.getInstance().decrypt(tempdir + "\\" + random + suffix, list.get(0).getSourceKey());
            return  tempdir + "\\" + random + suffix;
        }
        else {
            return null;
        }

    }
}
