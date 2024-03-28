package com.jxdinfo.doc.newupload.service.impl;

import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.front.docmanager.dao.FrontDocInfoMapper;
import com.jxdinfo.doc.manager.docmanager.model.FsFile;
import com.jxdinfo.doc.newupload.dao.UploadMapper;
import com.jxdinfo.doc.newupload.service.UploadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UploadServiceImpl implements UploadService {
    /**
     * 文件上传状态的dao层
     */
    @Resource
    private UploadMapper uploadMapper;

    @Resource
    private FrontDocInfoMapper frontDocInfoMapper;
    /**
     * 缓存服务
     */
    @Resource
    private CacheToolService cacheToolService;
    @Override
    public void newUploadState(Map map){
        uploadMapper.newUploadState(map);
    }

    @Override
    public List<Map<String,String>> getUploadState() {
        return uploadMapper.getUploadState();
    }

    @Override
    public int updateUploadState(Map map) {
        return uploadMapper.updateUploadState(map);
    }

    @Override
    public void deleteUploadState(Map map) {
        if (null != map.get("docId")){
            uploadMapper.deleteUploadState(map.get("docId").toString());
        }
    }

    @Override
    public void deleteUploadState(String docId) {
        uploadMapper.deleteUploadState(docId);
    }

    @Override
    public boolean checkUploadState(String docId){
        List<Map<String,String>> changePdf = cacheToolService.getUploadStateList();
        return checkUploadStateFromFast(docId);
    }

    @Override
    public boolean checkUploadStateFromFast(String docId) {
        String pdfPath = null;
        pdfPath = frontDocInfoMapper.getPdfPathById(docId);
        if (pdfPath == null || pdfPath.equals("")){
            FsFile fsfile=frontDocInfoMapper.selectDocId(docId);
            if(fsfile!=null){
                String md5=fsfile.getMd5();
                if(md5!=null&&!"".equals(md5)){
                    List<String>  path=   frontDocInfoMapper.selectPdfPath(md5,docId);
                    List<String>  pdfKey=   frontDocInfoMapper.selectKey(md5,docId);
                    if(path!=null&&pdfKey!=null&&path.size()>0&&pdfKey.size()>0){
                        frontDocInfoMapper.updatePdfPath(path.get(0),pdfKey.get(0),docId);
                    }
                }
            }

            return  !(frontDocInfoMapper.getPdfPathById(docId) == null || frontDocInfoMapper.getPdfPathById(docId).equals(""));
        }
        return true;
    }
    @Override
    public boolean checkVideoStateFromFast(String docId) {
        String pdfPath = null;
        pdfPath = frontDocInfoMapper.getPdfPathById(docId);
        if (pdfPath == null || pdfPath.equals("")){
         return false;
            }else if(!pdfPath.endsWith(".mp4")){
            return false;
        }else{

            return  true;
        }

    }
    @Override
    public List<Map<String,String>> selectUpload(String docId) {
        return uploadMapper.selectUpload(docId);
    }
}