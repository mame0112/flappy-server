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

	public void pushGCMNotification(int userId, int targetUserId,
			String userName, String targetUserName, String msg, String regId) {

		log.log(Level.WARNING, "pushGCMNotification");

		int RETRY_COUNT = 5;

		Sender sender = new Sender(LcomConst.API_KEY);

		String builtMessage = buildMessageString(userId, targetUserId,
				userName, targetUserName, msg);

		Message message = new Message.Builder().addData("msg", builtMessage)
				.build();

		// Message message = new Message.Builder().addData("msg", msg).build();

		log.log(Level.WARNING, "message: " + message);
		log.log(Level.WARNING, "registrationId: " + regId);

		Result result = null;
		if (message != null) {
			try {
				// TODO Need to check return value and meaning
				result = sender.send(message, regId, RETRY_COUNT);
			} catch (IOException e) {
				log.log(Level.WARNING, "IOException: " + e.getMessage());
			}

		}
	}

	private String buildMessageString(int userId, int targetUserId,
			String userName, String targetUserName, String message) {
		if (userId == LcomConst.NO_USER) {
			return null;
		}

		String result = null;
		if (message == null) {
			return null;
		}

		result = userId + LcomConst.SEPARATOR + targetUserId
				+ LcomConst.SEPARATOR + userName + LcomConst.SEPARATOR
				+ targetUserName + LcomConst.SEPARATOR + message;

		return result;
	}

}
