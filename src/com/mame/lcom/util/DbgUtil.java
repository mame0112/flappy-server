package com.mame.lcom.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.mame.lcom.constant.LcomConst;

public class DbgUtil {

	private final static Logger log = Logger.getLogger(DbgUtil.class.getName());

	public static void showLog(Level level, String message) {
		if (LcomConst.IS_DEBUG) {
			if (message != null) {
				log.log(level, message);
			}
		}
	}
}
