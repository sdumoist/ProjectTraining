package com.jxdinfo.doc.manager.docmanager.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.common.util.ESUtil;
import com.jxdinfo.doc.manager.docmanager.dao.DocFileAuthorityMapper;
import com.jxdinfo.doc.manager.docmanager.model.DocFileAuthority;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.service.DocFileAuthorityService;
import com.jxdinfo.doc.manager.docmanager.service.DocInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 文件权限服务实现类
 * </p>
 *
 * @author lyq
 * @since 2018-08-07
 */
@Service
public class DocFileAuthorityServiceImpl extends ServiceImpl<DocFileAuthorityMapper, DocFileAuthority> implements DocFileAuthorityService {
    @Autowired
    private DocFileAuthorityMapper docFileAuthorityMapper;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private ESUtil esUtil;

    @Override
    public List<Integer> judgeFileAuthority(String fileId, String userId, List<String> parentOrganList, List<String> listGroup) {
        return docFileAuthorityMapper.judgeFileAuthority(fileId, userId, parentOrganList, listGroup);
    }

    /**
     * 文件id
     *
     * @param fileIds
     */
    @Override
    public void generateFileAuthorityToEs(List<String> fileIds) {
        try {
            for (String fileId : fileIds) {
                List<DocFileAuthority> fileAuthorities = docFileAuthorityMapper.selectAuthorityList(fileId);
                DocInfo docInfo = docInfoService.getById(fileId);
                if (docInfo != null) {
                    // 组装权限
                    List<String> list = new ArrayList<>();
                    list.add(docInfo.getUserId());
                    if (fileAuthorities != null && fileAuthorities.size() > 0) {
                        for (DocFileAuthority fileAuthority : fileAuthorities) {
                            String authorityId = fileAuthority.getAuthorId();
                            if (fileAuthority.getAuthorType() == 2) {
                                authorityId = fileAuthority.getOrganId();
                            }
                            list.add(authorityId);
                        }
                    }
                    Map<String, Object> esInfo = esUtil.getIndex(fileId);
                    if (esInfo != null) {
                        esInfo.put("permission", list.toArray(new String[list.size()]));
                        esUtil.updateIndex(fileId, esInfo);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("将文件权限加入es异常！");
        }
    }
}
