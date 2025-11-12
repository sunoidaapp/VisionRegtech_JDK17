package com.vision.dao;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import jcifs.util.Base64;

public class enc {
	static final String SECRET = "Spiral Architect";
	public static void main(String[] args) {
		String enc = passwordEncrypt("SS_MGMT");
		System.out.println(enc);
		System.out.println(passwordDecrypt(enc));
	}
	public static String passwordEncrypt(String plaintext) {
		try {
			byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
			Cipher des = Cipher.getInstance("DES");
			des.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secret, "DES"));
			byte[] ciphertext = des.doFinal(plaintext.getBytes());
			return Base64.encode(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plaintext;
	}
	public static String passwordDecrypt(String ciphertext) {
		try {
			byte[] secret = (SECRET.hashCode() + "").substring(0, 8).getBytes();
			Cipher des = Cipher.getInstance("DES");
			des.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secret, "DES"));
			byte[] plaintext = des.doFinal(Base64.decode(ciphertext));
			return new String(plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ciphertext;
	}

}
