package com.vision.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public final class RsaUtils {
    private RsaUtils() {}

    public static KeyPair generateRsaKeyPair(int keySize) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keySize, SecureRandom.getInstanceStrong());
        return kpg.generateKeyPair();
    }

    public static void savePublicKeyBase64(PublicKey pub, Path out) throws Exception {
        String b64 = Base64.getEncoder().encodeToString(pub.getEncoded()); // X.509
        Files.writeString(out, b64);
    }

    public static void savePrivateKeyBase64(PrivateKey priv, Path out) throws Exception {
        String b64 = Base64.getEncoder().encodeToString(priv.getEncoded()); // PKCS#8
        Files.writeString(out, b64);
    }

    public static PublicKey loadPublicKeyBase64(Path in) throws Exception {
        String b64 = Files.readString(in).trim();
        byte[] der = Base64.getDecoder().decode(b64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public static PrivateKey loadPrivateKeyBase64(Path in) throws Exception {
        String b64 = Files.readString(in).trim();
        byte[] der = Base64.getDecoder().decode(b64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    // IRS-style: RSA/ECB/PKCS1Padding (PKCS#1 v1.5)
    public static byte[] rsaEncryptPkcs1(byte[] data, PublicKey publicKey) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.ENCRYPT_MODE, publicKey);
        return rsa.doFinal(data);
    }

    public static byte[] rsaDecryptPkcs1(byte[] encrypted, PrivateKey privateKey) throws Exception {
        Cipher rsa = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsa.init(Cipher.DECRYPT_MODE, privateKey);
        return rsa.doFinal(encrypted);
    }
}
