package com.mame.lcom.db;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.mame.lcom.constant.LcomConst;

public class LcomMemcacheHelper<T> {

	private MemcacheService memcacheService = MemcacheServiceFactory
			.getMemcacheService();

	private LcomMemcacheHelper() {

	}

	public synchronized static LcomMemcacheHelper getMemcacheHelper() {
		LcomMemcacheHelper helper = new LcomMemcacheHelper();
		return helper;
	}

	public T getMemcache(Object prefix, Object param1) {
		@SuppressWarnings("unchecked")
		T value = (T) memcacheService.get(prefix + LcomConst.MEMCACHE_SEPARATOR
				+ param1);
		return value;
	}

	public void setMemcache(Object prefix, Object param1, T value) {
		// MemcacheService memcacheService = MemcacheServiceFactory
		// .getMemcacheService();
		memcacheService.put(prefix + LcomConst.MEMCACHE_SEPARATOR + param1,
				value);
	}
}
