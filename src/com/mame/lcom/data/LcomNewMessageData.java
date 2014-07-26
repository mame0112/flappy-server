package com.mame.lcom.data;

import java.util.ArrayList;
import java.util.List;

public class LcomNewMessageData {
	private long mUserId = 0;

	private long mTargetUserId = 0;

	private String mTargetUserName = null;

	private List<String> mMessage = new ArrayList<String>();

	private List<Long> mPostedDate = new ArrayList<Long>();

	private List<Long> mExpireTime = new ArrayList<Long>();

	/**
	 * Constructor
	 */
	public LcomNewMessageData(long userId, long targetUserId,
			String targetUserName, List<String> message, List<Long> postedDate,
			List<Long> expireTime) {
		mUserId = userId;
		mTargetUserId = targetUserId;
		// mUserName = userName;
		mTargetUserName = targetUserName;
		mMessage = message;
		mPostedDate = postedDate;
		mExpireTime = expireTime;
	}

	public long getUserId() {
		return mUserId;
	}

	public long getTargetUserId() {
		return mTargetUserId;
	}

	// public String getUserName() {
	// return mUserName;
	// }

	public String getTargetUserName() {
		return mTargetUserName;
	}

	public List<String> getMessage() {
		return mMessage;
	}

	public List<Long> getPostedDate() {
		return mPostedDate;
	}

	public List<Long> getExpireDate() {
		return mExpireTime;
	}

	// public boolean isMessageRead() {
	// return mIsRead;
	// }

	public void setUserId(int userId) {
		mUserId = userId;
	}

	public void setTargetUserId(int targetUserId) {
		mTargetUserId = targetUserId;
	}

	// public void setUserName(String userName) {
	// mUserName = userName;
	// }

	public void setTargetUserName(String targetUserName) {
		mTargetUserName = targetUserName;
	}

	public void setMessage(List<String> message) {
		mMessage = message;
	}

	public void setPostedDate(List<Long> postedDate) {
		mPostedDate = postedDate;
	}

	public void setExpireTime(List<Long> expireTime) {
		mExpireTime = expireTime;
	}

	// public void setReadState(boolean isRead) {
	// mIsRead = isRead;
	// }

}
