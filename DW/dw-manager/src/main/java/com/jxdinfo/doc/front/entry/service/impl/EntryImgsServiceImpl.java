package com.jxdinfo.doc.front.entry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.entry.dao.EntryImgsMapper;
import com.jxdinfo.doc.front.entry.model.EntryImgs;
import com.jxdinfo.doc.front.entry.service.EntryImgsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryImgsServiceImpl extends ServiceImpl<EntryImgsMapper, EntryImgs> implements EntryImgsService {

    @Autowired
    private EntryImgsMapper entryImgsMapper;


}
