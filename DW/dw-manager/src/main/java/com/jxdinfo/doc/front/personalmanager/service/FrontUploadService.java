package com.jxdinfo.doc.front.personalmanager.service;

import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/10.
 */
public interface FrontUploadService {
    /**
     * 动态加载文件树
     *
     * @return 目录信息
     */
    public List<FsFolder> getTreeDataLazy(FsFolderParams fsFolderParams);

    public List<Map> getChildCountList(List list, List groupList,
                                       String userId, Integer adminFlag, String type, String levelCodeString);

    public List<Map> checkChildCount(List<FsFolder> list, List<Map> childCountList);
    public List<FsFolder> getChildList(List list, List groupList,
                                       String userId, Integer adminFlag, String type, String levelCodeString);
}
