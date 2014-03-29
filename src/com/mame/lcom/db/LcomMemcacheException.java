package com.mame.lcom.db;

public class LcomMemcacheException extends Exception {

	String mMessage = null;

	public LcomMemcacheException(String exception) {
		mMessage = exception;
	}

	public String getExceptionMessage() {
		return mMessage;
	}

}
