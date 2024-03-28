package com.jxdinfo.doc.manager.docconfigure.service.impl;/**
 * Created by zoufeng on 2018/10/15.
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.manager.docconfigure.dao.DocConfigureMapper;
import com.jxdinfo.doc.manager.docconfigure.model.DocConfigure;
import com.jxdinfo.doc.manager.docconfigure.service.DocConfigureService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName DocConfigService
 * @Description TODO
 * @Author zoufeng
 * @Date 2018/10/15 11:44
 * @Version 1.0
 **/
@Service
public class DocConfigureServiceImpl extends ServiceImpl<DocConfigureMapper,DocConfigure> implements DocConfigureService{

    /** 配置信息Mapper*/
    @Resource
    private DocConfigureMapper docConfigureMapper;

    @Resource
    private CacheToolService cacheToolService;

    /**
     * @Author zoufeng
     * @Description 获取配置信息
     * @Date 11:43 2018/10/15
     * @Param []
     * @return java.util.List<com.jxdinfo.doc.manager.docconfigure.model.DocConfigure>
     **/
    public List<DocConfigure> getConfigure(){
        return docConfigureMapper.getConfigure();
    }

    /**
     * @Author zoufeng
     * @Description 保存
     * @Date 16:26 2018/10/15
     * @Param [docConfigure]
     * @return boolean
     **/
    public boolean save(DocConfigure docConfigure){
        boolean res = false;
        String[] id = docConfigure.getId().split(",");
        String[] configKey = docConfigure.getConfigKey().split(",");
        String[] configValue = docConfigure.getConfigValue().split("@");
        String[] configValidFlag = docConfigure.getConfigValidFlag().split(",");

        for(int i = 0 ; i < id.length ; i ++){
            DocConfigure docConfigureinfo = new DocConfigure();
            docConfigureinfo.setId(id[i]);
            docConfigureinfo.setConfigKey(configKey[i]);
            docConfigureinfo.setConfigValue(configValue[i]);
            docConfigureinfo.setConfigValidFlag(configValidFlag[i]);
            res = updateById(docConfigureinfo);
        }
        // 更新session中的配置
        ShiroKit.getSubject().getSession().setAttribute("projectTitle", configValue[10]);
        ShiroKit.getSubject().getSession().setAttribute("clientShow", configValue[12]);
        ShiroKit.getSubject().getSession().setAttribute("contactShow", configValue[13]);
        // 更新缓存中ip地址
        cacheToolService.updateServerAddress(configValue[11]);
        if (res&&upDateFolder(configValue[8])){
            res = true;
        }
        return res;
    }

    @Override
    public boolean upDateFolder(String folderName) {
        boolean isUpDate = false;
        if (docConfigureMapper.upDateFolderName(folderName)>0){
            isUpDate = true;
        }
        return isUpDate;
    }
}
