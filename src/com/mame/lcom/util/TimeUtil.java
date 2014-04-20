package com.mame.lcom.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mame.lcom.constant.LcomConst;

public class TimeUtil {

	private static long sEndTime = 0;

	public static long calcResponse() {
		long time = new Date().getTime();
		long response = time - sEndTime;
		sEndTime = time;
		return response;
	}

	public static long getCurrentDate() {
		Date date1 = new Date();
		// SimpleDateFormat sdf = new SimpleDateFormat(LcomConst.DATE_PATTERN);
		// Date currentDate = new Date(date1.getTime());
		// Date currentDate = sdf.par
		// return currentDate;
		return date1.getTime();
	}

	public static long getExpireDate(long date) {
		Calendar now = Calendar.getInstance();
		long expireTime = now.getTimeInMillis()
				+ LcomConst.MESSAGE_EXPIRE_PERIOD; // 3omin
		// now.setTime(date);
		// now.add(Calendar.MINUTE, LcomConst.MESSAGE_EXPIRE_PERIOD);
		// Date time = now.getTime();
		return expireTime;
	}

	// public static long getDateInDateFormat(String date) throws ParseException
	// {
	// Date result = new SimpleDateFormat(LcomConst.DATE_PATTERN).parse(date);
	// return result;
	// }
}
