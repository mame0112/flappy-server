package com.mame.lcom.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
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

	@Test
	public void testUpdateUserData1() {
		// LcomUserData data2 = new LcomUserData();
		int userId = 2;
		LcomUserData data2 = new LcomUserData(userId, "cccc2", "dddd2", "b@b2",
				null);
		mManager.debugModifyNumOfUser(userId + 1);
		mManager.addNewUserData(data2);
		LcomUserData result = mManager.getUserData(userId);

		assertEquals(result.getUserName(), "cccc2");

		mManager.updateUserData(userId, "cccc_updated", null, null, null);

		LcomUserData result2 = mManager.getUserData(userId);

		assertEquals(result2.getUserName(), "cccc_updated");
		assertEquals(result2.getPassword(), "dddd");
		assertEquals(result2.getMailAddress(), "b@b");
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

	@Test
	public void testUpdateUserDate() {
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

	}
}
