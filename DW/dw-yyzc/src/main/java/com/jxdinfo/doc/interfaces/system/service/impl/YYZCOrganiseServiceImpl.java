package com.jxdinfo.doc.interfaces.system.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.interfaces.system.dao.YYZCOrganiseMapper;
import com.jxdinfo.doc.interfaces.system.model.YYZCOrganise;
import com.jxdinfo.doc.interfaces.system.service.YYZCOrganiseService;

import com.jxdinfo.hussar.bsp.organ.dao.SysOrganMapper;
import com.jxdinfo.hussar.bsp.organ.dao.SysStruMapper;
import com.jxdinfo.hussar.bsp.organ.model.SysStru;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class YYZCOrganiseServiceImpl  extends ServiceImpl<YYZCOrganiseMapper, YYZCOrganise>
        implements YYZCOrganiseService {

    @Resource
    private YYZCOrganiseMapper yyzcOrganiseMapper;

    @Resource
    SysStruMapper sysStruMapper;

    @Resource
    SysOrganMapper sysOrganMapper;

    /**
     * 更新运营支撑用户表信息
     * 比对从运营支撑取过来的数据和上次更新的数据的差异并进行插入操作
     * @param organisesList
     * @return true or false
     */
    @Transactional
    public boolean insertOrUpdateYYZCOrganise(List<YYZCOrganise> organisesList){
        boolean flag = true;
        List<YYZCOrganise> organisesInsertList = new ArrayList<YYZCOrganise>();
        List<YYZCOrganise> organisesUpdateList = new ArrayList<YYZCOrganise>();
        //查询现有数据
        QueryWrapper<YYZCOrganise> ew = new QueryWrapper<YYZCOrganise>();
        List<YYZCOrganise> organisesOldList = list(ew);
        Map<String,String> organisesCompareMap = new HashMap<String,String>();
        for(YYZCOrganise compareOrganises:organisesOldList){
            organisesCompareMap.put(compareOrganises.getOrganiseid(), compareOrganises.getMD5());
        }
        //遍历接过来的数据生成MD5码
        for(YYZCOrganise yyzcOrganises:organisesList){
            String md5 = md5Password(yyzcOrganises.toString());
            yyzcOrganises.setMD5(md5);
        }
        //进行比对  形成更新列表和插入列表
        if(organisesCompareMap!=null&&organisesCompareMap.size()!=0){
            for(YYZCOrganise organises:organisesList){
                String organisesId = organises.getOrganiseid();
                String md5 = organises.getMD5();
                //如果存在此用户Id
                if(organisesCompareMap.get(organisesId)!=null){
                    //如果存在此用户 信息MD5码与现存用的的MD5码不一致
                    if(!organisesCompareMap.get(organisesId).equals(md5)){
                        organisesUpdateList.add(organises);
                    }
                }else{
                    organisesInsertList.add(organises);
                }
            }
        } else {
            organisesInsertList = organisesList;
        }
        try {
            if (organisesInsertList.size()>0){
                for (int i = 0; i < organisesInsertList.size(); i++) {
                    YYZCOrganise organise = organisesInsertList.get(i);
                    String organiseId = organise.getOrganiseid().replace("-", "");
                    SysStru stru = sysStruMapper.selectById(organiseId);
                    if (null != stru) {
                        sysStruMapper.deleteById(organiseId);
                        sysOrganMapper.deleteById(organiseId);
                    }
                }
                yyzcOrganiseMapper.insertList(organisesInsertList);
                yyzcOrganiseMapper.insertOrganList(organisesInsertList);
                yyzcOrganiseMapper.insertStruList(organisesInsertList);
                yyzcOrganiseMapper.updateStruRoot();
                yyzcOrganiseMapper.delLeave();
            }
            if (organisesUpdateList.size()>0){
                yyzcOrganiseMapper.updateYYZC(organisesUpdateList);
                yyzcOrganiseMapper.updateOrgan(organisesUpdateList);
                yyzcOrganiseMapper.updateStru(organisesUpdateList);
                yyzcOrganiseMapper.updateStruRoot();
                yyzcOrganiseMapper.delLeave();
            }
        }catch (Exception e){
            e.printStackTrace();
            flag = false;
            throw new RuntimeException("组织机构插入异常！", e);
        }
        return flag;
    }
    /**
     * 生成32位md5码
     * @return
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
}
