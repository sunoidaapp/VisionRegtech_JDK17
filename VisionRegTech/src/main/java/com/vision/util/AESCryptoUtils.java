package com.vision.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESCryptoUtils {
    private AESCryptoUtils() {}

    public static byte[] randomBytes(int len) throws Exception {
        byte[] b = new byte[len];
        SecureRandom.getInstanceStrong().nextBytes(b);
        return b;
    }

    public static byte[] buildAesKeyFile(byte[] aesKey32, byte[] iv16) {
        if (aesKey32.length != 32) throw new IllegalArgumentException("AES key must be 32 bytes");
        if (iv16.length != 16) throw new IllegalArgumentException("IV must be 16 bytes");
        byte[] out = new byte[48];
        System.arraycopy(aesKey32, 0, out, 0, 32);
        System.arraycopy(iv16, 0, out, 32, 16);
        return out;
    }

    public static byte[] extractAesKeyFromKeyFile(byte[] keyFile48) {
        if (keyFile48.length != 48) throw new IllegalArgumentException("Key file must be 48 bytes");
        byte[] key = new byte[32];
        System.arraycopy(keyFile48, 0, key, 0, 32);
        return key;
    }

    public static byte[] extractIvFromKeyFile(byte[] keyFile48) {
        if (keyFile48.length != 48) throw new IllegalArgumentException("Key file must be 48 bytes");
        byte[] iv = new byte[16];
        System.arraycopy(keyFile48, 32, iv, 0, 16);
        return iv;
    }

    // Java JCE: PKCS5Padding works for AES block size; functionally PKCS#7 for 16-byte blocks
    public static byte[] aesCbcEncrypt(byte[] plaintext, byte[] aesKey32, byte[] iv16) throws Exception {
        SecretKey key = new SecretKeySpec(aesKey32, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv16));
        return cipher.doFinal(plaintext);
    }

    public static byte[] aesCbcDecrypt(byte[] ciphertext, byte[] aesKey32, byte[] iv16) throws Exception {
        SecretKey key = new SecretKeySpec(aesKey32, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv16));
        return cipher.doFinal(ciphertext);
    }
}
