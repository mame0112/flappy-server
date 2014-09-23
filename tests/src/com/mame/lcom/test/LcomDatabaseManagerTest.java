package com.mame.lcom.test;

import com.mame.lcom.util.DbgUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomExpiredMessageData;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomDatabaseManagerUtil;
import com.mame.lcom.db.LcomMemcacheException;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManagerTest {

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerTest.class.getName());

	private LcomDatabaseManager mManager = null;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	private DatastoreService ds = null;

	private LcomDatabaseManagerUtil util = null;

	@Before
	public void setUp() {
		helper.setUp();
		ds = DatastoreServiceFactory.getDatastoreService();
		util = new LcomDatabaseManagerUtil();
	}

	@After
	public void tearDown() {
		helper.tearDown();
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	}

	@Test
	public void testIsEntityForKeyUserIdExist1() {
		// Initial case
		boolean result = util.isEntityForKeyUserIdExist(1, ds);
		assertEquals(result, false);
	}

	@Test
	public void testIsEntityForKeyUserIdExist2() {
		int userId = 1;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		Entity entity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, userId, key);
		ds.put(entity);

		// Correct user id case
		boolean result = util.isEntityForKeyUserIdExist(userId, ds);
		assertEquals(result, true);

		// Wrong user id case
		boolean result2 = util.isEntityForKeyUserIdExist(2, ds);
		assertEquals(result2, false);

		ds.delete(key);
	}

	@Test
	public void testGetEntityForKeyUser1() {
		int userId = 1;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		Entity entity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, userId, key);
		ds.put(entity);

		Entity result = util.getEntityForKeyUser(userId, ds);

		assertNotNull(result);

	}

	@Test
	public void testGetFriendshipEntityForUserId1() {
		int userId = 1;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		Entity entity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, userId, key);

		ds.put(entity);

		// Because ENTITY_FRIENDSHIP_EXPIRE_TIME is null, to be returned entity
		// should be null
		Entity result = util.getFriendshipEntityForUserId(userId, ds);
		assertNull(result);
	}

	@Test
	public void testGetFriendshipEntityForUserId2() {
		int userId = 1;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		Entity entity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, userId, key);

		long time = TimeUtil.getCurrentDate();
		entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(time));
		entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(time - 10000));
		entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList("Test message"));

		ds.put(entity);

		// Because ENTITY_FRIENDSHIP_EXPIRE_TIME is null, to be returned entity
		// should be null
		Entity result = util.getFriendshipEntityForUserId(userId, ds);
		assertNotNull(result);

		// ArrayList<String>
		//
		// assertEquals(entity.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME),
		// 1);
	}

	@Test
	public void testAddMessageForFriendUser1() {
		// Put test data onto Datastore

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate();
		long expireTime = TimeUtil.getExpireDate(currentTime);

		// FIrst, no friend Id case
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
		// Arrays.asList(keyUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		boolean result = util.addMessageForFriendUser(e, senderUserId,
				senderName, keyUserId, keyUserName, lastMessage, currentTime,
				ds);

		assertEquals(result, false);

		ds.delete(userKey);

	}

	@Test
	public void testAddMessageForFriendUser2() {
		// Put test data onto Datastore

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate();
		long expireTime = TimeUtil.getExpireDate(currentTime);

		// Second, friend Id itself exists, but no user Id on it.
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, Arrays.asList(3));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		boolean result = util.addMessageForFriendUser(e, senderUserId,
				senderName, keyUserId, keyUserName, lastMessage, currentTime,
				ds);

		assertEquals(result, false);

		ds.delete(userKey);

	}

	@Test
	public void testAddMessageForFriendUser3() {
		// Put test data onto Datastore

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate();
		long expireTime = TimeUtil.getExpireDate(currentTime);

		// third, friend Id is OK but no new message case.
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE, null);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME, null);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		boolean result = util.addMessageForFriendUser(e, senderUserId,
				senderName, keyUserId, keyUserName, lastMessage, currentTime,
				ds);

		assertEquals(result, true);

		ds.delete(userKey);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddMessageForFriendUser4() {
		// Put test data onto Datastore

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate() - 100000;
		long expireTime = TimeUtil.getExpireDate(currentTime);

		// forth, friend Id is OK and message is already there case.
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		String newMessage = "new Message";
		long currentTime2 = TimeUtil.getCurrentDate();

		boolean result = util.addMessageForFriendUser(e, senderUserId,
				senderName, keyUserId, keyUserName, newMessage, currentTime2,
				ds);

		assertEquals(result, true);

		List<Long> friendUserIdArray = (List<Long>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> messageArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> expireTimeArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		List<String> postedTimeArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

		assertNotNull(friendUserIdArray);
		assertEquals(1, friendUserIdArray.size());

		String message = messageArray.get(0);
		assertEquals(message, lastMessage + LcomConst.SEPARATOR + newMessage);

		String posted = postedTimeArray.get(0);
		assertEquals(posted, currentTime + LcomConst.SEPARATOR + currentTime2);

		ds.delete(userKey);
	}

	@Test
	public void testGetAllValidFriendshipData1() {
		DbgUtil.showLog(Level.INFO, "testGetAllValidFriendshipData1");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate() + 100000;
		long expireTime = TimeUtil.getExpireDate(currentTime);

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		// First, we have only one message info in Entity
		List<LcomFriendshipData> result = util.getAllValidFriendshipData(e, ds,
				keyUserId);

		assertNotNull(result);
		assertEquals(result.size(), 1);

		LcomFriendshipData data = result.get(0);

		List<String> messages = data.getLatestMessage();
		List<Long> expires = data.getLastMessageExpireTime();
		long expire = expires.get(0);
		assertEquals(expire, expireTime);

		assertEquals(messages.get(0), lastMessage);

		ds.delete(userKey);

	}

	@Test
	public void testGetAllValidFriendshipData2() {
		DbgUtil.showLog(Level.INFO, "testGetAllValidFriendshipData2");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		String lastMessage2 = "test message2";
		long currentTime = TimeUtil.getCurrentDate() + 100000;
		long currentTime2 = currentTime + 500000;
		long expireTime = TimeUtil.getExpireDate(currentTime);
		long expireTime2 = expireTime + 50000;

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage + LcomConst.SEPARATOR + lastMessage2));
		e.setProperty(
				LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime + LcomConst.SEPARATOR
						+ currentTime2)));
		e.setProperty(
				LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime + LcomConst.SEPARATOR
						+ expireTime2)));
		ds.put(e);

		// Second, we have more than two messages for one user in Entity
		List<LcomFriendshipData> result = util.getAllValidFriendshipData(e, ds,
				keyUserId);

		assertNotNull(result);
		assertEquals(result.size(), 1);

		LcomFriendshipData data = result.get(0);

		List<String> messages = data.getLatestMessage();
		List<Long> expires = data.getLastMessageExpireTime();

		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0), lastMessage);
		assertEquals(messages.get(1), lastMessage2);

		assertEquals(expires.size(), 2);
		assertEquals(expires.get(0), Long.valueOf(expireTime));
		assertEquals(expires.get(1), Long.valueOf(expireTime2));

		ds.delete(userKey);
	}

	@Test
	public void testGetAllValidFriendshipData3() {
		DbgUtil.showLog(Level.INFO, "testGetAllValidFriendshipData3");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		long senderUserId2 = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String keyUserName2 = "cccc";
		String lastMessage = "test message";
		String lastMessage2 = "test message2";
		String lastMessage3 = "test message3";
		String lastMessage4 = "test message4";
		long currentTime = TimeUtil.getCurrentDate() + 100000;
		long currentTime2 = currentTime + 50000;
		long currentTime3 = TimeUtil.getCurrentDate() + 200000;
		long currentTime4 = currentTime + 10000;
		long expireTime = TimeUtil.getExpireDate(currentTime);
		long expireTime2 = expireTime + 50000;
		long expireTime3 = TimeUtil.getExpireDate(currentTime) - 10000;
		long expireTime4 = expireTime + 10000;

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId, senderUserId2));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName, keyUserName2));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE, Arrays
				.asList(lastMessage + LcomConst.SEPARATOR + lastMessage2,
						lastMessage3 + LcomConst.SEPARATOR + lastMessage4));
		e.setProperty(
				LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime) + LcomConst.SEPARATOR
						+ String.valueOf(currentTime2),
						String.valueOf(currentTime3) + LcomConst.SEPARATOR
								+ String.valueOf(currentTime4)));
		e.setProperty(
				LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime) + LcomConst.SEPARATOR
						+ String.valueOf(expireTime2),
						String.valueOf(expireTime3) + LcomConst.SEPARATOR
								+ String.valueOf(expireTime4)));
		ds.put(e);

		// Third, we have more than two messages for two users in Entity
		List<LcomFriendshipData> result = util.getAllValidFriendshipData(e, ds,
				keyUserId);

		assertNotNull(result);
		assertEquals(result.size(), 2);

		LcomFriendshipData data = result.get(0);

		List<String> messages = data.getLatestMessage();
		List<Long> expires = data.getLastMessageExpireTime();

		assertEquals(messages.size(), 2);
		assertEquals(messages.get(0), lastMessage);
		assertEquals(messages.get(1), lastMessage2);

		assertEquals(expires.size(), 2);
		assertEquals(expires.get(0), Long.valueOf(expireTime));
		assertEquals(expires.get(1), Long.valueOf(expireTime2));

		LcomFriendshipData data2 = result.get(1);

		List<String> messages2 = data2.getLatestMessage();
		List<Long> expires2 = data2.getLastMessageExpireTime();

		assertEquals(messages2.size(), 2);
		assertEquals(messages2.get(0), lastMessage3);
		assertEquals(messages2.get(1), lastMessage4);

		assertEquals(expires2.size(), 2);
		assertEquals(expires2.get(0), Long.valueOf(expireTime3));
		assertEquals(expires2.get(1), Long.valueOf(expireTime4));

		ds.delete(userKey);
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public void testAddMessageToFriendshipKind1() {

		DbgUtil.showLog(Level.INFO, "testAddMessageToFriendshipKind1");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate() + 1000000;
		long expireTime = TimeUtil.getExpireDate(currentTime);

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		String lastMessage2 = "test message2";
		long currentTime2 = TimeUtil.getCurrentDate() + 500000;
		long expireTime2 = TimeUtil.getExpireDate(currentTime);

		// First, not expired message in Datastore
		util.addMessageToFriendshipKind(senderUserId, senderName, currentTime2,
				expireTime2, lastMessage2, e, ds);

		Key testKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
		Entity e2;
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, testKey);
		PreparedQuery pQuery = ds.prepare(query);
		e2 = pQuery.asSingleEntity();
		List<Long> friendUserIdArray = (List<Long>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> messageArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> expireTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		List<String> postedTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

		assertEquals(messageArray.size(), 1);

		assertEquals(messageArray.size(), 1);
		String[] msg = messageArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(msg.length, 2);
		assertEquals(msg[0], lastMessage);
		assertEquals(msg[1], lastMessage2);

		assertEquals(expireTimeArray.size(), 1);
		String[] expires = expireTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(expires.length, 2);
		assertEquals(expires[0], String.valueOf(expireTime));
		assertEquals(expires[1], String.valueOf(expireTime2));

		assertEquals(postedTimeArray.size(), 1);
		String[] posteds = postedTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(posteds.length, 2);
		assertEquals(posteds[0], String.valueOf(currentTime));
		assertEquals(posteds[1], String.valueOf(currentTime2));

		ds.delete(userKey);

	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public void testAddMessageToFriendshipKind2() {

		DbgUtil.showLog(Level.INFO, "testAddMessageToFriendshipKind2");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String lastMessage = "test message";
		long currentTime = 100000;
		long expireTime = TimeUtil.getExpireDate(currentTime);
		DbgUtil.showLog(Level.INFO, "expireTime::" + expireTime);

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(currentTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		ds.put(e);

		String lastMessage2 = "test message2";
		long currentTime2 = TimeUtil.getCurrentDate() + 500000;
		long expireTime2 = TimeUtil.getExpireDate(currentTime);

		// Second, already expired message in Datastore
		util.addMessageToFriendshipKind(senderUserId, senderName, currentTime2,
				expireTime2, lastMessage2, e, ds);

		Key testKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
		Entity e2;
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, testKey);
		PreparedQuery pQuery = ds.prepare(query);
		e2 = pQuery.asSingleEntity();
		List<Long> friendUserIdArray = (List<Long>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> messageArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> expireTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		List<String> postedTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

		assertEquals(messageArray.size(), 1);
		String[] msg = messageArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(msg.length, 1);
		assertEquals(msg[0], lastMessage2);

		assertEquals(expireTimeArray.size(), 1);
		String[] expires = expireTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(expires.length, 1);
		assertEquals(expires[0], String.valueOf(expireTime2));

		assertEquals(postedTimeArray.size(), 1);
		String[] posteds = postedTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(posteds.length, 1);
		assertEquals(posteds[0], String.valueOf(currentTime2));

		ds.delete(userKey);

	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Test
	public void testAddMessageToFriendshipKind3() {

		DbgUtil.showLog(Level.INFO, "testAddMessageToFriendshipKind3");

		long keyUserId = 2;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);

		long senderUserId = 1;
		long senderUserId2 = 3;
		String senderName = "aaaa";
		String keyUserName = "bbbb";
		String keyUserName2 = "cccc";
		String lastMessage = "test message" + LcomConst.SEPARATOR
				+ "test message2";
		String lastMessage2 = "test message3";
		long current = TimeUtil.getCurrentDate();
		long c1 = current - 100000000;
		long c2 = current - 100;
		long c3 = current - 200000000;
		long c4 = TimeUtil.getCurrentDate() - 200;
		String currentTime1 = c1 + LcomConst.SEPARATOR + c2;
		// String currentTime2 = c3;
		long exp1 = TimeUtil.getExpireDate(c1);
		long exp2 = TimeUtil.getExpireDate(c2);
		long exp3 = TimeUtil.getExpireDate(c3);
		long exp4 = TimeUtil.getExpireDate(c4);
		String expireTime1 = exp1 + LcomConst.SEPARATOR + exp2;
		// String expireTime2 = exp3 + LcomConst.SEPARATOR + exp4;

		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, keyUserId,
				userKey);
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId, senderUserId2));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(keyUserName, keyUserName2));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage, lastMessage2));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(currentTime1, String.valueOf(c3)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(expireTime1, String.valueOf(exp3)));
		ds.put(e);

		// Second, There are more than two friends and each has already expired
		// message in Datastore
		util.addMessageToFriendshipKind(senderUserId, senderName, c3, exp4,
				lastMessage2, e, ds);

		Key testKey = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
		Entity e2;
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, testKey);
		PreparedQuery pQuery = ds.prepare(query);
		e2 = pQuery.asSingleEntity();
		List<Long> friendUserIdArray = (List<Long>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> messageArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> expireTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		List<String> postedTimeArray = (List<String>) e2
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

		assertEquals(messageArray.size(), 2);
		String[] msg = messageArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(msg.length, 2);
		assertEquals(msg[0], "test message2");
		assertEquals(msg[1], "test message3");

		String[] msg2 = messageArray.get(1).split(LcomConst.SEPARATOR);
		assertEquals(msg2.length, 1);
		assertEquals(msg2[0], lastMessage2);

		assertEquals(postedTimeArray.size(), 2);
		String[] posts = postedTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(posts.length, 2);
		assertEquals(posts[0], String.valueOf(c2));
		assertEquals(posts[1], String.valueOf(c3));

		String[] posts2 = postedTimeArray.get(1).split(LcomConst.SEPARATOR);
		assertEquals(posts2.length, 1);
		assertEquals(posts2[0], String.valueOf(c3));

		assertEquals(expireTimeArray.size(), 2);
		String[] expires = expireTimeArray.get(0).split(LcomConst.SEPARATOR);
		assertEquals(expires.length, 2);
		assertEquals(expires[0], String.valueOf(exp2));
		assertEquals(expires[1], String.valueOf(exp4));

		String[] expires2 = expireTimeArray.get(1).split(LcomConst.SEPARATOR);
		assertEquals(expires2.length, 1);
		assertEquals(expires2[0], String.valueOf(exp3));

		ds.delete(userKey);

	}
}
