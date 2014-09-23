package com.mame.lcom.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mame.lcom.constant.LcomConst;

public class DbgUtil {

	private final static Logger loga = Logger
			.getLogger(DbgUtil.class.getName());

	public static void showLog(String tag, String message) {
		if (LcomConst.IS_DEBUG) {
			if (message != null) {
				loga.log(Level.WARNING, tag + ": " + message);
			}
		}
	}
}
