package com.vision.util;

import java.security.SecureRandom;

public class SecureCodeGenerator {


	    private static final String CAPITAL_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    private static final String SMALL_CHARS = "abcdefghijklmnopqrstuvwxyz";
	    private static final String SYMBOLS = "!@#$";

	    private static final SecureRandom secureRandom = new SecureRandom();

	    // Dynamically generate NUMBERS string: "0123456789"
	    private static final String NUMBERS = generateNumbers();

	    private static String generateNumbers() {
	        StringBuilder numbers = new StringBuilder();
	        for (int i = 0; i <= 9; i++) {
	            numbers.append(i);
	        }
	        return numbers.toString();
	    }

	    public static String generateCode(String flag) {
	        switch (flag.toUpperCase()) {
	            case "OTP":
	                return generateOTP(6); // Only numbers
	            case "CAPTCHA":
	                return generateCaptchaWord(6); // Letters + numbers
	            default:
	                return "Invalid flag. Use OTP, CAPTCHA.";
	        }
	    }

	    private static String generateOTP(int length) {
	        StringBuilder otp = new StringBuilder();
	        for (int i = 0; i < length; i++) {
	            otp.append(NUMBERS.charAt(secureRandom.nextInt(NUMBERS.length())));
	        }
	        return otp.toString();
	    }

	    private static String generateCaptchaWord(int length) {
	        String chars = CAPITAL_CHARS + SMALL_CHARS + NUMBERS;
	        StringBuilder captcha = new StringBuilder();
	        for (int i = 0; i < length; i++) {
	            captcha.append(chars.charAt(secureRandom.nextInt(chars.length())));
	        }
	        return captcha.toString();
	    }

	    public static void main(String[] args) {
	        System.out.println("OTP: " + generateCode("OTP"));
	        System.out.println("CAPTCHA: " + generateCode("CAPTCHA"));
	    }
	}



