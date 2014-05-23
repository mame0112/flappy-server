package com.mame.lcom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomExpiredMessageData;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomMemcacheException;
import com.mame.lcom.util.TimeUtil;

public class FirstTest {

	private final static Logger log = Logger.getLogger(FirstTest.class
			.getName());

	private LcomDatabaseManager mManager = null;

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
		mManager = LcomDatabaseManager.getInstance();
	}

	@After
	public void tearDown() {
		helper.tearDown();
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	}

	@Test
	public void testGetUserIdByNameAndPassword1() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByNameAndPassword("aaaa", "bbbb");
		assertEquals(userId, 0);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByNameAndPassword2() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByNameAndPassword(null, "bbbb");
		assertEquals(userId, -1);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByNameAndPassword3() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByNameAndPassword("aaaa", null);
		assertEquals(userId, -1);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByNameAndPassword4() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByNameAndPassword("aaaa", "illegal");
		assertEquals(userId, -1);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByName1() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByName("aaaa");
		assertEquals(userId, 0);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByName2() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);
		int userId = mManager.getUserIdByName(null);
		assertEquals(userId, -1);
		mManager.deleteUserData(0);
	}

	@Test
	public void testGetUserIdByName3() {
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);

		int userId = mManager.getUserIdByName("illegal");
		assertEquals(userId, -1);
		mManager.deleteUserData(0);
	}

	@Test
	public void testAddNewUserData1() {
		LcomUserData data2 = new LcomUserData(LcomConst.NO_USER, "cccc",
				"dddd", "b@b", null);
		mManager.debugModifyNumOfUser(1);
		int userId = mManager.addNewUserData(data2);

		assertEquals(userId, 1);

		LcomUserData result = mManager.getUserData(userId);
		assertEquals(result.getUserName(), "cccc");
		assertEquals(result.getPassword(), "dddd");
		assertEquals(result.getMailAddress(), "b@b");
		assertEquals(result.getThumbnail(), null);
		mManager.deleteUserData(0);
		mManager.debugModifyNumOfUser(1);
	}

	@Test
	public void testAddNewUserData2() {
		LcomUserData data2 = new LcomUserData(1, "cccc", "dddd", "b@b", null);
		mManager.debugModifyNumOfUser(2);
		int userId = mManager.addNewUserData(data2);

		assertEquals(userId, 1);

		mManager.deleteUserData(0);
		mManager.debugModifyNumOfUser(1);
	}

	@Test
	public void testAddNewUserData3() {
		int userId = mManager.addNewUserData(null);
		assertEquals(userId, -1);
	}

	@Test
	public void testAddNewUserData4() {
		LcomUserData data2 = new LcomUserData(1, null, "dddd", "b@b", null);
		mManager.debugModifyNumOfUser(2);
		int userId = mManager.addNewUserData(data2);

		LcomUserData result = mManager.getUserData(userId);
		assertEquals(result.getUserName(), LcomConst.NULL);
		assertEquals(result.getPassword(), "dddd");
		assertEquals(result.getMailAddress(), "b@b");
		assertEquals(result.getThumbnail(), null);
	}

	@Test
	public void testAddNewUserData5() {
		LcomUserData data2 = new LcomUserData(1, "cccc", null, "b@b", null);
		mManager.debugModifyNumOfUser(2);
		int userId = mManager.addNewUserData(data2);

		LcomUserData result = mManager.getUserData(userId);
		assertEquals(result.getUserName(), "cccc");
		assertEquals(result.getPassword(), LcomConst.NULL);
		assertEquals(result.getMailAddress(), "b@b");
		assertEquals(result.getThumbnail(), null);
	}

	// TODO need to add check fot thumbnail
	@Test
	public void testAddNewUserData6() {
		LcomUserData data2 = new LcomUserData(1, "cccc", "dddd", null, null);
		mManager.debugModifyNumOfUser(2);
		int userId = mManager.addNewUserData(data2);

		LcomUserData result = mManager.getUserData(userId);
		assertEquals(result.getUserName(), "cccc");
		assertEquals(result.getPassword(), "dddd");
		assertEquals(result.getMailAddress(), LcomConst.NULL);
		assertEquals(result.getThumbnail(), null);
	}

	/**
	 * Without memcache
	 */
	@Test
	public void testUpdateUserData1() {
		// LcomUserData data2 = new LcomUserData();
		int userId = 2;
		LcomDatabaseManagerHelper dbHelper = new LcomDatabaseManagerHelper();

		LcomUserData data2 = new LcomUserData(userId, "cccc2", "dddd2", "b@b2",
				null);
		mManager.debugModifyNumOfUser(userId);
		mManager.addNewUserData(data2);

		LcomUserData result = mManager.getUserData(userId);

		assertEquals(result.getUserName(), "cccc2");

		mManager.updateUserData(userId, "cccc_updated", null, null, null);

		dbHelper.removeUserDataFromMemcache(userId);

		LcomUserData result2 = mManager.getUserData(userId);

		assertEquals(result2.getUserName(), "cccc_updated");
		assertEquals(result2.getPassword(), "dddd2");
		assertEquals(result2.getMailAddress(), "b@b2");
		assertEquals(result2.getThumbnail(), null);

		dbHelper.removeUserDataFromMemcache(userId);
	}

	/**
	 * With memcache
	 */
	@Test
	public void testUpdateUserData2() {
		// LcomUserData data2 = new LcomUserData();
		int userId = 2;
		LcomDatabaseManagerHelper dbHelper = new LcomDatabaseManagerHelper();

		LcomUserData data2 = new LcomUserData(userId, "cccc2", "dddd2", "b@b2",
				null);
		mManager.debugModifyNumOfUser(userId);
		mManager.addNewUserData(data2);

		LcomUserData result = mManager.getUserData(userId);

		assertEquals(result.getUserName(), "cccc2");

		mManager.updateUserData(userId, "cccc_updated", null, null, null);

		LcomUserData result2 = mManager.getUserData(userId);

		assertEquals(result2.getUserName(), "cccc_updated");
		assertEquals(result2.getPassword(), "dddd2");
		assertEquals(result2.getMailAddress(), "b@b2");
		assertEquals(result2.getThumbnail(), null);
	}

	@Test
	public void testUpdateUserNameInFriendhsiopTable1() {
		mManager.addNewFriendshipInfo(1, "aaaa", 3, null, "aaaa",
				TimeUtil.getCurrentDate(), 1);

		mManager.updateUserNameInFriendhsiopTable(3, "updated_cccc");

		ArrayList<LcomFriendshipData> datas = (ArrayList<LcomFriendshipData>) mManager
				.getFriendshipDataForUser(3);

		assertNotNull(datas);
		assertEquals(1, datas.size());

		LcomFriendshipData data = datas.get(0);
		String updatedName = data.getSecondUserName();

		assertEquals("updated_cccc", updatedName);
		// TODO need to remove memcache
		// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();

	}

	/**
	 * Without memcache
	 */
	@Test
	public void testUpdateUserDate1() {

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		helper.removeUserDataFromMemcache(1);

		LcomUserData oldData = new LcomUserData(1, "cccc", "dddd", "b@b", null);
		mManager.addNewUserData(oldData);

		LcomUserData newData = new LcomUserData(1, "cccc2", "dddd2", "b@b2",
				null);

		mManager.updateUserDate(newData);

		helper.removeUserDataFromMemcache(1);

		LcomUserData data = mManager.getUserData(1);

		assertNotNull(data);

		assertEquals(data.getUserId(), 1);
		assertEquals(data.getUserName(), "cccc2");
		assertEquals(data.getMailAddress(), "b@b2");
		assertEquals(data.getPassword(), "dddd2");

		helper.removeUserDataFromMemcache(1);

	}

	/**
	 * With memcache
	 */
	@Test
	public void testUpdateUserDate2() {

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		helper.removeUserDataFromMemcache(1);

		LcomUserData oldData = new LcomUserData(1, "cccc", "dddd", "b@b", null);
		mManager.addNewUserData(oldData);

		LcomUserData newData = new LcomUserData(1, "cccc2", "dddd2", "b@b2",
				null);

		mManager.updateUserDate(newData);

		LcomUserData data = mManager.getUserData(1);

		assertNotNull(data);

		assertEquals(data.getUserId(), 1);
		assertEquals(data.getUserName(), "cccc2");
		assertEquals(data.getMailAddress(), "b@b2");
		assertEquals(data.getPassword(), "dddd2");

		helper.removeUserDataFromMemcache(1);

	}

	/**
	 * With memcach
	 */
	@Test
	public void testGetNumOfUserId1() {
		mManager.debugModifyNumOfUser(1);
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		helper.putTotalNumberOfUser(2);

		int num = mManager.getNumOfUserId();

		assertEquals(num, 2);
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		assertEquals(dbhelper.getTotalNumberOfUser(), 2);
	}

	/**
	 * Without memcach
	 */
	@Test
	public void testGetNumOfUserId2() {
		mManager.debugModifyNumOfUser(3);
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeTotalNumberOfUser();

		int num = mManager.getNumOfUserId();

		assertEquals(num, 3);
		assertEquals(dbhelper.getTotalNumberOfUser(), 3);
	}

	/**
	 * Without memcache
	 */
	@Test
	public void testGetNewMessages1() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		mManager.addNewMessageInfo(0, 1, "aaaa", "bbbb", "test message 1",
				TimeUtil.getCurrentDate() + 100);
		mManager.addNewMessageInfo(0, 1, "aaaa", "bbbb", "test message 2",
				TimeUtil.getCurrentDate() + 100);
		dbhelper.removeNewMessagesFromMemCache(1);

		// try {
		// dbhelper.putNewMessagesToMemCache(0, newMessages);
		// } catch (LcomMemcacheException e) {
		// assertTrue(false);
		// }

		List<LcomNewMessageData> results = mManager.getNewMessages(1);

		assertNotNull(results);
		assertEquals(results.size(), 2);

		LcomNewMessageData data = results.get(0);

		assertEquals(data.getMessage(), "test message 1");
		try {
			assertNotNull(dbhelper.getNewMessageFromMemcache(1));
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}
	}

	/**
	 * With memcache
	 */
	@Test
	public void testGetNewMessages2() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		ArrayList<LcomNewMessageData> newMessages = new ArrayList<LcomNewMessageData>();

		LcomNewMessageData data1 = new LcomNewMessageData(0, 1, "aaaa", "bbbb",
				"test message 1", TimeUtil.getCurrentDate() - 100,
				TimeUtil.getCurrentDate() + 100, true);
		;
		LcomNewMessageData data2 = new LcomNewMessageData(0, 1, "aaaa", "bbbb",
				"test message 2", TimeUtil.getCurrentDate() - 100,
				TimeUtil.getCurrentDate() + 100, false);

		newMessages.add(data1);
		newMessages.add(data2);

		try {
			dbhelper.putNewMessagesToMemCache(1, newMessages);
		} catch (LcomMemcacheException e1) {
			assertTrue(false);
		}

		List<LcomNewMessageData> results = mManager.getNewMessages(1);

		assertNotNull(results);
		assertEquals(results.size(), 1);

		LcomNewMessageData data = results.get(0);

		assertEquals(data.getMessage(), "test message 2");
		try {
			assertNotNull(dbhelper.getNewMessageFromMemcache(1));
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}
	}

	/**
	 * With memcache
	 */
	@Test
	public void testGetNewMessagesWithTargetUser1() {
		log.log(Level.INFO, "testGetNewMessagesWithTargetUser1");
		mManager.debugDeleteNewMessageInfo(0, 1);

		// Userid is 0, targetUserId is 1.
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeNewMessagesFromMemCache(0);

		ArrayList<LcomNewMessageData> newMessages = new ArrayList<LcomNewMessageData>();

		LcomNewMessageData data1 = new LcomNewMessageData(0, 1, "aaaa", "bbbb",
				"test message 1", TimeUtil.getCurrentDate() - 100,
				TimeUtil.getCurrentDate() + 100, true);
		;
		LcomNewMessageData data2 = new LcomNewMessageData(0, 1, "aaaa", "bbbb",
				"test message 2", TimeUtil.getCurrentDate() - 100,
				TimeUtil.getCurrentDate() + 100, false);

		newMessages.add(data1);
		newMessages.add(data2);

		try {
			dbhelper.putNewMessagesToMemCache(1, newMessages);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// List<LcomNewMessageData> result = mManager
		// .getNewMessagesWithTargetUser(0, 1);
		// assertNotNull(result);
		// assertEquals(2, result.size());

	}

	@Test
	public void testGetUserIdByMailAddress() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb",
				"test@mail.com", null);
		mManager.addNewUserData(data);

		int userId = mManager.getUserIdByMailAddress("test@mail.com");

		assertEquals(userId, 0);
		LcomUserData cacheData = dbhelper.getUserDataFromMemcache(0);
		assertNotNull(cacheData);
		assertEquals(0, cacheData.getUserId());
		assertEquals("aaaa", cacheData.getUserName());
		assertEquals("bbbb", cacheData.getPassword());
		assertEquals("test@mail.com", cacheData.getMailAddress());

	}

	/**
	 * with memcache
	 */
	@Test
	public void testGetUserData1() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb",
				"test@mail.com", null);

		LcomUserData data2 = new LcomUserData(0, "aaaa2", "bbbb2",
				"test@mail.com2", null);
		mManager.addNewUserData(data2);

		// Override cache to check cache function
		dbhelper.putUserDataToMemCache(data);

		LcomUserData result = mManager.getUserData(0);

		assertNotNull(result);
		assertEquals(result.getUserName(), "aaaa");
	}

	/**
	 * without memcache
	 */
	@Test
	public void testGetUserData() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		LcomUserData data2 = new LcomUserData(0, "aaaa2", "bbbb2",
				"test@mail.com2", null);
		mManager.addNewUserData(data2);

		// Override cache to check cache function
		dbhelper.removeUserDataFromMemcache(0);

		LcomUserData result = mManager.getUserData(0);

		assertNotNull(result);
		assertEquals(result.getUserName(), "aaaa2");
	}

	@Test
	public void testAddNewFriendshipInfo() {
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 3);
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		try {
			LcomFriendshipData result = dbhelper
					.getFriendListDataFromMemCacheWithFriendId(0, 1);
			assertNotNull(result);
			assertEquals(result.getFirstUserId(), 0);
			assertEquals(result.getSecondUserId(), 1);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(0);
		assertNotNull(datas);
		assertEquals(1, datas.size());

	}

	/**
	 * Hit by first user. Then hit by second user.
	 */
	@Test
	public void testUpdateLatestMessageInfoOnFriendshipTable1() {

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 1);

		// TODO Do we need to put new data to memcache?
		mManager.updateLatestMessageInfoOnFriendshipTable(0, 1,
				"Last message here", TimeUtil.getCurrentDate());

		try {
			LcomFriendshipData cachedData = dbhelper
					.getFriendListDataFromMemCacheWithFriendId(0, 1);
			assertNotNull(cachedData);
			assertEquals(cachedData.getFirstUserId(), 0);
			assertEquals(cachedData.getSecondUserId(), 1);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// First check for memcache
		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(0);
		assertNotNull(datas);
		assertEquals(datas.size(), 1);

		// Then, check for datastore
		dbhelper.removeFriendshipDataFromMemcache(0);

		List<LcomFriendshipData> dataStoreDatas = mManager
				.getFriendshipDataForUser(0);
		assertNotNull(dataStoreDatas);
		assertEquals(dataStoreDatas.size(), 1);

	}

	/**
	 * Hit by second user. Then hit by first user.
	 */
	@Test
	public void testUpdateLatestMessageInfoOnFriendshipTable2() {

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		mManager.addNewFriendshipInfo(1, "bbbb", 0, "aaaa", "last message",
				TimeUtil.getCurrentDate(), 1);

		// TODO Do we need to put new data to memcache?
		mManager.updateLatestMessageInfoOnFriendshipTable(1, 0,
				"Last message here", TimeUtil.getCurrentDate());

		try {
			LcomFriendshipData cachedData = dbhelper
					.getFriendListDataFromMemCacheWithFriendId(1, 0);
			assertNotNull(cachedData);
			assertEquals(cachedData.getFirstUserId(), 1);
			assertEquals(cachedData.getSecondUserId(), 0);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// First check for memcache
		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(1);
		assertNotNull(datas);
		assertEquals(datas.size(), 1);

		// Then, check for datastore
		dbhelper.removeFriendshipDataFromMemcache(1);

		List<LcomFriendshipData> dataStoreDatas = mManager
				.getFriendshipDataForUser(1);
		assertNotNull(dataStoreDatas);
		assertEquals(dataStoreDatas.size(), 1);

	}

	/**
	 * Hit by first user. but not hit second user.
	 */
	@Test
	public void testUpdateLatestMessageInfoOnFriendshipTable3() {

		mManager.debugDeleteFriendshipData();

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 1);

		mManager.updateLatestMessageInfoOnFriendshipTable(0, 2,
				"Last updated message", TimeUtil.getCurrentDate());

		try {
			LcomFriendshipData cachedData = dbhelper
					.getFriendListDataFromMemCacheWithFriendId(0, 2);
			assertNull(cachedData);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(2);
		assertEquals(datas.size(), 0);
	}

	@Test
	public void testIsUsersAreFriend1() {
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 1);
		boolean result = mManager.isUsersAreFriend(0, 1);
		assertTrue(result);
	}

	@Test
	public void testIsUsersAreFriend2() {
		mManager.addNewFriendshipInfo(0, "aaaa", 2, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 1);
		boolean result = mManager.isUsersAreFriend(0, 1);
		assertTrue(result == false);
	}

	@Test
	public void testIsUsersAreFriend3() {
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				TimeUtil.getCurrentDate(), 1);
		boolean result = mManager.isUsersAreFriend(0, 1);
		assertTrue(result);

		boolean result2 = mManager.isUsersAreFriend(1, 0);
		assertTrue(result2);
	}

	/**
	 * With memcache, single default data
	 */
	@Test
	public void testGetFriendListData1() {
		int userId = 0;
		long currentTime = TimeUtil.getCurrentDate() - 1000;
		long expireTime = currentTime - 1000;

		// int firstUserId, String firstUserName,
		// int secondUserId, String secondUserName, String lastMessage,
		// long expireTime, int numOfNewMessage
		LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
				"last message", expireTime, 0);

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		try {
			dbhelper.putFriendListDataToMemCache(data);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomFriendshipData> datas = mManager.getFriendListData(userId,
				currentTime);
		assertNotNull(datas);
		assertEquals(datas.size(), 1);

		LcomFriendshipData result = datas.get(0);

		assertEquals(result.getNumOfNewMessage(), 0);
	}

	/**
	 * With memcache, multiple default data
	 */
	@Test
	public void testGetFriendListData2() {
		int userId = 0;
		long currentTime = TimeUtil.getCurrentDate() - 1000;
		long expireTime1 = currentTime - 1000;
		long expireTime2 = currentTime - 2000;
		long expireTime3 = currentTime - 3000;
		long expireTime4 = currentTime - 4000;

		LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
				"last message1", expireTime1, 0);
		LcomFriendshipData data2 = new LcomFriendshipData(0, "aaaa2", 2,
				"bbbb2", "last message2", expireTime2, 1);
		LcomFriendshipData data3 = new LcomFriendshipData(1, "aaaa3", 3,
				"bbbb3", "last message3", expireTime3, 2);

		// TODO Do we need to take care about this case?
		// LcomFriendshipData data4 = new LcomFriendshipData(4, "aaaa4", 0,
		// "bbbb4", "last message4", expireTime4, 3);

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		try {
			dbhelper.putFriendListDataToMemCache(data);
			dbhelper.putFriendListDataToMemCache(data2);
			dbhelper.putFriendListDataToMemCache(data3);
			// dbhelper.putFriendListDataToMemCache(data4);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomFriendshipData> datas = mManager.getFriendListData(userId,
				currentTime);
		assertNotNull(datas);
		assertEquals(datas.size(), 2);

		LcomFriendshipData result1 = datas.get(0);
		LcomFriendshipData result2 = datas.get(1);

		assertEquals(result1.getNumOfNewMessage(), 0);
		assertEquals(result2.getNumOfNewMessage(), 1);
	}

	/**
	 * Without memcache, single default data
	 */
	@Test
	public void testGetFriendListData3() {
		int userId = 0;
		long currentTime = TimeUtil.getCurrentDate() - 1000;
		long expireTime = currentTime - 1000;

		// int firstUserId, String firstUserName,
		// int secondUserId, String secondUserName, String lastMessage,
		// long expireTime, int numOfNewMessage
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				expireTime, 4);

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeFriendshipDataFromMemcache(userId);

		List<LcomFriendshipData> datas = mManager.getFriendListData(userId,
				currentTime);
		assertNotNull(datas);
		assertEquals(datas.size(), 1);

		LcomFriendshipData result = datas.get(0);
		assertEquals(result.getNumOfNewMessage(), 4);

		dbhelper.removeFriendshipDataFromMemcache(userId);
	}

	/**
	 * With memcache
	 */
	@Test
	public void testGetFriendshipDataForUser1() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		long currentTime = TimeUtil.getCurrentDate() - 1000;
		long expireTime = currentTime - 1000;

		// mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
		// expireTime, 4);
		LcomFriendshipData data = new LcomFriendshipData(0, "aaaa", 1, "bbbb",
				"last message.", expireTime, 4);
		try {
			dbhelper.putFriendListDataToMemCache(data);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(0);

		assertNotNull(datas);
		assertEquals(datas.size(), 1);
		assertEquals(datas.get(0).getNumOfNewMessage(), 4);
		assertEquals(datas.get(0).getLatestMessage(), "last message.");
	}

	/**
	 * Without memcache
	 */
	@Test
	public void testGetFriendshipDataForUser2() {
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		long currentTime = TimeUtil.getCurrentDate() - 1000;
		long expireTime = currentTime - 1000;

		mManager.addNewFriendshipInfo(0, "aaaa", 1, "bbbb", "last message",
				expireTime, 0);
		mManager.addNewFriendshipInfo(0, "aaaa", 1, "cccc", "last message2",
				expireTime, 1);
		dbhelper.removeFriendshipDataFromMemcache(0);

		List<LcomFriendshipData> datas = mManager.getFriendshipDataForUser(0);

		assertNotNull(datas);
		assertEquals(datas.size(), 2);
		assertEquals(datas.get(0).getNumOfNewMessage(), 0);
		assertEquals(datas.get(1).getNumOfNewMessage(), 1);
		assertEquals(datas.get(0).getLatestMessage(), "last message");
		assertEquals(datas.get(1).getLatestMessage(), "last message2");
	}

	@Test
	public void testAddNewMessageInfo() {
		log.log(Level.INFO, "testAddNewMessageInfo");
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeNewMessagesFromMemCache(0);
		mManager.debugDeleteUserData(0);
		mManager.debugDeleteNewMessageInfo(0, 1);

		long currentTime = TimeUtil.getCurrentDate() - 1000;
		mManager.addNewMessageInfo(0, 1, "aaaa", "bbbb", "Message here",
				currentTime);

		List<LcomNewMessageData> datas = mManager.getNewMessages(1);

		assertNotNull(datas);
		assertEquals(datas.size(), 1);
		assertEquals(datas.get(0).getMessage(), "Message here");

		try {
			List<LcomNewMessageData> memDatas = dbhelper
					.getNewMessageFromMemcache(1);
			assertNotNull(memDatas);
			assertEquals(memDatas.size(), 1);
			assertEquals(memDatas.get(0).getMessage(), "Message here");
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

	}

	/**
	 * With no user data
	 */
	@Test
	public void testGetFriendThubmnails1() {

		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeUserDataFromMemcache(0);
		dbhelper.removeUserDataFromMemcache(1);

		List<String> input = new ArrayList<String>();
		input.add("0");
		input.add("1");

		HashMap<Integer, String> result = mManager.getFriendThubmnails(input);

		assertNotNull(result);
		assertEquals(result.size(), 0);

	}

	/**
	 * With no thumbnail data (although user data is stored)
	 */
	@Test
	public void testGetFriendThubmnails2() {

		LcomUserData data1 = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		LcomUserData data2 = new LcomUserData(1, "cccc", "dddd", "b@b", null);
		mManager.addNewUserData(data1);
		mManager.addNewUserData(data2);

		List<String> input = new ArrayList<String>();
		input.add("0");
		input.add("1");

		HashMap<Integer, String> result = mManager.getFriendThubmnails(input);

		assertNotNull(result);
		assertEquals(result.size(), 0);

	}

	/**
	 * With thumbnail data
	 */
	// TODO
	// @Test
	// public void testGetFriendThubmnails3() {
	//
	// try {
	// URL url = new URL("welcome_title_logo.png");
	// InputStream in = url.openStream();
	// ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	// Streams.copy(in, bytes, true /* close stream after copy */);
	// Blob blob = new Blob(bytes.toByteArray());
	//
	// LcomUserData data1 = new LcomUserData(0, "aaaa", "bbbb", "a@a",
	// null);
	// LcomUserData data2 = new LcomUserData(1, "cccc", "dddd", "b@b",
	// null);
	// mManager.addNewUserData(data1);
	// mManager.addNewUserData(data2);
	//
	// List<String> input = new ArrayList<String>();
	// input.add("0");
	// input.add("1");
	//
	// HashMap<Integer, String> result = mManager
	// .getFriendThubmnails(input);
	//
	// assertNotNull(result);
	// assertEquals(result.size(), 0);
	//
	// } catch (Exception e) {
	// assertTrue(false);
	// }
	//
	// }

	/**
	 * With no memcache and no datastore data
	 */
	@Test
	public void testBackupOldMessageData1() {
		long current = TimeUtil.getCurrentDate();
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		mManager.backupOldMessageData(current);

		// Check for memcache
		try {
			List<LcomNewMessageData> datas = dbhelper
					.getNewMessageFromMemcache(0);
			assertNull(datas);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// check for datastore
		dbhelper.removeNewMessagesFromMemCache(0);
		List<LcomNewMessageData> datas2 = mManager.getNewMessages(1);
		assertNull(datas2);

	}

	/**
	 * With memcache and datastore data
	 */
	@Test
	public void testBackupOldMessageData2() {
		log.log(Level.INFO, "testBackupOldMessageData2");
		long current = TimeUtil.getCurrentDate();
		long previous = current - 100000;
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeNewMessagesFromMemCache(0);
		mManager.debugDeleteUserData(0);

		log.log(Level.INFO, "previous: " + previous);
		mManager.addNewMessageInfo(0, 1, "aaaa", "bbbb", "test", current);

		// add memcache if it doesn't exist
		List<LcomNewMessageData> cached = null;
		try {
			cached = dbhelper.getNewMessageFromMemcache(0);
		} catch (LcomMemcacheException e1) {
			assertTrue(false);
		}
		if (cached == null) {
			LcomNewMessageData data = new LcomNewMessageData(0, 1, "aaaa",
					"bbbb", "test", current - 100000, current, false);
			List<LcomNewMessageData> dataList = new ArrayList<LcomNewMessageData>();
			dataList.add(data);
			try {
				dbhelper.putNewMessagesToMemCache(0, dataList);
			} catch (LcomMemcacheException e) {
				assertTrue(false);
			}
		}

		mManager.backupOldMessageData(current + 10000000);

		// Check for memcache
		try {
			List<LcomNewMessageData> datas = dbhelper
					.getNewMessageFromMemcache(0);
			assertNull(datas);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// check for datastore
		dbhelper.removeNewMessagesFromMemCache(1);
		List<LcomNewMessageData> datas2 = mManager.getNewMessages(1);
		assertNull(datas2);

		// Check backup table
		List<LcomExpiredMessageData> backUped = mManager
				.debugGetExpiredMessages();
		assertNotNull(backUped);
		assertEquals(1, backUped.size());
	}

	/**
	 * With memcache and datastore data and not expire case
	 */
	@Test
	public void testBackupOldMessageData3() {
		log.log(Level.INFO, "testBackupOldMessageData3");
		long current = TimeUtil.getCurrentDate();
		long previous = current - 100000;
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();
		dbhelper.removeNewMessagesFromMemCache(0);
		mManager.debugDeleteUserData(0);
		mManager.debugDeleteNewMessageInfo(0, 1);

		log.log(Level.INFO, "previous: " + previous);
		mManager.addNewMessageInfo(0, 1, "aaaa", "bbbb", "test", current);

		// add memcache if it doesn't exist
		List<LcomNewMessageData> cached = null;
		try {
			cached = dbhelper.getNewMessageFromMemcache(0);
		} catch (LcomMemcacheException e1) {
			assertTrue(false);
		}
		if (cached == null) {
			LcomNewMessageData data = new LcomNewMessageData(0, 1, "aaaa",
					"bbbb", "test", current - 100000, current, false);
			List<LcomNewMessageData> dataList = new ArrayList<LcomNewMessageData>();
			dataList.add(data);
			try {
				dbhelper.putNewMessagesToMemCache(0, dataList);
			} catch (LcomMemcacheException e) {
				assertTrue(false);
			}
		}

		mManager.backupOldMessageData(current);

		// Check for memcache
		try {
			List<LcomNewMessageData> datas = dbhelper
					.getNewMessageFromMemcache(0);
			assertNull(datas);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		// check for datastore
		dbhelper.removeNewMessagesFromMemCache(0);
		List<LcomNewMessageData> datas2 = mManager.getNewMessages(1);
		assertNotNull(datas2);

		// Check backup table
		List<LcomExpiredMessageData> backUped = mManager
				.debugGetExpiredMessages();
		assertNotNull(backUped);
		assertEquals(0, backUped.size());
	}

	@Test
	public void testSetDeviceIdForMessagePush() {
		String deviceId = "TestDeviceId";
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		mManager.setDeviceIdForMessagePush(0, deviceId);

		// Check memcache
		String cached = mManager.getDeviceIdForGCMPush(0);
		assertNotNull(cached);
		assertEquals(cached, deviceId);

		// Check datastore
		dbhelper.removeDevceIdFromMemCache(0);
		String stored = mManager.getDeviceIdForGCMPush(0);
		assertNotNull(stored);
		assertEquals(stored, deviceId);
	}

	@Test
	public void testDeleteUserData() {
		mManager.deleteUserData(0);
		LcomDatabaseManagerHelper dbhelper = new LcomDatabaseManagerHelper();

		// With no user data case
		mManager.deleteUserData(0);

		dbhelper.removeUserDataFromMemcache(0);
		LcomUserData result1 = mManager.getUserData(0);
		assertNull(result1);

		// With user data case
		LcomUserData data = new LcomUserData(0, "aaaa", "bbbb", "a@a", null);
		mManager.addNewUserData(data);

		mManager.deleteUserData(0);

		dbhelper.removeUserDataFromMemcache(0);
		LcomUserData result2 = mManager.getUserData(0);
		assertNull(result2);
	}
}
