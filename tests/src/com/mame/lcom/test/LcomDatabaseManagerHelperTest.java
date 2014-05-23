package com.mame.lcom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomMemcacheException;
import com.mame.lcom.util.LcomMemcacheUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManagerHelperTest {
	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerHelperTest.class.getName());

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();

	}

	@Test
	public void testPutUserDataToMemCache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "abc@mail.com",
				null);
		dbhelper.putUserDataToMemCache(data);

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomUserData.class.getSimpleName());
		@SuppressWarnings("unchecked")
		String params = (String) memcacheService.get(0);

		String result = 0 + LcomConst.SEPARATOR + "aaaa" + LcomConst.SEPARATOR
				+ "bbbb" + LcomConst.SEPARATOR + "abc@mail.com";

		assertEquals(params, result);

		dbhelper.removeNewMessagesFromMemCache(0);
	}

	@Test
	public void testGetUserDataFromMemcache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		String inputParams = 2 + LcomConst.SEPARATOR + "cccc"
				+ LcomConst.SEPARATOR + "dddd" + LcomConst.SEPARATOR
				+ "mail@address";

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomUserData.class.getSimpleName());
		memcacheService.put(2, inputParams);

		LcomUserData result = dbhelper.getUserDataFromMemcache(2);

		assertEquals(result.getUserId(), 2);
		assertEquals(result.getUserName(), "cccc");
		assertEquals(result.getPassword(), "dddd");
		assertEquals(result.getMailAddress(), "mail@address");

		// In case of wrong argument case
		LcomUserData result2 = dbhelper.getUserDataFromMemcache(5);
		assertNull(result2);

		dbhelper.removeNewMessagesFromMemCache(0);

	}

	@Test
	public void testRemoveUserDataFromMemcache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		String inputParams = 3 + LcomConst.SEPARATOR + "cccc"
				+ LcomConst.SEPARATOR + "dddd" + LcomConst.SEPARATOR
				+ "mail@address";

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomUserData.class.getSimpleName());
		memcacheService.put(3, inputParams);

		dbhelper.removeUserDataFromMemcache(3);

		String result = (String) memcacheService.get(3);
		assertNull(result);

	}

	@Test
	public void testPutTotalNumberOfUser() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		dbhelper.putTotalNumberOfUser(10);

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomAllUserData.class.getSimpleName());
		String num = (String) memcacheService.get(LcomConst.NUM_OF_USER);

		assertEquals(10, Integer.valueOf(num).intValue());

	}

	@Test
	public void testRemoveTotalNumberOfUser() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomAllUserData.class.getSimpleName());
		memcacheService.put(LcomConst.NUM_OF_USER, String.valueOf(20));

		int before = dbhelper.getTotalNumberOfUser();

		// Before
		assertEquals(before, 20);

		dbhelper.removeTotalNumberOfUser();

		// After
		int after = dbhelper.getTotalNumberOfUser();
		assertEquals(after, -1);
	}

	@Test
	public void testGetTotalNumberOfUser() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomAllUserData.class.getSimpleName());
		memcacheService.put(LcomConst.NUM_OF_USER, String.valueOf(40));

		int result = dbhelper.getTotalNumberOfUser();

		assertEquals(result, 40);

		// In case of no cache case
		memcacheService.delete(LcomConst.NUM_OF_USER);

		int result2 = dbhelper.getTotalNumberOfUser();
		assertEquals(result2, LcomConst.NO_USER);

	}

	@Test
	public void testGetNewMessageFromMemcache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomNewMessageData.class.getSimpleName());

		try {
			List<LcomNewMessageData> result1 = dbhelper
					.getNewMessageFromMemcache(1);
			assertNull(result1);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		long current = TimeUtil.getCurrentDate();
		LcomNewMessageData message = new LcomNewMessageData(0, 1, "aaaa",
				"bbbb", "message", current - 100000, current - 100, false);

		LcomMemcacheUtil util = new LcomMemcacheUtil();
		String result = util.parseMessageData2String(message);

		int targetUserId = message.getTargetUserId();
		String currentMessage = (String) memcacheService.get(targetUserId);
		String updatedMessage = util.createNewMssageToMemcache(currentMessage,
				result);

		// Update new message memcache
		memcacheService.delete(targetUserId);
		memcacheService.put(targetUserId, updatedMessage);

		try {
			List<LcomNewMessageData> result2 = dbhelper
					.getNewMessageFromMemcache(1);
			assertNotNull(result2);
			assertEquals(result2.size(), 1);

			LcomNewMessageData data = result2.get(0);

			assertEquals(data.getMessage(), "message");
			assertEquals(data.getTargetUserId(), 1);

		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testPutNewMessageToMemCache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		long current = TimeUtil.getCurrentDate();
		LcomNewMessageData message = new LcomNewMessageData(2, 3, "cccc",
				"dddd", "test message", current - 100000, current - 100, false);

		try {
			dbhelper.putNewMessageToMemCache(message);

			MemcacheService memcacheService = MemcacheServiceFactory
					.getMemcacheService(LcomNewMessageData.class
							.getSimpleName());

			// Wrong data case
			String cachedMessage = (String) memcacheService.get(0);
			assertNull(cachedMessage);

			// Correct data case
			cachedMessage = (String) memcacheService.get(3);
			assertNotNull(cachedMessage);

			LcomMemcacheUtil util = new LcomMemcacheUtil();

			// Parse cached message to Messsage data list
			List<LcomNewMessageData> messages = util
					.parseCachedMessageToList(cachedMessage);

			assertEquals(messages.size(), 1);
			assertEquals(messages.get(0).getTargetUserId(), 3);
			assertEquals(messages.get(0).getTargetUserName(), "dddd");

		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testPutNewMessagesToMemCache() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		long current = TimeUtil.getCurrentDate();
		LcomNewMessageData message2 = new LcomNewMessageData(4, 7, "gggg",
				"hhhh1", "test message2", current - 200000, current - 200, true);

		LcomNewMessageData message3 = new LcomNewMessageData(5, 7, "gggg",
				"hhhh2", "test message3", current - 200000, current - 200, true);

		List<LcomNewMessageData> messages = new ArrayList<LcomNewMessageData>();
		messages.add(message2);
		messages.add(message3);

		try {
			dbhelper.putNewMessagesToMemCache(7, messages);

			MemcacheService memcacheService = MemcacheServiceFactory
					.getMemcacheService(LcomNewMessageData.class
							.getSimpleName());

			// Wrong data case
			String cachedMessage = (String) memcacheService.get(0);
			assertNull(cachedMessage);

			// Correct data case
			cachedMessage = (String) memcacheService.get(7);
			assertNotNull(cachedMessage);

			LcomMemcacheUtil util = new LcomMemcacheUtil();

			// Parse cached message to Messsage data list
			List<LcomNewMessageData> results = util
					.parseCachedMessageToList(cachedMessage);

			assertEquals(results.size(), 2);
			assertEquals(results.get(0).getTargetUserId(), 7);
			assertEquals(results.get(0).getUserId(), 4);
			assertEquals(results.get(1).getTargetUserId(), 7);
			assertEquals(results.get(1).getUserId(), 5);
			assertEquals(results.get(0).getTargetUserName(), "hhhh1");

		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

	}
}
