package com.jxdinfo.doc.common.util;



import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class DesUtil {

    private DesUtil() {
        throw new AssertionError("No DesCipherUtil instances for you!");
    }

    static {
        // add BC provider
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 加密
     *
     * @param encryptText 需要加密的信息
     * @param key 加密密钥
     * @return 加密后Base64编码的字符串
     */
    public static String encrypt(String encryptText, String key) {

        if (encryptText == null || key == null) {
            throw new IllegalArgumentException("encryptText or key must not be null");
        }

        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding", "");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] bytes = cipher.doFinal(encryptText.getBytes(Charset.forName("UTF-8")));
            return Base64.getEncoder().encodeToString(bytes);

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException
                | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            throw new RuntimeException("encrypt failed", e);
        }

    }

    /**
     * 解密
     *
     * @param decryptText 需要解密的信息
     * @param key 解密密钥，经过Base64编码
     * @return 解密后的字符串
     */
    public static  String decrypt(String decryptText, String key) {

        if (decryptText == null || key == null) {
            throw new IllegalArgumentException("decryptText or key must not be null");
        }

        try {
            DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = secretKeyFactory.generateSecret(desKeySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", " BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] bytes = new byte[0];

                bytes = cipher.doFinal(decryptText.getBytes());

            return new String(bytes, Charset.forName("UTF-8"));

        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | NoSuchPaddingException
                | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException e) {
            throw new RuntimeException("decrypt failed", e);
        }
    }

    public static String base64_decode_8859(final String source) {
        String result = "";


        final Base64.Decoder decoder = Base64.getDecoder();
        try {
            result = new String(decoder.decode(source), "ISO-8859-1");
            //此处的字符集是ISO-8859-1
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
return result;
    }

}