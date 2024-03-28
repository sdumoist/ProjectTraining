/*
 * ApiClient.java
 * 版权所有：金现代信息产业股份有限公司 2017-2022 
 * 金现代信息产业股份有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.jxdinfo.doc.client.login;

import com.alibaba.fastjson.JSONObject;
import net.dongliu.requests.Client;
import net.dongliu.requests.PostRequestBuilder;
import net.dongliu.requests.Response;
import net.dongliu.requests.Session;
import org.csource.common.IniFileReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;


public class LoginClient {

    /**
     * 客户端
     */
    private final Client client;

    /**
     * 会话
     */
    private final Session session;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";



    public LoginClient() {
        this.client = Client.pooled().maxPerRoute(5).maxTotal(10).build();
        this.session = client.session();
    }

    /**
     * 获取人员信息
     * @param token
     * @return
     */
    public  String getUserInfo(String token){
        JSONObject job = new JSONObject();
        job.put("token",token);
        return post(job,"loginUrl","interfaceUrl").getBody();
    }

    /**
     * 退出登录
     * @return
     */
    public  String logout(){
        JSONObject job = new JSONObject();
        return post(job,"logoutUrl","logoutInterfaceUrl").getBody();
    }

    /**
     * 获取配置文件
     * @param propsFilePath
     * @return
     */
   public Properties getProperties(String propsFilePath){
       Properties props = new Properties();
       InputStream in = IniFileReader.loadFromOsFileSystemOrClasspathAsStream(propsFilePath);
       try {
           if (in != null) {
               props.load(in);
           }
       }catch (IOException e){
           e.printStackTrace();
       }
       return props;
   }


    /**
     *  发送post请求
     * @Title: post
     * @param r 请求参数 json列表
     * @return 返回文本内容
     */
    private Response<String> post(JSONObject r,String urlStr,String interfaceUrlStr) {
        Properties  prop = getProperties("loginCheck.properties");
        String url = prop.getProperty(urlStr);
        String interfaceUrl = prop.getProperty(interfaceUrlStr);
        String allUrl = url+interfaceUrl;
        String organUrl = allUrl.substring(0,allUrl.lastIndexOf("/"));
        final PostRequestBuilder post = session.post(url+interfaceUrl).addHeader("User-Agent", USER_AGENT)
                .addHeader("Referer", url).addHeader("Origin", organUrl);
        final Iterator<String> it = r.keySet().iterator();
        while (it.hasNext()) {
            final String key = it.next();
            final String value = r.getString(key);
            post.addForm(key, value);
        }
        return post.text();
    }

}
