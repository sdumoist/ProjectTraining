package com.jxdinfo.doc.client.docmanager.service.impl;

import com.jxdinfo.doc.client.docmanager.service.ClientFilesService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.jwt.util.JWTUtil;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.doc.manager.groupmanager.service.DocGroupService;
import com.jxdinfo.doc.manager.statistics.model.DocSpace;
import com.jxdinfo.doc.manager.statistics.service.DocSpaceService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xubin
 * @version 1.0
 * @since 2019/12/10 9:34
 * <p>
 * ClientFilesServiceImpl
 * </p>
 */
@Service
public class ClientFilesServiceImpl implements ClientFilesService {
    @Resource
    private JWTUtil jwtUtil;

    @Resource
    private SysStruMapper sysStruMapper;

    /**
     * 部门空间类接口
     */
    @Autowired
    private DocSpaceService docSpaceService;

    /**
     * 权限群组服务类
     */
    @Autowired
    private DocGroupService docGroupService;

    /**
     * 文档管理服务类
     */
    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    @Autowired
    private ISysUsersService iSysUsersService;
    /**
     * 缓存工具类接口
     */
    @Autowired
    private CacheToolService cacheToolService;

    @Autowired
    private ISysUserRoleService sysUserRoleService;

    @Value("${SPACE.SIZE}")
    private double SpaceSize;

    /**
     * 检查个人存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author lishilin
     */
    @Override
    public Map<String, Object> checkEmpSpace(String fileSize) {
        String userId = jwtUtil.getSysUsers().getUserId();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        if (adminFlag != 1) {
            String deptId = jwtUtil.getSysUsers().getDepartmentId();
            SysStru sysStru = sysStruMapper.selectById(deptId);
            String orgId = sysStru.getOrganId();
            Double deptSpace;
            if (docSpaceService.getDocSpaceByDeptId(userId) == null) {
                DocSpace docSpace = new DocSpace();
                docSpace.setOrganId(userId);
                docSpace.setSpaceSize(SpaceSize);
                deptSpace = SpaceSize;
            } else {
                deptSpace = docSpaceService.getDocSpaceByDeptId(userId).getSpaceSize();
            }

            Double newSize = StringUtil.getDouble(fileSize.substring(0, fileSize.length() - 2));

            Double emptySize = 0d;
            double usedSpace = cacheToolService.getDeptUsedSpace(userId);
            if (deptSpace == null) {
                //空间不足
                deptSpace = 0d;
            }
            emptySize = deptSpace * 1024 - usedSpace - newSize;
            if (emptySize < 0) {
                //空间不足
                resultMap.put("flag", false);
                resultMap.put("size", 0d);
            } else {
                //更新缓存中已用空间数
                cacheToolService.updateDeptUsedSpace(userId, newSize);
                resultMap.put("flag", true);
                resultMap.put("size", 0d - newSize);
            }
        } else {
            resultMap.put("flag", true);
            resultMap.put("size", 0d);
        }
        return resultMap;
    }

    /**
     * @param childCountList
     * @return java.util.List<java.util.Map>
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     */
    @Override
    public List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList) {
        String userId = jwtUtil.getSysUsers().getUserId();
        List<Map> resultList = new ArrayList<>();
        List<String> listGroup = docGroupService.getPremission(userId);
        String deptId = iSysUsersService.getById(userId).getDepartmentId();
        List<String> roleList = sysUserRoleService.getRolesByUserId(userId);
        //获得权限标识符 1为超管和文库管理员，2为部门负责人管理员，3普通用户
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        for (int j = 0; j < list.size(); j++) {
            FsFolder fsFolderChild = list.get(j);
            Map childMap = new HashMap();
            childMap.put("id", fsFolderChild.getFolderId());
            childMap.put("text", fsFolderChild.getFolderName());
            childMap.put("pid", fsFolderChild.getParentFolderId());
            childMap.put("path", fsFolderChild.getFolderPath());
            childMap.put("isEdit", fsFolderChild.getIsEdit());
            for (int i = 0; i < childCountList.size(); i++) {

                Map map = childCountList.get(i);
                if (fsFolderChild.getFolderId().equals(map.get("id"))) {

                    if (Integer.valueOf(map.get("num").toString()) > 0) {
                        childMap.put("children", true);
                    } else {
                        childMap.put("children", false);

                    }

                }

            }
            if (adminFlag == 1) {
                childMap.put("result", true);
            } else {
                int isEdits = docFoldAuthorityService.findEditClient(fsFolderChild.getFolderId(), listGroup, userId, deptId);
                if (isEdits != 2) {
                    childMap.put("result", false);
                } else {
                    childMap.put("result", true);
                }
            }
            resultList.add(childMap);
        }
        return resultList;
    }
}
