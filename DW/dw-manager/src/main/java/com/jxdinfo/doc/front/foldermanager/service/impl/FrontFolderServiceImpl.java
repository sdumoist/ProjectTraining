package com.jxdinfo.doc.front.foldermanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.BusinessService;
import com.jxdinfo.doc.common.util.CommonUtil;
import com.jxdinfo.doc.front.foldermanager.dao.FrontFolderMapper;
import com.jxdinfo.doc.front.foldermanager.service.FrontFolderService;
import com.jxdinfo.doc.front.groupmanager.service.FrontDocGroupService;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;
import com.jxdinfo.doc.manager.foldermanager.dao.FsFolderMapper;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolder;
import com.jxdinfo.doc.manager.foldermanager.model.FsFolderParams;
import com.jxdinfo.doc.manager.foldermanager.service.IFsFolderService;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 前台目录表 服务实现类
 * </p>
 *
 * @author zf
 * @since 2018-09-06
 */
@Service
public class FrontFolderServiceImpl extends ServiceImpl<FsFolderMapper, FsFolder> implements FrontFolderService {

    /**
     * 目录dao层
     */
    @Resource
    private FrontFolderMapper frontFolderMapper;

    /** 文档群组服务类 */
    @Resource
    private FrontDocGroupService frontDocGroupService;


    /** 目录管理工具类 */
    @Resource
    private BusinessService businessService;

    @Resource
    private IFsFolderService fsFolderService;
    /**
     * 动态加载文件树
     *
     * @return 目录信息
     */
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

    /**
     * 动态加载目录树
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    public List getTreeDataLazy(String id, String type) {
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        FsFolder folder;
        if ("#".equals(id)) {
            String fid="2bb61cdb2b3c11e8aacf429ff4208431";
            folder=fsFolderService.getById(fid);
        }else{
            folder=fsFolderService.getById(id);
        }
        List<String> roleList = ShiroKit.getUser().getRolesList();
        //人员Id
        String userId = ShiroKit.getUser().getId();
        //所属群组id
        List<String> listGroup = frontDocGroupService.getPremission(userId);

        //超级管理员：1 文库管理员：2
        Integer adminFlag = CommonUtil.getAdminFlag(roleList);
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType("front");
        fsFolderParams.setRoleList(roleList);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
        fsFolderParams.setType("2");
        fsFolderParams.setLevelCodeString(levelCodeString);
        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList = getChildList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountList(secondList, listGroup, userId, adminFlag, type,levelCodeString);
            List<Map> childResultList = checkChildCount(childList,childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("opened", true);
                resultList.add(parentMap);
            }
        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            //非首次访问， 获取点击节点的下级
            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            resultList = checkChildCount(list,childCountList);
        }
        return resultList;
    }

    /**
     * @Author zoufeng
     * @Description 判断目录是否有下级
     * @Date 11:58 2018/9/18
     * @Param [list, childCountList]子节点目录信息，子节点包含下级的数量集合
     * @return java.util.List<java.util.Map>
     **/
    public List<Map> checkChildCountJqx(List<FsFolder> list,List<Map> childCountList){

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
                        childMap.put("isLeaf", false);
                        childMap.put("expanded", false);
                    } else {
                        childMap.put("isLeaf", true);
                        childMap.put("expanded", true);

                    }

                }

            }
            resultList.add(childMap);
        }
        return resultList;
    }
    public List<Map> checkChildCount(List<FsFolder> list,List<Map> childCountList){

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

    public List<Map> getChildCountList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type,String levelCodeString) {

        List<Map> listMap = null;
        if (adminFlag == 1) {
            listMap = frontFolderMapper.getChildCountListForSuperAdmin(list);
        } else {
            listMap = frontFolderMapper.getChildCount(list, userId, levelCodeString);
        }

        return listMap;
    }

    /**
     * 获取第一级目录
     *
     * @param list      第一级目录id
     * @param groupList 群组id
     * @param userId    用户id
     * @param adminFlag 管理员级别
     * @param type      前后台标识
     * @return 第一级节点相关信息
     */
    public List<FsFolder> getChildList(@Param("list") List list, @Param("groupList") List groupList,
                                       @Param("UserId") String userId, Integer adminFlag, String type,String levelCodeString) {
        List<FsFolder> listFsFolder = new ArrayList<FsFolder>();
        if (adminFlag == 1) {
            listFsFolder = frontFolderMapper.getChildListForSuperAdmin(list);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
            listFsFolder = frontFolderMapper.selectByLevelCodeList(list, userId, levelCodeString);
        }
        return listFsFolder;
    }

    @Override
    public List<FsFolderView> getFilesAndFloder(int pageNumber, int pageSize, String id, String[] typeArr, String name,
                                                String orderResult, List groupList, String userId, Integer adminFlag,
                                                String operateType,String levelCodeString,String  levelCode,String orgId,List roleList) {
        List<FsFolderView> list = null;
        if (adminFlag == 1) {
            list = frontFolderMapper.getFilesAndFloderBySuperAdmin(pageNumber, pageSize, id, name, orderResult, typeArr,userId);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            list = frontFolderMapper.getFilesAndFloderByAdmin(levelCodeString, pageNumber, pageSize, id, name, orderResult, groupList, userId,
                    typeArr, operateType,levelCode,orgId,roleList);
        }
        return list;
    }


    /**
     * 获取下级文件及目录的数量
     *
     * @param id          节点id
     * @param typeArr     文件类型
     * @param name        文件名称
     * @param orderResult
     * @param groupList   群组id
     * @param userId      用户id
     * @param adminFlag   管理员级别
     * @param operateType 前后台
     * @return 下级文件及目录的数量
     */
    @Override
    public int getFilesAndFloderNum(String id, String[] typeArr, String name, String orderResult, List groupList,
                                    String userId, Integer adminFlag, String operateType,String levelCodeString,
                                    String  levelCode,String orgId,List roleList) {
        int num = 0;
        if (adminFlag == 1) {
            num = frontFolderMapper.getFilesAndFloderNumBySuperAdmin(id, name, orderResult, typeArr);

        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            num = frontFolderMapper.getFilesAndFloderByAdminNum(levelCodeString, id, name, orderResult, groupList, userId,
                    typeArr, operateType,levelCode,orgId,roleList);
        }
        return num;
    }

    /**
     * 获取子文件的数量
     *
     * @param id        节点id
     * @param typeArr   文件类型
     * @param name      文件名称
     * @param groupList 群组id
     * @param userId    用户id
     * @param adminFlag 管理员标识
     * @param type      前后台标识
     * @return 文件的数量
     */
    public int getFileNum(String id, String[] typeArr, String name, List groupList,
                          String userId, Integer adminFlag, String type,String levelCodeString,String orgId,List roleList) {
        int num = 0;
        if (adminFlag == 1) {
            num = frontFolderMapper.getFileNumBySuperAdmin(id, name, typeArr);

        } else {
            if ("0".equals(type)) {
                num = frontFolderMapper.getFileNum(id, name, groupList, userId, type, typeArr,levelCodeString,orgId,roleList);
            } else {
//                List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, type);
                num = frontFolderMapper.selectByLevelCodeNum(id, userId, levelCodeString, name);
            }

        }
        return num;
    }

    @Override
    public List<FsFolder> getFsFolderByName(String name) {
        return frontFolderMapper.getFsFolderByName(name);
    }

    @Override
    public List<Map> getFolderList(String pFolderId, int startNum, int size,String userId, List groupList, Integer adminFlag) {
        FsFolderParams fsFolderParams = new FsFolderParams();

        if (adminFlag == 1) {
            //查询根节点ID
            //超级管理员获取目录文件
            return  frontFolderMapper.getFolderList(pFolderId,startNum,size);
        } else {
            //其他管理员获取目录权限
//            List<String> levelCodeList = businessService.getlevelCodeList(groupList, userId, "0");
            fsFolderParams.setGroupList(groupList);
            fsFolderParams.setUserId(userId);
            fsFolderParams.setType("front");
            //加载首页金现代层级码0001
            fsFolderParams.setRoleList(ShiroKit.getUser().getRolesList());
            fsFolderParams.setLevelCodeString("0001");
            //long time1 = System.currentTimeMillis();
            System.out.println("===============查询levelCodeString开始");
            String levelCodeString = businessService.getFileLevelCodeFront(fsFolderParams);
            //long time2 = System.currentTimeMillis();
           // System.out.println("===============查询levelCodeString结束"+(time2-time1));
            fsFolderParams.setType("2");
            List list = new ArrayList<>();
            //查询根节点ID
            FsFolder rootFloder = frontFolderMapper.getRoot().get(0);
            list.add(rootFloder.getFolderId());
            //通过层级码和用户进行查询目录
            return frontFolderMapper.getFolderListPerson(pFolderId,startNum,size,userId,levelCodeString);
        }
    }

    @Override
    public List<DocInfo> getFileByAuthor(Integer pageNumber, Integer pageSize, String userName, List<String> groupList,
                                         String userId, Boolean adminFlag, String levelCode, String orgId, String[] typeArr,List roleList, Integer order) {
        List<DocInfo> list = null;
        if (adminFlag) {
            list = frontFolderMapper.getFileByAuthorBySuper(pageNumber, pageSize,  userName,typeArr,order);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            list = frontFolderMapper.getFileByAuthorByAdmin(pageNumber, pageSize, userName, groupList, userId, levelCode, orgId,
                    typeArr,roleList,order);
        }
        return list;
    }

    @Override
    public int getFileByAuthorCount(String userName, List<String> groupList, String userId, Boolean adminFlag,
                                    String levelCode, String orgId, String[] typeArr,List roleList) {
     Integer num =0;
        if (adminFlag) {
            num = frontFolderMapper.getFileByAuthorBySuperNum(userName,typeArr);
        } else {
//            List<String> levelCodeList = folderService.getlevelCodeList(groupList, userId, operateType);
            num = frontFolderMapper.getFileByAuthorByAdminNum(userName, groupList, userId, levelCode, orgId,
                    typeArr,roleList);
        }
        return num;
    }

    /**
     * 动态加载目录树
     * @param id   节点id
     * @param type 前台：0 后台：1
     * @return 返回目录信息
     */
    public List getTreeDataLazyJqx(String id, String type) {
        if(id==null||"".equals(id)){
            id="#";
        }
        List resultList = new ArrayList();
        FsFolderParams fsFolderParams = new FsFolderParams();
        FsFolder folder;
        if ("#".equals(id)) {
            String fid="2bb61cdb2b3c11e8aacf429ff4208431";
            folder=fsFolderService.getById(fid);
        }else{
            folder=fsFolderService.getById(id);
        }
        //人员Id
        String userId = "superadmin";
        //所属群组id
        List<String> listGroup = frontDocGroupService.getPremission(userId);

        //超级管理员：1 文库管理员：2
        Integer adminFlag =1;
        fsFolderParams.setAdminFlag(adminFlag);
        fsFolderParams.setGroupList(listGroup);
        fsFolderParams.setUserId(userId);
        fsFolderParams.setType(type);
        fsFolderParams.setLevelCodeString(folder.getLevelCode());
        String levelCodeString = businessService.getFileLevelCodeFrontMobile(fsFolderParams);
        fsFolderParams.setLevelCodeString(levelCodeString);
        //判断是不是首次访问（id是否是根节点）
        if ("#".equals(id)) {
            //首次访问
            String idParam = "root";
            //获取根节点
            fsFolderParams.setId(idParam);
            fsFolderParams.setType("0");

            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取第一级
            List<FsFolder> childList = getChildList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            List<String> secondList = new ArrayList<>();
            //将文件id拼接
            for (int i = 0; i < childList.size(); i++) {
                FsFolder fsFolder = childList.get(i);
                secondList.add(fsFolder.getFolderId());
            }
            //获取第一级是否有下级
            List<Map> childCountList = getChildCountList(secondList, listGroup, userId, adminFlag, type,levelCodeString);
            List<Map> childResultList = checkChildCountJqx(childList,childCountList);

            for (int i = 0; i < list.size(); i++) {
                Map parentMap = new HashMap();
                FsFolder fsFolder = list.get(i);
                parentMap.put("id", fsFolder.getFolderId());
                parentMap.put("text", fsFolder.getFolderName());
                List childMapList = new ArrayList();
                for (int j = 0; j < childResultList.size(); j++) {
                    Map map = childResultList.get(j);
                    if (fsFolder.getFolderId().equals(map.get("pid"))) {
                        childMapList.add(map);
                    }
                }
                parentMap.put("children", childMapList);
                parentMap.put("expanded", true);
                resultList.add(parentMap);
            }
        } else {
            fsFolderParams.setId(id);
            fsFolderParams.setType(type);
            //非首次访问， 获取点击节点的下级
            List<FsFolder> list = getTreeDataLazy(fsFolderParams);
            List<String> firstList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                FsFolder fsFolder = list.get(i);
                firstList.add(fsFolder.getFolderId());
            }
            //获取是否有下级
            List<Map> childCountList = getChildCountList(firstList, listGroup, userId, adminFlag, type,levelCodeString);
            resultList = checkChildCountJqx(list,childCountList);
        }
        return resultList;
    }
}