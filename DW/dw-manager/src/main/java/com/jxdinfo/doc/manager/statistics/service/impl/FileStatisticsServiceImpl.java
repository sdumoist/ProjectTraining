package com.jxdinfo.doc.manager.statistics.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxdinfo.doc.manager.statistics.dao.FileStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.service.FileStatisticsService;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 类的用途：文件统计Service实现<p>
 * 创建日期：2018年9月26日 <br>
 * 修改历史：<br>
 * 修改日期：2018年9月26日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author XuXinYing
 * @version 1.0
 */
@Service
public class FileStatisticsServiceImpl implements FileStatisticsService {

    /**
     * fileStatisticsMapper接口
     */
    @Resource
    private FileStatisticsMapper fileStatisticsMapper;

    /**
     * 获取用户上传文档预览量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @author: XuXinYing
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getUserPreviewData(String opType) {
        List<String> namelist = new ArrayList<String>();
        List<Integer> numlist = new ArrayList<Integer>();
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<Map> list = this.fileStatisticsMapper.getUserPreviewData(opType);
        for (Map map : list) {
            if (ToolUtil.isNotEmpty(map.get("AUTHOR_ID"))) {
                namelist.add(map.get("AUTHOR_ID").toString());
                numlist.add(Integer.valueOf(map.get("YLSUM").toString()));
            }

        }
        dataMap.put("list", namelist);
        dataMap.put("numList", numlist);
        return dataMap;
    }

    /**
     * 获取部门上传文档下载或预览量
     * @Title: getUserPreviewData 
     * @param opType 操作类型
     * @author: XuXinYing
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Map<String, Object> getDeptData(String opType) {
        Map<String, Object> result = new HashMap<>();
        List<Map> list = this.fileStatisticsMapper.getDeptData(opType);
        List<String> namelist = new ArrayList<String>();
        List<Integer> numlist = new ArrayList<Integer>();
        for (Map map : list) {
            if (ToolUtil.isNotEmpty(map.get("SHORT_NAME"))) {
                namelist.add(map.get("SHORT_NAME").toString());
                numlist.add(Integer.valueOf(map.get("YLSUM").toString()));
            }
        }
        result.put("list", namelist);
        result.put("numList", numlist);
        return result;
    }

    /**
     * 查询文件列表包含下载预览
     * @Title: getFileListData 
     * @author: XuXinYing
     * @param page
     * @param opType 操作类型
     * @return List
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List getFileListData(Page page, @Param("opType") String opType) {
        return this.fileStatisticsMapper.getFileListData(page, opType);
    }

    /**
     * 获取上传文档数量
     * @Title: getUploadData 
     * @param type 类型
     * @author: XuXinYing
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<Map> getUploadData(String type) {
        List<Map> list = null;
//        Map<String, Object> result = new HashMap<>();
//        List<String> namelist = new ArrayList<String>();
//        List<Integer> numlist = new ArrayList<Integer>();
        if ("user".equals(type)) {
            list = this.fileStatisticsMapper.getUserFileUploadData();
//            for (Map map : list) {
//                if (ToolUtil.isNotEmpty(map.get("AUTHOR_ID"))) {
//                    namelist.add(map.get("AUTHOR_ID").toString());
//                    numlist.add(Integer.valueOf(map.get("FILENUM").toString()));
//                }
//            }
        } else {
            list = this.fileStatisticsMapper.getDeptFileUploadData();
//            for (Map map : list) {
//                if (ToolUtil.isNotEmpty(map.get("SHORT_NAME"))) {
//                    namelist.add(map.get("SHORT_NAME").toString());
//                    numlist.add(Integer.valueOf(map.get("FILENUM").toString()));
//                }
//            }
        }
//        result.put("list", namelist);
//        result.put("numList", numlist);
        return list;
    }

    /**
     * 查询文件数量
     * @Title:
     * @author: bjj
     * @param
     * @param
     * @return List
     */
    @Override
    public List<String> getFileNums() {
        return this.fileStatisticsMapper.getFileNumData();
    }

    /**
     * 活跃度分析
     * @Title: getDeptActive
     * @author: bjj
     * @return List
     */
    @Override
    public List<Map> getDeptActive(String searchTime){
        return  this.fileStatisticsMapper.getDeptActive(searchTime);
    }

    @Override
    public List<Map> getFileListDataAllPerson(Page page, String opType) {
        return this.fileStatisticsMapper.getFileListDataAllPerson(page,opType);
    }

    @Override
    public int getFileListDataAllPersonCount(String opType) {
        return this.fileStatisticsMapper.getFileListDataAllPersonCount(opType);
    }

    @Override
    public int getFilesCount() {
        return fileStatisticsMapper.getFilesCount();
    }

    @Override
    public int getFilesCountByIdAndFlag(String id, String flag) {
        return fileStatisticsMapper.getFilesCountByIdAndFlag(id,flag);
    }
    @Override
    public List<Map> getOrigin(String searchTime){
        return  this.fileStatisticsMapper.getOrigin(searchTime);
    }

    /**
     * 查询文件预览排行
     *
     * @param page
     * @return
     */
    @Override
    public List getPreviewRankListData(Page page) {
        return this.fileStatisticsMapper.getPreviewRankListData(page);
    }

    /**
     * 查询文件下载排行
     *
     * @param page
     * @return
     */
    @Override
    public List getDownloadRankListData(Page page) {
        return this.fileStatisticsMapper.getDownloadRankListData(page);
    }

}
