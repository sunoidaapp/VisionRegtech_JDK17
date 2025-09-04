package com.vision.wb;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class JasyptConfig {
	public static void main(String[] args) {
		System.out.println(jaspytPasswordDecrypt("ENC(3ZCNt552KPjXkmEJ+kpXjAFQtg7N6bdb)"));
	}
	public static String jaspytPasswordDecrypt(String encryptedPwd) {
		String decryptedPwd = "";
		try {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	    	encryptor.setPassword("v!$!0n");
	        encryptor.setAlgorithm("PBEWithSHA1AndDESede");
			encryptedPwd = encryptedPwd.substring(4, encryptedPwd.length()-1);
			decryptedPwd = encryptor.decrypt(encryptedPwd);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return decryptedPwd;
	}
}
