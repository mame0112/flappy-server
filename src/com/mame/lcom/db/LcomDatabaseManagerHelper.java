package com.mame.lcom.db;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.LcomAllUserData;
import com.mame.lcom.data.LcomUserData;

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
}
