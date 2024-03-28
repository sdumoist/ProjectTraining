package com.jxdinfo.doc.manager.sharemanager.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxdinfo.doc.manager.sharemanager.model.DocShare;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: IPersonalShareService
 * @Description:TODO
 * @Author: lishilin
 * @Date: 2019/10/30
 * @Version: 1.0
 */
public interface IPersonalShareService extends IService<DocShare> {
    /**
     * 获取个人分享记录
     * @param userId 当前登录用户id
     * @param beginIndex 历史记录的起始下标
     * @param pageSize 每页记录长度
     * @param name 搜索关键字
     * return 查询到得分享记录列表
     */
    Map<String,Object> getMyShareHistory(String userId, String name, String order, int beginIndex, int pageSize, String timeType,String levelCode,String orgId,List roleList);

    Map<String,Object> getMyShareHistoryMobile(String userId, String name, String order, int beginIndex, int pageSize, String timeType,String levelCode,String orgId,List roleList,List folderIds);

}
