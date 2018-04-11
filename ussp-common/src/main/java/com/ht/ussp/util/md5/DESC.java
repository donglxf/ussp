package com.ht.ussp.util.md5;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

/**
 * 信贷系统加密解密类
 */
@Component
public class DESC {
    
	public static String disturbKey = "BvVHR3MYs6iWWTtEc4TE0xIwYZg2mDSm"; //MD5干扰码

    private static final String UTF_8 = "UTF-8";

    /**
     * 3DES加密
     *
     * @param original
     * @return
     */
    public static String Encryption(String original) {
        try {
            return Cryptography.tripleDESEncrypt(original, disturbKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
//		String json="{'bmUserId': '信贷userId','email': '邮箱','idCardNo': '证件号(身份证号)','jobNumber': '工号','userName': '姓名','bmOrgCode': '所属信贷机构编码','bmOrgName': '所属信贷机构名称','orgCode': 'UC所属机构编码','mobile': '手机号码','status': '用户状态0正常1禁用2离职4冻结5锁定','app': '系统编码'}";
//		String en=Encryption(json);
//		System.out.println(en);
//		EncryptionResult result = JSON.parseObject(en,EncryptionResult.class);
//		System.out.println("解密");
//		System.out.println(Decode(Encryption(result.getParam())));
    	String str="TGrU1YubUsK5F%2fBK%2bJct45OFMWH9B3jz4oP8UiI55cUbpZH84mx%2fIt16sf%2frxfExSYuAEvAVDLymRmD2BGuZma%2ffyWpxA3QCFHamX%2fBF4vOO6zU2Us739R9PIVhh8rSmDIi48qvBkOk7ZFMqiePeCBrlh%2fcumXN7kTh0LWumyD9PMdcppudvTQ%3d%3d";
		try {
			//System.out.println(URLDecoder.decode(str,"UTF-8"));
			System.out.println(DESC.Decode(URLDecoder.decode(str,"UTF-8")));
			String keyvalue = String.format("Json:%s-Key:%s", str, "BvVHR3MYs6iWWTtEc4TE0xIwYZg2mDSm");
			System.out.println(keyvalue);
			String key = getMD5(keyvalue);
			System.out.println(key);
			/*String mi="TGrU1YubUsK5F/BK+Jct45OFMWH9B3jz4oP8UiI55cUbpZH84mx/It16sf/rxfExSYuAEvAVDLymRmD2BGuZma/fyWpxA3QCFHamX/BF4vOO6zU2Us739R9PIVhh8rSmDIi48qvBkOk7ZFMqiePeCBrlh/cumXN7kTh0LWumyD9PMdcppudvTQ==";
			String key2 = Decode(mi);
			System.out.println(key2);
			String key3 = Encryption(key2);
			System.out.println(key3);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    

    /**
     * 3DES解密
     *
     * @param original
     * @param uuid
     * @return
     */
    public static String Decode(String original) {
        try {
            return Cryptography.tripleDESDecrypt(original, disturbKey);
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
    public static String getMD5(String str, String encoding) {
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
