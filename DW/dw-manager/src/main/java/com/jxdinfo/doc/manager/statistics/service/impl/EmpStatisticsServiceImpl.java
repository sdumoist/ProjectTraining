package com.jxdinfo.doc.manager.statistics.service.impl;

import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.common.util.MathUtil;
import com.jxdinfo.doc.manager.statistics.dao.DocSpaceMapper;
import com.jxdinfo.doc.manager.statistics.dao.EmpStatisticsMapper;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.doc.manager.statistics.service.EmpStatisticsService;
import com.jxdinfo.hussar.bsp.organ.dao.SysOrganMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmpStatisticsServiceImpl
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/12/4
 * @Version: 1.0
 */

@Service
public class EmpStatisticsServiceImpl implements EmpStatisticsService {

    @Resource
    private EmpStatisticsMapper empStatisticsMapper;
    @Resource
    private DocSpaceMapper docSpaceMapper;
    @Resource
    private SysOrganMapper sysOrganMapper;
    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Value("${SPACE.SIZE}")
    private double SpaceSize;
    /**
     * 修改个人空间
     *
     * @author yjs
     * @date 2018/8/28
     */
    @Override
    public void updateSpace(String id, String space) {
        DocSpace docSpace = docSpaceMapper.selectById(id);
        if (docSpace != null) {
            docSpace.setSpaceSize(Double.parseDouble(space));
            docSpaceMapper.updateById(docSpace);
        }else{
            docSpace = new DocSpace();
            docSpace.setOrganId(id);
            docSpace.setSpaceSize(Double.parseDouble(space));
            docSpaceMapper.insert(docSpace);
        }
    }

    /**
     * 通过userID得到个人已用空间
     *
     * @param userId 组织机构ID
     * @author wangning
     * @date 2018/9/13
     */
    @Override
    public double getUsedSpaceByUserId(String userId) {
        return empStatisticsMapper.getStatisticsDataByUserId(userId);
    }

    /**
     * 通过ID得到该部门剩余空间
     *
     * @return double
     * @author yjs
     * @date 2018/8/28
     */
    @Override
    public double getStatisticsDataByUserId(String userId, String size) {
        Double fileSize = Double.parseDouble(size.substring(0, size.length() - 2));
        DocSpace docSpace = docSpaceMapper.selectById(userId);
        if (docSpace != null) {
            double share = empStatisticsMapper.getStatisticsDataByUserId(userId);
            return docSpace.getSpaceSize() * 1024 - share - fileSize;
        } else {
            docSpace = new DocSpace();
            docSpace.setOrganId(userId);
            docSpace.setSpaceSize(SpaceSize);
            docSpaceMapper.insert(docSpace);
            return SpaceSize * 1024;
        }
    }



    @Override
    public List<Map<String, Object>> getEmpStatisticsData(String groupId, String uerName, int startIndex, int pageSize) {
        List<Map<String, Object>> list = empStatisticsMapper.getEmpStatisticsData(groupId,uerName,startIndex,pageSize);
        for (Map<String, Object> map : list) {
            double count = (double) map.get("NUM");
            DocSpace docSpace = docSpaceMapper.selectById(map.get("USERID").toString());
            if (docSpace == null) {
                docSpace = new DocSpace();
                docSpace.setOrganId(map.get("USERID").toString());
                docSpace.setSpaceSize(SpaceSize);
                docSpaceMapper.insert(docSpace);
            }
            Double initSpace = docSpace.getSpaceSize();
            double newCount = initSpace * 1024 - count;
            if (newCount < 0) {
                newCount = 0;
            }
            String unit = FileTool.doubleToString(initSpace*1024).substring(FileTool.doubleToString(initSpace*1024).length()-2);
            //部门名称
            map.put("orgName", map.get("orgName"));
            //SpaceNum总空间大小
            if ("GB".equals(unit)){
                map.put("SpaceNum", FileTool.doubleToString(initSpace*1024).substring(0,FileTool.doubleToString(initSpace*1024).length()-3));
            }else {
                map.put("SpaceNum", (int)Double.parseDouble(FileTool.doubleToString(initSpace*1024).substring(0,FileTool.doubleToString(initSpace*1024).length()-3)));
            }
            //单位 GB/MB
            map.put("unit",unit);

            //判断显示方式（GB,MB,KB）NUM可用空间
            map.put("NUM", FileTool.doubleToString(newCount));

            // 查询该用户是否为管理员
            List<String> roleList = sysUserRoleService.getRolesByUserId(map.get("USERID").toString());
            //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
            Integer adminFlag = CommonUtil.getAdminFlag(roleList);
            if (adminFlag == 1) {
                map.put("SpaceNum","不限");
                map.put("NUM","不限");
                map.put("adminFlag","1");
            } else {
                map.put("adminFlag","0");
            }

            //判断显示方式（GB,MB,KB）USERNUM已用空间
            map.put("USERNUM",FileTool.doubleToString(count));
        }
        return list;
    }

    @Override
    public Map<String, String> getSpaceByUserId(String id, Integer adminFlag) {
        Map<String, String> map = new HashMap();
        DocSpace docSpace = docSpaceMapper.selectById(id);
        if (adminFlag != 1) {
            if(docSpace == null){
                docSpace = new DocSpace();
                docSpace.setOrganId(id);
                docSpace.setSpaceSize(SpaceSize);
                docSpaceMapper.insert(docSpace);
            }
            //limit是否为无限空间--超管无限空间
            map.put("limit", "1");
            double share = empStatisticsMapper.getStatisticsDataByUserId(id);
            //total总空间大小
            map.put("total", FileTool.doubleToString(docSpace.getSpaceSize()*1024));
            Double lackSize = share;
            if (lackSize < 0) {
                share = docSpace.getSpaceSize();
            }
            double present = share / (docSpace.getSpaceSize() * 1024 ) * 100;
            if(present>100){
                present=100;
            }
            //present已用空间占比
            map.put("present", MathUtil.getDecimal(present, 2) + "");
            //lack已用空间
            if (share>=docSpace.getSpaceSize()*1024){
                map.put("lack", FileTool.doubleToString(Double.parseDouble((String.valueOf(docSpace.getSpaceSize()*1024)))));
            }else {
                map.put("lack", FileTool.doubleToString(Double.parseDouble((String.valueOf(share)))));
            }

        } else {
            map.put("limit", "0");
        }
        return map;
    }
}
