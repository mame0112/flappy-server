package com.mame.lcom.test;

import static org.junit.Assert.*;

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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gwt.http.client.URL;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.db.LcomDatabaseManagerHelper;
import com.mame.lcom.db.LcomDatabaseManagerUtil;

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
}
