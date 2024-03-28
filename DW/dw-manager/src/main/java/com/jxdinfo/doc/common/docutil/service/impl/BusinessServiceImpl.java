package com.jxdinfo.doc.common.docutil.service.impl;/**
 * Created by zoufeng on 2018/9/10.
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.common.docutil.dao.BusinessMapper;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.docutil.service.CacheToolService;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IDocFoldAuthorityService;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.sys.model.DicType;
import com.jxdinfo.hussar.core.sys.service.ISysDicTypeService;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FolderServiceImpl
 * @Description TODO
 * @Author zoufeng
 * @Date 2018/9/10 9:07
 * @Version 1.0
 **/
@Service
public class BusinessServiceImpl implements BusinessService {
    @Resource
    private SysStruMapper sysStruMapper;
    /**
     * 目录管理dao层
     */
    @Resource
    private FsFolderMapper fsFolderMapper;


    @Autowired
    private IDocFoldAuthorityService docFoldAuthorityService;

    /**
     * 目录dao层
     */
    @Resource
    private BusinessMapper businessMapper;

    /**
     * 缓存工具服务类
     */
    @Autowired

    private CacheToolService cacheToolService;

    @Resource
    private ISysUsersService iSysUsersService;

    @Autowired
    private ISysDicTypeService dicTypeService;

    @Override
    public List<Map<String, Object>> getDictListByType(String typeName) {
        List<Map<String, Object>> result = new ArrayList();
        DicType type = this.getTypeInfo(typeName);
        if (ToolUtil.isNotEmpty(type)) {
            result = this.businessMapper.getDicListByType(type.getId());
        }

        return (List)result;
    }

    private DicType getTypeInfo(String typeName) {
        return  (DicType)this.dicTypeService.getOne(new QueryWrapper<DicType>().eq("type_name", typeName));
    }


    /**
     * 获取当前用户及用户组的目录层级码
     *
     * @param groupList 群组id集合
     * @param userId    当前用户id
     * @param type      前后台
     * @return 目录层级码
     */
    public List<String> getlevelCodeList(List groupList, String userId, String type) {
        String groupIds = String.join(",", groupList);
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String roleIds = String.join(",",ShiroKit.getUser().getRolesList());
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, userId, type, orgId,roleIds);

        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
        }

        String str[] = levelCodes.split(",");

        List<String> levelCodeList = new ArrayList<String>();

        HashSet<String> hs = new HashSet<String>(Arrays.asList(str));
        levelCodeList.addAll(hs);

        levelCodeList.add("");

        return levelCodeList;
       
/*    	int num = 0;
        List<String> levelCodeList = new ArrayList();
        levelCodeList.add("001");
        List<DocFoldAuthority> docFoldAuthorities = docFoldAuthorityMapper.selectUserRole(groupList, userId, type);
        if (docFoldAuthorities != null && docFoldAuthorities.size() > 0) {
            for (int i = 0; i < docFoldAuthorities.size(); i++) {
            	DocFoldAuthority iDocFoldAuthority = docFoldAuthorities.get(i);
            	
            	if (iDocFoldAuthority == null){
            		continue;
            	}
            	
                String foldId = iDocFoldAuthority.getFoldId();
                if (foldId != null) {
                    FsFolder fsFolderChild = fsFolderMapper.selectById(foldId);
                    if (fsFolderChild == null) {
                        continue;
                    }
                    if (fsFolderChild.getLevelCode() == null || "".equals(fsFolderChild.getLevelCode())) {
                        continue;
                    } else {
                        List<String> childLevelCodeList = fsFolderMapper.getChildLevelCode(fsFolderChild.getLevelCode());
                        levelCodeList.addAll(childLevelCodeList);
                        for (int m = 0; m < fsFolderChild.getLevelCode().length() / 3; m++) {
                            String code = fsFolderChild.getLevelCode().substring(0, (m + 1) * 3);
                            levelCodeList.add(code);
                        }
                    }
                }
            }
        }
        return levelCodeList;*/
    }

    /**
     * 上移下移交换showorder
     *
     * @param table  表名
     * @param cloum  排序字段名
     * @param id     需要交换的id
     * @param nextid 被交换的id
     * @return
     */
    public int changeShowOrder(String table, String cloum, String id, String nextid) {
        return businessMapper.changeShowOrder(table, cloum, id, nextid);
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserClient(FsFolderParams fsFolderParams, String orgId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUser(FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    public String getLevelCodeByUserRecycle(FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        String levelCodesUp = cacheToolService.getUpLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), "1", orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        levelCodes = levelCodes + levelCodesUp;
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserUploadClient(FsFolderParams fsFolderParams, String orgId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserUpload(FsFolderParams fsFolderParams) {
        String roleIds = "";
        if(fsFolderParams.getRoleList()!=null) {
             roleIds = String.join(",", fsFolderParams.getRoleList());
        }
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    // 查询
    public String getFolderIdByUserUpload(FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        //long time1 = System.currentTimeMillis();
        //System.out.println("查询目录管理权限开始");
        String levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
       // long time2 = System.currentTimeMillis();
        //System.out.println("查询目录管理权限结束"+(time2-time1));

       // System.out.println("查询目录管理权限id的开始");
        //long time3 = System.currentTimeMillis();
        if (levelCodes == null) {
            levelCodes = "";
        }
        String ids = "";
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            //String levelCodeStr[] = levelCodes.split(",");
            ids = fsFolderMapper.selectIdsByLevelCode(Arrays.asList(levelCodes.split(",")));
           // long time4 = System.currentTimeMillis();
           // System.out.println("==============1=======查询目录管理权限id的结束"+(time4-time3));
            //List<FsFolder> fsList = fsFolderService.selectList(new EntityWrapper<FsFolder>().in("level_code",Arrays.asList(levelCodes.split(","))));


           /* long time5 = System.currentTimeMillis();
            for (int i = 0; i < levelCodeStr.length; i++) {
                String code = levelCodeStr[i];

                List<FsFolder> fsfolder = fsFolderMapper.getFolderByLevelCode(code);
                if (fsfolder != null && fsfolder.size() > 0) {
                    ids = ids + "," + fsfolder.get(0).getFolderId();
                } else {
                    continue;
                }
            }
            long time6 = System.currentTimeMillis();*/
        }


        return ids;
    }

    public String getFolderIdByUserUploadClient(FsFolderParams fsFolderParams, String userId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String orgId =docFoldAuthorityService.getDeptIds( iSysUsersService.getById(userId).getDepartmentId());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds, userId, fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        String ids = "";
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            ids = fsFolderMapper.selectIdsByLevelCode(Arrays.asList(levelCodes.split(",")));
        }
        return ids;
    }

    /**
     * 查询拥有权限的目录层级码
     * 手机端方法
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserUploadMobile(String userId, FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String orgId = docFoldAuthorityService.getDeptIds( iSysUsersService.getById(userId).getDepartmentId());
        String levelCodes = cacheToolService.getLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        if (levelCodes == null) {
            levelCodes = "";
        }
        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     * 手机端方法
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getLevelCodeByUserMobile(FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String orgId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(fsFolderParams.getUserId()).getDepartmentId());
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);
        String levelcodeStr = cacheToolService.getFileUpLevelCodeCache(groupIds, fsFolderParams.getUserId(), "0001", orgId, "front",roleIds);
        if (levelcodeStr != null) {
            levelCodes = levelCodes + levelcodeStr;
        }

        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getUpLevelCodeByUser(FsFolderParams fsFolderParams) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getUpLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);

        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    /**
     * 查询拥有权限的目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getUpLevelCodeByUserClient(FsFolderParams fsFolderParams, String orgId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getUpLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getType(), orgId,roleIds);

        if (levelCodes.indexOf(",") == 0) {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
        } else {
            levelCodes = "('')";
        }

        return levelCodes;
    }

    @Override
    public String getUploadLevelCodeFront(FsFolderParams fsFolderParams) {
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
        String upLevelCodes = cacheToolService.getUpLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), "1", orgId,roleIds);
        levelCodes = levelCodes + upLevelCodes;
//        String fileLevelCodes = businessMapper.getFileLevelCodeByUser(groupIds, fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId);
//        levelCodes = levelCodes + cacheToolService.getFileUpLevelCodeCache(groupIds,fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId,"");
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }


    }

    @Override
    public String getUploadLevelCodeFrontMobile(FsFolderParams fsFolderParams, String orgId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
        String upLevelCodes = cacheToolService.getUpLevelCodeByUserByUploadCache(groupIds, fsFolderParams.getUserId(), "1", orgId,roleIds);
        levelCodes = levelCodes + upLevelCodes;
//        String fileLevelCodes = businessMapper.getFileLevelCodeByUser(groupIds, fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId);
//        levelCodes = levelCodes + cacheToolService.getFileUpLevelCodeCache(groupIds,fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId,"");
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }


    }

    /**
     * 查询拥有权限的文件所在目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
//    public String getFileLevelCodeByUser(FsFolderParams fsFolderParams) {
//        String groupIds = String.join(",", fsFolderParams.getGroupList());
//        String orgId = ShiroKit.getUser().getDeptName();
//        String levelCodes = businessMapper.getFileLevelCodeByUser(groupIds, fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId);
//
//        if (levelCodes.indexOf(",") == 0) {
//            levelCodes = levelCodes.substring(1, levelCodes.length());
//            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
//        }else{
//            levelCodes = "('')";
//        }
//
//        return levelCodes;
//    }

    /**
     * 查询拥有权限的文件后太目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCode(FsFolderParams fsFolderParams) {
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
//        String upLevelCodes = businessMapper.getUpLevelCodeByUser(groupIds, fsFolderParams.getUserId(), "1",orgId);
//        levelCodes = levelCodes + upLevelCodes;
//        String fileLevelCodes = businessMapper.getFileLevelCodeByUser(groupIds, fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId);
        String levelCodesFile = cacheToolService.getFileUpLevelCodeCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString(), orgId, "",roleIds);
        if (levelCodesFile == null) {
            levelCodesFile = "";
        }
        levelCodes = levelCodes + levelCodesFile;
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }

    }

    /**
     * 查询拥有权限的文件后太目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeClient(FsFolderParams fsFolderParams, String orgId) {
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
//        String upLevelCodes = businessMapper.getUpLevelCodeByUser(groupIds, fsFolderParams.getUserId(), "1",orgId);
//        levelCodes = levelCodes + upLevelCodes;
//        String fileLevelCodes = businessMapper.getFileLevelCodeByUser(groupIds, fsFolderParams.getUserId(),fsFolderParams.getLevelCodeString(),orgId);
        String levelCodesFile = cacheToolService.getFileUpLevelCodeCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString(), orgId, "",roleIds);
        if (levelCodesFile == null) {
            levelCodesFile = "";
        }
        levelCodes = levelCodes + levelCodesFile;
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }

    }

    /**
     * 查询拥有权限的文件后太目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getFileLevelCodeFront(FsFolderParams fsFolderParams) {
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        //long time1 = System.currentTimeMillis();
        //System.out.println("===============getFileLevelCodeFront查询levelCodes结束");
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
        //long time2 = System.currentTimeMillis();
        //System.out.println("===============getFileLevelCodeFront查询levelCodes结束"+(time2-time1)); //1290
        //将文件层级码函数合并
        //long time3 = System.currentTimeMillis();
        //System.out.println("===============getFileLevelCodeFront查询levelcodeStr开始");
        String levelcodeStr = cacheToolService.getFileUpLevelCodeCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString(), orgId, fsFolderParams.getType(),roleIds);
       // long time4 = System.currentTimeMillis();
        //System.out.println("===============getFileLevelCodeFront查询levelCodes的长度"+levelcodeStr.length()); //3473
        //System.out.println("===============getFileLevelCodeFront查询levelCodes结束"+(time4-time3)); //3473
        if (levelcodeStr != null) {
            levelCodes = levelCodes + levelcodeStr;
        }
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }

    }

    /**
     * 查询所有拥有权限的目录层级码
     * 手机端方法
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    @Override
    public String getFileLevelCodeFrontMobile(FsFolderParams fsFolderParams) {
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String orgId = docFoldAuthorityService.getDeptIds(iSysUsersService.getById(fsFolderParams.getUserId()).getDepartmentId());
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        // 获取有管理权限的目录 并向上、向下发散
        //System.out.println("============查询有管理权限的层级码开始");
        //long time1 = System.currentTimeMillis();
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
        //long time2 = System.currentTimeMillis();
       // System.out.println("============查询有管理权限的层级码结束"+(time2-time1));

        // 获取有文件所有权限、目录查看、上传权限的目录levelCode 并向上发散
        //long time3 = System.currentTimeMillis();
        //System.out.println("============查询有文件所有权限、目录查看上传权限开始");
        String levelcodeStr = cacheToolService.getFileUpLevelCodeCache(groupIds, fsFolderParams.getUserId(), fsFolderParams.getLevelCodeString(), orgId, "front",roleIds);
        //long time4 = System.currentTimeMillis();
        //System.out.println("============查询有文件所有权限、目录查看上传权限结束"+(time4-time3));
        if (levelcodeStr != null) {
            levelCodes = levelCodes + levelcodeStr;
        }
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }

    }

    /**
     * 查询拥有权限的文件后太目录层级码
     *
     * @param fsFolderParams 群组id，用户id，前后台标识
     * @return string 逗号隔开形式（'2','3'）
     */
    public String getMoveFileLevelCode(FsFolderParams fsFolderParams) {
        String orgId = docFoldAuthorityService.getDeptIds(ShiroKit.getUser().getDeptId());
        String groupIds = String.join(",", fsFolderParams.getGroupList());
        String roleIds = "";                     if(fsFolderParams.getRoleList()!=null) {              roleIds = String.join(",", fsFolderParams.getRoleList());         }
        String levelCodes = cacheToolService.getLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "2", orgId,roleIds);
        String upLevelCodes = cacheToolService.getUpLevelCodeByUserCache(groupIds, fsFolderParams.getUserId(), "1", orgId,roleIds);
        levelCodes = levelCodes + upLevelCodes;
        if (StringUtil.checkIsEmpty(levelCodes)) {
            return "('')";
        } else {
            levelCodes = levelCodes.substring(1, levelCodes.length());
            levelCodes = "('" + levelCodes.replace(",", "','") + "')";
            return levelCodes;
        }
    }
}
