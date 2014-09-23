package com.mame.lcom.cron;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.TimeUtil;
import com.mame.lcom.util.DbgUtil;

public class LcomCleanOldMessagesServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomCleanOldMessagesServlet.class.getName());

	private final static String TAG = "LcomCleanOldMessagesServlet";

	// This method shall be called from cron
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "Clean message doGet");

		LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
		long currentTime = TimeUtil.getCurrentDate();
		manager.backupOldMessageData(currentTime);

	}

}
