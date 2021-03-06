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

	private final static String TAG = "DatastoreUtil";

	public static Blob transcodeString2Blob(String origin) {
		if (origin != null) {
			try {
				byte[] bytes = origin.getBytes("UTF-8");
				return new Blob(bytes);
			} catch (UnsupportedEncodingException e) {
				DbgUtil.showLog(TAG,
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
					DbgUtil.showLog(TAG,
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
			// DbgUtil.showLog(TAG, "parseNewMessage(data):"
			// + parseNewMessage(data));
			if (data != null) {
				if (isFirstTime) {
					result = parseNewMessage(data);
					isFirstTime = false;
				} else {
					result = result + LcomConst.ITEM_SEPARATOR
							+ parseNewMessage(data);
				}
				DbgUtil.showLog(TAG, "result: " + result);
			}
		}
		// Remove last separator
		// result = result.substring(0,
		// result.length() - LcomConst.ITEM_SEPARATOR.length());
		return result;
	}

	public static String parseFriendListData(int userId,
			List<LcomFriendshipData> friendListData) {
		DbgUtil.showLog(TAG, "parseFriendListData");
		String result = null;
		boolean isFirstTime = true;

		if (friendListData != null && friendListData.size() != 0) {
			// Reverse order so that newest message shall be shown up top
			// for (int i = friendListData.size() - 1; i >= 0; i--) {
			// LcomFriendshipData data = friendListData.get(i);
			for (LcomFriendshipData data : friendListData) {
				// DbgUtil.showLog(TAG, "parseNewMessage(data):"
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
			result = result.substring(0, result.length()
					- LcomConst.ITEM_SEPARATOR.length());
		}

		return result;
	}

	public static String parseNewMessage(LcomNewMessageData data) {
		String parsed = null;
		long userId = data.getUserId();
		// String userName = data.getUserName();
		long targetUserId = data.getTargetUserId();
		String targetUserName = data.getTargetUserName();
		List<String> messages = data.getMessage();
		List<Long> postDate = data.getPostedDate();

		if (targetUserName == null) {
			targetUserName = "unknown";
		}

		String parsedMessage = null;
		if (messages != null && messages.size() != 0) {
			boolean isFirst = true;
			for (String msg : messages) {
				if (isFirst) {
					parsedMessage = msg;
					isFirst = false;
				} else {
					parsedMessage = parsedMessage + LcomConst.MESSAGE_SEPARATOR
							+ msg;
				}
			}
		}

		String parsedDate = null;
		if (postDate != null && postDate.size() != 0) {
			boolean isFirst = true;
			for (Long date : postDate) {
				if (isFirst) {
					parsedDate = String.valueOf(date);
					isFirst = false;
				} else {
					parsedDate = parsedDate + LcomConst.MESSAGE_SEPARATOR
							+ date;
				}
			}
		}

		parsed = userId + LcomConst.SEPARATOR + +targetUserId
				+ LcomConst.SEPARATOR + targetUserName + LcomConst.SEPARATOR
				+ parsedMessage + LcomConst.SEPARATOR + parsedDate;
		return parsed;
	}

	public static String parseNewMessage(long userId, LcomFriendshipData data) {
		DbgUtil.showLog(TAG, "parseNewMessage");
		String parsed = null;
		// int firstUserId = data.getFirstUserId();
		// String firstUserName = data.getFirstUserName();
		long secondUserId = data.getFriendUserId();
		String secondUserName = data.getFriendUserName();
		List<String> messages = data.getLatestMessage();
		List<Long> expireDate = data.getLastMessageExpireTime();
		// int numOfMessage = data.getNumOfNewMessage();

		// if (firstUserName == null) {
		// firstUserName = "unknown";
		// }
		if (secondUserName == null) {
			secondUserName = "unknown";
		}
		// if (messages == null) {
		// messages = "unknown";
		// }

		// if (expireDate == 0L) {
		// expireDate = TimeUtil.getCurrentDate() + 1;
		// }

		String parsedMessage = null;
		if (messages != null && messages.size() != 0) {
			DbgUtil.showLog(TAG, "A1");
			boolean isFirst = true;
			for (int i = messages.size() - 1; i >= 0; i--) {
				DbgUtil.showLog(TAG, "A2");
				String msg = messages.get(i);
				DbgUtil.showLog(TAG, "msg: " + msg);
				// for (String msg : messages) {
				if (isFirst) {
					parsedMessage = msg;
					isFirst = false;
				} else {
					parsedMessage = parsedMessage + LcomConst.MESSAGE_SEPARATOR
							+ msg;
				}
			}
		}
		String parsedDate = null;
		if (expireDate != null && expireDate.size() != 0) {
			DbgUtil.showLog(TAG, "B1");
			boolean isFirst = true;
			// for (Long date : expireDate) {
			for (int j = expireDate.size() - 1; j >= 0; j--) {
				DbgUtil.showLog(TAG, "B2");
				long date = expireDate.get(j);
				DbgUtil.showLog(TAG, "date: " + date);
				if (isFirst) {
					parsedDate = String.valueOf(date);
					isFirst = false;
				} else {
					parsedDate = parsedDate + LcomConst.MESSAGE_SEPARATOR
							+ date;
				}
			}
		}

		DbgUtil.showLog(TAG, "parsedMessage: " + parsedMessage);
		DbgUtil.showLog(TAG, "parsedDate: " + parsedDate);

		parsed = secondUserId + LcomConst.SEPARATOR + secondUserName
				+ LcomConst.SEPARATOR + parsedMessage + LcomConst.SEPARATOR
				+ parsedDate;

		return parsed;
	}
}
