package com.mame.lcom;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.gcm.GCMIntentManager;
import com.mame.lcom.util.TimeUtil;

public class LcomSendAndAddMessageServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomSendAndAddMessageServlet.class.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());

		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String targetUserId = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_ID);
		String targetUserName = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_NAME);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String date = req.getParameter(LcomConst.SERVLET_MESSAGE_DATE);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.SEND_MESSAGE_RESULT_OK;

		if (userId != null && userName != null && targetUserId != null
				&& targetUserName != null && message != null && date != null) {

			log.log(Level.WARNING, "message:" + message);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			// Put latest message to LcomNewMessage table
			manager.addNewMessageInfo(Integer.valueOf(userId),
					Integer.valueOf(targetUserId), userName, targetUserName,
					message, Long.valueOf(date));

			// Update Friendship table so that we can show it in
			// FrinedListActivity
			manager.updateLatestMessageInfoOnFriendshipTable(
					Integer.valueOf(userId), Integer.valueOf(targetUserId),
					message, Long.valueOf(date));

			// Send message data to friend via GCM
			String regId = manager.getDeviceIdForGCMPush(Integer
					.valueOf(targetUserId));
			if (regId != null && !regId.isEmpty()) {
				GCMIntentManager pushManager = new GCMIntentManager();
				pushManager.pushGCMNotification(Integer.valueOf(userId),
						Integer.valueOf(targetUserId), userName,
						targetUserName, message, regId);
			}

		} else {
			log.log(Level.WARNING, "Some of parameters are null");
			result = LcomConst.SEND_MESSAGE_UNKNOWN_ERROR;
		}

		list.add(String.valueOf(result));

		list.add(userId);
		list.add(userName);
		list.add(targetUserId);
		list.add(targetUserName);
		list.add(message);
		list.add(date);

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
