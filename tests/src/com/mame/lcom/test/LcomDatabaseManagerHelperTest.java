package com.mame.lcom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomAllUserData;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomMessageDeviceId;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomMemcacheException;
import com.mame.lcom.util.LcomMemcacheUtil;
import com.mame.lcom.util.TimeUtil;

//public class LcomDatabaseManagerHelperTest {
//	private final static Logger log = Logger
//			.getLogger(LcomDatabaseManagerHelperTest.class.getName());
//
//	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
//			new LocalDatastoreServiceTestConfig());
//
//	@Before
//	public void setUp() {
//		helper.setUp();
//	}
//
//	@After
//	public void tearDown() {
//		helper.tearDown();
//
//	}

// @Test
// public void testPutUserDataToMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "abc@mail.com",
// null);
// dbhelper.putUserDataToMemCache(data);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomUserData.class.getSimpleName());
// @SuppressWarnings("unchecked")
// String params = (String) memcacheService.get(0);
//
// String result = 0 + LcomConst.SEPARATOR + "aaaa" + LcomConst.SEPARATOR
// + "bbbb" + LcomConst.SEPARATOR + "abc@mail.com";
//
// assertEquals(params, result);
//
// dbhelper.removeNewMessagesFromMemCache(0);
// }
//
// @Test
// public void testGetUserDataFromMemcache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// String inputParams = 2 + LcomConst.SEPARATOR + "cccc"
// + LcomConst.SEPARATOR + "dddd" + LcomConst.SEPARATOR
// + "mail@address";
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomUserData.class.getSimpleName());
// memcacheService.put(2, inputParams);
//
// LcomUserData result = dbhelper.getUserDataFromMemcache(2);
//
// assertEquals(result.getUserId(), 2);
// assertEquals(result.getUserName(), "cccc");
// assertEquals(result.getPassword(), "dddd");
// assertEquals(result.getMailAddress(), "mail@address");
//
// // In case of wrong argument case
// LcomUserData result2 = dbhelper.getUserDataFromMemcache(5);
// assertNull(result2);
//
// dbhelper.removeNewMessagesFromMemCache(0);
//
// }
//
// @Test
// public void testRemoveUserDataFromMemcache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// String inputParams = 3 + LcomConst.SEPARATOR + "cccc"
// + LcomConst.SEPARATOR + "dddd" + LcomConst.SEPARATOR
// + "mail@address";
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomUserData.class.getSimpleName());
// memcacheService.put(3, inputParams);
//
// dbhelper.removeUserDataFromMemcache(3);
//
// String result = (String) memcacheService.get(3);
// assertNull(result);
//
// }
//
// @Test
// public void testPutTotalNumberOfUser() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// dbhelper.putTotalNumberOfUser(10);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomAllUserData.class.getSimpleName());
// String num = (String) memcacheService.get(LcomConst.NUM_OF_USER);
//
// assertEquals(10, Integer.valueOf(num).intValue());
//
// }
//
// @Test
// public void testRemoveTotalNumberOfUser() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomAllUserData.class.getSimpleName());
// memcacheService.put(LcomConst.NUM_OF_USER, String.valueOf(20));
//
// int before = dbhelper.getTotalNumberOfUser();
//
// // Before
// assertEquals(before, 20);
//
// dbhelper.removeTotalNumberOfUser();
//
// // After
// int after = dbhelper.getTotalNumberOfUser();
// assertEquals(after, -1);
// }
//
// @Test
// public void testGetTotalNumberOfUser() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomAllUserData.class.getSimpleName());
// memcacheService.put(LcomConst.NUM_OF_USER, String.valueOf(40));
//
// int result = dbhelper.getTotalNumberOfUser();
//
// assertEquals(result, 40);
//
// // In case of no cache case
// memcacheService.delete(LcomConst.NUM_OF_USER);
//
// int result2 = dbhelper.getTotalNumberOfUser();
// assertEquals(result2, LcomConst.NO_USER);
//
// }
//
// @Test
// public void testGetNewMessageFromMemcache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
//
// try {
// List<LcomNewMessageData> result1 = dbhelper
// .getNewMessageFromMemcache(1);
// assertNull(result1);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// long current = TimeUtil.getCurrentDate();
// LcomNewMessageData message = new LcomNewMessageData(0, 1, "aaaa",
// "bbbb", "message", current - 100000, current - 100, false);
//
// LcomMemcacheUtil util = new LcomMemcacheUtil();
// String result = util.parseMessageData2String(message);
//
// int targetUserId = message.getTargetUserId();
// String currentMessage = (String) memcacheService.get(targetUserId);
// String updatedMessage = util.createNewMssageToMemcache(currentMessage,
// result);
//
// // Update new message memcache
// memcacheService.delete(targetUserId);
// memcacheService.put(targetUserId, updatedMessage);
//
// try {
// List<LcomNewMessageData> result2 = dbhelper
// .getNewMessageFromMemcache(1);
// assertNotNull(result2);
// assertEquals(result2.size(), 1);
//
// LcomNewMessageData data = result2.get(0);
//
// assertEquals(data.getMessage(), "message");
// assertEquals(data.getTargetUserId(), 1);
//
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
// }
//
// @Test
// public void testPutNewMessageToMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// long current = TimeUtil.getCurrentDate();
// LcomNewMessageData message = new LcomNewMessageData(2, 3, "cccc",
// "dddd", "test message", current - 100000, current - 100, false);
//
// try {
// dbhelper.putNewMessageToMemCache(message);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomNewMessageData.class
// .getSimpleName());
//
// // Wrong data case
// String cachedMessage = (String) memcacheService.get(0);
// assertNull(cachedMessage);
//
// // Correct data case
// cachedMessage = (String) memcacheService.get(3);
// assertNotNull(cachedMessage);
//
// LcomMemcacheUtil util = new LcomMemcacheUtil();
//
// // Parse cached message to Messsage data list
// List<LcomNewMessageData> messages = util
// .parseCachedMessageToList(cachedMessage);
//
// assertEquals(messages.size(), 1);
// assertEquals(messages.get(0).getTargetUserId(), 3);
// assertEquals(messages.get(0).getTargetUserName(), "dddd");
//
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
// }
//
// @Test
// public void testPutNewMessagesToMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// long current = TimeUtil.getCurrentDate();
// LcomNewMessageData message2 = new LcomNewMessageData(4, 7, "gggg",
// "hhhh1", "test message2", current - 200000, current - 200, true);
//
// LcomNewMessageData message3 = new LcomNewMessageData(5, 7, "gggg",
// "hhhh2", "test message3", current - 200000, current - 200, true);
//
// List<LcomNewMessageData> messages = new ArrayList<LcomNewMessageData>();
// messages.add(message2);
// messages.add(message3);
//
// try {
// dbhelper.putNewMessagesToMemCache(7, messages);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomNewMessageData.class
// .getSimpleName());
//
// // Wrong data case
// String cachedMessage = (String) memcacheService.get(0);
// assertNull(cachedMessage);
//
// // Correct data case
// cachedMessage = (String) memcacheService.get(7);
// assertNotNull(cachedMessage);
//
// LcomMemcacheUtil util = new LcomMemcacheUtil();
//
// // Parse cached message to Messsage data list
// List<LcomNewMessageData> results = util
// .parseCachedMessageToList(cachedMessage);
//
// assertEquals(results.size(), 2);
// assertEquals(results.get(0).getTargetUserId(), 7);
// assertEquals(results.get(0).getUserId(), 4);
// assertEquals(results.get(1).getTargetUserId(), 7);
// assertEquals(results.get(1).getUserId(), 5);
// assertEquals(results.get(0).getTargetUserName(), "hhhh1");
//
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// }
//
// @Test
// public void testRemoveNewMessagesFromMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
//
// LcomNewMessageData message2 = new LcomNewMessageData(4, 7, "gggg",
// "hhhh1", "test message2", current - 200000, current - 200, true);
//
// LcomNewMessageData message3 = new LcomNewMessageData(5, 7, "gggg",
// "hhhh2", "test message3", current - 200000, current - 200, true);
//
// List<LcomNewMessageData> messages = new ArrayList<LcomNewMessageData>();
// messages.add(message2);
// messages.add(message3);
//
// LcomMemcacheUtil util = new LcomMemcacheUtil();
// String parsed = util.parseMessagesData2String(messages);
//
// memcacheService.put(0, parsed);
//
// try {
// List<LcomNewMessageData> datas = dbhelper
// .getNewMessageFromMemcache(0);
// assertNotNull(datas);
// assertEquals(datas.size(), 2);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// dbhelper.removeNewMessagesFromMemCache(0);
//
// try {
// List<LcomNewMessageData> datas = dbhelper
// .getNewMessageFromMemcache(0);
// assertNull(datas);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
// }
//
// @Test
// public void testGetPushDevceIdToMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// String registrationId = "gagkrgansdafksdjfasdkfjasfamfjaiwehufan";
// int userId = 0;
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
// memcacheService.put(userId, registrationId);
//
// String deviceId = dbhelper.getPushDevceIdToMemCache(userId);
// assertNotNull(deviceId);
// assertEquals(deviceId, registrationId);
// }
//
// /**
// * With originally device id is empty case
// */
// @Test
// public void testPutPushDevceIdToMemCache1() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// int userId = 0;
// String registrationId = "afwefjasdvidsvodnfauefnlasdnfsdnaop";
//
// try {
// dbhelper.putPushDevceIdToMemCache(userId, registrationId);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
//
// // Remove memcache just in case.
// memcacheService.delete(userId);
//
// try {
// dbhelper.putPushDevceIdToMemCache(userId, registrationId);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// String result = (String) memcacheService.get(userId);
// assertNotNull(result);
// assertEquals(result, registrationId);
//
// }
//
// /**
// * With originally device udpate case
// */
// @Test
// public void testPutPushDevceIdToMemCache2() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// int userId = 0;
// String registrationId = "pwqweuiuerigpovmxvhwqfjoqlslgnsadk";
// String oldId = "agsfpvadioawkeofiasdvnkasdn";
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
//
// // Put original id (for test)
// memcacheService.put(userId, oldId);
//
// try {
// dbhelper.putPushDevceIdToMemCache(userId, registrationId);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// String result = (String) memcacheService.get(userId);
// assertNotNull(result);
// assertEquals(result, registrationId);
// }
//
// /**
// * Illegal cases
// */
// @Test
// public void testPutPushDevceIdToMemCache3() {
//
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
//
// int userId = LcomConst.NO_USER;
// String registrationId = null;
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
//
// // Remove memcache just in case.
// memcacheService.delete(userId);
//
// // Illegal userId
// try {
// dbhelper.putPushDevceIdToMemCache(userId, "aaa");
// assertTrue(false);
// } catch (LcomMemcacheException e) {
// assertTrue(true);
// }
//
// // Illegal device id
// try {
// dbhelper.putPushDevceIdToMemCache(0, registrationId);
// assertTrue(false);
// } catch (LcomMemcacheException e) {
// assertTrue(true);
// }
//
// }
//
// @Test
// public void testRemoveDevceIdFromMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// int userId = 0;
// String registrationId = "pwqweuiuerigpovmxvhwqfjoqlslgnsadk";
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomMessageDeviceId.class.getSimpleName());
//
// // Put original id (for test)
// memcacheService.put(userId, registrationId);
//
// try {
// dbhelper.removeDevceIdFromMemCache(userId);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// String result = (String) memcacheService.get(userId);
// assertNull(result);
// }
//
// @Test
// public void testDeleteAllNewMessages() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
//
// ArrayList<Integer> ids = new ArrayList<Integer>();
//
// ids.add(0);
// ids.add(1);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomNewMessageData.class.getSimpleName());
//
// // Prepare test data
// LcomNewMessageData message1 = new LcomNewMessageData(0, 1, "aaaa",
// "bbbb", "message", current - 100000, current - 100, false);
// LcomNewMessageData message2 = new LcomNewMessageData(2, 1, "aaaa",
// "bbbb", "message", current - 10000, current - 10, false);
//
// LcomMemcacheUtil util = new LcomMemcacheUtil();
// String parsed = util.parseMessageData2String(message1);
//
// String parsed2 = util.parseMessageData2String(message2);
// String parsed3 = util.createNewMssageToMemcache(parsed, parsed2);
// memcacheService.put(1, parsed3);
//
// try {
// dbhelper.deleteAllNewMessages(ids);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// String result = (String) memcacheService.get(1);
// assertNull(result);
//
// }
//
// /**
// * Without default data
// */
// @Test
// public void testPutFriendListDataToMemCache1() {
// DbgUtil.showLog(TAG, "testPutFriendListDataToMemCache1");
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
// long expire = current - 10000;
//
// LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message", expire, 4);
//
// try {
// dbhelper.putFriendListDataToMemCache(data);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
// String parsedData = (String) memcacheService.get(0);
// assertNotNull(parsedData);
// String[] itemArray = parsedData.split(LcomConst.ITEM_SEPARATOR);
//
// String[] dataArray = itemArray[0].split(LcomConst.SEPARATOR);
// String firstUserId = dataArray[0];
// String secondUserId = dataArray[1];
// String firstName = dataArray[2];
// String secondName = dataArray[3];
// String message = dataArray[4];
// String numOfMessage = dataArray[5];
// String messageTime = dataArray[6];
//
// assertEquals(firstUserId, String.valueOf(0));
// assertEquals(secondUserId, String.valueOf(1));
// assertEquals(firstName, "aaaa");
// assertEquals(secondName, "bbbb");
// assertEquals(message, "last message");
// assertEquals(numOfMessage, String.valueOf(4));
// assertEquals(messageTime, String.valueOf(expire));
// }
//
// /**
// * With default data
// */
// @Test
// public void testPutFriendListDataToMemCache2() {
// DbgUtil.showLog(TAG, "testPutFriendListDataToMemCache2");
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
// long expire = current - 10000;
//
// LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message", expire, 2);
//
// LcomFriendshipData data2 = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message2", expire + 100, 3);
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
//
// try {
// dbhelper.putFriendListDataToMemCache(data);
// dbhelper.putFriendListDataToMemCache(data2);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// String parsedData = (String) memcacheService.get(0);
// assertNotNull(parsedData);
// String[] itemArray = parsedData.split(LcomConst.ITEM_SEPARATOR);
//
// // Test for original data
// String[] dataArray = itemArray[0].split(LcomConst.SEPARATOR);
// String firstUserId = dataArray[0];
// String secondUserId = dataArray[1];
// String firstName = dataArray[2];
// String secondName = dataArray[3];
// String message = dataArray[4];
// String numMessage = dataArray[5];
// String msgTime = dataArray[6];
//
// assertEquals(firstUserId, String.valueOf(0));
// assertEquals(secondUserId, String.valueOf(1));
// assertEquals(firstName, "aaaa");
// assertEquals(secondName, "bbbb");
// assertEquals(message, "last message2");
// assertEquals(numMessage, String.valueOf(3));
// assertEquals(msgTime, String.valueOf(expire + 100));
// }
//
// @Test
// public void testRemoveFriendshipDataFromMemcache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
// long expire = current - 10000;
//
// LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message", expire, 2);
// int firstId = data.getFirstUserId();
// int secondId = data.getSecondUserId();
// String firstName = data.getFirstUserName();
// String secondName = data.getSecondUserName();
// String message = data.getLatestMessage();
// int numOfMessage = data.getNumOfNewMessage();
// long messageTime = data.getLastMessageExpireTime();
//
// String parsed = firstId + LcomConst.SEPARATOR + secondId
// + LcomConst.SEPARATOR + firstName + LcomConst.SEPARATOR
// + secondName + LcomConst.SEPARATOR + message
// + LcomConst.SEPARATOR + numOfMessage + LcomConst.SEPARATOR
// + messageTime;
//
// MemcacheService memcacheService = MemcacheServiceFactory
// .getMemcacheService(LcomFriendshipData.class.getSimpleName());
// memcacheService.put(0, parsed);
//
// try {
// List<LcomFriendshipData> result1 = dbhelper
// .getFriendListDataFromMemCache(0);
// assertNotNull(result1);
// assertEquals(result1.size(), 1);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// dbhelper.removeFriendshipDataFromMemcache(0);
//
// try {
// List<LcomFriendshipData> result2 = dbhelper
// .getFriendListDataFromMemCache(0);
// assertNull(result2);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// }
//
// @Test
// public void testGetFriendListDataFromMemCacheWithFriendId() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
// long expire = current - 10000;
//
// try {
// LcomFriendshipData result1 = dbhelper
// .getFriendListDataFromMemCacheWithFriendId(0, 1);
// assertNull(result1);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// LcomFriendshipData data1 = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message", expire, 1);
// LcomFriendshipData data2 = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message2", expire, 1);
// LcomFriendshipData data3 = new LcomFriendshipData(0, "aaaa", 2, "bbbb",
// "last message3", expire, 2);
//
// try {
// dbhelper.putFriendListDataToMemCache(data1);
// dbhelper.putFriendListDataToMemCache(data2);
// dbhelper.putFriendListDataToMemCache(data3);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// try {
// LcomFriendshipData result2 = dbhelper
// .getFriendListDataFromMemCacheWithFriendId(0, 1);
// assertNotNull(result2);
// assertEquals(result2.getNumOfNewMessage(), 1);
// assertEquals(result2.getLatestMessage(), "last message2");
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// }
//
// @Test
// public void testGetFriendListDataFromMemCache() {
// LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
// long current = TimeUtil.getCurrentDate();
// long expire = current - 10000;
//
// LcomFriendshipData data1 = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message", expire, 1);
// LcomFriendshipData data2 = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
// "last message2", expire, 1);
// LcomFriendshipData data3 = new LcomFriendshipData(0, "aaaa", 2, "bbbb",
// "last message3", expire, 2);
//
// try {
// dbhelper.putFriendListDataToMemCache(data1);
// dbhelper.putFriendListDataToMemCache(data2);
// dbhelper.putFriendListDataToMemCache(data3);
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
//
// try {
// List<LcomFriendshipData> result = dbhelper
// .getFriendListDataFromMemCache(0);
// assertNotNull(result);
// assertEquals(result.size(), 2);
// LcomFriendshipData result2 = result.get(1);
// assertEquals(result2.getLatestMessage(), "last message3");
// } catch (LcomMemcacheException e) {
// assertTrue(false);
// }
// }
// }
