package com.jxdinfo.doc.manager.foldermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.manager.foldermanager.dao.DocFoldAuthorityMapper;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.DocFoldAuthority;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.permit.service.ISysUserRoleService;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
@Service
public class DocFoldAuthorityServiceImpl extends ServiceImpl<DocFoldAuthorityMapper, DocFoldAuthority> implements IDocFoldAuthorityService {

    /**
     * Mapper
     */




    @Resource
    private DocFoldAuthorityMapper docFoldAuthorityMapper;
    @Resource
    private FsFolderMapper fsFolderMapper;

    @Resource
    private SysStruMapper sysStruMapper;
    @Resource
    @Lazy
    private BusinessService businessService;
    @Resource
    @Lazy
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    @Lazy
    private CacheToolService cacheToolService;
    /**
     * 用户 服务类（app）
     */
    @Autowired
    @Lazy
    private ISysUsersService iSysUsersService;

    @Override
    public int findEditClient(String id, List groupList, String userId,String  orgId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            String levelCodes = businessService.getUpLevelCodeByUserClient(fsFolderParams,orgId);
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
            levelCodes = businessService.getLevelCodeByUserUploadClient(fsFolderParams,orgId);
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

    @Override
    public int findEdit(String id, List groupList, String userId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
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
    @Override
    public int findEditByUploadClient(String id, List groupList, String userId,String orgId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            String groupIds = String.join(",", fsFolderParams.getGroupList());
            String levelCodes = businessService.getUpLevelCodeByUserClient(fsFolderParams,orgId);
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
            String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
            levelCodes =  cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
            if(levelCodes == null){
                levelCodes = "";
            }
            if (levelCodes.indexOf(",") == 0) {
                levelCodes = levelCodes.substring(1, levelCodes.length());
                levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            }else{
                levelCodes = "('')";
            }

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
    @Override
    public int findEditByUpload(String id, List groupList, String userId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            String orgId = getDeptIds(ShiroKit.getUser().getDeptId());
            String groupIds = String.join(",", fsFolderParams.getGroupList());
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
            String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
            levelCodes =  cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
            if(levelCodes == null){
                levelCodes = "";
            }
            if (levelCodes.indexOf(",") == 0) {
                levelCodes = levelCodes.substring(1, levelCodes.length());
                levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            }else{
                levelCodes = "('')";
            }

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
    @Override
    public int findEditByUploadMobile(String id, List groupList, String userId) {
        List<String> levelCodeList = new ArrayList();
        int editValue = 0;
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            String code = fsFolderChild.getLevelCode();
            FsFolderParams fsFolderParams = new FsFolderParams();
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("1");
            String deptId = iSysUsersService.getById(userId).getDepartmentId();
            String orgId =sysStruMapper.selectById(deptId).getOrganAlias();
            String groupIds = String.join(",", fsFolderParams.getGroupList());
            String levelCodes = businessService.getUpLevelCodeByUserClient(fsFolderParams,orgId);
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
            String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
            levelCodes =  cacheToolService.getLevelCodeByUserByUploadCache(groupIds,userId,"2",orgId,roleIds);
            if(levelCodes == null){
                levelCodes = "";
            }
            if (levelCodes.indexOf(",") == 0) {
                levelCodes = levelCodes.substring(1, levelCodes.length());
                levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            }else{
                levelCodes = "('')";
            }

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



    /**
     * 保存目录权限
     *
     * @param fsFolderParams 保存目录信息对象
     * @return 是否保存成功
     */
    public boolean saveDocFoldAuthority(FsFolderParams fsFolderParams) {
        boolean flag = false;
        List<DocFoldAuthority> list = new ArrayList<>();
        //将新增的数据存入权限表
        if (ToolUtil.isNotEmpty(fsFolderParams.getRole())) {
            String[] roleArr = fsFolderParams.getRole().split(",");
            String[] authorTypeStrRole = fsFolderParams.getAuthorTypeStrRole().split(",");
            String[] operateTypeStrRole = fsFolderParams.getOperateTypeStrRole().split(",");
            for (int i = 0; i < roleArr.length; i++) {
                DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                docFoldAuthority.setAuthorId(roleArr[i]);
                docFoldAuthority.setAuthorType(authorTypeStrRole[i]);
                docFoldAuthority.setFoldId(fsFolderParams.getFolderId());
                docFoldAuthority.setIsEdit(fsFolderParams.getIsEdit());
                docFoldAuthority.setOperateType(operateTypeStrRole[i]);
                list.add(docFoldAuthority);
            }
        }
        if (ToolUtil.isNotEmpty(fsFolderParams.getGroup())) {
            String[] groupArr = fsFolderParams.getGroup().split(",");
            String[] authorTypeStrGroup = fsFolderParams.getAuthorTypeStrGroup().split(",");
            String[] operateTypeStrGroup = fsFolderParams.getOperateTypeStrGroup().split(",");
            for (int i = 0; i < groupArr.length; i++) {
                DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                docFoldAuthority.setAuthorId(groupArr[i]);
                docFoldAuthority.setAuthorType(authorTypeStrGroup[i]);
                docFoldAuthority.setFoldId(fsFolderParams.getFolderId());
                docFoldAuthority.setIsEdit(fsFolderParams.getIsEdit());
                docFoldAuthority.setOperateType(operateTypeStrGroup[i]);
                list.add(docFoldAuthority);
            }
        }
        if (ToolUtil.isNotEmpty(fsFolderParams.getPerson())) {
            String[] personArr = fsFolderParams.getPerson().split(",");
            String[] personOrganArr = fsFolderParams.getPersonOrgan().split(",");
            String[] authorTypeStrPerson = fsFolderParams.getAuthorTypeStrPerson().split(",");
            String[] operateTypeStrPerson = fsFolderParams.getOperateTypeStrPerson().split(",");
            for (int i = 0; i < personArr.length; i++) {
                DocFoldAuthority docFoldAuthority = new DocFoldAuthority();
                docFoldAuthority.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                if(authorTypeStrPerson[i].equals("2")){
                   SysStru sysStru=sysStruMapper.selectById(personArr[i]);
                   if(sysStru==null){
                       docFoldAuthority.setOrganId(personOrganArr[i]);
                       docFoldAuthority.setAuthorId(personArr[i]);
                   }else{
                       docFoldAuthority.setOrganId(sysStru.getStruId());
                       docFoldAuthority.setAuthorId(sysStru.getOrganAlias());
                   }
                }else {
                    docFoldAuthority.setAuthorId(personArr[i]);
                }
                docFoldAuthority.setAuthorType(authorTypeStrPerson[i]);
                docFoldAuthority.setFoldId(fsFolderParams.getFolderId());
                docFoldAuthority.setIsEdit(fsFolderParams.getIsEdit());
                docFoldAuthority.setOperateType(operateTypeStrPerson[i]);
                list.add(docFoldAuthority);
            }
        }
        if (list.size() > 0) {
            flag = saveBatch(list);
        }else{
            flag =true;
        }
        return flag;
    }

    /**
     * @return 是否可编辑
     * @Author zoufeng
     * @Description 查询文件是否可编辑 20181009重写
     * @Date 10:39 2018/10/9
     * @Param id 目录id groupList 群组id userid 用户id
     **/
    public int findEditNew(String id, List groupList, String userId) {
        List<String> levelCodeList = new ArrayList();
        String orgName = getDeptIds(ShiroKit.getUser().getDeptId());
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            if (fsFolderChild != null) {
                for (int m = 0; m < fsFolderChild.getLevelCode().length() / 4; m++) {
                    String code = fsFolderChild.getLevelCode().substring(0, (m + 1) * 4);
                    levelCodeList.add(code);
                }
            }
        }
        return docFoldAuthorityMapper.findEditNew(levelCodeList, groupList, userId, orgName,ShiroKit.getUser().getRolesList());
    }
    /**
     * @return 是否可编辑
     * @Author zoufeng
     * @Description 查询文件是否可编辑 20181009重写
     * @Date 10:39 2018/10/9
     * @Param id 目录id groupList 群组id userid 用户id
     **/
    public int findEditNewClient(String id, List groupList, String userId,String orgName) {
        List<String> levelCodeList = new ArrayList();
        if (id != null) {
            FsFolder fsFolderChild = fsFolderMapper.selectById(id);
            if (fsFolderChild != null) {
                for (int m = 0; m < fsFolderChild.getLevelCode().length() / 4; m++) {
                    String code = fsFolderChild.getLevelCode().substring(0, (m + 1) * 4);
                    levelCodeList.add(code);
                }
            }
        }
        return docFoldAuthorityMapper.findEditNew(levelCodeList, groupList, userId, orgName,sysUserRoleService.getRolesByUserId(userId));
    }

    public String getDeptIds(String orgId){
        List<String> orgList = new ArrayList<>();
        String deptId =orgId;
        SysStru sysStru = sysStruMapper.selectById(deptId);
        if(sysStru != null){
            while (!"11".equals(sysStru.getParentId())) {
                orgList.add(sysStru.getStruId());
                sysStru = sysStruMapper.selectById(sysStru.getParentId());
                if(sysStru == null){
                    break;
                }
            }
        }
        String resultStr = "";
        for(int i=0;i<orgList.size();i++){
            if(i == orgList.size() -1 ){
                resultStr = resultStr + orgList.get(i);
            } else {
                resultStr = resultStr+ orgList.get(i) + ",";
            }

        }
		/*if(resultStr != null){
			resultStr = "( " + resultStr + " )";
		}*/
        return resultStr;
    }
}
