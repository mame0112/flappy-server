package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomAllUserData;
import com.mame.lcom.data.LcomExpiredMessageData;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

//import com.mame.lcom.data.LcomFriendshipData;

public class LcomDatabaseManager {

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManager.class.getName());

	private static LcomDatabaseManager sDatabaseManager = new LcomDatabaseManager();

	// private static final PersistenceManager sPM = JDOHelper
	// .getPersistenceManagerFactory("transactions-optional")
	// .getPersistenceManager();

	private LcomDatabaseManager() {
		// Singletone
	}

	public static LcomDatabaseManager getInstance() {
		return sDatabaseManager;
	}

	public static boolean isUserNameAlreadyExist(String userName) {
		log.log(Level.WARNING, "isUserNameAlreadyExist");

		CipherUtil util = new CipherUtil();
		userName = util.encryptForInputString(userName);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Filter nameFilter = new FilterPredicate(LcomConst.ENTITY_USER_NAME,
				FilterOperator.EQUAL, userName);

		Query query = new Query(LcomUserData.class.getSimpleName());
		query.setKeysOnly();
		query.setFilter(nameFilter);
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		// If no username existy
		if (entity == null) {
			log.log(Level.WARNING, "Exist");
			return false;
		} else {
			log.log(Level.WARNING, "Not exist");
			return true;
		}
	}

	public long getUserIdByNameAndPassword(String userName, String password) {

		long userId = LcomConst.NO_USER;

		if (userName != null && password != null) {

			CipherUtil util = new CipherUtil();
			userName = util.encryptForInputString(userName);
			password = util.encryptForInputString(password);

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter nameFilter = new FilterPredicate(LcomConst.ENTITY_USER_NAME,
					FilterOperator.EQUAL, userName);

			Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
			Query query = new Query(LcomConst.KIND_USER_DATA, ancKey);
			query.setKeysOnly();
			query.setFilter(nameFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			userId = getIdFromEntity(entity);
		}
		return userId;
	}

	public long getUserIdByName(String userName) {
		log.log(Level.WARNING, "getUserIdByName");
		long userId = LcomConst.NO_USER;
		if (userName != null) {

			CipherUtil util = new CipherUtil();
			userName = util.encryptForInputString(userName);

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter nameFilter = new FilterPredicate(LcomConst.ENTITY_USER_NAME,
					FilterOperator.EQUAL, userName);

			Query query = new Query(LcomUserData.class.getSimpleName());
			query.setFilter(nameFilter);
			query.setKeysOnly();
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			if (entity != null) {
				userId = entity.getKey().getId();
				log.log(Level.WARNING, "userId: " + userId);
			} else {
				userId = LcomConst.NO_USER;
			}
		}
		return userId;
	}

	/**
	 * Add new userId to UserData table and AllUserTable. This method shall
	 * return user Id. Then, argument (data) user id should be No_user if no
	 * user exist.
	 * 
	 * @param data
	 */
	public synchronized long addNewUserData(LcomUserData data) {
		log.log(Level.INFO, "addNewUserData");
		// int userId = LcomConst.NO_USER;

		CipherUtil util = new CipherUtil();
		data = util.encryptForInputLcomUserData(data);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		long userId = data.getUserId();

		Transaction tx = ds.beginTransaction();

		try {
			// Query query = new Query(LcomAllUserData.class.getSimpleName());
			// query.setKeysOnly();
			// PreparedQuery pQuery = ds.prepare(query);
			// Entity entity = pQuery.asSingleEntity();

			Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
			Entity entity = null;
			try {
				entity = ds.get(ancKey);
			} catch (EntityNotFoundException e) {
				// if no All user data exist, create it as total user num is 0.
				// (Because we will add +1 below part)
				log.log(Level.WARNING,
						"EntityNotFoundException: " + e.getMessage());
				// entity = new Entity(LcomConst.KIND_ALL_USER_DATA,
				// LcomConst.ENTITY_TOTAL_USER_NUM);
				// entity.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, 0L);
				// ds.put(entity);
			}
			// Key key = KeyFactory.createKey(ancKey, LcomConst.KIND_USER_DATA,
			// LcomConst.ENTITY_TOTAL_USER_NUM);

			if (entity != null) {
				log.log(Level.WARNING, "AllUserData entity is not null");

				// If user id is already assigned
				if (userId != LcomConst.NO_USER) {
					Entity childEntity = new Entity(LcomConst.KIND_USER_DATA,
							userId, ancKey);
					childEntity.setProperty(LcomConst.ENTITY_USER_ID,
							data.getUserId());
					childEntity.setProperty(LcomConst.ENTITY_USER_NAME,
							data.getUserName());
					childEntity.setProperty(LcomConst.ENTITY_PASSWORD,
							data.getPassword());
					childEntity.setProperty(LcomConst.ENTITY_MAIL_ADDRESS,
							data.getMailAddress());
					childEntity.setProperty(LcomConst.ENTITY_THUMBNAIL,
							data.getThumbnail());
					ds.put(childEntity);
					// LcomDatabaseManagerHelper.putUserDataToMemcache(
					// childEntity.getKey(), childEntity);
					tx.commit();
				} else {
					long id = (long) entity
							.getProperty(LcomConst.ENTITY_TOTAL_USER_NUM);
					// long id = entity.getKey().getId();
					// Object object = entity
					// .getProperty(LcomConst.ENTITY_TOTAL_USER_NUM);
					int numOfUser = (int) id + 1;
					userId = numOfUser;

					log.log(Level.WARNING, "numOfUser: " + numOfUser);

					entity.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM,
							numOfUser);
					ds.put(entity);

					Entity childEntity = new Entity(LcomConst.KIND_USER_DATA,
							numOfUser, ancKey);
					childEntity
							.setProperty(LcomConst.ENTITY_USER_ID, numOfUser);
					childEntity.setProperty(LcomConst.ENTITY_USER_NAME,
							data.getUserName());
					childEntity.setProperty(LcomConst.ENTITY_PASSWORD,
							data.getPassword());
					childEntity.setProperty(LcomConst.ENTITY_MAIL_ADDRESS,
							data.getMailAddress());
					childEntity.setProperty(LcomConst.ENTITY_THUMBNAIL,
							data.getThumbnail());
					ds.put(childEntity);
					// LcomDatabaseManagerHelper.putUserDataToMemcache(
					// childEntity.getKey(), childEntity);
					tx.commit();
				}

			} else {
				log.log(Level.WARNING, "AllUserData entity is null");
				// If no LcomAllUserDada exist
				userId = 1;
				Entity allUserEntity = new Entity(ancKey);
				allUserEntity.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM,
						userId);
				ds.put(allUserEntity);

				Entity childEntity = new Entity(LcomConst.KIND_USER_DATA,
						userId, ancKey);
				childEntity.setProperty("mUserId", userId);
				childEntity.setProperty("mUserName", data.getUserName());
				childEntity.setProperty("mPassword", data.getPassword());
				childEntity.setProperty("mMailAddress", data.getMailAddress());
				childEntity.setProperty("mThumbnail", data.getThumbnail());
				ds.put(childEntity);
				// LcomDatabaseManagerHelper.putUserDataToMemcache(
				// childEntity.getKey(), childEntity);
				tx.commit();
			}

		} catch (ConcurrentModificationException e) {
			log.log(Level.INFO,
					"ConcurrentModificationException: " + e.getMessage());
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		return userId;
	}

	/**
	 * This will NOT use datastore read ops. Just use small ops.
	 * 
	 * @return
	 */
	private int getAllUserDataNum() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();

		Query query = new Query(LcomUserData.class.getSimpleName());
		query.setKeysOnly();
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		if (entity != null) {
			long id = entity.getKey().getId();
			return (int) id;
		} else {
			// If no AllUserData exist (This shall be occur at the very first
			// time
			log.log(Level.WARNING, "No all user data key");
			Entity totalEntity = new Entity(ancKey);
			totalEntity.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, 1);
			ds.put(totalEntity);
			return 1;
		}
	}

	private long getIdFromEntity(Entity e) {
		if (e != null) {
			return e.getKey().getId();
		}
		return LcomConst.NO_USER;
	}

	public void debugDeleteUserData(int userId) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		String query = "select from " + LcomUserData.class.getName()
				+ " where mUserId == " + userId;
		List<LcomUserData> datas = (List<LcomUserData>) pm.newQuery(query)
				.execute();
		if (datas != null && datas.size() != 0) {
			for (LcomUserData data : datas) {
				try {
					try {
						pm.deletePersistent(data);
					} finally {
						pm.close();
					}

				} catch (IndexOutOfBoundsException e) {
					// Nothing to do
				}
			}
		}
	}

	/**
	 * Update user data. Key is userId.
	 * 
	 * @param userId
	 * @param userName
	 * @param password
	 * @param mailAddress
	 */
	public synchronized void updateUserData(long userId, String userName,
			String password, String mailAddress, Blob thumbnail) {
		log.log(Level.INFO, "updateUserData");

		CipherUtil util = new CipherUtil();
		userName = util.encryptForInputString(userName);
		password = util.encryptForInputString(password);
		mailAddress = util.encryptForInputString(mailAddress);
		thumbnail = util.encryptForInputBlob(thumbnail);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Entity entity = null;
		try {
			entity = ds.get(key);
			if (userName != null) {
				entity.setProperty(LcomConst.ENTITY_USER_NAME, userName);
			}

			if (mailAddress != null) {
				entity.setProperty(LcomConst.ENTITY_MAIL_ADDRESS, mailAddress);
			}

			if (password != null) {
				entity.setProperty(LcomConst.ENTITY_PASSWORD, password);
			}

			if (thumbnail != null) {
				entity.setProperty(LcomConst.ENTITY_THUMBNAIL, thumbnail);
			}

			ds.put(entity);

		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "EntityNotFoundException: " + e.getMessage());
			LcomUserData data = new LcomUserData(userId, userName, password,
					mailAddress, thumbnail);
			addNewUserData(data);
		}
	}

	/**
	 * THis method shall be called if the user register his user name before his
	 * friend invited to this service (meaning user name was mail address)
	 * 
	 * @param userId
	 * @param userName
	 */
	public synchronized void updateUserNameInFriendhsiopTable(long userId,
			String userName) {
		log.log(Level.INFO, "updateUserNameInFriendhsiopTable");
		if (userId != LcomConst.NO_USER && userName != null) {
			log.log(Level.INFO, "userId: " + userId + " uerName: " + userName);

			CipherUtil util = new CipherUtil();
			userName = util.encryptForInputString(userName);

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			// Update all userName data belong to all kind
			Filter friendIdFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					FilterOperator.EQUAL, userId);

			// Key ancKey = getAllUserDataKey();
			Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, key);
			query.setFilter(friendIdFilter);
			PreparedQuery pQuery = ds.prepare(query);

			FetchOptions option = FetchOptions.Builder.withOffset(0);
			List<Entity> allEntities = pQuery.asList(option);

			// Update all entites by using userName
			for (Entity e : allEntities) {
				e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME, userName);
				ds.put(e);
			}
		} else {
			log.log(Level.INFO, "userId: " + userId);
			if (userName != null) {
				log.log(Level.INFO, "useName: " + userName);
			}
		}
	}

	public synchronized int getNumOfUserId() {
		log.log(Level.INFO, "getNumOfUserId");
		int userNum = 0;

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey(LcomAllUserData.class.getSimpleName(),
				"mTotalUserNum");
		Entity entity;
		try {
			entity = ds.get(key);
			userNum = (int) entity.getProperty("mTotalUserNum");
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "EntityNotFoundException: " + e.getMessage());
		}

		return userNum;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<LcomNewMessageData> getNewMessagesWithTargetUser(
			long userId, long friendUserId, long currentTime) {
		log.log(Level.WARNING, "getNewMessagesWithTargetUser: " + userId
				+ " / " + friendUserId);

		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		LcomDatabaseManagerUtil util = new LcomDatabaseManagerUtil();
		Entity entity = util.getEntityForTargetUser(userId, friendUserId, ds);

		if (entity != null) {
			log.log(Level.WARNING, "A");
			ArrayList<Long> friendId = (ArrayList<Long>) entity
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			ArrayList<String> messageArray = (ArrayList<String>) entity
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			ArrayList<String> messageTimeArray = (ArrayList<String>) entity
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
			ArrayList<String> messagePostedArray = (ArrayList<String>) entity
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
			ArrayList<String> targetUserNameArray = (ArrayList<String>) entity
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);

			// Values for returning valid data to client side
			List<String> validMessage = new ArrayList<String>();
			List<Long> validMessageTime = new ArrayList<Long>();
			List<Long> validPostedTime = new ArrayList<Long>();
			String targetUserName = null;

			if (friendId != null && messageArray != null
					&& messageTimeArray != null) {

				int index = friendId.indexOf(friendUserId);
				if (index >= 0) {
					// If the data is for the friendUserId
					targetUserName = targetUserNameArray.get(index);
					String messages = messageArray.get(index);
					String messageTimes = messageTimeArray.get(index);
					String messagePostTimes = messagePostedArray.get(index);

					if (messages != null && messageTimes != null
							&& messagePostTimes != null) {
						String[] msg = messages.split(LcomConst.SEPARATOR);
						String[] t = messageTimes.split(LcomConst.SEPARATOR);
						String[] postT = messagePostTimes
								.split(LcomConst.SEPARATOR);

						// If message is valid
						if (msg != null) {
							for (int j = 0; j < msg.length; j++) {
								if (Long.valueOf(t[j]) > currentTime) {
									// Put only valid data to return array
									validMessage.add(msg[j]);
									validMessageTime.add(Long.valueOf(t[j]));
									validPostedTime.add(Long.valueOf(postT[j]));
								}
							}

							// Remove unncessary characters
							messageArray.set(index, null);
							messageTimeArray.set(index, null);
							messagePostedArray.set(index, null);
						}
					}
				}

				// Update entity
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
						messageArray);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
						messageTimeArray);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
						messagePostedArray);

				ds.put(entity);

				if (validMessage != null && validMessage.size() != 0) {
					LcomNewMessageData data = new LcomNewMessageData(userId,
							friendUserId, targetUserName, validMessage,
							validPostedTime, validMessageTime);
					result.add(data);
				}
			}
		}

		CipherUtil cipherUtil = new CipherUtil();
		cipherUtil.decryptForNewMessageData(result);

		return result;

	}

	public synchronized long getUserIdByMailAddress(String address) {
		log.log(Level.WARNING, "getUserIdByMailAddress");
		long userId = LcomConst.NO_USER;

		CipherUtil util = new CipherUtil();
		address = util.encryptForInputString(address);

		if (address != null) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter mailFilter = new FilterPredicate(
					LcomConst.ENTITY_MAIL_ADDRESS, FilterOperator.EQUAL,
					address);

			Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
			Query query = new Query(LcomConst.KIND_USER_DATA, ancKey);
			query.setFilter(mailFilter);
			query.setKeysOnly();
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();

			userId = getIdFromEntity(entity);
		}

		return userId;
	}

	public synchronized LcomUserData getUserData(long userId) {
		log.log(Level.WARNING, "getUserData");
		LcomUserData result = null;

		if (userId != LcomConst.NO_USER) {

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
			Entity entity = null;
			try {
				entity = ds.get(key);
				result = new LcomUserData(userId, (String) entity
						.getProperty(LcomConst.ENTITY_USER_NAME),
						(String) entity.getProperty(LcomConst.ENTITY_PASSWORD),
						(String) entity
								.getProperty(LcomConst.ENTITY_MAIL_ADDRESS),
						(Blob) entity.getProperty(LcomConst.ENTITY_THUMBNAIL));
			} catch (EntityNotFoundException e) {
				log.log(Level.WARNING,
						"EntityNotFoundException: " + e.getMessage());
			}
		}

		CipherUtil util = new CipherUtil();
		util.decryptLcomUserData(result);

		return result;
	}

	public synchronized long addNewUserAndFriendshipInfo(LcomUserData data,
			long senderUserId, String senderName, String lastMessage,
			long currentTime) {
		log.log(Level.WARNING, "addNewUserAndFriendshipInfo");

		CipherUtil util = new CipherUtil();
		senderName = util.encryptForInputString(senderName);
		lastMessage = util.encryptForInputString(lastMessage);
		data = util.encryptForInputLcomUserData(data);

		long newUserId = LcomConst.NO_USER;

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		// Start transaction
		Transaction tx = ds.beginTransaction();

		try {

			newUserId = addNewUserData(data);

			addNewFriendshipInfo(senderUserId, senderName, newUserId, null,
					lastMessage, currentTime);
			// Finish transaction
			tx.commit();
		} catch (ConcurrentModificationException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		return newUserId;

	}

	@SuppressWarnings("unchecked")
	public synchronized void addNewFriendshipInfo(long senderUserId,
			String senderName, long keyUserId, String keyUserName,
			String lastMessage, long currentTime) {
		log.log(Level.WARNING, "addNewFriendshipData");

		CipherUtil cipherUtil = new CipherUtil();
		senderName = cipherUtil.encryptForInputString(senderName);
		keyUserName = cipherUtil.encryptForInputString(keyUserName);
		lastMessage = cipherUtil.encryptForInputString(lastMessage);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		LcomDatabaseManagerUtil util = new LcomDatabaseManagerUtil();

		// If entity itself exists
		if (util.isEntityForKeyUserIdExist(keyUserId, ds)) {
			log.log(Level.WARNING, "A");
			// If targetUserId already exist (meaning this user has already
			// sent message)

			// Get whole enttiy
			Entity entity = util.getEntityForKeyUser(keyUserId, ds);

			if (entity != null) {
				log.log(Level.WARNING, "B");
				// If sender User data exist in entity
				if (util.isFriendUserIdExistInEntity(entity, senderUserId)) {
					log.log(Level.WARNING, "C");
					// Add and update only message
					util.addMessageForFriendUser(entity, senderUserId,
							senderName, keyUserId, keyUserName, lastMessage,
							currentTime, ds);
				} else {
					log.log(Level.WARNING, "D");
					// If entity itself exist but it is new to send message
					// to this user
					// Add new user data and message
					util.addNewUserDataAndMessageToFriendship(entity,
							senderUserId, senderName, keyUserId, keyUserName,
							lastMessage, currentTime, ds);
				}
			} else {
				log.log(Level.WARNING, "E");
				// Something wrong. (this should not be happen)
			}
		} else {
			log.log(Level.WARNING, "F");
			// If entity itself doesn't exist
			// Create new entity
			util.addNewEntiyInFriendshipTable(senderUserId, senderName,
					keyUserId, keyUserName, lastMessage, currentTime, ds);
		}

	}

	public synchronized boolean isUsersAreFriend(long userId, long targetUserId) {

		boolean result = false;

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
				targetUserId);

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);
		// Key friendshipKey = KeyFactory.createKey(userKey,
		// LcomConst.KIND_FRIENDSHIP_DATA, userId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		query.setFilter(messageFilter);
		query.setKeysOnly();
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		if (entity != null) {
			result = true;
		}

		return result;
	}

	public synchronized List<LcomFriendshipData> getAllFriendshipData(
			long userId) {
		log.log(Level.INFO, "getAllFriendshipData");

		List<LcomFriendshipData> result = null;

		if (userId != LcomConst.NO_USER) {

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			LcomDatabaseManagerUtil util = new LcomDatabaseManagerUtil();
			Entity entity = util.getFriendshipEntityForUserIdWituhoutFilter(
					userId, ds);
			if (entity != null) {
				result = util.getAllFriendshipData(entity, ds, userId);
			}
		}

		CipherUtil util = new CipherUtil();
		util.decryptForLcomFriendshipData(result);

		return result;
	}

	@SuppressWarnings("unchecked")
	public synchronized List<LcomFriendshipData> getNewMessageData(long userId,
			long currentTime) {
		log.log(Level.INFO, "getNewMessageData");

		List<LcomFriendshipData> result = null;

		if (userId != LcomConst.NO_USER && currentTime > 0) {

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			LcomDatabaseManagerUtil util = new LcomDatabaseManagerUtil();
			Entity entity = util.getFriendshipEntityForUserId(userId, ds);
			if (entity != null) {
				result = util.getAllValidFriendshipData(entity, ds, userId);
			}
		}

		CipherUtil util = new CipherUtil();
		util.decryptForLcomFriendshipData(result);

		return result;
	}

	@SuppressWarnings("unchecked")
	public synchronized void addNewMessageInfo(long userId, long targetUserId,
			String userName, String targetUserName, String message,
			long currentDate) {
		log.log(Level.INFO, "addNewMessageInfo");

		CipherUtil cipherUtil = new CipherUtil();
		userName = cipherUtil.encryptForInputString(userName);
		targetUserName = cipherUtil.encryptForInputString(targetUserName);
		message = cipherUtil.encryptForInputString(message);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		LcomDatabaseManagerUtil util = new LcomDatabaseManagerUtil();

		// Check if Entity for target user exists or not
		boolean isExist = util.isConversationDataForTargetUserExist(
				targetUserId, ds);
		if (isExist) {
			Entity e = util.getConversationEntity(targetUserId, ds);
			if (e != null) {

				boolean isUserIdExist = util.isUserIdExistInFriendshipKind(
						userId, e);

				long expireTime = TimeUtil.getExpireDate(currentDate);

				// If userId exist
				if (isUserIdExist) {
					util.addMessageToFriendshipKind(userId, userName,
							currentDate, expireTime, message, e, ds);
				} else {
					// if userId doesn't exist, need to newly add friendId
					util.addNewUserDataToFriendshipKind(userId, userName,
							currentDate, expireTime, message, e, ds);
				}
			} else {
				log.log(Level.INFO, "Something wrong");
			}
		} else {
			// Entity doesn't exist, nothing to do.
			long expireTime = TimeUtil.getExpireDate(currentDate);
			log.log(Level.INFO, "Entity doesn't exist");
			util.createNewEntity(userId, userName, targetUserId, currentDate,
					expireTime, message, ds);
		}
	}

	public synchronized void debugDeleteNewMessageInfo(int userId,
			int targetUserId) {
		log.log(Level.INFO, "debugDeleteNewMessageInfo");
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		String queryFirst = "select from " + LcomNewMessageData.class.getName()
				+ " where mUserId == " + userId;
		List<LcomNewMessageData> firstMessages = (List<LcomNewMessageData>) pm
				.newQuery(queryFirst).execute();

		String querySecomd = "select from "
				+ LcomNewMessageData.class.getName()
				+ " where mTargetUserId == " + userId;
		List<LcomNewMessageData> secondMessages = (List<LcomNewMessageData>) pm
				.newQuery(querySecomd).execute();

		try {
			if (firstMessages != null && firstMessages.size() != 0) {
				log.log(Level.INFO,
						"firstFriendship size: " + firstMessages.size());
				pm.deletePersistent(firstMessages);
			}
			if (secondMessages != null && secondMessages.size() != 0) {
				log.log(Level.INFO,
						"secondMessages size: " + secondMessages.size());
				pm.deletePersistent(secondMessages);
			}

		} finally {
			pm.close();
		}

	}

	/**
	 * Debug method
	 */
	public synchronized void debugModifyNumOfUser(int numOfUser) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey(LcomAllUserData.class.getSimpleName(),
				"mTotalUserNum");
		Entity entity;
		try {
			entity = ds.get(key);
			entity.setProperty("mTotalUserNum", numOfUser);
			ds.put(entity);
		} catch (EntityNotFoundException e) {
			log.log(Level.WARNING, "EntityNotFoundException: " + e.getMessage());
		}
	}

	/**
	 * Get friends thumbnail data. It should be returned as Hashmap. Integer for
	 * friend userId. And String for thubmanil data itself.
	 * 
	 * @param friendsId
	 * @return
	 */
	public synchronized HashMap<Integer, String> getFriendThubmnails(
			List<String> friendsId) {

		log.log(Level.INFO, "getFriendThubmnails");

		HashMap<Integer, String> result = new HashMap<Integer, String>();

		if (friendsId != null && friendsId.size() != 0) {

			CipherUtil util = new CipherUtil();

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			for (String id : friendsId) {
				Key key = LcomDatabaseManagerUtil.getUserDataKey(Long
						.valueOf(id));
				Entity entity = null;
				try {
					entity = ds.get(key);
					// If thumbnail is available
					if (entity != null) {
						Blob thumbnail = (Blob) entity
								.getProperty(LcomConst.ENTITY_THUMBNAIL);
						if (thumbnail != null) {
							String thumbStr = DatastoreUtil
									.transcodeBlob2String(thumbnail);
							String decrypted = util
									.decryptForInputString(thumbStr);
							// Note that this is decrypted.
							result.put(Integer.valueOf(id), decrypted);
						} else {
							// If thumbnail is not available (null)
							// Nothing to do
						}
					}
				} catch (EntityNotFoundException e) {
					log.log(Level.INFO,
							"EntityNotFoundException: " + e.getMessage());
				}
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public synchronized void backupOldMessageData(long currentTime) {
		log.log(Level.INFO, "backupOldMessageData");

		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				FilterOperator.NOT_EQUAL, null);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Key ancKey = LcomDatabaseManagerUtil.getAllUserDataKey();
		Query query = new Query(LcomConst.KIND_USER_DATA, ancKey);
		query.setKeysOnly();
		// query.setFilter(messageFilter);
		// query.setKeysOnly();
		PreparedQuery pQuery = ds.prepare(query);
		FetchOptions option = FetchOptions.Builder.withOffset(0);
		List<Entity> allEntities = pQuery.asList(option);
		log.log(Level.INFO, "A");

		if (allEntities != null && allEntities.size() != 0) {

			log.log(Level.INFO, "A2 size: " + allEntities.size());

			for (Entity e : allEntities) {

				Key userKey = e.getKey();

				Query queryFS = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
						userKey);
				queryFS.setFilter(messageFilter);
				PreparedQuery pQueryFS = ds.prepare(queryFS);
				Entity entity = pQueryFS.asSingleEntity();

				if (entity != null) {
					List<String> messageArray = (List<String>) entity
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
					List<String> expireTimeArray = (List<String>) entity
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
					List<String> messagePostedArray = (List<String>) entity
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

					List<String> validMessageArray = new ArrayList<String>();
					List<String> validExpireTimeArray = new ArrayList<String>();
					List<String> validPostTimeArray = new ArrayList<String>();

					for (int i = 0; i < expireTimeArray.size(); i++) {

						String tmpMessageString = "a";
						String tmpExpireString = "a";
						String tmpPostString = "a";

						String messageString = messageArray.get(i);
						String expireTimeString = expireTimeArray.get(i);
						String postTimeString = messagePostedArray.get(i);

						if (expireTimeString != null && messageString != null
								&& postTimeString != null) {

							String[] expire = expireTimeString
									.split(LcomConst.SEPARATOR);
							String[] msg = messageString
									.split(LcomConst.SEPARATOR);
							String[] post = postTimeString
									.split(LcomConst.SEPARATOR);

							for (int j = 0; j < expire.length; j++) {
								// If message has already expired
								if (Long.valueOf(expire[j]) > currentTime) {
									tmpMessageString = tmpMessageString
											+ LcomConst.SEPARATOR + msg[j];
									tmpExpireString = tmpExpireString
											+ LcomConst.SEPARATOR + expire[j];
									tmpPostString = tmpPostString
											+ LcomConst.SEPARATOR + post[j];
								}
							}

							// If more than 1 message has been registered
							if (tmpMessageString != null
									&& (tmpMessageString.length() > (1 + LcomConst.SEPARATOR
											.length()))) {
								tmpMessageString = tmpMessageString
										.substring((1 + LcomConst.SEPARATOR
												.length()));
								log.log(Level.INFO, "tmpMessageString: "
										+ tmpMessageString);
								validMessageArray.add(tmpMessageString);
							}

							if (tmpExpireString != null
									&& (tmpExpireString.length() > (1 + LcomConst.SEPARATOR
											.length()))) {
								tmpExpireString = tmpExpireString
										.substring((1 + LcomConst.SEPARATOR
												.length()));
								validExpireTimeArray.add(tmpExpireString);
							}

							if (tmpPostString != null
									&& (tmpPostString.length() > (1 + LcomConst.SEPARATOR
											.length()))) {
								tmpPostString = tmpPostString
										.substring((1 + LcomConst.SEPARATOR
												.length()));
								validPostTimeArray.add(tmpPostString);
							}

							// If the number of message is changed (meaning some
							// old message is contained)

							if (validMessageArray == null
									|| messageArray.size() > validMessageArray
											.size()) {
								if (validMessageArray != null) {
									log.log(Level.INFO,
											"validMessageArray size:"
													+ validMessageArray.size());
								} else {
									log.log(Level.INFO,
											"validMessageArray is null");
								}

								// Set updated parameters to Entity
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
										validMessageArray);
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
										validPostTimeArray);
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
										validExpireTimeArray);
								// TODO
								// ds.put(e);
							} else {
								log.log(Level.INFO,
										"validMessageArray is not null or size not changed");
							}
						}
					}

				}
			}
		}
	}

	private static synchronized List<LcomExpiredMessageData> backupToExpiredTable(
			List<LcomNewMessageData> oldMessages) {
		log.log(Level.INFO, "backupToExpiredTable");

		// if (oldMessages != null) {
		//
		// List<LcomExpiredMessageData> expiredMessages = new
		// ArrayList<LcomExpiredMessageData>();
		//
		// for (LcomNewMessageData oldMessage : oldMessages) {
		// int userId = oldMessage.getUserId();
		// int targetUserId = oldMessage.getTargetUserId();
		// String message = oldMessage.getMessage();
		// long postedDate = oldMessage.getPostedDate();
		// LcomExpiredMessageData expireNessage = new LcomExpiredMessageData(
		// userId, targetUserId, message, postedDate);
		//
		// expiredMessages.add(expireNessage);
		//
		// }
		// return expiredMessages;
		// }
		return null;
	}

	public List<LcomExpiredMessageData> debugGetExpiredMessages() {
		log.log(Level.INFO, "debugGetExpiredMessages");

		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		String query = "select from " + LcomExpiredMessageData.class.getName();
		List<LcomExpiredMessageData> messages = (List<LcomExpiredMessageData>) pm
				.newQuery(query).execute();

		return messages;
	}

	public synchronized void setDeviceIdForMessagePush(int userId,
			String deviceId) {
		log.log(Level.INFO, "setDeviceIdForMessagePush");

		CipherUtil cipherUtil = new CipherUtil();
		deviceId = cipherUtil.encryptForInputString(deviceId);

		if (userId != LcomConst.NO_USER && deviceId != null) {
			DatastoreService datastoreService = DatastoreServiceFactory
					.getDatastoreService();
			// Key key =
			// KeyFactory.createKey(LcomUserData.class.getSimpleName(),
			// userId);
			Key ancestorKey = KeyFactory.createKey(
					LcomConst.KIND_ALL_USER_DATA,
					LcomConst.ENTITY_TOTAL_USER_NUM);
			Key key = KeyFactory.createKey(ancestorKey,
					LcomConst.KIND_USER_DATA, userId);
			Entity entity = null;
			try {
				entity = datastoreService.get(key);
				entity.setProperty(LcomConst.ENTITY_DEVICE_ID, deviceId);
				datastoreService.put(entity);

				// Try to put device id to memcache
				LcomDatabaseManagerHelper.putDeviceIdForGCMPush(userId,
						deviceId);

			} catch (EntityNotFoundException e1) {
				log.log(Level.WARNING,
						"EntityNotFoundException: " + e1.getMessage());
				// If no data is on datastore
				entity = new Entity(LcomConst.KIND_USER_DATA, userId,
						ancestorKey);
				// String userName = (String) entity.getProperty("mUserName");
				entity.setProperty(LcomConst.ENTITY_DEVICE_ID, deviceId);
				datastoreService.put(entity);

				// Try to put deviceId to memcache

			}
		}
	}

	public String getDeviceIdForGCMPush(long userId) {
		log.log(Level.INFO, "getDeviceIdForGCMPush");

		if (userId == LcomConst.NO_USER) {
			return null;
		}

		String deviceId = LcomDatabaseManagerHelper
				.getDeviceIdForGCMPush(userId);

		// If memcache doesn't exist
		if (deviceId == null) {

			DatastoreService datastoreService = DatastoreServiceFactory
					.getDatastoreService();

			Key key = LcomDatabaseManagerUtil.getUserDataKey(userId);
			try {
				Entity entity = datastoreService.get(key);
				deviceId = (String) entity
						.getProperty(LcomConst.ENTITY_DEVICE_ID);

				// Try to put device id to memcache
				LcomDatabaseManagerHelper.putDeviceIdForGCMPush(userId,
						deviceId);

			} catch (EntityNotFoundException e) {
				log.log(Level.INFO,
						"EntityNotFoundException: " + e.getMessage());
			}

		}

		CipherUtil cipherUtil = new CipherUtil();
		deviceId = cipherUtil.decryptForInputString(deviceId);

		return deviceId;
	}

	public void deleteUserData(int userId) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		String query = "select from " + LcomUserData.class.getName()
				+ " where mUserId == " + userId;
		List<LcomUserData> datas = (List<LcomUserData>) pm.newQuery(query)
				.execute();
		if (datas != null) {
			try {
				LcomUserData data = datas.get(0);
				if (data != null && data.getUserId() != LcomConst.NO_USER) {
					try {
						pm.deletePersistent(data);
					} finally {
						pm.close();
					}
				}
			} catch (IndexOutOfBoundsException e) {
				// Nothing to do
			}

		}
	}

	public void debugDeleteFriendshipData() {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		String query = "select from " + LcomFriendshipData.class.getName();
		List<LcomFriendshipData> datas = (List<LcomFriendshipData>) pm
				.newQuery(query).execute();
		if (datas != null && datas.size() != 0) {
			for (LcomFriendshipData data : datas) {
				try {
					try {
						pm.deletePersistent(data);
					} finally {
						pm.close();
					}

				} catch (IndexOutOfBoundsException e) {
					// Nothing to do
				}
			}
		}
	}

}
