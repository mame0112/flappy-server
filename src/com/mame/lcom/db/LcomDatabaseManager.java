package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
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

		Key key = KeyFactory.createKey(LcomUserData.class.getSimpleName(),
				userName);
		if (key != null) {
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			LcomUserData data = pm.getObjectById(LcomUserData.class, key);
			if (data != null) {
				return true;
			}
		}
		return false;
	}

	public int getUserIdByNameAndPassword(String userName, String password) {

		int userId = LcomConst.NO_USER;
		if (userName != null && password != null) {
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			String query = "select from " + LcomUserData.class.getName()
					+ " where mUserName == \"" + userName + "\"";
			// Query query = pm.newQuery(LcomUserData.class);
			// query.setFilter("mUserName == \"userName\"");
			// query.declareParameters("String userName");
			try {
				List<LcomUserData> users = (List<LcomUserData>) pm.newQuery(
						query).execute();
				if (users != null) {
					if (users.size() != 0) {
						LcomUserData data = users.get(0);
						String pass = data.getPassword();
						if (pass != null && pass.equals(password)) {
							return data.getUserId();
						}
					}
				}
			} finally {
				pm.close();
			}
		}
		return userId;
	}

	public int getUserIdByName(String userName) {
		int userId = LcomConst.NO_USER;
		if (userName != null) {
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			Query query = pm.newQuery(LcomUserData.class);
			query.setFilter("mUserName == userName");
			query.declareParameters("String userName");
			try {
				List<LcomUserData> users = (List<LcomUserData>) query
						.execute(userName);
				if (users != null) {
					if (users.size() != 0) {
						LcomUserData data = users.get(0);
						return data.getUserId();
					}
				}
			} finally {
				query.closeAll();
				pm.close();
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
	public synchronized int addNewUserData(LcomUserData data) {
		log.log(Level.INFO, "addNewUserData");
		int userId = LcomConst.NO_USER;

		if (data != null) {
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			Query query = pm.newQuery(LcomAllUserData.class);
			List<LcomAllUserData> totalDatas = (List<LcomAllUserData>) query
					.execute();
			if (totalDatas != null) {
				log.log(Level.INFO, "A");
				LcomAllUserData totalData = null;
				if (totalDatas.size() != 0) {
					log.log(Level.INFO, "B");
					totalData = totalDatas.get(0);
					int userNum = totalData.getTotalUserNum();
					log.log(Level.INFO, "userNum: " + userNum);

					if (data != null) {
						int originalUserId = data.getUserId();
						if (originalUserId == LcomConst.NO_USER) {
							data.setUserId(userNum);
						}
					}
					// int newUserNum = userNum + 1;
					userId = userNum;
					int newUserNum = userNum + 1;
					totalData.changetTotalUserNum(newUserNum);
				} else {
					// If total user data is 0
					totalData = new LcomAllUserData(1);
					totalData.changetTotalUserNum(1);
				}

				try {
					pm.makePersistent(data);
					pm.makePersistent(totalData);

					// Store data to memcache
					LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
					helper.putUserDataToMemCache(data);

				} finally {
					pm.close();
				}
			}
		}
		return userId;
	}

	/**
	 * Update user data. Key is userId.
	 * 
	 * @param userId
	 * @param userName
	 * @param password
	 * @param mailAddress
	 */
	public synchronized void updateUserData(int userId, String userName,
			String password, String mailAddress, Blob thumbnail) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		String query = "select from " + LcomUserData.class.getName()
				+ " where mUserId == " + userId;
		List<LcomUserData> datas = (List<LcomUserData>) pm.newQuery(query)
				.execute();
		if (datas != null && datas.size() != 0) {
			LcomUserData targetData = datas.get(0);

			if (userName != null) {
				targetData.setUserName(userName);
			}

			if (mailAddress != null) {
				targetData.setMailAddress(mailAddress);
			}

			if (password != null) {
				targetData.setPassword(password);
			}

			if (thumbnail != null) {
				targetData.setThumbnail(thumbnail);
			}

			try {
				pm.makePersistent(targetData);
			} finally {
				pm.close();
			}

		}
	}

	/**
	 * THis method shall be called if the user register his user name before his
	 * friend invited to this service (meaning user name was mail address)
	 * 
	 * @param userId
	 * @param userName
	 */
	public synchronized void updateUserNameInFriendhsiopTable(int userId,
			String userName) {
		log.log(Level.INFO, "updateUserNameInFriendhsiopTable");
		if (userId != LcomConst.NO_USER && userName != null) {
			log.log(Level.INFO, "useId: " + userId + " uerName: " + userName);
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			// Get user information based on mail address
			String query = "select from " + LcomFriendshipData.class.getName()
					+ " where mSecondUserId == " + userId;
			List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();
			result = (List<LcomFriendshipData>) pm.newQuery(query).execute();

			if (result != null && result.size() != 0) {
				log.log(Level.INFO, "result size: " + result.size());
				for (LcomFriendshipData data : result) {
					String secondName = data.getSecondUserName();
					if (secondName == null || secondName.equals(LcomConst.NULL)
							|| secondName.equals("")) {
						data.setSecondUserName(userName);
						try {
							pm.makePersistent(data);
						} finally {
							pm.close();
						}
					}
				}
			}

		} else {
			log.log(Level.INFO, "useId: " + userId);
			if (userName != null) {
				log.log(Level.INFO, "useName: " + userName);
			}
		}
	}

	public synchronized void updateUserDate(LcomUserData updatedData) {
		if (updatedData != null) {
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			try {
				LcomUserData oldData = pm.getObjectById(LcomUserData.class,
						updatedData.getUserId());

				String userName = updatedData.getUserName();
				if (userName != null) {
					oldData.setUserName(userName);
				}

				String address = updatedData.getMailAddress();
				if (address != null) {
					oldData.setMailAddress(address);
				}

				String password = updatedData.getPassword();
				if (password != null) {
					oldData.setPassword(password);
				}

				Blob thumbnail = updatedData.getThumbnail();
				if (thumbnail != null) {
					oldData.setThumbnail(thumbnail);
				}

			} finally {
				pm.close();
			}
		}
	}

	public synchronized int getNumOfUserId() {
		log.log(Level.INFO, "getNumOfUserId");
		int userNum = 0;
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		int userNumCached = helper.getTotalNumberOfUser();
		// If cache exist
		if (userNumCached != LcomConst.NO_USER) {
			userNum = Integer.valueOf(userNumCached);
			log.log(Level.WARNING, "getNumOfUserId memcache: " + userNum);
		} else {
			log.log(Level.WARNING, "getNumOfUserId Not memcache: " + userNum);
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			Query query = pm.newQuery(LcomAllUserData.class);
			List<LcomAllUserData> latestDatas = (List<LcomAllUserData>) query
					.execute();

			if (latestDatas != null && latestDatas.size() != 0) {
				userNum = latestDatas.get(0).getTotalUserNum();
				log.log(Level.WARNING, "getNumOfUserId from Datastore: "
						+ userNum);
				helper.putTotalNumberOfUser(userNum);
			}

			pm.close();
		}

		return userNum;
	}

	private synchronized List<LcomNewMessageData> getNewMessages(int userId) {
		log.log(Level.WARNING, "getNewMessages");

		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		try {
			result = helper.getNewMessageFromMemcache(userId);
		} catch (LcomMemcacheException e) {
			log.log(Level.WARNING, "LcomMemcacheException: " + e.getMessage());
		}

		// If we can't get message from memcache, we try to get it from
		// datastore
		// TODO we may be able to datastore chceck if we can change get logic
		// from memcache
		if (result == null || result.size() == 0) {
			log.log(Level.INFO, "Data from datastore");
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			// query messages its target user is me
			String queryFromOthers = "select from "
					+ LcomNewMessageData.class.getName()
					+ " where mTargetUserId == " + userId;
			result = (List<LcomNewMessageData>) pm.newQuery(queryFromOthers)
					.execute();

			pm.close();

			// Store message to memcache
			try {
				helper.putNewMessagesToMemCache(userId, result);
			} catch (LcomMemcacheException e) {
				log.log(Level.WARNING,
						"LcomMemcacheException: " + e.getMessage());
			}

		} else {
			log.log(Level.INFO, "Data from memcache. size: " + result.size());
		}

		// Get only unread message
		if (result != null && result.size() != 0) {
			List<LcomNewMessageData> unreadMessages = new ArrayList<LcomNewMessageData>();
			for (LcomNewMessageData message : result) {
				boolean isRead = message.isMessageRead();
				if (!isRead) {
					unreadMessages.add(message);
				}
			}

			return unreadMessages;
		}

		return null;
	}

	/**
	 * Get thread message for target user. If you call this method, message read
	 * state shall automatically be changed to "read" state.
	 * 
	 * @param userId
	 * @param friendUserId
	 * @return
	 */
	public synchronized List<LcomNewMessageData> getNewMessagesWithTargetUser(
			int userId, int friendUserId) {
		log.log(Level.WARNING, "getNewMessagesWithTargetUser");

		// List<LcomNewMessageData> messagesInMemcache
		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();

		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		try {
			// By calling below method, read state in memcache is automatically
			// changed.
			result = helper.getNewMessageFromMemcacheWithChangeReadState(
					userId, friendUserId);

			// Common operation for no cache and cache without new message

			// query messages its target user is me
			List<LcomNewMessageData> messagesFromOthers = null;
			String queryFromOthers = "select from "
					+ LcomNewMessageData.class.getName()
					+ " where mTargetUserId == " + userId;
			messagesFromOthers = (List<LcomNewMessageData>) pm.newQuery(
					queryFromOthers).execute();

			result = getMessageForTargetUser(friendUserId, messagesFromOthers);

			// Cache exist, but no new message. In this case cache and Data
			// store should have same information. Then we need not to put data
			// onto memcache

		} catch (LcomMemcacheException e) {
			// This catch includes no cache exist case
			log.log(Level.WARNING, "LcomMemcacheException: " + e.getMessage());

			// In case of no cache exist, we need to put latest inforamtion to
			// cache
			if (result == null || result.size() == 0) {
				log.log(Level.INFO, "Data from datastore");

				if (result != null && result.size() != 0) {
					log.log(Level.WARNING,
							"messagesFromOthers size: " + result.size());

					// Try to put latest message data to memcache
					try {
						helper.putNewMessagesToMemCache(userId, result);
					} catch (LcomMemcacheException e1) {
						log.log(Level.WARNING,
								"LcomMemcacheException: " + e1.getMessage());
					}
				}
			}

		} finally {
			pm.close();
		}

		return result;
	}

	private List<LcomNewMessageData> getMessageForTargetUser(int targetUserId,
			List<LcomNewMessageData> original) {
		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

		if (targetUserId != LcomConst.NO_USER && original != null) {
			for (LcomNewMessageData message : original) {
				if (message != null) {
					boolean isRead = message.isMessageRead();
					if (isRead == false) {
						int targetId = message.getUserId();
						if (targetId == targetUserId) {
							result.add(message);

							// Set message to "already read"state
							message.setReadState(true);
						}
					}
				}
			}
			return result;
		}
		return null;
	}

	public synchronized int getUserIdByMailAddress(String address) {
		int userId = LcomConst.NO_USER;

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		@SuppressWarnings("unchecked")
		List<LcomUserData> datas = (List<LcomUserData>) memcacheService
				.get(LcomUserData.class + LcomConst.MEMCACHE_SEPARATOR
						+ LcomConst.USER_ID_BY_MAIL_ADDRESS);
		if (datas != null && datas.size() != 0) {
			LcomUserData data = datas.get(0);
			userId = data.getUserId();
		} else {
			// If memcache doesn't exit here
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			String query = "select from " + LcomUserData.class.getName()
					+ " where mMailAddress == \"" + address + "\"";
			List<LcomUserData> users = (List<LcomUserData>) pm.newQuery(query)
					.execute();
			// If user (mail address) is already registered into DB
			if (users != null && users.size() != 0) {
				LcomUserData data = users.get(0);
				userId = data.getUserId();
			} else {
				// If user (mail address) is not registered into DB
				userId = LcomConst.NO_USER;
			}
			pm.close();
			// helper.setMemcache(LcomAllUserData.class, address, userId);
		}

		// TODO
		// MemcacheService memcacheService = MemcacheServiceFactory
		// .getMemcacheService();
		// @SuppressWarnings("unchecked")
		// userId = (Integer) memcacheService
		// .get(LcomAllUserData.class + LcomConst.MEMCACHE_SEPARATOR
		// + LcomConst.NUM_OF_USER);

		// LcomMemcacheHelper<Integer> helper = LcomMemcacheHelper
		// .getMemcacheHelper();
		// userId = (Integer) helper.getMemcache(LcomAllUserData.class,
		// address);

		return userId;
	}

	public synchronized LcomUserData getUserData(int userId) {
		LcomUserData result = null;

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		LcomUserData data = helper.getUserDataFromMemcache(userId);
		if (data != null) {
			log.log(Level.WARNING, "getUserData from cache");
			return data;
		} else {
			log.log(Level.WARNING, "getUserData from Datastore");
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();
			String query = "select from " + LcomUserData.class.getName()
					+ " where mUserId == " + userId + "";
			List<LcomUserData> users = (List<LcomUserData>) pm.newQuery(query)
					.execute();
			if (users != null && users.size() != 0) {
				result = users.get(0);
				helper.putUserDataToMemCache(result);
			}
			pm.close();

		}

		return result;
	}

	public synchronized void addNewFriendshipInfo(int firstUserId,
			String firstUserName, int secondUserId, String secondUserName,
			String lastMessage, long time, int numOfNewMessage) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		// int firstUserId, String firstUserName,
		// int secondUserId, String secondUserName, String lastMessage,
		// long time, int numOfNewMessage

		LcomFriendshipData data = new LcomFriendshipData(firstUserId,
				firstUserName, secondUserId, secondUserName, lastMessage, time,
				numOfNewMessage);

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		try {
			helper.putFriendListDataToMemCache(data);
		} catch (LcomMemcacheException e) {
			log.log(Level.INFO, "LcomMemcacheException: " + e.getMessage());
		}

		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	public synchronized void updateLatestMessageInfoOnFriendshipTable(
			int userId, int targetUserId, String latestMessage,
			long lastPostedTime) {
		log.log(Level.INFO, "updateLatestMessageInfoOnFriendshipTable");

		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();

		if (userId != LcomConst.NO_USER && targetUserId != LcomConst.NO_USER) {
			if (latestMessage != null && lastPostedTime != 0L) {
				boolean result = false;

				PersistenceManager pm = LcomPersistenceManagerFactory.get()
						.getPersistenceManager();
				String queryFirst = "select from "
						+ LcomFriendshipData.class.getName()
						+ " where mFirstUserId == " + userId + "";
				List<LcomFriendshipData> firstUsers = (List<LcomFriendshipData>) pm
						.newQuery(queryFirst).execute();

				if (firstUsers != null && firstUsers.size() != 0) {
					for (LcomFriendshipData firstData : firstUsers) {
						if (firstData != null) {
							int friendId = firstData.getSecondUserId();
							if (targetUserId == friendId) {
								// If we found message, update message and
								// posted time
								firstData.setLatestMessage(latestMessage);
								long expireDate = TimeUtil
										.getExpireDate(lastPostedTime);
								firstData.setLastMessageExpireTime(expireDate);

								// Put to memcache
								try {
									helper.putFriendListDataToMemCache(firstData);
								} catch (LcomMemcacheException e) {
									log.log(Level.WARNING,
											"LcomMemcacheException: "
													+ e.getMessage());
								}

								result = true;
								break;
							}
						}
					}
				}

				// If result is still false, we continue to check
				if (result == false) {
					// Second, friend to user
					String querySecond = "select from "
							+ LcomFriendshipData.class.getName()
							+ " where mSecondUserId == " + userId + "";
					List<LcomFriendshipData> secondUsers = (List<LcomFriendshipData>) pm
							.newQuery(querySecond).execute();
					if (secondUsers != null && secondUsers.size() != 0) {
						for (LcomFriendshipData secondData : secondUsers) {
							if (secondData != null) {
								int friendId = secondData.getFirstUserId();
								if (targetUserId == friendId) {
									// If we found message, update message and
									// posted time
									secondData.setLatestMessage(latestMessage);
									long expireDate = TimeUtil
											.getExpireDate(lastPostedTime);
									secondData
											.setLastMessageExpireTime(expireDate);

									// Put to memcache
									try {
										helper.putFriendListDataToMemCache(secondData);
									} catch (LcomMemcacheException e) {
										log.log(Level.WARNING,
												"LcomMemcacheException: "
														+ e.getMessage());
									}

									result = true;
									break;
								}
							}
						}
					}
				}

			}
		}

	}

	public synchronized boolean isUsersAreFriend(int userId, int targetUserId) {

		boolean result = false;

		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		// First, from user to friend
		String queryFirst = "select from " + LcomFriendshipData.class.getName()
				+ " where mFirstUserId == " + userId + "";
		List<LcomFriendshipData> firstUsers = (List<LcomFriendshipData>) pm
				.newQuery(queryFirst).execute();
		if (firstUsers != null && firstUsers.size() != 0) {
			for (LcomFriendshipData data : firstUsers) {
				if (data != null) {
					int friendId = data.getSecondUserId();
					if (targetUserId == friendId) {
						// If already friend
						result = true;
						break;
					}
				}
			}
		}

		// If result is still false, we continue to check
		if (result == false) {
			// Second, friend to user
			String querySecond = "select from "
					+ LcomFriendshipData.class.getName()
					+ " where mSecondUserId == " + userId + "";
			List<LcomFriendshipData> secondUsers = (List<LcomFriendshipData>) pm
					.newQuery(queryFirst).execute();
			if (secondUsers != null && secondUsers.size() != 0) {
				for (LcomFriendshipData data : secondUsers) {
					if (data != null) {
						int friendId = data.getFirstUserId();
						if (targetUserId == friendId) {
							// If already friend
							result = true;
							break;
						}
					}
				}
			}

		}

		return result;
	}

	public synchronized List<LcomFriendshipData> getFriendListData(int userId,
			long currentTime) {
		log.log(Level.INFO, "getFriendListData");

		// Try to get from memcache
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		List<LcomFriendshipData> friendList = null;
		try {
			friendList = helper.getFriendListDataFromMemCache(userId);
		} catch (LcomMemcacheException e) {
			log.log(Level.INFO, "LcomMemcacheException: " + e.getMessage());
		}

		// If there is no data in memcache, get friendlist from memcache
		if (friendList == null || friendList.size() == 0) {
			friendList = getFriendshipDataForUser(userId);

			// Debug
			for (LcomFriendshipData data : friendList) {
				if (data != null) {
					log.log(Level.INFO,
							"firstUserName: " + data.getFirstUserName());
					log.log(Level.INFO,
							"secondUserName: " + data.getSecondUserName());
					log.log(Level.INFO,
							"latest message: " + data.getLatestMessage());
				}
			}
		} else {
			// TOOD Do we need friend list to store memcache?
		}

		// Get new message
		if (friendList != null && friendList.size() != 0) {
			log.log(Level.WARNING, "friendList is not null");
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			List<LcomNewMessageData> newMessages = getNewMessages(Integer
					.valueOf(userId));

			if (newMessages != null && newMessages.size() != 0) {
				log.log(Level.WARNING, "newmessage is not null. size: "
						+ newMessages.size());
				// Check the number of new messages.
				HashMap<Integer, Integer> messageNum = new HashMap<Integer, Integer>();

				for (LcomNewMessageData message : newMessages) {

					// We shall handle messages those expire time is still valid
					if (message.getExpireDate() > currentTime) {
						// If target user is me
						int targetUserId = message.getUserId();

						// If the target user infomration is not in Hashmap
						if (!messageNum.containsKey(targetUserId)) {
							messageNum.put(targetUserId, 1);
							log.log(Level.WARNING,
									"A targetUserId" + targetUserId
											+ " userId: " + message.getUserId());
						} else {
							// Otherwise (meaning target user infomration is
							// already
							// been in hashmap),
							// Update it (+1)
							int num = messageNum.get(targetUserId);
							int newNum = num + 1;
							messageNum.put(targetUserId, newNum);
							log.log(Level.WARNING,
									"B targetUserId" + targetUserId
											+ " userId: " + message.getUserId()
											+ " newNum: " + newNum);
						}
					}
				}

				// Then, we merge FriendListData and the number of message
				for (LcomFriendshipData data : friendList) {
					if (data != null) {
						int firstUserId = data.getFirstUserId();
						int secondUserId = data.getSecondUserId();

						// If first user is user himself
						if (messageNum != null) {
							if (messageNum.containsKey(firstUserId)) {
								int firstNumMessage = messageNum
										.get(firstUserId);
								data.setNumOfNewMessage(firstNumMessage);
							} else if (messageNum.containsKey(secondUserId)) {
								int secondNumMessage = messageNum
										.get(secondUserId);
								data.setNumOfNewMessage(secondNumMessage);
							} else {
								// Nothing to do (error case)
							}
						}
					}
				}

			} else {
				log.log(Level.WARNING, "newmessage is null");
			}

			pm.close();

			return friendList;
		} else {
			log.log(Level.WARNING, "friendList is null");
		}

		return null;
	}

	private synchronized List<LcomFriendshipData> getFriendshipDataForUser(
			int userId) {
		log.log(Level.INFO, "getFriendshipDataForUser");
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		List<LcomFriendshipData> result = new ArrayList<LcomFriendshipData>();

		// Get friendship data.
		// First, user is first.
		String queryFirst = "select from " + LcomFriendshipData.class.getName()
				+ " where mFirstUserId == " + userId;
		List<LcomFriendshipData> firstFriendship = (List<LcomFriendshipData>) pm
				.newQuery(queryFirst).execute();

		if (firstFriendship != null && firstFriendship.size() != 0) {
			log.log(Level.INFO,
					"firstFriendship size: " + firstFriendship.size());
			result.addAll(firstFriendship);
		}

		// First, user is second.
		String querySecond = "select from "
				+ LcomFriendshipData.class.getName()
				+ " where mSecondUserId == " + userId;
		List<LcomFriendshipData> secondFriendship = (List<LcomFriendshipData>) pm
				.newQuery(querySecond).execute();

		// Combine two List
		if (secondFriendship != null && secondFriendship.size() != 0) {
			log.log(Level.INFO,
					"secondFriendship size: " + secondFriendship.size());
			result.addAll(secondFriendship);
		}
		pm.close();

		return result;
	}

	public synchronized void addNewMessageInfo(int userId, int targetUserId,
			String userName, String targetUserName, String message,
			long currentDate) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		long expireDate = TimeUtil.getExpireDate(currentDate);

		LcomNewMessageData data = new LcomNewMessageData(userId, targetUserId,
				userName, targetUserName, message, currentDate, expireDate,
				false);

		if (data != null) {

			// Put the data to datastore
			try {
				pm.makePersistent(data);
			} finally {
				pm.close();
			}

			// Put it to memache
			LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
			try {
				helper.putNewMessageToMemCache(data);
			} catch (LcomMemcacheException e) {
				log.log(Level.WARNING,
						"LcomMemcacheException: " + e.getMessage());
			}

		}

	}

	/**
	 * Debug method
	 */
	public synchronized void debugModifyNumOfUser(int numOfUser) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		// TODO May need to chanage this logic later on.
		Query query = pm.newQuery(LcomAllUserData.class);
		List<LcomAllUserData> datas = (List<LcomAllUserData>) query.execute();
		LcomAllUserData data = null;
		if (datas != null && datas.size() != 0) {
			data = datas.get(0);
			data.changetTotalUserNum(numOfUser);
			try {
				pm.makePersistent(data);
			} finally {
				pm.close();
			}
		} else {
			// If data doesn't exist
			try {
				LcomAllUserData newData = new LcomAllUserData(numOfUser);
				pm.makePersistent(newData);
			} finally {
				pm.close();
			}
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
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		HashMap<Integer, String> result = new HashMap<Integer, String>();

		if (friendsId != null && friendsId.size() != 0) {
			for (String id : friendsId) {
				String query = "select from " + LcomUserData.class.getName()
						+ " where mUserId ==" + id;
				List<LcomUserData> users = (List<LcomUserData>) pm.newQuery(
						query).execute();
				LcomUserData data = users.get(0);
				Blob thumbnail = data.getThumbnail();
				if (thumbnail != null) {
					String thumbStr = DatastoreUtil
							.transcodeBlob2String(thumbnail);
					result.put(Integer.valueOf(id), thumbStr);
				}
			}
		}

		return result;
	}

	public static synchronized void backupOldMessageData(long currentTime) {
		log.log(Level.INFO, "backupOldMessageData");
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		String query = "select from " + LcomNewMessageData.class.getName();

		List<LcomNewMessageData> allMessages = (List<LcomNewMessageData>) pm
				.newQuery(query).execute();
		if (allMessages != null && allMessages.size() != 0) {

			ArrayList<Integer> registeredIds = new ArrayList<Integer>();

			for (LcomNewMessageData message : allMessages) {
				if (message != null) {
					registeredIds.add(message.getUserId());
				}
			}

			// Clear memcache
			LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
			try {
				helper.deleteAllNewMessages(registeredIds);
			} catch (LcomMemcacheException e) {
				log.log(Level.WARNING,
						"LcomMemcacheException: " + e.getMessage());
			}

			// List for old messages
			List<LcomNewMessageData> oldMessages = new ArrayList<LcomNewMessageData>();

			// List for not expired (valid) messages
			// List<LcomNewMessageData> validMessages = new
			// ArrayList<LcomNewMessageData>();

			log.log(Level.INFO, "currentTime: " + currentTime);

			for (LcomNewMessageData message : allMessages) {
				long expireTime = message.getExpireDate();
				log.log(Level.INFO, "expireTime: " + expireTime);

				// If the target message is already expired
				if (currentTime > expireTime) {
					oldMessages.add(message);
				}
				// else {
				// validMessages.add(message);
				// }
			}

			// Backuo old messages
			if (oldMessages != null && oldMessages.size() != 0) {
				List<LcomExpiredMessageData> expiredMessage = backupToExpiredTable(oldMessages);

				try {
					pm.deletePersistentAll(oldMessages);
					pm.makePersistentAll(expiredMessage);
				} finally {
					pm.close();
				}
			}

			// TODO Depends on situtaiton, we need to support below function
			// Put valid message to memcache
			// if (validMessages != null && validMessages.size() != 0) {
			// try {
			// helper.putNewMessagesToMemCache(validMessages);
			// } catch (LcomMemcacheException e) {
			// log.log(Level.WARNING,
			// "LcomMemcacheException: " + e.getMessage());
			// }
			// }

		}

	}

	private static synchronized List<LcomExpiredMessageData> backupToExpiredTable(
			List<LcomNewMessageData> oldMessages) {
		log.log(Level.INFO, "backupToExpiredTable");

		if (oldMessages != null) {

			List<LcomExpiredMessageData> expiredMessages = new ArrayList<LcomExpiredMessageData>();

			for (LcomNewMessageData oldMessage : oldMessages) {
				int userId = oldMessage.getUserId();
				int targetUserId = oldMessage.getTargetUserId();
				String message = oldMessage.getMessage();
				long postedDate = oldMessage.getPostedDate();
				LcomExpiredMessageData expireNessage = new LcomExpiredMessageData(
						userId, targetUserId, message, postedDate);

				expiredMessages.add(expireNessage);

			}
			return expiredMessages;
		}
		return null;
	}

	public synchronized void setDeviceIdForMessagePush(int userId,
			String deviceId) {
		log.log(Level.INFO, "setDeviceIdForMessagePush");

		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		if (userId != LcomConst.NO_USER && deviceId != null) {
			// First, we check if the target device id is already registerd or
			// not.
			String query = "select from " + LcomMessageDeviceId.class.getName()
					+ " where mUserId == " + userId;

			List<LcomMessageDeviceId> deviceIds = (List<LcomMessageDeviceId>) pm
					.newQuery(query).execute();

			if (deviceIds != null && deviceIds.size() != 0) {
				LcomMessageDeviceId id = deviceIds.get(0);
				if (id != null) {
					log.log(Level.INFO, "udate1");
					String registeredId = id.getDeviceId();
					// If device id has not been registered, put new device id
					// to DB.
					id.setDeviceId(deviceId);
					try {
						pm.makePersistent(id);
					} finally {
						pm.close();
					}

				} else {
					log.log(Level.INFO, "udate2");
					// If deviceId object has not been registered yet (This
					// should
					// not happen, though...)
					LcomMessageDeviceId newId = new LcomMessageDeviceId(userId,
							deviceId);
					try {
						pm.makePersistent(newId);
					} finally {
						pm.close();
					}
				}
			} else {
				log.log(Level.INFO, "new");
				// If device id has not been registered
				LcomMessageDeviceId newId = new LcomMessageDeviceId(userId,
						deviceId);
				try {
					pm.makePersistent(newId);
				} finally {
					pm.close();
				}
			}

			// Try to set memcache
			LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
			try {
				helper.putPushDevceIdToMemCache(userId, deviceId);
			} catch (LcomMemcacheException e) {
				log.log(Level.INFO, "LcomMemcacheException: " + e.getMessage());
			}

		}
	}

	public String getDeviceIdForGCMPush(int userId) {
		log.log(Level.INFO, "getDeviceIdForGCMPush");
		if (userId == LcomConst.NO_USER) {
			return null;
		}

		// Tey to get device id from memcache at first
		LcomDatabaseManagerHelper helper = new LcomDatabaseManagerHelper();
		String registrationId = helper.getPushDevceIdToMemCache(userId);

		// If no cache exist
		if (registrationId == null) {
			log.log(Level.INFO, "Registration Id from datastore");
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			// First, we check if the target device id is already registerd or
			// not.
			String query = "select from " + LcomMessageDeviceId.class.getName()
					+ " where mUserId == " + userId;

			List<LcomMessageDeviceId> deviceIds = (List<LcomMessageDeviceId>) pm
					.newQuery(query).execute();
			String result = null;
			if (deviceIds != null && deviceIds.size() != 0) {
				LcomMessageDeviceId deviceId = deviceIds.get(0);
				if (deviceId != null) {
					result = deviceId.getDeviceId();

					// Try to put it to memcache
					try {
						helper.putPushDevceIdToMemCache(deviceId.getUserId(),
								result);
					} catch (LcomMemcacheException e) {
						log.log(Level.INFO,
								"LcomMemcacheException: " + e.getMessage());
					}
					return result;
				}
			}

		} else {
			log.log(Level.INFO, "Registration Id from memcache");
			return registrationId;
		}

		return null;
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
}
