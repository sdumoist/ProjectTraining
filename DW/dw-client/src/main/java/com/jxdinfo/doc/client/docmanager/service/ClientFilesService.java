package com.jxdinfo.doc.client.docmanager.service;

import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;

import java.util.List;
import java.util.Map;

/**
 * @author xubin
 * @version 1.0
 * @since 2019/12/10 9:34
 * <p>
 * ClientFilesService
 * </p>
 */
public interface ClientFilesService {

    /**
     * 检查个人存储空间
     *
     * @param fileSize
     * @return Map flag: true,充足 false,不足 size:异常时需要缓存释放的资源
     * @author lishilin
     */
    public Map<String,Object> checkEmpSpace(String fileSize);
    /**
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     * @return java.util.List<java.util.Map>
     **/
    List<Map> checkChildCount(List<FsFolder> childList, List<Map> childCountList);
}
