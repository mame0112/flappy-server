package com.mame.lcom.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomExpiredMessageData {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key mKey;

	@Persistent
	private int mUserId = 0;

	@Persistent
	private int mTargetUserId = 0;

	@Persistent
	private String mMessage = null;

	/**
	 * This date should be stored as UTC.
	 */
	@Persistent
	private long mPostedDate = 0L;

	/**
	 * Constructor
	 */
	public LcomExpiredMessageData(int userId, int targetUserId, String message,
			long postedDate) {
		mUserId = userId;
		mTargetUserId = targetUserId;
		mMessage = message;
		mPostedDate = postedDate;
	}

	public int getUserId() {
		return mUserId;
	}

	public int getTargetUserId() {
		return mTargetUserId;
	}

	public String getMessage() {
		return mMessage;
	}

	public long getPostedDate() {
		return mPostedDate;
	}

	public void setUserId(int userId) {
		mUserId = userId;
	}

	public void setTargetUserId(int targetUserId) {
		mTargetUserId = targetUserId;
	}

	public void setMessage(String message) {
		mMessage = message;
	}

	public void setPostedDate(long postedDate) {
		mPostedDate = postedDate;
	}

}
