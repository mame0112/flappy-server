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
	private int mSecondUserId = 0;

	public LcomFriendshipData(int fromUserId, int toUserId) {
		mFirstUserId = fromUserId;
		mSecondUserId = toUserId;
	}

	public int getFirstUserId() {
		return mFirstUserId;
	}

	public int getSecondUserId() {
		return mSecondUserId;
	}

	public void setFirstUserId(int firstUserId) {
		mFirstUserId = firstUserId;
	}

	public void setSecondUserId(int secondUserId) {
		mSecondUserId = secondUserId;
	}

}
