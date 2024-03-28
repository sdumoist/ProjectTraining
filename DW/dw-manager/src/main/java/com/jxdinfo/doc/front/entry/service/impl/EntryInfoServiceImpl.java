package com.jxdinfo.doc.front.entry.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.docutil.model.DocES;
import com.jxdinfo.doc.common.docutil.service.ESService;
import com.jxdinfo.doc.common.util.ExceptionUtils;
import com.jxdinfo.doc.common.util.SpringContextUtil;
import com.jxdinfo.doc.common.util.StringUtil;
import com.jxdinfo.doc.common.util.UserInfoUtil;
import com.jxdinfo.doc.front.entry.dao.EnrtyInfoMapper;
import com.jxdinfo.doc.front.entry.model.EntryBody;
import com.jxdinfo.doc.front.entry.model.EntryImgs;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.model.EntryInfoBar;
import com.jxdinfo.doc.front.entry.service.EntryBodyService;
import com.jxdinfo.doc.front.entry.service.EntryImgsService;
import com.jxdinfo.doc.front.entry.service.EntryInfoBarService;
import com.jxdinfo.doc.front.entry.service.EntryInfoService;
import com.jxdinfo.hussar.core.util.DateUtil;
import com.mysql.cj.xdevapi.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EntryInfoServiceImpl extends ServiceImpl<EnrtyInfoMapper, EntryInfo> implements EntryInfoService {


    static final public Logger LOGGER = LoggerFactory.getLogger(EntryInfoServiceImpl.class);

    @Autowired
    private EnrtyInfoMapper enrtyInfoMapper;

    /**
     * 词条详情
     * @param id
     * @return
     */
    public EntryInfo getEntryDetail(String id){
        return enrtyInfoMapper.getEntryDetail(id);
    }

    /**
     * 查询审核列表
     * @param page
     * @param name
     */
    public List<EntryInfo> getAuditList(Page page, String name){
       return enrtyInfoMapper.getAuditList(page, name);
    }

    /**
     * 查询审核列表
     * @param page
     * @param order 排序 2 降序 3 升序
     */
    public List<Map<String,Object>> selectList(Page page, Integer order, List<String> tagList){
       return enrtyInfoMapper.selectList(page, order, tagList);
    }

    @Autowired
    private EntryBodyService entryBodyService;

    @Autowired
    private EntryInfoBarService entryInfoBarService;

    @Autowired
    private EntryImgsService entryImgsService;

    private static ApplicationContext appCtx = SpringContextUtil.getApplicationContext();

    /**
     * ES操作接口
     */
    @Autowired
    private ESService esService;


    @Override
    public List<EntryInfo> selectListByIds(List<String> ids) {
        return enrtyInfoMapper.selectListByIds(ids);
    }

    /**
     * 获取热门词条
     *
     * @return
     */
    public List<EntryInfo> getHotEntrys() {
        return enrtyInfoMapper.getHotEntrys();
    }

    /**
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     * @return
     */
    @Override
    public int addEntryInfo(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList, List<EntryImgs> entryImgsList) {
        int result = 0;
        entryInfo.setState("0");//状态(0：待审核，1：已审核 2：已驳回)
        entryInfo.setValidFlag("1");//状态( 0 无效 1 有效)
        entryInfo.setReadNum(0);
        entryInfo.setCreateUserId(UserInfoUtil.getUserInfo().get("ID").toString());
        entryInfo.setCreateTime(DateUtil.format(new Date()));
        boolean flag = this.saveOrUpdate(entryInfo);
        for (EntryBody b: entryBodyList) {
            b.setEntryId(entryInfo.getId());
        }
        for (EntryInfoBar info: entryInfoBarList) {
            info.setEntryId(entryInfo.getId());
        }
        for (EntryImgs img: entryImgsList) {
            img.setEntryId(entryInfo.getId());
        }
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        if (entryBodyList.size() > 0) {
            flag1 = entryBodyService.saveOrUpdateBatch(entryBodyList);
        } else {
            flag1 = true;
        }
        if (entryInfoBarList.size() > 0) {
            flag2 = entryInfoBarService.saveOrUpdateBatch(entryInfoBarList);
        } else {
            flag2 = true;
        }
        if (entryImgsList.size() > 0) {
            flag3 = entryImgsService.saveOrUpdateBatch(entryImgsList);
        } else {
            flag3 = true;
        }
        //将词条信息加入ES中
        boolean flag4 = this.createEsIndex(entryInfo, entryBodyList, entryInfoBarList, entryImgsList);
        if (flag && flag1 && flag2 && flag3 && flag4) {
            result = 1;
        }
        return result;
    }

    /**
     * 修改词条信息
     *
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     * @return
     */
    @Override
    public int updateEntryInfo(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList, List<EntryImgs> entryImgsList) {
        int result = 0;
        entryInfo.setState("0");//状态(0：待审核，1：已审核 2：已驳回)
        entryInfo.setUpdateUserId(UserInfoUtil.getUserInfo().get("ID").toString());
        entryInfo.setUpdateTime(DateUtil.format(new Date()));
        boolean flag = this.saveOrUpdate(entryInfo);
        for (EntryBody b: entryBodyList) {
            b.setEntryId(entryInfo.getId());
        }
        for (EntryInfoBar info: entryInfoBarList) {
            info.setEntryId(entryInfo.getId());
        }
        for (EntryImgs img: entryImgsList) {
            img.setEntryId(entryInfo.getId());
        }
        //删除旧的子表数据
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("entry_id", entryInfo.getId());
        entryBodyService.remove(queryWrapper);
        entryInfoBarService.remove(queryWrapper);
        entryImgsService.remove(queryWrapper);
        //保存新的子表数据
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        if (entryBodyList.size() > 0) {
            flag1 = entryBodyService.saveOrUpdateBatch(entryBodyList);
        } else {
            flag1 = true;
        }
        if (entryInfoBarList.size() > 0) {
            flag2 = entryInfoBarService.saveOrUpdateBatch(entryInfoBarList);
        } else {
            flag2 = true;
        }
        if (entryImgsList.size() > 0) {
            flag3 = entryImgsService.saveOrUpdateBatch(entryImgsList);
        } else {
            flag3 = true;
        }

        //更新ES中的词条信息
        boolean flag4 = this.updateEsIndex(entryInfo, entryBodyList, entryInfoBarList, entryImgsList);
        if (flag && flag1 && flag2 && flag3 && flag4) {
            result = 1;
        }
        return result;
    }

    /**
     * 删除词条信息
     *
     * @param entryInfo
     * @return
     */
    @Override
    public int deleteEntryInfo(EntryInfo entryInfo) {
        int result = 0;
        entryInfo.setValidFlag("0");//状态( 0 无效 1 有效)
        boolean flag1 = this.saveOrUpdate(entryInfo);
        //修改ES索引回收状态
        boolean flag2 = this.updateEsIndexRecycle(entryInfo, "0");//0无效，1有效
        if (flag1 && flag2) {
            result = 1;
        }
        return result;
    }

    /**
     * 查询词条数据
     *
     * @param page
     * @param name
     * @param status
     * @param userId
     */
    @Override
    public List<EntryInfo> getEntryInfoList(Page page, String name, String status, String userId) {
        return enrtyInfoMapper.getEntryInfoList(page, name, status, userId);
    }

    /**
     * 根据Id获取词条详细信息
     *
     * @param id
     * @return
     */
    @Override
    public Map getEntryInfoDetailById(String id) {
        Map map = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("entry_id", id);
        queryWrapper.orderByAsc("showOrder");
        map.put("entryInfo", this.getById(id));
        map.put("entryImgsList", entryImgsService.list(queryWrapper));
        map.put("entryInfoBarList", entryInfoBarService.list(queryWrapper));
        map.put("entryBodyList", entryBodyService.list(queryWrapper));
        return map;
    }

    /**
     * 词条审核通过
     *
     * @param entryInfo
     * @return
     */
    @Override
    public int entryInfoApproved(EntryInfo entryInfo) {
        int result = 0;
        entryInfo.setValidFlag("1");//状态( 0 无效 1 有效)
        entryInfo.setState("1"); //状态：0：待审核，1：已审核 2：已驳回
        boolean flag1 = this.saveOrUpdate(entryInfo);
        //修改ES索引回收状态
        boolean flag2 = this.updateEsIndexRecycle(entryInfo, "1");//0无效，1有效
        if (flag1 && flag2) {
            result = 1;
        }
        return result;
    }

    /**
     * 词条审核驳回
     *
     * @param entryInfo
     * @return
     */
    @Override
    public int entryInfoReject(EntryInfo entryInfo) {
        int result = 0;
        entryInfo.setValidFlag("1");// 状态( 0 无效 1 有效)
        entryInfo.setState("2"); // 状态：0：待审核，1：已审核 2：已驳回
        boolean flag1 = this.saveOrUpdate(entryInfo);
        //修改ES索引回收状态
        boolean flag2 = this.updateEsIndexRecycle(entryInfo,"0");//0无效，1有效
        if (flag1 && flag2) {
            result = 1;
        }
        return result;
    }

    /**
     * 创建ES索引
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     */
    public boolean createEsIndex(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList, List<EntryImgs> entryImgsList) {
        boolean flag = false;
        LOGGER.info("******************词条:" + entryInfo.getName() + "正在生成ES索引******************");
        try {
            DocES docVOBean = new DocES();
            docVOBean.setId(entryInfo.getId());
            String content = (entryInfo == null ? "" : JSONObject.toJSONString(entryInfo))
                    + (entryBodyList.size() > 0 ? JSONObject.toJSONString(entryBodyList) : "")
                    + (entryInfoBarList.size() > 0 ? JSONObject.toJSONString(entryInfoBarList) : "")
                    + (entryImgsList.size() > 0 ?JSONObject.toJSONString(entryImgsList) : "");
            docVOBean.setContent(content);
            docVOBean.setContentType("entry");
            docVOBean.setTitle(entryInfo.getName());
            docVOBean.setTags(entryInfo.getTag());
            docVOBean.setRecycle("0");//0无效，1有效
            docVOBean.setUpDate(new Date());
            esService.createESIndex(docVOBean);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("创建词条：" + entryInfo.getName() + "ES索引失败：" + ExceptionUtils.getErrorInfo(e));
        }
        LOGGER.info("******************词条:" + entryInfo.getName() + "创建ES索引结束******************");
        return flag;
    }

    /**
     * 修改ES索引
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     */
    public boolean updateEsIndex(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList, List<EntryImgs> entryImgsList) {
        boolean flag = false;
        try {
            Map<String, Object> docVO = new HashMap();
            String content = (entryInfo == null ? "" : JSONObject.toJSONString(entryInfo))
                    + (entryBodyList.size() > 0 ? JSONObject.toJSONString(entryBodyList) : "")
                    + (entryInfoBarList.size() > 0 ? JSONObject.toJSONString(entryInfoBarList) : "")
                    + (entryImgsList.size() > 0 ?JSONObject.toJSONString(entryImgsList) : "");
            docVO.put("content", content);
            docVO.put("title", entryInfo.getName());
            docVO.put("tags", entryInfo.getTag());
            docVO.put("recycle", "0");//0无效，1有效
            docVO.put("upDate", new Date());
            if (esService.getIndex(entryInfo.getId()) != null) {
                LOGGER.info("******************词条:" + entryInfo.getName() + "正在修改ES索引******************");
                esService.updateIndex(entryInfo.getId(), docVO);
                LOGGER.info("******************词条:" + entryInfo.getName() + "修改ES索引结束******************");
                flag = true;
            } else {
                flag = this.createEsIndex(entryInfo, entryBodyList, entryInfoBarList, entryImgsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("修改词条：" + entryInfo.getName() + "ES索引失败：" + ExceptionUtils.getErrorInfo(e));
        }
        return flag;
    }

    /**
     * 修改ES索引回收状态
     * @param entryInfo
     */
    public boolean updateEsIndexRecycle(EntryInfo entryInfo, String recycleValue) {
        boolean flag = false;
        try {
            Map<String, Object> docVO = new HashMap();
            docVO.put("recycle", recycleValue);//是否在回收站 0表示不在回收站，1表示在回收站
            docVO.put("upDate", new Date());
            if (esService.getIndex(entryInfo.getId()) != null) {
                LOGGER.info("******************词条:" + entryInfo.getName() + "正在更新ES索引******************");
                esService.updateIndex(entryInfo.getId(), docVO);
                LOGGER.info("******************词条:" + entryInfo.getName() + "更新ES索引结束******************");
                flag = true;
            }else{
                DocES docVO1 = new DocES();
                docVO1.setId(entryInfo.getId());
                docVO1.setRecycle(recycleValue);//是否在回收站 0表示不在回收站，1表示在回收站
                docVO1.setUpDate(new Date());
                esService.createESIndex(docVO1);
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("删除词条：" + entryInfo.getName() + "ES索引失败：" + ExceptionUtils.getErrorInfo(e));
        }
        return flag;
    }

}
