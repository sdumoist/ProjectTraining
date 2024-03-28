package com.jxdinfo.doc.manager.commonFolderManager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.commonFolderManager.dao.FsCommonFolderMapper;
import com.jxdinfo.doc.manager.commonFolderManager.model.FsCommonFolder;
import com.jxdinfo.doc.manager.commonFolderManager.service.IFsCommonFolderService;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 常用目录Service实现类
 */
@Service
public class FsCommonFolderServiceImpl extends ServiceImpl<FsCommonFolderMapper, FsCommonFolder> implements IFsCommonFolderService {

    /**
     * 常用目录dao层
     */
    @Resource
    private FsCommonFolderMapper fsCommonFolderMapper;

    @Resource
    private IFsFolderService fsFolderService;

    /**
     * 获取用户所有常用目录
     *
     * @param userId
     * @return
     */
    @Override
    public List<FsCommonFolder> selectAllCommonFold(String userId, int startIndex, int pageSize, String order ) {
        return fsCommonFolderMapper.selectAllCommonFold(userId, startIndex, pageSize, order);
    }

    @Override
    public Map<String,Object> addCommonFold(String ids) {
        //添加失败的文件名称列表
        List<String> failNameList = new ArrayList<>();
        List<FsCommonFolder> saveList = new ArrayList<>();
        if (ids != null && ids != ""){
            String[] idArray = ids.split(",");
            for (int i = 0;  i < idArray.length; i++){
                String folderId = idArray[i];
                //查询是否已添加到数据库
                FsCommonFolder isExitFsCommonFolder = this.getOne(new QueryWrapper<FsCommonFolder>()
                        .eq("create_user_id",ShiroKit.getUser().getId())
                        .eq("folder_id",folderId));
                if (isExitFsCommonFolder == null){
                    FsCommonFolder fsCommonFolder = new FsCommonFolder();
                    fsCommonFolder.setFolderId(folderId);
                    FsFolder fsFolder = fsFolderService.getById(folderId);
                    if (fsFolder != null){
                        String folderName = fsFolder.getFolderName();
                        fsCommonFolder.setFolderName(folderName);
                        fsCommonFolder.setCommonFolderName(folderName);
                    }

                    fsCommonFolder.setCreateUserId(ShiroKit.getUser().getId());
                    fsCommonFolder.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    int maxOrder = Integer.valueOf(this.getMaxOrder() + i + 1);
                    fsCommonFolder.setShowOrder(maxOrder);
                    fsCommonFolder.setFileType("folder");
                    saveList.add(fsCommonFolder);
                }else{
                    failNameList.add(isExitFsCommonFolder.getFolderName());
                }
            }
        }
        Map<String,Object> result = new HashMap<>();
        if (failNameList != null && failNameList.size() > 0){
            result.put("code",1);
            result.put("fail",failNameList.stream().collect(Collectors.joining(",")));
        }else{
            this.saveBatch(saveList);
            result.put("code",0);
        }
        return result;
    }

    @Override
    public void deleteCommonFold(String ids) {
        if (ids != null && ids != ""){
            List<String> idList = Arrays.asList(ids.split(","));
            this.removeByIds(idList);
        }
    }

    @Override
    public Integer getMaxOrder() {
        Integer maxOrder = fsCommonFolderMapper.getMaxOrder();
        return maxOrder == null ? 0 : maxOrder;
    }

    @Override
    public void updateCommonFold(String commonFolderId, String commonFolderName) {
        FsCommonFolder fsCommonFolder = this.getById(commonFolderId);
        fsCommonFolder.setCommonFolderName(commonFolderName);
        this.updateById(fsCommonFolder);
    }

    @Override
    public void moveFolder(String idOne, String idTwo) {
        fsCommonFolderMapper.moveFolder(idOne, idTwo);
    }
}
