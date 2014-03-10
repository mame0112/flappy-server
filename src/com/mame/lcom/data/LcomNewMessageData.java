package com.mame.lcom.data;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import javax.jdo.annotations.IdentityType;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomNewMessageData {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key mKey;

	@Persistent
	private int mUserId = 0;

	@Persistent
	private int mTargetUserId = 0;

	@Persistent
	private String mUserName = null;

	@Persistent
	private String mTargetUserName = null;

	@Persistent
	private String mMessage = null;

	/**
	 * This date should be stored as UTC.
	 */
	@Persistent
	private long mPostedDate = 0L;

	/**
	 * Expire time. This message will be expired at this time. And this date
	 * should be stored as UTC.
	 */
	@Persistent
	private long mExpireTime = 0L;

	/**
	 * Constructor
	 */
	public LcomNewMessageData(int userId, int targetUserId, String userName,
			String targetUserName, String message, long postedDate,
			long expireTime) {
		mUserId = userId;
		mTargetUserId = targetUserId;
		mUserName = userName;
		mTargetUserName = targetUserName;
		mMessage = message;
		mPostedDate = postedDate;
		mExpireTime = expireTime;
	}

	public int getUserId() {
		return mUserId;
	}

	public int getTargetUserId() {
		return mTargetUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public String getTargetUserName() {
		return mTargetUserName;
	}

	public String getMessage() {
		return mMessage;
	}

	public long getPostedDate() {
		return mPostedDate;
	}

	public long getExpireDate() {
		return mExpireTime;
	}

	public void setUserId(int userId) {
		mUserId = userId;
	}

	public void setTargetUserId(int targetUserId) {
		mTargetUserId = targetUserId;
	}

	public void setUserName(String userName) {
		mUserName = userName;
	}

	public void setTargetUserName(String targetUserName) {
		mTargetUserName = targetUserName;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public void setPostedDate(long postedDate) {
		mPostedDate = postedDate;
	}

	public void setExpireTime(long expireTime) {
		mExpireTime = expireTime;
	}

}
