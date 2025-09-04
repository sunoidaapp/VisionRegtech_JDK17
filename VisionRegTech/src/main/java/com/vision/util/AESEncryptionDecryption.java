package com.vision.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptionDecryption {
	
	public static String encrypt(String strToEncrypt, String secret) {
		try {
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//			setKey(secret);
			SecretKeySpec aesSecretKey= new SecretKeySpec(secret.substring(0, 16).getBytes("UTF-8"),"AES" );
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
//			Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	public static String decrypt(String strToDecrypt, String secret) {
		try {
//			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			SecretKeySpec aesSecretKey= new SecretKeySpec(secret.substring(0, 16).getBytes("UTF-8"),"AES" );
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
//			Cipher cipher = Cipher.getInstance("RSA/NONE/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
}
