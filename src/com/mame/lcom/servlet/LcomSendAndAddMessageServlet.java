package com.mame.lcom.servlet;

import java.io.IOException;

import com.mame.lcom.util.DbgUtil;

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
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.TimeUtil;

public class LcomSendAndAddMessageServlet extends HttpServlet {

	private final static Logger log = Logger
			.getLogger(LcomSendAndAddMessageServlet.class.getName());

	private final static String TAG = "LcomSendAndAddMessageServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DbgUtil.showLog(TAG, "doPost:" + TimeUtil.calcResponse());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_ID), secretKey);
		String userName = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_NAME), secretKey);
		String targetUserId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_TARGET_USER_ID), secretKey);
		String targetUserName = CipherUtil
				.decrypt(req.getParameter(LcomConst.SERVLET_TARGET_USER_NAME),
						secretKey);
		String message = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_MESSAGE_BODY), secretKey);
		String date = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_MESSAGE_DATE), secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		List<String> list = new ArrayList<String>();
		list.add(origin);

		int result = LcomConst.SEND_MESSAGE_RESULT_OK;

		if (userId != null && userName != null && targetUserId != null
				&& targetUserName != null && message != null && date != null
				&& apiLevel != null) {

			DbgUtil.showLog(TAG, "message:" + message);

			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			// Put latest message to LcomNewMessage table
			manager.addNewMessageInfo(Integer.valueOf(userId),
					Integer.valueOf(targetUserId), userName, targetUserName,
					message, Long.valueOf(date));

			// Update Friendship table so that we can show it in
			// FrinedListActivity
			// manager.updateLatestMessageInfoOnFriendshipTable(
			// Integer.valueOf(userId), Integer.valueOf(targetUserId),
			// message, Long.valueOf(date));

			// Send message data to friend via GCM
			String regId = manager.getDeviceIdForGCMPush(Integer
					.valueOf(targetUserId));
			if (regId != null && !regId.isEmpty()) {
				long expireDate = TimeUtil.getExpireDate(Long.valueOf(date));
				GCMIntentManager pushManager = new GCMIntentManager();
				pushManager.pushGCMNotification(Integer.valueOf(userId),
						Integer.valueOf(targetUserId), userName,
						targetUserName, message, regId, expireDate);
			}

		} else {
			DbgUtil.showLog(TAG, "Some of parameters are null");
			result = LcomConst.SEND_MESSAGE_UNKNOWN_ERROR;
		}

		list.add(String.valueOf(result));

		list.add(userId);
		list.add(userName);
		list.add(targetUserId);
		list.add(targetUserName);
		list.add(message);
		list.add(date);

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
