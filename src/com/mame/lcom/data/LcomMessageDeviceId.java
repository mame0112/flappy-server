package com.mame.lcom.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomMessageDeviceId {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key mKey;

	@Persistent
	private int mUserId = 0;

	@Persistent
	private String mDeviceId = null;

	public LcomMessageDeviceId(int userId, String deviceId) {
		mUserId = userId;
		mDeviceId = deviceId;
	}

	public int getUserId() {
		return mUserId;
	}

	public String getDeviceId() {
		return mDeviceId;
	}

	public void setUserId(int userId) {
		mUserId = userId;
	}

	public void setDeviceId(String deviceId) {
		mDeviceId = deviceId;
	}

}
