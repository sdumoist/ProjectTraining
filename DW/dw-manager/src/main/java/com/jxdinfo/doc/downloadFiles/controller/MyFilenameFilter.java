package com.jxdinfo.doc.downloadFiles.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFilenameFilter implements FilenameFilter {

    private String fileName;

    private String suffix;

    MyFilenameFilter(String fileName, String suffix){
        this.fileName = fileName;
        this.suffix = suffix;
    }

    @Override
    //重写accept方法,测试指定文件是否应该包含在某一文件列表中
    public boolean accept(File dir, String name) {
        // 创建返回值
        boolean flag = false;
        // 定义筛选条件
        String strRegex = "^" + this.fileName.replace("(","\\(").replace(")","\\)") + "(\\(\\d+\\))?" + this.suffix + "$";
        Pattern pattern = Pattern.compile(strRegex);
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            flag = true;
        }
        return flag;
    }

}

