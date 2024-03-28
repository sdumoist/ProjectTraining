package com.jxdinfo.doc.front.personalmanager.service.impl;

import com.jxdinfo.doc.front.foldermanager.dao.FrontFolderMapper;
import com.jxdinfo.doc.front.personalmanager.service.FrontUploadService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/10.
 */
@Service
public class FrontUploadServiceImpl implements FrontUploadService {
    /**
     * 目录dao层
     */
    @Resource
    private FrontFolderMapper frontFolderMapper;
    @Override
    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams) {
        List<FsFolder> list = null;
        //获取目录结构
        //超级管理员：1 文库管理员：2
        if (fsFolderParams.getAdminFlag() == 1) {
            //获取节点的信息
            list = frontFolderMapper.getTreeDataLazyBySuper(fsFolderParams.getId());
        } else {
            if ("root".equals(fsFolderParams.getId())) {
                //查询当前用户所拥有的目录
                list = frontFolderMapper.getTreeDataLazy(fsFolderParams.getId(), fsFolderParams.getGroupList(), fsFolderParams.getUserId(), fsFolderParams.getType());
            } else {
//                List<String> levelCodeList = folderService.getlevelCodeList(fsFolderParams.getGroupList(), fsFolderParams.getUserId(), fsFolderParams.getType());
                list = frontFolderMapper.selectByLevelCode(fsFolderParams.getId(), fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString());
            }
        }
        return list;
    }

    @Override
    public List<Map> getChildCountList(List list, List groupList, String userId, Integer adminFlag, String type, String levelCodeString) {
        List<Map> listMap = null;
        if (adminFlag == 1) {
            listMap = frontFolderMapper.getChildCountListForSuperAdmin(list);
        } else {
            listMap = frontFolderMapper.getChildCount(list, userId, levelCodeString);
        }

        return listMap;
    }

    @Override
    public List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList) {
        List<Map> resultList = new ArrayList<>();

        for (int j = 0; j < list.size(); j++) {
            FsFolder fsFolderChild = list.get(j);
            Map childMap = new HashMap();

            childMap.put("id", fsFolderChild.getFolderId());
            childMap.put("text", fsFolderChild.getFolderName());
            childMap.put("pid", fsFolderChild.getParentFolderId());

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
            resultList.add(childMap);
        }
        return resultList;
    }

    @Override
    public List<FsFolder> getChildList(List list, List groupList, String userId, Integer adminFlag, String type, String levelCodeString) {
        List<FsFolder> listFsFolder = new ArrayList<FsFolder>();
        if (adminFlag == 1) {
            listFsFolder = frontFolderMapper.getChildListForSuperAdmin(list);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
            listFsFolder = frontFolderMapper.selectByLevelCodeList(list, userId, levelCodeString);
        }
        return listFsFolder;
    }
}
