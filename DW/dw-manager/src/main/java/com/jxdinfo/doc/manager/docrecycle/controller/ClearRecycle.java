package com.jxdinfo.doc.manager.docrecycle.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxdinfo.doc.manager.docrecycle.dao.DocRecycleMapper;
import com.jxdinfo.doc.manager.docrecycle.model.DocRecycle;
import com.jxdinfo.hussar.core.util.SpringContextHolder;
import com.jxdinfo.hussar.core.util.ToolUtil;
import com.jxdinfo.hussar.quartz.job.BaseJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 类的用途：定时器，凌晨1点执行 清空过期的文件
 * 创建日期：2018-08-10 17:07;
 * 修改者：ChenXin;
 * @author ChenXin;
 * @version 1.0
 */
public class ClearRecycle implements BaseJob {

    DocRecycleMapper docRecycleMapper = SpringContextHolder.getBean(DocRecycleMapper.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        clear();
    }

    private void clear() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        //-10今天的时间减10天
        calendar.add(Calendar.DAY_OF_MONTH, -10);
        Date date = calendar.getTime();
        Timestamp ts = new Timestamp(date.getTime());
        QueryWrapper ew=new QueryWrapper<DocRecycle>();
        ew.lt("delete_time",ts);
        List<DocRecycle> list = this.docRecycleMapper.selectList(ew);
        String ids="";
        for(DocRecycle docRecycle:list){
            ids+=","+docRecycle.getRecycleId();
        }
        if(ToolUtil.isNotEmpty(ids)){
            String[] id=ids.substring(1).split(",");
            DocRecycle docRecycle=new DocRecycle();
            docRecycle.setClearFlag("0");
            docRecycleMapper.update(docRecycle,new QueryWrapper<DocRecycle>()
                    .in("recycle_id",id));
        }
    }
}
