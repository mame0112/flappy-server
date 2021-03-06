package com.mame.lcom.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomMessageDeviceId;
import com.mame.lcom.data.LcomNewMessageData;

public class LcomMemcacheUtil {
	private final Logger log = Logger.getLogger(LcomMemcacheUtil.class
			.getName());

	// public String parseMessagesData2String(List<LcomNewMessageData> messages)
	// {
	// String input = "a";
	// if (messages != null && messages.size() != 0) {
	// for (LcomNewMessageData message : messages) {
	// input = input + message.getUserId() + LcomConst.SEPARATOR
	// + message.getUserName() + LcomConst.SEPARATOR
	// + message.getTargetUserId() + LcomConst.SEPARATOR
	// + message.getTargetUserName() + LcomConst.SEPARATOR
	// + message.getMessage() + LcomConst.SEPARATOR
	// + message.getPostedDate() + LcomConst.SEPARATOR
	// + message.getExpireDate() + LcomConst.SEPARATOR
	// + message.isMessageRead() + LcomConst.ITEM_SEPARATOR;
	// }
	//
	// if (input != null) {
	//
	// // Remove first "a" and last item separator
	// String result = input.substring(1, input.length()
	// - LcomConst.ITEM_SEPARATOR.length());
	// return result;
	// }
	// }
	// return null;
	// }
	//
	// public String parseMessageData2String(LcomNewMessageData message) {
	// if (message != null) {
	// String result = message.getUserId() + LcomConst.SEPARATOR
	// + message.getUserName() + LcomConst.SEPARATOR
	// + message.getTargetUserId() + LcomConst.SEPARATOR
	// + message.getTargetUserName() + LcomConst.SEPARATOR
	// + message.getMessage() + LcomConst.SEPARATOR
	// + message.getPostedDate() + LcomConst.SEPARATOR
	// + message.getExpireDate() + LcomConst.SEPARATOR
	// + message.isMessageRead();
	// DbgUtil.showLog(TAG, "result: " + result);
	// return result;
	// }
	//
	// return null;
	// }
	//
	// public List<LcomNewMessageData> parseCachedMessageToList(
	// String cachedMessage) {
	// DbgUtil.showLog(TAG, "parseCachedMessageToList");
	//
	// if (cachedMessage != null) {
	// DbgUtil.showLog(TAG, "cachedMessage: " + cachedMessage);
	// List<LcomNewMessageData> messages = new ArrayList<LcomNewMessageData>();
	//
	// // First, we divide String to each message items.
	// String[] item = cachedMessage.split(LcomConst.ITEM_SEPARATOR);
	// if (item != null && item.length != 0) {
	//
	// DbgUtil.showLog(TAG, "item.length: " + item.length);
	//
	// // Then, we devide each item to each data.
	// for (int i = 0; i < item.length; i++) {
	//
	// try {
	// String[] parsed = item[i].split(LcomConst.SEPARATOR);
	//
	// String userId = parsed[0];
	// String userName = parsed[1];
	// String targetUserId = parsed[2];
	// String targetUserName = parsed[3];
	// String message = parsed[4];
	// String postDate = parsed[5];
	// String expireDate = parsed[6];
	// String isRead = parsed[7];
	//
	// // Create NewMessageData object
	// LcomNewMessageData data = new LcomNewMessageData(
	// Integer.valueOf(userId),
	// Integer.valueOf(targetUserId), userName,
	// targetUserName, message,
	// Long.valueOf(postDate),
	// Long.valueOf(expireDate),
	// Boolean.valueOf(isRead));
	// messages.add(data);
	// } catch (IndexOutOfBoundsException e) {
	// // Nothing to do
	// DbgUtil.showLog(TAG, "IndexOutOfBoundsException: "
	// + e.getMessage());
	// }
	// }
	// }
	//
	// return messages;
	//
	// }
	//
	// return null;
	// }
	//
	// public String createNewMssageToMemcache(String currentMessage,
	// String newMessage) {
	// String result = null;
	// if (newMessage != null) {
	// if (currentMessage != null) {
	// // If new message is update (meaning current message is in
	// // memcache)
	// result = currentMessage + LcomConst.ITEM_SEPARATOR + newMessage;
	// } else {
	// // If new message is first message
	// result = newMessage;
	// }
	// } else {
	// result = currentMessage;
	// }
	//
	// return result;
	// }
	//
	// public String parsePushDeviceId2String(LcomMessageDeviceId deviceId) {
	// if (deviceId != null) {
	// String result = deviceId.getUserId() + LcomConst.SEPARATOR
	// + deviceId.getDeviceId();
	// DbgUtil.showLog(TAG, "result: " + result);
	// return result;
	// }
	// return null;
	// }
}
