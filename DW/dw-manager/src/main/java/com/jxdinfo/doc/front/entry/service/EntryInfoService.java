package com.jxdinfo.doc.front.entry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.front.entry.model.EntryBody;
import com.jxdinfo.doc.front.entry.model.EntryImgs;
import com.jxdinfo.doc.front.entry.model.EntryInfo;
import com.jxdinfo.doc.front.entry.model.EntryInfoBar;

import java.util.List;
import java.util.Map;

public interface EntryInfoService extends IService<EntryInfo> {

    /**
     * 词条详情
     * @param id
     * @return
     */
    public EntryInfo getEntryDetail(String id);

    /**
     * 查询审核列表
     * @param page
     * @param name
     */
    public List<EntryInfo> getAuditList(Page page, String name);

    /**
     * 查询词条列表
     * @param page
     * @param name
     * @return
     */
    public List<Map<String,Object>> selectList(Page page, Integer name, List<String> tagList);

    /**
     * 根据id查询词条
     * @param ids
     * @return
     */
    public List<EntryInfo> selectListByIds(List<String> ids);

    /**
     * 获取热门词条
     *
     * @return
     */
    public List<EntryInfo> getHotEntrys();

    /**
     * 新增词条信息
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     * @return
     */
    public int addEntryInfo(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList,
                            List<EntryImgs> entryImgsList);

    /**
     * 修改词条信息
     * @param entryInfo
     * @param entryBodyList
     * @param entryInfoBarList
     * @param entryImgsList
     * @return
     */
    public int updateEntryInfo(EntryInfo entryInfo, List<EntryBody> entryBodyList, List<EntryInfoBar> entryInfoBarList,
                               List<EntryImgs> entryImgsList);

    /**
     * 删除词条信息
     * @param entryInfo
     * @return
     */
    public int deleteEntryInfo(EntryInfo entryInfo);

    /**
     * 查询词条数据
     * @param page
     * @param name
     * @param status
     * @param userId
     */
    public List<EntryInfo> getEntryInfoList(Page page, String name, String status, String userId);

    /**
     * 根据Id获取词条详细信息
     * @param id
     * @return
     */
    public Map getEntryInfoDetailById(String id);

    /**
     * 词条审核通过
     * @param entryInfo
     * @return
     */
    public int entryInfoApproved(EntryInfo entryInfo);

    /**
     * 词条审核驳回
     * @param entryInfo
     * @return
     */
    public int entryInfoReject(EntryInfo entryInfo);

}
