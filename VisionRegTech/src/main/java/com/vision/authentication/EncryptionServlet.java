package com.vision.authentication;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vision.exception.JSONExceptionCode;
import com.vision.exception.RuntimeCustomException;
import com.vision.util.JCryptionUtil;
import com.vision.util.ValidationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class EncryptionServlet {
	
	@Autowired
	static JCryptionUtil jCryptionUtil;
	@Autowired
	static SessionContextHolder sessionContextHolder;
	
	private static String twoFactorAuthFlag;
	private static String twoFactorAuthMethod;

	@Value("${twoFactorAuthFlag}")
	public void setTwoFactorAuthFlag(String twoFactorAuthFlag) {
		EncryptionServlet.twoFactorAuthFlag = twoFactorAuthFlag;
	}
	
	@Value("${twoFactorAuthMethod}")
	public void setTwoFactorAuthMethod(String twoFactorAuthMethod) {
		EncryptionServlet.twoFactorAuthMethod = twoFactorAuthMethod;
	}
	
	/*@CrossOrigin(origins = "http://localhost:4200")*/
	@GetMapping(value="generateKeypair")
	static ResponseEntity<JSONExceptionCode> genrateKey(HttpServletRequest request, HttpServletResponse response) throws IOException{
		JSONExceptionCode responseCode = new JSONExceptionCode();
		try {
			String connectionId = request.getSession(true).getId();
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec rsaPublicKeySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			RSAPrivateKeySpec rsaPrivateKeySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);
			System.out.println("Public Module:"+ rsaPublicKeySpec.getModulus()+ "Public Exponent:"+ rsaPublicKeySpec.getPublicExponent());
			TreeMap<String, Object> responseMap = new TreeMap<String, Object>();
			HttpSession ssn = request.getSession(true);
			sessionContextHolder.addOrUpdate(connectionId, ssn);
			byte[] encryptedData = encryptData("Sunoida@123", rsaPublicKeySpec);
			String temporaryToken = EncryptionServlet.genrateKeyWithoutSessionStorage();
			SessionContextHolder.addTokenForConnectionId(temporaryToken, connectionId, keyPair, rsaPublicKeySpec, rsaPrivateKeySpec, encryptedData);
			responseMap.put("temporary-token", temporaryToken);
			
			/* Setting Two Factor Authentication Flag */
			if(!ValidationUtil.isValid(twoFactorAuthFlag)) {
				twoFactorAuthFlag = "false";
			}
		
			responseMap.put("twoFactorAuthFlag", twoFactorAuthFlag);
			responseMap.put("twoFactorAuthMethod", twoFactorAuthMethod);
			
			ConnectionHolder connectionHolder = SessionContextHolder.getconnectionClassFromTempToken(temporaryToken);
			responseCode.setResponse(responseMap);
			return new ResponseEntity<JSONExceptionCode>(responseCode, HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	
	static String genrateKeyWithoutSessionStorage() throws IOException{
		try {
			KeyPair keyPair = jCryptionUtil.generateKeypair(512);
			StringBuffer output = new StringBuffer();
			String e = JCryptionUtil.getPublicKeyExponent(keyPair);
			String n = JCryptionUtil.getPublicKeyModulus(keyPair);
			String md = String.valueOf(JCryptionUtil.getMaxDigits(512));
			return n;
		}catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeCustomException(e.getMessage());
		}
	}
	
	public static RSAPublicKey generateRSAPublicKey(KeyPair keyPair) throws Exception {
		KeyFactory keyFac = null;
		try {
			keyFac = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
		} catch (NoSuchAlgorithmException ex) {
			throw new Exception(ex.getMessage());
		}
		RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(((RSAPublicKey) keyPair.getPublic()).getModulus(), ((RSAPublicKey) keyPair.getPublic()).getPublicExponent());
		try {
			return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
		} catch (InvalidKeySpecException ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	private static byte[] encryptData(String data, RSAPublicKeySpec rsaPublicKeySpec) {
		byte[] dateToEncrypt = data.getBytes();
		byte[] encryptedData = null;
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey pubKey = fact.generatePublic(rsaPublicKeySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			encryptedData = cipher.doFinal(dateToEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedData;
	}
}

