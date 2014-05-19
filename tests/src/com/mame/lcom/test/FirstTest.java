package com.mame.lcom.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomMemcacheException;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TimeUtil;

public class FirstTest {

	private final static String TAG = "DatabaseManagerTest";

	private DatastoreService ds;

	private static final String DATA_KIND = "LcomUserData";

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
		// Userid is 0, targetUserId is 1.
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();

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
			helper.putNewMessagesToMemCache(1, newMessages);
		} catch (LcomMemcacheException e) {
			assertTrue(false);
		}

		List<LcomNewMessageData> result = mManager
				.getNewMessagesWithTargetUser(0, 1);
		assertNotNull(result);
		assertEquals(2, result.size());

	}
}
