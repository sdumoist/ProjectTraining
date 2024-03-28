package com.jxdinfo.doc.manager.docconfigure.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;

import java.util.List;

/**
 * Created by zoufeng on 2018/10/15.
 */
public interface DocConfigureService extends IService<DocConfigure> {

    /**
     * @Author zoufeng
     * @Description 获取配置信息
     * @Date 11:43 2018/10/15
     * @Param []
     * @return java.util.List<com.jxdinfo.doc.manager.docconfigure.model.DocConfigure>
     **/
    List<DocConfigure> getConfigure();

    /**
     * @Author zoufeng
     * @Description 保存
     * @Date 16:26 2018/10/15
     * @Param [docConfigure]
     * @return boolean
     **/
    public boolean save(DocConfigure docConfigure);

    boolean upDateFolder(String folderName);
}
