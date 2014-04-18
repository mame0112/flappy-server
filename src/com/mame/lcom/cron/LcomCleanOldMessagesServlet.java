package com.mame.lcom.cron;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.util.TimeUtil;

public class LcomCleanOldMessagesServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomCleanOldMessagesServlet.class.getName());

	// This method shall be called from cron
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.WARNING, "Clean message doGet");

		LcomDatabaseManager manager = LcomDatabaseManager.getInstance();
		long currentTime = TimeUtil.getCurrentDate();
		manager.backupOldMessageData(currentTime);

	}

}
