package com.jxdinfo.doc.front.docsearch.service;

import com.jxdinfo.doc.common.docutil.model.ESResponse;

import java.text.ParseException;
import java.util.Map;

public interface SeachStrengthenService {



    ESResponse<Map<String, Object>> seachStrengthen(String keyword, String contentType, int page, Boolean adminFlag, Integer size, String tagString,Integer titlePower,
                                        Integer contentPower,Integer tagsPower,Integer categoryPower,String folderIds, Integer order);
}
