package com.jxdinfo.doc.front.entry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxdinfo.doc.front.entry.dao.EntryInfoBarMapper;
import com.jxdinfo.doc.front.entry.model.EntryInfoBar;
import com.jxdinfo.doc.front.entry.service.EntryInfoBarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EntryInfoBarServiceImpl extends ServiceImpl<EntryInfoBarMapper, EntryInfoBar> implements EntryInfoBarService{

    @Autowired
    private EntryInfoBarMapper entryInfoBarMapper;



}
