/*
 * ApiURL.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.timer.constants;

/**
 * 类的用途：Api的请求地址和Referer<p>
 * 创建日期：2018年6月1日 <br>
 * 修改历史：<br>
 * 修改日期：2018年6月1日 <br>
 * 修改作者：WangBinBin <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public enum ApiURL {
	/*CEB文件转换*/
    CEBTOPDF("http://127.0.0.1:8081/cebtopdf2/c2b.do", "http://127.0.0.1:8081/"),
    /*获取组织机构树*/
    ORGANTREE("http://192.168.2.246:8081/userservice/organise/list", "http://192.168.2.246:8081/"),
    /*获取人员列表*/
    USERPHOTOLIST("http://192.168.2.246:8081/userservice/sysuser/userPhotolist","http://192.168.2.246:8081/"),
    /*获取人员列表*/
    MESSAGELIST("http://192.168.2.246:8081/xmjbxx/getZxyxMess","http://192.168.2.246:8081"),
    /*获取人员列表*/
    USERLIST("http://192.168.2.246:8081/userservice/sysuser/list", "http://192.168.2.246:8081/"),
    PROJECTLIST("http://192.168.2.246:8081/xmjbxx/getProjectInfo","http://192.168.2.246:8081"),
    GETTOKEN("http://192.168.2.246:8081/xmjbxx/getProjectInfo","http://192.168.2.246:8081");
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";

    private String url;

    private String referer;

    ApiURL(String url, String referer) {
        this.url = url;
        this.referer = referer;
    }

    public String getUrl() {
        return url;
    }

    public String getReferer() {
        return referer;
    }

    /**
     * 获取Origin 防止跨域请求
     * @Title: getOrigin 
     * @author: WangBinBin
     * @return Origin
     */
    public String getOrigin() {
        return this.url.substring(0, url.lastIndexOf("/"));
    }

    /**
     * 拼装URL地址 {0},{1}占位符方式替换
     * @Title: buildUrl 
     * @author: WangBinBin
     * @param params
     * @return
     */
    public String buildUrl(Object... params) {
        int i = 1;
        String url = this.url;
        for (final Object param : params) {
            if (url.contains("{" + i + "}")) {
                url = url.replace("{" + i++ + "}", param.toString());
            }
        }
        return url;
    }

    /**
     * 拼装referer地址 {0},{1}占位符方式替换
     * @Title: buildReferer 
     * @author: WangBinBin
     * @param params
     * @return
     */
    public String buildReferer(Object... params) {
        int i = 1;
        String refererStr = this.referer;
        for (final Object param : params) {
            if (refererStr.contains("{" + i + "}")) {
                refererStr = refererStr.replace("{" + i++ + "}", param.toString());
            }
        }
        return refererStr;
    }

}
