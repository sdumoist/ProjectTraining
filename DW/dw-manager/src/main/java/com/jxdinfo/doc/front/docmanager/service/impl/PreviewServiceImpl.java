package com.jxdinfo.doc.front.docmanager.service.impl;

import com.jxdinfo.doc.front.docmanager.dao.PreviewMapper;
import com.jxdinfo.doc.front.docmanager.service.PreviewService;
import com.jxdinfo.doc.manager.componentmanager.model.ComponentApply;
import com.jxdinfo.doc.manager.componentmanager.service.ComponentApplyService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.doc.manager.middlegroundConsulation.model.MiddlegroundConsulation;
import com.jxdinfo.doc.manager.middlegroundConsulation.service.impl.MiddlegroundConsulationServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 类的用途：预览页面获取预览文件的路径<p>
 * 创建日期：<br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */
@Service
public class PreviewServiceImpl implements PreviewService {

    @Resource
    private ComponentApplyService componentApplyService;

    @Resource
    private MiddlegroundConsulationServiceImpl middlegroundConsulationService;
    @Resource
    private DocInfoService docInfoService;
    /** 文件预览mapper */
    @Resource
    private PreviewMapper previewMapper;

    /** 文件预览mapper */
    @Resource
    private IFsFolderService iFsFolderService;
    /**
     * 获取预览文件的路径
     *
     * @param docId 文件ID
     * @return List<Map<String,String>> 路径文件集合
     */
    public List<Map<String, String>> getFoldPathByDocId(String docId,String showType) {
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        //获取文档所属目录
        Map<String, String> foldInfo = new HashMap<>();
        foldInfo = previewMapper.getFoldInfoByDocId(docId);
        if(foldInfo==null){
            if ("consulation".equals(showType)){
                foldInfo = new HashMap<String, String>();
                String foldId= docInfoService.getDocDetail(docId).getFoldId();
                MiddlegroundConsulation middlegroundConsulation= middlegroundConsulationService.selectById(foldId);
                foldInfo.put("foldId",middlegroundConsulation.getConsulationId());
                foldInfo.put("foldName",middlegroundConsulation.getConsulationTitle());
                foldInfo.put("type","3");
                returnList.add(copyFoldInfoMap(foldInfo));
            }else {
                foldInfo = new HashMap<String, String>();
                String foldId= docInfoService.getDocDetail(docId).getFoldId();
                ComponentApply componentApply= componentApplyService.getById(foldId);
                if(componentApply!= null){
                foldInfo.put("foldId",componentApply.getComponentId());
                foldInfo.put("foldName",componentApply.getComponentName());
                foldInfo.put("type","2");
                }
                returnList.add(copyFoldInfoMap(foldInfo));
            }

        } else{
            returnList.add(copyFoldInfoMap(foldInfo));
        }


        while (foldInfo != null) {
            foldInfo = previewMapper.getFoldInfoByChildFoldId(foldInfo.get("foldId"));
            if (foldInfo != null) {
                returnList.add(copyFoldInfoMap(foldInfo));
            }
        }
        Collections.reverse(returnList);
        return returnList;
    }
    /**
     * 获取预览文件夹的路径
     *
     * @param foldInfo 文件夹ID、文件夹名
     * @return List<Map<String,String>> 路径文件集合
     */
    public List<Map<String, String>> getFoldPathByFolder(Map<String, String> foldInfo) {
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        if (foldInfo != null) {
            returnList.add(copyFoldInfoMap(foldInfo));
        }
        if(foldInfo.get("shareFolder")==null){
            while (foldInfo != null) {
                foldInfo = previewMapper.getFoldInfoByChildFoldId(foldInfo.get("foldId"));
                if (foldInfo != null) {
                    returnList.add(copyFoldInfoMap(foldInfo));
                }
            }
        }
        Collections.reverse(returnList);
        return returnList;
    }

    /**
     * 将目录的ID和NAME放入map中
     *
     * @param fromMap
     * @return List<Map<String,String>>
     */
    private Map<String, String> copyFoldInfoMap(Map<String, String> fromMap) {
        Map<String, String> toMap = new HashMap<String, String>();
        if (fromMap != null) {
            toMap.put("foldId", fromMap.get("foldId"));
            toMap.put("foldName", fromMap.get("foldName"));
            toMap.put("type", fromMap.get("type"));
        }
        return toMap;
    }
}
