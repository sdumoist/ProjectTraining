package com.jxdinfo.doc.unstructured;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxdinfo.hussar.encrypt.util.SM2Util;
import com.jxdinfo.hussar.encrypt.util.SM4Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 接口加解密工具
 * 用于非结构化平台接口加解密
 * 访问接口的时候 contentType设置为apincryptjson则启用加解密功能
 */
@Component
public class ApiJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private String SM2PriKey = "fed37453321fb10821283ef2e6a52de731dca974f0e63761cc96fbfe3fd9d47b";//后台密钥
    private String SM2PubKey = "045182843ecd1aabab0fb5c18b339d633aa2dd278b53ac78d960854ccfafd1792fcbf95fbb2bd1280743e2a04b09c85b0c2f35ea442e50e393a41c8c93123c2c87";//前台公钥

    public static final MediaType APPLICATION_ENCRYPTJSON = new MediaType("application", "apincryptjson");

    protected ObjectMapper objectMapper;

    public ApiJsonHttpMessageConverter() {
        this(APPLICATION_ENCRYPTJSON);
    }

    public ApiJsonHttpMessageConverter(MediaType mediaType) {
        super(mediaType);
    }

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * @param clazz
     * @return
     * @Title: supports
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String contentType = request.getHeader("Content-Type");
        return contentType != null && contentType.toLowerCase().lastIndexOf("apincryptjson") > -1;

    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        String subtype = mediaType.getSubtype();
        if ("apincryptjson".equals(subtype)) {
            return super.canRead(mediaType);
        } else {
            return false;
        }
    }

    /**
     * @param clazz
     * @param inputMessage
     * @return
     * @throws IOException
     * @throws HttpMessageNotReadableException
     * @Title: readInternal
     */
    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        String requestBody = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
        JSONObject json = JSONObject.parseObject(requestBody);
        String data = json.getString("data");
        String sign = json.getString("sign");
        String key = json.getString("key");
        //首先判断签名是否正确
        //String sign_check = SM3Util.digest(data);
        //再获取密钥
        String realKey = SM2Util.decrypt(key, SM2PriKey);
        //使用密钥解密数据
        String realBody = SM4Util.decrypt(data, realKey);
        logger.info("requestBody:orign:{},real:{}", requestBody, realBody);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        request.setAttribute("realRequestBody", realBody);
        return objectMapper.readValue(realBody, clazz);

    }

    /**
     * @param outputMessage
     * @throws IOException
     * @throws HttpMessageNotWritableException
     * @Title: writeInternal
     */
    @Override
    protected void writeInternal(Object resp, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {

        byte[] responseBody = null;
        outputMessage.getHeaders().setContentType(APPLICATION_ENCRYPTJSON);

        String data = objectMapper.writeValueAsString(resp);
        //返回数据加密处理
        //1、生成随机SM4密钥
        String sm4Key = SM4Util.getRandomKey();
        //2、 SM4加密数据
        String encryptedData = SM4Util.encrypt(data, sm4Key);
        //3、SM3对加密后数据签名
        //String sign = SM3Util.digest(encryptedData);
        //4、SM2公钥对SM4密钥加密
        String key = SM2Util.encrypt(sm4Key, SM2PubKey);
        //5、 封装json
        JSONObject json = new JSONObject();
        json.put("data", encryptedData);
        //json.put("sign", sign);
        json.put("key", key);

        responseBody = objectMapper.writeValueAsBytes(json);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        request.setAttribute("realResponseBody", JSONObject.toJSONString(resp));
        logger.info("{}:responseBody:{},real:{}", request.getRequestURI(), resp, new String(responseBody));
        outputMessage.getBody().write(responseBody);
        outputMessage.getBody().flush();
    }
}
