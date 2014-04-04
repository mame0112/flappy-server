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
import com.mame.lcom.data.LcomMessageDeviceId;
import com.mame.lcom.data.LcomNewMessageData;
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.util.LcomMemcacheUtil;

public class LcomDatabaseManagerHelper {

	private final Logger log = Logger.getLogger(LcomDatabaseManagerHelper.class
			.getName());

	public void putUserDataToMemCache(LcomUserData data) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomUserData.class.getSimpleName());
		if (data != null) {
			int userId = data.getUserId();
			String userName = data.getUserName();
			String mailAddress = data.getMailAddress();
			String password = data.getPassword();

			String inputParams = userId + LcomConst.SEPARATOR + userName
					+ LcomConst.SEPARATOR + password + LcomConst.SEPARATOR
					+ mailAddress;

			// Because there is no case we update user information at current
			// specification, we don't cheeck user name and update it. But once
			// we have such kind of function, we need to check and update user
			// information
			memcacheService.put(userId, inputParams);
		}
	}

	public LcomUserData getUserDataFromMemcache(int userId) {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomUserData.class.getSimpleName());
		@SuppressWarnings("unchecked")
		String params = (String) memcacheService.get(userId);
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
				.getMemcacheService(LcomAllUserData.class.getSimpleName());
		try {
			memcacheService.put(LcomConst.NUM_OF_USER,
					String.valueOf(numOfUser));
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
		} catch (MemcacheServiceException e) {
			log.log(Level.WARNING,
					"MemcacheServiceException: " + e.getMessage());
		}

	}

	public int getTotalNumberOfUser() {
		int userNum = LcomConst.NO_USER;

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomAllUserData.class.getSimpleName());

		try {
			@SuppressWarnings("unchecked")
			String userNumCached = (String) memcacheService
					.get(LcomConst.NUM_OF_USER);
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

		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
		} catch (InvalidValueException e) {
			log.log(Level.WARNING, "InvalidValueException: " + e.getMessage());
		}

		return LcomConst.NO_USER;
	}

	public List<LcomNewMessageData> getNewMessageFromMemcache(int userId)
			throws LcomMemcacheException {
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomNewMessageData.class.getSimpleName());
		try {
			if (userId != LcomConst.NO_USER) {

				@SuppressWarnings("unchecked")
				String cachedMessage = (String) memcacheService.get(userId);
				if (cachedMessage != null) {
					log.log(Level.INFO, "cachedMessage length: "
							+ cachedMessage.length());
				}

				if (cachedMessage != null) {
					LcomMemcacheUtil util = new LcomMemcacheUtil();

					// Parse cached message to Messsage data list
					List<LcomNewMessageData> messages = util
							.parseCachedMessageToList(cachedMessage);
					return messages;

					// if (messages != null && messages.size() != 0) {
					// List<LcomNewMessageData> result = new
					// ArrayList<LcomNewMessageData>();

					// for (LcomNewMessageData message : messages) {
					// int id = message.getTargetUserId();
					// if (userId == id) {
					// result.add(message);
					// }
					// }
					// return result;
					// } else {
					// return null;
					// }
				}

			} else {
				log.log(Level.WARNING, "LcomMemcacheException illega userId");
				throw new LcomMemcacheException("illegal userId");
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (InvalidValueException e) {
			log.log(Level.WARNING, "InvalidValueException: " + e.getMessage());
			throw new LcomMemcacheException("InvalidValueException: "
					+ e.getMessage());
		}
		return null;

	}

	public void putNewMessageToMemCache(LcomNewMessageData message)
			throws LcomMemcacheException {
		log.log(Level.INFO, "putNewMessageToMemCache");

		try {
			if (message != null) {

				MemcacheService memcacheService = MemcacheServiceFactory
						.getMemcacheService(LcomNewMessageData.class
								.getSimpleName());

				LcomMemcacheUtil util = new LcomMemcacheUtil();
				String result = util.parseMessageData2String(message);

				if (result != null) {
					// Memcache always needs to be stored key by using Target
					// user Id because only friend (=target user) shall use this
					// cache
					int targetUserId = message.getTargetUserId();
					String currentMessage = (String) memcacheService
							.get(targetUserId);
					String updatedMessage = util.createNewMssageToMemcache(
							currentMessage, result);

					// Update new message memcache
					memcacheService.delete(targetUserId);
					memcacheService.put(targetUserId, updatedMessage);
				}
			} else {
				log.log(Level.WARNING,
						"LcomMemcacheException: messages is null");
				throw new LcomMemcacheException("messages is null");
			}

		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (MemcacheServiceException e) {
			log.log(Level.WARNING,
					"MemcacheServiceException: " + e.getMessage());
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
				log.log(Level.WARNING, "LcomMemcacheException message is null");
				throw new LcomMemcacheException("messages is null");
			}

		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (MemcacheServiceException e) {
			log.log(Level.WARNING,
					"MemcacheServiceException: " + e.getMessage());
			throw new LcomMemcacheException("MemcacheServiceException: "
					+ e.getMessage());
		}
	}

	public String getPushDevceIdToMemCache(int userId) {
		log.log(Level.INFO, "getPushDevceIdToMemCache");

		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomMessageDeviceId.class.getSimpleName());

		try {
			@SuppressWarnings("unchecked")
			String registrationId = (String) memcacheService.get(userId);
			if (registrationId != null) {
				log.log(Level.INFO, "registrationId is not null");
				return registrationId;
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
		} catch (InvalidValueException e) {
			log.log(Level.WARNING, "InvalidValueException: " + e.getMessage());
		}

		return null;
	}

	public void putPushDevceIdToMemCache(int userId, String registrationId)
			throws LcomMemcacheException {
		log.log(Level.INFO, "putPushDevceIdToMemCache");

		// Get memcache for push device id
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomMessageDeviceId.class.getSimpleName());

		try {
			if (userId != LcomConst.NO_USER && registrationId != null) {
				String currentCache = (String) memcacheService.get(userId);
				// Check if already cache is stored
				if (currentCache != null) {
					// If cache is already there
					// Once delete memcache
					log.log(Level.INFO, "delete memcache");
					memcacheService.delete(userId);
				}
				// And insert memcache
				log.log(Level.INFO, "put memcache");
				memcacheService.put(userId, registrationId);

			} else {
				throw new LcomMemcacheException("device id is null");
			}

		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		} catch (MemcacheServiceException e) {
			log.log(Level.WARNING,
					"MemcacheServiceException: " + e.getMessage());
			throw new LcomMemcacheException("MemcacheServiceException: "
					+ e.getMessage());
		}

	}

	public synchronized void deleteAllNewMessages(
			ArrayList<Integer> registeredIds) throws LcomMemcacheException {
		log.log(Level.INFO, "deleteAllNewMessages");
		MemcacheService memcacheService = MemcacheServiceFactory
				.getMemcacheService(LcomNewMessageData.class.getSimpleName());

		try {
			// Remove new message
			if (registeredIds != null && registeredIds.size() != 0) {
				memcacheService.deleteAll(registeredIds);
			}
		} catch (IllegalArgumentException e) {
			log.log(Level.WARNING,
					"IllegalArgumentException: " + e.getMessage());
			throw new LcomMemcacheException("IllegalArgumentException: "
					+ e.getMessage());
		}

	}
}
