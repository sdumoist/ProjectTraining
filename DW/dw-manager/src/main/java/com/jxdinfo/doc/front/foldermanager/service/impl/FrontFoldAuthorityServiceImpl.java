package com.jxdinfo.doc.front.foldermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.front.foldermanager.dao.FrontFoldAuthorityMapper;
import com.jxdinfo.doc.front.foldermanager.dao.FrontFolderMapper;
import com.jxdinfo.doc.front.foldermanager.service.FrontFoldAuthorityService;
import com.jxdinfo.doc.manager.foldermanager.dao.DocFoldAuthorityMapper;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
@Service
public class FrontFoldAuthorityServiceImpl extends ServiceImpl<DocFoldAuthorityMapper, DocFoldAuthority>
        implements FrontFoldAuthorityService {

    /**
     * Mapper
     */
    @Resource
    private FrontFoldAuthorityMapper frontFoldAuthorityMapper;
    @Resource
    private FrontFolderMapper frontFolderMapper;

    @Resource
    private BusinessService businessService;


    @Override
    public int findEdit(String id, List groupList, String userId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = frontFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId("333");
            fsFolderParams.setType("");
            String levelCodes = businessService.getUpLevelCodeByUser(fsFolderParams);
            String levelCodeNew = "";
            if (levelCodes.length() != 0) {
                levelCodeNew = levelCodes.substring(1, levelCodes.length() - 1);
                String[] codeList = levelCodeNew.split(",");
                for (int i = 0; i < codeList.length; i++) {
                    if (codeList[i].equals("'" + code + "'")) {
                        editValue = 1;
                        break;
                    }
                }
            } else {
                editValue = 0;
            }
            fsFolderParams.setType("2");
            levelCodes = businessService.getLevelCodeByUserUpload(fsFolderParams);
            if (levelCodes.length() != 0) {
                levelCodeNew = levelCodes.substring(1, levelCodes.length() - 1);
                String[] codeListNew = levelCodeNew.split(",");
                for (int i = 0; i < codeListNew.length; i++) {
                    if (codeListNew[i].equals("'" + code + "'")) {
                        editValue = 2;
                        break;
                    }
                }
            }

        }
        return editValue;
    }

}
