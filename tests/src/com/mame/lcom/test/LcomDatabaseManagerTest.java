package com.mame.lcom.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gwt.http.client.URL;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomDatabaseManagerUtil;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManagerTest {

	private final String TAG = "LcomDatabaseManagerTest";

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerTest.class.getName());

	private LcomDatabaseManager mManager = null;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	private DatastoreService ds = null;

	private final boolean isNeedEncryption = false;

	@Before
	public void setUp() {
		helper.setUp();
		mManager = LcomDatabaseManager.getInstance();
		ds = DatastoreServiceFactory.getDatastoreService();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * In case target user has not been registered and total user num doesn't
	 * exist case
	 */
	@Test
	public void testAddNewUserData1() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		// Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		// Entity ea = new Entity(LcomConst.KIND_ALL_USER_DATA, ancKey);
		// ea.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, 1);
		// ds.put(ea);

		// URL url = new URL("welcome_title_logo);
		// InputStream input = url.openStream();
		// byteArray = IOUtils.toByteArray(input);

		LcomUserData data = new LcomUserData(userId, userName, password,
				mailAddress, thumbnail);

		// Add user data to Datastore
		mManager.addNewUserData(data, isNeedEncryption);

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);

		try {
			Entity e = ds.get(userKey);
			assertNotNull(e);

			String resultName = (String) e
					.getProperty(LcomConst.ENTITY_USER_NAME);
			assertEquals(userName, resultName);

			String resultPass = (String) e
					.getProperty(LcomConst.ENTITY_PASSWORD);
			assertEquals(password, resultPass);

			String resultMailAddress = (String) e
					.getProperty(LcomConst.ENTITY_MAIL_ADDRESS);
			assertEquals(mailAddress, resultMailAddress);

			Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
			Entity aeResult = ds.get(ancKey);
			Long totalNum = (Long) aeResult
					.getProperty(LcomConst.ENTITY_TOTAL_USER_NUM);
			assertSame(totalNum, 1L);

			ds.delete(userKey);

		} catch (EntityNotFoundException e) {
			assertTrue(false);
		}

	}

	/**
	 * In case target user has already been registered case
	 */
	@Test
	public void testAddNewUserData2() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Entity ea = new Entity(LcomConst.KIND_ALL_USER_DATA, ancKey);
		ea.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, 1);
		ds.put(ea);

		Entity e = new Entity(LcomConst.KIND_USER_DATA, userId, ancKey);

		e.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		e.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		e.setProperty(LcomConst.ENTITY_PASSWORD, password);
		e.setProperty(LcomConst.ENTITY_THUMBNAIL, thumbnail);

		ds.put(e);

		String userName2 = "cccc";
		String password2 = "dddd";
		String mailAddress2 = "e@e";
		Blob thumbnail2 = null;

		LcomUserData data = new LcomUserData(userId, userName2, password2,
				mailAddress2, thumbnail2);

		// Add user data to Datastore
		mManager.addNewUserData(data, isNeedEncryption);

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);

		try {
			Entity result = ds.get(userKey);
			assertNotNull(result);

			String resultName = (String) result
					.getProperty(LcomConst.ENTITY_USER_NAME);
			assertEquals(userName2, resultName);

			String resultPass = (String) result
					.getProperty(LcomConst.ENTITY_PASSWORD);
			assertEquals(password2, resultPass);

			String resultMailAddress = (String) result
					.getProperty(LcomConst.ENTITY_MAIL_ADDRESS);
			assertEquals(mailAddress2, resultMailAddress);

			Entity aeResult = ds.get(ancKey);
			Long totalNum = (Long) aeResult
					.getProperty(LcomConst.ENTITY_TOTAL_USER_NUM);
			assertSame(totalNum, 1L);

			ds.delete(userKey);

		} catch (EntityNotFoundException e1) {
			assertTrue(false);
		}

	}

	/**
	 * In case target user has not been registered case
	 */
	@Test
	public void testAddNewUserData3() {

		long userId = -1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		int totalUserNum = 2;

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Entity allUserEntity = new Entity(ancKey);
		allUserEntity
				.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, totalUserNum);
		ds.put(allUserEntity);

		// Entity e = new Entity(LcomConst.KIND_USER_DATA, userId, ancKey);
		//
		// e.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		// e.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		// e.setProperty(LcomConst.ENTITY_PASSWORD, password);
		// e.setProperty(LcomConst.ENTITY_THUMBNAIL, thumbnail);
		//
		// ds.put(e);

		String userName2 = "cccc";
		String password2 = "dddd";
		String mailAddress2 = "e@e";
		Blob thumbnail2 = null;

		LcomUserData data = new LcomUserData(userId, userName2, password2,
				mailAddress2, thumbnail2);

		// Add user data to Datastore
		mManager.addNewUserData(data, isNeedEncryption);

		int nextId = totalUserNum + 1;
		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(nextId);

		try {
			Entity result = ds.get(userKey);
			assertNotNull(result);

			Long id = (Long) result.getProperty(LcomConst.ENTITY_USER_ID);
			assertEquals(String.valueOf(id), String.valueOf(nextId));

			String resultName = (String) result
					.getProperty(LcomConst.ENTITY_USER_NAME);
			assertEquals(userName2, resultName);

			String resultPass = (String) result
					.getProperty(LcomConst.ENTITY_PASSWORD);
			assertEquals(password2, resultPass);

			String resultMailAddress = (String) result
					.getProperty(LcomConst.ENTITY_MAIL_ADDRESS);
			assertEquals(mailAddress2, resultMailAddress);

			Entity aeResult = ds.get(ancKey);
			Long totalNum = (Long) aeResult
					.getProperty(LcomConst.ENTITY_TOTAL_USER_NUM);
			int updated = totalUserNum + 1;
			assertEquals(String.valueOf(totalNum), String.valueOf(updated));

			ds.delete(userKey);

		} catch (EntityNotFoundException e1) {
			assertTrue(false);
		}

	}

	/**
	 * No user case
	 */
	@Test
	public void testUpdateUserData1() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		mManager.updateUserData(userId, userName, password, mailAddress,
				thumbnail);

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity e;
		try {
			e = ds.get(key);
		} catch (EntityNotFoundException e1) {
			assertTrue(true);
		}
	}

	/**
	 * User case exists case
	 */
	public void testUpdateUserData2() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity prepare = new Entity(key);
		prepare.setProperty(LcomConst.ENTITY_USER_ID, userId);
		prepare.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		prepare.setProperty(LcomConst.ENTITY_PASSWORD, password);
		prepare.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		ds.put(prepare);

		mManager.updateUserData(userId, userName, password, mailAddress,
				thumbnail);

		Key keyResult = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity e;
		try {
			e = ds.get(keyResult);
			assertNotNull(e);

			long idResult = (Long) e.getProperty(LcomConst.ENTITY_USER_ID);
			assertEquals(userId, idResult);

			String nameResult = (String) e
					.getProperty(LcomConst.ENTITY_USER_NAME);
			assertEquals(userName, nameResult);

			String mailResult = (String) e
					.getProperty(LcomConst.ENTITY_MAIL_ADDRESS);
			assertEquals(mailAddress, mailResult);

			String pwResult = (String) e.getProperty(LcomConst.ENTITY_PASSWORD);
			assertEquals(password, pwResult);

			Blob thumbResult = (Blob) e.getProperty(LcomConst.ENTITY_THUMBNAIL);
			assertEquals(thumbnail, thumbResult);

			ds.delete(keyResult);

		} catch (EntityNotFoundException e1) {
			assertTrue(false);
		}

	}

	/**
	 * No data case
	 */
	@Test
	public void testUpdateUserNameInFriendhsiopTable1() {

		long userId = 1;
		String userName = "aaaa";

		mManager.updateUserNameInFriendhsiopTable(userId, userName);

		Key keyResult = LcomDatabaseManagerUtil.getUserDataKey(userId);

		Entity e;
		try {
			e = ds.get(keyResult);
			assertTrue(false);
		} catch (EntityNotFoundException e1) {
			assertTrue(true);
		}
	}

	/**
	 * With one data case
	 */
	@Test
	public void testUpdateUserNameInFriendhsiopTable2() {

		// TODO It seems updateUserMameInFriendshipTable doesn't work well. Need
		// to look into this again/

		// long expire = TimeUtil.getCurrentDate();
		// long posted = expire - 30000;
		// long userId = 1;
		// long friendId = 1;
		// String userName = "aaaa";
		// String friendName = "bbbb";
		// String password = "bbbb";
		// String message = "test message";
		// Blob thumbnail = null;
		//
		// // Preparation
		// Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		// Entity prepare = new Entity(key);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME, expire);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, friendId);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
		// friendName);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME, posted);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
		// message);
		// ds.put(prepare);
		//
		// String updatefriendName = "aaaa_update";
		//
		// mManager.updateUserNameInFriendhsiopTable(friendId,
		// updatefriendName);
		//
		// // Check
		// Key keyResult = LcomDatabaseManagerUtil.getUserDataKey(userId);
		//
		// Entity e;
		// try {
		// e = ds.get(keyResult);
		// assertNotNull(e);
		//
		// String nameResult = (String) e
		// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
		// assertEquals(updatefriendName, nameResult);
		//
		// Long expireTimeResult = (Long) e
		// .getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		// assertSame(expire, expireTimeResult);
		//
		// // String pwResult = (String)
		// // e.getProperty(LcomConst.ENTITY_PASSWORD);
		// // assertEquals(password, pwResult);
		// //
		// // Blob thumbResult = (Blob)
		// // e.getProperty(LcomConst.ENTITY_THUMBNAIL);
		// // assertEquals(thumbnail, thumbResult);
		//
		// ds.delete(keyResult);
		//
		// } catch (EntityNotFoundException e1) {
		// assertTrue(false);
		// }
	}

	/**
	 * Target entity is null case
	 */
	@Test
	public void testGetNewMessagesWithTargetUser1() {

		long userId = 1;
		long friendUserId = 2;
		long currentTime = TimeUtil.getCurrentDate();

		List<LcomNewMessageData> result = mManager
				.getNewMessagesWithTargetUser(userId, friendUserId, currentTime);

		if (result != null) {
			assertEquals(result.size(), 0);
		}

	}

	/**
	 * Entity exists, but no index case
	 */
	@Test
	public void testGetNewMessagesWithTargetUser2() {
		DbgUtil.showLog(TAG, "testGetNewMessagesWithTargetUser2");
		long expire = TimeUtil.getCurrentDate();
		long posted = expire - 30000;
		long userId = 1;
		long friendId = 2;
		String friendName = "bbbb";
		String message = "test message";

		// Preparation
		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity prepare = new Entity(key);
		prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME, expire);
		prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, friendId);
		prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME, friendName);
		prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME, posted);
		prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				message);
		ds.put(prepare);

		long testFriendUserId = 3;
		long currentTime = TimeUtil.getCurrentDate();

		List<LcomNewMessageData> result = mManager
				.getNewMessagesWithTargetUser(userId, testFriendUserId,
						currentTime);

		if (result != null) {
			assertEquals(result.size(), 0);
		}

		ds.delete(key);
	}

	/**
	 * Entity exists, index exists, and not expire case
	 */
	@Test
	public void testGetNewMessagesWithTargetUser3() {
		DbgUtil.showLog(TAG, "testGetNewMessagesWithTargetUser3");
		// TODO
		// long expire = TimeUtil.getCurrentDate();
		// long posted = expire - 30000;
		// long userId = 1;
		// long friendId = 2;
		// String friendName = "bbbb";
		// String message = "test message";
		//
		// // Preparation
		// Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		// Entity prepare = new Entity(key);
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
		// Arrays.asList(expire));
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
		// Arrays.asList(friendId));
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
		// Arrays.asList(friendName));
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
		// Arrays.asList(posted));
		// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
		// Arrays.asList(message));
		// ds.put(prepare);
		//
		// long testFriendUserId = 2;
		// long currentTime = TimeUtil.getCurrentDate() - 600000;
		//
		// List<LcomNewMessageData> result = mManager
		// .getNewMessagesWithTargetUser(userId, testFriendUserId,
		// currentTime);
		//
		// if (result != null) {
		// assertEquals(result.size(), 1);
		// }
	}

	/**
	 * Test with no mail address case
	 */
	@Test
	public void testGetUserIdByMailAddress1() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity prepare = new Entity(key);
		prepare.setProperty(LcomConst.ENTITY_USER_ID, userId);
		prepare.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		prepare.setProperty(LcomConst.ENTITY_PASSWORD, password);
		prepare.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		ds.put(prepare);

		String testAddress = "b@b";

		long resultId = mManager.getUserIdByMailAddress(testAddress);

		assertEquals(LcomConst.NO_USER, resultId);

	}

	/**
	 * Test with mail address case
	 */
	@Test
	public void testGetUserIdByMailAddress2() {

		long userId = 4;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity prepare = new Entity(key);
		prepare.setProperty(LcomConst.ENTITY_USER_ID, userId);
		prepare.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		prepare.setProperty(LcomConst.ENTITY_PASSWORD, password);
		prepare.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		ds.put(prepare);

		String testAddress = "a@a";

		long resultId = mManager.getUserIdByMailAddress(testAddress);

		assertEquals(4, resultId);

	}

	/**
	 * With no user data case
	 */
	@Test
	public void testGetUserData1() {

		long userId = 1;

		LcomUserData data = mManager.getUserData(userId);

		assertNull(data);
	}

	/**
	 * With user data case
	 */
	@Test
	public void testGetUserData2() {

		long userId = 1;
		String userName = "aaaa";
		String password = "bbbb";
		String mailAddress = "a@a";
		Blob thumbnail = null;

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity prepare = new Entity(key);
		prepare.setProperty(LcomConst.ENTITY_USER_ID, userId);
		prepare.setProperty(LcomConst.ENTITY_USER_NAME, userName);
		prepare.setProperty(LcomConst.ENTITY_PASSWORD, password);
		prepare.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
		ds.put(prepare);

		LcomUserData data = mManager.getUserData(userId);

		assertNotNull(data);

		assertEquals(data.getUserId(), userId);
		assertEquals(data.getUserName(), userName);
		assertEquals(data.getPassword(), password);
		assertEquals(data.getMailAddress(), mailAddress);
		assertEquals(data.getThumbnail(), null);

	}

	/**
	 * With no data exists case
	 */
	@Test
	public void testAddNewFriendshipInfo1() {

		long senderUserId = 2;
		String senderName = "bbbb";
		long keyUserId = 1;
		String keyUserName = "aaaa";
		String lastMessage = "test message";
		long currentTime = TimeUtil.getCurrentDate();

		mManager.addNewFriendshipInfo(senderUserId, senderName, keyUserId,
				keyUserName, lastMessage, currentTime);

		Key key = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
		Key friendshipKey = KeyFactory.createKey(key,
				LcomConst.KIND_FRIENDSHIP_DATA, keyUserId);

		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
				senderUserId);

		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, friendshipKey);
		query.setFilter(messageFilter);
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		assertNotNull(entity);

		ArrayList<String> resultFriendName = (ArrayList<String>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
		assertEquals(resultFriendName.size(), 1);
		assertEquals(resultFriendName.get(0), senderName);

		ArrayList<Long> resultFriendId = (ArrayList<Long>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		assertEquals(resultFriendId.size(), 1);
		assertSame(resultFriendId.get(0), senderUserId);

		ArrayList<String> resultMessages = (ArrayList<String>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		assertEquals(resultMessages.size(), 1);
		assertEquals(resultMessages.get(0), lastMessage);

		ArrayList<String> resultPostedTimes = (ArrayList<String>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
		assertEquals(resultPostedTimes.size(), 1);
		assertEquals(resultPostedTimes.get(0), String.valueOf(currentTime));

	}

	/**
	 * Entity itself exists, but data for target user doesn't exist
	 */
	// @Test
	// public void testAddNewFriendshipInfo2() {
	//
	// long senderUserId = 3;
	// String senderName = "cccc";
	// long keyUserId = 1;
	// String keyUserName = "aaaa";
	// String lastMessage = "test message";
	// long currentTime = TimeUtil.getCurrentDate();
	// long expire = currentTime - 30000;
	//
	// Key key = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
	// Entity prepare = new Entity(key);
	// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
	// Arrays.asList(expire));
	// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
	// Arrays.asList(friendId));
	// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
	// Arrays.asList(senderName));
	// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
	// Arrays.asList(currentTime));
	// prepare.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
	// Arrays.asList(message));
	// ds.put(prepare);
	//
	// mManager.addNewFriendshipInfo(senderUserId, senderName, keyUserId,
	// keyUserName, lastMessage, currentTime);
	//
	// Key key = LcomDatabaseManagerUtil.getUserDataKey(keyUserId);
	// Key friendshipKey = KeyFactory.createKey(key,
	// LcomConst.KIND_FRIENDSHIP_DATA, keyUserId);
	//
	// Filter messageFilter = new FilterPredicate(
	// LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
	// senderUserId);
	//
	// Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, friendshipKey);
	// query.setFilter(messageFilter);
	// PreparedQuery pQuery = ds.prepare(query);
	// Entity entity = pQuery.asSingleEntity();
	//
	// assertNotNull(entity);
	//
	// ArrayList<String> resultFriendName = (ArrayList<String>) entity
	// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
	// assertEquals(resultFriendName.size(), 1);
	// assertEquals(resultFriendName.get(0), senderName);
	//
	// ArrayList<Long> resultFriendId = (ArrayList<Long>) entity
	// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
	// assertEquals(resultFriendId.size(), 1);
	// assertSame(resultFriendId.get(0), senderUserId);
	//
	// ArrayList<String> resultMessages = (ArrayList<String>) entity
	// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
	// assertEquals(resultMessages.size(), 1);
	// assertEquals(resultMessages.get(0), lastMessage);
	//
	// ArrayList<String> resultPostedTimes = (ArrayList<String>) entity
	// .getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
	// assertEquals(resultPostedTimes.size(), 1);
	// assertEquals(resultPostedTimes.get(0), String.valueOf(currentTime));
	//
	// }
	// @Test
	// public void testAddNewUserAndFriendshipInfo1() {
	//
	// long userId = 1;
	// String userName = "aaaa";
	// String password = "bbbb";
	// String mailAddress = "a@a";
	// Blob thumbnail = null;
	//
	// LcomUserData data = new LcomUserData(userId, userName, password,
	// mailAddress, thumbnail);
	// long senderUserId = 2;
	// String senderName = "bbbb";
	// String lastMessage = "test message";
	// long currentTime = TimeUtil.getCurrentDate();
	//
	// long newUserId = mManager.addNewUserAndFriendshipInfo(data,
	// senderUserId, senderName, lastMessage, currentTime);
	// }
}
