package com.mame.lcom.util;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Blob;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.db.LcomDatabaseManager;

public class DatastoreUtil {

	private final static Logger log = Logger.getLogger(DatastoreUtil.class
			.getName());

	public static Blob transcodeString2Blob(String origin) {
		if (origin != null) {
			try {
				byte[] bytes = origin.getBytes("UTF-8");
				return new Blob(bytes);
			} catch (UnsupportedEncodingException e) {
				log.log(Level.WARNING,
						"UnsupportedEncodingException: " + e.getMessage());
			}
		}
		return null;
	}

	public static String transcodeBlob2String(Blob origin) {
		// TODO need to add.
		if (origin != null) {
			byte[] bytes = origin.getBytes();
			if (bytes != null) {
				try {
					return new String(bytes, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					log.log(Level.WARNING,
							"UnsupportedEncodingException: " + e.getMessage());
				}
			}
		}
		return null;
	}

	public static String parseNewMessageList(
			List<LcomNewMessageData> newMessages) {
		String result = null;
		boolean isFirstTime = true;
		for (LcomNewMessageData data : newMessages) {
			// log.log(Level.INFO, "parseNewMessage(data):"
			// + parseNewMessage(data));
			if (data != null) {
				if (isFirstTime) {
					result = parseNewMessage(data) + LcomConst.ITEM_SEPARATOR;
					isFirstTime = false;
				} else {
					result = result + parseNewMessage(data)
							+ LcomConst.ITEM_SEPARATOR;
				}
				log.log(Level.WARNING, "result: " + result);
			}
		}
		// Remove last separator
		result = result.substring(0,
				result.length() - LcomConst.ITEM_SEPARATOR.length());
		return result;
	}

	public static String parseFriendListData(int userId,
			List<LcomFriendshipData> friendListData) {
		String result = null;
		boolean isFirstTime = true;
		for (LcomFriendshipData data : friendListData) {
			// log.log(Level.INFO, "parseNewMessage(data):"
			// + parseNewMessage(data));
			if (data != null) {
				if (isFirstTime) {
					result = parseNewMessage(userId, data)
							+ LcomConst.ITEM_SEPARATOR;
					isFirstTime = false;
				} else {
					result = result + parseNewMessage(userId, data)
							+ LcomConst.ITEM_SEPARATOR;
				}
			}
		}
		// Remove last separator
		result = result.substring(0,
				result.length() - LcomConst.ITEM_SEPARATOR.length());
		return result;
	}

	public static String parseNewMessage(LcomNewMessageData data) {
		String parsed = null;
		int userId = data.getUserId();
		String userName = data.getUserName();
		int targetUserId = data.getTargetUserId();
		String targetUserName = data.getTargetUserName();
		String message = data.getMessage();
		long postDate = data.getPostedDate();

		if (userName == null) {
			userName = "unknown";
		}
		if (targetUserName == null) {
			targetUserName = "unknown";
		}
		if (message == null) {
			message = "unknown";
		}

		if (postDate == 0L) {
			postDate = TimeUtil.getCurrentDate();
		}

		parsed = userId + LcomConst.SEPARATOR + userName + LcomConst.SEPARATOR
				+ targetUserId + LcomConst.SEPARATOR + targetUserName
				+ LcomConst.SEPARATOR + message + LcomConst.SEPARATOR
				+ String.valueOf(postDate);
		return parsed;
	}

	public static String parseNewMessage(int userId, LcomFriendshipData data) {
		String parsed = null;
		int firstUserId = data.getFirstUserId();
		String firstUserName = data.getFirstUserName();
		int secondUserId = data.getSecondUserId();
		String secondUserName = data.getSecondUserName();
		String message = data.getLatestMessage();
		long postDate = 0;
		int numOfMessage = data.getNumOfNewMessage();

		if (firstUserName == null) {
			firstUserName = "unknown";
		}
		if (secondUserName == null) {
			secondUserName = "unknown";
		}
		if (message == null) {
			message = "unknown";
		}

		if (postDate == 0L) {
			postDate = TimeUtil.getCurrentDate();
		}

		if (userId == firstUserId) {
			parsed = firstUserId + LcomConst.SEPARATOR + firstUserName
					+ LcomConst.SEPARATOR + secondUserId + LcomConst.SEPARATOR
					+ secondUserName + LcomConst.SEPARATOR + message
					+ LcomConst.SEPARATOR + String.valueOf(postDate)
					+ LcomConst.SEPARATOR + numOfMessage;
		} else {
			parsed = secondUserId + LcomConst.SEPARATOR + secondUserName
					+ LcomConst.SEPARATOR + firstUserId + LcomConst.SEPARATOR
					+ firstUserName + LcomConst.SEPARATOR + message
					+ LcomConst.SEPARATOR + String.valueOf(postDate)
					+ LcomConst.SEPARATOR + numOfMessage;
		}

		return parsed;
	}
}
