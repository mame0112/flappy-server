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
import com.mame.lcom.data.LcomFriendshipData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.util.DatastoreUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomDatabaseManager {

	private final Logger log = Logger.getLogger(LcomDatabaseManager.class
			.getName());

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
			// TODO Need to modify LcomTotalUserData table as well.
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
					totalData = new LcomAllUserData();
					totalData.changetTotalUserNum(1);
				}

				try {
					pm.makePersistent(data);
					pm.makePersistent(totalData);
				} finally {
					pm.close();
				}
			}
		}
		return userId;
	}

	// public synchronized void updateUserData(LcomUserData data) {
	// if (data != null) {
	// PersistenceManager pm = LcomPersistenceManagerFactory.get()
	// .getPersistenceManager();
	// Query query = pm.newQuery(LcomAllUserData.class);
	// List<LcomUserData> datas = (List<LcomUserData>) query.execute();
	// if (datas != null && datas.size() != 0) {
	// LcomUserData targetData = datas.get(0);
	//
	// String userName = data.getUserName();
	// if (userName != null) {
	// targetData.setUserName(userName);
	// }
	//
	// String mailAddress = data.getMailAddress();
	// if (mailAddress != null) {
	// targetData.setMailAddress(mailAddress);
	// }
	//
	// String password = data.getPassword();
	// if (password != null) {
	// targetData.setPassword(password);
	// }
	//
	// try {
	// pm.makePersistent(data);
	// } finally {
	// pm.close();
	// }
	// }
	// }
	// }

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

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		@SuppressWarnings("unchecked")
		List<LcomAllUserData> latestDatas = (List<LcomAllUserData>) memcacheService
				.get(LcomAllUserData.class + LcomConst.MEMCACHE_SEPARATOR
						+ LcomConst.NUM_OF_USER);
		// If cache exist
		if (latestDatas != null && latestDatas.size() != 0) {
			LcomAllUserData latestUser = latestDatas.get(0);
			userNum = latestUser.getTotalUserNum();
			log.log(Level.WARNING, "getNumOfUserId memcache: " + userNum);
			return userNum;
		} else {
			log.log(Level.WARNING, "getNumOfUserId Not memcache: " + userNum);
			PersistenceManager pm = LcomPersistenceManagerFactory.get()
					.getPersistenceManager();

			// TODO May need to chanage this logic later on.
			Query query = pm.newQuery(LcomAllUserData.class);
			latestDatas = (List<LcomAllUserData>) query.execute();

			if (latestDatas != null && latestDatas.size() != 0) {
				userNum = latestDatas.get(0).getTotalUserNum();
				log.log(Level.WARNING, "getNumOfUserId memcache: " + userNum);
			}

			memcacheService.put(LcomAllUserData.class
					+ LcomConst.MEMCACHE_SEPARATOR + LcomConst.NUM_OF_USER,
					String.valueOf(userNum), null);

			pm.close();
		}

		return userNum;
	}

	public synchronized List<LcomNewMessageData> getNewMessages(int userId) {
		log.log(Level.WARNING, "getNewMessages");
		// MemcacheService memcacheService = MemcacheServiceFactory
		// .getMemcacheService();
		// @SuppressWarnings("unchecked")
		// List<LcomNewMessageData> messages = (List<LcomNewMessageData>)
		// memcacheService
		// .get(LcomNewMessageData.class + LcomConst.MEMCACHE_SEPARATOR
		// + userId);

		// LcomMemcacheHelper<List<LcomNewMessageData>> helper =
		// LcomMemcacheHelper.getMemcacheHelper();
		// List<LcomNewMessageData> messages = (List<LcomNewMessageData>) helper
		// .getMemcache(LcomNewMessageData.class, userId);
		// if (messages == null) {

		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();
		// List<LcomNewMessageData> messagesFromMe = null;
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		// query messages its sender user is me
		// String query = "select from " + LcomNewMessageData.class.getName()
		// + " where mUserId == " + userId;
		// messagesFromMe = (List<LcomNewMessageData>) pm.newQuery(query)
		// .execute();

		// log.log(Level.WARNING, "messagesFromMe size: " +
		// messagesFromMe.size());

		// query messages its target user is me
		List<LcomNewMessageData> messagesFromOthers = null;
		String queryFromOthers = "select from "
				+ LcomNewMessageData.class.getName()
				+ " where mTargetUserId == " + userId;
		messagesFromOthers = (List<LcomNewMessageData>) pm.newQuery(
				queryFromOthers).execute();

		log.log(Level.WARNING,
				"messagesFromOthers size: " + messagesFromOthers.size());

		pm.close();

		// result.addAll(messagesFromMe);
		result.addAll(messagesFromOthers);

		return result;

		// if (messagesFromMe != null && messagesFromMe.size() != 0) {
		// if (messagesFromOthers != null && messagesFromOthers.size() != 0) {
		// messagesFromMe.addAll(messagesFromOthers);
		// }
		// return messagesFromMe;
		// } else {
		// if (messagesFromOthers != null && messagesFromOthers.size() != 0) {
		// return messagesFromOthers;
		// } else {
		// return null;
		// }
		// }
		// TODO
		// Date currentDate = new Date();
		// Calendar cal = Calendar.getInstance();
		// cal.getTimeInMillis();

		// long time = currentDate.getTime();
		// for (LcomNewMessageData message : messages) {
		// Date expireTime = message.getExpireDate();
		//
		// }

		// }
	}

	public synchronized List<LcomNewMessageData> getNewMessagesWithTargetUser(
			int userId, int targetUserId) {
		log.log(Level.WARNING, "getNewMessagesWithTargetUser");

		List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();
		// List<LcomNewMessageData> messagesFromMe = null;
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		// query messages its sender user is me
		// String query = "select from " + LcomNewMessageData.class.getName()
		// + " where mUserId == " + userId;
		// messagesFromMe = (List<LcomNewMessageData>) pm.newQuery(query)
		// .execute();
		//
		// for (LcomNewMessageData dataMe : messagesFromMe) {
		// if (dataMe != null) {
		// int targetId1 = dataMe.getTargetUserId();
		// if (targetId1 == targetUserId) {
		// result.add(dataMe);
		// }
		// }
		// }

		// log.log(Level.WARNING, "messagesFromMe size: " +
		// messagesFromMe.size());

		// query messages its target user is me
		List<LcomNewMessageData> messagesFromOthers = null;
		String queryFromOthers = "select from "
				+ LcomNewMessageData.class.getName()
				+ " where mTargetUserId == " + userId;
		messagesFromOthers = (List<LcomNewMessageData>) pm.newQuery(
				queryFromOthers).execute();

		for (LcomNewMessageData dataTarget : messagesFromOthers) {
			if (dataTarget != null) {
				int targetId = dataTarget.getUserId();
				if (targetId == targetUserId) {
					result.add(dataTarget);
				}
			}
		}

		log.log(Level.WARNING, "messagesFromOthers size: " + result.size());

		pm.close();

		return result;
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

		// LcomMemcacheHelper mcHelper = new LcomMemcacheHelper();
		// List<LcomUserData> users = (List<LcomUserData>) mcHelper.getMemcache(
		// LcomUserData.class, userId);
		// if (users == null) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		String query = "select from " + LcomUserData.class.getName()
				+ " where mUserId == " + userId + "";
		List<LcomUserData> users = (List<LcomUserData>) pm.newQuery(query)
				.execute();
		if (users != null && users.size() != 0) {
			result = users.get(0);
		}
		pm.close();
		// }
		return result;
	}

	public synchronized void addNewFriendshipInfo(int firstUserId,
			int secondUserId) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();
		LcomFriendshipData data = new LcomFriendshipData(firstUserId,
				secondUserId);
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}
	}

	public synchronized void addNewMessageInfo(int userId, int targetUserId,
			String userName, String targetUserName, String message,
			long currentDate) {
		PersistenceManager pm = LcomPersistenceManagerFactory.get()
				.getPersistenceManager();

		long expireDate = TimeUtil.getExpireDate(currentDate);

		LcomNewMessageData data = new LcomNewMessageData(userId, targetUserId,
				userName, targetUserName, message, currentDate, expireDate);
		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
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
		}

		try {
			pm.makePersistent(data);
		} finally {
			pm.close();
		}

		pm.close();
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
						+ " where mUserId == " + id;
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

}
