package com.jxdinfo.doc.front.docmanager.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

/**
 * 类的用途：获取首页文件目录<p>
 * 创建日期：<br>
 * 修改历史：<br>
 * 修改日期：2018年9月6日 <br>
 * 修改作者：yjs <br>
 * 修改内容：重构代码 <br>
 */

public interface FrontFsFileMapper {
    /**
     * 获取超级管理员首页文件目录
     * @param id   根目录ID
     * @return List<FsFolder> 文件夹集合
     */
    List<FsFolder> getFsFolderListBySuperAdmin(@Param("id") String id);


    /**
     * 获取下载次数等信息
     */
    List<Map> getInfo(@Param("list") List list, @Param("userId") String userId, @Param("groupList") List<String> listGroup, @Param("roleList") List roleList);

    /**
     * 查看子文件夹数量
     *
     * @param id
     * @return 节点ID字符串
     */
    int getNumByChildFloder(@Param("id") String id);

}
