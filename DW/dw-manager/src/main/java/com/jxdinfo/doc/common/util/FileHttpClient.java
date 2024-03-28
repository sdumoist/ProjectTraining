package com.jxdinfo.doc.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class FileHttpClient {
    /**
     * 上传文件  文件为multipartFile
     * @param url
     * @param file
     * @param otherParams
     * @return
     */
    public  String postMultipartFile(String url, MultipartFile file, Map<String,String> otherParams) {
        ClientConnectionManager connManager = new PoolingClientConnectionManager();
        CloseableHttpClient httpClient = new DefaultHttpClient(connManager);
        // 设置超时时间
       /* httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 300000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);*/

        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(300000)
                .setSocketTimeout(300000).setConnectTimeout(300000).build();
        String result = "";
        HttpEntity httpEntity = null;
        HttpEntity responseEntity = null;
        try {
            String fileName = file.getOriginalFilename();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(requestConfig);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            builder.addBinaryBody("file", file.getInputStream(), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            for (Map.Entry<String, String> e : otherParams.entrySet()) {
                builder.addTextBody(e.getKey(), e.getValue());// 类似浏览器表单提交，对应input的name和value
            }
            httpEntity = builder.build();
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }  finally {
        }
        return result;
    }
}
