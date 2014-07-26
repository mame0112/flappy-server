package com.mame.lcom.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomFriendshipData {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private long mUserId = 0;

	@Persistent
	private long mFriendUserId = 0;

	@Persistent
	private String mFriendUserName = null;

	@Persistent
	private String mLastMessage = null;

	/**
	 * Time that last messag is sent
	 */
	@Persistent
	private long mLastExpireTime = 0L;

	/**
	 * This will be used to send the number of new message to client side. Then,
	 * we correct latest information if the user requests this data. Therefore,
	 * we need not to store to Datastore.
	 */
	// @Persistent
	// private int mNumOfNewMessage = 0;

	public LcomFriendshipData(long userId, long friendUserId,
			String friendUserName, String lastMessage, long expireTime) {
		mUserId = userId;
		mFriendUserId = friendUserId;
		mFriendUserName = friendUserName;
		mLastMessage = lastMessage;
		mLastExpireTime = expireTime;
		// mNumOfNewMessage = numOfNewMessage;

	}

	public long getFirstUserId() {
		return mUserId;
	}

	public long getSecondUserId() {
		return mFriendUserId;
	}

	// public String getFirstUserName() {
	// return mUserName;
	// }

	public String getSecondUserName() {
		return mFriendUserName;
	}

	public String getLatestMessage() {
		return mLastMessage;
	}

	public long getLastMessageExpireTime() {
		return mLastExpireTime;
	}

	// public int getNumOfNewMessage() {
	// return mNumOfNewMessage;
	// }

	public void setFirstUserId(long userId) {
		mUserId = userId;
	}

	public void setSecondUserId(long friendUserId) {
		mFriendUserId = friendUserId;
	}

	// public void setFirstUserName(String userName) {
	// mUserName = userName;
	// }

	public void setSecondUserName(String friendUserName) {
		mFriendUserName = friendUserName;
	}

	public void setLatestMessage(String lastMessage) {
		mLastMessage = lastMessage;
	}

	public void setLastMessageExpireTime(long time) {
		mLastExpireTime = time;
	}

	// public void setNumOfNewMessage(int numOfNewMessage) {
	// mNumOfNewMessage = numOfNewMessage;
	// }
}
