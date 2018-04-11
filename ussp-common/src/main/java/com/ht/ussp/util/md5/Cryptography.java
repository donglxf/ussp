package com.ht.ussp.util.md5;

import java.net.URLDecoder;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 加密解密工具类
 * @author 林子尧
 */
public class Cryptography {

    private static final Logger log = LoggerFactory.getLogger(Cryptography.class);

    private static final String Algorithm = "DESede"; // 定义 加密算法,可用 DES,DESede,Blowfish

    /**
     * ECB加密
     *
     * @param original 源文
     * @param key      秘钥
     * @return
     */
    public static String tripleDESEncrypt(String original, String key) throws Exception {
        byte[] keybyte = key.substring(0, 24).getBytes("UTF-8");
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
        // 加密
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.ENCRYPT_MODE, deskey);
        return Base64.getEncoder().encodeToString(c1.doFinal(original.getBytes("UTF-8")));
    }

    /**
     * ECB解密
     *
     * @param cryptograph 密文
     * @param key         秘钥
     * @return
     */
    public static String tripleDESDecrypt(String cryptograph, String key) throws Exception {
        byte[] keybyte = key.substring(0, 24).getBytes();
        byte[] src = Base64.getDecoder().decode(cryptograph);
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
        // 解密
        Cipher c1 = Cipher.getInstance(Algorithm);
        c1.init(Cipher.DECRYPT_MODE, deskey);
        return new String(c1.doFinal(src));
    }

    public static void main(String[] args) {
    	try {
    		/*String json="{'bmUserId':'信贷userId','email':'邮箱','idCardNo': '证件号(身份证号)','jobNumber': '工号','userName': '姓名','bmOrgCode': '所属信贷机构编码','bmOrgName': '所属信贷机构名称','orgCode': 'UC所属机构编码','mobile': '手机号码','status': '用户状态0正常1禁用2离职4冻结5锁定','app': '系统编码'}";
    		String en=tripleDESEncrypt(json,"BvVHR3MYs6iWWTtEc4TE0xIwYZg2mDSm");
    		System.out.println(en);
    		
    		System.out.println("解密");
    		System.out.println(tripleDESDecrypt(en,"BvVHR3MYs6iWWTtEc4TE0xIwYZg2mDSm"));*/
    		
    		String str="TGrU1YubUsK5F%2fBK%2bJct45OFMWH9B3jz4oP8UiI55cUbpZH84mx%2fIt16sf%2frxfExSYuAEvAVDLymRmD2BGuZma%2ffyWpxA3QCFHamX%2fBF4vOO6zU2Us739R9PIVhh8rSmDIi48qvBkOk7ZFMqiePeCBrlh%2fcumXN7kTh0LWumyD9PMdcppudvTQ%3d%3d";
    		System.out.println(URLDecoder.decode(str,"UTF-8"));
    		System.out.println(tripleDESDecrypt(URLDecoder.decode(str,"UTF-8"),"BvVHR3MYs6iWWTtEc4TE0xIwYZg2mDSm"));
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
		
	}
    /**
     * CBC加密
     *
     * @param key      密钥
     * @param keyiv    IV
     * @param original 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static String des3EncodeCBC(byte[] key, byte[] keyiv, String original) throws Exception {
        byte[] data = original.getBytes("UTF-8");
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv, 0, 8);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return Base64.getEncoder().encodeToString(bOut);
    }

    /**
     * CBC解密
     *
     * @param key         密钥
     * @param keyiv       IV
     * @param cryptograph Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static String des3DecodeCBC(byte[] key, byte[] keyiv, String cryptograph) throws Exception {
        byte[] data = Base64.getDecoder().decode(cryptograph);
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv, 0, 8);
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return new String(bOut, "UTF-8");
    }

}

