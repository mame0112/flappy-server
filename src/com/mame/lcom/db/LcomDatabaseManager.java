package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomAllUserData;
import com.mame.lcom.data.LcomExpiredMessageData;
import com.mame.lcom.data.LcomFriendshipData;
//import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomMessageDeviceId;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

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

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter nameFilter = new FilterPredicate(LcomConst.ENTITY_USER_NAME,
					FilterOperator.EQUAL, userName);

			Key ancKey = getAllUserDataKey();
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

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		long userId = data.getUserId();

		Transaction tx = ds.beginTransaction();

		try {
			// Query query = new Query(LcomAllUserData.class.getSimpleName());
			// query.setKeysOnly();
			// PreparedQuery pQuery = ds.prepare(query);
			// Entity entity = pQuery.asSingleEntity();

			Key ancKey = getAllUserDataKey();
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

	private Key getAllUserDataKey() {
		Key ancKey = KeyFactory.createKey(LcomConst.KIND_ALL_USER_DATA,
				LcomConst.ENTITY_TOTAL_USER_NUM);
		return ancKey;
	}

	private Key getUserDataKey(long userId) {
		Key ancKey = getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		return key;
	}

	/**
	 * This will NOT use datastore read ops. Just use small ops.
	 * 
	 * @return
	 */
	private int getAllUserDataNum() {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key ancKey = getAllUserDataKey();

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

	// private Entity getUserDataEntity() {
	// DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	// Key ancKey = getAllUserDataKey();
	// Entity entity = null;
	// try {
	// entity = ds.get(ancKey);
	// } catch (EntityNotFoundException e) {
	// // If no AllUserData exist (This shall be occur at the very first
	// // time
	// log.log(Level.WARNING, "EntityNotFoundException: " + e.getMessage());
	// Entity totalEntity = new Entity(ancKey);
	// totalEntity.setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, 1);
	// ds.put(totalEntity);
	// }
	// return entity;
	// }

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

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Key key = getUserDataKey(userId);
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

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			// Update all userName data belong to all kind
			Filter friendIdFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					FilterOperator.EQUAL, userId);

			// Key ancKey = getAllUserDataKey();
			Key key = getUserDataKey(userId);
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

	// public synchronized void updateUserDate(LcomUserData updatedData) {
	// if (updatedData != null) {
	// PersistenceManager pm = LcomPersistenceManagerFactory.get()
	// .getPersistenceManager();
	// try {
	// LcomUserData oldData = pm.getObjectById(LcomUserData.class,
	// updatedData.getUserId());
	//
	// String userName = updatedData.getUserName();
	// if (userName != null) {
	// oldData.setUserName(userName);
	// }
	//
	// String address = updatedData.getMailAddress();
	// if (address != null) {
	// oldData.setMailAddress(address);
	// }
	//
	// String password = updatedData.getPassword();
	// if (password != null) {
	// oldData.setPassword(password);
	// }
	//
	// Blob thumbnail = updatedData.getThumbnail();
	// if (thumbnail != null) {
	// oldData.setThumbnail(thumbnail);
	// }
	//
	// // Update memcache
	// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	// helper.putUserDataToMemCache(oldData);
	//
	// } finally {
	// pm.close();
	// }
	// }
	// }

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

	// public synchronized List<LcomNewMessageData> getNewMessages(int userId) {
	// log.log(Level.WARNING, "getNewMessages");
	//
	// List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();
	//
	// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	// List<LcomNewMessageData> unreadMessages = null;
	// try {
	// result = helper.getNewMessageFromMemcache(userId);
	// unreadMessages = changeNewMessageReadState(result);
	//
	// // `Put new messages to memcache
	// helper.removeNewMessagesFromMemCache(userId);
	// helper.putNewMessagesToMemCache(userId, unreadMessages);
	//
	// if (unreadMessages != null && unreadMessages.size() != 0) {
	// // cache exist. It means we should not store it to cache again.
	// log.log(Level.WARNING, "Cache exists result: " + unreadMessages);
	// return unreadMessages;
	// } else {
	// // cache exist
	// log.log(Level.WARNING, "Cache could be strange status");
	// }
	// } catch (LcomMemcacheException e) {
	// // Cache doesn't exit. It means we need to put it to cache.
	// log.log(Level.WARNING, "LcomMemcacheException: " + e.getMessage());
	// // If we can't get message from memcache, we try to get it from
	// // datastore
	// if (result == null || result.size() == 0) {
	// log.log(Level.INFO, "Data from datastore");
	// PersistenceManager pm = LcomPersistenceManagerFactory.get()
	// .getPersistenceManager();
	//
	// // query messages its target user is me
	// String queryFromOthers = "select from "
	// + LcomNewMessageData.class.getName()
	// + " where mTargetUserId == " + userId;
	// result = (List<LcomNewMessageData>) pm
	// .newQuery(queryFromOthers).execute();
	//
	// pm.close();
	//
	// // Put data to memcache
	// if (result != null && result.size() != 0) {
	// try {
	// unreadMessages = changeNewMessageReadState(result);
	// if (unreadMessages != null
	// && unreadMessages.size() != 0) {
	// helper.putNewMessagesToMemCache(userId,
	// unreadMessages);
	// }
	// } catch (LcomMemcacheException e1) {
	// log.log(Level.WARNING,
	// "LcomMemcacheException: " + e1.getMessage());
	// }
	// }
	// return unreadMessages;
	// } else {
	// // Nothing to do
	// log.log(Level.INFO,
	// "result is not null or 0 (From memcache, could be strange status");
	// }
	// }
	//
	// return null;
	// }

	// private List<LcomNewMessageData> changeNewMessageReadState(
	// List<LcomNewMessageData> input) {
	// log.log(Level.INFO, "changeNewMessageReadState");
	// // Store message to memcache
	// if (input != null && input.size() != 0) {
	//
	// log.log(Level.INFO, "resultsize::: " + input.size());
	// List<LcomNewMessageData> unreadMessages = new
	// ArrayList<LcomNewMessageData>();
	// for (LcomNewMessageData message : input) {
	// boolean isRead = message.isMessageRead();
	// if (isRead == false) {
	// log.log(Level.INFO, "message with already read:: "
	// + message.getMessage());
	// unreadMessages.add(message);
	// }
	// }
	//
	// return unreadMessages;
	//
	// } else {
	// log.log(Level.INFO, "result is null or 0 A");
	// }
	//
	// return null;
	// }

	/**
	 * Get thread message for target user. If you call this method, message read
	 * state shall automatically be changed to "read" state.
	 * 
	 * @param userId
	 * @param friendUserId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<LcomNewMessageData> getNewMessagesWithTargetUser(
			long userId, long friendUserId, long currentTime) {
		log.log(Level.WARNING, "getNewMessagesWithTargetUser");

		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

		if (userId != LcomConst.NO_USER && friendUserId != LcomConst.NO_USER) {
			Key userKey = getUserDataKey(userId);
			Key key = KeyFactory.createKey(userKey,
					LcomConst.KIND_FRIENDSHIP_DATA, userId);

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter friendFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					FilterOperator.EQUAL, friendUserId);

			// Query query = new Query(key);
			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, key);
			query.setFilter(friendFilter);
			// query.setKeysOnly();
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();

			// If target entity already exist
			if (entity != null) {
				ArrayList<String> messageArray = (ArrayList<String>) entity
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
				ArrayList<Long> messageTimeArray = (ArrayList<Long>) entity
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
				ArrayList<Long> messagePostedArray = (ArrayList<Long>) entity
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
				String targetUserName = (String) entity
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
				// String[] message = messageArray.split(",");
				// String[] time = messageTimeArray.split(",");
				// String[] postedTime = messagePostedArray.split(",");

				List<String> validMessage = new ArrayList<String>();
				List<Long> validMessageTime = new ArrayList<Long>();
				List<Long> validPostedTime = new ArrayList<Long>();

				// boolean isRemoved = false;

				if (messageTimeArray != null && messageTimeArray.size() != 0) {
					for (int i = 0; i < messageTimeArray.size(); i++) {
						long t = Long.valueOf(messageTimeArray.get(i));
						long postedT = Long.valueOf(messagePostedArray.get(i));
						// If message is valid
						if (t > currentTime) {
							validMessage.add(messageArray.get(i));
							validMessageTime.add(t);
							validPostedTime.add(postedT);

						}
						// else {
						// isRemoved = true;
						// }
					}
				}

				// Remove read message
				// if (isRemoved == true) {
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
						null);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
						null);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
						null);
				ds.put(entity);
				// }

				LcomNewMessageData data = new LcomNewMessageData(userId,
						friendUserId, targetUserName, validMessage,
						validPostedTime, validMessageTime);
				result.add(data);
			} else {
				// If target user has not been set, nothing to do
			}

		}

		return result;
	}

	// private List<LcomNewMessageData> getMessageForTargetUser(int
	// targetUserId,
	// List<LcomNewMessageData> original) {
	// List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();
	//
	// if (targetUserId != LcomConst.NO_USER && original != null) {
	// for (LcomNewMessageData message : original) {
	// if (message != null) {
	// boolean isRead = message.isMessageRead();
	// if (isRead == false) {
	// int targetId = message.getUserId();
	// if (targetId == targetUserId) {
	// // Set message to "already read"state
	// message.setReadState(true);
	//
	// result.add(message);
	// }
	// }
	// }
	// }
	// return result;
	// }
	// return null;
	// }

	public synchronized long getUserIdByMailAddress(String address) {
		log.log(Level.WARNING, "getUserIdByMailAddress");
		long userId = LcomConst.NO_USER;

		if (address != null) {
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			Filter mailFilter = new FilterPredicate(
					LcomConst.ENTITY_MAIL_ADDRESS, FilterOperator.EQUAL,
					address);

			Key ancKey = getAllUserDataKey();
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
			Key key = getUserDataKey(userId);
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

		return result;
	}

	public synchronized void addNewFriendshipInfo(long userId, String userName,
			long friendUserId, String friendUserName, String lastMessage,
			long time) {
		log.log(Level.WARNING, "addNewFriendshipInfo");

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Transaction tx = ds.beginTransaction();
		try {
			Key ancKey1 = KeyFactory.createKey(LcomConst.KIND_ALL_USER_DATA,
					LcomConst.ENTITY_TOTAL_USER_NUM);
			Key ancKey2 = KeyFactory.createKey(ancKey1,
					LcomConst.KIND_USER_DATA, userId);
			Key key = KeyFactory.createKey(ancKey2,
					LcomConst.KIND_FRIENDSHIP_DATA, userId);

			// Check if already friendship is added
			// Filter friendFilter = new FilterPredicate(
			// LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
			// FilterOperator.EQUAL, friendUserId);
			//
			// Query query = new Query(LcomConst.KIND_USER_DATA);
			// query.setFilter(friendFilter);
			// query.setKeysOnly();
			// PreparedQuery pQuery = ds.prepare(query);
			// Entity entity = pQuery.asSingleEntity();
			//
			// // If friend is not added (meaning new user)
			// if (entity == null) {
			// //
			// } else {
			// // Just add frienddata
			// }

			// Update the number of total user
			// try {
			// Entity totalEntity = ds.get(ancKey1);
			// int num = (int) totalEntity.getKey().getId();
			// int newNum = num + 1;
			// totalEntity
			// .setProperty(LcomConst.ENTITY_TOTAL_USER_NUM, newNum);
			// ds.put(totalEntity);
			// } catch (EntityNotFoundException e) {
			// log.log(Level.WARNING,
			// "EntityNotFoundException: " + e.getMessage());
			// }

			// TODO Need to query and update message

			// Add friendship
			long expireDate = TimeUtil.getExpireDate(time);
			Entity entity = new Entity(key);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, userId);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, userName);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					friendUserId);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					friendUserName);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					Arrays.asList(lastMessage));
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					Arrays.asList(time));
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					Arrays.asList(expireDate));
			ds.put(entity);

			tx.commit();
		} catch (ConcurrentModificationException e) {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	// public synchronized void updateLatestMessageInfoOnFriendshipTable(
	// int userId, int targetUserId, String latestMessage,
	// long lastPostedTime) {
	// log.log(Level.INFO, "updateLatestMessageInfoOnFriendshipTable");
	//
	// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	//
	// if (userId != LcomConst.NO_USER && targetUserId != LcomConst.NO_USER) {
	// if (latestMessage != null && lastPostedTime != 0L) {
	// boolean result = false;
	//
	// PersistenceManager pm = LcomPersistenceManagerFactory.get()
	// .getPersistenceManager();
	// String queryFirst = "select from "
	// + LcomFriendshipData.class.getName()
	// + " where mFirstUserId == " + userId + "";
	// List<LcomFriendshipData> firstUsers = (List<LcomFriendshipData>) pm
	// .newQuery(queryFirst).execute();
	//
	// if (firstUsers != null && firstUsers.size() != 0) {
	// for (LcomFriendshipData firstData : firstUsers) {
	// if (firstData != null) {
	// int friendId = firstData.getSecondUserId();
	// if (targetUserId == friendId) {
	// // If we found message, update message and
	// // posted time
	// firstData.setLatestMessage(latestMessage);
	// long expireDate = TimeUtil
	// .getExpireDate(lastPostedTime);
	// firstData.setLastMessageExpireTime(expireDate);
	//
	// // Put to memcache
	// try {
	// helper.putFriendListDataToMemCache(firstData);
	// } catch (LcomMemcacheException e) {
	// log.log(Level.WARNING,
	// "LcomMemcacheException: "
	// + e.getMessage());
	// }
	//
	// result = true;
	// break;
	// }
	// }
	// }
	// }
	//
	// // If result is still false, we continue to check
	// if (result == false) {
	// // Second, friend to user
	// String querySecond = "select from "
	// + LcomFriendshipData.class.getName()
	// + " where mSecondUserId == " + userId + "";
	// List<LcomFriendshipData> secondUsers = (List<LcomFriendshipData>) pm
	// .newQuery(querySecond).execute();
	// if (secondUsers != null && secondUsers.size() != 0) {
	// for (LcomFriendshipData secondData : secondUsers) {
	// if (secondData != null) {
	// int friendId = secondData.getFirstUserId();
	// if (targetUserId == friendId) {
	// // If we found message, update message and
	// // posted time
	// secondData.setLatestMessage(latestMessage);
	// long expireDate = TimeUtil
	// .getExpireDate(lastPostedTime);
	// secondData
	// .setLastMessageExpireTime(expireDate);
	//
	// // Put to memcache
	// try {
	// helper.putFriendListDataToMemCache(secondData);
	// } catch (LcomMemcacheException e) {
	// log.log(Level.WARNING,
	// "LcomMemcacheException: "
	// + e.getMessage());
	// }
	//
	// result = true;
	// break;
	// }
	// }
	// }
	// }
	// }
	//
	// }
	// }
	//
	// }

	public synchronized boolean isUsersAreFriend(long userId, long targetUserId) {

		boolean result = false;

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
				targetUserId);

		Key userKey = getUserDataKey(userId);
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

	@SuppressWarnings("unchecked")
	public synchronized List<LcomFriendshipData> getNewMessageData(long userId,
			long currentTime) {
		log.log(Level.INFO, "getNewMessageData");

		if (userId != LcomConst.NO_USER && currentTime > 0) {

			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

			Filter messageFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					FilterOperator.NOT_EQUAL, null);

			Key userKey = getUserDataKey(userId);
			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
			// TODO
			// query.setFilter(messageFilter);
			PreparedQuery pQuery = ds.prepare(query);

			FetchOptions option = FetchOptions.Builder.withOffset(0);
			List<Entity> allEntities = pQuery.asList(option);

			if (allEntities != null) {
				log.log(Level.INFO, "allEntities size: " + allEntities.size());
			}

			if (allEntities != null && allEntities.size() != 0) {

				List<String> validMessage = new ArrayList<String>();
				List<Long> validTime = new ArrayList<Long>();
				List<Long> validPostedTime = new ArrayList<Long>();

				for (Entity e : allEntities) {
					Long friendId = (Long) e
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
					String friendName = (String) e
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
					List<String> messageArray = (List<String>) e
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
					ArrayList<Long> messageTimeArray = (ArrayList<Long>) e
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
					ArrayList<Long> postedTimeArray = (ArrayList<Long>) e
							.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
					// String[] message = messageArray.split(",");
					// String[] time = messageTimeArray.split(",");

					boolean isRemoved = false;

					log.log(Level.INFO, "friendId: " + friendId);
					log.log(Level.INFO, "friendName: " + friendName);

					if (messageArray != null) {
						log.log(Level.INFO, "messageArray size: "
								+ messageArray.size());
					} else {
						log.log(Level.INFO, "messageArray is null");
					}

					if (postedTimeArray != null && postedTimeArray.size() != 0) {
						log.log(Level.INFO, "postedTimeArray: "
								+ postedTimeArray.get(0));
					}

					if (messageArray != null && messageArray.size() != 0) {
						log.log(Level.WARNING, "AA");
						for (int i = 0; i < messageArray.size(); i++) {
							log.log(Level.WARNING, "BB");
							long t = messageTimeArray.get(i);
							long postT = postedTimeArray.get(i);
							log.log(Level.WARNING, "t: " + t);
							log.log(Level.WARNING, "currentTime: "
									+ currentTime);
							// If message is valid
							if (t > currentTime) {
								log.log(Level.WARNING, "CC");
								validMessage.add(messageArray.get(i));
								validTime.add(t);
								validPostedTime.add(postT);

							} else {
								log.log(Level.WARNING, "DD");
								// If message has already been expired
								// Nothing to do
								isRemoved = true;
							}
						}
					}

					// If more than 1 message is new message
					if (validMessage != null && validMessage.size() != 0) {
						log.log(Level.WARNING, "validMessage size: "
								+ validMessage.size());
						LcomFriendshipData data = new LcomFriendshipData(
								userId, friendId, friendName, validMessage,
								validTime);
						result.add(data);
					} else {
						log.log(Level.WARNING, "validMessage is null or size 0");
					}

					// TODO Need to update user name because it may be empty.

					if (isRemoved == true) {
						e.setProperty(
								LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
								validMessage);
						e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
								validTime);
						e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
								validPostedTime);
						// TODO need to have this function later on.
						// ds.put(e);

					}
				}
			}
			return result;
		} else {
			return null;
		}
	}

	// public synchronized List<LcomFriendshipData> getFriendshipDataForUser(
	// int userId) {
	// log.log(Level.INFO, "getFriendshipDataForUser");
	//
	// List<LcomFriendshipData> firstFriendship = null;
	// List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();
	//
	// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
	//
	// try {
	// firstFriendship = helper.getFriendListDataFromMemCache(userId);
	// } catch (LcomMemcacheException e) {
	// log.log(Level.INFO, "LcomMemcacheException: " + e.getMessage());
	// }
	//
	// if (firstFriendship == null) {
	// PersistenceManager pm = LcomPersistenceManagerFactory.get()
	// .getPersistenceManager();
	//
	// // Get friendship data.
	// // First, user is first.
	// String queryFirst = "select from "
	// + LcomFriendshipData.class.getName()
	// + " where mFirstUserId == " + userId;
	// firstFriendship = (List<LcomFriendshipData>) pm
	// .newQuery(queryFirst).execute();
	//
	// if (firstFriendship != null && firstFriendship.size() != 0) {
	// log.log(Level.INFO,
	// "firstFriendship size: " + firstFriendship.size());
	// result.addAll(firstFriendship);
	// }
	//
	// // First, user is second.
	// String querySecond = "select from "
	// + LcomFriendshipData.class.getName()
	// + " where mSecondUserId == " + userId;
	// List<LcomFriendshipData> secondFriendship = (List<LcomFriendshipData>) pm
	// .newQuery(querySecond).execute();
	//
	// // Combine two List
	// if (secondFriendship != null && secondFriendship.size() != 0) {
	// log.log(Level.INFO, "secondFriendship size: "
	// + secondFriendship.size());
	// result.addAll(secondFriendship);
	// }
	//
	// // TODO Need to put data to memcache
	//
	// pm.close();
	// } else {
	// result = firstFriendship;
	// }
	//
	// return result;
	// }

	@SuppressWarnings("unchecked")
	public synchronized void addNewMessageInfo(long userId, long targetUserId,
			String userName, String targetUserName, String message,
			long currentDate) {
		log.log(Level.INFO, "addNewMessageInfo");

		// Get friend Key (Because we need to put message data onto target user
		// kind)
		Key targetUserKey = getUserDataKey(targetUserId);

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Filter userFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
				userId);
		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				FilterOperator.NOT_EQUAL, null);

		// Key friendshipKey = KeyFactory.createKey(userKey,
		// LcomConst.KIND_FRIENDSHIP_DATA, userId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, targetUserKey);
		query.setFilter(userFilter);
		// TODO Need to consider setKeysOnly if Datastore Ops is not good
		// query.setKeysOnly();
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		long expireDate = TimeUtil.getExpireDate(currentDate);

		// First, check if entity for user already exist
		// If entity for user exists
		if (entity != null) {
			Query queryForNewMsg = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
					targetUserKey);
			queryForNewMsg.setFilter(userFilter);
			queryForNewMsg.setFilter(messageFilter);
			PreparedQuery pQueryForNewMsg = ds.prepare(queryForNewMsg);
			Entity entityForNewMsg = pQueryForNewMsg.asSingleEntity();

			// If new message exists
			if (entityForNewMsg != null) {
				ArrayList<String> messageList = (ArrayList<String>) entityForNewMsg
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
				ArrayList<Long> postDateList = (ArrayList<Long>) entityForNewMsg
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
				ArrayList<Long> expireDateList = (ArrayList<Long>) entityForNewMsg
						.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);

				// If message already exist
				if (messageList != null) {
					messageList.add(message);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
							messageList);

					postDateList.add(currentDate);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
							postDateList);

					expireDateList.add(expireDate);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
							expireDateList);
					ds.put(entityForNewMsg);

				} else {
					// if no message exist
					List<String> newMessageList = new ArrayList<String>();
					List<Long> newPostDateList = new ArrayList<Long>();
					List<Long> newExpireDateList = new ArrayList<Long>();
					newMessageList.add(message);
					newPostDateList.add(currentDate);
					newExpireDateList.add(expireDate);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
							newMessageList);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
							newPostDateList);
					entityForNewMsg.setProperty(
							LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
							newExpireDateList);
					ds.put(entityForNewMsg);
				}
			} else {
				// If new message doesn't exist
				List<String> newMessageList = new ArrayList<String>();
				List<Long> newPostDateList = new ArrayList<Long>();
				List<Long> newExpireDateList = new ArrayList<Long>();
				newMessageList.add(message);
				newPostDateList.add(currentDate);
				newExpireDateList.add(expireDate);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
						newMessageList);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
						newPostDateList);
				entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
						newExpireDateList);
				ds.put(entity);
			}

		} else {
			// If entity for user doesn't exist
			// We need to create entity
			entity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, targetUserId,
					targetUserKey);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, userId);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					userName);
			List<String> newMessageList = new ArrayList<String>();
			List<Long> newPostDateList = new ArrayList<Long>();
			List<Long> newExpireDateList = new ArrayList<Long>();
			newMessageList.add(message);
			newPostDateList.add(currentDate);
			newExpireDateList.add(expireDate);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					newMessageList);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					newPostDateList);
			entity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					newExpireDateList);
			ds.put(entity);
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

		// PersistenceManager pm = LcomPersistenceManagerFactory.get()
		// .getPersistenceManager();
		// Query query = pm.newQuery(LcomAllUserData.class);
		// List<LcomAllUserData> datas = (List<LcomAllUserData>)
		// query.execute();
		// LcomAllUserData data = null;
		// if (datas != null && datas.size() != 0) {
		// data = datas.get(0);
		// data.changetTotalUserNum(numOfUser);
		// try {
		// pm.makePersistent(data);
		// } finally {
		// pm.close();
		// }
		// } else {
		// // If data doesn't exist
		// try {
		// LcomAllUserData newData = new LcomAllUserData(numOfUser);
		// pm.makePersistent(newData);
		// } finally {
		// pm.close();
		// }
		// }

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
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		HashMap<Integer, String> result = new HashMap<Integer, String>();

		if (friendsId != null && friendsId.size() != 0) {
			for (String id : friendsId) {
				Key key = getAllUserDataKey();

				DatastoreService ds = DatastoreServiceFactory
						.getDatastoreService();

				Filter nullFilter = new FilterPredicate(
						LcomConst.ENTITY_THUMBNAIL, FilterOperator.NOT_EQUAL,
						null);

				Query query = new Query(LcomConst.KIND_USER_DATA, key);
				query.setFilter(nullFilter);
				PreparedQuery pQuery = ds.prepare(query);
				Entity entity = pQuery.asSingleEntity();

				// If thumbnail is available
				if (entity != null) {
					Blob thumbnail = (Blob) entity
							.getProperty(LcomConst.ENTITY_THUMBNAIL);
					if (thumbnail != null) {
						String thumbStr = DatastoreUtil
								.transcodeBlob2String(thumbnail);
						result.put(Integer.valueOf(id), thumbStr);
					} else {
						// If thumbnail is not available (null)
						// Nothing to do
					}
				}
			}
		}

		return result;
	}

	public static synchronized void backupOldMessageData(long currentTime) {
		log.log(Level.INFO, "backupOldMessageData");
		// PersistenceManager pm = LcomPersistenceManagerFactory.get()
		// .getPersistenceManager();
		//
		// // Delete latest message on Friendship table
		// String friendShipQuery = "select from "
		// + LcomFriendshipData.class.getName();
		// List<LcomFriendshipData> friendDatas = (List<LcomFriendshipData>) pm
		// .newQuery(friendShipQuery).execute();
		//
		// if (friendDatas != null && friendDatas.size() != 0) {
		// for (LcomFriendshipData data : friendDatas) {
		// if (data != null) {
		// log.log(Level.INFO, "A");
		// String latestMessage = data.getLatestMessage();
		// // If message has not been marked as "expired"
		// if (latestMessage != null
		// && !latestMessage.equals(LcomConst.MESSAGE_EXPIRED)) {
		// log.log(Level.INFO, "B");
		// // Now we check message expire date
		// long messageTime = data.getLastMessageExpireTime();
		// if (currentTime > messageTime) {
		// log.log(Level.INFO, "C");
		// // Set message as "expired" mark.
		// data.setLatestMessage(LcomConst.MESSAGE_EXPIRED);
		// }
		// }
		// }
		// }
		// }
		//
		// // TODO need to restore memcache after we remove it.
		//
		// // Delete already exipre messages from LcomNewMessage table
		// String query = "select from " + LcomNewMessageData.class.getName();
		//
		// List<LcomNewMessageData> allMessages = (List<LcomNewMessageData>) pm
		// .newQuery(query).execute();
		// if (allMessages != null && allMessages.size() != 0) {
		//
		// ArrayList<Integer> registeredIds = new ArrayList<Integer>();
		//
		// for (LcomNewMessageData message : allMessages) {
		// if (message != null) {
		// registeredIds.add(message.getUserId());
		// }
		// }
		//
		// // Clear out friendship memcache
		// LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		// // try {
		// // helper
		// // TODO need to remove
		// // helper.removeFriendshipDataFromMemcache(userId);
		// // } catch (LcomMemcacheException e) {
		// // log.log(Level.WARNING,
		// // "LcomMemcacheException: " + e.getMessage());
		// // }
		//
		// // Clear memcache
		// try {
		// helper.deleteAllNewMessages(registeredIds);
		// } catch (LcomMemcacheException e) {
		// log.log(Level.WARNING,
		// "LcomMemcacheException: " + e.getMessage());
		// }
		//
		// // List for old messages
		// List<LcomNewMessageData> oldMessages = new
		// ArrayList<LcomNewMessageData>();
		//
		// // List for not expired (valid) messages
		// // List<LcomNewMessageData> validMessages = new
		// // ArrayList<LcomNewMessageData>();
		//
		// log.log(Level.INFO, "currentTime: " + currentTime);
		//
		// for (LcomNewMessageData message : allMessages) {
		// long expireTime = message.getExpireDate();
		// log.log(Level.INFO, "expireTime: " + expireTime);
		//
		// // If the target message is already expired
		// if (currentTime > expireTime) {
		// oldMessages.add(message);
		// }
		// // else {
		// // validMessages.add(message);
		// // }
		// }
		//
		// // Backuo old messages
		// if (oldMessages != null && oldMessages.size() != 0) {
		// List<LcomExpiredMessageData> expiredMessage =
		// backupToExpiredTable(oldMessages);
		//
		// try {
		// pm.deletePersistentAll(oldMessages);
		// pm.makePersistentAll(expiredMessage);
		// } finally {
		// pm.close();
		// }
		// }
		//
		// // TODO Depends on situtaiton, we need to support below function
		// // Put valid message to memcache
		// // if (validMessages != null && validMessages.size() != 0) {
		// // try {
		// // helper.putNewMessagesToMemCache(validMessages);
		// // } catch (LcomMemcacheException e) {
		// // log.log(Level.WARNING,
		// // "LcomMemcacheException: " + e.getMessage());
		// // }
		// // }
		//
		// }

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
			} catch (EntityNotFoundException e1) {
				log.log(Level.WARNING,
						"EntityNotFoundException: " + e1.getMessage());
				// If no data is on datastore
				entity = new Entity(LcomConst.KIND_USER_DATA, userId,
						ancestorKey);
				// String userName = (String) entity.getProperty("mUserName");
				entity.setProperty(LcomConst.ENTITY_DEVICE_ID, deviceId);
				datastoreService.put(entity);
			}

			// Try to set memcache
			// LcomDatabaseManagerHelper helper = new
			// LcomDatabaseManagerHelper();
			// try {
			// helper.putPushDevceIdToMemCache(userId, deviceId);
			// } catch (LcomMemcacheException e) {
			// log.log(Level.INFO, "LcomMemcacheException: " + e.getMessage());
			// }
		}
	}

	public String getDeviceIdForGCMPush(long userId) {
		log.log(Level.INFO, "getDeviceIdForGCMPush");

		String deviceId = null;

		if (userId == LcomConst.NO_USER) {
			return null;
		}

		DatastoreService datastoreService = DatastoreServiceFactory
				.getDatastoreService();

		Key key = getUserDataKey(userId);
		try {
			Entity entity = datastoreService.get(key);
			deviceId = (String) entity.getProperty(LcomConst.ENTITY_DEVICE_ID);
		} catch (EntityNotFoundException e) {
			log.log(Level.INFO, "EntityNotFoundException: " + e.getMessage());
		}

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
