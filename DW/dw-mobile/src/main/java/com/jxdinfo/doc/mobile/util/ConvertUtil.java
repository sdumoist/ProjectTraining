package com.jxdinfo.doc.mobile.util;

import com.jxdinfo.doc.common.util.FileTool;
import com.jxdinfo.doc.manager.docmanager.model.DocInfo;
import com.jxdinfo.doc.manager.docmanager.model.FsFolderView;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhongGuangrui on 2019/1/17.
 * 转换时间类型、转换文件大小 工具类
 */
public class ConvertUtil {
    /**
     * 转化时间的方法
     */
    public static List<DocInfo> changeTime(List<DocInfo> list) {
        for (DocInfo docInfo : list) {
            Timestamp ts = docInfo.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                docInfo.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {
                docInfo.setShowTime(lackTs/(1000*60)+"分钟前");
            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                docInfo.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                docInfo.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String time =ts+"";
                time = time.substring(0,time.indexOf(" "));
                docInfo.setShowTime(time);
            }
        }
        return list;
    }
    /**
     * 转化时间的方法(map)
     * @author zgr
     */
    public static List<Map> changeMapTime(List<Map> list) {
        for (Map docInfo : list) {
            Timestamp ts = (Timestamp) docInfo.get("CREATETIME");
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            String time =ts+"";
            String year = (new Date() + "");
            year = year.substring(year.length() - 4);
            String tsYear = time.substring(0,4);
            if( lackTs < 1000*60 && lackTs<1000*60*60*24){
                time = time.substring(time.indexOf(" ") + 1,time.lastIndexOf(":"));
            }else if (year.equals(tsYear)){
                time = time.substring(5,time.indexOf(" "));
            }else {
                time = time.substring(0,time.indexOf(" "));
            }
            docInfo.put("SHOWTIME",time);
            // TODO: 2019/1/17 暂时写死，拟调es接口返回内容
//            docInfo.put("CONTENT","传统的搜索方式只能根据文件名搜索，但金企文库更酷，可以根据文件内容搜索，" +
//                    "比如我要根据文件内容搜索啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊");
        }
        return list;
    }

    /**
     * 转化时间的方法
     * @author zgr
     */
    public static String changeTime(Timestamp ts) {
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            String time =ts+"";
            String year = (new Date() + "");
            year = year.substring(year.length() - 4);
            String tsYear = time.substring(0,4);
            if( lackTs < 1000*60 && lackTs<1000*60*60*24){
                time = time.substring(time.indexOf(" ") + 1,time.lastIndexOf(":"));
            }else if (year.equals(tsYear)){
                time = time.substring(5,time.indexOf(" "));
            }else {
                time = time.substring(0,time.indexOf(" "));
            }
        return time;
    }
    /**
     * @author yjs
     * @description 转化文件大小的方法,转换时间
     * @param list 待转换数据的列表
     * @return
     */
    public static List<FsFolderView> changeSize(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            fsFolderView.setShowTime(changeTime(fsFolderView.getCreateTime()));
            if (fsFolderView.getFileSize() != null && !"".equals(fsFolderView.getFileSize())) {
                fsFolderView.setFileSize(FileTool.longToString(fsFolderView.getFileSize()));
            }
        }
        return list;
    }

    /**
     * 转化时间的方法
     */
    public static List<FsFolderView> changeTime2(List<FsFolderView> list) {
        for (FsFolderView fsFolderView : list) {
            Timestamp ts = fsFolderView.getCreateTime();
            Long tsLong=  ts.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                fsFolderView.setShowTime("刚刚");
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {
                fsFolderView.setShowTime(lackTs/(1000*60)+"分钟前");
            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                fsFolderView.setShowTime(lackTs/(1000*60*60)+"小时前");
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                fsFolderView.setShowTime(lackTs/(1000*60*60*24)+"天前");
            }else {
                String time =ts+"";
                time = time.substring(0,time.indexOf(" "));
                fsFolderView.setShowTime(time);
            }
        }
        return list;
    }

    /**
     * 转化时间的方法
     */
    public static String changeTime3(Timestamp time) {
            String finalTime;
        if(time==null){
            finalTime = "空数据";
            return finalTime;
        }
            Long tsLong=  time.getTime();
            Long nowTs= new Date().getTime();
            Long lackTs = nowTs -tsLong;
            if( lackTs < 1000*60 ){
                finalTime = "刚刚";
            }else if (lackTs >=  1000*60 && lackTs < 1000*60*60 ) {
                finalTime = lackTs/(1000*60)+"分钟前";
            }else if(lackTs >=1000*60*60 && lackTs<1000*60*60*24 ){
                finalTime = lackTs/(1000*60*60)+"小时前";
            }
            else if(lackTs >=1000*60*60*24 && lackTs<1000*60*60*24*7 ){
                finalTime = lackTs/(1000*60*60*24)+"天前";
            }else {

                finalTime =  changeTime(time);
            }
        return finalTime;
    }
}
