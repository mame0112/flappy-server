package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import com.google.appengine.api.datastore.Text;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManagerUtil {

	private final static Logger log = Logger
			.getLogger(LcomDatabaseManagerUtil.class.getName());

	private final static String TAG = "LcomDatabaseManagerUtil";

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

	public static String getStoredStringValue(Entity e, String column) {
		if (column != null) {

			Text textValue = (Text) e.getProperty(column);
			String output = null;

			if (textValue != null) {
				output = textValue.getValue();
			}

			return output;

		}
		return null;
	}

	public static List<Long> getStoredLongList(Entity e, String column) {
		if (e != null && column != null) {

			List<Text> textArray = (List<Text>) e.getProperty(column);
			List<Long> output = new ArrayList<Long>();

			for (Text t : textArray) {
				if (t != null) {
					output.add(Long.valueOf(t.getValue()));
				}
			}

			return output;
		}
		return null;
	}

	public static void setLongListAsText(Entity e, String column,
			List<Long> input) {
		if (e != null && column != null && input != null) {

			List<Text> output = new ArrayList<Text>();

			for (Long value : input) {
				output.add(new Text(String.valueOf(value)));
			}

			// Put new data
			e.setUnindexedProperty(column, output);

		}
	}

	public static List<String> getStoredStringList(Entity e, String column) {
		if (e != null && column != null) {

			ArrayList<Text> textArray = (ArrayList<Text>) e.getProperty(column);
			List<String> output = new ArrayList<String>();

			for (Text t : textArray) {
				if (t != null) {
					String value = t.getValue();
					if (value != null) {
						output.add(t.getValue());
					} else {
						output.add(null);
					}
				} else {
					output.add(null);
				}

			}

			return output;
		}
		return null;
	}

	public static void setStringListAsText(Entity e, String column,
			List<String> input) {
		DbgUtil.showLog(TAG, "A");
		if (e != null && column != null && input != null) {
			DbgUtil.showLog(TAG, "B");
			List<Text> output = new ArrayList<Text>();

			DbgUtil.showLog(TAG, "C");
			for (String value : input) {

				DbgUtil.showLog(TAG, "D: " + value);
				if (value != null) {
					DbgUtil.showLog(TAG, "E");
					output.add(new Text(value));
				} else {
					DbgUtil.showLog(TAG, "F");
					output.add(null);
				}
			}

			// Put new data
			// if we set "null", collection order change due to Property list
			// specification. then, we have to use "setUnindexedProperty.
			e.setUnindexedProperty(column, output);

		}
	}

	/**
	 * 
	 * @param keyUserId
	 * @param ds
	 * @return
	 */
	public boolean isEntityForKeyUserIdExist(long keyUserId, DatastoreService ds) {
		DbgUtil.showLog(TAG, "isEntityForKeyUserIdExist");

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

		DbgUtil.showLog(TAG, "getEntityForKeyUser");

		Key userKey = getUserDataKey(keyUserId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		PreparedQuery pQuery = ds.prepare(query);
		Entity entity = pQuery.asSingleEntity();

		return entity;
	}

	public boolean isFriendUserIdExistInEntity(Entity entity, long friendUserId) {

		DbgUtil.showLog(TAG, "isFriendUserIdExistInEntity");

		@SuppressWarnings("unchecked")
		List<Long> friendUserIdArray = (List<Long>) entity
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);

		if (friendUserIdArray != null && friendUserIdArray.size() >= 1) {
			int index = friendUserIdArray.indexOf(friendUserId);
			if (index >= 0) {
				return true;
			}
		}
		return false;
	}

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

		DbgUtil.showLog(TAG, "addMessageForFriendUser");

		if (e != null) {

			List<Long> friendUserIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			// List<String> messageArray = (List<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> messageArray = getStoredStringList(e,
					LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
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
							setStringListAsText(
									e,
									LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
									messageArray);
							// e.setProperty(
							// LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
							// messageArray);
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
						DbgUtil.showLog(TAG,
								"friend id exist but no friend Id here. something wrong.");
						return false;
					}

					return true;

				}
			} catch (IndexOutOfBoundsException e1) {
				// TODO
				DbgUtil.showLog(TAG,
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
		DbgUtil.showLog(TAG, "putNewMessageInfoToEntity");
		if (e != null) {
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					Arrays.asList(expireTime));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
					Arrays.asList(senderUserId));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
			// Arrays.asList(senderUserName));
			setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					Arrays.asList(senderUserName));
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					Arrays.asList(postTime));
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// Arrays.asList(message));
			setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					Arrays.asList(message));
		}
	}

	@SuppressWarnings("unchecked")
	public void addNewUserDataAndMessageToFriendship(Entity e,
			long senderUserId, String senderName, String lastMessage,
			long currentTime, DatastoreService ds) {

		DbgUtil.showLog(TAG, "addNewUserDataAndMessageToFriendship");

		List<Long> friendUserIdArray = (List<Long>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		// List<String> friendUserNameArray = (List<String>) e
		// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
		List<String> friendUserNameArray = getStoredStringList(e,
				LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
		// List<String> messageArray = (List<String>) e
		// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> messageArray = getStoredStringList(e,
				LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);

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
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
			// friendUserNameArray);
			setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
					friendUserNameArray);
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// messageArray);
			setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
					messageArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
					postedTimeArray);
			e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
					expireTimeArray);
			ds.put(e);

		} else {
			// In case of first user case
			putNewMessageInfoToEntity(e, senderUserId, senderName, lastMessage,
					String.valueOf(currentTime), String.valueOf(expireDate));
			ds.put(e);
		}
	}

	public boolean addNewEntiyInFriendshipTable(long senderUserId,
			String senderName, long keyUserId, String keyUserName,
			String lastMessage, long time, DatastoreService ds) {

		DbgUtil.showLog(TAG, "addNewEntiyInFriendshipTable");

		Key userKey = getUserDataKey(keyUserId);

		long expireDate = TimeUtil.getExpireDate(time);
		Entity newEntity = new Entity(LcomConst.KIND_FRIENDSHIP_DATA,
				keyUserId, userKey);

		putNewMessageInfoToEntity(newEntity, senderUserId, senderName,
				lastMessage, String.valueOf(time), String.valueOf(expireDate));

		ds.put(newEntity);

		return true;
	}

	public Entity getFriendshipEntityForUserIdWituhoutFilter(long userId,
			DatastoreService ds) {
		DbgUtil.showLog(TAG, "getFriendshipEntityForUserIdWituhoutFilter: "
				+ userId);

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(userId);
		Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA, userKey);
		PreparedQuery pQuery = ds.prepare(query);
		Entity e = pQuery.asSingleEntity();
		return e;
	}

	public Entity getFriendshipEntityForUserId(long userId, DatastoreService ds) {
		DbgUtil.showLog(TAG, "getFriendshipEntityForUserId");
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
	public List<LcomFriendshipData> getAllFriendshipData(Entity e,
			DatastoreService ds, long userId) {
		DbgUtil.showLog(TAG, "getAllFriendshipData");

		List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

		if (e != null) {

			List<Long> friendIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			// List<String> friendNameArray = (List<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
			List<String> friendNameArray = getStoredStringList(e,
					LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);

			// List<String> messageArray = (List<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> messageArray = getStoredStringList(e,
					LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);

			List<String> messageTimeArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);

			if (friendIdArray != null && friendIdArray.size() != 0) {

				long current = TimeUtil.getCurrentDate();

				for (int i = 0; i < friendIdArray.size(); i++) {
					long friendId = friendIdArray.get(i);

					DbgUtil.showLog(TAG, "friendId: " + friendId);

					String friendName = friendNameArray.get(i);
					String messageForUser = messageArray.get(i);
					String msgTimeForUser = messageTimeArray.get(i);

					DbgUtil.showLog(TAG, "messageForUser: " + messageForUser);
					DbgUtil.showLog(TAG, "msgTimeForUser: " + msgTimeForUser);

					List<String> validMessage = new ArrayList<String>();
					List<Long> validExpireTime = new ArrayList<Long>();

					if (messageForUser != null) {
						String[] msgParsed = messageForUser
								.split(LcomConst.SEPARATOR);
						String[] timeParsed = msgTimeForUser
								.split(LcomConst.SEPARATOR);
						if (msgParsed != null && msgParsed.length != 0) {
							try {
								for (int j = 0; j < msgParsed.length; j++) {
									long t = Long.valueOf(timeParsed[j]);
									if (t > current) {
										validMessage.add(msgParsed[j]);
										validExpireTime.add(t);
									}
								}
							} catch (NumberFormatException e1) {
								DbgUtil.showLog(TAG, "NumberFormatException: "
										+ e1.getMessage());
							}
						}
					}

					LcomFriendshipData data = new LcomFriendshipData(userId,
							friendId, friendName, validMessage, validExpireTime);
					result.add(data);

				}
			}

		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<LcomFriendshipData> getAllValidFriendshipData(Entity e,
			DatastoreService ds, long userId) {
		DbgUtil.showLog(TAG, "getAllValidFriendshipData");

		List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

		if (e != null) {
			List<Long> friendIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			// List<String> friendNameArray = (List<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);
			List<String> friendNameArray = getStoredStringList(e,
					LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME);

			// List<String> messageArray = (List<String>) e
			// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> messageArray = getStoredStringList(e,
					LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
			List<String> messageTimeArray = (List<String>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME);

			if (friendIdArray != null && friendIdArray.size() != 0) {

				long currentTime = TimeUtil.getCurrentDate();

				for (int i = 0; i < friendIdArray.size(); i++) {
					long friendId = friendIdArray.get(i);
					String friendName = friendNameArray.get(i);
					String messageForUser = messageArray.get(i);
					String msgTimeForUser = messageTimeArray.get(i);

					DbgUtil.showLog(TAG, "messageForUser: " + messageForUser);
					DbgUtil.showLog(TAG, "msgTimeForUser: " + msgTimeForUser);

					if (messageForUser != null) {
						String[] msgParsed = messageForUser
								.split(LcomConst.SEPARATOR);
						String[] timeParsed = msgTimeForUser
								.split(LcomConst.SEPARATOR);
						if (msgParsed != null && msgParsed.length != 0) {
							List<String> validMessage = new ArrayList<String>();
							List<Long> validExpireTime = new ArrayList<Long>();

							// Old message should be removed when the user goes
							// to Conversation activity
							try {
								for (int j = 0; j < msgParsed.length; j++) {
									long t = Long.valueOf(timeParsed[j]);
									DbgUtil.showLog(TAG, "t: " + t);
									DbgUtil.showLog(TAG, "currentTime: "
											+ currentTime);
									if (t > currentTime) {
										DbgUtil.showLog(TAG, "msgParsed[j]: "
												+ msgParsed[j]);
										validMessage.add(msgParsed[j]);
										validExpireTime.add(t);
									}
								}

								// If no message is valid, should avoid to
								// return
								if (validMessage != null
										&& validMessage.size() != 0) {
									DbgUtil.showLog(TAG, "validMessage: "
											+ validMessage);
									LcomFriendshipData data = new LcomFriendshipData(
											userId, friendId, friendName,
											validMessage, validExpireTime);
									result.add(data);
								}
							} catch (NumberFormatException e1) {
								DbgUtil.showLog(TAG, "NumberFormatException: "
										+ e1.getMessage());
							}
						}
					}
				}
			}

		}

		return result;
	}

	public Entity getEntityForTargetUser(long userId, DatastoreService ds) {
		DbgUtil.showLog(TAG, "getEntityForTargetUser");

		if (userId != LcomConst.NO_USER) {
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

	/**
	 * Get Entity for target user id
	 * 
	 * @param targetUserId
	 * @param ds
	 * @return
	 */
	public Entity getConversationEntity(long targetUserId, DatastoreService ds) {
		DbgUtil.showLog(TAG, "getConversationEntity");

		if (targetUserId != LcomConst.NO_USER) {
			Key targetUserKey = LcomDatabaseManagerUtil
					.getUserDataKey(targetUserId);

			// Filter messageFilter = new FilterPredicate(
			// LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// FilterOperator.NOT_EQUAL, null);

			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
					targetUserKey);
			// query.setFilter(messageFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			return entity;

		}

		return null;
	}

	/**
	 * Check if entity for target user exists or not. This method doesn't care
	 * about message. (Just check entity)
	 * 
	 * @param targetUserId
	 * @param ds
	 * @return
	 */
	public boolean isConversationDataForTargetUserExist(long targetUserId,
			DatastoreService ds) {
		DbgUtil.showLog(TAG, "isConversationDataForTargetUserExist");

		if (targetUserId != LcomConst.NO_USER) {
			Key targetUserKey = LcomDatabaseManagerUtil
					.getUserDataKey(targetUserId);

			// Filter messageFilter = new FilterPredicate(
			// LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// FilterOperator.NOT_EQUAL, null);

			Query query = new Query(LcomConst.KIND_FRIENDSHIP_DATA,
					targetUserKey);
			query.setKeysOnly();
			// query.setFilter(messageFilter);
			PreparedQuery pQuery = ds.prepare(query);
			Entity entity = pQuery.asSingleEntity();
			if (entity != null) {
				return true;
			}
		}
		return false;
	}

	public boolean isUserIdExistInFriendshipKind(long userId, Entity e) {
		DbgUtil.showLog(TAG, "isUserIdExistInFriendshipKind");

		if (e != null && userId != LcomConst.NO_USER) {
			@SuppressWarnings("unchecked")
			ArrayList<Long> friendList = (ArrayList<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
			if (friendList != null && friendList.size() != 0) {
				DbgUtil.showLog(TAG, "friendList is not null");
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
		DbgUtil.showLog(TAG, "addNewUserDataToFriendshipKind");

		if (userId != LcomConst.NO_USER && e != null) {

			List<Long> friendUserIdArray = (List<Long>) e
					.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);

			if (friendUserIdArray != null) {

				int index = friendUserIdArray.indexOf(userId);

				// If target user already exist
				if (index != -1) {
					putNewMessageInfoToEntity(e, userId, userName, message,
							String.valueOf(postTime),
							String.valueOf(expireTime));
				} else {
					// If target user doesn't exist (first time)
					addNewUserDataAndMessageToFriendship(e, userId, userName,
							message, postTime, ds);
				}
			}

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
		DbgUtil.showLog(TAG, "addMessageToFriendshipKind");

		List<Long> friendList = (List<Long>) e
				.getProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID);
		// List<String> messageList = (List<String>) e
		// .getProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
		List<String> messageList = getStoredStringList(e,
				LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE);
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
						DbgUtil.showLog(TAG, "msg: " + msg[i]);
						DbgUtil.showLog(TAG, "currentTime: " + currentTime);
						DbgUtil.showLog(TAG, "expireDate[i]: " + expireDate[i]);
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
					DbgUtil.showLog(TAG, "validMsg2: " + validMsg2);
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
				DbgUtil.showLog(TAG, "registered message is null");
				messageList.set(index, message);
				postDateList.set(index, String.valueOf(postTime));
				expireDateList.set(index, String.valueOf(expireTime));
			}

			// Update Entity
			// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
			// messageList);
			setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
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
		DbgUtil.showLog(TAG, "createNewEntity");

		Key userKey = LcomDatabaseManagerUtil.getUserDataKey(targetUserId);
		Entity e = new Entity(LcomConst.KIND_FRIENDSHIP_DATA, targetUserId,
				userKey);

		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_EXPIRE_TIME,
				Arrays.asList(String.valueOf(expireTime)));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_ID,
				Arrays.asList(userId));
		// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
		// Arrays.asList(userName));
		setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_FRIEND_NAME,
				Arrays.asList(userName));
		e.setProperty(LcomConst.ENTITY_FRIENDSHIP_POSTED_TIME,
				Arrays.asList(String.valueOf(postTime)));
		// e.setProperty(LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
		// Arrays.asList(message));
		setStringListAsText(e, LcomConst.ENTITY_FRIENDSHIP_RECEIVE_MESSAGE,
				Arrays.asList(message));

		ds.put(e);

	}
}
