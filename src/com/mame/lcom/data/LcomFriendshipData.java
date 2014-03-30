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
	private Key mKey;

	@Persistent
	private int mFirstUserId = 0;

	@Persistent
	private String mFirstUserName = null;

	@Persistent
	private int mSecondUserId = 0;

	@Persistent
	private String mSecondUserName = null;

	@Persistent
	private String mLastMessage = null;

	/**
	 * Time that two users comes to friend
	 */
	@Persistent
	private long mFriendTime = 0;

	/**
	 * This will be used to send the number of new message to client side. Then,
	 * we correct latest information if the user requests this data. Therefore,
	 * we need not to store to Datastore.
	 */
	@Persistent
	private int mNumOfNewMessage = 0;

	public LcomFriendshipData(int firstUserId, String firstUserName,
			int secondUserId, String secondUserName, String lastMessage,
			long time, int numOfNewMessage) {
		mFirstUserId = firstUserId;
		mFirstUserName = firstUserName;
		mSecondUserId = secondUserId;
		mSecondUserName = secondUserName;
		mLastMessage = lastMessage;
		mFriendTime = time;
		mNumOfNewMessage = numOfNewMessage;

	}

	public int getFirstUserId() {
		return mFirstUserId;
	}

	public int getSecondUserId() {
		return mSecondUserId;
	}

	public String getFirstUserName() {
		return mFirstUserName;
	}

	public String getSecondUserName() {
		return mSecondUserName;
	}

	public String getLatestMessage() {
		return mLastMessage;
	}

	public long getFriendTime() {
		return mFriendTime;
	}

	public int getNumOfNewMessage() {
		return mNumOfNewMessage;
	}

	public void setFirstUserId(int firstUserId) {
		mFirstUserId = firstUserId;
	}

	public void setSecondUserId(int secondUserId) {
		mSecondUserId = secondUserId;
	}

	public void setFirstUserName(String firstUserName) {
		mFirstUserName = firstUserName;
	}

	public void setSecondUserName(String secondUserName) {
		mSecondUserName = secondUserName;
	}

	public void getLatestMessage(String lastMessage) {
		mLastMessage = lastMessage;
	}

	public void setFriendTime(long time) {
		mFriendTime = time;
	}

	public void setNumOfNewMessage(int numOfNewMessage) {
		mNumOfNewMessage = numOfNewMessage;
	}
}
