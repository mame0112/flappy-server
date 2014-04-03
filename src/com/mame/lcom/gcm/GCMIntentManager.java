package com.mame.lcom.gcm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.mame.lcom.constant.LcomConst;

public class GCMIntentManager {
	private final static Logger log = Logger.getLogger(GCMIntentManager.class
			.getName());

	public GCMIntentManager() {
	}

	public void pushGCMNotification(HttpServletResponse res, int userId,
			String msg, String regId) {

		log.log(Level.WARNING, "pushGCMNotification2");

		int RETRY_COUNT = 5;

		Sender sender = new Sender(LcomConst.API_KEY);
		Message message = new Message.Builder().addData("msg", msg).build();

		log.log(Level.WARNING, "message: " + message);
		log.log(Level.WARNING, "registrationId: " + regId);

		Result result = null;
		try {
			// TODO Need to check return value and meaning
			result = sender.send(message, regId, RETRY_COUNT);
		} catch (IOException e) {
			log.log(Level.WARNING, "IOException: " + e.getMessage());
		}
	}

}
