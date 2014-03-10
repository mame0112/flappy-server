package com.mame.lcom.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class LcomAllUserData {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key mKey;

	@Persistent
	private int mTotalUserNum = 0;

	public LcomAllUserData() {

	}

	public int getTotalUserNum() {
		return mTotalUserNum;
	}

	public void changetTotalUserNum(int newTotalUserNum) {
		mTotalUserNum = newTotalUserNum;
	}

}
