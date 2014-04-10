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

	public void pushGCMNotification(int userId, int targetUserId, String msg,
			String regId) {

		log.log(Level.WARNING, "pushGCMNotification");

		int RETRY_COUNT = 5;

		Sender sender = new Sender(LcomConst.API_KEY);

		String builtMessage = buildMessageString(userId, targetUserId, msg);

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
			String message) {
		if (userId == LcomConst.NO_USER) {
			return null;
		}

		String result = null;
		if (message == null) {
			return null;
		}

		// In case of ConversationActivity case
		if (targetUserId != LcomConst.NO_USER) {
			result = userId + LcomConst.SEPARATOR + targetUserId
					+ LcomConst.SEPARATOR + message;
		} else {
			// In case of FriendListActivity case
			result = userId + LcomConst.SEPARATOR + message;
		}

		return result;
	}

}
