package com.mame.lcom;

import java.io.IOException;
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
import com.mame.lcom.invitation.LcomMail;
import com.mame.lcom.util.TimeUtil;

/**
 * Send invitation message servlet
 * 
 */

public class LcomSendConfirmMessageServlet extends HttpServlet {

	private final static Logger log = Logger.getLogger(LcomLoginServlet.class
			.getName());

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// In case of new user, target user name and target user id is null
		// If tbe targetuser mail address is registerd, target user name is null
		// (target user id is not null)

		log.log(Level.INFO, "doPost:" + TimeUtil.calcResponse());
		String origin = req.getParameter(LcomConst.SERVLET_ORIGIN);
		String userId = req.getParameter(LcomConst.SERVLET_USER_ID);
		String userName = req.getParameter(LcomConst.SERVLET_USER_NAME);
		String mailAddress = req.getParameter(LcomConst.SERVLET_MAILADDRESS);
		String language = req.getParameter(LcomConst.SERVLET_LANGUAGE);
		String message = req.getParameter(LcomConst.SERVLET_MESSAGE_BODY);
		String targetUserId = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_ID);
		String targetUserName = req
				.getParameter(LcomConst.SERVLET_TARGET_USER_NAME);
		String apiLevel = req.getParameter(LcomConst.SERVLET_API_LEVEL);

		int result = LcomConst.INVITATION_CONFIRMED_RESULT_OK;

		List<String> list = new ArrayList<String>();
		// targetUserId and targetUserName could be null if target address has
		// not been registered
		if (origin != null && userId != null && userName != null
				&& mailAddress != null && message != null && apiLevel != null) {
			LcomDatabaseManager manager = LcomDatabaseManager.getInstance();

			list.add(origin);
			list.add(userId);
			list.add(userName);

			// If target user has been already been registered
			if (targetUserId != null && !targetUserId.equals("")
					&& !targetUserId.equals(LcomConst.NULL)
					&& targetUserName != null && !targetUserName.equals("")
					&& !targetUserName.equals(LcomConst.NULL)) {
				log.log(Level.WARNING, "targetUserId: " + targetUserId);
				if (targetUserName != null) {
					log.log(Level.WARNING, "targetUserName: " + targetUserName);
				}

				// LcomMail mail = new LcomMail();
				// mail.sendInvitationMail(mailAddress, userName, message);

				// TODO SEND Notification (Not e-mail)

				long currentTime = TimeUtil.getCurrentDate();

				manager.addNewFriendshipInfo(Integer.valueOf(userId), userName,
						Integer.valueOf(targetUserId), targetUserName, message,
						currentTime, 0);

				long currentDate = TimeUtil.getCurrentDate();
				manager.addNewMessageInfo(Integer.valueOf(userId),
						Integer.valueOf(targetUserId), userName, null, message,
						currentDate);

				// Send back targetUserId
				list.add(targetUserId);

				// Send back userName (in this case, it shall be null)
				list.add(targetUserName);

				// Send back message
				list.add(message);

				// Send back date info
				list.add(String.valueOf(currentDate));

				// Set result
				list.add(String.valueOf(result));

				// Send back mail address (to be shown before the target user
				// set his/her user name)
				list.add(mailAddress);

			} else {
				// If the target user has NOT been registered. (= new user)
				log.log(Level.WARNING, "mailAddress: " + mailAddress
						+ "/ userName: " + userName + "/ message: " + message);
				LcomMail mail = new LcomMail();
				boolean mailResult = mail.sendInvitationMail(mailAddress,
						userName, message, language);

				long currentTime = TimeUtil.getCurrentDate();
				int newUserId = LcomConst.NO_USER;

				if (mailResult) {
					result = LcomConst.INVITATION_CONFIRMED_RESULT_OK;
					// If target user been registered
					// manager.updateUserData(Integer.valueOf(targetUserId),
					// targetUserName, null, mailAddress);

					LcomUserData data = new LcomUserData(LcomConst.NO_USER,
							null, null, mailAddress, null);
					newUserId = manager.addNewUserData(data);

					// If user and targer user is not friend yet.
					if (!manager.isUsersAreFriend(Integer.valueOf(userId),
							newUserId)) {
						// manager.addNewFriendshipInfo(Integer.valueOf(userId),
						// newUserId);

						manager.addNewFriendshipInfo(Integer.valueOf(userId),
								userName, Integer.valueOf(newUserId),
								targetUserName, message, currentTime, 0);

					}

					manager.addNewMessageInfo(Integer.valueOf(userId),
							newUserId, userName, null, message, currentTime);
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

		String json = new Gson().toJson(list);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(json);

	}
}
