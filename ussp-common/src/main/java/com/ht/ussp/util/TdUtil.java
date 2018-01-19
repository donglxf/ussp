package com.ht.ussp.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 摘要:
 *
 * @author xyt
 * @create 2018-01-13 下午1:30
 **/
public class TdUtil {
    private static final String CHARSET = "UTF-8";

    // 测试密钥
    private static final String TEST_KEY = "MJNdwerlobvURRJHitgYTEYKopI";
    public static void main(String[] args) throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("username", "xxx");
//        data.put("password", "xxx");
//        // ...
//
        data.put("projectId", "e6643a70-d439-454a-bdeb-3f1519775d3e");
        String invokeJsonString = TdUtil.toJsonString("td_zhulidai", TEST_KEY, data);

        // 打印调用接口所需数据
        System.out.println(invokeJsonString);

        System.out.println("解密后的数据：" + TdUtil.stringToMap(TEST_KEY,invokeJsonString));
    }

    /**
     * 获取调用接口JSON串
     *
     * @param tdUserName 团贷网约定资产端名称
     * @param key        密钥
     * @param data       加密数据
     * @return
     * @throws Exception
     */
    public static String toJsonString(String tdUserName, String key, Object data) throws Exception {
        String jsonString = JsonUtil.obj2Str(data);
        String encryptJsonString = encrypt(key, jsonString);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key", sign(encryptJsonString, tdUserName, key));
        map.put("tdUserName", tdUserName);
        map.put("data", encryptJsonString);
        return JsonUtil.obj2Str(map);
    }

    public static Map<String, Object>  stringToMap(String key,String data) throws Exception {
        Map<String, Object> map = JsonUtil.json2Map(data);
        if(map!=null && map.size()>0){

            String body = URLDecoder.decode(map.get("data").toString(), "UTF-8");
//            String body = map.get("data").toString();
            String decodeData = DESUtil.des3DecodeECB(key,body);
            map.put("data", decodeData);
        }
        return map;
    }

    /**
     * 生成签名串
     *
     * @param encryptJson 待签名数据
     * @param tdUserName  调用名称
     * @param encryptKey  秘钥
     * @return
     */
    private static String sign(String encryptJson, String tdUserName, String encryptKey) {
        // 标准MD5散列算法，可用自家工具类代替
        return HashUtil.md5(String.format("Json:%s_tdUserName:%s_Key:%s", encryptJson, tdUserName, encryptKey));
    }

    /**
     * 加密数据
     *
     * @param key  秘钥
     * @param data 待加密数据
     * @return
     * @throws Exception
     */
    private static String encrypt(String key, String data) throws Exception {
        return URLEncoder.encode(DESUtil.des3EncodeECB(key, data), CHARSET);
    }

    private static class HashUtil {
        private HashUtil() {
        }

        public static final String md5(String text) {
            MessageDigest md = null;
            StringBuilder sb = new StringBuilder();

            try {
                md = MessageDigest.getInstance("MD5");
                md.update(text.getBytes(StandardCharsets.UTF_8));
                for (byte b : md.digest()) {
                    int n = b;
                    if (n < 0) n += 256;
                    if (n < 16) sb.append("0");
                    sb.append(Integer.toHexString(n));
                }
            } catch (NoSuchAlgorithmException e) {

            }

            return sb.toString();
        }
    }

    private static class DESUtil {
        private static final String ALGORITHM = "DESede";
        private static final String CHARSET = "UTF-8";
        private static final String DEFAULT_CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

        private DESUtil() {
        }

        /**
         * ECB加密,不要IV
         *
         * @param key  密钥
         * @param data 明文
         * @return Base64编码的密文
         * @throws Exception
         */
        public static final String des3EncodeECB(String key, String data) throws Exception {
            DESedeKeySpec spec = new DESedeKeySpec(key.substring(0, 24).getBytes(CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Key desKey = keyFactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            byte[] bOut = cipher.doFinal(data.getBytes(CHARSET));
            return new String(Base64.getEncoder().encode(bOut));
        }

        /**
         * ECB解密,不要IV
         *
         * @param key  密钥
         * @param data Base64编码的密文
         * @return 明文
         * @throws Exception
         */
        public static final String des3DecodeECB(String key, String data) throws Exception {
            DESedeKeySpec spec = new DESedeKeySpec(key.substring(0, 24).getBytes(CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Key desKey = keyFactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            byte[] bOut = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(bOut);
        }
    }









}


