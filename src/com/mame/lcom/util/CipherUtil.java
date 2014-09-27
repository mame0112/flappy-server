package com.mame.lcom.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.appengine.api.datastore.Blob;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.exception.LcomCipherException;

public class CipherUtil {

	private final static Logger log = Logger.getLogger(CipherUtil.class
			.getName());

	private final static String TAG = "CipherUtil";

	// public static final String ENCRYPT_KEY = "1234567890123456";
	public static final String LOOSECOM_DB_KEY = "loosecomm_db_key";
	public static final String ENCRYPT_IV = "loosecomm_vector";

	private Cipher mCipher = null;

	private final static int AES_KEY_LENGTH = 16;

	private SecretKeySpec mKey = null;

	private IvParameterSpec mIv = null;

	public CipherUtil() {
		try {

			// Trancode decode key and initialize vector to byte array
			byte[] byteKey = LOOSECOM_DB_KEY.getBytes("UTF-8");
			byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

			// Create object for decode key and initialize vector
			mKey = new SecretKeySpec(byteKey, "AES");
			mIv = new IvParameterSpec(byteIv);

			// Create Cipher object
			mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

		} catch (UnsupportedEncodingException e) {
			DbgUtil.showLog(TAG,
					"UnsupportedEncodingException: " + e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			DbgUtil.showLog(TAG, "NoSuchAlgorithmException: " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			DbgUtil.showLog(TAG, "NoSuchPaddingException: " + e.getMessage());
		}
	}

	public String encryptForInputString(String input) {

		String output = null;

		// If it is not debug mode
		if (LcomConst.IS_ENCRYPT) {
			if (input != null && mCipher != null) {
				// Decode base64 to byte array
				try {

					mCipher.init(Cipher.ENCRYPT_MODE, mKey, mIv);

					byte[] byteText = input.getBytes("UTF-8");

					String test = new String(byteText, "UTF-8");
					DbgUtil.showLog(TAG, "test:" + test);

					// Get cipher result
					byte[] byteResult;
					byteResult = mCipher.doFinal(byteText);

					// Encode to base64
					// strResult = Base64.encodeBase64String(byteResult);
					output = new String(Base64.encodeBase64(byteResult));

				} catch (UnsupportedEncodingException e) {
					DbgUtil.showLog(TAG,
							"UnsupportedEncodingException: " + e.getMessage());
				} catch (IllegalBlockSizeException e) {
					DbgUtil.showLog(TAG,
							"IllegalBlockSizeException: " + e.getMessage());
				} catch (BadPaddingException e) {
					DbgUtil.showLog(TAG,
							"BadPaddingException: " + e.getMessage());
				} catch (InvalidKeyException e) {
					DbgUtil.showLog(TAG,
							"InvalidKeyException: " + e.getMessage());
				} catch (InvalidAlgorithmParameterException e) {
					DbgUtil.showLog(TAG, "InvalidAlgorithmParameterException: "
							+ e.getMessage());
				}
			}
		} else {
			// If debug is true, just output input parameter
			output = input;
		}

		DbgUtil.showLog(TAG, "output: " + output);

		return output;
	}

	public String decryptForInputString(String input) {
		DbgUtil.showLog(TAG, "decryptForInputString");
		String output = null;

		// If it is not debug mode
		if (LcomConst.IS_ENCRYPT) {
			if (input != null && mCipher != null) {
				try {

					// Initialize Cipher object
					mCipher.init(Cipher.DECRYPT_MODE, mKey, mIv);

					// Decode base64 to byte array
					byte[] byteText = Base64.decodeBase64(input);

					// Get decoded result
					byte[] byteResult = mCipher.doFinal(byteText);

					// Transcode byte array to string
					output = new String(byteResult, "UTF-8");

				} catch (UnsupportedEncodingException e) {
					DbgUtil.showLog(TAG,
							"UnsupportedEncodingException: " + e.getMessage());
				} catch (IllegalBlockSizeException e) {
					DbgUtil.showLog(TAG,
							"IllegalBlockSizeException: " + e.getMessage());
				} catch (BadPaddingException e) {
					DbgUtil.showLog(TAG,
							"BadPaddingException: " + e.getMessage());
				} catch (InvalidKeyException e) {
					DbgUtil.showLog(TAG,
							"InvalidKeyException: " + e.getMessage());
				} catch (InvalidAlgorithmParameterException e) {
					DbgUtil.showLog(TAG, "InvalidAlgorithmParameterException: "
							+ e.getMessage());
				}
			}
		} else {
			// If debug is true, just output input parameter
			output = input;
		}

		return output;
	}

	public Blob decryptForInputBlob(Blob input) {
		Blob output = null;

		// If it is not debug mode
		if (LcomConst.IS_ENCRYPT) {
			if (input != null && mCipher != null) {
				try {

					mCipher.init(Cipher.DECRYPT_MODE, mKey, mIv);

					// Decode base64 to byte array
					byte[] byteText = input.getBytes();

					// Get decoded result
					byte[] byteResult = mCipher.doFinal(byteText);

					// Transcode byte array to string
					output = new Blob(byteResult);

				} catch (IllegalBlockSizeException e) {
					DbgUtil.showLog(TAG,
							"IllegalBlockSizeException: " + e.getMessage());
				} catch (BadPaddingException e) {
					DbgUtil.showLog(TAG,
							"BadPaddingException: " + e.getMessage());
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			// If debug is true, just output input parameter
			output = input;
		}

		return output;
	}

	public List<LcomNewMessageData> decryptForNewMessageData(
			List<LcomNewMessageData> input) {
		List<LcomNewMessageData> output = new ArrayList<LcomNewMessageData>();

		if (LcomConst.IS_ENCRYPT) {
			if (input != null && input.size() != 0 && mCipher != null) {
				for (LcomNewMessageData data : input) {
					DbgUtil.showLog(TAG, "data: " + data);
					// long userId, long targetUserId,
					// String targetUserName, List<String> message, List<Long>
					// postedDate,
					// List<Long> expireTime
					LcomNewMessageData decryptedData = new LcomNewMessageData(
							data.getUserId(), data.getTargetUserId(),
							decryptForInputString(data.getTargetUserName()),
							decryptInputStringList(data.getMessage()),
							data.getPostedDate(), data.getExpireDate());
					output.add(decryptedData);
				}
			}
		} else {
			output = input;
		}

		return output;
	}

	private List<String> decryptInputStringList(List<String> inputs) {
		DbgUtil.showLog(TAG, "decryptInputStringList");
		List<String> output = new ArrayList<String>();
		if (inputs != null && inputs.size() != 0) {
			for (String str : inputs) {
				output.add(decryptForInputString(str));
			}
		}
		return output;
	}

	public LcomUserData decryptLcomUserData(LcomUserData data) {

		if (data != null) {
			Blob before = data.getThumbnail();

			LcomUserData output = new LcomUserData(data.getUserId(),
					decryptForInputString(data.getUserName()),
					decryptForInputString(data.getPassword()),
					decryptForInputString(data.getMailAddress()),
					decryptForInputBlob(before));
			return output;
		}

		return null;
	}

	public List<LcomFriendshipData> decryptForLcomFriendshipData(
			List<LcomFriendshipData> input) {
		DbgUtil.showLog(TAG, "decryptForLcomFriendshipData");
		List<LcomFriendshipData> output = new ArrayList<LcomFriendshipData>();

		if (LcomConst.IS_ENCRYPT) {
			if (input != null && input.size() != 0 && mCipher != null) {
				DbgUtil.showLog(TAG, "size: " + input.size());

				for (LcomFriendshipData data : input) {
					// List<String> messages = decryptInputStringList(data
					// .getLatestMessage());
					// String name = decryptForInputString(data
					// .getSecondUserName());
					//
					// data.setLatestMessage(messages);
					// data.setSecondUserName(name);

					// long userId, long friendUserId,
					// String friendUserName, List<String> newMessage,
					// List<Long> expireTime

					LcomFriendshipData decryptedData = new LcomFriendshipData(
							data.getFirstUserId(), data.getSecondUserId(),
							decryptForInputString(data.getSecondUserName()),
							decryptInputStringList(data.getLatestMessage()),
							data.getLastMessageExpireTime());

					output.add(decryptedData);

				}
			}
		} else {
			output = input;
		}

		return output;
	}

	public Blob encryptForInputBlob(Blob input) {

		Blob output = null;

		// If it is not debug mode
		if (LcomConst.IS_ENCRYPT) {
			if (input != null && mCipher != null) {
				// Decode base64 to byte array
				try {

					mCipher.init(Cipher.ENCRYPT_MODE, mKey, mIv);

					byte[] byteText = input.getBytes();

					// Get cipher result
					byte[] byteResult = mCipher.doFinal(byteText);

					// Encode to base64
					output = new Blob(byteResult);

				} catch (IllegalBlockSizeException e) {
					DbgUtil.showLog(TAG,
							"IllegalBlockSizeException: " + e.getMessage());
				} catch (BadPaddingException e) {
					DbgUtil.showLog(TAG,
							"BadPaddingException: " + e.getMessage());
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			// If debug is true, just output input parameter
			output = input;
		}

		return output;
	}

	public LcomUserData encryptForInputLcomUserData(LcomUserData input) {
		LcomUserData output = null;

		if (LcomConst.IS_ENCRYPT) {
			if (input != null && mCipher != null) {
				long userId = input.getUserId();
				String userName = encryptForInputString(input.getUserName());
				String password = encryptForInputString(input.getPassword());
				String mailAddress = encryptForInputString(input
						.getMailAddress());
				Blob thumbnail = encryptForInputBlob(input.getThumbnail());
				output = new LcomUserData(userId, userName, password,
						mailAddress, thumbnail);
			}
		} else {
			output = input;
		}

		return output;

	}

	public static String encrypt(String text, String secretKey) {
		String strResult = null;

		if (text != null && secretKey != null) {
			try {

				// Decode base64 to byte array
				byte[] byteText = text.getBytes("UTF-8");

				// Trancode decode key and initialize vector to byte array
				byte[] byteKey = secretKey.getBytes("UTF-8");
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
				DbgUtil.showLog(TAG,
						"UnsupportedEncodingException: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				DbgUtil.showLog(TAG,
						"NoSuchAlgorithmException: " + e.getMessage());
			} catch (NoSuchPaddingException e) {
				DbgUtil.showLog(TAG,
						"NoSuchPaddingException: " + e.getMessage());
			} catch (InvalidKeyException e) {
				DbgUtil.showLog(TAG, "InvalidKeyException: " + e.getMessage());
			} catch (IllegalBlockSizeException e) {
				DbgUtil.showLog(TAG,
						"IllegalBlockSizeException: " + e.getMessage());
			} catch (BadPaddingException e) {
				DbgUtil.showLog(TAG, "BadPaddingException: " + e.getMessage());
			} catch (InvalidAlgorithmParameterException e) {
				DbgUtil.showLog(TAG,
						"InvalidAlgorithmParameterException: " + e.getMessage());
			}
		}

		return strResult;
	}

	public static List<String> encryptArrayList(List<String> input,
			String secretKey) {

		List<String> result = new ArrayList<String>();

		if (input != null && input.size() != 0) {
			for (String str : input) {
				result.add(encrypt(str, secretKey));
			}
		}

		return result;

	}

	public static String decrypt(String text, String secretKey) {
		String strResult = null;

		if (text != null && secretKey != null) {
			try {
				// Decode base64 to byte array
				byte[] byteText = Base64.decodeBase64(text);

				// Trancode decode key and initialize vector to byte array
				byte[] byteKey = secretKey.getBytes("UTF-8");
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
				DbgUtil.showLog(TAG,
						"UnsupportedEncodingException: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				DbgUtil.showLog(TAG,
						"NoSuchAlgorithmException: " + e.getMessage());
			} catch (NoSuchPaddingException e) {
				DbgUtil.showLog(TAG,
						"NoSuchPaddingException: " + e.getMessage());
			} catch (InvalidKeyException e) {
				DbgUtil.showLog(TAG, "InvalidKeyException: " + e.getMessage());
			} catch (IllegalBlockSizeException e) {
				DbgUtil.showLog(TAG,
						"IllegalBlockSizeException: " + e.getMessage());
			} catch (BadPaddingException e) {
				DbgUtil.showLog(TAG, "BadPaddingException: " + e.getMessage());
			} catch (InvalidAlgorithmParameterException e) {
				DbgUtil.showLog(TAG,
						"InvalidAlgorithmParameterException: " + e.getMessage());
			}
		}

		return strResult;
	}

	public static String createSecretKeyFromIdentifier(String identifier) {
		if (identifier != null) {
			String result = UUID.nameUUIDFromBytes(identifier.getBytes())
					.toString();
			result = result.substring(0, AES_KEY_LENGTH);
			return result;
		}
		return null;
	}
}
