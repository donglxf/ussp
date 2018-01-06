//package com.ht.ussp.gateway.app.util;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.security.InvalidKeyException;
//import java.security.KeyStore;
//import java.security.KeyStore.PrivateKeyEntry;
//import java.security.KeyStoreException;
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//import java.security.Signature;
//import java.security.SignatureException;
//import java.security.UnrecoverableEntryException;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateException;
//import java.util.Base64;
//import java.util.Enumeration;
//
//import javax.annotation.PostConstruct;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Header;
//import io.jsonwebtoken.Jwt;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
///**
// * 
//* @ClassName: JwtUtils
//* @Description: jwt生成与验证
//* @author wim qiuwenwu@hongte.info
//* @date 2018年1月5日 上午9:30:38
// */
//
//public class JwtUtils {
//
//	
//	private static File keyStoreFile;
//	
//	 @PostConstruct     
//	 public void initConfig () {
//		 keyStoreFile = new File(jwtConfig.getKeystore());
//	 }
//	
//	public static String genJwt(com.upbchain.pointcoin.member.api.model.Signature s) throws Exception {
//		PrivateKeyEntry entry = getKeyEntry(keyStoreFile, "storepass");
//		if(entry==null)
//			return null;
//	 	String memId = s.getMemId();
//		String column = s.getColumn();
//		String value = s.getValue();
//        String jwts = Jwts.builder().setSubject("signature demo")
//                .claim("memId", memId)
//                .claim("column", column)
//                .claim("value", value)
//                .setHeaderParam("header1", "header1 value2")
//                .signWith(SignatureAlgorithm.RS256, entry.getPrivateKey())
//                .compact();
//        return jwts;
//	 }
//	 
//	 public static boolean checkJwt(String jwts, com.upbchain.pointcoin.member.api.model.Signature s, boolean isNumberic) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, FileNotFoundException{
//		PrivateKeyEntry entry = getKeyEntry(keyStoreFile, "storepass");
//		if(entry==null)
//			return false;
//		Certificate cert = entry.getCertificate();
//		PublicKey pubKey = cert.getPublicKey();
//	 	String[] jwtsa = jwts.split("\\.");
//	    Signature signature = Signature.getInstance("SHA256withRSA");
//	    signature.initVerify(pubKey);
//	    signature.update((jwtsa[0] + "." + jwtsa[1]).getBytes());
//	    if(!signature.verify(Base64.getUrlDecoder().decode(jwtsa[2])))
//	    	return false;
//	    Jwt<Header, Claims> jwt = null;
//	    // using public key for verify
//	    jwt= Jwts.parser().setSigningKey(pubKey).parse(jwts);
//	    if(jwt.getBody().get("memId")==null||StringUtils.isEmpty(jwt.getBody().get("memId").toString())||!jwt.getBody().get("memId").equals(s.getMemId()))
//	    	return false;
//	    if(jwt.getBody().get("column")==null||StringUtils.isEmpty(jwt.getBody().get("column").toString())||!jwt.getBody().get("column").equals(s.getColumn()))
//	    	return false;
//	    if ( isNumberic == true) {
//	    	try {
//	    		BigDecimal v = new BigDecimal(jwt.getBody().get("value").toString().toCharArray());
//	    		BigDecimal v2 = new BigDecimal(s.getValue().toCharArray());
//	    		v = v.setScale(8, v.ROUND_DOWN);
//	            v2 = v2.setScale(8, v2.ROUND_DOWN);
//	    		if ( v.compareTo(v2) != 0 ) {
//	    			return false;
//	    		}
//			} catch (Exception e) {
//				return false;
//			}
//	    } else {
//	    	if(jwt.getBody().get("value")==null||StringUtils.isEmpty(jwt.getBody().get("value").toString())||!jwt.getBody().get("value").equals(s.getValue()))
//		    	return false;	
//	    }
//	    return true;
//	 }
//	 
//	 private static PrivateKeyEntry getKeyEntry(File keystoreFile, String storepass){
//		 
//		 KeyStore.PrivateKeyEntry privEntry = null;
//		 try {
//			 KeyStore ks = KeystoreManager.loadKeystore(keystoreFile, storepass);
//			 Enumeration<String> aliasInWalletKS = ks.aliases();
//			 while (aliasInWalletKS.hasMoreElements()) {
//		           String alias = aliasInWalletKS.nextElement();
//	
//		           KeyStore.Entry entry = ks.getEntry(alias, ks.isKeyEntry(alias) ? KeystoreManager.getProtParam("test.keystore", alias) : null);
//		
//		           switch (alias) {
//		               case "wallet-default-test":
//		               case "test":
//		                   privEntry = (KeyStore.PrivateKeyEntry) entry;
//		                   break;
//		           }
//			}
//		} catch (CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnrecoverableEntryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return privEntry;
//	 }
//}
//
//
