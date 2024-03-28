package com.jxdinfo.doc.front.entry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.entry.dao.EntryBodyMapper;
import com.jxdinfo.doc.front.entry.model.EntryBody;
import com.jxdinfo.doc.front.entry.service.EntryBodyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryBodyServiceImpl extends ServiceImpl<EntryBodyMapper, EntryBody> implements EntryBodyService {

    @Autowired
    private EntryBodyMapper entryBodyMapper;

}
