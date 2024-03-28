package com.jxdinfo.doc.manager.statistics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.MathUtil;
import com.jxdinfo.doc.manager.statistics.dao.DeptStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.dao.DocSpaceMapper;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.doc.manager.statistics.service.DeptStatisticsService;
import com.jxdinfo.hussar.bsp.organ.dao.SysOrganMapper;

/**
 * 部门文件大小
 * 作者：yjs ;
 * 修改内容：
 *
 * @author yjs ;
 * @version 1.0
 */
@Service
public class DeptStatisticsServiceImpl implements DeptStatisticsService {

    @Resource
    private DeptStatisticsMapper deptStatisticsMapper;
    @Resource
    private DocSpaceMapper docSpaceMapper;
    @Resource
    private SysOrganMapper sysOrganMapper;

    /**
     * 获取列表页数据
     *
     * @return List<Map<String, Object>>
     * @author yjs
     * @date 2018/8/28
     */
    @Override
    public List<Map<String, Object>> getStatisticsData() {
        List<Map<String, Object>> list = deptStatisticsMapper.getStatisticsData();
        for (Map<String, Object> map : list) {
            double count = (double) map.get("NUM");
            DocSpace docSpace = docSpaceMapper.selectById(map.get("ORGAN_ID") + "");
            if (docSpace == null) {
                continue;
            }
            Double initSpace = docSpace.getSpaceSize();
            double newCount = initSpace * 1024 * 1024 - count;
            if (newCount < 0) {
                newCount = 0;
            }
            map.put("INIT_SPACE", initSpace + "GB");
            
            //判断显示方式（GB,MB,KB）
            map.put("NUM",FileTool.doubleToString(newCount));
            
            //判断显示方式（GB,MB,KB）
            map.put("USERNUM",FileTool.doubleToString(count));
        }
        return list;
    }

    /**
     * 通过ID得到该部门剩余空间
     *
     * @return double
     * @author yjs
     * @date 2018/8/28
     */
    @Override
    public double getStatisticsDataByOrganId(String organId, String size) {
        Double fileSize = Double.parseDouble(size.substring(0, size.length() - 2));
        DocSpace docSpac = docSpaceMapper.selectById(organId);
        if (docSpac != null) {
            double share = deptStatisticsMapper.getStatisticsDataByOrganId(organId);
            return docSpac.getSpaceSize() * 1024 * 1024 - share - fileSize;
        } else {
            return 50 * 1024 * 1024;
        }
    }
    
    /**
     * 通过orgID得到该部门已用空间
     *
     * @param organId 组织机构ID
     * @author wangning
     * @date 2018/9/13
     */
    @Override
    public double getUsedSpaceByOrganId(String organId) {
    	return deptStatisticsMapper.getStatisticsDataByOrganId(organId);
    }

    /**
     * 修改部门的剩余空间
     *
     * @author yjs
     * @date 2018/8/28
     */
    @Override
    public void updateSpace(String id, String space) {
        DocSpace docSpac = docSpaceMapper.selectById(id);
        if (docSpac != null) {
            docSpac.setSpaceSize(Double.parseDouble(space));
            docSpaceMapper.updateById(docSpac);
        }
    }

    @Override
    public Map<String, String> getSpaceByOrganId(String id, Integer adminFlag) {
        Map<String, String> map = new HashMap();
        DocSpace docSpace = docSpaceMapper.selectById(id);
        if (docSpace != null && adminFlag != 1) {
            map.put("limit", "1");
            double share = deptStatisticsMapper.getStatisticsDataByOrganId(id);
            map.put("total", docSpace.getSpaceSize() + "GB");
            Double lackSize = share;
            if (lackSize < 0) {
                share = docSpace.getSpaceSize();
            }
            double present = share / (docSpace.getSpaceSize() * 1024 * 1024) * 100;
            if(present>100){
                present=100;
            }
            map.put("present", MathUtil.getDecimal(present, 2) + "");
            map.put("lack", FileTool.doubleToString(Double.parseDouble((String.valueOf(share)))));;
        } else {
            map.put("limit", "0");
        }
        return map;
    }
}
