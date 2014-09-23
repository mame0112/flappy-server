package com.mame.lcom.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.DbgUtil;

public class LcomDatabaseManagerHelper {

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerHelper.class.getName());
	
	private final static String TAG = "LcomDatabaseManagerHelper";

	public static String getDeviceIdForGCMPush(long userId) {
		DbgUtil.showLog(TAG, "getDeviceIdForGCMPush");

		MemcacheService ms = MemcacheServiceFactory
				.getMemcacheService(LcomConst.MEMCACHE_KEY_DEVICEID);

		String deviceId = null;
		try {
			deviceId = (String) ms.get(userId);
		} catch (IllegalArgumentException e1) {
			DbgUtil.showLog(TAG, "IllegalArgumentException: " + e1.getMessage());
		} catch (InvalidValueException e2) {
			DbgUtil.showLog(TAG, "InvalidValueException: " + e2.getMessage());
		}

//		CipherUtil util = new CipherUtil();
//		deviceId = util.decryptForInputString(deviceId);

		return deviceId;
	}

	public static void putDeviceIdForGCMPush(long userId, String deviceId) {
		DbgUtil.showLog(TAG, "putDeviceIdForGCMPush");
		MemcacheService ms = MemcacheServiceFactory
				.getMemcacheService(LcomConst.MEMCACHE_KEY_DEVICEID);

		// CipherUtil util = new CipherUtil();
		// deviceId = util.encryptForInputString(deviceId);

		try {
			ms.put(userId, deviceId);
		} catch (IllegalArgumentException e1) {
			DbgUtil.showLog(TAG, "IllegalArgumentException: " + e1.getMessage());
		} catch (MemcacheServiceException e2) {
			DbgUtil.showLog(TAG, "MemcacheServiceException: " + e2.getMessage());
		}
	}
	// public static void putUserDataToMemcache(Key userKey, Entity data) {
	//
	// if (userKey != null && data != null) {
	// MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
	// ms.put(userKey, data);
	// }
	// }
	//
	// public static Entity getUserDataFromMemcahe(Key key) {
	// if (key != null) {
	// MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
	// Entity data = (Entity) ms.get(key);
	// return data;
	// }
	// return null;
	// }
	// public void putUserDataToMemCache(LcomUserData data) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomUserData.class.getSimpleName());
	// if (data != null) {
	// long userId = data.getUserId();
	// String userName = data.getUserName();
	// String mailAddress = data.getMailAddress();
	// String password = data.getPassword();
	//
	// String inputParams = userId + LcomConst.SEPARATOR + userName
	// + LcomConst.SEPARATOR + password + LcomConst.SEPARATOR
	// + mailAddress;
	//
	// // Because there is no case we update user information at current
	// // specification, we don't cheeck user name and update it. But once
	// // we have such kind of function, we need to check and update user
	// // information
	// memcacheService.put(userId, inputParams);
	// }
	// }
	//
	// public LcomUserData getUserDataFromMemcache(int userId) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomUserData.class.getSimpleName());
	// @SuppressWarnings("unchecked")
	// String params = (String) memcacheService.get(userId);
	// if (params != null) {
	//
	// try {
	// String[] parsed = params.split(LcomConst.SEPARATOR);
	//
	// if (parsed != null) {
	// String userName = parsed[1];
	// String password = parsed[2];
	// String mailAddress = parsed[3];
	// LcomUserData data = new LcomUserData(userId, userName,
	// password, mailAddress, null);
	// return data;
	// }
	// } catch (Exception e) {
	// DbgUtil.showLog(TAG, "Exception: " + e.getMessage());
	// }
	// }
	// return null;
	// }
	//
	// public void removeUserDataFromMemcache(int userId) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomUserData.class.getSimpleName());
	//
	// try {
	// memcacheService.delete(userId);
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// }
	//
	// }
	//
	// public void putTotalNumberOfUser(int numOfUser) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomAllUserData.class.getSimpleName());
	// try {
	// memcacheService.put(LcomConst.NUM_OF_USER,
	// String.valueOf(numOfUser));
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// }
	// }
	//
	// public void removeTotalNumberOfUser() {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomAllUserData.class.getSimpleName());
	//
	// try {
	// memcacheService.delete(LcomConst.NUM_OF_USER);
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// }
	// }
	//
	// public int getTotalNumberOfUser() {
	// int userNum = LcomConst.NO_USER;
	//
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomAllUserData.class.getSimpleName());
	//
	// try {
	// @SuppressWarnings("unchecked")
	// String userNumCached = (String) memcacheService
	// .get(LcomConst.NUM_OF_USER);
	// if (userNumCached != null) {
	// try {
	// userNum = Integer.valueOf(userNumCached);
	// return userNum;
	// } catch (NumberFormatException e) {
	// DbgUtil.showLog(TAG,
	// "NumberFormatException: " + e.getMessage());
	// return LcomConst.NO_USER;
	// }
	// }
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG, "InvalidValueException: " + e.getMessage());
	// }
	//
	// return LcomConst.NO_USER;
	// }
	//
	// /**
	// * This userId must be your own id. this must be targetUserId
	// *
	// * @param userId
	// * @return
	// * @throws LcomMemcacheException
	// */
	// public List<LcomNewMessageData> getNewMessageFromMemcache(int userId)
	// throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "getNewMessageFromMemcache");
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
	// try {
	// if (userId != LcomConst.NO_USER) {
	//
	// @SuppressWarnings("unchecked")
	// String cachedMessage = (String) memcacheService.get(userId);
	// if (cachedMessage != null) {
	// DbgUtil.showLog(TAG, "cachedMessage length: "
	// + cachedMessage.length());
	// LcomMemcacheUtil util = new LcomMemcacheUtil();
	//
	// // Parse cached message to Messsage data list
	// List<LcomNewMessageData> messages = util
	// .parseCachedMessageToList(cachedMessage);
	//
	// // List<LcomNewMessageData> result = new
	// // ArrayList<LcomNewMessageData>();
	// //
	// // // Set read state to already read
	// // for (LcomNewMessageData message : messages) {
	// // if (message != null) {
	// // message.setReadState(true);
	// // result.add(message);
	// // }
	// // }
	//
	// return messages;
	// } else {
	// DbgUtil.showLog(TAG,
	// "LcomMemcacheException illega userId");
	// throw new LcomMemcacheException("Cache doesn't exist");
	// }
	//
	// } else {
	// DbgUtil.showLog(TAG, "LcomMemcacheException illega userId");
	// throw new LcomMemcacheException("illegal userId");
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG, "InvalidValueException: " + e.getMessage());
	// throw new LcomMemcacheException("InvalidValueException: "
	// + e.getMessage());
	// }
	// }
	//
	// /**
	// * get new message information from memcache. Aware that this method throw
	// * LcomMencacheException in case ther is no cache. Return null if no data
	// * for target user.
	// *
	// * @param userId
	// * @param friendUserId
	// * @return
	// * @throws LcomMemcacheException
	// */
	// public List<LcomNewMessageData>
	// getNewMessageFromMemcacheWithChangeReadState(
	// int userId, int friendUserId) throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "getNewMessageFromMemcacheWithChangeReadState: "
	// + userId + " / " + friendUserId);
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
	// try {
	// if (friendUserId != LcomConst.NO_USER) {
	//
	// @SuppressWarnings("unchecked")
	// String cachedMessage = (String) memcacheService.get(userId);
	// if (cachedMessage != null) {
	// DbgUtil.showLog(TAG, "cachedMessage length: "
	// + cachedMessage.length());
	// DbgUtil.showLog(TAG, "cachedMessage: " + cachedMessage);
	// }
	//
	// if (cachedMessage != null) {
	// LcomMemcacheUtil util = new LcomMemcacheUtil();
	//
	// // Parse cached message to Messsage data list
	// List<LcomNewMessageData> messages = util
	// .parseCachedMessageToList(cachedMessage);
	//
	// List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();
	// List<LcomNewMessageData> cacheUpdated = new
	// ArrayList<LcomNewMessageData>();
	//
	// // Set read state to already read
	// for (LcomNewMessageData message : messages) {
	// if (message != null) {
	//
	// DbgUtil.showLog(TAG,
	// "message content: " + message.getMessage());
	//
	// // only for meesage that is sent by targetUserId, we
	// // shall return it.
	// int toUserId = message.getTargetUserId();
	// DbgUtil.showLog(TAG, "toUserId: " + toUserId);
	// if (toUserId == userId) {
	//
	// boolean isRead = message.isMessageRead();
	//
	// if (isRead == false) {
	// message.setReadState(true);
	// DbgUtil.showLog(TAG, "Changed");
	// result.add(message);
	// }
	// }
	//
	// // For update cache
	// cacheUpdated.add(message);
	// }
	// }
	//
	// String updatedString = util
	// .parseMessagesData2String(cacheUpdated);
	// if (updatedString != null) {
	// DbgUtil.showLog(TAG, "updatedString: " + updatedString);
	// }
	//
	// // set read state-updated message to memcache again
	// memcacheService.delete(userId);
	// memcacheService.put(userId, updatedString);
	//
	// return result;
	// } else {
	// // No cache exist
	// DbgUtil.showLog(TAG, "No cache exist");
	// throw new LcomMemcacheException("No cache exist");
	// }
	//
	// } else {
	// DbgUtil.showLog(TAG, "LcomMemcacheException illega userId");
	// throw new LcomMemcacheException("illegal userId");
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG, "InvalidValueException: " + e.getMessage());
	// throw new LcomMemcacheException("InvalidValueException: "
	// + e.getMessage());
	// }
	// }
	//
	// public void putNewMessageToMemCache(LcomNewMessageData message)
	// throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "putNewMessageToMemCache");
	//
	// try {
	// if (message != null) {
	//
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class
	// .getSimpleName());
	//
	// LcomMemcacheUtil util = new LcomMemcacheUtil();
	// String result = util.parseMessageData2String(message);
	//
	// if (result != null) {
	// // Memcache always needs to be stored key by using Target
	// // user Id because only friend (=target user) shall use this
	// // cache
	// int targetUserId = message.getTargetUserId();
	// String currentMessage = (String) memcacheService
	// .get(targetUserId);
	// String updatedMessage = util.createNewMssageToMemcache(
	// currentMessage, result);
	//
	// // Update new message memcache
	// memcacheService.delete(targetUserId);
	// memcacheService.put(targetUserId, updatedMessage);
	// }
	// } else {
	// DbgUtil.showLog(TAG,
	// "LcomMemcacheException: messages is null");
	// throw new LcomMemcacheException("messages is null");
	// }
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// throw new LcomMemcacheException("MemcacheServiceException: "
	// + e.getMessage());
	// }
	//
	// }
	//
	// public void putNewMessagesToMemCache(int userId,
	// List<LcomNewMessageData> messages) throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "putNewMessagesToMemCache");
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
	//
	// try {
	// if (messages != null && messages.size() != 0) {
	//
	// LcomMemcacheUtil util = new LcomMemcacheUtil();
	// String parsed = util.parseMessagesData2String(messages);
	// DbgUtil.showLog(TAG, "parsed: " + parsed);
	// if (parsed != null) {
	//
	// // In come cases, there is current memcache. Then, we try to
	// // update.
	// String currentCache = (String) memcacheService.get(userId);
	// String result = null;
	// if (currentCache != null) {
	// result = util.createNewMssageToMemcache(currentCache,
	// parsed);
	// } else {
	// result = parsed;
	// }
	//
	// // Update memcache
	// memcacheService.delete(userId);
	// memcacheService.put(userId, result);
	// }
	//
	// } else {
	// DbgUtil.showLog(TAG, "LcomMemcacheException message is null");
	// // throw new LcomMemcacheException("messages is null");
	// }
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// throw new LcomMemcacheException("MemcacheServiceException: "
	// + e.getMessage());
	// }
	// }
	//
	// public void removeNewMessagesFromMemCache(int userId) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
	//
	// try {
	// memcacheService.delete(userId);
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// }
	// }
	//
	// public String getPushDevceIdToMemCache(int userId) {
	// DbgUtil.showLog(TAG, "getPushDevceIdToMemCache");
	//
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
	//
	// try {
	// @SuppressWarnings("unchecked")
	// String registrationId = (String) memcacheService.get(userId);
	// if (registrationId != null) {
	// DbgUtil.showLog(TAG, "registrationId is not null");
	// return registrationId;
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG, "InvalidValueException: " + e.getMessage());
	// }
	//
	// return null;
	// }
	//
	// public void putPushDevceIdToMemCache(int userId, String registrationId)
	// throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "putPushDevceIdToMemCache");
	//
	// // Get memcache for push device id
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
	//
	// try {
	// if (userId != LcomConst.NO_USER && registrationId != null) {
	// String currentCache = (String) memcacheService.get(userId);
	// // Check if already cache is stored
	// if (currentCache != null) {
	// // If cache is already there
	// // Once delete memcache
	// DbgUtil.showLog(TAG, "delete memcache");
	// memcacheService.delete(userId);
	// }
	// // And insert memcache
	// DbgUtil.showLog(TAG, "put memcache");
	// memcacheService.put(userId, registrationId);
	//
	// } else {
	// throw new LcomMemcacheException("device id is null");
	// }
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// throw new LcomMemcacheException("MemcacheServiceException: "
	// + e.getMessage());
	// }
	// }
	//
	// public void removeDevceIdFromMemCache(int userId)
	// throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "removeDevceIdFromMemCache");
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
	// try {
	// if (userId != LcomConst.NO_USER) {
	// memcacheService.delete(userId);
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// }
	// }
	//
	// public synchronized void deleteAllNewMessages(
	// ArrayList<Integer> registeredIds) throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "deleteAllNewMessages");
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
	//
	// try {
	// // Remove new message
	// if (registeredIds != null && registeredIds.size() != 0) {
	// DbgUtil.showLog(TAG, "delete all");
	// memcacheService.deleteAll(registeredIds);
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// }
	//
	// }
	//
	// public synchronized void putFriendListDataToMemCache(LcomFriendshipData
	// data)
	// throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "putFriendListDataToMemCache");
	//
	// // Get memcache for push device id
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
	//
	// if (data != null) {
	// int firstId = data.getFirstUserId();
	// int secondId = data.getSecondUserId();
	// String firstName = data.getFirstUserName();
	// String secondName = data.getSecondUserName();
	// String message = data.getLatestMessage();
	// int numOfMessage = data.getNumOfNewMessage();
	// long messageTime = data.getLastMessageExpireTime();
	//
	// if (firstId != LcomConst.NO_USER && secondId != LcomConst.NO_USER) {
	// // String key = firstId + LcomConst.SEPARATOR + secondId;
	//
	// String parsed = firstId + LcomConst.SEPARATOR + secondId
	// + LcomConst.SEPARATOR + firstName + LcomConst.SEPARATOR
	// + secondName + LcomConst.SEPARATOR + message
	// + LcomConst.SEPARATOR + numOfMessage
	// + LcomConst.SEPARATOR + messageTime;
	// try {
	//
	// // TODO can we use firstId??
	//
	// // Once we get current data
	// String currentData = (String) memcacheService.get(firstId);
	//
	// // And combine current data and new data
	// // FIrst, we check if current data is already in memcache
	//
	// boolean isExist = false;
	// String output = "a";
	//
	// if (currentData != null) {
	// String[] itemData = currentData
	// .split(LcomConst.ITEM_SEPARATOR);
	// if (itemData != null && itemData.length != 0) {
	// for (int i = 0; i < itemData.length; i++) {
	// String[] parsedData = itemData[i]
	// .split(LcomConst.SEPARATOR);
	// String currentSecondId = parsedData[1];
	// // If target second user id is memcache
	// if (secondId == Integer
	// .valueOf(currentSecondId)) {
	// isExist = true;
	// // Put new data instead of old data
	// output = output + LcomConst.ITEM_SEPARATOR
	// + parsed;
	// } else {
	// // Keep current data
	// output = output + LcomConst.ITEM_SEPARATOR
	// + itemData[i];
	// }
	// }
	// }
	//
	// } else {
	// DbgUtil.showLog(TAG, "currentData is null");
	// }
	//
	// // If target second user id is new for memcache
	// if (!isExist) {
	// output = output + LcomConst.ITEM_SEPARATOR + parsed;
	// DbgUtil.showLog(TAG, "user doesn't exist");
	// }
	//
	// // Remove unnecessary part
	// output = output.substring(
	// 1 + LcomConst.ITEM_SEPARATOR.length(),
	// output.length());
	//
	// DbgUtil.showLog(TAG, "output: " + output);
	//
	// // Once we delete old memcache
	// memcacheService.delete(firstId);
	//
	// // Put latest message to memcache
	// memcacheService.put(firstId, output);
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException(
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// throw new LcomMemcacheException(
	// "MemcacheServiceException: " + e.getMessage());
	// }
	// } else {
	// throw new LcomMemcacheException("firstId or secondId is null");
	// }
	// } else {
	// throw new LcomMemcacheException("data is null");
	// }
	// }
	//
	// // public synchronized void putFriendListDatasToMemCache(
	// // List<LcomFriendshipData> datas) {
	// // DbgUtil.showLog(TAG, "putFriendListDataToMemCache");
	// //
	// // // Get memcache for push device id
	// // MemcacheService memcacheService = MemcacheServiceFactory
	// // .getMemcacheService(LcomFriendshipData.class.getSimpleName());
	// //
	// // String input = "a";
	// //
	// // if (datas != null && datas.size() != 0) {
	// // for (LcomFriendshipData data : datas) {
	// //
	// // }
	// // }
	// // if (data != null) {
	// // int firstId = data.getFirstUserId();
	// // int secondId = data.getSecondUserId();
	// // String firstName = data.getFirstUserName();
	// // String secondName = data.getSecondUserName();
	// // String message = data.getLatestMessage();
	// // int numOfMessage = data.getNumOfNewMessage();
	// // long messageTime = data.getLastMessageExpireTime();
	// //
	// // if (firstId != LcomConst.NO_USER && secondId != LcomConst.NO_USER) {
	// // // String key = firstId + LcomConst.SEPARATOR + secondId;
	// //
	// // String parsed = firstId + LcomConst.SEPARATOR + secondId
	// // + LcomConst.SEPARATOR + firstName + LcomConst.SEPARATOR
	// // + secondName + LcomConst.SEPARATOR + message
	// // + LcomConst.SEPARATOR + numOfMessage
	// // + LcomConst.SEPARATOR + messageTime;
	// // try {
	// //
	// // // Once we get current data
	// // String currentData = (String) memcacheService.get(firstId);
	// //
	// // // And combine current data and new data
	// // // FIrst, we check if current data is already in memcache
	// //
	// // boolean isExist = false;
	// // String output = "a";
	// //
	// // if (currentData != null) {
	// // String[] itemData = currentData
	// // .split(LcomConst.ITEM_SEPARATOR);
	// // if (itemData != null && itemData.length != 0) {
	// // for (int i = 0; i < itemData.length; i++) {
	// // String[] parsedData = itemData[i]
	// // .split(LcomConst.SEPARATOR);
	// // String currentSecondId = parsedData[1];
	// // // If target second user id is memcache
	// // if (secondId == Integer
	// // .valueOf(currentSecondId)) {
	// // isExist = true;
	// // // Put new data instead of old data
	// // output = output + LcomConst.ITEM_SEPARATOR
	// // + parsed;
	// // } else {
	// // // Keep current data
	// // output = output + LcomConst.ITEM_SEPARATOR
	// // + itemData[i];
	// // }
	// // }
	// // }
	// //
	// // } else {
	// // DbgUtil.showLog(TAG, "currentData is null");
	// // }
	// //
	// // // If target second user id is new for memcache
	// // if (!isExist) {
	// // output = output + LcomConst.ITEM_SEPARATOR + parsed;
	// // DbgUtil.showLog(TAG, "user doesn't exist");
	// // }
	// //
	// // // Remove unnecessary part
	// // output = output.substring(
	// // 1 + LcomConst.ITEM_SEPARATOR.length(),
	// // output.length());
	// //
	// // DbgUtil.showLog(TAG, "output: " + output);
	// //
	// // // Once we delete old memcache
	// // memcacheService.delete(firstId);
	// //
	// // // Put latest message to memcache
	// // memcacheService.put(firstId, output);
	// //
	// // } catch (IllegalArgumentException e) {
	// // DbgUtil.showLog(TAG,
	// // "IllegalArgumentException: " + e.getMessage());
	// // throw new LcomMemcacheException(
	// // "IllegalArgumentException: " + e.getMessage());
	// // } catch (MemcacheServiceException e) {
	// // DbgUtil.showLog(TAG,
	// // "MemcacheServiceException: " + e.getMessage());
	// // throw new LcomMemcacheException(
	// // "MemcacheServiceException: " + e.getMessage());
	// // }
	// // } else {
	// // throw new LcomMemcacheException("firstId or secondId is null");
	// // }
	// // } else {
	// // throw new LcomMemcacheException("data is null");
	// // }
	// // }
	//
	// public void removeFriendshipDataFromMemcache(int userId) {
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
	//
	// try {
	// memcacheService.delete(userId);
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// } catch (MemcacheServiceException e) {
	// DbgUtil.showLog(TAG,
	// "MemcacheServiceException: " + e.getMessage());
	// }
	//
	// }
	//
	// public synchronized LcomFriendshipData
	// getFriendListDataFromMemCacheWithFriendId(
	// int firstId, int secondId) throws LcomMemcacheException {
	//
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
	//
	// if (firstId != LcomConst.NO_USER && secondId != LcomConst.NO_USER) {
	// try {
	// // Get data for first userId
	// String parsedData = (String) memcacheService.get(firstId);
	//
	// if (parsedData != null) {
	//
	// String[] itemArray = parsedData
	// .split(LcomConst.ITEM_SEPARATOR);
	// if (itemArray != null && itemArray.length != 0) {
	// for (int i = 0; i < itemArray.length; i++) {
	// String[] dataArray = itemArray[i]
	// .split(LcomConst.SEPARATOR);
	// String secondUserId = dataArray[1];
	// if (Integer.valueOf(secondUserId) == secondId) {
	// String firstUserId = dataArray[0];
	// String firstName = dataArray[2];
	// String secondName = dataArray[3];
	// String message = dataArray[4];
	// String numOfMessage = dataArray[5];
	// String messageTime = dataArray[6];
	//
	// LcomFriendshipData data = new LcomFriendshipData(
	// Integer.valueOf(firstUserId),
	// firstName,
	// Integer.valueOf(secondUserId),
	// secondName, message,
	// Long.valueOf(messageTime),
	// Integer.valueOf(numOfMessage));
	// return data;
	//
	// }
	// }
	// }
	//
	// }
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG,
	// "InvalidValueException: " + e.getMessage());
	// throw new LcomMemcacheException("InvalidValueException: "
	// + e.getMessage());
	// } catch (IndexOutOfBoundsException e) {
	// DbgUtil.showLog(TAG,
	// "IndexOutOfBoundsException: " + e.getMessage());
	// throw new LcomMemcacheException("IndexOutOfBoundsException: "
	// + e.getMessage());
	// }
	// }
	//
	// return null;
	//
	// }
	//
	// public synchronized List<LcomFriendshipData>
	// getFriendListDataFromMemCache(
	// int firstId) throws LcomMemcacheException {
	// DbgUtil.showLog(TAG, "getFriendListDataFromMemCache");
	//
	// MemcacheService memcacheService = MemcacheServiceFactory
	// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
	//
	// if (firstId != LcomConst.NO_USER) {
	//
	// List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();
	//
	// try {
	// // Get data for first userId
	// String parsedData = (String) memcacheService.get(firstId);
	//
	// if (parsedData != null) {
	// DbgUtil.showLog(TAG, "parsedData: " + parsedData);
	//
	// String[] itemArray = parsedData
	// .split(LcomConst.ITEM_SEPARATOR);
	//
	// if (itemArray != null && itemArray.length != 0) {
	//
	// for (int i = 0; i < itemArray.length; i++) {
	//
	// String[] dataArray = itemArray[i]
	// .split(LcomConst.SEPARATOR);
	// String firstUserId = dataArray[0];
	// String secondUserId = dataArray[1];
	// String firstName = dataArray[2];
	// String secondName = dataArray[3];
	// String message = dataArray[4];
	// String numOfMessage = dataArray[5];
	// String messageTime = dataArray[6];
	//
	// if (message != null) {
	// DbgUtil.showLog(TAG, "message: " + message);
	// }
	//
	// LcomFriendshipData data = new LcomFriendshipData(
	// Integer.valueOf(firstUserId), firstName,
	// Integer.valueOf(secondUserId), secondName,
	// message, Long.valueOf(messageTime),
	// Integer.valueOf(numOfMessage));
	//
	// result.add(data);
	// }
	// }
	// return result;
	// }
	//
	// } catch (IllegalArgumentException e) {
	// DbgUtil.showLog(TAG,
	// "IllegalArgumentException: " + e.getMessage());
	// throw new LcomMemcacheException("IllegalArgumentException: "
	// + e.getMessage());
	// } catch (InvalidValueException e) {
	// DbgUtil.showLog(TAG,
	// "InvalidValueException: " + e.getMessage());
	// throw new LcomMemcacheException("InvalidValueException: "
	// + e.getMessage());
	// } catch (IndexOutOfBoundsException e) {
	// DbgUtil.showLog(TAG,
	// "IndexOutOfBoundsException: " + e.getMessage());
	// throw new LcomMemcacheException("IndexOutOfBoundsException: "
	// + e.getMessage());
	// }
	// }
	//
	// return null;
	//
	// }
}
