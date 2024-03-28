package com.jxdinfo.doc.manager.professional.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.manager.professional.dao.ProfessionalMapper;
import com.jxdinfo.doc.manager.professional.model.Professional;
import com.jxdinfo.doc.manager.professional.service.IProfessionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专业专职表 服务实现类
 * </p>
 *
 * @author cxk
 * @since 2021-05-08
 */
@Service
public class ProfessionalServiceImpl extends ServiceImpl<ProfessionalMapper, Professional> implements IProfessionalService {


    /**
     * 专题维护
     */
    @Autowired
    private ProfessionalMapper mapper;


    @Override
    public List<Professional> professionalList(String majorId, String userName, int startIndex, int pageSize) {
        return mapper.professionalList(majorId,userName,startIndex,pageSize);
    }

    @Override
    public int getProfessionalListCount(String majorId, String userName) {
        return mapper.getProfessionalListCount(majorId,userName);
    }

    @Override
    public int operationJudge(String majorId, String id) {
        return mapper.operationJudge(majorId,id);
    }

    @Override
    public Map getProfessionalByMojorId(String majorId) {
        List<Professional> list = mapper.getProfessionalByMojorId(majorId);
        Map<String,String> map = new HashMap<>();
        String userId = "";
        String userName = "";
        for(Professional item:list){
            userId = userId + item.getUserId() + ",";
            userName = userName + item.getUserName() + ",";
        }
        if(userId.length() > 0){
            userId = userId.substring(0,userId.length()-1);
            userName = userName.substring(0,userName.length()-1);
        }
        map.put("userId",userId);
        map.put("userName",userName);
        return map;
    }
}
