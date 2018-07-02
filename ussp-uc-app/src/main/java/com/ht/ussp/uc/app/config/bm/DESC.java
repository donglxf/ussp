package com.ht.ussp.uc.app.config.bm;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.ht.ussp.util.md5.Cryptography;

/**
 * 信贷系统加密解密类
 */
@Component
public class DESC {
	@Value(value="${bm.bmApi.sign}")
    private String sign = "67902e2fd52504f3859065f8c58d654b";//请求签名
	@Value(value="${bm.bmApi.disturbKey}")
    private String disturbKey = "YInLLGcrVnv"; //MD5干扰码

    private static final String UTF_8 = "UTF-8";

    /**
     * 3DES加密
     *
     * @param original
     * @return
     */
    public String Encryption(String original) {
        try {
            String uuid = UUID.randomUUID().toString();
            //加密userId
            String key = sign + disturbKey + uuid;
            String desKey = getMD5(key, "UTF-8");
            String encryptionData = Cryptography.tripleDESEncrypt(original, desKey);
            EncryptionResult result = new EncryptionResult();
            result.setUuid(uuid);
            result.setParam(encryptionData);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES解密
     *
     * @param original
     * @param uuid
     * @return
     */
    public String Decode(String original, String uuid) {
        try {
            String key = sign + disturbKey + uuid;
            String desKey = getMD5(key, "UTF-8");
            String encryptionData = Cryptography.tripleDESDecrypt(original, desKey);
            EncryptionResult result = new EncryptionResult();
            result.setUuid(uuid);
            result.setParam(encryptionData);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5加密
     *
     * @param str
     * @param encoding
     * @return
     */
    public String getMD5(String str, String encoding) {
        StringBuffer sb = new StringBuffer(32);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(encoding));
            byte[] result = md.digest();
            for (int i = 0; i < result.length; i++) {
                int val = result[i] & 0xff;
                if (val <= 0xf) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return sb.toString().toLowerCase();
    }

    /**
     * 返回加密后的MD5字符串
     * @param str 待加密的字符串
     * @return java.lang.String
     * @author 伦惠峰
     * @Date 2018/1/31 16:11
     */
    public static String getMD5(String str) {

        DESC desc=new DESC();
        return desc.getMD5(str,"utf8");
    }

    /**
     * 对给定的字符串进行base64解码操作
     */
    public static String base64Decoder(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.decodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    /**
     * 对给定的字符串进行base64加密操作
     */
    public static String base64Encoder(String inputData) {
        try {
            if (null == inputData) {
                return null;
            }
            return new String(Base64.encodeBase64(inputData.getBytes(UTF_8)), UTF_8);
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}
