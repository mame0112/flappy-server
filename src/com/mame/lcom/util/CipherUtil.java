package com.mame.lcom.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CipherUtil {

	private final static Logger log = Logger.getLogger(CipherUtil.class
			.getName());

	public static final String ENCRYPT_KEY = "1234567890123456";
	public static final String ENCRYPT_IV = "abcdefghijklmnop";

	public static String encrypt(String text) {
		String strResult = null;

		try {
			// Decode base64 to byte array
			byte[] byteText = text.getBytes("UTF-8");

			// Trancode decode key and initialize vector to byte array
			byte[] byteKey = ENCRYPT_KEY.getBytes("UTF-8");
			byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

			// Create object for decode key and initialize vector
			SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(byteIv);

			// Create Cipher object
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Initialize Cipher object
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);

			// Get cipher result
			byte[] byteResult = cipher.doFinal(byteText);

			// Encode to base64
			// strResult = Base64.encodeBase64String(byteResult);
			strResult = new String(Base64.encodeBase64(byteResult));

		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING,
					"UnsupportedEncodingException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.WARNING,
					"NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			log.log(Level.WARNING, "NoSuchPaddingException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			log.log(Level.WARNING, "InvalidKeyException: " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			log.log(Level.WARNING,
					"IllegalBlockSizeException: " + e.getMessage());
		} catch (BadPaddingException e) {
			log.log(Level.WARNING, "BadPaddingException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			log.log(Level.WARNING,
					"InvalidAlgorithmParameterException: " + e.getMessage());
		}

		return strResult;
	}

	public static String decrypt(String text) {
		String strResult = null;

		try {
			// Decode base64 to byte array
			byte[] byteText = Base64.decodeBase64(text);

			// Trancode decode key and initialize vector to byte array
			byte[] byteKey = ENCRYPT_KEY.getBytes("UTF-8");
			byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

			// Create object for decode key and initialize vector
			SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
			IvParameterSpec iv = new IvParameterSpec(byteIv);

			// Create Cipher object
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			// Initialize Cipher object
			cipher.init(Cipher.DECRYPT_MODE, key, iv);

			// Get decoded result
			byte[] byteResult = cipher.doFinal(byteText);

			// Transcode byte array to string
			strResult = new String(byteResult, "UTF-8");

		} catch (UnsupportedEncodingException e) {
			log.log(Level.WARNING,
					"UnsupportedEncodingException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			log.log(Level.WARNING,
					"NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			log.log(Level.WARNING, "NoSuchPaddingException: " + e.getMessage());
		} catch (InvalidKeyException e) {
			log.log(Level.WARNING, "InvalidKeyException: " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			log.log(Level.WARNING,
					"IllegalBlockSizeException: " + e.getMessage());
		} catch (BadPaddingException e) {
			log.log(Level.WARNING, "BadPaddingException: " + e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			log.log(Level.WARNING,
					"InvalidAlgorithmParameterException: " + e.getMessage());
		}

		return strResult;
	}
}
