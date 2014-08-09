package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManagerUtil {

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerUtil.class.getName());

	public static Key getAllUserDataKey() {
		Key ancKey = KeyFactory.createKey(LcomConst.KIND_ALL_USER_DATA,
				LcomConst.ENTITY_TOTAL_USER_NUM);
		return ancKey;
	}

	public static Key getUserDataKey(long userId) {
		Key ancKey = getAllUserDataKey();
		Key key = KeyFactory
				.createKey(ancKey, LcomConst.KIND_USER_DATA, userId);
		return key;
	}

	public boolean isEntityForKeyUserIdExist(long keyUserId, DatastoreService ds) {
		log.log(Level.WARNING, "isEntityForKeyUserIdExist");

		Key userKey = getUserDataKey(keyUserId);

		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		query.setKeysOnly();
		// query.setFilter(userIdFilter);
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		if (entity != null) {
			return true;
		} else {
			return false;
		}
	}

	public Entity getEntityForKeyUser(long keyUserId, DatastoreService ds) {

		log.log(Level.WARNING, "getEntityForKeyUser");

		Key userKey = getUserDataKey(keyUserId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		return entity;
	}

	public boolean isFriendUserIdExistInEntity(Entity entity, long friendUserId) {

		log.log(Level.WARNING, "isFriendUserIdExistInEntity");

		@SuppressWarnings("unchecked")
		List<Long> friendUserIdArray = (List<Long>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);

		for (long id : friendUserIdArray) {
			if (id == friendUserId) {
				return true;
			}
		}

		return false;
	}

	// public boolean isFriendUserDataExist(long keyUserId, long friendUserId,
	// DatastoreService ds) {
	//
	// Key userKey = getUserDataKey(keyUserId);
	//
	// Filter userIdFilter = new FilterPredicate(
	// LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID, FilterOperator.EQUAL,
	// friendUserId);
	//
	// Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
	// query.setKeysOnly();
	// query.setFilter(userIdFilter);
	// PreparedQuery pQuery = ds.prepare(query);
	// Entity entity = pQuery.asSingleEntity();
	//
	// if (entity != null) {
	// return true;
	// } else {
	// return false;
	// }
	// }

	/**
	 * add message for friend user.
	 * 
	 * @param keyUserId
	 * @param friendUserId
	 * @param ds
	 * @param isRemoveOldMessage
	 * @return true if success to add. Otherwise, return false
	 */
	@SuppressWarnings("unchecked")
	public boolean addMessageForFriendUser(Entity e, long senderUserId,
			String senderName, long keyUserId, String keyUserName,
			String lastMessage, long currentTime, DatastoreService ds) {

		log.log(Level.WARNING, "addMessageForFriendUser");

		if (e != null) {

			List<Long> friendUserIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			List<String> messageArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> expireTimeArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
			List<String> postedTimeArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

			try {
				if (friendUserIdArray != null && friendUserIdArray.size() != 0) {

					for (int i = 0; i < friendUserIdArray.size(); i++) {
						if (friendUserIdArray.get(i) == senderUserId) {

							// If message already exist
							if (messageArray != null && expireTimeArray != null
									&& postedTimeArray != null
									&& postedTimeArray != null) {

								long expireDate = TimeUtil
										.getExpireDate(currentTime);

								String message = messageArray.get(i);
								String expireTime = expireTimeArray.get(i);
								String postedTime = postedTimeArray.get(i);

								message = message + LcomConst.SEPARATOR
										+ lastMessage;
								expireTime = expireTime + LcomConst.SEPARATOR
										+ expireDate;
								postedTime = postedTime + LcomConst.SEPARATOR
										+ currentTime;

								// Update array
								messageArray.set(i, message);
								expireTimeArray.set(i, expireTime);
								postedTimeArray.set(i, postedTime);

								// Update entity
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
										messageArray);
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
										postedTimeArray);
								e.setProperty(
										LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
										expireTimeArray);
								ds.put(e);

								// TODO
								// if (expireTime != null && postedTime != null)
								// {
								//
								// String[] t = expireTime
								// .split(LcomConst.SEPARATOR);
								// String[] postT = postedTime
								// .split(LcomConst.SEPARATOR);
								//
								// long currentTime = TimeUtil
								// .getCurrentDate();
								//
								// // Remove expire message
								// for (int j = 0; j < t.length; j++) {
								// // If message is still valid
								// if (Long.valueOf(t[j]) < currentTime) {
								//
								// }
								// }
								//
								// }
							} else {
								// If message is empty
								// TODO need error handling
								return false;
							}

							return true;
						}
					}

				}
			} catch (IndexOutOfBoundsException e1) {
				// TODO
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean addNewUserDataAndMessageToFriendship(Entity e,
			long senderUserId, String senderName, long keyUserId,
			String keyUserName, String lastMessage, long currentTime,
			DatastoreService ds) {

		log.log(Level.WARNING, "addNewUserDataAndMessageToFriendship");

		List<Long> friendUserIdArray = (List<Long>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> friendUserNameArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
		List<String> messageArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> expireTimeArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
		List<String> postedTimeArray = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

		long expireDate = TimeUtil.getExpireDate(currentTime);

		// If some user already exisst
		if (friendUserIdArray != null) {
			friendUserIdArray.add(senderUserId);
			friendUserNameArray.add(senderName);
			messageArray.add(lastMessage);
			expireTimeArray.add(String.valueOf(expireDate));
			postedTimeArray.add(String.valueOf(currentTime));

			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					friendUserIdArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					friendUserNameArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					messageArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					expireTimeArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					postedTimeArray);
			ds.put(e);

		} else {
			// In case of first user case
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					Arrays.asList(senderUserId));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					Arrays.asList(senderName));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					Arrays.asList(lastMessage));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					Arrays.asList(currentTime));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					Arrays.asList(expireDate));
			ds.put(e);
		}

		return true;
	}

	public boolean addNewEntiyInFriendshipTable(long senderUserId,
			String senderName, long keyUserId, String keyUserName,
			String lastMessage, long time, DatastoreService ds) {

		log.log(Level.WARNING, "addNewEntiyInFriendshipTable");

		Key userKey = getUserDataKey(keyUserId);

		long expireDate = TimeUtil.getExpireDate(time);
		Entity newEntity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA,
				keyUserId, userKey);
		newEntity
				.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		newEntity
				.setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(senderUserId));
		newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(senderName));
		newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(lastMessage));
		newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(time)));
		newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireDate)));
		ds.put(newEntity);

		return true;
	}

	public Entity getFriendshipEntityForUserId(long userId, DatastoreService ds) {
		Filter messageFilter = new FilterPredicate(
				LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				FilterOperator.NOT_EQUAL, null);

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		query.setFilter(messageFilter);
		PreparedQuery pQuery = ds.prepare(query);
		Entity e = pQuery.asSingleEntity();
		return e;
	}

	@SuppressWarnings("unchecked")
	public List<LcomFriendshipData> getAllValidFriendshipData(Entity e,
			DatastoreService ds, long userId) {

		List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

		if (e != null) {
			List<Long> friendIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			List<String> friendNameArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
			List<String> messageArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			ArrayList<String> messageTimeArray = (ArrayList<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);
			// ArrayList<String> postedTimeArray = (ArrayList<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);

			if (friendIdArray != null && friendIdArray.size() != 0) {
				for (int i = 0; i < friendIdArray.size(); i++) {
					long friendId = friendIdArray.get(i);
					String friendName = friendNameArray.get(i);
					String messageForUser = messageArray.get(i);
					String msgTimeForUser = messageTimeArray.get(i);

					if (messageForUser != null) {
						String[] msgParsed = messageForUser
								.split(LcomConst.SEPARATOR);
						String[] timeParsed = msgTimeForUser
								.split(LcomConst.SEPARATOR);
						if (msgParsed != null && msgParsed.length != 0) {
							List<String> validMessage = new ArrayList<String>();
							List<Long> validExpireTime = new ArrayList<Long>();

							long currentTime = TimeUtil.getCurrentDate();

							// TODO Need to remove obsolete message
							for (int j = 0; j < msgParsed.length; j++) {
								long t = Long.valueOf(timeParsed[j]);
								if (t > currentTime) {
									validMessage.add(msgParsed[j]);
									validExpireTime.add(t);
								}
							}
							LcomFriendshipData data = new LcomFriendshipData(
									userId, friendId, friendName, validMessage,
									validExpireTime);
							result.add(data);
						}
					}
				}

			}

		}

		return result;
	}
}
