package com.mame.lcom.servlet;

import java.io.IOException;

import com.mame.lcom.util.DbgUtil;

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
import com.mame.lcom.data.LcomUserData;
import com.mame.lcom.db.LcomDatabaseManager;
import com.mame.lcom.gcm.GCMIntentManager;
import com.mame.lcom.invitation.LcomMail;
import com.mame.lcom.util.CipherUtil;
import com.mame.lcom.util.TimeUtil;

/**
 * Send invitation message servlet
 * 
 */

public class LcomSendConfirmMessageServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomLoginServlet.class
			.getName());

	private final static String TAG = "LcomSendConfirmMessageServlet";

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// In case of new user, target user name and target user id is null
		// If tbe targetuser mail address is registerd, target user name is null
		// (target user id is not null)
		DbgUtil.showLog(TAG, "doPost:" + TimeUtil.calcResponse());

		String secretKey = req.getParameter(LcomConst.SERVLET_IDENTIFIER);

		String origin = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_ORIGIN), secretKey);
		String userId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_ID), secretKey);
		String userName = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_USER_NAME), secretKey);
		String mailAddress = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_MAILADDRESS), secretKey);
		String language = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_LANGUAGE), secretKey);
		String message = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_MESSAGE_BODY), secretKey);
		String targetUserId = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_TARGET_USER_ID), secretKey);
		String targetUserName = CipherUtil
				.decrypt(req.getParameter(LcomConst.SERVLET_TARGET_USER_NAME),
						secretKey);
		String apiLevel = CipherUtil.decrypt(
				req.getParameter(LcomConst.SERVLET_API_LEVEL), secretKey);

		int result = LcomConst.INVITATION_CONFIRMED_RESULT_OK;

		List<String> list = new ArrayList<String>();
		// targetUserId and targetUserName could be null if target address has
		// not been registered
		if (origin != null && userId != null && userName != null
				&& mailAddress != null && message != null && apiLevel != null) {
			DbgUtil.showLog(TAG, "message: " + message);
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			list.add(origin);
			list.add(userId);
			list.add(userName);

			// If target user has been already been registered
			if (targetUserId != null && !targetUserId.equals("")
					&& !targetUserId.equals(LcomConst.NULL)) {
				DbgUtil.showLog(TAG, "targetUserId: " + targetUserId);
				if (targetUserName != null) {
					DbgUtil.showLog(TAG, "targetUserName: " + targetUserName);
				}

				long currentTime = TimeUtil.getCurrentDate();

				manager.addNewFriendshipInfo(Long.valueOf(userId), userName,
						Long.valueOf(targetUserId), targetUserName, message,
						currentTime);

				// long currentDate = TimeUtil.getCurrentDate();
				// manager.addNewMessageInfo(Integer.valueOf(userId),
				// Integer.valueOf(targetUserId), userName, null, message,
				// currentDate);

				// Send back targetUserId
				list.add(targetUserId);

				// Send back userName (in this case, it shall be null)
				list.add(targetUserName);

				// Send back message
				list.add(message);

				// Send back date info
				list.add(String.valueOf(currentTime));

				// Set result
				list.add(String.valueOf(result));

				// Send back mail address (to be shown before the target user
				// set his/her user name)
				list.add(mailAddress);

				// Push message
				String regId = manager.getDeviceIdForGCMPush(Integer
						.valueOf(targetUserId));
				if (regId != null && !regId.isEmpty()) {
					long expireDate = TimeUtil.getExpireDate(currentTime);
					GCMIntentManager pushManager = new GCMIntentManager();
					pushManager.pushGCMNotification(Integer.valueOf(userId),
							Integer.valueOf(targetUserId), userName,
							targetUserName, message, regId, expireDate);
				}

			} else {
				// If the target user has NOT been registered. (= new user)
				DbgUtil.showLog(TAG, "mailAddress: " + mailAddress
						+ "/ userName: " + userName + "/ message: " + message);
				LcomMail mail = new LcomMail();
				boolean mailResult = mail.sendInvitationMail(mailAddress,
						userName, message, language);

				long currentTime = TimeUtil.getCurrentDate();
				long newUserId = LcomConst.NO_USER;

				if (mailResult) {
					result = LcomConst.INVITATION_CONFIRMED_RESULT_OK;
					// If target user been registered
					// manager.updateUserData(Integer.valueOf(targetUserId),
					// targetUserName, null, mailAddress);

					LcomUserData data = new LcomUserData(LcomConst.NO_USER,
							null, null, mailAddress, null);

					newUserId = manager.addNewUserAndFriendshipInfo(data,
							Long.valueOf(userId), userName, message,
							currentTime);

					// newUserId = manager.addNewUserData(data);
					//
					// manager.addNewFriendshipInfo(Long.valueOf(userId),
					// userName, newUserId, null, message, currentTime);

					// manager.addNewMessageInfo(Long.valueOf(userId),
					// newUserId,
					// userName, null, message, currentTime);
				}

				// Send back targetUserId
				list.add(String.valueOf(newUserId));

				// Send back userName
				list.add(LcomConst.NULL);

				// Send back message
				list.add(message);

				// Send back date info
				list.add(String.valueOf(currentTime));

				// Set result
				list.add(String.valueOf(result));

				// Send back mail address (to be shown before the target
				// user
				// set his/her user name)
				list.add(mailAddress);

			}

		} else {
			// If some of data is lacked
			result = LcomConst.INVITATION_CONFIRMED_UNKNOWN_ERROR;
		}

		list.add(String.valueOf(result));

		String json = new Gson().toJson(CipherUtil.encryptArrayList(list,
				secretKey));
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
