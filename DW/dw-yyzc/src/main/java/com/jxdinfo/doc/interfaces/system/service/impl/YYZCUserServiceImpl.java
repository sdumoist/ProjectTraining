package com.jxdinfo.doc.interfaces.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.service.FastdfsService;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.ThumbnailsUtil;
import com.jxdinfo.doc.interfaces.system.dao.YYZCUserMapper;
import com.jxdinfo.doc.interfaces.system.model.HeadPhoto;
import com.jxdinfo.doc.interfaces.system.model.YYZCUser;
import com.jxdinfo.doc.interfaces.system.service.YYZCUserService;
import com.jxdinfo.doc.manager.docmanager.ex.ServiceException;
import com.jxdinfo.doc.timer.client.ApiClient;
import com.jxdinfo.doc.timer.constants.ApiURL;
import com.jxdinfo.hussar.bsp.organ.dao.SysOrganMapper;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import com.jxdinfo.hussar.bsp.organ.service.SysOrgManageService;
import com.jxdinfo.hussar.bsp.permit.dao.SysUserRoleMapper;
import com.jxdinfo.hussar.bsp.permit.model.SysUserRole;
import com.jxdinfo.hussar.bsp.permit.service.ISysUsersService;
import com.jxdinfo.hussar.config.properties.GlobalProperties;
import com.jxdinfo.hussar.core.sys.model.DicSingle;
import com.jxdinfo.hussar.core.sys.model.DicType;
import com.jxdinfo.hussar.core.sys.service.ISysDicSingleService;
import com.jxdinfo.hussar.core.sys.service.ISysDicTypeService;
import com.jxdinfo.hussar.core.util.ToolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 组织机构service实现类
 * @author XuXinYing
 * @Date 2018/3/20 0020
 */
@Service
public class YYZCUserServiceImpl extends ServiceImpl<YYZCUserMapper, YYZCUser> implements YYZCUserService {

    /**
     * FAST操作接口
     */
    @Autowired
    private FastdfsService fastdfsService;

    /**
     * Mapper
     */
    @Resource
    private YYZCUserMapper yyzcUserMapper;

    @Resource
    SysStruMapper sysStruMapper;

    @Resource
    SysOrganMapper sysOrganMapper;

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    /**
     * 组织机构
     */
    @Resource
    private SysOrgManageService orgMaintenanceService;

    @Value("${docbase.ThumbnailsDir}")
    private String ThumbnailsDir;

    @Value("${fastdfs.using}")
    private boolean fastdfsUsingFlag;

    /**
     * 用户管理服务 接口
     */
    @Resource
    private ISysUsersService iSysUsersService;

    /**
     * global 配置
     */
    @Resource
    private GlobalProperties globalProperties;

    /**
     * 字典接口
     */
    @Resource
    private ISysDicSingleService dicSingleService;

    @Value("${docbase.filedir}")
    private String tempdir;

    /**
     * 字典类型接口
     */
    @Autowired
    private ISysDicTypeService dicTypeService;

    /**
     * 更新运营支撑用户表信息 
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param userList
     * @return true or false
     */
    @Override
    @Transactional
    public boolean insertOrUpdateYyzcUser(List<YYZCUser> userList) {
        boolean flag = true;
        List<YYZCUser> userinsertList = new ArrayList<YYZCUser>();
        List<YYZCUser> userupdateList = new ArrayList<YYZCUser>();
        List<String> userDelList = new ArrayList<String>();
        List<YYZCUser> userupdateUserRoleList = new ArrayList<YYZCUser>();

        List<DicSingle> dicUpdateList = new ArrayList<DicSingle>();
        List<DicSingle> dicInsertList = new ArrayList<DicSingle>();

        // 查询现有数据
        List<YYZCUser> userOldList = yyzcUserMapper.getYyzcUserList();
        Map<String, String> userCompareMap = new HashMap<String, String>();
        Map<String, String> userCompareNameMap = new HashMap<String, String>();
        Map<String, String> userCompareDelMap = new HashMap<String, String>();

        // 查询职务字典表
        Map<String, String> postCompareMap = new HashMap<String, String>();
        Map<String, String> postIdCompareMap = new HashMap<String, String>();
        // 查询人员职务

        DicType dictype = dicTypeService.getOne(new QueryWrapper<DicType>().eq("type_name", "staff_position"));
        String dicTypeId = "";
        if (ToolUtil.isNotEmpty(dictype)) {
            dicTypeId = dictype.getId();
        } else {
            DicType dictypeModel = new DicType();
            dictypeModel.setId(StringUtil.getUUID());
            dictypeModel.setRangeType("biz");
            dictypeModel.setTypeName("staff_position");
            dictypeModel.setParentId("biz");
            dictypeModel.setTypeDescription("人员职务");
            dicTypeService.save(dictypeModel);
            dictypeModel.setSort(34);
            dicTypeId = dictype.getId();
        }
        List<DicSingle> dicList = dicSingleService.list(new QueryWrapper<DicSingle>().eq("type_id", dicTypeId));
        for (DicSingle dic : dicList) {
            postCompareMap.put(dic.getLabel(), dic.getValue());
            postIdCompareMap.put(dic.getLabel(), dic.getId());

        }
        // 遍历接过来的数据生成MD5码

        for (YYZCUser yyzcuser : userList) {
            DicSingle dicModel = new DicSingle();
            String md5 = md5Password(yyzcuser.toString());
            yyzcuser.setMd5(md5);
            userCompareDelMap.put(yyzcuser.getUserid(), "");
            String zwmc = yyzcuser.getZwmc();
            if (ToolUtil.isNotEmpty(zwmc)) {
                String zwname = zwmc.replace("（", "(").replace("）", ")");
                yyzcuser.setZwmc(zwname);
                // 如果存在字典项
                if (ToolUtil.isNotEmpty(postCompareMap)) {
                    // 字典项 有值
                    if (ToolUtil.isNotEmpty(postCompareMap.get(zwname))) {
                        // 如果 字典项 中 存在 名称 但是值不对 视为更新
                        if (!zwname.equals(postCompareMap.get(zwname))) {
                            dicModel.setLabel(zwname);
                            dicModel.setValue(zwname);
                            dicModel.setSort(Integer.valueOf(yyzcuser.getZworder()));
                            // 放入字典ID
                            dicModel.setId(postIdCompareMap.get(zwname));
                            dicModel.setTypeId(dicTypeId);
                            dicUpdateList.add(dicModel);
                            postCompareMap.put(zwname, zwname);
                        }
                    } else {
                        // 字典项没有值 证明不存在此职务新增
                        dicModel.setLabel(zwname);
                        dicModel.setValue(zwname);
                        dicModel.setSort(Integer.valueOf(yyzcuser.getZworder()));
                        dicModel.setTypeId(dicTypeId);
                        dicModel.setId(StringUtil.getUUID());
                        dicInsertList.add(dicModel);
                        postCompareMap.put(zwname, zwname);
                    }
                } else {
                    dicModel.setLabel(zwname);
                    dicModel.setValue(zwname);
                    dicModel.setSort(Integer.valueOf(yyzcuser.getZworder()));
                    dicModel.setId(StringUtil.getUUID());
                    dicModel.setTypeId(dicTypeId);
                    dicInsertList.add(dicModel);
                    postCompareMap.put(zwname, zwname);
                }
            }

        }

        for (YYZCUser compareuser : userOldList) {
            userCompareMap.put(compareuser.getUserid(), compareuser.getMd5());
            userCompareNameMap.put(compareuser.getUserid(), compareuser.getUsername());
            // 遍历取出离职人员
            if (userCompareDelMap.get(compareuser.getUserid()) == null) {
                userDelList.add(compareuser.getUserid());
            }
        }

        // 进行比对 形成更新列表和插入列表
        if (userCompareMap != null && userCompareMap.size() != 0) {
            for (YYZCUser user : userList) {
                String userid = user.getUserid();
                String md5 = user.getMd5();
                String username = user.getUsername();
                // 如果存在此用户Id
                if (userCompareMap.get(userid) != null) {
                    // 如果存在此用户 信息MD5码与现存用的的MD5码不一致
                    if (!userCompareMap.get(userid).equals(md5)) {
                        userupdateList.add(user);
                    }
                    if (!userCompareNameMap.get(userid).equals(username)) {
                        YYZCUser newRoleUser = new YYZCUser();
                        newRoleUser.setUsername(username);
                        newRoleUser.setUsercode(userCompareNameMap.get(userid));
                        userupdateUserRoleList.add(newRoleUser);
                    }
                } else {
                    userinsertList.add(user);
                }
            }
        } else {
            userinsertList = userList;
        }

        // 执行插入
        if (ToolUtil.isNotEmpty(userinsertList)) {
            try {
                for (int i = 0; i < userinsertList.size(); i++) {
                    YYZCUser user = userinsertList.get(i);
                    String userid = user.getUserid();
                    String username = user.getUsername();
                    SysStru sysStru = sysStruMapper.selectById(userid);
                    if (null != sysStru) {
                        sysStruMapper.deleteById(userid);
                        sysOrganMapper.deleteById(userid);
                        iSysUsersService.removeById(username);
                        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("user_id", username).eq("granted_role", "public_role"));
                    }
                }

                yyzcUserMapper.insertList(userinsertList);
                yyzcUserMapper.insertSysOrgan(userinsertList);
                yyzcUserMapper.insertSysStru(userinsertList);
                yyzcUserMapper.insertSysUsers(userinsertList);
                yyzcUserMapper.insertSysUserRole(userinsertList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("用户插入出现异常！",e);
            }
        }

        // 执行更新或者插入
        if (ToolUtil.isNotEmpty(userupdateList)) {
            try {
                yyzcUserMapper.updateList(userupdateList);
                yyzcUserMapper.updateSysOrgan(userupdateList);
                yyzcUserMapper.updateSysStru(userupdateList);
                yyzcUserMapper.updateSysUsers(userupdateList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("用户更新出现异常！",e);
            }
        }
        if (ToolUtil.isNotEmpty(userupdateUserRoleList)) {
            try {
                yyzcUserMapper.updateSysUserRole(userupdateUserRoleList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("权限更新出现异常！",e);
            }
        }
        // 删除离职人员
        if (ToolUtil.isNotEmpty(userDelList)) {
            try {
                yyzcUserMapper.delYYZCList(userDelList);
//                yyzcUserMapper.delSysOrgan(userDelList);
               yyzcUserMapper.delSysStru(userDelList);
//                yyzcUserMapper.delUserRole(userDelList);
//                yyzcUserMapper.delSysUsers(userDelList);
                yyzcUserMapper.delSysUsers(userDelList);
                //删除文件权限
                yyzcUserMapper.delDocFileAuthority(userDelList);
                //删除目录权限
                yyzcUserMapper.delDocFoldAuthority(userDelList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("用户删除出现异常！",e);
            }
        }
        // 职务字典值插入
        if (ToolUtil.isNotEmpty(dicInsertList)) {
            try {
                dicSingleService.saveBatch(dicInsertList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("字典插入异常",e);
            }
        }
        // 职务字典值更新
        if (ToolUtil.isNotEmpty(dicUpdateList)) {
            try {
                dicSingleService.updateBatchById(dicUpdateList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("字典更新异常",e);
            }
        }
        return flag;
    }

    /**
     * 生成32位md5码
     * @param strAll 字符串
     * @return md5
     */
    public static String md5Password(String strAll) {

        try {
            // 得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(strAll.getBytes());
            StringBuffer buffer = new StringBuffer();
            // 把每一个byte 做一个与运算 0xff;
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            // 标准的md5加密后的结果
            return buffer.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 更新运营支撑用户图片表信息 
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param userphotoList 用戶图片信息集合
     * @return true or false
    */
    @Override
    @Transactional
    public boolean insertOrUpdateYyzcUserPhoto(List<HeadPhoto> userphotoList) {
        boolean flag = true;
        List<HeadPhoto> userPhotoinsertList = new ArrayList<HeadPhoto>();
        List<HeadPhoto> userPhotoupdateList = new ArrayList<HeadPhoto>();
        // 查询现有数据
        List<HeadPhoto> userPhotoOldList = yyzcUserMapper.getYyzcUserHeadPhotos();
        Map<String, String> userPhotoCompareMap = new HashMap<String, String>();
        for (HeadPhoto compareheadPhoto : userPhotoOldList) {
            userPhotoCompareMap.put(compareheadPhoto.getUserid(), compareheadPhoto.getMd5());
        }
        // 遍历接过来的数据生成MD5码
        for (HeadPhoto headPhoto : userphotoList) {
            String md5 = md5Password(headPhoto.toString());
            headPhoto.setMd5(md5);
        }
        // 进行比对 形成更新列表和插入列表
        if (userPhotoCompareMap != null && userPhotoCompareMap.size() != 0) {
            for (HeadPhoto headPhoto : userphotoList) {
                String userid = headPhoto.getUserid();

                String md5 = headPhoto.getMd5();
                // 如果存在此用户Id
                if (userPhotoCompareMap.get(userid) != null) {
                    // 如果存在此用户 信息MD5码与现存用的的MD5码不一致
                    String newmd5 = headPhoto.getMd5();
                    if (!userPhotoCompareMap.get(userid).equals(newmd5)) {
                        getPath(headPhoto);
                        userPhotoupdateList.add(headPhoto);
                    }
                } else {
                    getPath(headPhoto);
                    userPhotoinsertList.add(headPhoto);
                }
            }
        } else {
            userPhotoinsertList = userphotoList;
        }

        // 执行更新或者插入
        if (userPhotoinsertList != null && userPhotoinsertList.size() > 0) {
            try {
                yyzcUserMapper.insertUserPhotoList(userPhotoinsertList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("用户照片插入出现异常！");
            }
        }

        // 执行更新或者插入
        if (userPhotoupdateList != null && userPhotoupdateList.size() > 0) {
            try {
                yyzcUserMapper.updateUserPhotoList(userPhotoupdateList);
            } catch (Exception e) {
                e.printStackTrace();
                flag = false;
                throw new RuntimeException("用户照片更新出现异常！");
            }
        }

        return flag;

    }

    public  HeadPhoto getPath(HeadPhoto headPhoto){
        String path = "";
        String pic = headPhoto.getPicture64();
        Base64.Decoder decoder = Base64.getDecoder();
        pic = pic.substring(pic.indexOf(",")+1);
        File file = new File(tempdir+ File.separator + (UUID.randomUUID().toString().replace("-", "")) + ".jpeg");
        byte[] buffer = new byte[0];
        try {
            buffer = new BASE64Decoder().decodeBuffer(pic);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
            try {
                out.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String thumbNewPath = null;
        thumbNewPath = ThumbnailsDir + File.separator
                + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_Thumbnails.jpg";
        File thumbNewFile = new File(thumbNewPath);
        try {
            boolean isCreateThumb = ThumbnailsUtil.createThumbnails(file.getPath(), thumbNewPath, 80, 80);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (fastdfsUsingFlag) {
                String thumbNewPathFast = fastdfsService.uploadFile(thumbNewFile);
                headPhoto.setPath(thumbNewPathFast);
            }else {
                headPhoto.setPath(thumbNewPath);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return  headPhoto;
    }
    /**
     * 更新员工照片信息
     * @Title: getUserPhotoInfo 
     * @author: XuXinYing
     */
    @Override
    public boolean getUserPhotoInfo() {
        Boolean flag = true;
        List<String> deptidList = yyzcUserMapper.getDeptidlist();
        if (deptidList != null && deptidList.size() > 0) {
            for (String deptid : deptidList) {
                ApiClient client = new ApiClient();
                String url = ApiURL.USERPHOTOLIST.getUrl() + "?deptid=" + deptid;
                String userphotoList = client.userphotoList(url);
                List<HeadPhoto> userphotoInfoList = new ArrayList<HeadPhoto>();
                userphotoInfoList = JSONObject.parseArray(userphotoList, HeadPhoto.class);
                try {
                    insertOrUpdateYyzcUserPhoto(userphotoInfoList);
                } catch (Exception e) {
                    flag = false;
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
    /**
     * 判断运营支撑传输的用户名密码是否正确
     * @Title: checkUser
     * @author: bjj
     * @return
     */
    public List<String> checkUser(String username,String password){
        return yyzcUserMapper.checkUser( username,password);
    }


    public static void main(String[] args) {
        File file = new File("C://knowledge/file/"+ File.separator + (UUID.randomUUID().toString().replace("-", "")) + ".jpeg");

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buffer = decoder.decode("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");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file.getPath());
            try {
                out.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String thumbNewPath = null;
        thumbNewPath = "C://knowledge/ThumbnailsDir/" + File.separator
                + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_Thumbnails.jpg";
        File thumbNewFile = new File(thumbNewPath);
        try {
            boolean isCreateThumb = ThumbnailsUtil.createThumbnails(file.getPath(), thumbNewPath, 80, 80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
