package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.InvalidValueException;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomAllUserData;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.util.LcomMemcacheUtil;

public class LcomDatabaseManagerHelper {

	private final Logger log = Logger.getLogger(LcomDatabaseManagerHelper.class
			.getName());

	public void putUserDataToMemCache(LcomUserData data) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		if (data != null) {
			int userId = data.getUserId();
			String userName = data.getUserName();
			String mailAddress = data.getMailAddress();
			String password = data.getPassword();

			String inputParams = userId + LcomConst.SEPARATOR + userName
					+ LcomConst.SEPARATOR + password + LcomConst.SEPARATOR
					+ mailAddress;

			memcacheService.put(LcomUserData.class
					+ LcomConst.MEMCACHE_SEPARATOR + userId, inputParams, null);
		}
	}

	public LcomUserData getUserDataFromMemcache(int userId) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		@SuppressWarnings("unchecked")
		String params = (String) memcacheService.get(LcomUserData.class
				+ LcomConst.MEMCACHE_SEPARATOR + userId);
		if (params != null) {

			try {
				String[] parsed = params.split(LcomConst.SEPARATOR);

				if (parsed != null) {
					String userName = parsed[1];
					String password = parsed[2];
					String mailAddress = parsed[3];
					LcomUserData data = new LcomUserData(userId, userName,
							password, mailAddress, null);
					return data;
				}
			} catch (Exception e) {
				log.log(Level.WARNING, "Exception: " + e.getMessage());
			}
		}
		return null;
	}

	public void putTotalNumberOfUser(int numOfUser) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		memcacheService.put(LcomAllUserData.class
				+ LcomConst.MEMCACHE_SEPARATOR + LcomConst.NUM_OF_USER,
				String.valueOf(numOfUser), null);

	}

	public int getTotalNumberOfUser() {
		int userNum = LcomConst.NO_USER;

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		@SuppressWarnings("unchecked")
		String userNumCached = (String) memcacheService
				.get(LcomAllUserData.class + LcomConst.MEMCACHE_SEPARATOR
						+ LcomConst.NUM_OF_USER);
		if (userNumCached != null) {
			try {
				userNum = Integer.valueOf(userNumCached);
				return userNum;
			} catch (NumberFormatException e) {
				log.log(Level.WARNING,
						"NumberFormatException: " + e.getMessage());
				return LcomConst.NO_USER;
			}
		}

		return LcomConst.NO_USER;
	}

	public List<LcomNewMessageData> getNewMessageFromMemcache(int userId)
			throws LcomMemcacheException {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();
		try {
			if (userId != LcomConst.NO_USER) {

				@SuppressWarnings("unchecked")
				String cachedMessage = (String) memcacheService
						.get(LcomNewMessageData.class.getSimpleName());
				if (cachedMessage != null) {
					log.log(Level.INFO, "cachedMessage length: "
							+ cachedMessage.length());
				}

				if (cachedMessage != null) {
					LcomMemcacheUtil util = new LcomMemcacheUtil();

					// Parse cached message to Messsage data list
					List<LcomNewMessageData> messages = util
							.parseCachedMessageToList(cachedMessage);

					if (messages != null && messages.size() != 0) {
						List<LcomNewMessageData> result = new ArrayList<LcomNewMessageData>();

						for (LcomNewMessageData message : messages) {
							int id = message.getTargetUserId();
							if (userId == id) {
								result.add(message);
							}
						}
						return result;
					} else {
						return null;
					}
				}

			} else {
				throw new LcomMemcacheException("illegal userId");
			}
		} catch (IllegalArgumentException e) {
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (InvalidValueException e) {
			throw new LcomMemcacheException("InvalidValueException: "
					+ e.getMessage());
		}
		return null;

	}

	public void putNewMessageToMemCache(LcomNewMessageData message)
			throws LcomMemcacheException {
		log.log(Level.INFO, "putNewMessageToMemCache");
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();

		try {
			if (message != null) {
				LcomMemcacheUtil util = new LcomMemcacheUtil();
				String result = util.parseMessageData2String(message);

				if (result != null) {
					String currentMessage = (String) memcacheService
							.get(LcomNewMessageData.class.getSimpleName());
					String updatedMessage = util.createNewMssageToMemcache(
							currentMessage, result);

					// Update new message memcache
					memcacheService.delete(LcomNewMessageData.class
							.getSimpleName());
					memcacheService.put(
							LcomNewMessageData.class.getSimpleName(),
							updatedMessage);
				}

			} else {
				throw new LcomMemcacheException("messages is null");
			}

		} catch (IllegalArgumentException e) {
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (MemcacheServiceException e) {
			throw new LcomMemcacheException("MemcacheServiceException: "
					+ e.getMessage());
		}

	}

	public void putNewMessagesToMemCache(List<LcomNewMessageData> messages)
			throws LcomMemcacheException {
		log.log(Level.INFO, "putNewMessagesToMemCache");
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();

		try {
			if (messages != null) {

				LcomMemcacheUtil util = new LcomMemcacheUtil();
				String parsed = util.parseMessagesData2String(messages);
				if (parsed != null) {

					// In come cases, there is current memcache. Then, we try to
					// update.
					String currentCache = (String) memcacheService
							.get(LcomNewMessageData.class.getSimpleName());
					String result = null;
					if (currentCache != null) {
						result = util.createNewMssageToMemcache(currentCache,
								parsed);
					} else {
						result = parsed;
					}

					// Update memcache
					memcacheService.delete(LcomNewMessageData.class
							.getSimpleName());
					memcacheService.put(
							LcomNewMessageData.class.getSimpleName(), result);
				}

			} else {
				throw new LcomMemcacheException("messages is null");
			}

		} catch (IllegalArgumentException e) {
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (MemcacheServiceException e) {
			throw new LcomMemcacheException("MemcacheServiceException: "
					+ e.getMessage());
		}

	}

	public void deleteAllNewMessages() throws LcomMemcacheException {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService();

		try {
			// Remove new message
			memcacheService.delete(LcomNewMessageData.class);
		} catch (IllegalArgumentException e) {
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		}

	}
}
