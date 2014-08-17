package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
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

		if (friendUserIdArray != null) {
			int index = friendUserIdArray.indexOf(friendUserId);
			if (index >= 0) {
				return true;
			}
		}

		// for (long id : friendUserIdArray) {
		// if (id == friendUserId) {
		// return true;
		// }
		// }

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
	 * 
	 * @param e
	 * @param senderUserId
	 * @param senderName
	 * @param keyUserId
	 * @param keyUserName
	 * @param lastMessage
	 * @param currentTime
	 * @param ds
	 * @return
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

					int index = friendUserIdArray.indexOf(senderUserId);
					if (index >= 0) {
						// If message already exist
						if (messageArray != null && expireTimeArray != null
								&& postedTimeArray != null
								&& postedTimeArray != null) {

							long expireDate = TimeUtil
									.getExpireDate(currentTime);

							String message = messageArray.get(index);
							String expireTime = expireTimeArray.get(index);
							String postedTime = postedTimeArray.get(index);

							if (message != null
									&& !message.equals(LcomConst.NULL)) {
								message = message + LcomConst.SEPARATOR
										+ lastMessage;
							} else {
								message = lastMessage;
							}

							if (expireTime != null
									&& !expireTime.equals(LcomConst.NULL)) {
								expireTime = expireTime + LcomConst.SEPARATOR
										+ expireDate;
							} else {
								expireTime = String.valueOf(expireDate);
							}

							if (postedTime != null
									&& !postedTime.equals(LcomConst.NULL)) {
								postedTime = postedTime + LcomConst.SEPARATOR
										+ currentTime;
							} else {
								postedTime = String.valueOf(currentTime);
							}

							// Update array
							messageArray.set(index, message);
							expireTimeArray.set(index, expireTime);
							postedTimeArray.set(index, postedTime);

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
						} else {
							// If message is empty
							long expireTime = TimeUtil
									.getExpireDate(currentTime);
							putNewMessageInfoToEntity(e, senderUserId,
									senderName, lastMessage,
									String.valueOf(currentTime),
									String.valueOf(expireTime));
							ds.put(e);
							return true;
						}
					} else {
						log.log(Level.WARNING,
								"friend id exist but no friend Id here. something wrong.");
						return false;
					}

					return true;

				}
			} catch (IndexOutOfBoundsException e1) {
				// TODO
				log.log(Level.WARNING,
						"IndexOutOfBoundsException: " + e1.getMessage());
			}
		}
		return false;
	}

	/**
	 * This method put necessary information onto target entity. Note that this
	 * method doesn't put entity to DatastoreService
	 * 
	 * @param e
	 * @param senderUserId
	 * @param senderUserName
	 * @param message
	 * @param postTime
	 * @param expireTime
	 */
	private void putNewMessageInfoToEntity(Entity e, long senderUserId,
			String senderUserName, String message, String postTime,
			String expireTime) {
		log.log(Level.WARNING, "putNewMessageInfoToEntity");
		if (e != null) {
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					Arrays.asList(expireTime));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					Arrays.asList(senderUserId));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					Arrays.asList(senderUserName));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					Arrays.asList(postTime));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					Arrays.asList(message));
		}
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
			putNewMessageInfoToEntity(e, senderUserId, senderName, lastMessage,
					String.valueOf(currentTime), String.valueOf(expireDate));
			ds.put(e);
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
			// Arrays.asList(senderUserId));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
			// Arrays.asList(senderName));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// Arrays.asList(lastMessage));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
			// Arrays.asList(String.valueOf(currentTime)));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
			// Arrays.asList(String.valueOf(expireDate)));
			// ds.put(e);
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

		// Entity e, long senderUserId,
		// String senderUserName, String message, String postTime,
		// String expireTime
		putNewMessageInfoToEntity(newEntity, senderUserId, senderName,
				lastMessage, String.valueOf(time), String.valueOf(expireDate));
		// newEntity
		// .setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_ID, senderUserId);
		// newEntity
		// .setProperty(LcomConst.ENTITY_FRIENDSHIP_USER_NAME, senderName);
		// newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
		// Arrays.asList(senderUserId));
		// newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
		// Arrays.asList(senderName));
		// newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
		// Arrays.asList(lastMessage));
		// newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
		// Arrays.asList(String.valueOf(time)));
		// newEntity.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
		// Arrays.asList(String.valueOf(expireDate)));
		ds.put(newEntity);

		return true;
	}

	public Entity getFriendshipEntityForUserId(long userId, DatastoreService ds) {
		log.log(Level.WARNING, "getFriendshipEntityForUserId");
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
		log.log(Level.INFO, "getAllValidFriendshipData");

		List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

		if (e != null) {
			List<Long> friendIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			List<String> friendNameArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
			List<String> messageArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> messageTimeArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);

			if (friendIdArray != null && friendIdArray.size() != 0) {
				for (int i = 0; i < friendIdArray.size(); i++) {
					long friendId = friendIdArray.get(i);
					String friendName = friendNameArray.get(i);
					String messageForUser = messageArray.get(i);
					String msgTimeForUser = messageTimeArray.get(i);

					log.log(Level.WARNING, "messageForUser: " + messageForUser);
					log.log(Level.WARNING, "msgTimeForUser: " + msgTimeForUser);

					if (messageForUser != null) {
						String[] msgParsed = messageForUser
								.split(LcomConst.SEPARATOR);
						String[] timeParsed = msgTimeForUser
								.split(LcomConst.SEPARATOR);
						if (msgParsed != null && msgParsed.length != 0) {
							List<String> validMessage = new ArrayList<String>();
							List<Long> validExpireTime = new ArrayList<Long>();

							long currentTime = TimeUtil.getCurrentDate();

							// Old message should be removed when the user goes
							// to Conversation activity
							try {
								for (int j = 0; j < msgParsed.length; j++) {
									long t = Long.valueOf(timeParsed[j]);
									if (t > currentTime) {
										validMessage.add(msgParsed[j]);
										validExpireTime.add(t);
									}
								}

								// If no message is valid, should avoid to
								// return
								if (validMessage != null
										&& validMessage.size() != 0) {
									LcomFriendshipData data = new LcomFriendshipData(
											userId, friendId, friendName,
											validMessage, validExpireTime);
									result.add(data);
								}
							} catch (NumberFormatException e1) {
								log.log(Level.WARNING,
										"NumberFormatException: "
												+ e1.getMessage());
							}
						}
					}
				}
			}

		}

		return result;
	}

	public Entity getEntityForTargetUser(long userId, long targetUserId,
			DatastoreService ds) {
		log.log(Level.WARNING, "getEntityForTargetUser");

		if (userId != LcomConst.NO_USER && targetUserId != LcomConst.NO_USER) {
			Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);
			Key key = KeyFactory.createKey(userKey,
					LcomConst.KIND_FRIENDSHIP_DATA, userId);

			// Filter friendFilter = new FilterPredicate(
			// LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
			// FilterOperator.EQUAL, targetUserId);

			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, key);
			// query.setFilter(friendFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			return entity;
		}

		return null;
	}

	public Entity getConversationEntity(long targetUserId, DatastoreService ds) {
		log.log(Level.INFO, "getConversationEntity");

		if (targetUserId != LcomConst.NO_USER) {
			Key targetUserKey = LcomDatabaseManagerUtil
					.getUserDataKey(targetUserId);

			Filter messageFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					FilterOperator.NOT_EQUAL, null);

			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
					targetUserKey);
			query.setFilter(messageFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			return entity;

		}

		return null;
	}

	public boolean isConversationDataForTargetUserExist(long targetUserId,
			DatastoreService ds) {
		log.log(Level.INFO, "isConversationDataForTargetUserExist");

		if (targetUserId != LcomConst.NO_USER) {
			Key targetUserKey = LcomDatabaseManagerUtil
					.getUserDataKey(targetUserId);

			Filter messageFilter = new FilterPredicate(
					LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					FilterOperator.NOT_EQUAL, null);

			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
					targetUserKey);
			query.setKeysOnly();
			query.setFilter(messageFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			if (entity != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isUserIdExistInFriendshipKind(long userId, Entity e) {
		log.log(Level.INFO, "isUserIdExistInFriendshipKind");

		if (e != null && userId != LcomConst.NO_USER) {
			@SuppressWarnings("unchecked")
			ArrayList<Long> friendList = (ArrayList<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			if (friendList != null && friendList.size() != 0) {
				log.log(Level.INFO, "friendList is not null");
				if (friendList.contains(userId)) {
					return true;
				}
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void addNewUserDataToFriendshipKind(long userId, String userName,
			long postTime, long expireTime, String message, Entity e,
			DatastoreService ds) {
		log.log(Level.INFO, "addNewUserDataToFriendshipKind");

		if (userId != LcomConst.NO_USER && e != null) {
			putNewMessageInfoToEntity(e, userId, userName, message,
					String.valueOf(postTime), String.valueOf(expireTime));

			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
			// Arrays.asList(String.valueOf(expireTime)));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
			// Arrays.asList(String.valueOf(postTime)));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
			// Arrays.asList(userId));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
			// Arrays.asList(userName));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// Arrays.asList(message));

			ds.put(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void addMessageToFriendshipKind(long userId, String userName,
			long postTime, long expireTime, String message, Entity e,
			DatastoreService ds) {
		log.log(Level.INFO, "addMessageToFriendshipKind");

		List<Long> friendList = (List<Long>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		List<String> messageList = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> postDateList = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME);
		List<String> expireDateList = (List<String>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);

		int index = friendList.indexOf(userId);

		// TODO need to care in case of messageList null case
		if (messageList != null && postDateList != null
				&& expireDateList != null) {
			String messages = messageList.get(index);
			String postDates = postDateList.get(index);
			String expireDates = expireDateList.get(index);

			if (messages != null && postDates != null && expireDates != null) {
				String[] msg = messages.split(LcomConst.SEPARATOR);
				String[] postDate = postDates.split(LcomConst.SEPARATOR);
				String[] expireDate = expireDates.split(LcomConst.SEPARATOR);

				String validMsg = "a";
				String validPostDate = "a";
				String validExpireDate = "a";

				long currentTime = TimeUtil.getCurrentDate();

				for (int i = 0; i < msg.length; i++) {
					if (Long.valueOf(expireDate[i]) > currentTime) {
						log.log(Level.INFO, "msg: " + msg[i]);
						log.log(Level.INFO, "currentTime: " + currentTime);
						log.log(Level.INFO, "expireDate[i]: " + expireDate[i]);
						validMsg = validMsg + LcomConst.SEPARATOR + msg[i];
						validPostDate = validPostDate + LcomConst.SEPARATOR
								+ postDate[i];
						validExpireDate = validExpireDate + LcomConst.SEPARATOR
								+ expireDate[i];
					}
				}

				// Add new message
				validMsg = validMsg + LcomConst.SEPARATOR + message;
				validPostDate = validPostDate + LcomConst.SEPARATOR
						+ String.valueOf(postTime);
				validExpireDate = validExpireDate + LcomConst.SEPARATOR
						+ String.valueOf(expireTime);

				String validMsg2 = null;
				String validPostDate2 = null;
				String validExpireDate2 = null;

				// Remove unnecessary characters
				if (validMsg != null
						&& (validMsg.length() > (1 + LcomConst.SEPARATOR
								.length()))) {
					validMsg2 = validMsg.substring((1 + LcomConst.SEPARATOR
							.length()));
				}

				if (validPostDate != null
						&& (validPostDate.length() > (1 + LcomConst.SEPARATOR
								.length()))) {
					validPostDate2 = validPostDate
							.substring((1 + LcomConst.SEPARATOR.length()));
				}

				if (validExpireDate != null
						&& (validExpireDate.length() > (1 + LcomConst.SEPARATOR
								.length()))) {
					validExpireDate2 = validExpireDate
							.substring((1 + LcomConst.SEPARATOR.length()));
				}

				if (validMsg2 != null && validMsg2.length() > 0) {
					log.log(Level.INFO, "validMsg2: " + validMsg2);
					// Update original List
					messageList.set(index, validMsg2);
					postDateList.set(index, validPostDate2);
					expireDateList.set(index, validExpireDate2);
				} else {
					// Update original List
					messageList.set(index, null);
					postDateList.set(index, null);
					expireDateList.set(index, null);
				}
			} else {
				// If already registered message is null
				log.log(Level.INFO, "registered message is null");
				messageList.set(index, message);
				postDateList.set(index, String.valueOf(postTime));
				expireDateList.set(index, String.valueOf(expireTime));
			}

			// Update Entity
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					messageList);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME, postDateList);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					expireDateList);

			// Put to DatastoreService
			ds.put(e);
		}

	}

	public void createNewEntity(long userId, String userName,
			long targetUserId, long postTime, long expireTime, String message,
			DatastoreService ds) {
		log.log(Level.INFO, "createNewEntity");

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(targetUserId);
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, targetUserId,
				userKey);

		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(userId));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(userName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(postTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(message));

		ds.put(e);

	}
}
