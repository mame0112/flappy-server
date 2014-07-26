package com.mame.lcom.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import javax.jdo.annotations.IdentityType;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomUserData {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long mUserId = 0;

	@Persistent
	private String mUserName = null;

	@Persistent
	private String mPassword = null;

	@Persistent
	private String mMailAddress = null;

	@Persistent
	private Blob mThumbnail = null;

	/**
	 * Constructor
	 */
	public LcomUserData(long userId, String userName, String password,
			String mailAddress, Blob thumbnail) {
		mUserId = userId;
		mUserName = userName;
		mPassword = password;
		mMailAddress = mailAddress;
		mThumbnail = thumbnail;
	}

	public long getUserId() {
		return mUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public String getPassword() {
		return mPassword;
	}

	public String getMailAddress() {
		return mMailAddress;
	}

	public Blob getThumbnail() {
		return mThumbnail;
	}

	public void setUserId(long userId) {
		mUserId = userId;
	}

	public void setUserName(String userName) {
		mUserName = userName;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public void setMailAddress(String mailAddress) {
		mMailAddress = mailAddress;
	}

	public void setThumbnail(Blob thumbnail) {
		mThumbnail = thumbnail;
	}

}
