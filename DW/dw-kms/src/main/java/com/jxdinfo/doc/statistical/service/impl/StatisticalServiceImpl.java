package com.jxdinfo.doc.statistical.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.question.model.QaQuestion;
import com.jxdinfo.doc.question.service.QuestionService;
import com.jxdinfo.doc.statistical.dao.StatisticalMapper;
import com.jxdinfo.doc.statistical.service.StatisticalService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 统计service 实现层
 * @author cxk
 * @since 2021-05-14
 */
@Service
public class StatisticalServiceImpl extends ServiceImpl<StatisticalMapper,QaQuestion> implements StatisticalService {

    @Autowired
    private StatisticalMapper mapper;

    @Override
    public Map getEchartData(List<String> majorIdList) {
        List<Map<String,Object>> list = mapper.distinguishMajor();
        for (Map<String,Object> item:list) {
            int num = Integer.parseInt(item.get("num")+"");
            if(num > 0 ){
                int timely = mapper.timelyRate(item.get("queIdStr").toString());
                item.put("timely",timely);
            } else {
                item.put("timely",0);
            }

        }
        List<String> a = new ArrayList<>(); // 专业
        List<Integer> b = new ArrayList<>(); // 专业问题数
        List<Double> c = new ArrayList<>(); // 问题及时个数
        List<String> mojorIdArr = new ArrayList<>(); // 专业
        for (Map<String,Object> item:list) {
            a.add(item.get("majorName").toString());
            mojorIdArr.add(item.get("majorId").toString());
            int num = Integer.parseInt(item.get("num")+"");
            b.add(num);
            int timely = Integer.parseInt(item.get("timely")+"");
            if(num == 0 || timely == 0){
                c.add((double) 0);
            } else {
                BigDecimal f = new BigDecimal(timely * 100);
                BigDecimal e = new BigDecimal(num);
                Double h = f.divide(e,2,BigDecimal.ROUND_HALF_UP).doubleValue();
                c.add(h);
            }
        }
        HashSet hs1 = new HashSet(majorIdList);
        HashSet hs2 = new HashSet(mojorIdArr);
        hs1.removeAll(hs2);
        List<String> d = new ArrayList<String>();
        d.addAll(hs1);
        if(d.size() > 0){
            for(int i = 0;i<d.size();i++){
                a.add(d.get(i));
                b.add(0);
                c.add((double) 0);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("majorName", a);
        map.put("queAllNum", b);
        map.put("timelyNum", c);
        return map;
    }

    @Override
    public List<Map<String,Object>> getTableData() {
        List<Map<String,Object>> list = mapper.distinguishMajor();
        for (Map<String,Object> item:list) {
            int num = Integer.parseInt(item.get("num")+"");
            if(num > 0 ){
                int timely = mapper.timelyRate(item.get("queIdStr").toString());
                if(timely == 0){
                    item.put("timely",0);
                } else {
                    BigDecimal f = new BigDecimal(timely * 100);
                    BigDecimal e = new BigDecimal(num);
                    Double h = f.divide(e,2,BigDecimal.ROUND_HALF_UP).doubleValue();
                    item.put("timely",h+"%");
                }
            } else {
                item.put("timely",0);
            }

        }

        return list;
    }
}
