/*
 * ApiClient.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.timer.client;

import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.doc.timer.constants.ApiURL;
import net.dongliu.requests.*;

import java.util.Iterator;

/**
 * 类的用途：<p>
 * 创建日期：2018年6月30日 <br>
 * 修改历史：<br>
 * 修改日期：2018年6月30日 <br>
 * 修改作者：XuXinYing <br>
 * 修改内容：修改内容 <br>
 * @author WangBinBin
 * @version 1.0
 */
public class ApiClient {

    /**
     * 客户端
     */
    private final Client client;

    /**
     * 会话
     */
    private final Session session;

    /**
     * 
     * @Title:ApiClient
     */
    public ApiClient() {
        this.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
        this.session = client.session();
    }

    /**
     * 
     * 获取组织机构树
     * @Title: orgTree 
     * @return 组织机构
     */
    public String orgOrganise() {
        return get(ApiURL.ORGANTREE).getBody();
    }

    /**
     * 获取人员树
     * @Title: userList 
     * @return 人员
     */
    public String userList() {
        return get(ApiURL.USERLIST).getBody();
    }

    /**
     * 获取资讯印象列表
     * @Title: messageList
     * @return 人员
     */
    public String messageList(String url) {
        return newget(url).getBody();
    }

    /**
     * 获取人员照片
     * @Title: userphotoList 
     * @return 人员
     */
    public String userphotoList(String url) {
        return newget(url).getBody();
    }

    /**
     * 获取人员照片
     * @Title: userList 
     * @return 人员
     */
    public String cebToPdf(JSONObject r) {
        return post(ApiURL.CEBTOPDF, r).getBody();
    }

    /**
     *  发送get请求
     * @Title: get 
     * @author: WangBinBin
     * @param url url地址
     * @param params 请求参数
     * @return 返回文本内容
     */
    private Response<String> get(ApiURL url, Object... params) {
        HeadOnlyRequestBuilder request = session.get(url.buildUrl(params)).addHeader("User-Agent", ApiURL.USER_AGENT)
                .addHeader("Referer", url.getReferer());
        return request.text();
    }

    /**
     *  发送post请求
     * @Title: post 
     * @author: WangBinBin
      *@param url url地址
     * @param tokenKey token key
     * @param tokenValue token value
     * @param r 请求参数 json列表
     * @return 返回文本内容
     */
    private Response<String> post(ApiURL url, JSONObject r) {
        final PostRequestBuilder post = session.post(url.getUrl()).addHeader("User-Agent", ApiURL.USER_AGENT)
                .addHeader("Referer", url.getReferer()).addHeader("Origin", url.getOrigin());
        final Iterator<String> it = r.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final String value = r.getString(key);
            post.addForm(key, value);
        }
        return post.text();
    }

    /**
     *  发送get请求
     * @Title: get 
     * @author: WangBinBin
     * @param url url地址
     * @param params 请求参数
     * @return 返回文本内容
     */
    private Response<String> newget(String url) {
        MixinHeadOnlyRequestBuilder request = Requests.get(url).socketTimeout(2000_000).connectTimeout(3000_000);
        return request.text();
    }
}
