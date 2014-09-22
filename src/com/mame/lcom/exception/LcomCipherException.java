package com.mame.lcom.exception;

public class LcomCipherException extends Exception {

	private static final long serialVersionUID = 3L;
	private String mMessage = null;

	public LcomCipherException(String exception) {
		mMessage = exception;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}

}
