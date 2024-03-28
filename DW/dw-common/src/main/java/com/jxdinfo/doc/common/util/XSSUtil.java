package com.jxdinfo.doc.common.util;

public class XSSUtil {
    public  static  String xss(String value){
        if(value!=null){
            value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
            value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
            value = value.replaceAll("'", "& #39;");
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\'][\\s]*javascript:(.*)[\\\"\\']", "\"\"");
            value = value.replaceAll("img", "iMg");
            value = value.replaceAll("script", "");
            value = value.replaceAll("ScRiPt", "");
            value = value.replaceAll("=","& #61;");
            value = value.replaceAll("%3d","& #61;");
            value = value.replaceAll("%253d","& #61;");
            value = value.replaceAll("\"","");

        }
        return  value;
    }
}
